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
import org.eclipse.jdt.core.IPackageFragment;

/**
 * @author Michael Grundmann
 * @version $Revision: 1.1 $
 */
public class MetadataCreationModel
{
    private ICompilationUnit[] affectedClasses;

    private IPackageFragment packageFragment;

    private String fileName;

    /**
     * Default contructor
     */
    public MetadataCreationModel()
    {
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public IPackageFragment getPackageFragment()
    {
        return packageFragment;
    }

    public void setPackageFragment(IPackageFragment packageName)
    {
        this.packageFragment = packageName;
    }

    public ICompilationUnit[] getAffectedClasses()
    {
        return affectedClasses;
    }

    public void setAffectedClasses(ICompilationUnit[] affectedClasses)
    {
        this.affectedClasses = affectedClasses;
    }
}
