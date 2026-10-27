package net.pm_equips.items.materials;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pm_equips.ItemInit;
import net.pm_equips.PMEquipsMain;

public enum ArmorEquips {
    FINGER1_ARMOR1("finger1_armor1", 1000, new int[]{0, 12, 0, 0}, 0, SoundEvents.ARMOR_EQUIP_GENERIC, 12, 0, () -> Ingredient.of(Items.IRON_INGOT)),
    FINGER1_ARMOR2("finger1_armor2", 2000, new int[]{0, 15, 0, 0}, 0, SoundEvents.ARMOR_EQUIP_GENERIC, 15, 0, () -> Ingredient.of(Items.IRON_INGOT)),
    FINGER1_ARMOR3("finger1_armor3", 3000, new int[]{0, 20, 0, 0}, 0, SoundEvents.ARMOR_EQUIP_GENERIC, 20, 0, () -> Ingredient.of(Items.IRON_INGOT)),
    FINGER2_ARMOR1("finger2_armor1", 1000, new int[]{0, 15, 0, 0}, 0, SoundEvents.ARMOR_EQUIP_GENERIC, 15, 0, () -> Ingredient.of(Items.IRON_INGOT)),
    FINGER2_ARMOR2("finger2_armor2", 2000, new int[]{0, 18, 0, 0}, 0, SoundEvents.ARMOR_EQUIP_GENERIC, 18, 0, () -> Ingredient.of(Items.IRON_INGOT)),
    FINGER2_ARMOR3("finger2_armor3", 3000, new int[]{0, 20, 0, 0}, 0, SoundEvents.ARMOR_EQUIP_GENERIC, 20, 0, () -> Ingredient.of(Items.IRON_INGOT)),
    HEAD_ARBITER("head_arbiter", 0, new int[]{0, 50, 0, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 50.0f, 1.0f, () -> Ingredient.EMPTY),

    A5_TWILIGHT("s5_twilight", 4000, new int[]{0, 0, 40, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 40.0f, 0.9f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A5_PARADISE_LOST("s5_white_night", 4000, new int[]{0, 0, 40, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 40.0f, 0.9f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A5_MIMICRY("s5_mimicry", 4000, new int[]{0, 0, 30, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 30.0f, 0.75f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A5_SMILE("s5_smile", 4000, new int[]{0, 0, 30, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 30.0f, 0.75f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A5_SOUND_OF_A_STAR("s5_star", 4000, new int[]{0, 0, 30, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 30.0f, 0.75f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A5_DA_CAPO("s5_da_capo", 4000, new int[]{0, 0, 30, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 30.0f, 0.75f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A5_PINK("s5_pink", 4000, new int[]{0, 0, 30, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 30.0f, 0.75f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A5_JUSTITIA("s5_justitia", 4000, new int[]{0, 0, 30, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 30.0f, 0.75f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A4_HATRED("s4_hatred", 3000, new int[]{0, 0, 25, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 25.0f, 0.6f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A4_CRIMSON_SCAR("s4_crimson_scar", 3000, new int[]{0, 0, 25, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 25.0f, 0.6f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A4_THE_SWORD_SHARPENED_WITH_TEARS("s4_tears", 3000, new int[]{0, 0, 25, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 25.0f, 0.6f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A4_HORNET("s4_hornet", 3000, new int[]{0, 0, 25, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 25.0f, 0.6f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A4_HEAVEN("s4_heaven", 3000, new int[]{0, 0, 25, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 25.0f, 0.6f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A4_AROMA("s4_aroma", 3000, new int[]{0, 0, 25, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 25.0f, 0.6f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A4_SWAN("s4_swan", 3000, new int[]{0, 0, 25, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 25.0f, 0.6f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A4_STEM("s4_stem", 3000, new int[]{0, 0, 25, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 25.0f, 0.6f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A4_LAMP("s4_lamp", 3000, new int[]{0, 0, 25, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 25.0f, 0.6f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A4_BLUE_SCAR("s4_blue_scar", 3000, new int[]{0, 0, 25, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 25.0f, 0.6f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A3_MAGIC_BULLET("s3_magic_bullet", 2000, new int[]{0, 0, 20, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 20.0f, 0.45f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A3_ICE_SHARD("s3_ice_shard", 2000, new int[]{0, 0, 20, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 20.0f, 0.45f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A3_LAETITIA("s3_laetitia", 2000, new int[]{0, 0, 20, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 20.0f, 0.45f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A3_BLOOD("s3_blood", 2000, new int[]{0, 0, 20, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 20.0f, 0.45f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A3_BEAR("s3_bear", 2000, new int[]{0, 0, 20, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 20.0f, 0.45f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A3_SOLEMN_LAMENT("s3_solemn_lament", 2000, new int[]{0, 0, 20, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 20.0f, 0.45f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A3_LOGGING("s3_logging", 2000, new int[]{0, 0, 20, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 20.0f, 0.45f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A3_GRINDER_MK4("s3_grinder_mk4", 2000, new int[]{0, 0, 20, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 20.0f, 0.45f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A3_GALAXY("s3_galaxy", 2000, new int[]{0, 0, 20, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 20.0f, 0.45f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A3_HARVEST("s3_harvest", 2000, new int[]{0, 0, 20, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 20.0f, 0.45f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A2_FOURTH_MATCH_FIRE("s2_match", 1000, new int[]{0, 0, 15, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 15.0f, 0.3f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A2_BEAK("s2_beak", 1000, new int[]{0, 0, 15, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 15.0f, 0.3f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A2_BATH("s2_bath", 1000, new int[]{0, 0, 15, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 15.0f, 0.3f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A2_SOMEWHERE("s2_somewhere", 1000, new int[]{0, 0, 15, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 15.0f, 0.3f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A2_SOLITUDE("s2_solitude", 1000, new int[]{0, 0, 15, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 15.0f, 0.3f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A2_TODAY("s2_today", 1000, new int[]{0, 0, 15, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 15.0f, 0.3f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A2_LANTERN("s2_lantern", 1000, new int[]{0, 0, 15, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 15.0f, 0.3f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A2_RED_EYE("s2_red_eye", 1000, new int[]{0, 0, 15, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 15.0f, 0.3f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A2_REGLET("s2_reglet", 1000, new int[]{0, 0, 15, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 15.0f, 0.3f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A1_PENITENCE("s1_penitence", 800, new int[]{0, 0, 10, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 10.0f, 0.15f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A1_SODA("s1_soda", 800, new int[]{0, 0, 10, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 10.0f, 0.15f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    A1_WINGBEAT("s1_wingbeat", 800, new int[]{0, 0, 10, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 10.0f, 0.15f, () -> Ingredient.of(ItemInit.LCORP_EBOX.get())),

    ASC2_ARMOR_SEC6("asc2_armor_sec6", 100, new int[]{0, 0, 4, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 1.0f, 0.0f, () -> Ingredient.of(Items.LEATHER)),

    ASC2_ARMOR_SEC5("asc2_armor_sec5", 200, new int[]{0, 0, 6, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 1.2f, 0.0f, () -> Ingredient.of(Items.LEATHER)),

    ASC2_ARMOR_SEC4("asc2_armor_sec4", 300, new int[]{0, 0, 8, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 1.5f, 0.0f, () -> Ingredient.of(Items.LEATHER)),

    ASC2_ARMOR_SEC3("asc2_armor_sec3", 400, new int[]{0, 0, 10, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 1.8f, 0.0f, () -> Ingredient.of(Items.LEATHER)),

    ASC2_ARMOR_SEC2("asc2_armor_sec2", 500, new int[]{0, 0, 12, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 2.0f, 0.0f, () -> Ingredient.of(Items.LEATHER)),

    ASC2_ARMOR_SEC1("asc2_armor_sec1", 600, new int[]{0, 0, 15, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 2.5f, 0.0f, () -> Ingredient.of(Items.LEATHER)),

    WCORP_ARMOR_L1("wcorp_armor_class1", 300, new int[]{0, 0, 12, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 4.0f, 0.1f, () -> Ingredient.of(ItemInit.WCORP_BATTERY.get())),

    WCORP_ARMOR_L2("wcorp_armor_class2", 500, new int[]{0, 0, 14, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 8.0f, 0.2f, () -> Ingredient.of(ItemInit.WCORP_BATTERY.get())),

    WCORP_ARMOR_L3("wcorp_armor_class3", 700, new int[]{0, 0, 16, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 10.0f, 0.3f, () -> Ingredient.of(ItemInit.WCORP_BATTERY.get())),

    WCORP_ARMOR_L4("wcorp_armor_class4", 850, new int[]{0, 0, 18, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 12.0f, 0.4f, () -> Ingredient.of(ItemInit.WCORP_BATTERY.get())),

    WCORP_ARMOR_L5("wcorp_armor_class5", 1000, new int[]{0, 0, 20, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 15.0f, 0.5f, () -> Ingredient.of(ItemInit.WCORP_BATTERY.get())),

    KCORP_ARMOR_L1("kcorp_armor_class1", 300, new int[]{0, 0, 12, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 4.0f, 0.1f, () -> Ingredient.of(ItemInit.KCORP_AMPOULE.get())),

    KCORP_ARMOR_L2("kcorp_armor_class2", 500, new int[]{0, 0, 14, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 8.0f, 0.2f, () -> Ingredient.of(ItemInit.KCORP_AMPOULE.get())),

    KCORP_ARMOR_L3("kcorp_armor_class3", 700, new int[]{0, 0, 16, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 10.0f, 0.3f, () -> Ingredient.of(ItemInit.KCORP_AMPOULE.get())),

    KCORP_ARMOR_L4("kcorp_armor_class4", 850, new int[]{0, 0, 18, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 12.0f, 0.4f, () -> Ingredient.of(ItemInit.KCORP_AMPOULE.get())),

    KCORP_ARMOR_L5("kcorp_armor_class5", 1000, new int[]{0, 0, 20, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 15.0f, 0.5f, () -> Ingredient.of(ItemInit.KCORP_AMPOULE.get())),

    RCORP_4TH_RABBIT_ARMOR_L1("rcorp_4th_rabbit_armor_class1", 300, new int[]{0, 0, 12, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 4.0f, 0.1f, () -> Ingredient.of(ItemInit.RCORP_BATTERY.get())),

    RCORP_4TH_RABBIT_ARMOR_L2("rcorp_4th_rabbit_armor_class2", 500, new int[]{0, 0, 14, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 8.0f, 0.2f, () -> Ingredient.of(ItemInit.RCORP_BATTERY.get())),

    RCORP_4TH_RABBIT_ARMOR_L3("rcorp_4th_rabbit_armor_class3", 700, new int[]{0, 0, 16, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 10.0f, 0.3f, () -> Ingredient.of(ItemInit.RCORP_BATTERY.get())),

    RCORP_4TH_RABBIT_ARMOR_L4("rcorp_4th_rabbit_armor_class4", 850, new int[]{0, 0, 18, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 12.0f, 0.4f, () -> Ingredient.of(ItemInit.RCORP_BATTERY.get())),

    RCORP_4TH_RABBIT_ARMOR_L5("rcorp_4th_rabbit_armor_class5", 1000, new int[]{0, 0, 20, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 15.0f, 0.5f, () -> Ingredient.of(ItemInit.RCORP_BATTERY.get())),

    HCORP_MAO_ARMOR_L1("hcorp_mao_armor_class1", 300, new int[]{0, 0, 12, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 4.0f, 0.1f, () -> Ingredient.of(ItemInit.HCORP_BOLUS_MAO.get())),

    HCORP_MAO_ARMOR_L2("hcorp_mao_armor_class2", 500, new int[]{0, 0, 14, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 8.0f, 0.2f, () -> Ingredient.of(ItemInit.HCORP_BOLUS_MAO.get())),

    HCORP_MAO_ARMOR_L3("hcorp_mao_armor_class3", 700, new int[]{0, 0, 16, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 10.0f, 0.3f, () -> Ingredient.of(ItemInit.HCORP_BOLUS_MAO.get())),

    HCORP_MAO_ARMOR_L4("hcorp_mao_armor_class4", 850, new int[]{0, 0, 18, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 12.0f, 0.4f, () -> Ingredient.of(ItemInit.HCORP_BOLUS_MAO.get())),

    HCORP_MAO_ARMOR_L5("hcorp_mao_armor_class5", 1000, new int[]{0, 0, 20, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 15.0f, 0.5f, () -> Ingredient.of(ItemInit.HCORP_BOLUS_MAO.get())),

    HCORP_SI_ARMOR_L1("hcorp_si_armor_class1", 300, new int[]{0, 0, 12, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 4.0f, 0.1f, () -> Ingredient.of(ItemInit.HCORP_BOLUS_SI.get())),

    HCORP_SI_ARMOR_L2("hcorp_si_armor_class2", 500, new int[]{0, 0, 14, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 8.0f, 0.2f, () -> Ingredient.of(ItemInit.HCORP_BOLUS_SI.get())),

    HCORP_SI_ARMOR_L3("hcorp_si_armor_class3", 700, new int[]{0, 0, 16, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 10.0f, 0.3f, () -> Ingredient.of(ItemInit.HCORP_BOLUS_SI.get())),

    HCORP_SI_ARMOR_L4("hcorp_si_armor_class4", 850, new int[]{0, 0, 18, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 12.0f, 0.4f, () -> Ingredient.of(ItemInit.HCORP_BOLUS_SI.get())),

    HCORP_SI_ARMOR_L5("hcorp_si_armor_class5", 1000, new int[]{0, 0, 20, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 15.0f, 0.5f, () -> Ingredient.of(ItemInit.HCORP_BOLUS_SI.get())),

    HCORP_YOU_ARMOR_L1("hcorp_you_armor_class1", 300, new int[]{0, 0, 12, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 4.0f, 0.1f, () -> Ingredient.of(ItemInit.HCORP_BOLUS_YOU.get())),

    HCORP_YOU_ARMOR_L2("hcorp_you_armor_class2", 500, new int[]{0, 0, 14, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 8.0f, 0.2f, () -> Ingredient.of(ItemInit.HCORP_BOLUS_YOU.get())),

    HCORP_YOU_ARMOR_L3("hcorp_you_armor_class3", 700, new int[]{0, 0, 16, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 10.0f, 0.3f, () -> Ingredient.of(ItemInit.HCORP_BOLUS_YOU.get())),

    HCORP_YOU_ARMOR_L4("hcorp_you_armor_class4", 850, new int[]{0, 0, 18, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 12.0f, 0.4f, () -> Ingredient.of(ItemInit.HCORP_BOLUS_YOU.get())),

    HCORP_YOU_ARMOR_L5("hcorp_you_armor_class5", 1000, new int[]{0, 0, 20, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 15.0f, 0.5f, () -> Ingredient.of(ItemInit.HCORP_BOLUS_YOU.get())),

    LCA_UDJAT("lca_udjat", 4000, new int[]{0, 0, 30, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 15.0f, 0.9f, () -> Ingredient.of(Items.GOLD_INGOT)),

    LCC_BEFORE("lcc_before", 1000, new int[]{0, 0, 15, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 15.0f, 0.35f, () -> Ingredient.of(Items.IRON_INGOT)),

    LCD_AENG_DU("lcd_aeng_du", 3000, new int[]{0, 0, 20, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 15.0f, 0.5f, () -> Ingredient.of(Items.LEATHER)),

    LCE_EGO_DIMENSION_SHREDDER("lce_ego_dimension_shredder", 2000, new int[]{0, 0, 18, 0}, 0,
            SoundEvents.ARMOR_EQUIP_GENERIC, 15.0f, 0.75f, () -> Ingredient.of(Items.REDSTONE));

    public static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, PMEquipsMain.MOD_ID);
    static {
        for (ArmorEquips material : values()) {
            material.holder = MATERIALS.register(material.name, material::createMaterial);
        }
    }

    private final String name;
    private final int durabilityMultiplier;
    private final int[] protectionAmounts;
    private final int enchantmentValue;
    private final Holder<SoundEvent> equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredient;
    private DeferredHolder<ArmorMaterial, ArmorMaterial> holder;

    ArmorEquips(String name, int durability, int[] protection, int enchantmentValue,
                Holder<SoundEvent> sound, float toughness, float knockbackResistance,
                Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durability;
        this.protectionAmounts = protection;
        this.enchantmentValue = enchantmentValue;
        this.equipSound = sound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
    }

    private ArmorMaterial createMaterial() {
        return new ArmorMaterial(Map.of(
                ArmorItem.Type.BOOTS, protectionAmounts[0],
                ArmorItem.Type.LEGGINGS, protectionAmounts[1] + protectionAmounts[2],
                ArmorItem.Type.CHESTPLATE, protectionAmounts[2],
                ArmorItem.Type.HELMET, protectionAmounts[3],
                ArmorItem.Type.BODY, 0), enchantmentValue, equipSound, repairIngredient,
                List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(PMEquipsMain.MOD_ID, name))),
                toughness, knockbackResistance);
    }

    public Holder<ArmorMaterial> asHolder() { return holder; }
    public int getDurabilityForType(ArmorItem.Type type) { return durabilityMultiplier; }
    public int getDefenseForType(ArmorItem.Type type) {
        return switch (type) {
            case BOOTS -> protectionAmounts[0];
            case LEGGINGS -> protectionAmounts[1] + protectionAmounts[2];
            case CHESTPLATE -> protectionAmounts[2];
            case HELMET -> protectionAmounts[3];
            default -> 0;
        };
    }
    public int getEnchantmentValue() { return enchantmentValue; }
    public SoundEvent getEquipSound() { return equipSound.value(); }
    public Ingredient getRepairIngredient() { return repairIngredient.get(); }
    public String getName() { return PMEquipsMain.MOD_ID + ":" + name; }
    public float getToughness() { return toughness; }
    public float getKnockbackResistance() { return knockbackResistance; }
}
