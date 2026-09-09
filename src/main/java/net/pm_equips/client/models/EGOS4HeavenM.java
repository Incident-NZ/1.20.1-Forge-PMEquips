package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LCorpEGOL4Heaven;
import software.bernie.geckolib.model.GeoModel;

public class EGOS4HeavenM extends GeoModel<LCorpEGOL4Heaven> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL4Heaven animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL4Heaven animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s4_heaven.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL4Heaven animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}
