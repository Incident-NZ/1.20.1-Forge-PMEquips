package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LCCBArmor;
import software.bernie.geckolib.model.GeoModel;

public class LCCBArmorM extends GeoModel<LCCBArmor> {

    @Override
    public ResourceLocation getModelResource(LCCBArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/lccb_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCCBArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/lccb_armor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCCBArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
