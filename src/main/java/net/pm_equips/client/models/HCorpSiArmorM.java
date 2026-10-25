package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingHCorpSiArmor;
import software.bernie.geckolib.model.GeoModel;

public class HCorpSiArmorM extends GeoModel<WingHCorpSiArmor> {
    @Override
    public ResourceLocation getModelResource(WingHCorpSiArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/hcorp_si_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WingHCorpSiArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/hcorp_si.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WingHCorpSiArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
