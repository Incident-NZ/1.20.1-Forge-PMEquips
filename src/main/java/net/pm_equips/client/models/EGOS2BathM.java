package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LCorpEGOL2Bath;
import software.bernie.geckolib.model.GeoModel;

public class EGOS2BathM extends GeoModel<LCorpEGOL2Bath> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL2Bath animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL2Bath animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s2_bath.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL2Bath animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}