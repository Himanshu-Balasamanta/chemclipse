/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.matlab.parafac.converter;

import java.io.File;

import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.converter.supplier.matlab.parafac.TestPathHelper;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.IPeakMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IPeakModelMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;

import junit.framework.TestCase;

public class MatlabParafacPeakImportConverter_3_ITest extends TestCase {

	private IProcessingInfo processingInfo;
	private Object object;
	private MatlabParafacPeakImportConverter converter;
	private IPeaks peaks;
	private IPeakMSD peak;
	private IPeakMassSpectrum peakMassSpectrum;
	private IExtractedIonSignal extractedIonSignal;
	private IPeakModelMSD peakModel;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		converter = new MatlabParafacPeakImportConverter();
		File file = new File(TestPathHelper.getAbsolutePath(TestPathHelper.TESTFILE_IMPORT_MATLAB_PEAKS));
		processingInfo = converter.convert(file, new NullProgressMonitor());
		object = processingInfo.getProcessingResult();
		if(object instanceof IPeaks) {
			peaks = (IPeaks)object;
		}
		peak = (IPeakMSD)peaks.getPeak(3);
		peakMassSpectrum = peak.getExtractedMassSpectrum();
		extractedIonSignal = peakMassSpectrum.getExtractedIonSignal();
		peakModel = peak.getPeakModel();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
	}

	public void testPeaks_1() {

		assertEquals(3, peaks.size());
	}

	public void testPeaks_2() {

		assertEquals("PARAFAC model, 257820 to 276016 milliseconds, Peak 3", peak.getModelDescription());
	}

	public void testPeaks_3() {

		assertEquals(50, extractedIonSignal.getStartIon());
	}

	public void testPeaks_4() {

		assertEquals(252, extractedIonSignal.getStopIon());
	}

	public void testPeaks_5() {

		assertEquals(0.00133164f, extractedIonSignal.getAbundance(50));
	}

	public void testPeaks_6() {

		assertEquals(1.9297e-005f, extractedIonSignal.getAbundance(252));
	}

	public void testPeaks_7() {

		assertEquals(257820, peakModel.getStartRetentionTime());
	}

	public void testPeaks_8() {

		assertEquals(276016, peakModel.getStopRetentionTime());
	}
}
