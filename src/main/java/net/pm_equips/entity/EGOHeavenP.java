package net.pm_equips.entity;

import net.minecraft.server.level.ServerEntity;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.pm_equips.PMERegistryEntity;
import net.pm_equips.PMERegistryItem;
import net.pm_equips.config.CommonConfig;

import java.util.HashSet;
import java.util.Set;

public class EGOHeavenP extends ThrowableItemProjectile {
    private static final float DAMAGE = 100.0F;
    private static final double MAX_DISTANCE = 128.0D;
    private static final double SPEED = 8.0D;
    private static final double HITBOX_INFLATION = 0.3D;

    private final Set<Integer> hitEntityIds = new HashSet<>();
    private double traveledDistance;

    public EGOHeavenP(EntityType<? extends EGOHeavenP> type, Level level) {
        super(type, level);
        setNoGravity(true);
    }

    public EGOHeavenP(Level level, LivingEntity owner, Vec3 direction) {
        this(PMERegistryEntity.HEAVEN_PROJECTILE.get(), level);
        setOwner(owner);

        Vec3 normalizedDirection = direction.normalize();
        setDeltaMovement(normalizedDirection.scale(SPEED));

        Vec3 spawnPos = owner.getEyePosition().add(normalizedDirection.scale(0.8D));
        setPos(spawnPos.x, spawnPos.y, spawnPos.z);
    }

    @Override
    protected Item getDefaultItem() {
        return PMERegistryItem.W4_HEAVEN.get();
    }

    @Override
    public void tick() {
        baseTick();

        Vec3 motion = getDeltaMovement();
        if (motion.lengthSqr() <= 0.0001D || traveledDistance >= MAX_DISTANCE) {
            discard();
            return;
        }

        double remainingDistance = MAX_DISTANCE - traveledDistance;
        Vec3 step = motion.length() > remainingDistance
                ? motion.normalize().scale(remainingDistance)
                : motion;
        Vec3 start = position();
        Vec3 end = start.add(step);

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
            hitEntitiesAlongPath(start, collisionEnd);

            if (blockHit.getType() == HitResult.Type.BLOCK) {
                setPos(collisionEnd.x, collisionEnd.y, collisionEnd.z);
                discard();
                return;
            }
        }

        move(MoverType.SELF, step);
        traveledDistance += step.length();
        checkInsideBlocks();

        if (traveledDistance >= MAX_DISTANCE) {
            discard();
        }
    }

    private void hitEntitiesAlongPath(Vec3 start, Vec3 end) {
        Entity ownerEntity = getOwner();
        if (!(ownerEntity instanceof LivingEntity owner)) {
            return;
        }

        AABB searchArea = getBoundingBox().expandTowards(end.subtract(start)).inflate(HITBOX_INFLATION);
        for (LivingEntity target : level().getEntitiesOfClass(LivingEntity.class, searchArea,
                living -> canHit(living, owner, start, end))) {
            hitEntityIds.add(target.getId());
            target.hurt(level().damageSources().thrown(this, owner), DAMAGE);
        }
    }

    private boolean canHit(LivingEntity target, LivingEntity owner, Vec3 start, Vec3 end) {
        if (!target.isAlive() || target == owner || hitEntityIds.contains(target.getId())) {
            return false;
        }

        if (!CommonConfig.ALLOW_FRIENDLY_FIRE.get() && owner.isAlliedTo(target)) {
            return false;
        }

        return target.getBoundingBox().inflate(HITBOX_INFLATION).clip(start, end).isPresent();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        return new ClientboundAddEntityPacket(this, serverEntity);
    }
}
