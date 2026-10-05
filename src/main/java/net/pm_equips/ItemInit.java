package net.pm_equips;

import net.minecraft.world.item.Rarity;
import net.pm_equips.items.*;
import net.pm_equips.items.materials.ArmorEquips;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PMEquipsMain.MOD_ID);

    //アイテム
    public static final RegistryObject<Item> ITEM_ENKEPHALIN = ITEMS.register("item_enkephalin",
            () -> new ItemEnkephalin(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> SILVER_INGOT = ITEMS.register("item_silver",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> METAL_INGOT = ITEMS.register("item_metal",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> PLATINUM_INGOT = ITEMS.register("item_platinum",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> BLADE_GEAR = ITEMS.register("item_blade_gear",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> WCORP_BATTERY = ITEMS.register("item_wcorp_battery",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> RCORP_BATTERY = ITEMS.register("item_rcorp_battery",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> KCORP_AMPOULE = ITEMS.register("item_kcorp_ampoule",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> HCORP_BOLUS_MAO = ITEMS.register("item_hcorp_bolus_mao",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> HCORP_BOLUS_SI = ITEMS.register("item_hcorp_bolus_si",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> HCORP_BOLUS_YOU = ITEMS.register("item_hcorp_bolus_you",
            () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> MAGIC_BULLET_AMMO = ITEMS.register("item_bullet_ego",
            () -> new AmmoMagicBullet(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> RIFLE_BULLET_AMMO = ITEMS.register("item_bullet_r",
            () -> new AmmoRifle(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> PISTOL_BULLET_AMMO = ITEMS.register("item_bullet_p",
            () -> new AmmoPistol(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> EXPLOSIVE_BULLET_AMMO = ITEMS.register("item_bullet_ex",
            () -> new AmmoExplosive(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> P_BULLET_LARV = ITEMS.register("item_bullet_larv",
            () -> new AmmoLARV(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> P_BULLET_LASG = ITEMS.register("item_bullet_lasg",
            () -> new AmmoLASG(new Item.Properties().stacksTo(64)));

    //L社EGO武器 近距離
    public static final RegistryObject<Item> W5_TWILIGHT = ITEMS.register("w5_twilight", EGOW5Twilight::new);
    public static final RegistryObject<Item> W5_PARADISE_LOST = ITEMS.register("w5_whitenight", EGOW5WhiteNight::new);
    public static final RegistryObject<Item> W5_MIMICRY = ITEMS.register("w5_mimicry", EGOW5Mimicry::new);
    public static final RegistryObject<Item> W5_DA_CAPO = ITEMS.register("w5_da_capo", EGOW5DaCapo::new);
    public static final RegistryObject<Item> W5_SMILE = ITEMS.register("w5_smile", EGOW5Smile::new);
    public static final RegistryObject<Item> W5_SOUND_OF_A_STAR = ITEMS.register("w5_star", EGOW5Star::new);
    public static final RegistryObject<Item> W5_JUSTITIA = ITEMS.register("w5_justitia", EGOW5Justitia::new);
    public static final RegistryObject<Item> W4_THE_SWORD_SHARPENED_WITH_TEARS = ITEMS.register("w4_tears", EGOW4Tears::new);
    public static final RegistryObject<Item> W4_LAMP = ITEMS.register("w4_lamp", EGOW4Lamp::new);
    public static final RegistryObject<Item> W4_HATRED = ITEMS.register("w4_hatred", EGOW4Hatred::new);
    public static final RegistryObject<Item> W4_HEAVEN = ITEMS.register("w4_heaven", EGOW4Heaven::new);
    public static final RegistryObject<Item> W4_SWAN = ITEMS.register("w4_swan", EGOW4Swan::new);
    public static final RegistryObject<Item> W4_STEM = ITEMS.register("w4_stem", EGOW4Stem::new);
    public static final RegistryObject<Item> W4_DIFFRACTION = ITEMS.register("w4_diffraction", EGOW4Diffraction::new);
    public static final RegistryObject<Item> W4_BLUE_SCAR = ITEMS.register("w4_blue_scar", EGOW4BlueScar::new);
    public static final RegistryObject<Item> W4_CRIMSON_SCAR_R = ITEMS.register("w4_crimson_scar_r", EGOW4CrimsonScarR::new);
    public static final RegistryObject<Item> W3_BLOOD = ITEMS.register("w3_blood", EGOW3Blood::new);
    public static final RegistryObject<Item> W3_BEAR = ITEMS.register("w3_bear", EGOW3Bear::new);
    public static final RegistryObject<Item> W3_HARVEST = ITEMS.register("w3_harvest", EGOW3Harvest::new);
    public static final RegistryObject<Item> W3_LOGGING = ITEMS.register("w3_logging", EGOW3Logging::new);
    public static final RegistryObject<Item> W3_GALAXY = ITEMS.register("w3_galaxy", EGOW3Galaxy::new);
    public static final RegistryObject<Item> W3_GRINDER_MK4 = ITEMS.register("w3_mk4", EGOW3MK4::new);
    public static final RegistryObject<Item> W3_ICE_SHARD = ITEMS.register("w3_ice_shard", EGOW3IceShard::new);
    public static final RegistryObject<Item> W2_REGLET = ITEMS.register("w2_reglet", EGOW2Reglet::new);
    public static final RegistryObject<Item> W2_SOMEWHERE = ITEMS.register("w2_somewhere", EGOW2SomeWhere::new);
    public static final RegistryObject<Item> W2_BATH = ITEMS.register("w2_bath", EGOW2Bath::new);
    public static final RegistryObject<Item> W2_RED_EYE = ITEMS.register("w2_red_eye", EGOW2RedEye::new);
    public static final RegistryObject<Item> W2_LANTERN = ITEMS.register("w2_lantern", EGOW2Lantern::new);
    public static final RegistryObject<Item> W1_PENITENCE = ITEMS.register("w1_penitence", EGOW1Penitence::new);
    public static final RegistryObject<Item> W1_WINGBEAT = ITEMS.register("w1_wingbeat", EGOW1WingBeat::new);

    //L社EGO武器 遠距離
    public static final RegistryObject<Item> W5_PINK = ITEMS.register("w5_pink",
            () -> new EGOW5Pink(new Item.Properties().durability(4000)));
    public static final RegistryObject<Item> W4_AROMA = ITEMS.register("w4_aroma",
            () -> new EGOW4Aroma(new Item.Properties().durability(3000)));
    public static final RegistryObject<Item> W4_SOLEMN_LAMENT_L = ITEMS.register("w4_lament_l",
            () -> new EGOW4LamentL(new Item.Properties().durability(3000)));
    public static final RegistryObject<Item> W4_SOLEMN_LAMENT_R = ITEMS.register("w4_lament_r",
            () -> new EGOW4LamentR(new Item.Properties().durability(3000)));
    public static final RegistryObject<Item> W4_MAGIC_BULLET = ITEMS.register("w4_magic_bullet",
            () -> new EGOW4MagicBullet(new Item.Properties().durability(3000)));
    public static final RegistryObject<Item> W4_HORNET = ITEMS.register("w4_hornet",
            () -> new EGOW4Hornet(new Item.Properties().durability(3000)));
    public static final RegistryObject<Item> W4_CRIMSON_SCAR_L = ITEMS.register("w4_crimson_scar_l",
            () -> new EGOW4CrimsonScarL(new Item.Properties().durability(3000)));
    public static final RegistryObject<Item> W3_HARMONY = ITEMS.register("w3_harmony",
            () -> new EGOW3Harmony(new Item.Properties().durability(2000)));
    public static final RegistryObject<Item> W3_LAETITIA = ITEMS.register("w3_laetitia",
            () -> new EGOW3Laetitia(new Item.Properties().durability(2000)));
    public static final RegistryObject<Item> W2_BEAK = ITEMS.register("w2_beak",
            () -> new EGOW2Beak(new Item.Properties().durability(1000)));
    public static final RegistryObject<Item> W2_SOLITUDE = ITEMS.register("w2_solitude",
            () -> new EGOW2Solitude(new Item.Properties().durability(1000)));
    public static final RegistryObject<Item> W2_TODAY = ITEMS.register("w2_today",
            () -> new EGOW2Today(new Item.Properties().durability(1000)));
    public static final RegistryObject<Item> W2_FOURTH_MATCH_FIRE = ITEMS.register("w2_match",
            () -> new EGOW2Match(new Item.Properties().durability(1000)));
    public static final RegistryObject<Item> W1_SODA = ITEMS.register("w1_soda",
            () -> new EGOW1Soda(new Item.Properties().durability(800)));

    //L社EGO防具(EGOページ)
    public static final RegistryObject<Item> S5_TWILIGHT = ITEMS.register("s5_twilight",
            () -> new EGOP5Twilight(ArmorEquips.A5_TWILIGHT, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> S5_PARADISE_LOST = ITEMS.register("s5_whitenight",
            () -> new EGOP5WhiteNight(ArmorEquips.A5_PARADISE_LOST, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> S5_MIMICRY = ITEMS.register("s5_mimicry",
            () -> new EGOP5Mimicry(ArmorEquips.A5_MIMICRY, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S5_DA_CAPO = ITEMS.register("s5_da_capo",
            () -> new EGOP5DaCapo(ArmorEquips.A5_DA_CAPO, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S5_SMILE = ITEMS.register("s5_smile",
            () -> new EGOP5Smile(ArmorEquips.A5_SMILE, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S5_SOUND_OF_A_STAR = ITEMS.register("s5_star",
            () -> new EGOP5Star(ArmorEquips.A5_SOUND_OF_A_STAR, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S5_PINK = ITEMS.register("s5_pink",
            () -> new EGOP5Pink(ArmorEquips.A5_PINK, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S5_JUSTITIA = ITEMS.register("s5_justitia",
            () -> new EGOP5Justitia(ArmorEquips.A5_JUSTITIA, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S4_THE_SWORD_SHARPENED_WITH_TEARS = ITEMS.register("s4_tears",
            () -> new EGOP4Tears(ArmorEquips.A4_THE_SWORD_SHARPENED_WITH_TEARS, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S4_LAMP = ITEMS.register("s4_lamp",
            () -> new EGOP4Lamp(ArmorEquips.A4_LAMP, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S4_HORNET = ITEMS.register("s4_hornet",
            () -> new EGOP4Hornet(ArmorEquips.A4_HORNET, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S4_HEAVEN = ITEMS.register("s4_heaven",
            () -> new EGOP4Heaven(ArmorEquips.A4_HEAVEN, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S4_HATRED = ITEMS.register("s4_hatred",
            () -> new EGOP4Hatred(ArmorEquips.A4_HATRED, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S4_BLUE_SCAR = ITEMS.register("s4_blue_scar",
            () -> new EGOP4BlueScar(ArmorEquips.A4_BLUE_SCAR, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S4_CRIMSON_SCAR = ITEMS.register("s4_crimson_scar",
            () -> new EGOP4CrimsonScar(ArmorEquips.A4_CRIMSON_SCAR, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S4_STEM = ITEMS.register("s4_stem",
            () -> new EGOP4Stem(ArmorEquips.A4_STEM, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S4_SWAN = ITEMS.register("s4_swan",
            () -> new EGOP4Swan(ArmorEquips.A4_SWAN, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S4_AROMA = ITEMS.register("s4_aroma",
            () -> new EGOP4Aroma(ArmorEquips.A4_AROMA, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S3_MAGIC_BULLET = ITEMS.register("s3_magic_bullet",
            () -> new EGOP3MagicBullet(ArmorEquips.A3_MAGIC_BULLET, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S3_SOLEMN_LAMENT = ITEMS.register("s3_lament",
            () -> new EGOP3Lament(ArmorEquips.A3_SOLEMN_LAMENT, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S3_GALAXY = ITEMS.register("s3_galaxy",
            () -> new EGOP3Galaxy(ArmorEquips.A3_GALAXY, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S3_BLOOD = ITEMS.register("s3_blood",
            () -> new EGOP3Blood(ArmorEquips.A3_BLOOD, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S3_LOGGING = ITEMS.register("s3_logging",
            () -> new EGOP3Logging(ArmorEquips.A3_LOGGING, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S3_BEAR = ITEMS.register("s3_bear",
            () -> new EGOP3Bear(ArmorEquips.A3_BEAR, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S3_HARVEST = ITEMS.register("s3_harvest",
            () -> new EGOP3Harvest(ArmorEquips.A3_HARVEST, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S3_GRINDER_MK4 = ITEMS.register("s3_mk4",
            () -> new EGOP3MK4(ArmorEquips.A3_GRINDER_MK4, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S3_ICE_SHARD = ITEMS.register("s3_ice_shard",
            () -> new EGOP3IceShard(ArmorEquips.A3_ICE_SHARD, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S3_LAETITIA = ITEMS.register("s3_laetitia",
            () -> new EGOP3Laetitia(ArmorEquips.A3_LAETITIA, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S2_REGLET = ITEMS.register("s2_reglet",
            () -> new EGOP2Reglet(ArmorEquips.A2_REGLET, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S2_RED_EYE = ITEMS.register("s2_red_eye",
            () -> new EGOP2RedEye(ArmorEquips.A2_RED_EYE, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S2_BEAK = ITEMS.register("s2_beak",
            () -> new EGOP2Beak(ArmorEquips.A2_BEAK, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S2_FOURTH_MATCH_FIRE = ITEMS.register("s2_match",
            () -> new EGOP2Match(ArmorEquips.A2_FOURTH_MATCH_FIRE, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S2_SOMEWHERE = ITEMS.register("s2_somewhere",
            () -> new EGOP2SomeWhere(ArmorEquips.A2_SOMEWHERE, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S2_SOLITUDE = ITEMS.register("s2_solitude",
            () -> new EGOP2Solitude(ArmorEquips.A2_SOLITUDE, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S2_TODAY = ITEMS.register("s2_today",
            () -> new EGOP2Today(ArmorEquips.A2_TODAY, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S2_BATH = ITEMS.register("s2_bath",
            () -> new EGOP2Bath(ArmorEquips.A2_BATH, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S2_LANTERN = ITEMS.register("s2_lantern",
            () -> new EGOP2Lantern(ArmorEquips.A2_LANTERN, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S1_SODA = ITEMS.register("s1_soda",
            () -> new EGOP1Soda(ArmorEquips.A1_SODA, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S1_WINGBEAT = ITEMS.register("s1_wingbeat",
            () -> new EGOP1WingBeat(ArmorEquips.A1_WINGBEAT, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> S1_PENITENCE = ITEMS.register("s1_penitence",
            () -> new EGOP1Penitence(ArmorEquips.A1_PENITENCE, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    //L社装備
    public static final RegistryObject<Item> LCORP_BATON = ITEMS.register("lcorp_baton", WingLCorpBaton::new);
    public static final RegistryObject<Item> LCORP_PISTOL = ITEMS.register("lcorp_pistol",
            () -> new WingLCorpPistol(new Item.Properties()));
    public static final RegistryObject<Item> LIBRARY_SWORD_1 = ITEMS.register("library_sword_1", LibrarySword::new);

    //フィクサー武器 近距離
    public static final RegistryObject<Item> G_MIMICRY = ITEMS.register("fixer_kali_mimicry", WeaponKaliMimicry::new);
    public static final RegistryObject<Item> EX_DURANDAL = ITEMS.register("fixer_roland_durandal", WeaponRolandDurandal::new);
    public static final RegistryObject<Item> FIXER_ROLAND_WHEELS = ITEMS.register("fixer_roland_wheels", WeaponRolandWheels::new);
    public static final RegistryObject<Item> FIXER_ROLAND_MOOK = ITEMS.register("fixer_roland_mook", WeaponRolandMook::new);
    public static final RegistryObject<Item> FIXER_ROLAND_CRYSTAL = ITEMS.register("fixer_roland_crystal", WeaponRolandCrystal::new);
    public static final RegistryObject<Item> FIXER_ROLAND_ALLAS = ITEMS.register("fixer_roland_allas", WeaponRolandAllas::new);
    public static final RegistryObject<Item> FIXER_ROLAND_OLD_BOYS = ITEMS.register("fixer_roland_oldboys", WeaponRolandOldBoys::new);
    public static final RegistryObject<Item> FIXER_ROLAND_RANGA = ITEMS.register("fixer_roland_ranga", WeaponRolandRanga::new);
    public static final RegistryObject<Item> FIXER_ROLAND_ZELKOVA_AXE = ITEMS.register("fixer_roland_zelkova_axe", WeaponRolandZelkovaAxe::new);
    public static final RegistryObject<Item> FIXER_ROLAND_ZELKOVA_MACE = ITEMS.register("fixer_roland_zelkova_mace", WeaponRolandZelkovaMace::new);
    public static final RegistryObject<Item> EX_ARGALIA = ITEMS.register("fixer_argalia", WeaponArgalia::new);

    public static final RegistryObject<Item> ASC2_SOUTH_W1 = ITEMS.register("asc2_south_weapon1", Asc2SouthWeapon1::new);
    public static final RegistryObject<Item> ASC2_SOUTH_W2 = ITEMS.register("asc2_south_weapon2", Asc2SouthWeapon2::new);
    public static final RegistryObject<Item> ASC2_WALTER = ITEMS.register("asc2_south_walter", Asc2Walter::new);
    public static final RegistryObject<Item> ASC2_WEST_WEAPON = ITEMS.register("asc2_west_weapon", Asc2WestWeapon::new);
    public static final RegistryObject<Item> ASC2_SOUTH_ARMOR_S6 = ITEMS.register("asc2_south_armor_s6",
            () -> new Asc2SouthArmor(ArmorEquips.ASC2_ARMOR_SEC6, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ASC2_SOUTH_ARMOR_S5 = ITEMS.register("asc2_south_armor_s5",
            () -> new Asc2SouthArmor(ArmorEquips.ASC2_ARMOR_SEC5, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ASC2_SOUTH_ARMOR_S4 = ITEMS.register("asc2_south_armor_s4",
            () -> new Asc2SouthArmor(ArmorEquips.ASC2_ARMOR_SEC4, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ASC2_SOUTH_ARMOR_S3 = ITEMS.register("asc2_south_armor_s3",
            () -> new Asc2SouthArmor(ArmorEquips.ASC2_ARMOR_SEC3, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ASC2_SOUTH_ARMOR_S2 = ITEMS.register("asc2_south_armor_s2",
            () -> new Asc2SouthArmor(ArmorEquips.ASC2_ARMOR_SEC2, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ASC2_SOUTH_ARMOR_S1 = ITEMS.register("asc2_south_armor_s1",
            () -> new Asc2SouthArmor(ArmorEquips.ASC2_ARMOR_SEC1, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));


    //フィクサー武器 遠距離
    public static final RegistryObject<Item> WEAPON_ROLAND_REVOLVER = ITEMS.register("fixer_roland_lar",
            () -> new WeaponRolandLogicHG(new Item.Properties().durability(2000)));
    public static final RegistryObject<Item> WEAPON_ROLAND_SHOTGUN = ITEMS.register("fixer_roland_lasg",
            () -> new WeaponRolandLogicSG(new Item.Properties().durability(2000)));

    //W社装備
    public static final RegistryObject<Item> WCORP_WEAPON_1 = ITEMS.register("wcorp_w1", WCorpWeapon1::new);
    public static final RegistryObject<Item> WCORP_WEAPON_2 = ITEMS.register("wcorp_w2", WCorpWeapon2::new);
    public static final RegistryObject<Item> WCORP_WEAPON_3 = ITEMS.register("wcorp_w3", WCorpWeapon3::new);

    public static final RegistryObject<Item> WCORP_ARMOR_L1 = ITEMS.register("wcorp_armor_l1",
            () -> new WCorpArmor(ArmorEquips.WCORP_ARMOR_L1, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> WCORP_ARMOR_L2 = ITEMS.register("wcorp_armor_l2",
            () -> new WCorpArmor(ArmorEquips.WCORP_ARMOR_L2, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> WCORP_ARMOR_L3 = ITEMS.register("wcorp_armor_l3",
            () -> new WCorpArmor(ArmorEquips.WCORP_ARMOR_L3, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> WCORP_ARMOR_L4 = ITEMS.register("wcorp_armor_l4",
            () -> new WCorpArmor(ArmorEquips.WCORP_ARMOR_L4, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> WCORP_ARMOR_L5 = ITEMS.register("wcorp_armor_l5",
            () -> new WCorpArmor(ArmorEquips.WCORP_ARMOR_L5, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    //R社装備
    public static final RegistryObject<Item> RCORP_RABBIT_RIFLE = ITEMS.register("rcorp_rabbit_rifle",
            () -> new RCorpRabbitRifle(new Item.Properties().durability(2000)));
    public static final RegistryObject<Item> RCORP_RABBIT_KNIFE = ITEMS.register("rcorp_rabbit_knife", RCorpRabbitCombatKnife::new);

    public static final RegistryObject<Item> RCORP_RABBIT_ARMOR_L1 = ITEMS.register("rcorp_rabbit_armor_l1",
            () -> new RCorp4thRabbitArmor(ArmorEquips.RCORP_4TH_RABBIT_ARMOR_L1, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RCORP_RABBIT_ARMOR_L2 = ITEMS.register("rcorp_rabbit_armor_l2",
            () -> new RCorp4thRabbitArmor(ArmorEquips.RCORP_4TH_RABBIT_ARMOR_L2, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RCORP_RABBIT_ARMOR_L3 = ITEMS.register("rcorp_rabbit_armor_l3",
            () -> new RCorp4thRabbitArmor(ArmorEquips.RCORP_4TH_RABBIT_ARMOR_L3, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RCORP_RABBIT_ARMOR_L4 = ITEMS.register("rcorp_rabbit_armor_l4",
            () -> new RCorp4thRabbitArmor(ArmorEquips.RCORP_4TH_RABBIT_ARMOR_L4, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RCORP_RABBIT_ARMOR_L5 = ITEMS.register("rcorp_rabbit_armor_l5",
            () -> new RCorp4thRabbitArmor(ArmorEquips.RCORP_4TH_RABBIT_ARMOR_L5, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    //K社装備
    public static final RegistryObject<Item> KCORP_WEAPON_1 = ITEMS.register("kcorp_w1", KCorpWeapon1::new);
    public static final RegistryObject<Item> KCORP_WEAPON_2 = ITEMS.register("kcorp_w2", KCorpWeapon2::new);
    public static final RegistryObject<Item> KCORP_WEAPON_3 = ITEMS.register("kcorp_w3", KCorpWeapon3::new);

    public static final RegistryObject<Item> KCORP_OFFICER_ARMOR_L1 = ITEMS.register("kcorp_officer_armor_l1",
            () -> new KCorpOfficerArmor(ArmorEquips.KCORP_ARMOR_L1, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> KCORP_OFFICER_ARMOR_L2 = ITEMS.register("kcorp_officer_armor_l2",
            () -> new KCorpOfficerArmor(ArmorEquips.KCORP_ARMOR_L2, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> KCORP_AGENT_ARMOR_L3 = ITEMS.register("kcorp_agent_armor_l3",
            () -> new KCorpAgentArmor(ArmorEquips.KCORP_ARMOR_L3, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> KCORP_AGENT_ARMOR_L4 = ITEMS.register("kcorp_agent_armor_l4",
            () -> new KCorpAgentArmor(ArmorEquips.KCORP_ARMOR_L4, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> KCORP_AGENT_ARMOR_L5 = ITEMS.register("kcorp_agent_armor_l5",
            () -> new KCorpAgentArmor(ArmorEquips.KCORP_ARMOR_L5, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    //H社装備
    public static final RegistryObject<Item> HCORP_MAO_WEAPON = ITEMS.register("hcorp_mao_weapon", HCorpMaoWeapon::new);
    public static final RegistryObject<Item> HCORP_MAO_ARMOR_L1 = ITEMS.register("hcorp_mao_armor_l1",
            () -> new HCorpMaoArmor(ArmorEquips.HCORP_MAO_ARMOR_L1, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HCORP_MAO_ARMOR_L2 = ITEMS.register("hcorp_mao_armor_l2",
            () -> new HCorpMaoArmor(ArmorEquips.HCORP_MAO_ARMOR_L2, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HCORP_MAO_ARMOR_L3 = ITEMS.register("hcorp_mao_armor_l3",
            () -> new HCorpMaoArmor(ArmorEquips.HCORP_MAO_ARMOR_L3, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HCORP_MAO_ARMOR_L4 = ITEMS.register("hcorp_mao_armor_l4",
            () -> new HCorpMaoArmor(ArmorEquips.HCORP_MAO_ARMOR_L4, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HCORP_MAO_ARMOR_L5 = ITEMS.register("hcorp_mao_armor_l5",
            () -> new HCorpMaoArmor(ArmorEquips.HCORP_MAO_ARMOR_L5, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> HCORP_SI_WEAPON = ITEMS.register("hcorp_si_weapon", HCorpSiWeapon::new);
    public static final RegistryObject<Item> HCORP_SI_ARMOR_L1 = ITEMS.register("hcorp_si_armor_l1",
            () -> new HCorpSiArmor(ArmorEquips.HCORP_SI_ARMOR_L1, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HCORP_SI_ARMOR_L2 = ITEMS.register("hcorp_si_armor_l2",
            () -> new HCorpSiArmor(ArmorEquips.HCORP_SI_ARMOR_L2, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HCORP_SI_ARMOR_L3 = ITEMS.register("hcorp_si_armor_l3",
            () -> new HCorpSiArmor(ArmorEquips.HCORP_SI_ARMOR_L3, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HCORP_SI_ARMOR_L4 = ITEMS.register("hcorp_si_armor_l4",
            () -> new HCorpSiArmor(ArmorEquips.HCORP_SI_ARMOR_L4, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HCORP_SI_ARMOR_L5 = ITEMS.register("hcorp_si_armor_l5",
            () -> new HCorpSiArmor(ArmorEquips.HCORP_SI_ARMOR_L5, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> HCORP_YOU_WEAPON = ITEMS.register("hcorp_you_weapon", HCorpYouWeapon::new);
    public static final RegistryObject<Item> HCORP_YOU_ARMOR_L1 = ITEMS.register("hcorp_you_armor_l1",
            () -> new HCorpYouArmor(ArmorEquips.HCORP_YOU_ARMOR_L1, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HCORP_YOU_ARMOR_L2 = ITEMS.register("hcorp_you_armor_l2",
            () -> new HCorpYouArmor(ArmorEquips.HCORP_YOU_ARMOR_L2, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HCORP_YOU_ARMOR_L3 = ITEMS.register("hcorp_you_armor_l3",
            () -> new HCorpYouArmor(ArmorEquips.HCORP_YOU_ARMOR_L3, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HCORP_YOU_ARMOR_L4 = ITEMS.register("hcorp_you_armor_l4",
            () -> new HCorpYouArmor(ArmorEquips.HCORP_YOU_ARMOR_L4, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> HCORP_YOU_ARMOR_L5 = ITEMS.register("hcorp_you_armor_l5",
            () -> new HCorpYouArmor(ArmorEquips.HCORP_YOU_ARMOR_L5, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    //頭装備
    public static final RegistryObject<Item> HEAD_CLAW = ITEMS.register("head_claw", HeadClaw::new);
}
