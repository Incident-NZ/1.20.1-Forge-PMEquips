package net.pm_equips;

import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.pm_equips.effects.CustomMobEffects;
import net.pm_equips.effects.SingularityEffects;

public class PMERegistryMobEffect {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, PMEquipsMain.MOD_ID);

    public static final DeferredHolder<MobEffect, ? extends MobEffect> QUICK = MOB_EFFECTS.register("effect_quick", CustomMobEffects.QuickEffect::new);
    public static final DeferredHolder<MobEffect, ? extends MobEffect> BIND = MOB_EFFECTS.register("effect_bind", CustomMobEffects.BindEffect::new);
    public static final DeferredHolder<MobEffect, ? extends MobEffect> POWER = MOB_EFFECTS.register("effect_power", CustomMobEffects.PowerEffect::new);
    public static final DeferredHolder<MobEffect, ? extends MobEffect> WEAK = MOB_EFFECTS.register("effect_weak", CustomMobEffects.WeakEffect::new);
    public static final DeferredHolder<MobEffect, ? extends MobEffect> ENDURANCE = MOB_EFFECTS.register("effect_endurance", CustomMobEffects.EnduranceEffect::new);
    public static final DeferredHolder<MobEffect, ? extends MobEffect> DISARM = MOB_EFFECTS.register("effect_disarm", CustomMobEffects.DisarmEffect::new);
    public static final DeferredHolder<MobEffect, ? extends MobEffect> BLEED = MOB_EFFECTS.register("effect_bleed", CustomMobEffects.BleedEffect::new);
    public static final DeferredHolder<MobEffect, ? extends MobEffect> PARALYSIS = MOB_EFFECTS.register("effect_paralysis", CustomMobEffects.ParalysisEffect::new);
    public static final DeferredHolder<MobEffect, ? extends MobEffect> FAIRY = MOB_EFFECTS.register("effect_fairy", CustomMobEffects.FairyEffect::new);

    public static final DeferredHolder<MobEffect, ? extends MobEffect> KCORP_SIN = MOB_EFFECTS.register("effect_sin_kcorp", SingularityEffects.KCorpSingularityEffect::new);
    public static final DeferredHolder<MobEffect, ? extends MobEffect> RCORP_SIN = MOB_EFFECTS.register("effect_sin_rcorp", SingularityEffects.RCorpSingularityEffect::new);
    public static final DeferredHolder<MobEffect, ? extends MobEffect> WCORP_SIN = MOB_EFFECTS.register("effect_sin_wcorp", SingularityEffects.WCorpSingularityEffect::new);

}