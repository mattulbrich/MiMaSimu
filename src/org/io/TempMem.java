package org.io;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class TempMem {

    private static final Map<Integer, String> namedAddresses = new HashMap<>();

    private static String text = "";

    public static LinkedList<String[]> getValueList() {
        final LinkedList<String[]> lines = new LinkedList<String[]>();
        final String[] splittedLines = text.split("\n");

        for (final String line : splittedLines) {
            final String[] elements = line.split("\\s+");
            lines.add(elements);
        }

        return lines;
    }

    public static String getText() {
        return text;
    }

    public static void setText(final String inputText) {
        text = inputText;
    }

    public static void setText(final LinkedList<String[]> lines) {
        text = "";
        for (final String[] line : lines) {
            for (final String element : line) {
                text += element + " ";
            }
            text += "\n";
        }
    }

    public static void addNamedAddress(int adr, String name) {
        namedAddresses.put(adr, name);
    }

    public static String getNamedAddress(int adr) {
        return namedAddresses.get(adr);
    }

    public static void clearNamdAddresses() {
        namedAddresses.clear();
    }
}
