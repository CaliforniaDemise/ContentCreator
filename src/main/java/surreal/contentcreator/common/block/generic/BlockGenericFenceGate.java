package surreal.contentcreator.common.block.generic;

import net.minecraft.block.*;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import surreal.contentcreator.types.CTSoundType;

import javax.annotation.Nullable;

@SuppressWarnings("all")
public class BlockGenericFenceGate extends Block implements IGenericBlock {
    protected static final AxisAlignedBB AABB_HITBOX_ZAXIS = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.0D, 0.625D);
    protected static final AxisAlignedBB AABB_HITBOX_XAXIS = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.0D, 1.0D);
    protected static final AxisAlignedBB AABB_HITBOX_ZAXIS_INWALL = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 0.8125D, 0.625D);
    protected static final AxisAlignedBB AABB_HITBOX_XAXIS_INWALL = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 0.8125D, 1.0D);
    protected static final AxisAlignedBB AABB_COLLISION_BOX_ZAXIS = new AxisAlignedBB(0.0D, 0.0D, 0.375D, 1.0D, 1.5D, 0.625D);
    protected static final AxisAlignedBB AABB_COLLISION_BOX_XAXIS = new AxisAlignedBB(0.375D, 0.0D, 0.0D, 0.625D, 1.5D, 1.0D);

    public BlockGenericFenceGate(Material material, MapColor mapColor)
    {
        super(material, mapColor);
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockFenceGate.OPEN, Boolean.FALSE).withProperty(BlockFenceGate.POWERED, false).withProperty(BlockFenceGate.IN_WALL, false));
        this.setCreativeTab(CreativeTabs.REDSTONE);
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        state = this.getActualState(state, source, pos);

        if (((Boolean)state.getValue(BlockFenceGate.IN_WALL)).booleanValue())
        {
            return ((EnumFacing)state.getValue(BlockFenceGate.FACING)).getAxis() == EnumFacing.Axis.X ? AABB_HITBOX_XAXIS_INWALL : AABB_HITBOX_ZAXIS_INWALL;
        }
        else
        {
            return ((EnumFacing)state.getValue(BlockFenceGate.FACING)).getAxis() == EnumFacing.Axis.X ? AABB_HITBOX_XAXIS : AABB_HITBOX_ZAXIS;
        }
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        EnumFacing.Axis enumfacing$axis = ((EnumFacing)state.getValue(BlockFenceGate.FACING)).getAxis();

        if (enumfacing$axis == EnumFacing.Axis.Z && (worldIn.getBlockState(pos.west()).getBlock() instanceof BlockWall || worldIn.getBlockState(pos.east()).getBlock() instanceof BlockWall) || enumfacing$axis == EnumFacing.Axis.X && (worldIn.getBlockState(pos.north()).getBlock() instanceof BlockWall || worldIn.getBlockState(pos.south()).getBlock() instanceof BlockWall))
        {
            state = state.withProperty(BlockFenceGate.IN_WALL, Boolean.valueOf(true));
        }

        return state;
    }

    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(BlockFenceGate.FACING, rot.rotate((EnumFacing)state.getValue(BlockFenceGate.FACING)));
    }

    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(BlockFenceGate.FACING)));
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos.down()).getMaterial().isSolid() ? super.canPlaceBlockAt(worldIn, pos) : false;
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        if (((Boolean)blockState.getValue(BlockFenceGate.OPEN)).booleanValue())
        {
            return NULL_AABB;
        }
        else
        {
            return ((EnumFacing)blockState.getValue(BlockFenceGate.FACING)).getAxis() == EnumFacing.Axis.Z ? AABB_COLLISION_BOX_ZAXIS : AABB_COLLISION_BOX_XAXIS;
        }
    }

    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
    {
        return ((Boolean)worldIn.getBlockState(pos).getValue(BlockFenceGate.OPEN)).booleanValue();
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        boolean flag = worldIn.isBlockPowered(pos);
        return this.getDefaultState().withProperty(BlockFenceGate.FACING, placer.getHorizontalFacing()).withProperty(BlockFenceGate.OPEN, Boolean.valueOf(flag)).withProperty(BlockFenceGate.POWERED, Boolean.valueOf(flag)).withProperty(BlockFenceGate.IN_WALL, Boolean.valueOf(false));
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (((Boolean)state.getValue(BlockFenceGate.OPEN)).booleanValue())
        {
            state = state.withProperty(BlockFenceGate.OPEN, Boolean.valueOf(false));
            worldIn.setBlockState(pos, state, 10);
        }
        else
        {
            EnumFacing enumfacing = EnumFacing.fromAngle((double)playerIn.rotationYaw);

            if (state.getValue(BlockFenceGate.FACING) == enumfacing.getOpposite())
            {
                state = state.withProperty(BlockFenceGate.FACING, enumfacing);
            }

            state = state.withProperty(BlockFenceGate.OPEN, Boolean.valueOf(true));
            worldIn.setBlockState(pos, state, 10);
        }

        worldIn.playEvent(playerIn, ((Boolean)state.getValue(BlockFenceGate.OPEN)).booleanValue() ? 1008 : 1014, pos, 0);
        return true;
    }

    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote)
        {
            boolean flag = worldIn.isBlockPowered(pos);

            if (((Boolean)state.getValue(BlockFenceGate.POWERED)).booleanValue() != flag)
            {
                worldIn.setBlockState(pos, state.withProperty(BlockFenceGate.POWERED, Boolean.valueOf(flag)).withProperty(BlockFenceGate.OPEN, Boolean.valueOf(flag)), 2);

                if (((Boolean)state.getValue(BlockFenceGate.OPEN)).booleanValue() != flag)
                {
                    worldIn.playEvent((EntityPlayer)null, flag ? 1008 : 1014, pos, 0);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(BlockFenceGate.FACING, EnumFacing.getHorizontal(meta)).withProperty(BlockFenceGate.OPEN, Boolean.valueOf((meta & 4) != 0)).withProperty(BlockFenceGate.POWERED, Boolean.valueOf((meta & 8) != 0));
    }

    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | ((EnumFacing)state.getValue(BlockFenceGate.FACING)).getHorizontalIndex();

        if (((Boolean)state.getValue(BlockFenceGate.POWERED)).booleanValue())
        {
            i |= 8;
        }

        if (((Boolean)state.getValue(BlockFenceGate.OPEN)).booleanValue())
        {
            i |= 4;
        }

        return i;
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {BlockFenceGate.FACING, BlockFenceGate.OPEN, BlockFenceGate.POWERED, BlockFenceGate.IN_WALL});
    }

    @Override
    public boolean canBeConnectedTo(IBlockAccess world, BlockPos pos, EnumFacing facing)
    {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockFenceGate &&
                state.getBlockFaceShape(world, pos, facing) == BlockFaceShape.MIDDLE_POLE)
        {
            Block connector = world.getBlockState(pos.offset(facing)).getBlock();
            return connector instanceof BlockFence || connector instanceof BlockWall;
        }
        return false;
    }

    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        if (face != EnumFacing.UP && face != EnumFacing.DOWN)
        {
            return ((EnumFacing)state.getValue(BlockFenceGate.FACING)).getAxis() == face.rotateY().getAxis() ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.UNDEFINED;
        }
        else
        {
            return BlockFaceShape.UNDEFINED;
        }
    }

    @Override
    public void setSoundType(CTSoundType soundType) {
        setSoundType(soundType.getType());
    }
}
