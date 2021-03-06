/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add equals method, add copy constructor, extend for new methods
 *******************************************************************************/
package org.eclipse.chemclipse.model.methods;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;

public class ProcessMethod extends ListProcessEntryContainer implements IProcessMethod {

	public static final Set<DataCategory> CHROMATOGRAPHY = Collections.unmodifiableSet(EnumSet.copyOf(Arrays.asList(DataCategory.MSD, DataCategory.CSD, DataCategory.WSD)));
	public static final Set<DataCategory> NMR = Collections.unmodifiableSet(EnumSet.copyOf(Arrays.asList(DataCategory.FID, DataCategory.NMR)));
	private String UUID = java.util.UUID.randomUUID().toString();
	private String operator;
	private String category;
	private File sourceFile;
	private final Set<DataCategory> catgories;
	private final Map<String, String> metadata = new LinkedHashMap<>();

	/**
	 * @deprecated specify explicitly the desired categories
	 */
	@Deprecated
	public ProcessMethod() {
		this(CHROMATOGRAPHY);
	}

	public ProcessMethod(Set<DataCategory> categories) {
		this.catgories = Collections.unmodifiableSet(categories);
	}

	/**
	 * Copies all data from other into a new process method. This does <b>NOT</b> copies the UUID but generates a new one!
	 * 
	 * @param other
	 */
	public ProcessMethod(IProcessMethod other) {
		super(other);
		if(other != null) {
			this.operator = other.getOperator();
			setDescription(other.getDescription());
			this.category = other.getCategory();
			setName(other.getName());
			if(other instanceof ProcessMethod) {
				this.sourceFile = ((ProcessMethod)other).sourceFile;
			}
			metadata.putAll(other.getMetaData());
			this.catgories = Collections.unmodifiableSet(new HashSet<>(other.getDataCategories()));
			// read-only must be set at the end!
			setReadOnly(other.isFinal());
		} else {
			this.catgories = Collections.emptySet();
		}
	}

	@Override
	public String getOperator() {

		if(operator == null) {
			return "";
		}
		return operator;
	}

	public void setOperator(String operator) {

		this.operator = operator;
	}

	@Override
	public String getName() {

		String name = super.getName();
		if(name == null || name.isEmpty()) {
			if(sourceFile != null) {
				return sourceFile.getName();
			}
			return "";
		}
		return name;
	}

	@Override
	public String getCategory() {

		if(category == null) {
			return "";
		}
		return category;
	}

	public void setCategory(String category) throws IllegalStateException {

		this.category = category;
	}

	public void setSourceFile(File sourceFile) {

		this.sourceFile = sourceFile;
	}

	@Override
	public File getSourceFile() {

		return sourceFile;
	}

	@Override
	public String getUUID() {

		return UUID;
	}

	public void setUUID(String UUID) {

		this.UUID = UUID;
	}

	@Override
	public boolean isFinal() {

		return isReadOnly();
	}

	@Override
	public Set<DataCategory> getDataCategories() {

		return catgories;
	}

	@Override
	public Map<String, String> getMetaData() {

		if(isFinal()) {
			return Collections.unmodifiableMap(metadata);
		}
		return metadata;
	}
}
