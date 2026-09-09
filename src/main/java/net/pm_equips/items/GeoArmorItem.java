package net.pm_equips.items;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.client.models.GeoArmorModel;
import net.pm_equips.client.renderer.FullBodyArmorRenderer;
import net.pm_equips.energy.EnergyUtils;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

import java.util.List;
import java.util.function.Consumer;

/** Common GeoItem implementation shared by all full-body equipment pages. */
public class GeoArmorItem extends Item implements GeoItem {
    private static final RawAnimation IDLE = RawAnimation.begin().then("idle", Animation.LoopType.LOOP);
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public GeoArmorItem(Properties properties) {
        super(properties);
    }

    public static Properties properties(int durability, double armor, double toughness, double knockbackResistance) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(PMEquipsMain.MOD_ID, "full_body_armor");
        ItemAttributeModifiers attributes = ItemAttributeModifiers.builder()
                .add(Attributes.ARMOR, new AttributeModifier(id.withSuffix("_armor"), armor, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.LEGS)
                .add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(id.withSuffix("_toughness"), toughness, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.LEGS)
                .add(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(id.withSuffix("_knockback_resistance"), knockbackResistance, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.LEGS)
                .build();
        return new Properties().stacksTo(1).durability(durability).attributes(attributes);
    }

    @Override
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.LEGS;
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity entity) {
        return slot == EquipmentSlot.LEGS;
    }

    public ResourceLocation modelResource() {
        String name = getClass().getSimpleName();
        if (name.startsWith("WingsWCorp")) return resource("geo/wcorp_armor.geo.json");
        if (name.startsWith("WingsKCorpOfficer")) return resource("geo/kcorp_officer_armor.geo.json");
        if (name.startsWith("WingsKCorpAgent")) return resource("geo/kcorp_agent_armor.geo.json");
        if (name.startsWith("WingsRCorp4thRabbit")) return resource("geo/rcorp_4th_rabbit_armor.geo.json");
        if (name.startsWith("WingsHCorpMao")) return resource("geo/hcorp_mao_armor.geo.json");
        if (name.startsWith("WingsHCorpSi")) return resource("geo/hcorp_si_armor.geo.json");
        if (name.startsWith("WingsHCorpYou")) return resource("geo/hcorp_you_armor.geo.json");
        if (name.startsWith("ASC2")) return resource("geo/asc2_south_armor.geo.json");
        return resource(isTypeOneModel() ? "geo/ego_armor_type1.geo.json" : "geo/ego_armor_type2.geo.json");
    }

    public ResourceLocation textureResource() {
        String name = getClass().getSimpleName();
        if (name.startsWith("WingsWCorp")) return resource("textures/armor/wcorp_armor.png");
        if (name.startsWith("WingsKCorpOfficer")) return resource("textures/armor/kcorp_officer_armor.png");
        if (name.startsWith("WingsKCorpAgent")) return resource("textures/armor/kcorp_agent_armor.png");
        if (name.startsWith("WingsRCorp4thRabbit")) return resource("textures/armor/rcorp_4th_rabbit_armor.png");
        if (name.startsWith("WingsHCorpMao")) return resource("textures/armor/hcorp_mao.png");
        if (name.startsWith("WingsHCorpSi")) return resource("textures/armor/hcorp_si.png");
        if (name.startsWith("WingsHCorpYou")) return resource("textures/armor/hcorp_you.png");
        if (name.startsWith("ASC2")) return resource("textures/armor/asc2_south_armor.png");

        String path = BuiltInRegistries.ITEM.getKey(this).getPath();
        String texture = switch (path) {
            case "s5_white_night" -> "ego_s5_whitenight.png";
            case "s4_crimson_scar" -> "ego_s4_crimson_scar.png";
            default -> "ego_" + path + ".png";
        };
        return resource("textures/armor/" + texture);
    }

    public ResourceLocation animationResource() {
        return resource(getClass().getSimpleName().startsWith("LCorp")
                ? "animations/ego_armor.animation.json"
                : "animations/armor.animation.json");
    }

    public boolean hasActiveAbility() {
        String name = getClass().getSimpleName();
        return name.startsWith("WingsWCorp") || name.startsWith("WingsRCorp4thRabbit")
                || name.startsWith("WingsKCorpOfficer") || name.startsWith("WingsKCorpAgent");
    }

    private boolean isTypeOneModel() {
        String path = BuiltInRegistries.ITEM.getKey(this).getPath();
        return List.of("s5_twilight", "s5_star", "s5_justitia", "s4_tears", "s4_hornet", "s4_lamp",
                "s4_aroma", "s4_stem", "s4_swan", "s4_crimson_scar", "s4_blue_scar", "s3_magic_bullet",
                "s3_ice_shard", "s3_laetitia", "s3_blood", "s3_lament", "s3_mk4", "s2_match",
                "s2_red_eye", "s2_reglet", "s2_somewhere").contains(path);
    }

    private static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(PMEquipsMain.MOD_ID, path);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private FullBodyArmorRenderer<GeoArmorItem> renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(T entity, ItemStack stack,
                                                                                 EquipmentSlot slot, HumanoidModel<T> original) {
                if (renderer == null) renderer = new FullBodyArmorRenderer<>(new GeoArmorModel());
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle", 0, state -> {
            state.getController().setAnimation(IDLE);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return EnergyUtils.get(stack).map(storage -> storage.getMaxEnergyStored() > 0
                && storage.getEnergyStored() < storage.getMaxEnergyStored()).orElse(false);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return EnergyUtils.get(stack).map(storage -> Math.round(13.0F * storage.getEnergyStored()
                / (float) storage.getMaxEnergyStored())).orElse(0);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00FFFF;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        EnergyUtils.get(stack).filter(storage -> storage.getMaxEnergyStored() > 0)
                .ifPresent(storage -> tooltip.add(Component.literal("FE: " + storage.getEnergyStored()
                        + " / " + storage.getMaxEnergyStored())));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}


