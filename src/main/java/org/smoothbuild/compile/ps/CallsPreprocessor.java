package org.smoothbuild.compile.ps;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.Math.max;
import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.compile.ps.CompileError.compileError;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.util.bindings.ImmutableBindings.immutableBindings;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Maps.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.smoothbuild.compile.lang.define.DefinitionsS;
import org.smoothbuild.compile.lang.define.ItemS;
import org.smoothbuild.compile.lang.define.NamedEvaluableS;
import org.smoothbuild.compile.lang.define.NamedFuncS;
import org.smoothbuild.compile.ps.ast.ModuleVisitorP;
import org.smoothbuild.compile.ps.ast.expr.AnonymousFuncP;
import org.smoothbuild.compile.ps.ast.expr.CallP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.FuncP;
import org.smoothbuild.compile.ps.ast.expr.ItemP;
import org.smoothbuild.compile.ps.ast.expr.ModuleP;
import org.smoothbuild.compile.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.ps.ast.expr.NamedFuncP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.RefableP;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.out.log.Logs;
import org.smoothbuild.util.bindings.Bindings;
import org.smoothbuild.util.bindings.ImmutableBindings;
import org.smoothbuild.util.bindings.MutableBindings;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.util.collect.Named;

import com.google.common.collect.ImmutableList;

public class CallsPreprocessor {
  public static Logs preprocessCalls(ModuleP moduleP, DefinitionsS imported) {
    var logger = new LogBuffer();
    var localBindings = localBindings(moduleP);
    new Preprocessor(imported, localBindings, logger)
        .visitAst(moduleP);
    return logger;
  }

  private static ImmutableBindings<RefableP> localBindings(ModuleP moduleP) {
    var constructors = map(moduleP.structs(), s -> (RefableP) s.constructor());
    var localRefables = concat(moduleP.evaluables(), constructors);
    return immutableBindings(toMap(localRefables, Named::name, e -> e));
  }

  private static class Preprocessor extends ModuleVisitorP {
    private final DefinitionsS imported;
    private final Bindings<RefableP> localBindings;
    private final LogBuffer logger;

    public Preprocessor(DefinitionsS imported, Bindings<RefableP> localBindings, LogBuffer logger) {
      this.localBindings = localBindings;
      this.imported = imported;
      this.logger = logger;
    }

    @Override
    public void visitCall(CallP callP) {
      super.visitCall(callP);
      callP.setPositionedArgs(inferPositionedArgs(callP));
    }

    @Override
    public void visitFuncBody(FuncP funcP, ExprP body) {
      var funcBodyBindings = new MutableBindings<>(localBindings);
      funcP.params().forEach(p -> funcBodyBindings.add(p.name(), p));
      new Preprocessor(imported, funcBodyBindings, logger)
          .visitExpr(body);
    }

    @Override
    public void visitAnonymousFunc(AnonymousFuncP anonymousFuncP) {
      super.visitAnonymousFunc(anonymousFuncP);
    }

    private ImmutableList<ExprP> inferPositionedArgs(CallP callP) {
      if (callP.callee() instanceof RefP refP) {
        var name = refP.name();
        var optional = localBindings.getOptional(name);
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


    public static ImmutableList<ExprP> inferPositionedArgs(CallP callP, Logger logger) {
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
          .map(Preprocessor::positionalArgumentsMustBePlacedBeforeNamedArguments)
          .collect(toList());
    }

    private static List<Log> findUnknownParamNameErrors(CallP callP, NList<Param> params) {
      return callP.args()
          .stream()
          .filter(a -> a instanceof NamedArgP)
          .map(a -> (NamedArgP) a)
          .filter(a -> params.isEmpty() || !params.containsName(a.name()))
          .map(Preprocessor::unknownParameterError)
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
          .map(Preprocessor::paramIsAlreadyAssignedError)
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
