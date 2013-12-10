/**********************************************************************
Copyright (c) 2005 Michael Grundmann and others. All rights reserved.
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
2007 Andy Jefferson - documented
    ...
**********************************************************************/
package org.datanucleus.ide.eclipse.jobs;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaCore;

/**
 * Class providing auto-enhancement of classes, linking in to the building process.
 *
 * @version $Revision: 1.1 $
 */
public class AutoEnhancer extends IncrementalProjectBuilder
{
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int,
     *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException
    {
        if (kind == IncrementalProjectBuilder.FULL_BUILD)
        {
            fullBuild(monitor);
        }
        else
        {
            IResourceDelta delta = getDelta(getProject());
            if (delta == null)
            {
                fullBuild(monitor);
            }
            else
            {
                incrementalBuild(delta, monitor);
            }
        }
        return null;

    }

    protected void fullBuild(final IProgressMonitor monitor)
    {
        Job job = new EnhancerJob(JavaCore.create(this.getProject()));
        job.setPriority(Job.SHORT);
        job.setRule(ResourcesPlugin.getWorkspace().getRoot());
        job.schedule();
    }

    protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException
    {
        // the visitor does the work.
        // delta.accept(new MyBuildDeltaVisitor());
        // TODO do incremental build
        fullBuild(monitor);
    }
}