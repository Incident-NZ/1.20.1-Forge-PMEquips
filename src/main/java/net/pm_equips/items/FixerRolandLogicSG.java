package net.pm_equips.items;

import net.minecraft.world.item.Item;


import net.pm_equips.util.ItemStackData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.pm_equips.PMERegistryEntity;
import net.pm_equips.PMERegistryItem;
import net.pm_equips.PMERegistrySound;
import net.pm_equips.entity.AmmoLogicSG;

import java.util.List;
import java.util.function.Predicate;

public class FixerRolandLogicSG extends net.minecraft.world.item.ProjectileWeaponItem {

	private static final int MAX_AMMO = 8;
	private static final int RELOAD_TICKS = 100; // 5 seconds
	private static final int COOLDOWN_TICKS = 50; // 2.5 second between shots
	private static final float VELOCITY = 8.0f;

	public FixerRolandLogicSG(Properties properties) {
		super(properties);
	}
    @Override
    protected void shootProjectile(net.minecraft.world.entity.LivingEntity shooter,
                                   net.minecraft.world.entity.projectile.Projectile projectile,
                                   int index, float velocity, float inaccuracy, float angle,
                                   net.minecraft.world.entity.LivingEntity target) {
        projectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), angle, velocity, inaccuracy);
    }

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack gun = player.getItemInHand(hand);

		if (player.getCooldowns().isOnCooldown(this)) {
			return InteractionResultHolder.fail(gun);
		}

		int ammo = ItemStackData.getInt(gun, "Ammo");
		int reload = ItemStackData.getInt(gun, "Reload");

		if (reload > 0) {
			return InteractionResultHolder.fail(gun);
		}

		if (ammo <= 0) {
			if (!level.isClientSide) {
				player.displayClientMessage(Component.literal("弾薬切れ / No Ammo"), true);
				level.playSound(null, player, SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0F, 1.2F);
			}
			return InteractionResultHolder.fail(gun);
		}

		if (level.isClientSide) {
			player.level().playSound(
					null,
					player.blockPosition(),
					PMERegistrySound.GUN_ROLAND_SHOTGUN.get(),
					SoundSource.PLAYERS,
					1.0F,
					1.0F
			);
			return InteractionResultHolder.success(gun);
		}

		// server-side: spawn projectile
		Vec3 look = player.getLookAngle();
		float damage = 7 + player.getRandom().nextInt(6); // 7..12

		AmmoLogicSG bullet = new AmmoLogicSG(PMERegistryEntity.BULLET_LASG.get(), level);
		bullet.initFromShooter(player, damage, VELOCITY, look);
		level.addFreshEntity(bullet);

		ItemStackData.putInt(gun, "Ammo", ammo - 1);

		player.level().playSound(
				null,
				player.blockPosition(),
				PMERegistrySound.GUN_ROLAND_SHOTGUN.get(),
				SoundSource.PLAYERS,
				1.0F,
				1.0F
		);

		player.awardStat(Stats.ITEM_USED.get(this));
		player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

		gun.hurtAndBreak(1, player, hand == net.minecraft.world.InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND);

		return InteractionResultHolder.success(gun);
	}

	// Rキーリロード開始 (弾が残っていても可能、残存弾は捨てずに継ぎ足す)
	public void startReload(ItemStack stack, Player player) {
		int reload = ItemStackData.getInt(stack, "Reload");
		if (reload > 0) return;

		int ammo = ItemStackData.getInt(stack, "Ammo");
		if (ammo >= MAX_AMMO) return;

		boolean hasAmmo = false;
		for (ItemStack invStack : player.getInventory().items) {
			if (invStack.is(PMERegistryItem.P_BULLET_LASG.get())) {
				hasAmmo = true;
				break;
			}
		}

		if (!hasAmmo) return;

		ItemStackData.putInt(stack, "Reload", RELOAD_TICKS);
		player.level().playSound(null, player.blockPosition(), SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.PLAYERS, 1.0F, 1.0F);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
		if (!ItemStackData.contains(stack, "Ammo")) {
			ItemStackData.putInt(stack, "Ammo", MAX_AMMO);
		}

		int reload = ItemStackData.getInt(stack, "Reload");

		if (reload > 0) {
			reload--;
			ItemStackData.putInt(stack, "Reload", reload);

			if (reload <= 0 && entity instanceof Player player) {
				int ammo = ItemStackData.getInt(stack, "Ammo");
				int needed = MAX_AMMO - ammo;
				if (needed <= 0) return;

				int loaded = 0;
				for (ItemStack invStack : player.getInventory().items) {
					if (invStack.is(PMERegistryItem.P_BULLET_LASG.get())) {
						while (!invStack.isEmpty() && loaded < needed) {
							invStack.shrink(1);
							loaded++;
						}
					}
					if (loaded >= needed) break;
				}

				ItemStackData.putInt(stack, "Ammo", ammo + loaded);
			}
		}

		super.inventoryTick(stack, level, entity, slot, selected);
	}

	@Override
	public Predicate<ItemStack> getAllSupportedProjectiles() {
		return stack -> stack.is(PMERegistryItem.P_BULLET_LASG.get());
	}

	@Override
	public int getDefaultProjectileRange() {
		return 32;
	}

	@Override
	public void appendHoverText(
			ItemStack stack,
			Item.TooltipContext context,
			List<Component> tooltip,
			TooltipFlag flag
	) {

		CompoundTag tag = ItemStackData.copy(stack);
		tooltip.add(
				Component.literal(
						"Ammo: "
								+ tag.getInt("Ammo")
								+ " / "
								+ MAX_AMMO
				)
		);

		super.appendHoverText(
				stack,
				context,
				tooltip,
				flag
		);
	}
}

