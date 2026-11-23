package net.pm_equips.client.models;

import net.minecraft.resources.ResourceLocation;
import net.pm_equips.items.WingRCorpArmor3;
import software.bernie.geckolib.model.GeoModel;

public class RCorpArmor3M extends GeoModel<WingRCorpArmor3> {
    @Override public ResourceLocation getModelResource(WingRCorpArmor3 item) { return ResourceLocation.fromNamespaceAndPath("pm_equips", "geo/rcorp_3.geo.json"); }
    @Override public ResourceLocation getTextureResource(WingRCorpArmor3 item) { return ResourceLocation.fromNamespaceAndPath("pm_equips", "textures/armor/rcorp_3.png"); }
    @Override public ResourceLocation getAnimationResource(WingRCorpArmor3 item) { return ResourceLocation.fromNamespaceAndPath("pm_equips", "animations/armor.animation.json"); }
}
