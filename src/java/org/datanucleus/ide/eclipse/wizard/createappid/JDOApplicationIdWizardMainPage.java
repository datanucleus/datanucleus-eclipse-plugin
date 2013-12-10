/**********************************************************************
 Copyright (c) 2004 Erik Bengtson and others.
 All rights reserved. This program and the accompanying materials
 are made available under the terms of the JPOX License v1.0
 which accompanies this distribution. 

 Contributors:
 Michael Grundmann  -  Removed dependencies to JDT internal classes. 
 ...
 **********************************************************************/
package org.datanucleus.ide.eclipse.wizard.createappid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.datanucleus.ide.eclipse.Localiser;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.model.WorkbenchLabelProvider;


/**
 * Main Page for the generation of application id primary key classes.
 * @version $Revision: 1.1 $
 */
public class JDOApplicationIdWizardMainPage extends NewTypeWizardPage
{
    private Table fieldsTable;

    private TableViewer fieldsTableViewer;

    /**
     * Constructor
     */
    public JDOApplicationIdWizardMainPage()
    {
        super(true, "Page1"); //$NON-NLS-1$
        setTitle(Localiser.getString("JDOApplicationIdWizardMainPage_title")); //$NON-NLS-1$
        setDescription(Localiser.getString("JDOApplicationIdWizardMainPage_description")); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent)
    {
        initializeDialogUnits(parent);

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());

        int nColumns = 4;

        GridLayout layout = new GridLayout();
        layout.numColumns = nColumns;
        composite.setLayout(layout);

        // pick & choose the wanted UI components
        createContainerControls(composite, nColumns);
        createPackageControls(composite, nColumns);
        createEnclosingTypeControls(composite, nColumns);

        createSeparator(composite, nColumns);

        createTypeNameControls(composite, nColumns);
        createModifierControls(composite, nColumns);

        createFieldListControls(composite, nColumns);

        createCommentControls(composite, nColumns);
        enableCommentControl(true);

        setControl(composite);
    }

    /**
     * Creates the necessary controls (label, table and tableviewer) to select
     * the desired id fields. The method expects that the parent composite uses
     * a <code>GridLayout</code> as its layout manager and that the grid
     * layout has at least 2 columns.
     * @param parent the parent composite
     * @param nColumns the number of columns to span. This number must be
     * greater or equal three
     */
    protected void createFieldListControls(Composite composite, int columns)
    {
        Label fieldsLabel = new Label(composite, SWT.NULL);
        fieldsLabel.setText("Fields:");// XXX
        fieldsLabel.setLayoutData(new GridData(SWT.NULL, SWT.BEGINNING, false, false));

        fieldsTable = new Table(composite, SWT.MULTI | SWT.BORDER);
        fieldsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, columns - 1, 1));

        fieldsTableViewer = new TableViewer(fieldsTable);
        fieldsTableViewer.setContentProvider(new ArrayContentProvider());
        fieldsTableViewer.setLabelProvider(new WorkbenchLabelProvider());
        fieldsTableViewer.setInput(getFields(getEnclosingType()));
    }

    private void doStatusUpdate()
    {
        // define the components for which a status is desired
        IStatus[] status = new IStatus[]{fContainerStatus, isEnclosingTypeSelected() ? fEnclosingTypeStatus : fPackageStatus, fTypeNameStatus,};
        updateStatus(status);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jdt.ui.wizards.NewContainerWizardPage#handleFieldChanged(String)
     */
    protected void handleFieldChanged(String fieldName)
    {
        super.handleFieldChanged(fieldName);
        if (fieldName == ENCLOSING)
        {
            if (fieldsTableViewer != null && !fieldsTableViewer.getControl().isDisposed())
                fieldsTableViewer.setInput(getFields(getEnclosingType()));
        }
        doStatusUpdate();
    }

    /**
     * The wizard managing this wizard page must call this method during
     * initialization with a corresponding selection.
     */
    public void init(IStructuredSelection selection)
    {
        IJavaElement jelem = getInitialJavaElement(selection);
        initContainerPage(jelem);
        initTypePage(jelem);
        doStatusUpdate();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jdt.ui.wizards.NewTypeWizardPage#initTypePage(IJavaElement)
     */
    protected void initTypePage(IJavaElement elem)
    {
        super.initTypePage(elem);
        if (elem != null)
        {
            setTypeName("Id", true);
            setEnclosingTypeSelection(true, false);
            setEnclosingType(getEnclosingType(), true);
            setModifiers(F_STATIC | F_PUBLIC, false);
            List superinterfaces = new ArrayList();
            superinterfaces.add("java.io.Serializable");
            setSuperInterfaces(superinterfaces, false);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jdt.ui.wizards.NewTypeWizardPage#createTypeMembers(IType,
     * ImportsManager, IProgressMonitor)
     */
    protected void createTypeMembers(IType newType, ImportsManager imports, IProgressMonitor monitor) throws CoreException
    {
        List selectedFields = getSelectedFields();
        String TYPE_STRING = "java.lang.String";
        imports.addImport(TYPE_STRING);
        for (int i = 0; i < selectedFields.size(); i++)
        {
            IField field = ((IField) selectedFields.get(i));
            String fieldGenerated = "public " + Signature.toString(field.getTypeSignature()) + " " + field.getElementName() + ";";
            newType.createField(fieldGenerated, null, false, null);
        }
        String method = "public " + newType.getElementName() + "() {} ";
        newType.createMethod(method, null, false, null);

        method = "public " + newType.getElementName() + "(" + TYPE_STRING + " str) { ";
        method = method + "java.util.StringTokenizer token = new java.util.StringTokenizer(str, \"::\");";
        for (int i = 0; i < selectedFields.size(); i++)
        {
            IField field = ((IField) selectedFields.get(i));
            method = method + "this." + field.getElementName() + " = " + CodeGenerationUtil.convert("token.nextToken()", field.getTypeSignature()) + ";";
        }
        method = method + " } ";
        newType.createMethod(method, null, false, null);

        method = "public " + TYPE_STRING + " toString() { ";
        method = method + "java.lang.String str = \"\"; ";
        for (int i = 0; i < selectedFields.size(); i++)
        {
            IField field = ((IField) selectedFields.get(i));
            method = method + "str += java.lang.String.valueOf(this." + field.getElementName() + ")";
            if (i < selectedFields.size())
            {
                method = method + "+\"::\"";
            }
            method = method + ";";
        }
        method = method + " return str;";
        method = method + " } ";
        newType.createMethod(method, null, false, null);
        newType.createMethod(CodeGenerationUtil.createEqualsMethod(newType.getElementName(), newType), null, false, null);
    }

    /**
     * Utility method to get the selected fields, which should be used for the
     * id class.
     * @return a list containing the selected fields
     */
    protected List getSelectedFields()
    {
        List result = new ArrayList();
        ISelection selection = fieldsTableViewer.getSelection();
        if (selection instanceof IStructuredSelection)
        {
            Iterator iter = ((IStructuredSelection) selection).iterator();
            while (iter.hasNext())
            {
                result.add(iter.next());
            }
        }
        return result;
    }

    /**
     * Utility method to get an array of fields declared in the enclosing type
     * or an empty array if none can be found.
     * @param enclosingType the enclosing type
     * @return an array of fields
     */
    protected IField[] getFields(IType enclosingType)
    {
        IField[] fieldsArray = new IField[0];
        if (enclosingType != null)
        {
            try
            {
                fieldsArray = enclosingType.getFields();
            }
            catch (JavaModelException e)
            {
                e.printStackTrace();
            }
        }
        return fieldsArray;
    }
}