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
package com.dadabeatnik.codemap.ui.editors;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.dadabeatnik.codemap.model.CodeMapModel;
import com.dadabeatnik.codemap.model.EntryModel;
import com.dadabeatnik.codemap.ui.ImageFactory;


/**
 * Table Viewer for an Entry
 * 
 * @author Phillip Beauvoir
 */
public class EntryTableViewer extends TableViewer {

    /**
     * The Column Names
     */
    private static String[] columnNames = {
            "",
            "Entry"
    };

    
    
    public EntryTableViewer(CodeMapEditor editor, Composite parent, int style) {
        super(parent, style | SWT.FULL_SELECTION | SWT.MULTI);
        
        setColumns();
        
        setContentProvider(new EntryTableViewerContentProvider());
        setLabelProvider(new EntryTableViewerLabelProvider());
        
        new EntryTableViewerDragDropHandler(editor, this);
    }
    
    /**
     * Set up the tree columns
     */
    private void setColumns() {
        Table table = getTable();
        
        table.setHeaderVisible(true);
        //table.setLinesVisible(true);
        
        // Use layout from parent container
        TableColumnLayout layout = (TableColumnLayout)getControl().getParent().getLayout();

        TableColumn[] columns = new TableColumn[columnNames.length];
        
        for(int i = 0; i < columnNames.length; i++) {
            columns[i] = new TableColumn(table, SWT.NONE);
            columns[i].setText(columnNames[i]);
            
            // Needed for sorting
            //columns[i].setData("index", new Integer(i)); //$NON-NLS-1$
            //columns[i].addListener(SWT.Selection, sortListener);
        }
        
        layout.setColumnData(columns[0], new ColumnWeightData(1, 32, false));
        layout.setColumnData(columns[1], new ColumnWeightData(50, false));
        //layout.setColumnData(columns[2], new ColumnWeightData(50, true));
        
        //setSorter(new Sorter());

        // Column names are properties
        setColumnProperties(columnNames);
    }

    
    // =============================================================================================
    //
    //                                   CONTENT PROVIDER
    //
    // =============================================================================================

    
    private class EntryTableViewerContentProvider implements IStructuredContentProvider {
        
        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        }
        
        public void dispose() {
        }
        
        public Object[] getElements(Object parent) {
            if(parent instanceof CodeMapModel) {
                return ((CodeMapModel)parent).getEntries().toArray();
            }
            return new Object[0];
        }
    }

    // =============================================================================================
    //
    //                                   LABEL PROVIDER
    //
    // =============================================================================================

    private class EntryTableViewerLabelProvider
    extends LabelProvider
    implements ITableLabelProvider
    {
        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
         */
        public Image getColumnImage(Object element, int columnIndex) {
            if(columnIndex == 0) {
                return ImageFactory.getFileIcon(element);
            }
            
            return null;
        }
        
        /* (non-Javadoc)
         * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
         */
        public String getColumnText(Object element, int columnIndex) {
            switch(columnIndex) {
               case 1:
                    return ((EntryModel)element).getTitle();

               default:
                    return null;
            }
        }
    }
}
