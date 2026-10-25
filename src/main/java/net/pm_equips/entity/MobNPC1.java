package net.pm_equips.entity;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.pm_equips.EntityInit;

/** Server-authoritative companion. Rendering and shared services live in MobNPC2/3. */
public class MobNPC1 extends TamableAnimal implements RangedAttackMob {
    public static final int CHILDHOOD_TICKS = 7 * 24000;
    public static final int MAX_LEVEL = 100;
    public static final int MAX_RANK = 5;
    public static final double MOVEMENT_SPEED = 0.6;
    public static final int FOLLOW = 0, WAIT = 1, FREE = 2;
    private static final EntityDataAccessor<Integer> NPC_LEVEL = SynchedEntityData.defineId(MobNPC1.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ORDER = SynchedEntityData.defineId(MobNPC1.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> SKIN = SynchedEntityData.defineId(MobNPC1.class, EntityDataSerializers.STRING);
    private int kills;
    private long adultAt, awakeUntil;
    private BlockPos restDestination;
    private int restSearchAt, nextMeleeAttackAt, nextRangedAttackAt;
    private InteractionHand nextMeleeHand = InteractionHand.MAIN_HAND;
    @Nullable private InteractionHand attackingHand;
    private int safetyCheckAt;
    private boolean safeRestArea;
    @Nullable FakePlayer weaponUser;
    @Nullable FakePlayer offhandWeaponUser;

    public MobNPC1(EntityType<? extends MobNPC1> type, Level level) {
        super(type, level);
        setPersistenceRequired();
        setCanPickUpLoot(false);
        if (getNavigation() instanceof GroundPathNavigation ground) ground.setCanOpenDoors(true);
        for (EquipmentSlot slot : EquipmentSlot.values()) setDropChance(slot, 1.0F);
    }

    public static AttributeSupplier.Builder attributes() {
        return createMobAttributes().add(Attributes.MAX_HEALTH, 20).add(Attributes.ARMOR, 0)
                .add(Attributes.ATTACK_DAMAGE, 2).add(Attributes.ATTACK_SPEED, 4)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED).add(Attributes.FOLLOW_RANGE, 15);
    }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(NPC_LEVEL, 1); builder.define(ORDER, FREE); builder.define(SKIN, "");
    }

    @Override protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new NPCActivityGoal());
        goalSelector.addGoal(2, new OpenDoorGoal(this, true));
        goalSelector.addGoal(3, new BreedGoal(this, 0.8));
        goalSelector.addGoal(4, new FollowOwnerGoal(this, 1, 8, 3));
        goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.7) {
            @Override public boolean canUse() { return (!isTame() || getOrder() == FREE) && !isResting() && super.canUse(); }
        });
        goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8));
        goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        targetSelector.addGoal(3, new HurtByTargetGoal(this));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                target -> target instanceof Enemy || MobNPC3.isUnprovokedEnemy(this, target)));
    }

    public int getNPCLevel() { return entityData.get(NPC_LEVEL); }
    public int getRank() { return Math.min(MAX_RANK, 1 + (getNPCLevel() - 1) / 15); }
    public boolean isEXRank() { return getNPCLevel() > MAX_RANK * 15; }
    public String getRankDisplay() { return isEXRank() ? "EX" : Integer.toString(getRank()); }
    public int getKillsTowardLevel() { return kills; }
    public int getOrder() { return entityData.get(ORDER); }
    public String getSkinName() { return entityData.get(SKIN); }
    public void setSkinName(String name) { entityData.set(SKIN, name); }
    public boolean isResting() { return hasPose(Pose.SLEEPING); }

    public void setOrder(int order) {
        entityData.set(ORDER, Math.floorMod(order, 3));
        // Vanilla FollowOwnerGoal obeys this flag; our WAIT goal keeps the player model upright.
        setOrderedToSit(order != FOLLOW);
        getNavigation().stop();
    }

    public void cycleOrder(Player player) {
        if (!isTame() || !isOwnedBy(player)) return;
        wakeUp();
        setOrder((getOrder() + 1) % 3);
        player.displayClientMessage(Component.translatable("message.pm_equips.npc.order." + getOrder()), true);
    }

    public void gainLevels(int amount) {
        if (level().isClientSide || amount <= 0) return;
        int next = (int)Math.min(MAX_LEVEL, (long)getNPCLevel() + amount);
        float oldMax = getMaxHealth();
        entityData.set(NPC_LEVEL, next);
        if (next == MAX_LEVEL) kills = 0;
        updateStats();
        heal(Math.max(0, getMaxHealth() - oldMax));
    }

    public void recordKill() {
        if (getNPCLevel() >= MAX_LEVEL) return;
        if (++kills >= getNPCLevel() * 3) { kills -= getNPCLevel() * 3; gainLevels(1); }
    }

    private void updateStats() {
        int gained = getNPCLevel() - 1;
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0 + gained * 2.0);
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(2.0 + gained);
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(MOVEMENT_SPEED);
        getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(15.0 + (isEXRank() ? MAX_RANK : getRank() - 1) * 5.0);
    }

    @Override public boolean canAttack(LivingEntity target) { return super.canAttack(target) && MobNPC3.canFight(this, target); }
    @Override public boolean isOwnedBy(LivingEntity entity) { return getOwnerUUID() != null && getOwnerUUID().equals(entity.getUUID()); }
    @Override public boolean wantsToAttack(LivingEntity target, LivingEntity owner) { return canAttack(target); }
    @Override public boolean removeWhenFarAway(double distance) { return false; }
    @Override protected boolean isImmobile() { return isResting() || super.isImmobile(); }

    @Override public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        if (slot.isArmor() && !stack.isEmpty() && !level().isClientSide) {
            for (EquipmentSlot other : EquipmentSlot.values()) if (other.isArmor() && other != slot) dropSlot(other);
        }
        super.setItemSlot(slot, stack);
    }

    @Override public boolean hurt(DamageSource source, float amount) {
        if (!level().isClientSide && MobNPC3.preventDamage(this, source)) return false;
        if (!level().isClientSide) wakeUp();
        return super.hurt(source, amount);
    }

    @Override public ItemStack getWeaponItem() {
        return attackingHand == null ? super.getWeaponItem() : getItemInHand(attackingHand);
    }

    @Override public double getAttributeValue(Holder<Attribute> attribute) {
        if (attackingHand == null || !attribute.equals(Attributes.ATTACK_DAMAGE) && !attribute.equals(Attributes.ATTACK_KNOCKBACK))
            return super.getAttributeValue(attribute);
        AttributeInstance equipped = getAttribute(attribute);
        if (equipped == null) return super.getAttributeValue(attribute);
        // Evaluate the selected weapon as a main-hand weapon without changing live attributes or equipment.
        AttributeInstance selected = new AttributeInstance(attribute, ignored -> {});
        selected.replaceFrom(equipped);
        getMainHandItem().forEachModifier(EquipmentSlot.MAINHAND, (type, modifier) -> {
            if (type.equals(attribute)) selected.removeModifier(modifier.id());
        });
        getOffhandItem().forEachModifier(EquipmentSlot.OFFHAND, (type, modifier) -> {
            if (type.equals(attribute)) selected.addOrUpdateTransientModifier(modifier);
        });
        getItemInHand(attackingHand).forEachModifier(EquipmentSlot.MAINHAND, (type, modifier) -> {
            if (type.equals(attribute)) selected.addOrUpdateTransientModifier(modifier);
        });
        return selected.getValue();
    }

    @Override public boolean doHurtTarget(Entity target) { return attackWithHand(target, InteractionHand.MAIN_HAND); }

    public boolean attackWithHand(Entity target, InteractionHand hand) {
        if (!(target instanceof LivingEntity living) || !canAttack(living)) return false;
        if (hand == InteractionHand.OFF_HAND && getOffhandItem().isEmpty()) return false;
        InteractionHand previous = attackingHand;
        attackingHand = hand;
        try {
            boolean hit = super.doHurtTarget(target);
            ItemStack weapon = getItemInHand(hand);
            if (hit && !level().isClientSide && !weapon.isEmpty()) {
                weapon.getItem().hurtEnemy(weapon, living, this);
                if (weapon.isDamageableItem()) weapon.hurtAndBreak(1, this, getSlotForHand(hand));
            }
            swing(hand);
            return hit;
        } finally {
            attackingHand = previous;
        }
    }

    public static boolean isRangedWeapon(ItemStack stack) {
        return stack.getItem() instanceof ProjectileWeaponItem || MobNPC3.isSpecialRanged(stack);
    }

    @Nullable FakePlayer getWeaponUser(InteractionHand hand) { return hand == InteractionHand.MAIN_HAND ? weaponUser : offhandWeaponUser; }
    void setWeaponUser(InteractionHand hand, @Nullable FakePlayer user) {
        if (hand == InteractionHand.MAIN_HAND) weaponUser = user; else offhandWeaponUser = user;
    }

    public static int bookLevels(ItemStack stack) {
        if (stack.is(Items.PAPER)) return 1;
        if (stack.is(Items.BOOK)) return 2;
        if (stack.is(Items.WRITABLE_BOOK)) return 3;
        if (stack.is(Items.WRITTEN_BOOK)) return 4;
        return stack.is(Items.ENCHANTED_BOOK) ? 5 : 0;
    }

    public static boolean isGold(ItemStack stack) {
        return stack.is(Items.GOLD_NUGGET) || stack.is(Items.GOLD_INGOT) || stack.is(Items.GOLD_BLOCK);
    }

    @Override public boolean isFood(ItemStack stack) {
        return stack.is(Items.BREAD) || stack.is(Items.CAKE)
                || stack.is(Items.COOKED_BEEF) || stack.is(Items.COOKED_PORKCHOP) || stack.is(Items.COOKED_CHICKEN)
                || stack.is(Items.COOKED_MUTTON) || stack.is(Items.COOKED_RABBIT);
    }

    @Override public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (isTame() && !isOwnedBy(player)) return InteractionResult.PASS;
        if (level().isClientSide) return InteractionResult.SUCCESS;
        if (held.isEmpty()) {
            if (isResting()) wakeUp(); else dropEquipment();
            return InteractionResult.CONSUME;
        }
        if (isGold(held) && !isTame()) {
            if (isBaby()) { player.displayClientMessage(Component.translatable("message.pm_equips.npc.child"), true); return InteractionResult.CONSUME; }
            tame(player); setOrder(FOLLOW); wakeUp(); takeOne(player, held);
            level().broadcastEntityEvent(this, (byte)7);
            return InteractionResult.CONSUME;
        }
        int levels = bookLevels(held);
        if (levels > 0) {
            if (getNPCLevel() >= MAX_LEVEL) {
                player.displayClientMessage(Component.translatable("message.pm_equips.npc.level_max", MAX_LEVEL), true);
                return InteractionResult.CONSUME;
            }
            gainLevels(levels); takeOne(player, held);
            player.displayClientMessage(Component.translatable("message.pm_equips.npc.level", getNPCLevel(), getRankDisplay()), true);
            return InteractionResult.CONSUME;
        }
        if (held.has(DataComponents.FOOD) || held.is(Items.CAKE)) {
            boolean heal = getHealth() < getMaxHealth();
            boolean breed = !heal && isFood(held) && getAge() == 0 && canFallInLove();
            if (heal || breed) {
                if (heal) { resetLove(); heal(8 + getNPCLevel()); }
                if (breed) setInLove(player);
                takeOne(player, held);
            }
            return InteractionResult.CONSUME;
        }
        EquipmentSlot slot = held.getItem() instanceof ArmorItem armor ? armor.getEquipmentSlot() : getEquipmentSlotForItem(held);
        if (held.getItem() instanceof ArmorItem || slot.isArmor()) {
            if (!canUseSlot(slot)) return InteractionResult.FAIL;
            for (EquipmentSlot equipped : EquipmentSlot.values()) if (equipped.isArmor()) dropSlot(equipped);
        } else if (held.getItem() instanceof SwordItem || held.getItem() instanceof DiggerItem
                || held.getItem() instanceof ProjectileWeaponItem || held.getItem() instanceof TridentItem
                || held.getItem() instanceof MaceItem || MobNPC3.isSpecialRanged(held)) {
            slot = getMainHandItem().isEmpty() ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            dropSlot(slot);
        } else return InteractionResult.PASS;
        setItemSlot(slot, held.copyWithCount(1)); setDropChance(slot, 1.0F); takeOne(player, held);
        if (slot == EquipmentSlot.MAINHAND) weaponUser = null;
        else if (slot == EquipmentSlot.OFFHAND) offhandWeaponUser = null;
        return InteractionResult.CONSUME;
    }

    @Override public boolean canMate(Animal partner) {
        return getAge() == 0 && partner.getAge() == 0 && getHealth() >= getMaxHealth()
                && partner.getHealth() >= partner.getMaxHealth() && super.canMate(partner);
    }

    @Override public void spawnChildFromBreeding(ServerLevel level, Animal partner) {
        if (canMate(partner)) super.spawnChildFromBreeding(level, partner);
    }

    private static void takeOne(Player player, ItemStack stack) { if (!player.getAbilities().instabuild) stack.shrink(1); }
    private void dropSlot(EquipmentSlot slot) {
        ItemStack stack = getItemBySlot(slot);
        if (!stack.isEmpty()) { setItemSlot(slot, ItemStack.EMPTY); spawnAtLocation(stack); }
    }
    public void dropEquipment() {
        for (EquipmentSlot slot : EquipmentSlot.values()) dropSlot(slot);
        weaponUser = null; offhandWeaponUser = null;
    }

    @Override public MobNPC1 getBreedOffspring(ServerLevel level, AgeableMob partner) {
        MobNPC1 child = EntityInit.MOB_NPC_1.get().create(level);
        if (child != null) child.setBaby(true);
        return child;
    }

    @Override public void setBaby(boolean baby) {
        super.setBaby(baby);
        if (baby) { adultAt = level().getGameTime() + CHILDHOOD_TICKS; setAge(-CHILDHOOD_TICKS); }
        else adultAt = 0;
    }

    public void wakeUp() {
        if (super.isSleeping()) super.stopSleeping();
        setPose(Pose.STANDING);
        restDestination = null;
        awakeUntil = level().getGameTime() + 600;
        getNavigation().stop();
    }

    @Override public void stopSleeping() { super.stopSleeping(); restDestination = null; }

    @Override public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        if (getHealth() < getMaxHealth() && isInLove()) resetLove();
        if (weaponUser != null) weaponUser.getCooldowns().tick();
        if (offhandWeaponUser != null) offhandWeaponUser.getCooldowns().tick();
        if (isBaby()) {
            if (adultAt == 0) adultAt = level().getGameTime() + CHILDHOOD_TICKS;
            long remaining = adultAt - level().getGameTime();
            setAge(remaining <= 0 ? 0 : -(int)Math.min(CHILDHOOD_TICKS, remaining));
        }
        if (tickCount % 200 == getId() % 200 || getSkinName().isEmpty()) MobNPC3.ensureSkin(this);
        if (getTarget() != null && (!getTarget().isAlive() || !canAttack(getTarget()))) setTarget(null);
        if (isResting() && (!level().isNight() || isInWaterOrBubble() || getTarget() != null || (tickCount % 20 == 0 && !safeToRest()))) wakeUp();
        if (tickCount >= restSearchAt && !isResting() && wantsToRest()) {
            restSearchAt = tickCount + 200 + getRandom().nextInt(40);
            restDestination = MobNPC3.findRestPlace(this);
        }
    }

    private boolean safeToRest() {
        if (tickCount < safetyCheckAt) return safeRestArea;
        safetyCheckAt = tickCount + 20;
        safeRestArea = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(15),
                target -> target instanceof Enemy && target.isAlive()).isEmpty();
        return safeRestArea;
    }
    private boolean wantsToRest() {
        return level().isNight() && level().getGameTime() >= awakeUntil && getTarget() == null
                && !isInWaterOrBubble() && safeToRest();
    }

    @Override public void performRangedAttack(LivingEntity target, float velocity) {
        for (InteractionHand hand : InteractionHand.values()) if (isRangedWeapon(getItemInHand(hand))) MobNPC3.shoot(this, target, hand);
    }

    void useWeapons(LivingEntity target, double distance) {
        if (distance <= 2.2 * 2.2 && tickCount >= nextMeleeAttackAt) {
            boolean main = !getMainHandItem().isEmpty() && (!isRangedWeapon(getMainHandItem()) || distance <= 2.25);
            boolean off = !getOffhandItem().isEmpty() && (!isRangedWeapon(getOffhandItem()) || distance <= 2.25);
            if (getMainHandItem().isEmpty() && getOffhandItem().isEmpty()) main = true;
            if (main || off) {
                InteractionHand hand = main && off ? nextMeleeHand : main ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
                attackWithHand(target, hand);
                nextMeleeHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
                nextMeleeAttackAt = tickCount + (main && off ? 10 : 20);
            }
        }
        if (distance > 2.25 && tickCount >= nextRangedAttackAt) {
            performRangedAttack(target, 1);
            nextRangedAttackAt = tickCount + 4;
        }
    }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("NPCLevel", getNPCLevel()); tag.putInt("NPCKills", kills); tag.putInt("NPCOrder", getOrder());
        tag.putString("NPCSkin", getSkinName()); tag.putLong("NPCAdultAt", adultAt); tag.putLong("NPCAwakeUntil", awakeUntil);
    }
    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(NPC_LEVEL, Mth.clamp(tag.getInt("NPCLevel"), 1, MAX_LEVEL));
        kills = getNPCLevel() == MAX_LEVEL ? 0 : Mth.clamp(tag.getInt("NPCKills"), 0, getNPCLevel() * 3 - 1);
        setOrder(tag.contains("NPCOrder") ? tag.getInt("NPCOrder") : FREE);
        setInSittingPose(false);
        setSkinName(tag.getString("NPCSkin")); adultAt = tag.getLong("NPCAdultAt"); awakeUntil = tag.getLong("NPCAwakeUntil");
        updateStats();
        setHealth(Math.min(getHealth(), getMaxHealth()));
    }

    private final class NPCActivityGoal extends Goal {
        private NPCActivityGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP)); }
        @Override public boolean canUse() {
            return isResting() || getTarget() != null || (isTame() && getOrder() == WAIT)
                    || (restDestination != null && wantsToRest());
        }
        @Override public boolean requiresUpdateEveryTick() { return true; }
        @Override public void stop() { setAggressive(false); getNavigation().stop(); }
        @Override public void tick() {
            LivingEntity target = getTarget();
            if (target != null && canAttack(target)) {
                if (isResting()) wakeUp();
                restDestination = null; setAggressive(true);
                getLookControl().setLookAt(target, 30, 30);
                double distance = distanceToSqr(target);
                boolean ranged = isRangedWeapon(getMainHandItem()) || isRangedWeapon(getOffhandItem());
                double range = ranged ? Math.min(32, getAttributeValue(Attributes.FOLLOW_RANGE)) : 2.2;
                if (distance > range * range || !getSensing().hasLineOfSight(target)) getNavigation().moveTo(target, 1);
                else {
                    getNavigation().stop();
                    useWeapons(target, distance);
                }
                return;
            }
            setAggressive(false);
            if (isResting()) { getNavigation().stop(); setDeltaMovement(Vec3.ZERO); return; }
            if (restDestination != null && wantsToRest()) {
                if (distanceToSqr(Vec3.atCenterOf(restDestination)) > 3) {
                    if (tickCount % 20 == 0 && !getNavigation().moveTo(restDestination.getX() + .5, restDestination.getY(), restDestination.getZ() + .5, .8))
                        restDestination = blockPosition();
                } else {
                    getNavigation().stop();
                    var state = level().getBlockState(restDestination);
                    if (state.getBlock() instanceof BedBlock && state.getValue(BedBlock.PART) == BedPart.HEAD && !state.getValue(BedBlock.OCCUPIED))
                        startSleeping(restDestination);
                    else { setPose(Pose.SLEEPING); setDeltaMovement(Vec3.ZERO); }
                }
            } else { restDestination = null; getNavigation().stop(); }
        }
    }
}
