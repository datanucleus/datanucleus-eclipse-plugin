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


/**
 * @author Tony Lai
 * @version $Revision: 1.2 $
 */
public class EnhancerToolAction extends JavaProjectAction
{
    @Override
    public void run(IAction action)
    {
        for (IJavaProject javaProject : getSelectedJavaProjects())
        {
            EnhancerJob job = new EnhancerJob(javaProject);
            job.setUser(true);
            job.schedule();
        }
    }

}