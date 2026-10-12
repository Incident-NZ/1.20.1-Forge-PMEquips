package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit4Swan;
import software.bernie.geckolib.model.GeoModel;

public class EGOS4SwanM extends GeoModel<LobEGOSuit4Swan> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit4Swan animatable) {
        return new ResourceLocation("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit4Swan animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/ego_s4_swan.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit4Swan animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}
