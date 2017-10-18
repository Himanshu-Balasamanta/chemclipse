/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.provider;

import java.util.ArrayList;
import java.util.List;

public class ExplorerListSupport {

	/**
	 * Use static methods only.
	 */
	private ExplorerListSupport() {
	}

	public static List<ISupplierFileIdentifier> getSupplierFileIdentifierList(ISupplierFileIdentifier supplierFileIdentifier) {

		List<ISupplierFileIdentifier> list = new ArrayList<ISupplierFileIdentifier>();
		list.add(supplierFileIdentifier);
		return list;
	}

	public static List<ISupplierFileEditorSupport> getChromatogramEditorSupportList(ISupplierFileEditorSupport supplierEditorSupport) {

		List<ISupplierFileEditorSupport> list = new ArrayList<ISupplierFileEditorSupport>();
		list.add(supplierEditorSupport);
		return list;
	}
}
