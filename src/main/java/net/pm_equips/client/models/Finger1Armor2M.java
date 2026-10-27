package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.Finger1Armor2;
import software.bernie.geckolib.model.GeoModel;

public class Finger1Armor2M extends GeoModel<Finger1Armor2> {
    @Override
    public ResourceLocation getModelResource(Finger1Armor2 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/finger1_armor2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(Finger1Armor2 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/finger1_armor2.png");
    }

    @Override
    public ResourceLocation getAnimationResource(Finger1Armor2 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
