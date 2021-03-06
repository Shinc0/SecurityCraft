package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.tileentity.TileEntityIMS;
import net.geforcemods.securitycraft.tileentity.TileEntityIMS.EnumIMSTargetingMode;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiIMS extends GuiContainer{

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");

	private TileEntityIMS tileEntity;
	private GuiButton targetButton;
	private int targetingOptionIndex = 0;

	public GuiIMS(InventoryPlayer inventory, TileEntityIMS te) {
		super(new ContainerGeneric(inventory, te));
		tileEntity = te;
		targetingOptionIndex = tileEntity.getTargetingOption().modeIndex;
	}

	@Override
	public void initGui(){
		super.initGui();

		buttonList.add(targetButton = new GuiButton(0, width / 2 - 38, height / 2 - 58, 120, 20, tileEntity.getTargetingOption() == EnumIMSTargetingMode.PLAYERS_AND_MOBS ? StatCollector.translateToLocal("gui.securitycraft:ims.hostileAndPlayers") : StatCollector.translateToLocal("tooltip.securitycraft:module.players")));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks){
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		fontRendererObj.drawString(StatCollector.translateToLocal("tile.securitycraft:ims.name"), xSize / 2 - fontRendererObj.getStringWidth(StatCollector.translateToLocal("tile.securitycraft:ims.name")) / 2, 6, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("gui.securitycraft:ims.target"), xSize / 2 - 78, 30, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	protected void actionPerformed(GuiButton button){
		switch(button.id){
			case 0:
				targetingOptionIndex++;

				if(targetingOptionIndex > 1)
					targetingOptionIndex = 0;

				tileEntity.setTargetingOption(EnumIMSTargetingMode.values()[targetingOptionIndex]);

				ClientUtils.syncTileEntity(tileEntity);

				updateButtonText();
		}
	}

	private void updateButtonText() {
		if(EnumIMSTargetingMode.values()[targetingOptionIndex] == EnumIMSTargetingMode.PLAYERS)
			targetButton.displayString = StatCollector.translateToLocal("tooltip.securitycraft:module.playerCustomization.players");
		else if(EnumIMSTargetingMode.values()[targetingOptionIndex] == EnumIMSTargetingMode.PLAYERS_AND_MOBS)
			targetButton.displayString = StatCollector.translateToLocal("gui.securitycraft:ims.hostileAndPlayers");
	}

}
