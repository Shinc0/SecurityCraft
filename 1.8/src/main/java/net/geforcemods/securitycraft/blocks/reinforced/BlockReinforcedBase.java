package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockReinforcedBase extends BlockOwnable implements IReinforcedBlock
{
	private List<Block> vanillaBlocks;
	private int amount;

	public BlockReinforcedBase(Material mat, int a, Block... vB)
	{
		super(mat, true);

		vanillaBlocks = Arrays.asList(vB);
		amount = a;
	}

	@Override
	public List<Block> getVanillaBlocks()
	{
		return vanillaBlocks;
	}

	@Override
	public int getAmount()
	{
		return amount;
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, BlockPos pos, Entity entity)
	{
		return !(entity instanceof EntityWither);
	}
}
