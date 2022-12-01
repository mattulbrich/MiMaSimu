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
import org.codeProcessing.exceptions.InvalidOpCodeException;

/**
 * Converts the assembly statements to Hexcodes
 * 
 * @author Samuel Teuber
 * @version 1.0
 */
public class CodewordConverter implements ProcessingActivity {

    // Regex for removing unnecessary lines
    private static String unnecessaryRegex = "^\\s*0x([0-9a-fA-F]{1,5})(\\s+)(DS)?(\\s*)$";
    private static Pattern unnecessaryPattern = Pattern.compile(unnecessaryRegex);

    private static String commandRegex = "^\\s*0x([0-9a-fA-F]{1,5})\\s+([A-Z]+)(\\s+(0x[0-9a-fA-F]{1,5}|[0-9]+))?\\s*$";
    private static Pattern commandPattern = Pattern.compile(commandRegex);

    @Override
    public ProcessingDataCollection processDataObject(final ProcessingDataCollection inputParam) {
        final ProcessingDataCollection input = inputParam;
        final Map<String, Integer> variables = new HashMap<String, Integer>();
        final List<String> file = input.getFile();
        final List<String> noUnnecessaryFile = new LinkedList<String>();
        Iterator<String> fileIterator = file.iterator();
        // Jump over start instruction
        noUnnecessaryFile.add(fileIterator.next());
        while (fileIterator.hasNext()) {
            final String line = fileIterator.next();
            final Matcher matcher = unnecessaryPattern.matcher(line);
            if (!matcher.matches()) {
                noUnnecessaryFile.add(line);
            }
        }
        final List<String> newFile = new LinkedList<String>();
        fileIterator = noUnnecessaryFile.iterator();
        // Jump over start instruction
        newFile.add(fileIterator.next());
        while (fileIterator.hasNext()) {
            String line = fileIterator.next();
            if (line.trim().equals("")) {
                continue;
            }
            final Matcher curCommand = commandPattern.matcher(line);
            if (curCommand.matches()) {
                int commandCode = 0;
                try {
                    commandCode = input.getOpCode(curCommand.group(2));
                } catch (final InvalidOpCodeException e) {
                    System.out.println("Invalid command sequence: " + line);
                    System.exit(-1);
                }
                // System.out.println(line);
                if (curCommand.group(4) != null) {
                    line = "0x" + curCommand.group(1) + " 0x"
                            + Integer.toHexString(commandCode + Integer.decode(curCommand.group(4)));
                } else {
                    line = "0x" + curCommand.group(1) + " 0x" + Integer.toHexString(commandCode);
                }
                // System.out.println(line);
            } else {
                System.out.println("Invalid command sequence: " + line);
                System.exit(-1);
            }
            newFile.add(line);
        }
        input.setFile(newFile);
        return input;
    }

    @Override
    public List<ProcessingActivity> getPrerequisits() {
        final List<ProcessingActivity> result = new LinkedList<ProcessingActivity>();
        result.add(new PreProcessor());
        return result;
    }

    public int hashCode() {
        return "CodewordConverter".hashCode();
    }

    public boolean equals(final Object obj) {
        return obj.getClass().getCanonicalName().equals(this.getClass().getCanonicalName());
    }
}
