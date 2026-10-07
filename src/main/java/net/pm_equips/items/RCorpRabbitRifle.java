package net.pm_equips.items;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;
import net.pm_equips.ItemInit;
import net.pm_equips.MobEffectInit;
import net.pm_equips.SoundInit;
import net.pm_equips.energy.WeaponEnergyProvider;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * R社 ラビットライフル（リワーク）
 * - 装弾数50 / 攻撃力8 / 連射4tick / 弾速概念8.0
 * - エンティティなし・レッドストーン粉パーティクル軌跡
 * - 通常命中: BLEED Amp0 2秒
 * - FE最大10000 / Shift+Cで100FE消費し次弾をショットガン化（8ペレット・各21ダメ・弾薬1のみ）
 * - 特殊命中: BLEED Amp1 5秒
 */
public class RCorpRabbitRifle extends ProjectileWeaponItem {

    private static final int MAX_ENERGY = 10000;
    private static final int ENERGY_SPECIAL = 100;

    private static final int MAX_AMMO = 50;
    private static final int RELOAD_TICKS = 30;

    private static final int COOLDOWN_TICKS = 4;
    private static final double RANGE = 128.0;
    private static final double HIT_RADIUS = 0.35;
    /** 弾速8.0相当の軌跡密度用 */
    private static final double TRAIL_STEP = 0.5;

    private static final float NORMAL_DAMAGE = 8.0F;
    private static final float SPECIAL_DAMAGE = 21.0F;
    private static final int SPECIAL_PELLETS = 8;
    private static final double SPECIAL_SPREAD_DEG = 8.0;

    private static final String TAG_AMMO = "Ammo";
    private static final String TAG_RELOAD = "Reload";
    private static final String TAG_NEXT_SPECIAL = "NextSpecialShotgun";

    private static final DustParticleOptions REDSTONE_DUST =
            new DustParticleOptions(new Vector3f(1.0F, 0.1F, 0.1F), 1.0F);

    public RCorpRabbitRifle(Properties properties) {
        super(properties.durability(2000));
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.is(ItemInit.RIFLE_BULLET_AMMO.get());
    }

    @Override
    public int getDefaultProjectileRange() {
        return (int) RANGE;
    }

    // -------------------------------------------------------------------------
    // 射撃
    // -------------------------------------------------------------------------

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack gun = player.getItemInHand(hand);
        CompoundTag tag = gun.getOrCreateTag();

        if (tag.getInt(TAG_RELOAD) > 0) {
            return InteractionResultHolder.fail(gun);
        }

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(gun);
        }

        int ammo = tag.getInt(TAG_AMMO);
        if (ammo <= 0) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.literal("弾薬切れ / No Ammo"), true);
                level.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0F, 1.2F);
            }
            return InteractionResultHolder.fail(gun);
        }

        if (level.isClientSide) {
            return InteractionResultHolder.sidedSuccess(gun, true);
        }

        boolean special = tag.getBoolean(TAG_NEXT_SPECIAL);
        if (special) {
            tag.putBoolean(TAG_NEXT_SPECIAL, false);
            shootShotgun(level, player);
        } else {
            shootSingle(level, player);
        }

        tag.putInt(TAG_AMMO, ammo - 1); // 特殊でも弾薬は1のみ
        gun.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

        level.playSound(null, player.blockPosition(), SoundInit.GUN_SEMI.get(), SoundSource.PLAYERS, 1.0F, special ? 0.85F : 1.0F);

        return InteractionResultHolder.sidedSuccess(gun, false);
    }

    private void shootSingle(Level level, Player player) {
        Vec3 start = player.getEyePosition();
        Vec3 dir = player.getLookAngle();
        fireRay(level, player, start, dir, NORMAL_DAMAGE, false);
    }

    private void shootShotgun(Level level, Player player) {
        Vec3 start = player.getEyePosition();
        Vec3 base = player.getLookAngle();

        for (int i = 0; i < SPECIAL_PELLETS; i++) {
            Vec3 dir = spreadDirection(base, level.random.nextDouble(), level.random.nextDouble());
            fireRay(level, player, start, dir, SPECIAL_DAMAGE, true);
        }
    }

    private Vec3 spreadDirection(Vec3 base, double r1, double r2) {
        double yawOff = (r1 - 0.5) * 2.0 * Math.toRadians(RCorpRabbitRifle.SPECIAL_SPREAD_DEG);
        double pitchOff = (r2 - 0.5) * 2.0 * Math.toRadians(RCorpRabbitRifle.SPECIAL_SPREAD_DEG);

        // 簡易的に水平・垂直にずらす
        Vec3 right = base.cross(new Vec3(0, 1, 0));
        if (right.lengthSqr() < 1.0E-6) {
            right = base.cross(new Vec3(1, 0, 0));
        }
        right = right.normalize();
        Vec3 up = right.cross(base).normalize();

        return base.add(right.scale(Math.tan(yawOff))).add(up.scale(Math.tan(pitchOff))).normalize();
    }

    private void fireRay(Level level, Player player, Vec3 start, Vec3 dir, float damage, boolean special) {
        Vec3 end = start.add(dir.scale(RANGE));

        AABB box = new AABB(start, end).inflate(HIT_RADIUS + 1.0);
        List<LivingEntity> list = level.getEntitiesOfClass(
                LivingEntity.class, box,
                e -> e != player && e.isAlive() && e.isPickable()
        );
        list.sort(Comparator.comparingDouble(e -> e.position().distanceToSqr(start)));

        LivingEntity hit = null;
        for (LivingEntity e : list) {
            if (rayHits(start, end, e)) {
                hit = e;
                break;
            }
        }

        Vec3 trailEnd = hit != null ? hit.getBoundingBox().getCenter() : end;
        if (level instanceof ServerLevel server) {
            spawnRedstoneTrail(server, start, trailEnd);
        }

        if (hit != null) {
            hit.invulnerableTime = 0;
            hit.hurtTime = 0;
            hit.hurt(level.damageSources().playerAttack(player), damage);

            if (special) {
                // BLEED Amp1 5秒
                hit.addEffect(new MobEffectInstance(MobEffectInit.BLEED.get(), 100, 1));
            } else {
                // BLEED Amp0 2秒
                hit.addEffect(new MobEffectInstance(MobEffectInit.BLEED.get(), 40, 0));
            }

            if (level instanceof ServerLevel server) {
                Vec3 p = hit.getBoundingBox().getCenter();
                server.sendParticles(REDSTONE_DUST, p.x, p.y, p.z, 8, 0.2, 0.2, 0.2, 0.02);
            }
        }
    }

    private boolean rayHits(Vec3 start, Vec3 end, LivingEntity entity) {
        AABB box = entity.getBoundingBox().inflate(HIT_RADIUS);
        return box.clip(start, end).isPresent() || box.contains(start);
    }

    private void spawnRedstoneTrail(ServerLevel level, Vec3 start, Vec3 end) {
        Vec3 d = end.subtract(start);
        double dist = d.length();
        if (dist < 0.1) return;

        Vec3 step = d.normalize().scale(TRAIL_STEP);
        Vec3 cur = start;
        int n = (int) (dist / TRAIL_STEP);
        for (int i = 0; i < n; i++) {
            cur = cur.add(step);
            level.sendParticles(REDSTONE_DUST, cur.x, cur.y, cur.z, 1, 0.01, 0.01, 0.01, 0.0);
        }
    }

    // -------------------------------------------------------------------------
    // Shift+C 特殊（キーバインドから呼ぶ）
    // -------------------------------------------------------------------------

    /**
     * Shift+C 用。FEが100以上なら消費して次弾をショットガン化。
     */
    public void tryActivateSpecial(ItemStack stack, Player player) {
        if (player.level().isClientSide) return;
        if (!(stack.getItem() instanceof RCorpRabbitRifle)) return;

        if (getEnergy(stack) < ENERGY_SPECIAL) {
            player.displayClientMessage(Component.literal("FE不足 / Not enough FE"), true);
            player.level().playSound(null, player.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0F, 1.2F);
            return;
        }

        CompoundTag tag = stack.getOrCreateTag();
        if (tag.getBoolean(TAG_NEXT_SPECIAL)) {
            player.displayClientMessage(Component.literal("既に特殊弾が装填済み"), true);
            return;
        }

        if (!consumeEnergy(stack)) {
            player.displayClientMessage(Component.literal("FE不足 / Not enough FE"), true);
            return;
        }

        tag.putBoolean(TAG_NEXT_SPECIAL, true);
        player.displayClientMessage(Component.literal("特殊弾装填（次弾ショットガン）"), true);
        player.level().playSound(null, player.blockPosition(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.PLAYERS, 1.0F, 1.4F);
    }

    // -------------------------------------------------------------------------
    // リロード
    // -------------------------------------------------------------------------

    public void startReload(ItemStack stack, Player player) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.getInt(TAG_RELOAD) > 0) return;
        if (tag.getInt(TAG_AMMO) >= MAX_AMMO) return;

        boolean found = false;
        for (ItemStack inv : player.getInventory().items) {
            if (inv.is(ItemInit.RIFLE_BULLET_AMMO.get())) {
                found = true;
                break;
            }
        }
        if (!found) return;

        tag.putInt(TAG_RELOAD, RELOAD_TICKS);
        player.level().playSound(null, player.blockPosition(), SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(TAG_AMMO)) {
            tag.putInt(TAG_AMMO, MAX_AMMO);
        }

        int reload = tag.getInt(TAG_RELOAD);
        if (reload > 0) {
            reload--;
            tag.putInt(TAG_RELOAD, reload);

            if (reload <= 0 && entity instanceof Player player) {
                int ammo = tag.getInt(TAG_AMMO);
                int needed = MAX_AMMO - ammo;
                if (needed > 0) {
                    int loaded = 0;
                    for (ItemStack inv : player.getInventory().items) {
                        if (!inv.is(ItemInit.RIFLE_BULLET_AMMO.get())) continue;
                        while (!inv.isEmpty() && loaded < needed) {
                            inv.shrink(1);
                            loaded++;
                        }
                        if (loaded >= needed) break;
                    }
                    tag.putInt(TAG_AMMO, ammo + loaded);
                }
            }
        }

        super.inventoryTick(stack, level, entity, slot, selected);
    }

    // -------------------------------------------------------------------------
    // 近接・修理・エネルギー
    // -------------------------------------------------------------------------

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide()) {
            target.hurtTime = 0;
            target.invulnerableTime = 0;
            target.hurt(attacker.level().damageSources().generic(), NORMAL_DAMAGE);
            target.addEffect(new MobEffectInstance(MobEffectInit.BLEED.get(), 40, 0));
        }
        stack.hurtAndBreak(1, attacker, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repair) {
        return repair.is(ItemInit.RCORP_BATTERY.get());
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag nbt) {
        return new WeaponEnergyProvider(stack, MAX_ENERGY);
    }

    private int getEnergy(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ENERGY)
                .map(IEnergyStorage::getEnergyStored)
                .orElse(0);
    }

    private boolean consumeEnergy(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ENERGY).map(storage -> {
            if (storage.getEnergyStored() < RCorpRabbitRifle.ENERGY_SPECIAL) return false;
            storage.extractEnergy(RCorpRabbitRifle.ENERGY_SPECIAL, false);
            return true;
        }).orElse(false);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag tag = stack.getOrCreateTag();
        tooltip.add(Component.literal("Ammo: " + tag.getInt(TAG_AMMO) + " / " + MAX_AMMO));
        tooltip.add(Component.literal("FE: " + getEnergy(stack) + " / " + MAX_ENERGY));
        tooltip.add(Component.literal("攻撃力8 | 超高速 | 超長"));
        tooltip.add(Component.literal("Shift+CでFE100を消費し、次の一撃を強化可能。(8発/各21/的中時出血2付与)"));
        if (tag.getBoolean(TAG_NEXT_SPECIAL)) {
            tooltip.add(Component.literal("特殊弾装填中"));
        }
        super.appendHoverText(stack, level, tooltip, flag);
    }
}