package org.smoothbuild.compile.ps.infer;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.Math.max;
import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.compile.ps.CompileError.compileError;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.smoothbuild.compile.lang.define.ItemS;
import org.smoothbuild.compile.ps.ast.expr.CallP;
import org.smoothbuild.compile.ps.ast.expr.DefaultArgP;
import org.smoothbuild.compile.ps.ast.expr.ExprP;
import org.smoothbuild.compile.ps.ast.expr.NamedArgP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class InferPositionedArgs {
  public static Optional<ImmutableList<ExprP>> inferPositionedArgs(CallP call,
      RefP refP, Optional<NList<ItemS>> params, Logger logger) {
    var logBuffer = new LogBuffer();
    var positionalArgs = leadingPositionalArgs(call);
    logBuffer.logAll(findPositionalArgAfterNamedArgError(call));
    logBuffer.logAll(findUnknownParamNameErrors(call, params));
    logBuffer.logAll(findDuplicateAssignmentErrors(call, positionalArgs, params));
    logger.logAll(logBuffer);
    if (logBuffer.containsProblem()) {
      return Optional.empty();
    }
    return positionedArgs(call, refP, params, positionalArgs.size(), logger);
  }

  private static ImmutableList<ExprP> leadingPositionalArgs(CallP call) {
    return call.args()
        .stream()
        .takeWhile(a -> !(a instanceof NamedArgP))
        .collect(toImmutableList());
  }

  private static Optional<ImmutableList<ExprP>> positionedArgs(CallP call, RefP refP,
      Optional<NList<ItemS>> params, int positionalArgsCount, Logger logBuffer) {
    var args = call.args();
    // Case where positional args count exceeds function params count is reported as error
    // during call unification. Here we silently ignore it by creating list that is big enough
    // to hold all args.
    var size = max(positionalArgsCount, params.map(NList::size).orElse(args.size()));
    var result = new ArrayList<ExprP>(nCopies(size, null));
    for (int i = 0; i < args.size(); i++) {
      var arg = args.get(i);
      if (arg instanceof NamedArgP namedArgP) {
        result.set(params.get().indexMap().get(namedArgP.name()), namedArgP);
      } else {
        result.set(i, arg);
      }
    }
    var error = false;
    for (int i = 0; i < result.size(); i++) {
      if (result.get(i) == null) {
        var defaultArg = params.get().get(i).body();
        if (defaultArg.isPresent()) {
          var exprS = defaultArg.get();
          result.set(i, new DefaultArgP(refP, exprS, exprS.loc()));
        } else {
          error = true;
          logBuffer.log(paramsMustBeSpecifiedError(call, i, params.get()));
        }
      }
    }
    return error ? Optional.empty() : Optional.of(ImmutableList.copyOf(result));
  }

  private static List<Log> findPositionalArgAfterNamedArgError(CallP call) {
    return call.args()
        .stream()
        .dropWhile(a -> !(a instanceof NamedArgP))
        .dropWhile(a -> a instanceof NamedArgP)
        .map(a -> compileError(a, "Positional arguments must be placed before named arguments."))
        .collect(toList());
  }

  private static List<Log> findUnknownParamNameErrors(CallP call, Optional<NList<ItemS>> params) {
    return call.args()
        .stream()
        .filter(a -> a instanceof NamedArgP)
        .map(a -> (NamedArgP) a)
        .filter(a -> params.isEmpty() || !params.get().containsName(a.name()))
        .map(a -> compileError(a, "Unknown parameter " + a.q() + "."))
        .collect(toList());
  }

  private static List<Log> findDuplicateAssignmentErrors(
      CallP call, List<ExprP> positionalArgs, Optional<NList<ItemS>> params) {
    if (params.isPresent()) {
      var names = positionalArgNames(positionalArgs, params.get());
      return call.args()
          .stream()
          .filter(a -> a instanceof NamedArgP)
          .map(a -> (NamedArgP) a)
          .filter(a -> !names.add(a.name()))
          .map(a -> compileError(a, a.q() + " is already assigned."))
          .collect(toList());
    } else {
      return list();
    }
  }

  private static Set<String> positionalArgNames(List<ExprP> positionalArgs, NList<ItemS> params) {
    return params.stream()
        .limit(positionalArgs.size())
        .flatMap(p -> p.nameO().stream())
        .collect(toSet());
  }

  private static Log paramsMustBeSpecifiedError(CallP call, int i, NList<ItemS> params) {
    String paramName = params.get(i).nameO().map(n -> "`" + n + "`").orElse("#" + (i + 1));
    return compileError(call, "Parameter " + paramName + " must be specified.");
  }
}
