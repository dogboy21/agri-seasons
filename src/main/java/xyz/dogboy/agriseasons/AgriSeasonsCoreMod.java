package xyz.dogboy.agriseasons;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.Name("agriseasons")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class AgriSeasonsCoreMod implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{ "xyz.dogboy.agriseasons.AgriSeasonsTransformer" };
    }

    @Override
    public String getModContainerClass() {
        return "xyz.dogboy.agriseasons.AgriSeasons";
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

}
