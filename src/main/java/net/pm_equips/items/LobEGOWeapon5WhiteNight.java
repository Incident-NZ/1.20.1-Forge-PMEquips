package net.pm_equips.items;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.pm_equips.EntityInit;
import net.pm_equips.ItemInit;
import net.pm_equips.MobEffectInit;
import net.pm_equips.SoundInit;
import net.pm_equips.client.screen.TooltipLines;
import net.pm_equips.config.CommonConfig;
import net.pm_equips.entity.PWhiteNight;

public class LobEGOWeapon5WhiteNight extends SwordItem {

    private static final ResourceLocation REACH_UUID = ResourceLocation.fromNamespaceAndPath("pm_equips", "lobegoweapon5whitenight_reach_uuid");
    private static final AttributeModifier REACH_MODIFIER =
            new AttributeModifier(REACH_UUID, 3.0, AttributeModifier.Operation.ADD_VALUE);

    /** 左クリック時バリア Amp3 の持続（10秒） */
    private static final int BARRIER_DURATION = 200;
    private static final int BARRIER_AMPLIFIER = 3; // 40以下のダメージ無効

    public LobEGOWeapon5WhiteNight() {
        super(new CustomTier(), new Properties().durability(4000).rarity(Rarity.EPIC).attributes(SwordItem.createAttributes(new CustomTier(), 50, -3.5f)));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide || !(entity instanceof Player player)) return;

        boolean isHolding = selected && player.getMainHandItem() == stack;

        AttributeInstance reachAttr = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (reachAttr != null) {
            if (isHolding && !reachAttr.hasModifier(REACH_MODIFIER.id())) {
                reachAttr.addTransientModifier(REACH_MODIFIER);
            } else if (!isHolding && reachAttr.hasModifier(REACH_MODIFIER.id())) {
                reachAttr.removeModifier(REACH_MODIFIER.id());
            }
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean result = super.hurtEnemy(stack, target, attacker);

        if (result && !attacker.level().isClientSide()) {
            // 左クリック: 衝撃吸収 → バリア Amp3
            if (attacker instanceof Player player) {
                player.addEffect(new MobEffectInstance(
                        MobEffectInit.BARRIER,
                        BARRIER_DURATION,
                        BARRIER_AMPLIFIER,
                        false,
                        true,
                        true
                ));
            }

            target.hurtTime = 0;
            target.invulnerableTime = 0;
        }

        return result;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            Vec3 eyePos = player.getEyePosition();
            Vec3 lookDir = player.getLookAngle();
            Vec3 end = eyePos.add(lookDir.scale(64.0D));

            AABB box = player.getBoundingBox().expandTowards(lookDir.scale(64.0D)).inflate(1.0D);

            EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
                    level,
                    player,
                    eyePos,
                    end,
                    box,
                    e -> e instanceof LivingEntity
                            && e.isAlive()
                            && e != player
                            && (CommonConfig.ALLOW_FRIENDLY_FIRE.get() || !(e instanceof Player) && !player.isAlliedTo(e))
            );

            if (entityHit != null && entityHit.getEntity() instanceof LivingEntity target) {
                player.getCooldowns().addCooldown(this, 40);
                fireRangedAttack(level, player, target, itemStack);
                player.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResultHolder.success(itemStack);
            }
        }

        return InteractionResultHolder.pass(itemStack);
    }

    private void fireRangedAttack(Level level, Player player, LivingEntity target, ItemStack itemStack) {
        int damage = 22 + level.random.nextInt(7); // 22-28

        int projectilesPerWeapon = 4;
        int totalProjectiles = 3 * projectilesPerWeapon;
        int projectileIndex = 0;

        for (int weaponType = 0; weaponType < 3; weaponType++) {
            for (int i = 0; i < projectilesPerWeapon; i++) {
                double angleRadians = (Math.PI * 2.0D * projectileIndex) / totalProjectiles;
                double radius = 1.45D + weaponType * 0.15D;
                double spawnX = target.getX() + Math.cos(angleRadians) * radius;
                double spawnZ = target.getZ() + Math.sin(angleRadians) * radius;
                double spawnY = findSurfaceY(level, spawnX, target.getY() + target.getBbHeight() + 1.0D, spawnZ);
                Vec3 spawnPos = new Vec3(spawnX, spawnY, spawnZ);
                float yawDegrees = (float) (angleRadians * Mth.RAD_TO_DEG) + 90.0F;

                PWhiteNight projectile = new PWhiteNight(
                        EntityInit.WHITENIGHT_PROJECTILE.get(),
                        level,
                        player,
                        spawnPos,
                        (float) damage,
                        weaponType,
                        yawDegrees,
                        PWhiteNight.DEFAULT_RENDER_SCALE
                );

                level.addFreshEntity(projectile);
                projectileIndex++;
            }
        }

        target.addEffect(new MobEffectInstance(MobEffectInit.BIND, 100, 5, false, false));
        target.hurt(level.damageSources().playerAttack(player), (float) damage);

        // 右クリック命中: 与えたダメージ分だけ自己回復
        // setHealth を使い、食料/ポーション封鎖用の LivingHealEvent を避ける
        float newHealth = Math.min(player.getMaxHealth(), player.getHealth() + damage);
        player.setHealth(newHealth);

        level.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundInit.EGO_WHITENIGHT_ATK_1.get(), SoundSource.PLAYERS, 1.0F, 1.2F);

        itemStack.hurtAndBreak(1, player, (InteractionHand.MAIN_HAND == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND));
    }

    private static double findSurfaceY(Level level, double x, double startY, double z) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(Mth.floor(x), Mth.floor(startY), Mth.floor(z));
        int minY = level.getMinBuildHeight();

        while (pos.getY() > minY && level.getBlockState(pos).isAir()) {
            pos.move(Direction.DOWN);
        }

        if (level.getBlockState(pos).isCollisionShapeFullBlock(level, pos)) {
            return pos.getY() + 1.0D;
        }

        return startY;
    }

    /** インベントリ（メイン・オフ・アーマー以外のアイテム欄）にこの武器があるか */
    public static boolean hasInInventory(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof LobEGOWeapon5WhiteNight) {
                return true;
            }
        }
        return player.getOffhandItem().getItem() instanceof LobEGOWeapon5WhiteNight;
    }

    private static class CustomTier implements Tier {
        @Override public int getUses() { return 4000; }
        @Override public float getSpeed() { return 4.0f; }
        @Override public float getAttackDamageBonus() { return 10.0f; }
        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_WOODEN_TOOL;
        }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public Ingredient getRepairIngredient() {
            return Ingredient.of(ItemInit.LCORP_EBOX.get());
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext level, List<Component> tooltip, TooltipFlag flag) {
        TooltipLines.addShiftExpanded(tooltip, TooltipLines.WHITENIGHT_WEAPON);
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
