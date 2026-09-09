package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.GeoArmorItem;
import software.bernie.geckolib.model.GeoModel;

public final class GeoArmorModel extends GeoModel<GeoArmorItem> {
    @Override
    public ResourceLocation getModelResource(GeoArmorItem item) {
        return item.modelResource();
    }

    @Override
    public ResourceLocation getTextureResource(GeoArmorItem item) {
        return item.textureResource();
    }

    @Override
    public ResourceLocation getAnimationResource(GeoArmorItem item) {
        return item.animationResource();
    }
}
