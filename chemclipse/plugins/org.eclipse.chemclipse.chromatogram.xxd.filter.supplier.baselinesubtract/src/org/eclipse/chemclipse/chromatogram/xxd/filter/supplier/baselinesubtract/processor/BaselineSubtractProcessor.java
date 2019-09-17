/*******************************************************************************
 * Copyright (c) 2016, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.filter.supplier.baselinesubtract.processor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.core.runtime.IProgressMonitor;

public class BaselineSubtractProcessor {

	/**
	 * Removes the baseline from the given chromatogram selection.
	 * 
	 * @param chromatogramSelection
	 */
	public void removeBaseline(IChromatogramSelection chromatogramSelection, IProgressMonitor monitor) {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		/*
		 * Get the total ion signals.
		 */
		IBaselineModel baselineModel = chromatogram.getBaselineModel();
		List<Integer> scansToRemove = new ArrayList<Integer>();
		for(int i = startScan; i <= stopScan; i++) {
			IScan scan = chromatogram.getScan(i);
			int retentionTime = scan.getRetentionTime();
			float backgroundSignal = baselineModel.getBackgroundAbundance(retentionTime);
			float adjustedSignal = scan.getTotalSignal() - backgroundSignal;
			if(adjustedSignal > 0) {
				scan.adjustTotalSignal(adjustedSignal);
			} else {
				scansToRemove.add(i);
			}
		}
		/*
		 * Remove scans and the baseline.
		 */
		int offset = 0;
		for(int i : scansToRemove) {
			int scan = i + offset;
			chromatogram.removeScan(scan);
			offset--;
		}
		/*
		 * Reset the chromatogram selection if scans are removed.
		 */
		if(scansToRemove.size() > 0) {
			chromatogramSelection.reset(false);
		}
		/*
		 * Recalculate chromatogram values.
		 */
		chromatogram.recalculateScanNumbers();
		chromatogram.recalculateTheNoiseFactor();
		chromatogram.removeAllPeaks();
		baselineModel.removeBaseline();
	}
}
