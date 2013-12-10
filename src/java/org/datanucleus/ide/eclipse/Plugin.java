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
2005 Michael Grundmann - Added support for logging.
    ...
**********************************************************************/
package org.datanucleus.ide.eclipse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Entry point for the DataNucleus Eclipse plugin.
 * 
 * @version $Revision: 1.7 $
 */
public class Plugin extends AbstractUIPlugin
{
    /** Status code indicating an unexpected internal error. */
    public static final int INTERNAL_ERROR = 120;

    // The shared instance.
    private static Plugin plugin;

    // Resource bundle.
    private ResourceBundle resourceBundle;

    /**
     * Unique identifier constant (value <code>"org.datanucleus.ide.eclipse"</code>)
     */
    public static final String ID = "org.datanucleus.ide.eclipse";

    public static final String EXTENSION_POINT_URLTEMPLATE = ID + ".urlTemplate";

    private static final String EMPTY_STRING = "";

    /**
     * The constructor.
     */
    public Plugin()
    {
        super();
        plugin = this;
        try
        {
            resourceBundle = ResourceBundle.getBundle("org.datanucleus.ide.eclipse.JDOResources");
        }
        catch (MissingResourceException x)
        {
            try
            {
                // WORKAROUND: when using export, the PDE export tool, puts the properties file under java in the jar
                resourceBundle = ResourceBundle.getBundle("java.org.datanucleus.ide.eclipse.JDOResources");
            }
            catch (MissingResourceException x1)
            {
                resourceBundle = null;
            }
        }
    }

    /**
     * This method is called upon plug-in activation
     */
    public void start(BundleContext context) throws Exception
    {
        super.start(context);
    }

    /**
     * This method is called when the plug-in is stopped
     */
    public void stop(BundleContext context) throws Exception
    {
        super.stop(context);
    }

    /**
     * Returns the shared instance.
     */
    public static Plugin getDefault()
    {
        return plugin;
    }

    public static String getPluginId()
    {
        return getDefault().getBundle().getSymbolicName();
    }

    /**
     * Returns the string from the plugin's resource bundle, or 'key' if not found.
     */
    public static String getResourceString(String key)
    {
        ResourceBundle bundle = Plugin.getDefault().getResourceBundle();
        try
        {
            return (bundle != null) ? bundle.getString(key) : key;
        }
        catch (MissingResourceException e)
        {
            return key;
        }
    }

    public static IWorkspace getWorkspace()
    {
        return ResourcesPlugin.getWorkspace();
    }

    public static Shell getActiveWorkbenchShell()
    {
        IWorkbenchWindow window = getActiveWorkbenchWindow();
        if (window != null)
        {
            return window.getShell();
        }
        return null;
    }

    public static IWorkbenchWindow getActiveWorkbenchWindow()
    {
        return getDefault().getWorkbench().getActiveWorkbenchWindow();
    }

    /**
     * Utility method to compute an absolute <code>URL</code> for a given path relative to the plugin bundle.
     * @param path The path relative to the plugin's root
     * @return The absolute <code>URL</code> for the given path
     * @throws IOException
     */
    public static URL getBundleRelativeURL(String path) throws IOException
    {
        Bundle bundle = Plugin.getDefault().getBundle();
        URL logFileURL = bundle.getEntry(path);
        if (logFileURL != null)
        {
            return FileLocator.resolve(logFileURL);
        }
        return null;
    }

    public static IPath getPathForBundleFile(String path) throws IOException
    {
        Bundle bundle = Plugin.getDefault().getBundle();
        URL logFileURL = bundle.getEntry(path);
        if (logFileURL != null)
        {
            return new Path(FileLocator.resolve(logFileURL).getPath());
        }
        return null;
    }

    public ResourceBundle getResourceBundle()
    {
        return resourceBundle;
    }

    public static void logError(String message)
    {
        IStatus status = new Status(IStatus.ERROR, getPluginId(), IStatus.OK, message, null);
        ResourcesPlugin.getPlugin().getLog().log(status);
    }

    public static void logException(Throwable e)
    {
        if (e instanceof InvocationTargetException)
        {
            e = ((InvocationTargetException) e).getTargetException();
        }

        IStatus status = null;
        if (e instanceof CoreException)
        {
            status = ((CoreException) e).getStatus();
        }
        else
        {
            String message = e.getMessage();
            if (message == null)
                message = e.toString();
            status = new Status(IStatus.ERROR, getPluginId(), IStatus.OK, message, e);
        }
        ResourcesPlugin.getPlugin().getLog().log(status);
    }

    /**
     * Logs the specified status with this plug-in's log.
     * @param status status
     */
    public static void log(IStatus status)
    {
        getDefault().getLog().log(status);
    }

    /**
     * Writes the message to the plug-in's log
     * @param message the text to write to the log
     */
    public static void log(String message, Throwable exception)
    {
        IStatus status = newErrorStatus(message, exception);
        log(status);
    }

    /**
     * Logs the specified throwable with this plug-in's log.
     * @param t throwable to log
     */
    public static void log(Throwable t)
    {
        IStatus status = new Status(IStatus.ERROR, ID, INTERNAL_ERROR, "Error logged from Ant UI: ", t); //$NON-NLS-1$
        log(status);
    }

    /**
     * Returns a new <code>IStatus</code> for this plug-in
     */
    public static IStatus newErrorStatus(String message, Throwable exception)
    {
        if (message == null)
        {
            message = EMPTY_STRING;
        }
        return new Status(IStatus.ERROR, ID, 0, message, exception);
    }    
}