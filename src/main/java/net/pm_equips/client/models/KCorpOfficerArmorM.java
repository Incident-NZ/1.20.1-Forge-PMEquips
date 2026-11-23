package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingKCorpArmor1;
import software.bernie.geckolib.model.GeoModel;

public class KCorpOfficerArmorM extends GeoModel<WingKCorpArmor1> {

    @Override
    public ResourceLocation getModelResource(WingKCorpArmor1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/kcorp_officer_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WingKCorpArmor1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/kcorp_officer_armor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WingKCorpArmor1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
