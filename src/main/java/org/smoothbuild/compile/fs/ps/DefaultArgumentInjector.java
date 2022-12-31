package org.smoothbuild.compile.fs.ps;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.Math.max;
import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.compile.fs.ps.CompileError.compileError;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.util.bindings.Bindings.immutableBindings;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.smoothbuild.compile.fs.lang.define.ItemS;
import org.smoothbuild.compile.fs.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.fs.lang.define.NamedFuncS;
import org.smoothbuild.compile.fs.lang.define.ScopeS;
import org.smoothbuild.compile.fs.ps.ast.ModuleVisitorP;
import org.smoothbuild.compile.fs.ps.ast.ScopingModuleVisitorP;
import org.smoothbuild.compile.fs.ps.ast.expr.CallP;
import org.smoothbuild.compile.fs.ps.ast.expr.ExprP;
import org.smoothbuild.compile.fs.ps.ast.expr.ItemP;
import org.smoothbuild.compile.fs.ps.ast.expr.ModuleP;
import org.smoothbuild.compile.fs.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.fs.ps.ast.expr.NamedFuncP;
import org.smoothbuild.compile.fs.ps.ast.expr.RefP;
import org.smoothbuild.compile.fs.ps.ast.expr.RefableP;
import org.smoothbuild.compile.fs.ps.ast.expr.WithScopeP;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.out.log.Logs;
import org.smoothbuild.util.bindings.Bindings;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.util.collect.Named;

import com.google.common.collect.ImmutableList;

public class DefaultArgumentInjector {
  public static Logs injectDefaultArguments(ModuleP moduleP, ScopeS imported) {
    var logger = new LogBuffer();
    new Visitor(imported, immutableBindings(), logger)
        .visitModule(moduleP);
    return logger;
  }

  private static class Visitor extends ScopingModuleVisitorP {
    private final ScopeS imported;
    private final Bindings<RefableP> refables;
    private final LogBuffer logger;

    public Visitor(ScopeS imported, Bindings<RefableP> refables, LogBuffer logger) {
      this.imported = imported;
      this.refables = refables;
      this.logger = logger;
    }

    @Override
    protected ModuleVisitorP createVisitorForScopeOf(WithScopeP withScopeP) {
      return new Visitor(imported, withScopeP.scope().refables(), logger);
    }

    @Override
    public void visitCall(CallP callP) {
      super.visitCall(callP);
      callP.setPositionedArgs(inferPositionedArgs(callP));
    }

    private ImmutableList<ExprP> inferPositionedArgs(CallP callP) {
      if (callP.callee() instanceof RefP refP) {
        var name = refP.name();
        var optional = refables.getOptional(name);
        if (optional.isPresent()) {
          return inferPositionedArgs(callP, optional.get());
        } else {
          return inferPositionedArgs(callP, imported.evaluables().get(name));
        }
      } else {
       return inferPositionedArgs(callP, logger);
      }
    }

    private ImmutableList<ExprP> inferPositionedArgs(CallP callP, RefableP refableP) {
      if (refableP instanceof NamedFuncP namedFuncP) {
        var mappedParams = namedFuncP.params().map(Param::new);
        return inferPositionedArgs(callP, mappedParams, logger);
      } else {
        return inferPositionedArgs(callP, logger);
      }
    }

    private ImmutableList<ExprP> inferPositionedArgs(CallP callP, NamedEvaluableS namedEvaluableS) {
      if (namedEvaluableS instanceof NamedFuncS namedFuncS) {
        var mappedParams = namedFuncS.params().map(Param::new);
        return inferPositionedArgs(callP, mappedParams, logger);
      } else {
        return inferPositionedArgs(callP, logger);
      }
    }

    private static ImmutableList<ExprP> inferPositionedArgs(CallP callP, Logger logger) {
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

    private static ImmutableList<ExprP> inferPositionedArgs(
        CallP callP, NList<Param> params, Logger logger) {
      var logBuffer = new LogBuffer();
      var positionalArgs = leadingPositionalArgs(callP);
      logBuffer.logAll(findPositionalArgAfterNamedArgError(callP));
      logBuffer.logAll(findUnknownParamNameErrors(callP, params));
      logBuffer.logAll(findDuplicateAssignmentErrors(callP, positionalArgs, params));
      logger.logAll(logBuffer);
      if (logBuffer.containsAtLeast(ERROR)) {
        return null;
      }
      return positionedArgs(callP, params, positionalArgs.size(), logger);
    }

    private static ImmutableList<ExprP> leadingPositionalArgs(CallP callP) {
      return callP.args()
          .stream()
          .takeWhile(a -> !(a instanceof NamedArgP))
          .collect(toImmutableList());
    }

    private static ImmutableList<ExprP> positionedArgs(CallP callP,
        NList<Param> params, int positionalArgsCount, Logger logBuffer) {
      var args = callP.args();
      // Case where positional args count exceeds function params count is reported as error
      // during call unification. Here we silently ignore it by creating list that is big enough
      // to hold all args.
      var size = max(positionalArgsCount, params.size());
      var result = new ArrayList<ExprP>(nCopies(size, null));
      for (int i = 0; i < args.size(); i++) {
        var arg = args.get(i);
        if (arg instanceof NamedArgP namedArgP) {
          result.set(params.indexOf(namedArgP.name()), namedArgP.expr());
        } else {
          result.set(i, arg);
        }
      }
      var error = false;
      for (int i = 0; i < result.size(); i++) {
        if (result.get(i) == null) {
          var param = params.get(i);
          if (param.hasDefaultValue()) {
            var name = ((RefP) callP.callee()).name() + ":" + param.name();
            var element = new RefP(name, callP.location());
            result.set(i, element);
          } else {
            error = true;
            logBuffer.log(paramsMustBeSpecifiedError(callP, i, params));
          }
        }
      }
      return error ? null : ImmutableList.copyOf(result);
    }

    private static List<Log> findPositionalArgAfterNamedArgError(CallP callP) {
      return callP.args()
          .stream()
          .dropWhile(a -> !(a instanceof NamedArgP))
          .dropWhile(a -> a instanceof NamedArgP)
          .map(Visitor::positionalArgumentsMustBePlacedBeforeNamedArguments)
          .collect(toList());
    }

    private static List<Log> findUnknownParamNameErrors(CallP callP, NList<Param> params) {
      return callP.args()
          .stream()
          .filter(a -> a instanceof NamedArgP)
          .map(a -> (NamedArgP) a)
          .filter(a -> params.isEmpty() || !params.containsName(a.name()))
          .map(Visitor::unknownParameterError)
          .collect(toList());
    }

    private static List<Log> findDuplicateAssignmentErrors(
        CallP callP, List<ExprP> positionalArgs, NList<Param> params) {
      var names = positionalArgNames(positionalArgs, params);
      return callP.args()
          .stream()
          .filter(a -> a instanceof NamedArgP)
          .map(a -> (NamedArgP) a)
          .filter(a -> !names.add(a.name()))
          .map(Visitor::paramIsAlreadyAssignedError)
          .collect(toList());
    }

    private static Set<String> positionalArgNames(List<ExprP> positionalArgs, NList<Param> params) {
      return params.stream()
          .limit(positionalArgs.size())
          .flatMap(p -> p.nameO().stream())
          .collect(toSet());
    }

    private static Log paramsMustBeSpecifiedError(CallP callP, int i, NList<Param> params) {
      var paramName = params.get(i).nameO()
          .map(n -> "`" + n + "`")
          .orElse("#" + (i + 1));
      return compileError(callP, "Parameter " + paramName + " must be specified.");
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

  private static record Param(String name, boolean hasDefaultValue) implements Named {
    public Param(ItemS param) {
      this(param.name(), param.defaultValue().isPresent());
    }

    public Param(ItemP param) {
      this(param.name(), param.defaultValue().isPresent());
    }
  }
}
