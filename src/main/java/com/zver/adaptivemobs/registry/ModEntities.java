package com.zver.adaptivemobs.registry;

import com.zver.adaptivemobs.AdaptiveMobsMod;
import com.zver.adaptivemobs.entity.ProtagonistEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = AdaptiveMobsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, AdaptiveMobsMod.MOD_ID);
    public static final RegistryObject<EntityType<ProtagonistEntity>> PROTAGONIST = ENTITIES.register(
            "protagonist", () -> EntityType.Builder.of(ProtagonistEntity::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.25F)
                    .clientTrackingRange(8)
                    .noSummon()
                    .build(AdaptiveMobsMod.MOD_ID + ":protagonist"));

    private ModEntities() {
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        if (PROTAGONIST.isPresent()) {
            event.put(PROTAGONIST.get(), ProtagonistEntity.createAttributes().build());
        }
    }
}
