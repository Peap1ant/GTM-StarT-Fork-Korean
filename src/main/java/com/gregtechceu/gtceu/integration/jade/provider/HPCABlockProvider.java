package com.gregtechceu.gtceu.integration.jade.provider;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.research.DataBankMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.research.HPCAMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
                int CWUt = blockAccessor.getServerData().getInt("CWUt");
                int numBridges = blockAccessor.getServerData().getInt("numBridges");
                int maxCoolingDemand = blockAccessor.getServerData().getInt("maxCoolingDemand");
                int maxCoolingAmount = blockAccessor.getServerData().getInt("maxCoolingAmount");

                Component voltageText = getEnergyUsage(energyUsage);
                Component cwutInfo = getCWUtProductionComponent(CWUt);
                Component coolingInfo = getCoolingComponent(maxCoolingAmount, maxCoolingDemand);
                Component coolingAvailableInfo = getCoolingAvailableComponent(maxCoolingAmount, maxCoolingDemand);
                Component coolantRequiredInfo = getCoolantRequiredComponent(maxCoolingDemand);
                Component bridgingInfo = getBridgingComponent(numBridges);

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
            if (machine instanceof HPCAMachine hpca) {
                compoundTag.putLong("energyUsage", hpca.getEnergyUsage());
                compoundTag.putInt("CWUt", hpca.getMaxCWUt());
                compoundTag.putInt("numBridges", hpca.getNumBridges());
                compoundTag.putInt("maxCoolingDemand", hpca.getMaxCoolingDemand());
                compoundTag.putInt("maxCoolingAmount", hpca.getMaxCoolingAmount());
            }
        }
    }

    private MutableComponent getEnergyUsage(long energyUsage) {
        String energyFormatted = FormattingUtil.formatNumbers(energyUsage);
        Component voltageName = Component.literal(GTValues.VNF[GTUtil.getTierByVoltage(energyUsage)]);
        return Component.translatable(
                "gtceu.multiblock.energy_consumption",
                energyFormatted,
                voltageName);
    }

    private MutableComponent getCWUtProductionComponent(int CWUt) {
        MutableComponent data = Component.literal(Integer.toString(CWUt)).withStyle(ChatFormatting.AQUA);
        return Component.translatable("gtceu.multiblock.hpca.info_max_computation", data)
                .withStyle(ChatFormatting.GRAY);
    }

    private ChatFormatting getCoolingColor(int maxCoolingAmount, int maxCoolingDemand) {
        return maxCoolingAmount < maxCoolingDemand ? ChatFormatting.RED : ChatFormatting.GREEN;
    }

    private MutableComponent getCoolingComponent(int maxCoolingAmount, int maxCoolingDemand) {
        MutableComponent data = Component.literal(Integer.toString(maxCoolingDemand))
                .withStyle(getCoolingColor(maxCoolingAmount, maxCoolingDemand));
        return Component.translatable("gtceu.multiblock.hpca.info_max_cooling_demand", data)
                .withStyle(ChatFormatting.GRAY);
    }

    private MutableComponent getCoolingAvailableComponent(int maxCoolingAmount, int maxCoolingDemand) {
        MutableComponent data = Component.literal(Integer.toString(maxCoolingAmount))
                .withStyle(getCoolingColor(maxCoolingAmount, maxCoolingDemand));
        return Component.translatable("gtceu.multiblock.hpca.info_max_cooling_available", data)
                .withStyle(ChatFormatting.GRAY);
    }

    private MutableComponent getCoolantRequiredComponent(int maxCoolantDemand) {
        MutableComponent data;
        if (maxCoolantDemand > 0) {
            data = Component.translatable("gtceu.universal.liters", maxCoolantDemand)
                    .withStyle(ChatFormatting.YELLOW).append(" ");
            Component coolantName = Component.translatable("gtceu.multiblock.hpca.info_coolant_name")
                    .withStyle(ChatFormatting.YELLOW);
            data.append(coolantName);
        } else {
            data = Component.literal("0").withStyle(ChatFormatting.GREEN);
        }
        return Component.translatable("gtceu.multiblock.hpca.info_max_coolant_required", data)
                .withStyle(ChatFormatting.GRAY);
    }

    private MutableComponent getBridgingComponent(int numBridges) {
        if (numBridges > 0) {
            return Component.translatable("gtceu.multiblock.hpca.info_bridging_enabled")
                    .withStyle(ChatFormatting.GREEN);
        } else {
            return Component.translatable("gtceu.multiblock.hpca.info_bridging_disabled")
                    .withStyle(ChatFormatting.RED);
        }
    }
}
