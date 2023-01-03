package org.smoothbuild.compile.fs.fp;

import static org.smoothbuild.compile.fs.lang.base.TypeNamesS.isVarName;
import static org.smoothbuild.compile.fs.lang.base.TypeNamesS.startsWithLowerCase;
import static org.smoothbuild.compile.fs.lang.base.TypeNamesS.startsWithUpperCase;
import static org.smoothbuild.compile.fs.lang.type.AnnotationNames.ANNOTATION_NAMES;
import static org.smoothbuild.compile.fs.lang.type.AnnotationNames.BYTECODE;
import static org.smoothbuild.compile.fs.lang.type.AnnotationNames.NATIVE_IMPURE;
import static org.smoothbuild.compile.fs.lang.type.AnnotationNames.NATIVE_PURE;
import static org.smoothbuild.compile.fs.ps.CompileError.compileError;

import org.smoothbuild.compile.fs.ps.ast.ModuleVisitorP;
import org.smoothbuild.compile.fs.ps.ast.define.AnonymousFuncP;
import org.smoothbuild.compile.fs.ps.ast.define.ItemP;
import org.smoothbuild.compile.fs.ps.ast.define.ModuleP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedFuncP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedValueP;
import org.smoothbuild.compile.fs.ps.ast.define.RefableP;
import org.smoothbuild.compile.fs.ps.ast.define.StructP;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;

/**
 * Detect syntax errors that are not caught by Antlr.
 * We could extend Antlr grammar to catch those,
 * but it would make it more complex.
 * Catching those errors here makes it easier
 * to provide more detailed error message.
 */
public class FindSyntaxErrors {
  public static LogBuffer findSyntaxErrors(ModuleP moduleP) {
    var logBuffer = new LogBuffer();
    detectIllegalNames(logBuffer, moduleP);
    detectIllegalAnnotations(logBuffer, moduleP);
    detectStructFieldWithDefaultValue(logBuffer, moduleP);
    detectAnonymousFuncParamWithDefaultValue(logBuffer, moduleP);
    return logBuffer;
  }

  private static void detectIllegalNames(Logger logger, ModuleP moduleP) {
    new ModuleVisitorP() {
      @Override
      public void visitNameOf(RefableP refableP) {
        var name = refableP.simpleName();
        if (name.equals("_")) {
          logger.log(compileError(refableP.location(), "`" + name + "` is illegal identifier name. "
              + "`_` is reserved for future use."));
        } else if (!startsWithLowerCase(name)) {
          logger.log(compileError(refableP.location(), "`" + name + "` is illegal identifier name. "
          + "Identifiers should start with lowercase."));
        }
      }

      @Override
      public void visitStruct(StructP structP) {
        super.visitStruct(structP);
        var name = structP.name();
        if (name.equals("_")) {
          logger.log(compileError(structP.location(), "`" + name + "` is illegal struct name. "
              + "`_` is reserved for future use."));
        } else if (isVarName(name)) {
          logger.log(compileError(structP.location(), "`" + name + "` is illegal struct name."
              + " All-uppercase names are reserved for type variables in generic types."));
        } else if (!startsWithUpperCase(name)) {
          logger.log(compileError(structP.location(), "`" + name + "` is illegal struct name."
              + " Struct name must start with uppercase letter."));
        }
      }
    }.visitModule(moduleP);
  }

  private static void detectIllegalAnnotations(Logger logger, ModuleP moduleP) {
    new ModuleVisitorP() {
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
    }.visitModule(moduleP);
  }

  private static void detectStructFieldWithDefaultValue(LogBuffer logger, ModuleP moduleP) {
    new ModuleVisitorP() {
      @Override
      public void visitStruct(StructP structP) {
        super.visitStruct(structP);
        structP.fields().forEach(this::logErrorIfDefaultValuePresent);
      }

      private void logErrorIfDefaultValuePresent(ItemP param) {
        if (param.defaultValue().isPresent()) {
          logger.log(compileError(param.location(), "Struct field `" + param.name()
              + "` has default value. Only function parameters can have default value."));
        }
      }
    }.visitModule(moduleP);
  }

  private static void detectAnonymousFuncParamWithDefaultValue(LogBuffer logger, ModuleP moduleP) {
    new ModuleVisitorP() {
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
    }.visitModule(moduleP);
  }
}
