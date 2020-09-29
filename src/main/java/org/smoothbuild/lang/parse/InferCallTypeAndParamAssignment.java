package org.smoothbuild.lang.parse;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;
import static org.smoothbuild.lang.base.type.InferTypeParameters.inferTypeParameters;
import static org.smoothbuild.lang.parse.ParseError.parseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.cli.console.ValueWithLogs;
import org.smoothbuild.lang.base.Callable;
import org.smoothbuild.lang.base.Definitions;
import org.smoothbuild.lang.base.Evaluable;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.GenericBasicType;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.CallNode;
import org.smoothbuild.lang.parse.ast.CallableNode;
import org.smoothbuild.lang.parse.ast.ExprNode;

import com.google.common.collect.ImmutableMap;

public class InferCallTypeAndParamAssignment {
  public static void inferCallTypeAndParamAssignment(CallNode call, Definitions imported,
      ImmutableMap<String, CallableNode> callables, Logger logger) {
    new Runnable() {
      @Override
      public void run() {
        call.setType(empty());
        List<? extends ItemSignature> parameters = callableParameters();
        ValueWithLogs<List<Assigned>> assigned = assigned(parameters);
        assigned.logs().forEach(logger::log);
        if (assigned.hasProblems() || !allAssignedHaveInferredType(assigned.value())) {
          return;
        }

        Map<GenericBasicType, Type> typeParametersMap =
            inferActualTypesOfGenericParameters(parameters, assigned.value());
        if (typeParametersMap == null) {
          return;
        }

        call.setAssignedArgs(assigned.value().stream().map(Assigned::expr).collect(toList()));
        call.setType(callableResultType(typeParametersMap));
      }

      private boolean allAssignedHaveInferredType(List<Assigned> assigned) {
        return assigned.stream().allMatch(a -> a.type().isPresent());
      }

      private ValueWithLogs<List<Assigned>> assigned(List<? extends ItemSignature> parameters) {
        var result = new ValueWithLogs<List<Assigned>>();
        List<Assigned> assignedList = asList(new Assigned[parameters.size()]);
        Map<String, Integer> nameToIndex = range(0, parameters.size())
            .boxed()
            .collect(toMap(i -> parameters.get(i).name(), i -> i));
        List<ArgNode> args = call.args();
        boolean inNamedArgsSection = false;
        for (int i = 0; i < args.size(); i++) {
          ArgNode arg = args.get(i);
          if (arg.declaresName()) {
            inNamedArgsSection = true;
            Integer index = nameToIndex.get(arg.name());
            if (index == null) {
              result.log(
                  parseError(arg, inCallToPrefix(call) + "Unknown parameter " + arg.q() + "."));
            } else if (assignedList.get(index) != null) {
              result.log(parseError(arg,
                  inCallToPrefix(call) + "`" + arg.name() + "` is already assigned."));
            } else {
              assignedList.set(index, new Assigned(arg));
            }
          } else {
            if (inNamedArgsSection) {
              result.log(parseError(arg, inCallToPrefix(call)
                  + "Positional arguments must be placed before named arguments."));
            } else if (i < parameters.size()) {
              assignedList.set(i, new Assigned(arg));
            } else {
              result.log(parseError(arg,
                  inCallToPrefix(call) + "Too many positional arguments."));
            }
          }
        }
        if (result.hasProblems()) {
          return result;
        }

        for (int i = 0; i < parameters.size(); i++) {
          ItemSignature param = parameters.get(i);
          Assigned assigned = assignedList.get(i);
          if (assigned == null) {
            if (param.defaultValueType().isPresent()) {
              assignedList.set(i, new Assigned(param));
            } else {
              result.log(parseError(call,
                  inCallToPrefix(call) + "Parameter " + param.q() + " must be specified."));
            }
          } else if (assigned.type().isPresent()) {
            Type argType = assigned.type().get();
            if (!param.type().isParamAssignableFrom(argType)) {
              result.log(parseError(assigned.location(), inCallToPrefix(call)
                  + "Cannot assign argument of type " + argType.q() + " to parameter "
                  + param.q() + " of type " + param.type().q() + "."));
            }
          }
        }
        result.setValue(assignedList);
        return result;
      }

      private String inCallToPrefix(CallNode call) {
        return "In call to `" + call.calledName() + "`: ";
      }

      private List<? extends ItemSignature> callableParameters() {
        String name = call.calledName();
        Evaluable evaluable = imported.evaluables().get(name);
        if (evaluable != null) {
          return ((Callable) evaluable).parameterSignatures();
        }
        CallableNode node = callables.get(name);
        if (node != null) {
          return node.parameterSignatures();
        }
        throw new RuntimeException("Couldn't find `" + call.calledName() + "` function.");
      }

      private Map<GenericBasicType, Type> inferActualTypesOfGenericParameters(
          List<? extends ItemSignature> parameters, List<Assigned> assigned) {
        List<Type> genericTypes = new ArrayList<>();
        List<Type> actualTypes = new ArrayList<>();
        for (int i = 0; i < parameters.size(); i++) {
          if (parameters.get(i).type().isGeneric()) {
            genericTypes.add(parameters.get(i).type());
            actualTypes.add(assigned.get(i).type().get());
          }
        }
        if (actualTypes.contains(null)) {
          return null;
        }
        try {
          return inferTypeParameters(genericTypes, actualTypes);
        } catch (IllegalArgumentException e) {
          logger.log(
              parseError(call, "Cannot infer actual type(s) for generic parameter(s) in call to `"
                  + call.calledName() + "`."));
          return null;
        }
      }

      private Optional<Type> callableResultType(Map<GenericBasicType, Type> typeParametersMap) {
        return callableResultType().map(t -> t.mapTypeParameters(typeParametersMap));
      }

      private Optional<Type> callableResultType() {
        String name = call.calledName();
        Callable callable = (Callable) imported.evaluables().get(name);
        if (callable != null) {
          return Optional.of(callable.resultType());
        }
        CallableNode node = callables.get(name);
        if (node != null) {
          return node.type();
        }
        throw new RuntimeException("Couldn't find `" + call.calledName() + "` function.");
      }
    }.run();
  }

  private static record Assigned(Optional<Type> type, ExprNode expr, Location location) {
    public Assigned(ArgNode arg) {
      this(arg.type(), arg.expr(), arg.location());
    }

    public Assigned(ItemSignature param) {
      this(param.defaultValueType(), null, null);
    }
  }
}
