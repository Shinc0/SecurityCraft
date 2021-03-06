package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.tileentity.TileEntityMotionLight;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMotionActivatedLight extends BlockOwnable {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool LIT = PropertyBool.create("lit");

	public BlockMotionActivatedLight(Material material) {
		super(material);
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public boolean isFullCube(){
		return false;
	}

	@Override
	public boolean isNormalCube(){
		return false;
	}

	@Override
	public int getRenderType(){
		return 3;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos){
		float px = 1.0F / 16.0F;

		if(BlockUtils.getBlockProperty(world, pos, FACING) == EnumFacing.NORTH) {
			setBlockBounds(px * 6, px * 3, 0F, px * 10, px * 9, px * 3);
		}
		else if(BlockUtils.getBlockProperty(world, pos, FACING) == EnumFacing.SOUTH) {
			setBlockBounds(px * 6, px * 3, .995F - (px * 3), px * 10, px * 9, .995F);
		}
		else if(BlockUtils.getBlockProperty(world, pos, FACING) == EnumFacing.EAST) {
			setBlockBounds(.995F - (px * 3), px * 3, px * 6, .995F, px * 9, px * 10);
		}
		else if(BlockUtils.getBlockProperty(world, pos, FACING) == EnumFacing.WEST) {
			setBlockBounds(0F, px * 3, px * 6, px * 3, px * 9, px * 10);
		}
	}

	@Override
	public int getLightValue(IBlockAccess world, BlockPos pos) {
		return ((Boolean) world.getBlockState(pos).getValue(LIT)).booleanValue() ? 15 : 0;
	}

	public static void toggleLight(World world, BlockPos pos, double searchRadius, Owner owner, boolean isLit) {
		if(!world.isRemote)
		{
			if(isLit)
			{
				BlockUtils.setBlockProperty(world, pos, LIT, true);

				if(((IOwnable) world.getTileEntity(pos)) != null)
					((IOwnable) world.getTileEntity(pos)).setOwner(owner.getUUID(), owner.getName());

				BlockUtils.updateAndNotify(world, pos, SCContent.motionActivatedLight, 1, false);
			}
			else
			{
				BlockUtils.setBlockProperty(world, pos, LIT, false);

				if(((IOwnable) world.getTileEntity(pos)) != null)
					((IOwnable) world.getTileEntity(pos)).setOwner(owner.getUUID(), owner.getName());

				BlockUtils.updateAndNotify(world, pos, SCContent.motionActivatedLight, 1, false);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IBlockState getStateForEntityRender(IBlockState state)
	{
		return getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
	}

	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return world.isSideSolid(pos.offset(facing.getOpposite()), facing, true) ? getDefaultState().withProperty(FACING, facing.getOpposite()).withProperty(LIT, false) : getDefaultState().withProperty(FACING, EnumFacing.DOWN);
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side){
		if(side == EnumFacing.UP || side == EnumFacing.DOWN) return false;

		return world.isSideSolid(pos.offset(side.getOpposite()), side);
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
		if (!canPlaceBlockOnSide(world, pos, ((EnumFacing) world.getBlockState(pos).getValue(FACING)).getOpposite())) {
			dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
			world.setBlockToAir(pos);
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		if(meta == 15) return getDefaultState();

		if(meta <= 5)
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]).withProperty(LIT, false);
		else
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta - 6]).withProperty(LIT, true);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		if(state.getProperties().containsKey(LIT) && ((Boolean) state.getValue(LIT)).booleanValue())
			return (((EnumFacing) state.getValue(FACING)).getIndex() + 6);
		else{
			if(!state.getProperties().containsKey(FACING)) return 15;

			return ((EnumFacing) state.getValue(FACING)).getIndex();
		}
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {FACING, LIT});
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityMotionLight().attacks(EntityPlayer.class, SecurityCraft.config.motionActivatedLightSearchRadius, 1);
	}

}
