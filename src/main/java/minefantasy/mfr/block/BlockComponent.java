package minefantasy.mfr.block;

import minefantasy.mfr.constants.Constants;
import minefantasy.mfr.init.MineFantasyBlocks;
import minefantasy.mfr.tile.TileEntityBase;
import minefantasy.mfr.tile.TileEntityComponent;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockComponent extends BlockTileEntity<TileEntityComponent> {
	public static final PropertyDirection FACING = BlockHorizontal.FACING;
	public static final PropertyBool PERSIST = PropertyBool.create("persist");

	public BlockComponent() {
		super(Material.CIRCUITS);

		setRegistryName("component");
		setTranslationKey("component");
		this.setHardness(1F);
		this.setResistance(1F);

	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, PERSIST);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityComponent();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState()
				.withProperty(PERSIST, (meta & 4) != 0)
				.withProperty(FACING, EnumFacing.byHorizontalIndex(meta & 3));
	}

	public int getMetaFromState(IBlockState state) {
		int i = 0;
		i = i | state.getValue(FACING).getHorizontalIndex();

		if (state.getValue(PERSIST))
		{
			i |= 4;
		}

		return i;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState()
				.withProperty(FACING, placer.getHorizontalFacing().getOpposite())
				.withProperty(PERSIST, false);
	}

	/**
	 * Checks if this block can be placed exactly at the given position.
	 */
	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		return world.getBlockState(pos).getMaterial() != Material.WATER
				&& world.getBlockState(pos).getMaterial() != Material.LAVA
				&& world.isSideSolid(pos.down(), EnumFacing.UP);
	}

	public static int placeComponent(EntityPlayer user, ItemStack item, World world, BlockPos pos, EnumFacing facing,
			float hitX, float hitY, float hitZ, EntityPlayer player, EnumHand hand, String type, String tex) {
		if (world.isAirBlock(pos) && canBuildOn(world, pos.down())){
			world.setBlockState(pos, MineFantasyBlocks.COMPONENTS.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, 0, player, hand), 2);

			int max = getStorageSize(type);
			int size = user.isSneaking() ? Math.min(item.getCount(), max) : 1;

			TileEntityComponent tile = (TileEntityComponent) world.getTileEntity(pos);
			if (tile != null) {
				ItemStack newItem = item.copy();
				newItem.setCount(size);
				tile.setItem(newItem, type, tex, max);
			}
			return size;
		}
		return 0;
	}

	public static boolean canBuildOn(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityComponent) {
			return ((TileEntityComponent) tile).isFull();
		}
		return world.isSideSolid(pos, EnumFacing.UP);
	}

	public static int getStorageSize(String id) {
		if (id == null)
			return 0;

		if (id.equalsIgnoreCase(Constants.StorageTextures.BAR))
			return 64;
		if (id.equalsIgnoreCase(Constants.StorageTextures.PLANK))
			return 64;
		if (id.equalsIgnoreCase(Constants.StorageTextures.POT))
			return 64;
		if (id.equalsIgnoreCase(Constants.StorageTextures.JUG))
			return 32;
		if (id.equalsIgnoreCase(Constants.StorageTextures.SHEET))
			return 16;
		if (id.equalsIgnoreCase(Constants.StorageTextures.BIGPLATE))
			return 8;

		return 0;
	}

	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer user) {
		useBlock(world, pos, user, true);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer user, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		useBlock(world, pos, user, false);
		return true;
	}

	private void useBlock(World world, BlockPos pos, EntityPlayer user, boolean leftClick) {
		ItemStack held = user.getHeldItemMainhand();
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityComponent) {
			((TileEntityComponent) tile).interact(user, held, leftClick);
		}
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityComponent) {
			((TileEntityComponent) tile).checkStack();
		}
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityComponent) {
			if (!((TileEntityComponent) tile).getInventory().getStackInSlot(0).isEmpty()) {
				ItemStack item = ((TileEntityComponent) tile).getInventory().getStackInSlot(0).copy();
				item.setCount(1);
				return item;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntityBase tile = getTile(world, pos);
		tile.onBlockBreak();
		world.removeTileEntity(pos);
	}

	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, BlockPos pos, net.minecraft.client.particle.ParticleManager manager) {
		return true;
	}

	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, net.minecraft.client.particle.ParticleManager manager) {
		return true;
	}

	@SideOnly(Side.CLIENT)
	public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity) {
		return true;
	}

	public boolean addLandingEffects(IBlockState state, net.minecraft.world.WorldServer worldObj, BlockPos blockPosition, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles) {
		return true;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		float height = 1.0F;
		TileEntity tile = source.getTileEntity(pos);
		if (tile instanceof TileEntityComponent) {
			height = ((TileEntityComponent) tile).getBlockHeight();
		}
		return new AxisAlignedBB(1 / 16F, 0F, 1 / 16F, 15 / 16F, 1 / 16F + height, 15 / 16F);
	}
}
