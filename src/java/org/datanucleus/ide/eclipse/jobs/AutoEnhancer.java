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
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.datanucleus.ide.eclipse.preferences.EnhancerPreferencePage;
import org.datanucleus.ide.eclipse.preferences.PreferenceConstants;
import org.datanucleus.ide.eclipse.project.ProjectHelper;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaCore;

/**
 * Class providing auto-enhancement of classes, linking in to the building process.
 *
 * @version $Revision: 1.2 $
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

    protected void incrementalBuild(IResourceDelta resourceDelta, IProgressMonitor monitor) throws CoreException
    {
        
        final AtomicBoolean hasAnyRelevantFileBeenChanged = new AtomicBoolean(false);
        final Predicate<IResource> resourceFilter = 
                createResourceFilterForRelevantResourceChanges(resourceDelta.getResource());
        
        resourceDelta.accept(new IResourceDeltaVisitor() {
            
            @Override
            public boolean visit(IResourceDelta delta) throws CoreException {
                
                if(resourceFilter.test(delta.getResource())) {
                    hasAnyRelevantFileBeenChanged.set(true);
                    return false; // stop searching
                }
                return true; // continue searching
            }
        });
        
        if(!hasAnyRelevantFileBeenChanged.get()) {
            return; // don't trigger build
        }
        
        // the visitor does the work.
        // delta.accept(new MyBuildDeltaVisitor());
        // TODO do incremental build
        fullBuild(monitor);
    }
    
    private static Predicate<IResource> createResourceFilterForRelevantResourceChanges(IResource projectHolder) {
        
        final String fileSuffix = ProjectHelper.getStringPreferenceValue(projectHolder,
                EnhancerPreferencePage.PAGE_ID, PreferenceConstants.ENHANCER_INPUT_FILE_EXTENSIONS);
        final String[] fileSuffixes = fileSuffix.split(System.getProperty("path.separator"));
        
        final Set<String> relevantFileSuffixes = Stream.of(fileSuffixes).collect(Collectors.toSet());
        relevantFileSuffixes.remove("java");
        relevantFileSuffixes.add("class");
        
        return resource -> {
            if (resource.getType() == IResource.FILE) {
                if(relevantFileSuffixes.contains(resource.getFileExtension())) {
                    return true;
                }
            }
            return false;
        };
        
        
    }
    
    
}