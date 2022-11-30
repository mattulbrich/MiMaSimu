package org;

import java.io.File;

import org.components.ACC;
import org.components.ALU;
import org.components.Bus;
import org.components.Clock;
import org.components.IAR;
import org.components.IR;
import org.components.One;
import org.components.SAR;
import org.components.SDR;
import org.components.SW;
import org.components.X;
import org.components.Y;
import org.components.Z;
import org.external.Memory;
import org.gui.GUI;
import org.io.Input;
import org.io.Output;
import org.io.TempMem;
import org.loadingScreen.LoadingScreen;

/**
 * Main Controller of the simulation
 * 
 * @author Dominik Muth
 * 
 */
public class Controller {
    private Bus bus;
    private ACC akku;
    private IAR iar;
    private One eins;
    private IR ir;
    private Z z;
    private X x;
    private Y y;
    private ALU alu;
    private SDR sdr;
    private SAR sar;
    private Memory speicher;
    private SW sw;
    private final Clock clock;

    private final GUI gui;
    private final LoadingScreen ls;

    private boolean auto_run;
    private int mem_start;
    private int mem_end;

    /**
     * Creates a new Controller
     * 
     * @param args
     *            arguments given by the console
     */
    public Controller(final String[] args) {
        auto_run = false;
        mem_start = 0;
        mem_end = 0;
        if (args.length > 1) {
            assert args.length > 2;
            auto_run = true;
            mem_start = Integer.decode(args[1]);
            mem_end = Integer.decode(args[2]);
            assert mem_start <= mem_end;
        }
        if (!auto_run) {
            ls = new LoadingScreen();
            ls.start();
        } else {
            ls = null;
        }

        // Ignore file ending since it should be the choice of the user
        // if ((args.length > 0)
        // && (args[0].endsWith(".mima") || args[0].endsWith(".mem"))) {
        if (args.length > 0) {
            final File input = new File(args[0]);
            if (input.exists()) {
                loadMem(input);
            } else {
                System.err.println("File doesn't exist!");
            }
        }

        if (!auto_run) {
            gui = new GUI(this);
        } else {
            gui = null;
        }
        initMima();
        if (!auto_run) {
            ls.stop();
            gui.setVisible(true);
            clock = new Clock(500, sw);
            clock.pause(true);
        } else {
            clock = new Clock(0, sw);
            clock.pause(false);
        }

        Thread compute_thread = new Thread(clock);
        compute_thread.start();
        if (auto_run) {
            try {
                compute_thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for (int i = mem_start; i <= mem_end; i++) {
                System.out.print("0x"+String.format("%05X: ", i));
                System.out.println("0x"+String.format("%06X", speicher.getMemory().getOrDefault(i, 0)));
            }
        }
        return;
    }

    private void initMima() {
        bus = new Bus();
        akku = new ACC(bus);
        iar = new IAR(bus);
        eins = new One(bus);
        ir = new IR(bus);
        x = new X(bus);
        y = new Y(bus);
        z = new Z(bus);
        alu = new ALU(x, y, z);
        sdr = new SDR(bus);
        speicher = new Memory(sdr);
        sar = new SAR(bus, speicher);
        sw = new SW(akku, iar, eins, ir, z, x, y, alu, sdr, sar, speicher, this);

        iar.set(speicher.getStartPoint());

        if (!auto_run) {
            gui.setStatus(false, false, false, false, false, false, false, false, false, false, false, false, false, false,
                    false, 0);
            gui.setValues(0, 0, 0, 0, 0, 0, 0, 0, 0, speicher.getMemory());
        }
    }

    /**
     * set status of the elements
     * 
     * @param Ar
     * @param Aw
     * @param RX
     * @param RY
     * @param RZ
     * @param E
     * @param Pr
     * @param Pw
     * @param Ir
     * @param Iw
     * @param Dr
     * @param Dw
     * @param S
     * @param R
     * @param W
     * @param c
     */
    public void setStatus(final boolean Ar, final boolean Aw, final boolean RX, final boolean RY, final boolean RZ,
            final boolean E, final boolean Pr, final boolean Pw, final boolean Ir, final boolean Iw, final boolean Dr,
            final boolean Dw, final boolean S, final boolean R, final boolean W, final int c) {
        if (auto_run) { return; }
        gui.setValues(akku.getValue(), c, iar.getValue(), ir.getValue(), sar.getValue(), sdr.getValue(), x.getValue(),
                y.getValue(), z.getValue(), speicher.getMemory());
        gui.setStatus(Ar, Aw, RX, RY, RZ, E, Pr, Pw, Ir, Iw, Dr, Dw, S, R, W, c);
    }

    /**
     * toggle clock state
     * 
     * @param active
     *            whether the clock is active or not
     */
    public void clock(final boolean active) {
        if (auto_run) { return; }
        gui.clock(active);
    }

    /**
     * toggle clock manually
     */
    public void manualClock() {
        sw.clock();
        clock(false);
    }

    public void manualMacroClock() {
        clock.runMacroStep();
    }

    /**
     * toggle pause
     * 
     * @param state
     *            whether the simulation is paused or not
     */
    public void pause(final boolean state) {
        clock.pause(state);
        if (auto_run) {
            if (state) {clock.stop();}
        } else {
            gui.pause(state);
        }
    }

    /**
     * reset the simulation
     */
    public void reset() {
        clock.pause(true);
        if (!auto_run) {
            gui.deleteValues(speicher.getMemory());
        }
        initMima();
        clock.setSW(sw);

    }

    /**
     * change timeout and therefor frequency of the clock
     * 
     * @param timeout
     *            time in ms
     */
    public void setTimeout(final int timeout) {
        clock.setTimeout(timeout);
    }

    /**
     * loads a memory file
     * 
     * @param filepath
     *            path of the memory file
     */
    public void loadMem(final String filepath) {
        loadMem(new File(filepath));
    }

    /**
     * loads a memory file
     * 
     * @param file
     *            memory file
     */
    public void loadMem(final File file) {
        TempMem.setText(Input.loadFile(file));
    }

    /**
     * save memory
     * 
     * @param filepath
     *            file path to save memory to
     */
    public void saveMem(final String filepath) {
        saveMem(new File(filepath));
    }

    /**
     * save memory
     * 
     * @param file
     *            file to save memory to
     */
    public void saveMem(final File file) {
        Output.saveFile(file, TempMem.getText());
    }

    /**
     * save result memory to file
     * 
     * @param filepath
     *            file path to save result to
     */
    public void saveResult(final String filepath) {
        saveResult(new File(filepath));
    }

    /**
     * save result memory to file
     * 
     * @param file
     *            file to save result to
     */
    public void saveResult(final File file) {
        Output.saveFile(file, speicher.getState());
    }

}
