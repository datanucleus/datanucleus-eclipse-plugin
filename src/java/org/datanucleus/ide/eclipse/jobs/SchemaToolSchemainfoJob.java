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

import org.eclipse.jdt.core.IJavaProject;

/**
 * Class that performs the SchemaTool "schemainfo" process.
 */
public class SchemaToolSchemainfoJob extends AbstractSchemaToolJob
{
    public SchemaToolSchemainfoJob(IJavaProject javaProject)
    {
        super(javaProject);
    }

	@Override
	protected String getMode() 
	{
		return "schemainfo";
	}
}