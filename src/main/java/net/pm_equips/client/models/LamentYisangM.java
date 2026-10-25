package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LimbusEGOYisangLament;
import software.bernie.geckolib.model.GeoModel;

public class LamentYisangM extends GeoModel<LimbusEGOYisangLament> {

    @Override
    public ResourceLocation getModelResource(LimbusEGOYisangLament animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/lament_yisang.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LimbusEGOYisangLament animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/item/item_lament_yisang.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LimbusEGOYisangLament animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/item_lament_yisang.animation.json");
    }
}
