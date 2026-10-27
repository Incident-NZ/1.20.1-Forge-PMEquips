package net.pm_equips.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.pm_equips.entity.PArbiterPillar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Forge 1.20.1 has no post-LivingEntity tick event; apply the anchor after vanilla movement. */
@Mixin(LivingEntity.class)
public class FeatureRestraintMixin {
    @Inject(method="tick",at=@At("TAIL"))
    private void pmEquips$finishRestraint(CallbackInfo ci){PArbiterPillar.afterLivingTick((LivingEntity)(Object)this);}
}
