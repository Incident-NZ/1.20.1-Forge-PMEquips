package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit3Lament;
import software.bernie.geckolib.model.GeoModel;

public class EGOS3LamentM extends GeoModel<LobEGOSuit3Lament> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit3Lament animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit3Lament animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s3_lament.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit3Lament animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
