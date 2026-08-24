package dev.h2o_cl.spearfix.client;

import dev.h2o_cl.spearfix.SpearfixConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Plain-vanilla config screen: one cycling button per option plus Done.
 * Strings come from assets/spearfix/lang, so the game's selected language
 * (which follows the player's region on first launch) picks the locale.
 */
public class SpearfixConfigScreen extends Screen {
   private static final int BUTTON_WIDTH = 220;
   private final Screen parent;

   public SpearfixConfigScreen(Screen parent) {
      super(Component.translatable("spearfix.config.title"));
      this.parent = parent;
   }

   @Override
   protected void init() {
      int centerX = this.width / 2 - BUTTON_WIDTH / 2;
      int y = this.height / 2 - 46;

      this.addRenderableWidget(
         Button.builder(rewindLabel(), button -> {
            SpearfixConfig.setRewindTicks((SpearfixConfig.rewindTicks() + 1) % 5);
            button.setMessage(rewindLabel());
         }).bounds(centerX, y, BUTTON_WIDTH, 20).build()
      );

      this.addRenderableWidget(
         Button.builder(refundLabel(), button -> {
            SpearfixConfig.setRefundMissedCooldown(!SpearfixConfig.refundMissedCooldown());
            button.setMessage(refundLabel());
         }).bounds(centerX, y + 24, BUTTON_WIDTH, 20).build()
      );

      this.addRenderableWidget(
         Button.builder(Component.translatable("gui.done"), button -> this.onClose())
            .bounds(centerX, y + 56, BUTTON_WIDTH, 20).build()
      );
   }

   private Component rewindLabel() {
      return Component.translatable("spearfix.config.rewindTicks", SpearfixConfig.rewindTicks());
   }

   private Component refundLabel() {
      Component state = Component.translatable(SpearfixConfig.refundMissedCooldown() ? "spearfix.config.on" : "spearfix.config.off");
      return Component.translatable("spearfix.config.refundMissedCooldown", state);
   }

   @Override
   public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
      super.extractRenderState(graphics, mouseX, mouseY, delta);
      graphics.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
      graphics.centeredText(
         this.font, Component.translatable("spearfix.config.note"), this.width / 2, this.height / 2 + 34, 0x808080
      );
   }

   @Override
   public void onClose() {
      if (this.minecraft != null) {
         this.minecraft.setScreenAndShow(this.parent);
      }
   }
}
