/**********************************************************************
 Copyright (c) 2005 Michael Grundmann and others.
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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;


/**
 * @author Michael Grundmann
 * @version $Revision: 1.3 $
 */
public class AutoEnhancementAction implements IObjectActionDelegate
{
    private IJavaProject javaProject;

    private IProject project;

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action)
    {
        if (project != null)
        {
            ProjectHelper.hasNature(project);
            boolean builderFound = ProjectHelper.getBuilderExistence(project, ProjectNature.BUILDER);

            if (!builderFound)
            {
                ProjectHelper.addBuilderToBuildSpec(project, ProjectNature.BUILDER);
            }
            else
            {
                ProjectHelper.removeBuilderFromBuildSpec(project, ProjectNature.BUILDER);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     * org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection)
    {
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
        project = javaProject.getProject();
        
        // Workaround for issue #IDE-22
        action.setChecked(ProjectHelper.getBuilderExistence(project, ProjectNature.BUILDER));
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
     * org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {
    }
}
