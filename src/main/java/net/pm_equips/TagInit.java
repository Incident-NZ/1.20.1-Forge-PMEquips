package net.pm_equips;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class TagInit {
    public static class Items {
        public static final TagKey<Item> BULLET = createTag("bullet");
        public static final TagKey<Item> E_BOX = createTag("e_box");
        public static final TagKey<Item> EGO_T1_ZAYIN = createTag("ego_t1_zayin");
        public static final TagKey<Item> EGO_T2_TETH = createTag("ego_t2_teth");
        public static final TagKey<Item> EGO_T3_HE = createTag("ego_t3_he");
        public static final TagKey<Item> EGO_T4_WAW = createTag("ego_t4_waw");
        public static final TagKey<Item> EGO_T5_ALEPH = createTag("ego_t5_aleph");
        public static final TagKey<Item> WINGS_MATERIAL = createTag("wings_material");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(PMEquipsMain.MOD_ID, name));
        }
    }
}
