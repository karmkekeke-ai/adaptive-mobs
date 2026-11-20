package com.zver.adaptivemobs.registry;

import com.zver.adaptivemobs.AdaptiveMobsMod;
import com.zver.adaptivemobs.item.AegisCoreItem;
import com.zver.adaptivemobs.item.CatchTheRainbowMaskItem;
import com.zver.adaptivemobs.item.GravityThreadItem;
import com.zver.adaptivemobs.item.HamonBandItem;
import com.zver.adaptivemobs.item.NaoiZeninCharmItem;
import com.zver.adaptivemobs.item.PulseSealItem;
import com.zver.adaptivemobs.item.StandArrowItem;
import com.zver.adaptivemobs.item.VoidPrismItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AdaptiveMobsMod.MOD_ID);

    public static final RegistryObject<Item> VOID_PRISM = ITEMS.register("void_prism",
            () -> new VoidPrismItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> AEGIS_CORE = ITEMS.register("aegis_core",
            () -> new AegisCoreItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PULSE_SEAL = ITEMS.register("pulse_seal",
            () -> new PulseSealItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GRAVITY_THREAD = ITEMS.register("gravity_thread",
            () -> new GravityThreadItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CATCH_THE_RAINBOW_MASK = ITEMS.register("catch_the_rainbow_mask",
            () -> new CatchTheRainbowMaskItem(false, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> CATCH_THE_RAINBOW_REQUIEM = ITEMS.register("catch_the_rainbow_requiem",
            () -> new CatchTheRainbowMaskItem(true, new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> STAND_ARROW = ITEMS.register("stand_arrow",
            () -> new StandArrowItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HAMON_BAND = ITEMS.register("hamon_band",
            () -> new HamonBandItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> NAOI_ZENIN_CHARM = ITEMS.register("naoi_zenin_charm",
            () -> new NaoiZeninCharmItem(new Item.Properties().stacksTo(1)));

    private ModItems() {
    }
}
