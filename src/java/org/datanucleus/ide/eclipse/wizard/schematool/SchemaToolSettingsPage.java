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

Contributors :
    ...
**********************************************************************/
package org.datanucleus.ide.eclipse.wizard.schematool;

import org.datanucleus.ide.eclipse.Localiser;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;


/**
 * Optional settings page for running SchemaTool.
 *
 * @version $Revision$
 */
public class SchemaToolSettingsPage extends WizardPage
{
    private SchemaToolSettingsPanel control;

    protected SchemaToolSettingsPage()
    {
        super("Page2");
        setTitle(Localiser.getString("SchemaToolWizard.Secondary.Title"));
        setDescription(Localiser.getString("SchemaToolWizard.Secondary.Description"));
    }

    public void createControl(Composite parent)
    {
        // set the composite as the control for this page
        control = new SchemaToolSettingsPanel(parent, SWT.NONE);

        setControl(control);
    }

    public boolean isPageComplete()
    {
        saveModel();
        return true;
    }

    protected void saveModel()
    {
        SchemaToolWizard wizard = (SchemaToolWizard) getWizard();
        SchemaToolModel model = wizard.getModel();

        model.setAdditionalVMArguments(control.getAdditionalVMArguments());
    }
}