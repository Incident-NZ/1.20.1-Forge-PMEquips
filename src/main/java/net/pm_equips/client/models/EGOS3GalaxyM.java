package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LCorpEGOL3Galaxy;
import software.bernie.geckolib.model.GeoModel;

public class EGOS3GalaxyM extends GeoModel<LCorpEGOL3Galaxy> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL3Galaxy animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL3Galaxy animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s3_galaxy.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL3Galaxy animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}
