package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LCDArmor;
import software.bernie.geckolib.model.GeoModel;

public class LCDArmorM extends GeoModel<LCDArmor> {
    @Override
    public ResourceLocation getModelResource(LCDArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/lcd_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCDArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/lcd_osir_armor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCDArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
