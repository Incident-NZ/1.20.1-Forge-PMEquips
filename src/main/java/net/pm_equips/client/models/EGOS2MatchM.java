package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit2Match;
import software.bernie.geckolib.model.GeoModel;

public class EGOS2MatchM extends GeoModel<LobEGOSuit2Match> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit2Match animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit2Match animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s2_match.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit2Match animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
