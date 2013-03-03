package powercrystals.minefactoryreloaded.gui.container;

import powercrystals.minefactoryreloaded.core.TileEntityFactoryPowered;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;

/* packet values:
 * 0: current work
 * 1: current energy
 * 2: current idle
 * 3: current tank 
 * 4: tank id
 * 5: tank meta
 */

public class ContainerFactoryPowered extends ContainerFactoryInventory
{
	protected TileEntityFactoryPowered _te;
	
	private int _tankAmount;
	private int _tankId;
	
	public ContainerFactoryPowered(TileEntityFactoryPowered te, InventoryPlayer inv)
	{
		super(te, inv);
		_te = te;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer)
	{
		return entityplayer.getDistanceSq((double)_te.xCoord + 0.5D, (double)_te.yCoord + 0.5D, (double)_te.zCoord + 0.5D) <= 64D;
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		for(int i = 0; i < crafters.size(); i++)
		{
			((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 0, _te.getWorkDone());
			((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 1, _te.getEnergyStored());
			((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 2, _te.getIdleTicks());
			if(_te.getTank() != null && _te.getTank().getLiquid() != null)
			{
				((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 3, _te.getTank().getLiquid().amount);
				((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 4, _te.getTank().getLiquid().itemID);
				((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 5, _te.getTank().getLiquid().itemMeta);
			}
			else if(_te.getTank() != null)
			{
				((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 3, 0);
				((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 4, 0);
				((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 5, 0);
			}
		}
	}
	
	@Override
	public void updateProgressBar(int var, int value)
	{
		if(var == 0) _te.setWorkDone(value);
		else if(var == 1) _te.setEnergyStored(value);
		else if(var == 2) _te.setIdleTicks(value);
		else if(var == 3) _tankAmount = value;
		else if(var == 4) _tankId = value;
		else if(var == 5) ((LiquidTank)_te.getTank()).setLiquid(new LiquidStack(_tankId, _tankAmount, value));
	}
}
