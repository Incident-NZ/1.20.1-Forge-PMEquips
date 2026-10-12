package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.HeadCCorpGauntlet;
import software.bernie.geckolib.model.GeoModel;

public class HeadClawM extends GeoModel<HeadCCorpGauntlet> {

    @Override
    public ResourceLocation getModelResource(HeadCCorpGauntlet animatable) {
        return new ResourceLocation("pm_equips", "geo/head_claw.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HeadCCorpGauntlet animatable) {
        return new ResourceLocation("pm_equips", "textures/item/head_claw.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HeadCCorpGauntlet animatable) {
        return new ResourceLocation("pm_equips", "animations/head_claw.animation.json");
    }
}
