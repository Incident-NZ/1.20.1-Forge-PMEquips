package net.pm_equips.client.models;

import net.pm_equips.items.LCorpEGOL3IceShard;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS3IceShardM extends GeoModel<LCorpEGOL3IceShard> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL3IceShard animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL3IceShard animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s3_ice_shard.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL3IceShard animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}