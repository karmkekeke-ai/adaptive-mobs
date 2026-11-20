package com.zver.adaptivemobs.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PulseSealItem extends Item {
    public PulseSealItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && !player.getCooldowns().isOnCooldown(this)) {
            for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(7.0D), entity -> entity != player && entity.isAlive())) {
                target.knockback(1.8D, player.getX() - target.getX(), player.getZ() - target.getZ());
                target.hurt(player.damageSources().playerAttack(player), 4.0F);
            }
            player.getCooldowns().addCooldown(this, 160);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
