/**********************************************************************
Copyright (c) 2008 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.ide.eclipse.popup.actions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.datanucleus.ide.eclipse.jobs.LaunchUtilities;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;


/**
 * Action to create a persistence.xml file for persistent classes in the project.
 */
public class CreatePersistenceXmlAction implements IObjectActionDelegate
{
    private ISelection lastSelection;

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
     * org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action)
    {
        if (lastSelection != null && lastSelection.isEmpty() == false && lastSelection instanceof IStructuredSelection)
        {
            IStructuredSelection ssel = (IStructuredSelection) lastSelection;
            if (ssel.size() > 1)
            {
                // More than 1 selection?
                return;
            }

            Object obj = ssel.getFirstElement();
            IPackageFragment pkg = null;
            if (obj instanceof IJavaElement)
            {
                if (obj instanceof ICompilationUnit)
                {
                    // selection is an ICompilationUnit
                    ICompilationUnit compilationUnit = (ICompilationUnit) obj;
                    pkg = (IPackageFragment) compilationUnit.getParent();
                }
                else if (obj instanceof IPackageFragment)
                {
                    // selection is an IPackageFragment
                    pkg = (IPackageFragment) obj;
                }
            }

            if (pkg != null)
            {
                // Find root of package structure
                boolean moreLevels = true;
                IJavaElement root = pkg;
                while (moreLevels)
                {
                    IJavaElement parent = pkg.getParent();
                    if (parent != null && parent instanceof IPackageFragment)
                    {
                        pkg = (IPackageFragment)parent;
                    }
                    else if (parent != null && !(parent instanceof IPackageFragment))
                    {
                        // First non-package level, hence the root
                        moreLevels = false;
                        root = parent;
                    }
                    else
                    {
                        moreLevels = false;
                    }
                }

                try
                {
                    IResource resource = root.getCorrespondingResource();
                    IContainer container = (IContainer) resource;
                    try
                    {
                        // Make sure META-INF exists
                        IFolder metaInfDir = container.getFolder(new Path("META-INF"));
                        if (!metaInfDir.exists())
                        {
                            metaInfDir.create(true, true, null);
                        }

                        // Create persistence.xml with the desired contents
                        final IFile file = metaInfDir.getFile(new Path("persistence.xml"));
                        String contents = getContents(pkg);
                        InputStream stream = new ByteArrayInputStream(contents.toString().getBytes());
                        if (file.exists())
                        {
                            file.setContents(stream, true, true, null);
                        }
                        else
                        {
                            file.create(stream, true, null);
                        }
                        stream.close();

                        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                        try
                        {
                            IDE.openEditor(page, file, true);
                        }
                        catch (PartInitException e)
                        {
                        }
                    }
                    catch (CoreException e)
                    {
                    }
                    catch (IOException e)
                    {
                    }
                }
                catch (JavaModelException jme)
                {
                    
                }
            }
        }
    }

    private String getContents(IPackageFragment pkg)
    {
        StringBuffer str = new StringBuffer();
        str.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
        str.append("<persistence xmlns=\"http://java.sun.com/xml/ns/persistence\"\n");
        str.append("    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        str.append("    xsi:schemaLocation=\"http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd\" version=\"1.0\">\n");
        str.append("\n");

        String indent = "    ";
        str.append(indent).append("<persistence-unit name=\"TEST\">\n");

        // <mapping-file> for each jdo file
        List argsList = new ArrayList();
        LaunchUtilities.getInputFiles(argsList, pkg.getResource(), pkg.getJavaProject(), 
            new String[] {"jdo"}, false);
        for (int i = 0; i < argsList.size(); i++)
        {
            str.append(indent).append(indent).append("<mapping-file>").append(argsList.get(i)).append("</mapping-file>\n");
        }

        // <class> for each annotated class
        argsList = new ArrayList();
        LaunchUtilities.getInputFiles(argsList, pkg.getResource(), pkg.getJavaProject(), 
            new String[] {"class"}, false);
        for (int i = 0; i < argsList.size(); i++)
        {
            // TODO Check if class is annotated
        }

        str.append(indent).append("</persistence-unit>\n");
        str.append("</persistence>\n");
        return str.toString();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     * org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection)
    {
        lastSelection = selection;
    }
}