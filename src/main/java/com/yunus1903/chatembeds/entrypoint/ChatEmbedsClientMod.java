package com.yunus1903.chatembeds.entrypoint;

import static com.yunus1903.chatembeds.ChatEmbeds.CONFIG;
import static com.yunus1903.chatembeds.ChatEmbeds.LOGGER;

import net.fabricmc.api.ClientModInitializer;

public class ChatEmbedsClientMod implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		LOGGER.info("ChatEmbeds is being initialized");

		if (!CONFIG.load()) {
			CONFIG.save();
		}
	}
}
