package xyz.dogboy.agriseasons;

import com.infinityraider.agricraft.tiles.TileEntityCrop;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockReed;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import sereneseasons.api.SSBlocks;
import sereneseasons.config.FertilityConfig;
import sereneseasons.init.ModFertility;

public class AgriSeasonsHelper {

    private static boolean isGreenhouseGlassAboveBlock(World world, BlockPos cropPos) {
        for(int i = 0; i < FertilityConfig.general_category.greenhouse_glass_max_height; ++i) {
            if (world.getBlockState(cropPos.add(0, i + 1, 0)).getBlock().equals(SSBlocks.greenhouse_glass)) {
                return true;
            }
        }

        return false;
    }

    private static IPlantable getPlantable(TileEntityCrop crop) {
        if (crop.getSeed() == null) {
            return null;
        }

        Item item = crop.getSeed().getPlant().getSeed().getItem();
        if (item instanceof IPlantable) {
            return (IPlantable) item;
        }

        return null;
    }

    public static boolean onGrowthTickPre(TileEntityCrop crop) {
        IPlantable plantable = AgriSeasonsHelper.getPlantable(crop);
        if (plantable == null) {
            return false;
        }

        Block block = plantable.getPlant(crop.getCropWorld(), crop.getCropPos()).getBlock();

        boolean isFertile = ModFertility.isCropFertile(block.getRegistryName().toString(), crop.getCropWorld(), crop.getCropPos());
        if (FertilityConfig.general_category.seasonal_crops && !isFertile && !AgriSeasonsHelper.isGreenhouseGlassAboveBlock(crop.getCropWorld(), crop.getCropPos())) {
            if (FertilityConfig.general_category.crops_break && !(block instanceof BlockGrass) && !(block instanceof BlockReed)) {
                crop.getCropWorld().destroyBlock(crop.getCropPos(), true);
            }

            return true;
        }

        return false;
    }

}
