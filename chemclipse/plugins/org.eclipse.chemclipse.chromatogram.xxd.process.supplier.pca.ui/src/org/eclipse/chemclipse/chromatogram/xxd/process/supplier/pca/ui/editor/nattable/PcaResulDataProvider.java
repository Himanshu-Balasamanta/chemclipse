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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editor.nattable;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

public class PcaResulDataProvider implements IDataProvider {

	private SortModel sortModel;
	private TableProvider tableProvider;

	public PcaResulDataProvider(TableProvider tableProvider, SortModel sortModel) {
		this.sortModel = sortModel;
		this.tableProvider = tableProvider;
	}

	@Override
	public int getColumnCount() {

		return tableProvider.getColumnCount();
	}

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {

		int sortRowIndex = sortModel.getOrderRow().get(rowIndex);
		if(columnIndex == TableProvider.COLUMN_INDEX_SELECTED) {
			return tableProvider.getDataTable().getRetentionTimes().get(sortRowIndex).isSelected();
		} else if(columnIndex == TableProvider.COLUMN_INDEX_RETENTION_TIMES) {
			return tableProvider.getDataTable().getRetentionTimes().get(sortRowIndex).getRetentionTimeMinutes();
		} else if(columnIndex == TableProvider.COLUMN_INDEX_PEAK_NAMES) {
			String peaksNames = tableProvider.getDataTable().getPeaksNames().get(sortRowIndex);
			return peaksNames;
		} else {
			List<ISample> samples = tableProvider.getDataTable().getSamples();
			ISample sample = samples.get(columnIndex - TableProvider.NUMER_OF_DESCRIPTION_COLUMN);
			double sampleData = sample.getSampleData().get(sortRowIndex).getModifiedData();
			return sampleData;
		}
	}

	@Override
	public int getRowCount() {

		return tableProvider.getRowCount();
	}

	@Override
	public void setDataValue(int columnIndex, int rowIndex, Object newValue) {

		int sortRowIndex = sortModel.getOrderRow().get(rowIndex);
		if(columnIndex == TableProvider.COLUMN_INDEX_SELECTED) {
			tableProvider.getDataTable().getRetentionTimes().get(sortRowIndex).setSelected((boolean)newValue);
		}
	}
}
