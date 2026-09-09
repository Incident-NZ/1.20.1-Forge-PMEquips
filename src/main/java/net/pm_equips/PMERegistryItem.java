package net.pm_equips;

import net.pm_equips.items.*;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;

public class PMERegistryItem {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, PMEquipsMain.MOD_ID);

    //アイテム
    public static final DeferredHolder<Item, Item> ITEM_ENKEPHALIN = ITEMS.register("item_enkephalin",
            () -> new ItemEnkephalin(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> SILVER_INGOT = ITEMS.register("item_silver",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> METAL_INGOT = ITEMS.register("item_metal",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> PLATINUM_INGOT = ITEMS.register("item_platinum",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> BLADE_GEAR = ITEMS.register("item_blade_gear",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> WCORP_BATTERY = ITEMS.register("item_wcorp_battery",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> RCORP_BATTERY = ITEMS.register("item_rcorp_battery",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> KCORP_AMPOULE = ITEMS.register("item_kcorp_ampoule",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> HCORP_BOLUS_MAO = ITEMS.register("item_hcorp_bolus_mao",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> HCORP_BOLUS_SI = ITEMS.register("item_hcorp_bolus_si",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> HCORP_BOLUS_YOU = ITEMS.register("item_hcorp_bolus_you",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> MAGIC_BULLET_AMMO = ITEMS.register("item_bullet_ego",
            () -> new AmmoMagicBullet(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> RIFLE_BULLET_AMMO = ITEMS.register("item_bullet_r",
            () -> new AmmoRifle(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> PISTOL_BULLET_AMMO = ITEMS.register("item_bullet_p",
            () -> new AmmoPistol(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> EXPLOSIVE_BULLET_AMMO = ITEMS.register("item_bullet_ex",
            () -> new AmmoExplosive(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> P_BULLET_LARV = ITEMS.register("item_bullet_larv",
            () -> new AmmoLARV(new Item.Properties().stacksTo(64)));
    public static final DeferredHolder<Item, Item> P_BULLET_LASG = ITEMS.register("item_bullet_lasg",
            () -> new AmmoLASG(new Item.Properties().stacksTo(64)));

    //L社EGO武器 近距離
    public static final DeferredHolder<Item, Item> W5_TWILIGHT = ITEMS.register("w5_twilight", LCorpEGOR5Twilight::new);
    public static final DeferredHolder<Item, Item> W5_PARADISE_LOST = ITEMS.register("w5_whitenight", LCorpEGOR5WhiteNight::new);
    public static final DeferredHolder<Item, Item> W5_MIMICRY = ITEMS.register("w5_mimicry", LCorpEGOR5Mimicry::new);
    public static final DeferredHolder<Item, Item> W5_DA_CAPO = ITEMS.register("w5_da_capo", LCorpEGOR5DaCapo::new);
    public static final DeferredHolder<Item, Item> W5_SMILE = ITEMS.register("w5_smile", LCorpEGOR5Smile::new);
    public static final DeferredHolder<Item, Item> W5_SOUND_OF_A_STAR = ITEMS.register("w5_star", LCorpEGOR5Star::new);
    public static final DeferredHolder<Item, Item> W5_JUSTITIA = ITEMS.register("w5_justitia", LCorpEGOR5Justitia::new);
    public static final DeferredHolder<Item, Item> W4_THE_SWORD_SHARPENED_WITH_TEARS = ITEMS.register("w4_tears", LCorpEGOR4Tears::new);
    public static final DeferredHolder<Item, Item> W4_LAMP = ITEMS.register("w4_lamp", LCorpEGOR4Lamp::new);
    public static final DeferredHolder<Item, Item> W4_HATRED = ITEMS.register("w4_hatred", LCorpEGOR4Hatred::new);
    public static final DeferredHolder<Item, Item> W4_HEAVEN = ITEMS.register("w4_heaven", LCorpEGOR4Heaven::new);
    public static final DeferredHolder<Item, Item> W4_SWAN = ITEMS.register("w4_swan", LCorpEGOR4Swan::new);
    public static final DeferredHolder<Item, Item> W4_STEM = ITEMS.register("w4_stem", LCorpEGOR4Stem::new);
    public static final DeferredHolder<Item, Item> W4_DIFFRACTION = ITEMS.register("w4_diffraction", LCorpEGOR4Diffraction::new);
    public static final DeferredHolder<Item, Item> W4_BLUE_SCAR = ITEMS.register("w4_blue_scar", LCorpEGOR4BlueScar::new);
    public static final DeferredHolder<Item, Item> W4_CRIMSON_SCAR_R = ITEMS.register("w4_crimson_scar_r", LCorpEGOR4CrimsonScarR::new);
    public static final DeferredHolder<Item, Item> W3_BLOOD = ITEMS.register("w3_blood", LCorpEGOR3Blood::new);
    public static final DeferredHolder<Item, Item> W3_BEAR = ITEMS.register("w3_bear", LCorpEGOR3Bear::new);
    public static final DeferredHolder<Item, Item> W3_HARVEST = ITEMS.register("w3_harvest", LCorpEGOR3Harvest::new);
    public static final DeferredHolder<Item, Item> W3_LOGGING = ITEMS.register("w3_logging", LCorpEGOR3Logging::new);
    public static final DeferredHolder<Item, Item> W3_GALAXY = ITEMS.register("w3_galaxy", LCorpEGOR3Galaxy::new);
    public static final DeferredHolder<Item, Item> W3_GRINDER_MK4 = ITEMS.register("w3_mk4", LCorpEGOR3MK4::new);
    public static final DeferredHolder<Item, Item> W3_ICE_SHARD = ITEMS.register("w3_ice_shard", LCorpEGOR3IceShard::new);
    public static final DeferredHolder<Item, Item> W2_REGLET = ITEMS.register("w2_reglet", LCorpEGOR2Reglet::new);
    public static final DeferredHolder<Item, Item> W2_SOMEWHERE = ITEMS.register("w2_somewhere", LCorpEGOR2SomeWhere::new);
    public static final DeferredHolder<Item, Item> W2_BATH = ITEMS.register("w2_bath", LCorpEGOR2Bath::new);
    public static final DeferredHolder<Item, Item> W2_RED_EYE = ITEMS.register("w2_red_eye", LCorpEGOR2RedEye::new);
    public static final DeferredHolder<Item, Item> W2_LANTERN = ITEMS.register("w2_lantern", LCorpEGOR2Lantern::new);
    public static final DeferredHolder<Item, Item> W1_PENITENCE = ITEMS.register("w1_penitence", LCorpEGOR1Penitence::new);
    public static final DeferredHolder<Item, Item> W1_WINGBEAT = ITEMS.register("w1_wingbeat", LCorpEGOR1WingBeat::new);

    //L社EGO武器 遠距離
    public static final DeferredHolder<Item, Item> W5_PINK = ITEMS.register("w5_pink",
            () -> new LCorpEGOR5Pink(new Item.Properties().durability(4000)));
    public static final DeferredHolder<Item, Item> W4_AROMA = ITEMS.register("w4_aroma",
            () -> new LCorpEGOR4Aroma(new Item.Properties().durability(3000)));
    public static final DeferredHolder<Item, Item> W4_SOLEMN_LAMENT_L = ITEMS.register("w4_lament_l", LCorpEGOR4LamentL::new);
    public static final DeferredHolder<Item, Item> W4_SOLEMN_LAMENT_R = ITEMS.register("w4_lament_r",
            () -> new LCorpEGOR4LamentR(new Item.Properties().durability(3000)));
    public static final DeferredHolder<Item, Item> W4_MAGIC_BULLET = ITEMS.register("w4_magic_bullet",
            () -> new LCorpEGOR4MagicBullet(new Item.Properties().durability(3000)));
    public static final DeferredHolder<Item, Item> W4_HORNET = ITEMS.register("w4_hornet",
            () -> new LCorpEGOR4Hornet(new Item.Properties().durability(3000)));
    public static final DeferredHolder<Item, Item> W4_CRIMSON_SCAR_L = ITEMS.register("w4_crimson_scar_l",
            () -> new LCorpEGOR4CrimsonScarL(new Item.Properties().durability(3000)));
    public static final DeferredHolder<Item, Item> W3_HARMONY = ITEMS.register("w3_harmony",
            () -> new LCorpEGOR3Harmony(new Item.Properties().durability(2000)));
    public static final DeferredHolder<Item, Item> W3_LAETITIA = ITEMS.register("w3_laetitia",
            () -> new LCorpEGOR3Laetitia(new Item.Properties().durability(2000)));
    public static final DeferredHolder<Item, Item> W2_BEAK = ITEMS.register("w2_beak",
            () -> new LCorpEGOR2Beak(new Item.Properties().durability(1000)));
    public static final DeferredHolder<Item, Item> W2_SOLITUDE = ITEMS.register("w2_solitude",
            () -> new LCorpEGOR2Solitude(new Item.Properties().durability(1000)));
    public static final DeferredHolder<Item, Item> W2_TODAY = ITEMS.register("w2_today",
            () -> new LCorpEGOR2Today(new Item.Properties().durability(1000)));
    public static final DeferredHolder<Item, Item> W2_FOURTH_MATCH_FIRE = ITEMS.register("w2_match",
            () -> new LCorpEGOR2Match(new Item.Properties().durability(1000)));
    public static final DeferredHolder<Item, Item> W1_SODA = ITEMS.register("w1_soda",
            () -> new LCorpEGOR1Soda(new Item.Properties().durability(800)));

    //L社EGO防具(EGOページ)
    public static final DeferredHolder<Item, Item> S5_TWILIGHT = ITEMS.register("s5_twilight",
            () -> new LCorpEGOL5Twilight(GeoArmorItem.properties(4000, 40, 40, 0.9)));

    public static final DeferredHolder<Item, Item> S5_PARADISE_LOST = ITEMS.register("s5_whitenight",
            () -> new LCorpEGOL5WhiteNight(GeoArmorItem.properties(4000, 40, 40, 0.9)));

    public static final DeferredHolder<Item, Item> S5_MIMICRY = ITEMS.register("s5_mimicry",
            () -> new LCorpEGOL5Mimicry(GeoArmorItem.properties(4000, 30, 30, 0.75)));

    public static final DeferredHolder<Item, Item> S5_DA_CAPO = ITEMS.register("s5_da_capo",
            () -> new LCorpEGOL5DaCapo(GeoArmorItem.properties(4000, 30, 30, 0.75)));

    public static final DeferredHolder<Item, Item> S5_SMILE = ITEMS.register("s5_smile",
            () -> new LCorpEGOL5Smile(GeoArmorItem.properties(4000, 30, 30, 0.75)));

    public static final DeferredHolder<Item, Item> S5_SOUND_OF_A_STAR = ITEMS.register("s5_star",
            () -> new LCorpEGOL5Star(GeoArmorItem.properties(4000, 30, 30, 0.75)));

    public static final DeferredHolder<Item, Item> S5_PINK = ITEMS.register("s5_pink",
            () -> new LCorpEGOL5Pink(GeoArmorItem.properties(4000, 30, 30, 0.75)));

    public static final DeferredHolder<Item, Item> S5_JUSTITIA = ITEMS.register("s5_justitia",
            () -> new LCorpEGOL5Justitia(GeoArmorItem.properties(4000, 30, 30, 0.75)));

    public static final DeferredHolder<Item, Item> S4_THE_SWORD_SHARPENED_WITH_TEARS = ITEMS.register("s4_tears",
            () -> new LCorpEGOL4Tears(GeoArmorItem.properties(3000, 25, 25, 0.6)));

    public static final DeferredHolder<Item, Item> S4_LAMP = ITEMS.register("s4_lamp",
            () -> new LCorpEGOL4Lamp(GeoArmorItem.properties(3000, 25, 25, 0.6)));

    public static final DeferredHolder<Item, Item> S4_HORNET = ITEMS.register("s4_hornet",
            () -> new LCorpEGOL4Hornet(GeoArmorItem.properties(3000, 25, 25, 0.6)));

    public static final DeferredHolder<Item, Item> S4_HEAVEN = ITEMS.register("s4_heaven",
            () -> new LCorpEGOL4Heaven(GeoArmorItem.properties(3000, 25, 25, 0.6)));

    public static final DeferredHolder<Item, Item> S4_HATRED = ITEMS.register("s4_hatred",
            () -> new LCorpEGOL4Hatred(GeoArmorItem.properties(3000, 25, 25, 0.6)));

    public static final DeferredHolder<Item, Item> S4_BLUE_SCAR = ITEMS.register("s4_blue_scar",
            () -> new LCorpEGOL4BlueScar(GeoArmorItem.properties(3000, 25, 25, 0.6)));

    public static final DeferredHolder<Item, Item> S4_CRIMSON_SCAR = ITEMS.register("s4_crimson_scar",
            () -> new LCorpEGOL4CrimsonScar(GeoArmorItem.properties(3000, 25, 25, 0.6)));

    public static final DeferredHolder<Item, Item> S4_STEM = ITEMS.register("s4_stem",
            () -> new LCorpEGOL4Stem(GeoArmorItem.properties(3000, 25, 25, 0.6)));

    public static final DeferredHolder<Item, Item> S4_SWAN = ITEMS.register("s4_swan",
            () -> new LCorpEGOL4Swan(GeoArmorItem.properties(3000, 25, 25, 0.6)));

    public static final DeferredHolder<Item, Item> S4_AROMA = ITEMS.register("s4_aroma",
            () -> new LCorpEGOL4Aroma(GeoArmorItem.properties(3000, 25, 25, 0.6)));

    public static final DeferredHolder<Item, Item> S3_MAGIC_BULLET = ITEMS.register("s3_magic_bullet",
            () -> new LCorpEGOL3MagicBullet(GeoArmorItem.properties(2000, 20, 20, 0.45)));

    public static final DeferredHolder<Item, Item> S3_SOLEMN_LAMENT = ITEMS.register("s3_lament",
            () -> new LCorpEGOL3Lament(GeoArmorItem.properties(2000, 20, 20, 0.45)));

    public static final DeferredHolder<Item, Item> S3_GALAXY = ITEMS.register("s3_galaxy",
            () -> new LCorpEGOL3Galaxy(GeoArmorItem.properties(2000, 20, 20, 0.45)));

    public static final DeferredHolder<Item, Item> S3_BLOOD = ITEMS.register("s3_blood",
            () -> new LCorpEGOL3Blood(GeoArmorItem.properties(2000, 20, 20, 0.45)));

    public static final DeferredHolder<Item, Item> S3_LOGGING = ITEMS.register("s3_logging",
            () -> new LCorpEGOL3Logging(GeoArmorItem.properties(2000, 20, 20, 0.45)));

    public static final DeferredHolder<Item, Item> S3_BEAR = ITEMS.register("s3_bear",
            () -> new LCorpEGOL3Bear(GeoArmorItem.properties(2000, 20, 20, 0.45)));

    public static final DeferredHolder<Item, Item> S3_HARVEST = ITEMS.register("s3_harvest",
            () -> new LCorpEGOL3Harvest(GeoArmorItem.properties(2000, 20, 20, 0.45)));

    public static final DeferredHolder<Item, Item> S3_GRINDER_MK4 = ITEMS.register("s3_mk4",
            () -> new LCorpEGOL3MK4(GeoArmorItem.properties(2000, 20, 20, 0.45)));

    public static final DeferredHolder<Item, Item> S3_ICE_SHARD = ITEMS.register("s3_ice_shard",
            () -> new LCorpEGOL3IceShard(GeoArmorItem.properties(2000, 20, 20, 0.45)));

    public static final DeferredHolder<Item, Item> S3_LAETITIA = ITEMS.register("s3_laetitia",
            () -> new LCorpEGOL3Laetitia(GeoArmorItem.properties(2000, 20, 20, 0.45)));

    public static final DeferredHolder<Item, Item> S2_REGLET = ITEMS.register("s2_reglet",
            () -> new LCorpEGOL2Reglet(GeoArmorItem.properties(1000, 15, 15, 0.3)));

    public static final DeferredHolder<Item, Item> S2_RED_EYE = ITEMS.register("s2_red_eye",
            () -> new LCorpEGOL2RedEye(GeoArmorItem.properties(1000, 15, 15, 0.3)));

    public static final DeferredHolder<Item, Item> S2_BEAK = ITEMS.register("s2_beak",
            () -> new LCorpEGOL2Beak(GeoArmorItem.properties(1000, 15, 15, 0.3)));

    public static final DeferredHolder<Item, Item> S2_FOURTH_MATCH_FIRE = ITEMS.register("s2_match",
            () -> new LCorpEGOL2Match(GeoArmorItem.properties(1000, 15, 15, 0.3)));

    public static final DeferredHolder<Item, Item> S2_SOMEWHERE = ITEMS.register("s2_somewhere",
            () -> new LCorpEGOL2SomeWhere(GeoArmorItem.properties(1000, 15, 15, 0.3)));

    public static final DeferredHolder<Item, Item> S2_SOLITUDE = ITEMS.register("s2_solitude",
            () -> new LCorpEGOL2Solitude(GeoArmorItem.properties(1000, 15, 15, 0.3)));

    public static final DeferredHolder<Item, Item> S2_TODAY = ITEMS.register("s2_today",
            () -> new LCorpEGOL2Today(GeoArmorItem.properties(1000, 15, 15, 0.3)));

    public static final DeferredHolder<Item, Item> S2_BATH = ITEMS.register("s2_bath",
            () -> new LCorpEGOL2Bath(GeoArmorItem.properties(1000, 15, 15, 0.3)));

    public static final DeferredHolder<Item, Item> S2_LANTERN = ITEMS.register("s2_lantern",
            () -> new LCorpEGOL2Lantern(GeoArmorItem.properties(1000, 15, 15, 0.3)));

    public static final DeferredHolder<Item, Item> S1_SODA = ITEMS.register("s1_soda",
            () -> new LCorpEGOL1Soda(GeoArmorItem.properties(800, 10, 10, 0.15)));

    public static final DeferredHolder<Item, Item> S1_WINGBEAT = ITEMS.register("s1_wingbeat",
            () -> new LCorpEGOL1WingBeat(GeoArmorItem.properties(800, 10, 10, 0.15)));

    public static final DeferredHolder<Item, Item> S1_PENITENCE = ITEMS.register("s1_penitence",
            () -> new LCorpEGOL1Penitence(GeoArmorItem.properties(800, 10, 10, 0.15)));

    //フィクサー武器 近距離
    public static final DeferredHolder<Item, Item> G_MIMICRY = ITEMS.register("fixer_kali_mimicry", FixerKaliMimicry::new);
    public static final DeferredHolder<Item, Item> EX_DURANDAL = ITEMS.register("fixer_roland_durandal", FixerRolandDurandal::new);
    public static final DeferredHolder<Item, Item> FIXER_ROLAND_WHEELS = ITEMS.register("fixer_roland_wheels", FixerRolandWheels::new);
    public static final DeferredHolder<Item, Item> FIXER_ROLAND_MOOK = ITEMS.register("fixer_roland_mook", FixerRolandMook::new);
    public static final DeferredHolder<Item, Item> FIXER_ROLAND_CRYSTAL = ITEMS.register("fixer_roland_crystal", FixerRolandCrystal::new);
    public static final DeferredHolder<Item, Item> FIXER_ROLAND_ALLAS = ITEMS.register("fixer_roland_allas", FixerRolandAllas::new);
    public static final DeferredHolder<Item, Item> FIXER_ROLAND_OLD_BOYS = ITEMS.register("fixer_roland_oldboys", FixerRolandOldBoys::new);
    public static final DeferredHolder<Item, Item> FIXER_ROLAND_RANGA = ITEMS.register("fixer_roland_ranga", FixerRolandRanga::new);
    public static final DeferredHolder<Item, Item> FIXER_ROLAND_ZELKOVA_AXE = ITEMS.register("fixer_roland_zelkova_axe", FixerRolandZelkovaAxe::new);
    public static final DeferredHolder<Item, Item> FIXER_ROLAND_ZELKOVA_MACE = ITEMS.register("fixer_roland_zelkova_mace", FixerRolandZelkovaMace::new);
    public static final DeferredHolder<Item, Item> EX_ARGALIA = ITEMS.register("fixer_argalia", FixerArgalia::new);

    public static final DeferredHolder<Item, Item> ASC2_SOUTH_W1 = ITEMS.register("asc2_south_weapon1", ASC2Weapon1::new);
    public static final DeferredHolder<Item, Item> ASC2_SOUTH_W2 = ITEMS.register("asc2_south_weapon2", ASC2Weapon2::new);
    public static final DeferredHolder<Item, Item> ASC2_WALTER = ITEMS.register("asc2_south_walter", ASC2Weapon3::new);
    public static final DeferredHolder<Item, Item> ASC2_WEST_WEAPON = ITEMS.register("asc2_west_weapon", ASC2Weapon4::new);
    public static final DeferredHolder<Item, Item> ASC2_SOUTH_ARMOR_S6 = ITEMS.register("asc2_south_armor_s6",
            () -> new ASC2ArmorTier1(GeoArmorItem.properties(100, 4, 1, 0)));
    public static final DeferredHolder<Item, Item> ASC2_SOUTH_ARMOR_S5 = ITEMS.register("asc2_south_armor_s5",
            () -> new ASC2ArmorTier2(GeoArmorItem.properties(200, 6, 1.2, 0)));
    public static final DeferredHolder<Item, Item> ASC2_SOUTH_ARMOR_S4 = ITEMS.register("asc2_south_armor_s4",
            () -> new ASC2ArmorTier3(GeoArmorItem.properties(300, 8, 1.5, 0)));
    public static final DeferredHolder<Item, Item> ASC2_SOUTH_ARMOR_S3 = ITEMS.register("asc2_south_armor_s3",
            () -> new ASC2ArmorTier4(GeoArmorItem.properties(400, 10, 1.8, 0)));
    public static final DeferredHolder<Item, Item> ASC2_SOUTH_ARMOR_S2 = ITEMS.register("asc2_south_armor_s2",
            () -> new ASC2ArmorTier5(GeoArmorItem.properties(500, 12, 2, 0)));
    public static final DeferredHolder<Item, Item> ASC2_SOUTH_ARMOR_S1 = ITEMS.register("asc2_south_armor_s1",
            () -> new ASC2ArmorTier6(GeoArmorItem.properties(600, 15, 2.5, 0)));


    //フィクサー武器 遠距離
    public static final DeferredHolder<Item, Item> WEAPON_ROLAND_REVOLVER = ITEMS.register("fixer_roland_lar",
            () -> new FixerRolandLogicHG(new Item.Properties().durability(2000)));
    public static final DeferredHolder<Item, Item> WEAPON_ROLAND_SHOTGUN = ITEMS.register("fixer_roland_lasg",
            () -> new FixerRolandLogicSG(new Item.Properties().durability(2000)));

    //W社装備
    public static final DeferredHolder<Item, Item> WCORP_WEAPON_1 = ITEMS.register("wcorp_w1", WingsWCorpWeapon1::new);
    public static final DeferredHolder<Item, Item> WCORP_WEAPON_2 = ITEMS.register("wcorp_w2", WingsWCorpWeapon2::new);
    public static final DeferredHolder<Item, Item> WCORP_WEAPON_3 = ITEMS.register("wcorp_w3", WingsWCorpWeapon3::new);

    public static final DeferredHolder<Item, Item> WCORP_ARMOR_L1 = ITEMS.register("wcorp_armor_l1",
            () -> new WingsWCorpArmorTier1(GeoArmorItem.properties(300, 12, 4, 0.01)));

    public static final DeferredHolder<Item, Item> WCORP_ARMOR_L2 = ITEMS.register("wcorp_armor_l2",
            () -> new WingsWCorpArmorTier2(GeoArmorItem.properties(500, 14, 8, 0.02)));

    public static final DeferredHolder<Item, Item> WCORP_ARMOR_L3 = ITEMS.register("wcorp_armor_l3",
            () -> new WingsWCorpArmorTier3(GeoArmorItem.properties(700, 16, 10, 0.03)));

    public static final DeferredHolder<Item, Item> WCORP_ARMOR_L4 = ITEMS.register("wcorp_armor_l4",
            () -> new WingsWCorpArmorTier4(GeoArmorItem.properties(850, 18, 12, 0.04)));

    public static final DeferredHolder<Item, Item> WCORP_ARMOR_L5 = ITEMS.register("wcorp_armor_l5",
            () -> new WingsWCorpArmorTier5(GeoArmorItem.properties(1000, 20, 15, 0.05)));

    //R社装備
    public static final DeferredHolder<Item, Item> RCORP_RABBIT_RIFLE = ITEMS.register("rcorp_rabbit_rifle",
            () -> new WingsRCorp4thRabbitWeapon1(new Item.Properties().durability(2000)));
    public static final DeferredHolder<Item, Item> RCORP_RABBIT_KNIFE = ITEMS.register("rcorp_rabbit_knife", WingsRCorp4thRabbitWeapon2::new);

    public static final DeferredHolder<Item, Item> RCORP_RABBIT_ARMOR_L1 = ITEMS.register("rcorp_rabbit_armor_l1",
            () -> new WingsRCorp4thRabbitArmorTier1(GeoArmorItem.properties(300, 12, 4, 0.01)));

    public static final DeferredHolder<Item, Item> RCORP_RABBIT_ARMOR_L2 = ITEMS.register("rcorp_rabbit_armor_l2",
            () -> new WingsRCorp4thRabbitArmorTier2(GeoArmorItem.properties(500, 14, 8, 0.02)));

    public static final DeferredHolder<Item, Item> RCORP_RABBIT_ARMOR_L3 = ITEMS.register("rcorp_rabbit_armor_l3",
            () -> new WingsRCorp4thRabbitArmorTier3(GeoArmorItem.properties(700, 16, 10, 0.03)));

    public static final DeferredHolder<Item, Item> RCORP_RABBIT_ARMOR_L4 = ITEMS.register("rcorp_rabbit_armor_l4",
            () -> new WingsRCorp4thRabbitArmorTier4(GeoArmorItem.properties(850, 18, 12, 0.04)));

    public static final DeferredHolder<Item, Item> RCORP_RABBIT_ARMOR_L5 = ITEMS.register("rcorp_rabbit_armor_l5",
            () -> new WingsRCorp4thRabbitArmorTier5(GeoArmorItem.properties(1000, 20, 15, 0.05)));

    //K社装備
    public static final DeferredHolder<Item, Item> KCORP_WEAPON_1 = ITEMS.register("kcorp_w1", WingsKCorpWeapon1::new);
    public static final DeferredHolder<Item, Item> KCORP_WEAPON_2 = ITEMS.register("kcorp_w2", WingsKCorpWeapon2::new);
    public static final DeferredHolder<Item, Item> KCORP_WEAPON_3 = ITEMS.register("kcorp_w3", WingsKCorpWeapon3::new);

    public static final DeferredHolder<Item, Item> KCORP_OFFICER_ARMOR_L1 = ITEMS.register("kcorp_officer_armor_l1",
            () -> new WingsKCorpOfficerArmorTier1(GeoArmorItem.properties(300, 12, 4, 0.01)));

    public static final DeferredHolder<Item, Item> KCORP_OFFICER_ARMOR_L2 = ITEMS.register("kcorp_officer_armor_l2",
            () -> new WingsKCorpOfficerArmorTier2(GeoArmorItem.properties(500, 14, 8, 0.02)));

    public static final DeferredHolder<Item, Item> KCORP_AGENT_ARMOR_L3 = ITEMS.register("kcorp_agent_armor_l3",
            () -> new WingsKCorpAgentArmorTier3(GeoArmorItem.properties(700, 16, 10, 0.03)));

    public static final DeferredHolder<Item, Item> KCORP_AGENT_ARMOR_L4 = ITEMS.register("kcorp_agent_armor_l4",
            () -> new WingsKCorpAgentArmorTier4(GeoArmorItem.properties(850, 18, 12, 0.04)));

    public static final DeferredHolder<Item, Item> KCORP_AGENT_ARMOR_L5 = ITEMS.register("kcorp_agent_armor_l5",
            () -> new WingsKCorpAgentArmorTier5(GeoArmorItem.properties(1000, 20, 15, 0.05)));

    //H社装備
    public static final DeferredHolder<Item, Item> HCORP_MAO_WEAPON = ITEMS.register("hcorp_mao_weapon", WingsHCorpMaoWeapon1::new);
    public static final DeferredHolder<Item, Item> HCORP_MAO_ARMOR_L1 = ITEMS.register("hcorp_mao_armor_l1",
            () -> new WingsHCorpMaoArmorTier1(GeoArmorItem.properties(300, 12, 4, 0.01)));
    public static final DeferredHolder<Item, Item> HCORP_MAO_ARMOR_L2 = ITEMS.register("hcorp_mao_armor_l2",
            () -> new WingsHCorpMaoArmorTier2(GeoArmorItem.properties(500, 14, 8, 0.02)));
    public static final DeferredHolder<Item, Item> HCORP_MAO_ARMOR_L3 = ITEMS.register("hcorp_mao_armor_l3",
            () -> new WingsHCorpMaoArmorTier3(GeoArmorItem.properties(700, 16, 10, 0.03)));
    public static final DeferredHolder<Item, Item> HCORP_MAO_ARMOR_L4 = ITEMS.register("hcorp_mao_armor_l4",
            () -> new WingsHCorpMaoArmorTier4(GeoArmorItem.properties(850, 18, 12, 0.04)));
    public static final DeferredHolder<Item, Item> HCORP_MAO_ARMOR_L5 = ITEMS.register("hcorp_mao_armor_l5",
            () -> new WingsHCorpMaoArmorTier5(GeoArmorItem.properties(1000, 20, 15, 0.05)));

    public static final DeferredHolder<Item, Item> HCORP_SI_WEAPON = ITEMS.register("hcorp_si_weapon", WingsHCorpSiWeapon1::new);
    public static final DeferredHolder<Item, Item> HCORP_SI_ARMOR_L1 = ITEMS.register("hcorp_si_armor_l1",
            () -> new WingsHCorpSiArmorTier1(GeoArmorItem.properties(300, 12, 4, 0.01)));
    public static final DeferredHolder<Item, Item> HCORP_SI_ARMOR_L2 = ITEMS.register("hcorp_si_armor_l2",
            () -> new WingsHCorpSiArmorTier2(GeoArmorItem.properties(500, 14, 8, 0.02)));
    public static final DeferredHolder<Item, Item> HCORP_SI_ARMOR_L3 = ITEMS.register("hcorp_si_armor_l3",
            () -> new WingsHCorpSiArmorTier3(GeoArmorItem.properties(700, 16, 10, 0.03)));
    public static final DeferredHolder<Item, Item> HCORP_SI_ARMOR_L4 = ITEMS.register("hcorp_si_armor_l4",
            () -> new WingsHCorpSiArmorTier4(GeoArmorItem.properties(850, 18, 12, 0.04)));
    public static final DeferredHolder<Item, Item> HCORP_SI_ARMOR_L5 = ITEMS.register("hcorp_si_armor_l5",
            () -> new WingsHCorpSiArmorTier5(GeoArmorItem.properties(1000, 20, 15, 0.05)));

    public static final DeferredHolder<Item, Item> HCORP_YOU_WEAPON = ITEMS.register("hcorp_you_weapon", WingsHCorpYouWeapon1::new);
    public static final DeferredHolder<Item, Item> HCORP_YOU_ARMOR_L1 = ITEMS.register("hcorp_you_armor_l1",
            () -> new WingsHCorpYouArmorTier1(GeoArmorItem.properties(300, 12, 4, 0.01)));
    public static final DeferredHolder<Item, Item> HCORP_YOU_ARMOR_L2 = ITEMS.register("hcorp_you_armor_l2",
            () -> new WingsHCorpYouArmorTier2(GeoArmorItem.properties(500, 14, 8, 0.02)));
    public static final DeferredHolder<Item, Item> HCORP_YOU_ARMOR_L3 = ITEMS.register("hcorp_you_armor_l3",
            () -> new WingsHCorpYouArmorTier3(GeoArmorItem.properties(700, 16, 10, 0.03)));
    public static final DeferredHolder<Item, Item> HCORP_YOU_ARMOR_L4 = ITEMS.register("hcorp_you_armor_l4",
            () -> new WingsHCorpYouArmorTier4(GeoArmorItem.properties(850, 18, 12, 0.04)));
    public static final DeferredHolder<Item, Item> HCORP_YOU_ARMOR_L5 = ITEMS.register("hcorp_you_armor_l5",
            () -> new WingsHCorpYouArmorTier5(GeoArmorItem.properties(1000, 20, 15, 0.05)));

    //頭装備
    public static final DeferredHolder<Item, Item> HEAD_CLAW = ITEMS.register("head_claw", HeadClaw::new);
}




