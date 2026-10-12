package net.pm_equips.client.models;

import net.pm_equips.items.LobEGOSuit3MagicBullet;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS3MagicBulletM extends GeoModel<LobEGOSuit3MagicBullet> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit3MagicBullet animatable) {
        return new ResourceLocation("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit3MagicBullet animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/ego_s3_magic_bullet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit3MagicBullet animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}
