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
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.chromatogram.msd.classifier.core.ChromatogramClassifier;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.menu.AbstractChartMenuEntry;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;

public class ClassifierMenuEntry extends AbstractChartMenuEntry implements IChartMenuEntry {

	private ExtendedChromatogramUI extendedChromatogramUI;
	private String name;
	private String classifierId;
	private String type;
	@SuppressWarnings("rawtypes")
	private IChromatogramSelection chromatogramSelection;

	@SuppressWarnings("rawtypes")
	public ClassifierMenuEntry(ExtendedChromatogramUI extendedChromatogramUI, String name, String classifierId, String type, IChromatogramSelection chromatogramSelection) {
		this.extendedChromatogramUI = extendedChromatogramUI;
		this.name = name;
		this.classifierId = classifierId;
		this.type = type;
		this.chromatogramSelection = chromatogramSelection;
	}

	@Override
	public String getCategory() {

		return "Classifier";
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void execute(Shell shell, ScrollableChart scrollableChart) {

		if(chromatogramSelection != null) {
			/*
			 * Create the runnable.
			 */
			IRunnableWithProgress runnable = new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

					switch(type) {
						case ExtendedChromatogramUI.TYPE_MSD:
							if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
								IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
								ChromatogramClassifier.applyClassifier(chromatogramSelectionMSD, classifierId, monitor);
							}
							break;
					}
				}
			};
			/*
			 * Execute
			 */
			extendedChromatogramUI.processChromatogram(runnable);
		}
	}
}