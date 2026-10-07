package net.pm_equips.items;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
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
import net.pm_equips.ItemInit;
import net.pm_equips.SoundInit;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Roland Logic SG
 * - 通常射撃: 攻撃力2のペレット6発（弾薬消費は1）
 * - パーティクル: ASH + SMOKE
 * - 射程16 / クールダウン50tick / リロードなし
 */
public class WeaponRolandLogicSG extends ProjectileWeaponItem {

	private static final int COOLDOWN_TICKS = 50;
	private static final double RANGE = 16.0;
	private static final double HIT_RADIUS = 0.4;
	private static final double TRAIL_STEP = 0.4;

	private static final float PELLET_DAMAGE = 2.0F;
	private static final int PELLET_COUNT = 6;
	private static final double SPREAD_DEG = 10.0; // ショットガンの拡散角度

	public WeaponRolandLogicSG(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack gun = player.getItemInHand(hand);

		if (player.getCooldowns().isOnCooldown(this)) {
			return InteractionResultHolder.fail(gun);
		}

		if (!hasAmmo(player)) {
			if (!level.isClientSide) {
				player.displayClientMessage(Component.literal("弾薬切れ / No Ammo"), true);
				level.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0F, 1.2F);
			}
			return InteractionResultHolder.fail(gun);
		}

		if (level.isClientSide) {
			level.playSound(player, player.blockPosition(), SoundInit.GUN_ROLAND_SHOTGUN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
			return InteractionResultHolder.success(gun);
		}

		shootShotgun(level, player);
		consumeAmmo(player);

		level.playSound(null, player.blockPosition(), SoundInit.GUN_ROLAND_SHOTGUN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		player.awardStat(Stats.ITEM_USED.get(this));
		player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
		gun.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

		return InteractionResultHolder.success(gun);
	}

	private void shootShotgun(Level level, Player player) {
		Vec3 start = player.getEyePosition();
		Vec3 base = player.getLookAngle();

		for (int i = 0; i < PELLET_COUNT; i++) {
			Vec3 dir = spreadDirection(base, level.random.nextDouble(), level.random.nextDouble());
			firePellet(level, player, start, dir);
		}
	}

	private Vec3 spreadDirection(Vec3 base, double r1, double r2) {
		double yawOff = (r1 - 0.5) * 2.0 * Math.toRadians(WeaponRolandLogicSG.SPREAD_DEG);
		double pitchOff = (r2 - 0.5) * 2.0 * Math.toRadians(WeaponRolandLogicSG.SPREAD_DEG);

		Vec3 right = base.cross(new Vec3(0, 1, 0));
		if (right.lengthSqr() < 1.0E-6) {
			right = base.cross(new Vec3(1, 0, 0));
		}
		right = right.normalize();
		Vec3 up = right.cross(base).normalize();

		return base.add(right.scale(Math.tan(yawOff))).add(up.scale(Math.tan(pitchOff))).normalize();
	}

	private void firePellet(Level level, Player player, Vec3 start, Vec3 dir) {
		Vec3 end = start.add(dir.scale(RANGE));

		AABB searchBox = new AABB(start, end).inflate(HIT_RADIUS + 1.0);
		List<LivingEntity> candidates = level.getEntitiesOfClass(
				LivingEntity.class,
				searchBox,
				e -> e != player && e.isAlive() && e.isPickable()
		);
		candidates.sort(Comparator.comparingDouble(e -> e.position().distanceToSqr(start)));

		LivingEntity hit = null;
		for (LivingEntity target : candidates) {
			if (rayIntersects(start, end, target)) {
				hit = target;
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
			hit.hurt(level.damageSources().playerAttack(player), PELLET_DAMAGE);

			if (level instanceof ServerLevel server) {
				Vec3 p = hit.getBoundingBox().getCenter();
				server.sendParticles(ParticleTypes.ASH, p.x, p.y, p.z, 4, 0.1, 0.1, 0.1, 0.01);
				server.sendParticles(ParticleTypes.SMOKE, p.x, p.y, p.z, 3, 0.1, 0.1, 0.1, 0.01);
			}
		}
	}

	private boolean rayIntersects(Vec3 start, Vec3 end, LivingEntity entity) {
		AABB box = entity.getBoundingBox().inflate(HIT_RADIUS);
		return box.clip(start, end).isPresent() || box.contains(start);
	}

	private void spawnTrail(ServerLevel level, Vec3 start, Vec3 end) {
		Vec3 direction = end.subtract(start);
		double distance = direction.length();
		if (distance < 0.1) return;

		Vec3 step = direction.normalize().scale(TRAIL_STEP);
		Vec3 current = start;
		int count = (int) (distance / TRAIL_STEP);

		for (int i = 0; i < count; i++) {
			current = current.add(step);
			level.sendParticles(ParticleTypes.ASH, current.x, current.y, current.z, 1, 0.02, 0.02, 0.02, 0.0);
			if (i % 2 == 0) {
				level.sendParticles(ParticleTypes.SMOKE, current.x, current.y, current.z, 1, 0.02, 0.02, 0.02, 0.0);
			}
		}
	}

	private boolean hasAmmo(Player player) {
		if (player.getAbilities().instabuild) return true;
		for (ItemStack stack : player.getInventory().items) {
			if (stack.is(ItemInit.P_BULLET_LASG.get()) && !stack.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private void consumeAmmo(Player player) {
		if (player.getAbilities().instabuild) return;
		for (ItemStack stack : player.getInventory().items) {
			if (stack.is(ItemInit.P_BULLET_LASG.get()) && !stack.isEmpty()) {
				stack.shrink(1);
				return;
			}
		}
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return stack -> stack.is(ItemInit.P_BULLET_LASG.get());
	}

	@Override
	public int getDefaultProjectileRange() {
		return (int) RANGE;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.literal("攻撃力2x6 | 低速 | 近"));
		super.appendHoverText(stack, level, tooltip, flag);
	}
}
