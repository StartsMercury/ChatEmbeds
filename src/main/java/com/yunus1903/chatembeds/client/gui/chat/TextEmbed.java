package com.yunus1903.chatembeds.client.gui.chat;

import static com.yunus1903.chatembeds.ChatEmbeds.LOGGER;
import static javax.swing.text.html.HTML.Attribute.CONTENT;
import static javax.swing.text.html.HTML.Attribute.NAME;
import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.ChatFormatting.UNDERLINE;
import static net.minecraft.client.gui.components.ComponentRenderUtils.wrapComponents;
import static net.minecraft.util.FormattedCharSequence.EMPTY;
import static net.minecraft.util.Mth.floor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;

public class TextEmbed extends Embed {
	private FormattedText description;

	private FormattedText title;

	public TextEmbed(final int addedTime, final URL url, final int guiMessageId) {
		super(addedTime, url, guiMessageId);
	}

	@Override
	protected ObjectArrayList<GuiMessage<FormattedCharSequence>> createMessages() {
		final ObjectArrayList<GuiMessage<FormattedCharSequence>> messages = new ObjectArrayList<>();

		if (!loadText()) {
			return messages;
		}

		final int addedTime = getAddedTime();
		final int guiMessageId = getGuiMessageId();
		final Language language = Language.getInstance();

		messages.add(new GuiMessage<>(guiMessageId, EMPTY, addedTime));
		messages.add(new GuiMessage<>(guiMessageId, language.getVisualOrder(this.title), addedTime));
		messages.add(new GuiMessage<>(guiMessageId, EMPTY, addedTime));

		if (this.description == null) {
			return messages;
		}

		final Minecraft minecraft = Minecraft.getInstance();
		final int i = floor(ChatComponent.getWidth(minecraft.options.chatWidth) / minecraft.options.chatScale);
		final List<FormattedCharSequence> components = wrapComponents(this.description, i, minecraft.font);

		for (final FormattedCharSequence component : components) {
			messages.add(new GuiMessage<>(addedTime, component, guiMessageId));
		}

		messages.add(new GuiMessage<>(addedTime, EMPTY, guiMessageId));

		return messages;
	}

	public FormattedText getDescription() {
		return this.description;
	}

	public FormattedText getTitle() {
		return this.title;
	}

	private boolean loadText() {
		final HttpURLConnection connection = getConnection();
		final HTMLDocument doc = (HTMLDocument) new HTMLEditorKit().createDefaultDocument();
		final HTMLEditorKit.Parser parser = new ParserDelegator();

		if (connection == null) {
			return false;
		}

		try {
			parser.parse(new InputStreamReader(connection.getInputStream()), doc.getReader(0), true);
		} catch (final IOException ioe) {
			LOGGER.error("Exception reading HTML", ioe);
		}

		try {
			this.title = new TextComponent(doc.getProperty("title").toString()).withStyle(GRAY, UNDERLINE);
		} catch (final NullPointerException ignored) {
		}

		if (this.title == null) {
			try {
				this.title = new TextComponent(Objects.toString(doc
						.getElement(doc.getDefaultRootElement(), NAME, "title").getAttributes().getAttribute(CONTENT)))
								.withStyle(GRAY, UNDERLINE);
			} catch (final NullPointerException ignored) {
			}
		}

		try {
			final String desc = Objects.toString(doc.getElement(doc.getDefaultRootElement(), NAME, "description")
					.getAttributes().getAttribute(CONTENT));

			this.description = new TextComponent(desc.replace('\r', '\n')).withStyle(GRAY);
		} catch (final NullPointerException ignored) {
		}

		return true;
	}
}
