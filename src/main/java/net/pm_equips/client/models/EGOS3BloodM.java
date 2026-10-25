package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit3Blood;
import software.bernie.geckolib.model.GeoModel;

public class EGOS3BloodM extends GeoModel<LobEGOSuit3Blood> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit3Blood animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/ego_armor_type1.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit3Blood animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/ego_s3_blood.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit3Blood animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
