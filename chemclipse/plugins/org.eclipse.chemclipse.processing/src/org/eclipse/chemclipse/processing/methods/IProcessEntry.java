/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - make unmodifiable except the Settings
 *******************************************************************************/
package org.eclipse.chemclipse.processing.methods;

import java.util.Set;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;

public interface IProcessEntry extends ProcessEntryContainer {

	/**
	 * 
	 * @return the {@link IProcessSupplier} id this {@link IProcessEntry} referees to
	 */
	String getProcessorId();

	/**
	 * 
	 * @return the name of this {@link IProcessEntry}, most likely the name of the {@link IProcessSupplier}
	 */
	@Override
	String getName();

	/**
	 * 
	 * @return the description of this {@link IProcessEntry}, most likely the description of the {@link IProcessSupplier}
	 */
	@Override
	String getDescription();

	/**
	 * 
	 * @return the current settings of the {@link IProcessEntry} might be <code>null</code>
	 */
	String getSettings();

	/**
	 * 
	 * @return the {@link DataCategory}s this {@link IProcessEntry} applies to, most likely equals to the {@link DataCategory}s of the {@link IProcessSupplier}
	 */
	Set<DataCategory> getDataCategories();

	/**
	 * Set the settings for this entry
	 * 
	 * @param settings
	 * @throws IllegalArgumentException
	 *             if the entry is readonly
	 */
	void setSettings(String settings) throws IllegalArgumentException;

	boolean isReadOnly();

	ProcessEntryContainer getParent();

	default <T> ProcessorPreferences<T> getPreferences(ProcessSupplierContext context) {

		return getPreferences(context.getSupplier(getProcessorId()));
	}

	default <T> ProcessorPreferences<T> getPreferences(IProcessSupplier<T> supplier) {

		if(supplier == null) {
			return null;
		}
		return new ProcessEntryProcessorPreferences<>(supplier, this);
	}

	/**
	 * Compares this entry content to the other entries content, the default implementation compares {@link #getName()}, {@link #getDescription()}, {@link #getSettings()}, {@link #isReadOnly()} {@link #getProcessorId()},
	 * this method is different to {@link #equals(Object)} that it does compares for user visible properties to be equal in contrast to objects identity and it allows to compare differnt instance type, this also means that it is not required that
	 * Object1.contentEquals(Object2} == Object2.contentEquals(Object1}
	 * 
	 * @param other
	 * @return
	 */
	default boolean contentEquals(IProcessEntry other) {

		if(other == null) {
			return false;
		}
		if(other == this) {
			return true;
		}
		if(isReadOnly() != other.isReadOnly()) {
			return false;
		}
		if(!getName().equals(other.getName())) {
			return false;
		}
		if(!getDescription().equals(other.getDescription())) {
			return false;
		}
		String settings = getSettings();
		if(settings == null) {
			settings = "";
		}
		String otherSettings = other.getSettings();
		if(otherSettings == null) {
			otherSettings = "";
		}
		if(!otherSettings.equals(settings)) {
			return false;
		}
		if(!getProcessorId().equals(other.getProcessorId())) {
			return false;
		}
		return entriesEquals(other);
	}

	public static ProcessSupplierContext getContext(IProcessEntry entry, ProcessSupplierContext defaultContext) {

		ProcessEntryContainer container = entry.getParent();
		if(container instanceof IProcessEntry) {
			IProcessEntry parent = (IProcessEntry)container;
			IProcessSupplier<?> supplier = getContext(parent, defaultContext).getSupplier(parent.getProcessorId());
			if(supplier instanceof ProcessSupplierContext) {
				return (ProcessSupplierContext)supplier;
			}
		}
		return defaultContext;
	}
}