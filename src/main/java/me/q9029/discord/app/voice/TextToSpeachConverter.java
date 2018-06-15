package me.q9029.discord.app.voice;

import javax.sound.sampled.AudioInputStream;

public interface TextToSpeachConverter {

	AudioInputStream convertToMp3(String text);
}
