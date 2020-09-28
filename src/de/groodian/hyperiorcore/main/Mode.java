package de.groodian.hyperiorcore.main;

public class Mode {
	
	private static ModeType modeType;
	
	public static void setModeType(ModeType modeType) {
		Mode.modeType = modeType;
	}
	
	public static ModeType getModeType() {
		return modeType;
	}

}
