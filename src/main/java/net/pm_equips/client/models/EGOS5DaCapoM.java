package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit5DaCapo;
import software.bernie.geckolib.model.GeoModel;

public class EGOS5DaCapoM extends GeoModel<LobEGOSuit5DaCapo> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit5DaCapo animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit5DaCapo animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s5_da_capo.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit5DaCapo animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
