package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLogger extends BlockContainer {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	public BlockLogger(Material material) {
		super(material);
	}

	@Override
	public int getRenderType(){
		return 3;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		if(world.isRemote)
			return true;
		else{
			player.openGui(SecurityCraft.instance, GuiHandler.USERNAME_LOGGER_GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
	}

	/**
	 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
	 * their own) Args: x, y, z, neighbor Block
	 */
	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighbor)
	{
		if (!world.isRemote)
			if(world.isBlockPowered(pos))
				((TileEntityLogger)world.getTileEntity(pos)).logPlayers();
	}

	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IBlockState getStateForEntityRender(IBlockState state)
	{
		return getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		if(EnumFacing.values()[meta] == EnumFacing.DOWN || EnumFacing.values()[meta] == EnumFacing.UP)
			return getDefaultState();

		return getDefaultState().withProperty(FACING, EnumFacing.values()[meta]);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return ((EnumFacing) state.getValue(FACING)).getIndex();
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {FACING});
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityLogger().attacks(EntityPlayer.class, SecurityCraft.config.usernameLoggerSearchRadius, 80);
	}
}
