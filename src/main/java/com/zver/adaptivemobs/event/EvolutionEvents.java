package com.zver.adaptivemobs.event;

import com.zver.adaptivemobs.AdaptiveMobsMod;
import com.zver.adaptivemobs.data.EvolutionData;
import com.zver.adaptivemobs.registry.ModEffects;
import com.zver.adaptivemobs.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.saveddata.DataStorage;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = AdaptiveMobsMod.MOD_ID)
public final class EvolutionEvents {
    private static final String DATA_KEY = AdaptiveMobsMod.MOD_ID + "_evolution";
    private static final String BOOSTED_KEY = "adaptivemobs_boosted_stage";
    private static final String CHEST_KEY = "adaptivemobs_artifact_chest";

    private EvolutionEvents() {
    }

    private static EvolutionData data(ServerLevel level) {
        DataStorage storage = level.getDataStorage();
        return storage.computeIfAbsent(EvolutionData::load, EvolutionData::new, DATA_KEY);
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.level.isClientSide
                || !(event.level instanceof ServerLevel level)
                || level.dimension() != Level.OVERWORLD || level.getGameTime() % 20 != 0) {
            return;
        }

        EvolutionData evolution = data(level);
        int before = evolution.stage();
        evolution.advanceOneSecond();
        int after = evolution.stage();
        if (after != before) {
            for (ServerPlayer player : level.players()) {
                player.displayClientMessage(Component.translatable(
                        "message.adaptivemobs.stage_up", after), true);
            }
        }
    }

    @SubscribeEvent
    public static void onMobJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide || !(event.getEntity() instanceof Monster monster)
                || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        applyStage(monster, data(level).stage());
    }

    private static void applyStage(Monster monster, int stage) {
        int oldStage = monster.getPersistentData().getInt(BOOSTED_KEY);
        if (oldStage >= stage) {
            return;
        }
        monster.getPersistentData().putInt(BOOSTED_KEY, stage);

        if (stage >= 1) {
            monster.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, Math.min(2, stage / 4), false, false));
        }
        if (stage >= 2) {
            monster.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, Math.min(2, stage / 4), false, false));
        }
        if (stage >= 3) {
            monster.setCanPickUpLoot(true);
        }
        if (stage >= 4) {
            monster.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, Math.min(1, stage / 5), false, false));
        }
        if (stage >= 6) {
            monster.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 0, false, false));
        }
        if (stage >= 8) {
            monster.addEffect(new MobEffectInstance(MobEffects.REGENERATION, -1, 0, false, false));
        }
        if (stage >= 10) {
            monster.addEffect(new MobEffectInstance(ModEffects.AETHER.get(), -1, 0, false, false));
        }

        if (monster instanceof Zombie zombie) {
            equipZombie(zombie, stage);
        } else if (monster instanceof Skeleton skeleton && stage >= 4) {
            skeleton.setCanPickUpLoot(true);
        }
    }

    private static void equipZombie(Zombie zombie, int stage) {
        if (zombie.getMainHandItem().isEmpty()) {
            ItemStack weapon = stage >= 8 ? new ItemStack(Items.DIAMOND_SWORD)
                    : stage >= 5 ? new ItemStack(Items.IRON_SWORD) : new ItemStack(Items.STONE_SWORD);
            zombie.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, weapon);
            zombie.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        }
        if (stage >= 6 && zombie.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(stage >= 9 ? Items.DIAMOND_HELMET : Items.IRON_HELMET));
            zombie.setDropChance(EquipmentSlot.HEAD, 0.0F);
        }
        if (stage >= 9 && zombie.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            zombie.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
            zombie.setDropChance(EquipmentSlot.CHEST, 0.0F);
        }
    }

    @SubscribeEvent
    public static void onMobTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof Monster monster) || monster.level().isClientSide
                || monster.tickCount % 40 != 0 || !(monster.level() instanceof ServerLevel level)) {
            return;
        }
        int stage = data(level).stage();
        applyStage(monster, stage);

        if (stage >= 2 && monster.getTarget() == null) {
            ServerPlayer player = level.getNearestPlayer(monster, 24.0D + stage * 4.0D);
            if (player != null && player.isAlive()) {
                monster.setTarget(player);
            }
        }

        if (stage >= 3 && monster instanceof Zombie zombie && zombie.getTarget() != null
                && zombie.tickCount % 200 == 0) {
            tryBreakNearbyBlock(zombie, level, stage);
        }
    }

    private static void tryBreakNearbyBlock(Zombie zombie, ServerLevel level, int stage) {
        BlockPos front = zombie.blockPosition().relative(zombie.getDirection());
        if (level.getBlockState(front).getDestroySpeed(level, front) >= 0.0F
                && level.getBlockState(front).getDestroySpeed(level, front) <= Math.max(1.5F, stage - 1)) {
            level.destroyBlock(front, true, zombie);
        } else if (stage >= 7) {
            BlockPos above = front.above();
            if (level.isEmptyBlock(above) && level.isEmptyBlock(front)) {
                level.setBlock(front, Blocks.COBBLESTONE.defaultBlockState(), 3);
            }
        }
    }

    @SubscribeEvent
    public static void onChestOpen(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !(event.getLevel() instanceof ServerLevel level)
                || data(level).stage() < 5
                || !(level.getBlockEntity(event.getPos()) instanceof ChestBlockEntity chest)
                || chest.getPersistentData().getBoolean(CHEST_KEY)
                || level.random.nextFloat() > 0.20F) {
            return;
        }
        List<Integer> emptySlots = new java.util.ArrayList<>();
        for (int slot = 0; slot < chest.getContainerSize(); slot++) {
            if (chest.getItem(slot).isEmpty()) {
                emptySlots.add(slot);
            }
        }
        if (emptySlots.isEmpty()) {
            chest.getPersistentData().putBoolean(CHEST_KEY, true);
            return;
        }
        chest.getPersistentData().putBoolean(CHEST_KEY, true);
        ItemStack artifact = switch (level.random.nextInt(4)) {
            case 0 -> new ItemStack(ModItems.VOID_PRISM.get());
            case 1 -> new ItemStack(ModItems.AEGIS_CORE.get());
            case 2 -> new ItemStack(ModItems.PULSE_SEAL.get());
            default -> new ItemStack(ModItems.GRAVITY_THREAD.get());
        };
        chest.setItem(emptySlots.get(level.random.nextInt(emptySlots.size())), artifact);
        chest.setChanged();
        player.displayClientMessage(Component.translatable("message.adaptivemobs.artifact_found"), true);
    }

    @SubscribeEvent
    public static void onMobAttack(LivingAttackEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel level)
                || !(event.getSource().getEntity() instanceof Monster monster)) {
            return;
        }
        int stage = data(level).stage();
        if (stage >= 7 && event.getEntity() instanceof ServerPlayer player
                && monster.getRandom().nextFloat() < 0.15F) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, Math.min(2, stage / 4)));
        }
    }
}
