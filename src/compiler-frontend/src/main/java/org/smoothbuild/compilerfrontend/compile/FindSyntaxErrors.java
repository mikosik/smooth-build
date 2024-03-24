package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILE_PREFIX;
import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;

import org.smoothbuild.common.dag.TryFunction1;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PImplicitType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReferenceable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.lang.base.TypeNamesS;
import org.smoothbuild.compilerfrontend.lang.type.AnnotationNames;

/**
 * Detect syntax errors that are not caught by Antlr.
 * We could extend Antlr grammar to catch those,
 * but it would make it more complex.
 * Catching those errors here makes it easier
 * to provide more detailed error message.
 */
public class FindSyntaxErrors implements TryFunction1<PModule, PModule> {
  @Override
  public Label label() {
    return Label.label(COMPILE_PREFIX, "findSyntaxErrors");
  }

  @Override
  public Try<PModule> apply(PModule pModule) {
    var logger = new Logger();
    detectIllegalNames(pModule, logger);
    detectIllegalAnnotations(pModule, logger);
    detectStructFieldWithDefaultValue(pModule, logger);
    detectLambdaParamWithDefaultValue(pModule, logger);
    return Try.of(pModule, logger);
  }

  private static void detectIllegalNames(PModule pModule, Logger logger) {
    new PModuleVisitor() {
      @Override
      public void visitNameOf(PReferenceable pReferenceable) {
        var name = pReferenceable.shortName();
        if (name.equals("_")) {
          logger.log(compileError(
              pReferenceable.location(),
              "`" + name + "` is illegal identifier name. `_` is reserved for future use."));
        } else if (!TypeNamesS.startsWithLowerCase(name)) {
          logger.log(compileError(
              pReferenceable.location(),
              "`" + name
                  + "` is illegal identifier name. Identifiers should start with lowercase."));
        }
      }

      @Override
      public void visitStruct(PStruct pStruct) {
        super.visitStruct(pStruct);
        var name = pStruct.name();
        if (name.equals("_")) {
          logger.log(compileError(
              pStruct.location(),
              "`" + name + "` is illegal struct name. " + "`_` is reserved for future use."));
        } else if (TypeNamesS.isVarName(name)) {
          logger.log(compileError(
              pStruct.location(),
              "`" + name + "` is illegal struct name."
                  + " All-uppercase names are reserved for type variables in generic types."));
        } else if (!TypeNamesS.startsWithUpperCase(name)) {
          logger.log(compileError(
              pStruct.location(),
              "`" + name + "` is illegal struct name."
                  + " Struct name must start with uppercase letter."));
        }
      }
    }.visitModule(pModule);
  }

  private static void detectIllegalAnnotations(PModule pModule, Logger logger) {
    new PModuleVisitor() {
      @Override
      public void visitNamedFunc(PNamedFunc pNamedFunc) {
        super.visitNamedFunc(pNamedFunc);
        if (pNamedFunc.annotation().isSome()) {
          var ann = pNamedFunc.annotation().get();
          var annName = ann.name();
          if (AnnotationNames.ANNOTATION_NAMES.contains(annName)) {
            if (pNamedFunc.body().isSome()) {
              logger.log(compileError(
                  pNamedFunc,
                  "Function " + pNamedFunc.q() + " with @" + annName
                      + " annotation cannot have body."));
            }
            if (pNamedFunc.resultT() instanceof PImplicitType) {
              logger.log(compileError(
                  pNamedFunc,
                  "Function " + pNamedFunc.q() + " with @" + annName
                      + " annotation must declare result type."));
            }
          } else {
            logger.log(compileError(ann.location(), "Unknown annotation " + ann.q() + "."));
          }
        } else if (pNamedFunc.body().isNone()) {
          logger.log(compileError(pNamedFunc, "Function body is missing."));
        }
      }

      @Override
      public void visitNamedValue(PNamedValue pNamedValue) {
        super.visitNamedValue(pNamedValue);
        if (pNamedValue.annotation().isSome()) {
          var ann = pNamedValue.annotation().get();
          var annName = ann.name();
          switch (annName) {
            case AnnotationNames.BYTECODE -> {
              if (pNamedValue.body().isSome()) {
                logger.log(compileError(
                    pNamedValue, "Value with @" + annName + " annotation cannot have body."));
              }
              if (pNamedValue.type() instanceof PImplicitType) {
                logger.log(compileError(
                    pNamedValue,
                    "Value " + pNamedValue.q() + " with @" + annName
                        + " annotation must declare type."));
              }
            }
            case AnnotationNames.NATIVE_PURE, AnnotationNames.NATIVE_IMPURE -> logger.log(
                compileError(
                    pNamedValue.annotation().get(),
                    "Value cannot have @" + annName + " annotation."));
            default -> logger.log(compileError(ann, "Unknown annotation " + ann.q() + "."));
          }
        } else if (pNamedValue.body().isNone()) {
          logger.log(compileError(pNamedValue, "Value cannot have empty body."));
        }
      }
    }.visitModule(pModule);
  }

  private static void detectStructFieldWithDefaultValue(PModule pModule, Logger logger) {
    new PModuleVisitor() {
      @Override
      public void visitStruct(PStruct pStruct) {
        super.visitStruct(pStruct);
        pStruct.fields().forEach(this::logErrorIfDefaultValuePresent);
      }

      private void logErrorIfDefaultValuePresent(PItem param) {
        if (param.defaultValue().isSome()) {
          logger.log(compileError(
              param.location(),
              "Struct field `" + param.name()
                  + "` has default value. Only function parameters can have default value."));
        }
      }
    }.visitModule(pModule);
  }

  private static void detectLambdaParamWithDefaultValue(PModule pModule, Logger logger) {
    new PModuleVisitor() {
      @Override
      public void visitLambda(PLambda pLambda) {
        super.visitLambda(pLambda);
        pLambda.params().forEach(this::logErrorIfDefaultValuePresent);
      }

      private void logErrorIfDefaultValuePresent(PItem param) {
        if (param.defaultValue().isSome()) {
          logger.log(compileError(
              param.location(),
              "Parameter " + param.q() + " of lambda cannot have default value."));
        }
      }
    }.visitModule(pModule);
  }
}
