package com.yunus1903.chatembeds.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public final class Jsons {
	public static boolean getAsBooleanOrDefault(final JsonElement json, final boolean defaultValue) {
		if (json instanceof final JsonPrimitive jsonPrimitive) {
			return unsafelyGetAsBooleanOrDefault(jsonPrimitive, defaultValue);
		} else {
			return defaultValue;
		}
	}

	public static boolean getAsBooleanOrDefault(final JsonPrimitive jsonPrimitive, final boolean defaultValue) {
		if (jsonPrimitive != null) {
			return unsafelyGetAsBooleanOrDefault(jsonPrimitive, defaultValue);
		} else {
			return defaultValue;
		}
	}

	public static int getAsIntOrDefault(final JsonElement json, final int defaultValue) {
		if (json instanceof final JsonPrimitive jsonPrimitive) {
			return unsafelyGetAsIntOrDefault(jsonPrimitive, defaultValue);
		} else {
			return defaultValue;
		}
	}

	public static int getAsIntOrDefault(final JsonPrimitive jsonPrimitive, final int defaultValue) {
		if (jsonPrimitive != null) {
			return unsafelyGetAsIntOrDefault(jsonPrimitive, defaultValue);
		} else {
			return defaultValue;
		}
	}

	public static boolean getBooleanOrDefault(final JsonElement json, final String memberName,
			final boolean defaultValue) {
		if (json instanceof final JsonObject jsonObject) {
			return unsafelyGetBooleanOrDefault(jsonObject, memberName, defaultValue);
		} else {
			return defaultValue;
		}
	}

	public static boolean getBooleanOrDefault(final JsonObject jsonObject, final String memberName,
			final boolean defaultValue) {
		return jsonObject != null ? unsafelyGetBooleanOrDefault(jsonObject, memberName, defaultValue) : defaultValue;
	}

	public static int getIntOrDefault(final JsonElement json, final String memberName, final int defaultValue) {
		if (json instanceof final JsonObject jsonObject) {
			return unsafelyGetIntOrDefault(jsonObject, memberName, defaultValue);
		} else {
			return defaultValue;
		}
	}

	public static int getIntOrDefault(final JsonObject jsonObject, final String memberName, final int defaultValue) {
		return jsonObject != null ? unsafelyGetIntOrDefault(jsonObject, memberName, defaultValue) : defaultValue;
	}

	@Deprecated
	public static boolean unsafelyGetAsBooleanOrDefault(final JsonPrimitive jsonPrimitive, final boolean defaultValue) {
		if (jsonPrimitive.isBoolean()) {
			return jsonPrimitive.getAsBoolean();
		} else {
			return defaultValue;
		}
	}

	@Deprecated
	public static int unsafelyGetAsIntOrDefault(final JsonPrimitive jsonPrimitive, final int defaultValue) {
		if (jsonPrimitive.isNumber()) {
			return jsonPrimitive.getAsInt();
		} else {
			return defaultValue;
		}
	}

	@Deprecated
	public static boolean unsafelyGetBooleanOrDefault(final JsonObject jsonObject, final String memberName,
			final boolean defaultValue) {
		return getAsBooleanOrDefault(jsonObject.get(memberName), defaultValue);
	}

	@Deprecated
	public static int unsafelyGetIntOrDefault(final JsonObject jsonObject, final String memberName,
			final int defaultValue) {
		return getAsIntOrDefault(jsonObject.get(memberName), defaultValue);
	}
}
