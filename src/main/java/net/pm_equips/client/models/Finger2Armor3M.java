package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.Finger2Armor3;
import software.bernie.geckolib.model.GeoModel;

public class Finger2Armor3M extends GeoModel<Finger2Armor3> {
    @Override
    public ResourceLocation getModelResource(Finger2Armor3 animatable) {
        return new ResourceLocation("pm_equips", "geo/finger2_armor3.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Finger2Armor3 animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/finger2_armor3.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Finger2Armor3 animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}
