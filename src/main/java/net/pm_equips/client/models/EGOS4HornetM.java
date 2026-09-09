package net.pm_equips.client.models;

import net.pm_equips.items.LCorpEGOL4Hornet;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS4HornetM extends GeoModel<LCorpEGOL4Hornet> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL4Hornet animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL4Hornet animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s4_hornet.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL4Hornet animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}
