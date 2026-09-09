package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LCorpEGOL1Penitence;
import net.pm_equips.items.LCorpEGOL1Soda;
import software.bernie.geckolib.model.GeoModel;

public class EGOS1SodaM extends GeoModel<LCorpEGOL1Soda> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL1Soda animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL1Soda animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s1_soda.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL1Soda animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}