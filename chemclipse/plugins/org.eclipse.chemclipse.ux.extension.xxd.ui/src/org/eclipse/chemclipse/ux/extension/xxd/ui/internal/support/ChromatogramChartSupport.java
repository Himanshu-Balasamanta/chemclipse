/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.model.baseline.IBaselineModel;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.signals.ITotalScanSignal;
import org.eclipse.chemclipse.model.signals.ITotalScanSignalExtractor;
import org.eclipse.chemclipse.model.signals.ITotalScanSignals;
import org.eclipse.chemclipse.model.signals.TotalScanSignalExtractor;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignal;
import org.eclipse.chemclipse.wsd.model.xwc.IExtractedWavelengthSignals;
import org.eclipse.eavp.service.swtchart.core.ISeriesData;
import org.eclipse.eavp.service.swtchart.core.SeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesData;
import org.eclipse.eavp.service.swtchart.linecharts.ILineSeriesSettings;
import org.eclipse.eavp.service.swtchart.linecharts.LineSeriesData;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Color;
import org.swtchart.LineStyle;

public class ChromatogramChartSupport {

	/*
	 * Overlay Type
	 */
	public static final String DISPLAY_TYPE_TIC = "TIC"; // Total Intensity Chromatogram
	public static final String DISPLAY_TYPE_BPC = "BPC"; // Base Peak Chromatogram
	public static final String DISPLAY_TYPE_XIC = "XIC"; // Extracted Ion Chromatogram
	public static final String DISPLAY_TYPE_SIC = "SIC"; // Selected Ion Chromatogram
	public static final String DISPLAY_TYPE_TSC = "TSC"; // Total Substracted Chromatogram
	public static final String DISPLAY_TYPE_SRM = "SRM"; // Single Reaction Monitoring
	public static final String DISPLAY_TYPE_MRM = "MRM"; // Single Reaction Monitoring
	public static final String DISPLAY_TYPE_SWC = "SWC"; // Selected Wavelength Chromatogram
	//
	public static final String DERIVATIVE_NONE = "--";
	public static final String DERIVATIVE_FIRST = "1st";
	public static final String DERIVATIVE_SECOND = "2nd";
	public static final String DERIVATIVE_THIRD = "3rd";
	//
	private IColorScheme colorSchemeNormal;
	private Map<String, Color> usedColorsNormal;
	private IColorScheme colorSchemeSIC;
	private Map<String, Color> usedColorsSIC;
	private IColorScheme colorSchemeSWC;
	private Map<String, Color> usedColorsSWC;
	//
	private LineStyle lineStyleTIC;
	private LineStyle lineStyleBPC;
	private LineStyle lineStyleXIC;
	private LineStyle lineStyleSIC;
	private LineStyle lineStyleTSC;
	private LineStyle lineStyleSWC;
	private LineStyle lineStyleDefault;

	public ChromatogramChartSupport() {
		usedColorsNormal = new HashMap<String, Color>();
		usedColorsSIC = new HashMap<String, Color>();
		usedColorsSWC = new HashMap<String, Color>();
		loadUserSettings();
	}

	public void loadUserSettings() {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		//
		colorSchemeNormal = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_NORMAL));
		colorSchemeSIC = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_SIC));
		colorSchemeSWC = Colors.getColorScheme(preferenceStore.getString(PreferenceConstants.P_COLOR_SCHEME_DISPLAY_SWC));
		lineStyleTIC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_DISPLAY_TIC));
		lineStyleBPC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_DISPLAY_BPC));
		lineStyleXIC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_DISPLAY_XIC));
		lineStyleSIC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_DISPLAY_SIC));
		lineStyleTSC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_DISPLAY_TSC));
		lineStyleSWC = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_DISPLAY_SWC));
		lineStyleDefault = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_LINE_STYLE_DISPLAY_DEFAULT));
		//
		resetColorMaps();
	}

	public Color getSeriesColor(String seriesId, String overlayType) {

		Color color;
		if(DISPLAY_TYPE_SIC.equals(overlayType)) {
			/*
			 * SIC
			 */
			color = usedColorsSIC.get(seriesId);
			if(color == null) {
				color = colorSchemeSIC.getColor();
				colorSchemeSIC.incrementColor();
				usedColorsSIC.put(seriesId, color);
			}
		} else if(DISPLAY_TYPE_SWC.equals(overlayType)) {
			/*
			 * SIC
			 */
			color = usedColorsSWC.get(seriesId);
			if(color == null) {
				color = colorSchemeSWC.getColor();
				colorSchemeSWC.incrementColor();
				usedColorsSWC.put(seriesId, color);
			}
		} else {
			/*
			 * Normal
			 */
			color = usedColorsNormal.get(seriesId);
			if(color == null) {
				color = colorSchemeNormal.getColor();
				colorSchemeNormal.incrementColor();
				usedColorsNormal.put(seriesId, color);
			}
		}
		return color;
	}

	public ILineSeriesData getLineSeriesDataChromatogram(IChromatogramSelection chromatogramSelection, String seriesId, Color color) {

		String overlayType = DISPLAY_TYPE_TIC;
		String derivativeType = DERIVATIVE_NONE;
		return getLineSeriesData(chromatogramSelection, seriesId, overlayType, derivativeType, color, null, false);
	}

	@SuppressWarnings("rawtypes")
	public ILineSeriesData getLineSeriesDataChromatogram(IChromatogram chromatogram, String seriesId, Color color) {

		String overlayType = DISPLAY_TYPE_TIC;
		String derivativeType = DERIVATIVE_NONE;
		return getLineSeriesData(chromatogram, seriesId, overlayType, derivativeType, color, null, false);
	}

	public ILineSeriesData getLineSeriesDataBaseline(IChromatogramSelection chromatogramSelection, String seriesId, Color color) {

		String overlayType = DISPLAY_TYPE_TIC;
		String derivativeType = DERIVATIVE_NONE;
		return getLineSeriesData(chromatogramSelection, seriesId, overlayType, derivativeType, color, null, true);
	}

	@SuppressWarnings("rawtypes")
	public ILineSeriesData getLineSeriesDataBaseline(IChromatogram chromatogram, String seriesId, Color color) {

		String overlayType = DISPLAY_TYPE_TIC;
		String derivativeType = DERIVATIVE_NONE;
		return getLineSeriesData(chromatogram, seriesId, overlayType, derivativeType, color, null, true);
	}

	@SuppressWarnings("rawtypes")
	public ILineSeriesData getLineSeriesData(IChromatogramSelection chromatogramSelection, String seriesId, String overlayType, String derivativeType, Color color, List<Integer> ions, boolean baseline) {

		IChromatogram chromatogram = chromatogramSelection.getChromatogram();
		int startScan = chromatogram.getScanNumber(chromatogramSelection.getStartRetentionTime());
		int stopScan = chromatogram.getScanNumber(chromatogramSelection.getStopRetentionTime());
		return getLineSeriesData(chromatogram, startScan, stopScan, seriesId, overlayType, derivativeType, color, ions, baseline);
	}

	@SuppressWarnings("rawtypes")
	public ILineSeriesData getLineSeriesData(IChromatogram chromatogram, String seriesId, String overlayType, String derivativeType, Color color, List<Integer> ions, boolean baseline) {

		int startScan = 1;
		int stopScan = chromatogram.getNumberOfScans();
		return getLineSeriesData(chromatogram, startScan, stopScan, seriesId, overlayType, derivativeType, color, ions, baseline);
	}

	public ILineSeriesData getLineSeriesData(IExtractedWavelengthSignals extractedWavelengthSignals, int wavelength, String seriesId, String overlayType) {

		Color color = getSeriesColor(seriesId, overlayType);
		LineStyle lineStyle = getLineStyle(overlayType);
		//
		int length = extractedWavelengthSignals.getExtractedWavelengthSignals().size();
		double[] xSeries = new double[length];
		double[] ySeries = new double[length];
		//
		int index = 0;
		for(IExtractedWavelengthSignal extractedWavelengthSignal : extractedWavelengthSignals.getExtractedWavelengthSignals()) {
			/*
			 * X,Y array
			 */
			xSeries[index] = extractedWavelengthSignal.getRetentionTime();
			ySeries[index] = extractedWavelengthSignal.getAbundance(wavelength);
			index++;
		}
		//
		ISeriesData seriesData = new SeriesData(xSeries, ySeries, seriesId);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setLineColor(color);
		lineSeriesSettings.setLineStyle(lineStyle);
		lineSeriesSettings.setEnableArea(false);
		ILineSeriesSettings lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
		lineSeriesSettingsHighlight.setLineWidth(2);
		//
		return lineSeriesData;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private ILineSeriesData getLineSeriesData(IChromatogram chromatogram, int startScan, int stopScan, String seriesId, String overlayType, String derivativeType, Color color, List<Integer> ions, boolean baseline) {

		IBaselineModel baselineModel = chromatogram.getBaselineModel();
		LineStyle lineStyle = getLineStyle(overlayType);
		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		boolean condenseCycleNumberScans = preferenceStore.getBoolean(PreferenceConstants.P_CONDENSE_CYCLE_NUMBER_SCANS);
		//
		double[] xSeries;
		double[] ySeries;
		//
		if(chromatogram.containsScanCycles() && condenseCycleNumberScans) {
			/*
			 * TandemMS + Cycles
			 */
			ITotalScanSignalExtractor totalIonSignalExtractor = new TotalScanSignalExtractor(chromatogram);
			ITotalScanSignals totalScanSignals = totalIonSignalExtractor.getTotalScanSignals(startScan, stopScan, false, condenseCycleNumberScans);
			//
			int length = totalScanSignals.size();
			xSeries = new double[length];
			ySeries = new double[length];
			//
			int index = 0;
			for(ITotalScanSignal totalScanSignal : totalScanSignals.getTotalScanSignals()) {
				/*
				 * Get the retention time and intensity.
				 */
				int retentionTime = totalScanSignal.getRetentionTime();
				xSeries[index] = retentionTime;
				if(baseline) {
					ySeries[index] = baselineModel.getBackgroundAbundance(retentionTime);
				} else {
					ySeries[index] = totalScanSignal.getTotalSignal();
				}
				index++;
			}
		} else {
			/*
			 * Normal
			 */
			int length = stopScan - startScan + 1;
			xSeries = new double[length];
			ySeries = new double[length];
			//
			int index = 0;
			for(int i = startScan; i <= stopScan; i++) {
				/*
				 * Get the retention time and intensity.
				 */
				IScan scan = chromatogram.getScan(i);
				int retentionTime = scan.getRetentionTime();
				xSeries[index] = retentionTime;
				if(baseline) {
					ySeries[index] = baselineModel.getBackgroundAbundance(retentionTime);
				} else {
					ySeries[index] = getIntensity(scan, overlayType, ions);
				}
				index++;
			}
		}
		/*
		 * Calculate a derivative?
		 */
		int derivatives = getNumberOfDerivatives(derivativeType);
		for(int i = 1; i <= derivatives; i++) {
			ySeries = calculateDerivate(ySeries);
		}
		/*
		 * Add the series.
		 */
		ISeriesData seriesData = new SeriesData(xSeries, ySeries, seriesId);
		ILineSeriesData lineSeriesData = new LineSeriesData(seriesData);
		ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
		lineSeriesSettings.setLineColor(color);
		lineSeriesSettings.setLineStyle(lineStyle);
		lineSeriesSettings.setEnableArea(false);
		ILineSeriesSettings lineSeriesSettingsHighlight = (ILineSeriesSettings)lineSeriesSettings.getSeriesSettingsHighlight();
		lineSeriesSettingsHighlight.setLineWidth(2);
		//
		return lineSeriesData;
	}

	private double getIntensity(IScan scan, String overlayType, List<Integer> ions) {

		double intensity = 0.0d;
		if(overlayType.equals(DISPLAY_TYPE_TIC)) {
			/*
			 * TIC
			 */
			intensity = scan.getTotalSignal();
		} else if(overlayType.equals(DISPLAY_TYPE_BPC)) {
			/*
			 * BPC
			 */
			if(scan instanceof IScanMSD) {
				IScanMSD scanMSD = (IScanMSD)scan;
				IIon ion = scanMSD.getHighestAbundance();
				if(ion != null) {
					intensity = ion.getAbundance();
				}
			}
		} else if(overlayType.equals(DISPLAY_TYPE_XIC) || overlayType.equals(DISPLAY_TYPE_SIC)) {
			/*
			 * XIC, SIC
			 */
			if(scan instanceof IScanMSD) {
				IScanMSD scanMSD = (IScanMSD)scan;
				IExtractedIonSignal extractedIonSignal = scanMSD.getExtractedIonSignal();
				if(ions != null) {
					for(int ion : ions) {
						intensity += extractedIonSignal.getAbundance(ion);
					}
				}
			}
		} else if(overlayType.equals(DISPLAY_TYPE_TSC)) {
			/*
			 * TSC
			 */
			if(scan instanceof IScanMSD) {
				IScanMSD scanMSD = (IScanMSD)scan;
				IExtractedIonSignal extractedIonSignal = scanMSD.getExtractedIonSignal();
				intensity = scanMSD.getTotalSignal();
				if(ions != null) {
					for(int ion : ions) {
						intensity -= extractedIonSignal.getAbundance(ion);
					}
				}
			}
		} else if(overlayType.equals(DISPLAY_TYPE_SWC)) {
			/*
			 * SWC
			 */
			if(scan instanceof IScanWSD) {
				IScanWSD scanWSD = (IScanWSD)scan;
				List<IScanSignalWSD> scanSignalsWSD = scanWSD.getScanSignals();
				if(scanSignalsWSD != null) {
					for(IScanSignalWSD scanSignalWSD : scanSignalsWSD) {
						intensity += scanSignalWSD.getAbundance();
					}
				}
			}
		}
		//
		return intensity;
	}

	private LineStyle getLineStyle(String overlayType) {

		LineStyle lineStyle;
		if(overlayType.equals(DISPLAY_TYPE_TIC)) {
			lineStyle = lineStyleTIC;
		} else if(overlayType.equals(DISPLAY_TYPE_BPC)) {
			lineStyle = lineStyleBPC;
		} else if(overlayType.equals(DISPLAY_TYPE_XIC)) {
			lineStyle = lineStyleXIC;
		} else if(overlayType.equals(DISPLAY_TYPE_SIC)) {
			lineStyle = lineStyleSIC;
		} else if(overlayType.equals(DISPLAY_TYPE_TSC)) {
			lineStyle = lineStyleTSC;
		} else if(overlayType.equals(DISPLAY_TYPE_SWC)) {
			lineStyle = lineStyleSWC;
		} else {
			lineStyle = lineStyleDefault;
		}
		return lineStyle;
	}

	private int getNumberOfDerivatives(String derivativeType) {

		int derivatives;
		switch(derivativeType) {
			case DERIVATIVE_FIRST:
				derivatives = 1;
				break;
			case DERIVATIVE_SECOND:
				derivatives = 2;
				break;
			case DERIVATIVE_THIRD:
				derivatives = 3;
				break;
			default:
				derivatives = 0;
				break;
		}
		return derivatives;
	}

	private double[] calculateDerivate(double[] values) {

		int size = values.length;
		double[] derivative = new double[size];
		for(int i = 1; i < size; i++) {
			derivative[i] = values[i] - values[i - 1];
		}
		return derivative;
	}

	private void resetColorMaps() {

		colorSchemeNormal.reset();
		usedColorsNormal.clear();
		colorSchemeSIC.reset();
		usedColorsSIC.clear();
		colorSchemeSWC.reset();
		usedColorsSWC.clear();
	}
}
