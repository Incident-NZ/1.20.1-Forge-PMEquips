package net.pm_equips.client.models;

import net.pm_equips.items.LCorpEGOL5Justitia;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS5JustitiaM extends GeoModel<LCorpEGOL5Justitia> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL5Justitia animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL5Justitia animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s5_justitia.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL5Justitia animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}
