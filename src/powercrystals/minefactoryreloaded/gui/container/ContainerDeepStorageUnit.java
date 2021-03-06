package powercrystals.minefactoryreloaded.gui.container;

import cofh.lib.gui.slot.SlotInvisible;
import cofh.lib.gui.slot.SlotRemoveOnly;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import powercrystals.minefactoryreloaded.tile.machine.TileEntityDeepStorageUnit;

public class ContainerDeepStorageUnit extends ContainerFactoryInventory
{
	private TileEntityDeepStorageUnit _dsu;
	private int _tempQuantity;
	
	public ContainerDeepStorageUnit(TileEntityDeepStorageUnit dsu, InventoryPlayer inventoryPlayer)
	{
		super(dsu, inventoryPlayer);
		_dsu = dsu;
	}
	
	@Override
	protected void addSlots()
	{
		addSlotToContainer(new Slot(_te, 0, 134, 16));
		addSlotToContainer(new Slot(_te, 1, 152, 16));
		addSlotToContainer(new SlotRemoveOnly(_te, 2, 152, 49));
		for (int i = 34; i --> 0; )
			addSlotToContainer(new SlotInvisible(_te, 3 + i, 170, 16, 0));
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot)
	{
		ItemStack stack = null;
		Slot slotObject = (Slot) inventorySlots.get(slot);
		
		if(slotObject != null && slotObject.getHasStack())
		{
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();
			
			if(slot < 37)
			{
				if(!mergeItemStack(stackInSlot, 37, inventorySlots.size(), true))
				{
					return null;
				}
				for (int i = inventorySlots.size(); i --> 37; )
				{
					Slot slotObject2 = (Slot)inventorySlots.get(i);
					if (slotObject2 != null)
						for (int j = 0; j < this.crafters.size(); ++j)
							((ICrafting)this.crafters.get(j)).
								sendSlotContents(this, slotObject2.slotNumber, slotObject2.getStack());
				}
			}
			else if(!mergeItemStack(stackInSlot, 0, 36, false))
			{
				return null;
			}
			
			if(stackInSlot.stackSize == 0)
			{
				slotObject.putStack(null);
			}
			else
			{
				slotObject.onSlotChanged();
			}
			
			if(stackInSlot.stackSize == stack.stackSize)
			{
				return null;
			}
			
			slotObject.onPickupFromSlot(player, stackInSlot);
		}
		
		return stack;
	}
	
	@Override
	public void detectAndSendChanges()
	{
		for(int i = 0; i < crafters.size(); i++)
		{
			((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 200, _dsu.getQuantity());
			((ICrafting)crafters.get(i)).sendProgressBarUpdate(this, 201, _dsu.getQuantity() >> 16);
		}

		for (int i = 3; i --> 0; )
		{
			Slot slotObject = (Slot)inventorySlots.get(i);
			if (slotObject != null)
				for (int j = 0; j < this.crafters.size(); ++j)
					((ICrafting)this.crafters.get(j)).
						sendSlotContents(this, slotObject.slotNumber, slotObject.getStack());
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int var, int value)
	{
		super.updateProgressBar(var, value);
		
		if(var == 200) _tempQuantity = upcastShort(value);
		if(var == 201) _dsu.setQuantity(_tempQuantity | (value << 16));
	}
	
	@Override
	protected int getPlayerInventoryVerticalOffset()
	{
		return 124;
	}
	
	private int upcastShort(int value)
	{
		if(value < 0) value += 65536;
		return value;
	}
}
