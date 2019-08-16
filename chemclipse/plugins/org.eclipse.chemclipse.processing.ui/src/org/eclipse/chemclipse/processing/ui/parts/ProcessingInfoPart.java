/*******************************************************************************
 * Copyright (c) 2012, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Janos Binder - cleanup
 * Christoph Läubrich - use E4 DI to listen for topic changes, init view with current data on construction
 *******************************************************************************/
package org.eclipse.chemclipse.processing.ui.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.chemclipse.processing.core.MessageProvider;
import org.eclipse.chemclipse.processing.ui.support.DynamicProcessingInfoUpdateNotifier;
import org.eclipse.chemclipse.processing.ui.swt.ProcessingInfoUI;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

public class ProcessingInfoPart {

	public static String ID = IPerspectiveAndViewIds.VIEW_PROCESSING_INFO;
	//
	private static final String POPUP_MENU_ID = "#PopUpMenu"; // $NON-NLS-1$
	private static final String POPUP_MENU_POSTFIX = "PopUpMenu"; // $NON-NLS-1$
	//
	@Inject
	private Composite parent;
	@Inject
	private DynamicProcessingInfoUpdateNotifier updateNotifier;
	/*
	 * The info is static to display it on focus.
	 */
	private ProcessingInfoUI processingInfoUI;

	@PostConstruct
	private void createControl() {

		parent.setLayout(new FillLayout());
		processingInfoUI = new ProcessingInfoUI(parent, SWT.NONE);
		TableViewer tableViewer = processingInfoUI.getTableViewer();
		/*
		 * Copy and Paste of the table content.
		 */
		tableViewer.getTable().addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				/*
				 * The selected content will be placed to the clipboard if the
				 * user is using "Function + c". "Function-Key" 262144
				 * (stateMask) + "c" 99 (keyCode)
				 */
				if(e.keyCode == 99 && e.stateMask == 262144) {
					processingInfoUI.copyToClipboard();
				}
			}
		});
		initContextMenu();
		processingInfoUI.update(updateNotifier.getProcessingInfo());
	}

	@Focus
	public void setFocus() {

		processingInfoUI.setFocus();
	}

	/*
	 * Initialize a context menu.
	 */
	private void initContextMenu() {

		MenuManager menuManager = new MenuManager(POPUP_MENU_ID, getClass().getName() + POPUP_MENU_POSTFIX);
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {

				IAction action = new Action() {

					@Override
					public void run() {

						super.run();
						processingInfoUI.copyToClipboard();
					}
				};
				action.setText("Copy selection to clipboard");
				manager.add(action);
			}
		});
		TableViewer tableViewer = processingInfoUI.getTableViewer();
		Menu menu = menuManager.createContextMenu(tableViewer.getTable());
		tableViewer.getTable().setMenu(menu);
	}

	@Inject
	public void update(@Optional @UIEventTopic(IChemClipseEvents.TOPIC_PROCESSING_INFO_UPDATE) MessageProvider data) {

		if(processingInfoUI != null) {
			processingInfoUI.update(data);
		}
	}
}