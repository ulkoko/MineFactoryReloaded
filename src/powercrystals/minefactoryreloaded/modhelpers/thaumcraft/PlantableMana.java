//crudely copied from PlantableCocoa

package powercrystals.minefactoryreloaded.modhelpers.thaumcraft;

import powercrystals.minefactoryreloaded.farmables.plantables.PlantableStandard;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.tileentity.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;


import powercrystals.minefactoryreloaded.api.ReplacementBlock;

public class PlantableMana extends PlantableStandard
{

	public PlantableMana(Item source, Block plantedBlock)
	{
		this(source, plantedBlock, WILDCARD);
	}


			
	public PlantableMana(Item source, Block plantedBlock, int validMeta)
	{
		super(source, plantedBlock, validMeta);
		_plantedBlock = new ReplacementBlock(_block) {
			@Override
			public int getMeta(World world, int x, int y, int z, ItemStack stack)
			{
				int meta = 0;
// kept this because I needed to accellerate growth for testing purposes and more importantly because I have no idea what i'm doing and didn't want to break anything
				return meta;
			}
		};
	}

	@Override
	public boolean canBePlantedHere(World world, int x, int y, int z, ItemStack stack)
	{
		if (!world.isAirBlock(x, y, z))
			return false;

		return isNextToAcceptableLog(world, x, y, z);
	}

		
	protected boolean isNextToAcceptableLog(World world, int x, int y, int z) {
		boolean isMagic = false;
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
			if (biome != null)
						isMagic = BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.MAGICAL);
					//	System.out.println("mfc-tc-planter: isbiome:"+isMagic +" isLog:"+ isGoodLog(world, x, y-1, z) );
			return isMagic &&
							isGoodLog(world, x, y+1, z);
							
	}

	
	protected boolean isGoodLog(World world, int x, int y, int z) {
		Block id = world.getBlock(x, y, z);
		Block tcLog = GameRegistry.findBlock("Thaumcraft", "blockMagicalLog");
		return id == tcLog || id.equals(Blocks.log);
	}
				
				
				
				
	@Override
	public void postPlant(World world, int x, int y, int z, ItemStack stack)
	{
		NBTTagList aspects = stack.stackTagCompound.getTagList( "Aspects" , 10);
		//	System.out.println("Aspect_count:"+aspects.tagCount()); //should be one but who knows
	
		String aspectstr= "herba"; //because pods without aspect seem to drop herba beans as well
		
		for (int i = 0; i < aspects.tagCount(); i++){
			NBTTagCompound aspect = (NBTTagCompound)aspects.getCompoundTagAt(i);
			if (aspect.hasKey("key")){
				//System.out.println("asp:"+aspect.getString("key"));
				aspectstr = aspect.getString("key");
			
			}
		}

		//	System.out.println("asp2:"+stack.stackTagCompound.hasKey("Aspects"));
	
		TileEntity te = world.getTileEntity(x, y, z);
	
		if (te != null) {
			NBTTagCompound tag = new NBTTagCompound();
			te.writeToNBT(tag);
			tag.setString("aspect", aspectstr);
			te.readFromNBT(tag);
		} //else  System.out.println("huh, no tile entity?");
	
	
	
	}
				

}
