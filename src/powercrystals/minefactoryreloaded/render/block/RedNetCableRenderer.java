package powercrystals.minefactoryreloaded.render.block;

import codechicken.lib.lighting.LightModel;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.uv.IconTransformation;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.connectivity.RedNetConnectionType;
import powercrystals.minefactoryreloaded.setup.MFRConfig;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetCable;
import powercrystals.minefactoryreloaded.tile.rednet.TileEntityRedNetEnergy;

public class RedNetCableRenderer implements ISimpleBlockRenderingHandler {
	protected static CCModel base;
	protected static CCModel cage;
	protected static CCModel[] cable = new CCModel[6];
	protected static CCModel[] iface = new CCModel[6];
	protected static CCModel[] band  = new CCModel[6];
	protected static CCModel[] plate = new CCModel[6];
	protected static CCModel[] platef = new CCModel[6];
	protected static CCModel[] grip  = new CCModel[6];
	protected static CCModel[] wire  = new CCModel[6];
	protected static CCModel[] caps  = new CCModel[6];

	public static IconTransformation uvt;
	public static boolean brightBand;

	static {
		try {
			Map<String, CCModel> cableModels = CCModel.parseObjModels(MineFactoryReloadedCore.class.
					getResourceAsStream("/powercrystals/minefactoryreloaded/models/RedNetCable.obj"),
					7, new Scale(1/16f));
			Vector3 p = new Vector3(0, 0, 0);
			base = cableModels.get("base").backfacedCopy();
			compute(base);

			cage = cableModels.get("cage").backfacedCopy();
			compute(cage);

			cable[5] = cableModels.get("cable").backfacedCopy();
			calculateSidedModels(cable, p);

			iface[5] = cableModels.get("interface").backfacedCopy();
			calculateSidedModels(iface, p);

			band[5] = cableModels.get("band").backfacedCopy();
			calculateSidedModels(band, p);

			plate[5] = cableModels.get("plate").backfacedCopy();
			calculateSidedModels(plate, p);

			platef[5] = cableModels.get("plateface").backfacedCopy();
			calculateSidedModels(platef, p);

			grip[5] = cableModels.get("grip").backfacedCopy();
			calculateSidedModels(grip, p);

			wire[5] = cableModels.get("wire").backfacedCopy();
			calculateSidedModels(wire, p);

			caps[5] = cableModels.get("cap").backfacedCopy();
			calculateSidedModels(caps, p);
		} catch (Throwable _) { _.printStackTrace(); }
	}

	private static void calculateSidedModels(CCModel[] m, Vector3 p) {
		compute(m[4] = m[5].copy().apply(new Rotation(Math.PI * 1.0, 0, 1, 0)));
		compute(m[3] = m[5].copy().apply(new Rotation(Math.PI * -.5, 0, 1, 0)));
		compute(m[2] = m[5].copy().apply(new Rotation(Math.PI * 0.5, 0, 1, 0)));
		compute(m[1] = m[5].copy().apply(new Rotation(Math.PI * 0.5, 0, 0, 1).with(new Rotation(Math.PI, 0, 1, 0))));
		compute(m[0] = m[5].copy().apply(new Rotation(Math.PI * -.5, 0, 0, 1)));
		compute(m[5]);
	}

	private static void compute(CCModel m) {
		m.apply(new Translation(0.5, 0.5, 0.5));
		m.computeNormals();
		m.computeLighting(LightModel.standardLightModel);
	}

	public static void updateUVT(IIcon icon) {
		uvt = new IconTransformation(icon);
		brightBand = MFRConfig.brightRednetBand.getBoolean(true);
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		CCRenderState.reset();
		CCRenderState.useNormals = true;
		Tessellator tess = Tessellator.instance;

		GL11.glTranslatef(-.5f, -.5f, -.5f);
		tess.startDrawingQuads();
		base.render(uvt);
		cable[2].render(uvt);
		cable[3].render(uvt);
		if (metadata == 3 | metadata == 2)
		{
			cage.render(uvt);
			wire[2].render(uvt);
			wire[3].render(uvt);
			caps[2].render(uvt);
			caps[3].render(uvt);
		}
		tess.draw();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		CCRenderState.reset();
		CCRenderState.useNormals = true;
		TileEntityRedNetCable _cable = (TileEntityRedNetCable)world.getTileEntity(x, y, z);
		TileEntityRedNetEnergy _cond = null;
		int brightness = block.getMixedBrightnessForBlock(world, x, y, z);
		int bandBrightness = brightBand ? 0xd00070 : brightness;

		Tessellator tess = Tessellator.instance;
		tess.setColorOpaque_F(1,1,1);
		tess.setBrightness(brightness);

		Translation tlate = new Translation(new Vector3(x, y, z));

		base.render(tlate, uvt);
		if (_cable instanceof TileEntityRedNetEnergy) {
			cage.render(tlate, uvt);
			_cond = (TileEntityRedNetEnergy)_cable;
		}

		for (ForgeDirection f : ForgeDirection.VALID_DIRECTIONS) {
			int side = f.ordinal();
			RedNetConnectionType state = _cable.getCachedConnectionState(f);
			switch (state.flags & 31) {
			case 11: // isCable, isSingleSubnet
				tess.setBrightness(bandBrightness);
				band[side].setColour(_cable.getSideColorValue(f));
				band[side].render(tlate, uvt);
				tess.setColorOpaque_F(1,1,1);
				tess.setBrightness(brightness);
			case 19: // isCable, isAllSubnets
				if (state.isSingleSubnet) {
					iface[side].render(tlate, uvt);
					grip[side].render(tlate, uvt);
				} else
					cable[side].render(tlate, uvt);
				break;
			case 13: // isPlate, isSingleSubnet
				tess.setBrightness(bandBrightness);
				band[side].setColour(_cable.getSideColorValue(f));
				band[side].render(tlate, uvt);
				platef[side].setColour(_cable.getSideColorValue(f));
				platef[side].render(tlate, uvt);
				tess.setColorOpaque_F(1,1,1);
				tess.setBrightness(brightness);
			case 21: // isPlate, isAllSubnets
				iface[side].render(tlate, uvt);
				plate[side].render(tlate, uvt);
				if (state.isAllSubnets) {
					platef[side].setColour(-1);
					platef[side].render(tlate, uvt);
				}
			default:
				break;
			}
			if (_cond != null && _cond.isInterfacing(f)) {
				wire[side].render(tlate, uvt);
				if (_cond.interfaceMode(f) != 4)
					caps[side].render(tlate, uvt);
			}
		}

		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return MineFactoryReloadedCore.renderIdRedNet;
	}

}
