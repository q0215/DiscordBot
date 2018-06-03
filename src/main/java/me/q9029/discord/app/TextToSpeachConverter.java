package me.q9029.discord.app;

import javax.sound.sampled.AudioInputStream;

public interface TextToSpeachConverter {

	AudioInputStream convertToMp3(String text);

	// AudioInputStream convertToWav(String text);
}
