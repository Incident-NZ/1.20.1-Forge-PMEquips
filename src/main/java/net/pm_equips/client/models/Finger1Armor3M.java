package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.Finger1Armor3;
import software.bernie.geckolib.model.GeoModel;

public class Finger1Armor3M extends GeoModel<Finger1Armor3> {
    @Override
    public ResourceLocation getModelResource(Finger1Armor3 animatable) {
        return new ResourceLocation("pm_equips", "geo/finger1_armor3.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Finger1Armor3 animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/finger1_armor3.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Finger1Armor3 animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}
