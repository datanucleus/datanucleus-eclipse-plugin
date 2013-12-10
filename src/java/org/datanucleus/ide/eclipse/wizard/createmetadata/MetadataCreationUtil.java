/**********************************************************************
Copyright (c) 2005 Michael Grundmann and others. ALl rights reserved.
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
2007 Andy Jefferson - documented and added final/static traps on fields
    ...
**********************************************************************/
package org.datanucleus.ide.eclipse.wizard.createmetadata;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Modifier;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Utility class to create a new JDO 2.0 Metadata file.
 * 
 * @version $Revision: 1.1 $
 */
public class MetadataCreationUtil
{
    private static final String LINE_SEPARATOR = System.getProperty("line.separator"); //$NON-NLS-1$

    private static final String DEFAULT_INDENT = "    "; //$NON-NLS-1$

    private static final String ATTRIBUTE_INDENT = "      "; //$NON-NLS-1$

    /**
     * Open an <code>InputStream</code> and provide basic content.
     * @param model The model holding the values from the dialog
     * @return An InputStream to be written into a file
     * @throws JavaModelException
     */
    public static InputStream getMetadataContentStream(MetadataCreationModel model) throws JavaModelException
    {
        StringBuffer contents = new StringBuffer();
        writeHeader(contents, model);
        writeBody(contents, model);
        return new ByteArrayInputStream(contents.toString().getBytes());
    }

    /**
     * Write the body of the metadata file.
     * @param The StringBuffer all content is added to
     * @param The model holding the values from the dialog
     * @throws JavaModelException
     */
    private static void writeBody(StringBuffer contents, MetadataCreationModel model) throws JavaModelException
    {
        String currentIdent = LINE_SEPARATOR + DEFAULT_INDENT;
        contents.append("<jdo>");
        contents.append(currentIdent);
        contents.append("<package name=\"");
        contents.append(model.getPackageFragment().getElementName());
        contents.append("\">");
        writeClasses(contents, model, currentIdent);
        contents.append(currentIdent);
        contents.append("</package>");
        contents.append(LINE_SEPARATOR);
        contents.append("</jdo>");
    }

    /**
     * Write Metadata for classes.
     * @param The StringBuffer all content is added to
     * @param The model holding the values from the dialog
     * @param The line indent
     * @throws JavaModelException 
     * TODO Add support for interfaces 
     * TODO Add support for fine-tuning. (preference page, dialog, etc) 
     * (for example setting the detachable flag by default)
     */
    private static void writeClasses(StringBuffer contents, MetadataCreationModel model, String parentIdent) throws JavaModelException
    {
        String currentIdent = parentIdent + DEFAULT_INDENT;
        String attributeIdent = currentIdent + ATTRIBUTE_INDENT;

        ICompilationUnit[] compilationUnits = model.getAffectedClasses();
        for (int i = 0; i < compilationUnits.length; i++)
        {
            IType[] types = compilationUnits[i].getTypes();
            for (int j = 0; j < types.length; j++)
            {
                IType currentType = types[j];
                if (currentType.isClass())
                {
                    contents.append(currentIdent);
                    contents.append("<class");
                    writeAttribute(contents, "name", currentType.getElementName(), attributeIdent);
                    contents.append(">");
                    writeFields(contents, currentType, currentIdent);
                    contents.append(currentIdent);
                    contents.append("</class>");
                }
            }
        }
    }

    /**
     * Write metadata informations for the fields defined in the
     * <code>IType</code> passed to this method.
     * @param The StringBuffer all content is added to
     * @param The current <code>IType</code>
     * @param The line indent
     * @throws JavaModelException
     */
    private static void writeFields(StringBuffer contents, IType currentType, String parentIndent) throws JavaModelException
    {
        String currentIndent = parentIndent + DEFAULT_INDENT;
        String attributeIndent = currentIndent + ATTRIBUTE_INDENT;

        IField[] fields = currentType.getFields();
        for (int i = 0; i < fields.length; i++)
        {
            IField currentField = fields[i];
            int flags = currentField.getFlags();
            if (Modifier.isStatic(flags) || Modifier.isTransient(flags) || Modifier.isFinal(flags))
            {
                // Dont generate fields for static/final/transient fields
                continue;
            }

            contents.append(currentIndent);
            contents.append("<field");
            writeAttribute(contents, "name", currentField.getElementName(), attributeIndent);
            writeAttribute(contents, "persistence-modifier", "persistent", attributeIndent);
            contents.append("/>");
        }
    }

    /**
     * Write valid header information and set the DOCTYPE for this document.
     * @param The StringBuffer all content is added to
     * @param The model holding the values from the dialog TODO Add support for
     * ORM files
     */
    private static void writeHeader(StringBuffer contents, MetadataCreationModel model)
    {
        contents.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        contents.append(LINE_SEPARATOR);
        contents.append("<!DOCTYPE jdo PUBLIC");
        contents.append(LINE_SEPARATOR);
        contents.append(DEFAULT_INDENT);
        contents.append("\"-//Sun Microsystems, Inc.//DTD Java Data Objects Metadata 2.0//EN\"");
        contents.append(LINE_SEPARATOR);
        contents.append(DEFAULT_INDENT);
        contents.append("\"http://java.sun.com/dtd/jdo_2_0.dtd\">");
        contents.append(LINE_SEPARATOR);
    }

    /**
     * Utility method to write an attribute to the stream using appropriate line
     * indenting.
     * @param The StringBuffer all content is added to
     * @param The name of this attribute
     * @param This attribute's value
     * @param The line indent
     */
    private static void writeAttribute(StringBuffer contents, String name, String value, String indent)
    {
        contents.append(indent);
        contents.append(name);
        contents.append("=\"");
        contents.append(value);
        contents.append("\"");
    }
}