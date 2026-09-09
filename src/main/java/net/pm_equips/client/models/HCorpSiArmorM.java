package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingsHCorpSiArmorTier1;
import software.bernie.geckolib.model.GeoModel;

public class HCorpSiArmorM extends GeoModel<WingsHCorpSiArmorTier1> {
    @Override
    public ResourceLocation getModelResource(WingsHCorpSiArmorTier1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/hcorp_si_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WingsHCorpSiArmorTier1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/hcorp_si.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WingsHCorpSiArmorTier1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}