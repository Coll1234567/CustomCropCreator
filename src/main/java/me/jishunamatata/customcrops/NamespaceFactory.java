package me.jishunamatata.customcrops;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public class NamespaceFactory {
	
	private Map<String, NamespacedKey> keyMap = new HashMap<>();
	
	public NamespaceFactory(Plugin plugin) {
		keyMap.put("croptype", new NamespacedKey(plugin, "croptype"));
	}
	
	public NamespacedKey getKey(String name) {
		return keyMap.get(name);
	}

}
