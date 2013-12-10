/**********************************************************************
 Copyright (c) 2005 Michael Grundmann and others.
 All rights reserved. This program and the accompanying materials
 are made available under the terms of the JPOX License v1.0
 which accompanies this distribution. 

 Contributors:
 ...
 **********************************************************************/
package org.datanucleus.ide.eclipse.wizard.schematool;

import org.datanucleus.ide.eclipse.Localiser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;


public class SchemaToolSettingsPanel extends Composite
{
    private Table table;

    private Button addButton;

    private Button removeButton;

    public SchemaToolSettingsPanel(Composite parent, int style)
    {
        super(parent, style);
        createComposite();
    }

    private void createComposite()
    {
        GridLayout gridLayout = new GridLayout();
        gridLayout.verticalSpacing = 10;
        this.setLayout(gridLayout);

        createVMArgumentGroup();
    }

    private void createVMArgumentGroup()
    {
        Group group = new Group(this, SWT.NONE);
        group.setText(Localiser.getString("SchemaToolSettingsPanel_groupAdditionalVMArguments")); //$NON-NLS-1$
        group.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = layout.marginHeight = 10;
        group.setLayout(layout);

        table = new Table(group, SWT.BORDER | SWT.FULL_SELECTION);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        GridData gd = new GridData(SWT.FILL, SWT.NULL, true, false);
        gd.heightHint = 100;
        table.setLayoutData(gd);
        table.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                removeButton.setEnabled(table.getSelectionCount() > 0);
            }
        });
        table.addMouseListener(new MouseAdapter()
        {
            public void mouseDoubleClick(MouseEvent e)
            {
                int index = table.getSelectionIndex();
                TableItem item = table.getItem(index);
                SchemaToolPropertyInputDialog dialog = new SchemaToolPropertyInputDialog(getShell(), Localiser.getString("SchemaToolSettingsPanel_labelEditVMArguments"), item.getText(0), item.getText(1)); //$NON-NLS-1$
                dialog.open();

                if (dialog.getProperty() != null && dialog.getValue() != null)
                {
                    if (dialog.getProperty().length() > 0 && dialog.getValue().length() > 0)
                    {
                        item.setText(0, dialog.getProperty());
                        item.setText(1, dialog.getValue());
                    }
                }
            }
        });

        TableColumn propertyColumn = new TableColumn(table, SWT.LEFT);
        propertyColumn.setText(Localiser.getString("SchemaToolSettingsPanel_labelProperty")); //$NON-NLS-1$
        propertyColumn.setWidth(200);

        TableColumn valueColumn = new TableColumn(table, SWT.LEFT);
        valueColumn.setText(Localiser.getString("SchemaToolSettingsPanel_labelValue")); //$NON-NLS-1$
        valueColumn.setWidth(100);

        Composite buttonComposite = new Composite(group, SWT.NULL);
        buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        GridLayout buttonCompositeLayout = new GridLayout();
        buttonComposite.setLayout(buttonCompositeLayout);

        addButton = new Button(buttonComposite, SWT.PUSH);
        addButton.setText(Localiser.getString("SchemaToolSettingsPanel_labelAdd")); //$NON-NLS-1$
        addButton.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
        addButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                SchemaToolPropertyInputDialog dialog = new SchemaToolPropertyInputDialog(getShell(), Localiser.getString("SchemaToolSettingsPanel_labelNewVMArgument"), null, null); //$NON-NLS-1$
                dialog.open();

                if (dialog.getProperty() != null && dialog.getValue() != null)
                {
                    TableItem item = new TableItem(table, SWT.NONE, table.getItemCount());
                    if (dialog.getProperty().length() > 0 && dialog.getValue().length() > 0)
                    {
                        item.setText(0, dialog.getProperty());
                        item.setText(1, dialog.getValue());
                    }
                }
            }
        });

        removeButton = new Button(buttonComposite, SWT.PUSH);
        removeButton.setText(Localiser.getString("SchemaToolSettingsPanel_labelRemove")); //$NON-NLS-1$
        removeButton.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
        removeButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                table.remove(table.getSelectionIndex());
            }
        });
        removeButton.setEnabled(table.getSelectionCount() > 0);
    }

    protected String getAdditionalVMArguments()
    {
        StringBuffer buffer = new StringBuffer();
        TableItem[] items = table.getItems();
        for (int i = 0; i < items.length; i++)
        {
            TableItem item = items[i];
            buffer.append(" -D"); //$NON-NLS-1$
            buffer.append(item.getText(0));
            buffer.append("="); //$NON-NLS-1$
            buffer.append(item.getText(1));
        }
        return buffer.toString();
    }
}
