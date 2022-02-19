package com.yunus1903.chatembeds.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yunus1903.chatembeds.client.gui.chat.AnimatedImageEmbed;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.ChatScreen;

public class AnimatedImageEmbedScreen extends AbstractImageEmbedScreen<AnimatedImageEmbed> {
	private int currentFrame;

	private int lastFrameIndex;

	private int time;

	public AnimatedImageEmbedScreen(final ChatScreen parent, final int chatScrollbarPos,
			final AnimatedImageEmbed embed) {
		super(parent, chatScrollbarPos, embed, embed.getFrames().get(0).getImage());
	}

	@Override
	public void render(final PoseStack poseStack, final int i, final int j, final float f) {
		if (this.minecraft == null) {
			return;
		}

		super.render(poseStack, i, j, f);

		final int frameIndex = this.minecraft.frameTimer.getLogEnd();

		if (this.lastFrameIndex != frameIndex) {
			this.lastFrameIndex = frameIndex;
			this.time++;
		}

		if (this.time >= this.embed.getFrames().get(this.currentFrame).getDelay() / 2) {
			this.time = 0;

			if (this.currentFrame + 1 >= this.embed.getFrames().size()) {
				this.currentFrame = 0;
			} else {
				this.currentFrame++;
			}
		}

		this.minecraft.getTextureManager()
				.getTexture(this.embed.getFrames().get(this.currentFrame).getResourceLocation());
		GuiComponent.blit(poseStack, (this.width - this.scaledImageWidth) / 2,
				(this.height - this.scaledImageHeight) / 2, 0, 0, this.scaledImageWidth, this.scaledImageHeight,
				this.scaledImageWidth, this.scaledImageHeight);
	}
}