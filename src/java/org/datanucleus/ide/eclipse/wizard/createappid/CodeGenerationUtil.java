/**********************************************************************
 Copyright (c) Sep 4, 2004 Erik Bengtson and others.
 All rights reserved. This program and the accompanying materials
 are made available under the terms of the JPOX License v1.0
 which accompanies this distribution. 

 Contributors:
 ...
 **********************************************************************/
package org.datanucleus.ide.eclipse.wizard.createappid;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

/**
 * @author erik
 * @version $Revision: 1.1 $
 */
public class CodeGenerationUtil
{
    /**
     * Generates a conversion code from an string into a type <br/>example
     * <br/><i>new Integer(expression).intValue(); </i>
     * @param expression
     * @param typeSignature
     * @return an expression that converts a string to a primitive
     */
    public static String convert(String expression, String typeSignature)
    {
        if (isPrimitiveType(typeSignature))
        {
            return convertToPrimitive(expression, typeSignature);
        }
        else
        {
            return convertToWrapper(expression, Signature.toString(typeSignature));
        }
    }

    /**
     * @param expression
     * @param wrapperType the wrapper type. eg Integer, java.lang.Integer,
     * String
     * @return an expression that converts a string to a wrapper
     */
    private static String convertToWrapper(String expression, String typeSignature)
    {
        return "new " + typeSignature + "(" + expression + ")";
    }

    private static String convertToPrimitive(String expression, String typeSignature)
    {
        String convertedExpression;
        convertedExpression = convertToWrapper(appendToExpresson(expression, typeSignature), getWrapper(typeSignature)) + "." + Signature
                .toString(typeSignature) + "Value()";
        return convertedExpression;
    }

    private static String getWrapper(String typeSignature)
    {
        char c = typeSignature.charAt(0);
        switch (c)
        {
            case Signature.C_BOOLEAN :
                return "java.lang.Boolean";
            case Signature.C_BYTE :
                return "java.lang.Byte";
            case Signature.C_CHAR :
                return "java.lang.Character";
            case Signature.C_DOUBLE :
                return "java.lang.Double";
            case Signature.C_FLOAT :
                return "java.lang.Float";
            case Signature.C_INT :
                return "java.lang.Integer";
            case Signature.C_LONG :
                return "java.lang.Long";
            case Signature.C_SHORT :
                return "java.lang.Short";
        }
        return Signature.toString(typeSignature);
    }

    private static String appendToExpresson(String expression, String typeSignature)
    {
        char c = typeSignature.charAt(0);
        switch (c)
        {
            case Signature.C_CHAR :
                return expression + ".toCharArray()[0]";
        }
        return expression;
    }

    public static boolean isPrimitiveType(String typeSignature)
    {
        if (typeSignature.length() > 1)
        {
            return false;
        }
        else
        {
            char c = typeSignature.charAt(0);
            return (c == Signature.C_BOOLEAN || c == Signature.C_BYTE || c == Signature.C_CHAR || c == Signature.C_DOUBLE || c == Signature.C_FLOAT || c == Signature.C_INT || c == Signature.C_LONG || c == Signature.C_SHORT);
        }
    }

    /**
     * Generates an equals method
     * @param className
     * @param type
     * @return an equals method
     * @throws JavaModelException
     */
    public static String createEqualsMethod(String className, IType type) throws JavaModelException
    {
        StringBuffer buf = new StringBuffer();

        buf.append("public boolean equals(Object o) {");
        buf.append("if(this == o) {\n");
        buf.append("return true; }\n");

        /*
         * if the class has a super class (which is not java.lang.Object), the
         */
        if (!(type.getSuperclassName() == null || type.getSuperclassName().equals("java.lang.Object")))
        {
            buf.append("if(!super.equals(o)) {\n");
            buf.append("return false; }\n");
        }

        buf.append("if(o == null) {\n");
        buf.append("return false; }\n");

        /*
         * The classes must be the same, to make equals symmetric. The
         * instanceof operator must not be used because:
         */
        buf.append("if(o.getClass() != getClass()) {\n");
        buf.append("return false;}\n");

        buf.append(className + " objToCompare = (" + className + ") o;\n");

        IField[] fields = type.getFields();
        for (int i = 0; i < fields.length; i++)
        {
            IField f = fields[i];
            String fname = f.getElementName();
            String fsig = f.getTypeSignature();
            int dims = Signature.getArrayCount(fsig);
            if (!Flags.isStatic(f.getFlags()) && (dims > 1))
            {
                String elsig = Signature.getElementType(fsig);
                buf.append("if (this." + fname + " != null) {");
                buf.append("if (this." + fname + ".length != objToCompare." + fname + ".length){ return false;}");
                for (int j = 0; j < dims; j++)
                {
                    buf.append("for (int i" + j + " = 0; i" + j + " < " + subElement("this." + fname, j) + ".length; i" + j + "++) {");
                    if (j < dims - 1)
                    {
                        buf.append("if (" + subElement("this." + fname, j + 1) + ".length != " + subElement("objToCompare." + fname, j + 1) + ".length) { return false; }");
                    }
                }

                if (isPrimitiveType(elsig))
                {
                    buf.append(checkPrimitive(subElement("this." + fname, dims), subElement("objToCompare." + fname, dims)));
                }
                else
                {
                    buf.append(checkObject(subElement("this." + fname, dims), subElement("objToCompare." + fname, dims)));
                }

                for (int j = 0; j < dims; j++)
                {
                    buf.append("}");
                }
                buf.append("}");
                buf.append("if(objToCompare." + fname + " != null)\n");
                buf.append("return false;\n");
            }
        }

        boolean started = false;

        for (int i = 0; i < fields.length; i++)
        {
            IField f = fields[i];
            String fname = f.getElementName();
            String fsig = f.getTypeSignature();
            int dims = Signature.getArrayCount(fsig);

            if (!Flags.isStatic(f.getFlags()) && (dims < 2))
            {
                if (!started)
                {
                    started = true;
                    buf.append("return (");
                }
                else
                {
                    buf.append("&&");
                }
                if (isPrimitiveType(fsig))
                {
                    buf.append("(this." + fname + " == objToCompare." + fname + ")");
                }
                else if (dims == 0)
                {
                    buf.append("(this." + fname + " == null ? objToCompare." + fname + " == null :" + "this." + fname + ".equals(objToCompare." + fname + "))");
                }
                else
                    // dims==1
                    buf.append("java.util.Arrays.equals(this." + fname + ",objToCompare. " + fname + ")");
            }
        }
        if (started)
        {
            buf.append(");\n");
        }
        else
        {
            buf.append("return true;\n");
        }
        buf.append("}");

        return buf.toString();
    }

    private static String checkPrimitive(String a, String b)
    {
        return "if (" + a + " != " + b + ") return false;";
    }

    private static String checkObject(String a, String b)
    {
        return "if (!(" + a + " == null ? " + b + " == null : " + a + ".equals(" + b + "))) return false;";
    }

    /**
     * @param level >= 0
     */
    private static String subElement(String a, int level)
    {
        StringBuffer buf = new StringBuffer();
        buf.append(a);
        for (int i = 0; i < level; i++)
        {
            buf.append("[i" + i + "]");
        }
        return buf.toString();
    }

}