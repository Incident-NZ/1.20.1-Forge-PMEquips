package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.HeadArbiter;
import software.bernie.geckolib.model.GeoModel;

public class HeadArbiterArmorM extends GeoModel<HeadArbiter> {
    @Override
    public ResourceLocation getModelResource(HeadArbiter animatable) {
        return new ResourceLocation("pm_equips", "geo/armor_head_arbiter.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HeadArbiter animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/head_arbiter.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HeadArbiter animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}
