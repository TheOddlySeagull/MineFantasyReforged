package minefantasy.mfr.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.config.ConfigCrafting;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.recipe.factories.BigFurnaceRecipeFactory;
import minefantasy.mfr.recipe.types.BigFurnaceRecipeType;
import minefantasy.mfr.util.CustomToolHelper;
import minefantasy.mfr.util.FileUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CraftingManagerBigFurnace {

	public static final String RECIPE_FOLDER_PATH = Constants.ASSET_DIRECTORY + "/recipes_mfr/big_furnace_recipes/";

	public static final String CONFIG_RECIPE_DIRECTORY = "config/" + Constants.CONFIG_DIRECTORY + "/custom/recipes/big_furnace_recipes/";

	public CraftingManagerBigFurnace() {

	}

	private static final IForgeRegistry<BigFurnaceRecipeBase> BIG_FURNACE_RECIPES = (new RegistryBuilder<BigFurnaceRecipeBase>()).setName(new ResourceLocation(MineFantasyReforged.MOD_ID, "big_furnace_recipes")).setType(BigFurnaceRecipeBase.class).setMaxID(Integer.MAX_VALUE >> 5).disableSaving().allowModification().create();
	private static final Set<String> BIG_FURNACE_RESEARCHES = new HashSet<>();

	public static void init() {
		//call this so that the static final gets initialized at proper time
	}

	public static Collection<BigFurnaceRecipeBase> getRecipes() {
		return BIG_FURNACE_RECIPES.getValuesCollection();
	}

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final BigFurnaceRecipeFactory factory = new BigFurnaceRecipeFactory();

	public static void loadRecipes() {
		ModContainer modContainer = Loader.instance().activeModContainer();

		FileUtils.createCustomDataDirectory(CONFIG_RECIPE_DIRECTORY);
		Loader.instance().getActiveModList().forEach(m -> CraftingHelper
				.loadFactories(m, String.format(RECIPE_FOLDER_PATH, m.getModId()), CraftingHelper.CONDITIONS));
		//noinspection ConstantConditions
		loadRecipes(modContainer, new File(CONFIG_RECIPE_DIRECTORY), "");
		Loader.instance().getActiveModList().forEach(m ->
				loadRecipes(m, m.getSource(), String.format(RECIPE_FOLDER_PATH, m.getModId())));

		Loader.instance().setActiveModContainer(modContainer);
	}

	private static void loadRecipes(ModContainer mod, File source, String base) {
		JsonContext ctx = new JsonContext(mod.getModId());

		FileUtils.findFiles(source, base, root -> FileUtils.loadConstants(source, base, ctx), (root, file) -> {
			Path relative = root.relativize(file);
			if (relative.getNameCount() > 1) {
				String extension = FilenameUtils.getExtension(file.toString());

				if (!extension.equals(Constants.JSON_FILE_EXT)) {
					return;
				}

				String modName = relative.getName(relative.getNameCount() - 2).toString();
				String fileName = FilenameUtils.removeExtension(relative.getFileName().toString());

				if (!Loader.isModLoaded(modName) || fileName.startsWith("_")) {
					return;
				}

				Loader.instance().setActiveModContainer(mod);

				if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
					return;

				ResourceLocation key = new ResourceLocation(ctx.getModId(), fileName);

				BufferedReader reader = null;
				try {
					reader = Files.newBufferedReader(file);
					JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);

					String type = ctx.appendModId(JsonUtils.getString(json, "type"));
					if (Loader.isModLoaded(mod.getModId())) {
						if (BigFurnaceRecipeType.getByNameWithModId(type, mod.getModId()) != BigFurnaceRecipeType.NONE) {
							BigFurnaceRecipeBase recipe = factory.parse(ctx, json);
							if (CraftingHelper.processConditions(json, "conditions", ctx)) {
								addRecipe(recipe, mod.getModId().equals(MineFantasyReforged.MOD_ID), key);
							}
						} else {
							MineFantasyReforged.LOG.info("Skipping recipe {} of type {} because it's not a MFR Big Furnace recipe", key, type);
						}
					}
					else {
						MineFantasyReforged.LOG.info("Skipping recipe {} of type {} because it the mod it depends on is not loaded", key, type);
					}
				}
				catch (JsonParseException e) {
					MineFantasyReforged.LOG.error("Parsing error loading recipe {}", key, e);
				}
				catch (IOException e) {
					MineFantasyReforged.LOG.error("Couldn't read recipe {} from {}", key, file, e);
				}
				finally {
					IOUtils.closeQuietly(reader);
				}
			}
		});
	}

	public static void addRecipe(BigFurnaceRecipeBase recipe, boolean checkForExistence, ResourceLocation key) {
		ItemStack itemStack = recipe.getBigFurnaceRecipeOutput();
		if (ConfigCrafting.isBigFurnaceItemCraftable(itemStack)) {
			NonNullList<ItemStack> subItems = NonNullList.create();

			recipe.setRegistryName(key);
			itemStack.getItem().getSubItems(itemStack.getItem().getCreativeTab(), subItems);
			if (subItems.stream().anyMatch(s -> recipe.getBigFurnaceRecipeOutput().isItemEqual(s))
					&& (!checkForExistence || !BIG_FURNACE_RECIPES.containsKey(recipe.getRegistryName()))) {
				BIG_FURNACE_RECIPES.register(recipe);
				String requiredResearch = recipe.getRequiredResearch();
				if (!requiredResearch.equals("none")) {
					BIG_FURNACE_RESEARCHES.add(requiredResearch);
				}
			}
		}
	}

	public static BigFurnaceRecipeBase findMatchingRecipe(ItemStack input, Set<String> knownResearches) {
		//// Normal, registered recipes.

		for (BigFurnaceRecipeBase rec : getRecipes()) {
			if (rec.matches(input)) {
				if (rec.getRequiredResearch().equals("none")
						|| knownResearches.contains(rec.getRequiredResearch())) {
					return rec;
				}
			}
		}
		return null;
	}

	public static BigFurnaceRecipeBase findRecipeByOutput(ItemStack output) {
		for (BigFurnaceRecipeBase recipe : getRecipes()) {
			if (CustomToolHelper.areEqual(recipe.getBigFurnaceRecipeOutput(), output)) {
				return recipe;
			}
		}
		return null;
	}

	public static BigFurnaceRecipeBase getRecipeByName(String modId, String name) {
		ResourceLocation resourceLocation = new ResourceLocation(modId, name);
		if (!BIG_FURNACE_RECIPES.containsKey(resourceLocation)) {
			MineFantasyReforged.LOG.error("Big Furnace Recipe Registry does not contain recipe: {}", name);
		}
		return BIG_FURNACE_RECIPES.getValue(resourceLocation);
	}

	public static List<BigFurnaceRecipeBase> getRecipesByName(String modId, String... names) {
		List<BigFurnaceRecipeBase> recipes = new ArrayList<>();
		for (String name : names) {
			recipes.add(getRecipeByName(modId, name));
		}
		return recipes;
	}

	public static BigFurnaceRecipeBase getRecipeByResourceLocation(ResourceLocation resourceLocation) {
		return BIG_FURNACE_RECIPES.getValue(resourceLocation);
	}

	public static Set<String> getBigFurnaceResearches() {
		return BIG_FURNACE_RESEARCHES;
	}
}
