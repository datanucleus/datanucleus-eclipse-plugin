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
2007 Andy Jefferson - renamed to Localiser and made similar to JPOX Core variant
    ...
**********************************************************************/
package org.datanucleus.ide.eclipse;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Container for localised messages used by the Eclipse plugin.
 *
 * @version $Revision: 1.1 $
 */
public class Localiser
{
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("org.datanucleus.ide.eclipse.Localisation");

    private Localiser()
    {
    }

    /**
     * Accessor for a localised string with a key.
     * @param key The key
     * @return The message
     */
    public static String getString(String key)
    {
        try
        {
            return RESOURCE_BUNDLE.getString(key);
        }
        catch (MissingResourceException e)
        {
            return '!' + key + '!';
        }
    }
}