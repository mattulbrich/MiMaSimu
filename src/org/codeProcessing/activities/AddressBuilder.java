package org.codeProcessing.activities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codeProcessing.ProcessingActivity;
import org.codeProcessing.ProcessingDataCollection;

public class AddressBuilder implements ProcessingActivity {

    // Regex for finding addresses
    private static String addressRegex = "^\\s*\\*=\\s*0x([0-9a-fA-F]+)\\s*;?.*";
    private static Pattern addressPattern = Pattern.compile(addressRegex);

    @Override
    public ProcessingDataCollection processDataObject(final ProcessingDataCollection inputParam) {
        final Map<Integer, Boolean> spaceUsed = new HashMap<Integer, Boolean>();
        final ProcessingDataCollection input = inputParam;
        final List<String> file = input.getFile();
        final List<String> newFile = new LinkedList<String>();
        final Iterator<String> fileIterator = file.iterator();
        int curAddress = 0;
        int lineNum = 0;
        while (fileIterator.hasNext()) {
            lineNum++;
            final String line = fileIterator.next();
            final Matcher matcher = addressPattern.matcher(line);
            if (matcher.matches()) {
                try {
                    curAddress = Integer.parseInt(matcher.group(1), 16);
                } catch (final NumberFormatException err) {
                    System.out.println("Invalid address '" + matcher.group(1) + "' at line " + lineNum);
                    System.exit(-1);
                }
                newFile.add("");
            } else {
                if (!spaceUsed.containsKey(curAddress)) {
                    newFile.add("0x" + Integer.toHexString(curAddress) + " " + line);
                    spaceUsed.put(curAddress, true);
                    curAddress++;
                } else {
                    System.out.println("Address 0x" + Integer.toHexString(curAddress)
                            + " is being used multiple times. In line " + lineNum);
                    System.exit(-1);
                }
            }
        }
        input.setFile(newFile);
        return input;
    }

    @Override
    public List<ProcessingActivity> getPrerequisits() {
        return null;
    }

    public int hashCode() {
        return "AddressBuilder".hashCode();
    }

    public boolean equals(final Object obj) {
        return obj.getClass().getCanonicalName().equals(this.getClass().getCanonicalName());
    }
}
