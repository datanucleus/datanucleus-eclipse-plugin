/**********************************************************************
 Copyright (c) 2005 Michael Grundmann and others.
 All rights reserved. This program and the accompanying materials
 are made available under the terms of the JPOX License v1.0
 which accompanies this distribution. 

 Contributors:
 ...
 **********************************************************************/
package org.datanucleus.ide.eclipse.wizard.createmetadata;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Michael Grundmann
 * @version $Revision: 1.2 $
 */
public class MetadataCreationMainPage extends WizardPage
{
    private ISelection selection;

    private MetadataCreationModel model;

    private MetadataCreationPanel control;

    /**
     * Constructor calling the inherited super-constructor.
     * @param selection The current selection
     */
    public MetadataCreationMainPage(ISelection selection)
    {
        super("wizardPage");
        setTitle("JDO 2.0 Metadata File");
        setDescription("This wizard creates a new JDO 2.0 Metadata file\n" +
                "and adds basic content according to the specified input.");
        this.selection = selection;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent)
    {
        MetadataCreationWizard wizard = (MetadataCreationWizard) getWizard();
        model = wizard.model;
        control = new MetadataCreationPanel(parent, SWT.NONE);
        setControl(control);
        initialize();
    }

    /**
     * Utility method to obtain default values according to the selected input.
     */
    private void initialize()
    {
        if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection)
        {
            IStructuredSelection ssel = (IStructuredSelection) selection;
            if (ssel.size() > 1)
                return;
            Object obj = ssel.getFirstElement();
            if (obj instanceof IJavaElement)
            {
                try
                {
                    if (obj instanceof ICompilationUnit)
                    {
                        // selection is an ICompilationUnit
                        ICompilationUnit compilationUnit = (ICompilationUnit) obj;
                        IPackageFragment container = (IPackageFragment) compilationUnit.getParent();
                        model.setPackageFragment(container);
                        model.setAffectedClasses(new ICompilationUnit[]{compilationUnit});

                        StringBuffer className = new StringBuffer(compilationUnit.getElementName());
                        className.replace(className.indexOf(".") + 1, className.length(), "jdo");
                        control.getFileText().setText(className.toString());
                    }
                    if (obj instanceof IPackageFragment)
                    {
                        // selection is an IPackageFragment
                        IPackageFragment container = (IPackageFragment) obj;
                        model.setPackageFragment(container);
                        model.setAffectedClasses(container.getCompilationUnits());
                        control.getFileText().setText("package.jdo");
                    }
                }
                catch (JavaModelException ex)
                {
                }
            }
        }
    }

    /**
     * Save this wizard's underlying model.
     */
    public void saveModel()
    {
        model.setFileName(control.getFileText().getText());
    }
}
