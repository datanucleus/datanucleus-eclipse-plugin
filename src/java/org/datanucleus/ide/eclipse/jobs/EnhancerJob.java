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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
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

    @Override
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
        StringBuilder args = new StringBuilder();

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
            appendInputFiles(args, resource, javaProject);

        return args.toString();
    }

    private static void appendInputFiles(StringBuilder args, IResource resource, IJavaProject javaProject) {
        final boolean useFileListFile = ProjectHelper.getBooleanPreferenceValue(resource, EnhancerPreferencePage.PAGE_ID,
                PreferenceConstants.ENHANCER_USE_FILE_LIST_FILE,
                PreferenceConstants.ENHANCER_USE_FILE_LIST_FILE_DEFAULT_VALUE);

        final String fileSuffix = ProjectHelper.getStringPreferenceValue(resource,
                EnhancerPreferencePage.PAGE_ID, PreferenceConstants.ENHANCER_INPUT_FILE_EXTENSIONS);
        final String[] fileSuffixes = fileSuffix.split(System.getProperty("path.separator"));

        final List<String> inputFiles = new ArrayList<String>();
        LaunchUtilities.getInputFiles(inputFiles, resource, javaProject, fileSuffixes, !useFileListFile);

        if (useFileListFile) {
            File fileListFile = writeFileListFile(inputFiles);
            args.append(" -flf \"").append(fileListFile.getAbsolutePath()).append('"');
        }
        else {
            for (int i = 0; i < inputFiles.size(); i++)
            {
                args.append(inputFiles.get(i));
            }
        }
    }

    /**
     * Writes the given {@code files} into a temporary file. The file is deleted by the enhancer.
     *
     * @param files the list of files to be written into the file (UTF-8-encoded). Must not be <code>null</code>.
     * @return the temporary file.
     */
    private static File writeFileListFile(Collection<String> files) {
        try {
            File fileListFile = File.createTempFile("enhancer-", ".flf");
            FileOutputStream out = new FileOutputStream(fileListFile);
            try {
                OutputStreamWriter w = new OutputStreamWriter(out, "UTF-8");
                try {
                    for (String file : files) {
                        w.write(file);
                        // The enhancer uses a BufferedReader, which accepts all types of line feeds (CR, LF, CRLF).
                        // Therefore a single \n is fine.
                        w.write('\n');
                    }
                } finally {
                    w.close();
                }
            } finally {
                out.close();
            }
            return fileListFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}