/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.internal.filter.BatchProcessJobEventFilter;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.internal.support.JobTags;
import org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model.BatchProcessJob;
import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.converter.model.ChromatogramInputEntry;
import org.eclipse.chemclipse.converter.model.IChromatogramInputEntry;
import org.eclipse.chemclipse.model.methods.IProcessEntry;
import org.eclipse.chemclipse.model.methods.ProcessEntry;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.core.runtime.IProgressMonitor;

public class JobReader {

	public BatchProcessJob read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		BatchProcessJob batchProcessJob = new BatchProcessJob();
		try {
			readHeader(file, batchProcessJob);
			readChromatogramInputEntries(file, batchProcessJob, monitor);
			readChromatogramProcessEntries(file, batchProcessJob, monitor);
		} catch(XMLStreamException e) {
			throw new IOException(e);
		}
		return batchProcessJob;
	}

	/**
	 * Reads the header information.
	 * 
	 * @throws IOException
	 */
	private void readHeader(File file, BatchProcessJob batchProcessJob) throws XMLStreamException, IOException {

		/*
		 * Open the streams.
		 */
		XMLEventReader eventReader;
		XMLInputFactory.newInstance().setProperty(XMLInputFactory.IS_COALESCING, true);
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		eventReader = inputFactory.createXMLEventReader(bufferedInputStream, JobTags.UTF8);
		XMLEvent event;
		String elementName;
		exitloop:
		while(eventReader.hasNext()) {
			event = eventReader.nextEvent();
			if(event.isStartElement()) {
				elementName = event.asStartElement().getName().getLocalPart();
				/*
				 * Read the header elements. There are none to read actually.
				 */
			} else {
				/*
				 * Check if it is the end of the header. If yes, break to not
				 * run through all elements of the xml document.
				 */
				if(event.isEndElement()) {
					elementName = event.asEndElement().getName().getLocalPart();
					if(elementName.equals(JobTags.HEADER)) {
						break exitloop;
					}
				}
			}
		}
		/*
		 * Close the streams.
		 */
		eventReader.close();
		bufferedInputStream.close();
	}

	/**
	 * Read the chromatogram input entries.
	 * 
	 * @param file
	 * @param batchProcessJob
	 * @param monitor
	 * @throws XMLStreamException
	 * @throws IOException
	 */
	private void readChromatogramInputEntries(File file, BatchProcessJob batchProcessJob, IProgressMonitor monitor) throws XMLStreamException, IOException {

		IChromatogramInputEntry inputEntry;
		XMLEvent event;
		/*
		 * Open the streams.
		 */
		XMLEventReader eventReader;
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		eventReader = inputFactory.createXMLEventReader(bufferedInputStream, JobTags.UTF8);
		/*
		 * Use event filters.
		 */
		List<String> acceptedElements = new ArrayList<String>();
		acceptedElements.add(JobTags.CHROMATOGRAM_INPUT_ENTRY);
		EventFilter eventFilter = new BatchProcessJobEventFilter(acceptedElements);
		XMLEventReader filteredEventReader = inputFactory.createFilteredReader(eventReader, eventFilter);
		/*
		 * Read all entries.
		 */
		while(filteredEventReader.hasNext()) {
			event = filteredEventReader.nextEvent();
			event = eventReader.nextEvent();
			inputEntry = new ChromatogramInputEntry(event.asCharacters().getData());
			batchProcessJob.getChromatogramInputEntries().add(inputEntry);
		}
		/*
		 * Close the streams.
		 */
		eventReader.close();
		bufferedInputStream.close();
	}

	@SuppressWarnings("unchecked")
	private void readChromatogramProcessEntries(File file, BatchProcessJob batchProcessJob, IProgressMonitor monitor) throws XMLStreamException, IOException {

		IProcessEntry processEntry;
		XMLEvent event;
		Attribute attribute;
		String attributeName;
		//
		String id = "";
		String name = "";
		String description = "";
		String jsonSettings = IProcessEntry.EMPTY_JSON_SETTINGS;
		String dataTypes = "";
		/*
		 * Open the streams.
		 */
		XMLEventReader eventReader;
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
		eventReader = inputFactory.createXMLEventReader(bufferedInputStream, JobTags.UTF8);
		/*
		 * Use event filters.
		 */
		List<String> acceptedElements = new ArrayList<String>();
		acceptedElements.add(JobTags.CHROMATOGRAM_PROCESS_ENTRY);
		EventFilter eventFilter = new BatchProcessJobEventFilter(acceptedElements);
		XMLEventReader filteredEventReader = inputFactory.createFilteredReader(eventReader, eventFilter);
		/*
		 * Read all entries.
		 */
		while(filteredEventReader.hasNext()) {
			event = filteredEventReader.nextEvent();
			Iterator<? extends Attribute> attributes = event.asStartElement().getAttributes();
			while(attributes.hasNext()) {
				attribute = attributes.next();
				attributeName = attribute.getName().getLocalPart();
				//
				if(attributeName.equals(JobTags.PROCESSOR_ID)) {
					id = attribute.getValue();
				} else if(attributeName.equals(JobTags.PROCESSOR_NAME)) {
					name = attribute.getValue();
				} else if(attributeName.equals(JobTags.PROCESSOR_DESCRIPTION)) {
					description = attribute.getValue();
				} else if(attributeName.equals(JobTags.PROCESSOR_JSON_SETTINGS)) {
					jsonSettings = attribute.getValue();
				} else if(attributeName.equals(JobTags.PROCESSOR_DATA_TYPES)) {
					dataTypes = attribute.getValue();
				}
			}
			/*
			 * Create
			 */
			processEntry = new ProcessEntry();
			processEntry.setProcessorId(id);
			processEntry.setName(name);
			processEntry.setDescription(description);
			processEntry.setJsonSettings(jsonSettings);
			String[] types = dataTypes.split(JobTags.DELIMITER_DATA_TYPE);
			for(String type : types) {
				DataType dataType = DataType.valueOf(type.trim());
				if(dataType != null) {
					processEntry.getSupportedDataTypes().add(dataType);
				}
			}
			//
			batchProcessJob.getProcessMethod().add(processEntry);
		}
		/*
		 * Close the streams.
		 */
		eventReader.close();
		bufferedInputStream.close();
	}
}
