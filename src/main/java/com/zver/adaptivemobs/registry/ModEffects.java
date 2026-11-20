package com.zver.adaptivemobs.registry;

import com.zver.adaptivemobs.AdaptiveMobsMod;
import com.zver.adaptivemobs.effect.AetherEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, AdaptiveMobsMod.MOD_ID);
    public static final RegistryObject<MobEffect> AETHER = EFFECTS.register("aether", AetherEffect::new);

    private ModEffects() {
    }
}
