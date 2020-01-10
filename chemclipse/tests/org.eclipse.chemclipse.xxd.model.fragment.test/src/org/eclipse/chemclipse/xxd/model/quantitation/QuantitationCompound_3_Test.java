/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.quantitation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.model.quantitation.IQuantitationPeak;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignal;
import org.eclipse.chemclipse.model.quantitation.IQuantitationSignals;
import org.eclipse.chemclipse.model.quantitation.IResponseSignal;
import org.eclipse.chemclipse.model.quantitation.IResponseSignals;
import org.eclipse.chemclipse.msd.model.core.AbstractIon;
import org.eclipse.chemclipse.msd.model.implementation.QuantitationPeakMSD;

public class QuantitationCompound_3_Test extends ReferencePeakMSDTestCase {

	private IQuantitationCompound quantitationCompound;
	private IQuantitationSignals quantitationSignals;
	private IResponseSignals concentrationResponseEntries;

	@Override
	protected void setUp() throws Exception {

		super.setUp();
		quantitationCompound = new QuantitationCompound("Styrene", "mg/ml", 5500);
		//
		List<IQuantitationPeak> quantitationPeaks = new ArrayList<IQuantitationPeak>();
		IQuantitationPeak quantitationPeak = new QuantitationPeakMSD(getReferencePeakMSD_TIC_1(), 0.1d, "mg/ml");
		quantitationPeaks.add(quantitationPeak);
		quantitationCompound.getQuantitationPeaks().addAll(quantitationPeaks);
		//
		quantitationCompound.setUseTIC(true);
		quantitationCompound.calculateSignalTablesFromPeaks();
		//
		quantitationSignals = quantitationCompound.getQuantitationSignals();
		concentrationResponseEntries = quantitationCompound.getResponseSignals();
	}

	@Override
	protected void tearDown() throws Exception {

		super.tearDown();
		quantitationCompound = null;
	}

	public void testGetQuantitationSignals_1() {

		assertNotNull(quantitationSignals);
	}

	public void testGetQuantitationSignals_2() {

		assertEquals(1, quantitationSignals.size());
	}

	public void testGetQuantitationSignals_3() {

		IQuantitationSignal quantitationSignal = quantitationSignals.first();
		assertEquals(AbstractIon.TIC_ION, quantitationSignal.getSignal());
		assertEquals(IQuantitationSignal.ABSOLUTE_RELATIVE_RESPONSE, quantitationSignal.getRelativeResponse());
		assertEquals(0.0d, quantitationSignal.getUncertainty());
		assertTrue(quantitationSignal.isUse());
	}

	public void testGetConcentrationResponseEntries_1() {

		assertNotNull(concentrationResponseEntries);
	}

	public void testGetConcentrationResponseEntries_2() {

		assertEquals(1, concentrationResponseEntries.size());
	}

	public void testGetConcentrationResponseEntries_3() {

		IResponseSignal concentrationResponseEntry = concentrationResponseEntries.get(0);
		assertEquals(AbstractIon.TIC_ION, concentrationResponseEntry.getSignal());
		assertEquals(0.1d, concentrationResponseEntry.getConcentration());
		assertEquals(750220.0d, concentrationResponseEntry.getResponse());
	}
}
