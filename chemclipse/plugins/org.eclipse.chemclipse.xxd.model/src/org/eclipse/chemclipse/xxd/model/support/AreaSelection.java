/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Stark - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.support;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 *  Describes the criterion to select the peak area:
 *  <li>{@link #AREA_LESS_THAN_MINIMUM}</li>
 *  <li>{@link #AREA_GREATER_THAN_MAXIMUM}</li>
 *  <li>{@link #AREA_NOT_WITHIN_RANGE}</li>
 */
public enum AreaSelection {
	/**
	 * Select peak areas smaller than the defined minimum
	 */
	AREA_LESS_THAN_MINIMUM("Area < minimum"), //
	/**
	 * Select peak areas greater than the defined maximum
	 */
	AREA_GREATER_THAN_MAXIMUM("Area > maximum"), //
	/**
	 * Select peak areas within a specified range, e.g. greater than the defined
	 * minimum and smaller than the defined maximum
	 */
	AREA_NOT_WITHIN_RANGE("Area < minimum or Area > maximum"); //

	@JsonValue
	private String filterSelectionCriterion;

	private AreaSelection(String filterSelectionCriterion) {

		this.filterSelectionCriterion = filterSelectionCriterion;
	}

	@Override
	public String toString() {

		return filterSelectionCriterion;
	}
}
