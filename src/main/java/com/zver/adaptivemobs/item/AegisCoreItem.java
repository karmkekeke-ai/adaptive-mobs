package com.zver.adaptivemobs.item;

import com.zver.adaptivemobs.registry.ModEffects;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AegisCoreItem extends Item {
    public AegisCoreItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && !player.getCooldowns().isOnCooldown(this)) {
            player.addEffect(new MobEffectInstance(ModEffects.AETHER.get(), 240, 1, false, true));
            player.getCooldowns().addCooldown(this, 600);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
