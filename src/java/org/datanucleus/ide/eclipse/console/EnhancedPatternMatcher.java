/**********************************************************************
Copyright (c) 2004 Andy Jefferson and others. All rights reserved.
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
package org.datanucleus.ide.eclipse.console;

import org.eclipse.ui.console.PatternMatchEvent;

/**
 * Pattern matcher that matches the Enhancer output lines, and adds hyperlinks for the classes referenced.
 * All enhancer success/failure lines are marked with "ENHANCED"
 * TODO This is currently disabled in plugin.xml since there is a TODO below that is required first
 */
public class EnhancedPatternMatcher extends AbstractJavacPatternMatcher
{
    public void matchFound(PatternMatchEvent event)
    {
        String matchedText = getMatchedText(event);
        if (matchedText == null)
        {
            return;
        }

        int index = matchedText.indexOf(" : ");
        String javaClassName = matchedText.substring(index + 3).trim();

        // TODO this is relative to the root of the classpath, so need to make absolute filename
        String javaFileName = javaClassName.replace('.', '/');

        int fileStart = matchedText.indexOf(javaClassName);
        int eventOffset = event.getOffset() + fileStart;
        int eventLength = javaClassName.length();

        int lineNumber = -1;
        addLink(javaFileName, lineNumber, eventOffset, eventLength);
    }
}