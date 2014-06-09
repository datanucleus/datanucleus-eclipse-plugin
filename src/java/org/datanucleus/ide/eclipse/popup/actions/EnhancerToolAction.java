/**********************************************************************
Copyright (c) 2005 Tony Lai and others. All rights reserved.
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
2014 Marco Schulze - updated to inherit JavaProjectAction etc.
 ...
**********************************************************************/
package org.datanucleus.ide.eclipse.popup.actions;

import org.datanucleus.ide.eclipse.jobs.EnhancerJob;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;

/**
 * Action invoked when the user selects the popup menu option "Enhance" from a Java project.
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