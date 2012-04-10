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

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;


/**
 * Model for an Entry
 * 
 * @author Phillip Beauvoir
 */
public class EntryModel implements IJDOMPersistence {
    
    public static final String XMLTAG = "entry";

    private List<FileModel> fReferencedFiles = new ArrayList<FileModel>();
    
    private String fTitle = "";
    private String fDescription = "";
    
    public EntryModel() {
    }
    
    public String getTitle() {
        return fTitle;
    }
    
    public void setTitle(String title) {
        fTitle = title;
    }
    
    public String getDescription() {
        return fDescription;
    }
    
    public void setDescription(String description) {
        fDescription = description;
    }
    
    public List<FileModel> getReferencedFiles() {
        return fReferencedFiles;
    }
    
    public boolean addReferencedFile(FileModel fileModel) {
        if(fileModel == null) {
            return false;
        }

        if(!fReferencedFiles.contains(fileModel)) {
            return fReferencedFiles.add(fileModel);
        }
        
        return false;
    }
    
    public boolean removeReferencedFile(FileModel fileModel) {
        if(fileModel == null) {
            return false;
        }
        return fReferencedFiles.remove(fileModel);
    }

    // JDOM
    
    public void fromJDOM(Element element) {
        for(Object o : element.getChildren()) {
            Element child = (Element)o;
            String tag = child.getName();
            
            if(tag.equals("title")) {
                setTitle(child.getText());
            }
            
            else if(tag.equals("description")) {
                setDescription(child.getText());
            }
            
            else if(tag.equals(FileModel.XMLTAG)) {
                FileModel fileModel = new FileModel();
                fileModel.fromJDOM(child);
                fReferencedFiles.add(fileModel);
            }
        }
    }

    public String getTagName() {
        return XMLTAG;
    }

    public Element toJDOM() {
        Element element = new Element(getTagName(), CodeMapModel.NAMESPACE);
        
        // Title
        Element titleElement = new Element("title", CodeMapModel.NAMESPACE);
        titleElement.setText(getTitle());
        element.addContent(titleElement);
        
        // Description
        Element descElement = new Element("description", CodeMapModel.NAMESPACE);
        descElement.setText(getDescription());
        element.addContent(descElement);

        // Files
        for(FileModel fileModel : fReferencedFiles) {
            Element f = fileModel.toJDOM();
            element.addContent(f);
        }
        
        return element;
    }
}
