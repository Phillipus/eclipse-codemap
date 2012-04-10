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

import org.jdom.Element;


/**
 * Interface for XML persistence
 * 
 * @author Phillip Beauvoir
 */
public interface IJDOMPersistence {

    /**
     * Write the Object to JDOM.
     * 
     * @return The newly created JDOM Element that this object represents.
     */
    Element toJDOM();
    
    /**
     * Unmarshall the given JDOM XML Element to this Object
     * 
     * @param element The JDOM Element to unmarshall
     */
    void fromJDOM(Element element);
    
    /**
     * @return The XML Element tag name for this object
     */
    String getTagName();

}
