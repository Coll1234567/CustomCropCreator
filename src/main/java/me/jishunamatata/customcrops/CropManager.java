package me.jishunamatata.customcrops;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import me.jishunamatata.customcrops.util.CropDataType;

public class CropManager implements Listener {
	private final PersistentDataType<PersistentDataContainer[], CropData[]> cropType;
	private final NamespacedKey cropDataKey;

	private Map<String, CustomCrop> cropMap = new HashMap<>();
	private Map<Chunk, Map<Vector, String>> chunkMap = new HashMap<>();

	public CropManager(CustomCropCreator plugin) {
		cropType = new CropDataType(plugin);
		cropDataKey = new NamespacedKey(plugin, "cropdata");
	}

	public void registerCrop(String name, CustomCrop crop) {
		cropMap.put(name, crop);
	}

	public CustomCrop getCrop(Location location) {
		return getCrop(location.getChunk(),
				new Vector(location.getBlockX() & 0xF, location.getBlockY(), location.getBlockZ() & 0xF));
	}

	@Nullable
	public CustomCrop getCrop(Chunk chunk, Vector pos) {
		Map<Vector, String> cropDataMap = chunkMap.get(chunk);

		if (cropDataMap != null) {
			String key = cropDataMap.get(pos);
			return key == null ? null : cropMap.get(key);
		}
		return null;
	}

	public CustomCrop getByName(String string) {
		return this.cropMap.get(string);
	}

	public void placeCrop(Location location, CustomCrop crop) {
		Map<Vector, String> cropDataMap = chunkMap.computeIfAbsent(location.getChunk(), k -> new HashMap<>());
		cropDataMap.put(new Vector(location.getBlockX() & 0xF, location.getBlockY(), location.getBlockZ() & 0xF),
				crop.getName());
	}

	public void removeCrop(Location location) {
		Map<Vector, String> cropDataMap = this.chunkMap.get(location.getChunk());

		if (cropDataMap != null) {
			cropDataMap.remove(new Vector(location.getBlockX() & 0xF, location.getBlockY(), location.getBlockZ() & 0xF));
		}

	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		this.cropMap.values().forEach(crop -> event.getPlayer().getInventory().addItem(crop.getSeed()));
	}

	@EventHandler
	public void onLoad(ChunkLoadEvent event) {
		Chunk chunk = event.getChunk();

		PersistentDataContainer container = chunk.getPersistentDataContainer();

		if (container.has(cropDataKey, cropType)) {
			CropData[] cropData = container.get(cropDataKey, cropType);

			Map<Vector, String> cropDataMap = new HashMap<>();

			for (CropData crop : cropData) {
				cropDataMap.put(crop.getPosition(), crop.getCropType());
			}
			this.chunkMap.put(chunk, cropDataMap);
		}

	}

	@EventHandler
	public void onUnload(ChunkUnloadEvent event) {
		Chunk chunk = event.getChunk();

		Map<Vector, String> cropDataMap = this.chunkMap.get(chunk);

		if (cropDataMap != null) {
			PersistentDataContainer container = chunk.getPersistentDataContainer();

			CropData[] cropData = new CropData[cropDataMap.size()];

			int index = 0;
			for (Entry<Vector, String> cropEntry : cropDataMap.entrySet()) {
				cropData[index++] = new CropData(cropEntry.getKey(), cropEntry.getValue());
			}

			container.set(cropDataKey, cropType, cropData);
		}
		this.chunkMap.remove(chunk);
	}
}
