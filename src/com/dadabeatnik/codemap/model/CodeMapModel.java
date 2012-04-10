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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


/**
 * Main Model
 * 
 * @author Phillip Beauvoir
 */
public class CodeMapModel {
    
    public static final Namespace NAMESPACE = Namespace.getNamespace("", "http://www.dadabeatnik.com/codemap");
    
    public static final String XMLTAG = "codemap";
    
    /**
     * We have to use the IFile from the Editor to keep the Resource in sync in the workspace
     */
    private IFile fFile;
    
    private List<EntryModel> fEntries = new ArrayList<EntryModel>();
    
    public CodeMapModel(IFile file) {
        fFile = file;
    }
    
    public List<EntryModel> getEntries() {
        return fEntries;
    }
    
    public boolean addEntry(EntryModel entryModel) {
        if(entryModel == null) {
            return false;
        }

        if(!fEntries.contains(entryModel)) {
            return fEntries.add(entryModel);
        }
        
        return false;
    }
    
    public boolean removeEntry(EntryModel entryModel) {
        if(entryModel == null) {
            return false;
        }
        return fEntries.remove(entryModel);
    }
    
    public void load() throws IOException, JDOMException {
        if(!fFile.exists()) {
            return;
        }
        
        Document doc = readXMLFile(fFile.getLocation().toFile());
        
        if(doc == null || !doc.hasRootElement()) {
            return;
        }
        
        Element root = doc.getRootElement();
        
        // Entries
        for(Object o : root.getChildren(EntryModel.XMLTAG, NAMESPACE)) {
            Element child = (Element)o;
            EntryModel entryModel = new EntryModel();
            entryModel.fromJDOM(child);
            fEntries.add(entryModel);
        }
    }

    public void save() throws CoreException {
        Document doc = new Document();
        Element root = new Element(XMLTAG, NAMESPACE);
        doc.setRootElement(root);
        
        // Entries
        for(EntryModel entryModel : fEntries) {
            Element element = entryModel.toJDOM();
            root.addContent(element);
        }
        
        try {
            InputStream stream = openContentStream(doc);
            
            if(fFile.exists()) {
                // Seems that true(?) needs to be used for the second parameter, otherwise we get Resource sync problems
                fFile.setContents(stream, true, true, null);  
            }
            else {
                fFile.create(stream, true, null);
            }
            
            stream.close();
        }
        catch(IOException ex) {
            MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error saving", ex.getMessage());
        }
    }
    
    private InputStream openContentStream(Document doc) throws UnsupportedEncodingException {
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        String contents = outputter.outputString(doc);
        return new ByteArrayInputStream(contents.getBytes("UTF-8")); // Has to be UTF-8
    }
    
    private Document readXMLFile(File file) throws IOException, JDOMException {
        // Becuase we saved new blank files with ""
        if(file.length() == 0) {
            return null;
        }
        
        SAXBuilder builder = new SAXBuilder();
        // This allows UNC mapped locations to load
        return builder.build(new FileInputStream(file));
    }
}
