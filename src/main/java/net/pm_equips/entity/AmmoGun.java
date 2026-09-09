package net.pm_equips.entity;

import net.minecraft.server.level.ServerEntity;

import net.minecraft.network.syncher.SynchedEntityData;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;

public class AmmoGun extends Projectile implements GeoEntity {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private float damage;

    private double maxDistance = 128D;

    private double traveledDistance;

    private boolean terrainDamageEnabled = false;

    private boolean ignoreInvulnerabilityFrames;

    public AmmoGun(
            EntityType<? extends AmmoGun> type,
            Level level
    ) {
        super(type, level);

        this.setNoGravity(true);
    }

    public AmmoGun(
            Level level,
            LivingEntity shooter,
            float damage,
            float velocity,
            Vec3 direction
    ) {

        this(
                PMERegistryEntity.BULLET.get(),
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

                onHitBlock(blockHit);

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
                                    .inflate(0.25D),

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

                onHitEntity(entityHit);

                return;
            }
        }

        traveledDistance += motion.length();

        if (traveledDistance >= maxDistance) {

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

    protected void onHitBlock(
            BlockHitResult hit
    ) {

        if (terrainDamageEnabled
                && CommonConfig.ALLOW_TERRAIN_DAMAGE.get()) {

            BlockPos pos =
                    hit.getBlockPos();

            if (!level().isClientSide
                    && !level().getBlockState(pos)
                    .is(Blocks.BEDROCK))
            {
                level().destroyBlock(
                        pos,
                        true
                );
            }
        }

        discard();
    }

    @Override
    protected void onHit(
            HitResult result
    ) {
        discard();
    }

    @Override
    protected void onHitEntity(
            EntityHitResult result
    ) {

        Entity target =
                result.getEntity();

        Entity ownerEntity =
                getOwner();

        if (!(ownerEntity instanceof LivingEntity owner)) {

            discard();

            return;
        }

        if (!(target instanceof LivingEntity living)) {

            discard();

            return;
        }

        if (!CommonConfig.ALLOW_FRIENDLY_FIRE.get()
                && owner.isAlliedTo(living))
        {
            discard();
            return;
        }

        if (ignoreInvulnerabilityFrames) {
            living.invulnerableTime = 0;
            living.hurtTime = 0;
        }

        living.hurt(
                level()
                        .damageSources()
                        .thrown(this, owner),
                damage
        );

        discard();
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

    public void setTerrainDamageEnabled(
            boolean terrainDamageEnabled
    ) {
        this.terrainDamageEnabled = terrainDamageEnabled;
    }

    public void setIgnoreInvulnerabilityFrames(
            boolean ignoreInvulnerabilityFrames
    ) {
        this.ignoreInvulnerabilityFrames = ignoreInvulnerabilityFrames;
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
                0.1F,
                0.1F
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

    @Override
    public double getTick(Object object) {
        return 0;
    }
}
