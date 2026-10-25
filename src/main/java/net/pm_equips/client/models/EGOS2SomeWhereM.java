package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit2SomeWhere;
import software.bernie.geckolib.model.GeoModel;

public class EGOS2SomeWhereM extends GeoModel<LobEGOSuit2SomeWhere> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit2SomeWhere animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit2SomeWhere animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s2_somewhere.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit2SomeWhere animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
