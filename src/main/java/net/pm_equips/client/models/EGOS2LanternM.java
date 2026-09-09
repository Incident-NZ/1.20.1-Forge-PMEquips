package net.pm_equips.client.models;

import net.pm_equips.items.LCorpEGOL2Lantern;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS2LanternM extends GeoModel<LCorpEGOL2Lantern> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL2Lantern animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL2Lantern animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s2_lantern.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL2Lantern animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}