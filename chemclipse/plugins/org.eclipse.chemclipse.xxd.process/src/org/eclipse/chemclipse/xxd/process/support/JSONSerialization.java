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
package org.eclipse.chemclipse.xxd.process.support;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.chemclipse.model.methods.IProcessEntry;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.model.methods.ProcessEntry;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIons;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIon;
import org.eclipse.chemclipse.msd.model.core.support.MarkedIons;
import org.eclipse.chemclipse.support.settings.parser.InputValue;
import org.eclipse.chemclipse.support.settings.serialization.SettingsSerialization;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JSONSerialization implements SettingsSerialization {

	@Override
	public Map<InputValue, Object> fromString(Collection<? extends InputValue> inputValues, String content) throws IOException {

		LinkedHashMap<InputValue, Object> result = new LinkedHashMap<>();
		for(InputValue inputValue : inputValues) {
			result.put(inputValue, inputValue.getDefaultValue());
		}
		if(content != null) {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = createMapper().readValue(content, HashMap.class);
			for(Map.Entry<String, Object> entry : map.entrySet()) {
				for(InputValue inputValue : inputValues) {
					if(inputValue.getName().equals(entry.getKey())) {
						Object value = entry.getValue();
						if(value != null) {
							result.put(inputValue, value);
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public String toString(Map<InputValue, Object> values) throws IOException {

		Map<String, Object> result = new HashMap<>();
		for(Entry<InputValue, Object> entry : values.entrySet()) {
			result.put(entry.getKey().getName(), entry.getValue());
		}
		ObjectMapper mapper = createMapper();
		return mapper.writeValueAsString(result);
	}

	private ObjectMapper createMapper() {

		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule("ChemClipse", Version.unknownVersion());
		SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
		resolver.addMapping(IMarkedIons.class, MarkedIons.class);
		resolver.addMapping(IMarkedIon.class, MarkedIon.class);
		resolver.addMapping(IProcessMethod.class, ProcessMethod.class);
		resolver.addMapping(IProcessEntry.class, ProcessEntry.class);
		module.setAbstractTypes(resolver);
		mapper.registerModule(module);
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return mapper;
	}

	@Override
	public <Settings> Settings fromString(Class<Settings> settingsClass, String content) throws IOException {

		if(content == null) {
			return null;
		}
		return createMapper().readValue(content, settingsClass);
	}

	@Override
	public String toString(Object settingsObject) throws IOException {

		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(settingsObject);
	}
}
