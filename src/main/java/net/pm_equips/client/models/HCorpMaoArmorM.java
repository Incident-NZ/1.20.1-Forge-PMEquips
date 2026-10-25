package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingHCorpMaoArmor;
import software.bernie.geckolib.model.GeoModel;

public class HCorpMaoArmorM extends GeoModel<WingHCorpMaoArmor> {
    @Override
    public ResourceLocation getModelResource(WingHCorpMaoArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/hcorp_mao_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WingHCorpMaoArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/hcorp_mao.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WingHCorpMaoArmor animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
