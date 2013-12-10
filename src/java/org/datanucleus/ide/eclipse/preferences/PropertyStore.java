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
2007 Andy Jefferson - fixed to be like the referenced source
    ...
**********************************************************************/
package org.datanucleus.ide.eclipse.preferences;

import java.io.IOException;
import java.io.OutputStream;

import org.datanucleus.ide.eclipse.Plugin;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;


/**
 * Store of properties for a project (as opposed to preferences for all projects).
 * This is taken from the Eclipse article
 * http://www.eclipse.org/articles/Article-Mutatis-mutandis/overlay-pages.html
 * Provides access to the workbenchStore for defaults, and to stored values where
 * there are any.
 */
public class PropertyStore extends PreferenceStore
{
    private IResource resource;

    private IPreferenceStore workbenchStore;

    private String pageId;

    private boolean inserting = false;

    public PropertyStore(IResource resource, IPreferenceStore workbenchStore, String pageId)
    {
        this.resource = resource;
        this.workbenchStore = workbenchStore;
        this.pageId = pageId;
    }

    /** * Write modified values back to properties ** */

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.IPersistentPreferenceStore#save()
     */
    public void save() throws IOException
    {
        writeProperties();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferenceStore#save(java.io.OutputStream,
     * java.lang.String)
     */
    public void save(OutputStream out, String header) throws IOException
    {
        writeProperties();
    }

    /**
     * Writes modified preferences into resource properties.
     */
    private void writeProperties() throws IOException
    {
        String[] preferences = super.preferenceNames();
        for (int i = 0; i < preferences.length; i++)
        {
            String name = preferences[i];
            try
            {
                setProperty(name, getString(name));
            }
            catch (CoreException e)
            {
                Plugin.logException(e);
            }
        }
    }

    /**
     * Convenience method to set a property
     * @param name the preference name
     * @param value the property value or null to delete the property
     * @throws CoreException
     */
    private void setProperty(String name, String value) throws CoreException
    {
        resource.setPersistentProperty(new QualifiedName(pageId, name), value);
    }

    /** * Get default values (Delegate to workbench store) ** */

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultBoolean(java.lang.String)
     */
    public boolean getDefaultBoolean(String name)
    {
        return workbenchStore.getDefaultBoolean(name);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultDouble(java.lang.String)
     */
    public double getDefaultDouble(String name)
    {
        return workbenchStore.getDefaultDouble(name);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultFloat(java.lang.String)
     */
    public float getDefaultFloat(String name)
    {
        return workbenchStore.getDefaultFloat(name);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultInt(java.lang.String)
     */
    public int getDefaultInt(String name)
    {
        return workbenchStore.getDefaultInt(name);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultLong(java.lang.String)
     */
    public long getDefaultLong(String name)
    {
        return workbenchStore.getDefaultLong(name);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferenceStore#getDefaultString(java.lang.String)
     */
    public String getDefaultString(String name)
    {
        return workbenchStore.getDefaultString(name);
    }

    /** Get property values */

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferenceStore#getBoolean(java.lang.String)
     */
    public boolean getBoolean(String name)
    {
        insertValue(name);
        return super.getBoolean(name);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferenceStore#getDouble(java.lang.String)
     */
    public double getDouble(String name)
    {
        insertValue(name);
        return super.getDouble(name);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferenceStore#getFloat(java.lang.String)
     */
    public float getFloat(String name)
    {
        insertValue(name);
        return super.getFloat(name);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferenceStore#getInt(java.lang.String)
     */
    public int getInt(String name)
    {
        insertValue(name);
        return super.getInt(name);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferenceStore#getLong(java.lang.String)
     */
    public long getLong(String name)
    {
        insertValue(name);
        return super.getLong(name);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferenceStore#getString(java.lang.String)
     */
    public String getString(String name)
    {
        insertValue(name);
        return super.getString(name);
    }

    /**
     * @param name
     */
    private synchronized void insertValue(String name)
    {
        if (inserting)
            return;
        if (super.contains(name))
            return;
        inserting = true;
        String prop = null;
        try
        {
            prop = getProperty(name);
        }
        catch (CoreException e)
        {
        }
        if (prop == null)
            prop = workbenchStore.getString(name);
        if (prop != null)
            setValue(name, prop);
        inserting = false;
    }

    /**
     * Convenience method to fetch a property for this page.
     * @param name - the preference name
     * @return the property value
     * @throws CoreException
     */
    private String getProperty(String name) throws CoreException
    {
        return resource.getPersistentProperty(new QualifiedName(pageId, name));
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferenceStore#contains(java.lang.String)
     */
    public boolean contains(String name)
    {
        return workbenchStore.contains(name);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferenceStore#setToDefault(java.lang.String)
     */
    public void setToDefault(String name)
    {
        setValue(name, getDefaultString(name));
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferenceStore#isDefault(java.lang.String)
     */
    public boolean isDefault(String name)
    {
        String defaultValue = getDefaultString(name);
        if (defaultValue == null)
            return false;
        return defaultValue.equals(getString(name));
    }
}