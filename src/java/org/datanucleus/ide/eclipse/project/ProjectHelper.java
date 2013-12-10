/**********************************************************************
 Copyright (c) 2005 Michael Grundmann and others.
 All rights reserved. This program and the accompanying materials
 are made available under the terms of the JPOX License v1.0
 which accompanies this distribution. 

 Contributors:
 ...
 **********************************************************************/
package org.datanucleus.ide.eclipse.project;

import org.datanucleus.ide.eclipse.Plugin;
import org.datanucleus.ide.eclipse.preferences.PropertyAndPreferencePage;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.IPreferenceStore;


/**
 * Utility class to support nature usage and project specific properties. If special properties are 
 * set for the underlying resource we use them, otherwise we get the common values from the plugin's 
 * preference store.
 * 
 * @version $Revision: 1.4 $
 */
public class ProjectHelper
{
    private static IPreferenceStore store = Plugin.getDefault().getPreferenceStore();

    public static void addNature(IProject project, String natureId)
    {
        if (!hasNature(project))
        {
            try
            {
                IProjectDescription description = project.getDescription();
                String[] natures = description.getNatureIds();
                String[] newNatures = new String[natures.length + 1];
                System.arraycopy(natures, 0, newNatures, 0, natures.length);
                newNatures[natures.length] = natureId;
                description.setNatureIds(newNatures);
                project.setDescription(description, null);
            }
            catch (CoreException e)
            {
                Plugin.logException(e);
            }
        }
    }

    public static boolean getBuilderExistence(IProject project, String builderId)
    {
        boolean builderFound = false;
        try
        {
            IProjectDescription description = project.getDescription();
            ICommand[] commands = description.getBuildSpec();
            for (int i = 0; i < commands.length; ++i)
            {
                if (commands[i].getBuilderName().equals(builderId))
                {
                    builderFound = true;
                }
            }
        }
        catch (CoreException e)
        {
            Plugin.logException(e);
        }
        return builderFound;
    }

    public static boolean getBooleanPreferenceValue(IResource resource, String pageId, String name)
    {
        IProject project = resource.getProject();
        String value = null;
        if (useProjectSettings(project, pageId))
            value = getProperty(resource, pageId, name);
        if (value != null)
        {
            if (value.equals(IPreferenceStore.TRUE))
                return true;
            else
                return false;
        }
        value = store.getString(name);
        if (value != null)
        {
            if (value.equals(IPreferenceStore.TRUE))
                return true;
            else
                return false;
        }
        return false;
    }

    public static String getStringPreferenceValue(IResource resource, String pageId, String name)
    {
        IProject project = resource.getProject();
        String value = null;
        if (useProjectSettings(project, pageId))
            value = getProperty(resource, pageId, name);
        if (value != null)
            return value;
        return store.getString(name);
    }

    public static boolean hasNature(IProject project)
    {
        boolean hasNature = false;
        try
        {
            hasNature = project.hasNature(ProjectNature.NATURE);
        }
        catch (CoreException e)
        {
        }
        return hasNature;
    }

    /**
     * @param project
     * @return true if using project settings 
     */
    private static boolean useProjectSettings(IResource resource, String pageId)
    {
        String use = getProperty(resource, pageId, PropertyAndPreferencePage.USEPROJECTSETTINGS);
        return "true".equals(use);
    }

    private static String getProperty(IResource resource, String pageId, String key)
    {
        try
        {
            return resource.getPersistentProperty(new QualifiedName(pageId, key));
        }
        catch (CoreException e)
        {
        }
        return null;
    }

    public static void removeNature(IProject project, String natureId)
    {
        if (hasNature(project))
        {
            try
            {
                IProjectDescription description = project.getDescription();
                String[] natures = description.getNatureIds();
                String[] newNatures = new String[natures.length - 1];
                for (int i = 0, j = 0; i < natures.length; i++)
                {
                    if (!natures[i].equals(natureId))
                    {
                        newNatures[j++] = natures[i];
                    }
                }
                description.setNatureIds(newNatures);
                project.setDescription(description, null);
            }
            catch (CoreException e)
            {
                Plugin.logException(e);
            }
        }
    }

    public static void addBuilderToBuildSpec(IProject project, String builderId)
    {
        try
        {
            IProjectDescription description = project.getDescription();
            ICommand[] commands = description.getBuildSpec();
            for (int i = 0; i < commands.length; ++i)
            {
                if (commands[i].getBuilderName().equals(builderId))
                {
                    return;
                }
            }
            ICommand command = description.newCommand();
            command.setBuilderName(builderId);
            ICommand[] newCommands = new ICommand[commands.length + 1];
            System.arraycopy(commands, 0, newCommands, 0, commands.length);
            newCommands[newCommands.length - 1] = command;
            description.setBuildSpec(newCommands);
            project.setDescription(description, null);
        }
        catch (CoreException e)
        {
            Plugin.logException(e);
        }
    }

    public static void removeBuilderFromBuildSpec(IProject project, String builderId)
    {
        try
        {
            IProjectDescription description = project.getDescription();
            ICommand[] commands = description.getBuildSpec();
            boolean found = false;
            for (int i = 0; i < commands.length; ++i)
            {
                if (commands[i].getBuilderName().equals(builderId))
                {
                    found = true;
                }
            }
            if (!found)
            {
                return;
            }
            ICommand[] newCommands = new ICommand[commands.length - 1];
            for (int i = 0, j = 0; i < newCommands.length; i++)
            {
                if (!commands[i].equals(builderId))
                {
                    newCommands[j++] = commands[i];
                }
            }
            description.setBuildSpec(newCommands);
            project.setDescription(description, null);
        }
        catch (CoreException e)
        {
            Plugin.logException(e);
        }
    }
}
