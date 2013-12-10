/**********************************************************************
 Copyright (c) 2005 Michael Grundmann and others.
 All rights reserved. This program and the accompanying materials
 are made available under the terms of the JPOX License v1.0
 which accompanies this distribution. 

 Contributors:
 ...
 **********************************************************************/
package org.datanucleus.ide.eclipse.preferences;

import org.datanucleus.ide.eclipse.Localiser;
import org.datanucleus.ide.eclipse.Plugin;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.part.PageBook;


public abstract class PropertyAndPreferencePage extends PropertyPage
{
    public static final String USEPROJECTSETTINGS = "useProjectSettings";

    private static final String FALSE = "false";

    private static final String TRUE = "true";

    // Additional buttons for property pages
    private Button projectSettingsButton;

    Link preferencePageLink;

    // Overlay preference store for property pages
    private PropertyStore overlayStore;

    // Cache for page id
    private String pageId;

    // Container for subclass controls
    private Composite contents;

    /**
     * Constructor
     */
    public PropertyAndPreferencePage()
    {
        super();
    }

    /**
     * Constructor
     * @param title - title string
     */
    public PropertyAndPreferencePage(String title)
    {
        super();
        setTitle(title);
    }

    /**
     * Constructor
     * @param title - title string
     * @param image - title image
     */
    public PropertyAndPreferencePage(String title, ImageDescriptor image)
    {
        super();
        setTitle(title);
        setImageDescriptor(image);
    }

    /**
     * Returns the id of the current preference page as defined in plugin.xml
     * Subclasses must implement.
     * @return - the qualifier
     */
    protected abstract String getPageId();

    /**
     * Returns true if this instance represents a property page
     * @return true for property pages, false for preference pages
     */
    public boolean isPropertyPage()
    {
        return getElement() != null;
    }

    /**
     * We need to implement createContents method. In case of property pages we
     * insert two radio buttons and a push button at the top of the page. Below
     * this group we create a new composite for the contents created by
     * subclasses.
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    protected Control createContents(Composite parent)
    {
        if (isPropertyPage())
            createSelectionGroup(parent);
        contents = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        contents.setLayout(layout);
        contents.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return contents;
    }

    /**
     * In case of property pages we create a new PropertyStore as local overlay
     * store. After all controls have been create, we enable/disable these
     * controls
     * @see org.eclipse.jface.preference.PreferencePage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent)
    {
        // Special treatment for property pages
        if (isPropertyPage())
        {
            // Cache the page id
            pageId = getPageId();

            // Create an overlay preference store and fill it with properties
            overlayStore = new PropertyStore((IResource) getElement(), super.getPreferenceStore(), getPageId());

            // Set overlay store as current preference store TODO ?
        }
        super.createControl(parent);

        // Update enablement of all subclass controls
        if (isPropertyPage())
            setControlsEnabled();
    }

    /*
     * Create a composite on top of the page containing a check button and a
     * link, allowing the user to enable project specific settings and open the
     * workspace preference page.
     */
    private void createSelectionGroup(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        projectSettingsButton = createCheckButton(composite, Localiser.getString("PreferenceOverlayPage_labelEnableProjectSettings"));
        preferencePageLink = createLink(composite, Localiser.getString("PreferenceOverlayPage_labelConfigureWorkspaceSettings"));

        try
        {
            String use = ((IResource) getElement()).getPersistentProperty(new QualifiedName(pageId, USEPROJECTSETTINGS));
            if (TRUE.equals(use))
            {
                projectSettingsButton.setSelection(true);
                preferencePageLink.setEnabled(false);
            }
            else
                projectSettingsButton.setSelection(false);
        }
        catch (CoreException e)
        {
            projectSettingsButton.setSelection(false);
        }
    }

    /*
     * Convenience method for creating a check button with the given label which
     * toggles enablement of project specific settings if this page is currently
     * used as a property page.
     */
    private Button createCheckButton(Composite parent, String label)
    {
        final Button button = new Button(parent, SWT.CHECK);
        button.setText(label);
        button.setLayoutData(new GridData(SWT.FILL, SWT.NULL, true, false));
        button.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                preferencePageLink.setEnabled(!projectSettingsButton.getSelection());
                setControlsEnabled();
            }
        });
        return button;
    }

    /*
     * Convenience method for creating a link widget with the given text, which
     * opens the workspace preference page if this page is currently used as a
     * property page.
     */
    private Link createLink(Composite composite, String text)
    {
        Link link = new Link(composite, SWT.NONE);
        link.setFont(composite.getFont());
        link.setText("<A>" + text + "</A>"); //$NON-NLS-1$ //$NON-NLS-2$
        link.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                PreferencesUtil.createPreferenceDialogOn(getShell(), getPageId(), new String[]{getPageId()}, null).open();
            }
        });
        return link;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
     */
    protected IPreferenceStore doGetPreferenceStore()
    {
        return Plugin.getDefault().getPreferenceStore();
    }

    /*
     * Returns in case of property pages the overlay store - otherwise the
     * standard preference store
     * @see org.eclipse.jface.preference.PreferencePage#getPreferenceStore()
     */
    public IPreferenceStore getPreferenceStore()
    {
        if (isPropertyPage())
            return overlayStore;
        return super.getPreferenceStore();
    }

    /**
     * Enables or disables the controls of this page
     */
    private void setControlsEnabled()
    {
        boolean enabled = projectSettingsButton.getSelection();
        setControlsEnabled(enabled);
    }

    /**
     * Enables or disables the controls of this page Subclasses may override.
     * @param enabled - true if controls shall be enabled
     */
    protected void setControlsEnabled(boolean enabled)
    {
        setControlsEnabled(contents, enabled);
    }

    /**
     * Enables or disables a tree of controls starting at the specified root. We
     * spare tabbed notebooks and pagebooks to allow for user navigation.
     * @param root - the root composite
     * @param enabled - true if controls shall be enabled
     */
    private void setControlsEnabled(Composite root, boolean enabled)
    {
        Control[] children = root.getChildren();
        for (int i = 0; i < children.length; i++)
        {
            Control child = children[i];
            if (!(child instanceof CTabFolder) && !(child instanceof TabFolder) && !(child instanceof PageBook))
                child.setEnabled(enabled);
            if (child instanceof Composite)
                setControlsEnabled((Composite) child, enabled);
        }
    }

    /**
     * We override the performOk method. In case of property pages we save the
     * state of the radio buttons.
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk()
    {
        boolean result = super.performOk();
        if (result && isPropertyPage())
        {
            // Save state of radiobuttons in project properties
            IResource resource = (IResource) getElement();
            try
            {
                String value = (projectSettingsButton.getSelection()) ? TRUE : FALSE;
                resource.setPersistentProperty(new QualifiedName(getPageId(), USEPROJECTSETTINGS), value);
            }
            catch (CoreException e)
            {
            }
        }
        return result;
    }

    /**
     * We override the performDefaults method. In case of property pages we
     * switch back to the workspace settings and disable the page controls.
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    protected void performDefaults()
    {
        if (isPropertyPage())
        {
            projectSettingsButton.setSelection(false);
            preferencePageLink.setEnabled(true);
            setControlsEnabled();
        }
        super.performDefaults();
    }

    /**
     * Show a single preference pages
     * @param id - the preference page identification
     * @param page - the preference page
     */
    protected void showPreferencePage(String id, IPreferencePage page)
    {
        final IPreferenceNode targetNode = new PreferenceNode(id, page);
        PreferenceManager manager = new PreferenceManager();
        manager.addToRoot(targetNode);
        final PreferenceDialog dialog = new PreferenceDialog(getControl().getShell(), manager);
        BusyIndicator.showWhile(getControl().getDisplay(), new Runnable()
        {
            public void run()
            {
                dialog.create();
                dialog.setMessage(targetNode.getLabelText());
                dialog.open();
            }
        });
    }
}