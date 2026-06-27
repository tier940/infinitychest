package com.github.tier940.infinitychest;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.cleanroommc.modularui.factory.TileEntityGuiFactory;

/**
 * Block backing for the InfinityChest. Right-click inserts the held stack; sneak right-click
 * extracts one stack. On break, the contents are spilled as one or more {@link ItemStack}s.
 */
public class BlockInfinityChest extends Block {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;

    // Vanilla chest's bounding box: a 14×14×14 cube inset 1 pixel from each side, with the top
    // truncated to 14 pixels — matches the chest model's visual footprint exactly.
    private static final AxisAlignedBB CHEST_AABB = new AxisAlignedBB(
            0.0625D, 0.0D, 0.0625D, 0.9375D, 0.875D, 0.9375D);

    public BlockInfinityChest() {
        super(Material.GLASS);
        setHardness(2.5F);
        setResistance(10.0F);
        setSoundType(SoundType.GLASS);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
                                            float hitX, float hitY, float hitZ, int meta,
                                            EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing dir = EnumFacing.byHorizontalIndex(meta);
        return getDefaultState().withProperty(FACING, dir);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return CHEST_AABB;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileInfinityChest();
    }

    // Not a fully opaque cube so the texture's alpha channel renders through and adjacent block
    // faces are not culled.
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileInfinityChest)) return false;
        TileInfinityChest chest = (TileInfinityChest) te;

        if (world.isRemote) return true;

        if (player.isSneaking()) {
            ItemStack template = chest.getTemplate();
            int amount = template.isEmpty() ? 0 : template.getMaxStackSize();
            ItemStack extracted = chest.extract(amount);
            if (extracted.isEmpty()) {
                player.sendStatusMessage(new TextComponentTranslation("message.infinitychest.empty"), true);
            } else if (!player.inventory.addItemStackToInventory(extracted)) {
                player.dropItem(extracted, false);
            }
            return true;
        }

        ItemStack held = player.getHeldItem(hand);
        if (held.isEmpty()) {
            world.playSound(null, pos, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS,
                    0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
            TileEntityGuiFactory.INSTANCE.open(player, pos);
            return true;
        }
        if (!chest.accepts(held)) return true;
        ItemStack leftover = chest.insert(held);
        player.setHeldItem(hand, leftover);
        return true;
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state,
                             @Nullable TileEntity te, ItemStack tool) {
        player.addStat(StatList.getBlockStats(this));
        player.addExhaustion(0.005F);
        if (!world.isRemote) {
            spawnAsEntity(world, pos, buildDrop(te));
        }
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos,
                         IBlockState state, int fortune) {
        // explosions/pistons: TE is still present at call time
        drops.add(buildDrop(world.getTileEntity(pos)));
    }

    private ItemStack buildDrop(@Nullable TileEntity te) {
        ItemStack stack = new ItemStack(this);
        if (!(te instanceof TileInfinityChest)) return stack;
        TileInfinityChest chest = (TileInfinityChest) te;
        if (chest.isEmpty()) return stack;
        NBTTagCompound teTag = chest.writeToNBT(new NBTTagCompound());
        teTag.removeTag("x");
        teTag.removeTag("y");
        teTag.removeTag("z");
        teTag.removeTag("id");
        NBTTagCompound stackTag = new NBTTagCompound();
        stackTag.setTag("BlockEntityTag", teTag);
        stack.setTagCompound(stackTag);
        return stack;
    }
}
