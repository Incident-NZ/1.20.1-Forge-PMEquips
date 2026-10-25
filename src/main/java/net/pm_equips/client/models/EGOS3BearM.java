package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit3Bear;
import software.bernie.geckolib.model.GeoModel;

public class EGOS3BearM extends GeoModel<LobEGOSuit3Bear> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit3Bear animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit3Bear animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s3_bear.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit3Bear animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
