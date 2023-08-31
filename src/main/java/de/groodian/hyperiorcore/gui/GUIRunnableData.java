package de.groodian.hyperiorcore.gui;

import java.time.Duration;
import java.time.Instant;

public class GUIRunnableData {

    public GUIRunnable guiRunnable;
    public Duration duration;
    public Instant lastCalled;

    public GUIRunnableData(GUIRunnable guiRunnable, Duration duration, Instant lastCalled) {
        this.guiRunnable = guiRunnable;
        this.duration = duration;
        this.lastCalled = lastCalled;
    }

}
