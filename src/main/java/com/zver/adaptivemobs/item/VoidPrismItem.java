package com.zver.adaptivemobs.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VoidPrismItem extends Item {
    public VoidPrismItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }
        if (!level.isClientSide) {
            for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                    player.getBoundingBox().inflate(5.0D), entity -> entity != player && entity.isAlive())) {
                target.hurt(player.damageSources().magic(), 8.0F);
                target.setDeltaMovement(target.getDeltaMovement().scale(0.25D));
            }
            player.getCooldowns().addCooldown(this, 240);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
