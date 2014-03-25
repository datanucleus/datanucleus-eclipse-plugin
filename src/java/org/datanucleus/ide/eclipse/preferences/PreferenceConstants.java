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
    ...
**********************************************************************/
package org.datanucleus.ide.eclipse.preferences;

import java.io.File;

/**
 * Convenience class with a series of constants.
 */
public interface PreferenceConstants
{
    String PERSISTENCE_API = "persistenceAPI";

    String LOGGING_CONFIGURATION_FILE = "loggingConfigurationFile";

    String CLASSPATH_ENTRIES = "classpathEntries";

    String USE_PROJECT_CLASSPATH = "useProjectClasspath";

    String ENHANCER_AUTO_ENHANCEMENT = "enhancerAutoEnhancement";

    String ENHANCER_VERBOSE_MODE = "enhancerVerboseMode";

    String ENHANCER_CAPTURE_OUTPUT = "enhancerCaptureOutput";

    String ENHANCER_INPUT_FILE_EXTENSIONS = "enhancerInputFileExtensions";

    String ENHANCER_PERSISTENCE_UNIT = "enhancerPersistenceUnit";

    /**
     * Whether to use a file-list-file.
     * <p>
     * See: <a href="http://www.datanucleus.org/servlet/jira/browse/NUCACCECLIPSE-11">NUCACCECLIPSE-11</a>
     */
    String ENHANCER_USE_FILE_LIST_FILE = "useFileListFile";

    /**
     * The default value for the setting {@link #ENHANCER_USE_FILE_LIST_FILE}.
     * <p>
     * This default value is <code>true</code> on Windows and <code>false</code> on all
     * other systems (primarily GNU/Linux).
     */
    boolean ENHANCER_USE_FILE_LIST_FILE_DEFAULT_VALUE = File.separatorChar == '\\';

    String SCHEMATOOL_INPUT_FILE_EXTENSIONS = "schematoolInputFileExtensions";

    String SCHEMATOOL_DATASTORE_DRIVERJAR = "schemaToolConnectionDriverJar";

    String SCHEMATOOL_DATASTORE_DRIVERNAME = "schemaToolConnectionDriverName";

    String SCHEMATOOL_DATASTORE_URL = "schemaToolConnectionURL";

    String SCHEMATOOL_DATASTORE_USERNAME = "schemaToolConnectionUsername";

    String SCHEMATOOL_DATASTORE_PASSWORD = "schemaToolConnectionPassword";

    String SCHEMATOOL_PROPERTIES_FILE = "schemaToolPropertiesFile";

    String SCHEMATOOL_VERBOSE_MODE = "schemaToolVerboseMode";

    String SCHEMATOOL_PERSISTENCE_UNIT = "schematoolPersistenceUnit";

}