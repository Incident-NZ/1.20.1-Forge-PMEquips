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
 * Roland Logic HG（リボルバー）
 * - エンティティなし / パーティクル軌跡
 * - リロードなし（撃つたびにインベントリから P_BULLET_LARV 消費）
 * - 攻撃力8 / 射程32 / クールダウン20tick
 * - 両手に同じ銃 → 二丁射撃（左右2発・弾薬2消費）
 */
public class WeaponRolandLogicHG extends ProjectileWeaponItem {

	private static final int COOLDOWN_TICKS = 20;
	private static final int DUAL_COOLDOWN_TICKS = 10;
	private static final double RANGE = 32.0;
	private static final double HIT_RADIUS = 0.4;
	private static final double TRAIL_STEP = 0.4;
	private static final float DAMAGE = 8.0F;
	private static final double DUAL_SPREAD_DEG = 3.5;

	public WeaponRolandLogicHG(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack gun = player.getItemInHand(hand);

		if (player.getCooldowns().isOnCooldown(this)) {
			return InteractionResultHolder.fail(gun);
		}

		boolean dual = isDualWielding(player);
		int ammoCost = dual ? 2 : 1;

		if (!hasAmmo(player, ammoCost)) {
			if (!level.isClientSide) {
				player.displayClientMessage(
						Component.literal(dual ? "弾薬不足（2発必要）" : "弾薬切れ / No Ammo"),
						true
				);
				level.playSound(null, player.blockPosition(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0F, 1.2F);
			}
			return InteractionResultHolder.fail(gun);
		}

		if (dual) {
			shootDual(level, player);
			// 両手の耐久を減らす
			hurtBoth(player);
			player.getCooldowns().addCooldown(this, DUAL_COOLDOWN_TICKS);
		} else {
			shootSingle(level, player, player.getLookAngle());
			gun.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
			player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
		}

		consumeAmmo(player, ammoCost);
		level.playSound(null, player.blockPosition(), SoundInit.GUN_ROLAND_REVOLVER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		if (dual) {
			level.playSound(null, player.blockPosition(), SoundInit.GUN_ROLAND_REVOLVER.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
		}
		player.awardStat(Stats.ITEM_USED.get(this));

		return InteractionResultHolder.success(gun);
	}

	/** メイン・オフ両方にこの銃があるか */
	private boolean isDualWielding(Player player) {
		return player.getMainHandItem().getItem() == this
				&& player.getOffhandItem().getItem() == this;
	}

	private void shootSingle(Level level, Player player, Vec3 direction) {
		fireRay(level, player, player.getEyePosition(), direction);
	}

	private void shootDual(Level level, Player player) {
		Vec3 eye = player.getEyePosition();
		Vec3 look = player.getLookAngle();
		fireRay(level, player, eye, rotateYaw(look, DUAL_SPREAD_DEG));
		fireRay(level, player, eye, rotateYaw(look, -DUAL_SPREAD_DEG));
	}

	private Vec3 rotateYaw(Vec3 vec, double degrees) {
		double rad = Math.toRadians(degrees);
		double cos = Math.cos(rad);
		double sin = Math.sin(rad);
		return new Vec3(vec.x * cos - vec.z * sin, vec.y, vec.x * sin + vec.z * cos).normalize();
	}

	private void fireRay(Level level, Player player, Vec3 start, Vec3 dir) {
		Vec3 end = start.add(dir.scale(RANGE));

		AABB box = new AABB(start, end).inflate(HIT_RADIUS + 1.0);
		List<LivingEntity> list = level.getEntitiesOfClass(
				LivingEntity.class, box,
				e -> e != player && e.isAlive() && e.isPickable()
		);
		list.sort(Comparator.comparingDouble(e -> e.position().distanceToSqr(start)));

		LivingEntity hit = null;
		for (LivingEntity e : list) {
			if (rayHits(start, end, e)) {
				hit = e;
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
			hit.hurt(level.damageSources().playerAttack(player), DAMAGE);

			if (level instanceof ServerLevel server) {
				Vec3 p = hit.getBoundingBox().getCenter();
				server.sendParticles(ParticleTypes.CRIT, p.x, p.y, p.z, 6, 0.15, 0.15, 0.15, 0.05);
				server.sendParticles(ParticleTypes.SMOKE, p.x, p.y, p.z, 4, 0.1, 0.1, 0.1, 0.01);
			}
		}
	}

	private boolean rayHits(Vec3 start, Vec3 end, LivingEntity entity) {
		AABB box = entity.getBoundingBox().inflate(HIT_RADIUS);
		return box.clip(start, end).isPresent() || box.contains(start);
	}

	private void spawnTrail(ServerLevel level, Vec3 start, Vec3 end) {
		Vec3 d = end.subtract(start);
		double dist = d.length();
		if (dist < 0.1) return;

		Vec3 step = d.normalize().scale(TRAIL_STEP);
		Vec3 cur = start;
		int n = (int) (dist / TRAIL_STEP);
		for (int i = 0; i < n; i++) {
			cur = cur.add(step);
			level.sendParticles(ParticleTypes.CRIT, cur.x, cur.y, cur.z, 1, 0.02, 0.02, 0.02, 0.0);
			if (i % 2 == 0) {
				level.sendParticles(ParticleTypes.SMOKE, cur.x, cur.y, cur.z, 1, 0.015, 0.015, 0.015, 0.0);
			}
		}
	}

	private void hurtBoth(Player player) {
		ItemStack main = player.getMainHandItem();
		ItemStack off = player.getOffhandItem();
		if (main.getItem() == this) {
			main.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
		}
		if (off.getItem() == this) {
			off.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.OFF_HAND));
		}
	}

	private boolean hasAmmo(Player player, int amount) {
		if (player.getAbilities().instabuild) return true;
		int count = 0;
		for (ItemStack stack : player.getInventory().items) {
			if (stack.is(ItemInit.P_BULLET_LARV.get())) {
				count += stack.getCount();
				if (count >= amount) return true;
			}
		}
		return false;
	}

	private void consumeAmmo(Player player, int amount) {
		if (player.getAbilities().instabuild) return;
		int left = amount;
		for (ItemStack stack : player.getInventory().items) {
			if (!stack.is(ItemInit.P_BULLET_LARV.get()) || stack.isEmpty()) continue;
			int take = Math.min(left, stack.getCount());
			stack.shrink(take);
			left -= take;
			if (left <= 0) return;
		}
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return stack -> stack.is(ItemInit.P_BULLET_LARV.get());
	}

	@Override
	public int getDefaultProjectileRange() {
		return (int) RANGE;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.literal("攻撃力8 | 高速 | 普通"));
		tooltip.add(Component.literal("オフハンドに同名の武器を持っている場合、二丁拳銃として使用可能"));
		super.appendHoverText(stack, level, tooltip, flag);
	}
}
