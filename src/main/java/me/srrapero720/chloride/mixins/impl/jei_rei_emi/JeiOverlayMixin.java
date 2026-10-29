package me.srrapero720.chloride.mixins.impl.jei_rei_emi;

import me.srrapero720.chloride.ChlorideConfig;
import mezz.jei.gui.input.GuiTextFieldFilter;
import mezz.jei.gui.overlay.IngredientListOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Targets JEI 15.74: gate the list itself so its background, contents, tooltips,
// and input handlers agree about visibility. Leave JEI's search, config button,
// and lookup history rendering and input handling intact.
@Mixin(value = IngredientListOverlay.class, remap = false)
@Pseudo
public class JeiOverlayMixin {
    @Shadow @Final private GuiTextFieldFilter searchField;

    @Shadow
    private boolean isSearchDisplayed() {
        throw new AssertionError();
    }

    @Inject(method = "isListDisplayed()Z", at = @At("RETURN"), cancellable = true, require = 1)
    private void chloride$hideListWithoutSearch(final CallbackInfoReturnable<Boolean> cir) {
        if (ChlorideConfig.ui.hideJREMI && this.searchField.getValue().isEmpty()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "drawForeground", at = @At("TAIL"), require = 1)
    private void chloride$drawSearchHint(final Minecraft minecraft, final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTicks, final CallbackInfo ci) {
        if (!ChlorideConfig.ui.hideJREMI || ChlorideConfig.ui.hideJREMIHint) return;
        if (!this.searchField.getValue().isEmpty() || !this.isSearchDisplayed()) return;

        guiGraphics.drawCenteredString(minecraft.font, Component.translatable("chloride.jei.message"), this.searchField.getX() + (this.searchField.getWidth() / 2), guiGraphics.guiHeight() / 2, 0xFFFFFF);
    }
}
