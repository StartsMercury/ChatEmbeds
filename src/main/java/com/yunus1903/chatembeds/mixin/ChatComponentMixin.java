package com.yunus1903.chatembeds.mixin;

import static com.google.common.collect.Lists.reverse;
import static com.yunus1903.chatembeds.ChatEmbeds.CONFIG;
import static com.yunus1903.chatembeds.ChatEmbeds.LOGGER;
import static java.lang.Math.min;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.stream.Collectors.toList;
import static net.minecraft.client.gui.components.ComponentRenderUtils.wrapComponents;
import static net.minecraft.util.Mth.floor;
import static org.objectweb.asm.Opcodes.IF_ICMPLE;
import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yunus1903.chatembeds.client.GuiEmbed;
import com.yunus1903.chatembeds.client.gui.chat.AnimatedImageEmbed;
import com.yunus1903.chatembeds.client.gui.chat.Embed;
import com.yunus1903.chatembeds.client.gui.chat.ImageEmbed;
import com.yunus1903.chatembeds.client.gui.screens.AnimatedImageEmbedScreen;
import com.yunus1903.chatembeds.client.gui.screens.ImageEmbedScreen;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;

// https://cdn-icons-png.flaticon.com/512/25/25231.png
@Mixin(ChatComponent.class)
public class ChatComponentMixin {
	private static final String URL_PATTERN = "((https?)://|(www)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?";

	private static final boolean displayImageEmbedScreen(final Minecraft minecraft, final int chatScrollbarPos,
			final Embed embed) {
		if (minecraft.screen instanceof ChatScreen) {
			if (embed instanceof AnimatedImageEmbed) {
				minecraft.setScreen(new AnimatedImageEmbedScreen((ChatScreen) minecraft.screen, chatScrollbarPos,
						(AnimatedImageEmbed) embed));

				return true;
			}

			if (embed instanceof ImageEmbed) {
				minecraft.setScreen(
						new ImageEmbedScreen((ChatScreen) minecraft.screen, chatScrollbarPos, (ImageEmbed) embed));

				return true;
			}
		}

		return false;
	}

	@Final
	@Shadow
	private List<GuiMessage<Component>> allMessages;

	private final LongSet embedsFound = new LongOpenHashSet();

	private int index;

	private boolean indexed;

	@Final
	@Shadow
	private Minecraft minecraft;

	@Shadow
	private boolean newMessageSinceScroll;

	@Final
	@Shadow
	private List<GuiMessage<FormattedCharSequence>> trimmedMessages;

	//	@Inject(method = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;IIZ)V",
	//			at = @At(value = "JUMP", opcode = <opcode>, ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
	//	private final <T> void detectEmbed(final Component component, final int id, final int addedTime, final boolean bl,
	//			final CallbackInfo callback, final int i, final List<T> list) {
	//		final Matcher matcher = Pattern.compile(URL_PATTERN, Pattern.CASE_INSENSITIVE).matcher(component.getString());
	//		final boolean embedFound = matcher.find();
	//
	//		if (!embedFound) {
	//			return;
	//		}
	//
	//		this.embedsFound.add((long) addedTime << 32L | id);
	//
	//		final Thread embedLoaderThread = new Thread(() -> {
	//			this.indexed = true;
	//			final Embed embed = Embed.of(addedTime, matcher.group(), id);
	//
	//			if (embed != null) {
	//				if (ChatEmbeds.CONFIG.isUrlMessagesRemoved()) {
	//					this.trimmedMessages.removeAll(this.trimmedMessages.stream()
	//							.filter(trimmedMessage -> list.contains(trimmedMessage.getMessage()))
	//							.collect(Collectors.toList()));
	//				}
	//
	//				this.trimmedMessages.add(this.index, new GuiMessage<>(addedTime, Language.getInstance()
	//						.getVisualOrder(new TextComponent(component.getString().split(" ", 2)[0])), id));
	//				this.trimmedMessages.addAll(this.index, Lists.reverse(embed.getGuiMessages()));
	//			}
	//
	//			this.index = 0;
	//			this.indexed = false;
	//		}, "Embed loader");
	//
	//		embedLoaderThread.setDaemon(true);
	//		embedLoaderThread.setUncaughtExceptionHandler((thread, throwable) -> {
	//			ChatEmbeds.LOGGER.error("Caught previously unhandled exception :", throwable);
	//
	//			this.index = 0;
	//			this.indexed = false;
	//		});
	//		embedLoaderThread.start();
	//	}

	@Inject(method = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;IIZ)V",
			slice = @Slice(to = @At(value = "JUMP", opcode = IF_ICMPLE, ordinal = 0)),
			at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", shift = AFTER))
	private final void doIndex(final CallbackInfo callback) {
		if (this.indexed) {
			this.index++;
		}
	}

	private Embed getEmbed(final double mouseX, final double mouseY) {
		if (isChatHidden() || this.minecraft.options.hideGui || isChatFocused()) {
			return null;
		}

		final ChatComponent chat = (ChatComponent) (Object) this;
		final ChatComponentAccessor chatAccessor = (ChatComponentAccessor) chat;
		double d0 = mouseX - 2.0D;
		double d1 = this.minecraft.getWindow().getGuiScaledHeight() - mouseY - 40.0D;

		d0 = floor(d0 / chat.getScale());
		d1 = floor(d1 / chat.getScale() * (this.minecraft.options.chatLineSpacing + 1.0D));

		if (d0 < 0.0D || d1 < 0.0D) {
			return null;
		}

		final int i = min(chat.getLinesPerPage(), this.trimmedMessages.size());

		if (d0 > floor(chat.getWidth() / chat.getScale()) || d1 >= 9 * i + i) {
			return null;
		}

		final int j = (int) (d1 / 9.0D + chatAccessor.getChatScrollbarPos());

		if (j < 0 || j >= this.trimmedMessages.size()) {
			return null;
		}

		final GuiMessage<FormattedCharSequence> chatLine = this.trimmedMessages.get(j);

		if (chatLine instanceof GuiEmbed && d0 - 3 <= ((GuiEmbed<?>) chatLine).getWidth()) {
			return ((GuiEmbed<?>) chatLine).getEmbed();
		}

		return null;
	}

	@Shadow
	private boolean isChatFocused() {
		throw new InternalError("Shadow method.");
	}

	@Shadow
	private boolean isChatHidden() {
		throw new InternalError("Shadow method.");
	}

	@Redirect(
			method = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;IIZ)V",
			at = @At(value = "INVOKE", target = "Ljava/util/List;add(ILjava/lang/Object;)V", ordinal = 1))
	private final <E> void onEmbedNotFound(final List<? super E> allMessages, final int zero, final E message,
			final Component component, final int id, final int addedTime) {
		if (!this.embedsFound.remove((long) addedTime << 32L | id)) {
			allMessages.add(message);
		}
	}

	@Inject(method = "Lnet/minecraft/client/gui/components/ChatComponent;handleChatQueueClicked(DD)Z", at = @At("HEAD"),
			cancellable = true)
	private final void onHandleChatQueueClicked(final double d, final double e,
			final CallbackInfoReturnable<Boolean> callback) {
		if (displayImageEmbedScreen(this.minecraft, this.index, getEmbed(d, e))) {
			callback.setReturnValue(true);
		}
	}

	@Inject(method = "Lnet/minecraft/client/gui/components/ChatComponent;processPendingMessages()V", at = @At("HEAD"))
	private final void onProcessPendingMessages(final CallbackInfo callback) {
		this.index = 0;
	}

	@Inject(method = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;IIZ)V",
			at = @At("HEAD"), cancellable = true)
	private final void redirectAddMessage(final Component component, final int i, final int j, final boolean bl,
			final CallbackInfo callback) {
		if (i != 0) {
			this.removeById(i);
		}

		final ChatComponent chat = (ChatComponent) (Object) this;
		final ChatComponentAccessor chatAccessor = (ChatComponentAccessor) chat;
		final int k = floor(chat.getWidth() / chat.getScale());
		final List<FormattedCharSequence> list = wrapComponents(component, k, this.minecraft.font);
		final boolean bl2 = isChatFocused();

		for (final FormattedCharSequence formattedCharSequence : list) {
			if (bl2 && chatAccessor.getChatScrollbarPos() > 0) {
				this.newMessageSinceScroll = true;

				chat.scrollChat(1.0);
			}

			this.trimmedMessages.add(0, new GuiMessage<>(j, formattedCharSequence, i));

			if (this.indexed) {
				this.index++;
			}
		}

		final Matcher matcher = Pattern.compile(URL_PATTERN, CASE_INSENSITIVE).matcher(component.getString());
		final boolean embedFound = matcher.find();

		if (!embedFound) {
			return;
		}

		final Thread embedLoaderThread = new Thread(() -> {
			this.indexed = true;
			final Embed embed = Embed.of(j, matcher.group(), i);

			if (embed != null) {
				if (CONFIG.isUrlMessagesRemoved()) {
					this.trimmedMessages.removeAll(this.trimmedMessages.stream()
							.filter(trimmedMessage -> list.contains(trimmedMessage.getMessage())).collect(toList()));
				}

				this.trimmedMessages.add(this.index, new GuiMessage<>(j, Language.getInstance()
						.getVisualOrder(new TextComponent(component.getString().split(" ", 2)[0])), i));
				this.trimmedMessages.addAll(this.index, reverse(embed.getGuiMessages()));
			}

			this.index = 0;
			this.indexed = false;
		}, "Embed loader");

		embedLoaderThread.setDaemon(true);
		embedLoaderThread.setUncaughtExceptionHandler((thread, throwable) -> {
			LOGGER.error("Caught previously unhandled exception :", throwable);

			this.index = 0;
			this.indexed = false;
		});
		embedLoaderThread.start();

		while (this.trimmedMessages.size() > 100) {
			this.trimmedMessages.remove(this.trimmedMessages.size() - 1);
		}

		if (!bl) {
			if (!embedFound) {
				this.allMessages.add(0, new GuiMessage<>(j, component, i));
			}

			while (this.allMessages.size() > 100) {
				this.allMessages.remove(this.allMessages.size() - 1);
			}
		}

		callback.cancel();
	}

	@Shadow
	private void removeById(final int i) {
		throw new InternalError("Shadow method.");
	}

	@Redirect(
			method = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lcom/mojang/blaze3d/vertex/PoseStack;I)V",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/gui/Font;drawShadow(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/util/FormattedCharSequence;FFI)I",
					ordinal = 0))
	private final int renderEmbed(final Font font, final PoseStack poseStack, final FormattedCharSequence guiMessage,
			final float f, final float g, final int i) {
		if (guiMessage instanceof final GuiEmbed<?> guiMessageEmbed) {
			guiMessageEmbed.render(this.minecraft, poseStack, 3, (int) g);

			return 0; // TODO confirm valid
		} else {
			return font.drawShadow(poseStack, guiMessage, f, g, i);
		}
	}
}
