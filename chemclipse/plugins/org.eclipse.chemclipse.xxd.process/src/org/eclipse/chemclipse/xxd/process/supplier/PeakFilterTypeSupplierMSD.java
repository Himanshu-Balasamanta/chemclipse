/*******************************************************************************
 * Copyright (c) 2013, 2019 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.filter.exceptions.NoPeakFilterSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.filter.settings.IPeakFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.peak.IPeakFilterSupplier;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.peak.IPeakFilterSupport;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.peak.PeakFilter;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.process.support.IChromatogramSelectionProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakFilterTypeSupplierMSD extends AbstractProcessTypeSupplier implements IChromatogramSelectionProcessTypeSupplier {

	public static final String CATEGORY = "Peak Filter";

	public PeakFilterTypeSupplierMSD() {
		super(CATEGORY);
		try {
			IPeakFilterSupport support = PeakFilter.getPeakFilterSupport();
			for(String processorId : support.getAvailableFilterIds()) {
				IPeakFilterSupplier supplier = support.getFilterSupplier(processorId);
				//
				ProcessorSupplier processorSupplier = new ProcessorSupplier(processorId, MSD_DATA_TYPES, this);
				processorSupplier.setName(supplier.getFilterName());
				processorSupplier.setDescription(supplier.getDescription());
				processorSupplier.setSettingsClass(supplier.getSettingsClass());
				addProcessorSupplier(processorSupplier);
			}
		} catch(NoPeakFilterSupplierAvailableException e) {
			// nothing to do
		}
	}

	@Override
	public IProcessingInfo<IChromatogramSelection<?, ?>> applyProcessor(IChromatogramSelection<?, ?> chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor) {

		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			IChromatogramSelectionMSD chromatogramSelectionMSD = (IChromatogramSelectionMSD)chromatogramSelection;
			if(processSettings instanceof IPeakFilterSettings) {
				return getProcessingResult(PeakFilter.applyFilter(chromatogramSelectionMSD, (IPeakFilterSettings)processSettings, processorId, monitor), chromatogramSelection);
			} else {
				return getProcessingResult(PeakFilter.applyFilter(chromatogramSelectionMSD, processorId, monitor), chromatogramSelection);
			}
		} else {
			return getProcessingInfoError(processorId);
		}
	}
}
