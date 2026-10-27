package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.Finger2Armor2;
import software.bernie.geckolib.model.GeoModel;

public class Finger2Armor2M extends GeoModel<Finger2Armor2> {
    @Override
    public ResourceLocation getModelResource(Finger2Armor2 animatable) {
        return new ResourceLocation("pm_equips", "geo/finger2_armor2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Finger2Armor2 animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/finger2_armor2.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Finger2Armor2 animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}
