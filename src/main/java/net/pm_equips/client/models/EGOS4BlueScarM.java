package net.pm_equips.client.models;

import net.pm_equips.items.LCorpEGOL4BlueScar;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS4BlueScarM extends GeoModel<LCorpEGOL4BlueScar> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL4BlueScar animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL4BlueScar animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s4_blue_scar.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL4BlueScar animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}
