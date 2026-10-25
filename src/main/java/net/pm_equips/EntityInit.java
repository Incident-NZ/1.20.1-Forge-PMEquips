package net.pm_equips;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pm_equips.entity.*;

public class EntityInit {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, PMEquipsMain.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<MobNPC1>> MOB_NPC_1 =
            ENTITY_TYPES.register("mob_npc_1", () -> EntityType.Builder.of(MobNPC1::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.8F).eyeHeight(1.62F).clientTrackingRange(10).updateInterval(3)
                    .build("pm_equips:mob_npc_1"));

    public static final DeferredHolder<EntityType<?>, EntityType<EGOStarP>> W5_SOUND_OF_A_STAR_PROJECTILE =
            ENTITY_TYPES.register("ego_sound_of_a_star_projectile",
                    () -> EntityType.Builder.<EGOStarP>of(EGOStarP::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("ego_sound_of_a_star_projectile"));

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
