package minefantasy.mfr.block;

import minefantasy.mfr.init.MineFantasyTabs;
import minefantasy.mfr.mechanics.knowledge.ResearchLogic;
import minefantasy.mfr.tile.TileEntityCarpenter;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCarpenter extends BlockTileEntity<TileEntityCarpenter> {

	public static final PropertyDirection FACING = BlockHorizontal.FACING;

	public BlockCarpenter() {
		super(Material.WOOD);

		setRegistryName("carpenter_bench");
		setTranslationKey("carpenter_bench");
		this.setSoundType(SoundType.WOOD);
		this.setHardness(5F);
		this.setResistance(2F);
		this.setLightOpacity(0);
		this.setCreativeTab(MineFantasyTabs.tabUtil);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityCarpenter();
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.byIndex(meta);

		if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
			enumfacing = EnumFacing.NORTH;
		}

		return this.getDefaultState().withProperty(FACING, enumfacing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(FACING).getIndex();
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	/**
	 * Called upon block activation (right click on the block.)
	 * For the carpenter, clicking at the top attempts to progress the crafting (if the in-hand item is valid or the hand is empty), clicking on the side opens the gui
	 */
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		TileEntityCarpenter tile = (TileEntityCarpenter) getTile(world, pos);
		if (tile != null && (world.isAirBlock(pos.add(0, 1, 0)) || !world.isSideSolid(pos.add(0, 1, 0), EnumFacing.DOWN))) {
			if (facing != EnumFacing.UP || (!tile.tryCraft(player) && !world.isRemote)) {
				tile.openGUI(world, player);
			}
		}
		if (!world.isRemote) {
			ResearchLogic.syncData(player);
		}
		return true;
	}

	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer user) {
		TileEntityCarpenter tile = (TileEntityCarpenter) getTile(world, pos);
		if (tile != null) {
			tile.tryCraft(user);
		}
	}
}