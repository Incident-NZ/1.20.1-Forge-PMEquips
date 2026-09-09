package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LCorpEGOL4Aroma;
import software.bernie.geckolib.model.GeoModel;

public class EGOS4AromaM extends GeoModel<LCorpEGOL4Aroma> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL4Aroma animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL4Aroma animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s4_aroma.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL4Aroma animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}
