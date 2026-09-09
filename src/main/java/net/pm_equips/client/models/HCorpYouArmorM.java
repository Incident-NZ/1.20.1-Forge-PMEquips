package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingsHCorpYouArmorTier1;
import software.bernie.geckolib.model.GeoModel;

public class HCorpYouArmorM extends GeoModel<WingsHCorpYouArmorTier1> {
    @Override
    public ResourceLocation getModelResource(WingsHCorpYouArmorTier1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/hcorp_you_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WingsHCorpYouArmorTier1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/hcorp_you.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WingsHCorpYouArmorTier1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}