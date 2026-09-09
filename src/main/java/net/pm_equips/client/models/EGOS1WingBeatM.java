package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LCorpEGOL1WingBeat;
import software.bernie.geckolib.model.GeoModel;

public class EGOS1WingBeatM extends GeoModel<LCorpEGOL1WingBeat> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL1WingBeat animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL1WingBeat animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s1_wingbeat.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL1WingBeat animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}