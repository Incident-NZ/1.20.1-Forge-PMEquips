package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.LobEGOSuit1WingBeat;
import software.bernie.geckolib.model.GeoModel;

public class EGOS1WingBeatM extends GeoModel<LobEGOSuit1WingBeat> {
    @Override
    public ResourceLocation getModelResource(LobEGOSuit1WingBeat animatable) {
        return new ResourceLocation("pm_equips", "geo/ego_armor_type2.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(LobEGOSuit1WingBeat animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/ego_s1_wingbeat.png");
    }

    @Override
    public ResourceLocation getAnimationResource(LobEGOSuit1WingBeat animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}