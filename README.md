Background Music Mod
======
Background Music Mod is a client mod that allows players to play audio files directly using chat commands and a file name instead of using Minecraft's (rather restrictive and difficult to use) Sound Events system.

<span style="color:red;font-size:1.25em">!</span> **Currently only supports 16-bit little endian WAV/OGG files.**

Usage
======

### To play an audio file:

Run `/bgm play <file_path>`. The chat will return a number that corresponds to the ID of the source. It can also be looped using `/bgm play <file_path> true`.

### To stop an audio file:

If you have a source ID, run `/bgm stop_id <id>`.

If you have a file path, run `/bgm stop_file <file_path>`. *Note: this will stop all playing instances of that file.*

To stop all currently playing sounds, run `/bgm stop_all`.

### To see currently playing audio files:

Run `/bgm list`.

### To stop all audio files:

Run `/bgm stop_all`.

### To see debug info:

For checking the current JVM user (e.g. for checking user permissions), run `/bgm debug whoami`. *(due to platform differences it is not possible to check actual permissions, rather the user has to check them themselves)*

For checking the current JVM path (e.g. for relative paths), run `/bgm debug pwd`.