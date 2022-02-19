package com.yunus1903.chatembeds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ChatEmbeds {
	public static final String MOD_ID = "chatembeds";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static final ChatEmbedsConfig CONFIG = new ChatEmbedsConfig();
}
