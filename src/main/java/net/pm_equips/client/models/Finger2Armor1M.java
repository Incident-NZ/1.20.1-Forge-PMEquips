package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.Finger2Armor1;
import software.bernie.geckolib.model.GeoModel;

public class Finger2Armor1M extends GeoModel<Finger2Armor1> {
    @Override
    public ResourceLocation getModelResource(Finger2Armor1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/finger2_armor1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Finger2Armor1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/finger2_armor1.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Finger2Armor1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
