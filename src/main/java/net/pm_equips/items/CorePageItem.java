package net.pm_equips.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.pm_equips.compat.Curios;
import net.pm_equips.energy.WeaponEnergyProvider;
import net.pm_equips.items.materials.ArmorEquips;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CorePageItem extends ArmorItem implements GeoItem, ICurioItem {
    private final ArmorEquips material;
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public CorePageItem(ArmorMaterial material, ArmorItem.Type type, Properties properties) {
        super(material, type, properties);
        this.material = (ArmorEquips) material;
    }

    public ResourceLocation getModelResource() {
        return switch (this) {
            case WCorpArmor wCorpArmor -> resource("geo/wcorp_armor.geo.json");
            case KCorpAgentArmor kCorpAgentArmor -> resource("geo/kcorp_agent_armor.geo.json");
            case KCorpOfficerArmor kCorpOfficerArmor -> resource("geo/kcorp_officer_armor.geo.json");
            case RCorp4thRabbitArmor rCorp4thRabbitArmor -> resource("geo/rcorp_4th_rabbit_armor.geo.json");
            case Asc2SouthArmor asc2SouthArmor -> resource("geo/asc2_south_armor.geo.json");
            case HCorpMaoArmor hCorpMaoArmor -> resource("geo/hcorp_mao_armor.geo.json");
            case HCorpSiArmor hCorpSiArmor -> resource("geo/hcorp_si_armor.geo.json");
            case HCorpYouArmor hCorpYouArmor -> resource("geo/hcorp_you_armor.geo.json");
            default -> resource(isTypeOneModel() ? "geo/ego_armor_type1.geo.json" : "geo/ego_armor_type2.geo.json");
        };

    }

    public ResourceLocation getTextureResource() {
        return switch (this) {
            case WCorpArmor wCorpArmor -> resource("textures/armor/wcorp_armor.png");
            case KCorpAgentArmor kCorpAgentArmor -> resource("textures/armor/kcorp_agent_armor.png");
            case KCorpOfficerArmor kCorpOfficerArmor -> resource("textures/armor/kcorp_officer_armor.png");
            case RCorp4thRabbitArmor rCorp4thRabbitArmor -> resource("textures/armor/rcorp_4th_rabbit_armor.png");
            case Asc2SouthArmor asc2SouthArmor -> resource("textures/armor/asc2_south_armor.png");
            case HCorpMaoArmor hCorpMaoArmor -> resource("textures/armor/hcorp_mao.png");
            case HCorpSiArmor hCorpSiArmor -> resource("textures/armor/hcorp_si.png");
            case HCorpYouArmor hCorpYouArmor -> resource("textures/armor/hcorp_you.png");
            default -> resource("textures/armor/" + switch (material) {
                case A5_PARADISE_LOST -> "ego_s5_whitenight.png";
                case A4_THE_SWORD_SHARPENED_WITH_TEARS -> "ego_s4_tears.png";
                case A4_CRIMSON_SCAR -> "ego_s4_crimson_scar.png";
                case A2_FOURTH_MATCH_FIRE -> "ego_s2_match.png";
                case A3_SOLEMN_LAMENT -> "ego_s3_lament.png";
                case A3_GRINDER_MK4 -> "ego_s3_mk4.png";
                default -> "ego_" + material.getName().substring(material.getName().indexOf(':') + 1) + ".png";
            });
        };

    }

    public ResourceLocation getAnimationResource() {
        return resource(hasDedicatedArmorRenderer() ? "animations/armor.animation.json" : "animations/ego_armor.animation.json");
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return getTextureResource().toString();
    }

    private boolean hasDedicatedArmorRenderer() {
        return this instanceof WCorpArmor
                || this instanceof KCorpAgentArmor
                || this instanceof KCorpOfficerArmor
                || this instanceof RCorp4thRabbitArmor
                || this instanceof Asc2SouthArmor
                || this instanceof HCorpMaoArmor
                || this instanceof HCorpSiArmor
                || this instanceof HCorpYouArmor;
    }

    private boolean isTypeOneModel() {
        return switch (material) {
            case A5_TWILIGHT, A5_SOUND_OF_A_STAR, A5_JUSTITIA,
                    A4_THE_SWORD_SHARPENED_WITH_TEARS, A4_HORNET, A4_LAMP, A4_AROMA,
                    A4_STEM, A4_SWAN, A4_CRIMSON_SCAR, A4_BLUE_SCAR,
                    A3_MAGIC_BULLET, A3_ICE_SHARD, A3_LAETITIA, A3_BLOOD,
                    A3_SOLEMN_LAMENT, A3_GRINDER_MK4,
                    A2_FOURTH_MATCH_FIRE, A2_RED_EYE, A2_REGLET, A2_SOMEWHERE -> true;
            default -> false;
        };
    }

    private static ResourceLocation resource(String path) {
        return new ResourceLocation("pm_equips", path);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return Curios.CORE_PAGE_SLOT.equals(slotContext.identifier());
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return canEquip(slotContext, stack);
    }

    @Override
    public @NotNull ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(material.getEquipSound(), 1.0f, 1.0f);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributes = ImmutableMultimap.builder();
        int protection = material.getDefenseForType(ArmorItem.Type.CHESTPLATE)
                + material.getDefenseForType(ArmorItem.Type.LEGGINGS);
        attributes.put(Attributes.ARMOR, new AttributeModifier(uuid, "Core page armor", protection, AttributeModifier.Operation.ADDITION));
        attributes.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Core page armor toughness", material.getToughness(), AttributeModifier.Operation.ADDITION));
        attributes.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Core page knockback resistance", material.getKnockbackResistance(), AttributeModifier.Operation.ADDITION));
        return attributes.build();
    }

    public int getCorePageMaxEnergy() {
        if (this instanceof WCorpArmor) {
            return 5000;
        }
        if (this instanceof RCorp4thRabbitArmor) {
            return 10000;
        }
        if (this instanceof KCorpAgentArmor || this instanceof KCorpOfficerArmor) {
            return 7500;
        }
        return 0;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        int maxEnergy = getCorePageMaxEnergy();
        return maxEnergy > 0 ? new WeaponEnergyProvider(stack, maxEnergy) : null;
    }

    public static Optional<ItemStack> findEquippedAbilityItem(LivingEntity entity) {
        return CuriosApi.getCuriosHelper().findEquippedCurio(
                stack -> stack.getItem() instanceof WCorpArmor
                        || stack.getItem() instanceof RCorp4thRabbitArmor
                        || stack.getItem() instanceof KCorpAgentArmor
                        || stack.getItem() instanceof KCorpOfficerArmor,
                entity
        ).map(ImmutableTriple::getRight);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ENERGY)
                .map(storage -> storage.getMaxEnergyStored() > 0 && storage.getEnergyStored() < storage.getMaxEnergyStored())
                .orElse(false);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ENERGY)
                .map(storage -> Math.round(13.0F * storage.getEnergyStored() / (float) storage.getMaxEnergyStored()))
                .orElse(0);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00FFFF;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (getCorePageMaxEnergy() > 0) {
            stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(storage ->
                    tooltip.add(Component.literal("FE: " + storage.getEnergyStored() + " / " + storage.getMaxEnergyStored())));
        }
    }

    private PlayState predicate(AnimationState<CorePageItem> animationState) {
        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
