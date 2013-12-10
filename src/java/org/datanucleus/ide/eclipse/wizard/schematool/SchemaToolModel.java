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
2004 Andy Jefferson - added log file
2007 Andy Jefferson - added props, api, verbose, filename
    ...
**********************************************************************/
package org.datanucleus.ide.eclipse.wizard.schematool;

/**
 * Model for the SchemaTool process, storing all control data that we could use as input.
 */
public class SchemaToolModel
{
    /** path with several URLs separated by ";" semi-colon **/
    private String jdbcDriverPath;

    /** URL for datastore. */
    private String connectionURL;

    /** Class name of JDBC driver. */
    private String connectionDriverName;

    /** Username for connection to datastore. */
    private String user;

    /** Password for connection to datastore. */
    private String password;

    /** Filename of a properties file defining the datastore to use. */
    private String propertiesFilename;

    private String options;

    private String additionalVMArguments;

    /** Whether to dump the DDL to a file. */
    private boolean dumpToFile;

    /** Filename to use for dumping the DDL. */
    private String fileName;

    /** PersistenceUNit to use with SchemaTool. */
    private String persistenceUnit;

    /** Whether to run in verbose mode. */
    private boolean verbose;

    public String getConnectionDriverName()
    {
        return connectionDriverName;
    }

    public void setConnectionDriverName(String connectionDriverName)
    {
        this.connectionDriverName = connectionDriverName;
    }
    
    /**
     * Accessor for the driver path with 0 or more URLs separated by ";" semi-colon 
     * @param jdbcDriverPath may be null
     */    
    public String getJdbcDriverPath()
    {
        return jdbcDriverPath;
    }

    /**
     * Accessor for the driver path with 0 or more URLs separated by ";" semi-colon 
     * @param jdbcDriverPath may be null or a value of 1 or more URLs separated by ";" semi-colon 
     */
    public void setJdbcDriverPath(String jdbcDriverPath)
    {
        this.jdbcDriverPath = jdbcDriverPath;
    }

    public String getConnectionURL()
    {
        return connectionURL;
    }

    public void setConnectionURL(String connectionURL)
    {
        this.connectionURL = connectionURL;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getPropertiesFileName()
    {
        return propertiesFilename;
    }

    public void setPropertiesFileName(String name)
    {
        this.propertiesFilename = name;
    }

    public String getOptions()
    {
        return options;
    }

    public void setOptions(String options)
    {
        this.options = options;
    }

    public String getAdditionalVMArguments()
    {
        return additionalVMArguments;
    }

    public void setAdditionalVMArguments(String additionalVMArguments)
    {
        this.additionalVMArguments = additionalVMArguments;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public boolean getDumpToFile()
    {
        return dumpToFile;
    }

    public void setDumpToFile(boolean dumpToFile)
    {
        this.dumpToFile = dumpToFile;
    }

    public String getPersistenceUnit()
    {
        return persistenceUnit;
    }

    public void setPersistenceUnit(String unit)
    {
        this.persistenceUnit = unit;
    }

    public boolean getVerbose()
    {
        return verbose;
    }

    public void setVerbose(boolean verbose)
    {
        this.verbose = verbose;
    }
}