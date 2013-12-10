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

import org.datanucleus.ide.eclipse.Localiser;
import org.datanucleus.ide.eclipse.Plugin;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
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
 * Preference page for general parameters (logging, classpath etc).
 */
public class GeneralPreferencePage extends PropertyAndPreferencePage implements IWorkbenchPreferencePage, PreferenceConstants
{
    public static final String PAGE_ID = "org.datanucleus.ide.eclipse.preferences.general"; 

    /** Combo selector for API. */
    private Combo apiCombo;

    /** Text widget storing the log file name. */
    private Text logFileText;

    /** Browse button for log file name selection. */
    private Button logFileBrowseButton;

    /** List widget storing the CLASSPATH entries. */
    private List classpathJarsList;

    /** Button for adding a jar. */
    private Button addJarButton;

    /** Button for removing a jar. */
    private Button removeJarButton;

    /** Button to use the project classpath rather than the selected jars */
    private Button projectClasspathButton;

    /** Listener for button clicks. */
    private SelectionListener selectionListener = createSelectionListener();

    private IDialogSettings dialogSettings = Plugin.getDefault().getDialogSettings();

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    protected Control createContents(Composite parent)
    {
        Composite composite = (Composite) super.createContents(parent);
        GridLayout mainLayout = new GridLayout();
        mainLayout.numColumns = 3;
        mainLayout.horizontalSpacing = 20;
        composite.setLayout(mainLayout);

        // Help text
        Label help = new Label(composite, SWT.SHADOW_IN | SWT.WRAP | SWT.BORDER);
        help.setBackground(new Color(composite.getDisplay(), 189, 211, 216));
        help.setText(Localiser.getString("GeneralPreferences.Help"));
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 3;
        help.setLayoutData(gd);

        // API
        Label apiLabel = new Label(composite, SWT.NULL);
        apiLabel.setText(Localiser.getString("GeneralPreferences.API.Label"));

        apiCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
        apiCombo.setItems(new String[] {"JDO", "JPA"});
        GridData apiGrid = new GridData(SWT.FILL, SWT.NULL, false, false);
        apiCombo.setLayoutData(apiGrid);
        apiCombo.setToolTipText(Localiser.getString("GeneralPreferences.API.Tooltip"));

        // Classpath
        Group group = new Group(composite, SWT.NONE);
        group.setText(Localiser.getString("GeneralPreferences.JarEntries.Label"));
        gd = new GridData(GridData.FILL_VERTICAL);
        gd.horizontalSpan = 3;
        gd.heightHint = 200;
        group.setLayoutData(gd);

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.marginWidth = layout.marginHeight = 10;
        layout.horizontalSpacing = 10;
        group.setLayout(layout);

        classpathJarsList = new List(group, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        classpathJarsList.setLayoutData(data);
        classpathJarsList.setToolTipText(Localiser.getString("GeneralPreferences.JarEntries.Tooltip"));

        Composite buttonGroup = new Composite(group, SWT.NULL);
        GridLayout buttonLayout = new GridLayout();
        buttonLayout.numColumns = 2;
        buttonLayout.marginTop = 0;
        buttonGroup.setLayout(buttonLayout);
        buttonGroup.setLayoutData(new GridData(GridData.FILL_BOTH, SWT.BEGINNING, false, false));

        addJarButton = new Button(buttonGroup, SWT.PUSH);
        addJarButton.setText(Localiser.getString("GeneralPreferences.JarEntries.Add.Label"));
        addJarButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        addJarButton.addSelectionListener(selectionListener);

        removeJarButton = new Button(buttonGroup, SWT.PUSH);
        removeJarButton.setText(Localiser.getString("GeneralPreferences.JarEntries.Remove.Label"));
        removeJarButton.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
        removeJarButton.addSelectionListener(selectionListener);

        // Use project classpath
        projectClasspathButton = new Button(composite, SWT.CHECK);
        projectClasspathButton.setText(Localiser.getString("GeneralPreferences.UseProjectClasspath.Label"));
        gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1);
        gd.horizontalSpan = 3;
        projectClasspathButton.setLayoutData(gd);
        projectClasspathButton.addSelectionListener(selectionListener);
        projectClasspathButton.setToolTipText(Localiser.getString("GeneralPreferences.UseProjectClasspath.Tooltip"));

        // Log configuration
        Label logFileLabel = new Label(composite, SWT.NULL);
        logFileLabel.setText(Localiser.getString("GeneralPreferences.LogConfiguration.Label"));

        logFileText = new Text(composite, SWT.BORDER | SWT.SINGLE);
        data = new GridData(SWT.FILL, SWT.NULL, false, false);
        data.widthHint = 180;
        logFileText.setLayoutData(data);
        logFileText.setToolTipText(Localiser.getString("GeneralPreferences.LogConfiguration.Tooltip"));

        logFileBrowseButton = new Button(composite, SWT.PUSH);
        logFileBrowseButton.setText(Localiser.getString("GeneralPreferences.LogConfiguration.Browse.Label"));
        logFileBrowseButton.addSelectionListener(selectionListener);

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
                if (widget == logFileBrowseButton)
                {
                    handleBrowseLogFile();
                }
                else if (widget == addJarButton)
                {
                    handleAddJars();
                }
                else if (widget == removeJarButton)
                {
                    handleRemoveJars();
                }
                else if (widget == projectClasspathButton)
                {
                    // Grey/ungrey the classpath entries
                    classpathJarsList.setEnabled(!projectClasspathButton.getSelection());
                    addJarButton.setEnabled(!projectClasspathButton.getSelection());
                    removeJarButton.setEnabled(!projectClasspathButton.getSelection());
                }
            }
        };
        return selectionListener;
    }

    /**
     * Convenience method to handle the removal of jar entries.
     */
    private void handleRemoveJars()
    {
        int index = classpathJarsList.getSelectionIndex();
        if (index >= 0)
        {
            classpathJarsList.remove(index);
        }
    }

    protected void handleBrowseLogFile()
    {
        String filetypeSuffix = ".lastlogfile";
        String lastUsedPath = dialogSettings.get(Plugin.ID + filetypeSuffix);
        if (lastUsedPath == null)
        {
            lastUsedPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
        }
        FileDialog dialog = new FileDialog(logFileText.getShell(), SWT.SINGLE);
        dialog.setFilterPath(lastUsedPath);
        String result = dialog.open();
        if (result == null)
        {
            return;
        }

        IPath filterPath = new Path(dialog.getFilterPath());
        String fileName = dialog.getFileName();
        IPath path = filterPath.append(fileName).makeAbsolute();
        logFileText.setText(path.toOSString());
        dialogSettings.put(Plugin.ID + filetypeSuffix, path.toOSString());
    }

    /**
     * Convenience method to handle the addition of jar entries.
     */
    private void handleAddJars()
    {
        String lastUsedPath = dialogSettings.get(Plugin.ID + ".lastextjar");
        if (lastUsedPath == null)
        {
            lastUsedPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
        }
        FileDialog dialog = new FileDialog(classpathJarsList.getShell(), SWT.MULTI);
        dialog.setFilterExtensions(new String[]{"*.jar"});
        String result = dialog.open();
        if (result == null)
        {
            return;
        }
        IPath filterPath = new Path(dialog.getFilterPath());
        String[] results = dialog.getFileNames();
        for (int i = 0; i < results.length; i++)
        {
            String jarName = results[i];
            IPath path = filterPath.append(jarName).makeAbsolute();
            classpathJarsList.add(path.toOSString());
        }
        dialogSettings.put(Plugin.ID + ".lastextjar", filterPath.toOSString());
    }

    /**
     * Convenience method for returning a string representation of the selected
     * classpath entries.
     * @return a string representation of classpath entries
     */
    private String getClasspathEntries()
    {
        String[] classpathEntriesArray = classpathJarsList.getItems();
        StringBuffer classpathEntries = new StringBuffer();
        for (int i = 0; i < classpathEntriesArray.length; i++)
        {
            classpathEntries.append(classpathEntriesArray[i]);
            classpathEntries.append(System.getProperty("path.separator"));
        }
        return classpathEntries.toString();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench)
    {
    }

    /**
     * Convenience method for initializing values. This is called after the controls have been created.
     */
    private void initControls()
    {
        apiCombo.setText(getPreferenceStore().getString(PERSISTENCE_API));

        String classpath = getPreferenceStore().getString(CLASSPATH_ENTRIES);
        String[] classpathEntries = classpath.split(System.getProperty("path.separator"));
        classpathJarsList.setItems(classpathEntries);

        boolean selection = getPreferenceStore().getBoolean(USE_PROJECT_CLASSPATH);
        projectClasspathButton.setSelection(selection);
        classpathJarsList.setEnabled(!selection);
        addJarButton.setEnabled(!selection);
        removeJarButton.setEnabled(!selection);

        String logFile = getPreferenceStore().getString(LOGGING_CONFIGURATION_FILE);
        logFileText.setText(logFile);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk()
    {
        getPreferenceStore().setValue(PERSISTENCE_API, apiCombo.getText());
        getPreferenceStore().setValue(LOGGING_CONFIGURATION_FILE, logFileText.getText());
        getPreferenceStore().setValue(CLASSPATH_ENTRIES, getClasspathEntries());
        getPreferenceStore().setValue(USE_PROJECT_CLASSPATH, projectClasspathButton.getSelection());
        return super.performOk();
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