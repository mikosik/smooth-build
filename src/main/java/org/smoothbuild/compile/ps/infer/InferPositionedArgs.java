package org.smoothbuild.compile.ps.infer;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.Math.max;
import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.compile.ps.CompileError.compileError;
import static org.smoothbuild.out.log.Level.ERROR;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.smoothbuild.compile.lang.define.ItemS;
import org.smoothbuild.compile.ps.ast.expr.CallP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.util.collect.Named;

import com.google.common.collect.ImmutableList;

public class InferPositionedArgs {
  public static void inferPositionedArgs(CallP callP, Logger logger) {
    var args = callP.args();
    var result = Optional.of(args);
    for (var arg : args) {
      if (arg instanceof NamedArgP namedArgP) {
        logger.log(unknownParameterError(namedArgP));
        result = Optional.empty();
      }
    }
    callP.setPositionedArgs(result);
  }

  public static void inferPositionedArgs(
      CallP callP, NList<ItemS> params, Logger logger) {
    NList<Param> mappedParams = params.map(Param::new);
    callP.setPositionedArgs(inferPositionedArgsImpl(callP, mappedParams, logger));
  }

  private static Optional<ImmutableList<ExprP>> inferPositionedArgsImpl(
      CallP callP, NList<Param> params, Logger logger) {
    var logBuffer = new LogBuffer();
    var positionalArgs = leadingPositionalArgs(callP);
    logBuffer.logAll(findPositionalArgAfterNamedArgError(callP));
    logBuffer.logAll(findUnknownParamNameErrors(callP, params));
    logBuffer.logAll(findDuplicateAssignmentErrors(callP, positionalArgs, params));
    logger.logAll(logBuffer);
    if (logBuffer.containsAtLeast(ERROR)) {
      return Optional.empty();
    }
    return positionedArgs(callP, params, positionalArgs.size(), logger);
  }

  private static ImmutableList<ExprP> leadingPositionalArgs(CallP callP) {
    return callP.args()
        .stream()
        .takeWhile(a -> !(a instanceof NamedArgP))
        .collect(toImmutableList());
  }

  private static Optional<ImmutableList<ExprP>> positionedArgs(CallP callP,
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
        result.set(params.indexOf(namedArgP.name()), namedArgP);
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
    return error ? Optional.empty() : Optional.of(ImmutableList.copyOf(result));
  }

  private static List<Log> findPositionalArgAfterNamedArgError(CallP callP) {
    return callP.args()
        .stream()
        .dropWhile(a -> !(a instanceof NamedArgP))
        .dropWhile(a -> a instanceof NamedArgP)
        .map(InferPositionedArgs::positionalArgumentsMustBePlacedBeforeNamedArguments)
        .collect(toList());
  }

  private static List<Log> findUnknownParamNameErrors(CallP callP, NList<Param> params) {
    return callP.args()
        .stream()
        .filter(a -> a instanceof NamedArgP)
        .map(a -> (NamedArgP) a)
        .filter(a -> params.isEmpty() || !params.containsName(a.name()))
        .map(InferPositionedArgs::unknownParameterError)
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
        .map(InferPositionedArgs::paramIsAlreadyAssignedError)
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

  private static record Param(String name, boolean hasDefaultValue) implements Named {
    public Param(ItemS param) {
      this(param.name(), param.defaultValue().isPresent());
    }
  }
}
