package fr.uranoscopidae.hatedmobs.common.blocks;

import fr.uranoscopidae.hatedmobs.HatedMobs;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockNet extends Block
{
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool DOWN = PropertyBool.create("down");
    public static final PropertyBool[] ALL_FACES = new PropertyBool[] {UP, NORTH, SOUTH, WEST, EAST, DOWN};
    protected static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.0D, 0.9375D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0625D, 1.0D, 1.0D);
    protected static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.9375D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.0625D);
    protected static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.9375D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D);

    public BlockNet()
    {
        super(Material.CLOTH);
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(UP, false)
                .withProperty(NORTH, false)
                .withProperty(EAST, false)
                .withProperty(SOUTH, false)
                .withProperty(WEST, false)
                .withProperty(DOWN, false)
        );
        this.setCreativeTab(HatedMobs.TAB);
        setRegistryName(new ResourceLocation(HatedMobs.MODID, "net"));
        setUnlocalizedName("net");
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        state = state.getActualState(source, pos);
        int i = 0;
        AxisAlignedBB axisalignedbb = FULL_BLOCK_AABB;

        if (state.getValue(UP))
        {
            axisalignedbb = UP_AABB;
            ++i;
        }

        if (state.getValue(NORTH))
        {
            axisalignedbb = NORTH_AABB;
            ++i;
        }

        if (state.getValue(EAST))
        {
            axisalignedbb = EAST_AABB;
            ++i;
        }

        if (state.getValue(SOUTH))
        {
            axisalignedbb = SOUTH_AABB;
            ++i;
        }

        if (state.getValue(WEST))
        {
            axisalignedbb = WEST_AABB;
            ++i;
        }

        if (state.getValue(DOWN))
        {
            axisalignedbb = DOWN_AABB;
            ++i;
        }

        return i == 1 ? axisalignedbb : FULL_BLOCK_AABB;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        // pass the event to the block behind
        BlockPos behind = pos.offset(facing.getOpposite());
        IBlockState stateBehind = worldIn.getBlockState(behind);
        return stateBehind.getBlock().onBlockActivated(worldIn, behind, stateBehind, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        BlockPos north = pos.north();
        BlockPos west = pos.west();
        BlockPos east = pos.east();
        BlockPos south = pos.south();
        BlockPos up = pos.up();
        BlockPos down = pos.down();
        return state
                .withProperty(NORTH, canPlaceOn(worldIn.getBlockState(north).getBlock()))
                .withProperty(EAST, canPlaceOn(worldIn.getBlockState(east).getBlock()))
                .withProperty(WEST, canPlaceOn(worldIn.getBlockState(west).getBlock()))
                .withProperty(SOUTH, canPlaceOn(worldIn.getBlockState(south).getBlock()))
                .withProperty(UP, canPlaceOn(worldIn.getBlockState(up).getBlock()))
                .withProperty(DOWN, canPlaceOn(worldIn.getBlockState(down).getBlock()));
    }

    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
    {
        BlockPos neighbor = pos.offset(side.getOpposite());
        Block block = worldIn.getBlockState(neighbor).getBlock();
        return canPlaceOn(block);
    }

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!canPlace(pos, worldIn))
        {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    private boolean canPlace(BlockPos pos, World world)
    {
        for(EnumFacing facing : EnumFacing.values())
        {
            BlockPos neighbor = pos.offset(facing);
            world.getBlockState(neighbor).getBlock();

            if(canPlaceOn(world.getBlockState(neighbor).getBlock()))
            {
                return true;
            }
        }
        return false;
    }

    private boolean canPlaceOn(Block block)
    {
        return block instanceof BlockDoor
                || block instanceof BlockTrapDoor
                || block instanceof BlockGlass
                || block instanceof BlockStainedGlass
                || block instanceof BlockPane;
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, ALL_FACES);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState();
    }

    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return getDefaultState();
    }

    public static PropertyBool getPropertyFor(EnumFacing facing) {
        if(facing == null)
            throw new NullPointerException("facing");
        switch (facing) {
            case UP:
                return UP;

            case DOWN:
                return DOWN;

            case EAST:
                return EAST;

            case WEST:
                return WEST;

            case NORTH:
                return NORTH;

            case SOUTH:
                return SOUTH;

            default: // should never happen
                return UP;
        }
    }

}
