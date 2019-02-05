/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.csd.filter.core.chromatogram.ChromatogramFilterCSD;
import org.eclipse.chemclipse.chromatogram.csd.filter.core.chromatogram.IChromatogramFilterSupportCSD;
import org.eclipse.chemclipse.chromatogram.csd.identifier.peak.IPeakIdentifierSupplierCSD;
import org.eclipse.chemclipse.chromatogram.csd.identifier.peak.IPeakIdentifierSupportCSD;
import org.eclipse.chemclipse.chromatogram.csd.identifier.peak.PeakIdentifierCSD;
import org.eclipse.chemclipse.chromatogram.csd.peak.detector.core.IPeakDetectorCSDSupplier;
import org.eclipse.chemclipse.chromatogram.csd.peak.detector.core.IPeakDetectorCSDSupport;
import org.eclipse.chemclipse.chromatogram.csd.peak.detector.core.PeakDetectorCSD;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.ChromatogramFilter;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilterSupplier;
import org.eclipse.chemclipse.chromatogram.filter.core.chromatogram.IChromatogramFilterSupport;
import org.eclipse.chemclipse.chromatogram.filter.exceptions.NoChromatogramFilterSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.classifier.core.ChromatogramClassifier;
import org.eclipse.chemclipse.chromatogram.msd.classifier.core.IChromatogramClassifierSupplier;
import org.eclipse.chemclipse.chromatogram.msd.classifier.core.IChromatogramClassifierSupport;
import org.eclipse.chemclipse.chromatogram.msd.classifier.exceptions.NoChromatogramClassifierSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.ChromatogramFilterMSD;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.IChromatogramFilterSupportMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.IPeakIdentifierSupplierMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.IPeakIdentifierSupportMSD;
import org.eclipse.chemclipse.chromatogram.msd.identifier.peak.PeakIdentifierMSD;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.IPeakDetectorMSDSupplier;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.IPeakDetectorMSDSupport;
import org.eclipse.chemclipse.chromatogram.msd.peak.detector.core.PeakDetectorMSD;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.core.IPeakQuantifierSupplier;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.core.IPeakQuantifierSupport;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.core.PeakQuantifier;
import org.eclipse.chemclipse.chromatogram.msd.quantitation.exceptions.NoPeakQuantifierAvailableException;
import org.eclipse.chemclipse.chromatogram.peak.detector.exceptions.NoPeakDetectorAvailableException;
import org.eclipse.chemclipse.chromatogram.wsd.filter.core.chromatogram.ChromatogramFilterWSD;
import org.eclipse.chemclipse.chromatogram.wsd.filter.core.chromatogram.IChromatogramFilterSupportWSD;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.core.IPeakDetectorWSDSupplier;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.core.IPeakDetectorWSDSupport;
import org.eclipse.chemclipse.chromatogram.wsd.peak.detector.core.PeakDetectorWSD;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.chromatogram.ChromatogramCalculator;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.chromatogram.IChromatogramCalculatorSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.core.chromatogram.IChromatogramCalculatorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.calculator.exceptions.NoChromatogramCalculatorSupplierAvailableException;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks.IPeakIntegratorSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks.IPeakIntegratorSupport;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks.PeakIntegrator;
import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.NoIntegratorAvailableException;
import org.eclipse.chemclipse.chromatogram.xxd.report.core.ChromatogramReports;
import org.eclipse.chemclipse.chromatogram.xxd.report.core.IChromatogramReportSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.report.core.IChromatogramReportSupport;
import org.eclipse.chemclipse.csd.model.core.IPeakCSD;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.SeparationColumnFactory;
import org.eclipse.chemclipse.model.comparator.PeakRetentionTimeComparator;
import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.NoIdentifierAvailableException;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.updates.IChromatogramSelectionUpdateListener;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.comparator.SortOrder;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.components.IMethodListener;
import org.eclipse.chemclipse.swt.ui.preferences.PreferencePageSWT;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.calibration.RetentionIndexUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.charts.ChromatogramChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.charts.IdentificationLabelMarker;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.MethodSupportUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogram;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogramAxes;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogramPeaks;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferencePageChromatogramScans;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.DisplayType;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ChromatogramDataSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.PeakChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.support.charts.ScanChartSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ChromatogramActionUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ChromatogramReferencesUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.HeatmapUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.IChromatogramReferencesListener;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ToolbarConfig;
import org.eclipse.chemclipse.wsd.model.core.IPeakWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swtchart.IAxis.Position;
import org.eclipse.swtchart.ILineSeries.PlotSymbolType;
import org.eclipse.swtchart.IPlotArea;
import org.eclipse.swtchart.LineStyle;
import org.eclipse.swtchart.extensions.axisconverter.MillisecondsToScanNumberConverter;
import org.eclipse.swtchart.extensions.core.BaseChart;
import org.eclipse.swtchart.extensions.core.IAxisSettings;
import org.eclipse.swtchart.extensions.core.IChartSettings;
import org.eclipse.swtchart.extensions.core.IExtendedChart;
import org.eclipse.swtchart.extensions.core.ISecondaryAxisSettings;
import org.eclipse.swtchart.extensions.core.RangeRestriction;
import org.eclipse.swtchart.extensions.core.SecondaryAxisSettings;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesData;
import org.eclipse.swtchart.extensions.linecharts.ILineSeriesSettings;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;
import org.eclipse.swtchart.extensions.menu.ResetChartHandler;

@SuppressWarnings("rawtypes")
public class ExtendedChromatogramUI implements ToolbarConfig {

	private static final Logger logger = Logger.getLogger(ExtendedChromatogramUI.class);
	//
	protected static final String TYPE_GENERIC = "TYPE_GENERIC";
	protected static final String TYPE_MSD = "TYPE_MSD";
	protected static final String TYPE_CSD = "TYPE_CSD";
	protected static final String TYPE_WSD = "TYPE_WSD";
	//
	private static final String TITLE_X_AXIS_SCANS = "Scans (approx.)";
	private static final String LABEL_SCAN_NUMBER = "Scan Number";
	//
	private static final String SERIES_ID_CHROMATOGRAM = "Chromatogram";
	private static final String SERIES_ID_BASELINE = "Baseline";
	private static final String SERIES_ID_PEAKS_NORMAL_ACTIVE = "Peak(s) [Active]";
	private static final String SERIES_ID_PEAKS_NORMAL_INACTIVE = "Peak(s) [Inactive]";
	private static final String SERIES_ID_PEAKS_ISTD_ACTIVE = "Peak(s) ISTD [Active]";
	private static final String SERIES_ID_PEAKS_ISTD_INACTIVE = "Peak(s) ISTD [Inactive]";
	private static final String SERIES_ID_SELECTED_PEAK_MARKER = "Selected Peak Marker";
	private static final String SERIES_ID_SELECTED_PEAK_SHAPE = "Selected Peak Shape";
	private static final String SERIES_ID_SELECTED_PEAK_BACKGROUND = "Selected Peak Background";
	private static final String SERIES_ID_SELECTED_SCAN = "Selected Scan";
	private static final String SERIES_ID_IDENTIFIED_SCANS = "Identified Scans";
	private static final String SERIES_ID_IDENTIFIED_SCAN_SELECTED = "Identified Scan Selected";
	//
	private static final int THREE_MINUTES = (int)(AbstractChromatogram.MINUTE_CORRELATION_FACTOR * 3);
	private static final int FIVE_MINUTES = (int)(AbstractChromatogram.MINUTE_CORRELATION_FACTOR * 5);
	//
	private static final String TOOLBAR_INFO = "TOOLBAR_INFO";
	private static final String TOOLBAR_RETENTION_INDICES = "TOOLBAR_RETENTION_INDICES";
	private static final String TOOLBAR_METHOD = "TOOLBAR_METHOD";
	private static final String TOOLBAR_PEAK_TARGET_TRANSFER = "TOOLBAR_PEAK_TARGET_TRANSFER";
	private static final String TOOLBAR_CHROMATOGRAM_ALIGNMENT = "TOOLBAR_CHROMATOGRAM_ALIGNMENT";
	private static final String TOOLBAR_EDIT = "TOOLBAR_EDIT";
	private Map<String, Composite> toolbars = new HashMap<>();
	//
	private Composite toolbarMain;
	private Label labelChromatogramInfo;
	private ChromatogramReferencesUI chromatogramReferencesUI;
	private RetentionIndexUI retentionIndexUI;
	private ChromatogramChart chromatogramChart;
	private ComboViewer comboViewerSeparationColumn;
	private ChromatogramActionUI chromatogramActionUI;
	private HeatmapUI heatmapUI;
	private Composite heatmapArea;
	//
	private IChromatogramSelection chromatogramSelection = null;
	//
	private List<IChartMenuEntry> chartMenuEntriesCalculators = new ArrayList<IChartMenuEntry>();
	private List<IChartMenuEntry> chartMenuEntriesClassifier = new ArrayList<IChartMenuEntry>();
	private List<IChartMenuEntry> chartMenuEntriesFilter = new ArrayList<IChartMenuEntry>();
	private List<IChartMenuEntry> chartMenuEntriesPeakDetectors = new ArrayList<IChartMenuEntry>();
	private List<IChartMenuEntry> chartMenuEntriesPeakIntegrators = new ArrayList<IChartMenuEntry>();
	private List<IChartMenuEntry> chartMenuEntriesPeakIdentifier = new ArrayList<IChartMenuEntry>();
	private List<IChartMenuEntry> chartMenuEntriesPeakQuantifier = new ArrayList<IChartMenuEntry>();
	private List<IChartMenuEntry> chartMenuEntriesReports = new ArrayList<IChartMenuEntry>();
	//
	private Map<String, IdentificationLabelMarker> peakLabelMarkerMap = new HashMap<String, IdentificationLabelMarker>();
	private Map<String, IdentificationLabelMarker> scanLabelMarkerMap = new HashMap<String, IdentificationLabelMarker>();
	//
	private PeakRetentionTimeComparator peakRetentionTimeComparator = new PeakRetentionTimeComparator(SortOrder.ASC);
	private PeakChartSupport peakChartSupport = new PeakChartSupport();
	private ScanChartSupport scanChartSupport = new ScanChartSupport();
	private ChromatogramDataSupport chromatogramDataSupport = new ChromatogramDataSupport();
	private ChromatogramChartSupport chromatogramChartSupport = new ChromatogramChartSupport();
	private ChartSupport chartSupport = new ChartSupport(Activator.getDefault().getPreferenceStore());
	//
	private DisplayType displayType = DisplayType.TIC;
	//
	private boolean suspendUpdate = false;
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

	@Inject
	public ExtendedChromatogramUI(Composite parent, int style) {
		initialize(parent, style);
	}

	@Override
	public void setToolbarVisible(boolean visible) {

		PartSupport.setCompositeVisibility(toolbarMain, visible);
	}

	public void fireUpdate() {

		fireUpdateChromatogram();
		if(!fireUpdatePeak()) {
			fireUpdateScan();
		}
	}

	public boolean fireUpdateChromatogram() {

		IChromatogramSelection chromatogramSelection = getChromatogramSelection();
		if(chromatogramSelection != null) {
			/*
			 * Will be removed as soon as the topics
			 * IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION
			 * ...
			 * are removed.
			 */
			if(preferenceStore.getBoolean(PreferenceConstants.P_LEGACY_UPDATE_CHROMATOGRAM_MODUS)) {
				fireUpdateChromatogramLegacy();
			}
			//
			DisplayUtils.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {

					IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
					eventBroker.send(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_LOAD_CHROMATOGRAM_SELECTION, chromatogramSelection);
				}
			});
		}
		return chromatogramSelection != null ? true : false;
	}

	private void fireUpdateChromatogramLegacy() {

		Map<String, Object> map = new HashMap<>();
		map.put(IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION, chromatogramSelection);
		map.put(IChemClipseEvents.PROPERTY_FORCE_RELOAD, true);
		String topic;
		//
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			topic = IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION;
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
			topic = IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_SELECTION;
		} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
			topic = IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION;
		} else {
			topic = null;
		}
		//
		if(topic != null) {
			DisplayUtils.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {

					IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
					eventBroker.post(topic, map);
				}
			});
		}
	}

	public boolean fireUpdatePeak() {

		boolean update = false;
		IChromatogramSelection chromatogramSelection = getChromatogramSelection();
		if(chromatogramSelection != null) {
			final IPeak peak = chromatogramSelection.getSelectedPeak();
			if(peak != null) {
				/*
				 * Will be removed as soon as the topics
				 * IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_PEAK
				 * ...
				 * are removed.
				 */
				if(preferenceStore.getBoolean(PreferenceConstants.P_LEGACY_UPDATE_PEAK_MODUS)) {
					fireUpdatePeakLegacy(peak);
				}
				//
				update = true;
				DisplayUtils.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {

						IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
						eventBroker.send(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION, peak);
					}
				});
			}
		}
		return update;
	}

	private void fireUpdatePeakLegacy(IPeak peak) {

		if(peak != null) {
			Map<String, Object> map = new HashMap<>();
			map.put(IChemClipseEvents.PROPERTY_PEAK_MSD, peak);
			map.put(IChemClipseEvents.PROPERTY_FORCE_RELOAD, true);
			String topic;
			//
			if(peak instanceof IPeakMSD) {
				topic = IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_PEAK;
			} else if(peak instanceof IPeakCSD) {
				topic = IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_PEAK;
			} else if(peak instanceof IPeakWSD) {
				topic = IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_PEAK;
			} else {
				topic = null;
			}
			//
			if(topic != null) {
				DisplayUtils.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {

						IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
						eventBroker.post(topic, map);
					}
				});
			}
		}
	}

	public boolean fireUpdateScan() {

		boolean update = false;
		IChromatogramSelection chromatogramSelection = getChromatogramSelection();
		if(chromatogramSelection != null) {
			final IScan scan = chromatogramSelection.getSelectedScan();
			if(scan != null) {
				update = true;
				DisplayUtils.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {

						IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
						eventBroker.post(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, scan);
					}
				});
			}
		}
		return update;
	}

	public ChromatogramChart getChromatogramChart() {

		return chromatogramChart;
	}

	public synchronized void updateChromatogramSelection(IChromatogramSelection chromatogramSelection) {

		this.chromatogramSelection = chromatogramSelection;
		chromatogramActionUI.setChromatogramActionMenu(chromatogramSelection);
		updateToolbar(toolbars.get(TOOLBAR_PEAK_TARGET_TRANSFER), chromatogramSelection);
		updateToolbar(toolbars.get(TOOLBAR_CHROMATOGRAM_ALIGNMENT), chromatogramSelection);
		//
		if(chromatogramSelection != null) {
			/*
			 * Adjust
			 */
			adjustAxisSettings();
			addChartMenuEntries();
			updateChromatogram();
			setSeparationColumnSelection();
			chromatogramReferencesUI.updateChromatogramSelection(chromatogramSelection);
			retentionIndexUI.setInput(chromatogramSelection.getChromatogram().getSeparationColumnIndices());
		} else {
			chromatogramReferencesUI.clear();
			retentionIndexUI.setInput(null);
			updateChromatogram();
		}
	}

	public void update() {

		if(!suspendUpdate) {
			updateChromatogram();
			adjustChromatogramSelectionRange();
			setSeparationColumnSelection();
		}
	}

	public void updateSelectedScan() {

		chromatogramChart.deleteSeries(SERIES_ID_SELECTED_SCAN);
		chromatogramChart.deleteSeries(SERIES_ID_IDENTIFIED_SCAN_SELECTED);
		//
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		addSelectedScanData(lineSeriesDataList);
		addSelectedIdentifiedScanData(lineSeriesDataList);
		addLineSeriesData(lineSeriesDataList);
		adjustChromatogramSelectionRange();
	}

	public void updateSelectedPeak() {

		chromatogramChart.deleteSeries(SERIES_ID_SELECTED_PEAK_MARKER);
		chromatogramChart.deleteSeries(SERIES_ID_SELECTED_PEAK_SHAPE);
		chromatogramChart.deleteSeries(SERIES_ID_SELECTED_PEAK_BACKGROUND);
		//
		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		addSelectedPeakData(lineSeriesDataList);
		addLineSeriesData(lineSeriesDataList);
		adjustChromatogramSelectionRange();
	}

	public IChromatogramSelection getChromatogramSelection() {

		return chromatogramSelection;
	}

	public boolean isActiveChromatogramSelection(IChromatogramSelection chromatogramSelection) {

		if(this.chromatogramSelection == chromatogramSelection) {
			return true;
		}
		return false;
	}

	protected void setChromatogramSelectionRange(int startRetentionTime, int stopRetentionTime, float startAbundance, float stopAbundance) {

		chromatogramSelection.setRanges(startRetentionTime, stopRetentionTime, startAbundance, stopAbundance, false);
		suspendUpdate = true;
		chromatogramSelection.update(true);
		suspendUpdate = false;
		adjustChromatogramSelectionRange();
	}

	protected void updateSelection() {

		if(chromatogramSelection != null) {
			chromatogramSelection.update(true);
			adjustChromatogramSelectionRange();
		}
	}

	protected void processChromatogram(IRunnableWithProgress runnable) {

		ProgressMonitorDialog monitor = new ProgressMonitorDialog(DisplayUtils.getShell());
		try {
			monitor.run(true, true, runnable);
			updateChromatogram();
			updateSelection();
			fireUpdate();
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	private void adjustMinuteScale() {

		if(chromatogramSelection != null) {
			int startRetentionTime = chromatogramSelection.getStartRetentionTime();
			int stopRetentionTime = chromatogramSelection.getStopRetentionTime();
			int deltaRetentionTime = stopRetentionTime - startRetentionTime + 1;
			IChartSettings chartSettings = chromatogramChart.getChartSettings();
			List<ISecondaryAxisSettings> axisSettings = chartSettings.getSecondaryAxisSettingsListX();
			for(ISecondaryAxisSettings axisSetting : axisSettings) {
				if(axisSetting.getTitle().equals("Minutes")) {
					if(deltaRetentionTime >= FIVE_MINUTES) {
						axisSetting.setDecimalFormat(new DecimalFormat(("0.00"), new DecimalFormatSymbols(Locale.ENGLISH)));
					} else if(deltaRetentionTime >= THREE_MINUTES) {
						axisSetting.setDecimalFormat(new DecimalFormat(("0.000"), new DecimalFormatSymbols(Locale.ENGLISH)));
					} else {
						axisSetting.setDecimalFormat(new DecimalFormat(("0.0000"), new DecimalFormatSymbols(Locale.ENGLISH)));
					}
				}
			}
			chromatogramChart.applySettings(chartSettings);
		}
	}

	private void addChartMenuEntries() {

		addChartMenuEntriesCalculators();
		addChartMenuEntriesClassifier();
		addChartMenuEntriesFilter();
		addChartMenuEntriesPeakDetectors();
		addChartMenuEntriesPeakIntegrators();
		addChartMenuEntriesPeakIdentifier();
		addChartMenuEntriesPeakQuantifier();
		addChartMenuEntriesReport();
	}

	private void addChartMenuEntriesCalculators() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		cleanChartMenuEntries(chartSettings, chartMenuEntriesCalculators);
		//
		if(chromatogramSelection != null) {
			if(chromatogramSelection instanceof IChromatogramSelection) {
				addChartMenuEntriesCalculators(chartSettings);
			}
		}
		//
		chromatogramChart.applySettings(chartSettings);
	}

	private void addChartMenuEntriesCalculators(IChartSettings chartSettings) {

		try {
			IChromatogramCalculatorSupport chromatogramCalculatorSupport = ChromatogramCalculator.getChromatogramCalculatorSupport();
			for(String calculatorId : chromatogramCalculatorSupport.getAvailableCalculatorIds()) {
				IChromatogramCalculatorSupplier calculator = chromatogramCalculatorSupport.getCalculatorSupplier(calculatorId);
				String name = calculator.getCalculatorName();
				CalculatorMenuEntry calculatorMenuEntry = new CalculatorMenuEntry(this, name, calculatorId, TYPE_GENERIC, chromatogramSelection);
				chartMenuEntriesCalculators.add(calculatorMenuEntry);
				chartSettings.addMenuEntry(calculatorMenuEntry);
			}
		} catch(NoChromatogramCalculatorSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesClassifier() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		cleanChartMenuEntries(chartSettings, chartMenuEntriesClassifier);
		//
		if(chromatogramSelection != null) {
			/*
			 * MSD
			 */
			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				addChartMenuEntriesClassifierMSD(chartSettings);
			}
		}
		//
		chromatogramChart.applySettings(chartSettings);
	}

	private void addChartMenuEntriesClassifierMSD(IChartSettings chartSettings) {

		try {
			IChromatogramClassifierSupport chromatogramClassifierSupport = ChromatogramClassifier.getChromatogramClassifierSupport();
			for(String filterId : chromatogramClassifierSupport.getAvailableClassifierIds()) {
				IChromatogramClassifierSupplier classifier = chromatogramClassifierSupport.getClassifierSupplier(filterId);
				String name = classifier.getClassifierName();
				ClassifierMenuEntry classifierMenuEntry = new ClassifierMenuEntry(this, name, filterId, TYPE_MSD, chromatogramSelection);
				chartMenuEntriesClassifier.add(classifierMenuEntry);
				chartSettings.addMenuEntry(classifierMenuEntry);
			}
		} catch(NoChromatogramClassifierSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesFilter() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		cleanChartMenuEntries(chartSettings, chartMenuEntriesFilter);
		//
		if(chromatogramSelection != null) {
			/*
			 * Generic
			 */
			addChartMenuEntriesFilter(chartSettings);
			/*
			 * Specific
			 */
			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				addChartMenuEntriesFilterMSD(chartSettings);
			} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
				addChartMenuEntriesFilterCSD(chartSettings);
			} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
				addChartMenuEntriesFilterWSD(chartSettings);
			}
		}
		//
		chromatogramChart.applySettings(chartSettings);
	}

	private void addChartMenuEntriesFilter(IChartSettings chartSettings) {

		try {
			IChromatogramFilterSupport chromatogramFilterSupport = ChromatogramFilter.getChromatogramFilterSupport();
			for(String filterId : chromatogramFilterSupport.getAvailableFilterIds()) {
				IChromatogramFilterSupplier filter = chromatogramFilterSupport.getFilterSupplier(filterId);
				String name = filter.getFilterName();
				FilterMenuEntry filterMenuEntry = new FilterMenuEntry(this, name, filterId, TYPE_GENERIC, chromatogramSelection);
				chartMenuEntriesFilter.add(filterMenuEntry);
				chartSettings.addMenuEntry(filterMenuEntry);
			}
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesFilterMSD(IChartSettings chartSettings) {

		try {
			IChromatogramFilterSupportMSD chromatogramFilterSupport = ChromatogramFilterMSD.getChromatogramFilterSupport();
			for(String filterId : chromatogramFilterSupport.getAvailableFilterIds()) {
				IChromatogramFilterSupplier filter = chromatogramFilterSupport.getFilterSupplier(filterId);
				String name = filter.getFilterName();
				FilterMenuEntry filterMenuEntry = new FilterMenuEntry(this, name, filterId, TYPE_MSD, chromatogramSelection);
				chartMenuEntriesFilter.add(filterMenuEntry);
				chartSettings.addMenuEntry(filterMenuEntry);
			}
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesFilterCSD(IChartSettings chartSettings) {

		try {
			IChromatogramFilterSupportCSD chromatogramFilterSupport = ChromatogramFilterCSD.getChromatogramFilterSupport();
			for(String filterId : chromatogramFilterSupport.getAvailableFilterIds()) {
				IChromatogramFilterSupplier filter = chromatogramFilterSupport.getFilterSupplier(filterId);
				String name = filter.getFilterName();
				FilterMenuEntry filterMenuEntry = new FilterMenuEntry(this, name, filterId, TYPE_CSD, chromatogramSelection);
				chartMenuEntriesFilter.add(filterMenuEntry);
				chartSettings.addMenuEntry(filterMenuEntry);
			}
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesFilterWSD(IChartSettings chartSettings) {

		try {
			IChromatogramFilterSupportWSD chromatogramFilterSupport = ChromatogramFilterWSD.getChromatogramFilterSupport();
			for(String filterId : chromatogramFilterSupport.getAvailableFilterIds()) {
				IChromatogramFilterSupplier filter = chromatogramFilterSupport.getFilterSupplier(filterId);
				String name = filter.getFilterName();
				FilterMenuEntry filterMenuEntry = new FilterMenuEntry(this, name, filterId, TYPE_WSD, chromatogramSelection);
				chartMenuEntriesFilter.add(filterMenuEntry);
				chartSettings.addMenuEntry(filterMenuEntry);
			}
		} catch(NoChromatogramFilterSupplierAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesPeakDetectors() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		cleanChartMenuEntries(chartSettings, chartMenuEntriesPeakDetectors);
		//
		if(chromatogramSelection != null) {
			/*
			 * Specific
			 */
			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				addChartMenuEntriesPeakDetectorMSD(chartSettings);
			} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
				addChartMenuEntriesPeakDetectorCSD(chartSettings);
			} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
				addChartMenuEntriesPeakDetectorWSD(chartSettings);
			}
		}
		//
		chromatogramChart.applySettings(chartSettings);
	}

	private void addChartMenuEntriesPeakDetectorMSD(IChartSettings chartSettings) {

		try {
			IPeakDetectorMSDSupport peakDetectorSupport = PeakDetectorMSD.getPeakDetectorSupport();
			for(String peakDetectorId : peakDetectorSupport.getAvailablePeakDetectorIds()) {
				IPeakDetectorMSDSupplier peakDetecorSupplier = peakDetectorSupport.getPeakDetectorSupplier(peakDetectorId);
				String name = peakDetecorSupplier.getPeakDetectorName();
				PeakDetectorMenuEntry menuEntry = new PeakDetectorMenuEntry(this, name, peakDetectorId, TYPE_MSD, chromatogramSelection);
				chartMenuEntriesPeakDetectors.add(menuEntry);
				chartSettings.addMenuEntry(menuEntry);
			}
		} catch(NoPeakDetectorAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesPeakDetectorCSD(IChartSettings chartSettings) {

		try {
			IPeakDetectorCSDSupport peakDetectorSupport = PeakDetectorCSD.getPeakDetectorSupport();
			for(String peakDetectorId : peakDetectorSupport.getAvailablePeakDetectorIds()) {
				IPeakDetectorCSDSupplier peakDetecorSupplier = peakDetectorSupport.getPeakDetectorSupplier(peakDetectorId);
				String name = peakDetecorSupplier.getPeakDetectorName();
				PeakDetectorMenuEntry menuEntry = new PeakDetectorMenuEntry(this, name, peakDetectorId, TYPE_CSD, chromatogramSelection);
				chartMenuEntriesPeakDetectors.add(menuEntry);
				chartSettings.addMenuEntry(menuEntry);
			}
		} catch(NoPeakDetectorAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesPeakDetectorWSD(IChartSettings chartSettings) {

		try {
			IPeakDetectorWSDSupport peakDetectorSupport = PeakDetectorWSD.getPeakDetectorSupport();
			for(String peakDetectorId : peakDetectorSupport.getAvailablePeakDetectorIds()) {
				IPeakDetectorWSDSupplier peakDetecorSupplier = peakDetectorSupport.getPeakDetectorSupplier(peakDetectorId);
				String name = peakDetecorSupplier.getPeakDetectorName();
				PeakDetectorMenuEntry menuEntry = new PeakDetectorMenuEntry(this, name, peakDetectorId, TYPE_WSD, chromatogramSelection);
				chartMenuEntriesPeakDetectors.add(menuEntry);
				chartSettings.addMenuEntry(menuEntry);
			}
		} catch(NoPeakDetectorAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesPeakIntegrators() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		cleanChartMenuEntries(chartSettings, chartMenuEntriesPeakIntegrators);
		//
		if(chromatogramSelection != null) {
			/*
			 * Generic
			 */
			addChartMenuEntriesPeakIntegrator(chartSettings);
		}
	}

	private void addChartMenuEntriesPeakIntegrator(IChartSettings chartSettings) {

		try {
			IPeakIntegratorSupport peakIntegratorSupport = PeakIntegrator.getPeakIntegratorSupport();
			for(String peakIntegratorId : peakIntegratorSupport.getAvailableIntegratorIds()) {
				IPeakIntegratorSupplier peakIntegratorSupplier = peakIntegratorSupport.getIntegratorSupplier(peakIntegratorId);
				String name = peakIntegratorSupplier.getIntegratorName();
				PeakIntegratorMenuEntry menuEntry = new PeakIntegratorMenuEntry(this, name, peakIntegratorId, TYPE_GENERIC, chromatogramSelection);
				chartMenuEntriesPeakIntegrators.add(menuEntry);
				chartSettings.addMenuEntry(menuEntry);
			}
		} catch(NoIntegratorAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesPeakIdentifier() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		cleanChartMenuEntries(chartSettings, chartMenuEntriesPeakIdentifier);
		//
		if(chromatogramSelection != null) {
			/*
			 * Specific
			 */
			if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
				addChartMenuEntriesPeakIdentifierMSD(chartSettings);
			} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
				addChartMenuEntriesPeakIdentifierCSD(chartSettings);
			}
		}
	}

	private void addChartMenuEntriesPeakIdentifierMSD(IChartSettings chartSettings) {

		try {
			IPeakIdentifierSupportMSD peakIdentifierSupport = PeakIdentifierMSD.getPeakIdentifierSupport();
			for(String peakIdentifierId : peakIdentifierSupport.getAvailableIdentifierIds()) {
				IPeakIdentifierSupplierMSD peakIdentifierSupplier = peakIdentifierSupport.getIdentifierSupplier(peakIdentifierId);
				String name = peakIdentifierSupplier.getIdentifierName();
				PeakIdentifierMenuEntry menuEntry = new PeakIdentifierMenuEntry(this, name, peakIdentifierId, TYPE_MSD, chromatogramSelection);
				chartMenuEntriesPeakIdentifier.add(menuEntry);
				chartSettings.addMenuEntry(menuEntry);
			}
		} catch(NoIdentifierAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesPeakIdentifierCSD(IChartSettings chartSettings) {

		try {
			IPeakIdentifierSupportCSD peakIdentifierSupport = PeakIdentifierCSD.getPeakIdentifierSupport();
			for(String peakIdentifierId : peakIdentifierSupport.getAvailableIdentifierIds()) {
				IPeakIdentifierSupplierCSD peakIdentifierSupplier = peakIdentifierSupport.getIdentifierSupplier(peakIdentifierId);
				String name = peakIdentifierSupplier.getIdentifierName();
				PeakIdentifierMenuEntry menuEntry = new PeakIdentifierMenuEntry(this, name, peakIdentifierId, TYPE_CSD, chromatogramSelection);
				chartMenuEntriesPeakIdentifier.add(menuEntry);
				chartSettings.addMenuEntry(menuEntry);
			}
		} catch(NoIdentifierAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesPeakQuantifier() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		cleanChartMenuEntries(chartSettings, chartMenuEntriesPeakQuantifier);
		//
		if(chromatogramSelection != null) {
			/*
			 * Generic
			 */
			addChartMenuEntriesPeakQuantifier(chartSettings);
		}
	}

	private void addChartMenuEntriesPeakQuantifier(IChartSettings chartSettings) {

		try {
			IPeakQuantifierSupport peakQuantifierSupport = PeakQuantifier.getPeakQuantifierSupport();
			for(String peakQuantifierId : peakQuantifierSupport.getAvailablePeakQuantifierIds()) {
				IPeakQuantifierSupplier peakQuantifierSupplier = peakQuantifierSupport.getPeakQuantifierSupplier(peakQuantifierId);
				String name = peakQuantifierSupplier.getPeakQuantifierName();
				PeakQuantifierMenuEntry menuEntry = new PeakQuantifierMenuEntry(this, name, peakQuantifierId, TYPE_GENERIC, chromatogramSelection);
				chartMenuEntriesPeakQuantifier.add(menuEntry);
				chartSettings.addMenuEntry(menuEntry);
			}
		} catch(NoPeakQuantifierAvailableException e) {
			logger.warn(e);
		}
	}

	private void addChartMenuEntriesReport() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		cleanChartMenuEntries(chartSettings, chartMenuEntriesReports);
		//
		if(chromatogramSelection != null) {
			/*
			 * Generic
			 */
			addChartMenuEntriesReport(chartSettings);
		}
	}

	private void addChartMenuEntriesReport(IChartSettings chartSettings) {

		IChromatogramReportSupport chromatogramReportSupport = ChromatogramReports.getChromatogramReportSupplierSupport();
		for(IChromatogramReportSupplier chromatogramReportSupplier : chromatogramReportSupport.getReportSupplier()) {
			ReportMenuEntry menuEntry = new ReportMenuEntry(this, chromatogramReportSupplier, TYPE_GENERIC);
			chartMenuEntriesReports.add(menuEntry);
			chartSettings.addMenuEntry(menuEntry);
		}
	}

	private void cleanChartMenuEntries(IChartSettings chartSettings, List<IChartMenuEntry> chartMenuEntries) {

		for(IChartMenuEntry chartMenuEntry : chartMenuEntries) {
			chartSettings.removeMenuEntry(chartMenuEntry);
		}
		chartMenuEntries.clear();
	}

	private void updateChromatogram() {

		updateLabel();
		clearPeakAndScanLabels();
		adjustMinuteScale();
		deleteScanNumberSecondaryAxisX();
		chromatogramChart.deleteSeries();
		//
		if(chromatogramSelection != null) {
			addjustChromatogramChart();
			addChromatogramSeriesData();
			adjustChromatogramSelectionRange();
		}
	}

	private void clearPeakAndScanLabels() {

		removeIdentificationLabelMarker(peakLabelMarkerMap, SERIES_ID_PEAKS_NORMAL_ACTIVE);
		removeIdentificationLabelMarker(peakLabelMarkerMap, SERIES_ID_PEAKS_NORMAL_INACTIVE);
		removeIdentificationLabelMarker(peakLabelMarkerMap, SERIES_ID_PEAKS_ISTD_ACTIVE);
		removeIdentificationLabelMarker(peakLabelMarkerMap, SERIES_ID_PEAKS_ISTD_INACTIVE);
		removeIdentificationLabelMarker(scanLabelMarkerMap, SERIES_ID_IDENTIFIED_SCANS);
		//
		clearLabelMarker(peakLabelMarkerMap);
		clearLabelMarker(scanLabelMarkerMap);
	}

	private void clearLabelMarker(Map<String, IdentificationLabelMarker> markerMap) {

		for(IdentificationLabelMarker identificationLabelMarker : markerMap.values()) {
			identificationLabelMarker.clear();
		}
		markerMap.clear();
	}

	private void removeIdentificationLabelMarker(Map<String, IdentificationLabelMarker> markerMap, String seriesId) {

		IPlotArea plotArea = (IPlotArea)chromatogramChart.getBaseChart().getPlotArea();
		IdentificationLabelMarker labelMarker = markerMap.get(seriesId);
		/*
		 * Remove the label marker.
		 */
		if(labelMarker != null) {
			plotArea.removeCustomPaintListener(labelMarker);
		}
	}

	private void addjustChromatogramChart() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		RangeRestriction rangeRestriction = chartSettings.getRangeRestriction();
		rangeRestriction.setForceZeroMinY(false);
		/*
		 * Add space on top to show labels correctly.
		 */
		double extendX = preferenceStore.getDouble(PreferenceConstants.P_CHROMATOGRAM_EXTEND_X);
		rangeRestriction.setExtendMaxY(extendX);
		/*
		 * MSD has no negative intensity values, so setZeroY(true)
		 */
		if(chromatogramSelection instanceof IChromatogramSelectionMSD) {
			rangeRestriction.setZeroY(true);
		} else if(chromatogramSelection instanceof IChromatogramSelectionCSD) {
			rangeRestriction.setZeroY(false);
		} else if(chromatogramSelection instanceof IChromatogramSelectionWSD) {
			rangeRestriction.setZeroY(false);
		}
		/*
		 * Zooming
		 */
		rangeRestriction.setXZoomOnly(preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_X_ZOOM_ONLY));
		rangeRestriction.setYZoomOnly(preferenceStore.getBoolean(PreferenceConstants.P_CHROMATOGRAM_Y_ZOOM_ONLY));
		//
		chromatogramChart.applySettings(chartSettings);
	}

	private void addChromatogramSeriesData() {

		List<ILineSeriesData> lineSeriesDataList = new ArrayList<ILineSeriesData>();
		//
		addChromatogramData(lineSeriesDataList);
		addPeakData(lineSeriesDataList);
		addIdentifiedScansData(lineSeriesDataList);
		addSelectedPeakData(lineSeriesDataList);
		addSelectedScanData(lineSeriesDataList);
		addSelectedIdentifiedScanData(lineSeriesDataList);
		addBaselineData(lineSeriesDataList);
		//
		addLineSeriesData(lineSeriesDataList);
	}

	private void addChromatogramData(List<ILineSeriesData> lineSeriesDataList) {

		Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM));
		boolean enableChromatogramArea = preferenceStore.getBoolean(PreferenceConstants.P_ENABLE_CHROMATOGRAM_AREA);
		//
		ILineSeriesData lineSeriesData = chromatogramChartSupport.getLineSeriesData(chromatogramSelection, SERIES_ID_CHROMATOGRAM, displayType, color, false);
		lineSeriesData.getLineSeriesSettings().setEnableArea(enableChromatogramArea);
		lineSeriesDataList.add(lineSeriesData);
	}

	private void addPeakData(List<ILineSeriesData> lineSeriesDataList) {

		if(chromatogramSelection != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE);
			//
			List<? extends IPeak> peaks = chromatogramDataSupport.getPeaks(chromatogram);
			List<IPeak> peaksActiveNormal = new ArrayList<IPeak>();
			List<IPeak> peaksInactiveNormal = new ArrayList<IPeak>();
			List<IPeak> peaksActiveISTD = new ArrayList<IPeak>();
			List<IPeak> peaksInactiveISTD = new ArrayList<IPeak>();
			//
			for(IPeak peak : peaks) {
				if(peak.getInternalStandards().size() > 0) {
					if(peak.isActiveForAnalysis()) {
						peaksActiveISTD.add(peak);
					} else {
						peaksInactiveISTD.add(peak);
					}
				} else {
					if(peak.isActiveForAnalysis()) {
						peaksActiveNormal.add(peak);
					} else {
						peaksInactiveNormal.add(peak);
					}
				}
			}
			//
			addPeaks(lineSeriesDataList, peaksActiveNormal, PlotSymbolType.INVERTED_TRIANGLE, symbolSize, Colors.DARK_GRAY, SERIES_ID_PEAKS_NORMAL_ACTIVE);
			addPeaks(lineSeriesDataList, peaksInactiveNormal, PlotSymbolType.INVERTED_TRIANGLE, symbolSize, Colors.GRAY, SERIES_ID_PEAKS_NORMAL_INACTIVE);
			addPeaks(lineSeriesDataList, peaksActiveISTD, PlotSymbolType.DIAMOND, symbolSize, Colors.RED, SERIES_ID_PEAKS_ISTD_ACTIVE);
			addPeaks(lineSeriesDataList, peaksInactiveISTD, PlotSymbolType.DIAMOND, symbolSize, Colors.GRAY, SERIES_ID_PEAKS_ISTD_INACTIVE);
		}
	}

	private void addPeaks(List<ILineSeriesData> lineSeriesDataList, List<IPeak> peaks, PlotSymbolType plotSymbolType, int symbolSize, Color symbolColor, String seriesId) {

		if(peaks.size() > 0) {
			//
			Collections.sort(peaks, peakRetentionTimeComparator);
			ILineSeriesData lineSeriesData = peakChartSupport.getPeaks(peaks, true, false, symbolColor, seriesId);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
			lineSeriesSettings.setEnableArea(false);
			lineSeriesSettings.setLineStyle(LineStyle.NONE);
			lineSeriesSettings.setSymbolType(plotSymbolType);
			lineSeriesSettings.setSymbolSize(symbolSize);
			lineSeriesSettings.setSymbolColor(symbolColor);
			lineSeriesDataList.add(lineSeriesData);
			/*
			 * Add the labels.
			 */
			removeIdentificationLabelMarker(peakLabelMarkerMap, seriesId);
			boolean showChromatogramPeakLabels = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_PEAK_LABELS);
			if(showChromatogramPeakLabels) {
				IPlotArea plotArea = (IPlotArea)chromatogramChart.getBaseChart().getPlotArea();
				int indexSeries = lineSeriesDataList.size() - 1;
				IdentificationLabelMarker peakLabelMarker = new IdentificationLabelMarker(chromatogramChart.getBaseChart(), indexSeries, peaks, null);
				plotArea.addCustomPaintListener(peakLabelMarker);
				peakLabelMarkerMap.put(seriesId, peakLabelMarker);
			}
		}
	}

	private void addIdentifiedScansData(List<ILineSeriesData> lineSeriesDataList) {

		if(chromatogramSelection != null) {
			String seriesId = SERIES_ID_IDENTIFIED_SCANS;
			List<IScan> scans = chromatogramDataSupport.getIdentifiedScans(chromatogramSelection.getChromatogram());
			int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE);
			addIdentifiedScansData(lineSeriesDataList, scans, PlotSymbolType.CIRCLE, symbolSize, Colors.DARK_GRAY, seriesId);
			/*
			 * Add the labels.
			 */
			removeIdentificationLabelMarker(scanLabelMarkerMap, seriesId);
			boolean showChromatogramScanLabels = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_SCAN_LABELS);
			if(showChromatogramScanLabels) {
				IPlotArea plotArea = (IPlotArea)chromatogramChart.getBaseChart().getPlotArea();
				int indexSeries = lineSeriesDataList.size() - 1;
				IdentificationLabelMarker scanLabelMarker = new IdentificationLabelMarker(chromatogramChart.getBaseChart(), indexSeries, null, scans);
				plotArea.addCustomPaintListener(scanLabelMarker);
				scanLabelMarkerMap.put(seriesId, scanLabelMarker);
			}
		}
	}

	private void addIdentifiedScansData(List<ILineSeriesData> lineSeriesDataList, List<IScan> scans, PlotSymbolType plotSymbolType, int symbolSize, Color symbolColor, String seriesId) {

		if(scans.size() > 0) {
			ILineSeriesData lineSeriesData = null;
			lineSeriesData = scanChartSupport.getLineSeriesDataPoint(scans, false, seriesId, displayType, chromatogramSelection);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
			lineSeriesSettings.setLineStyle(LineStyle.NONE);
			lineSeriesSettings.setSymbolType(plotSymbolType);
			lineSeriesSettings.setSymbolSize(symbolSize);
			lineSeriesSettings.setSymbolColor(symbolColor);
			lineSeriesDataList.add(lineSeriesData);
		}
	}

	private void addSelectedIdentifiedScanData(List<ILineSeriesData> lineSeriesDataList) {

		if(chromatogramSelection != null) {
			IScan scan = chromatogramSelection.getSelectedIdentifiedScan();
			if(scan != null) {
				String seriesId = SERIES_ID_IDENTIFIED_SCAN_SELECTED;
				Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_SCAN_IDENTIFIED));
				int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SCAN_LABEL_SYMBOL_SIZE);
				List<IScan> scans = new ArrayList<>();
				scans.add(scan);
				addIdentifiedScansData(lineSeriesDataList, scans, PlotSymbolType.CIRCLE, symbolSize, color, seriesId);
			}
		}
	}

	private void addSelectedPeakData(List<ILineSeriesData> lineSeriesDataList) {

		IPeak peak = chromatogramSelection.getSelectedPeak();
		if(peak != null) {
			/*
			 * Settings
			 */
			boolean mirrored = false;
			ILineSeriesData lineSeriesData;
			Color colorPeak = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_PEAK));
			/*
			 * Peak Marker
			 */
			int symbolSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_PEAK_LABEL_SYMBOL_SIZE);
			List<IPeak> peaks = new ArrayList<>();
			peaks.add(peak);
			addPeaks(lineSeriesDataList, peaks, PlotSymbolType.INVERTED_TRIANGLE, symbolSize, colorPeak, SERIES_ID_SELECTED_PEAK_MARKER);
			/*
			 * Peak
			 */
			int markerSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SELECTED_PEAK_MARKER_SIZE);
			PlotSymbolType symbolType = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SELECTED_PEAK_MARKER_TYPE));
			lineSeriesData = peakChartSupport.getPeak(peak, true, mirrored, colorPeak, SERIES_ID_SELECTED_PEAK_SHAPE);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
			lineSeriesSettings.setSymbolType(symbolType);
			lineSeriesSettings.setSymbolColor(colorPeak);
			lineSeriesSettings.setSymbolSize(markerSize);
			lineSeriesDataList.add(lineSeriesData);
			/*
			 * Background
			 */
			Color colorBackground = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_PEAK_BACKGROUND));
			lineSeriesData = peakChartSupport.getPeakBackground(peak, mirrored, colorBackground, SERIES_ID_SELECTED_PEAK_BACKGROUND);
			lineSeriesDataList.add(lineSeriesData);
		}
	}

	private void addSelectedScanData(List<ILineSeriesData> lineSeriesDataList) {

		IScan scan = chromatogramSelection.getSelectedScan();
		if(scan != null) {
			Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_SELECTED_SCAN));
			int markerSize = preferenceStore.getInt(PreferenceConstants.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_SIZE);
			PlotSymbolType symbolType = PlotSymbolType.valueOf(preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_SELECTED_SCAN_MARKER_TYPE));
			ILineSeriesData lineSeriesData = scanChartSupport.getLineSeriesDataPoint(scan, false, SERIES_ID_SELECTED_SCAN, displayType, chromatogramSelection);
			ILineSeriesSettings lineSeriesSettings = lineSeriesData.getLineSeriesSettings();
			lineSeriesSettings.setLineStyle(LineStyle.NONE);
			lineSeriesSettings.setSymbolType(symbolType);
			lineSeriesSettings.setSymbolSize(markerSize);
			lineSeriesSettings.setSymbolColor(color);
			lineSeriesDataList.add(lineSeriesData);
		}
	}

	private void addBaselineData(List<ILineSeriesData> lineSeriesDataList) {

		boolean showChromatogramBaseline = preferenceStore.getBoolean(PreferenceConstants.P_SHOW_CHROMATOGRAM_BASELINE);
		//
		if(chromatogramSelection != null && showChromatogramBaseline) {
			Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_CHROMATOGRAM_BASELINE));
			boolean enableBaselineArea = preferenceStore.getBoolean(PreferenceConstants.P_ENABLE_BASELINE_AREA);
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			ILineSeriesData lineSeriesData = null;
			lineSeriesData = chromatogramChartSupport.getLineSeriesDataBaseline(chromatogramSelection, SERIES_ID_BASELINE, displayType, color, false);
			lineSeriesData.getLineSeriesSettings().setEnableArea(enableBaselineArea);
			lineSeriesDataList.add(lineSeriesData);
		}
	}

	private void addLineSeriesData(List<ILineSeriesData> lineSeriesDataList) {

		/*
		 * Define the compression level.
		 */
		String compressionType = preferenceStore.getString(PreferenceConstants.P_CHROMATOGRAM_CHART_COMPRESSION_TYPE);
		int compressionToLength = chromatogramChartSupport.getCompressionLength(compressionType, lineSeriesDataList.size());
		chromatogramChart.addSeriesData(lineSeriesDataList, compressionToLength);
	}

	private void initialize(Composite parent, int style) {

		parent.setLayout(new GridLayout(1, true));
		//
		toolbarMain = createToolbarMain(parent);
		toolbars.put(TOOLBAR_INFO, createToolbarInfo(parent));
		toolbars.put(TOOLBAR_EDIT, createToolbarEdit(parent));
		toolbars.put(TOOLBAR_PEAK_TARGET_TRANSFER, createPeakTargetTransferUI(parent));
		toolbars.put(TOOLBAR_CHROMATOGRAM_ALIGNMENT, createChromatogramAlignmentUI(parent));
		toolbars.put(TOOLBAR_METHOD, createToolbarMethod(parent));
		toolbars.put(TOOLBAR_RETENTION_INDICES, retentionIndexUI = createToolbarRetentionIndexUI(parent));
		//
		SashForm chartsArea = new SashForm(parent, SWT.HORIZONTAL);
		chartsArea.setLayoutData(new GridData(GridData.FILL_BOTH));
		createChromatogramChart(chartsArea, style);
		createHeatmap(chartsArea);
		//
		comboViewerSeparationColumn.setInput(SeparationColumnFactory.getSeparationColumns());
		//
		PartSupport.setCompositeVisibility(toolbarMain, true);
		PartSupport.setCompositeVisibility(toolbars.get(TOOLBAR_INFO), false);
		PartSupport.setCompositeVisibility(toolbars.get(TOOLBAR_EDIT), false);
		PartSupport.setCompositeVisibility(toolbars.get(TOOLBAR_PEAK_TARGET_TRANSFER), false);
		PartSupport.setCompositeVisibility(toolbars.get(TOOLBAR_CHROMATOGRAM_ALIGNMENT), false);
		PartSupport.setCompositeVisibility(toolbars.get(TOOLBAR_METHOD), false);
		PartSupport.setCompositeVisibility(toolbars.get(TOOLBAR_RETENTION_INDICES), false);
		PartSupport.setCompositeVisibility(heatmapArea, false);
		//
		chromatogramActionUI.setChromatogramActionMenu(chromatogramSelection);
	}

	private Composite createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(10, false));
		//
		createToggleToolbarButton(composite, "Toggle the info toolbar.", IApplicationImage.IMAGE_INFO, TOOLBAR_INFO);
		comboViewerSeparationColumn = createComboViewerSeparationColumn(composite);
		createChromatogramReferencesUI(composite);
		createToggleToolbarButton(composite, "Toggle the edit toolbar.", IApplicationImage.IMAGE_EDIT, TOOLBAR_EDIT);
		createToggleToolbarButton(composite, "Toggle the peak targets transfer toolbar.", IApplicationImage.IMAGE_TARGETS, TOOLBAR_PEAK_TARGET_TRANSFER);
		createToggleToolbarButton(composite, "Toggle the chromatogram alignment toolbar.", IApplicationImage.IMAGE_ALIGN_CHROMATOGRAMS, TOOLBAR_CHROMATOGRAM_ALIGNMENT);
		createToggleToolbarButton(composite, "Toggle the method toolbar.", IApplicationImage.IMAGE_METHOD, TOOLBAR_METHOD);
		chromatogramActionUI = createChromatogramActionUI(composite);
		createResetButton(composite);
		createSettingsButton(composite);
		//
		return composite;
	}

	private void createChromatogramReferencesUI(Composite parent) {

		chromatogramReferencesUI = new ChromatogramReferencesUI(parent, SWT.NONE);
		chromatogramReferencesUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		chromatogramReferencesUI.setChromatogramReferencesListener(new IChromatogramReferencesListener() {

			@Override
			public void update(IChromatogramSelection chromatogramSelection) {

				updateChromatogramSelection(chromatogramSelection);
				fireUpdate();
			}
		});
	}

	private Composite createToolbarInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelChromatogramInfo = new Label(composite, SWT.NONE);
		labelChromatogramInfo.setText("");
		labelChromatogramInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private RetentionIndexUI createToolbarRetentionIndexUI(Composite parent) {

		RetentionIndexUI retentionIndexUI = new RetentionIndexUI(parent, SWT.NONE);
		retentionIndexUI.setLayoutData(new GridData(GridData.FILL_BOTH));
		retentionIndexUI.setSearchVisibility(false);
		//
		return retentionIndexUI;
	}

	private Composite createToolbarMethod(Composite parent) {

		MethodSupportUI methodSupportUI = new MethodSupportUI(parent, SWT.NONE);
		methodSupportUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		methodSupportUI.setMethodListener(new IMethodListener() {

			@Override
			public void execute(IProcessMethod processMethod, IProgressMonitor monitor) {

				ProcessTypeSupport processTypeSupport = new ProcessTypeSupport();
				processTypeSupport.applyProcessor(chromatogramSelection, processMethod, monitor);
				chromatogramSelection.update(false);
			}
		});
		//
		return methodSupportUI;
	}

	private PeakTargetTransferUI createPeakTargetTransferUI(Composite parent) {

		PeakTargetTransferUI composite = new PeakTargetTransferUI(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return composite;
	}

	private ChromatogramAlignmentUI createChromatogramAlignmentUI(Composite parent) {

		ChromatogramAlignmentUI composite = new ChromatogramAlignmentUI(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return composite;
	}

	private ComboViewer createComboViewerSeparationColumn(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof ISeparationColumn) {
					ISeparationColumn separationColumn = (ISeparationColumn)element;
					return separationColumn.getName();
				}
				return null;
			}
		});
		combo.setToolTipText("Select a chromatogram column.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof ISeparationColumn && chromatogramSelection != null) {
					ISeparationColumn separationColumn = (ISeparationColumn)object;
					chromatogramSelection.getChromatogram().getSeparationColumnIndices().setSeparationColumn(separationColumn);
					updateLabel();
				}
			}
		});
		//
		return comboViewer;
	}

	private Composite createToolbarEdit(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(7, false));
		//
		createToggleToolbarButton(composite, "Toggle the retention index toolbar.", IApplicationImage.IMAGE_RETENION_INDEX, TOOLBAR_RETENTION_INDICES);
		createVerticalSeparator(composite);
		createToggleChartSeriesLegendButton(composite);
		createToggleLegendMarkerButton(composite);
		createToggleRangeSelectorButton(composite);
		createVerticalSeparator(composite);
		createButtonSignalSelection(composite);
		//
		return composite;
	}

	private void createButtonSignalSelection(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Select signal");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_HEATMAP_DEFAULT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				signalSelectionHeatMap();
			}
		});
	}

	private void signalSelectionHeatMap() {

		PartSupport.setCompositeVisibility(heatmapArea, !heatmapArea.getVisible());
		heatmapUI.setChromatogramSelection(chromatogramSelection);
		if(chromatogramSelection instanceof ChromatogramSelectionWSD) {
			if(displayType.equals(DisplayType.SWC)) {
				displayType = DisplayType.TIC;
				update();
			} else if(displayType.equals(DisplayType.TIC)) {
				displayType = DisplayType.SWC;
				update();
			}
		}
	}

	private void createVerticalSeparator(Composite parent) {

		Label label = new Label(parent, SWT.SEPARATOR | SWT.VERTICAL);
		GridData gridData = new GridData();
		gridData.heightHint = 35;
		label.setLayoutData(gridData);
	}

	private void createChromatogramChart(Composite parent, int style) {

		chromatogramChart = new ChromatogramChart(parent, style);
		chromatogramChart.setLayoutData(new GridData(GridData.FILL_BOTH));
		/*
		 * Custom Selection Handler
		 */
		BaseChart baseChart = chromatogramChart.getBaseChart();
		baseChart.addCustomRangeSelectionHandler(new ChromatogramSelectionHandler(this));
		/*
		 * Chart Settings
		 */
		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		chartSettings.setCreateMenu(true);
		chartSettings.setEnableRangeSelector(true);
		chartSettings.setRangeSelectorDefaultAxisX(1); // Minutes
		chartSettings.setRangeSelectorDefaultAxisY(1); // Relative Abundance
		chartSettings.setShowRangeSelectorInitially(false);
		IChartMenuEntry chartMenuEntry = chartSettings.getChartMenuEntry(ResetChartHandler.NAME);
		chartSettings.removeMenuEntry(chartMenuEntry);
		chartSettings.addMenuEntry(new ChromatogramResetHandler(this));
		chartSettings.addHandledEventProcessor(new ScanSelectionHandler(this));
		chartSettings.addHandledEventProcessor(new PeakSelectionHandler(this));
		chartSettings.addHandledEventProcessor(new ScanSelectionArrowKeyHandler(this, SWT.ARROW_LEFT));
		chartSettings.addHandledEventProcessor(new ScanSelectionArrowKeyHandler(this, SWT.ARROW_RIGHT));
		chartSettings.addHandledEventProcessor(new ChromatogramMoveArrowKeyHandler(this, SWT.ARROW_LEFT));
		chartSettings.addHandledEventProcessor(new ChromatogramMoveArrowKeyHandler(this, SWT.ARROW_RIGHT));
		chartSettings.addHandledEventProcessor(new ChromatogramMoveArrowKeyHandler(this, SWT.ARROW_UP));
		chartSettings.addHandledEventProcessor(new ChromatogramMoveArrowKeyHandler(this, SWT.ARROW_DOWN));
		//
		chromatogramChart.applySettings(chartSettings);
	}

	private void createHeatmap(Composite composite) {

		heatmapArea = new Composite(composite, SWT.None);
		heatmapArea.setLayout(new FillLayout());
		heatmapUI = new HeatmapUI(heatmapArea);
	}

	private Button createToggleToolbarButton(Composite parent, String tooltip, String image, String toolbar) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText(tooltip);
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(toolbars.containsKey(toolbar)) {
					boolean visible = PartSupport.toggleCompositeVisibility(toolbars.get(toolbar));
					if(visible) {
						button.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImage.SIZE_16x16));
					} else {
						button.setImage(ApplicationImageFactory.getInstance().getImage(image, IApplicationImage.SIZE_16x16));
					}
				}
			}
		});
		//
		return button;
	}

	private void createToggleChartSeriesLegendButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart series legend.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TAG, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chromatogramChart.toggleSeriesLegendVisibility();
			}
		});
	}

	private void createToggleLegendMarkerButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart legend marker.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHART_LEGEND_MARKER, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chromatogramChart.togglePositionLegendVisibility();
				chromatogramChart.redraw();
			}
		});
	}

	private void createToggleRangeSelectorButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle the chart range selector.");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CHART_RANGE_SELECTOR, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				chromatogramChart.toggleRangeSelectorVisibility();
			}
		});
	}

	private ChromatogramActionUI createChromatogramActionUI(Composite parent) {

		return new ChromatogramActionUI(parent, SWT.NONE);
	}

	private void createResetButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Reset the chromatogram");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_RESET, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				reset(true);
			}
		});
	}

	private void createSettingsButton(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePageChromatogram = new PreferencePageChromatogram();
				preferencePageChromatogram.setTitle("Chromatogram Settings");
				IPreferencePage preferencePageChromatogramAxes = new PreferencePageChromatogramAxes();
				preferencePageChromatogramAxes.setTitle("Chromatogram Axes");
				IPreferencePage preferencePageChromatogramPeaks = new PreferencePageChromatogramPeaks();
				preferencePageChromatogramPeaks.setTitle("Chromatogram Peaks");
				IPreferencePage preferencePageChromatogramScans = new PreferencePageChromatogramScans();
				preferencePageChromatogramScans.setTitle("Chromatogram Scans");
				IPreferencePage preferencePageSWT = new PreferencePageSWT();
				preferencePageSWT.setTitle("Settings (SWT)");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageChromatogram));
				preferenceManager.addToRoot(new PreferenceNode("2", preferencePageChromatogramAxes));
				preferenceManager.addToRoot(new PreferenceNode("3", preferencePageChromatogramPeaks));
				preferenceManager.addToRoot(new PreferenceNode("4", preferencePageChromatogramScans));
				preferenceManager.addToRoot(new PreferenceNode("5", preferencePageSWT));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(e.display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					try {
						applySettings();
					} catch(Exception e1) {
						MessageDialog.openError(e.display.getActiveShell(), "Settings", "Something has gone wrong to apply the settings.");
					}
				}
			}
		});
	}

	private void applySettings() {

		adjustAxisSettings();
		updateChromatogram();
	}

	private void reset(boolean resetRange) {

		updateChromatogram();
		if(resetRange && chromatogramSelection != null) {
			chromatogramSelection.reset(true);
		}
	}

	private void updateLabel() {

		if(chromatogramSelection != null) {
			labelChromatogramInfo.setText(chromatogramDataSupport.getChromatogramLabelExtended(chromatogramSelection.getChromatogram()));
		} else {
			labelChromatogramInfo.setText("");
		}
	}

	private void deleteScanNumberSecondaryAxisX() {

		IChartSettings chartSettings = chromatogramChart.getChartSettings();
		List<ISecondaryAxisSettings> secondaryAxisSettings = chartSettings.getSecondaryAxisSettingsListX();
		//
		ISecondaryAxisSettings secondaryAxisScanNumber = null;
		exitloop:
		for(ISecondaryAxisSettings secondaryAxis : secondaryAxisSettings) {
			if(secondaryAxis.getLabel().equals(LABEL_SCAN_NUMBER)) {
				secondaryAxisScanNumber = secondaryAxis;
				break exitloop;
			}
		}
		//
		if(secondaryAxisScanNumber != null) {
			secondaryAxisSettings.remove(secondaryAxisScanNumber);
		}
		//
		chromatogramChart.applySettings(chartSettings);
	}

	private void adjustChromatogramSelectionRange() {

		if(chromatogramSelection != null) {
			chromatogramChart.setRange(IExtendedChart.X_AXIS, chromatogramSelection.getStartRetentionTime(), chromatogramSelection.getStopRetentionTime());
			chromatogramChart.setRange(IExtendedChart.Y_AXIS, chromatogramSelection.getStartAbundance(), chromatogramSelection.getStopAbundance());
		}
	}

	private void setSeparationColumnSelection() {

		if(chromatogramSelection != null) {
			ISeparationColumn separationColumn = chromatogramSelection.getChromatogram().getSeparationColumnIndices().getSeparationColumn();
			if(separationColumn != null) {
				String name = separationColumn.getName();
				int index = -1;
				exitloop:
				for(String item : comboViewerSeparationColumn.getCombo().getItems()) {
					index++;
					if(item.equals(name)) {
						break exitloop;
					}
				}
				//
				if(index >= 0) {
					comboViewerSeparationColumn.getCombo().select(index);
				}
			}
		}
	}

	private void adjustAxisSettings() {

		chromatogramChart.modifyAxes(true);
		/*
		 * Scan Axis
		 */
		if(chromatogramSelection != null) {
			IChromatogram chromatogram = chromatogramSelection.getChromatogram();
			if(chromatogram != null) {
				IChartSettings chartSettings = chromatogramChart.getChartSettings();
				ISecondaryAxisSettings axisSettings = chartSupport.getSecondaryAxisSettingsX(TITLE_X_AXIS_SCANS, chartSettings);
				//
				if(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_SCANS)) {
					if(axisSettings == null) {
						try {
							int scanDelay = chromatogram.getScanDelay();
							int scanInterval = chromatogram.getScanInterval();
							ISecondaryAxisSettings secondaryAxisSettingsX = new SecondaryAxisSettings(TITLE_X_AXIS_SCANS, new MillisecondsToScanNumberConverter(scanDelay, scanInterval));
							secondaryAxisSettingsX.setTitleVisible(preferenceStore.getBoolean(PreferenceConstants.P_SHOW_X_AXIS_TITLE_SCANS));
							setScanAxisSettings(secondaryAxisSettingsX);
							chartSettings.getSecondaryAxisSettingsListX().add(secondaryAxisSettingsX);
						} catch(Exception e) {
							logger.warn(e);
						}
					} else {
						setScanAxisSettings(axisSettings);
					}
				} else {
					/*
					 * Remove
					 */
					if(axisSettings != null) {
						chartSettings.getSecondaryAxisSettingsListX().remove(axisSettings);
					}
				}
				chromatogramChart.applySettings(chartSettings);
			}
		}
	}

	private void setScanAxisSettings(IAxisSettings axisSettings) {

		ChartSupport chartSupport = new ChartSupport(Activator.getDefault().getPreferenceStore());
		Position position = Position.valueOf(preferenceStore.getString(PreferenceConstants.P_POSITION_X_AXIS_SCANS));
		Color color = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_COLOR_X_AXIS_SCANS));
		LineStyle gridLineStyle = LineStyle.valueOf(preferenceStore.getString(PreferenceConstants.P_GRIDLINE_STYLE_X_AXIS_SCANS));
		Color gridColor = Colors.getColor(preferenceStore.getString(PreferenceConstants.P_GRIDLINE_COLOR_X_AXIS_SCANS));
		chartSupport.setAxisSettings(axisSettings, position, "0", color, gridLineStyle, gridColor);
	}

	private void updateToolbar(Composite composite, IChromatogramSelection chromatogramSelection) {

		if(composite instanceof IChromatogramSelectionUpdateListener) {
			IChromatogramSelectionUpdateListener listener = (IChromatogramSelectionUpdateListener)composite;
			listener.update(chromatogramSelection);
		}
	}
}
