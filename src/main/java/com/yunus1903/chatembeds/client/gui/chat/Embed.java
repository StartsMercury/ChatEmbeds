package com.yunus1903.chatembeds.client.gui.chat;

import static com.yunus1903.chatembeds.ChatEmbeds.CONFIG;
import static com.yunus1903.chatembeds.ChatEmbeds.LOGGER;
import static com.yunus1903.chatembeds.client.gui.chat.ImageExtractor.extractImageURL;
import static it.unimi.dsi.fastutil.objects.ObjectLists.unmodifiable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.yunus1903.chatembeds.ChatEmbeds;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.GuiMessage;
import net.minecraft.util.FormattedCharSequence;

public abstract class Embed {
	@Deprecated
	public static class Builder {
		private final int guiMessageId;

		private final int addedTime;

		private final String url;

		public Builder(final int addedTime, final String url, final int guiMessageId) {
			this.url = url;
			this.addedTime = addedTime;
			this.guiMessageId = guiMessageId;
		}

		public Embed build() {
			URL parsedURL = parseURL(this.url);

			if (parsedURL == null) {
				return null;
			}

			String extension = parsedURL.toString().substring(parsedURL.toString().lastIndexOf(".") + 1);

			if (extension.contains("?")) {
				extension = extension.substring(0, extension.indexOf("?"));
			}

			try {
				final String imageURL = extractImageURL(parsedURL);

				if (imageURL != null) {
					parsedURL = new URL(imageURL);

					if ("gif".equals(extension) || "gifv".equals(extension)) {
						if (CONFIG.isImageEmbedsAnimated()) {
							return new AnimatedImageEmbed(this.addedTime, parsedURL, this.guiMessageId);
						}
					} else if (CONFIG.isImageEmbedsEnabled()) {
						return new ImageEmbed(this.addedTime, parsedURL, this.guiMessageId);
					}
				}
			} catch (final MalformedURLException murle) {
				ChatEmbeds.LOGGER.debug("Failed to recreate URL", murle);
			} catch (final IOException ignored) {
			}

			if (CONFIG.isTextEmbedsEnabled()) {
				return new TextEmbed(this.addedTime, parsedURL, this.guiMessageId);
			}

			return null;
		}

		private static URL parseURL(String url) {
			URL parsedURL = null;

			try {
				if (new URI(url).getScheme() == null) {
					url = "http://" + url;
				}

				parsedURL = new URL(url);
			} catch (MalformedURLException | URISyntaxException e) {
				ChatEmbeds.LOGGER.error("Failed to parse URL", e);
			}

			return parsedURL;
		}
	}

	public static Embed of(final int addedTime, final String url, final int guiMessageId) {
		return new Builder(addedTime, url, guiMessageId).build();
	}

	private static final String USER_AGENT_VALUE = "Mozilla/4.0";

	private static final String USER_AGENT_KEY = "User-Agent";

	private final int addedTime;

	private final HttpURLConnection connection;

	private final int guiMessageId;

	private final ObjectList<? extends GuiMessage<FormattedCharSequence>> guiMessages;

	private final URL url;

	protected Embed(final int addedTime, final URL url, final int guiMessageId) {
		this.addedTime = addedTime;
		this.url = url;
		this.guiMessageId = guiMessageId;

		this.connection = openConnection();
		this.guiMessages = createGuiMessages();
	}

	private final ObjectList<? extends GuiMessage<FormattedCharSequence>> createGuiMessages() {
		final ObjectArrayList<GuiMessage<FormattedCharSequence>> messages = createMessages();

		this.connection.disconnect();

		if (messages == null) {
			throw new InternalError("createGuiMessages() must not return null.",
					new NullPointerException(getClass().getName() + ".createGuiMessages() returned null."));
		}

		messages.trim();

		return unmodifiable(messages);
	}

	protected abstract ObjectArrayList<GuiMessage<FormattedCharSequence>> createMessages();

	public int getAddedTime() {
		return this.addedTime;
	}

	protected HttpURLConnection getConnection() {
		return this.connection;
	}

	public int getGuiMessageId() {
		return this.guiMessageId;
	}

	public ObjectList<? extends GuiMessage<FormattedCharSequence>> getGuiMessages() {
		return this.guiMessages;
	}

	public URL getUrl() {
		return this.url;
	}

	private final HttpURLConnection openConnection() {
		try {
			final HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();

			connection.addRequestProperty(USER_AGENT_KEY, USER_AGENT_VALUE);

			return connection;
		} catch (final IOException ioe) {
			LOGGER.error("Failed to open connection", ioe);

			return null;
		}
	}
}
