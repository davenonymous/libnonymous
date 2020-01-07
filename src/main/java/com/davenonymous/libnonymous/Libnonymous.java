package com.davenonymous.libnonymous;

import com.davenonymous.libnonymous.setup.IProxy;
import com.davenonymous.libnonymous.setup.ModSetup;
import com.davenonymous.libnonymous.setup.ProxyClient;
import com.davenonymous.libnonymous.setup.ProxyServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Libnonymous.MODID)
public class Libnonymous {
    public static final String MODID = "libnonymous";

    public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ProxyClient(), () -> () -> new ProxyServer());
    public static ModSetup setup = new ModSetup();

    public Libnonymous() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        setup.init();
        proxy.init();
    }
}
