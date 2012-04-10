/*******************************************************************************
 * Copyright (c) 2008, 2012 Phillip Beauvoir
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Phillip Beauvoir
 *******************************************************************************/
package com.dadabeatnik.codemap.ui.common;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;

/**
 * The abstract TableColumnSorter extends the ViewerSorter. It allows the
 * sorting of objects within all columns of a TableView. These objects should
 * implement the Comparable interface and all objects should be of the same class.
 * 
 * @author Phillipus
 */
public abstract class TableColumnSorter extends ViewerSorter {

	@Override
	public boolean isSorterProperty(Object element, String property) {
		return true;
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		if (e1.getClass() == e2.getClass()) {
			TableColumn sortColumn = ((TableViewer) viewer).getTable().getSortColumn();
			if (sortColumn == null)
				return 0;
			int index = ((Integer) sortColumn.getData("index")).intValue(); //$NON-NLS-1$

			Object o1 = getValue(e1, index);
			Object o2 = getValue(e2, index);
			return _compare(o1, o2) * (((TableViewer) viewer).getTable().getSortDirection() == SWT.UP ? 1 : -1);
		}
		return 0;
	}

	protected Object getValue(Object o, int index) {
		return o;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	protected int _compare(Object o1, Object o2) {
		if (o1 instanceof Comparable) {
			return ((Comparable) o1).compareTo(o2); // unchecked cast
		}
		return 0;
	}
}