package com.yunus1903.chatembeds.entrypoint;

import static com.yunus1903.chatembeds.ChatEmbeds.CONFIG;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.fabricmc.loader.api.FabricLoader;

public class ChatEmbedsModMenu implements ModMenuApi {
	private final ConfigScreenFactory<?> modConfigScreenFactory;

	public ChatEmbedsModMenu() {
		if (!FabricLoader.getInstance().isModLoaded("cloth-config")) {
			this.modConfigScreenFactory = ModMenuApi.super.getModConfigScreenFactory();
		} else {
			this.modConfigScreenFactory = CONFIG::createScreen;
		}
	}

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return this.modConfigScreenFactory;
	}
}
