package igentuman.bfr.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import igentuman.bfr.client.gui.element.GuiReactorTab;
import igentuman.bfr.common.tile.reactor.TileEntityReactorController;
import mekanism.api.TileNetworkList;
import mekanism.client.gui.element.GuiEnergyInfo;
import mekanism.client.gui.element.GuiProgress;
import mekanism.client.gui.element.GuiProgress.IProgressInfoHandler;
import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.gauge.GuiGauge.Type;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.Mekanism;
import mekanism.common.inventory.container.ContainerNull;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiReactorFuel extends GuiReactorInfo {

    private GuiTextField injectionRateField;

    public GuiReactorFuel(InventoryPlayer inventory, final TileEntityReactorController tile) {
        super(tile, new ContainerNull(inventory.player, tile));
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiEnergyInfo(() -> tileEntity.isFormed() ? Arrays.asList(
              LangUtils.localize("gui.storing") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getEnergy(), tileEntity.getMaxEnergy()),
              LangUtils.localize("gui.producing") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getReactor().getPassiveGeneration(false, true)) + "/t")
                                                                    : new ArrayList<>(), this, resource));
        addGuiElement(new GuiGasGauge(() -> tileEntity.deuteriumTank, Type.SMALL_RED, this, resource, 25, 64));
        addGuiElement(new GuiGasGauge(() -> tileEntity.fuelTank, Type.STANDARD, this, resource, 79, 50));
        addGuiElement(new GuiGasGauge(() -> tileEntity.tritiumTank, Type.SMALL_RED, this, resource, 133, 64));
        addGuiElement(new GuiProgress(new IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return tileEntity.getActive() ? 1 : 0;
            }
        }, ProgressBar.SMALL_RIGHT, this, resource, 45, 75));
        addGuiElement(new GuiProgress(new IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return tileEntity.getActive() ? 1 : 0;
            }
        }, ProgressBar.SMALL_LEFT, this, resource, 99, 75));
        addGuiElement(new GuiReactorTab(this, tileEntity, GuiReactorTab.ReactorTab.HEAT, resource));
        addGuiElement(new GuiReactorTab(this, tileEntity, GuiReactorTab.ReactorTab.STAT, resource));
        addGuiElement(new GuiReactorTab(this, tileEntity, GuiReactorTab.ReactorTab.EFFICIENCY, resource));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRenderer.drawString(tileEntity.getName(), 46, 6, 0x404040);
        String str = LangUtils.localize("gui.reactor.injectionRate") + ": " + (tileEntity.getReactor() == null ? "None" : tileEntity.getReactor().getInjectionRate());
        fontRenderer.drawString(str, (xSize / 2) - (fontRenderer.getStringWidth(str) / 2), 35, 0x404040);
        fontRenderer.drawString("Edit Rate" + ":", 50, 117, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
        super.drawGuiContainerBackgroundLayer(xAxis, yAxis);
        injectionRateField.drawTextBox();
        MekanismRenderer.resetColor();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        injectionRateField.updateCursorCounter();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        injectionRateField.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void keyTyped(char c, int i) throws IOException {
        if (!injectionRateField.isFocused() || i == Keyboard.KEY_ESCAPE) {
            super.keyTyped(c, i);
        }
        if (i == Keyboard.KEY_RETURN && injectionRateField.isFocused()) {
            setInjection();
        }
        if (Character.isDigit(c) || isTextboxKey(c, i)) {
            injectionRateField.textboxKeyTyped(c, i);
        }
    }

    private void setInjection() {
        if (!injectionRateField.getText().isEmpty()) {
            int toUse = Math.max(0, Integer.parseInt(injectionRateField.getText()));
            toUse -= toUse % 2;
            TileNetworkList data = TileNetworkList.withContents(0, toUse);
            Mekanism.packetHandler.sendToServer(new TileEntityMessage(tileEntity, data));
            injectionRateField.setText("");
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        String prevRad = injectionRateField != null ? injectionRateField.getText() : "";
        injectionRateField = new GuiTextField(0, fontRenderer, guiLeft + 98, guiTop + 115, 26, 11);
        injectionRateField.setMaxStringLength(4);
        injectionRateField.setText(prevRad);
    }
}