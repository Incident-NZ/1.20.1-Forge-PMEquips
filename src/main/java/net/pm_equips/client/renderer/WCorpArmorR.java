package net.pm_equips.client.renderer;

import net.pm_equips.client.models.WCorpArmorM;
import net.pm_equips.items.WingWCorpArmor;

public class WCorpArmorR extends FullBodyArmorRenderer<WingWCorpArmor> {
    public WCorpArmorR() {
        super(new WCorpArmorM());
    }
}
