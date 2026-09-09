package net.pm_equips.client.models;

import net.pm_equips.items.LCorpEGOL3Lament;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS3LamentM extends GeoModel<LCorpEGOL3Lament> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL3Lament animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL3Lament animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s3_lament.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL3Lament animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}
