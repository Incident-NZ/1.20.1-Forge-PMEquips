package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit2Solitude;
import software.bernie.geckolib.model.GeoModel;

public class EGOS2SolitudeM extends GeoModel<LobEGOSuit2Solitude> {
    @Override
        public ResourceLocation getModelResource(LobEGOSuit2Solitude animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit2Solitude animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s2_solitude.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit2Solitude animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
