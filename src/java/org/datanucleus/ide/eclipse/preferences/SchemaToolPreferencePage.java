/**********************************************************************
Copyright (c) 2007 Andy Jefferson and others. All rights reserved.
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
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preferences Page for DataNucleus SchemaTool.
 */
public class SchemaToolPreferencePage extends PropertyAndPreferencePage implements IWorkbenchPreferencePage, PreferenceConstants
{
    public static final String PAGE_ID = "org.datanucleus.ide.eclipse.preferences.schematool";

    /** List containing the selected file extensions to use with SchemaTool. */
    private List fileExtensionsList;

    /** Button to add a file extension. */
    private Button fileExtensionsAddButton;

    /** Button to remove file extension(s). */
    private Button fileExtensionsRemoveButton;

    private SelectionListener selectionListener = createSelectionListener();

    /** Text field where the DB driver name is input. */
    private Combo driverNameText;

    /** Text field for the driver jar. */
    private Text driverJarText;

    /** Text field where the DB URL is input. */
    private Combo urlText;

    /** Text field where the DB username is input. */
    private Text usernameText;

    /** Text field where the DB password is input. */
    private Text passwordText;

    /** Text widget storing the properties file name. */
    private Text propertiesFileText;

    /** Browse button for properties file name selection. */
    private Button propertiesFileBrowseButton;

    /** Text widget storing the persistence-unit name. */
    private Text persistenceUnitText;

    /** Button containing whether to run the SchemaTool in verbose mode or not. */
    private Button verboseModeCheckButton;

    private IDialogSettings dialogSettings = Plugin.getDefault().getDialogSettings();

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    protected Control createContents(Composite parent)
    {
        Composite composite = (Composite) super.createContents(parent);
        GridLayout mainLayout = new GridLayout();
        mainLayout.numColumns = 2;
        mainLayout.horizontalSpacing = 10;
        composite.setLayout(mainLayout);

        // Help text
        Label help = new Label(composite, SWT.SHADOW_IN | SWT.WRAP | SWT.BORDER);
        help.setBackground(new Color(composite.getDisplay(), 189, 211, 216));
        help.setText(Localiser.getString("SchemaToolPreferences.Help"));
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        help.setLayoutData(gd);

        // File Extensions
        Group filesGroup = new Group(composite, SWT.NONE);
        filesGroup.setText(Localiser.getString("SchemaToolPreferences.FileExtensions.Label"));
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        gd.heightHint = 120;
        filesGroup.setLayoutData(gd);

        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = layout.marginHeight = 10;
        layout.horizontalSpacing = 10;
        filesGroup.setLayout(layout);

        // a). List of file suffices
        fileExtensionsList = new List(filesGroup, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        fileExtensionsList.setLayoutData(data);
        fileExtensionsList.setToolTipText(Localiser.getString("SchemaToolPreferences.FileExtensions.Tooltip"));

        // b). Side bar with Add/Remove buttons
        Composite buttonGroup = new Composite(filesGroup, SWT.NULL);
        GridLayout buttonLayout = new GridLayout();
        buttonLayout.marginTop = 0;
        buttonGroup.setLayout(buttonLayout);
        buttonGroup.setLayoutData(new GridData(GridData.FILL_BOTH, SWT.BEGINNING, false, false));

        fileExtensionsAddButton = new Button(buttonGroup, SWT.PUSH);
        fileExtensionsAddButton.setText(Localiser.getString("SchemaToolPreferences.Add.Label"));
        fileExtensionsAddButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        fileExtensionsAddButton.addSelectionListener(selectionListener);

        fileExtensionsRemoveButton = new Button(buttonGroup, SWT.PUSH);
        fileExtensionsRemoveButton.setText(Localiser.getString("SchemaToolPreferences.Remove.Label"));
        fileExtensionsRemoveButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        fileExtensionsRemoveButton.addSelectionListener(selectionListener);

        // Verbose
        verboseModeCheckButton = new Button(composite, SWT.CHECK);
        verboseModeCheckButton.setText(Localiser.getString("SchemaToolPreferences.Verbose.Label"));
        gd = new GridData(SWT.FILL, SWT.NULL, true, false, 2, 1);
        gd.horizontalSpan = 2;
        verboseModeCheckButton.setLayoutData(gd);
        verboseModeCheckButton.setToolTipText(Localiser.getString("SchemaToolPreferences.Verbose.Tooltip"));

        // Persistence Unit
        Label persistenceUnitLabel = new Label(composite, SWT.NULL);
        persistenceUnitLabel.setText(Localiser.getString("SchemaToolPreferences.PersistenceUnit.Label"));

        persistenceUnitText = new Text(composite, SWT.BORDER | SWT.SINGLE);
        GridData persistenceUnitGrid = new GridData(SWT.FILL, SWT.NULL, false, false);
        persistenceUnitGrid.widthHint = 50;
        persistenceUnitText.setLayoutData(persistenceUnitGrid);
        persistenceUnitText.setToolTipText(Localiser.getString("SchemaToolPreferences.PersistenceUnit.Tooltip"));

        // Datastore
        Group datastoreGroup = new Group(composite, SWT.NONE);
        datastoreGroup.setText(Localiser.getString("SchemaToolPreferences.DatastoreDetails.Label"));
        gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 2;
        datastoreGroup.setLayoutData(gd);

        GridLayout datastoreLayout = new GridLayout();
        datastoreLayout.numColumns = 3;
        datastoreLayout.marginWidth = layout.marginHeight = 10;
        datastoreLayout.horizontalSpacing = 10;
        datastoreGroup.setLayout(datastoreLayout);

        // a). Database driver JAR
        Label driverJarLabel = new Label(datastoreGroup, SWT.NONE);
        driverJarLabel.setText(Localiser.getString("SchemaToolPreferences.DriverJar.Label"));
        driverJarText = new Text(datastoreGroup, SWT.BORDER);
        driverJarText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        driverJarText.setToolTipText(Localiser.getString("SchemaToolPreferences.DriverJar.Tooltip"));
        final Button browse = new Button(datastoreGroup, SWT.NONE);
        browse.setText("Browse");
        browse.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
        {
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
            {
                FileDialog dialog = new FileDialog(browse.getShell(), SWT.MULTI);
                dialog.setFilterExtensions(new String[]{"*.jar"}); 
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
                            if (driverJarText.getText().length() > 0)
                            {
                                driverJarText.setText(driverJarText.getText() + ";");
                            }
                            driverJarText.setText(driverJarText.getText() + path.toFile().toURI().toURL().toString());
                            JarFile jar = new JarFile(path.toFile());
                            try
                            {
                                Enumeration entries = jar.entries();
                                while (entries.hasMoreElements())
                                {
                                    String name = ((JarEntry) entries.nextElement()).getName();
                                    name = name.replace('/', '.');
                                    name = name.replace('\\', '.');
                                    if (name.endsWith(".class"))
                                    {
                                        name = name.substring(0, name.indexOf(".class"));
                                        try
                                        {
                                            Class cls = loader.loadClass(name);
                                            Class[] interfaces = cls.getInterfaces();
                                            for (int j = 0; j < interfaces.length; j++)
                                            {
                                                if (interfaces[j].getName().equals("java.sql.Driver"))
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

        // b). Database driver name
        Label driverNameLabel = new Label(datastoreGroup, SWT.NONE);
        driverNameLabel.setText(Localiser.getString("SchemaToolPreferences.DriverName.Label"));
        driverNameText = new Combo(datastoreGroup, SWT.NONE);
        driverNameText.setToolTipText(Localiser.getString("SchemaToolPreferences.DriverName.Tooltip"));
        driverNameText.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                String template = getUrlTemplate(driverNameText.getText());
                if (template != null)
                {
                    urlText.setText(template);
                }
            }
        });
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        gd.widthHint = 150;
        driverNameText.setLayoutData(gd);

        // c). Database URL
        Label urlLabel = new Label(datastoreGroup, SWT.NONE);
        urlLabel.setText(Localiser.getString("SchemaToolPreferences.ConnectionURL.Label"));
        urlText = new Combo(datastoreGroup, SWT.NONE);
        urlText.setToolTipText(Localiser.getString("SchemaToolPreferences.ConnectionURL.Tooltip"));
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        gd.widthHint = 150;
        urlText.setLayoutData(gd);

        // d). Database Username
        Label usernameLabel = new Label(datastoreGroup, SWT.NONE);
        usernameLabel.setText(Localiser.getString("SchemaToolPreferences.UserName.Label"));
        usernameText = new Text(datastoreGroup, SWT.BORDER);
        usernameText.setToolTipText(Localiser.getString("SchemaToolPreferences.UserName.Tooltip"));
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        gd.widthHint = 100;
        usernameText.setLayoutData(gd);

        // e). Database Password
        Label passwordLabel = new Label(datastoreGroup, SWT.NONE);
        passwordLabel.setText(Localiser.getString("SchemaToolPreferences.Password.Label"));
        passwordText = new Text(datastoreGroup, SWT.BORDER);
        passwordText.setToolTipText(Localiser.getString("SchemaToolPreferences.Password.Tooltip"));
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 2;
        gd.widthHint = 100;
        passwordText.setLayoutData(gd);
        passwordText.setEchoChar('*');

        // f). Properties file
        Label propertiesFileLabel = new Label(datastoreGroup, SWT.NULL);
        propertiesFileLabel.setText(Localiser.getString("SchemaToolPreferences.PropertiesFile.Label"));

        propertiesFileText = new Text(datastoreGroup, SWT.BORDER | SWT.SINGLE);
        data = new GridData(SWT.FILL, SWT.NULL, false, false);
        data.widthHint = 150;
        propertiesFileText.setLayoutData(data);
        propertiesFileText.setToolTipText(Localiser.getString("SchemaToolPreferences.PropertiesFile.Tooltip"));
        propertiesFileText.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                String propsFileName = propertiesFileText.getText();
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

        propertiesFileBrowseButton = new Button(datastoreGroup, SWT.PUSH);
        propertiesFileBrowseButton.setText(Localiser.getString("SchemaToolPreferences.PropertiesFile.Browse.Label"));
        propertiesFileBrowseButton.addSelectionListener(selectionListener);

        initControls();

        return composite;
    }

    private SelectionAdapter createSelectionListener()
    {
        SelectionAdapter selectionListener = new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent event)
            {
                Widget widget = event.widget;
                if (widget == fileExtensionsAddButton)
                {
                    handleAddFileExtension();
                }
                else if (widget == fileExtensionsRemoveButton)
                {
                    handleRemoveFileExtension();
                }
                else if (widget == propertiesFileBrowseButton)
                {
                    handleBrowsePropertiesFile();
                }
            }
        };
        return selectionListener;
    }

    /**
     * Accessor for the file extensions (suffices) from the List widget.
     * @return The file extensions (separated by ":")
     */
    private String getFileExtensions()
    {
        String[] fileExtensionsArray = fileExtensionsList.getItems();
        StringBuffer fileExtensions = new StringBuffer();
        for (int i = 0; i < fileExtensionsArray.length; i++)
        {
            fileExtensions.append(fileExtensionsArray[i]);
            fileExtensions.append(System.getProperty("path.separator"));
        }
        return fileExtensions.toString();
    }

    private void handleAddFileExtension()
    {
        InputDialog dialog = new InputDialog(fileExtensionsList.getShell(),
            Localiser.getString("SchemaToolPreferences.AddExtension.Title"),
            Localiser.getString("SchemaToolPreferences.AddExtension.Label"), null, null);
        dialog.open();
        String result = dialog.getValue();
        if (result == null)
        {
            return;
        }

        if (fileExtensionsList.indexOf(result) < 0)
        {
            // Add the new item if not already there
            fileExtensionsList.add(result);
        }
    }

    private void handleRemoveFileExtension()
    {
        int index = fileExtensionsList.getSelectionIndex();
        if (index >= 0)
        {
            fileExtensionsList.remove(index);
        }
    }

    protected void handleBrowsePropertiesFile()
    {
        String filetypeSuffix = ".lastpropsfile";
        String lastUsedPath = dialogSettings.get(Plugin.ID + filetypeSuffix);
        if (lastUsedPath == null)
        {
            lastUsedPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
        }
        FileDialog dialog = new FileDialog(propertiesFileText.getShell(), SWT.SINGLE);
        dialog.setFilterPath(lastUsedPath);
        String result = dialog.open();
        if (result == null)
        {
            return;
        }

        IPath filterPath = new Path(dialog.getFilterPath());
        String fileName = dialog.getFileName();
        IPath path = filterPath.append(fileName).makeAbsolute();
        propertiesFileText.setText(path.toOSString());
        dialogSettings.put(Plugin.ID + filetypeSuffix, path.toOSString());
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench)
    {
    }

    /**
     * Convenience method for initializing values. Called after the controls have been created.
     */
    private void initControls()
    {
        String extensions = getPreferenceStore().getString(SCHEMATOOL_INPUT_FILE_EXTENSIONS);
        String[] extensionEntries = extensions.split(System.getProperty("path.separator"));
        fileExtensionsList.setItems(extensionEntries);

        driverNameText.setText(getPreferenceStore().getString(SCHEMATOOL_DATASTORE_DRIVERNAME));
        driverJarText.setText(getPreferenceStore().getString(SCHEMATOOL_DATASTORE_DRIVERJAR));
        urlText.setText(getPreferenceStore().getString(SCHEMATOOL_DATASTORE_URL));
        usernameText.setText(getPreferenceStore().getString(SCHEMATOOL_DATASTORE_USERNAME));
        passwordText.setText(getPreferenceStore().getString(SCHEMATOOL_DATASTORE_PASSWORD));
        propertiesFileText.setText(getPreferenceStore().getString(SCHEMATOOL_PROPERTIES_FILE));
        verboseModeCheckButton.setSelection(getPreferenceStore().getBoolean(SCHEMATOOL_VERBOSE_MODE));
        persistenceUnitText.setText(getPreferenceStore().getString(SCHEMATOOL_PERSISTENCE_UNIT));
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk()
    {
        getPreferenceStore().setValue(SCHEMATOOL_INPUT_FILE_EXTENSIONS, getFileExtensions());
        getPreferenceStore().setValue(SCHEMATOOL_DATASTORE_DRIVERNAME, driverNameText.getText());
        getPreferenceStore().setValue(SCHEMATOOL_DATASTORE_DRIVERJAR, driverJarText.getText());
        getPreferenceStore().setValue(SCHEMATOOL_DATASTORE_URL, urlText.getText());
        getPreferenceStore().setValue(SCHEMATOOL_DATASTORE_USERNAME, usernameText.getText());
        getPreferenceStore().setValue(SCHEMATOOL_DATASTORE_PASSWORD, passwordText.getText());
        getPreferenceStore().setValue(SCHEMATOOL_PROPERTIES_FILE, propertiesFileText.getText());
        getPreferenceStore().setValue(SCHEMATOOL_VERBOSE_MODE, verboseModeCheckButton.getSelection());
        getPreferenceStore().setValue(SCHEMATOOL_PERSISTENCE_UNIT, persistenceUnitText.getText());

        return super.performOk();
    }

    // TODO This is duplicated from SchemaToolPanel
    private String getUrlTemplate(String driverClassName)
    {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry.getExtensionPoint(Plugin.EXTENSION_POINT_URLTEMPLATE);
        IExtension[] extensions = point.getExtensions();
        for (int i = 0; i < extensions.length; i++)
        {
            for (int j = 0; j < extensions[i].getConfigurationElements().length; j++)
            {
                if (extensions[i].getConfigurationElements()[j].getAttribute("driver-class-name").equals(driverClassName))
                {
                    return extensions[i].getConfigurationElements()[j].getAttribute("url-template");
                }
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.datanucleus.ide.eclipse.preferences.PropertyAndPreferencePage#getPageId()
     */
    protected String getPageId()
    {
        return PAGE_ID;
    }
}