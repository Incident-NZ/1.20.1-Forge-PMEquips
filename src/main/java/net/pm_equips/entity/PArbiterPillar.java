package net.pm_equips.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.fluids.FluidType;
import net.pm_equips.EntityInit;
import net.pm_equips.MobEffectInit;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.ParticleInit;
import net.pm_equips.SoundInit;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

@EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
public class PArbiterPillar extends Projectile implements GeoEntity {
    public static final double RANGE = 128;
    public static final int EMBEDDED_TICKS = 100;
    public static final float DAMAGE = 11;
    public static final double LENGTH = 2.5;
    private static final double RADIUS = .3125;
    private static final String RESTRAINTS = "pm_equips_pillar_restraints";
    private static final EntityDataAccessor<Boolean> EMBEDDED =
            SynchedEntityData.defineId(PArbiterPillar.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Set<UUID> hitEntities = new HashSet<>();
    private final Set<UUID> pushedEntities = new HashSet<>();
    private double distanceTravelled;
    private int stationaryTicks;
    private long embeddedUntil;
    private int idleTicks;

    public PArbiterPillar(EntityType<? extends PArbiterPillar> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public PArbiterPillar(Level level, LivingEntity shooter) {
        this(EntityInit.P_ARBITER_PILLAR.get(), level);
        setOwner(shooter);
        setPos(shooter.getEyePosition());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(EMBEDDED, false);
    }

    public boolean isEmbedded() {
        return entityData.get(EMBEDDED);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    @Override
    public void shootFromRotation(Entity shooter, float pitch, float yaw, float pitchOffset, float speed, float spread) {
        // Do not inherit the shooter's motion: the pillar always follows its firing direction.
        float radians = Mth.DEG_TO_RAD;
        shoot(-Mth.sin(yaw * radians) * Mth.cos(pitch * radians),
                -Mth.sin((pitch + pitchOffset) * radians),
                Mth.cos(yaw * radians) * Mth.cos(pitch * radians), speed, spread);
    }

    @Override
    public void tick() {
        super.tick();
        setNoGravity(true);
        if (isEmbedded()) {
            setDeltaMovement(Vec3.ZERO);
            if (!level().isClientSide) {
                if (++stationaryTicks >= EMBEDDED_TICKS || embeddedUntil > 0 && level().getGameTime() >= embeddedUntil) {
                    discard();
                    return;
                }
                applyAura();
            }
            return;
        }
        Vec3 motion = getDeltaMovement();
        double speed = motion.length();
        if (!Double.isFinite(speed) || speed < 1.0E-7) {
            if (!level().isClientSide && ++idleTicks >= 100) discard();
            return;
        }
        idleTicks = 0;
        setYRot((float) (Mth.atan2(motion.x, motion.z) * Mth.RAD_TO_DEG));
        setXRot((float) (Mth.atan2(motion.y, motion.horizontalDistance()) * Mth.RAD_TO_DEG));
        yRotO = getYRot(); xRotO = getXRot();
        Vec3 direction = motion.scale(1 / speed);
        Vec3 start = position();
        Vec3 end = start.add(direction.scale(Math.min(speed, RANGE - distanceTravelled)));
        BlockHitResult block = level().clip(new ClipContext(start, end,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        boolean hitBlock = block.getType() == HitResult.Type.BLOCK;
        if (hitBlock && !level().isClientSide && EventHooks.onProjectileImpact(this, block)) hitBlock = false;
        if (isRemoved()) return;
        if (hitBlock) end = block.getLocation();
        if (level().isClientSide) {
            spawnTrail(start, end, direction);
        } else {
            pushTargets(start, end, direction);
        }
        distanceTravelled += start.distanceTo(end);
        setPos(end);
        if (hitBlock) {
            if (!level().isClientSide) {
                onHit(block);
            }
        } else if (!level().isClientSide && distanceTravelled >= RANGE - 1.0E-7) {
            discard();
        }
    }

    private boolean canAffect(LivingEntity target) {
        if (!target.isAlive() || target.isSpectator() || target == getOwner() || !canHitEntity(target)) return false;
        Entity owner = getOwner();
        if (target instanceof MobNPC1 npc && MobNPC3.preventDamage(npc, damageSources().thrown(this, owner))) return false;
        if (target instanceof Player player) {
            if (player.isCreative()) return false;
            if (owner instanceof Player shooter && (!level().getServer().isPvpAllowed() || !shooter.canHarmPlayer(player))) return false;
        }
        return !(owner instanceof MobNPC1 npc) || npc.canAttack(target);
    }

    private void pushTargets(Vec3 start, Vec3 end, Vec3 direction) {
        Vec3 tail = start.subtract(direction.scale(LENGTH));
        AABB swept = new AABB(tail, end).inflate(RADIUS);
        Set<UUID> touching = new HashSet<>();
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, swept, this::canAffect)) {
            AABB bounds = target.getBoundingBox().inflate(RADIUS);
            if (!bounds.contains(tail) && bounds.clip(tail, end).isEmpty()) continue;
            EntityHitResult hit = new EntityHitResult(target);
            if (EventHooks.onProjectileImpact(this, hit)) continue;
            UUID id = target.getUUID();
            if (hitEntities.add(id)) {
                target.hurt(damageSources().thrown(this, getOwner()), DAMAGE);
                target.addEffect(new MobEffectInstance(MobEffectInit.FAIRY, 100, 2), getEffectSource());
                level().playSound(null, target.blockPosition(), SoundInit.ARBITER_PILLAR_END.get(), SoundSource.PLAYERS, 1, 1);
            }
            if (!target.isAlive()) continue;
            // Entity.move respects collision shapes; a carried victim cannot be pushed through a wall.
            Vec3 previous = target.position();
            target.move(MoverType.SELF, end.subtract(start));
            if (target instanceof ServerPlayer && target.position().distanceToSqr(previous) > 1.0E-8) {
                target.teleportTo(target.getX(), target.getY(), target.getZ());
            }
            target.setDeltaMovement(Vec3.ZERO);
            target.hurtMarked = true;
            touching.add(id);
            pushedEntities.add(id);
        }
        pushedEntities.retainAll(touching);
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {
        super.onHitBlock(hit);
        if (isRemoved()) return;
        entityData.set(EMBEDDED, true);
        level().playSound(null, blockPosition(), SoundInit.ARBITER_PILLAR_END.get(), SoundSource.PLAYERS, 1, 1);
        setDeltaMovement(Vec3.ZERO);
        hasImpulse = true;
        stationaryTicks = 0;
        String key = getUUID().toString();
        embeddedUntil = level().getGameTime() + EMBEDDED_TICKS;
        ServerLevel server = (ServerLevel) level();
        for (UUID id : pushedEntities) {
            if (!(server.getEntity(id) instanceof LivingEntity target) || !target.isAlive()) continue;
            CompoundTag locks = target.getPersistentData().getCompound(RESTRAINTS);
            CompoundTag lock = new CompoundTag();
            Vec3 anchor = target.position();
            lock.putLong("Until", embeddedUntil);
            lock.putString("Dimension", level().dimension().location().toString());
            lock.putDouble("X", anchor.x); lock.putDouble("Y", anchor.y); lock.putDouble("Z", anchor.z);
            locks.put(key, lock);
            target.getPersistentData().put(RESTRAINTS, locks);
            target.addTag(RESTRAINTS);
            holdAt(target, anchor);
        }
        applyAura();
    }

    private void applyAura() {
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class,
                new AABB(position(), position()).inflate(3), this::canAffect)) {
            if (target.getBoundingBox().distanceToSqr(position()) <= 9) {
                // Refresh every forty ticks so Fairy's duration-based damage ticks still run.
                MobEffectInstance current = target.getEffect(MobEffectInit.FAIRY);
                if (current == null || current.getAmplifier() < 2 || current.getAmplifier() == 2 && current.getDuration() <= 60) {
                    target.addEffect(new MobEffectInstance(MobEffectInit.FAIRY, 100, 2), getEffectSource());
                }
            }
        }
    }

    private void spawnTrail(Vec3 start, Vec3 end, Vec3 direction) {
        int count = Math.clamp(Mth.ceil(start.distanceTo(end) * 6), 1, 32);
        Vec3 tail = direction.scale(-LENGTH);
        for (int i = 0; i < count; i++) {
            Vec3 point = start.lerp(end, (i + .5) / count).add(tail);
            level().addParticle(ParticleInit.ARBITER_TRAIL.get(), point.x, point.y, point.z, 0, 0, 0);
        }
    }

    private static void holdAt(LivingEntity target, Vec3 anchor) {
        target.stopRiding();
        if (target instanceof Mob mob) mob.getNavigation().stop();
        if (target.position().distanceToSqr(anchor) > 1.0E-8) target.teleportTo(anchor.x, anchor.y, anchor.z);
        target.setDeltaMovement(Vec3.ZERO);
        target.hurtMarked = true;
        target.fallDistance = 0;
    }

    private static void updateRestraint(LivingEntity target) {
        if (target.level().isClientSide || !target.getTags().contains(RESTRAINTS)) return;
        CompoundTag locks = target.getPersistentData().getCompound(RESTRAINTS);
        long now = target.level().getGameTime();
        CompoundTag active = null;
        for (String key : Set.copyOf(locks.getAllKeys())) {
            CompoundTag lock = locks.getCompound(key);
            if (lock.getLong("Until") <= now || !lock.getString("Dimension").equals(target.level().dimension().location().toString())) locks.remove(key);
            else if (active == null || lock.getLong("Until") > active.getLong("Until")) active = lock;
        }
        if (active == null || !target.isAlive()) {
            target.removeTag(RESTRAINTS);
            target.getPersistentData().remove(RESTRAINTS);
            if (target.isAlive()) target.addEffect(new MobEffectInstance(MobEffectInit.FAIRY, 600, 5));
        } else {
            target.getPersistentData().put(RESTRAINTS, locks);
            holdAt(target, new Vec3(active.getDouble("X"), active.getDouble("Y"), active.getDouble("Z")));
        }
    }

    @SubscribeEvent
    public static void afterEntityTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity target && !(target instanceof Player)) updateRestraint(target);
    }

    @SubscribeEvent
    public static void afterPlayerTick(PlayerTickEvent.Post event) {
        updateRestraint(event.getEntity());
    }

    @Override
    public void remove(RemovalReason reason) {
        if (reason.shouldDestroy() && level() instanceof ServerLevel server) {
            String key = getUUID().toString();
            for (UUID id : pushedEntities) {
                if (server.getEntity(id) instanceof LivingEntity target && target.getTags().contains(RESTRAINTS)) {
                    CompoundTag locks = target.getPersistentData().getCompound(RESTRAINTS);
                    locks.remove(key);
                    target.getPersistentData().put(RESTRAINTS, locks);
                    updateRestraint(target);
                }
            }
        }
        super.remove(reason);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Embedded", isEmbedded());
        tag.putInt("StationaryTicks", stationaryTicks);
        tag.putLong("EmbeddedUntil", embeddedUntil);
        tag.putDouble("DistanceTravelled", distanceTravelled);
        ListTag hits = new ListTag();
        for (UUID id : hitEntities) { CompoundTag hit = new CompoundTag(); hit.putUUID("Id", id); hits.add(hit); }
        tag.put("HitEntities", hits);
        ListTag pushed = new ListTag();
        for (UUID id : pushedEntities) { CompoundTag target = new CompoundTag(); target.putUUID("Id", id); pushed.add(target); }
        tag.put("PushedEntities", pushed);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(EMBEDDED, tag.getBoolean("Embedded"));
        stationaryTicks = Mth.clamp(tag.getInt("StationaryTicks"), 0, EMBEDDED_TICKS);
        embeddedUntil = tag.getLong("EmbeddedUntil");
        double distance = tag.getDouble("DistanceTravelled");
        distanceTravelled = Double.isFinite(distance) ? Mth.clamp(distance, 0, RANGE) : 0;
        hitEntities.clear(); pushedEntities.clear();
        for (Tag value : tag.getList("HitEntities", Tag.TAG_COMPOUND)) {
            CompoundTag hit = (CompoundTag) value;
            if (hit.hasUUID("Id")) hitEntities.add(hit.getUUID("Id"));
        }
        for (Tag value : tag.getList("PushedEntities", Tag.TAG_COMPOUND)) {
            CompoundTag target = (CompoundTag) value;
            if (target.hasUUID("Id")) pushedEntities.add(target.getUUID("Id"));
        }
        setNoGravity(true);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 160 * 160;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return getBoundingBox().inflate(LENGTH);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
