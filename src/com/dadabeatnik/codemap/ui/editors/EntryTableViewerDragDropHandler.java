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

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;

import com.dadabeatnik.codemap.model.CodeMapModel;
import com.dadabeatnik.codemap.model.EntryModel;



/**
 *  DragDrop Handler for Entry Table
 * 
 * @author Phillip Beauvoir
 */
public class EntryTableViewerDragDropHandler {

    private CodeMapEditor fEditor;
    
    private StructuredViewer fViewer;
    
    private int fDragOperations = DND.DROP_MOVE;
    
    private Transfer[] fTransferTypes = new Transfer[] { LocalSelectionTransfer.getTransfer() };

    public EntryTableViewerDragDropHandler(CodeMapEditor editor, StructuredViewer viewer) {
        fEditor = editor;
        fViewer = viewer;
        
        registerDragSupport();
        registerDropSupport();
    }

    private void registerDragSupport() {
        fViewer.addDragSupport(fDragOperations, fTransferTypes, new DragSourceListener() {
            
            public void dragFinished(DragSourceEvent event) {
                LocalSelectionTransfer.getTransfer().setSelection(null);
            }

            public void dragSetData(DragSourceEvent event) {
                // For consistency set the data to the selection even though
                // the selection is provided by the LocalSelectionTransfer
                // to the drop target adapter.
                event.data = LocalSelectionTransfer.getTransfer().getSelection();
            }

            public void dragStart(DragSourceEvent event) {
                LocalSelectionTransfer.getTransfer().setSelection(fViewer.getSelection());
                event.doit = true;
            }
            
        });
    }
    
    private void registerDropSupport() {
        fViewer.addDropSupport(fDragOperations, fTransferTypes, new DropTargetListener() {
            int operations = DND.DROP_NONE;
            
            public void dragEnter(DropTargetEvent event) {
                operations = isValidSelection() ? event.detail : DND.DROP_NONE;
            }

            public void dragLeave(DropTargetEvent event) {
            }

            public void dragOperationChanged(DropTargetEvent event) {
                operations = isValidSelection() ? event.detail : DND.DROP_NONE;
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
                    doMoveOperation(event);
                }
                else if((event.detail == DND.DROP_MOVE)) {
                    doMoveOperation(event);
                }
            }

            public void dropAccept(DropTargetEvent event) {
            }
            
        });
    }
    
    private void doMoveOperation(DropTargetEvent event) {
        // Dragged Entries
        IStructuredSelection selection = (IStructuredSelection)LocalSelectionTransfer.getTransfer().getSelection();
        Object[] dragged = selection.toArray();
        
        // Find Drop Target
        Object dropTarget = getDropTarget(event);
        
        List<EntryModel> entries = ((CodeMapModel)fEditor.getAdapter(CodeMapModel.class)).getEntries();
        
        int newPosition;
        
        // Dropped on an entry
        if(dropTarget instanceof EntryModel) {
            newPosition = entries.indexOf(dropTarget);
        }
        // Dropped in clear space
        else {
            newPosition = entries.size() - 1;
        }
        
        for(Object draggedEntry : dragged) {
            int oldPosition = entries.indexOf(draggedEntry);
            entries.remove(draggedEntry);
            entries.add(newPosition, (EntryModel)draggedEntry);
            if(newPosition < oldPosition) {
                newPosition++;
            }
        }
        
        fViewer.refresh();
        fEditor.setDirty(true);
    }

    /**
     * @return the Drop target
     */
    private Object getDropTarget(DropTargetEvent event) {
        // If event.item is null then it's not dropped on an Entry, so must be dropped on Parent Group
        if(event.item == null) {
            return fViewer.getInput();  // Return CodeMap model
        }
        
        // Else we dropped on an Entry
        else {
            return event.item.getData();
        }
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
    
    private boolean isValidSelection() {
        IStructuredSelection selection = (IStructuredSelection)LocalSelectionTransfer.getTransfer().getSelection();
        Object object = selection.getFirstElement();
        return object instanceof EntryModel;
    }
}
