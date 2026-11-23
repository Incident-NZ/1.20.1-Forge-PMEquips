package net.pm_equips.client.renderer;

import net.pm_equips.client.models.KCorpAgentArmorM;
import net.pm_equips.items.WingKCorpArmor2;

public class KCorpAgentArmorR extends FullBodyArmorRenderer<WingKCorpArmor2> {
    public KCorpAgentArmorR() {
        super(new KCorpAgentArmorM());
    }
}
