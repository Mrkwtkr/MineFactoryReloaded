package powercrystals.minefactoryreloaded.modhelpers.buildcraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import buildcraft.api.gates.ITrigger;
import buildcraft.api.gates.ITriggerParameter;

public abstract class MFRBCTrigger implements ITrigger
{
	@SideOnly(Side.CLIENT)
	protected IIcon _icon;
	protected String _iconName; 
	protected String _tag;
	protected String _desc;
	
	protected MFRBCTrigger(String tag, String desc, String icon)
	{
		_tag = tag;
		_desc = desc;
		_iconName = "minefactoryreloaded:buildcraft/trigger/" + icon;
	}
	@Override
	public String getUniqueTag()
	{
		return _tag;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon()
	{
		return _icon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister)
	{
		if (_iconName != null)
			_icon = iconRegister.registerIcon(_iconName);
	}

	@Override
	public boolean hasParameter()
	{
		return false;
	}

	@Override
	public boolean requiresParameter()
	{
		return false;
	}

	@Override
	public String getDescription()
	{
		return _desc;
	}

	//@Override
	public abstract boolean isTriggerActive(ForgeDirection side,
			TileEntity tile, ITriggerParameter parameter);

	@Override
	public ITriggerParameter createParameter()
	{
		return null;
	}
	
	public abstract boolean canApplyTo(TileEntity tile);

	@Override
	public ITrigger rotateLeft()
	{
		return null;
	}
}
