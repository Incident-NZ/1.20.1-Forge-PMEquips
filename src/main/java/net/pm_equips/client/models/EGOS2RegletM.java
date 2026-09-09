package net.pm_equips.client.models;

import net.pm_equips.items.LCorpEGOL2Reglet;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS2RegletM extends GeoModel<LCorpEGOL2Reglet> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL2Reglet animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL2Reglet animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s2_reglet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL2Reglet animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}