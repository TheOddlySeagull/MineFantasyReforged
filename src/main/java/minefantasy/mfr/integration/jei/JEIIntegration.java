package minefantasy.mfr.integration.jei;

import mezz.jei.Internal;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.IStackHelper;
import mezz.jei.gui.GuiHelper;
import mezz.jei.runtime.JeiHelpers;
import minefantasy.mfr.config.ConfigIntegration;
import minefantasy.mfr.init.MineFantasyBlocks;
import minefantasy.mfr.init.MineFantasyItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JEIPlugin
public class JEIIntegration implements IModPlugin {

	public JEIIntegration() {
	}

	@Override
	public void registerCategories(@Nonnull IRecipeCategoryRegistration registry) {

		if (!ConfigIntegration.jeiIntegration) {
			return;
		}

		JeiHelpers jeiHelpers = Internal.getHelpers();
		GuiHelper guiHelper = jeiHelpers.getGuiHelper();

		registry.addRecipeCategories(new JEICarpenterRecipeCategory(registry));
		registry.addRecipeCategories(new JEIAnvilRecipeCategory(registry));
		registry.addRecipeCategories(new JEIBigFurnaceRecipeCategory(registry, guiHelper));
		registry.addRecipeCategories(new JEIAlloyRecipeCategory(registry, guiHelper));
		registry.addRecipeCategories(new JEIBloomeryRecipeCategory(registry, guiHelper));
		registry.addRecipeCategories(new JEIBlastFurnaceRecipeCategory(registry, guiHelper));
		registry.addRecipeCategories(new JEIQuernRecipeCategory(registry, guiHelper));
		registry.addRecipeCategories(new JEITannerRecipeCategory(registry));
		registry.addRecipeCategories(new JEIRoastRecipeCategory(registry, guiHelper, 0, MineFantasyBlocks.OVEN, "oven"));
		registry.addRecipeCategories(new JEIRoastRecipeCategory(registry, guiHelper, 56, MineFantasyBlocks.STOVE, "stovetop"));
		registry.addRecipeCategories(new JEIKitchenBenchRecipeCategory(registry));
		registry.addRecipeCategories(new JEISalvageRecipeCategory(registry));
		registry.addRecipeCategories(new JEITransformationRecipeCategory(registry));
		registry.addRecipeCategories(new JEISpecialRecipeCategory(registry));
	}

	@Override
	public void register(@Nonnull IModRegistry registry) {

		if (!ConfigIntegration.jeiIntegration)
			return;

		// UNUSED
		// If we want to hide some items, those can be listed here
		IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();
		// e.g.:
		// blacklist.addIngredientToBlacklist(MineFantasyItems.ANCIENT_JEWEL_ADAMANT);
		// /UNUSED

		IStackHelper stackHelper = registry.getJeiHelpers().getStackHelper();

		List<ItemStack> fuelItemStacks = new ArrayList<>(registry.getIngredientRegistry().getFuels());

		addItemExtendedInfo(registry);

		registry.addRecipes(JEICarpenterRecipeCategory.generateRecipes(stackHelper), JEICarpenterRecipeCategory.UID);
		registry.addRecipes(JEIAnvilRecipeCategory.generateRecipes(stackHelper), JEIAnvilRecipeCategory.UID);
		registry.addRecipes(JEIBigFurnaceRecipeCategory.generateRecipes(stackHelper), JEIBigFurnaceRecipeCategory.UID);
		registry.addRecipes(JEIAlloyRecipeCategory.generateRecipes(stackHelper), JEIAlloyRecipeCategory.UID);
		registry.addRecipes(JEIBloomeryRecipeCategory.generateRecipes(stackHelper, fuelItemStacks), JEIBloomeryRecipeCategory.UID);
		registry.addRecipes(JEIBlastFurnaceRecipeCategory.generateRecipes(stackHelper, fuelItemStacks), JEIBlastFurnaceRecipeCategory.UID);
		registry.addRecipes(JEIQuernRecipeCategory.generateRecipes(stackHelper), JEIQuernRecipeCategory.UID);
		registry.addRecipes(JEITannerRecipeCategory.generateRecipes(stackHelper), JEITannerRecipeCategory.UID);
		registry.addRecipes(JEIRoastRecipeCategory.generateRecipes(stackHelper, true), "minefantasyreforged:oven");
		registry.addRecipes(JEIRoastRecipeCategory.generateRecipes(stackHelper, false), "minefantasyreforged:stovetop");
		registry.addRecipes(JEIKitchenBenchRecipeCategory.generateRecipes(stackHelper), JEIKitchenBenchRecipeCategory.UID);
		registry.addRecipes(JEISalvageRecipeCategory.generateRecipes(stackHelper), JEISalvageRecipeCategory.UID);
		registry.addRecipes(JEITransformationRecipeCategory.generateRecipes(), JEITransformationRecipeCategory.UID);
		registry.addRecipes(JEISpecialRecipeCategory.generateRecipes(stackHelper), JEISpecialRecipeCategory.UID);
	}

	private static void addItemExtendedInfo(IModRegistry registry) {
		addExtendedInfo(registry, MineFantasyItems.FLUX, ".desc_extended");

		addExtendedInfo(registry, MineFantasyItems.BOWL_WATER_SALT, ".desc_extended");

		addExtendedInfo(registry, MineFantasyBlocks.CHEESE_WHEEL, ".desc_extended");
		addExtendedInfo(registry, MineFantasyItems.CHEESE_SLICE, ".desc_extended");

		addExtendedInfo(registry, MineFantasyBlocks.PIE_MEAT, ".desc_extended");
		addExtendedInfo(registry, MineFantasyItems.MEATPIE_SLICE, ".desc_extended");
		addExtendedInfo(registry, MineFantasyBlocks.PIE_APPLE, ".desc_extended");
		addExtendedInfo(registry, MineFantasyItems.PIESLICE_APPLE, ".desc_extended");
		addExtendedInfo(registry, MineFantasyBlocks.PIE_BERRY, ".desc_extended");
		addExtendedInfo(registry, MineFantasyItems.PIESLICE_BERRY, ".desc_extended");
		addExtendedInfo(registry, MineFantasyBlocks.PIE_SHEPARDS, ".desc_extended");
		addExtendedInfo(registry, MineFantasyItems.PIESLICE_SHEPARDS, ".desc_extended");

		addExtendedInfo(registry, MineFantasyBlocks.CAKE_VANILLA, ".desc_extended");
		addExtendedInfo(registry, MineFantasyItems.CAKE_SLICE, ".desc_extended");
		addExtendedInfo(registry, MineFantasyBlocks.CAKE_CARROT, ".desc_extended");
		addExtendedInfo(registry, MineFantasyItems.CARROTCAKE_SLICE, ".desc_extended");
		addExtendedInfo(registry, MineFantasyBlocks.CAKE_CHOCOLATE, ".desc_extended");
		addExtendedInfo(registry, MineFantasyItems.CHOCCAKE_SLICE, ".desc_extended");
		addExtendedInfo(registry, MineFantasyBlocks.CAKE_BF, ".desc_extended");
		addExtendedInfo(registry, MineFantasyItems.BFCAKE_SLICE, ".desc_extended");

		addExtendedInfo(registry, MineFantasyItems.EXPLODING_ARROW, ".desc_extended");
		addExtendedInfo(registry, MineFantasyItems.EXPLODING_BOLT, ".desc_extended");
		addExtendedInfo(registry, MineFantasyItems.BOMB_CASING_ARROW, ".desc_extended");
		addExtendedInfo(registry, MineFantasyItems.BOMB_CASING_BOLT, ".desc_extended");

		addExtendedInfoCustom(registry, MineFantasyItems.BOMB_CUSTOM, "item.bomb_custom.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.MINE_CUSTOM, "item.mine_custom.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.BLACKPOWDER, "item.bomb_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.BLACKPOWDER_ADVANCED, "item.bomb_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.SHRAPNEL, "item.bomb_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.MAGMA_CREAM_REFINED, "item.bomb_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.BOMB_FUSE, "item.bomb_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.BOMB_FUSE_LONG, "item.bomb_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.BOMB_CASING_CERAMIC, "item.bomb_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.MINE_CASING_CERAMIC, "item.bomb_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.BOMB_CASING_IRON, "item.bomb_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.MINE_CASING_IRON, "item.bomb_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.BOMB_CASING_OBSIDIAN, "item.bomb_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.MINE_CASING_OBSIDIAN, "item.bomb_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.BOMB_CASING_CRYSTAL, "item.bomb_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.MINE_CASING_CRYSTAL, "item.bomb_component.desc_extended");

		addExtendedInfo(registry, MineFantasyItems.CROSSBOW_CUSTOM, ".desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.CROSSBOW_ARMS_BASIC, "item.crossbow_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.CROSSBOW_ARMS_LIGHT, "item.crossbow_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.CROSSBOW_ARMS_HEAVY, "item.crossbow_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.CROSSBOW_ARMS_ADVANCED, "item.crossbow_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.CROSSBOW_STOCK_WOOD, "item.crossbow_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.CROSSBOW_STOCK_IRON, "item.crossbow_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.CROSSBOW_HANDLE_WOOD, "item.crossbow_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.CROSSBOW_AMMO, "item.crossbow_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.CROSSBOW_SCOPE, "item.crossbow_component.desc_extended");
		addExtendedInfoCustom(registry, MineFantasyItems.CROSSBOW_BAYONET, "item.crossbow_component.desc_extended");
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
		subtypeRegistry.useNbtForSubtypes(MineFantasyItems.BAR);
	}

	private static void addExtendedInfo(IModRegistry registry, Item item, String... suffixes) {
		NonNullList<ItemStack> subItems = NonNullList.create();
		item.getSubItems(item.getCreativeTab(), subItems);
		for (ItemStack stack : subItems)
			addExtendedInfo(registry, stack, suffixes);
	}

	private static void addExtendedInfo(IModRegistry registry, Block block, String... suffixes) {
		Item item = Item.getItemFromBlock(block);
		NonNullList<ItemStack> subItems = NonNullList.create();
		item.getSubItems(item.getCreativeTab(), subItems);
		for (ItemStack stack : subItems)
			addExtendedInfo(registry, stack, suffixes);
	}

	private static void addExtendedInfo(IModRegistry registry, ItemStack stack, String... suffixes) {
		String prefix = stack.getItem().getTranslationKey(stack);
		String[] keys = Arrays.stream(suffixes).map(s -> prefix + s).toArray(String[]::new);
		registry.addIngredientInfo(stack, VanillaTypes.ITEM, keys);
	}

	private static void addExtendedInfoCustom(IModRegistry registry, Item item, String key) {
		addExtendedInfoCustom(registry, new ItemStack(item), key);
	}

	private static void addExtendedInfoCustom(IModRegistry registry, ItemStack stack, String key) {
		registry.addIngredientInfo(stack, VanillaTypes.ITEM, key);
	}
}
