/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add datatypes to supplier
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier;

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.BaselineDetector;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.IBaselineDetectorSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core.IBaselineDetectorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.exceptions.NoBaselineDetectorAvailableException;
import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.settings.IBaselineDetectorSettings;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IChromatogramSelectionProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class BaselineDetectorTypeSupplier extends AbstractProcessTypeSupplier implements IChromatogramSelectionProcessTypeSupplier {

	public static final String CATEGORY = "Baseline Detector";

	public BaselineDetectorTypeSupplier() {
		super(CATEGORY);
		try {
			IBaselineDetectorSupport support = BaselineDetector.getBaselineDetectorSupport();
			for(String processorId : support.getAvailableDetectorIds()) {
				IBaselineDetectorSupplier supplier = support.getBaselineDetectorSupplier(processorId);
				//
				ProcessorSupplier processorSupplier = new ProcessorSupplier(processorId, ALL_DATA_TYPES, this);
				processorSupplier.setName(supplier.getDetectorName());
				processorSupplier.setDescription(supplier.getDescription());
				processorSupplier.setSettingsClass(supplier.getSettingsClass());
				addProcessorSupplier(processorSupplier);
			}
		} catch(NoBaselineDetectorAvailableException e) {
			// dosen't matter here...
		}
	}

	@Override
	public IProcessingInfo<IChromatogramSelection<?, ?>> applyProcessor(IChromatogramSelection<?, ?> chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		if(processSettings instanceof IBaselineDetectorSettings) {
			return getProcessingResult(BaselineDetector.setBaseline(chromatogramSelection, (IBaselineDetectorSettings)processSettings, processorId, monitor), chromatogramSelection);
		} else {
			return getProcessingResult(BaselineDetector.setBaseline(chromatogramSelection, processorId, monitor), chromatogramSelection);
		}
	}
}
