/**********************************************************************
 Copyright (c) 2004 Erik Bengtson and others.
 All rights reserved. This program and the accompanying materials
 are made available under the terms of the JPOX License v1.0
 which accompanies this distribution. 

 Contributors:
 Michael Grundmann  -  Removed dependencies to JDT internal classes. 
 ...
 **********************************************************************/
package org.datanucleus.ide.eclipse.wizard.createappid;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

/**
 * Wizard for the generation of application id primary key classes.
 * @version $Revision: 1.1 $
 */
public class JDOApplicationIdWizard extends Wizard implements INewWizard
{
    private JDOApplicationIdWizardMainPage mainPage;

    private IStructuredSelection selection;

    /**
     * Constructor for Application Id Wizard.
     */
    public JDOApplicationIdWizard()
    {
        setNeedsProgressMonitor(true);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    public void addPages()
    {
        mainPage = new JDOApplicationIdWizardMainPage();
        mainPage.init(selection);
        addPage(mainPage);
    }

    /*
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
        this.selection = selection;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    public boolean performFinish()
    {
        IRunnableWithProgress operation = new WorkspaceModifyOperation()
        {
            protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException
            {
                mainPage.createType(monitor);
            }
        };
        try
        {
            getContainer().run(false, true, operation);
        }
        catch (InvocationTargetException e)
        {
            return false;
        }
        catch (InterruptedException e)
        {
            return false;
        }
        return true;
    }
}