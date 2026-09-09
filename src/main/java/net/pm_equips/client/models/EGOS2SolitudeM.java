package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LCorpEGOL2Solitude;
import software.bernie.geckolib.model.GeoModel;

public class EGOS2SolitudeM extends GeoModel<LCorpEGOL2Solitude> {
    @Override
        public ResourceLocation getModelResource(LCorpEGOL2Solitude animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL2Solitude animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s2_solitude.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL2Solitude animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}