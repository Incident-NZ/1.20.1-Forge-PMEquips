package net.pm_equips.items;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.pm_equips.BlockInit;
import net.pm_equips.MobEffectInit;
import net.pm_equips.SoundInit;
import net.pm_equips.config.CommonConfig;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.pm_equips.entity.PWhiteNight;
import net.pm_equips.EntityInit;

import java.util.UUID;

public class EGOW5WhiteNight extends SwordItem {

    private static final UUID REACH_UUID = UUID.randomUUID();
    private static final AttributeModifier REACH_MODIFIER =
            new AttributeModifier(REACH_UUID, "white_night_reach", 3.0, AttributeModifier.Operation.ADDITION);

    public EGOW5WhiteNight() {
        super(new CustomTier(), 50, -3.5f, new Properties().durability(4000).rarity(Rarity.EPIC));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide || !(entity instanceof Player player)) return;

        boolean isHolding = selected && player.getMainHandItem() == stack;

        AttributeInstance reachAttr = player.getAttribute(ForgeMod.ENTITY_REACH.get());
        if (reachAttr != null) {
            if (isHolding && !reachAttr.hasModifier(REACH_MODIFIER)) {
                reachAttr.addTransientModifier(REACH_MODIFIER);
            } else if (!isHolding && reachAttr.hasModifier(REACH_MODIFIER)) {
                reachAttr.removeModifier(REACH_MODIFIER);
            }
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);
        if (result && !attacker.level().isClientSide()) {
            if (attacker instanceof Player player) {
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 0, false, false));
            }
        }

        if (result && !attacker.level().isClientSide()) {
            // Iフレーム無視
            target.hurtTime = 0;           // クライアント側の赤フラッシュ時間
            target.invulnerableTime = 0;   // または noDamageTicks (バージョンにより名称確認)
        }

        return result;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            // Use ProjectileUtil to pick an entity in the look direction
            Vec3 eyePos = player.getEyePosition();
            Vec3 lookDir = player.getLookAngle();
            Vec3 end = eyePos.add(lookDir.scale(64.0D));

            AABB box = player.getBoundingBox().expandTowards(lookDir.scale(64.0D)).inflate(1.0D);

            EntityHitResult entityHit = net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult(
                    level,
                    player,
                    eyePos,
                    end,
                    box,
                    e -> e instanceof LivingEntity
                            && e.isAlive()
                            && e != player
                            && (CommonConfig.ALLOW_FRIENDLY_FIRE.get() || !(e instanceof Player) && !player.isAlliedTo(e))
            );

            if (entityHit != null && entityHit.getEntity() instanceof LivingEntity target) {
                // apply a small cooldown (40 ticks = 2s)
                player.getCooldowns().addCooldown(this, 40);

                // perform ranged attack
                fireRangedAttack(level, player, target, itemStack);

                // award stat
                player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(this));

                return InteractionResultHolder.success(itemStack);
            }
        }

        return InteractionResultHolder.pass(itemStack);
    }

    private void fireRangedAttack(Level level, Player player, LivingEntity target, ItemStack itemStack) {
        // Calculate random ranged damage (22-28)
        int damage = 22 + level.random.nextInt(7);

        // Generate 12 stationary weapons around the target, similar to Cataclysm's phantom halberd.
        int projectilesPerWeapon = 4; // 3 weapon types x 4 = 12 total
        int totalProjectiles = 3 * projectilesPerWeapon;
        int projectileIndex = 0;

        for (int weaponType = 0; weaponType < 3; weaponType++) {
            for (int i = 0; i < projectilesPerWeapon; i++) {
                double angleRadians = (Math.PI * 2.0D * projectileIndex) / totalProjectiles;
                double radius = 1.45D + weaponType * 0.15D;
                double spawnX = target.getX() + Math.cos(angleRadians) * radius;
                double spawnZ = target.getZ() + Math.sin(angleRadians) * radius;
                double spawnY = findSurfaceY(level, spawnX, target.getY() + target.getBbHeight() + 1.0D, spawnZ);
                Vec3 spawnPos = new Vec3(spawnX, spawnY, spawnZ);
                float yawDegrees = (float) (angleRadians * Mth.RAD_TO_DEG) + 90.0F;

                PWhiteNight projectile = new PWhiteNight(
                        EntityInit.WHITENIGHT_PROJECTILE.get(),
                        level,
                        player,
                        spawnPos,
                        (float) damage,
                        weaponType,
                        yawDegrees,
                        PWhiteNight.DEFAULT_RENDER_SCALE
                );

                level.addFreshEntity(projectile);
                projectileIndex++;
            }
        }

        // Apply effects to target
        target.addEffect(new MobEffectInstance(MobEffectInit.BIND.get(), 100, 5, false, false)); // 5 sec, level 6 / 60% speed down (value 2)
        target.hurt(level.damageSources().playerAttack(player), (float) damage);

        // Play sound
        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundInit.EGO_WHITENIGHT_ATK_1.get(), SoundSource.PLAYERS, 1.0F, 1.2F);

        // Damage weapon
        itemStack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
    }

    private static double findSurfaceY(Level level, double x, double startY, double z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(Mth.floor(x), Mth.floor(startY), Mth.floor(z));
        int minY = level.getMinBuildHeight();

        while (pos.getY() > minY && level.getBlockState(pos).isAir()) {
            pos.move(Direction.DOWN);
        }

        if (level.getBlockState(pos).isCollisionShapeFullBlock(level, pos)) {
            return pos.getY() + 1.0D;
        }

        return startY;
    }

    private static class CustomTier implements Tier {
        @Override
        public int getUses() {
            return 4000;
        }

        @Override
        public float getSpeed() {
            return 0.2f;
        }

        @Override
        public float getAttackDamageBonus() {
            return 10.0f; // 50-60 damage = 10 base + 50 sword damage
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
            return Ingredient.of(BlockInit.BlockItems.ALEPH_PE_BOX.get());
        }
    }
}



