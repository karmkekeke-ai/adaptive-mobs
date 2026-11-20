package com.zver.adaptivemobs.event;

import com.zver.adaptivemobs.AdaptiveMobsMod;
import com.zver.adaptivemobs.item.CatchTheRainbowMaskItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Comparator;

@Mod.EventBusSubscriber(modid = AdaptiveMobsMod.MOD_ID)
public final class RainbowMaskEvents {
    private static final String DASH_READY_AT = "adaptivemobs_rainbow_dash_ready_at";
    private static final int RAIN_DURATION = 20 * 60 * 5;

    private RainbowMaskEvents() {
    }

    @SubscribeEvent
    public static void onEquip(LivingEquipmentChangeEvent event) {
        if (event.getSlot() != EquipmentSlot.HEAD || !(event.getEntity() instanceof ServerPlayer player)
                || event.getFrom().is(event.getTo().getItem())
                || !(event.getTo().getItem() instanceof CatchTheRainbowMaskItem)) {
            return;
        }
        ServerLevel level = player.serverLevel();
        if (level.dimension().equals(Level.OVERWORLD)) {
            level.setWeatherParameters(0, RAIN_DURATION, true, level.isThundering());
            level.playSound(null, player.blockPosition(), SoundEvents.TRIDENT_THUNDER, SoundSource.PLAYERS, 0.6F, 1.4F);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)
                || player.isSpectator() || !player.isAlive() || player.tickCount % 10 != 0
                || !(player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof CatchTheRainbowMaskItem mask)
                || !player.level().isRainingAt(player.blockPosition())) {
            return;
        }
        boolean requiem = mask.isRequiem();
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30, requiem ? 3 : 2, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 30, requiem ? 2 : 1, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30, requiem ? 2 : 1, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30, requiem ? 1 : 0, false, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 30, 0, false, false, false));

        ServerLevel level = player.serverLevel();
        long time = level.getGameTime();
        if (player.isSprinting() && !player.onGround() && !player.isFallFlying() && !player.getAbilities().flying
                && time >= player.getPersistentData().getLong(DASH_READY_AT)) {
            Vec3 direction = Vec3.directionFromRotation(0.0F, player.getYRot());
            double speed = requiem ? 1.15 : 0.80;
            player.setDeltaMovement(direction.x * speed, Math.max(player.getDeltaMovement().y, 0.12), direction.z * speed);
            player.hurtMarked = true;
            player.getPersistentData().putLong(DASH_READY_AT, time + (requiem ? 20 : 40));
            level.playSound(null, player.blockPosition(), SoundEvents.TRIDENT_RIPTIDE_1, SoundSource.PLAYERS, 0.6F, 1.3F);
        }

        if (player.tickCount % (requiem ? 40 : 60) == 0) {
            double range = requiem ? 16.0 : 12.0;
            level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(range),
                            target -> canStrike(player, target, range))
                    .stream().min(Comparator.comparingDouble(player::distanceToSqr))
                    .ifPresent(target -> strike(level, player, target, requiem ? 8.0F : 5.0F));
        }
    }

    public static void activate(ServerPlayer player, boolean focused) {
        if (player.isSpectator() || !player.isAlive()
                || !(player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof CatchTheRainbowMaskItem mask)
                || player.getCooldowns().isOnCooldown(mask)) {
            return;
        }
        ServerLevel level = player.serverLevel();
        if (!level.isRainingAt(player.blockPosition())) {
            return;
        }
        if (focused && mask.isRequiem()) {
            freezeRain(level, player, mask);
            return;
        }

        double range = mask.isRequiem() ? 11.0D : 8.0D;
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(range),
                entity -> canStrike(player, entity, range))) {
            strike(level, player, target, mask.isRequiem() ? 18.0F : 12.0F);
            target.knockback(2.2, player.getX() - target.getX(), player.getZ() - target.getZ());
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
        }
        for (int i = 0; i < 32; i++) {
            double angle = i * Math.PI / 16;
            level.sendParticles(ParticleTypes.SPLASH, player.getX() + Math.cos(angle) * range * 0.6,
                    player.getY() + 0.35, player.getZ() + Math.sin(angle) * range * 0.6, 4, 0.3, 0.5, 0.3, 0.15);
        }
        player.getCooldowns().addCooldown(mask, mask.isRequiem() ? 220 : 300);
        level.playSound(null, player.blockPosition(), SoundEvents.TRIDENT_THUNDER, SoundSource.PLAYERS, 1.2F, 0.8F);
    }

    private static void freezeRain(ServerLevel level, ServerPlayer player, CatchTheRainbowMaskItem mask) {
        double range = 20.0;
        Vec3 origin = player.getEyePosition();
        Vec3 direction = player.getLookAngle();
        LivingEntity target = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(range), entity -> {
                    Vec3 offset = entity.getBoundingBox().getCenter().subtract(origin);
                    return canStrike(player, entity, range) && direction.dot(offset.normalize()) >= 0.96;
                }).stream().min(Comparator.comparingDouble(player::distanceToSqr)).orElse(null);
        if (target == null) {
            player.displayClientMessage(Component.translatable("message.adaptivemobs.rain_no_target"), true);
            player.getCooldowns().addCooldown(mask, 10);
            return;
        }
        Vec3 step = target.getBoundingBox().getCenter().subtract(origin).scale(1.0 / 16.0);
        for (int i = 1; i <= 16; i++) {
            Vec3 point = origin.add(step.scale(i));
            level.sendParticles(ParticleTypes.SNOWFLAKE, point.x, point.y, point.z, 3, 0.15, 0.15, 0.15, 0.03);
        }
        strike(level, player, target, 24.0F);
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 4));
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 1));
        player.getCooldowns().addCooldown(mask, 260);
        level.playSound(null, player.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 0.9F, 0.65F);
    }

    private static boolean canStrike(ServerPlayer player, LivingEntity target, double range) {
        return target instanceof Enemy && target.isAlive() && !player.isAlliedTo(target)
                && player.distanceToSqr(target) <= range * range && player.hasLineOfSight(target);
    }

    private static void strike(ServerLevel level, ServerPlayer player, LivingEntity target, float damage) {
        target.hurt(player.damageSources().indirectMagic(player, player), damage);
        level.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY(0.6), target.getZ(), 12, 0.3, 0.4, 0.3, 0.12);
    }

    @SubscribeEvent
    public static void onPlayerHit(LivingHurtEvent event) {
        if (!event.getSource().is(DamageTypes.PLAYER_ATTACK)
                || !(event.getSource().getEntity() instanceof ServerPlayer player)
                || !(player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof CatchTheRainbowMaskItem mask)) {
            return;
        }
        boolean raining = player.level().isRainingAt(player.blockPosition());
        event.setAmount(event.getAmount() + (raining ? (mask.isRequiem() ? 4.0F : 2.5F) : 1.0F));
        if (raining) {
            event.getEntity().addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, mask.isRequiem() ? 2 : 1));
        }
    }
}
