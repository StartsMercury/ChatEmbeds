package com.yunus1903.chatembeds.client.gui.chat;

import static com.yunus1903.chatembeds.ChatEmbeds.CONFIG;
import static com.yunus1903.chatembeds.ChatEmbeds.LOGGER;
import static java.lang.Math.min;
import static net.minecraft.util.FormattedCharSequence.EMPTY;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yunus1903.chatembeds.client.GuiEmbed;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class ImageEmbed extends Embed {
	protected static NativeImage scale(final NativeImage image, final int maxWidth, final int maxHeight) {
		int width = image.getWidth();
		int height = image.getHeight();

		if (width > maxWidth) {
			width = maxWidth;
			height = (int) ((float) image.getHeight() / (float) image.getWidth() * width);
		}

		if (height > maxHeight) {
			height = maxHeight;
			width = (int) ((float) image.getWidth() / (float) image.getHeight() * height);
		}

		final NativeImage scaledImage = new NativeImage(width, height, false);

		image.resizeSubRectTo(0, 0, image.getWidth(), image.getHeight(), scaledImage);

		return scaledImage;
	}

	private NativeImage image;

	private ResourceLocation imageResourceLocation;

	private NativeImage scaledImage;

	public ImageEmbed(final int addedTime, final URL url, final int guiMessageId) {
		super(addedTime, url, guiMessageId);
	}

	@Override
	protected ObjectArrayList<GuiMessage<FormattedCharSequence>> createMessages() {
		final int addedTime = getAddedTime();
		final int guiMessageId = getGuiMessageId();
		final ObjectArrayList<GuiMessage<FormattedCharSequence>> messages = new ObjectArrayList<>();

		if (!loadImage()) {
			return messages;
		}

		if (!CONFIG.isUrlMessagesRemoved()) {
			messages.add(new GuiMessage<>(addedTime, EMPTY, guiMessageId));
		}

		final double imageHeight = this.scaledImage.getHeight();
		final double messageHeight = 9.0D;
		final double totalMessages = imageHeight / messageHeight;

		for (int i = 0; i < Math.ceil(totalMessages); i++) {
			final double heightScale = i == (int) totalMessages ? totalMessages - i : 1.0D;
			final int destHeight = (int) (messageHeight * heightScale);
			final int destWidth = this.scaledImage.getWidth();
			final int textureHeight = this.scaledImage.getHeight();
			final int textureWidth = this.scaledImage.getWidth();
			final float u0 = 0;
			final float v0 = (float) (i * messageHeight);

			messages.add(new GuiEmbed<>(addedTime, this, guiMessageId) {
				@Override
				public int getWidth() {
					return textureWidth;
				}

				@Override
				public void render(final Minecraft mc, final PoseStack matrixStack, final int x, final int y) {
					mc.getTextureManager().bindForSetup(ImageEmbed.this.imageResourceLocation);

					GuiComponent.blit(matrixStack, x, y, u0, v0, destWidth, destHeight, textureWidth, textureHeight);
				}
			});
		}

		return messages;
	}

	public NativeImage getImage() {
		return this.image;
	}

	public ResourceLocation getImageResourceLocation() {
		return this.imageResourceLocation;
	}

	public NativeImage getScaledImage() {
		return this.scaledImage;
	}

	private final boolean loadImage() {
		final HttpURLConnection connection = getConnection();

		if (connection == null) {
			return false;
		}

		try {
			this.image = NativeImage.read(connection.getInputStream());
		} catch (final IOException ioe) {
			LOGGER.error("Exception reading image", ioe);
		}

		if (this.image == null) {
			return false;
		}

		final Minecraft minecraft = Minecraft.getInstance();

		this.imageResourceLocation = minecraft.getTextureManager().register("chat_embed_image",
				new DynamicTexture(this.image));
		this.scaledImage = scale(this.image, min(CONFIG.getMaxImageEmbedWidth(), minecraft.gui.getChat().getWidth()),
				CONFIG.getMaxImageEmbedHeight());

		return true;
	}
}
