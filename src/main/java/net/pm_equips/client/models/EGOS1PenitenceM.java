package net.pm_equips.client.models;

import net.pm_equips.items.LCorpEGOL1Penitence;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS1PenitenceM extends GeoModel<LCorpEGOL1Penitence> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL1Penitence animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL1Penitence animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s1_penitence.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL1Penitence animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}