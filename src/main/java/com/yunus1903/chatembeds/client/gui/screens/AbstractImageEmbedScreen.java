package com.yunus1903.chatembeds.client.gui.screens;

import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.ChatFormatting.UNDERLINE;
import static net.minecraft.network.chat.ClickEvent.Action.OPEN_URL;
import static net.minecraft.network.chat.Style.EMPTY;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yunus1903.chatembeds.client.gui.chat.Embed;

import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;

public abstract class AbstractImageEmbedScreen<T extends Embed> extends EmbedScreen<T> {
	private final int imageHeight;

	private final int imageWidth;

	private FormattedText openImage;

	protected int scaledImageHeight;

	protected int scaledImageWidth;

	public AbstractImageEmbedScreen(final ChatScreen parent, final int chatScrollbarPos, final T embed,
			final NativeImage image) {
		super(parent, chatScrollbarPos, embed);

		this.imageHeight = this.scaledImageHeight = image.getHeight();
		this.imageWidth = this.scaledImageWidth = image.getWidth();
	}

	@Override
	protected void init() {
		if (this.scaledImageWidth > this.width / 2 || this.scaledImageHeight > this.height / 2) {
			if (this.scaledImageWidth > this.width / 2) { // Max width
				this.scaledImageWidth = this.width / 2;
				this.scaledImageHeight = (int) ((float) this.imageHeight / (float) this.imageWidth *
						this.scaledImageWidth);
			}

			if (this.scaledImageHeight > this.height / 2) { // Max height
				this.scaledImageHeight = this.height / 2;
				this.scaledImageWidth = (int) ((float) this.imageWidth / (float) this.imageHeight *
						this.scaledImageHeight);
			}
		}

		this.openImage = new TextComponent("Open image");
	}

	@Override
	public boolean mouseClicked(final double d, final double e, final int i) {
		if (d < this.width - this.scaledImageWidth >> 1 ||
				d > (this.width - this.scaledImageWidth >> 1) + this.scaledImageWidth ||
				e < this.height - this.scaledImageHeight >> 1 ||
				e > (this.height - this.scaledImageHeight >> 1) + this.scaledImageHeight) {
			if (mouseOverImage(d, e)) {
				handleComponentClicked(((TextComponent) this.openImage).getStyle());
			} else {
				onClose();
			}

			return true;
		}

		return super.mouseClicked(d, e, i);
	}

	private boolean mouseOverImage(final double d, final double e) {
		return this.minecraft != null && d >= this.width - this.scaledImageWidth >> 1 &&
				d <= (this.width - this.scaledImageWidth >> 1) +
						this.minecraft.font.width(this.openImage.getString()) &&
				e >= (this.height - this.scaledImageHeight >> 1) + this.scaledImageHeight + 5 &&
				e <= (this.height - this.scaledImageHeight >> 1) + this.scaledImageHeight + 5 +
						this.minecraft.font.lineHeight;
	}

	@Override
	public void render(final PoseStack poseStack, final int i, final int j, final float f) {
		if (this.minecraft == null) {
			return;
		}

		super.render(poseStack, i, j, f);

		if (mouseOverImage(i, j)) {
			((TextComponent) this.openImage).withStyle(UNDERLINE);
		} else {
			((TextComponent) this.openImage)
					.setStyle(EMPTY.withClickEvent(new ClickEvent(OPEN_URL, this.embed.getUrl().toString())));
			((TextComponent) this.openImage).withStyle(DARK_GRAY);
		}

		this.minecraft.font.draw(poseStack, Language.getInstance().getVisualOrder(this.openImage),
				this.width - this.scaledImageWidth >> 1,
				(this.height - this.scaledImageHeight >> 1) + this.scaledImageHeight + 5, 0xFFFFFF);
	}
}