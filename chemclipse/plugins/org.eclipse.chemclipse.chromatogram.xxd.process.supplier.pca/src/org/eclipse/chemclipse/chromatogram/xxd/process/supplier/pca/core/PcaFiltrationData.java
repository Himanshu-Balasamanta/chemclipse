/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters.IFilter;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IRetentionTime;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.core.runtime.IProgressMonitor;

public class PcaFiltrationData {

	private List<IFilter> filters;

	public PcaFiltrationData() {
		filters = new ArrayList<>();
	}

	public List<IFilter> getFilters() {

		return filters;
	}

	public void process(ISamples samples, boolean resetSelectedRetentionTimes, IProgressMonitor monitor) {

		List<IRetentionTime> retentionTimes = samples.getExtractedRetentionTimes();
		if(resetSelectedRetentionTimes) {
			setSelectAllRow(samples, true);
		}
		if(filters != null && !filters.isEmpty()) {
			for(int i = 0; i < filters.size(); i++) {
				List<Boolean> result = filters.get(i).filter(samples);
				for(int j = 0; j < result.size(); j++) {
					retentionTimes.get(j).setSelected(retentionTimes.get(j).isSelected() && result.get(j));
				}
			}
		}
	}

	public void setSelectAllRow(ISamples samples, boolean selection) {

		List<IRetentionTime> retentionTimes = samples.getExtractedRetentionTimes();
		for(int i = 0; i < retentionTimes.size(); i++) {
			retentionTimes.get(i).setSelected(selection);
		}
	}
}
