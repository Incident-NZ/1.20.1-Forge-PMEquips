package net.pm_equips.client.models;

import net.pm_equips.items.LCorpEGOL3Laetitia;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS3LaetitiaM extends GeoModel<LCorpEGOL3Laetitia> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL3Laetitia animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL3Laetitia animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s3_laetitia.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL3Laetitia animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}