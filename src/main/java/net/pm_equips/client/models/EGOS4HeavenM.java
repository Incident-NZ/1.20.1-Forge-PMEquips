package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit4Heaven;
import software.bernie.geckolib.model.GeoModel;

public class EGOS4HeavenM extends GeoModel<LobEGOSuit4Heaven> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit4Heaven animatable) {
        return new ResourceLocation("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit4Heaven animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/ego_s4_heaven.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit4Heaven animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}
