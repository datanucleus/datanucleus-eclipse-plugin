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
    ...
**********************************************************************/
package org.datanucleus.ide.eclipse.wizard.schematool;

import org.datanucleus.ide.eclipse.Localiser;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbench;


/**
 * Main dialog page for the SchemaTool wizard.
 * @version $Revision: 1.8 $
 */
public class SchemaToolMainPage extends WizardPage implements Listener
{
    IWorkbench workbench;

    IStructuredSelection selection;

    SchemaToolPanel control;

    /**
     * Constructor for SchemaToolMainPage.
     */
    public SchemaToolMainPage(IWorkbench workbench, IStructuredSelection selection)
    {
        super("Page1");
        setTitle(Localiser.getString("SchemaToolWizard.Primary.Title"));
        setDescription(Localiser.getString("SchemaToolWizard.Primary.Description"));
        this.workbench = workbench;
        this.selection = selection;
    }

    public void createControl(Composite parent)
    {
        SchemaToolWizard wizard = (SchemaToolWizard) getWizard();
        SchemaToolModel model = wizard.model;

        // set the composite as the control for this page
        control = new SchemaToolPanel(parent, SWT.NONE);
        // obtain last state from the model
        control.getDriverNameText().setText(model.getConnectionDriverName());
        control.getURLText().setText(model.getConnectionURL());
        control.getUsernameText().setText(model.getUser());
        if (model.getJdbcDriverPath() != null)
        {
            control.getDriverJarText().setText(model.getJdbcDriverPath());
        }
        control.getPropertiesFileNameText().setText(model.getPropertiesFileName());

        control.getCheckboxButtonDumpToFile().setSelection(model.getDumpToFile());
        control.getFileNameText().setText(model.getFileName());

        // compute enablement according to the state
        control.computeEnablement();

        setControl(control);
    }

    public void handleEvent(Event event)
    {
        saveModel();
    }

    public boolean isPageComplete()
    {
        saveModel();
        return true;
    }

    public void saveModel()
    {
        SchemaToolWizard wizard = (SchemaToolWizard) getWizard();
        SchemaToolModel model = wizard.model;
        model.setPassword(control.getPasswordText().getText());
        model.setConnectionDriverName(control.getDriverNameText().getText());
        model.setConnectionURL(control.getURLText().getText());
        model.setUser(control.getUsernameText().getText());
        model.setPropertiesFileName(control.getPropertiesFileNameText().getText());
        model.setDumpToFile(control.getCheckboxButtonDumpToFile().getSelection());
        model.setFileName(control.getFileNameText().getText());
        model.setJdbcDriverPath(control.getDriverJarText().getText());

        String options = null;
        if (control.getRadioButtonCreate().getSelection())
        {
            options = "-create ";
        }
        else if (control.getRadioButtonDelete().getSelection())
        {
            options = "-delete ";
        }
        else if (control.getRadioButtonValidate().getSelection())
        {
            options = "-validate ";
        }
        else if (control.getRadioButtonDBInfo().getSelection())
        {
            options = "-dbinfo ";
        }
        else if (control.getRadioButtonSchemaInfo().getSelection())
        {
            options = "-schemainfo ";
        }

        if (control.getCheckboxButtonDumpToFile().getSelection())
        {
            // DDL output
            String fileName = control.getFileNameText().getText();
            if (fileName != null && fileName.trim().length() > 0)
            {
                IPath path = ResourcesPlugin.getWorkspace().getRoot().getLocation();
                path = path.append(control.getFileNameText().getText());

                options = options + "-ddlFile " + "\"" + path.toOSString() + "\"";
            }
        }

        model.setOptions(options);
    }
}