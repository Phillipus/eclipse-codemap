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
package com.dadabeatnik.codemap.model;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.jdom.Element;


/**
 * Model for a File
 * 
 * @author Phillip Beauvoir
 */
public class FileModel implements IJDOMPersistence {
    
    public static final String XMLTAG = "file";
    
    private String fFilePath;
    private File fFile;
    private IFile fIFile;
    
    public FileModel() {
        fFilePath = "/";
    }
    
    public FileModel(String filePath) {
        fFilePath = filePath;
    }
    
    public FileModel(IFile iFile) {
        fIFile = iFile;
        fFilePath = iFile.getFullPath().toString();
    }

    public String getName() {
        return getIFile().getName();
    }

    public File getFile() {
        if(fFile == null) {
            fFile = getIFile().getLocation().toFile();
        }
        return fFile;
    }
    
    public IFile getIFile() {
        if(fIFile == null) {
            IPath path = new Path(fFilePath);
            fIFile = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
        }
        return fIFile;
    }

    public String getFilePath() {
        return fFilePath;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof FileModel) {
            FileModel that = (FileModel)obj;
            if(fFilePath == null || that.fFilePath == null) {
                return false;
            }
            return fFilePath.equals(that.fFilePath);
        }
        
        return super.equals(obj);
    }
    
    
    // JDOM
    
    public void fromJDOM(Element element) {
        fFilePath = element.getAttributeValue("path");
    }

    public String getTagName() {
        return XMLTAG;
    }

    public Element toJDOM() {
        Element element = new Element(getTagName(), CodeMapModel.NAMESPACE);
        element.setAttribute("path", fFilePath);
        return element;
    }
}
