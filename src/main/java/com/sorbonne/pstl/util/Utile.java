package com.sorbonne.pstl.util;

import java.util.List;

import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import com.sorbonne.pstl.ruast.interfaces.IRUAST;


public class Utile {
	public static boolean DEBUG_ON = false;

	public static void assertionCheck(boolean b, String msg) {
		if (!b)
			throw new AssertionError(msg);
	}

	public static boolean similarite(IRUAST a1, IRUAST a2) {
		return a1.getRoot().getName().equals(a2.getRoot().getName());
	}

	public static void debug_print(Object msg) {
		if (DEBUG_ON) {
			System.out.println("[DEBUG] " + msg.toString());
		}
	}

	public static String buildClassName(IRUAST ruast) {
		// ruaste.getName(): className>1parentName>...>nparentName
		String name = ruast.getName().split(">")[0];
		return name;
	}

}