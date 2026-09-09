package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LCorpEGOL5Pink;
import software.bernie.geckolib.model.GeoModel;

public class EGOS5PinkM extends GeoModel<LCorpEGOL5Pink> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL5Pink animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LCorpEGOL5Pink animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s5_pink.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LCorpEGOL5Pink animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}
