package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.HeadClawGauntlet;
import software.bernie.geckolib.model.GeoModel;

public class HeadClawM extends GeoModel<HeadClawGauntlet> {

    @Override
    public ResourceLocation getModelResource(HeadClawGauntlet animatable) {
        return new ResourceLocation("pm_equips", "geo/head_claw.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HeadClawGauntlet animatable) {
        return new ResourceLocation("pm_equips", "textures/item/head_claw.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HeadClawGauntlet animatable) {
        return new ResourceLocation("pm_equips", "animations/head_claw.animation.json");
    }
}
