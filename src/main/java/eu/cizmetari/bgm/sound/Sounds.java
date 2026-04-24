package eu.cizmetari.bgm.sound;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.cizmetari.bgm.BackgroundMusicMod;
import net.minecraft.network.chat.Component;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.stb.STBVorbis;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.nio.*;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Sounds {
    public static int playSound(Path filePath, boolean loop) throws CommandSyntaxException {
        int sSource = AL10.alGenSources();
        int sBuffer = AL10.alGenBuffers();

        AL10.alSourcei(sSource, AL10.AL_LOOPING, loop ? 1 : 0);

        // For laziness reasons, the input audio files have to be signed 16 bit LE. I DO make the rules, and they are these. Too bad if you can't convert it!

        // for ogg
        IntBuffer channels;
        IntBuffer sampleRate;

        // for wav
        AudioFormat wavFormat;
        ByteBuffer wavAudioData;

        // I trust you all with matching filenames and the codecs! It's your fault if you screw around!
        if (filePath.getFileName().toString().endsWith(".ogg")) {
            channels = BufferUtils.createIntBuffer(1);
            sampleRate = BufferUtils.createIntBuffer(1);
            AL10.alBufferData(sBuffer, channels.get(0) == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16, Objects.requireNonNull(STBVorbis.stb_vorbis_decode_filename(filePath.toString(), channels, sampleRate)), sampleRate.get(0));
        } else if (filePath.getFileName().toString().endsWith(".wav")) {
            try (AudioInputStream wavStream = AudioSystem.getAudioInputStream(filePath.toFile())) {
                wavFormat = wavStream.getFormat();
                // ByteBuffer.wrap(wavStream.readAllBytes()).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
                byte[] wavStreamBytes = wavStream.readAllBytes();
                wavAudioData = BufferUtils.createByteBuffer(wavStreamBytes.length);
                wavAudioData.put(wavStreamBytes).flip();
                AL10.alBufferData(sBuffer, wavFormat.getChannels() == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16, wavAudioData, (int) wavFormat.getSampleRate());
            } catch (UnsupportedAudioFileException e) {
                throw new RuntimeException("Unsupported audio format: " + filePath, e);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read audio file: " + filePath, e);
            }
        } else {
            throw new UnsupportedOperationException("This mod only supports OGG Vorbis files or WAV files!");
        }

        AL10.alSourcei(sSource, AL10.AL_BUFFER, sBuffer);

        AL10.alSourcePlay(sSource);
        BackgroundMusicMod.PLAYING_SOUNDS.put(sSource, new SoundEntry(filePath.toAbsolutePath().normalize(), loop));

        if (!loop) {
            CompletableFuture<Void> autodelete = CompletableFuture.runAsync(() -> {
                while (AL10.alGetSourcei(sSource, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ignored) {}
                }
                stopSound(sSource);
            });
        }

        return sSource;
    }

    public static void stopSound(int id) {
        AL10.alSourceStop(id);
        BackgroundMusicMod.PLAYING_SOUNDS.remove(id);
        int bufferID = AL10.alGetSourcei(id, AL10.AL_BUFFER);
        AL10.alSourcei(id, AL10.AL_BUFFER, 0);
        AL10.alDeleteBuffers(bufferID);
        AL10.alDeleteSources(id);
    }
}
