package me.jishunamatata.customcrops;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.jishunamatata.customcrops.listeners.BlockListener;

public class CustomCropCreator extends JavaPlugin {

	private CropManager cropManager;
	private NamespaceFactory namespaceFactory;
	private ConfigManager configManager;

	@Override
	public void onEnable() {
		namespaceFactory = new NamespaceFactory(this);
		cropManager = new CropManager(this);
		configManager = new ConfigManager(this);
		
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(cropManager, this);
		pm.registerEvents(new BlockListener(this, cropManager), this);
	}

	@Override
	public void onDisable() {
	}

	public CropManager getCropManager() {
		return cropManager;
	}

	public NamespaceFactory getNamespaceFactory() {
		return namespaceFactory;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

}
