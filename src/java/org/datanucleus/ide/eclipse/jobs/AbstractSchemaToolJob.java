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
package org.datanucleus.ide.eclipse.jobs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.datanucleus.ide.eclipse.preferences.GeneralPreferencePage;
import org.datanucleus.ide.eclipse.preferences.PreferenceConstants;
import org.datanucleus.ide.eclipse.preferences.SchemaToolPreferencePage;
import org.datanucleus.ide.eclipse.project.ProjectHelper;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * Class that provides for SchemaTool processes, extended by the specific job required.
 */
public abstract class AbstractSchemaToolJob extends WorkspaceJob
{
    private static final String NAME = "DataNucleus SchemaTool";

    private static final String MAINCLASS = "org.datanucleus.store.schema.SchemaTool";

    private IJavaProject javaProject;

    public AbstractSchemaToolJob(IJavaProject javaProject)
    {
        super(NAME);
        this.javaProject = javaProject;
    }

    @Override
    public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException
    {
    	// Create classpath, adding any JDBC driver if provided
        List classpath = LaunchUtilities.getDefaultClasspath(javaProject);
        String jdbcDriverPath = ProjectHelper.getStringPreferenceValue(javaProject.getProject(), SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_DATASTORE_DRIVERJAR);
        if (jdbcDriverPath != null && !jdbcDriverPath.isEmpty())
        {
            String[] paths = jdbcDriverPath.split(";");
            for( int i=0; i<paths.length; i++)
            {
                IPath jdbcDriverIPath;
                try
                {
                    jdbcDriverIPath = new Path(new URL(paths[i]).getFile());
                    classpath.add(JavaRuntime.newArchiveRuntimeClasspathEntry(jdbcDriverIPath).getMemento());
                }
                catch (MalformedURLException e)
                {
                }
            }
        }

        String vmArgs = getVMArguments(javaProject.getProject());
        String workingDir = null; // LaunchUtilities.getWorkingDir(javaProject);
        String programArgs = getProgramArguments(javaProject.getProject(), javaProject, getMode());
        LaunchUtilities.launch(javaProject, NAME, MAINCLASS, classpath, vmArgs, workingDir, programArgs, MAINCLASS, true);

        // Refresh Project resources
        javaProject.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

        return Status.OK_STATUS;
    }

    /**
     * Method to return the SchemaTool mode of operation, such as "create", "delete", "validate", "dbinfo", and "schemainfo".
     * @return The mode of operation
     */
    protected abstract String getMode();

    private static String getProgramArguments(IResource resource, IJavaProject javaProject, String mode)
    {
        StringBuilder args = new StringBuilder();

        // Mode
        args.append(" -" + mode);

        // API
        String apiName = ProjectHelper.getStringPreferenceValue(resource, GeneralPreferencePage.PAGE_ID, PreferenceConstants.PERSISTENCE_API);
        if (apiName != null && apiName.trim().length() > 0)
        {
            args.append(" -api ").append(apiName.trim());
        }

        // verbose
        boolean verboseMode = ProjectHelper.getBooleanPreferenceValue(resource, SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_VERBOSE_MODE);
        if (verboseMode)
        {
            args.append(" -v ");
        }

        // Properties file
        String propertiesFilename = ProjectHelper.getStringPreferenceValue(resource, SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_PROPERTIES_FILE);
        if (propertiesFilename != null && propertiesFilename.trim().length() > 0)
        {
            args.append(" -props \"").append(propertiesFilename).append("\"");
        }

        // PersistenceUnit
        boolean usingPersistenceUnit = false;
        String persistenceUnit = ProjectHelper.getStringPreferenceValue(resource, SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_PERSISTENCE_UNIT);
        if (persistenceUnit != null && persistenceUnit.trim().length() > 0)
        {
            usingPersistenceUnit = true;
            args.append(" -pu ").append(persistenceUnit.trim());
        }

        // DDL output
        boolean ddlOutput = ProjectHelper.getBooleanPreferenceValue(resource, SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_DDL_OUTPUT);
        String ddlFilename = ProjectHelper.getStringPreferenceValue(resource, SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_DDL_FILENAME);
        if (ddlOutput)
        {
        	args.append(" -ddlFile ").append(ddlFilename.trim());
        }

        // Input files (jdo/class)
        if (!usingPersistenceUnit)
        {
            List argsList = new ArrayList();
            String fileSuffix = ProjectHelper.getStringPreferenceValue(resource, SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_INPUT_FILE_EXTENSIONS);
            String[] fileSuffixes = fileSuffix.split(System.getProperty("path.separator"));
            LaunchUtilities.getInputFiles(argsList, resource, javaProject, fileSuffixes, true);
            for (int i = 0; i < argsList.size(); i++)
            {
                args.append(argsList.get(i));
            }
        }

        return args.toString();
    }

    private String getVMArguments(IResource resource)
    {
        StringBuffer args = new StringBuffer(LaunchUtilities.getDefaultVMArguments(javaProject.getProject()));

        String persistenceUnit = ProjectHelper.getStringPreferenceValue(resource, SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_PERSISTENCE_UNIT);
        String propertiesFilename = ProjectHelper.getStringPreferenceValue(resource, SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_PROPERTIES_FILE);
        String connectionURL = ProjectHelper.getStringPreferenceValue(resource, SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_DATASTORE_URL);
        String connectionDriver = ProjectHelper.getStringPreferenceValue(resource, SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_DATASTORE_DRIVERNAME);
        String connectionUser = ProjectHelper.getStringPreferenceValue(resource, SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_DATASTORE_USERNAME);
        String connectionPassword = ProjectHelper.getStringPreferenceValue(resource, SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_DATASTORE_PASSWORD);

        if (propertiesFilename.length() > 0)
        {
        	// Will use -props to define the datastore
        }
        else if (connectionURL.trim().length() > 0)
        {
            // Add on the args for the connection to the datastore
            args.append(" -Ddatanucleus.ConnectionDriverName=\"");
            args.append(connectionDriver);
            args.append("\"");

            args.append(" -Ddatanucleus.ConnectionURL=\"");
            args.append(connectionURL);
            args.append("\"");

            args.append(" -Ddatanucleus.ConnectionUserName=");
            args.append(connectionUser);

            args.append(" -Ddatanucleus.ConnectionPassword=");
            args.append(connectionPassword);
        }
        else if (persistenceUnit != null && persistenceUnit.trim().length() > 0)
        {
        	// Will use -pu to define the datastore
        }

        // Get any additional arguments from the wizard
        String additionalVMArgs = ProjectHelper.getStringPreferenceValue(resource, SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_VM_ARGS);
        if (additionalVMArgs != null && !additionalVMArgs.trim().isEmpty())
        {
        	args.append(additionalVMArgs);
        }

        return args.toString();
    }
}