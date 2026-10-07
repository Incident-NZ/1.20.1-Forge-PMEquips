package net.pm_equips.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.pm_equips.ItemInit;
import net.pm_equips.MobEffectInit;

public class WeaponRolandCrystal extends SwordItem {
    private static final int DURABILITY = 1000;
    private static final int SPEED_EFFECT_DURATION = 12;
    private static final int SPEED_EFFECT_AMPLIFIER = 7;
    private static final int DASH_COOLDOWN_TICKS = 60;
    private static final double DASH_DISTANCE = 15.0D;

    public WeaponRolandCrystal() {
        super(new CustomTier(), 10, -2.2F, new Properties().durability(DURABILITY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!isDualWielding(player)) {
            return InteractionResultHolder.fail(stack);
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide) {
            dashForward(level, player);
            player.getCooldowns().addCooldown(this, DASH_COOLDOWN_TICKS);
        }

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && entity instanceof Player player && isDualWielding(player)) {
            player.addEffect(new MobEffectInstance(
                    MobEffectInit.QUICK.get(),
                    SPEED_EFFECT_DURATION,
                    SPEED_EFFECT_AMPLIFIER,
                    false,
                    false,
                    true
            ));
        }

        super.inventoryTick(stack, level, entity, slot, selected);
    }

    private static boolean isDualWielding(Player player) {
        return player.getMainHandItem().is(ItemInit.FIXER_ROLAND_CRYSTAL.get())
                && player.getOffhandItem().is(ItemInit.FIXER_ROLAND_CRYSTAL.get());
    }

    private static void dashForward(Level level, Player player) {
        Vec3 start = player.position();
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();
        Vec3 end = eye.add(look.scale(DASH_DISTANCE));

        HitResult hit = level.clip(new ClipContext(
                eye,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));

        Vec3 target = hit.getType() == HitResult.Type.BLOCK
                ? hit.getLocation().subtract(look.scale(0.75D))
                : start.add(look.scale(DASH_DISTANCE));

        player.teleportTo(target.x, target.y, target.z);
        player.fallDistance = 0.0F;
    }

    private static class CustomTier implements Tier {
        @Override
        public int getUses() {
            return DURABILITY;
        }

        @Override
        public float getSpeed() {
            return 4.0F;
        }

        @Override
        public float getAttackDamageBonus() {
            return 0.0F;
        }

        @Override
        public int getLevel() {
            return 0;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }
    }
}
