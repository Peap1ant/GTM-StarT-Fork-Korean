package com.gregtechceu.gtceu.integration.jade.provider;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.research.NetworkSwitchMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class NetworkSwitchProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    @Override
    public ResourceLocation getUid() {
        return GTCEu.id("network_switch");
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (blockAccessor.getBlockEntity() instanceof IMachineBlockEntity blockEntity) {
            MetaMachine machine = blockEntity.getMetaMachine();
            if (machine instanceof NetworkSwitchMachine) {
                long energyUsage = blockAccessor.getServerData().getLong("energyUsage");
                String energyFormatted = FormattingUtil.formatNumbers(energyUsage);
                int receiversCount = blockAccessor.getServerData().getInt("receiversCount");
                int transmittersCount = blockAccessor.getServerData().getInt("transmittersCount");
                int cwut = blockAccessor.getServerData().getInt("cwut");
                // wrap in text component to keep it from being formatted
                Component voltageName = Component.literal(GTValues.VNF[GTUtil.getTierByVoltage(energyUsage)]);
                Component text = Component.translatable(
                        "gtceu.multiblock.energy_consumption",
                        energyFormatted,
                        voltageName);
                Component receivers = Component.translatable("gtceu.multiblock.network_switch.receivers",
                        receiversCount);
                Component transmitters = Component.translatable("gtceu.multiblock.network_switch.transmitters",
                        transmittersCount);
                Component cwutText = Component.translatable("gtceu.multiblock.computation.max", cwut)
                        .withStyle(ChatFormatting.GRAY);
                iTooltip.add(text);
                iTooltip.add(receivers);
                iTooltip.add(transmitters);
                iTooltip.add(cwutText);
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof IMachineBlockEntity blockEntity) {
            MetaMachine machine = blockEntity.getMetaMachine();
            if (machine instanceof NetworkSwitchMachine networkSwitch) {
                compoundTag.putLong("energyUsage", networkSwitch.getEnergyUsage());
                compoundTag.putInt("receiversCount", networkSwitch.getReceiversCount());
                compoundTag.putInt("transmittersCount", networkSwitch.getTransmittersCount());
                compoundTag.putInt("cwut", networkSwitch.getMaxCWUtForDisplay());
            }
        }
    }
}
