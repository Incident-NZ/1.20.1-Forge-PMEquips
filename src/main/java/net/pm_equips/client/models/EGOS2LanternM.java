package net.pm_equips.client.models;

import net.pm_equips.items.LobEGOSuit2Lantern;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS2LanternM extends GeoModel<LobEGOSuit2Lantern> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit2Lantern animatable) {
        return new ResourceLocation("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit2Lantern animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/ego_s2_lantern.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit2Lantern animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}