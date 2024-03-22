package org.smoothbuild.compilerfrontend.parse;

import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILE_PREFIX;
import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;

import org.smoothbuild.common.dag.TryFunction1;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.compilerfrontend.compile.ast.ModuleVisitorP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ImplicitTP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ItemP;
import org.smoothbuild.compilerfrontend.compile.ast.define.LambdaP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ModuleP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedFuncP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedValueP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ReferenceableP;
import org.smoothbuild.compilerfrontend.compile.ast.define.StructP;
import org.smoothbuild.compilerfrontend.lang.base.TypeNamesS;
import org.smoothbuild.compilerfrontend.lang.type.AnnotationNames;

/**
 * Detect syntax errors that are not caught by Antlr.
 * We could extend Antlr grammar to catch those,
 * but it would make it more complex.
 * Catching those errors here makes it easier
 * to provide more detailed error message.
 */
public class FindSyntaxErrors implements TryFunction1<ModuleP, ModuleP> {
  @Override
  public Label label() {
    return Label.label(COMPILE_PREFIX, "findSyntaxErrors");
  }

  @Override
  public Try<ModuleP> apply(ModuleP moduleP) {
    var logger = new Logger();
    detectIllegalNames(moduleP, logger);
    detectIllegalAnnotations(moduleP, logger);
    detectStructFieldWithDefaultValue(moduleP, logger);
    detectLambdaParamWithDefaultValue(moduleP, logger);
    return Try.of(moduleP, logger);
  }

  private static void detectIllegalNames(ModuleP moduleP, Logger logger) {
    new ModuleVisitorP() {
      @Override
      public void visitNameOf(ReferenceableP referenceableP) {
        var name = referenceableP.shortName();
        if (name.equals("_")) {
          logger.log(compileError(
              referenceableP.location(),
              "`" + name + "` is illegal identifier name. `_` is reserved for future use."));
        } else if (!TypeNamesS.startsWithLowerCase(name)) {
          logger.log(compileError(
              referenceableP.location(),
              "`" + name
                  + "` is illegal identifier name. Identifiers should start with lowercase."));
        }
      }

      @Override
      public void visitStruct(StructP structP) {
        super.visitStruct(structP);
        var name = structP.name();
        if (name.equals("_")) {
          logger.log(compileError(
              structP.location(),
              "`" + name + "` is illegal struct name. " + "`_` is reserved for future use."));
        } else if (TypeNamesS.isVarName(name)) {
          logger.log(compileError(
              structP.location(),
              "`" + name + "` is illegal struct name."
                  + " All-uppercase names are reserved for type variables in generic types."));
        } else if (!TypeNamesS.startsWithUpperCase(name)) {
          logger.log(compileError(
              structP.location(),
              "`" + name + "` is illegal struct name."
                  + " Struct name must start with uppercase letter."));
        }
      }
    }.visitModule(moduleP);
  }

  private static void detectIllegalAnnotations(ModuleP moduleP, Logger logger) {
    new ModuleVisitorP() {
      @Override
      public void visitNamedFunc(NamedFuncP namedFuncP) {
        super.visitNamedFunc(namedFuncP);
        if (namedFuncP.annotation().isSome()) {
          var ann = namedFuncP.annotation().get();
          var annName = ann.name();
          if (AnnotationNames.ANNOTATION_NAMES.contains(annName)) {
            if (namedFuncP.body().isSome()) {
              logger.log(compileError(
                  namedFuncP,
                  "Function " + namedFuncP.q() + " with @" + annName
                      + " annotation cannot have body."));
            }
            if (namedFuncP.resultT() instanceof ImplicitTP) {
              logger.log(compileError(
                  namedFuncP,
                  "Function " + namedFuncP.q() + " with @" + annName
                      + " annotation must declare result type."));
            }
          } else {
            logger.log(compileError(ann.location(), "Unknown annotation " + ann.q() + "."));
          }
        } else if (namedFuncP.body().isNone()) {
          logger.log(compileError(namedFuncP, "Function body is missing."));
        }
      }

      @Override
      public void visitNamedValue(NamedValueP namedValueP) {
        super.visitNamedValue(namedValueP);
        if (namedValueP.annotation().isSome()) {
          var ann = namedValueP.annotation().get();
          var annName = ann.name();
          switch (annName) {
            case AnnotationNames.BYTECODE -> {
              if (namedValueP.body().isSome()) {
                logger.log(compileError(
                    namedValueP, "Value with @" + annName + " annotation cannot have body."));
              }
              if (namedValueP.type() instanceof ImplicitTP) {
                logger.log(compileError(
                    namedValueP,
                    "Value " + namedValueP.q() + " with @" + annName
                        + " annotation must declare type."));
              }
            }
            case AnnotationNames.NATIVE_PURE, AnnotationNames.NATIVE_IMPURE -> logger.log(
                compileError(
                    namedValueP.annotation().get(),
                    "Value cannot have @" + annName + " annotation."));
            default -> logger.log(compileError(ann, "Unknown annotation " + ann.q() + "."));
          }
        } else if (namedValueP.body().isNone()) {
          logger.log(compileError(namedValueP, "Value cannot have empty body."));
        }
      }
    }.visitModule(moduleP);
  }

  private static void detectStructFieldWithDefaultValue(ModuleP moduleP, Logger logger) {
    new ModuleVisitorP() {
      @Override
      public void visitStruct(StructP structP) {
        super.visitStruct(structP);
        structP.fields().forEach(this::logErrorIfDefaultValuePresent);
      }

      private void logErrorIfDefaultValuePresent(ItemP param) {
        if (param.defaultValue().isSome()) {
          logger.log(compileError(
              param.location(),
              "Struct field `" + param.name()
                  + "` has default value. Only function parameters can have default value."));
        }
      }
    }.visitModule(moduleP);
  }

  private static void detectLambdaParamWithDefaultValue(ModuleP moduleP, Logger logger) {
    new ModuleVisitorP() {
      @Override
      public void visitLambda(LambdaP lambdaP) {
        super.visitLambda(lambdaP);
        lambdaP.params().forEach(this::logErrorIfDefaultValuePresent);
      }

      private void logErrorIfDefaultValuePresent(ItemP param) {
        if (param.defaultValue().isSome()) {
          logger.log(compileError(
              param.location(),
              "Parameter " + param.q() + " of lambda cannot have default value."));
        }
      }
    }.visitModule(moduleP);
  }
}
