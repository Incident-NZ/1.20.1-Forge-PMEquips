package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingHCorpArmor1;
import software.bernie.geckolib.model.GeoModel;

public class HCorpMaoArmorM extends GeoModel<WingHCorpArmor1> {
    @Override
    public ResourceLocation getModelResource(WingHCorpArmor1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/hcorp_mao_armor.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WingHCorpArmor1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/hcorp_mao.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WingHCorpArmor1 animatable) {
        return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json");
    }
}
