package xyz.dogboy.agriseasons;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.ModMetadata;

import javax.annotation.Nullable;
import java.security.cert.Certificate;

public class AgriSeasons extends DummyModContainer {

    public AgriSeasons() {
        super(new ModMetadata());
        this.getMetadata().modId = "agriseasons";
        this.getMetadata().name = "AgriSeasons";
        this.getMetadata().description = "Adds Serene Seasons compatibility to AgriCraft Crop Sticks";
        this.getMetadata().credits = "Dogboy21";
        this.getMetadata().version = Reference.version;
    }

    @Override
    @Nullable
    public Certificate getSigningCertificate() {
        Certificate[] certificates = this.getClass().getProtectionDomain().getCodeSource().getCertificates();
        return certificates != null ? certificates[0] : null;
    }

}
