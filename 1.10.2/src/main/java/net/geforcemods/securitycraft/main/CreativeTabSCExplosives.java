package net.geforcemods.securitycraft.main;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabSCExplosives extends CreativeTabs{
	
	public CreativeTabSCExplosives(){
		super(getNextID(), "tabSecurityCraft");
	}

	
	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem(){
		return Item.getItemFromBlock(mod_SecurityCraft.mine);
	}
	
	@Override
	public String getTranslatedTabLabel(){
		return "SecurityCraft: " + I18n.translateToLocal("creativeTabExplosives");
		
	}

}
