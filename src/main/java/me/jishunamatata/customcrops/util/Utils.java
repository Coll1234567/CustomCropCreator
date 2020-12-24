package me.jishunamatata.customcrops.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class Utils {
	public static void reduceHeldCount(Player player, EquipmentSlot hand, int amount) {
		ItemStack item;

		if (hand == EquipmentSlot.HAND) {
			item = player.getEquipment().getItemInMainHand();
			item.setAmount(item.getAmount() - amount);
		} else if (hand == EquipmentSlot.OFF_HAND) {
			item = player.getEquipment().getItemInOffHand();
			item.setAmount(item.getAmount() - amount);
		}
	}

}
