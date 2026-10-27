package net.pm_equips.mixin;

import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RangedAttribute.class)
public class FeatureArmorAttributeMixin {
    @Shadow @Final @Mutable private double maxValue;
    @Inject(method="<init>",at=@At("TAIL"))
    private void pmEquips$allowFiftyArmor(String description,double initial,double min,double max,CallbackInfo ci){
        if(description.equals("attribute.name.generic.armor")||description.equals("attribute.name.generic.armor_toughness"))
            maxValue=Math.max(maxValue,50);
    }
}
