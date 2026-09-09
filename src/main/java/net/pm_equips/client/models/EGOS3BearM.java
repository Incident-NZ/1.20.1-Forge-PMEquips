package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LCorpEGOL3Bear;
import software.bernie.geckolib.model.GeoModel;

public class EGOS3BearM extends GeoModel<LCorpEGOL3Bear> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL3Bear animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL3Bear animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s3_bear.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL3Bear animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}
