package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.HeadArbiterArmor;
import software.bernie.geckolib.model.GeoModel;

public class HeadArbiterArmorM extends GeoModel<HeadArbiterArmor> {
    @Override
    public ResourceLocation getModelResource(HeadArbiterArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/armor_arbiter.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(HeadArbiterArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/head_arbiter.png");
    }

    @Override
    public ResourceLocation getAnimationResource(HeadArbiterArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
