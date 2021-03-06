package org.smoothbuild.lang.parse;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toMap;
import static org.smoothbuild.lang.base.type.GenericTypeMap.inferMapping;
import static org.smoothbuild.lang.parse.ParseError.parseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.smoothbuild.cli.console.LoggerImpl;
import org.smoothbuild.lang.base.Callable;
import org.smoothbuild.lang.base.Item;
import org.smoothbuild.lang.base.type.GenericTypeMap;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.CallNode;
import org.smoothbuild.lang.parse.ast.CallableNode;

import com.google.common.collect.ImmutableMap;

public class InferCallTypeAndParamAssignment {
  public static void inferCallTypeAndParamAssignment(CallNode call, Definitions imported,
      ImmutableMap<String, CallableNode> callables, LoggerImpl logger) {
    new Runnable() {
      @Override
      public void run() {
        call.setType(empty());
        List<? extends Item> parameters = callableParameters();
        List<ArgNode> assignedArgs = assignedArguments(parameters);
        if (logger.hasProblems()) {
          return;
        }

        GenericTypeMap<Type> actualTypeMap =
            inferActualTypesOfGenericParameters(parameters, assignedArgs);
        if (actualTypeMap == null) {
          return;
        }

        call.setAssignedArgs(assignedArgs);
        call.setType(callType(actualTypeMap));
      }

      private List<ArgNode> assignedArguments(List<? extends Item> parameters) {
        List<ArgNode> assignedArgs = asList(new ArgNode[parameters.size()]);
        Map<String, ? extends Item> parametersMap = parameters.stream()
            .collect(toMap(Item::name, p -> p));
        List<ArgNode> args = call.args();
        boolean inNamedArgsSection = false;
        for (int i = 0; i < args.size(); i++) {
          ArgNode arg = args.get(i);
          if (arg.declaresName()) {
            inNamedArgsSection = true;
            Item param = parametersMap.get(arg.name());
            if (param == null) {
              logger.log(parseError(arg, inCallToPrefix(call)
                  + "Unknown parameter '" + arg.name() + "'."));
            } else if (assignedArgs.get(param.index()) != null) {
              logger.log(parseError(arg,
                  inCallToPrefix(call) + "Argument '" + arg.name() + "' is already assigned."));
            } else {
              assignedArgs.set(param.index(), arg);
            }
          } else {
            if (inNamedArgsSection) {
              logger.log(parseError(arg, inCallToPrefix(call)
                  + "Positional arguments must be placed before named arguments."));
            } else if (i < parameters.size()) {
              assignedArgs.set(i, arg);
            } else {
              logger.log(parseError(arg,
                  inCallToPrefix(call) + "Too many positional arguments."));
            }
          }
        }
        if (logger.hasProblems()) {
          return null;
        }

        for (int i = 0; i < parameters.size(); i++) {
          Item param = parameters.get(i);
          ArgNode arg = assignedArgs.get(i);
          if (arg == null) {
            if (!param.hasDefaultValue()) {
              logger.log(parseError(call,
                  inCallToPrefix(call) + "Parameter " + param.q() + " must be specified."));
            }
          } else {
            Type argType = arg.type().get();
            if (!param.type().isParamAssignableFrom(argType)) {
              logger.log(parseError(arg, inCallToPrefix(call)
                  + "Cannot assign argument of type " + argType.q() + " to parameter '"
                  + param.name() + "' of type " + param.type().q() + "."));
            }
          }
        }
        return assignedArgs;
      }

      private String inCallToPrefix(CallNode call) {
        return "In call to `" + call.calledName() + "`: ";
      }

      private List<? extends Item> callableParameters() {
        String name = call.calledName();
        Callable callable = imported.callables().get(name);
        if (callable != null) {
          return callable.signature().parameters();
        }
        CallableNode node = callables.get(name);
        if (node != null) {
          return node.parameterInfos();
        }
        throw new RuntimeException("Couldn't find '" + call.calledName() + "' function.");
      }

      private GenericTypeMap<Type> inferActualTypesOfGenericParameters(
          List<? extends Item> parameters, List<ArgNode> assignedArgs) {
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
              parseError(call, "Cannot infer actual type(s) for generic parameter(s) in call to '"
                  + call.calledName() + "'."));
          return null;
        }
      }

      private Optional<Type> callType(GenericTypeMap<Type> actualTypeMap) {
        return callableType().map(actualTypeMap::applyTo);
      }

      private Optional<Type> callableType() {
        String name = call.calledName();
        Callable callable = imported.callables().get(name);
        if (callable != null) {
          return Optional.of(callable.signature().type());
        }
        CallableNode node = callables.get(name);
        if (node != null) {
          return node.type();
        }
        throw new RuntimeException("Couldn't find '" + call.calledName() + "' function.");
      }
    }.run();
  }
}
