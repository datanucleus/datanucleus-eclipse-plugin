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
 * Pattern matcher that matches CLASSPATH entries for SchemaTool and the Enhancer and marks them with hyperlinks.
 * All classpath entries are preceded by ">> "
 */
public class ClasspathEntryPatternMatcher extends AbstractJavacPatternMatcher
{
    public void matchFound(PatternMatchEvent event)
    {
        String matchedText = getMatchedText(event);
        if (matchedText == null)
        {
            return;
        }

        int index = matchedText.indexOf(">> ");
        String filePath = matchedText.substring(index + 3);
        filePath = filePath.trim();

        int fileStart = matchedText.indexOf(filePath);
        int eventOffset = event.getOffset() + fileStart;
        int eventLength = filePath.length();

        int lineNumber = -1;
        addLink(filePath, lineNumber, eventOffset, eventLength);
    }
}