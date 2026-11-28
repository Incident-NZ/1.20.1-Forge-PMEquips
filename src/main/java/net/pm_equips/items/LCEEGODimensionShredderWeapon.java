package net.pm_equips.items;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pm_equips.ItemInit;
import net.pm_equips.PMEquipsMain;

@Mod.EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
public class LCEEGODimensionShredderWeapon extends SwordItem {
    private static final int RANGE = 128, COOLDOWN_TICKS = 60;
    private static final String SAFE_FALL = "pm_equips_dimension_shredder_safe_fall";

    public LCEEGODimensionShredderWeapon() {
        super(new CustomTier(), 8, -2.0f, new Properties().durability(2000));
    }

    private static boolean hasLoadout(Player player) {
        return player.getMainHandItem().getItem() instanceof LCEEGODimensionShredderWeapon
                && player.getOffhandItem().getItem() instanceof LCEEGODimensionShredderWeapon
                && player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof LCEEGODimensionShredderArmor;
    }

    // Vanilla block/entity interactions take priority; only unhandled clicks reach item use.
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (hand != InteractionHand.MAIN_HAND || !hasLoadout(player)) return InteractionResultHolder.pass(stack);
        if (!player.isAlive() || player.isSpectator() || player.isSleeping() || player.getCooldowns().isOnCooldown(this))
            return InteractionResultHolder.fail(stack);
        if (level.isClientSide) return InteractionResultHolder.consume(stack);

        EntityHitResult hit = aimedEntity(player);
        if (hit != null) {
            Entity target = hit.getEntity();
            // Arrive two blocks on the user's side of the target, at the target's feet height.
            Vec3 direction = player.position().subtract(target.position()).multiply(1, 0, 1);
            direction = direction.lengthSqr() < 1.0E-6
                    ? Vec3.directionFromRotation(0, player.getYRot()).scale(-1) : direction.normalize();
            Vec3 destination = target.position().add(direction.scale(2));
            AABB bounds = player.getBoundingBox().move(destination.subtract(player.position()));
            if (bounds.minY < level.getMinBuildHeight() || bounds.maxY > level.getMaxBuildHeight()
                    || !level.getWorldBorder().isWithinBounds(bounds)
                    || !level.hasChunksAt(BlockPos.containing(bounds.minX, bounds.minY, bounds.minZ),
                            BlockPos.containing(bounds.maxX, bounds.maxY, bounds.maxZ))
                    || !level.noCollision(player, bounds)) return InteractionResultHolder.fail(stack);
            Vec3 center = target.position();
            player.stopRiding();
            player.teleportTo(destination.x, destination.y, destination.z);
            player.setOnGround(false);
            player.fallDistance = 0;
            // A protected player target allows only the teleport, including no area strike.
            if (canStrike(player, target)) {
                strike(player, target, 13);
                for (Entity nearby : level.getEntities(player, new AABB(center, center).inflate(3),
                        entity -> entity != target && entity.isAlive() && entity.isPickable()
                                && !entity.isSpectator() && entity.position().distanceToSqr(center) <= 9))
                    strike(player, nearby, 10);
            }
        } else {
            if (!ClassWCorpTeleport.teleportForward(level, player, RANGE)) return InteractionResultHolder.fail(stack);
            player.setOnGround(false);
            player.getPersistentData().putLong(SAFE_FALL, level.getGameTime());
        }
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        return InteractionResultHolder.consume(stack);
    }

    private static EntityHitResult aimedEntity(Player player) {
        Level level = player.level();
        Vec3 start = player.getEyePosition(), look = player.getLookAngle();
        double distance = 0;
        for (double step = .5; step <= RANGE; step += .5) {
            if (!level.hasChunkAt(BlockPos.containing(start.add(look.scale(step))))) break;
            distance = step;
        }
        HitResult block = level.clip(new ClipContext(start, start.add(look.scale(distance)),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        Vec3 end = block.getLocation();
        return ProjectileUtil.getEntityHitResult(level, player, start, end,
                player.getBoundingBox().expandTowards(end.subtract(start)).inflate(1),
                entity -> entity.isAlive() && entity.isPickable() && !entity.isSpectator()
                        && !entity.isPassengerOfSameVehicle(player));
    }

    private static boolean canStrike(Player player, Entity target) {
        return !(target instanceof Player other) || player.getServer().isPvpAllowed() && player.canHarmPlayer(other);
    }

    private static void strike(Player player, Entity target, float damage) {
        if (!canStrike(player, target)) return;
        if (target instanceof LivingEntity living) living.invulnerableTime = 0;
        target.hurt(player.damageSources().playerAttack(player), damage);
    }

    @SubscribeEvent public static void onFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide
                || !player.getPersistentData().contains(SAFE_FALL)) return;
        event.setCanceled(true);
        player.fallDistance = 0;
        player.getPersistentData().remove(SAFE_FALL);
    }

    @SubscribeEvent public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase != TickEvent.Phase.END || player.level().isClientSide
                || !player.getPersistentData().contains(SAFE_FALL)) return;
        if (!player.isAlive() || player.level().getGameTime() > player.getPersistentData().getLong(SAFE_FALL)
                && (player.onGround() || player.isInWaterOrBubble() || player.onClimbable()))
            player.getPersistentData().remove(SAFE_FALL);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (result && !attacker.level().isClientSide()) {
            // Iフレーム無視
            target.hurtTime = 0;           // クライアント側の赤フラッシュ時間
            target.invulnerableTime = 0;   // または noDamageTicks (バージョンにより名称確認)
        }
        return result;
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 2000; }
        @Override public float getSpeed() { return 4.0f; }
        @Override public float getAttackDamageBonus() { return 0.0f; }
        @Override public int getLevel() { return 0; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.of(ItemInit.ITEM_ENKEPHALIN.get()); }
    }
}
