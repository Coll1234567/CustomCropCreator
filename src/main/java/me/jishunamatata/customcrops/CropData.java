package me.jishunamatata.customcrops;

import org.bukkit.util.Vector;

public class CropData {

	private final Vector position;
	private final String cropType;

	public CropData(int x, int y, int z, String cropType) {
		this(new Vector(x, y, z), cropType);
	}

	public CropData(Vector pos, String cropType) {
		this.position = pos;
		this.cropType = cropType;
	}

	public Vector getPosition() {
		return position;
	}

	public String getCropType() {
		return cropType;
	}

}
