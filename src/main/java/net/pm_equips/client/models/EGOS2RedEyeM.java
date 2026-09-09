package net.pm_equips.client.models;

import net.pm_equips.items.LCorpEGOL2RedEye;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS2RedEyeM extends GeoModel<LCorpEGOL2RedEye> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL2RedEye animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL2RedEye animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s2_red_eye.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL2RedEye animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}