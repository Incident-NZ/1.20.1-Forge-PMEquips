package net.pm_equips;

import net.pm_equips.entity.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class PMERegistryEntity {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, PMEquipsMain.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<EGOStarP>> W5_SOUND_OF_A_STAR_PROJECTILE =
            ENTITY_TYPES.register("ego_sound_of_a_star_projectile",
                    () -> EntityType.Builder.<EGOStarP>of(EGOStarP::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("ego_sound_of_a_star_projectile"));

    public static final DeferredHolder<EntityType<?>, EntityType<EGOMagicP>> MAGIC_BULLET =
            ENTITY_TYPES.register("magic_bullet", () ->
                    EntityType.Builder.<EGOMagicP>of(
                                    EGOMagicP::new,
                                    MobCategory.MISC
                            )
                            .sized(0.1F, 0.1F)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("magic_bullet"));

    public static final DeferredHolder<EntityType<?>, EntityType<AmmoGun>> BULLET =
            ENTITY_TYPES.register("bullet", () ->
                    EntityType.Builder.<AmmoGun>of(
                                    AmmoGun::new,
                                    MobCategory.MISC
                            )
                            .sized(0.3F, 0.3F)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("bullet"));

    public static final DeferredHolder<EntityType<?>, EntityType<EGOHeavenP>> HEAVEN_PROJECTILE =
            ENTITY_TYPES.register("heaven_projectile", () ->
                    EntityType.Builder.<EGOHeavenP>of(
                                    EGOHeavenP::new,
                                    MobCategory.MISC
                            )
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("heaven_projectile"));

    public static final DeferredHolder<EntityType<?>, EntityType<EGOHatredMagicP>> HATRED_MAGIC_PROJECTILE =
            ENTITY_TYPES.register("hatred_magic_projectile", () ->
                    EntityType.Builder.<EGOHatredMagicP>of(
                                    EGOHatredMagicP::new,
                                    MobCategory.MISC
                            )
                            .sized(0.1F, 0.1F)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("hatred_magic_projectile"));

    public static final DeferredHolder<EntityType<?>, EntityType<AmmoLogicRV>> BULLET_LARV =
            ENTITY_TYPES.register("bullet_larv", () ->
                    EntityType.Builder.<AmmoLogicRV>of(
                                    AmmoLogicRV::new,
                                    MobCategory.MISC
                            )
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("bullet_larv"));

    public static final DeferredHolder<EntityType<?>, EntityType<AmmoLogicSG>> BULLET_LASG =
            ENTITY_TYPES.register("bullet_lasg", () ->
                    EntityType.Builder.<AmmoLogicSG>of(
                                    AmmoLogicSG::new,
                                    MobCategory.MISC
                            )
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("bullet_lasg"));

    public static final DeferredHolder<EntityType<?>, EntityType<AmmoExp>> BULLET_EX =
            ENTITY_TYPES.register("bullet_ex", () ->
                    EntityType.Builder.<AmmoExp>of(
                            AmmoExp::new,
                            MobCategory.MISC
                            )
                            .sized(1.5F, 1.5F)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("bullet_ex"));

    public static final DeferredHolder<EntityType<?>, EntityType<PWhiteNight>> WHITENIGHT_PROJECTILE =
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
