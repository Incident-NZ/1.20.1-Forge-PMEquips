package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingsKCorpAgentArmorTier3;
import software.bernie.geckolib.model.GeoModel;

public class KCorpAgentArmorM extends GeoModel<WingsKCorpAgentArmorTier3> {

    @Override
    public ResourceLocation getModelResource(WingsKCorpAgentArmorTier3 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/kcorp_agent_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WingsKCorpAgentArmorTier3 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/kcorp_agent_armor.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WingsKCorpAgentArmorTier3 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
