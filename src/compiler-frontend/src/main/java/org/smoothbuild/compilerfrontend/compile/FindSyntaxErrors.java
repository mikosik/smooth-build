package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.lang.base.TokenNames.detectIdentifierNameErrors;
import static org.smoothbuild.compilerfrontend.lang.base.TokenNames.detectStructNameErrors;

import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PImplicitType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReferenceable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.lang.type.AnnotationNames;

/**
 * Detect syntax errors that are not caught by Antlr.
 * We could extend Antlr grammar to catch those,
 * but it would make it more complex.
 * Catching those errors here makes it easier
 * to provide more detailed error message.
 */
public class FindSyntaxErrors implements Task1<PModule, PModule> {
  @Override
  public Output<PModule> execute(PModule pModule) {
    var logger = new Logger();
    detectIllegalNames(pModule, logger);
    detectIllegalAnnotations(pModule, logger);
    detectStructFieldWithDefaultValue(pModule, logger);
    detectLambdaParamWithDefaultValue(pModule, logger);
    var label = COMPILER_FRONT_LABEL.append(":findSyntaxErrors");
    return output(pModule, label, logger.toList());
  }

  private static void detectIllegalNames(PModule pModule, Logger logger) {
    new PModuleVisitor<RuntimeException>() {
      @Override
      public void visitNameOf(PReferenceable pReferenceable) {
        var name = pReferenceable.shortName();
        detectIdentifierNameErrors(name).ifPresent(error -> {
          var message = "`" + name + "` is illegal identifier name. " + error;
          logger.log(compileError(pReferenceable.location(), message));
        });
      }

      @Override
      public void visitStruct(PStruct pStruct) {
        super.visitStruct(pStruct);
        var name = pStruct.name();
        detectStructNameErrors(name).ifPresent(error -> {
          var message = "`" + name + "` is illegal struct name. " + error;
          logger.log(compileError(pStruct.location(), message));
        });
      }
    }.visitModule(pModule);
  }

  private static void detectIllegalAnnotations(PModule pModule, Logger logger) {
    new PModuleVisitor<RuntimeException>() {
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
    new PModuleVisitor<RuntimeException>() {
      @Override
      public void visitStruct(PStruct pStruct) {
        super.visitStruct(pStruct);
        pStruct.fields().forEach(this::logErrorIfDefaultValuePresent);
      }

      private void logErrorIfDefaultValuePresent(PItem param) {
        if (param.defaultValueFullName().isSome()) {
          logger.log(compileError(
              param.location(),
              "Struct field `" + param.name()
                  + "` has default value. Only function parameters can have default value."));
        }
      }
    }.visitModule(pModule);
  }

  private static void detectLambdaParamWithDefaultValue(PModule pModule, Logger logger) {
    new PModuleVisitor<RuntimeException>() {
      @Override
      public void visitLambda(PLambda pLambda) {
        super.visitLambda(pLambda);
        pLambda.params().forEach(this::logErrorIfDefaultValuePresent);
      }

      private void logErrorIfDefaultValuePresent(PItem param) {
        if (param.defaultValueFullName().isSome()) {
          logger.log(compileError(
              param.location(),
              "Parameter " + param.q() + " of lambda cannot have default value."));
        }
      }
    }.visitModule(pModule);
  }
}
