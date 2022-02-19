package com.yunus1903.chatembeds.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yunus1903.chatembeds.client.gui.chat.ImageEmbed;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

public class ImageEmbedScreen extends AbstractImageEmbedScreen<ImageEmbed> {
	private ResourceLocation imageResourceLocation;

	public ImageEmbedScreen(final ChatScreen parent, final int chatScrollbarPos, final ImageEmbed embed) {
		super(parent, chatScrollbarPos, embed, embed.getImage());
	}

	@Override
	protected void init() {
		if (this.minecraft == null) {
			return;
		}

		super.init();
		super.tick();

		this.imageResourceLocation = this.minecraft.getTextureManager().register("embed_fullscreen_image",
				new DynamicTexture(this.embed.getImage()));
	}

	@Override
	public void render(final PoseStack poseStack, final int i, final int j, final float f) {
		if (this.minecraft == null) {
			return;
		}

		super.render(poseStack, i, j, f);

		this.minecraft.getTextureManager().bindForSetup(this.imageResourceLocation);

		GuiComponent.blit(poseStack, (this.width - this.scaledImageWidth) / 2,
				(this.height - this.scaledImageHeight) / 2, 0, 0, this.scaledImageWidth, this.scaledImageHeight,
				this.scaledImageWidth, this.scaledImageHeight);
	}
}