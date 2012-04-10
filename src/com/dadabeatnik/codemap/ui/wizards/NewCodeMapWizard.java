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
package com.dadabeatnik.codemap.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.dadabeatnik.codemap.CodeMapPlugin;

/**
 * Wizard for new Code map file
 * 
 * @author Phillip Beauvoir
 */
public class NewCodeMapWizard extends Wizard implements INewWizard {
	private NewCodeMapWizardPage page;
	private ISelection selection;

	/**
	 * Constructor for NewCodeMapWizard.
	 */
	public NewCodeMapWizard() {
		setNeedsProgressMonitor(true);
		setWindowTitle("New CodeMap file");
	}
	
	/**
	 * Adding the page to the wizard.
	 */
	@Override
    public void addPages() {
		page = new NewCodeMapWizardPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	@Override
    public boolean performFinish() {
        final String containerName = page.getContainerName();
        final String fileName = page.getFileName();
        IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                try {
                    doFinish(containerName, fileName, monitor);
                }
                catch(CoreException e) {
                    throw new InvocationTargetException(e);
                }
                finally {
                    monitor.done();
                }
            }
        };
        try {
            getContainer().run(true, false, op);
        }
        catch(InterruptedException e) {
            return false;
        }
        catch(InvocationTargetException e) {
            Throwable realException = e.getTargetException();
            MessageDialog.openError(getShell(), "Error", realException.getMessage());
            return false;
        }
        return true;
    }
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */
	private void doFinish(String containerName, String fileName, IProgressMonitor monitor) throws CoreException {
        // create a sample file
        monitor.beginTask("Creating " + fileName, 2);
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IResource resource = root.findMember(new Path(containerName));
        if(!resource.exists() || !(resource instanceof IContainer)) {
            throwCoreException("Container \"" + containerName + "\" does not exist.");
        }
        IContainer container = (IContainer)resource;
        final IFile file = container.getFile(new Path(fileName));
        
        try {
            InputStream stream = openContentStream();
            if(file.exists()) {
                // file.setContents(stream, true, true, monitor);
            }
            else {
                file.create(stream, true, monitor);
                //file.setCharset("UTF-8", monitor);  // Don't do this as it adds an entry to org.eclipse.core.resources.prefs
            }
            stream.close();
        }
        catch(Exception ex) {
            System.out.println(ex);
        }
        
        monitor.worked(1);
        monitor.setTaskName("Opening file for editing...");
        
        getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                try {
                    IDE.openEditor(page, file, true);
                }
                catch(PartInitException e) {
                }
            }
        });

        monitor.worked(1);
    }

    /**
     * We will initialize file contents with a sample text.
     * @throws UnsupportedEncodingException 
     */
    private InputStream openContentStream() throws UnsupportedEncodingException {
        String contents = "";
        return new ByteArrayInputStream(contents.getBytes("UTF-8"));
    }

    private void throwCoreException(String message) throws CoreException {
        IStatus status = new Status(IStatus.ERROR, CodeMapPlugin.PLUGIN_ID, IStatus.OK, message, null);
        throw new CoreException(status);
    }

    /**
     * We will accept the selection in the workbench to see if we can initialize
     * from it.
     * 
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
    }
}