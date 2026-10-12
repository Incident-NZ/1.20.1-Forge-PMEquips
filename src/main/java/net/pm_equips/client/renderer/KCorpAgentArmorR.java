package net.pm_equips.client.renderer;

import net.pm_equips.client.models.KCorpAgentArmorM;
import net.pm_equips.items.WingKCorpAgentArmor;

public class KCorpAgentArmorR extends FullBodyArmorRenderer<WingKCorpAgentArmor> {
    public KCorpAgentArmorR() {
        super(new KCorpAgentArmorM());
    }
}
