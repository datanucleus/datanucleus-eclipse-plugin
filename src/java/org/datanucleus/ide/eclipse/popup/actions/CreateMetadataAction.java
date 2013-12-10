/**********************************************************************
 Copyright (c) 2005 Michael Grundmann and others.
 All rights reserved. This program and the accompanying materials
 are made available under the terms of the JPOX License v1.0
 which accompanies this distribution. 

 Contributors:
 ...
 **********************************************************************/
package org.datanucleus.ide.eclipse.popup.actions;

import org.datanucleus.ide.eclipse.wizard.createmetadata.MetadataCreationWizard;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

/**
 */
public class CreateMetadataAction implements IObjectActionDelegate
{
    private ISelection lastSelection;

    private IWorkbenchPart part;

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
     * org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {
        this.part = targetPart;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action)
    {
        // Instantiates and initializes the wizard
        MetadataCreationWizard wizard = new MetadataCreationWizard();
        if ((lastSelection instanceof IStructuredSelection) || (lastSelection == null))
        wizard.init(part.getSite().getWorkbenchWindow().getWorkbench(), 
            (IStructuredSelection)lastSelection);
            
        // Instantiates the wizard container with the wizard and opens it
        WizardDialog dialog = new WizardDialog( part.getSite().getShell(), wizard);
        dialog.create();
        dialog.open();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     * org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection)
    {
        lastSelection = selection;
    }
}
