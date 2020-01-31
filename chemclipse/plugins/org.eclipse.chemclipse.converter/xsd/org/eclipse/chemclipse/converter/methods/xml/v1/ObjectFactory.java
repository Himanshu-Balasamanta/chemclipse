/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * SPDX-License-Identifier: EPL-1.0
 * 
 * Contributors:
 * generated by xjc compiler
 *******************************************************************************/
package org.eclipse.chemclipse.converter.methods.xml.v1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the org.eclipse.chemclipse.converter.methods.xml.v1 package.
 * <p>
 * An ObjectFactory allows you to programmatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups. Factory methods for each of these are
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

	private final static QName _ProcessMethod_QNAME = new QName("https://github.com/eclipse/chemclipse/processmethods/v1", "ProcessMethod");

	/**
	 * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.eclipse.chemclipse.converter.methods.xml.v1
	 * 
	 */
	public ObjectFactory() {
	}

	/**
	 * Create an instance of {@link Method }
	 * 
	 */
	public Method createMethod() {

		return new Method();
	}

	/**
	 * Create an instance of {@link Entry }
	 * 
	 */
	public Entry createEntry() {

		return new Entry();
	}

	/**
	 * Create an instance of {@link MetaData }
	 * 
	 */
	public MetaData createMetaData() {

		return new MetaData();
	}

	/**
	 * Create an instance of {@link JAXBElement }{@code <}{@link Method }{@code >}}
	 * 
	 */
	@XmlElementDecl(namespace = "https://github.com/eclipse/chemclipse/processmethods/v1", name = "ProcessMethod")
	public JAXBElement<Method> createProcessMethod(Method value) {

		return new JAXBElement<Method>(_ProcessMethod_QNAME, Method.class, null, value);
	}
}