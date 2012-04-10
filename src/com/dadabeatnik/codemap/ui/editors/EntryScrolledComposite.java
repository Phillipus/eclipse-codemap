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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.ide.IDE;

import com.dadabeatnik.codemap.model.EntryModel;
import com.dadabeatnik.codemap.model.FileModel;


/**
 * Scrolled Composite for an Entry
 * 
 * @author Phillip Beauvoir
 */
public class EntryScrolledComposite extends ScrolledComposite implements ModifyListener {

    private CodeMapEditor fEditor;
    
    private Text fTitleText;
    private Text fDescriptionText;
    private FileTableViewer fFileTableViewer;
    
    private FileTreeViewerDragDropHandler fFileTreeViewerDragDropHandler;
    
    private EntryModel fEntryModel;
    
    private boolean fIsModifying;
    
    public EntryScrolledComposite(CodeMapEditor editor, Composite parent) {
        super(parent, SWT.BORDER | SWT.V_SCROLL);
        fEditor = editor;
        
        setLayoutData(new GridData(GridData.FILL, SWT.FILL, true, true));
        setExpandHorizontal(true);

        FormToolkit toolkit = (FormToolkit)editor.getAdapter(FormToolkit.class);
        
        Composite client = toolkit.createComposite(this);
        
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        client.setLayout(layout);
        
        setContent(client);
        
        Color bkColor = toolkit.getColors().getColor(IFormColors.H_GRADIENT_START);
        client.setBackground(bkColor);
        setBackground(bkColor);
        
        Composite fieldsComposite = toolkit.createComposite(client);
        fieldsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        fieldsComposite.setLayout(new GridLayout(2, false));
        fieldsComposite.setBackground(bkColor);
        
        Label label = toolkit.createLabel(fieldsComposite, "Title:");
        label.setBackground(bkColor);
        fTitleText = toolkit.createText(fieldsComposite, "", SWT.NONE);
        fTitleText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        fTitleText.addModifyListener(this);
        
        label = toolkit.createLabel(fieldsComposite, "Description:");
        label.setBackground(bkColor);
        fDescriptionText = toolkit.createText(fieldsComposite, "", SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.heightHint = 220;
        fDescriptionText.setLayoutData(gd);
        fDescriptionText.addModifyListener(this);
        
        Composite tableComposite = toolkit.createComposite(client);
        tableComposite.setLayout(new TableColumnLayout());
        fFileTableViewer = new FileTableViewer(fEditor, tableComposite, SWT.BORDER);
        gd = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd.heightHint = 150;
        tableComposite.setLayoutData(gd);
        
        // Drop handler
        fFileTreeViewerDragDropHandler = new FileTreeViewerDragDropHandler(fEditor, fFileTableViewer);
        
        fFileTableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                Object obj = ((IStructuredSelection)event.getSelection()).getFirstElement();

                if(obj instanceof FileModel) {
                    final IFile ifile = ((FileModel)obj).getIFile();

                    getShell().getDisplay().asyncExec(new Runnable() {
                        public void run() {
                            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                            try {
                                IDE.openEditor(page, ifile);
                            }
                            catch(PartInitException e) {
                            }
                        }
                    });
                }
            }
        });

        setEntry(null);
        
        client.layout();
        client.pack();
    }

    public void setEntry(EntryModel entryModel) {
        fEntryModel = entryModel;
        
        fTitleText.setEnabled(entryModel != null);
        fDescriptionText.setEnabled(entryModel != null);
        fFileTableViewer.getControl().setEnabled(entryModel != null);
        
        fIsModifying = true;
        
        fTitleText.setText(fEntryModel == null ? "" : fEntryModel.getTitle());
        fDescriptionText.setText(fEntryModel == null ? "" : fEntryModel.getDescription());
        fFileTableViewer.setInput(fEntryModel);
        fFileTreeViewerDragDropHandler.setEntry(fEntryModel);
        
        fIsModifying = false;
    }
    
    public void modifyText(ModifyEvent e) {
        if(fIsModifying) {
            return;
        }
        
        if(fEntryModel == null) {
            return;
        }
        
        if(e.getSource() == fTitleText) {
            fEntryModel.setTitle(fTitleText.getText());
        }
        else if(e.getSource() == fDescriptionText) {
            fEntryModel.setDescription(fDescriptionText.getText());
        }
        
        fEditor.changedModel(fEntryModel);
        fEditor.setDirty(true);
    }


}
