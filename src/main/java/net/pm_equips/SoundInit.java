package net.pm_equips;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SoundInit {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, PMEquipsMain.MOD_ID);

    public static final RegistryObject<SoundEvent> ARBITER_FAIRY = register("item.arbiter_fairy");
    public static final RegistryObject<SoundEvent> ARBITER_PILLAR_START = register("item.arbiter_pillar_start");
    public static final RegistryObject<SoundEvent> ARBITER_PILLAR_SHOT = register("item.arbiter_pillar_shot");
    public static final RegistryObject<SoundEvent> ARBITER_PILLAR_END = register("item.arbiter_pillar_end");
    public static final RegistryObject<SoundEvent> EGO_LAMENT =
            register("item.ego_lament");
    public static final RegistryObject<SoundEvent> EGO_MAGIC_BULLET =
            register("item.ego_magic_bullet");
    public static final RegistryObject<SoundEvent> EGO_CRIMSON_SCAR =
            register("item.ego_crimson_scar");
    public static final RegistryObject<SoundEvent> EGO_WHITENIGHT_ATK_1 =
            register("item.ego_whitenight_atk_1");
    public static final RegistryObject<SoundEvent> EGO_WHITENIGHT_ATK_2 =
            register("item.ego_whitenight_atk_2");
    public static final RegistryObject<SoundEvent> GUN_AUTO =
            register("item.gun_auto");
    public static final RegistryObject<SoundEvent> GUN_SEMI =
            register("item.gun_semi");
    public static final RegistryObject<SoundEvent> GUN_ROLAND_REVOLVER =
            register("item.gun_roland_revolver");
    public static final RegistryObject<SoundEvent> GUN_ROLAND_SHOTGUN =
            register("item.gun_roland_shotgun");
    public static final RegistryObject<SoundEvent> ROLAND_MOOK_CHARGE =
            register("item.roland_mook_charge");
    public static final RegistryObject<SoundEvent> ROLAND_MOOK_ATTACK =
            register("item.roland_mook_attack");

    private static RegistryObject<SoundEvent> register(String name)
    {
        return SOUNDS.register(name,
                () -> SoundEvent.createVariableRangeEvent(
                        new ResourceLocation(PMEquipsMain.MOD_ID, name)
                ));
    }
}
