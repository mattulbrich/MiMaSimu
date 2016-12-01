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

public class PreProcessor implements ProcessingActivity {

    private static String variableRegex = "^\\s*0x([0-9a-fA-F]{1,5})\\s+([A-Z_]+)(\\s+([A-Z]+)?(\\s+[A-Z_0-9x]+)?)(\\s+;.*)?\\s*$";
    private static Pattern variablePattern = Pattern.compile(variableRegex);

    @Override
    public ProcessingDataCollection processDataObject(final ProcessingDataCollection inputParam) {
        final ProcessingDataCollection input = inputParam;
        final Map<String, Integer> variables = new HashMap<String, Integer>();
        final List<String> file = input.getFile();
        final List<String> preFile = new LinkedList<String>();
        Iterator<String> fileIterator = file.iterator();
        int lineNum = 0;
        while (fileIterator.hasNext()) {
            lineNum++;
            final String line = fileIterator.next();
            final Matcher matcher = variablePattern.matcher(line);
            if (matcher.matches() && !input.isKeyword(matcher.group(2))) {
                // System.out.println(matcher.group(1));
                preFile.add("0x" + matcher.group(1) + " " + matcher.group(4));
                variables.put(matcher.group(2), Integer.parseInt(matcher.group(1), 16));
            } else {
                // System.out.println("No match found:" + line);
                preFile.add(line);
            }
        }
        final List<String> newFile = new LinkedList<String>();
        if (variables.containsKey("START")) {
            newFile.add("start 0x" + Integer.toHexString(variables.get("START")));
        } else {
            newFile.add("start 0x0");
        }
        fileIterator = preFile.iterator();
        lineNum = 0;
        while (fileIterator.hasNext()) {
            lineNum++;
            String line = fileIterator.next();
            final Matcher matcher = variablePattern.matcher(line);
            final Iterator<String> variableWords = variables.keySet().iterator();
            while (variableWords.hasNext()) {
                final String curWord = variableWords.next();
                final Pattern wordPattern = Pattern
                        .compile("^\\s*0x([0-9a-fA-F]{1,5})(\\s+([A-Z_]+))\\s+" + curWord + "($|\\s+)[^;]*(;.*)?");
                final Matcher wordMatcher = wordPattern.matcher(line);
                if (wordMatcher.matches()) { // Check if current word exists in
                                             // line
                    line = "0x" + wordMatcher.group(1) + wordMatcher.group(2) + " 0x"
                            + Integer.toHexString(variables.get(curWord));
                } else {
                    final Pattern commentPattern = Pattern.compile("(.*);.*");
                    final Matcher removeComment = commentPattern.matcher(line);
                    if (removeComment.matches()) {
                        line = removeComment.group(1);
                    }
                }
            }
            newFile.add(line);
        }
        input.setFile(newFile);
        return input;
    }

    @Override
    public List<ProcessingActivity> getPrerequisits() {
        final List<ProcessingActivity> result = new LinkedList<ProcessingActivity>();
        result.add(new AddressBuilder());
        return result;
    }

    @Override
    public int hashCode() {
        return "PreProcessor".hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj.getClass().getCanonicalName().equals(this.getClass().getCanonicalName());
    }

}
