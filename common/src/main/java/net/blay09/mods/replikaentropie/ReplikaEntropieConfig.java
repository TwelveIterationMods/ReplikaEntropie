package net.blay09.mods.replikaentropie;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.config.reflection.Comment;
import net.blay09.mods.balm.api.config.reflection.Config;
import net.blay09.mods.balm.api.config.reflection.NestedType;

import java.util.List;

@Config(ReplikaEntropie.MOD_ID)
public class ReplikaEntropieConfig {

    @Comment("This is an example int property")
    public int exampleInt = 1234;

    public static ReplikaEntropieConfig getActive() {
        return Balm.getConfig().getActiveConfig(ReplikaEntropieConfig.class);
    }
}
