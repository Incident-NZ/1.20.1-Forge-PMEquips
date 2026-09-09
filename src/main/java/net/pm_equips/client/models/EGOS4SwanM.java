package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LCorpEGOL4Swan;
import software.bernie.geckolib.model.GeoModel;

public class EGOS4SwanM extends GeoModel<LCorpEGOL4Swan> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL4Swan animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL4Swan animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s4_swan.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL4Swan animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}
