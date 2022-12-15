package org.smoothbuild.compile.ps;

import static org.smoothbuild.compile.lang.base.ValidNamesS.isVarName;
import static org.smoothbuild.compile.lang.base.ValidNamesS.startsWithLowerCase;
import static org.smoothbuild.compile.lang.base.ValidNamesS.startsWithUpperCase;
import static org.smoothbuild.compile.lang.type.AnnotationNames.ANNOTATION_NAMES;
import static org.smoothbuild.compile.lang.type.AnnotationNames.BYTECODE;
import static org.smoothbuild.compile.lang.type.AnnotationNames.NATIVE_IMPURE;
import static org.smoothbuild.compile.lang.type.AnnotationNames.NATIVE_PURE;
import static org.smoothbuild.compile.ps.CompileError.compileError;

import org.smoothbuild.compile.lang.define.DefsS;
import org.smoothbuild.compile.ps.ast.Ast;
import org.smoothbuild.compile.ps.ast.AstVisitor;
import org.smoothbuild.compile.ps.ast.expr.AnonymousFuncP;
import org.smoothbuild.compile.ps.ast.expr.ItemP;
import org.smoothbuild.compile.ps.ast.expr.NamedFuncP;
import org.smoothbuild.compile.ps.ast.expr.NamedValueP;
import org.smoothbuild.compile.ps.ast.expr.RefableP;
import org.smoothbuild.compile.ps.ast.expr.StructP;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;

public class FindSyntaxErrors {
  /**
   * Detect syntax errors that are not caught by Antlr
   * because its grammar is not so demanding, so it can be more compact.
   */
  public static LogBuffer findSyntaxErrors(DefsS imported, Ast ast) {
    var logBuffer = new LogBuffer();
    detectIllegalNames(logBuffer, ast);
    detectIllegalAnnotations(logBuffer, ast);
    detectStructFieldWithDefaultValue(logBuffer, ast);
    detectAnonymousFuncParamWithDefaultValue(logBuffer, ast);
    return logBuffer;
  }

  private static void detectIllegalNames(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitNameOf(RefableP refable) {
        var name = refable.name();
        if (name.equals("_")) {
          logger.log(compileError(refable.location(), "`" + name + "` is illegal identifier name. "
              + "`_` is reserved for future use."));
        } else if (!startsWithLowerCase(name)) {
          logger.log(compileError(refable.location(), "`" + name + "` is illegal identifier name. "
          + "Identifiers should start with lowercase."));
        }
      }

      @Override
      public void visitStruct(StructP struct) {
        super.visitStruct(struct);
        var name = struct.name();
        if (name.equals("_")) {
          logger.log(compileError(struct.location(), "`" + name + "` is illegal struct name. "
              + "`_` is reserved for future use."));
        } else if (isVarName(name)) {
          logger.log(compileError(struct.location(), "`" + name + "` is illegal struct name."
              + " All-uppercase names are reserved for type variables in generic types."));
        } else if (!startsWithUpperCase(name)) {
          logger.log(compileError(struct.location(), "`" + name + "` is illegal struct name."
              + " Struct name must start with uppercase letter."));
        }
      }
    }.visitAst(ast);
  }

  private static void detectIllegalAnnotations(Logger logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitNamedFunc(NamedFuncP namedFuncP) {
        super.visitNamedFunc(namedFuncP);
        if (namedFuncP.annotation().isPresent()) {
          var ann = namedFuncP.annotation().get();
          var annName = ann.name();
          if (ANNOTATION_NAMES.contains(annName)) {
            if (namedFuncP.body().isPresent()) {
              logger.log(compileError(namedFuncP,
                  "Function " + namedFuncP.q() + " with @" + annName + " annotation cannot have body."));
            }
            if (namedFuncP.resT().isEmpty()) {
              logger.log(compileError(namedFuncP, "Function " + namedFuncP.q() + " with @" + annName
                  + " annotation must declare result type."));
            }
          } else {
            logger.log(compileError(ann.location(), "Unknown annotation " + ann.q() + "."));
          }
        } else if (namedFuncP.body().isEmpty()) {
          logger.log(compileError(namedFuncP, "Function body is missing."));
        }
      }

      @Override
      public void visitNamedValue(NamedValueP namedValueP) {
        super.visitNamedValue(namedValueP);
        if (namedValueP.annotation().isPresent()) {
          var ann = namedValueP.annotation().get();
          var annName = ann.name();
          switch (annName) {
            case BYTECODE -> {
              if (namedValueP.body().isPresent()) {
                logger.log(
                    compileError(namedValueP, "Value with @" + annName + " annotation cannot have body."));
              }
              if (namedValueP.type().isEmpty()) {
                logger.log(compileError(namedValueP, "Value " + namedValueP.q() + " with @" + annName
                    + " annotation must declare type."));
              }
            }
            case NATIVE_PURE, NATIVE_IMPURE -> logger.log(
                compileError(
                    namedValueP.annotation().get(),
                    "Value cannot have @" + annName + " annotation."));
            default -> logger.log(compileError(ann, "Unknown annotation " + ann.q() + "."));
          }
        } else if (namedValueP.body().isEmpty()) {
          logger.log(compileError(namedValueP, "Value cannot have empty body."));
        }

      }
    }.visitAst(ast);
  }

  private static void detectStructFieldWithDefaultValue(LogBuffer logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitField(ItemP field) {
        super.visitField(field);
        if (field.defaultValue().isPresent()) {
          logger.log(compileError(field.location(), "Struct field `" + field.name()
              + "` has default value. Only function parameters can have default value."));
        }
      }
    }.visitAst(ast);
  }

  private static void detectAnonymousFuncParamWithDefaultValue(LogBuffer logger, Ast ast) {
    new AstVisitor() {
      @Override
      public void visitAnonymousFunc(AnonymousFuncP anonymousFuncP) {
        super.visitAnonymousFunc(anonymousFuncP);
        anonymousFuncP.params().forEach(this::logErrorIfDefaultValuePresent);
      }

      private void logErrorIfDefaultValuePresent(ItemP param) {
        if (param.defaultValue().isPresent()) {
          logger.log(compileError(param.location(),
              "Parameter " + param.q() + " of anonymous function cannot have default value."));
        }
      }
    }.visitAst(ast);
  }
}
