package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit5Pink;
import software.bernie.geckolib.model.GeoModel;

public class EGOS5PinkM extends GeoModel<LobEGOSuit5Pink> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit5Pink animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit5Pink animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s5_pink.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit5Pink animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
