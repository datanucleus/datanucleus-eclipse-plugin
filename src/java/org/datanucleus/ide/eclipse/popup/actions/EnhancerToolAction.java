/**********************************************************************
 Copyright (c) 2005 Tony Lai and others.
 All rights reserved. This program and the accompanying materials
 are made available under the terms of the JPOX License v1.0
 which accompanies this distribution. 

 Contributors:
 ...
 **********************************************************************/
package org.datanucleus.ide.eclipse.popup.actions;

import org.datanucleus.ide.eclipse.jobs.EnhancerJob;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;


/**
 * @author Tony Lai
 * @version $Revision: 1.2 $
 */
public class EnhancerToolAction implements IObjectActionDelegate
{
    private IJavaProject javaProject;

    /**
     * Constructor for EnhancerToolAction.
     */
    public EnhancerToolAction()
    {
        super();
    }

    /**
     * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {
    }

    /**
     * @see IActionDelegate#run(IAction)
     */
    public void run(IAction action)
    {
        if (javaProject != null)
        {
            EnhancerJob job = new EnhancerJob(javaProject);
            job.setUser(true);
            job.schedule();
        }
    }

    /**
     * @see IActionDelegate#selectionChanged(IAction, ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection)
    {
        javaProject = null;
        if (selection == null)
        {
            return;
        }
        if (!(selection instanceof StructuredSelection))
        {
            return;
        }
        StructuredSelection ss = (StructuredSelection) selection;
        if (!(ss.getFirstElement() instanceof IJavaProject))
        {
            return;
        }
        javaProject = (IJavaProject) ss.getFirstElement();
    }

}