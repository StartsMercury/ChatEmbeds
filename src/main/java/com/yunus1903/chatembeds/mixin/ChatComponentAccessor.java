package com.yunus1903.chatembeds.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.components.ChatComponent;

@Mixin(ChatComponent.class)
public interface ChatComponentAccessor {
	@Accessor("chatScrollbarPos")
	int getChatScrollbarPos();

	@Accessor("chatScrollbarPos")
	void setChatScrollbarPos(int chatScrollbarPos);
}
