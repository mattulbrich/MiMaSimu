package org.io;

import java.util.LinkedList;

public class TempMem {

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
}
