package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit3Harvest;
import software.bernie.geckolib.model.GeoModel;

public class EGOS3HarvestM extends GeoModel<LobEGOSuit3Harvest> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit3Harvest animatable) {
        return new ResourceLocation("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit3Harvest animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/ego_s3_harvest.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit3Harvest animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}
