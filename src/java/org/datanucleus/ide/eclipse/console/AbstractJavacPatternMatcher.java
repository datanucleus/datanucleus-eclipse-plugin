/**********************************************************************
 Copyright (c) 2005 Erik Bengtson and others.
 All rights reserved. This program and the accompanying materials
 are made available under the terms of the JPOX License v1.0
 which accompanies this distribution. 

 Contributors:
 2005 eclipse.org - initial sources
 ...
 **********************************************************************/
package org.datanucleus.ide.eclipse.console;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.datanucleus.ide.eclipse.Plugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.ui.console.FileLink;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.console.IPatternMatchListenerDelegate;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;


public abstract class AbstractJavacPatternMatcher implements IPatternMatchListenerDelegate
{

    protected TextConsole fConsole;

    private static Pattern fgLineNumberPattern = Pattern.compile("\\d+"); //$NON-NLS-1$

    private Map fFileNameToIFile = new HashMap();

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.console.IPatternMatchListenerDelegate#connect(org.eclipse.ui.console.TextConsole)
     */
    public void connect(TextConsole console)
    {
        fConsole = console;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.console.IPatternMatchListenerDelegate#disconnect()
     */
    public void disconnect()
    {
        fConsole = null;
        fFileNameToIFile.clear();
    }

    @SuppressWarnings("deprecation")
    protected IFile getIFile(String filePath)
    {
        if (filePath == null)
        {
            return null;
        }
        IFile file = (IFile) fFileNameToIFile.get(filePath);
        if (file == null)
        {
            IFile[] files = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocation(new Path(filePath));
            if (files.length > 0)
            {
                file = files[0];
                fFileNameToIFile.put(filePath, file);
            }
        }
        return file;
    }

    protected String getMatchedText(PatternMatchEvent event)
    {
        int eventOffset = event.getOffset();
        int eventLength = event.getLength();
        IDocument document = fConsole.getDocument();
        String matchedText = null;
        try
        {
            matchedText = document.get(eventOffset, eventLength);
        }
        catch (BadLocationException e)
        {
            Plugin.log(e);
        }
        return matchedText;
    }

    protected int getLineNumber(int eventOffset)
    {
        IDocument document = fConsole.getDocument();
        try
        {
            int fileLine = document.getLineOfOffset(eventOffset);
            IRegion region = document.getLineInformation(++fileLine);
            String lineLine = document.get(region.getOffset(), region.getLength());
            Matcher matcher = null;
            synchronized (fgLineNumberPattern)
            {
                matcher = fgLineNumberPattern.matcher(lineLine);
            }
            if (matcher.find())
            {
                String lineString = matcher.group();
                return Integer.parseInt(lineString);
            }

        }
        catch (BadLocationException e)
        {
            Plugin.log(e);
        }
        catch (NumberFormatException e)
        {
            Plugin.log(e);
        }
        return -1;
    }

    protected void addLink(String filePath, int lineNumber, int offset, int length)
    {
        IFile file = getIFile(filePath);
        if (file == null)
        {
            file = getIFile(filePath+".java");
        }

        if (file == null)
        {
            return;
        }

        FileLink link = new FileLink(file, null, -1, -1, lineNumber);
        try
        {
            fConsole.addHyperlink(link, offset, length);
        }
        catch (BadLocationException e)
        {
            Plugin.log(e);
        }
    }
}