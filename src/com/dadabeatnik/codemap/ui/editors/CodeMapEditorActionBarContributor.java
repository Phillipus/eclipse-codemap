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

import org.eclipse.ui.part.EditorActionBarContributor;


/**
 * Action Bar Contributor for Editor
 * 
 * @author Phillip Beauvoir
 */
public class CodeMapEditorActionBarContributor extends EditorActionBarContributor {

    public CodeMapEditorActionBarContributor() {
    }

//    @Override
//    public void setActiveEditor(IEditorPart part) {
//        IActionBars actionBars = getActionBars();
//        if(actionBars != null) {
//            actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(),
//                    ((CodeMapEditor)part).getAction((ActionFactory.DELETE.getId())));
//        }
//    }
}
