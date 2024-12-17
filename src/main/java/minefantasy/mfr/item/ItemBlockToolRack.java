package minefantasy.mfr.item;

import minefantasy.mfr.material.CustomMaterial;
import minefantasy.mfr.registry.CustomMaterialRegistry;
import minefantasy.mfr.registry.types.CustomMaterialType;
import minefantasy.mfr.util.CustomToolHelper;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.ArrayList;

public class ItemBlockToolRack extends ItemBlockBase {
	public ItemBlockToolRack(Block base) {
		super(base);
	}

	@Override
	public void getSubItems(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		if (!isInCreativeTab(itemIn)) {
			return;
		}
		ArrayList<CustomMaterial> wood = CustomMaterialRegistry.getList(CustomMaterialType.WOOD_MATERIAL);
		for (CustomMaterial customMat : wood) {
			items.add(this.construct(customMat.getName()));
		}
	}

	private ItemStack construct(String name) {
		return CustomToolHelper.constructSingleColoredLayer(this, name, 1);
	}

	@Override
	public String getItemStackDisplayName(ItemStack item) {
		return CustomToolHelper.getLocalisedName(item, this.getUnlocalizedNameInefficiently(item) + ".name");
	}
}
