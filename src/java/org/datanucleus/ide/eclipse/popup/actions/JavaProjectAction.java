package org.datanucleus.ide.eclipse.popup.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public abstract class JavaProjectAction implements IObjectActionDelegate {

    private IAction action;
    private ISelection selection;
    private IWorkbenchPart activePart;

    @Override
    public void selectionChanged(IAction action, ISelection selection)
    {
        this.action = action;
        this.selection = selection;
    }

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart)
    {
        this.action = action;
        this.activePart = targetPart;
    }

    protected IAction getAction() {
        return action;
    }

    protected ISelection getSelection() {
        return selection;
    }

    protected IWorkbenchPart getActivePart() {
        return activePart;
    }

    protected IStructuredSelection getStructuredSelection() {
        final ISelection selection = getSelection();
        if (selection instanceof IStructuredSelection)
            return (IStructuredSelection) selection;
        else
            return null;
    }

    protected List<IJavaProject> getSelectedJavaProjects() {
        try {
            final IStructuredSelection selection = getStructuredSelection();
            if (selection == null)
                return Collections.emptyList();

            List<IJavaProject> result = new ArrayList<IJavaProject>(selection.size());
            for (Iterator<?> it = selection.iterator(); it.hasNext(); ) {
                Object element = it.next();

                if (element instanceof IJavaProject) {
                    result.add((IJavaProject) element);
                    continue;
                }

                if (element instanceof IAdaptable) {
                    Object adapter = ((IAdaptable) element).getAdapter(IJavaProject.class);
                    if (adapter instanceof IJavaProject) { // adapter might be null
                        result.add((IJavaProject) adapter);
                        continue;
                    }
                }

                if (element instanceof IProject) {
                    IProject project = (IProject) element;
                    if (project.hasNature(JavaCore.NATURE_ID)) {
                        IJavaProject javaProject = JavaCore.create(project);
                        if (javaProject != null) { // should never happen, but better play safe ;-)
                            result.add(javaProject);
                            continue;
                        }
                    }
                }
            }

            return Collections.unmodifiableList(result);
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

}
