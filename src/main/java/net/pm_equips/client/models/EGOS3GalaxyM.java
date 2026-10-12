package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit3Galaxy;
import software.bernie.geckolib.model.GeoModel;

public class EGOS3GalaxyM extends GeoModel<LobEGOSuit3Galaxy> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit3Galaxy animatable) {
        return new ResourceLocation("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit3Galaxy animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/ego_s3_galaxy.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit3Galaxy animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}
