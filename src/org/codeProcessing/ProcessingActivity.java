package org.codeProcessing;

import java.util.List;

/**
 * An activity during code processing. IMPORTANT: 2 objects of the same activity
 * need to be considered equal (.equals(Object obj)) and have the same hash code
 * (.hashCode())
 * 
 * @author Samuel Teuber
 * @version 1.0
 */
public interface ProcessingActivity {
    public ProcessingDataCollection processDataObject(ProcessingDataCollection input);

    public List<ProcessingActivity> getPrerequisits();

    public int hashCode();

    public boolean equals(Object obj);
}
