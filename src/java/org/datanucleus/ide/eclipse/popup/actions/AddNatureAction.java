/**********************************************************************
 Copyright (c) 2004 Erik Bengtson and others.
 All rights reserved. This program and the accompanying materials
 are made available under the terms of the JPOX License v1.0
 which accompanies this distribution.

 Contributors:
 ...
 **********************************************************************/
package org.datanucleus.ide.eclipse.popup.actions;

import org.datanucleus.ide.eclipse.project.ProjectHelper;
import org.datanucleus.ide.eclipse.project.ProjectNature;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;


/**
 * @author erik
 * @version $Revision: 1.2 $
 */
public class AddNatureAction extends JavaProjectAction
{

    @Override
    public void run(IAction action)
    {
        for (IJavaProject javaProject : getSelectedJavaProjects()) {
            IProject project = javaProject.getProject();
            ProjectHelper.addNature(project, ProjectNature.NATURE);
        }
    }

}