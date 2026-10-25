package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit2Beak;
import software.bernie.geckolib.model.GeoModel;

public class EGOS2BeakM extends GeoModel<LobEGOSuit2Beak> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit2Beak animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit2Beak animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s2_beak.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit2Beak animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
