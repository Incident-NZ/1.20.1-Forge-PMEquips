package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.HeadClaw;
import software.bernie.geckolib.model.GeoModel;

public class HeadClawM extends GeoModel<HeadClaw> {

    @Override
    public ResourceLocation getModelResource(HeadClaw animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/head_claw.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HeadClaw animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/item/head_claw.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HeadClaw animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/head_claw.animation.json");
    }
}
