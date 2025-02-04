package igentuman.bfr.common.recipes;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

public class StackHelper {

    public static ItemStack getStackFromString(String name,int meta)
    {
        return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(name)),1, meta);
    }

    public static FluidStack getFluidStackFromString(String name,int amount)
    {
        return new FluidStack(FluidRegistry.getFluid(name),amount);
    }

    public static boolean fluidExists(String name) {
        return FluidRegistry.getRegisteredFluids().keySet().contains(name.toLowerCase(Locale.ROOT));
    }

    public static boolean fluidsExist(String... names) {
        String[] var1 = names;
        int var2 = names.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            String name = var1[var3];
            if (!fluidExists(name)) {
                return false;
            }
        }

        return true;
    }

    public static ItemStack fixItemStack(Object object) {
        if (object instanceof ItemStack) {
            ItemStack stack = ((ItemStack)object).copy();
            if (stack.getCount() == 0) {
                stack.setCount(1);
            }

            return stack;
        } else if (object instanceof Item) {
            return new ItemStack((Item)object, 1);
        } else if (!(object instanceof Block)) {
            throw new RuntimeException(String.format("Invalid ItemStack: %s", object));
        } else {
            return new ItemStack((Block)object, 1);
        }
    }

    public static ItemStack blockStateToStack(IBlockState state) {
        if (state == null) {
            return ItemStack.EMPTY;
        } else {
            Block block = state.getBlock();
            if (block == null) {
                return ItemStack.EMPTY;
            } else {
                int meta = block.getMetaFromState(state);
                return new ItemStack(block, 1, meta);
            }
        }
    }

    public static IBlockState getBlockStateFromStack(ItemStack stack) {
        if (stack == null) {
            return null;
        } else if (stack.getItem() == Items.AIR) {
            return Blocks.AIR.getDefaultState();
        } else if (stack.isEmpty()) {
            return null;
        } else {
            int meta = getMetadata(stack);
            Item item = stack.getItem();
            if (!(item instanceof ItemBlock)) {
                return null;
            } else {
                ItemBlock itemBlock = (ItemBlock)item;
                return itemBlock.getBlock().getStateFromMeta(meta);
            }
        }
    }

    public static int getMetadata(ItemStack stack) {
        return Items.DIAMOND.getMetadata(stack);
    }

    public static ItemStack changeStackSize(ItemStack stack, int size) {
        ItemStack newStack = stack.copy();
        newStack.setCount(size);
        return newStack.copy();
    }

    public static String stackPath(ItemStack stack) {
        return ((ResourceLocation)Item.REGISTRY.getNameForObject(stack.getItem())).getPath();
    }

    public static String stackName(ItemStack stack) {
        ResourceLocation resourcelocation = (ResourceLocation)Item.REGISTRY.getNameForObject(stack.getItem());
        return resourcelocation == null ? "null" : resourcelocation.toString() + ":" + getMetadata(stack);
    }

    public static String stackListNames(List<ItemStack> list) {
        String names = "";

        ItemStack stack;
        for(Iterator var2 = list.iterator(); var2.hasNext(); names = names + ", " + stackName(stack)) {
            stack = (ItemStack)var2.next();
        }

        return names.substring(2);
    }

    public static boolean areItemStackTagsEqual(ItemStack stackA, ItemStack stackB) {
        if (stackA.isEmpty() && stackB.isEmpty()) {
            return true;
        } else if (!stackA.isEmpty() && !stackB.isEmpty()) {
            if (stackA.getTagCompound() == null && stackB.getTagCompound() != null) {
                return false;
            } else {
                return stackA.getTagCompound() == null || stackA.getTagCompound().equals(stackB.getTagCompound());
            }
        } else {
            return false;
        }
    }

    public static ItemStack getBucket(FluidStack fluidStack) {
        return FluidUtil.getFilledBucket(fluidStack);
    }

    public static ItemStack getBucket(String fluidName) {
        return getBucket(new FluidStack(FluidRegistry.getFluid(fluidName), 1000));
    }

    public static boolean doFluidStacksMatch(FluidStack stack1, FluidStack stack2)
    {
        return stack1.isFluidEqual(stack2);
    }

    public static FluidStack consumeFluid(FluidStack stack, int amount)
    {
        if (stack.amount > amount)
        {
            stack.amount -= amount;
            return stack;
        }
        return stack;
    }
}
