package net.blay09.mods.replikaentropie.client.gui.components;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.replikaentropie.network.protocol.MakeshiftPowerMessage;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import static net.blay09.mods.replikaentropie.ReplikaEntropie.id;

public class MakeshiftPowerButton extends ImageButton {
    private static final WidgetSprites SPRITES = new WidgetSprites(id("makeshift_psu_button"), id("makeshift_psu_button_highlighted"));
    private static final WidgetSprites OVERHEATED_SPRITES = new WidgetSprites(id("makeshift_psu_button_overheated"));
    private static final Component MESSAGE = Component.translatable("gui.replikaentropie.makeshift_psu");
    private static final long QUICK_PRESS_THRESHOLD_MS = 350;
    private static final long OVERHEAT_DURATION_MS = 3000;

    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;

    private final int containerId;
    private int soundCounter;
    private long lastPowerPressMillis = -1;
    private long overheatedUntilMillis = -1;

    public MakeshiftPowerButton(int x, int y, int containerId) {
        super(x, y, WIDTH, HEIGHT, SPRITES, _ -> {
        }, MESSAGE);
        this.containerId = containerId;
        setTooltip(Tooltip.create(MESSAGE));
    }

    @Override
    public void onPress(InputWithModifiers input) {
        if (!isOverheated()) {
            Balm.networking().sendToServer(new MakeshiftPowerMessage(containerId));
        }
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        final var now = System.currentTimeMillis();
        if (now < overheatedUntilMillis || lastPowerPressMillis != -1 && now - lastPowerPressMillis < QUICK_PRESS_THRESHOLD_MS) {
            overheatedUntilMillis = Math.max(overheatedUntilMillis, now + OVERHEAT_DURATION_MS);
            soundManager.play(SimpleSoundInstance.forUI(SoundEvents.FIRE_EXTINGUISH, 0.9f + (float) (Math.random() * 0.2f), 0.35f));
            return;
        }

        lastPowerPressMillis = now;
        soundManager.play(SimpleSoundInstance.forUI(soundCounter % 2 == 0 ? SoundEvents.PISTON_EXTEND : SoundEvents.PISTON_CONTRACT, (float) (0.8f + Math.random() * 0.4f)));
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.FLINTANDSTEEL_USE, (float) (0.8f + Math.random() * 0.4f)));
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.REDSTONE_TORCH_BURNOUT, 1f + (float) (0.8f + Math.random() * 0.4f)));
        soundCounter++;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        final var sprites = isOverheated() ? OVERHEATED_SPRITES : SPRITES;
        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), width, height);
    }

    private boolean isOverheated() {
        return System.currentTimeMillis() < overheatedUntilMillis;
    }
}
