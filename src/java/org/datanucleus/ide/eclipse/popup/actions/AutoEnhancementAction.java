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


/**
 * @author Michael Grundmann
 * @version $Revision: 1.3 $
 */
public class AutoEnhancementAction extends JavaProjectAction
{
    @Override
    public void run(IAction action)
    {
        for (IJavaProject javaProject : getSelectedJavaProjects()) {
            IProject project = javaProject.getProject();

            if (ProjectHelper.hasNature(project)) {
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
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection)
    {
        super.selectionChanged(action, selection);

        for (IJavaProject javaProject : getSelectedJavaProjects()) {
            IProject project = javaProject.getProject();

            // Workaround for issue #IDE-22
            action.setChecked(ProjectHelper.getBuilderExistence(project, ProjectNature.BUILDER));
        }
    }
}
