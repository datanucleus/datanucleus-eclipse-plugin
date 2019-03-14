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
2004 Andy Jefferson - added log file prompt
2005 Michael Grundmann - added support for file output
    ...
**********************************************************************/
package org.datanucleus.ide.eclipse.wizard.schematool;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.datanucleus.ide.eclipse.Localiser;
import org.datanucleus.ide.eclipse.Plugin;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * Representation of the body of the SchemaTool panel.
 * @version $Revision: 1.12 $
 */
public class SchemaToolPanel extends Composite
{
    private Combo driverNameText;

    private Combo urlText;

    private Text driverJarText;
    
    private Button browse;
    
    private Text usernameText;

    private Text passwordText;

    private Text propertiesFileNameText;

    private Button propertiesFileNameBrowseButton;

    private Button radioButtonCreate;

    private Button radioButtonDelete;

    private Button radioButtonValidate;

    private Button radioButtonDBInfo;

    private Button radioButtonSchemaInfo;
    
    private Button checkboxButtonDumpToFile;

    private Text fileNameText;

    private Button fileNameBrowseButton;

    /**
     * TODO Input validation
     */
    public SchemaToolPanel(Composite parent, int style)
    {
        super(parent, style);
        createComposite();
    }

    private void createComposite()
    {
        GridLayout gridLayout = new GridLayout();
        gridLayout.verticalSpacing = 10;
        this.setLayout(gridLayout);

        createRunModeGroup();
        createConnectionGroup();
        createFileOutputGroup();
    }

    private void createRunModeGroup()
    {
        Group group = new Group(this, SWT.NONE);
        group.setText(Localiser.getString("SchemaToolPanel_groupToolMode"));
        GridData gd = new GridData(GridData.FILL_BOTH);
        group.setLayoutData(gd);

        GridLayout layout = new GridLayout();
        layout.numColumns = 4;
        layout.marginWidth = layout.marginHeight = 10;
        group.setLayout(layout);

        radioButtonCreate = new Button(group, SWT.RADIO);
        radioButtonCreate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        radioButtonCreate.setText(Localiser.getString("SchemaToolPanel_labelCreation"));
        radioButtonCreate.setSelection(true);

        radioButtonDelete = new Button(group, SWT.RADIO);
        radioButtonDelete.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        radioButtonDelete.setText(Localiser.getString("SchemaToolPanel_labelDeletion"));

        radioButtonValidate = new Button(group, SWT.RADIO);
        radioButtonValidate.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        radioButtonValidate.setText(Localiser.getString("SchemaToolPanel_labelValidation"));

        radioButtonDBInfo = new Button(group, SWT.RADIO);
        radioButtonDBInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        radioButtonDBInfo.setText(Localiser.getString("SchemaToolPanel_labelDatabaseInfo"));

        radioButtonSchemaInfo = new Button(group, SWT.RADIO);
        radioButtonSchemaInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        radioButtonSchemaInfo.setText(Localiser.getString("SchemaToolPanel_labelSchemaInfo"));
    }

    private void createConnectionGroup()
    {
        Group group = new Group(this, SWT.NONE);
        group.setText(Localiser.getString("SchemaToolPreferences.DatastoreDetails.Label"));
        GridData gd = new GridData(GridData.FILL_BOTH);
        group.setLayoutData(gd);

        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.marginWidth = layout.marginHeight = 10;
        layout.horizontalSpacing = 10;
        group.setLayout(layout);

        Label driverJarLabel = new Label(group, SWT.NONE);
        driverJarLabel.setText(Localiser.getString("SchemaToolPreferences.DriverJar.Label"));
        driverJarText = new Text(group, SWT.BORDER);
        driverJarText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));        
        browse = new Button(group, SWT.NONE);
        browse.setText("Browse");
        browse.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
        {
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
            {
                FileDialog dialog = new FileDialog(browse.getShell(), SWT.MULTI);
                dialog.setFilterExtensions(new String[]{"*.jar"}); //$NON-NLS-1$ 
                String result = dialog.open();
                if (result == null)
                {
                    return;
                }
                IPath filterPath = new Path(dialog.getFilterPath());
                String[] results = dialog.getFileNames();
                driverJarText.setText("");
                URL[] urls = new URL[results.length];
                for (int i = 0; i < results.length; i++)
                {
                    String jarName = results[i];
                    IPath path = filterPath.append(jarName).makeAbsolute();
                    try
                    {
                        urls[i] = path.toFile().toURI().toURL();
                    }
                    catch (MalformedURLException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                URLClassLoader loader = new URLClassLoader(urls);
                try
                {
                    driverNameText.removeAll();
                    for (int i = 0; i < results.length; i++)
                    {
                        try
                        {
                            String jarName = results[i];
                            IPath path = filterPath.append(jarName).makeAbsolute();
                            urls[i] = path.toFile().toURI().toURL();

                            if (driverJarText.getText().length()>0 )
                            {
                                driverJarText.setText(driverJarText.getText()+";");
                            }
                            driverJarText.setText(driverJarText.getText()+path.toFile().toURI().toURL().toString());
                            JarFile jar = new JarFile(path.toFile());
                            try
                            {
                                Enumeration entries = jar.entries();
                                while( entries.hasMoreElements() )
                                {
                                    String name = ((JarEntry)entries.nextElement()).getName();
                                    name = name.replace('/','.');
                                    name = name.replace('\\','.');
                                    if( name.endsWith(".class") )
                                    {
                                        name = name.substring(0,name.indexOf(".class"));
                                        try
                                        {
                                            Class cls = loader.loadClass(name);
                                            Class[] interfaces = cls.getInterfaces();
                                            for(int j=0; j<interfaces.length; j++)
                                            {
                                                if( interfaces[j].getName().equals("java.sql.Driver") )
                                                {
                                                    driverNameText.add(cls.getName());
                                                }
                                            }
                                        }
                                        catch (Throwable ex)
                                        {
                                        }
                                    }
                                }
                            }
                            finally
                            {
                                jar.close();
                            }
                        }
                        catch (MalformedURLException e1)
                        {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        catch (IOException e1)
                        {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }
                finally
                {
                    try
                    {
                        loader.close();
                    }
                    catch (IOException e1)
                    {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        });
        
        Label driverNameLabel = new Label(group, SWT.NONE);
        driverNameLabel.setText(Localiser.getString("SchemaToolPreferences.DriverName.Label"));
        driverNameText = new Combo(group, SWT.NONE);
        driverNameText.addModifyListener(new ModifyListener()
        {
        
            public void modifyText(ModifyEvent e)
            {
                String template = getUrlTemplate(driverNameText.getText());
                if( template != null )
                {
                    urlText.setText(template);
                }
            }
        
        });
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        driverNameText.setLayoutData(gd);

        Label urlLabel = new Label(group, SWT.NONE);
        urlLabel.setText(Localiser.getString("SchemaToolPreferences.ConnectionURL.Label"));
        urlText = new Combo(group, SWT.NONE);
        urlText.setToolTipText(Localiser.getString("SchemaToolPreferences.ConnectionURL.Tooltip"));
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        urlText.setLayoutData(gd);

        Label usernameLabel = new Label(group, SWT.NONE);
        usernameLabel.setText(Localiser.getString("SchemaToolPreferences.UserName.Label"));
        usernameText = new Text(group, SWT.BORDER);
        usernameText.setToolTipText(Localiser.getString("SchemaToolPreferences.UserName.Tooltip"));
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        usernameText.setLayoutData(gd);

        Label passwordLabel = new Label(group, SWT.NONE);
        passwordLabel.setText(Localiser.getString("SchemaToolPreferences.Password.Label"));
        passwordText = new Text(group, SWT.BORDER);
        passwordText.setToolTipText(Localiser.getString("SchemaToolPreferences.Password.Tooltip"));
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        passwordText.setLayoutData(gd);
        passwordText.setEchoChar('*');

        // Properties filename
        Label fileNameLabel = new Label(group, SWT.NONE);
        fileNameLabel.setText(Localiser.getString("SchemaToolPreferences.PropertiesFile.Label"));

        propertiesFileNameText = new Text(group, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(SWT.FILL, SWT.NULL, false, false);
        gd.widthHint = 300;
        propertiesFileNameText.setLayoutData(gd);
        propertiesFileNameText.setToolTipText(Localiser.getString("SchemaToolPreferences.PropertiesFile.Tooltip"));
        propertiesFileNameText.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                String propsFileName = propertiesFileNameText.getText();
                if (propsFileName == null || propsFileName.trim().length() == 0)
                {
                    driverJarText.setEnabled(true);
                    driverNameText.setEnabled(true);
                    urlText.setEnabled(true);
                    usernameText.setEnabled(true);
                    passwordText.setEnabled(true);
                }
                else
                {
                    driverJarText.setEnabled(false);
                    driverNameText.setEnabled(false);
                    urlText.setEnabled(false);
                    usernameText.setEnabled(false);
                    passwordText.setEnabled(false);
                }
            }
        });

        propertiesFileNameBrowseButton = new Button(group, SWT.PUSH);
        propertiesFileNameBrowseButton.setText(Localiser.getString("SchemaToolPreferences.PropertiesFile.Browse.Label"));
        propertiesFileNameBrowseButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                IDialogSettings dialogSettings = Plugin.getDefault().getDialogSettings();
                String filetypeSuffix = ".lastpropsfile";
                String lastUsedPath = dialogSettings.get(Plugin.ID + filetypeSuffix);
                if (lastUsedPath == null)
                {
                    lastUsedPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
                }
                FileDialog dialog = new FileDialog(propertiesFileNameText.getShell(), SWT.SINGLE);
                dialog.setFilterPath(lastUsedPath);
                String result = dialog.open();
                if (result == null)
                {
                    return;
                }

                IPath filterPath = new Path(dialog.getFilterPath());
                String fileName = dialog.getFileName();
                IPath path = filterPath.append(fileName).makeAbsolute();
                propertiesFileNameText.setText(path.toOSString());
                dialogSettings.put(Plugin.ID + filetypeSuffix, path.toOSString());
            }
        });
    }

    private void createFileOutputGroup()
    {
        Group group = new Group(this, SWT.NONE);
        group.setText(Localiser.getString("SchemaToolPanel_groupFileOutput"));
        group.setLayoutData(new GridData(GridData.FILL_BOTH));

        GridLayout layout = new GridLayout();
        int numColumns = 3;
        layout.numColumns = numColumns;
        layout.marginWidth = layout.marginHeight = 10;
        layout.horizontalSpacing = 20;
        group.setLayout(layout);

        // Dump to DDL checkbox
        checkboxButtonDumpToFile = new Button(group, SWT.CHECK);
        checkboxButtonDumpToFile.setSelection(false);
        checkboxButtonDumpToFile.setText(Localiser.getString("SchemaToolPreferences.DumpDDL.Label"));
        checkboxButtonDumpToFile.setToolTipText(Localiser.getString("SchemaToolPreferences.DumpDDL.Tooltip"));
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = numColumns;
        checkboxButtonDumpToFile.setLayoutData(gd);
        checkboxButtonDumpToFile.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                boolean selection = checkboxButtonDumpToFile.getSelection();
                fileNameText.setEnabled(selection);
                fileNameBrowseButton.setEnabled(selection);
            }
        });

        // DDL filename
        Label fileNameLabel = new Label(group, SWT.NONE);
        fileNameLabel.setText(Localiser.getString("SchemaToolPreferences.DDLFile.Label"));

        fileNameText = new Text(group, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(SWT.FILL, SWT.NULL, false, false);
        gd.widthHint = 300;
        fileNameText.setLayoutData(gd);
        fileNameText.setToolTipText(Localiser.getString("SchemaToolPreferences.DDLFile.Tooltip"));

        fileNameBrowseButton = new Button(group, SWT.PUSH);
        fileNameBrowseButton.setText(Localiser.getString("SchemaToolPreferences.DDLFile.Browse.Label"));
        fileNameBrowseButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                IDialogSettings dialogSettings = Plugin.getDefault().getDialogSettings();
                String filetypeSuffix = ".lastddlfile";
                String lastUsedPath = dialogSettings.get(Plugin.ID + filetypeSuffix);
                if (lastUsedPath == null)
                {
                    lastUsedPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
                }
                FileDialog dialog = new FileDialog(fileNameText.getShell(), SWT.SINGLE);
                dialog.setFilterPath(lastUsedPath);
                String result = dialog.open();
                if (result == null)
                {
                    return;
                }

                IPath filterPath = new Path(dialog.getFilterPath());
                String fileName = dialog.getFileName();
                IPath path = filterPath.append(fileName).makeAbsolute();
                fileNameText.setText(path.toOSString());
                dialogSettings.put(Plugin.ID + filetypeSuffix, path.toOSString());
            }
        });
    }

    public Button getCheckboxButtonDumpToFile()
    {
        return checkboxButtonDumpToFile;
    }

    public Combo getDriverNameText()
    {
        return driverNameText;
    }

    public Text getFileNameText()
    {
        return fileNameText;
    }

    public Button getFileNameBrowseButton()
    {
        return fileNameBrowseButton;
    }

    public Text getPasswordText()
    {
        return passwordText;
    }

    public Button getRadioButtonCreate()
    {
        return radioButtonCreate;
    }

    public Button getRadioButtonDBInfo()
    {
        return radioButtonDBInfo;
    }

    public Button getRadioButtonSchemaInfo()
    {
        return radioButtonSchemaInfo;
    }

    public Button getRadioButtonDelete()
    {
        return radioButtonDelete;
    }

    public Button getRadioButtonValidate()
    {
        return radioButtonValidate;
    }

    public Combo getURLText()
    {
        return urlText;
    }

    public Text getDriverJarText()
    {
        return driverJarText;
    }
    
    public Text getUsernameText()
    {
        return usernameText;
    }

    public Text getPropertiesFileNameText()
    {
        return propertiesFileNameText;
    }

    private String getUrlTemplate(String driverClassName)
    {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry.getExtensionPoint(Plugin.EXTENSION_POINT_URLTEMPLATE);
        IExtension[] extensions = point.getExtensions();
        for (int i = 0; i < extensions.length; i++)
        {
            for (int j = 0; j < extensions[i].getConfigurationElements().length; j++)
            {
                if( extensions[i].getConfigurationElements()[j].getAttribute("driver-class-name").equals(driverClassName) )
                {
                    return extensions[i].getConfigurationElements()[j].getAttribute("url-template");
                }
            }
        }
        return null;
    }
}