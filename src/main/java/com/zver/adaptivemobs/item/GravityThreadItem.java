package com.zver.adaptivemobs.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GravityThreadItem extends Item {
    public GravityThreadItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && !player.getCooldowns().isOnCooldown(this)) {
            for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(6.0D), entity -> entity != player && entity.isAlive())) {
                target.setDeltaMovement(target.getDeltaMovement().x, 0.8D, target.getDeltaMovement().z);
                target.hurt(player.damageSources().magic(), 5.0F);
            }
            player.getCooldowns().addCooldown(this, 220);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
