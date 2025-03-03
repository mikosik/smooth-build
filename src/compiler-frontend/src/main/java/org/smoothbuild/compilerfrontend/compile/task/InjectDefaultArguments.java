package org.smoothbuild.compilerfrontend.compile.task;

import static java.lang.Math.max;
import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.compile.task.CompileError.compileError;
import static org.smoothbuild.compilerfrontend.lang.name.Name.referenceableName;

import java.util.ArrayList;
import java.util.Set;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.compilerfrontend.compile.ast.PScopingModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PCall;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExpr;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInstantiate;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedArg;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReference;
import org.smoothbuild.compilerfrontend.lang.base.Item;
import org.smoothbuild.compilerfrontend.lang.base.NamedFunc;
import org.smoothbuild.compilerfrontend.lang.base.Referenceable;
import org.smoothbuild.compilerfrontend.lang.define.SPolyEvaluable;
import org.smoothbuild.compilerfrontend.lang.name.NList;
import org.smoothbuild.compilerfrontend.lang.name.Name;

public class InjectDefaultArguments implements Task1<PModule, PModule> {
  @Override
  public Output<PModule> execute(PModule pModule) {
    var visitor = new Visitor();
    visitor.visit(pModule);
    var label = COMPILER_FRONT_LABEL.append(":injectDefaultArguments");
    return output(pModule, label, visitor.logger.toList());
  }

  private static class Visitor extends PScopingModuleVisitor<RuntimeException> {
    private final Logger logger = new Logger();

    @Override
    public void visitCall(PCall pCall) {
      super.visitCall(pCall);
      pCall.setPositionedArgs(inferPositionedArgs(pCall));
    }

    private List<PExpr> inferPositionedArgs(PCall pCall) {
      if (pCall.callee() instanceof PInstantiate pInstantiate
          && pInstantiate.reference() instanceof PReference pReference) {
        return inferPositionedArgs(pCall, pReference.referenced());
      } else {
        return inferPositionedArgs(pCall, logger);
      }
    }

    private List<PExpr> inferPositionedArgs(PCall pCall, Referenceable referenceable) {
      if (referenceable instanceof NamedFunc namedFunc) {
        return inferPositionedArgs(pCall, namedFunc.params(), logger);
      } else if (referenceable instanceof SPolyEvaluable sPolyEvaluable
          && sPolyEvaluable.evaluable() instanceof NamedFunc namedFunc) {
        return inferPositionedArgs(pCall, namedFunc.params(), logger);
      } else {
        return inferPositionedArgs(pCall, logger);
      }
    }

    private static List<PExpr> inferPositionedArgs(PCall pCall, Logger logger) {
      var args = pCall.args();
      for (var arg : args) {
        if (arg instanceof PNamedArg pNamedArg) {
          logger.log(unknownParameterError(pNamedArg));
        }
      }
      // We can return args even when errors above has been logged
      // as it will be ignored in such case.
      return args;
    }

    private List<PExpr> inferPositionedArgs(
        PCall pCall, NList<? extends Item> params, Logger mainLogger) {
      var logger = new Logger();
      var positionalArgs = leadingPositionalArgs(pCall);
      logger.logAll(findPositionalArgAfterNamedArgError(pCall));
      logger.logAll(findUnknownParamNameErrors(pCall, params));
      logger.logAll(findDuplicateAssignmentErrors(pCall, positionalArgs, params));
      mainLogger.logAll(logger);
      if (logger.containsFailure()) {
        return null;
      }
      return positionedArgs(pCall, params, positionalArgs.size(), mainLogger);
    }

    private static List<PExpr> leadingPositionalArgs(PCall pCall) {
      return pCall.args().takeWhile(a -> !(a instanceof PNamedArg));
    }

    private List<PExpr> positionedArgs(
        PCall pCall, NList<? extends Item> params, int positionalArgsCount, Logger logBuffer) {
      var names = params.list().map(Item::name);
      var args = pCall.args();
      // Case where positional args count exceeds function params count is reported as error
      // during call unification. Here we silently ignore it by creating list that is big enough
      // to hold all args.
      var size = max(positionalArgsCount, params.size());
      var result = new ArrayList<PExpr>(nCopies(size, null));
      for (int i = 0; i < args.size(); i++) {
        var arg = args.get(i);
        if (arg instanceof PNamedArg pNamedArg) {
          result.set(names.indexOf(referenceableName(pNamedArg.nameText())), pNamedArg.expr());
        } else {
          result.set(i, arg);
        }
      }
      var error = false;
      for (int i = 0; i < result.size(); i++) {
        if (result.get(i) == null) {
          var param = params.get(i);
          var defaultValue = param.defaultValue();
          if (defaultValue.isSome()) {
            var fqn = defaultValue.get().fqn();
            var location = pCall.location();
            var pReference = new PReference(fqn.toString(), location);
            pReference.setFqn(fqn);
            pReference.setReferenced(scope().referenceables().find(fqn).ok());
            var element = new PInstantiate(pReference, location);
            result.set(i, element);
          } else {
            error = true;
            logBuffer.log(paramsMustBeSpecifiedError(pCall, i, params));
          }
        }
      }
      return error ? null : listOfAll(result);
    }

    private static List<Log> findPositionalArgAfterNamedArgError(PCall pCall) {
      return pCall
          .args()
          .dropWhile(a -> !(a instanceof PNamedArg))
          .dropWhile(a -> a instanceof PNamedArg)
          .map(Visitor::positionalArgumentsMustBePlacedBeforeNamedArguments);
    }

    private static List<Log> findUnknownParamNameErrors(PCall pCall, NList<? extends Item> params) {
      var names = params.list().map(Item::name).toSet();
      return pCall
          .args()
          .filter(a -> a instanceof PNamedArg)
          .map(a -> (PNamedArg) a)
          .filter(a -> !names.contains(a.name()))
          .map(Visitor::unknownParameterError);
    }

    private static List<Log> findDuplicateAssignmentErrors(
        PCall pCall, List<PExpr> positionalArgs, NList<? extends Item> params) {
      var names = positionalArgNames(positionalArgs, params);
      return pCall
          .args()
          .filter(a -> a instanceof PNamedArg)
          .map(a -> (PNamedArg) a)
          .filter(a -> !names.add(a.name()))
          .map(Visitor::paramIsAlreadyAssignedError);
    }

    private static Set<Name> positionalArgNames(
        List<PExpr> positionalArgs, NList<? extends Item> params) {
      return params.stream().limit(positionalArgs.size()).map(Item::name).collect(toSet());
    }

    private static Log paramsMustBeSpecifiedError(
        PCall pCall, int i, NList<? extends Item> params) {
      return compileError(pCall, "Parameter " + params.get(i).name().q() + " must be specified.");
    }

    private static Log unknownParameterError(PNamedArg pNamedArg) {
      return compileError(pNamedArg, "Unknown parameter " + pNamedArg.q() + ".");
    }

    private static Log paramIsAlreadyAssignedError(PNamedArg pNamedArg) {
      return compileError(pNamedArg, pNamedArg.q() + " is already assigned.");
    }

    private static Log positionalArgumentsMustBePlacedBeforeNamedArguments(PExpr argument) {
      return compileError(argument, "Positional arguments must be placed before named arguments.");
    }
  }
}
