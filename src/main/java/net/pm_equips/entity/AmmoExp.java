package net.pm_equips.entity;

import net.minecraft.server.level.ServerEntity;

import net.minecraft.network.syncher.SynchedEntityData;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.pm_equips.PMERegistryEntity;
import net.pm_equips.config.CommonConfig;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.PlayState;

import java.util.List;

public class AmmoExp extends Projectile implements GeoEntity {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private static final double DEFAULT_EXPLOSION_RADIUS = 5.0D;

    private float damage;

    private double explosionRadius = DEFAULT_EXPLOSION_RADIUS;

    private double maxDistance = 64D;

    private double traveledDistance;

    public AmmoExp(
            EntityType<? extends AmmoExp> type,
            Level level
    ) {
        super(type, level);

        this.setNoGravity(true);
    }

    public AmmoExp(
            Level level,
            LivingEntity shooter,
            float damage,
            float velocity,
            Vec3 direction
    ) {

        this(
                PMERegistryEntity.BULLET_EX.get(),
                level
        );

        this.setOwner(shooter);

        this.damage = damage;

        Vec3 shootDir =
                direction.normalize();

        this.setDeltaMovement(
                shootDir.scale(velocity)
        );

        Vec3 spawnPos =
                shooter.getEyePosition()
                        .add(shootDir.scale(0.8D));

        this.setPos(
                spawnPos.x,
                spawnPos.y,
                spawnPos.z
        );
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {

        super.tick();

        Vec3 motion =
                this.getDeltaMovement();

        if (motion.lengthSqr() <= 0.0001D) {
            discard();
            return;
        }

        Vec3 currentPos =
                this.position();

        Vec3 nextPos =
                currentPos.add(motion);

        if (!level().isClientSide) {

            BlockHitResult blockHit =
                    level().clip(
                            new ClipContext(
                                    currentPos,
                                    nextPos,
                                    ClipContext.Block.COLLIDER,
                                    ClipContext.Fluid.NONE,
                                    this
                            )
                    );

            if (blockHit.getType()
                    == HitResult.Type.BLOCK) {

                explodeAt(
                        blockHit.getLocation().x,
                        blockHit.getLocation().y,
                        blockHit.getLocation().z
                );

                discard();

                return;
            }

            Entity owner =
                    getOwner();

            EntityHitResult entityHit =
                    ProjectileUtil.getEntityHitResult(
                            level(),
                            owner != null ? owner : this,
                            currentPos,
                            nextPos,
                            getBoundingBox()
                                    .expandTowards(motion)
                                    .inflate(0.5D),

                            entity -> {

                                if (!(entity instanceof LivingEntity living))
                                    return false;

                                if (!living.isAlive())
                                    return false;

                                if (entity == owner)
                                    return false;

                                if (!CommonConfig.ALLOW_FRIENDLY_FIRE.get()
                                        && owner instanceof LivingEntity ownerLiving
                                        && ownerLiving.isAlliedTo(living))
                                {
                                    return false;
                                }

                                return true;
                            }
                    );

            if (entityHit != null) {

                explodeAt(
                        entityHit.getLocation().x,
                        entityHit.getLocation().y,
                        entityHit.getLocation().z
                );

                discard();

                return;
            }
        }

        traveledDistance += motion.length();

        if (traveledDistance >= maxDistance) {
            explodeAt(nextPos.x, nextPos.y, nextPos.z);
            discard();
            return;
        }

        move(
                MoverType.SELF,
                motion
        );

        checkInsideBlocks();

        if (level().isClientSide) {

            Vec3 pos =
                    position();

            level().addParticle(
                    ParticleTypes.SMOKE,
                    pos.x,
                    pos.y,
                    pos.z,
                    0,
                    0,
                    0
            );

            if (tickCount % 3 == 0) {

                level().addParticle(
                        ParticleTypes.LARGE_SMOKE,
                        pos.x,
                        pos.y + 0.1D,
                        pos.z,
                        (random.nextFloat() - 0.5F) * 0.1F,
                        0,
                        (random.nextFloat() - 0.5F) * 0.1F
                );
            }
        }
    }

    @Override
    protected void onHit(
            HitResult result
    ) {

        if (!level().isClientSide) {
            explodeAt(
                    result.getLocation().x,
                    result.getLocation().y,
                    result.getLocation().z
            );
        }

        discard();
    }

    private void explodeAt(
            double x,
            double y,
            double z
    ) {

        Level level =
                level();

        if (level.isClientSide) {
            return;
        }

        Vec3 center =
                new Vec3(
                        x,
                        y,
                        z
                );

        level.playSound(
                null,
                x,
                y,
                z,
                SoundEvents.GENERIC_EXPLODE,
                SoundSource.PLAYERS,
                1.2F,
                0.8F
        );

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    ParticleTypes.EXPLOSION,
                    x,
                    y,
                    z,
                    1,
                    0,
                    0,
                    0,
                    0
            );
        }

        damageEntities(
                level,
                center
        );

        if (CommonConfig.ALLOW_TERRAIN_DAMAGE.get()) {
            destroyBlocks(
                    level,
                    center
            );
        }
    }

    private void damageEntities(
            Level level,
            Vec3 center
    ) {

        Entity owner =
                getOwner();

        AABB area =
                new AABB(
                        center,
                        center
                ).inflate(explosionRadius);

        List<LivingEntity> targets =
                level.getEntitiesOfClass(
                        LivingEntity.class,
                        area,
                        entity -> entity.isAlive()
                                && entity != owner
                                && entity.position().distanceTo(center) <= explosionRadius
                );

        for (LivingEntity target : targets) {

            if (!CommonConfig.ALLOW_FRIENDLY_FIRE.get()
                    && owner instanceof LivingEntity ownerLiving
                    && ownerLiving.isAlliedTo(target))
            {
                continue;
            }

            target.invulnerableTime = 0;
            target.hurt(
                    level.damageSources().thrown(this, owner),
                    damage
            );
        }
    }

    private void destroyBlocks(
            Level level,
            Vec3 center
    ) {

        int radius =
                (int)Math.ceil(explosionRadius);

        BlockPos centerPos =
                BlockPos.containing(center);

        for (BlockPos pos : BlockPos.betweenClosed(
                centerPos.offset(-radius, -radius, -radius),
                centerPos.offset(radius, radius, radius))) {

            if (pos.getCenter().distanceTo(center) > explosionRadius) {
                continue;
            }

            if (level.getBlockState(pos).isAir()
                    || level.getBlockState(pos).is(Blocks.BEDROCK)) {
                continue;
            }

            level.destroyBlock(
                    pos,
                    true
            );
        }
    }

    public void setDamage(
            float damage
    ) {
        this.damage = damage;
    }

    public void setVelocity(
            float velocity
    ) {

        Vec3 motion =
                getDeltaMovement();

        if (motion.lengthSqr() <= 0.0001D)
            return;

        setDeltaMovement(
                motion.normalize()
                        .scale(velocity)
        );
    }

    public void setMaxLifetime(
            int range
    ) {
        this.maxDistance = range;
    }

    public void setExplosionRadius(
            double explosionRadius
    ) {
        this.explosionRadius = Math.max(
                1.0D,
                explosionRadius
        );
    }

    @Override
    public Packet<ClientGamePacketListener>
    getAddEntityPacket(ServerEntity serverEntity)
    {
        return new ClientboundAddEntityPacket(this, serverEntity);
    }

    @Override
    public EntityDimensions getDimensions(
            Pose pose
    ) {
        return EntityDimensions.fixed(
                0.3F,
                0.3F
        );
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
