package com.yunus1903.chatembeds;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public final class MemberInfos {
	public static void main(final String... args) throws ReflectiveOperationException {
		System.out.println(ofMethod(List.class, "add", Object.class));
	}

	public static String of(final Class<?> clazz) {
		return clazz.descriptorString();
	}

	public static String of(final Constructor<?> constructor) {
		final StringBuilder builder;

		builder = new StringBuilder(of(constructor.getDeclaringClass()) + "<init>(");

		for (final Class<?> parameterType : constructor.getParameterTypes()) {
			builder.append(of(parameterType));
		}

		builder.append(")V");

		return builder.toString();
	}

	public static String of(final Field field) {
		return of(field.getDeclaringClass()) + field.getName() + ':' + of(field.getType());
	}

	public static String of(final Method method) {
		final StringBuilder builder;

		builder = new StringBuilder(of(method.getDeclaringClass()) + method.getName() + '(');

		for (final Class<?> parameterType : method.getParameterTypes()) {
			builder.append(of(parameterType));
		}

		builder.append(')' + of(method.getReturnType()));

		return builder.toString();
	}

	public static String ofClass(final String className) throws ClassNotFoundException {
		return of(Class.forName(className));
	}

	public static String ofConstructor(final Class<?> clazz) throws NoSuchMethodException {
		return of(clazz.getDeclaredConstructor());
	}

	public static final String ofConstructor(final Class<?> clazz, final Class<?>... parameterTypes)
			throws NoSuchMethodException {
		return of(clazz.getDeclaredConstructor(parameterTypes));
	}

	public static final String ofConstructor(final Class<?> clazz, final String... parameterTypeNames)
			throws ClassNotFoundException, NoSuchMethodException {
		final Class<?>[] parameterTypes = new Class[parameterTypeNames.length];

		for (int i = 0; i < parameterTypes.length; i++) {
			parameterTypes[i] = Class.forName(parameterTypeNames[i]);
		}

		return ofConstructor(clazz, parameterTypes);
	}

	public static String ofConstructor(final String className) throws ClassNotFoundException, NoSuchMethodException {
		return ofConstructor(Class.forName(className));
	}

	public static final String ofConstructor(final String className, final Class<?>... parameterTypes)
			throws ClassNotFoundException, NoSuchMethodException {
		return ofConstructor(Class.forName(className), parameterTypes);
	}

	public static final String ofConstructor(final String className, final String... parameterTypeNames)
			throws ClassNotFoundException, NoSuchMethodException {
		return ofConstructor(Class.forName(className), parameterTypeNames);
	}

	public static String ofField(final Class<?> clazz, final String fieldName) throws NoSuchFieldException {
		return of(clazz.getDeclaredField(fieldName));
	}

	public static String ofField(final String className, final String fieldName)
			throws ClassNotFoundException, NoSuchFieldException {
		return ofField(Class.forName(className), fieldName);
	}

	public static String ofMethod(final Class<?> clazz, final String methodName) throws NoSuchMethodException {
		return of(clazz.getDeclaredMethod(methodName));
	}

	public static final String ofMethod(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes)
			throws NoSuchMethodException {
		return of(clazz.getDeclaredMethod(methodName, parameterTypes));
	}

	public static final String ofMethod(final Class<?> clazz, final String methodName,
			final String... parameterTypeNames) throws ClassNotFoundException, NoSuchMethodException {
		final Class<?>[] parameterTypes = new Class[parameterTypeNames.length];

		for (int i = 0; i < parameterTypes.length; i++) {
			parameterTypes[i] = Class.forName(parameterTypeNames[i]);
		}

		return ofMethod(clazz, methodName, parameterTypes);
	}

	public static String ofMethod(final String className, final String methodName)
			throws ClassNotFoundException, NoSuchMethodException {
		return ofMethod(Class.forName(className), methodName);
	}

	public static final String ofMethod(final String className, final String methodName,
			final Class<?>... parameterTypes) throws ClassNotFoundException, NoSuchMethodException {
		return ofMethod(Class.forName(className), methodName, parameterTypes);
	}

	public static final String ofMethod(final String className, final String methodName,
			final String... parameterTypeNames) throws ClassNotFoundException, NoSuchMethodException {
		return ofMethod(Class.forName(className), methodName, parameterTypeNames);
	}
}
