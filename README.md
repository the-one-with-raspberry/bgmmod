Background Music Mod
======
Background Music Mod is a client mod that allows players to play audio files directly using chat commands and a file name instead of using Minecraft's (rather restrictive and difficult to use) Sound Events system.

Usage
======
## To play an audio file:
Run `/bgm play <file_path>`. The chat will return a number that corresponds to the ID of the source. It can also be looped using `/bgm play <file_path> true`.
## To stop an audio file:
If you have a source ID, run `/bgm stop_id <id>`.
If you have a file path, run `/bgm stop_file <file_path>`. *Note: this will stop all playing instances of that file.*
## To see currently playing audio files:
Run `/bgm list`.
## To stop all audio files:
Run `/bgm stop_all`.