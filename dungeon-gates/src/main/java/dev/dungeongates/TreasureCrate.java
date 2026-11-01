package dev.dungeongates;
import net.minecraft.core.*;import net.minecraft.nbt.CompoundTag;import net.minecraft.network.chat.Component;import net.minecraft.world.*;import net.minecraft.world.entity.player.Inventory;import net.minecraft.world.inventory.*;import net.minecraft.world.item.ItemStack;import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;import net.minecraft.world.level.block.state.BlockState;
public final class TreasureCrate extends RandomizableContainerBlockEntity {
 private NonNullList<ItemStack> items=NonNullList.withSize(27,ItemStack.EMPTY);
 public TreasureCrate(BlockPos p,BlockState s){super(DGRegistry.CRATE_BE.get(),p,s);}
 public int getContainerSize(){return 27;}
 protected NonNullList<ItemStack> getItems(){return items;}
 protected void setItems(NonNullList<ItemStack> i){items=i;}
 protected Component getDefaultName(){return Component.translatable(getBlockState().getBlock().getDescriptionId());}
 protected AbstractContainerMenu createMenu(int id,Inventory inv){return ChestMenu.threeRows(id,inv,this);}
 public void load(CompoundTag t){super.load(t);items=NonNullList.withSize(27,ItemStack.EMPTY);if(!tryLoadLootTable(t))ContainerHelper.loadAllItems(t,items);}
 protected void saveAdditional(CompoundTag t){super.saveAdditional(t);if(!trySaveLootTable(t))ContainerHelper.saveAllItems(t,items);}
}
