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
2007 Andy Jefferson - added ClassEnhancer selection
    ...
**********************************************************************/
package org.datanucleus.ide.eclipse.jobs;

import java.util.ArrayList;
import java.util.List;

import org.datanucleus.ide.eclipse.preferences.EnhancerPreferencePage;
import org.datanucleus.ide.eclipse.preferences.GeneralPreferencePage;
import org.datanucleus.ide.eclipse.preferences.PreferenceConstants;
import org.datanucleus.ide.eclipse.project.ProjectHelper;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;

/**
 * Class that performs the enhancement process.
 */
public class EnhancerJob extends WorkspaceJob
{
    private static final String NAME = "DataNucleus Enhancer";

    private static final String MAINCLASS = "org.datanucleus.enhancer.DataNucleusEnhancer";

    private IJavaProject javaProject;

    public EnhancerJob(IJavaProject javaProject)
    {
        super(NAME);
        this.javaProject = javaProject;
    }
 
    public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException
    {
        List classpath = LaunchUtilities.getDefaultClasspath(javaProject);
        String vmArgs = LaunchUtilities.getDefaultVMArguments(javaProject.getProject());
        String workingDir = null; //LaunchUtilities.getWorkingDir(javaProject);
        String programArgs = getProgramArguments(javaProject.getProject(), javaProject);
        boolean captureOutput = ProjectHelper.getBooleanPreferenceValue(javaProject.getProject(), EnhancerPreferencePage.PAGE_ID,
                PreferenceConstants.ENHANCER_CAPTURE_OUTPUT);
        LaunchUtilities.launch(javaProject, NAME, MAINCLASS, classpath, vmArgs, workingDir, programArgs, MAINCLASS, captureOutput);
        return Status.OK_STATUS;
    }

    private static String getProgramArguments(IResource resource, IJavaProject javaProject)
    {
        StringBuffer args = new StringBuffer();

        // API
        String apiName = ProjectHelper.getStringPreferenceValue(resource, GeneralPreferencePage.PAGE_ID,
            PreferenceConstants.PERSISTENCE_API);
        if (apiName != null && apiName.trim().length() > 0)
        {
            args.append(" -api ").append(apiName.trim());
        }

        // verbose
        boolean verboseMode = ProjectHelper.getBooleanPreferenceValue(resource, EnhancerPreferencePage.PAGE_ID,
            PreferenceConstants.ENHANCER_VERBOSE_MODE);
        boolean captureOutput = ProjectHelper.getBooleanPreferenceValue(javaProject.getProject(), EnhancerPreferencePage.PAGE_ID,
                PreferenceConstants.ENHANCER_CAPTURE_OUTPUT);
        if (captureOutput && verboseMode)
        {
            args.append(" -v ");
        }

        // PersistenceUnit
        boolean usingPersistenceUnit = false;
        String persistenceUnit = ProjectHelper.getStringPreferenceValue(resource, EnhancerPreferencePage.PAGE_ID,
            PreferenceConstants.ENHANCER_PERSISTENCE_UNIT);
        if (persistenceUnit != null && persistenceUnit.trim().length() > 0)
        {
            usingPersistenceUnit = true;
            args.append(" -pu ").append(persistenceUnit.trim());
        }

        // Input files (jdo/class)
        if (!usingPersistenceUnit)
        {
            List argsList = new ArrayList();
            String fileSuffix = ProjectHelper.getStringPreferenceValue(resource, 
                EnhancerPreferencePage.PAGE_ID, PreferenceConstants.ENHANCER_INPUT_FILE_EXTENSIONS);
            String[] fileSuffixes = fileSuffix.split(System.getProperty("path.separator"));
            LaunchUtilities.getInputFiles(argsList, resource, javaProject, fileSuffixes, true);
            for (int i = 0; i < argsList.size(); i++)
            {
                args.append(argsList.get(i));
            }
        }

        return args.toString();
    }
}