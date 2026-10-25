package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit3IceShard;
import software.bernie.geckolib.model.GeoModel;

public class EGOS3IceShardM extends GeoModel<LobEGOSuit3IceShard> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit3IceShard animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit3IceShard animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s3_ice_shard.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit3IceShard animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
