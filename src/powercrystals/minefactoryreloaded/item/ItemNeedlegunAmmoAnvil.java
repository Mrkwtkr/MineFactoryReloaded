package powercrystals.minefactoryreloaded.item;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemNeedlegunAmmoAnvil extends ItemNeedlegunAmmoBlock
{
	public ItemNeedlegunAmmoAnvil(Block block, int meta)
	{
		super(block, meta);
		setMaxDamage(0);
	}
	
	public ItemNeedlegunAmmoAnvil()
	{
		this(Blocks.anvil, 3);
	}
	
	@Override
	public float getSpread(ItemStack stack)
	{
		return 0.5f;
	}

	@Override
	protected void placeBlockAt(World world, int x, int y, int z, double distance)
	{
		if(!world.isRemote)
		{
	        EntityFallingBlock anvil = new EntityFallingBlock(world, x + 0.5, y + 0.5, z + 0.5,
	        		_block, _blockMeta);
	        anvil.func_145806_a(true);
	        world.spawnEntityInWorld(anvil);
	        anvil.fallDistance = ((float)distance) + 1f;
	        anvil.field_145812_b = 3;
	        anvil.onUpdate();
		}
	}
}
