/*
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.demos.complexgenerator.rebind;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JField;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import java.io.PrintWriter;

/**
 * An implementation for ComplexGenerator.
 */
public class ComplexGenerator extends Generator {

  @Override
  public String generate(TreeLogger logger, GeneratorContext context,
      String typeName) throws UnableToCompleteException {

    JClassType requestedClass;
    try {
      requestedClass = context.getTypeOracle().getType(typeName);
    } catch (NotFoundException e) {
      logger.log(
          TreeLogger.ERROR,
          "Could not find type '"
              + typeName
              + "'; please see the log, as this usually indicates a previous error ",
          e);
      throw new UnableToCompleteException();
    }
    // Get the stub class name
    String generatedClass = requestedClass.getSimpleSourceName() + "Impl";
    String packageName = requestedClass.getPackage().getName();
    String qualifiedStubClassName = packageName + "." + generatedClass;

    // check if its source file exists.
    SourceWriter sourceWriter = getSourceWriter(logger, context, packageName,
        generatedClass, typeName);
    if (sourceWriter != null) {
      addMetaDataMethod(sourceWriter, requestedClass);
      addGetValueMethod(sourceWriter, requestedClass);
      sourceWriter.commit(logger);
    }
    return qualifiedStubClassName;
  }

  private void addGetValueMethod(SourceWriter sw, JClassType requestedClass) {
    sw.println();
    sw.println("public String getValue() {");
    sw.indent();
    JField fields[] = requestedClass.getFields();
    StringBuffer sb = new StringBuffer();
    sb.append("return ");
    for (int i = 0; i < fields.length - 1; i++) {
      appendField(sb, fields[i]);
      sb.append(" + \", \" + ");
    }
    if (fields.length > 0) {
      appendField(sb, fields[fields.length - 1]);
    } else {
      sb.append("\"\"");
    }
    sb.append(";");
    sw.println(sb.toString());
    sw.outdent();
    sw.println("}");
  }
  
  private void addMetaDataMethod(SourceWriter sw, JClassType requestedClass) {
    sw.println();
    sw.println("public String getMetaData() {");
    sw.indent();
    sw.println("return \""
        + "w00t! This is an automatically generated method for "
        + requestedClass.getQualifiedSourceName() + "\";");
    sw.outdent();
    sw.println("}");
  }
  
  private void appendField(StringBuffer sb, JField field) {
    sb.append("\"");
    sb.append(field.getName());
    sb.append(":\" + ");
    sb.append(field.getName());
  }

  private SourceWriter getSourceWriter(TreeLogger logger, GeneratorContext ctx,
      String packageName, String className, String superclassName) {
    PrintWriter printWriter = ctx.tryCreate(logger, packageName, className);
    if (printWriter == null) {
      return null;
    }
    ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(
        packageName, className);
    composerFactory.setSuperclass(superclassName);
    return composerFactory.createSourceWriter(ctx, printWriter);
  }

}
