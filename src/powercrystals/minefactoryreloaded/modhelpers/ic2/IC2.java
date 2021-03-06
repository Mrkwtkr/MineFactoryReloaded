package powercrystals.minefactoryreloaded.modhelpers.ic2;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

import ic2.api.item.IC2Items;
import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.ISemiFluidFuelManager.BurnProperty;
import ic2.api.recipe.Recipes;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizerStandard;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableSapling;

@Mod(modid = "MineFactoryReloaded|CompatIC2", name = "MFR Compat: IC2", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:IC2")
public class IC2
{
	@EventHandler
	public static void postLoad(FMLPostInitializationEvent evt)
	{
		ItemArmor boots = net.minecraft.init.Items.leather_boots;
		ItemStack booties = new ItemStack(boots, 64, 0);
		boots.func_82813_b(booties, 0x3479F2);
		OreDictionary.registerOre("greggy_greg_do_please_kindly_stuff_a_sock_in_it", booties);
	}

	@EventHandler
	public static void load(FMLInitializationEvent evt)
	{
		if(!Loader.isModLoaded("IC2"))
		{
			return;
		}
		try
		{
			ItemStack crop = IC2Items.getItem("crop");
			ItemStack rubber = IC2Items.getItem("rubber").copy();
			ItemStack rubberSapling = IC2Items.getItem("rubberSapling");
			ItemStack rubberLeaves = IC2Items.getItem("rubberLeaves");
			ItemStack rubberWood = IC2Items.getItem("rubberWood");
			ItemStack stickyResin = IC2Items.getItem("resin");
			ItemStack plantBall = IC2Items.getItem("plantBall");

			if(rubberSapling != null)
			{
				MFRRegistry.registerPlantable(new PlantableSapling(rubberSapling.getItem(),
						Block.getBlockFromItem(rubberSapling.getItem())));
				MFRRegistry.registerFertilizable(new FertilizableIC2RubberTree(Block.getBlockFromItem(rubberSapling.getItem())));
			}
			if(rubberLeaves != null)
			{
				MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(Block.getBlockFromItem(rubberLeaves.getItem())));
			}
			if(rubberWood != null)
			{
				MFRRegistry.registerHarvestable(new HarvestableIC2RubberWood(Block.getBlockFromItem(rubberWood.getItem()), stickyResin.getItem()));
				MFRRegistry.registerFruitLogBlock(Block.getBlockFromItem(rubberWood.getItem()));
				MFRRegistry.registerFruit(new FruitIC2Resin(rubberWood, stickyResin));
			}

			ItemStack fertilizer = IC2Items.getItem("fertilizer");
			if(fertilizer != null)
			{
				MFRRegistry.registerFertilizer(new FertilizerStandard(fertilizer.getItem(), fertilizer.getItemDamage()));
			}

			MFRRegistry.registerHarvestable(new HarvestableIC2Crop(Block.getBlockFromItem(crop.getItem())));

			GameRegistry.addShapedRecipe(plantBall, new Object[]
					{
					"LLL",
					"L L",
					"LLL",
					Character.valueOf('L'), new ItemStack(MineFactoryReloadedCore.rubberLeavesBlock)
					} );

			Method m = null;
			ItemStack item = new ItemStack(MineFactoryReloadedCore.rubberSaplingBlock);
			rubber.stackSize = 1;
			try
			{
				for (Method t : IMachineRecipeManager.class.getDeclaredMethods())
					if (t.getName().equals("addRecipe"))
					{
						m = t;
						break;
					}
				m.invoke(Recipes.extractor, item, rubber);
			}
			catch (Throwable _)
			{
				try
				{
					Class<?> clazz = Class.forName("ic2.api.recipe.RecipeInputItemStack");
					Constructor<?> c = clazz.getDeclaredConstructor(ItemStack.class);
					Object o = c.newInstance(item);
					m.invoke(Recipes.extractor, o, null, new ItemStack[] {rubber});
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
			}
			copyEthanol();
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}//*/
	}

	private static void copyEthanol()
	{
		BurnProperty q = Recipes.semiFluidGenerator.getBurnProperty(FluidRegistry.getFluid("bioethanol"));
		if (q != null)
			Recipes.semiFluidGenerator.addFluid("biofuel", q.amount, q.power);
		else if (FluidRegistry.getFluid("bioethanol") == null)
			Recipes.semiFluidGenerator.addFluid("biofuel", 10, 16);
	}
}
