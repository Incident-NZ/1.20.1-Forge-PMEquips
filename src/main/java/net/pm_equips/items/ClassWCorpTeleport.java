package net.pm_equips.items;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;

final class ClassWCorpTeleport {
    private static final int COOLDOWN_TICKS = 40;
    private static final double TELEPORT_DISTANCE = 64.0D;

    private ClassWCorpTeleport() {
    }

    static InteractionResultHolder<ItemStack> useTeleport(
            Level level,
            Player player,
            InteractionHand hand,
            Item item,
            int energyCost
    ) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(item)) {
            return InteractionResultHolder.fail(stack);
        }

        if (level.isClientSide) {
            return InteractionResultHolder.consume(stack);
        }

        AtomicBoolean teleported = new AtomicBoolean(false);
        Optional.ofNullable(stack.getCapability(Capabilities.EnergyStorage.ITEM)).ifPresent(storage -> {
            if (storage.getEnergyStored() >= energyCost) {
                storage.extractEnergy(energyCost, false);
                teleportForward(level, player);
                player.getCooldowns().addCooldown(item, COOLDOWN_TICKS);
                teleported.set(true);
            }
        });

        return teleported.get()
                ? InteractionResultHolder.consume(stack)
                : InteractionResultHolder.fail(stack);
    }

    static boolean teleportForward(Level level, Player player, double maximumDistance) {
        Vec3 start = player.position(), eye = player.getEyePosition(), look = player.getLookAngle().normalize();
        double loadedDistance = 0;
        for (double distance = .5; distance <= maximumDistance; distance += .5) {
            if (!level.hasChunkAt(BlockPos.containing(eye.add(look.scale(distance))))) break;
            loadedDistance = distance;
        }
        HitResult hit = level.clip(new ClipContext(eye, eye.add(look.scale(loadedDistance)),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        double distance = hit.getType() == HitResult.Type.BLOCK ? Math.max(0, eye.distanceTo(hit.getLocation()) - .75) : loadedDistance;
        for (; distance >= .25; distance -= .25) {
            Vec3 offset = look.scale(distance), target = start.add(offset);
            AABB bounds = player.getBoundingBox().move(offset);
            if (bounds.minY < level.getMinBuildHeight() || bounds.maxY > level.getMaxBuildHeight()
                    || !level.getWorldBorder().isWithinBounds(bounds)
                    || !level.hasChunksAt(BlockPos.containing(bounds.minX, bounds.minY, bounds.minZ),
                            BlockPos.containing(bounds.maxX, bounds.maxY, bounds.maxZ))
                    || !level.noCollision(player, bounds)) continue;
            player.teleportTo(target.x, target.y, target.z);
            player.fallDistance = 0;
            return true;
        }
        return false;
    }

    private static void teleportForward(Level level, Player player) {
        Vec3 start = player.position();
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();
        Vec3 end = eye.add(look.scale(TELEPORT_DISTANCE));

        HitResult hit = level.clip(new ClipContext(
                eye,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));

        Vec3 target = hit.getType() == HitResult.Type.BLOCK
                ? hit.getLocation().subtract(look.scale(0.75D))
                : start.add(look.scale(TELEPORT_DISTANCE));

        player.teleportTo(target.x, target.y, target.z);
        player.fallDistance = 0.0F;
    }
}
