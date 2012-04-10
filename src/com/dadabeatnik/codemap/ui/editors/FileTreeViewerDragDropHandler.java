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
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;

import com.dadabeatnik.codemap.model.EntryModel;
import com.dadabeatnik.codemap.model.FileModel;



/**
 *  DragDrop Handler
 * 
 * @author Phillip Beauvoir
 */
public class FileTreeViewerDragDropHandler {

    private CodeMapEditor fEditor;
    
    private EntryModel fEntryModel;
    private StructuredViewer fViewer;
    
    private int fDragOperations = DND.DROP_COPY | DND.DROP_MOVE;

    public FileTreeViewerDragDropHandler(CodeMapEditor editor, StructuredViewer viewer) {
        fEditor = editor;
        fViewer = viewer;
        
        registerDropSupport();
    }


    public void setEntry(EntryModel entryModel) {
        fEntryModel = entryModel;
    }

    private void registerDropSupport() {
        Transfer[] dropTransferTypes = new Transfer[] {
                FileTransfer.getInstance(),
                LocalSelectionTransfer.getTransfer()
        };
        
        fViewer.addDropSupport(fDragOperations, dropTransferTypes, new DropTargetListener() {
            int operations = DND.DROP_NONE;
            
            public void dragEnter(DropTargetEvent event) {
                operations = isValidSelection() ? DND.DROP_COPY : DND.DROP_NONE;
            }

            public void dragLeave(DropTargetEvent event) {
            }

            public void dragOperationChanged(DropTargetEvent event) {
                operations = isValidSelection() ? DND.DROP_COPY : DND.DROP_NONE;
            }

            public void dragOver(DropTargetEvent event) {
                event.detail = getDropTarget(event) != null ? operations : DND.DROP_NONE;
                
                if(operations == DND.DROP_NONE) {
                    event.feedback = DND.FEEDBACK_NONE;
                }
                else {
                    event.feedback = getFeedbackType(event);
                    event.feedback |= DND.FEEDBACK_SCROLL | DND.FEEDBACK_EXPAND;
                }
            }

            public void drop(DropTargetEvent event) {
                if(!LocalSelectionTransfer.getTransfer().isSupportedType(event.currentDataType)){
                    return;
                }
                
                if((event.detail == DND.DROP_COPY)) {
                    doDropOperation(event);
                }
            }

            public void dropAccept(DropTargetEvent event) {
            }
            
        });
    }

    private void doDropOperation(DropTargetEvent event) {
        if(!(LocalSelectionTransfer.getTransfer().getSelection() instanceof IStructuredSelection)) {
            return;
        }
        
        IStructuredSelection selection = (IStructuredSelection)LocalSelectionTransfer.getTransfer().getSelection();
        
        for(Object element : selection.toArray()) {
            FileModel fileModel = null;
            
            if(element instanceof IFile) {
                fileModel = new FileModel((IFile)element);
            }
            
            else if(element instanceof ICompilationUnit) {
                IFile iFile = (IFile)((ICompilationUnit)element).getResource();
                fileModel = new FileModel(iFile);
            }
            
            if(fileModel != null && fEntryModel != null && fEntryModel.addReferencedFile(fileModel)) {
                fEditor.setDirty(true);
            }
        }
        
        fViewer.refresh();
    }

    /**
     * @return the Drop target
     */
    private Object getDropTarget(DropTargetEvent event) {
        return fViewer;
    }

    private boolean isValidSelection() {
        return true;
    }

    /**
     * Determine the feedback type for dropping
     * 
     * @param event
     * @return
     */
    private int getFeedbackType(DropTargetEvent event) {
        return DND.FEEDBACK_SELECT;
        //return DND.FEEDBACK_NONE;
    }
}
