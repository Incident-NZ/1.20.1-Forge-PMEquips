package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit2Today;
import software.bernie.geckolib.model.GeoModel;

public class EGOS2TodayM extends GeoModel<LobEGOSuit2Today> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit2Today animatable) {
        return new ResourceLocation("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit2Today animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/ego_s2_today.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit2Today animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}