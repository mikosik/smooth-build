package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.stream.IntStream.range;
import static org.smoothbuild.cli.console.Maybe.maybeLogs;
import static org.smoothbuild.cli.console.Maybe.maybeValueAndLogs;
import static org.smoothbuild.lang.parse.ParseError.parseError;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.LogBuffer;
import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.lang.base.define.ItemSignature;
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.base.type.impl.TypingS;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.CallNode;
import org.smoothbuild.util.collect.NamedList;

import com.google.common.collect.ImmutableList;

public class CallTypeInferrer {
  private final TypeFactoryS factory;
  private final TypingS typing;

  public CallTypeInferrer(TypeFactoryS factory, TypingS typing) {
    this.factory = factory;
    this.typing = typing;
  }

  public Maybe<TypeS> inferCallType(CallNode call, TypeS resultType,
      NamedList<ItemSignature> parameters) {
    var logBuffer = new LogBuffer();
    List<Optional<ArgNode>> assignedArgs = call.assignedArgs();
    ImmutableList<ItemSignature> paramSignatures = parameters.objects();
    findIllegalTypeAssignmentErrors(call, assignedArgs, paramSignatures, logBuffer);
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }
    List<Optional<TypeS>> assignedTypes = assignedTypes(paramSignatures, assignedArgs);
    if (allAssignedTypesAreInferred(assignedTypes)) {
      var boundedVariables = typing.inferVariableBoundsInCall(
          resultType,
          map(paramSignatures, ItemSignature::type),
          map(assignedTypes, Optional::get));
      var variableProblems = findVariableProblems(call, boundedVariables);
      if (!variableProblems.isEmpty()) {
        logBuffer.logAll(variableProblems);
        return maybeLogs(logBuffer);
      }
      TypeS mapped = typing.mapVariables(resultType, boundedVariables, factory.lower());
      return maybeValueAndLogs(mapped, logBuffer);
    }
    return maybeLogs(logBuffer);
  }

  private void findIllegalTypeAssignmentErrors(CallNode call,
      List<Optional<ArgNode>> assignedList, List<ItemSignature> parameters, Logger logger) {
    range(0, assignedList.size())
        .filter(i -> assignedList.get(i).isPresent())
        .filter(i -> !isAssignable(parameters.get(i), assignedList.get(i).get()))
        .mapToObj(i -> illegalAssignmentError(call, parameters.get(i), assignedList.get(i).get()))
        .forEach(logger::log);
  }

  private boolean isAssignable(ItemSignature parameter, ArgNode arg) {
    return typing.isParamAssignable(parameter.type(), arg.type().get());
  }

  private static Log illegalAssignmentError(CallNode call, ItemSignature parameter, ArgNode arg) {
    return parseError(arg.location(), inCallToPrefix(call)
        + "Cannot assign argument of type " + arg.type().get().q() + " to parameter "
        + parameter.q() + " of type " + parameter.type().q() + ".");
  }

  private static String inCallToPrefix(CallNode call) {
    return "In call to function with type " + call.function().type().get().q() + ": ";
  }

  private List<Optional<TypeS>> assignedTypes(
      List<ItemSignature> parameters, List<Optional<ArgNode>> arguments) {
    List<Optional<TypeS>> assigned = new ArrayList<>();
    for (int i = 0; i < parameters.size(); i++) {
      Optional<ArgNode> arg = arguments.get(i);
      if (arg.isPresent()) {
        assigned.add(arg.get().type());
      } else {
        assigned.add(parameters.get(i).defaultValueType());
      }
    }
    return assigned;
  }

  private static boolean allAssignedTypesAreInferred(List<Optional<TypeS>> assigned) {
    return assigned.stream().allMatch(Optional::isPresent);
  }

  private ImmutableList<Log> findVariableProblems(
      CallNode call, BoundsMap<TypeS> boundedVariables) {
    return boundedVariables.map().values().stream()
        .filter(b -> typing.contains(b.bounds().lower(), factory.any()))
        .map(b -> parseError(call, "Cannot infer actual type for type variable "
            + b.variable().q() + "."))
        .collect(toImmutableList());
  }
}
