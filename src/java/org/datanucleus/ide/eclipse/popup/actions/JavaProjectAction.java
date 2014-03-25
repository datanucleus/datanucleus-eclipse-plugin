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

/**
 * Common super-class for {@link IJavaProject}-related actions.
 * @author Marco หงุ่ยตระกูล-Schulze - marco at codewizards dot co
 */
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

    /**
     * Gets the associated {@link IAction}, if {@link #selectionChanged(IAction, ISelection) selectionChanged(...)}
     * or {@link #setActivePart(IAction, IWorkbenchPart) setActivePart(...)} were already invoked.
     * @return the associated {@link IAction} or <code>null</code>, if there is none (yet).
     */
    protected IAction getAction() {
        return action;
    }

    /**
     * Gets the current selection, if {@link #selectionChanged(IAction, ISelection)} was already invoked.
     * @return the current selection or <code>null</code>, if there is none (yet).
     */
    protected ISelection getSelection() {
        return selection;
    }

    /**
     * Gets the current {@link IWorkbenchPart}, if {@link #setActivePart(IAction, IWorkbenchPart)} was already invoked.
     * @return the current {@link IWorkbenchPart} or <code>null</code>, if there is none (yet).
     */
    protected IWorkbenchPart getActivePart() {
        return activePart;
    }

    /**
     * Gets the current selection, if it is an instance of {@link IStructuredSelection}.
     * @return the current selection, if it is an instance of {@link IStructuredSelection}; otherwise <code>null</code>.
     */
    protected IStructuredSelection getStructuredSelection() {
        final ISelection selection = getSelection();
        if (selection instanceof IStructuredSelection)
            return (IStructuredSelection) selection;
        else
            return null;
    }

    /**
     * Gets the {@link IJavaProject}s from the current {@link #getSelection()}.
     * @return the currently selected {@link IJavaProject}s. Never <code>null</code>, but maybe empty.
     */
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
