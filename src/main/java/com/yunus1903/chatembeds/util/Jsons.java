package com.yunus1903.chatembeds.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public final class Jsons {
	public static boolean getAsBooleanOrDefault(final JsonPrimitive jsonPrimitive, final boolean defaultValue) {
		return jsonPrimitive.isBoolean() ? jsonPrimitive.getAsBoolean() : defaultValue;
	}

	public static int getAsIntOrDefault(final JsonPrimitive jsonPrimitive, final int defaultValue) {
		return jsonPrimitive.isNumber() ? jsonPrimitive.getAsInt() : defaultValue;
	}

	public static boolean getBooleanOrDefault(final JsonElement json, final String memberName,
			final boolean defaultValue) {
		if (json instanceof final JsonObject jsonObject) {
			return getBooleanOrDefault(jsonObject, memberName, defaultValue);
		} else {
			return defaultValue;
		}
	}

	public static boolean getBooleanOrDefault(final JsonObject jsonObject, final String memberName,
			final boolean defaultValue) {
		if (jsonObject.get(memberName) instanceof final JsonPrimitive jsonPrimitive) {
			return getAsBooleanOrDefault(jsonPrimitive, defaultValue);
		} else {
			return defaultValue;
		}
	}

	public static int getIntOrDefault(final JsonElement json, final String memberName, final int defaultValue) {
		if (json instanceof final JsonObject jsonObject) {
			return getIntOrDefault(jsonObject, memberName, defaultValue);
		} else {
			return defaultValue;
		}
	}

	public static int getIntOrDefault(final JsonObject jsonObject, final String memberName, final int defaultValue) {
		if (jsonObject.get(memberName) instanceof final JsonPrimitive jsonPrimitive) {
			return getAsIntOrDefault(jsonPrimitive, defaultValue);
		} else {
			return defaultValue;
		}
	}
}
