package org.smoothbuild.compilerfrontend.compile;

import static java.lang.Math.max;
import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.common.base.Strings.q;
import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILE_PREFIX;
import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;

import java.util.ArrayList;
import java.util.Set;
import org.smoothbuild.common.bindings.Bindings;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.plan.TryFunction2;
import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.PScopingModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PCall;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExpr;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInstantiate;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedArg;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReference;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReferenceable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PScoped;
import org.smoothbuild.compilerfrontend.lang.base.STypeNames;
import org.smoothbuild.compilerfrontend.lang.define.SItem;
import org.smoothbuild.compilerfrontend.lang.define.SNamedEvaluable;
import org.smoothbuild.compilerfrontend.lang.define.SNamedFunc;
import org.smoothbuild.compilerfrontend.lang.define.SScope;

public class InjectDefaultArguments implements TryFunction2<PModule, SScope, PModule> {
  @Override
  public Label label() {
    return Label.label(COMPILE_PREFIX, "injectDefaultArguments");
  }

  @Override
  public Try<PModule> apply(PModule pModule, SScope environment) {
    var logger = new Logger();
    new Visitor(environment, immutableBindings(), logger).visitModule(pModule);
    return Try.of(pModule, logger);
  }

  private static class Visitor extends PScopingModuleVisitor {
    private final SScope imported;
    private final Bindings<PReferenceable> referenceables;
    private final Logger logger;

    public Visitor(SScope imported, Bindings<PReferenceable> referenceables, Logger logger) {
      this.imported = imported;
      this.referenceables = referenceables;
      this.logger = logger;
    }

    @Override
    protected PModuleVisitor createVisitorForScopeOf(PScoped pScoped) {
      return new Visitor(imported, pScoped.scope().referencables(), logger);
    }

    @Override
    public void visitCall(PCall pCall) {
      super.visitCall(pCall);
      pCall.setPositionedArgs(inferPositionedArgs(pCall));
    }

    private List<PExpr> inferPositionedArgs(PCall pCall) {
      if (pCall.callee() instanceof PInstantiate pInstantiate
          && pInstantiate.polymorphic() instanceof PReference pReference) {
        var name = pReference.referencedName();
        var optional = referenceables.getMaybe(name);
        if (optional.isSome()) {
          return inferPositionedArgs(pCall, optional.get());
        } else {
          return inferPositionedArgs(pCall, imported.evaluables().get(name));
        }
      } else {
        return inferPositionedArgs(pCall, logger);
      }
    }

    private List<PExpr> inferPositionedArgs(PCall pCall, PReferenceable pReferenceable) {
      if (pReferenceable instanceof PNamedFunc pNamedFunc) {
        var mappedParams = pNamedFunc.params().list().map(Param::new);
        return inferPositionedArgs(pCall, mappedParams, logger);
      } else {
        return inferPositionedArgs(pCall, logger);
      }
    }

    private List<PExpr> inferPositionedArgs(PCall pCall, SNamedEvaluable sNamedEvaluable) {
      if (sNamedEvaluable instanceof SNamedFunc sNamedFunc) {
        var mappedParams = sNamedFunc.params().list().map(Param::new);
        return inferPositionedArgs(pCall, mappedParams, logger);
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

    private static List<PExpr> inferPositionedArgs(
        PCall pCall, List<Param> params, Logger mainLogger) {
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

    private static List<PExpr> positionedArgs(
        PCall pCall, List<Param> params, int positionalArgsCount, Logger logBuffer) {
      var names = params.map(Param::name);
      var args = pCall.args();
      // Case where positional args count exceeds function params count is reported as error
      // during call unification. Here we silently ignore it by creating list that is big enough
      // to hold all args.
      var size = max(positionalArgsCount, params.size());
      var result = new ArrayList<PExpr>(nCopies(size, null));
      for (int i = 0; i < args.size(); i++) {
        var arg = args.get(i);
        if (arg instanceof PNamedArg pNamedArg) {
          result.set(names.indexOf(pNamedArg.name()), pNamedArg.expr());
        } else {
          result.set(i, arg);
        }
      }
      var error = false;
      for (int i = 0; i < result.size(); i++) {
        if (result.get(i) == null) {
          var param = params.get(i);
          if (param.hasDefaultValue()) {
            var name = STypeNames.fullName(nameOfReferencedCallee(pCall), param.name());
            var location = pCall.location();
            var element = new PInstantiate(new PReference(name, location), location);
            result.set(i, element);
          } else {
            error = true;
            logBuffer.log(paramsMustBeSpecifiedError(pCall, i, params));
          }
        }
      }
      return error ? null : listOfAll(result);
    }

    private static String nameOfReferencedCallee(PCall pCall) {
      return ((PReference) ((PInstantiate) pCall.callee()).polymorphic()).referencedName();
    }

    private static List<Log> findPositionalArgAfterNamedArgError(PCall pCall) {
      return pCall
          .args()
          .dropWhile(a -> !(a instanceof PNamedArg))
          .dropWhile(a -> a instanceof PNamedArg)
          .map(Visitor::positionalArgumentsMustBePlacedBeforeNamedArguments);
    }

    private static List<Log> findUnknownParamNameErrors(PCall pCall, List<Param> params) {
      var names = params.map(Param::name).toSet();
      return pCall
          .args()
          .filter(a -> a instanceof PNamedArg)
          .map(a -> (PNamedArg) a)
          .filter(a -> !names.contains(a.name()))
          .map(Visitor::unknownParameterError);
    }

    private static List<Log> findDuplicateAssignmentErrors(
        PCall pCall, List<PExpr> positionalArgs, List<Param> params) {
      var names = positionalArgNames(positionalArgs, params);
      return pCall
          .args()
          .filter(a -> a instanceof PNamedArg)
          .map(a -> (PNamedArg) a)
          .filter(a -> !names.add(a.name()))
          .map(Visitor::paramIsAlreadyAssignedError);
    }

    private static Set<String> positionalArgNames(List<PExpr> positionalArgs, List<Param> params) {
      return params.stream().limit(positionalArgs.size()).map(Param::name).collect(toSet());
    }

    private static Log paramsMustBeSpecifiedError(PCall pCall, int i, List<Param> params) {
      return compileError(pCall, "Parameter " + q(params.get(i).name()) + " must be specified.");
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

  private static record Param(String name, boolean hasDefaultValue) {
    public Param(SItem param) {
      this(param.name(), param.defaultValue().isSome());
    }

    public Param(PItem param) {
      this(param.name(), param.defaultValue().isSome());
    }
  }
}
