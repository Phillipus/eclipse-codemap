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

import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;

import com.dadabeatnik.codemap.model.EntryModel;
import com.dadabeatnik.codemap.model.FileModel;
import com.dadabeatnik.codemap.ui.CodeMapActionFactory;
import com.dadabeatnik.codemap.ui.ImageFactory;
import com.dadabeatnik.codemap.ui.common.TableColumnSorter;


/**
 * Table Viewer for Files
 * 
 * @author Phillip Beauvoir
 */
public class FileTableViewer extends TableViewer {
    
    private CodeMapEditor fEditor;
    
    private IAction fActionDelete;
    private IAction fActionSelectAll;
    
    /**
     * The Column Names
     */
    private static String[] columnNames = {
            "",
            "File",
            "Path"
    };
    
    private Listener sortListener = new Listener() {
        public void handleEvent(Event e) {
            TableColumn sortColumn = getTable().getSortColumn();
            TableColumn currentColumn = (TableColumn)e.widget;
            int direction = getTable().getSortDirection();

            if(sortColumn == currentColumn) {
                direction = (direction == SWT.UP ? SWT.DOWN : SWT.UP);
            }
            else {
                getTable().setSortColumn(currentColumn);
                direction = SWT.UP;
            }
            getTable().setSortDirection(direction);
            refresh(false);
        }
    };

    public FileTableViewer(CodeMapEditor editor, Composite parent, int style) {
        super(parent, style | SWT.FULL_SELECTION | SWT.MULTI);
        
        fEditor = editor;
        
        setColumns();
        
        setContentProvider(new FileTableViewerContentProvider());
        setLabelProvider(new FileTableViewerLabelProvider());
        
        makeActions();
        hookContextMenu();
        registerGlobalActions();
        
        addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                // Update actions
                updateActions(event.getSelection());
            }
        });
    }

    private void makeActions() {
        // Delete
        fActionDelete = new CodeMapActionFactory.DeleteAction() {
            @Override
            public void run() {
                doDeleteAction();
            }
        };
        fActionDelete.setEnabled(false);
        
        // Select All
        fActionSelectAll = new CodeMapActionFactory.SelectAllAction() {
            @Override
            public void run() {
                getTable().selectAll();
                updateActions(getSelection());
            }
        };
    }
    
    /**
     * Hook into a right-click menu
     */
    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager("#CodeMapPopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                fillContextMenu(manager);
            }
        });
        
        Menu menu = menuMgr.createContextMenu(getControl());
        getControl().setMenu(menu);
        //fEditor.getSite().registerContextMenu(menuMgr, this); // NO!!!!! Otherwise you get the full Eclipse context menu items
    }
    
    private void fillContextMenu(IMenuManager manager) {
        boolean isEmpty = getSelection().isEmpty();
        
        if(!isEmpty) {
            manager.add(new Separator(IWorkbenchActionConstants.EDIT_START));
            manager.add(fActionDelete);
        }
        manager.add(new Separator(IWorkbenchActionConstants.EDIT_END));

        manager.add(fActionSelectAll);

        // Other plug-ins can contribute their actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void updateActions(ISelection selection) {
        boolean isEmpty = selection.isEmpty();
        
        //Object obj = ((IStructuredSelection)selection).getFirstElement();

        fActionDelete.setEnabled(!isEmpty);
    }

    /**
     * Register Global Actions on focus events
     */
    private void registerGlobalActions() {
        final IActionBars bars = fEditor.getEditorSite().getActionBars();
        
        getControl().addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                register(true);
            }
            
            public void focusLost(FocusEvent e) {
                register(false);
            }
            
            // Only de-register specific actions (not undo/redo!)
            private void register(boolean hasFocus) {
                bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), hasFocus ? fActionDelete : null);
               // bars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(), hasFocus ? fActionSelectAll : null);
                bars.updateActionBars();
            }
        });
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
            columns[i].setData("index", new Integer(i)); //$NON-NLS-1$
            columns[i].addListener(SWT.Selection, sortListener);
        }
        
        layout.setColumnData(columns[0], new ColumnWeightData(1, 32, true));
        layout.setColumnData(columns[1], new ColumnWeightData(50, true));
        layout.setColumnData(columns[2], new ColumnWeightData(50, true));
        
        setSorter(new Sorter());

        // Column names are properties
        setColumnProperties(columnNames);
    }
    
    /**
     * Delete selected objects
     */
    private void doDeleteAction() {
        List<?> selected = ((IStructuredSelection)getSelection()).toList();
        
        if(!askUserDeleteResources(selected)) {
            return;
        }
        
        EntryModel model = (EntryModel)getInput();
        
        for(Object o : selected) {
            FileModel fileModel = (FileModel)o;
            model.removeReferencedFile(fileModel);
        }
        
        refresh();
        fEditor.setDirty(true);
    }
    
    private boolean askUserDeleteResources(List<?> selected) {
        // Confirmation dialog
        return MessageDialog.openQuestion(
                Display.getDefault().getActiveShell(),
                "Delete",
                selected.size() > 1 ?
                        "Are you sure you want to delete these entries?" 
                        : 
                        "Are you sure you want to delete this entry?");
    }


    
    // =============================================================================================
    //
    //                                   CONTENT PROVIDER
    //
    // =============================================================================================

    
    private class FileTableViewerContentProvider implements IStructuredContentProvider {
        
        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        }
        
        public void dispose() {
        }
        
        public Object[] getElements(Object parent) {
            if(parent instanceof EntryModel) {
                return ((EntryModel)parent).getReferencedFiles().toArray();
            }
            return new Object[0];
        }
    }

    // =============================================================================================
    //
    //                                   LABEL PROVIDER
    //
    // =============================================================================================

    private class FileTableViewerLabelProvider
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
                    return ((FileModel)element).getName();

               case 2:
                   return ((FileModel)element).getIFile().getParent().getFullPath().makeRelative().toString();

               default:
                    return null;
            }
        }
    }
    
    // =============================================================================================
    //
    //                                   SORTER
    //
    // =============================================================================================

    private class Sorter extends TableColumnSorter {

        @Override
        protected Object getValue(Object o, int index) {
            return ((ITableLabelProvider)getLabelProvider()).getColumnText(o, index);
        }
    }

}
