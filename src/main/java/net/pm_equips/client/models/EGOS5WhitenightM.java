package net.pm_equips.client.models;

import net.pm_equips.items.LCorpEGOL5WhiteNight;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EGOS5WhitenightM extends GeoModel<LCorpEGOL5WhiteNight> {
    @Override
    public ResourceLocation getModelResource(LCorpEGOL5WhiteNight animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
        public ResourceLocation getTextureResource(LCorpEGOL5WhiteNight animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s5_whitenight.png");
    }

    @Override
        public ResourceLocation getAnimationResource(LCorpEGOL5WhiteNight animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/ego_armor.animation.json");
    }
}
