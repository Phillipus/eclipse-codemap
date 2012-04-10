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
package com.dadabeatnik.codemap.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;




/**
 * Action Factory - these are for *local* Actions that can be instantiated.
 * Surely there must be a better way to do this in Eclipse than
 * repeating Actions from org.eclipse.ui.actions.ActionFactory?
 * 
 * @author Phillip Beauvoir
 */
public final class CodeMapActionFactory {
    /**
     * DELETE
     */
    public static class DeleteAction extends Action {
        public DeleteAction() {
            ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
            setText("Delete");
            setActionDefinitionId("org.eclipse.ui.edit.delete"); // Ensures key binding is displayed //$NON-NLS-1$
            setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
            setDisabledImageDescriptor(
                    sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
        }
    }

    /**
     * RENAME
     */
    public static class RenameAction extends Action {
        public RenameAction() {
            setText("Rename");
            setActionDefinitionId("org.eclipse.ui.edit.rename"); // Ensures key binding is displayed //$NON-NLS-1$
        }
    }

    /**
     * CUT
     */
    public static class CutAction extends Action {
        public CutAction() {
            ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
            setText("Cut");
            setActionDefinitionId("org.eclipse.ui.edit.cut"); // Ensures key binding is displayed //$NON-NLS-1$
            setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
            setDisabledImageDescriptor(
                    sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_CUT_DISABLED));
        }
    }

    /**
     * COPY
     */
    public static class CopyAction extends Action {
        public CopyAction() {
            ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
            setText("Copy");
            setActionDefinitionId("org.eclipse.ui.edit.copy"); // Ensures key binding is displayed //$NON-NLS-1$
            setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
            setDisabledImageDescriptor(
                    sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
        }
    }
    
    /**
     * PASTE
     */
    public static class PasteAction extends Action {
        public PasteAction() {
            ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
            setText("Paste");
            setActionDefinitionId("org.eclipse.ui.edit.paste"); // Ensures key binding is displayed //$NON-NLS-1$
            setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
            setDisabledImageDescriptor(
                    sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
        }
    }

    /**
     * SELECT ALL
     */
    public static class SelectAllAction extends Action {
        public SelectAllAction() {
            setText("Select All");
            setActionDefinitionId("org.eclipse.ui.edit.selectAll"); // Ensures key binding is displayed //$NON-NLS-1$
        }
    }

    /**
     * OPEN
     */
    public static class OpenAction extends Action {
        public OpenAction() {
            setText("Open");
            setToolTipText("Open");
            setActionDefinitionId("org.tencompetence.ui.open"); // Ensures key binding is displayed //$NON-NLS-1$
        }
    }
    
    /**
     * MOVE UP
     */
    public static class MoveUpAction extends Action {
        public MoveUpAction() {
            setText("Move Up");
            setToolTipText("Move Up");
            setImageDescriptor(ImageFactory.getImageDescriptor(ImageFactory.ICON_UP));
        }
    }

    /**
     * MOVE DOWN
     */
    public static class MoveDownAction extends Action {
        public MoveDownAction() {
            setText("Move Down");
            setToolTipText("Move Down");
            setImageDescriptor(ImageFactory.getImageDescriptor(ImageFactory.ICON_DOWN));
        }
    }
}
