package zabi.minecraft.extraalchemy.blocks;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import zabi.minecraft.extraalchemy.lib.Log;

@SuppressWarnings("deprecation")
public class BrewingStandFire {
	
	private static final Field FUEL = ReflectionHelper.findField(TileEntityBrewingStand.class, "fuel", "field_184278_m");
	
	private boolean errored = false;
	private static final ArrayList<Block> validConductors = new ArrayList<Block>(3);
	static {
		validConductors.add(Blocks.DOUBLE_STONE_SLAB);
		validConductors.add(Blocks.STONE_SLAB);
	}

	@SubscribeEvent
	public void onWorldTick(WorldTickEvent evt) {
		if (!errored && evt.phase.equals(Phase.END) && evt.world.getTotalWorldTime()%200 == 1) {
			try {
				ArrayList<TileEntity> allTiles = new ArrayList<TileEntity>();
				allTiles.addAll(evt.world.loadedTileEntityList);
				allTiles.stream()
					.filter(te -> (te instanceof TileEntityBrewingStand))
					.filter(te -> !te.isInvalid())
					.filter(te -> isHeatSource(te.getPos().down(), te.getWorld(), true))
					.map(te -> (TileEntityBrewingStand) te)
					.forEach(b -> {
						try {
							int fuel = FUEL.getInt(b);
							if (fuel < 20) {
								FUEL.set(b, fuel + 1);
							}
						} catch (IllegalArgumentException | IllegalAccessException ex) {
							ex.printStackTrace();
							errored = true;
						}
					});
			} catch (Exception e) {
				Log.e(e);
				e.printStackTrace();
				errored=true;
			}
		}
	}
	
	private static boolean isHeatSource(BlockPos pos, World world, boolean re) {
		if (re && validConductors.contains(world.getBlockState(pos).getBlock())) return isHeatSource(pos.down(), world, false);
		return world.getBlockState(pos).getMaterial().equals(Material.FIRE) || world.getBlockState(pos).getMaterial().equals(Material.LAVA);
	}
	
}
