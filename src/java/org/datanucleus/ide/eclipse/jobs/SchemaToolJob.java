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
2007 Andy Jefferson - support input file suffix specification
    ...
**********************************************************************/
package org.datanucleus.ide.eclipse.jobs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.datanucleus.ide.eclipse.Localiser;
import org.datanucleus.ide.eclipse.Plugin;
import org.datanucleus.ide.eclipse.preferences.GeneralPreferencePage;
import org.datanucleus.ide.eclipse.preferences.PreferenceConstants;
import org.datanucleus.ide.eclipse.preferences.SchemaToolPreferencePage;
import org.datanucleus.ide.eclipse.project.ProjectHelper;
import org.datanucleus.ide.eclipse.wizard.schematool.SchemaToolModel;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * Class that performs the SchemaTool process.
 */
public class SchemaToolJob extends Job implements IDebugEventSetListener
{
    private static final String NAME = "DataNucleus Schema Tool";

    private static final String MAINCLASS = "org.datanucleus.store.schema.SchemaTool";

    private IJavaProject javaProject;

    private SchemaToolModel model;

    private IProcess process;

    private boolean finished = false;

    private boolean failure = false;

    public SchemaToolJob(IJavaProject javaProject, SchemaToolModel model)
    {
        super(NAME);
        this.javaProject = javaProject;
        this.model = model;
    }

    protected IStatus run(IProgressMonitor monitor)
    {
        monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
        try
        {
            ILaunch launch = createLaunch();
            process = launch.getProcesses()[0];
            DebugPlugin debug = DebugPlugin.getDefault();
            debug.addDebugEventListener(this);
            while (!finished)
            {
                if (monitor.isCanceled())
                {
                    launch.terminate();
                    return Status.CANCEL_STATUS;
                }
            }
        }
        catch (CoreException e)
        {
            String message = getName() + " " + Localiser.getString("SchemaToolJob_messageCompletedError");
            return new Status(IStatus.ERROR, Plugin.getPluginId(), 0, message, e);
        }
        if (failure)
        {
            String message = getName() + " " + Localiser.getString("SchemaToolJob_messageCompletedError");
            return new Status(IStatus.ERROR, Plugin.getPluginId(), 0, message, null);
        }
        if (isModal(this))
        {
            showResults();
        }
        else
        {
            setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
            setProperty(IProgressConstants.ACTION_PROPERTY, getCompletedAction());
        }
        monitor.done();
        return Status.OK_STATUS;
    }

    /**
     * Create launch adding JDBC drivers to classpath
     * @return
     * @throws CoreException
     */
    protected ILaunch createLaunch() throws CoreException
    {
        List classpath = LaunchUtilities.getDefaultClasspath(javaProject);
        if (model.getJdbcDriverPath() != null)
        {
            String[] paths = model.getJdbcDriverPath().split(";");
            for( int i=0; i<paths.length; i++)
            {
                IPath jdbcDriverPath;
                try
                {
                    jdbcDriverPath = new Path(new URL(paths[i]).getFile());
                    classpath.add(JavaRuntime.newArchiveRuntimeClasspathEntry(jdbcDriverPath).getMemento());
                }
                catch (MalformedURLException e)
                {
                }
            }
        }
        String vmArgs = getVMArguments(javaProject.getProject());
        String workingDir = null; // LaunchUtilities.getWorkingDir(javaProject);
        String programArgs = getProgramArguments(javaProject.getProject(), javaProject);
        return LaunchUtilities.launch(javaProject, NAME, MAINCLASS, classpath, vmArgs, workingDir, programArgs, MAINCLASS, true);
    }

    private String getVMArguments(IResource resource)
    {
        StringBuffer args = new StringBuffer(LaunchUtilities.getDefaultVMArguments(javaProject.getProject()));

        String persistenceUnit = model.getPersistenceUnit();
        persistenceUnit = ProjectHelper.getStringPreferenceValue(resource, SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_PERSISTENCE_UNIT);
        String propertiesFilename = model.getPropertiesFileName().trim();

        if (propertiesFilename.length() > 0)
        {
        	// Will use -props to define the datastore
        }
        else if (model.getConnectionURL().trim().length() > 0)
        {
            // Add on the args for the connection to the datastore
            args.append(" -Ddatanucleus.ConnectionDriverName=\"");
            args.append(model.getConnectionDriverName());
            args.append("\"");

            args.append(" -Ddatanucleus.ConnectionURL=\"");
            args.append(model.getConnectionURL());
            args.append("\"");

            args.append(" -Ddatanucleus.ConnectionUserName=");
            args.append(model.getUser());

            args.append(" -Ddatanucleus.ConnectionPassword=");
            args.append(model.getPassword());
        }
        else if (persistenceUnit != null && persistenceUnit.trim().length() > 0)
        {
        	// Will use -pu to define the datastore
        }

        // Get any additional arguments from the wizard
        args.append(model.getAdditionalVMArguments());

        return args.toString();
    }

    private String getProgramArguments(IResource resource, IJavaProject javaProject2)
    {
        StringBuffer args = new StringBuffer();

        // SchemaTool option to invoke
        args.append(model.getOptions());

        // Properties file
        String propertiesFilename = model.getPropertiesFileName();
        if (propertiesFilename != null && propertiesFilename.trim().length() > 0)
        {
            args.append(" -props \"").append(propertiesFilename).append("\"");
        }

        // API
        String apiName = ProjectHelper.getStringPreferenceValue(resource, GeneralPreferencePage.PAGE_ID,
            PreferenceConstants.PERSISTENCE_API);
        if (apiName != null && apiName.trim().length() > 0)
        {
            args.append(" -api ").append(apiName.trim());
        }

        // PersistenceUnit
        boolean usingPersistenceUnit = false;
        String persistenceUnit = model.getPersistenceUnit();
        if (persistenceUnit != null && persistenceUnit.trim().length() > 0)
        {
            usingPersistenceUnit = true;
            args.append(" -pu ").append(persistenceUnit.trim());
        }

        // verbose
        boolean verboseMode = model.getVerbose();
        if (verboseMode)
        {
            args.append(" -v ");
        }

        // Input files (jdo/class)
        if (!usingPersistenceUnit)
        {
            List argsList = new ArrayList();
            String fileSuffix = ProjectHelper.getStringPreferenceValue(resource, 
                SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_INPUT_FILE_EXTENSIONS);
            String[] fileSuffixes = fileSuffix.split(System.getProperty("path.separator"));
            LaunchUtilities.getInputFiles(argsList, resource, javaProject, fileSuffixes, true);
            for (int i = 0; i < argsList.size(); i++)
            {
                args.append(argsList.get(i));
            }
        }

        return args.toString();
    }

    private void showResults()
    {
        Display.getDefault().asyncExec(new Runnable()
        {
            public void run()
            {
                getCompletedAction().run();
            }
        });
    }

    private boolean isModal(Job job)
    {
        Boolean isModal = (Boolean) getProperty(IProgressConstants.PROPERTY_IN_DIALOG);
        if (isModal == null)
            return false;
        return isModal.booleanValue();
    }

    private Action getCompletedAction()
    {
        return new Action(getName())
        {
            public void run()
            {
                String message = getName() + " " + Localiser.getString("SchemaToolJob_messageCompletedSuccess");
                MessageDialog.openInformation(getShell(), getName(), message);
            }
        };
    }

    protected Shell getShell()
    {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    }

    public void handleDebugEvents(DebugEvent[] events)
    {
        // if the event was a terminate...
        if (events[0].getKind() == DebugEvent.TERMINATE)
        {
            Object source = events[0].getSource();
            if (source instanceof IProcess)
            {
                IProcess process = (IProcess) source;
                // ...and the terminated process is the one we've started...
                if (SchemaToolJob.this.process.equals(process))
                {
                    try
                    {
                        if (process.getExitValue() != 0)
                            failure = true;
                        finished = true;
                    }
                    catch (DebugException e)
                    {
                        e.printStackTrace(); // TODO
                    }
                }
            }
        }
    }
}