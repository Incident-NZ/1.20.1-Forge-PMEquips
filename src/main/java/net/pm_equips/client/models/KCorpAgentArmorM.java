package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingKCorpArmor2;
import software.bernie.geckolib.model.GeoModel;

public class KCorpAgentArmorM extends GeoModel<WingKCorpArmor2> {

    @Override
    public ResourceLocation getModelResource(WingKCorpArmor2 animatable) {
        return new ResourceLocation("pm_equips", "geo/kcorp_agent_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WingKCorpArmor2 animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/kcorp_agent_armor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WingKCorpArmor2 animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}
