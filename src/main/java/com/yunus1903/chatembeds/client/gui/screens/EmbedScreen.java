package com.yunus1903.chatembeds.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yunus1903.chatembeds.client.gui.chat.Embed;
import com.yunus1903.chatembeds.mixin.ChatComponentAccessor;

import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;

public class EmbedScreen<T extends Embed> extends Screen {
	private final int chatScrollbarPos;

	protected final T embed;

	private final ChatScreen lastScreen;

	/**
	 * Constructor
	 *
	 * @param parent           Parent {@link ChatScreen} (gets returned to when this
	 *                         screen is closed)
	 * @param chatScrollbarPos Last chat scroll position
	 * @param embed            The {@link Embed embed} which this screen belongs to
	 */
	public EmbedScreen(final ChatScreen parent, final int chatScrollbarPos, final T embed) {
		super(new TextComponent("Embed image"));
		this.lastScreen = parent;
		this.chatScrollbarPos = chatScrollbarPos;
		this.embed = embed;
	}

	@Override
	public void onClose() {
		if (this.minecraft == null) {
			return;
		}

		this.minecraft.setScreen(this.lastScreen);

		((ChatComponentAccessor) this.minecraft.gui.getChat()).setChatScrollbarPos(this.chatScrollbarPos);
	}

	@Override
	public void render(final PoseStack poseStack, final int i, final int j, final float f) {
		renderBackground(poseStack);
	}
}