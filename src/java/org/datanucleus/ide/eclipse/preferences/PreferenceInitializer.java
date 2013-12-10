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
2008 Andy Jefferson - default for file types, API, enhancerName etc
    ...
**********************************************************************/
package org.datanucleus.ide.eclipse.preferences;

import java.io.IOException;

import org.datanucleus.ide.eclipse.Plugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Initialize the preference values for all common settings in the plugin.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer
{
    public void initializeDefaultPreferences()
    {
        IPreferenceStore store = Plugin.getDefault().getPreferenceStore();

        // General
        store.setDefault(PreferenceConstants.PERSISTENCE_API, "JDO");
        String loggingConfiguration = "";
        try
        {
            loggingConfiguration = Plugin.getPathForBundleFile("log4j.properties").toOSString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        store.setDefault(PreferenceConstants.LOGGING_CONFIGURATION_FILE, loggingConfiguration);
        store.setDefault(PreferenceConstants.USE_PROJECT_CLASSPATH, true);

        // Enhancer
        String val = "jdo" + System.getProperty("path.separator") + "class";
        store.setDefault(PreferenceConstants.ENHANCER_VERBOSE_MODE, false);
        store.setDefault(PreferenceConstants.ENHANCER_INPUT_FILE_EXTENSIONS, val);

        // SchemaTool
        store.setDefault(PreferenceConstants.SCHEMATOOL_INPUT_FILE_EXTENSIONS, val);
    }
}