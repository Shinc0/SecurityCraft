package net.geforcemods.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketUpdateLogger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityLogger extends TileEntityOwnable {

	public String[] players = new String[100];

	@Override
	public boolean attackEntity(Entity entity) {
		if (!worldObj.isRemote) {
			addPlayerName(((EntityPlayer) entity).getCommandSenderName());
			sendChangeToClient();
		}

		return true;
	}

	@Override
	public boolean canAttack() {
		return worldObj.isBlockIndirectlyGettingPowered(pos) > 0;
	}

	public void logPlayers(){
		double radius = SecurityCraft.config.usernameLoggerSearchRadius;

		AxisAlignedBB area = AxisAlignedBB.fromBounds(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).expand(radius, radius, radius);
		List<?> playersInArea = worldObj.getEntitiesWithinAABB(EntityPlayer.class, area);
		Iterator<?> iterator = playersInArea.iterator();

		while(iterator.hasNext())
			addPlayerName(((EntityPlayer)iterator.next()).getCommandSenderName());

		sendChangeToClient();
	}

	private void addPlayerName(String username) {
		if(!hasPlayerName(username))
			for(int i = 0; i < players.length; i++)
				if(players[i] == "" || players[i] == null){
					players[i] = username;
					break;
				}
				else
					continue;
	}

	private boolean hasPlayerName(String username) {
		for(int i = 0; i < players.length; i++)
			if(players[i] == username)
				return true;
			else
				continue;

		return false;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);

		for(int i = 0; i < players.length; i++)
			if(players[i] != null)
				tag.setString("player" + i, players[i]);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);

		for(int i = 0; i < players.length; i++)
			if (tag.hasKey("player" + i))
				players[i] = tag.getString("player" + i);
	}

	public void sendChangeToClient(){
		for(int i = 0; i < players.length; i++)
			if(players[i] != null)
				SecurityCraft.network.sendToAll(new PacketUpdateLogger(pos.getX(), pos.getY(), pos.getZ(), i, players[i]));
	}

}
