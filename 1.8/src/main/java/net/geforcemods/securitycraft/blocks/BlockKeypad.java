package net.geforcemods.securitycraft.blocks;

import java.util.List;
import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.compat.waila.ICustomWailaDisplay;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypad;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockKeypad extends BlockContainer implements ICustomWailaDisplay, IPasswordConvertible {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool POWERED = PropertyBool.create("powered");

	public BlockKeypad(Material material) {
		super(material);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockAccess world, BlockPos pos)
	{
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumWorldBlockLayer getBlockLayer() {
		return EnumWorldBlockLayer.CUTOUT;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof TileEntityKeypad && ((TileEntityKeypad)te).hasModule(EnumCustomModules.DISGUISE))
		{
			ItemStack module = ((TileEntityKeypad)te).getModule(EnumCustomModules.DISGUISE);

			return ((ItemModule)module.getItem()).getBlockAddons(module.getTagCompound()).get(0).getDefaultState().getBlock().isSideSolid(world, pos, side);
		}

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
		BlockPos keypadPos = pos.offset(side.getOpposite());

		if(world.getTileEntity(keypadPos) == null) return true;
		CustomizableSCTE tileEntity = (CustomizableSCTE) world.getTileEntity(keypadPos);

		if(tileEntity.hasModule(EnumCustomModules.DISGUISE))
		{
			ItemStack disguiseModule = tileEntity.getModule(EnumCustomModules.DISGUISE);
			List<Block> blocks = ((ItemModule) disguiseModule.getItem()).getBlockAddons(disguiseModule.getTagCompound());

			if(blocks.size() != 0)
			{
				Block blockToDisguiseAs = blocks.get(0);

				// If the keypad has a disguise module added with a transparent block inserted.
				if(!blockToDisguiseAs.isOpaqueCube() || !blockToDisguiseAs.isFullCube())
					return checkForSideTransparency(world, keypadPos, world.getBlockState(keypadPos.offset(side)).getBlock(), side);
			}
		}

		return true;
	}

	public boolean checkForSideTransparency(IBlockAccess world, BlockPos keypadPos, Block neighborBlock, EnumFacing side) {
		if(neighborBlock == Blocks.air)
			return true;

		// Slightly cheating here, checking if the block is an instance of BlockBreakable
		// and a vanilla block instead of checking for specific blocks, since all vanilla
		// BlockBreakable blocks are transparent.
		if(neighborBlock instanceof BlockBreakable && neighborBlock.toString().startsWith("net.minecraft.block"))
			return false;

		return true;
	}

	@Override
	public int getRenderType(){
		return 3;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		if(world.isRemote)
			return true;
		else {
			if(((Boolean) state.getValue(POWERED)).booleanValue())
				return false;

			if(ModuleUtils.checkForModule(world, pos, player, EnumCustomModules.WHITELIST) || ModuleUtils.checkForModule(world, pos, player, EnumCustomModules.BLACKLIST)){
				activate(world, pos);
				return true;
			}

			if(!PlayerUtils.isHoldingItem(player, SCContent.codebreaker) && !PlayerUtils.isHoldingItem(player, SCContent.keyPanel))
				((IPasswordProtected) world.getTileEntity(pos)).openPasswordGUI(player);

			return true;
		}
	}

	public static void activate(World world, BlockPos pos){
		BlockUtils.setBlockProperty(world, pos, POWERED, true);
		world.notifyNeighborsOfStateChange(pos, SCContent.keypad);
		world.scheduleUpdate(pos, SCContent.keypad, 60);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random random){
		BlockUtils.setBlockProperty(world, pos, POWERED, false);
		world.notifyNeighborsOfStateChange(pos, SCContent.keypad);
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		setDefaultFacing(world, pos, state);
	}

	private void setDefaultFacing(World world, BlockPos pos, IBlockState state) {
		Block north = world.getBlockState(pos.north()).getBlock();
		Block south = world.getBlockState(pos.south()).getBlock();
		Block west = world.getBlockState(pos.west()).getBlock();
		Block east = world.getBlockState(pos.east()).getBlock();
		EnumFacing facing = (EnumFacing)state.getValue(FACING);

		if (facing == EnumFacing.NORTH && north.isFullBlock() && !south.isFullBlock())
			facing = EnumFacing.SOUTH;
		else if (facing == EnumFacing.SOUTH && south.isFullBlock() && !north.isFullBlock())
			facing = EnumFacing.NORTH;
		else if (facing == EnumFacing.WEST && west.isFullBlock() && !east.isFullBlock())
			facing = EnumFacing.EAST;
		else if (facing == EnumFacing.EAST && east.isFullBlock() && !west.isFullBlock())
			facing = EnumFacing.WEST;

		world.setBlockState(pos, state.withProperty(FACING, facing), 2);
	}

	@Override
	public boolean canProvidePower(){
		return true;
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int isProvidingWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side){
		if(((Boolean) state.getValue(POWERED)).booleanValue())
			return 15;
		else
			return 0;
	}

	/**
	 * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	 * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int isProvidingStrongPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side){
		if(((Boolean) state.getValue(POWERED)).booleanValue())
			return 15;
		else
			return 0;
	}

	@Override
	public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
	{
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(POWERED, false);
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
		if(meta == 15) return getDefaultState();

		if(meta <= 5)
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta].getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : EnumFacing.values()[meta]).withProperty(POWERED, false);
		else
			return getDefaultState().withProperty(FACING, EnumFacing.values()[meta - 6]).withProperty(POWERED, true);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		if(state.getProperties().containsKey(POWERED) && ((Boolean) state.getValue(POWERED)).booleanValue())
			return (((EnumFacing) state.getValue(FACING)).getIndex() + 6);
		else{
			if(!state.getProperties().containsKey(FACING)) return 15;

			return ((EnumFacing) state.getValue(FACING)).getIndex();
		}
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		IBlockState disguisedState = getDisguisedBlockState(world, pos);

		return disguisedState != null ? disguisedState : state;
	}

	public IBlockState getDisguisedBlockState(IBlockAccess world, BlockPos pos) {
		if(world.getTileEntity(pos) instanceof TileEntityKeypad) {
			TileEntityKeypad te = (TileEntityKeypad) world.getTileEntity(pos);

			ItemStack module = te.hasModule(EnumCustomModules.DISGUISE) ? te.getModule(EnumCustomModules.DISGUISE) : null;

			if(module != null && !((ItemModule) module.getItem()).getBlockAddons(module.getTagCompound()).isEmpty()) {
				ItemStack disguisedStack = ((ItemModule) module.getItem()).getAddons(module.getTagCompound()).get(0);
				Block block = Block.getBlockFromItem(disguisedStack.getItem());
				boolean hasMeta = disguisedStack.getHasSubtypes();

				IBlockState disguisedModel = block.getStateFromMeta(hasMeta ? disguisedStack.getItemDamage() : getMetaFromState(world.getBlockState(pos)));

				if (block != this)
					return block.getActualState(disguisedModel, world, pos);
			}
		}

		return null;
	}

	public ItemStack getDisguisedStack(IBlockAccess world, BlockPos pos) {
		if(world.getTileEntity(pos) instanceof TileEntityKeypad) {
			TileEntityKeypad te = (TileEntityKeypad) world.getTileEntity(pos);

			ItemStack stack = te.hasModule(EnumCustomModules.DISGUISE) ? te.getModule(EnumCustomModules.DISGUISE) : null;

			if(stack != null && !((ItemModule) stack.getItem()).getBlockAddons(stack.getTagCompound()).isEmpty()) {
				ItemStack disguisedStack = ((ItemModule) stack.getItem()).getAddons(stack.getTagCompound()).get(0);

				if(Block.getBlockFromItem(disguisedStack.getItem()) != this)
					return disguisedStack;
			}
		}

		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, BlockPos pos) {
		return null;
	}

	@Override
	protected BlockState createBlockState()
	{
		return new BlockState(this, new IProperty[] {FACING, POWERED});
	}

	/**
	 * Returns a new instance of a block's tile entity class. Called on placing the block.
	 */
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityKeypad();
	}

	@Override
	public ItemStack getDisplayStack(World world, IBlockState state, BlockPos pos) {
		ItemStack stack = getDisguisedStack(world, pos);

		return stack != null ? stack : new ItemStack(this);
	}

	@Override
	public boolean shouldShowSCInfo(World world, IBlockState state, BlockPos pos) {
		return !(getDisguisedStack(world, pos) != null);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos)
	{
		ItemStack stack = getDisguisedStack(world, pos);

		return stack == null ? new ItemStack(this) : stack;
	}

	@Override
	public int colorMultiplier(IBlockAccess world, BlockPos pos, int renderPass)
	{
		TileEntityKeypad te = (TileEntityKeypad)world.getTileEntity(pos);

		if(te.hasModule(EnumCustomModules.DISGUISE) && !((ItemModule) te.getModule(EnumCustomModules.DISGUISE).getItem()).getBlockAddons(te.getModule(EnumCustomModules.DISGUISE).getTagCompound()).isEmpty())
			return Block.getBlockFromItem(getDisguisedStack(world, pos).getItem()).colorMultiplier(world, pos, renderPass);
		return super.colorMultiplier(world, pos, renderPass);
	}

	@Override
	public Block getOriginalBlock()
	{
		return SCContent.frame;
	}

	@Override
	public boolean convert(EntityPlayer player, World world, BlockPos pos)
	{
		world.setBlockState(pos, SCContent.keypad.getDefaultState().withProperty(BlockKeypad.FACING, world.getBlockState(pos).getValue(BlockFrame.FACING)).withProperty(BlockKeypad.POWERED, false));
		((IOwnable) world.getTileEntity(pos)).getOwner().set(((IOwnable)world.getTileEntity(pos)).getOwner());
		return true;
	}
}
