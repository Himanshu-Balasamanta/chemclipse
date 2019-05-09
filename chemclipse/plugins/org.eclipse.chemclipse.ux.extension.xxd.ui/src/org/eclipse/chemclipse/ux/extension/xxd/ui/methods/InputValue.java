/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

public class InputValue {

	private Class<?> rawType = null;
	private String name = "";
	private String description = "";
	private String value = null; // Needed for initialization.
	private String defaultValue = "";
	/*
	 * SettingsProperty ...
	 */
	private Object minValue = null;
	private Object maxValue = null;
	private String regularExpression = null;
	private IntegerValidation integerValidation = null;
	private boolean isMultiLine = false; // StringSettingsProperty

	public boolean hasMinMaxConstraint() {

		return (minValue != null) && (maxValue != null);
	}

	public boolean hasRegexConstraint() {

		return (regularExpression != null && !"".equals(regularExpression));
	}

	public boolean hasIntegerValidation() {

		return (integerValidation != null);
	}

	public Class<?> getRawType() {

		return rawType;
	}

	public void setRawType(Class<?> rawType) {

		this.rawType = rawType;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public String getDescription() {

		return description;
	}

	public void setDescription(String description) {

		this.description = description;
	}

	public String getValue() {

		return value;
	}

	public void setValue(String value) {

		this.value = value;
	}

	public String getDefaultValue() {

		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {

		this.defaultValue = defaultValue;
	}

	public Object getMinValue() {

		return minValue;
	}

	public void setMinValue(Object minValue) {

		this.minValue = minValue;
	}

	public Object getMaxValue() {

		return maxValue;
	}

	public void setMaxValue(Object maxValue) {

		this.maxValue = maxValue;
	}

	public String getRegularExpression() {

		return regularExpression;
	}

	public void setRegularExpression(String regularExpression) {

		this.regularExpression = regularExpression;
	}

	public IntegerValidation getIntegerValidation() {

		return integerValidation;
	}

	public void setIntegerValidation(IntegerValidation integerValidation) {

		this.integerValidation = integerValidation;
	}

	public boolean isMultiLine() {

		return isMultiLine;
	}

	public void setMultiLine(boolean isMultiLine) {

		this.isMultiLine = isMultiLine;
	}
}
