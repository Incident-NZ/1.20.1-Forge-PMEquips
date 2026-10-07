package net.pm_equips;

import net.pm_equips.entity.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityInit {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, PMEquipsMain.MOD_ID);

    public static final RegistryObject<EntityType<EGOStarP>> W5_SOUND_OF_A_STAR_PROJECTILE =
            ENTITY_TYPES.register("ego_sound_of_a_star_projectile",
                    () -> EntityType.Builder.<EGOStarP>of(EGOStarP::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("ego_sound_of_a_star_projectile"));

    public static final RegistryObject<EntityType<EGOHeavenP>> HEAVEN_PROJECTILE =
            ENTITY_TYPES.register("heaven_projectile", () ->
                    EntityType.Builder.<EGOHeavenP>of(
                                    EGOHeavenP::new,
                                    MobCategory.MISC
                            )
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("heaven_projectile"));

    public static final RegistryObject<EntityType<PWhiteNight>> WHITENIGHT_PROJECTILE =
            ENTITY_TYPES.register("whitenight_projectile", () ->
                    EntityType.Builder.<PWhiteNight>of(
                            PWhiteNight::new,
                            MobCategory.MISC
                            )
                            .sized(1.5F, 1.5F)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("whitenight_projectile"));

}
