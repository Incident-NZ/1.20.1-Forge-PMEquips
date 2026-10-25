package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit3Logging;
import software.bernie.geckolib.model.GeoModel;

public class EGOS3LoggingM extends GeoModel<LobEGOSuit3Logging> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit3Logging animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit3Logging animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s3_logging.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit3Logging animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
