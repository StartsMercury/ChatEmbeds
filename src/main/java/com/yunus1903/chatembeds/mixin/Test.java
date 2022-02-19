package com.yunus1903.chatembeds.mixin;

public class Test {
	public static void main(final String... args) {
		int i = 0;

		if (i < 2) {
			System.out.println(i);
			i++;
		}

		System.out.println("B");

		if (i < 4) {
			System.out.println(i);
			i++;
		}
	}
}
