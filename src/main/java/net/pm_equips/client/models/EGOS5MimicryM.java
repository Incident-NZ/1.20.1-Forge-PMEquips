package net.pm_equips.client.models;

import net.pm_equips.items.LCorpEGOL5Mimicry;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS5MimicryM extends GeoModel<LCorpEGOL5Mimicry> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL5Mimicry animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL5Mimicry animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s5_mimicry.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL5Mimicry animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}
