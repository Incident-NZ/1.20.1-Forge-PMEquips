package net.pm_equips.entity;

import net.minecraft.server.level.ServerEntity;

import net.minecraft.network.syncher.SynchedEntityData;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.PlayState;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EGOMagicP extends Projectile implements GeoEntity {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private static final double SPEED = 16.0D;
    private static final double MAX_DISTANCE = 128.0D;

    private Vec3 direction = Vec3.ZERO;
    private double speed = SPEED;
    private Vec3 startPos = Vec3.ZERO;

    private final Set<Integer> hitEntities = new HashSet<>();
    private float damageOverride = -1.0f;

    public EGOMagicP(EntityType<? extends EGOMagicP> type, Level level) {
        super(type, level);
    }

    public EGOMagicP(EntityType<? extends EGOMagicP> type, Level level, LivingEntity shooter) {
        this(type, level);

        this.setOwner(shooter);

        this.setPos(
                shooter.getX(),
                shooter.getEyeY() - 0.1D,
                shooter.getZ()
        );

        this.startPos = this.position();

        this.direction = shooter.getLookAngle().normalize();

        this.setDeltaMovement(direction.scale(this.speed));
    }

    public void setDamage(float dmg) {
        this.damageOverride = dmg;
    }
    public void setSpeed(double s) {
        this.speed = s;
        if (this.direction != null && this.direction.length() > 0) {
            this.setDeltaMovement(this.direction.scale(this.speed));
        }
    }

    @Override
    public void tick() {
        super.tick();

        Vec3 motion = this.getDeltaMovement();

        Vec3 currentPos = this.position();
        Vec3 nextPos = currentPos.add(motion);

        // =========================
        // ネザーポータル軌跡
        // =========================

        if (this.level().isClientSide) {

            for (int i = 0; i < 3; i++) {

                this.level().addParticle(
                        ParticleTypes.PORTAL,

                        this.getX(),
                        this.getY(),
                        this.getZ(),

                        0.0D,
                        0.0D,
                        0.0D
                );
            }
        }

        // =========================
        // Entity貫通判定
        // =========================

        AABB box = this.getBoundingBox()
                .expandTowards(motion)
                .inflate(1.0D);

        List<Entity> entities = this.level().getEntities(
                this,
                box,
                entity ->
                        entity instanceof LivingEntity
                                && entity.isAlive()
                                && entity != this.getOwner()
        );

        for (Entity entity : entities) {

            if (hitEntities.contains(entity.getId())) {
                continue;
            }

            hitEntities.add(entity.getId());

            RandomSource random = this.level().random;

            float damage;
            if (this.damageOverride > 0f) {
                damage = this.damageOverride;
            } else {
                damage = 22 + random.nextInt(5);
            }

            entity.hurt(
                    this.damageSources().thrown(this, getOwner()),
                    damage
            );
        }

        // ブロック完全貫通
        this.setPos(nextPos);

        // 最大射程
        if (this.position().distanceTo(startPos) >= MAX_DISTANCE) {
            this.discard();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        return new ClientboundAddEntityPacket(this, serverEntity);
    }

    private PlayState predicate(AnimationState animationState) {
        animationState.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this,"controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object object) {
        return 0;
    }
}
