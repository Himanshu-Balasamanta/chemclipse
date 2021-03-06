/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.preferences;

import org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.ui.preferences.fieldeditors.TextFieldEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PreferencePageSubtract extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePageSubtract() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStoreSubtract());
		setTitle("Subtract");
		setDescription("");
	}

	public void createFieldEditors() {

		addField(new BooleanFieldEditor(PreferenceSupplier.P_USE_NOMINAL_MZ, "Use nominal m/z", getFieldEditorParent()));
		addField(new BooleanFieldEditor(PreferenceSupplier.P_USE_NORMALIZED_SCAN, "Use normalized scan", getFieldEditorParent()));
		addField(new TextFieldEditor(PreferenceSupplier.P_SUBTRACT_MASS_SPECTRUM, "Subtract mass spectrum", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {

	}
}
