package org.codeProcessing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codeProcessing.exceptions.InvalidOpCodeException;

/**
 * Class which stores objects of a specific type, which are necessary for the
 * execution of the code processing
 * 
 * @author Samuel Teuber
 * @version 1.0
 */
public class ProcessingDataCollection {

    private final Map<String, Object> data;
    private final Map<String, Integer> codewords;
    private List<String> file;

    public ProcessingDataCollection(final List<String> fileParam) {
        this.file = fileParam;
        this.data = new HashMap<String, Object>();
        codewords = new HashMap<String, Integer>();
        codewords.put("LDC", 0x000000);
        codewords.put("DS", 0x000000);
        codewords.put("LDV", 0x100000);
        codewords.put("STV", 0x200000);
        codewords.put("ADD", 0x300000);
        codewords.put("AND", 0x400000);
        codewords.put("OR", 0x500000);
        codewords.put("XOR", 0x600000);
        codewords.put("EQL", 0x700000);
        codewords.put("JMP", 0x800000);
        codewords.put("JMN", 0x900000);
        codewords.put("LDIV", 0xA00000);
        codewords.put("STIV", 0xB00000);
        codewords.put("JMS", 0xC00000);
        codewords.put("JIND", 0xD00000);
        codewords.put("HALT", 0xF00000);
        codewords.put("NOT", 0xF10000);
        codewords.put("RAR", 0xF20000);

    }

    public boolean isKeyword(final String name) {
        return codewords.containsKey(name);
    }

    public int getOpCode(final String name) throws InvalidOpCodeException {
        if (isKeyword(name)) {
            return codewords.get(name);
        } else {
            throw new InvalidOpCodeException();
        }
    }

    public void addData(final String name, final Object data) {
        this.data.put(name, data);
    }

    public Object retrieveData(final String name) {
        return this.data.get(name);
    }

    public List<String> getFile() {
        return this.file;
    }

    public void setFile(final List<String> fileParam) {
        this.file = fileParam;
    }
}
