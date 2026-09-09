package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingsHCorpMaoArmorTier1;
import software.bernie.geckolib.model.GeoModel;

public class HCorpMaoArmorM extends GeoModel<WingsHCorpMaoArmorTier1> {
    @Override
    public ResourceLocation getModelResource(WingsHCorpMaoArmorTier1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/hcorp_mao_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WingsHCorpMaoArmorTier1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/hcorp_mao.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WingsHCorpMaoArmorTier1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}