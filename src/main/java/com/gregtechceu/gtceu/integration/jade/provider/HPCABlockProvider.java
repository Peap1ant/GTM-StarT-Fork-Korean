package com.gregtechceu.gtceu.integration.jade.provider;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.research.DataBankMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.research.HPCAMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class HPCABlockProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    @Override
    public ResourceLocation getUid() {
        return GTCEu.id("hpca");
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (blockAccessor.getBlockEntity() instanceof IMachineBlockEntity blockEntity) {
            MetaMachine machine = blockEntity.getMetaMachine();
            if (machine instanceof HPCAMachine hpca) {
                long energyUsage = blockAccessor.getServerData().getLong("energyUsage");
                String energyFormatted = FormattingUtil.formatNumbers(energyUsage);
                // wrap in text component to keep it from being formatted
                Component voltageName = Component.literal(GTValues.VNF[GTUtil.getTierByVoltage(energyUsage)]);
                Component voltageText = Component.translatable(
                        "gtceu.multiblock.energy_consumption",
                        energyFormatted,
                        voltageName);
                Component cwutInfo = hpca.getCWUtProductionComponent();
                Component coolingInfo = hpca.getCoolingComponent();
                Component coolingAvailableInfo = hpca.getCoolingAvailableComponent();
                Component coolantRequiredInfo = hpca.getCoolantRequiredComponent();
                Component bridgingInfo = hpca.getBridgingComponent();

                iTooltip.add(voltageText);
                iTooltip.add(cwutInfo);
                iTooltip.add(coolingInfo);
                iTooltip.add(coolingAvailableInfo);
                iTooltip.add(coolantRequiredInfo);
                iTooltip.add(bridgingInfo);
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof IMachineBlockEntity blockEntity) {
            MetaMachine machine = blockEntity.getMetaMachine();
            if (machine instanceof DataBankMachine dataBank) {
                compoundTag.putLong("energyUsage", dataBank.getEnergyUsage());
            }
        }
    }
}
