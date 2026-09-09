package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LCorpEGOL5DaCapo;
import software.bernie.geckolib.model.GeoModel;

public class EGOS5DaCapoM extends GeoModel<LCorpEGOL5DaCapo> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL5DaCapo animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL5DaCapo animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s5_da_capo.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL5DaCapo animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}
