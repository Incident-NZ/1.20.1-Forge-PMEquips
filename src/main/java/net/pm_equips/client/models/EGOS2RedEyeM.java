package net.pm_equips.client.models;

import net.pm_equips.items.LobEGOSuit2RedEye;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS2RedEyeM extends GeoModel<LobEGOSuit2RedEye> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit2RedEye animatable) {
        return new ResourceLocation("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit2RedEye animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/ego_s2_red_eye.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit2RedEye animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}