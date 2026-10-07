package net.pm_equips;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pm_equips.effects.CustomMobEffects;
import net.pm_equips.effects.SingularityEffects;

public class MobEffectInit {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, PMEquipsMain.MOD_ID);

    public static final RegistryObject<MobEffect> QUICK = MOB_EFFECTS.register("effect_quick", CustomMobEffects.QuickEffect::new);
    public static final RegistryObject<MobEffect> BIND = MOB_EFFECTS.register("effect_bind", CustomMobEffects.BindEffect::new);
    public static final RegistryObject<MobEffect> POWER = MOB_EFFECTS.register("effect_power", CustomMobEffects.PowerEffect::new);
    public static final RegistryObject<MobEffect> WEAK = MOB_EFFECTS.register("effect_weak", CustomMobEffects.WeakEffect::new);
    public static final RegistryObject<MobEffect> ENDURANCE = MOB_EFFECTS.register("effect_endurance", CustomMobEffects.EnduranceEffect::new);
    public static final RegistryObject<MobEffect> DISARM = MOB_EFFECTS.register("effect_disarm", CustomMobEffects.DisarmEffect::new);
    public static final RegistryObject<MobEffect> BLEED = MOB_EFFECTS.register("effect_bleed", CustomMobEffects.BleedEffect::new);
    public static final RegistryObject<MobEffect> PARALYSIS = MOB_EFFECTS.register("effect_paralysis", CustomMobEffects.ParalysisEffect::new);
    public static final RegistryObject<MobEffect> FAIRY = MOB_EFFECTS.register("effect_fairy", CustomMobEffects.FairyEffect::new);
    public static final RegistryObject<MobEffect> BARRIER = MOB_EFFECTS.register("effect_barrier", CustomMobEffects.BarrierEffect::new);

    public static final RegistryObject<MobEffect> KCORP_SIN = MOB_EFFECTS.register("effect_sin_kcorp", SingularityEffects.KCorpSingularityEffect::new);
    public static final RegistryObject<MobEffect> RCORP_SIN = MOB_EFFECTS.register("effect_sin_rcorp", SingularityEffects.RCorpSingularityEffect::new);
    public static final RegistryObject<MobEffect> WCORP_SIN = MOB_EFFECTS.register("effect_sin_wcorp", SingularityEffects.WCorpSingularityEffect::new);

}