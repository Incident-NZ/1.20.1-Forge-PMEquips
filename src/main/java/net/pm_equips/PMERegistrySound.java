package net.pm_equips;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class PMERegistrySound {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, PMEquipsMain.MOD_ID);

    public static final DeferredHolder<SoundEvent, ? extends SoundEvent> EGO_LAMENT =
            register("item.ego_lament");
    public static final DeferredHolder<SoundEvent, ? extends SoundEvent> EGO_MAGIC_BULLET =
            register("item.ego_magic_bullet");
    public static final DeferredHolder<SoundEvent, ? extends SoundEvent> EGO_CRIMSON_SCAR =
            register("item.ego_crimson_scar");
    public static final DeferredHolder<SoundEvent, ? extends SoundEvent> EGO_WHITENIGHT_ATK_1 =
            register("item.ego_whitenight_atk_1");
    public static final DeferredHolder<SoundEvent, ? extends SoundEvent> EGO_WHITENIGHT_ATK_2 =
            register("item.ego_whitenight_atk_2");
    public static final DeferredHolder<SoundEvent, ? extends SoundEvent> GUN_AUTO =
            register("item.gun_auto");
    public static final DeferredHolder<SoundEvent, ? extends SoundEvent> GUN_SEMI =
            register("item.gun_semi");
    public static final DeferredHolder<SoundEvent, ? extends SoundEvent> GUN_ROLAND_REVOLVER =
            register("item.gun_roland_revolver");
    public static final DeferredHolder<SoundEvent, ? extends SoundEvent> GUN_ROLAND_SHOTGUN =
            register("item.gun_roland_shotgun");
    public static final DeferredHolder<SoundEvent, ? extends SoundEvent> ROLAND_MOOK_CHARGE =
            register("item.roland_mook_charge");
    public static final DeferredHolder<SoundEvent, ? extends SoundEvent> ROLAND_MOOK_ATTACK =
            register("item.roland_mook_attack");

    private static DeferredHolder<SoundEvent, ? extends SoundEvent> register(String name)
    {
        return SOUNDS.register(name,
                () -> SoundEvent.createVariableRangeEvent(
                        ResourceLocation.fromNamespaceAndPath(PMEquipsMain.MOD_ID, name)
                ));
    }
}
