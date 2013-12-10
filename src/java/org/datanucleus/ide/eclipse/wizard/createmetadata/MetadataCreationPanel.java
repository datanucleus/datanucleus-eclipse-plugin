/**********************************************************************
 Copyright (c) 2005 Michael Grundmann and others.
 All rights reserved. This program and the accompanying materials
 are made available under the terms of the JPOX License v1.0
 which accompanies this distribution. 

 Contributors:
 ...
 **********************************************************************/
package org.datanucleus.ide.eclipse.wizard.createmetadata;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Michael Grundmann
 * @version $Revision: 1.2 $
 */
public class MetadataCreationPanel extends Composite
{
    private Label fileLabel;

    private Text fileText;

    /**
     * Constructor calling the inherited super-constructor.
     * 
     * @param parent The parent Composite
     * @param style flags for this widget
     */
    public MetadataCreationPanel(Composite parent, int style)
    {
        super(parent, style);
        createComposite();
    }

    /**
     * Create this widget's content.
     */
    private void createComposite()
    {
        GridLayout layout = new GridLayout();
        this.setLayout(layout);
        layout.numColumns = 2;

        fileLabel = new Label(this, SWT.NONE);
        fileText = new Text(this, SWT.BORDER);
        fileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        fileLabel.setText("File name:");
    }

    /**
     * @return The widget for the file name.
     */
    public Text getFileText()
    {
        return fileText;
    }
}
