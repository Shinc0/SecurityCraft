package net.geforcemods.securitycraft.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.compat.lookingglass.LookingGlassAPIProvider;
import net.geforcemods.securitycraft.misc.CameraShutoffTimer;
import net.geforcemods.securitycraft.misc.CameraView;
import net.minecraft.nbt.NBTTagCompound;


public class TileEntityFrame extends TileEntityOwnable {

	private CameraView cameraView;
	private boolean shouldShowView = false;
	private boolean createdView = false;

	@Override
	public void updateEntity(){
		if(worldObj.isRemote && worldObj.checkChunksExist(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord) && hasCameraLocation() && !SecurityCraft.instance.hasViewForCoords(cameraView.toNBTString()) && !createdView){
			if(worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 0 || createdView) return;

			LookingGlassAPIProvider.createLookingGlassView(worldObj, cameraView.dimension, xCoord, yCoord, zCoord, 192, 192);

			createdView = true;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);

		if(hasCameraLocation())
			tag.setString("cameraLoc", cameraView.toNBTString());
	}

	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);

		if(tag.hasKey("cameraLoc")){
			String[] coords = tag.getString("cameraLoc").split(" ");

			setCameraLocation(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), coords.length == 4 ? Integer.parseInt(coords[3]) : 0);
		}
	}

	public void setCameraLocation(int x, int y, int z, int dimension){
		if(cameraView == null) {
			cameraView = new CameraView(x, y, z, dimension);
			return;
		}

		cameraView.setLocation(x, y, z, dimension);
	}

	public CameraView getCameraView(){
		return cameraView;
	}

	public int getCamDimension(){
		return cameraView.dimension;
	}

	public boolean hasCameraLocation(){
		return cameraView != null;
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldShowView(){
		return SecurityCraft.config.fiveMinAutoShutoff ? shouldShowView : true;
	}

	@SideOnly(Side.CLIENT)
	public void enableView(){
		shouldShowView = true;

		if(SecurityCraft.config.fiveMinAutoShutoff){
			if(!SecurityCraft.instance.hasViewForCoords(cameraView.toNBTString()))
				LookingGlassAPIProvider.createLookingGlassView(worldObj, cameraView.dimension, cameraView.x, cameraView.y, cameraView.z, 192, 192);

			new CameraShutoffTimer(this);
		}
	}

	@SideOnly(Side.CLIENT)
	public void disableView(){
		if(SecurityCraft.config.fiveMinAutoShutoff && SecurityCraft.instance.hasViewForCoords(cameraView.toNBTString())){
			SecurityCraft.instance.getLGPanelRenderer().getApi().cleanupWorldView(SecurityCraft.instance.getViewFromCoords(cameraView.toNBTString()).getView());
			SecurityCraft.instance.removeViewForCoords(cameraView.toNBTString());
		}

		shouldShowView = false;
	}

}
