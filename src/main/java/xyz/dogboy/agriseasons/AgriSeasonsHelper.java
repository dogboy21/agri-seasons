package xyz.dogboy.agriseasons;

import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.util.FuzzyStack;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sereneseasons.api.SSBlocks;
import sereneseasons.config.FertilityConfig;
import sereneseasons.init.ModFertility;

import java.util.HashSet;
import java.util.Set;

public class AgriSeasonsHelper {
    private static final Logger log = LogManager.getLogger("AgriSeasons");
    private static final Set<String> missingPlants = new HashSet<>();

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

        return crop.getSeed().getPlant().getSeedItems().stream()
                .map(FuzzyStack::toStack)
                .map(ItemStack::getItem)
                .map(item -> {
                    if (item instanceof ItemBlock) {
                        return ((ItemBlock) item).getBlock();
                    } else if (item instanceof ItemBlockSpecial) {
                        return ((ItemBlockSpecial) item).getBlock();
                    }

                    return item;
                })
                .filter(object -> object instanceof IPlantable)
                .map(item -> (IPlantable) item)
                .findFirst()
                .orElse(null);
    }

    public static boolean onGrowthTickPre(TileEntityCrop crop) {
        IPlantable plantable = AgriSeasonsHelper.getPlantable(crop);
        if (plantable == null) {
            if (crop.getSeed() != null) {
                AgriSeasonsHelper.printMissingPlantable(crop.getSeed().getPlant());
            }
            return false;
        }

        Block block = plantable.getPlant(crop.getCropWorld(), crop.getCropPos()).getBlock();

        boolean isFertile = ModFertility.isCropFertile(block.getRegistryName().toString(), crop.getCropWorld(), crop.getCropPos());
        if (FertilityConfig.general_category.seasonal_crops && !isFertile && !AgriSeasonsHelper.isGreenhouseGlassAboveBlock(crop.getCropWorld(), crop.getCropPos())) {
            if (FertilityConfig.general_category.crops_break) {
                crop.getCropWorld().destroyBlock(crop.getCropPos(), true);
            }

            return true;
        }

        return false;
    }

    private static void printMissingPlantable(IAgriPlant plant) {
        if (!AgriSeasonsHelper.missingPlants.add(plant.getId())) {
            return;
        }

        AgriSeasonsHelper.log.warn("Unable to find valid plantable for AgriCraft plant " + plant + ". Is it misconfigured?");
        AgriSeasonsHelper.log.warn("Plant identification:");
        AgriSeasonsHelper.log.warn("  ID: " + plant.getId());
        AgriSeasonsHelper.log.warn("  Plant Name: " + plant.getPlantName());
        AgriSeasonsHelper.log.warn("  Seed Name: " + plant.getSeedName());
        AgriSeasonsHelper.log.warn("  Seed Items:");
        plant.getSeedItems().forEach(stack -> AgriSeasonsHelper.log.warn("    - " + stack.toString() + " (" + stack.toStack().getItem().getClass().getCanonicalName() + ")"));
    }

}
