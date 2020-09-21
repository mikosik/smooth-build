package org.smoothbuild.lang.parse;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;
import static org.smoothbuild.lang.base.type.GenericTypeMap.inferMapping;
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
import org.smoothbuild.lang.base.type.GenericTypeMap;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.CallNode;
import org.smoothbuild.lang.parse.ast.CallableNode;

import com.google.common.collect.ImmutableMap;

public class InferCallTypeAndParamAssignment {
  public static void inferCallTypeAndParamAssignment(CallNode call, Definitions imported,
      ImmutableMap<String, CallableNode> callables, Logger logger) {
    new Runnable() {
      @Override
      public void run() {
        call.setType(empty());
        List<? extends ItemSignature> parameters = callableParameters();
        ValueWithLogs<List<ArgNode>> assignedArgs = assignedArguments(parameters);
        assignedArgs.logs().forEach(logger::log);
        if (assignedArgs.hasProblems() || !allArgumentsHaveInferredType(assignedArgs.value())) {
          return;
        }

        GenericTypeMap actualTypeMap =
            inferActualTypesOfGenericParameters(parameters, assignedArgs.value());
        if (actualTypeMap == null) {
          return;
        }

        call.setAssignedArgs(assignedArgs.value());
        call.setType(callType(actualTypeMap));
      }

      private boolean allArgumentsHaveInferredType(List<ArgNode> args) {
        return args.stream().allMatch(a -> a == null || a.type().isPresent());
      }

      private ValueWithLogs<List<ArgNode>> assignedArguments(List<? extends ItemSignature> parameters) {
        var result = new ValueWithLogs<List<ArgNode>>();
        List<ArgNode> assignedArgs = asList(new ArgNode[parameters.size()]);
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
            } else if (assignedArgs.get(index) != null) {
              result.log(parseError(arg,
                  inCallToPrefix(call) + "`" + arg.name() + "` is already assigned."));
            } else {
              assignedArgs.set(index, arg);
            }
          } else {
            if (inNamedArgsSection) {
              result.log(parseError(arg, inCallToPrefix(call)
                  + "Positional arguments must be placed before named arguments."));
            } else if (i < parameters.size()) {
              assignedArgs.set(i, arg);
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
          ArgNode arg = assignedArgs.get(i);
          if (arg == null) {
            if (!param.hasDefaultValue()) {
              result.log(parseError(call,
                  inCallToPrefix(call) + "Parameter " + param.q() + " must be specified."));
            }
          } else if (arg.type().isPresent()) {
            Type argType = arg.type().get();
            if (!param.type().isParamAssignableFrom(argType)) {
              result.log(parseError(arg, inCallToPrefix(call)
                  + "Cannot assign argument of type " + argType.q() + " to parameter "
                  + param.q() + " of type " + param.type().q() + "."));
            }
          }
        }
        result.setValue(assignedArgs);
        return result;
      }

      private String inCallToPrefix(CallNode call) {
        return "In call to `" + call.calledName() + "`: ";
      }

      private List<? extends ItemSignature> callableParameters() {
        String name = call.calledName();
        Evaluable evaluable = imported.evaluables().get(name);
        if (evaluable != null) {
          return ((Callable) evaluable).signature().parameters();
        }
        CallableNode node = callables.get(name);
        if (node != null) {
          return node.parameterInfos();
        }
        throw new RuntimeException("Couldn't find `" + call.calledName() + "` function.");
      }

      private GenericTypeMap inferActualTypesOfGenericParameters(
          List<? extends ItemSignature> parameters, List<ArgNode> assignedArgs) {
        List<Type> genericTypes = new ArrayList<>();
        List<Type> actualTypes = new ArrayList<>();
        for (int i = 0; i < parameters.size(); i++) {
          if (parameters.get(i).type().isGeneric()) {
            genericTypes.add(parameters.get(i).type());
            actualTypes.add(assignedArgs.get(i).type().get());
          }
        }
        if (actualTypes.contains(null)) {
          return null;
        }
        try {
          return inferMapping(genericTypes, actualTypes);
        } catch (IllegalArgumentException e) {
          logger.log(
              parseError(call, "Cannot infer actual type(s) for generic parameter(s) in call to `"
                  + call.calledName() + "`."));
          return null;
        }
      }

      private Optional<Type> callType(GenericTypeMap actualTypeMap) {
        return callableType().map(actualTypeMap::applyTo);
      }

      private Optional<Type> callableType() {
        String name = call.calledName();
        Callable callable = (Callable) imported.evaluables().get(name);
        if (callable != null) {
          return Optional.of(callable.signature().type());
        }
        CallableNode node = callables.get(name);
        if (node != null) {
          return node.type();
        }
        throw new RuntimeException("Couldn't find `" + call.calledName() + "` function.");
      }
    }.run();
  }
}
