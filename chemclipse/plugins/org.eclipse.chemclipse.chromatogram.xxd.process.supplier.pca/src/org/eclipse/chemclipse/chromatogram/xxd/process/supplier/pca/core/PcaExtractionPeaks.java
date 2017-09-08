/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IRetentionTime;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.SampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeaks;
import org.eclipse.chemclipse.msd.converter.peak.PeakConverterMSD;
import org.eclipse.chemclipse.msd.converter.processing.peak.IPeakImportConverterProcessingInfo;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.core.runtime.IProgressMonitor;

public class PcaExtractionPeaks implements IDataExtraction {

	private List<IDataInputEntry> dataInputEntriesAll;
	private int extractionType;
	private int retentionTimeWindow;

	public PcaExtractionPeaks(List<IDataInputEntry> dataInputEntriesAll, int retentionTimeWindow, int extractionType) {
		this.retentionTimeWindow = retentionTimeWindow;
		this.dataInputEntriesAll = dataInputEntriesAll;
		this.extractionType = extractionType;
	}

	/**
	 * Calculates the condensed retention time.
	 *
	 * @return int
	 */
	private int calculateCondensedRetentionTime(List<Integer> retentionTimes) {

		int retentionTimeCondensed = 0;
		if(retentionTimes != null) {
			int size = retentionTimes.size();
			if(size > 0) {
				int retentionTimeSum = 0;
				for(Integer rt : retentionTimes) {
					retentionTimeSum += rt;
				}
				retentionTimeCondensed = retentionTimeSum / size;
			}
		}
		return retentionTimeCondensed;
	}

	private List<Integer> calculateCondensedRetentionTimes(Map<String, SortedMap<Integer, IPeak>> extractPeaks) {

		Set<Integer> rententionTimes = new TreeSet<>();
		for(Map.Entry<String, SortedMap<Integer, IPeak>> extractPeak : extractPeaks.entrySet()) {
			for(Integer retentionTime : extractPeak.getValue().keySet()) {
				rententionTimes.add(retentionTime);
			}
		}
		return new ArrayList<>(rententionTimes);
	}

	/**
	 * Calculates a list of condensed retention times. The condense factor is given by the retention time window.
	 *
	 * @param collectedRetentionTimes
	 * @return List<Integer>
	 */
	private List<Integer> calculateCondensedRetentionTimes(SortedSet<Integer> collectedRetentionTimes, int retentionTimeWindow) {

		List<Integer> extractedRetentionTimes = new ArrayList<Integer>();
		//
		int retentionTime;
		int retentionTimeMarker;
		List<Integer> retentionTimes = null;
		//
		Iterator<Integer> iterator = collectedRetentionTimes.iterator();
		if(iterator.hasNext()) {
			/*
			 * Initialize: Get the first marker.
			 */
			retentionTime = iterator.next();
			retentionTimeMarker = retentionTime + retentionTimeWindow;
			retentionTimes = new ArrayList<Integer>();
			retentionTimes.add(retentionTime);
			/*
			 * Parse all subsequent retention times.
			 */
			while(iterator.hasNext()) {
				retentionTime = iterator.next();
				/*
				 * Check the next retention time.
				 */
				if(retentionTime > retentionTimeMarker) {
					/*
					 * Condense the retention time list.
					 */
					int retentionTimeCondensed = calculateCondensedRetentionTime(retentionTimes);
					if(retentionTimeCondensed > 0) {
						extractedRetentionTimes.add(retentionTimeCondensed);
					}
					/*
					 * Initialize the next chunk.
					 */
					retentionTimeMarker = retentionTime + retentionTimeWindow;
					retentionTimes = new ArrayList<Integer>();
					retentionTimes.add(retentionTime);
				} else {
					/*
					 * Collect the retention time.
					 */
					retentionTimes.add(retentionTime);
				}
			}
		}
		/*
		 * Add the last condensed retention time.
		 */
		int retentionTimeCondensed = calculateCondensedRetentionTime(retentionTimes);
		if(retentionTimeCondensed > 0) {
			extractedRetentionTimes.add(retentionTimeCondensed);
		}
		return extractedRetentionTimes;
	}

	/**
	 * Collects all available retention times.
	 *
	 * @param peakMap
	 * @return SortedSet<Integer>
	 */
	private SortedSet<Integer> collectRetentionTimes(Map<String, IPeaks> peakMap) {

		SortedSet<Integer> collectedRetentionTimes = new TreeSet<Integer>();
		for(Map.Entry<String, IPeaks> peaksEntry : peakMap.entrySet()) {
			IPeaks peaks = peaksEntry.getValue();
			for(IPeak peak : peaks.getPeaks()) {
				if(peak instanceof IPeakMSD) {
					IPeakMSD peakMSD = (IPeakMSD)peak;
					int retentionTime = peakMSD.getPeakModel().getRetentionTimeAtPeakMaximum();
					collectedRetentionTimes.add(retentionTime);
				}
			}
		}
		return collectedRetentionTimes;
	}

	private Map<String, SortedMap<Integer, IPeak>> exctractPcaPeakMap(Map<String, IPeaks> peakMap, int retentionTimeWindow) {

		Map<String, TreeMap<Integer, IPeak>> pcaPeakRetentionTime = new HashMap<>();
		Map<String, SortedMap<Integer, IPeak>> pcaPeakCondenseRetentionTime = new HashMap<>();
		int totalCountPeak = 0;
		for(Map.Entry<String, IPeaks> peakEnry : peakMap.entrySet()) {
			String name = peakEnry.getKey();
			IPeaks peaks = peakEnry.getValue();
			TreeMap<Integer, IPeak> peakTree = new TreeMap<>();
			for(IPeak peak : peaks.getPeaks()) {
				int retentionTime = peak.getPeakModel().getRetentionTimeAtPeakMaximum();
				peakTree.put(retentionTime, peak);
			}
			totalCountPeak += peakTree.size();
			pcaPeakRetentionTime.put(name, peakTree);
		}
		while(totalCountPeak != 0) {
			SortedMap<Double, Collection<Integer>> weightRetentionTime = setWeightRetentionTimes(pcaPeakRetentionTime, retentionTimeWindow);
			Iterator<Map.Entry<Double, Collection<Integer>>> it = weightRetentionTime.entrySet().iterator();
			TreeSet<Integer> condenseRetentionTimes = new TreeSet<>();
			while(it.hasNext() && (totalCountPeak != 0)) {
				Map.Entry<Double, Collection<Integer>> entry = it.next();
				Collection<Integer> retentionTimesMax = entry.getValue();
				for(Integer retentionTimeMax : retentionTimesMax) {
					Integer closestCondenseRetentionTime = getClosestCondensedRetentionTime(condenseRetentionTimes, retentionTimeMax);
					if(closestCondenseRetentionTime != null) {
						if(Math.abs(closestCondenseRetentionTime - retentionTimeMax) < (retentionTimeWindow)) {
							continue;
						}
					}
					condenseRetentionTimes.add(retentionTimeMax);
					for(Map.Entry<String, TreeMap<Integer, IPeak>> pcaPeakRetetntionTime : pcaPeakRetentionTime.entrySet()) {
						TreeMap<Integer, IPeak> peakTree = pcaPeakRetetntionTime.getValue();
						String name = pcaPeakRetetntionTime.getKey();
						IPeak closestPeak = getClosestPeak(peakTree, retentionTimeMax);
						if(closestPeak != null) {
							int peakRetentionTime = closestPeak.getPeakModel().getRetentionTimeAtPeakMaximum();
							int dist = Math.abs(retentionTimeMax - peakRetentionTime);
							if(dist <= retentionTimeWindow) {
								totalCountPeak--;
								peakTree.remove(peakRetentionTime);
								SortedMap<Integer, IPeak> extractPeaks = pcaPeakCondenseRetentionTime.get(name);
								if(extractPeaks == null) {
									extractPeaks = new TreeMap<>();
									extractPeaks.put(retentionTimeMax, closestPeak);
									pcaPeakCondenseRetentionTime.put(name, extractPeaks);
								} else {
									extractPeaks.put(retentionTimeMax, closestPeak);
								}
							}
						}
					}
				}
			}
		}
		return pcaPeakCondenseRetentionTime;
	}

	/**
	 * Returns an array of area values of the peaks.
	 *
	 * @param peaks
	 * @param extractedRetentionTimes
	 * @param retentionTimeWindow
	 * @return double[]
	 */
	private List<ISampleData> extractPcaPeakIntensityValues(IPeaks peaks, List<Integer> extractedRetentionTimes, int retentionTimeWindow) {

		List<ISampleData> sampleDataList = new ArrayList<>();
		int retentionTimeDelta = retentionTimeWindow / 2;
		/*
		 * Try to get an intensity value for each extracted retention time.
		 * If there is no peak, the value will be 0.
		 */
		Set<IPeak> setPeaks = new HashSet<>(peaks.getPeaks());
		for(int extractedRetentionTime : extractedRetentionTimes) {
			double abundance = 0;
			int leftRetentionTimeBound = extractedRetentionTime - retentionTimeDelta;
			int rightRetentionTimeBound = extractedRetentionTime + retentionTimeDelta;
			/*
			 * Check each peak.
			 */
			Iterator<IPeak> it = setPeaks.iterator();
			Set<IPeak> peaksAtRetentionTime = new HashSet<>();
			while(it.hasNext()) {
				/*
				 * Try to get the retention time.
				 */
				IPeak peak = it.next();
				if(peak instanceof IPeakMSD) {
					/*
					 * The retention time must be in the retention time window.
					 */
					IPeakMSD peakMSD = (IPeakMSD)peak;
					int retentionTime = peakMSD.getPeakModel().getRetentionTimeAtPeakMaximum();
					if(retentionTime >= leftRetentionTimeBound && retentionTime <= rightRetentionTimeBound) {
						abundance += peakMSD.getIntegratedArea();
						it.remove();
						peaksAtRetentionTime.add(peakMSD);
					}
				}
			}
			/*
			 * Store the extracted abundance.
			 */
			ISampleData sampleData;
			if(Double.compare(0, abundance) != 0) {
				sampleData = new SampleData(abundance);
				sampleData.setPeaks(setPeaks);
			} else {
				sampleData = new SampleData();
			}
			sampleDataList.add(sampleData);
		}
		return sampleDataList;
	}

	/**
	 * Extracts a PCA peak map.
	 *
	 * @param peakMap
	 * @param extractedRetentionTimes
	 * @param retentionTimeWindow
	 * @return Map<String, double[]>
	 */
	private Map<String, List<ISampleData>> extractPcaPeakMap(Map<String, IPeaks> peakMap, List<Integer> extractedRetentionTimes, int retentionTimeWindow) {

		Map<String, List<ISampleData>> pcaPeakMap = new HashMap<>();
		for(Map.Entry<String, IPeaks> peaksEntry : peakMap.entrySet()) {
			String name = peaksEntry.getKey();
			IPeaks peaks = peaksEntry.getValue();
			List<ISampleData> sampleData = extractPcaPeakIntensityValues(peaks, extractedRetentionTimes, retentionTimeWindow);
			pcaPeakMap.put(name, sampleData);
		}
		return pcaPeakMap;
	}

	private void extractPeakData(ISamples samples, int retentionTimeWindow, IProgressMonitor monitor) {

		/*
		 * Extract data
		 */
		monitor.subTask("Extract peak values");
		Map<String, IPeaks> peakMap = extractPeaks(dataInputEntriesAll, monitor);
		monitor.subTask("Prepare peak values");
		Map<String, SortedMap<Integer, IPeak>> extractPeaks = exctractPcaPeakMap(peakMap, retentionTimeWindow);
		List<Integer> extractedRetentionTimes = calculateCondensedRetentionTimes(extractPeaks);
		samples.getExtractedRetentionTimes().addAll(IRetentionTime.create(extractedRetentionTimes));
		setExtractData(extractPeaks, samples);
	}

	private void extractPeakDataCumulation(ISamples samples, int retentionTimeWindow, IProgressMonitor monitor) {

		/*
		 * Extract data
		 */
		monitor.subTask("Extract peak values");
		Map<String, IPeaks> peakMap = extractPeaks(dataInputEntriesAll, monitor);
		monitor.subTask("Prepare peak values");
		SortedSet<Integer> collectedRetentionTimes = collectRetentionTimes(peakMap);
		List<Integer> extractedRetentionTimes = calculateCondensedRetentionTimes(collectedRetentionTimes, retentionTimeWindow);
		Map<String, List<ISampleData>> pcaPeakMap = extractPcaPeakMap(peakMap, extractedRetentionTimes, retentionTimeWindow);
		setExtractDataList(pcaPeakMap, samples);
		samples.getExtractedRetentionTimes().addAll(IRetentionTime.create(extractedRetentionTimes));
	}

	private Map<String, IPeaks> extractPeaks(List<IDataInputEntry> peakinpitFiles, IProgressMonitor monitor) {

		Map<String, IPeaks> peakMap = new HashMap<String, IPeaks>();
		for(IDataInputEntry peakFile : peakinpitFiles) {
			try {
				/*
				 * Try to catch exceptions if wrong files have been selected.
				 */
				IPeakImportConverterProcessingInfo processingInfo = PeakConverterMSD.convert(new File(peakFile.getInputFile()), monitor);
				IPeaks peaks = processingInfo.getPeaks();
				String name = peakFile.getName();
				peakMap.put(name, peaks);
			} catch(Exception e) {
			}
		}
		return peakMap;
	}

	private Integer getClosestCondensedRetentionTime(TreeSet<Integer> condensedRetentionTimes, int retentionTime) {

		Integer peakRetentionTimeCeil = condensedRetentionTimes.ceiling(retentionTime);
		Integer peakRetentionTimeFlour = condensedRetentionTimes.floor(retentionTime);
		if(peakRetentionTimeCeil != null && peakRetentionTimeFlour != null) {
			if((peakRetentionTimeCeil - retentionTime) < (retentionTime - peakRetentionTimeFlour)) {
				return peakRetentionTimeCeil;
			} else {
				return peakRetentionTimeFlour;
			}
		} else if(peakRetentionTimeCeil != null) {
			return peakRetentionTimeCeil;
		} else if(peakRetentionTimeFlour != null) {
			return peakRetentionTimeFlour;
		}
		return null;
	}

	private IPeak getClosestPeak(TreeMap<Integer, IPeak> peakTree, int retentionTime) {

		Map.Entry<Integer, IPeak> peakRetentionTimeCeil = peakTree.ceilingEntry(retentionTime);
		Map.Entry<Integer, IPeak> peakRetentionTimeFlour = peakTree.floorEntry(retentionTime);
		if(peakRetentionTimeCeil != null && peakRetentionTimeFlour != null) {
			if((peakRetentionTimeCeil.getKey() - retentionTime) < (retentionTime - peakRetentionTimeFlour.getKey())) {
				return peakRetentionTimeCeil.getValue();
			} else {
				return peakRetentionTimeFlour.getValue();
			}
		} else if(peakRetentionTimeCeil != null) {
			return peakRetentionTimeCeil.getValue();
		} else if(peakRetentionTimeFlour != null) {
			return peakRetentionTimeFlour.getValue();
		}
		return null;
	}

	@Override
	public ISamples process(IProgressMonitor monitor) {

		/*
		 * Initialize PCA Results
		 */
		ISamples samples = new Samples(dataInputEntriesAll);
		if(!(extractionType == EXTRACT_PEAK || extractionType == EXTRACT_PEAK_CUMULATION)) {
			extractionType = EXTRACT_PEAK;
		}
		/*
		 * Extract data
		 */
		switch(extractionType) {
			case EXTRACT_PEAK:
				extractPeakData(samples, retentionTimeWindow, monitor);
				break;
			case EXTRACT_PEAK_CUMULATION:
				extractPeakDataCumulation(samples, retentionTimeWindow, monitor);
				break;
		}
		/*
		 * create Groups
		 */
		samples.createGroups();
		return samples;
	}

	private void setExtractData(Map<String, SortedMap<Integer, IPeak>> extractData, ISamples samples) {

		List<IRetentionTime> extractedRetentionTimes = samples.getExtractedRetentionTimes();
		for(ISample sample : samples.getSampleList()) {
			Iterator<IRetentionTime> it = extractedRetentionTimes.iterator();
			SortedMap<Integer, IPeak> extractPeak = extractData.get(sample.getName());
			while(it.hasNext()) {
				int retentionTime = it.next().getRetentionTime();
				IPeak peak = extractPeak.get(retentionTime);
				if(peak != null) {
					ISampleData sampleData = new SampleData(peak.getIntegratedArea());
					Set<IPeak> peaks = new HashSet<>(1);
					peaks.add(peak);
					sampleData.setPeaks(peaks);
					sample.getSampleData().add(sampleData);
				} else {
					ISampleData sampleData = new SampleData();
					sample.getSampleData().add(sampleData);
				}
			}
		}
	}

	private void setExtractDataList(Map<String, List<ISampleData>> extractData, ISamples samples) {

		for(ISample sample : samples.getSampleList()) {
			String name = sample.getName();
			List<ISampleData> sampleData = extractData.get(name);
			sample.getSampleData().addAll(sampleData);
		}
	}

	private SortedMap<Double, Collection<Integer>> setWeightRetentionTimes(Map<String, TreeMap<Integer, IPeak>> pcaPeakRetetntionTimeMap, int retentionTimeWindow) {

		Map<Integer, Double> peakSum = new HashMap<>();
		int step = 1;
		if(retentionTimeWindow > 400) {
			step = retentionTimeWindow / 400;
			retentionTimeWindow = (retentionTimeWindow / step / 2) * step * 2;
		}
		for(TreeMap<Integer, IPeak> peaks : pcaPeakRetetntionTimeMap.values()) {
			for(Integer retentionTime : peaks.keySet()) {
				retentionTime = (retentionTime / step) * step;
				for(int i = retentionTime - retentionTimeWindow / 2; i <= retentionTime + retentionTimeWindow / 2; i += step) {
					int dis = Math.abs(i - retentionTime);
					double value = 1.0 / ((dis + 1) * (dis + 1));
					Double actualValue = peakSum.get(i);
					if(actualValue == null) {
						peakSum.put(i, value);
					} else {
						peakSum.put(i, value + actualValue);
					}
				}
			}
		}
		SortedMap<Double, Collection<Integer>> sortedWeightRetentionTimes = new TreeMap<>((o1, o2) -> -Double.compare(o1, o2));
		peakSum.forEach((k, v) -> {
			if(!(v < 1)) {
				Collection<Integer> retentionTimes = sortedWeightRetentionTimes.get(v);
				if(retentionTimes == null) {
					retentionTimes = new LinkedList<>();
					retentionTimes.add(k);
					sortedWeightRetentionTimes.put(v, retentionTimes);
				} else {
					retentionTimes.add(k);
				}
			}
		});
		return sortedWeightRetentionTimes;
	}
}
