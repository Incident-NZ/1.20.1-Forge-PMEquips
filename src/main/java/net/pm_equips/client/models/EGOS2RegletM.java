package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit2Reglet;
import software.bernie.geckolib.model.GeoModel;

public class EGOS2RegletM extends GeoModel<LobEGOSuit2Reglet> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit2Reglet animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit2Reglet animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s2_reglet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit2Reglet animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
