package org.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.codeProcessing.WorkflowControl;
import org.codeProcessing.activities.CodewordConverter;

public class Input {

    private Input() {
    }

    public static LinkedList<String[]> loadFile(final File file) {
        List<String> list = new LinkedList<String>();

        try {
            String line;
            final BufferedReader reader = new BufferedReader(new FileReader(file));

            while ((line = reader.readLine()) != null) {
                list.add(line);
            }

            reader.close();

        } catch (final IOException e) {
            System.out.println(e.getMessage());
        }
        final WorkflowControl codeProcessor = new WorkflowControl(list);
        codeProcessor.addActivity(new CodewordConverter());
        list = codeProcessor.execute();

        final LinkedList<String[]> result = new LinkedList<String[]>();
        final Iterator<String> processedLineIterator = list.iterator();
        while (processedLineIterator.hasNext()) {
            final String line = processedLineIterator.next();
            result.add(line.split("[ \t]+"));
        }

        return result;
    }
}
