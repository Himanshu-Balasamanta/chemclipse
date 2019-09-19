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
package org.eclipse.chemclipse.ux.extension.ui.swt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.definitions.TileDefinition;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class TaskTileContainer {

	private static final Color DEFAULT_COLOR_INACTIVE = Colors.getColor(74, 142, 142);
	private static final Color DEFAULT_COLOR_ACTIVE = Colors.getColor(5, 100, 100);
	private final List<TaskTile> tiles = new ArrayList<TaskTile>();
	private final Composite container;
	private final Supplier<IEclipseContext> contextSupplier;
	private final MouseMoveListener tileMouseMoveListener = mouseMove -> {
		for(TaskTile tile : tiles) {
			if(tile == mouseMove.widget) {
				tile.setActive();
			} else {
				tile.setInactive();
			}
		}
	};
	private Color[] colors;

	public TaskTileContainer(Composite parent, int columns, Supplier<IEclipseContext> contextSupplier) {
		this(parent, columns, contextSupplier, new Color[]{DEFAULT_COLOR_ACTIVE, DEFAULT_COLOR_INACTIVE});
	}

	public TaskTileContainer(Composite parent, int columns, Supplier<IEclipseContext> contextSupplier, Color[] colors) {
		this.contextSupplier = contextSupplier;
		this.colors = colors;
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(columns, false));
		container.setBackgroundMode(SWT.INHERIT_FORCE);
		MouseTrackAdapter trackAdapter = new MouseTrackAdapter() {

			@Override
			public void mouseExit(MouseEvent me) {

				for(TaskTile tile : tiles) {
					tile.setInactive();
				}
			}
		};
		container.addMouseTrackListener(trackAdapter);
		parent.addMouseTrackListener(trackAdapter);
	}

	public TaskTile addTaskTile(TileDefinition definition) {

		GridData gridData = new GridData(GridData.FILL_BOTH);
		TaskTile taskTile = new TaskTile(container, TaskTile.HIGHLIGHT, definition, this::executeHandler, colors);
		taskTile.setLayoutData(gridData);
		taskTile.addMouseMoveListener(tileMouseMoveListener);
		tiles.add(taskTile);
		container.layout();
		return taskTile;
	}

	private void executeHandler(TileDefinition tileDefinition) {

		ContextInjectionFactory.invoke(tileDefinition, Execute.class, contextSupplier.get());
	}

	public void removeTaskTile(TaskTile tile) {

		tile.removeMouseMoveListener(tileMouseMoveListener);
		tiles.remove(tile);
		tile.dispose();
		container.layout();
	}

	public List<TaskTile> getTiles() {

		return Collections.unmodifiableList(tiles);
	}
}
