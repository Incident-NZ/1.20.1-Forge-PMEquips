package net.pm_equips.items;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.pm_equips.ItemInit;
import net.pm_equips.SoundInit;
import net.pm_equips.client.renderer.LamentYisangR;
import net.pm_equips.client.screen.TooltipLines;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Limbus E.G.O - Yi Sang Lament（ガトリング）
 * - パーティクルヒットスキャン / エンティティなし
 * - 弾薬: RIFLE_BULLET_AMMO（1射撃あたり1消費）
 * - 射程128 / ダメージ8 / ペレット6
 * - 射撃レート: 40発/秒（毎tick 2回）
 */
public class LimbusEGOYisangLament extends ProjectileWeaponItem implements GeoItem {

    private static final double RANGE = 128.0;
    private static final float DAMAGE = 8.0F;
    private static final int PELLETS = 6;
    private static final double SPREAD_DEG = 6.0;
    private static final double HIT_RADIUS = 0.35;
    private static final double TRAIL_STEP = 0.55;

    /** 20TPS × 2 = 40発/秒 */
    private static final int SHOTS_PER_TICK = 2;

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public LimbusEGOYisangLament(Properties properties) {
        super(properties.durability(4000));
    }

    // -------------------------------------------------------------------------
    // 使用（長押し連射）
    // -------------------------------------------------------------------------

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (hasAmmo(player)) {
            if (!level.isClientSide) {
                level.playSound(null, player.blockPosition(),
                        net.minecraft.sounds.SoundEvents.DISPENSER_FAIL,
                        SoundSource.PLAYERS, 1.0F, 1.2F);
            }
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int remainingUseDuration) {
        if (!(living instanceof Player player)) return;

        // 弾薬切れで停止
        if (hasAmmo(player)) {
            player.stopUsingItem();
            return;
        }

        if (level.isClientSide) {
            return;
        }

        // 1秒40発 → 毎tick 2回射撃
        for (int i = 0; i < SHOTS_PER_TICK; i++) {
            if (hasAmmo(player)) {
                player.stopUsingItem();
                break;
            }
            fireVolley(level, player);
            consumeAmmo(player);
        }

        level.playSound(null, player.blockPosition(),
                SoundInit.EGO_LAMENT.get(),
                SoundSource.PLAYERS, 0.7F, 0.95F + level.random.nextFloat() * 0.1F);

        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
        player.awardStat(Stats.ITEM_USED.get(this));
    }

    // -------------------------------------------------------------------------
    // 射撃（6ペレット）
    // -------------------------------------------------------------------------

    private void fireVolley(Level level, Player player) {
        Vec3 start = player.getEyePosition();
        Vec3 base = player.getLookAngle();

        for (int i = 0; i < PELLETS; i++) {
            Vec3 dir = spreadDirection(base, SPREAD_DEG, level.random.nextDouble(), level.random.nextDouble());
            fireRay(level, player, start, dir);
        }
    }

    private Vec3 spreadDirection(Vec3 base, double spreadDeg, double r1, double r2) {
        double yawOff = (r1 - 0.5) * 2.0 * Math.toRadians(spreadDeg);
        double pitchOff = (r2 - 0.5) * 2.0 * Math.toRadians(spreadDeg);

        Vec3 right = base.cross(new Vec3(0, 1, 0));
        if (right.lengthSqr() < 1.0E-6) {
            right = base.cross(new Vec3(1, 0, 0));
        }
        right = right.normalize();
        Vec3 up = right.cross(base).normalize();

        return base.add(right.scale(Math.tan(yawOff))).add(up.scale(Math.tan(pitchOff))).normalize();
    }

    private void fireRay(Level level, Player player, Vec3 start, Vec3 dir) {
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
            spawnTrail(server, start, trailEnd);
        }

        if (hit != null) {
            hit.invulnerableTime = 0;
            hit.hurtTime = 0;
            hit.hurt(level.damageSources().playerAttack(player), DAMAGE);

            if (level instanceof ServerLevel server) {
                Vec3 p = hit.getBoundingBox().getCenter();
                server.sendParticles(ParticleTypes.ASH, p.x, p.y, p.z, 4, 0.1, 0.1, 0.1, 0.05);
                server.sendParticles(ParticleTypes.WHITE_ASH, p.x, p.y, p.z, 2, 0.05, 0.05, 0.05, 0.01);
            }
        }
    }

    private boolean rayHits(Vec3 start, Vec3 end, LivingEntity entity) {
        AABB box = entity.getBoundingBox().inflate(HIT_RADIUS);
        return box.clip(start, end).isPresent() || box.contains(start);
    }

    private void spawnTrail(ServerLevel level, Vec3 start, Vec3 end) {
        Vec3 d = end.subtract(start);
        double dist = d.length();
        if (dist < 0.1) return;

        // ガトリング用に間引き（負荷対策）
        double stepLen = TRAIL_STEP * 1.5;
        Vec3 step = d.normalize().scale(stepLen);
        Vec3 cur = start;
        int n = (int) (dist / stepLen);
        for (int i = 0; i < n; i++) {
            cur = cur.add(step);
            if (i % 2 == 0) {
                level.sendParticles(ParticleTypes.CRIT, cur.x, cur.y, cur.z, 1, 0.01, 0.01, 0.01, 0.0);
            }
        }
    }

    // -------------------------------------------------------------------------
    // 弾薬
    // -------------------------------------------------------------------------

    private boolean hasAmmo(Player player) {
        if (player.getAbilities().instabuild) return false;
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(ItemInit.RIFLE_BULLET_AMMO.get())) {
                count += stack.getCount();
                if (count >= 1) return false;
            }
        }
        return true;
    }

    private void consumeAmmo(Player player) {
        if (player.getAbilities().instabuild) return;
        int left = 1;
        for (ItemStack stack : player.getInventory().items) {
            if (!stack.is(ItemInit.RIFLE_BULLET_AMMO.get()) || stack.isEmpty()) continue;
            int take = Math.min(left, stack.getCount());
            stack.shrink(take);
            left -= take;
            if (left <= 0) return;
        }
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
    // GeckoLib
    // -------------------------------------------------------------------------

    private PlayState predicate(AnimationState animationState) {
        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController(this,"controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private LamentYisangR renderer = null;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {

                if (this.renderer == null)
                    this.renderer = new LamentYisangR();

                return this.renderer;
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.LAMENT_YISANG);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
