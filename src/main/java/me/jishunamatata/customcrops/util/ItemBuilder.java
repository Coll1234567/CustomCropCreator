package me.jishunamatata.customcrops.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ItemBuilder {

	private ItemStack item;
	private ItemMeta meta;

	private ItemBuilder() {
	}

	public ItemBuilder(Material material) {
		this(material, 1);
	}

	public ItemBuilder(Material material, int amount) {
		this.item = new ItemStack(material, amount);
		this.meta = this.item.getItemMeta();
	}

	public static ItemBuilder modifyItem(ItemStack item) {
		ItemBuilder builder = new ItemBuilder();
		builder.item = item;
		builder.meta = item.getItemMeta();

		return builder;
	}

	public ItemBuilder withEnchantment(Enchantment enchantment, int level) {
		this.meta.addEnchant(enchantment, level, true);

		return this;
	}

	public ItemBuilder withName(String name) {
		this.meta.setDisplayName(name);

		return this;
	}

	public ItemBuilder addLore(List<String> lore) {
		List<String> itemLore = getLore();
		itemLore.addAll(lore);

		meta.setLore(itemLore);

		return this;
	}

	public ItemBuilder addLore(String... lore) {
		List<String> itemLore = getLore();
		for (String loreLine : lore)
			itemLore.add(loreLine);

		meta.setLore(itemLore);

		return this;
	}

	public ItemBuilder withLore(List<String> lore) {
		this.meta.setLore(lore);
		return this;
	}

	public ItemBuilder withFlags(ItemFlag... flags) {
		this.meta.addItemFlags(flags);

		return this;
	}

	public ItemBuilder withPersistantData(NamespacedKey key, Object value) {
		PersistentDataContainer data = this.meta.getPersistentDataContainer();

		if (value instanceof Integer) {
			data.set(key, PersistentDataType.INTEGER, (int) value);
		} else if (value instanceof String) {
			data.set(key, PersistentDataType.STRING, (String) value);
		} else if (value instanceof Short) {
			data.set(key, PersistentDataType.SHORT, (short) value);
		} else if (value instanceof Double) {
			data.set(key, PersistentDataType.DOUBLE, (double) value);
		} else if (value instanceof Byte) {
			data.set(key, PersistentDataType.BYTE, (byte) value);
		} else if (value instanceof Float) {
			data.set(key, PersistentDataType.FLOAT, (float) value);
		} else if (value instanceof Long) {
			data.set(key, PersistentDataType.LONG, (long) value);
		}

		return this;
	}

	public ItemBuilder withModelData(int index) {
		this.meta.setCustomModelData(index);

		return this;
	}

	public ItemStack build() {
		ItemStack finalItem = this.item;
		finalItem.setItemMeta(this.meta);

		return finalItem;
	}

	public List<String> getLore() {
		return meta.hasLore() ? meta.getLore() : new ArrayList<>();
	}

}
