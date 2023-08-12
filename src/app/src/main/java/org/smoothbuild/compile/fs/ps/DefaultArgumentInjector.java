package org.smoothbuild.compile.fs.ps;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.Math.max;
import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.common.Strings.q;
import static org.smoothbuild.common.bindings.Bindings.immutableBindings;
import static org.smoothbuild.compile.fs.lang.base.TypeNamesS.fullName;
import static org.smoothbuild.compile.fs.ps.CompileError.compileError;
import static org.smoothbuild.out.log.Level.ERROR;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.smoothbuild.common.bindings.Bindings;
import org.smoothbuild.common.collect.Lists;
import org.smoothbuild.common.collect.Sets;
import org.smoothbuild.compile.fs.lang.define.ItemS;
import org.smoothbuild.compile.fs.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.fs.lang.define.NamedFuncS;
import org.smoothbuild.compile.fs.lang.define.ScopeS;
import org.smoothbuild.compile.fs.ps.ast.ModuleVisitorP;
import org.smoothbuild.compile.fs.ps.ast.ScopingModuleVisitorP;
import org.smoothbuild.compile.fs.ps.ast.define.CallP;
import org.smoothbuild.compile.fs.ps.ast.define.ExprP;
import org.smoothbuild.compile.fs.ps.ast.define.InstantiateP;
import org.smoothbuild.compile.fs.ps.ast.define.ItemP;
import org.smoothbuild.compile.fs.ps.ast.define.ModuleP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedArgP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedFuncP;
import org.smoothbuild.compile.fs.ps.ast.define.ReferenceP;
import org.smoothbuild.compile.fs.ps.ast.define.ReferenceableP;
import org.smoothbuild.compile.fs.ps.ast.define.ScopedP;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.out.log.Logs;

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
    private final Bindings<ReferenceableP> referenceables;
    private final LogBuffer logger;

    public Visitor(ScopeS imported, Bindings<ReferenceableP> referenceables, LogBuffer logger) {
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

    private ImmutableList<ExprP> inferPositionedArgs(CallP callP) {
      if (callP.callee() instanceof InstantiateP instantiateP
          && instantiateP.polymorphic() instanceof ReferenceP referenceP) {
        var name = referenceP.name();
        var optional = referenceables.getOptional(name);
        if (optional.isPresent()) {
          return inferPositionedArgs(callP, optional.get());
        } else {
          return inferPositionedArgs(callP, imported.evaluables().get(name));
        }
      } else {
       return inferPositionedArgs(callP, logger);
      }
    }

    private ImmutableList<ExprP> inferPositionedArgs(CallP callP, ReferenceableP referenceableP) {
      if (referenceableP instanceof NamedFuncP namedFuncP) {
        var mappedParams = Lists.map(namedFuncP.params(), Param::new);
        return inferPositionedArgs(callP, mappedParams, logger);
      } else {
        return inferPositionedArgs(callP, logger);
      }
    }

    private ImmutableList<ExprP> inferPositionedArgs(CallP callP, NamedEvaluableS namedEvaluableS) {
      if (namedEvaluableS instanceof NamedFuncS namedFuncS) {
        var mappedParams = Lists.map(namedFuncS.params(), Param::new);
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
        CallP callP, List<Param> params, Logger logger) {
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
        List<Param> params, int positionalArgsCount, Logger logBuffer) {
      var names = Lists.map(params, Param::name);
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
            var name = fullName(nameOfReferencedCallee(callP), param.name());
            var location = callP.location();
            var element = new InstantiateP(new ReferenceP(name, location), location);
            result.set(i, element);
          } else {
            error = true;
            logBuffer.log(paramsMustBeSpecifiedError(callP, i, params));
          }
        }
      }
      return error ? null : ImmutableList.copyOf(result);
    }

    private static String nameOfReferencedCallee(CallP callP) {
      return ((ReferenceP) ((InstantiateP) callP.callee()).polymorphic()).name();
    }

    private static List<Log> findPositionalArgAfterNamedArgError(CallP callP) {
      return callP.args()
          .stream()
          .dropWhile(a -> !(a instanceof NamedArgP))
          .dropWhile(a -> a instanceof NamedArgP)
          .map(Visitor::positionalArgumentsMustBePlacedBeforeNamedArguments)
          .toList();
    }

    private static List<Log> findUnknownParamNameErrors(CallP callP, List<Param> params) {
      var names = Sets.map(params, Param::name);
      return callP.args()
          .stream()
          .filter(a -> a instanceof NamedArgP)
          .map(a -> (NamedArgP) a)
          .filter(a -> !names.contains(a.name()))
          .map(Visitor::unknownParameterError)
          .toList();
    }

    private static List<Log> findDuplicateAssignmentErrors(
        CallP callP, List<ExprP> positionalArgs, List<Param> params) {
      var names = positionalArgNames(positionalArgs, params);
      return callP.args()
          .stream()
          .filter(a -> a instanceof NamedArgP)
          .map(a -> (NamedArgP) a)
          .filter(a -> !names.add(a.name()))
          .map(Visitor::paramIsAlreadyAssignedError)
          .toList();
    }

    private static Set<String> positionalArgNames(List<ExprP> positionalArgs, List<Param> params) {
      return params.stream()
          .limit(positionalArgs.size())
          .map(Param::name)
          .collect(toSet());
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
      this(param.name(), param.defaultValue().isPresent());
    }

    public Param(ItemP param) {
      this(param.name(), param.defaultValue().isPresent());
    }
  }
}
