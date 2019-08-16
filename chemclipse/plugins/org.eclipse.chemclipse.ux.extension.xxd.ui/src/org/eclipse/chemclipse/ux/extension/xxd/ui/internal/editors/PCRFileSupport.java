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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.editors;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.converter.scan.IScanConverterSupport;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.pcr.converter.core.PlateConverterPCR;
import org.eclipse.chemclipse.pcr.model.core.IPlate;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.runnables.PCRExportRunnable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class PCRFileSupport {

	private static final Logger logger = Logger.getLogger(PCRFileSupport.class);

	private PCRFileSupport() {
	}

	public static boolean savePlate(Shell shell, IPlate plate) throws NoConverterAvailableException {

		if(plate == null || shell == null) {
			return false;
		}
		//
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		/*
		 * Create the dialog.
		 */
		dialog.setFilterPath(Activator.getDefault().getSettingsPath());
		dialog.setFileName(plate.getName());
		dialog.setText("Save Plate As...");
		dialog.setOverwrite(true);
		//
		IScanConverterSupport converterSupport = PlateConverterPCR.getScanConverterSupport();
		if(converterSupport != null) {
			String[] filterExtensions = converterSupport.getExportableFilterExtensions();
			dialog.setFilterExtensions(filterExtensions);
			String[] filterNames = converterSupport.getExportableFilterNames();
			dialog.setFilterNames(filterNames);
			/*
			 * Opens the dialog.<br/> Use converterSupport.getExportSupplier()
			 * instead of converterSupport.getSupplier() otherwise a wrong supplier
			 * will be taken.
			 */
			String filename = dialog.open();
			if(filename != null) {
				validateFile(dialog, converterSupport.getExportSupplier(), shell, converterSupport, plate);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static void writeFile(Shell shell, File file, IPlate plate, ISupplier supplier) {

		if(file == null || plate == null || supplier == null) {
			return;
		}
		//
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		PCRExportRunnable runnable = new PCRExportRunnable(file, plate, supplier);
		try {
			dialog.run(true, false, runnable);
		} catch(InvocationTargetException e) {
			logger.warn(e);
		} catch(InterruptedException e) {
			logger.warn(e);
		}
		//
		File data = runnable.getData();
		if(data == null) {
			MessageDialog.openInformation(shell, "Save PCR", "There is no suitable plate converter available.");
		}
	}

	private static void validateFile(FileDialog dialog, List<ISupplier> supplier, Shell shell, IScanConverterSupport converterSupport, IPlate plate) {

		File plateFolder = null;
		boolean overwrite = dialog.getOverwrite();
		boolean folderExists = false;
		boolean isDirectory = false;
		/*
		 * Check if the selected supplier exists.<br/> If some super brain tries
		 * to edit the suppliers list.
		 */
		ISupplier selectedSupplier = supplier.get(dialog.getFilterIndex());
		if(selectedSupplier == null) {
			MessageDialog.openInformation(shell, "PCR Converter", "The requested plate converter does not exists.");
			return;
		}
		/*
		 * Get the file or directory name.
		 */
		String filename = dialog.getFilterPath() + File.separator + dialog.getFileName();
		if(selectedSupplier != null) {
			/*
			 * If the data file is stored in a directory create an
			 * appropriate directory.
			 */
			String directoryExtension = selectedSupplier.getDirectoryExtension();
			if(directoryExtension != "") {
				isDirectory = true;
				/*
				 * Remove a possible directory extension.
				 */
				filename = removeFileExtensions(filename, selectedSupplier);
				filename = filename.concat(selectedSupplier.getDirectoryExtension());
				/*
				 * Check if the folder still exists.
				 */
				plateFolder = new File(filename);
				if(plateFolder.exists()) {
					folderExists = true;
					if(MessageDialog.openQuestion(shell, "Overwrite", "Would you like to overwrite the plate " + plateFolder.toString() + "?")) {
						overwrite = true;
					} else {
						overwrite = false;
					}
				}
				/*
				 * Checks if the data shall be overwritten.
				 */
				if(overwrite) {
					if(!folderExists) {
						plateFolder.mkdir();
					}
				}
			} else {
				/*
				 * Remove a possible file extension.
				 */
				filename = removeFileExtensions(filename, selectedSupplier);
				filename = filename.concat(selectedSupplier.getFileExtension());
				//
				String filenameDialog = dialog.getFilterPath() + File.separator + dialog.getFileName();
				if(!filename.equals(filenameDialog)) {
					/*
					 * The file name has been modified. Ask for override if it
					 * still exists.
					 */
					File dataFile = new File(filename);
					if(dataFile.exists()) {
						if(MessageDialog.openQuestion(shell, "Overwrite", "Would you like to overwrite the plate " + dataFile.toString() + "?")) {
							overwrite = true;
						} else {
							overwrite = false;
						}
					}
				}
			}
			/*
			 * Write the data and check if the folder exists.
			 */
			if(overwrite) {
				/*
				 * Check the directory and file name and correct them if
				 * necessary.
				 */
				if(isDirectory) {
					if(!folderExists) {
						if(plateFolder != null) {
							plateFolder.mkdir();
						}
					}
				} else {
					String fileExtension = selectedSupplier.getFileExtension();
					if(!filename.endsWith(fileExtension)) {
						filename = filename + fileExtension;
					}
				}
				/*
				 * Export the plate.
				 */
				writeFile(shell, new File(filename), plate, selectedSupplier);
			}
		}
	}

	private static String removeFileExtensions(String filePath, ISupplier supplier) {

		int start = 0;
		int stop = 0;
		//
		if(supplier.getDirectoryExtension().equals("")) {
			/*
			 * Remove the file extension.
			 */
			String fileExtension = supplier.getFileExtension();
			if(filePath.endsWith(fileExtension) || filePath.endsWith(fileExtension.toLowerCase()) || filePath.endsWith(fileExtension.toUpperCase())) {
				stop = filePath.length() - fileExtension.length();
				filePath = filePath.substring(start, stop);
			}
		} else {
			/*
			 * Remove the directory extension.
			 */
			String directoryExtension = supplier.getDirectoryExtension();
			if(filePath.endsWith(directoryExtension) || filePath.endsWith(directoryExtension.toLowerCase()) || filePath.endsWith(directoryExtension.toUpperCase())) {
				stop = filePath.length() - directoryExtension.length();
				filePath = filePath.substring(start, stop);
			}
		}
		return filePath;
	}
}