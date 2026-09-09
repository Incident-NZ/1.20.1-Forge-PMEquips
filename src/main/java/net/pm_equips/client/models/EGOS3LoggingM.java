package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LCorpEGOL3Logging;
import software.bernie.geckolib.model.GeoModel;

public class EGOS3LoggingM extends GeoModel<LCorpEGOL3Logging> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL3Logging animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL3Logging animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s3_logging.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL3Logging animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}
