package com.yunus1903.chatembeds.client.gui.chat;

import static com.yunus1903.chatembeds.ChatEmbeds.CONFIG;
import static com.yunus1903.chatembeds.client.gui.chat.ImageEmbed.scale;
import static java.lang.Math.ceil;
import static java.lang.Math.min;
import static net.minecraft.util.FormattedCharSequence.EMPTY;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.google.common.io.ByteStreams;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yunus1903.chatembeds.ChatEmbeds;
import com.yunus1903.chatembeds.client.GuiEmbed;
import com.yunus1903.chatembeds.mixin.ChatComponentAccessor;

import at.dhyan.open_imaging.GifDecoder;
import at.dhyan.open_imaging.GifDecoder.GifImage;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceList;
import it.unimi.dsi.fastutil.objects.ReferenceLists;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class AnimatedImageEmbed extends Embed {
	public static class Frame {
		private final int delay;

		private final NativeImage image;

		private final ResourceLocation resourceLocation;

		private final NativeImage scaledImage;

		public Frame(final NativeImage image, final ResourceLocation resourceLocation, final int delay) {
			this.delay = delay;
			this.image = image;
			this.resourceLocation = resourceLocation;

			@SuppressWarnings("resource")
			final ChatComponent gui = Minecraft.getInstance().gui.getChat();

			this.scaledImage = scale(image, min(CONFIG.getMaxImageEmbedWidth(), gui.getWidth()),
					CONFIG.getMaxImageEmbedHeight());
		}

		public int getDelay() {
			return this.delay;
		}

		public NativeImage getImage() {
			return this.image;
		}

		public ResourceLocation getResourceLocation() {
			return this.resourceLocation;
		}

		public NativeImage getScaledImage() {
			return this.scaledImage;
		}
	}

	protected int currentFrame;

	protected GuiEmbed<AnimatedImageEmbed> currentRenderer;

	private ReferenceList<Frame> frames; // TODO: 22/01/2021 Fix final

	public AnimatedImageEmbed(final int addedTime, final URL url, final int guiMessageId) {
		super(addedTime, url, guiMessageId);
	}

	@Override
	protected ObjectArrayList<GuiMessage<FormattedCharSequence>> createMessages() {
		this.frames = new ReferenceArrayList<>();

		final ObjectArrayList<GuiMessage<FormattedCharSequence>> messages = new ObjectArrayList<>();

		if (!loadImage()) {
			return messages;
		}

		final int addedTime = getAddedTime();
		final int guiMessageId = getGuiMessageId();

		if (!CONFIG.isUrlMessagesRemoved()) {
			messages.add(new GuiMessage<>(addedTime, EMPTY, guiMessageId));
		}

		final NativeImage scaledImage = this.frames.get(0).getScaledImage();

		final double imageHeight = scaledImage.getHeight();
		final double messageHeight = 9.0D;
		final double totalMessages = imageHeight / messageHeight;

		for (int i = 0; i < ceil(totalMessages); i++) {
			final double heightScale = i == (int) totalMessages ? totalMessages - i : 1.0D;
			final int destWidth = scaledImage.getWidth();
			final int destHeight = (int) (messageHeight * heightScale);
			final int textureWidth = scaledImage.getWidth();
			final int textureHeight = scaledImage.getHeight();
			final float u0 = 0;
			final float v0 = (float) (i * messageHeight);

			messages.add(new GuiEmbed<>(addedTime, this, guiMessageId) {
				private int lastFrameIndex;

				private int lastScrollPos;

				private int time;

				@Override
				public int getWidth() {
					return scaledImage.getWidth();
				}

				@Override
				public void render(final Minecraft minecraft, final PoseStack poseStack, final int x, final int y) {
					final ChatComponentAccessor chatAccessor = (ChatComponentAccessor) minecraft.gui.getChat();

					if (this.lastScrollPos != chatAccessor.getChatScrollbarPos()) {
						AnimatedImageEmbed.this.currentRenderer = null;

						this.lastScrollPos = chatAccessor.getChatScrollbarPos();
					}

					if (AnimatedImageEmbed.this.currentRenderer == null) {
						AnimatedImageEmbed.this.currentRenderer = this;
					}

					if (AnimatedImageEmbed.this.currentRenderer == this) {
						final int frameIndex = minecraft.frameTimer.getLogEnd();

						if (this.lastFrameIndex != frameIndex) {
							this.lastFrameIndex = frameIndex;
							this.time++;
						}

						if (this.time >=
								AnimatedImageEmbed.this.frames.get(AnimatedImageEmbed.this.currentFrame).getDelay() /
										2) {
							this.time = 0;

							if (AnimatedImageEmbed.this.currentFrame + 1 >= AnimatedImageEmbed.this.frames.size()) {
								AnimatedImageEmbed.this.currentFrame = 0;
							} else {
								AnimatedImageEmbed.this.currentFrame++;
							}
						}
					}

					minecraft.getTextureManager().bindForSetup(AnimatedImageEmbed.this.frames
							.get(AnimatedImageEmbed.this.currentFrame).getResourceLocation());

					GuiComponent.blit(poseStack, x, y, u0, v0, destWidth, destHeight, textureWidth, textureHeight);
				}
			});
		}

		return messages;
	}

	public ReferenceList<Frame> getFrames() {
		return ReferenceLists.unmodifiable(this.frames);
	}

	private boolean loadImage() {
		GifImage image = null;

		try {
			if (getConnection() == null) {
				return false;
			}

			final ByteArrayOutputStream outputStream = new ByteArrayOutputStream() {
				@Override
				public synchronized byte[] toByteArray() {
					return this.buf;
				}
			};

			ByteStreams.copy(getConnection().getInputStream(), outputStream);

			image = GifDecoder.read(new ByteArrayInputStream(outputStream.toByteArray(), 0, outputStream.size()));
		} catch (final IOException e) {
			ChatEmbeds.LOGGER.error("Exception getting image from InputStream", e);
		}

		if (image == null) {
			return false;
		}

		try {
			final int numberOfFrames = image.getFrameCount();
			if (numberOfFrames == 0) {
				return false;
			}

			for (int i = 0; i < image.getFrameCount(); i++) {
				final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

				ImageIO.write(image.getFrame(i), "gif", outputStream);

				final NativeImage nativeImage = NativeImage.read(new ByteArrayInputStream(outputStream.toByteArray()));

				final ResourceLocation resourceLocation = Minecraft.getInstance().getTextureManager()
						.register("chat_embed_animated_image_frame_" + i, new DynamicTexture(nativeImage));

				this.frames.add(new Frame(nativeImage, resourceLocation, image.getDelay(i)));
			}
		} catch (final IOException e) {
			ChatEmbeds.LOGGER.error("Exception loading animated image", e);
		}

		return !this.frames.isEmpty();
	}
}