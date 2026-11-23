package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingTCorpArmor2;
import software.bernie.geckolib.model.GeoModel;

public class TCorpArmor2M extends GeoModel<WingTCorpArmor2> {
    @Override public ResourceLocation getModelResource(WingTCorpArmor2 item) { return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/tcorp_2.geo.json"); }
    @Override public ResourceLocation getTextureResource(WingTCorpArmor2 item) { return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/tcorp_2.png"); }
    @Override public ResourceLocation getAnimationResource(WingTCorpArmor2 item) { return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json"); }
}
