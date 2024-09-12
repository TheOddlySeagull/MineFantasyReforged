package minefantasy.mfr.material;

import minefantasy.mfr.registry.types.CustomMaterialType;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class CustomMaterial extends IForgeRegistryEntry.Impl<CustomMaterial>{
	/**
	 * Min and Max workable temps
	 */
	private static final int[] flameResistArray = new int[] {100, 300};
	private final String name;
	private final CustomMaterialType type;
	/**
	 * The material colour
	 */
	private int[] colourRGB = new int[] {237, 237, 237};
	/**
	 * Base threshold for armour rating
	 */
	private final float hardness;
	/**
	 * The Modifier for durability (1pt per 250 uses)
	 */
	private final float durability;
	/**
	 * used for bow power.. >1 weakens blunt prot, <1 weakens piercing prot
	 */
	private final float flexibility;
	/**
	 * The Efficiency modifier (Like ToolMaterial) Also does damage
	 */
	private final float sharpness;
	/**
	 * The modifier to resist elements like fire and corrosion)
	 */
	private final float resistance;
	/**
	 * The weight Kg/U (Kilogram per unit)
	 */
	private final float density;
	private final int tier;
	private int rarityID = 0;
	private int enchantability;
	private int crafterTier = 0;
	private int crafterAnvilTier = 0;
	private float craftTimeModifier = 1.0F;
	private float meltingPoint;
	private float[] armourProtection = new float[] {1.0F, 1.0F, 1.0F}; // TODO: consider making this property into a typed class
	private boolean unbreakable = false;

	public CustomMaterial(String name, CustomMaterialType type, int tier, float hardness, float durability,
			float flexibility, float resistance, float sharpness, float density, int enchantability) {
		this.name = name;
		this.type = type;
		this.tier = tier;
		this.hardness = hardness;
		this.durability = durability;
		this.flexibility = flexibility;
		this.sharpness = sharpness;
		this.density = density;
		this.resistance = resistance;
		this.enchantability = enchantability;
		this.craftTimeModifier = 2F + (sharpness * 2F);
	}

	public CustomMaterial(String name, CustomMaterialType type, int tier, float hardness, float durability,
			float flexibility, float resistance, float sharpness, float density, int enchantability,
			float[] armourProtection, int[] color) {
		this.name = name;
		this.type = type;
		this.tier = tier;
		this.hardness = hardness;
		this.durability = durability;
		this.flexibility = flexibility;
		this.sharpness = sharpness;
		this.density = density;
		this.enchantability = enchantability;
		this.resistance = resistance;
		this.craftTimeModifier = 2F + (sharpness * 2F);
		this.armourProtection = armourProtection;
		this.colourRGB = color;
	}

	public CustomMaterial(String name, CustomMaterialType type, int tier, float hardness, float durability,
			float flexibility, float resistance, float sharpness, float density, int[] color) {
		this.name = name;
		this.type = type;
		this.tier = tier;
		this.hardness = hardness;
		this.durability = durability;
		this.flexibility = flexibility;
		this.sharpness = sharpness;
		this.density = density;
		this.resistance = resistance;
		this.craftTimeModifier = 2F + (sharpness * 2F);
		this.colourRGB = color;
	}

	/**
	 * Gets material name
	 */
	public String getName() {
		return name.toLowerCase();
	}

	/**
	 * Gets material type
	 */
	public CustomMaterialType getType() {
		return type;
	}

	public CustomMaterial setColour(int red, int green, int blue) {
		colourRGB = new int[] {red, green, blue};
		return this;
	}

	public int[] getColourRGB() {
		return colourRGB;
	}

	public int getColourInt() {
		return (colourRGB[0] << 16) + (colourRGB[1] << 8) + colourRGB[2];
	}

	public float getHardness() {
		return hardness;
	}

	public float getDurability() {
		return durability;
	}

	public float getFlexibility() {
		return flexibility;
	}

	public float getSharpness() {
		return sharpness;
	}

	public float getResistance() {
		return resistance;
	}

	public float getDensity() {
		return density;
	}

	public int getTier() {
		return tier;
	}

	public CustomMaterial setRarity(int id) {
		rarityID = id;
		return this;
	}

	public int getRarityID() {
		return rarityID;
	}

	public int getEnchantability() {
		return enchantability;
	}

	public CustomMaterial setCrafterTiers(int tier) {
		this.crafterTier = tier;
		this.crafterAnvilTier = Math.min(tier, 4);
		return this;
	}

	public int getCrafterTier() {
		return crafterTier;
	}

	public int getCrafterAnvilTier() {
		return crafterAnvilTier;
	}

	public CustomMaterial modifyCraftTime(float time) {
		this.craftTimeModifier *= time;
		return this;
	}

	public float getCraftTimeModifier() {
		return craftTimeModifier;
	}

	public CustomMaterial setMeltingPoint(float heat) {
		meltingPoint = heat;
		return this;
	}

	public float getMeltingPoint() {
		return meltingPoint;
	}

	public CustomMaterial setArmourStats(float cutting, float blunt, float piercing) {
		armourProtection = new float[] {cutting, blunt, piercing};
		return this;
	}

	public float[] getArmourProtection() {
		return armourProtection;
	}

	public CustomMaterial setUnbreakable(boolean unbreakable) {
		this.unbreakable = unbreakable;
		return this;
	}

	public boolean isUnbreakable() {
		return unbreakable;
	}

	@SideOnly(Side.CLIENT)
	public String getMaterialString() {
		return I18n.format("materialtype." + this.type.getName() + ".name", this.crafterTier);
	}

	public float getArmourProtection(int id) {
		return armourProtection[id];
	}

	public ItemStack getItemStack() {
		return ItemStack.EMPTY;
	}

	public float getFireResistance() {
		if (meltingPoint > flameResistArray[0]) {
			float max = flameResistArray[1] - flameResistArray[0];
			float heat = meltingPoint - flameResistArray[0];

			int res = (int) (heat / max * 100F);
			return Math.min(100, res);
		}
		return 0F;
	}

	// -----------------------------------BOW
	// FUNCTIONS----------------------------------------\\

	public int[] getHeatableStats() {
		int workableTemp = (int) meltingPoint;
		int unstableTemp = (int) (workableTemp * 1.5F);
		int maxTemp = (int) (workableTemp * 2F);
		return new int[] {workableTemp, unstableTemp, maxTemp};
	}

	public boolean isHeatable() {
		return this instanceof MetalMaterial;
	}
}
