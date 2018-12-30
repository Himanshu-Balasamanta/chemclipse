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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

public class MethodImportRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(MethodImportRunnable.class);
	//
	private File file;
	private ProcessMethod processMethod = null;

	public MethodImportRunnable(File file) {
		this.file = file;
	}

	public ProcessMethod getProcessMethod() {

		return processMethod;
	}

	@Override
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask("Import Process Method", IProgressMonitor.UNKNOWN);
			IProcessingInfo processingInfo = MethodConverter.convert(file, monitor);
			processMethod = processingInfo.getProcessingResult(ProcessMethod.class);
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		} finally {
			monitor.done();
		}
	}
}
