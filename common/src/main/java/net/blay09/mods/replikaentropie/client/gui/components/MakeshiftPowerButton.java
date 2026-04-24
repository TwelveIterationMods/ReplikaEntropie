package net.blay09.mods.replikaentropie.client.gui.components;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.replikaentropie.network.protocol.MakeshiftPowerMessage;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class MakeshiftPowerButton extends ImageButton {
    private static final WidgetSprites SPRITES = new WidgetSprites(id("makeshift_psu_button"), id("makeshift_psu_button_highlighted"));
    private static final Component MESSAGE = Component.translatable("gui.replikaentropie.makeshift_psu");

    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;

    private int soundCounter;

    public MakeshiftPowerButton(int x, int y, int containerId) {
        super(x, y, WIDTH, HEIGHT, SPRITES, _ -> Balm.networking().sendToServer(new MakeshiftPowerMessage(containerId)), MESSAGE);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        soundManager.play(SimpleSoundInstance.forUI(soundCounter % 2 == 0 ? SoundEvents.PISTON_EXTEND : SoundEvents.PISTON_CONTRACT, (float) (0.8f + Math.random() * 0.4f)));
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.FLINTANDSTEEL_USE, (float) (0.8f + Math.random() * 0.4f)));
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.REDSTONE_TORCH_BURNOUT, 1f + (float) (0.8f + Math.random() * 0.4f)));
        soundCounter++;
    }
}
