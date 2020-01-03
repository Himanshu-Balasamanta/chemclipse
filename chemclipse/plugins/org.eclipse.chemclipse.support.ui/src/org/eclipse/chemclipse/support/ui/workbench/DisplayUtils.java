/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.workbench;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;

public class DisplayUtils {

	private static final Logger logger = Logger.getLogger(DisplayUtils.class);

	public static Display getDisplay(Widget widget) {

		Display display = null;
		if(widget instanceof Control) {
			display = ((Control)widget).getDisplay();
		} else {
			display = getDisplay();
		}
		return display;
	}

	/**
	 * Invokes the given action in the user interface thread either directly if the current thread is the user-interface-thread or synchronous if the current thread is an application-thread and returns the result to the caller. If the action throws an exception, it is wrapped in
	 * 
	 * @param action
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static <T> T executeInUserInterfaceThread(Callable<T> action) throws InterruptedException, ExecutionException {

		return executeInUserInterfaceThread(new UISynchronize() {

			@Override
			public void syncExec(Runnable runnable) {

				Display.getDefault().syncExec(runnable);
			}

			@Override
			public void asyncExec(Runnable runnable) {

				Display.getDefault().asyncExec(runnable);
			}
		}, action);
	}

	public static <T> T executeInUserInterfaceThread(UISynchronize ui, Callable<T> action) throws InterruptedException, ExecutionException {

		if(Display.findDisplay(Thread.currentThread()) == null) {
			// non ui thread!
			FutureTask<T> task = new FutureTask<>(action);
			try {
				ui.syncExec(task);
			} catch(SWTException e) {
				throw new ExecutionException(e.getCause());
			}
			return task.get();
		} else {
			// ui thread
			try {
				return action.call();
			} catch(Exception e) {
				throw new ExecutionException(e);
			}
		}
	}

	public static Display getDisplay() {

		Display display = null;
		//
		display = Display.getDefault();
		if(display == null) {
			logger.info("Default Display is null.");
			display = Display.getCurrent();
			if(display == null) {
				logger.info("Current Display is null.");
				try {
					display = PlatformUI.getWorkbench().getDisplay();
					if(display == null) {
						logger.info("PlatformUI Display is null.");
					}
				} catch(Exception e) {
					logger.warn(e);
				}
			}
		}
		//
		if(display == null) {
			logger.warn("Display is null!");
		}
		//
		return display;
	}

	public static Shell getShell(Widget widget) {

		Shell shell = null;
		if(widget instanceof Control) {
			shell = ((Control)widget).getShell();
		} else {
			shell = getShell();
		}
		return shell;
	}

	public static Shell getShell() {

		if(Display.getCurrent() == null) {
			logger.error("Try to access shell outside of UI-Thread!");
			Thread.dumpStack();
			return null;
		}
		Shell shell = null;
		//
		Display display = getDisplay();
		if(display != null) {
			try {
				shell = display.getActiveShell();
			} catch(Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
		//
		if(shell == null) {
			if(display != null) {
				Shell[] shells = display.getShells();
				for(Shell s : shells) {
					if(s.isDisposed() || !s.isVisible()) {
						continue;
					}
					Object ignoreDialog = s.getData("org.eclipse.e4.ui.ignoreDialog");
					if(ignoreDialog instanceof Boolean && (Boolean)ignoreDialog) {
						continue;
					}
					return s;
				}
			}
			logger.error("Shell is null!");
		}
		//
		return shell;
	}
}
