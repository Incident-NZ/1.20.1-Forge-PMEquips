package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingWCorpArmor;
import software.bernie.geckolib.model.GeoModel;

public class WCorpArmorM extends GeoModel<WingWCorpArmor> {

    @Override
    public ResourceLocation getModelResource(WingWCorpArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/wcorp_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WingWCorpArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/wcorp_armor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WingWCorpArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
