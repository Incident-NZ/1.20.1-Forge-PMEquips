package net.pm_equips.client.models;

import net.pm_equips.items.LCorpEGOL2Beak;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS2BeakM extends GeoModel<LCorpEGOL2Beak> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL2Beak animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL2Beak animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s2_beak.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL2Beak animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}