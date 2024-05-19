package minefantasy.mfr.mixin;

import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.JsonContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(JsonContext.class)
public interface JsonContextAccessor {

	@Accessor(value = "constants", remap = false)
	Map<String, Ingredient> getConstants();

	@Accessor(value = "constants", remap = false)
	void setConstants(Map<String, Ingredient> constants);
}
