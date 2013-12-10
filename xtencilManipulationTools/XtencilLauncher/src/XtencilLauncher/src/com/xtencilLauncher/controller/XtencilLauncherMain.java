package com.xtencilLauncher.controller;

import com.xtencilLauncher.model.FIProperties;
import com.xtencilLauncher.view.MainWindow;
import com.xtencilLauncher.view.PreferencesUI;

public class XtencilLauncherMain {
	public static void main(String[] args) {
		FIProperties fiProperties = new FIProperties(false);
		MainWindow mainWindow = new MainWindow(fiProperties);
		PreferencesUI preferencesUI = new PreferencesUI();
		@SuppressWarnings("unused")
		XtencilLauncherController xlc = new XtencilLauncherController(fiProperties, mainWindow, preferencesUI);
		
	}
}
