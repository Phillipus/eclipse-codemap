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

import java.io.IOException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.dadabeatnik.codemap.CodeMapPlugin;
import com.dadabeatnik.codemap.model.FileModel;
import com.dadabeatnik.codemap.ui.editors.CodeMapEditor;


/**
 * Image Factory
 * 
 * @author Phillip Beauvoir
 */
public class ImageFactory {
    
    public static final String ICONPATH = "icons/"; //$NON-NLS-1$
    
    public static String ICON_DOWN = ICONPATH + "down.gif"; //$NON-NLS-1$
    public static String ICON_UP = ICONPATH + "up.gif"; //$NON-NLS-1$

    
    public static Image getFileIcon(Object element) {
        ImageRegistry imageRegistry = CodeMapPlugin.getDefault().getImageRegistry();
        
        Image image = null;
        IEditorDescriptor editorDescriptor = null;
        
        if(element instanceof FileModel) {
            try {
                editorDescriptor = CodeMapEditor.getDefaultEditor(((FileModel)element).getFile());
            }
            catch(IOException ex) {
                ex.printStackTrace();
            }
        }
        
        if(editorDescriptor != null) {
            image = imageRegistry.get(editorDescriptor.getId());  // cached?
            if(image == null) {
                ImageDescriptor imageDescriptor = editorDescriptor.getImageDescriptor();
                if(imageDescriptor != null) {
                    image = imageDescriptor.createImage();
                    if(image != null) {
                        imageRegistry.put(editorDescriptor.getId(), image);  // cache it
                    }
                }
            }
        }
        
        if(image == null) {
            image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
        }
        
        return image;
    }
    
    /**
     * Returns the shared image description represented by the given key.
     * 
     * @param imageName
     *          the logical name of the image description to retrieve
     * @return the shared image description represented by the given name
     */
    public static ImageDescriptor getImageDescriptor(String imageName) {
        if (imageName == null || "file".equals(imageName)) { //$NON-NLS-1$
            return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE);
        }

        if ("folder".equals(imageName)) { //$NON-NLS-1$
            return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER);
        }

        return AbstractUIPlugin.imageDescriptorFromPlugin(CodeMapPlugin.PLUGIN_ID, imageName);
    }

}
