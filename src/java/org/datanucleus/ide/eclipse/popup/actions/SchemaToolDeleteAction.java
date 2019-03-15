/**********************************************************************
Copyright (c) 2019 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors:
 ...
 **********************************************************************/
package org.datanucleus.ide.eclipse.popup.actions;

import java.util.List;

import org.datanucleus.ide.eclipse.Plugin;
import org.datanucleus.ide.eclipse.jobs.SchemaToolDeleteJob;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IActionDelegate;

/**
 * Action invoked when the user selects the popup menu option "SchemaTool Delete" from a Java project.
 */
public class SchemaToolDeleteAction extends JavaProjectAction
{
    /**
     * @see IActionDelegate#run(IAction)
     */
    public void run(IAction action)
    {
        List<IJavaProject> javaProjects = getSelectedJavaProjects();

        if (javaProjects.isEmpty())
        {
            Plugin.logError("Attempt to invoke SchemaTool Delete but no JavaProject selected!");
            return;
        }

        for (IJavaProject javaProject : getSelectedJavaProjects())
        {
        	SchemaToolDeleteJob job = new SchemaToolDeleteJob(javaProject);
            job.setUser(true);
            job.schedule();
        }
    }
}