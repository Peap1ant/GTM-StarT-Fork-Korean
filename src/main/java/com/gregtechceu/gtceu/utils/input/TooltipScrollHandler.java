package com.gregtechceu.gtceu.utils.input;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import com.mojang.blaze3d.platform.InputConstants;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TooltipScrollHandler {

    private static @Nullable MachineBuilder<?> CURRENT_TOOLTIP_MACHINE = null;
    private static int CURRENT_TOOLTIP_PAGE = 0;
    private static int CURRENT_TOOLTIP_MODIFIER = 0;

    public static int getCurrentTooltipPage() {
        return CURRENT_TOOLTIP_PAGE;
    }

    public static int getCurrentTooltipModifier() {
        return CURRENT_TOOLTIP_MODIFIER;
    }

    public static void setCurrentTooltipMachine(MachineBuilder<?> builder) {
        if (CURRENT_TOOLTIP_MACHINE != null && CURRENT_TOOLTIP_MACHINE.id.equals(builder.id)) return;

        CURRENT_TOOLTIP_MACHINE = builder;
        CURRENT_TOOLTIP_PAGE = 0;
        CURRENT_TOOLTIP_MODIFIER = 0;
    }

    public static <T extends ScreenEvent> void onTooltipNext(T event) {
        if (CURRENT_TOOLTIP_MACHINE == null) return;

        if (GTUtil.isShiftDown()) {
            CURRENT_TOOLTIP_MODIFIER++;
        } else {
            CURRENT_TOOLTIP_PAGE++;
        }
        event.setCanceled(true);
    }

    public static <T extends ScreenEvent> void onTooltipPrev(T event) {
        if (CURRENT_TOOLTIP_MACHINE == null) return;

        if (GTUtil.isShiftDown()) {
            CURRENT_TOOLTIP_MODIFIER--;
        } else {
            CURRENT_TOOLTIP_PAGE--;
        }
        event.setCanceled(true);
    }

    public static void onTooltipEvent(ItemTooltipEvent event) {
        if (CURRENT_TOOLTIP_MACHINE == null) return;

        if (event.getItemStack().getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof MetaMachineBlock metaMachineBlock) {
                if (metaMachineBlock.getDefinition() == CURRENT_TOOLTIP_MACHINE.get()) {
                    return;
                }
            }
        }

        CURRENT_TOOLTIP_MACHINE = null;
        CURRENT_TOOLTIP_PAGE = 0;
        CURRENT_TOOLTIP_MODIFIER = 0;
    }

    public static KeyModifier getRequiredModifierForPageScroll(SyncedKeyMapping mapping) {
        if (mapping.getType() == InputConstants.Type.MOUSE &&
                (mapping.getKeyCode() == SyncedKeyMappings.KEY_MOUSEWHEEL_UP ||
                        mapping.getKeyCode() == SyncedKeyMappings.KEY_MOUSEWHEEL_DOWN)) {
            return KeyModifier.CONTROL;
        } else {
            return KeyModifier.NONE;
        }
    }
}
