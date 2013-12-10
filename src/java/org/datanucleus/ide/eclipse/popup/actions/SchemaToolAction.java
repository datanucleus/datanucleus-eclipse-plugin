/**********************************************************************
 Copyright (c) Sep 2, 2004 Erik Bengtson and others.
 All rights reserved. This program and the accompanying materials
 are made available under the terms of the JPOX License v1.0
 which accompanies this distribution. 

 Contributors:
 ...
 **********************************************************************/
package org.datanucleus.ide.eclipse.popup.actions;

import org.datanucleus.ide.eclipse.project.ProjectNature;
import org.datanucleus.ide.eclipse.wizard.schematool.SchemaToolWizard;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;


/**
 * @author erik
 * @version $Revision: 1.2 $
 */
public class SchemaToolAction implements IObjectActionDelegate
{

    private ISelection lastSelection;

    private IWorkbenchPart part;

    /**
     * Constructor for SchemaToolAction.
     */
    public SchemaToolAction()
    {
        super();
    }

    /**
     * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {
        this.part = targetPart;
    }

    /**
     * @see IActionDelegate#run(IAction)
     */
    public void run(IAction action)
    {
        // Instantiates and initializes the wizard
        SchemaToolWizard wizard = new SchemaToolWizard();
        if ((lastSelection instanceof IStructuredSelection) || (lastSelection == null))
            wizard.init(part.getSite().getWorkbenchWindow().getWorkbench(), (IStructuredSelection) lastSelection);

        // Instantiates the wizard container with the wizard and opens it
        WizardDialog dialog = new WizardDialog(part.getSite().getShell(), wizard);
        dialog.create();
        dialog.open();
    }

    /**
     * @see IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection)
    {
        lastSelection = selection;
        if (lastSelection == null)
        {
            return;
        }
        if (!(lastSelection instanceof StructuredSelection))
        {
            return;
        }
        StructuredSelection ss = (StructuredSelection) lastSelection;
        if (!(ss.getFirstElement() instanceof IJavaProject))
        {
            return;
        }
        IJavaProject javaProject = (IJavaProject) ss.getFirstElement();
        IProject project = javaProject.getProject();
        try
        {
            action.setEnabled(project.hasNature(ProjectNature.NATURE));
        }
        catch (CoreException e)
        {
        }
    }

}