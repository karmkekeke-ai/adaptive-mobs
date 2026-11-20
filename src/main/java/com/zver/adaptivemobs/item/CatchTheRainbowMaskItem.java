package com.zver.adaptivemobs.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CatchTheRainbowMaskItem extends ArmorItem {
    private final boolean requiem;

    public CatchTheRainbowMaskItem(boolean requiem, Properties properties) {
        super(ArmorMaterials.NETHERITE, Type.HELMET, properties.stacksTo(1));
        this.requiem = requiem;
    }

    public boolean isRequiem() {
        return requiem;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        String name = requiem ? "catch_the_rainbow_requiem" : "catch_the_rainbow_mask";
        return "adaptivemobs:textures/models/armor/" + name + ".png";
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(getDescriptionId() + ".tooltip").withStyle(ChatFormatting.GRAY));
    }
}
