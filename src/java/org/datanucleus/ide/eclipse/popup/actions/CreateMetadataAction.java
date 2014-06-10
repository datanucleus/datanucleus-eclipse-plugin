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

/**
 * 
 */
public class CreateMetadataAction extends JavaProjectAction
{
    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action)
    {
        // Instantiates and initializes the wizard
        MetadataCreationWizard wizard = new MetadataCreationWizard();
        ISelection selection = getSelection();
        if ((selection instanceof IStructuredSelection) || (selection == null))
        wizard.init(getActivePart().getSite().getWorkbenchWindow().getWorkbench(), (IStructuredSelection)selection);
            
        // Instantiates the wizard container with the wizard and opens it
        WizardDialog dialog = new WizardDialog(getActivePart().getSite().getShell(), wizard);
        dialog.create();
        dialog.open();
    }
}
