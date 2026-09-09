package net.pm_equips.entity;

import net.minecraft.server.level.ServerEntity;

import net.minecraft.network.syncher.SynchedEntityData;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;

public class AmmoLogicRV extends Projectile implements GeoEntity {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private float damage;
    private int maxLifetime = 32; // default: will be overwritten by shooter to match range

    public AmmoLogicRV(EntityType<? extends AmmoLogicRV> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
    }

    // use factory-style initialization from spawner code to avoid static init ordering issues
    public void initFromShooter(LivingEntity shooter, float damage, float velocity, Vec3 direction) {
        this.setOwner(shooter);
        this.damage = damage;
        this.setMaxLifetime(32);

        Vec3 shootDir = direction.normalize();
        Vec3 motion = shootDir.scale(velocity);
        this.setDeltaMovement(motion);

        Vec3 eyePos = shooter.getEyePosition();
        Vec3 spawnPos = eyePos.add(shootDir.scale(0.8));
        this.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        super.tick();

        Vec3 motion = this.getDeltaMovement();
        Vec3 currentPos = this.position();
        Vec3 nextPos = currentPos.add(motion);

        if (!this.level().isClientSide) {
            // block collision
            net.minecraft.world.phys.BlockHitResult blockHit = this.level().clip(
                    new ClipContext(currentPos, nextPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)
            );
            if (blockHit.getType() == HitResult.Type.BLOCK) {
                this.onHit(blockHit);
                return;
            }

            // entity collision
            Entity owner = this.getOwner();
            EntityHitResult entityHit = net.minecraft.world.entity.projectile.ProjectileUtil.getEntityHitResult(
                    this.level(), owner != null ? owner : this, currentPos, nextPos,
                    this.getBoundingBox().expandTowards(motion).inflate(0.5D), e -> e != owner && e instanceof LivingEntity && e.isAlive()
            );
            if (entityHit != null) {
                this.onHitEntity(entityHit);
                return;
            }
        }

        if (motion.lengthSqr() > 0) {
            this.setPos(nextPos.x, nextPos.y, nextPos.z);
        }

        this.checkInsideBlocks();

        if (this.level().isClientSide) {
            Vec3 pos = this.position();

            this.level().addParticle(ParticleTypes.SMOKE,
                    pos.x, pos.y, pos.z,
                    0, 0, 0);

            if (this.tickCount % 3 == 0) {
                this.level().addParticle(ParticleTypes.LARGE_SMOKE,
                        pos.x, pos.y + 0.1, pos.z,
                        (this.random.nextFloat() - 0.5) * 0.1, 0, (this.random.nextFloat() - 0.5) * 0.1);
            }
        }

        if (this.tickCount > this.maxLifetime) {
            this.discard();
        }
    }

    @Override
    protected void onHit(HitResult result) {
        this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity target = result.getEntity();
        LivingEntity owner = (LivingEntity) this.getOwner();

        if (target instanceof LivingEntity living && owner != null && !target.is(owner)) {
            living.hurt(this.level().damageSources().thrown(this, owner), this.damage);
        }
        this.discard();
    }

    public void setDamage(float damage) { this.damage = damage; }
    public void setVelocity(float velocity) {
        Vec3 dir = this.getDeltaMovement().normalize();
        if (dir.lengthSqr() > 0) {
            this.setDeltaMovement(dir.scale(velocity));
        }
    }
    public void setMaxLifetime(int lifetime) { this.maxLifetime = lifetime; }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        return new ClientboundAddEntityPacket(this, serverEntity);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return EntityDimensions.fixed(0.25F, 0.25F);
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


