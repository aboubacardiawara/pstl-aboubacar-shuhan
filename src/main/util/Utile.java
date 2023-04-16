package main.util;

import main.adaptation.interfaces.IRUAST;

public class Utile {
	public static void assertionCheck(boolean b, String msg) {
        if (!b)
            throw new AssertionError(msg);
    }

	public static boolean similarite(IRUAST a1, IRUAST a2) {
		return a1.getRoot().getName().equals(a2.getRoot().getName());
	}
}