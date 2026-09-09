package net.pm_equips.client.models;

import net.pm_equips.items.LCorpEGOL3MagicBullet;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS3MagicBulletM extends GeoModel<LCorpEGOL3MagicBullet> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL3MagicBullet animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL3MagicBullet animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s3_magic_bullet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL3MagicBullet animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}
