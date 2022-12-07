package io.github.theprez.jfiletail;

public class Debug {

    private static final boolean s_isDebug = Boolean.getBoolean("jtailor.debug");

    public static void debug(final String _str) {
        if (!s_isDebug) {
            return;
        }
        debug("" + _str);
    }
}
