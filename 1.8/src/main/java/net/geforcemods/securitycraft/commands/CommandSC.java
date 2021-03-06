package net.geforcemods.securitycraft.commands;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCEventHandler;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.ForgeHooks;

public class CommandSC extends CommandBase implements ICommand{

	private List<String> nicknames;

	public CommandSC(){
		nicknames = new ArrayList<String>();
		nicknames.add("sc");
	}

	/**
	 * Return the required permission level for this command.
	 */
	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}

	@Override
	public String getCommandName() {
		return "sc";
	}

	public List<String> getAliases() {
		return nicknames;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return StatCollector.translateToLocal("messages.securitycraft:command.sc.usage");
	}

	public boolean canCommandSenderUse(ICommandSender sender) {
		return true;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		if(args.length == 0)
			throw new WrongUsageException(StatCollector.translateToLocal("messages.securitycraft:command.sc.usage"));
		else if(args.length == 1){
			if(args[0].matches("connect"))
				sender.addChatMessage(new ChatComponentText("[" + EnumChatFormatting.GREEN + "IRC" + EnumChatFormatting.WHITE + "] " + StatCollector.translateToLocal("messages.securitycraft:irc.connected") + " ").appendSibling(ForgeHooks.newChatWithLinks(SCEventHandler.tipsWithLink.get("discord"))));
			else if(args[0].matches("help"))
				getCommandSenderAsPlayer(sender).inventory.addItemStackToInventory(new ItemStack(SCContent.scManual));
			else if(args[0].matches("bug"))
				PlayerUtils.sendMessageEndingWithLink(sender, "SecurityCraft", StatCollector.translateToLocal("messages.securitycraft:bugReport"), "http://goo.gl/forms/kfRpvvQzfl", EnumChatFormatting.GOLD);
			else
				throw new WrongUsageException(StatCollector.translateToLocal("messages.securitycraft:command.sc.usage"));
		}else if(args.length >= 2){
			if(args[0].matches("bug"))
				PlayerUtils.sendMessageEndingWithLink(sender, "SecurityCraft", StatCollector.translateToLocal("messages.securitycraft:bugReport"), "http://goo.gl/forms/kfRpvvQzfl", EnumChatFormatting.GOLD);
			else
				throw new WrongUsageException(StatCollector.translateToLocal("messages.securitycraft:command.sc.usage"));
		}
		else
			throw new WrongUsageException(StatCollector.translateToLocal("messages.securitycraft:command.sc.usage"));
	}

	@Override
	public int compareTo(Object obj)
	{
		return this.compareTo((ICommand)obj);
	}
}
