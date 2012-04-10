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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.EditorPart;

import com.dadabeatnik.codemap.model.CodeMapModel;
import com.dadabeatnik.codemap.model.EntryModel;
import com.dadabeatnik.codemap.ui.CodeMapActionFactory;


/**
 * Main Editor
 * 
 * @author Phillip Beauvoir
 */
public class CodeMapEditor extends EditorPart {
    
    /**
     * @param file
     * @return The default Editor for a file or null. The file's contents is used as the basis.
     * @throws IOException 
     * @throws FileNotFoundException 
     */
    public static IEditorDescriptor getDefaultEditor(File file) throws IOException {
        IContentTypeManager contentTypeManager = Platform.getContentTypeManager();
        FileInputStream fi = new FileInputStream(file);
        IContentType contentType = contentTypeManager.findContentTypeFor(fi, file.getName());
        fi.close(); // Must close it!
        IEditorRegistry editor_registry = PlatformUI.getWorkbench().getEditorRegistry();
        return editor_registry.getDefaultEditor(file.getName(), contentType);
    }

    private EntryTableViewer fEntriesTableViewer;
    private EntryComposite fEntryComposite;
    
    private FormToolkit fToolkit;
    private Form fForm;
    
    private CodeMapModel fCodeMapModel;
    
    private boolean fDirty;
    
    private IAction fActionDelete;
    private IAction fActionSelectAll;
    private IAction fActionNewEntry;
    

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
        
        setPartName(input.getName());
        
        fCodeMapModel = new CodeMapModel(((IFileEditorInput)input).getFile());
        
        try {
            fCodeMapModel.load();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        try {
            fCodeMapModel.save();
            setDirty(false);
        }
        catch(CoreException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void doSaveAs() {
    }

    @Override
    public boolean isDirty() {
        return fDirty;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }
    
    public void setDirty(boolean dirty) {
        if(fDirty != dirty) {
            fDirty = dirty;
            firePropertyChange(IEditorPart.PROP_DIRTY);
        }
    }

    @Override
    public void createPartControl(Composite parent) {
        fToolkit = new FormToolkit(Display.getDefault());
        
        fForm = fToolkit.createForm(parent);
        fToolkit.paintBordersFor(fForm.getBody());
        
        /*
         * Form Body is main composite
         */
        Composite body = fForm.getBody();
        body.setLayout(new GridLayout());
        
        SashForm sash = new SashForm(body, SWT.VERTICAL);
        sash.setLayoutData(new GridData(GridData.FILL_BOTH));
        fToolkit.adapt(sash);
        
        Composite composite = fToolkit.createComposite(sash);
        composite.setLayout(new TableColumnLayout());
        
        // Entries Table
        fEntriesTableViewer = new EntryTableViewer(this, composite, SWT.BORDER);
        
        fEntriesTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                // Pass it on
                EntryModel entryModel = (EntryModel)((StructuredSelection)event.getSelection()).getFirstElement();
                fEntryComposite.setEntry(entryModel);
                
                // Update actions
                updateActions(event.getSelection());
            }
        });
        
        fEntryComposite = new EntryComposite(this, sash);
        
        makeActions();
        hookContextMenu();
        registerGlobalActions();
        
        sash.setWeights(new int[] {30, 70});
        
        fEntriesTableViewer.setInput(fCodeMapModel);
        
        if(!fCodeMapModel.getEntries().isEmpty()) {
            fEntriesTableViewer.setSelection(new StructuredSelection(fCodeMapModel.getEntries().get(0)));
        }
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
                fEntriesTableViewer.getTable().selectAll();
                updateActions(fEntriesTableViewer.getSelection());
            }
        };
        
        // New Entry
        fActionNewEntry = new Action("New Entry") {
            @Override
            public void run() {
                EntryModel entryModel = new EntryModel();
                entryModel.setTitle("new entry");
                fCodeMapModel.addEntry(entryModel);
                fEntriesTableViewer.refresh();
                setDirty(true);
                fEntriesTableViewer.setSelection(new StructuredSelection(entryModel));
                fEntriesTableViewer.reveal(entryModel);
            }
        };
    }
    
    private void updateActions(ISelection selection) {
        boolean isEmpty = selection.isEmpty();
        
        //Object obj = ((IStructuredSelection)selection).getFirstElement();

        fActionDelete.setEnabled(!isEmpty);
    }

    /**
     * Delete selected objects
     */
    private void doDeleteAction() {
        List<?> selected = ((IStructuredSelection)fEntriesTableViewer.getSelection()).toList();
        
        if(!askUserDeleteResources(selected)) {
            return;
        }
        
        for(Object o : selected) {
            EntryModel entryModel = (EntryModel)o;
            fCodeMapModel.removeEntry(entryModel);
        }
        
        fEntriesTableViewer.refresh();
        setDirty(true);
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
        
        Menu menu = menuMgr.createContextMenu(fEntriesTableViewer.getControl());
        fEntriesTableViewer.getControl().setMenu(menu);
        //fEditor.getSite().registerContextMenu(menuMgr, this); // NO!!!!! Otherwise you get the full Eclipse context menu items
    }

    private void fillContextMenu(IMenuManager manager) {
        boolean isEmpty = fEntriesTableViewer.getSelection().isEmpty();
        
        manager.add(new Separator(IWorkbenchActionConstants.EDIT_START));
        manager.add(fActionNewEntry);
        
        if(!isEmpty) {
            manager.add(fActionDelete);
        }

        manager.add(new Separator(IWorkbenchActionConstants.EDIT_END));
        manager.add(fActionSelectAll);

        // Other plug-ins can contribute their actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    /**
     * Register Global Actions on focus events
     */
    private void registerGlobalActions() {
        final IActionBars bars = getEditorSite().getActionBars();
        
        fEntriesTableViewer.getControl().addFocusListener(new FocusListener() {
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

    public void changedModel(EntryModel entryModel) {
        fEntriesTableViewer.refresh(entryModel);
    }
    
    @Override
    public void setFocus() {
        if(fEntriesTableViewer != null) {
            fEntriesTableViewer.getControl().setFocus();
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getAdapter(Class adapter) {
        if(adapter == CodeMapModel.class) {
            return fCodeMapModel;
        }
        
        if(adapter == FormToolkit.class) {
            return fToolkit;
        }
        
        return super.getAdapter(adapter);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        fToolkit.dispose();
    }

}
