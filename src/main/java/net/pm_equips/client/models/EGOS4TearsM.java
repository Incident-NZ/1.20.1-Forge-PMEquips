package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit4Tears;
import software.bernie.geckolib.model.GeoModel;

public class EGOS4TearsM extends GeoModel<LobEGOSuit4Tears> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit4Tears animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit4Tears animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s4_tears.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit4Tears animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
