package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingsWCorpArmorTier1;
import software.bernie.geckolib.model.GeoModel;

public class WCorpArmorM extends GeoModel<WingsWCorpArmorTier1> {

    @Override
    public ResourceLocation getModelResource(WingsWCorpArmorTier1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/wcorp_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WingsWCorpArmorTier1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/wcorp_armor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WingsWCorpArmorTier1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
