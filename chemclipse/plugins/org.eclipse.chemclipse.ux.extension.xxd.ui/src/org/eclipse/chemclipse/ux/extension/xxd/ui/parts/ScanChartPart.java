/*******************************************************************************
 * Copyright (c) 2017, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - improve user feedback for unsaved changes
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EnhancedUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedScanChartUI;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

public class ScanChartPart extends EnhancedUpdateSupport implements IUpdateSupport {

	private static final String KEY_LABEL = ScanChartPart.class.getName() + ".label";
	private ExtendedScanChartUI extendedScanChartUI;
	private final IEventBroker eventBroker;

	@Inject
	public ScanChartPart(Composite parent, MPart part, IEventBroker eventBroker) {
		super(parent, Activator.getDefault().getDataUpdateSupport(), IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, part);
		this.eventBroker = eventBroker;
	}

	@Override
	public void createControl(Composite parent) {

		// as only EditorParts have a visual dirtyflag we emulate the "dirty" state here for our part
		extendedScanChartUI = new ExtendedScanChartUI(parent, eventBroker, new MDirtyable() {

			private boolean dirty;

			@Override
			public void setDirty(boolean value) {

				if(this.dirty != value) {
					MPart part = getPart();
					if(value) {
						String label = part.getLabel();
						part.getTransientData().put(KEY_LABEL, label);
						part.setLabel("*" + label);
					} else {
						String label = (String)part.getTransientData().get(KEY_LABEL);
						if(label != null) {
							part.setLabel(label);
						}
					}
					this.dirty = value;
				}
			}

			@Override
			public boolean isDirty() {

				return dirty;
			}
		});
	}

	@Override
	public void updateSelection(List<Object> objects, String topic) {

		/*
		 * 0 => because only one property was used to register the event.
		 */
		if(objects.size() == 1) {
			if(isScanOrPeakTopic(topic)) {
				Object object = objects.get(0);
				IScan scan = null;
				if(object instanceof IScan) {
					scan = (IScan)object;
				} else if(object instanceof IPeak) {
					IPeak peak = (IPeak)object;
					scan = peak.getPeakModel().getPeakMaximum();
				}
				extendedScanChartUI.update(scan);
			}
		}
	}

	private boolean isScanOrPeakTopic(String topic) {

		if(topic.equals(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION)) {
			return true;
		} else if(topic.equals(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION)) {
			return true;
		}
		return false;
	}
}
