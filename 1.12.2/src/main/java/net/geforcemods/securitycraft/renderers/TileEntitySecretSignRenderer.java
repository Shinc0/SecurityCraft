package net.geforcemods.securitycraft.renderers;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntitySecretSignRenderer extends TileEntitySpecialRenderer<TileEntitySecretSign>
{
	private static final ResourceLocation SIGN_TEXTURE = new ResourceLocation("textures/entity/sign.png");
	private static final ModelSign model = new ModelSign();

	@Override
	public void render(TileEntitySecretSign te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
	{
		Block block = te.getBlockType();
		GlStateManager.pushMatrix();

		if (block == SCContent.secretSignStanding)
		{
			GlStateManager.translate((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
			float rotation = te.getBlockMetadata() * 360 / 16.0F;
			GlStateManager.rotate(-rotation, 0.0F, 1.0F, 0.0F);
			model.signStick.showModel = true;
		}
		else
		{
			int meta = te.getBlockMetadata();
			float roation = 0.0F;

			if (meta == 2)
			{
				roation = 180.0F;
			}

			if (meta == 4)
			{
				roation = 90.0F;
			}

			if (meta == 5)
			{
				roation = -90.0F;
			}

			GlStateManager.translate((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
			GlStateManager.rotate(-roation, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
			model.signStick.showModel = false;
		}

		if (destroyStage >= 0)
		{
			bindTexture(DESTROY_STAGES[destroyStage]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(4.0F, 2.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		}
		else
		{
			bindTexture(SIGN_TEXTURE);
		}

		GlStateManager.enableRescaleNormal();
		GlStateManager.pushMatrix();
		GlStateManager.scale(0.6666667F, -0.6666667F, -0.6666667F);
		model.renderSign();
		GlStateManager.popMatrix();

		if(te.getOwner().isOwner(Minecraft.getMinecraft().player))
		{
			FontRenderer fontrenderer = getFontRenderer();
			GlStateManager.translate(0.0F, 0.33333334F, 0.046666667F);
			GlStateManager.scale(0.010416667F, -0.010416667F, 0.010416667F);
			GlStateManager.glNormal3f(0.0F, 0.0F, -0.010416667F);
			GlStateManager.depthMask(false);

			if (destroyStage < 0)
			{
				for (int j = 0; j < te.signText.length; ++j)
				{
					if (te.signText[j] != null)
					{
						ITextComponent text = te.signText[j];
						List<ITextComponent> textList = GuiUtilRenderComponents.splitText(text, 90, fontrenderer, false, true);
						String line = textList != null && !textList.isEmpty() ? textList.get(0).getFormattedText() : "";

						if (j == te.lineBeingEdited)
						{
							line = "> " + line + " <";
							fontrenderer.drawString(line, -fontrenderer.getStringWidth(line) / 2, j * 10 - te.signText.length * 5, 0);
						}
						else
						{
							fontrenderer.drawString(line, -fontrenderer.getStringWidth(line) / 2, j * 10 - te.signText.length * 5, 0);
						}
					}
				}
			}

			GlStateManager.depthMask(true);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		}

		GlStateManager.popMatrix();

		if (destroyStage >= 0)
		{
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	}
}