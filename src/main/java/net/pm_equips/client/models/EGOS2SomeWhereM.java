package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LCorpEGOL2SomeWhere;
import software.bernie.geckolib.model.GeoModel;

public class EGOS2SomeWhereM extends GeoModel<LCorpEGOL2SomeWhere> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL2SomeWhere animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL2SomeWhere animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s2_somewhere.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL2SomeWhere animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}