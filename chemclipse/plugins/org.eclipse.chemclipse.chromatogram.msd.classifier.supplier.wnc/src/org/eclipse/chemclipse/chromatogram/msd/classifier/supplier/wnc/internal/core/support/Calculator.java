/*******************************************************************************
 * Copyright (c) 2011, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.internal.core.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.exceptions.ClassifierException;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.IWncIon;
import org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.wnc.model.IWncIons;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;

public class Calculator {

	public IWncIons calculateIonPercentages(IChromatogramSelectionMSD chromatogramSelection, IWncIons wncIons) throws ClassifierException {

		Map<Integer, Double> ionAbundanceValues = extractIonValues(chromatogramSelection);
		double factorMax = calculateFactorMax(ionAbundanceValues);
		double factorSum = calculateFactorSum(ionAbundanceValues);
		IWncIons wncIonsEdited = calculateAndSetIntensityValues(ionAbundanceValues, wncIons, factorMax, factorSum);
		return wncIonsEdited;
	}

	private Map<Integer, Double> extractIonValues(IChromatogramSelectionMSD chromatogramSelection) {

		IChromatogramMSD chromatogram = chromatogramSelection.getChromatogramMSD();
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		Map<Integer, Double> ionAbundanceValues = new HashMap<Integer, Double>();
		double signal;
		/*
		 * Extract the ion abundance values.
		 */
		for(int scan = startScan; scan <= stopScan; scan++) {
			IExtractedIonSignal scanSignals = chromatogram.getSupplierScan(scan).getExtractedIonSignal();
			for(Integer ion = scanSignals.getStartIon(); ion <= scanSignals.getStopIon(); ion++) {
				/*
				 * If there is still an abundance for the ion in the map, than add the signal.
				 * Otherwise, store a new key-value pair in the map.
				 */
				signal = scanSignals.getAbundance(ion);
				if(ionAbundanceValues.containsKey(ion)) {
					signal += ionAbundanceValues.get(ion);
					ionAbundanceValues.put(ion, signal);
				} else {
					ionAbundanceValues.put(ion, signal);
				}
			}
		}
		return ionAbundanceValues;
	}

	private double calculateFactorMax(Map<Integer, Double> ionAbundanceValues) throws ClassifierException {

		double maxAbundance = Collections.max(ionAbundanceValues.values());
		if(maxAbundance == 0) {
			throw new ClassifierException("There is no max abundance value.");
		}
		return 100.0d / maxAbundance;
	}

	private double calculateFactorSum(Map<Integer, Double> ionAbundanceValues) throws ClassifierException {

		/*
		 * Get the sum of all signals.
		 */
		double sumAbundance = 0.0d;
		for(Double value : ionAbundanceValues.values()) {
			sumAbundance += value;
		}
		if(sumAbundance == 0) {
			throw new ClassifierException("There is no abundance value.");
		}
		return 100.0d / sumAbundance;
	}

	private IWncIons calculateAndSetIntensityValues(Map<Integer, Double> ionAbundanceValues, IWncIons wncIons, double factorMax, double factorSum) {

		double percentageMaxIntensity;
		double percentageSumIntensity;
		IWncIon wncIon;
		/*
		 * Calculate the percentages.
		 */
		Set<Map.Entry<Integer, Double>> entrySet = ionAbundanceValues.entrySet();
		for(Map.Entry<Integer, Double> entry : entrySet) {
			percentageMaxIntensity = factorMax * entry.getValue();
			percentageSumIntensity = factorSum * entry.getValue();
			/*
			 * Get the wncIon if not null and assign the calculated intensities.
			 */
			wncIon = wncIons.getWNCIon(entry.getKey());
			if(wncIon != null) {
				wncIon.setPercentageMaxIntensity(percentageMaxIntensity);
				wncIon.setPercentageSumIntensity(percentageSumIntensity);
			}
		}
		return wncIons;
	}
}
