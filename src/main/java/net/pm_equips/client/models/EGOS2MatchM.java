package net.pm_equips.client.models;

import net.pm_equips.items.LCorpEGOL2Match;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS2MatchM extends GeoModel<LCorpEGOL2Match> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL2Match animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL2Match animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s2_match.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL2Match animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}