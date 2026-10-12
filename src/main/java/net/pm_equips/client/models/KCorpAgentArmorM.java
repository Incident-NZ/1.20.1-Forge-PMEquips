package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingKCorpAgentArmor;
import software.bernie.geckolib.model.GeoModel;

public class KCorpAgentArmorM extends GeoModel<WingKCorpAgentArmor> {

    @Override
    public ResourceLocation getModelResource(WingKCorpAgentArmor animatable) {
        return new ResourceLocation("pm_equips", "geo/kcorp_agent_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WingKCorpAgentArmor animatable) {
        return new ResourceLocation("pm_equips", "textures/armor/kcorp_agent_armor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WingKCorpAgentArmor animatable) {
        return new ResourceLocation("pm_equips", "animations/armor.animation.json");
    }
}
