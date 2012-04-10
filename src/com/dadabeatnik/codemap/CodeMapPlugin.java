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
package com.dadabeatnik.codemap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class CodeMapPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.dadabeatnik.codemap";

	// The shared instance
	private static CodeMapPlugin fInstance;
	
	/**
	 * The constructor
	 */
	public CodeMapPlugin() {
	}

	@Override
    public void start(BundleContext context) throws Exception {
		super.start(context);
		fInstance = this;
	}

	@Override
    public void stop(BundleContext context) throws Exception {
		fInstance = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static CodeMapPlugin getDefault() {
		return fInstance;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
