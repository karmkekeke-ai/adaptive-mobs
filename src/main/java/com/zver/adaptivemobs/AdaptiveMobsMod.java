package com.zver.adaptivemobs;

import com.mojang.logging.LogUtils;
import com.zver.adaptivemobs.registry.ModEffects;
import com.zver.adaptivemobs.registry.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(AdaptiveMobsMod.MOD_ID)
public class AdaptiveMobsMod {
    public static final String MOD_ID = "adaptivemobs";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AdaptiveMobsMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(modBus);
        ModEffects.EFFECTS.register(modBus);
        modBus.addListener(this::addCreativeItems);
        LOGGER.info("Adaptive Mobs initialized");
    }

    private void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT || event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.VOID_PRISM);
            event.accept(ModItems.AEGIS_CORE);
            event.accept(ModItems.PULSE_SEAL);
            event.accept(ModItems.GRAVITY_THREAD);
            event.accept(ModItems.CATCH_THE_RAINBOW_MASK);
        }
    }
}
