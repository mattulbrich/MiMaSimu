package org.components;

public class Clock implements Runnable {
	private int timeout;
	private SW sw;
	private boolean paused;
	private boolean stopped;

	private boolean runMacro;

	public Clock(int timeout, SW sw) {
		this.timeout = timeout;
		this.sw = sw;
		this.stopped = true;
		this.paused = false;
		this.runMacro = false;
	}

	@Override
	public void run() {
		stopped = false;
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (!stopped) {
			if (!paused) {
				sw.clockOnGUIThread();
				sleep(timeout / 2);
				sw.clockOff();
				sleep(timeout / 2);
			} else if (runMacro) {
				do {
					sw.clockOnGUIThread();
					sleep(timeout / 2);
					sw.clockOff();
					sleep(timeout / 2);
				} while (!sw.isNextMicroZero());
				this.runMacro = false;
			} else
				sleep(100);
		}
	}

	public void pause(boolean value) {
		this.paused = value;
	}

	public void runMacroStep() {
		this.runMacro = true;
	}

	public void stop() {
		this.stopped = true;
	}

	public void setSW(SW sw) {
		this.sw = sw;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout + 10;
	}

	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
