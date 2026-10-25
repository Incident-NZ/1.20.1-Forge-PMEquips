package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingKCorpOfficerArmor;
import software.bernie.geckolib.model.GeoModel;

public class KCorpOfficerArmorM extends GeoModel<WingKCorpOfficerArmor> {

    @Override
    public ResourceLocation getModelResource(WingKCorpOfficerArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/kcorp_officer_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WingKCorpOfficerArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/kcorp_officer_armor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WingKCorpOfficerArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
