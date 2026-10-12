package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LCEEGODimensionShredderArmor;
import software.bernie.geckolib.model.GeoModel;

public class LCEEGODimensionShredderArmorM extends GeoModel<LCEEGODimensionShredderArmor> {
    @Override
    public ResourceLocation getModelResource(LCEEGODimensionShredderArmor animatable) {
        return new ResourceLocation("pm_equips", "geo/lce_ego_1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCEEGODimensionShredderArmor animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/lce_ego_1.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCEEGODimensionShredderArmor animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}
