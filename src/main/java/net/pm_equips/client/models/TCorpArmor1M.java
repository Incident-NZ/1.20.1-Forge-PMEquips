package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingTCorpArmor1;
import software.bernie.geckolib.model.GeoModel;

public class TCorpArmor1M extends GeoModel<WingTCorpArmor1> {
    @Override public ResourceLocation getModelResource(WingTCorpArmor1 item) { return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/tcorp_1.geo.json"); }
    @Override public ResourceLocation getTextureResource(WingTCorpArmor1 item) { return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/tcorp_1.png"); }
    @Override public ResourceLocation getAnimationResource(WingTCorpArmor1 item) { return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json"); }
}
