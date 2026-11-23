package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingHCorpArmor3;
import software.bernie.geckolib.model.GeoModel;

public class HCorpYouArmorM extends GeoModel<WingHCorpArmor3> {
    @Override
    public ResourceLocation getModelResource(WingHCorpArmor3 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/hcorp_you_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WingHCorpArmor3 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/hcorp_you.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WingHCorpArmor3 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
