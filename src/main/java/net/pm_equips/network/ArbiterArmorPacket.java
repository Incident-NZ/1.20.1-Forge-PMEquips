package net.pm_equips.network;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.pm_equips.ItemInit;
import net.pm_equips.MobEffectInit;
import net.pm_equips.PMEquipsMain;
import net.pm_equips.SoundInit;
import net.pm_equips.entity.MobNPC1;
import net.pm_equips.entity.MobNPC3;
import net.pm_equips.entity.PArbiterPillar;
import net.pm_equips.items.HeadArbiterArmor;

/** Only input edges/heartbeats cross the network; the server measures charge time and chooses the hit. */
@Mod.EventBusSubscriber(modid = PMEquipsMain.MOD_ID)
public record ArbiterArmorPacket(byte action)  {
    public static final byte START = 0, RELEASE = 1, CANCEL = 2, KEEP_ALIVE = 3;
    public static final int CHARGE_TICKS = 20;
    public static final int FAIRY_COOLDOWN = 10, PILLAR_COOLDOWN = 20;
    public static final float FAIRY_DAMAGE = 8;
    public static final int FAIRY_DURATION = 60;
    private static final Map<Player, Charge> CHARGES = new WeakHashMap<>();

    private static final class Charge {
        final ItemStack armor;
        final ServerLevel level;
        final long started;
        long lastSeen;
        boolean sounded;

        Charge(Player player, ServerLevel level) {
            armor = player.getItemBySlot(EquipmentSlot.LEGS);
            this.level = level;
            started = lastSeen = level.getGameTime();
        }
    }


public static boolean canUse(Player player) {
        return player.isAlive() && !player.isSpectator() && !player.isSleeping()
                && player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty()
                && player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof HeadArbiterArmor;
    }


    public static void process(Player player, byte action) {
        if (!(player.level() instanceof ServerLevel level)) return;
        if (action == CANCEL) { CHARGES.remove(player); return; }
        Charge charge = CHARGES.get(player);
        if (!canUse(player) || player.getCooldowns().isOnCooldown(ItemInit.HEAD_ARBITER_ARMOR.get())) {
            CHARGES.remove(player); return;
        }
        if (action == START) {
            if (charge == null) CHARGES.put(player, new Charge(player, level));
            return;
        }
        if (charge == null) return;
        if (charge.level != level || charge.armor != player.getItemBySlot(EquipmentSlot.LEGS)
                || level.getGameTime() - charge.lastSeen > 30) {
            CHARGES.remove(player); return;
        }
        if (action == KEEP_ALIVE) { charge.lastSeen = level.getGameTime(); return; }
        if (action != RELEASE) return;
        CHARGES.remove(player);
        if (level.getGameTime() - charge.started >= CHARGE_TICKS) {
            PArbiterPillar pillar = new PArbiterPillar(level, player);
            pillar.setPos(player.getEyePosition().add(player.getLookAngle().scale(.35)));
            pillar.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 2, 0);
            if (!level.addFreshEntity(pillar)) return;
            level.playSound(null, player.blockPosition(), SoundInit.ARBITER_PILLAR_SHOT.get(), SoundSource.PLAYERS, 1, 1);
            player.getCooldowns().addCooldown(ItemInit.HEAD_ARBITER_ARMOR.get(), PILLAR_COOLDOWN);
        } else {
            shootFairy(level, player);
            level.playSound(null, player.blockPosition(), SoundInit.ARBITER_FAIRY.get(), SoundSource.PLAYERS, 1, 1);
            player.getCooldowns().addCooldown(ItemInit.HEAD_ARBITER_ARMOR.get(), FAIRY_COOLDOWN);
        }
        player.awardStat(Stats.ITEM_USED.get(ItemInit.HEAD_ARBITER_ARMOR.get()));
    }

    private static boolean canHit(Player shooter, LivingEntity target) {
        if (target == shooter || !target.isAlive() || target.isSpectator() || !target.canBeHitByProjectile()) return false;
        if (target instanceof Player other && (other.isCreative() || !shooter.getServer().isPvpAllowed() || !shooter.canHarmPlayer(other))) return false;
        return !(target instanceof MobNPC1 npc) || !MobNPC3.preventDamage(npc, shooter.damageSources().playerAttack(shooter));
    }

    private static void shootFairy(ServerLevel level, Player player) {
        Vec3 start = player.getEyePosition(), look = player.getLookAngle();
        Vec3 end = start.add(look.scale(PArbiterPillar.RANGE));
        // Clip blocks first so a living target behind a wall is never selected.
        HitResult block = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        end = block.getLocation();
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(level, player, start, end,
                player.getBoundingBox().expandTowards(end.subtract(start)).inflate(1),
                entity -> entity instanceof LivingEntity living && canHit(player, living));
        if (hit != null && hit.getEntity() instanceof LivingEntity target) {
            end = hit.getLocation();
            target.hurt(player.damageSources().playerAttack(player), FAIRY_DAMAGE);
            target.addEffect(new MobEffectInstance(MobEffectInit.FAIRY.get(), FAIRY_DURATION, 0), player);
        }
        Vec3 midpoint = start.lerp(end, .5);
        ForgeFeaturePackets.sendNear(level, midpoint, start.distanceTo(end) / 2 + 64, new FairyTrail(start, end));
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;
        Charge charge = CHARGES.get(player);
        if (charge == null) return;
        if (!canUse(player) || charge.level != player.level() || charge.armor != player.getItemBySlot(EquipmentSlot.LEGS)
                || charge.level.getGameTime() - charge.lastSeen > 30) {
            CHARGES.remove(player); return;
        }
        if (!charge.sounded && charge.level.getGameTime() - charge.started >= 4) {
            charge.sounded = true;
            charge.level.playSound(null, player.blockPosition(), SoundInit.ARBITER_PILLAR_START.get(), SoundSource.PLAYERS, 1, 1);
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) { CHARGES.remove(event.getEntity()); }

    @SubscribeEvent
    public static void onServerStop(ServerStoppedEvent event) { CHARGES.clear(); }

    public record FairyTrail(Vec3 start, Vec3 end) {}
}
