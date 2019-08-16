/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.exceptions;

public class InstanceIsNullException extends Exception {

	/**
	 * Renew the serialVersionUID any time you have changed some fields or
	 * methods.
	 */
	private static final long serialVersionUID = -1000970121947163246L;

	public InstanceIsNullException() {
		super();
	}

	public InstanceIsNullException(String message) {
		super(message);
	}
}