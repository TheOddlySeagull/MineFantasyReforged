package minefantasy.mfr.registry.factories;

import com.google.gson.JsonObject;
import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.material.MetalMaterial;
import minefantasy.mfr.material.WoodMaterial;
import minefantasy.mfr.registry.types.CustomMaterialType;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.JsonContext;

public class CustomMaterialFactory {

	public CustomMaterial parse(JsonContext context, JsonObject json, String type) {
		CustomMaterialType materialType = CustomMaterialType.deserialize(type);
		switch (materialType) {
			case WOOD_MATERIAL:
				return parseWoodMaterial(context, json);
			case METAL_MATERIAL:
				return parseMetalMaterial(context, json);
			default:
				return null;
		}
	}

	private CustomMaterial parseMetalMaterial(JsonContext context, JsonObject json) {
		String name = JsonUtils.getString(json, "name");

		String oreDictList = JsonUtils.getString(json, "oreDictList");

		JsonObject properties = JsonUtils.getJsonObject(json, "properties");
		float durability = JsonUtils.getFloat(properties, "durability");
		float flexibility = JsonUtils.getFloat(properties, "flexibility");
		float sharpness = JsonUtils.getFloat(properties, "sharpness");
		float hardness = JsonUtils.getFloat(properties, "hardness");
		float resistance = JsonUtils.getFloat(properties, "resistance");
		float density = JsonUtils.getFloat(properties, "density");
		int tier = JsonUtils.getInt(properties, "tier");
		int meltingPoint = JsonUtils.getInt(properties, "melting_point");
		int rarity = JsonUtils.getInt(properties, "rarity");
		int enchantability = JsonUtils.getInt(properties, "enchantability");
		int craftTier = JsonUtils.getInt(properties, "craft_tier");
		int craftTimeModifier = JsonUtils.getInt(properties, "craft_time_modifier");
		boolean unbreakable = JsonUtils.getBoolean(properties, "unbreakable");

		JsonObject armourStats = JsonUtils.getJsonObject(json, "armour_stats");
		float cuttingProtection = JsonUtils.getFloat(armourStats, "cutting");
		float bluntProtection = JsonUtils.getFloat(armourStats, "blunt");
		float piercingProtection = JsonUtils.getFloat(armourStats, "piercing");
		float[] armour = {cuttingProtection, bluntProtection, piercingProtection};

		JsonObject color = JsonUtils.getJsonObject(json, "color");
		int red = JsonUtils.getInt(color, "red");
		int green = JsonUtils.getInt(color, "green");
		int blue = JsonUtils.getInt(color, "blue");
		int[] colors = {red, green, blue};

		MetalMaterial metal = new MetalMaterial(name, tier, hardness, durability, flexibility,
				sharpness, resistance, density, enchantability, armour, colors, oreDictList);

		metal.setMeltingPoint(meltingPoint);
		metal.setRarity(rarity);
		metal.setCrafterTiers(craftTier);
		metal.modifyCraftTime(craftTimeModifier);
		metal.setUnbreakable(unbreakable);

		return metal;
	}

	private CustomMaterial parseWoodMaterial(JsonContext context, JsonObject json) {
		String name = JsonUtils.getString(json, "name");

		ResourceLocation inputItemResourceLocation = new ResourceLocation(JsonUtils.getString(json, "inputItem"));
		int inputItemMeta = JsonUtils.getInt(json, "inputItemMeta");

		JsonObject properties = JsonUtils.getJsonObject(json, "properties");
		float durability = JsonUtils.getFloat(properties, "durability");
		float flexibility = JsonUtils.getFloat(properties, "flexibility");
		float hardness = JsonUtils.getFloat(properties, "hardness");
		float resistance = JsonUtils.getFloat(properties, "resistance");
		float density = JsonUtils.getFloat(properties, "density");
		int tier = JsonUtils.getInt(properties, "tier");
		int rarity = JsonUtils.getInt(properties, "rarity");
		int craftTier = JsonUtils.getInt(properties, "craft_tier");
		int craftTimeModifier = JsonUtils.getInt(properties, "craft_time_modifier");

		JsonObject color = JsonUtils.getJsonObject(json, "color");
		int red = JsonUtils.getInt(color, "red");
		int green = JsonUtils.getInt(color, "green");
		int blue = JsonUtils.getInt(color, "blue");
		int[] colors = {red, green, blue};

		WoodMaterial wood = new WoodMaterial(name, tier, hardness, durability, flexibility,
				resistance, density, colors, inputItemResourceLocation, inputItemMeta);

		wood.setRarity(rarity);
		wood.setCrafterTiers(craftTier);
		wood.modifyCraftTime(craftTimeModifier);

		return wood;
	}
}
