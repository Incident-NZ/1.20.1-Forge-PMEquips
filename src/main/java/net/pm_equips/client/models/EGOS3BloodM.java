package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LCorpEGOL3Blood;
import software.bernie.geckolib.model.GeoModel;

public class EGOS3BloodM extends GeoModel<LCorpEGOL3Blood> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL3Blood animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL3Blood animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s3_blood.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL3Blood animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}
