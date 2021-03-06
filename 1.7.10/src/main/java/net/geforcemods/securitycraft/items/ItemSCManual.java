package net.geforcemods.securitycraft.items;

import cpw.mods.fml.common.FMLCommonHandler;
import net.geforcemods.securitycraft.gui.GuiSCManual;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;

public class ItemSCManual extends Item {

	public ItemSCManual(){
		super();
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(world.isRemote)
			FMLCommonHandler.instance().showGuiScreen(new GuiSCManual());

		return stack;
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int par4, boolean update){
		if(stack.getTagCompound() == null){
			NBTTagList bookPages = new NBTTagList();

			stack.setTagInfo("pages", bookPages);
			stack.setTagInfo("author", new NBTTagString("Geforce"));
			stack.setTagInfo("title", new NBTTagString("SecurityCraft"));
		}
	}

}
