package me.jishunamatata.customcrops;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.google.common.io.ByteStreams;

public class ConfigManager {
	private final CustomCropCreator plugin;

	public ConfigManager(CustomCropCreator plugin) {
		this.plugin = plugin;
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdirs();
		}
		loadConfig();
	}

	public void loadConfig() {
		File file = copyResource(this.plugin, "config.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

		ConfigurationSection section = config.getConfigurationSection("crops");
		
		for (String cropString : section.getKeys(false)) {
			
			CustomCrop crop = new CustomCrop.Builder(cropString)
					.withHunger(section.getInt(cropString + ".hunger", 1))
					.withSaturation(section.getInt(cropString + ".saturation", 1))
					.withMinSeeds(section.getInt(cropString + ".minseeds", 1))
					.withMaxSeeds(section.getInt(cropString + ".maxseeds", 2))
					.withSeed(plugin, Material.getMaterial(section.getString(cropString + ".seedtype").toUpperCase()))
					.withCropType(Material.getMaterial(section.getString(cropString + ".croptype").toUpperCase()))
					.withTexture(plugin, section.getString(cropString + ".texture", ""))
					.build();
			
			plugin.getCropManager().registerCrop(cropString, crop);
		}
	}

	public void reloadConfig() {
		loadConfig();
	}


	// saveDefaultConfig doesn't copy comments, this will
	private File copyResource(Plugin plugin, String resource) {
		File folder = plugin.getDataFolder();
		File resourceFile = new File(folder, resource);
		if (!resourceFile.exists()) {
			try {
				resourceFile.createNewFile();
				try (InputStream in = plugin.getResource(resource);
						OutputStream out = new FileOutputStream(resourceFile)) {
					ByteStreams.copy(in, out);
				}

			} catch (Exception e) {
				Bukkit.getLogger().severe("Error copying file " + resource);
			}
		}
		return resourceFile;
	}
}
