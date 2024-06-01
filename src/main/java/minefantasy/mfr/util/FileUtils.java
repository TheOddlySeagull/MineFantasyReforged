package minefantasy.mfr.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.mixin.JsonContextAccessor;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class FileUtils {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private FileUtils() {}

	public static void createCustomDataDirectory(String directory) {
		// create custom data dirs if they don't exist
		File existTest = new File(directory);
		if (!existTest.exists()) {
			existTest.mkdirs();
		}
	}

	@Nonnull
	public static Boolean loadConstants(File source, String base, JsonContext ctx) {
		findFiles(source, base, (root, file) -> {
			Path relative = root.relativize(file);
			if (relative.getNameCount() > 1) {
				String extension = FilenameUtils.getExtension(file.toString());

				if (!extension.equals(Constants.JSON_FILE_EXT)) {
					return;
				}

				String modName = relative.getName(relative.getNameCount() - 2).toString();
				String fileName = FilenameUtils.removeExtension(relative.getFileName().toString());

				if (!Loader.isModLoaded(modName) || !fileName.equals("_constants")) {
					return;
				}

				if (Files.exists(file)) {
					BufferedReader reader = null;
					try {
						reader = Files.newBufferedReader(file);
						JsonObject[] jsonList = net.minecraft.util.JsonUtils.fromJson(GSON, reader, JsonObject[].class);
						if (jsonList != null) {
							Map<String, Ingredient> constants = ((JsonContextAccessor) ctx).getConstants();
							for (JsonObject json : jsonList) {
								String name = JsonUtils.getString(json, "name");
								Ingredient ingredient = CraftingHelper.getIngredient(json.get("ingredient"), ctx);
								constants.put(name, ingredient);
							}
							((JsonContextAccessor) ctx).setConstants(constants);
						}
					}
					catch (IOException e) {
						MineFantasyReforged.LOG.error("Error loading _constants.json: ", e);
					}
					finally {
						IOUtils.closeQuietly(reader);
					}
				}
			}
		});
		return true;
	}

	public static void findFiles(File source, String base, @Nullable BiConsumer<Path, Path> processor) {
		findFiles(source, base, null, processor);
	}

	public static void findFiles(File source, String base, @Nullable Function<Path, Boolean> preprocessor, @Nullable BiConsumer<Path, Path> processor) {
		FileSystem fs = null;
		try {
			@Nullable Path root = null;
			if (source.isFile()) {
				try {
					fs = FileSystems.newFileSystem(source.toPath(), null);
					root = fs.getPath("/" + base);
				}
				catch (IOException e) {
					System.out.println("Error loading FileSystem from jar: " + e);
					return;
				}
			} else if (source.isDirectory()) {
				root = source.toPath().resolve(base);
			}

			if (root == null || !Files.exists(root)) {
				return;
			}

			if (preprocessor != null) {
				Boolean cont = preprocessor.apply(root);
				if (cont == null || !cont) {
					return;
				}
			}

			if (processor != null) {
				Iterator<Path> itr;
				try {
					itr = Files.walk(root).iterator();
				}
				catch (IOException e) {
					System.out.println("Error iterating filesystem for: {}" + root + e);
					return;
				}

				while (itr.hasNext()) {
					processor.accept(root, itr.next());
				}
			}
		}
		finally {
			IOUtils.closeQuietly(fs);
		}
	}

	public static void exportToFile(File exportFile, List<String> rows) {
		if (!exportFile.exists()) {
			if (createFile(exportFile)) { return; }
		}
		try (FileWriter fileWriter = new FileWriter(exportFile); BufferedWriter writer = new BufferedWriter(fileWriter)) {
			for (String row : rows) {
				writer.write(row);
				writer.newLine();
			}
		}
		catch (IOException e) {
			System.out.println("\"Error exporting file: \"");
		}
	}

	private static boolean createFile(File exportFile) {
		try {
			if (!exportFile.getParentFile().exists() && !exportFile.getParentFile().mkdirs()) {
				System.out.println("Unable to create folders for file : " + exportFile.getAbsolutePath());
				return true;
			}
			if (!exportFile.createNewFile()) {
				System.out.println("Unable to open new file : " + exportFile.getAbsolutePath());
				return true;
			}
		}
		catch (IOException e) {
			System.out.println("Error opening file : " + exportFile.getAbsolutePath() + e);
			return true;
		}
		return false;
	}
}

