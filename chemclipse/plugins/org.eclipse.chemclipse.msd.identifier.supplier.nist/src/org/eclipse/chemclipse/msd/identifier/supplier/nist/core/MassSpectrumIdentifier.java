/*******************************************************************************
 * Copyright (c) 2010, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - adjust to simplified API, add generics
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.core;

import java.io.FileNotFoundException;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.massspectrum.AbstractMassSpectrumIdentifier;
import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IMassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.core.support.Identifier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.msd.identifier.supplier.nist.settings.MassSpectrumIdentifierSettings;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public class MassSpectrumIdentifier extends AbstractMassSpectrumIdentifier {

	private static final String DESCRIPTION = "NIST-DB Identifier";

	@Override
	public IProcessingInfo<IMassSpectra> identify(List<IScanMSD> massSpectrumList, IMassSpectrumIdentifierSettings identifierSettings, IProgressMonitor monitor) {

		IProcessingInfo<IMassSpectra> processingInfo = new ProcessingInfo<>();
		MassSpectrumIdentifierSettings massSpectrumIdentifierSettings;
		if(identifierSettings instanceof MassSpectrumIdentifierSettings) {
			massSpectrumIdentifierSettings = (MassSpectrumIdentifierSettings)identifierSettings;
		} else {
			massSpectrumIdentifierSettings = PreferenceSupplier.getMassSpectrumIdentifierSettings();
		}
		try {
			Identifier identifier = new Identifier();
			IMassSpectra massSpectra = identifier.runMassSpectrumIdentification(massSpectrumList, massSpectrumIdentifierSettings, monitor);
			processingInfo.setProcessingResult(massSpectra);
		} catch(FileNotFoundException e) {
			processingInfo.addErrorMessage(DESCRIPTION, "An I/O error ocurred.");
		}
		/*
		 * Run the identifier.
		 */
		return processingInfo;
	}
}
