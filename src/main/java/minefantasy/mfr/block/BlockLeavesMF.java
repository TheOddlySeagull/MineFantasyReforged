package minefantasy.mfr.block;

import minefantasy.mfr.MineFantasyReforged;
import minefantasy.mfr.init.MineFantasyBlocks;
import minefantasy.mfr.proxy.IClientRegister;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class BlockLeavesMF extends BlockLeaves implements IShearable, IClientRegister {

	private final int dropRate;

	public BlockLeavesMF(String baseWood, int droprate) {
		super();
		String name = baseWood.toLowerCase() + "_leaves";
		setRegistryName(name);
		setTranslationKey(name);
		this.dropRate = droprate;
		this.setTickRandomly(true);
		MineFantasyReforged.PROXY.setGraphicsLevel(this, true);
		setDefaultState(this.blockState.getBaseState().withProperty(CHECK_DECAY, Boolean.TRUE).withProperty(DECAYABLE, Boolean.valueOf(true)));
		MineFantasyReforged.PROXY.addClientRegister(this);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, CHECK_DECAY, DECAYABLE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(DECAYABLE, (meta & 4) == 0).withProperty(CHECK_DECAY, (meta & 8) > 0);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;

		if (!state.getValue(DECAYABLE)) {
			i |= 4;
		}

		if (state.getValue(CHECK_DECAY)) {
			i |= 8;
		}

		return i;
	}

	@Override
	public BlockPlanks.EnumType getWoodType(int meta) {
		return null;
	}

	@Override
	public int quantityDropped(Random rand) {
		return 1;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(
				this == MineFantasyBlocks.LEAVES_EBONY ? MineFantasyBlocks.SAPLING_EBONY //if ebony, drop ebony sapling
				: this == MineFantasyBlocks.LEAVES_IRONBARK ? MineFantasyBlocks.SAPLING_IRONBARK //if ironbark, drop ironbark sapling
				: MineFantasyBlocks.SAPLING_YEW); //if not anything else, drop yew sapling
	}

	@Override
	protected int getSaplingDropChance(IBlockState state) {
		return dropRate;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	@Nonnull
	@Override
	public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return NonNullList.withSize(1, new ItemStack(this));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "normal"));
	}
}