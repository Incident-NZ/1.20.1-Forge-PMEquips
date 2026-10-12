package net.pm_equips.client.models;

import net.pm_equips.items.LobEGOSuit4Lamp;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS4LampM extends GeoModel<LobEGOSuit4Lamp> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit4Lamp animatable) {
        return new ResourceLocation("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit4Lamp animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/ego_s4_lamp.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit4Lamp animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}
