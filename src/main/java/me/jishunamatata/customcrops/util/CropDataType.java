package me.jishunamatata.customcrops.util;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import me.jishunamatata.customcrops.CropData;
import me.jishunamatata.customcrops.CustomCropCreator;

public class CropDataType implements PersistentDataType<PersistentDataContainer[], CropData[]> {
	private final NamespacedKey xKey;
	private final NamespacedKey yKey;
	private final NamespacedKey zKey;
	private final NamespacedKey typeKey;

	public CropDataType(CustomCropCreator plugin) {
		xKey = new NamespacedKey(plugin, "x");
		yKey = new NamespacedKey(plugin, "y");
		zKey = new NamespacedKey(plugin, "z");
		typeKey = plugin.getNamespaceFactory().getKey("croptype");
	}

	@Override
	public Class<PersistentDataContainer[]> getPrimitiveType() {
		return PersistentDataContainer[].class;
	}

	@Override
	public Class<CropData[]> getComplexType() {
		return CropData[].class;
	}

	@Override
	public PersistentDataContainer[] toPrimitive(CropData[] complex, PersistentDataAdapterContext context) {
		PersistentDataContainer[] containerArray = new PersistentDataContainer[complex.length];

		for (int i = 0; i < complex.length; i++) {
			CropData cropData = complex[i];
			Vector pos = cropData.getPosition();

			PersistentDataContainer container = context.newPersistentDataContainer();

			container.set(xKey, PersistentDataType.INTEGER, (int) pos.getX());
			container.set(yKey, PersistentDataType.INTEGER, (int) pos.getY());
			container.set(zKey, PersistentDataType.INTEGER, (int) pos.getZ());
			container.set(typeKey, PersistentDataType.STRING, cropData.getCropType());

			containerArray[i] = container;
		}
		return containerArray;
	}

	@Override
	public CropData[] fromPrimitive(PersistentDataContainer[] primitive, PersistentDataAdapterContext context) {
		CropData[] cropDataArray = new CropData[primitive.length];

		for (int i = 0; i < primitive.length; i++) {
			PersistentDataContainer container = primitive[i];
			int x = container.get(xKey, PersistentDataType.INTEGER);
			int y = container.get(yKey, PersistentDataType.INTEGER);
			int z = container.get(zKey, PersistentDataType.INTEGER);

			cropDataArray[i] = new CropData(x, y, z, container.get(typeKey, PersistentDataType.STRING));
		}
		return cropDataArray;
	}

}
