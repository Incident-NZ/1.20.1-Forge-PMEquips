package net.pm_equips.client.models;

import net.pm_equips.items.LobEGOSuit4BlueScar;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS4BlueScarM extends GeoModel<LobEGOSuit4BlueScar> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit4BlueScar animatable) {
        return new ResourceLocation("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit4BlueScar animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/ego_s4_blue_scar.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit4BlueScar animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}
