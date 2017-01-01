/*******************************************************************************
 * Copyright (c) 2014, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.animl.io;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.msd.converter.io.AbstractChromatogramMSDWriter;
import org.eclipse.chemclipse.msd.converter.supplier.animl.internal.converter.IChromatogramTags;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;

public class ChromatogramWriter extends AbstractChromatogramMSDWriter {

	@Override
	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotWriteableException, IOException {

		XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
		try {
			XMLEventWriter eventWriter = xmlOutputFactory.createXMLEventWriter(bufferedOutputStream, "UTF-8");
			XMLEventFactory eventFactory = XMLEventFactory.newInstance();
			/*
			 * Document
			 */
			eventWriter.add(eventFactory.createStartDocument());
			eventWriter.add(eventFactory.createComment("Document generated by OpenChrom - the open source software for chromatography/mass spectrometry"));
			eventWriter.add(eventFactory.createStartElement("", "", IChromatogramTags.ANIML));
			eventWriter.add(eventFactory.createAttribute(IChromatogramTags.ATTRIBUTE_XMLNS, "http://www.w3.org/2000/09/xmldsig#"));
			eventWriter.add(eventFactory.createAttribute(IChromatogramTags.ATTRIBUTE_XMLNS_XSI, "http://www.w3.org/2001/XMLSchema-instance"));
			eventWriter.add(eventFactory.createAttribute(IChromatogramTags.ATTRIBUTE_XSI_SCHEMA_LOCATION, "http://animl.cvs.sourceforge.net/viewvc/animl/schema/animl-core.xsd"));
			eventWriter.add(eventFactory.createAttribute(IChromatogramTags.ATTRIBUTE_VERSION, "1.0"));
			//
			writeSampleSet(eventWriter, eventFactory, chromatogram, monitor);
			writeMeasurementData(eventWriter, eventFactory, chromatogram, monitor);
			writeAuditTrailEntrySet(eventWriter, eventFactory, chromatogram, monitor);
			writeSignatureSet(eventWriter, eventFactory, chromatogram, monitor);
			//
			eventWriter.add(eventFactory.createEndElement("", "", IChromatogramTags.ANIML));
			eventWriter.add(eventFactory.createEndDocument());
			/*
			 * Close the streams
			 */
			bufferedOutputStream.flush();
			eventWriter.flush();
			bufferedOutputStream.close();
			eventWriter.close();
		} catch(XMLStreamException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Sample Set
	 * 
	 * @param eventWriter
	 * @param eventFactory
	 * @throws XMLStreamException
	 */
	private void writeSampleSet(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws XMLStreamException {

		eventWriter.add(eventFactory.createComment("SampleSet is defined in animl-core.xsd"));
		eventWriter.add(eventFactory.createStartElement("", "", IChromatogramTags.ELEMENT_SAMPLE_SET));
		//
		eventWriter.add(eventFactory.createStartElement("", "", IChromatogramTags.ELEMENT_SAMPLE));
		eventWriter.add(eventFactory.createAttribute(IChromatogramTags.ATTRIBUTE_SAMPLE_NAME, chromatogram.getName()));
		eventWriter.add(eventFactory.createAttribute(IChromatogramTags.ATTRIBUTE_SAMPLE_ID, chromatogram.getName()));
		eventWriter.add(eventFactory.createEndElement("", "", IChromatogramTags.ELEMENT_SAMPLE));
		eventWriter.add(eventFactory.createComment("You can describe one or more samples here"));
		//
		eventWriter.add(eventFactory.createEndElement("", "", IChromatogramTags.ELEMENT_SAMPLE_SET));
	}

	/**
	 * Measurement Data
	 * 
	 * @param eventWriter
	 * @param eventFactory
	 * @throws XMLStreamException
	 */
	private void writeMeasurementData(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws XMLStreamException {

		eventWriter.add(eventFactory.createComment("MeasurementData is defined in animl-core.xsd"));
		eventWriter.add(eventFactory.createStartElement("", "", IChromatogramTags.ELEMENT_MEASUREMENT_DATA));
		eventWriter.add(eventFactory.createStartElement("", "", IChromatogramTags.ELEMENT_EXPERIMENT_STEP));
		eventWriter.add(eventFactory.createAttribute(IChromatogramTags.ATTRIBUTE_EXPERIMENT_NAME, "MSD_Measurement"));
		//
		eventWriter.add(eventFactory.createStartElement("", "", IChromatogramTags.ELEMENT_TIMESTAMP));
		eventWriter.add(eventFactory.createCData(chromatogram.getDate().toString()));
		eventWriter.add(eventFactory.createEndElement("", "", IChromatogramTags.ELEMENT_TIMESTAMP));
		//
		eventWriter.add(eventFactory.createStartElement("", "", IChromatogramTags.ELEMENT_PAGE_SET));
		eventWriter.add(eventFactory.createStartElement("", "", IChromatogramTags.ELEMENT_PAGE));
		eventWriter.add(eventFactory.createAttribute(IChromatogramTags.ATTRIBUTE_PAGE_NAME, "Scans MSD"));
		//
		eventWriter.add(eventFactory.createStartElement("", "", IChromatogramTags.ELEMENT_VECTOR_SET));
		eventWriter.add(eventFactory.createAttribute(IChromatogramTags.ATTRIBUTE_PAGE_LENGTH, Integer.toString(chromatogram.getNumberOfScans())));
		/*
		 * Write the scans.
		 */
		int scanNumber = 1;
		for(IScan scan : chromatogram.getScans()) {
			//
			monitor.subTask("Export Scan: " + scanNumber);
			if(scan instanceof IScanMSD) {
				/*
				 * Scan
				 */
				IScanMSD massSpectrum = (IScanMSD)scan;
				eventWriter.add(eventFactory.createStartElement("", "", IChromatogramTags.ELEMENT_VECTOR));
				eventWriter.add(eventFactory.createAttribute(IChromatogramTags.ATTRIBUTE_VECTOR_NAME, "Scan " + scanNumber));
				eventWriter.add(eventFactory.createAttribute(IChromatogramTags.ATTRIBUTE_VECTOR_RETENTION_TIME, Double.toString((massSpectrum.getRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR))));
				eventWriter.add(eventFactory.createAttribute(IChromatogramTags.ATTRIBUTE_VECTOR_RETENTION_INDEX, Float.toString(massSpectrum.getRetentionIndex())));
				/*
				 * Ions
				 */
				for(IIon ion : massSpectrum.getIons()) {
					//
					eventWriter.add(eventFactory.createStartElement("", "", IChromatogramTags.ELEMENT_ION));
					eventWriter.add(eventFactory.createAttribute(IChromatogramTags.ATTRIBUTE_ION_MZ, Double.toString(ion.getIon())));
					eventWriter.add(eventFactory.createAttribute(IChromatogramTags.ATTRIBUTE_ION_INTENSITY, Float.toString(ion.getAbundance())));
					eventWriter.add(eventFactory.createEndElement("", "", IChromatogramTags.ELEMENT_ION));
				}
				//
				eventWriter.add(eventFactory.createEndElement("", "", IChromatogramTags.ELEMENT_VECTOR));
			}
			scanNumber++;
		}
		//
		eventWriter.add(eventFactory.createEndElement("", "", IChromatogramTags.ELEMENT_VECTOR_SET));
		eventWriter.add(eventFactory.createEndElement("", "", IChromatogramTags.ELEMENT_PAGE));
		eventWriter.add(eventFactory.createEndElement("", "", IChromatogramTags.ELEMENT_PAGE_SET));
		//
		eventWriter.add(eventFactory.createEndElement("", "", IChromatogramTags.ELEMENT_EXPERIMENT_STEP));
		eventWriter.add(eventFactory.createEndElement("", "", IChromatogramTags.ELEMENT_MEASUREMENT_DATA));
	}

	/**
	 * Audit Trail Entry Set
	 * 
	 * @param eventWriter
	 * @param eventFactory
	 * @throws XMLStreamException
	 */
	private void writeAuditTrailEntrySet(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws XMLStreamException {

		eventWriter.add(eventFactory.createComment("AuditTrail is defined in animl-core.xsd"));
		eventWriter.add(eventFactory.createStartElement("", "", IChromatogramTags.ELEMENT_AUDIT_TRAIL_ENTRY_SET));
		//
		eventWriter.add(eventFactory.createStartElement("", "", IChromatogramTags.ELEMENT_AUDIT_TRAIL_ENTRY));
		eventWriter.add(eventFactory.createEndElement("", "", IChromatogramTags.ELEMENT_AUDIT_TRAIL_ENTRY));
		eventWriter.add(eventFactory.createComment("Over time the audit trail will grow when/if you change the file"));
		//
		eventWriter.add(eventFactory.createEndElement("", "", IChromatogramTags.ELEMENT_AUDIT_TRAIL_ENTRY_SET));
	}

	/**
	 * Signature Set
	 * 
	 * @param eventWriter
	 * @param eventFactory
	 * @throws XMLStreamException
	 */
	private void writeSignatureSet(XMLEventWriter eventWriter, XMLEventFactory eventFactory, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws XMLStreamException {

		eventWriter.add(eventFactory.createComment("SignatureSet is defined in xmldsig-core-schema.xsd via animl-core.xsd"));
		eventWriter.add(eventFactory.createStartElement("", "", IChromatogramTags.ELEMENT_SIGNATURE_SET));
		//
		eventWriter.add(eventFactory.createStartElement("", "", IChromatogramTags.ELEMENT_SIGNATURE));
		eventWriter.add(eventFactory.createEndElement("", "", IChromatogramTags.ELEMENT_SIGNATURE));
		eventWriter.add(eventFactory.createComment("You can sign all or part of a document"));
		//
		eventWriter.add(eventFactory.createEndElement("", "", IChromatogramTags.ELEMENT_SIGNATURE_SET));
	}
}
