/**********************************************************************
Copyright (c) 2004 Erik Bengtson and others. All rights reserved.
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
2007 Andy Jefferson - removed "container" since inconsistent with the rest of the plugin
    ...
**********************************************************************/
package org.datanucleus.ide.eclipse.wizard.schematool;

import org.datanucleus.ide.eclipse.Plugin;
import org.datanucleus.ide.eclipse.jobs.SchemaToolJob;
import org.datanucleus.ide.eclipse.preferences.PreferenceConstants;
import org.datanucleus.ide.eclipse.preferences.SchemaToolPreferencePage;
import org.datanucleus.ide.eclipse.project.ProjectHelper;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * Wizard for running SchemaTool.
 */
public class SchemaToolWizard extends Wizard implements INewWizard
{
    /** Name under which the JDBC driver jar is stored for the wizard. */
    public static final String P_DRIVER_PATH = "jdbcDriverPath";

    /** Name under which the datastore driver name is stored for the wizard. */
    public static final String P_DRIVER_NAME = "driverName";

    /** Name under which the datastore URL is stored for the wizard. */
    public static final String P_CONNECTION_URL = "connectionURL";

    /** Name under which the datastore username is stored for the wizard. */
    public static final String P_USER = "user";

    /** Name under which the properties filename is stored for the wizard. */
    public static final String P_PROPERTIES_FILE_NAME = "propertiesFileName";

    /** Name under which whether to dump to file is stored for the wizard. */
    public static final String P_DUMPTOFILE = "dumpToFile";

    /** Name under which the DDL filename is stored for the wizard. */
    public static final String P_FILE_NAME = "fileName";

    /** Main wizard page */
    private SchemaToolMainPage schemaToolMainPage;

    /** Detailed settings wizard page. */
    private SchemaToolSettingsPage schemaToolSettingsPage;

    /** The model. */
    SchemaToolModel model;

    IJavaProject javaProject;

    // workbench selection when the wizard was started
    protected IStructuredSelection selection;

    // the workbench instance
    protected IWorkbench workbench;

    public SchemaToolWizard()
    {
        model = new SchemaToolModel();
    }

    public void addPages()
    {
        schemaToolMainPage = new SchemaToolMainPage(workbench, selection);
        schemaToolSettingsPage = new SchemaToolSettingsPage();

        initializeModelState();

        addPage(schemaToolMainPage);
        addPage(schemaToolSettingsPage);
    }

    @SuppressWarnings("deprecation")
    private void initializeModelState()
    {
        String url = Plugin.getDefault().getPluginPreferences().getString(P_CONNECTION_URL);
        if (url != null && url.trim().length() > 0)
        {
            // Take the values from the last time through here
            model.setConnectionDriverName(Plugin.getDefault().getPluginPreferences().getString(P_DRIVER_NAME));
            model.setConnectionURL(Plugin.getDefault().getPluginPreferences().getString(P_CONNECTION_URL));
            model.setUser(Plugin.getDefault().getPluginPreferences().getString(P_USER));
            model.setJdbcDriverPath(Plugin.getDefault().getPluginPreferences().getString(P_DRIVER_PATH));
            model.setPropertiesFileName(Plugin.getDefault().getPluginPreferences().getString(P_PROPERTIES_FILE_NAME));
        }
        else
        {
            // Take the values from the default for the plugin
            IResource resource = javaProject.getProject();
            model.setConnectionDriverName(ProjectHelper.getStringPreferenceValue(resource,
                SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_DATASTORE_DRIVERNAME));
            model.setConnectionURL(ProjectHelper.getStringPreferenceValue(resource,
                SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_DATASTORE_URL));
            model.setUser(ProjectHelper.getStringPreferenceValue(resource,
                SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_DATASTORE_USERNAME));
            model.setJdbcDriverPath(ProjectHelper.getStringPreferenceValue(resource,
                SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_DATASTORE_DRIVERJAR));
            model.setPropertiesFileName(ProjectHelper.getStringPreferenceValue(resource,
                SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_PROPERTIES_FILE));
        }

        // Only available from wizard
        model.setDumpToFile(Plugin.getDefault().getPluginPreferences().getBoolean(P_DUMPTOFILE));
        model.setFileName(Plugin.getDefault().getPluginPreferences().getString(P_FILE_NAME));

        // From plugin/project preferences
        model.setVerbose(ProjectHelper.getBooleanPreferenceValue(javaProject.getProject(),
            SchemaToolPreferencePage.PAGE_ID, PreferenceConstants.SCHEMATOOL_VERBOSE_MODE));
    }

    /**
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection)
    {
        this.workbench = workbench;
        this.selection = selection;
        if (selection == null)
        {
            return;
        }
        if (!IJavaProject.class.isAssignableFrom(selection.getFirstElement().getClass()))
        {
            return;
        }
        javaProject = (IJavaProject) selection.getFirstElement();
    }

    public boolean canFinish()
    {
        return true;
    }

    @SuppressWarnings("deprecation")
    public boolean performFinish()
    {
        try
        {
            schemaToolMainPage.saveModel();
            schemaToolSettingsPage.saveModel();
            // Save the state of the wizard
            Plugin.getDefault().getPluginPreferences().setValue(P_DRIVER_PATH, model.getJdbcDriverPath());
            Plugin.getDefault().getPluginPreferences().setValue(P_DRIVER_NAME, model.getConnectionDriverName());
            Plugin.getDefault().getPluginPreferences().setValue(P_CONNECTION_URL, model.getConnectionURL());
            Plugin.getDefault().getPluginPreferences().setValue(P_USER, model.getUser());
            Plugin.getDefault().getPluginPreferences().setValue(P_PROPERTIES_FILE_NAME, model.getPropertiesFileName());
            Plugin.getDefault().getPluginPreferences().setValue(P_DUMPTOFILE, model.getDumpToFile());
            Plugin.getDefault().getPluginPreferences().setValue(P_FILE_NAME, model.getFileName());

            // Create a job to run Schema Tool
            Job job = new SchemaToolJob(javaProject, model);
            job.setPriority(Job.SHORT);
            job.setUser(true);
            job.schedule();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * @return Returns the model.
     */
    public SchemaToolModel getModel()
    {
        return model;
    }
}