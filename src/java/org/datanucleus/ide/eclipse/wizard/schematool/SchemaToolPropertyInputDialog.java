/**********************************************************************
 Copyright (c) 2005 Michael Grundmann and others.
 All rights reserved. This program and the accompanying materials
 are made available under the terms of the JPOX License v1.0
 which accompanies this distribution. 

 Contributors:
 ...
 **********************************************************************/
package org.datanucleus.ide.eclipse.wizard.schematool;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SchemaToolPropertyInputDialog extends Dialog
{
    private String title;

    private String property = ""; //$NON-NLS-1$

    private String value = ""; //$NON-NLS-1$

    private Text propertyText;

    private Text valueText;

    protected SchemaToolPropertyInputDialog(Shell parentShell, String title, String initialProperty, String initialValue)
    {
        super(parentShell);
        this.title = title;
        if (initialProperty == null)
            property = "";//$NON-NLS-1$
        else
            property = initialProperty;
        if (initialValue == null)
            value = "";//$NON-NLS-1$
        else
            value = initialValue;
    }

    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    protected void buttonPressed(int buttonId)
    {
        if (buttonId == IDialogConstants.OK_ID)
        {
            property = propertyText.getText().trim();
            value = valueText.getText().trim();
        }
        else
        {
            property = null;
            value = null;
        }
        super.buttonPressed(buttonId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell shell)
    {
        super.configureShell(shell);
        if (title != null)
            shell.setText(title);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent)
    {
        // create OK and Cancel buttons by default
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
        // do this here because setting the text will set enablement on the ok
        // button
        propertyText.setFocus();
        if (property != null)
        {
            propertyText.setText(property);
            propertyText.selectAll();
        }
        if (value != null)
        {
            valueText.setText(value);
            valueText.selectAll();
        }
    }

    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    protected Control createDialogArea(Composite parent)
    {
        Composite composite = (Composite) super.createDialogArea(parent);

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = layout.marginHeight = 10;
        layout.horizontalSpacing = 20;
        composite.setLayout(layout);

        Label label = new Label(composite, SWT.NULL);
        label.setText("Property:");
        propertyText = new Text(composite, SWT.SINGLE | SWT.BORDER);
        GridData gd = new GridData(SWT.FILL, SWT.NULL, true, false);
        gd.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
        propertyText.setLayoutData(gd);

        label = new Label(composite, SWT.NULL);
        label.setText("Value:");
        valueText = new Text(composite, SWT.SINGLE | SWT.BORDER);
        valueText.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));

        applyDialogFont(composite);
        return composite;
    }

    public String getProperty()
    {
        return property;
    }

    public String getValue()
    {
        return value;
    }
}
