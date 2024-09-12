package minefantasy.mfr.api.crafting;

import minefantasy.mfr.registry.types.CustomMaterialType;
import net.minecraft.item.ItemStack;

public interface ITieredComponent {
	/**
	 * is it made of "wood", "metal", etc
	 */
	CustomMaterialType getMaterialType(ItemStack item);
}
