package me.jishunamatata.customcrops;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import me.jishunamatata.customcrops.util.ItemBuilder;
import net.md_5.bungee.api.ChatColor;

public class CustomCrop {
	private static Field blockProfileField;
	private static final Encoder encoder = Base64.getEncoder().withoutPadding();

	private final String name;

	private GameProfile profile;

	private int saturation;
	private int hunger;
	private int minSeeds;
	private int maxSeeds;

	private ItemStack product;
	private ItemStack seed;
	private Material plantType;

	private CustomCrop(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getHunger() {
		return hunger;
	}

	public int getSaturation() {
		return saturation;
	}

	public Material getPlantType() {
		return plantType;
	}

	public ItemStack getProduct() {
		return product;
	}

	public ItemStack getSeed() {
		return seed;
	}
	
	public int getSeedCount() {
		return ThreadLocalRandom.current().nextInt(minSeeds, maxSeeds + 1);
	}

	public void setAsBlock(Skull skull) {
		try {
			if (blockProfileField == null) {
				blockProfileField = skull.getClass().getDeclaredField("profile");
				blockProfileField.setAccessible(true);
			}
			blockProfileField.set(skull, this.profile);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		skull.update(false, false);
	}

	public static class Builder {
		private CustomCrop crop;

		public Builder(String name) {
			crop = new CustomCrop(name);
		}
		
		public Builder withMinSeeds(int min) {
			crop.minSeeds = min;
			return this;
		}
		
		public Builder withMaxSeeds(int max) {
			crop.maxSeeds = max;
			return this;
		}

		public Builder withHunger(int hunger) {
			crop.hunger = hunger;
			return this;
		}

		public Builder withSaturation(int saturation) {
			crop.saturation = saturation;
			return this;
		}

		public Builder withCropType(Material type) {
			crop.plantType = type == null ? Material.WHEAT : type;
			return this;
		}

		public Builder withSeed(CustomCropCreator plugin, Material type) {
			crop.seed = new ItemBuilder(type == null ? Material.WHEAT_SEEDS : type)
					.withName(ChatColor.RESET + this.crop.getName().substring(0, 1) + this.crop.getName().substring(1) + " Seeds")
					.withPersistantData(plugin.getNamespaceFactory().getKey("croptype"), this.crop.getName()).build();
			return this;
		}

		public Builder withTexture(CustomCropCreator plugin, String texture) {
			ItemStack item = new ItemStack(Material.PLAYER_HEAD);

			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatColor.RESET + this.crop.getName().substring(0, 1) + this.crop.getName().substring(1));

			UUID id = UUID.nameUUIDFromBytes(texture.getBytes());
			StringBuilder builder = new StringBuilder(
					"{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/");
			builder.append(texture);
			builder.append("\"}}}");

			GameProfile profile = new GameProfile(id, null);
			profile.getProperties().put("textures",
					new Property("textures", encoder.encodeToString(builder.toString().getBytes())));
			crop.profile = profile;

			try {
				Method method = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
				method.setAccessible(true);

				method.invoke(meta, profile);
			} catch (ReflectiveOperationException ex) {
				ex.printStackTrace();
			}

			meta.getPersistentDataContainer().set(plugin.getNamespaceFactory().getKey("croptype"),
					PersistentDataType.STRING, this.crop.getName());
			item.setItemMeta(meta);

			crop.product = item;
			return this;
		}

		public CustomCrop build() {
			return crop;
		}
	}
}
