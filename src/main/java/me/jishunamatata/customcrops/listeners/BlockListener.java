package me.jishunamatata.customcrops.listeners;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import me.jishunamatata.customcrops.CropManager;
import me.jishunamatata.customcrops.CustomCrop;
import me.jishunamatata.customcrops.CustomCropCreator;
import me.jishunamatata.customcrops.util.Utils;

public class BlockListener implements Listener {

	private final BlockFace[] faces = new BlockFace[] { BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST,
			BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST };

	private final CustomCropCreator plugin;
	private final CropManager cropManager;

	public BlockListener(CustomCropCreator plugin, CropManager cropManager) {
		this.plugin = plugin;
		this.cropManager = cropManager;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		if (event.getClickedBlock().getType() != Material.FARMLAND
				|| !event.getClickedBlock().getRelative(BlockFace.UP).getType().isAir())
			return;
		if (event.useInteractedBlock() == Result.DENY || event.useItemInHand() == Result.DENY)
			return;

		ItemStack item = event.getItem();

		if (item == null || !item.hasItemMeta())
			return;

		PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
		NamespacedKey cropTypeKey = plugin.getNamespaceFactory().getKey("croptype");
		if (!container.has(cropTypeKey, PersistentDataType.STRING))
			return;

		CustomCrop crop = cropManager.getByName(container.get(cropTypeKey, PersistentDataType.STRING));
		if (crop == null)
			return;

		Block block = event.getClickedBlock().getRelative(BlockFace.UP);
		block.setType(crop.getPlantType());

		cropManager.placeCrop(block.getLocation(), crop);
		Utils.reduceHeldCount(event.getPlayer(), event.getHand(), 1);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		Location location = event.getBlock().getLocation();
		CustomCrop crop = cropManager.getCrop(location);

		if (crop != null) {
			event.setDropItems(false);
			handleCropBlock(event.getBlock(), crop);
			cropManager.removeCrop(location);
		}
	}

	public void handleCropBlock(Block block, CustomCrop crop) {
		Location location = block.getLocation();
		
		if (block.getType() == Material.PLAYER_HEAD) {
			location.getWorld().dropItemNaturally(location, crop.getProduct().clone());
			
			ItemStack seed = crop.getSeed().clone();
			seed.setAmount(crop.getSeedCount());
			location.getWorld().dropItemNaturally(location, seed);
		} else {
			location.getWorld().dropItemNaturally(location, crop.getSeed().clone());
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onGrow(BlockGrowEvent event) {
		BlockData data = event.getNewState().getBlockData();
		Location location = event.getBlock().getLocation();

		if (data instanceof Ageable) {
			Ageable ageable = (Ageable) data;

			if (ageable.getAge() < ageable.getMaximumAge())
				return;

			CustomCrop crop = cropManager.getCrop(location);
			if (crop == null)
				return;

			Bukkit.getScheduler().runTask(plugin, () -> {
				Block block = location.getBlock();
				block.setType(Material.PLAYER_HEAD);

				Rotatable rotate = (Rotatable) block.getBlockData();
				rotate.setRotation(faces[ThreadLocalRandom.current().nextInt(8)]);
				block.setBlockData(rotate);

				crop.setAsBlock((Skull) block.getState());
			});
		}
	}
}
