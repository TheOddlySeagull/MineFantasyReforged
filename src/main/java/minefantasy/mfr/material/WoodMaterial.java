package minefantasy.mfr.material;

import minefantasy.mfr.registry.types.CustomMaterialType;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public class WoodMaterial extends CustomMaterial {

	public ResourceLocation inputItemResourceLocation;
	public int inputItemMeta;

	public WoodMaterial(String name, int tier, float hardness, float durability, float flexibility, float resistance, float density, int[] color, ResourceLocation inputItemResourceLocation, int inputItemMeta) {
		super(name, CustomMaterialType.WOOD_MATERIAL, tier, hardness, durability, flexibility, resistance, 0F, density, color);
		this.inputItemResourceLocation = inputItemResourceLocation;
		this.inputItemMeta = inputItemMeta;
	}

	@Override
	public String getMaterialString() {
		return I18n.format("materialtype." + this.getType().getName() + ".name", this.getTier());
	}

	@Override
	public ItemStack getItemStack() {
		NonNullList<ItemStack> list = OreDictionary.getOres("plankWood");
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return ItemStack.EMPTY;
	}
}
