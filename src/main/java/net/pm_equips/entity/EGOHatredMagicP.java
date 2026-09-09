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
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.pm_equips.PMERegistryEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.PlayState;

public class EGOHatredMagicP extends Projectile implements GeoEntity {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final double SPEED = 16.0D;
    private static final double MAX_DISTANCE = 128.0D;
    private static final float MIN_EFFECT_AMOUNT = 5.0F;

    private Vec3 startPos = Vec3.ZERO;
    private boolean startPositionInitialized;

    public EGOHatredMagicP(EntityType<? extends EGOHatredMagicP> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public EGOHatredMagicP(Level level, LivingEntity shooter) {
        this(PMERegistryEntity.HATRED_MAGIC_PROJECTILE.get(), level);
        setOwner(shooter);

        Vec3 direction = shooter.getLookAngle().normalize();
        Vec3 spawnPos = shooter.getEyePosition().add(direction.scale(0.8D));
        setPos(spawnPos.x, spawnPos.y, spawnPos.z);
        startPos = spawnPos;
        startPositionInitialized = true;
        setDeltaMovement(direction.scale(SPEED));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        super.tick();

        if (!startPositionInitialized) {
            startPos = position();
            startPositionInitialized = true;
        }

        Vec3 motion = getDeltaMovement();
        if (motion.lengthSqr() <= 0.0001D) {
            discard();
            return;
        }

        Vec3 start = position();
        Vec3 end = start.add(motion);

        if (!level().isClientSide) {
            BlockHitResult blockHit = level().clip(new ClipContext(
                    start,
                    end,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    this
            ));

            Vec3 collisionEnd = blockHit.getType() == HitResult.Type.BLOCK
                    ? blockHit.getLocation()
                    : end;
            EntityHitResult entityHit = findEntityHit(start, collisionEnd, motion);

            if (entityHit != null) {
                onHitEntity(entityHit);
                return;
            }

            if (blockHit.getType() == HitResult.Type.BLOCK) {
                discard();
                return;
            }
        }

        setPos(end.x, end.y, end.z);

        if (position().distanceTo(startPos) >= MAX_DISTANCE) {
            discard();
        }

        if (level().isClientSide) {
            spawnEndRodParticles();
        }
    }

    private EntityHitResult findEntityHit(Vec3 start, Vec3 end, Vec3 motion) {
        Entity owner = getOwner();
        return net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult(
                level(),
                owner != null ? owner : this,
                start,
                end,
                getBoundingBox().expandTowards(motion).inflate(0.3D),
                entity -> entity instanceof LivingEntity living
                        && living.isAlive()
                        && entity != owner
        );
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (!(result.getEntity() instanceof LivingEntity target) || level().isClientSide) {
            return;
        }

        RandomSource random = level().random;
        float amount = MIN_EFFECT_AMOUNT + random.nextInt(3);

        if (isPlayerAligned(target)) {
            target.heal(amount);
        } else {
            switch (random.nextInt(4)) {
                case 0 -> target.hurt(damageSources().thrown(this, getOwner()), amount);
                case 1 -> target.hurt(damageSources().onFire(), amount);
                case 2 -> target.hurt(damageSources().freeze(), amount);
                default -> target.hurt(damageSources().magic(), amount);
            }
        }

        discard();
    }

    private static boolean isPlayerAligned(LivingEntity target) {
        return target instanceof Player
                || target instanceof OwnableEntity ownable
                && ownable.getOwner() instanceof Player;
    }

    private void spawnEndRodParticles() {
        for (int i = 0; i < 3; i++) {
            level().addParticle(
                    ParticleTypes.END_ROD,
                    getX(), getY(), getZ(),
                    (random.nextDouble() - 0.5D) * 0.05D,
                    (random.nextDouble() - 0.5D) * 0.05D,
                    (random.nextDouble() - 0.5D) * 0.05D
            );
        }
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
}
