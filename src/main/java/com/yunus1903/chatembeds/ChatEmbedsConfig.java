package com.yunus1903.chatembeds;

import static com.yunus1903.chatembeds.ChatEmbeds.CONFIG;
import static com.yunus1903.chatembeds.ChatEmbeds.GSON;
import static com.yunus1903.chatembeds.ChatEmbeds.LOGGER;
import static com.yunus1903.chatembeds.ChatEmbeds.MOD_ID;
import static com.yunus1903.chatembeds.util.Jsons.getBooleanOrDefault;
import static com.yunus1903.chatembeds.util.Jsons.getIntOrDefault;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;
import static net.minecraft.util.Mth.clamp;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;

import com.google.gson.JsonObject;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;
import me.shedaniel.clothconfig2.impl.builders.BooleanToggleBuilder;
import me.shedaniel.clothconfig2.impl.builders.IntSliderBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TranslatableComponent;

public class ChatEmbedsConfig implements Serializable {
	protected static class Category {
		protected static class Main {
			protected static final String URL_MESSAGES_REMOVED_KEY = "chatembeds.config.main." +
					ChatEmbedsConfig.URL_MESSAGES_REMOVED_KEY;

			protected static final TranslatableComponent URL_MESSAGES_REMOVED_COMPONENT = new TranslatableComponent(
					URL_MESSAGES_REMOVED_KEY);

			protected static final String TITLE_KEY = "chatembeds.config.main.title";

			protected static final TranslatableComponent TITLE_COMPONENT = new TranslatableComponent(TITLE_KEY);

			protected static final String TEXT_EMBEDS_ENABLED_KEY = "chatembeds.config.main." +
					ChatEmbedsConfig.TEXT_EMBEDS_ENABLED_KEY;

			protected static final TranslatableComponent TEXT_EMBEDS_ENABLED_COMPONENT = new TranslatableComponent(
					TEXT_EMBEDS_ENABLED_KEY);

			protected static final String MAX_IMAGE_EMBED_WIDTH_KEY = "chatembeds.config.main." +
					ChatEmbedsConfig.MAX_IMAGE_EMBED_WIDTH_KEY;

			protected static final TranslatableComponent MAX_IMAGE_EMBED_WIDTH_COMPONENT = new TranslatableComponent(
					MAX_IMAGE_EMBED_WIDTH_KEY);

			protected static final String MAX_IMAGE_EMBED_HEIGHT_KEY = "chatembeds.config.main." +
					ChatEmbedsConfig.MAX_IMAGE_EMBED_HEIGHT_KEY;

			protected static final TranslatableComponent MAX_IMAGE_EMBED_HEIGHT_COMPONENT = new TranslatableComponent(
					MAX_IMAGE_EMBED_HEIGHT_KEY);

			protected static final String IMAGE_EMBEDS_ENABLED_KEY = "chatembeds.config.main." +
					ChatEmbedsConfig.IMAGE_EMBEDS_ENABLED_KEY;

			protected static final TranslatableComponent IMAGE_EMBEDS_ENABLED_COMPONENT = new TranslatableComponent(
					IMAGE_EMBEDS_ENABLED_KEY);

			protected static final String IMAGE_EMBEDS_ANIMATED_KEY = "chatembeds.config.main." +
					ChatEmbedsConfig.IMAGE_EMBEDS_ANIMATED_KEY;

			protected static final TranslatableComponent IMAGE_EMBEDS_ANIMATED_COMPONENT = new TranslatableComponent(
					IMAGE_EMBEDS_ANIMATED_KEY);

			protected static ConfigCategory create(final ConfigBuilder builder) {
				final ConfigCategory it = builder.getOrCreateCategory(TITLE_COMPONENT);

				it.addEntry(createImageEmbedEnabledEntry(builder));
				it.addEntry(createImageEmbedAnimatedEntry(builder));
				it.addEntry(createTextEmbedEnabledEntry(builder));
				it.addEntry(createMaxImageEmbedWidthEntry(builder));
				it.addEntry(createMaxImageEmbedHeightEntry(builder));
				it.addEntry(createRemoveUrlMessageEntry(builder));

				return it;
			}

			protected static BooleanListEntry createImageEmbedAnimatedEntry(final ConfigBuilder builder) {
				final BooleanToggleBuilder imageEmbedAnimatedEntryBuilder = builder.entryBuilder()
						.startBooleanToggle(IMAGE_EMBEDS_ANIMATED_COMPONENT, CONFIG.isImageEmbedsAnimated());

				imageEmbedAnimatedEntryBuilder.setDefaultValue(IMAGE_EMBEDS_ANIMATED_DEFAULT_VALUE);
				imageEmbedAnimatedEntryBuilder.setSaveConsumer(CONFIG::setImageEmbedsAnimated);

				return imageEmbedAnimatedEntryBuilder.build();
			}

			protected static BooleanListEntry createImageEmbedEnabledEntry(final ConfigBuilder builder) {
				final BooleanToggleBuilder imageEmbedEnabledEntryBuilder = builder.entryBuilder()
						.startBooleanToggle(IMAGE_EMBEDS_ENABLED_COMPONENT, CONFIG.isImageEmbedsEnabled());

				imageEmbedEnabledEntryBuilder.setDefaultValue(IMAGE_EMBEDS_ENABLED_DEFAULT_VALUE);
				imageEmbedEnabledEntryBuilder.setSaveConsumer(CONFIG::setImageEmbedsEnabled);

				return imageEmbedEnabledEntryBuilder.build();
			}

			protected static IntegerSliderEntry createMaxImageEmbedHeightEntry(final ConfigBuilder builder) {
				final IntSliderBuilder maxImageEmbedHeightEntryBuilder = builder.entryBuilder()
						.startIntSlider(MAX_IMAGE_EMBED_HEIGHT_COMPONENT, CONFIG.getMaxImageEmbedHeight(), 0, 320);

				maxImageEmbedHeightEntryBuilder.setDefaultValue(MAX_IMAGE_EMBED_HEIGHT_DEFAULT_VALUE);
				maxImageEmbedHeightEntryBuilder.setSaveConsumer(CONFIG::setMaxImageEmbedHeight);

				return maxImageEmbedHeightEntryBuilder.build();
			}

			protected static IntegerSliderEntry createMaxImageEmbedWidthEntry(final ConfigBuilder builder) {
				final IntSliderBuilder maxImageEmbedWidthEntryBuilder = builder.entryBuilder()
						.startIntSlider(MAX_IMAGE_EMBED_WIDTH_COMPONENT, CONFIG.getMaxImageEmbedWidth(), 0, 320);

				maxImageEmbedWidthEntryBuilder.setDefaultValue(MAX_IMAGE_EMBED_WIDTH_DEFAULT_VALUE);
				maxImageEmbedWidthEntryBuilder.setSaveConsumer(CONFIG::setMaxImageEmbedWidth);

				return maxImageEmbedWidthEntryBuilder.build();
			}

			protected static BooleanListEntry createRemoveUrlMessageEntry(final ConfigBuilder builder) {
				final BooleanToggleBuilder removeUrlMessageEntryBuilder = builder.entryBuilder()
						.startBooleanToggle(URL_MESSAGES_REMOVED_COMPONENT, CONFIG.isUrlMessagesRemoved());

				removeUrlMessageEntryBuilder.setDefaultValue(URL_MESSAGES_REMOVED_DEFAULT_VALUE);
				removeUrlMessageEntryBuilder.setSaveConsumer(CONFIG::setUrlMessagesRemoved);

				return removeUrlMessageEntryBuilder.build();
			}

			protected static BooleanListEntry createTextEmbedEnabledEntry(final ConfigBuilder builder) {
				final BooleanToggleBuilder textEmbedEnabledEntryBuilder = builder.entryBuilder()
						.startBooleanToggle(TEXT_EMBEDS_ENABLED_COMPONENT, CONFIG.isTextEmbedsEnabled());

				textEmbedEnabledEntryBuilder.setDefaultValue(TEXT_EMBEDS_ENABLED_DEFAULT_VALUE);
				textEmbedEnabledEntryBuilder.setSaveConsumer(CONFIG::setTextEmbedsEnabled);

				return textEmbedEnabledEntryBuilder.build();
			}
		}
	}

	public static final String URL_MESSAGES_REMOVED_KEY = "urlMessagesRemoved";

	public static final boolean URL_MESSAGES_REMOVED_DEFAULT_VALUE = true;

	protected static final String TITLE_KEY = "chatembeds.config.title";

	protected static final TranslatableComponent TITLE_COMPONENT = new TranslatableComponent(TITLE_KEY);

	public static final String TEXT_EMBEDS_ENABLED_KEY = "textEmbedsEnabled";

	public static final boolean TEXT_EMBEDS_ENABLED_DEFAULT_VALUE = false;

	private static final long serialVersionUID = 5851042577604956980L;

	public static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".json");

	public static final String MAX_IMAGE_EMBED_WIDTH_KEY = "maxImageEmbedWidth";

	public static final int MAX_IMAGE_EMBED_WIDTH_DEFAULT_VALUE = 300;

	public static final String MAX_IMAGE_EMBED_HEIGHT_KEY = "maxImageEmbedHeight";

	public static final int MAX_IMAGE_EMBED_HEIGHT_DEFAULT_VALUE = 100;

	public static final String IMAGE_EMBEDS_ENABLED_KEY = "imageEmbedsEnabled";

	public static final boolean IMAGE_EMBEDS_ENABLED_DEFAULT_VALUE = true;

	public static final String IMAGE_EMBEDS_ANIMATED_KEY = "imageEmbedsAnimated";

	public static final boolean IMAGE_EMBEDS_ANIMATED_DEFAULT_VALUE = true;

	private boolean imageEmbedsAnimated;

	private boolean imageEmbedsEnabled;

	private int maxImageEmbedHeight;

	private int maxImageEmbedWidth;

	private boolean textEmbedsEnabled;

	private boolean urlMessagesRemoved;

	protected ChatEmbedsConfig() {
		this.imageEmbedsAnimated = true;
		this.imageEmbedsEnabled = IMAGE_EMBEDS_ENABLED_DEFAULT_VALUE;
		this.maxImageEmbedHeight = MAX_IMAGE_EMBED_HEIGHT_DEFAULT_VALUE;
		this.maxImageEmbedWidth = MAX_IMAGE_EMBED_WIDTH_DEFAULT_VALUE;
		this.textEmbedsEnabled = TEXT_EMBEDS_ENABLED_DEFAULT_VALUE;
		this.urlMessagesRemoved = true;
	}

	public Screen createScreen(final Screen parent) {
		final ConfigBuilder builder = ConfigBuilder.create();

		builder.setSavingRunnable(this::save);
		builder.setParentScreen(parent);
		builder.setTitle(TITLE_COMPONENT);
		builder.transparentBackground();

		Category.Main.create(builder);

		return builder.build();
	}

	public int getMaxImageEmbedHeight() {
		return this.maxImageEmbedHeight;
	}

	public int getMaxImageEmbedWidth() {
		return this.maxImageEmbedWidth;
	}

	public boolean isImageEmbedsAnimated() {
		return this.imageEmbedsAnimated;
	}

	public boolean isImageEmbedsEnabled() {
		return this.imageEmbedsEnabled;
	}

	public boolean isTextEmbedsEnabled() {
		return this.textEmbedsEnabled;
	}

	public boolean isUrlMessagesRemoved() {
		return this.urlMessagesRemoved;
	}

	public boolean load() {
		try {
			final JsonObject fromJson = GSON.fromJson(newBufferedReader(PATH), JsonObject.class);

			setImageEmbedsAnimated(
					getBooleanOrDefault(fromJson, IMAGE_EMBEDS_ANIMATED_KEY, IMAGE_EMBEDS_ANIMATED_DEFAULT_VALUE));
			setImageEmbedsEnabled(
					getBooleanOrDefault(fromJson, IMAGE_EMBEDS_ENABLED_KEY, IMAGE_EMBEDS_ENABLED_DEFAULT_VALUE));
			setMaxImageEmbedHeight(
					getIntOrDefault(fromJson, MAX_IMAGE_EMBED_HEIGHT_KEY, MAX_IMAGE_EMBED_HEIGHT_DEFAULT_VALUE));
			setMaxImageEmbedWidth(
					getIntOrDefault(fromJson, MAX_IMAGE_EMBED_WIDTH_KEY, MAX_IMAGE_EMBED_WIDTH_DEFAULT_VALUE));
			setUrlMessagesRemoved(
					getBooleanOrDefault(fromJson, URL_MESSAGES_REMOVED_KEY, URL_MESSAGES_REMOVED_DEFAULT_VALUE));
			setTextEmbedsEnabled(
					getBooleanOrDefault(fromJson, TEXT_EMBEDS_ENABLED_KEY, TEXT_EMBEDS_ENABLED_DEFAULT_VALUE));

			return true;
		} catch (final IOException ioe) {
			LOGGER.error("Unable to load config", ioe);

			return false;
		}
	}

	public boolean save() {
		try {
			GSON.toJson(this, newBufferedWriter(PATH));

			return true;
		} catch (final IOException ioe) {
			LOGGER.error("Unable to save config", ioe);

			return false;
		}
	}

	public ChatEmbedsConfig setImageEmbedsAnimated(final boolean imageEmbedsAnimated) {
		this.imageEmbedsAnimated = imageEmbedsAnimated;

		return this;
	}

	public ChatEmbedsConfig setImageEmbedsEnabled(final boolean imageEmbedsEnabled) {
		this.imageEmbedsEnabled = imageEmbedsEnabled;

		return this;
	}

	public ChatEmbedsConfig setMaxImageEmbedHeight(final int maxImageEmbedHeight) {
		this.maxImageEmbedHeight = clamp(0, maxImageEmbedHeight, 320);

		return this;
	}

	public ChatEmbedsConfig setMaxImageEmbedWidth(final int maxImageEmbedWidth) {
		this.maxImageEmbedWidth = clamp(0, maxImageEmbedWidth, 320);

		return this;
	}

	public ChatEmbedsConfig setTextEmbedsEnabled(final boolean textEmbedsEnabled) {
		this.textEmbedsEnabled = textEmbedsEnabled;

		return this;
	}

	public ChatEmbedsConfig setUrlMessagesRemoved(final boolean urlMessagesRemoved) {
		this.urlMessagesRemoved = urlMessagesRemoved;

		return this;
	}
}