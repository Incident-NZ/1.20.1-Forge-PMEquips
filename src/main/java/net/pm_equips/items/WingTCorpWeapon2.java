package net.pm_equips.items;

import net.minecraft.world.item.TooltipFlag;

import java.util.List;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.pm_equips.ItemInit;

public class WingTCorpWeapon2 extends SwordItem {
    private static final float DAMAGE_PER_TICK = 3.0F;
    private static final double RANGE = 5.0D;

    public WingTCorpWeapon2() {
        super(new ClockTier(), new Properties().durability(1000).attributes(SwordItem.createAttributes(new ClockTier(), 13, -3.2F)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!canUseAlongsideOtherHand(player, hand)) {
            player.displayClientMessage(Component.translatable("tooltip.pm_equips.tcorp_exclusive"), true);
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide || !(living instanceof Player player)) return;
        if (!canUseAlongsideOtherHand(player, player.getUsedItemHand())) {
            player.stopUsingItem();
            return;
        }
        Vec3 look = player.getLookAngle().scale(RANGE);
        AABB range = player.getBoundingBox().expandTowards(look).inflate(1.0D);
        for (Entity entity : level.getEntities(player, range,
                target -> target instanceof LivingEntity && target.isAlive() && !target.isPassengerOfSameVehicle(player))) {
            if (!(entity instanceof LivingEntity target) || !isInFront(player, target)) continue;
            target.hurtTime = 0;
            target.invulnerableTime = 0;
            if (target.hurt(player.damageSources().playerAttack(player), DAMAGE_PER_TICK))
                stack.hurtAndBreak(1, player, player.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            if (stack.isEmpty()) { player.stopUsingItem(); break; }
        }
    }

    private static boolean isInFront(Player player, Entity target) {
        Vec3 direction = target.position().subtract(player.position()).normalize();
        return player.getLookAngle().dot(direction) > 0.6D;
    }

    public static boolean canUseAlongsideOtherHand(Player player, InteractionHand activeHand) {
        ItemStack other = player.getItemInHand(activeHand == InteractionHand.MAIN_HAND
                ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        return other.isEmpty() || other.getItem() instanceof WingTCorpWeapon1 || !isWeapon(other);
    }

    static boolean isWeapon(ItemStack stack) {
        boolean[] weapon = {false};
        stack.forEachModifier(EquipmentSlot.MAINHAND, (attribute, modifier) -> { if (attribute.equals(Attributes.ATTACK_DAMAGE)) weapon[0] = true; });
        return stack.getItem() instanceof ProjectileWeaponItem || stack.getItem() instanceof TridentItem || weapon[0];
    }

    @Override public int getUseDuration(ItemStack stack, LivingEntity user) { return 72000; }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.BOW; }

    private static class ClockTier implements Tier {
        @Override public int getUses() { return 1000; }
        @Override public float getSpeed() { return 4.0F; }
        @Override public float getAttackDamageBonus() { return 0.0F; }
        @Override public TagKey<Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_WOODEN_TOOL; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(ItemInit.TCORP_CLOCK.get()); }
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.pm_equips.tcorp_exclusive"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
