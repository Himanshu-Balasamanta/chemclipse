/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support;

import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceStoreTargetDisplaySettings implements TargetDisplaySettings {

	private final IPreferenceStore preferenceStore;

	private PreferenceStoreTargetDisplaySettings(IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
		preferenceStore.setDefault(PreferenceConstants.P_PEAK_LABELS_ROTATION, PreferenceConstants.DEF_PEAK_LABELS_ROTATION);
		preferenceStore.setDefault(PreferenceConstants.P_PEAK_LABELS_COLLISION_DETECTION_DEPTH, PreferenceConstants.DEF_PEAK_LABELS_COLLISION_DETECTION_DEPTH);
	}

	@Override
	public boolean isShowPeakLabels() {

		return preferenceStore.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_PEAK_LABELS);
	}

	@Override
	public boolean isShowScanLables() {

		return preferenceStore.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_SCAN_LABELS);
	}

	@Override
	public LibraryField getField() {

		String string = preferenceStore.getString(PreferenceConstants.P_TARGET_LABEL_FIELD);
		if(string != null) {
			try {
				return LibraryField.valueOf(string);
			} catch(IllegalArgumentException e) {
				// must use the default then...
			}
		}
		return LibraryField.NAME;
	}

	@Override
	public void setField(LibraryField libraryField) {

		preferenceStore.setValue(PreferenceConstants.P_TARGET_LABEL_FIELD, libraryField.name());
	}

	@Override
	public void setShowPeakLabels(boolean showPeakLabels) {

		preferenceStore.setValue(PreferenceConstants.P_SHOW_CHROMATOGRAM_PEAK_LABELS, showPeakLabels);
	}

	@Override
	public void setShowScanLables(boolean showScanLables) {

		preferenceStore.setValue(PreferenceConstants.P_SHOW_CHROMATOGRAM_SCAN_LABELS, showScanLables);
	}

	public static TargetDisplaySettings getSettings(IPreferenceStore preferenceStore) {

		return new PreferenceStoreTargetDisplaySettings(preferenceStore);
	}

	@Override
	public int getRotation() {

		return preferenceStore.getInt(PreferenceConstants.P_PEAK_LABELS_ROTATION);
	}

	@Override
	public void setRotation(int degree) {

		preferenceStore.setValue(PreferenceConstants.P_PEAK_LABELS_ROTATION, degree);
	}

	@Override
	public int getCollisionDetectionDepth() {

		return preferenceStore.getInt(PreferenceConstants.P_PEAK_LABELS_COLLISION_DETECTION_DEPTH);
	}

	@Override
	public void setCollisionDetectionDepth(int depth) {

		preferenceStore.setValue(PreferenceConstants.P_PEAK_LABELS_COLLISION_DETECTION_DEPTH, depth);
	}
}
