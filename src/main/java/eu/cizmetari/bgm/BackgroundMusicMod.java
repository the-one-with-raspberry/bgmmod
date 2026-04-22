package eu.cizmetari.bgm;

import eu.cizmetari.bgm.additions.LoadPersistentObject;
import eu.cizmetari.bgm.sound.SoundEntry;
import eu.cizmetari.bgm.sound.Sounds;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.HashMap;
import java.util.Map;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(value = BackgroundMusicMod.MOD_ID, dist = Dist.CLIENT)
public class BackgroundMusicMod {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "bgm";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static LoadPersistentObject<Map<Integer, SoundEntry>> PLAYING_SOUNDS = new LoadPersistentObject<>(HashMap::new);

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public BackgroundMusicMod(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (BackgroundMusicMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        NeoForge.EVENT_BUS.register(ModCommands.class);
        NeoForge.EVENT_BUS.register(PLAYING_SOUNDS);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code
//        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    @SubscribeEvent
    private static void clearSounds(LevelEvent.Unload ignoredCtx) {
        for (int sid : PLAYING_SOUNDS.get().keySet()) {
            Sounds.stopSound(sid);
        }
    }

    public static void clearSounds() {
        for (int sid : PLAYING_SOUNDS.get().keySet()) {
            Sounds.stopSound(sid);
        }
    }
}
