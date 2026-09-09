package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingsKCorpOfficerArmorTier1;
import software.bernie.geckolib.model.GeoModel;

public class KCorpOfficerArmorM extends GeoModel<WingsKCorpOfficerArmorTier1> {

    @Override
    public ResourceLocation getModelResource(WingsKCorpOfficerArmorTier1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/kcorp_officer_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WingsKCorpOfficerArmorTier1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/kcorp_officer_armor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WingsKCorpOfficerArmorTier1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
