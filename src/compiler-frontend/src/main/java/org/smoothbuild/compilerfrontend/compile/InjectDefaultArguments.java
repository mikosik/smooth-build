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
import org.smoothbuild.common.dag.TryFunction2;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.compilerfrontend.compile.ast.ModuleVisitorP;
import org.smoothbuild.compilerfrontend.compile.ast.ScopingModuleVisitorP;
import org.smoothbuild.compilerfrontend.compile.ast.define.CallP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ExprP;
import org.smoothbuild.compilerfrontend.compile.ast.define.InstantiateP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ItemP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ModuleP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedArgP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedFuncP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ReferenceP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ReferenceableP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ScopedP;
import org.smoothbuild.compilerfrontend.lang.base.TypeNamesS;
import org.smoothbuild.compilerfrontend.lang.define.ItemS;
import org.smoothbuild.compilerfrontend.lang.define.NamedEvaluableS;
import org.smoothbuild.compilerfrontend.lang.define.NamedFuncS;
import org.smoothbuild.compilerfrontend.lang.define.ScopeS;

public class InjectDefaultArguments implements TryFunction2<ModuleP, ScopeS, ModuleP> {
  @Override
  public Label label() {
    return Label.label(COMPILE_PREFIX, "injectDefaultArguments");
  }

  @Override
  public Try<ModuleP> apply(ModuleP moduleP, ScopeS environment) {
    var logger = new Logger();
    new Visitor(environment, immutableBindings(), logger).visitModule(moduleP);
    return Try.of(moduleP, logger);
  }

  private static class Visitor extends ScopingModuleVisitorP {
    private final ScopeS imported;
    private final Bindings<ReferenceableP> referenceables;
    private final Logger logger;

    public Visitor(ScopeS imported, Bindings<ReferenceableP> referenceables, Logger logger) {
      this.imported = imported;
      this.referenceables = referenceables;
      this.logger = logger;
    }

    @Override
    protected ModuleVisitorP createVisitorForScopeOf(ScopedP scopedP) {
      return new Visitor(imported, scopedP.scope().referencables(), logger);
    }

    @Override
    public void visitCall(CallP callP) {
      super.visitCall(callP);
      callP.setPositionedArgs(inferPositionedArgs(callP));
    }

    private List<ExprP> inferPositionedArgs(CallP callP) {
      if (callP.callee() instanceof InstantiateP instantiateP
          && instantiateP.polymorphic() instanceof ReferenceP referenceP) {
        var name = referenceP.referencedName();
        var optional = referenceables.getMaybe(name);
        if (optional.isSome()) {
          return inferPositionedArgs(callP, optional.get());
        } else {
          return inferPositionedArgs(callP, imported.evaluables().get(name));
        }
      } else {
        return inferPositionedArgs(callP, logger);
      }
    }

    private List<ExprP> inferPositionedArgs(CallP callP, ReferenceableP referenceableP) {
      if (referenceableP instanceof NamedFuncP namedFuncP) {
        var mappedParams = namedFuncP.params().list().map(Param::new);
        return inferPositionedArgs(callP, mappedParams, logger);
      } else {
        return inferPositionedArgs(callP, logger);
      }
    }

    private List<ExprP> inferPositionedArgs(CallP callP, NamedEvaluableS namedEvaluableS) {
      if (namedEvaluableS instanceof NamedFuncS namedFuncS) {
        var mappedParams = namedFuncS.params().list().map(Param::new);
        return inferPositionedArgs(callP, mappedParams, logger);
      } else {
        return inferPositionedArgs(callP, logger);
      }
    }

    private static List<ExprP> inferPositionedArgs(CallP callP, Logger logger) {
      var args = callP.args();
      for (var arg : args) {
        if (arg instanceof NamedArgP namedArgP) {
          logger.log(unknownParameterError(namedArgP));
        }
      }
      // We can return args even when errors above has been logged
      // as it will be ignored in such case.
      return args;
    }

    private static List<ExprP> inferPositionedArgs(
        CallP callP, List<Param> params, Logger mainLogger) {
      var logger = new Logger();
      var positionalArgs = leadingPositionalArgs(callP);
      logger.logAll(findPositionalArgAfterNamedArgError(callP));
      logger.logAll(findUnknownParamNameErrors(callP, params));
      logger.logAll(findDuplicateAssignmentErrors(callP, positionalArgs, params));
      mainLogger.logAll(logger);
      if (logger.containsFailure()) {
        return null;
      }
      return positionedArgs(callP, params, positionalArgs.size(), mainLogger);
    }

    private static List<ExprP> leadingPositionalArgs(CallP callP) {
      return callP.args().takeWhile(a -> !(a instanceof NamedArgP));
    }

    private static List<ExprP> positionedArgs(
        CallP callP, List<Param> params, int positionalArgsCount, Logger logBuffer) {
      var names = params.map(Param::name);
      var args = callP.args();
      // Case where positional args count exceeds function params count is reported as error
      // during call unification. Here we silently ignore it by creating list that is big enough
      // to hold all args.
      var size = max(positionalArgsCount, params.size());
      var result = new ArrayList<ExprP>(nCopies(size, null));
      for (int i = 0; i < args.size(); i++) {
        var arg = args.get(i);
        if (arg instanceof NamedArgP namedArgP) {
          result.set(names.indexOf(namedArgP.name()), namedArgP.expr());
        } else {
          result.set(i, arg);
        }
      }
      var error = false;
      for (int i = 0; i < result.size(); i++) {
        if (result.get(i) == null) {
          var param = params.get(i);
          if (param.hasDefaultValue()) {
            var name = TypeNamesS.fullName(nameOfReferencedCallee(callP), param.name());
            var location = callP.location();
            var element = new InstantiateP(new ReferenceP(name, location), location);
            result.set(i, element);
          } else {
            error = true;
            logBuffer.log(paramsMustBeSpecifiedError(callP, i, params));
          }
        }
      }
      return error ? null : listOfAll(result);
    }

    private static String nameOfReferencedCallee(CallP callP) {
      return ((ReferenceP) ((InstantiateP) callP.callee()).polymorphic()).referencedName();
    }

    private static List<Log> findPositionalArgAfterNamedArgError(CallP callP) {
      return callP
          .args()
          .dropWhile(a -> !(a instanceof NamedArgP))
          .dropWhile(a -> a instanceof NamedArgP)
          .map(Visitor::positionalArgumentsMustBePlacedBeforeNamedArguments);
    }

    private static List<Log> findUnknownParamNameErrors(CallP callP, List<Param> params) {
      var names = params.map(Param::name).toSet();
      return callP
          .args()
          .filter(a -> a instanceof NamedArgP)
          .map(a -> (NamedArgP) a)
          .filter(a -> !names.contains(a.name()))
          .map(Visitor::unknownParameterError);
    }

    private static List<Log> findDuplicateAssignmentErrors(
        CallP callP, List<ExprP> positionalArgs, List<Param> params) {
      var names = positionalArgNames(positionalArgs, params);
      return callP
          .args()
          .filter(a -> a instanceof NamedArgP)
          .map(a -> (NamedArgP) a)
          .filter(a -> !names.add(a.name()))
          .map(Visitor::paramIsAlreadyAssignedError);
    }

    private static Set<String> positionalArgNames(List<ExprP> positionalArgs, List<Param> params) {
      return params.stream().limit(positionalArgs.size()).map(Param::name).collect(toSet());
    }

    private static Log paramsMustBeSpecifiedError(CallP callP, int i, List<Param> params) {
      return compileError(callP, "Parameter " + q(params.get(i).name()) + " must be specified.");
    }

    private static Log unknownParameterError(NamedArgP namedArgP) {
      return compileError(namedArgP, "Unknown parameter " + namedArgP.q() + ".");
    }

    private static Log paramIsAlreadyAssignedError(NamedArgP namedArgP) {
      return compileError(namedArgP, namedArgP.q() + " is already assigned.");
    }

    private static Log positionalArgumentsMustBePlacedBeforeNamedArguments(ExprP argument) {
      return compileError(argument, "Positional arguments must be placed before named arguments.");
    }
  }

  private static record Param(String name, boolean hasDefaultValue) {
    public Param(ItemS param) {
      this(param.name(), param.defaultValue().isSome());
    }

    public Param(ItemP param) {
      this(param.name(), param.defaultValue().isSome());
    }
  }
}
