package com.yunus1903.chatembeds.client;

import static net.minecraft.util.FormattedCharSequence.EMPTY;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yunus1903.chatembeds.client.gui.chat.Embed;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.util.FormattedCharSequence;

public abstract class GuiEmbed<T extends Embed> extends GuiMessage<FormattedCharSequence> {
	private final T embed;

	public GuiEmbed(final int addedTime, final T embed, final int id) {
		super(addedTime, EMPTY, id);

		this.embed = embed;
	}

	public final T getEmbed() {
		return this.embed;
	}

	public abstract int getWidth();

	public abstract void render(Minecraft minecraft, PoseStack poseStack, int x, int y);
}
