package org.smoothbuild.parse;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toMap;
import static org.smoothbuild.lang.object.type.GenericTypeMap.inferMapping;
import static org.smoothbuild.parse.ParseError.parseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.smoothbuild.cli.console.LoggerImpl;
import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.base.ParameterInfo;
import org.smoothbuild.lang.object.type.GenericTypeMap;
import org.smoothbuild.lang.object.type.Type;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.ParameterizedNode;

import com.google.common.collect.ImmutableMap;

public class InferCallTypeAndParamAssignment {
  public static void inferCallTypeAndParamAssignment(CallNode call, Definitions imported,
      ImmutableMap<String, ParameterizedNode> functions, LoggerImpl logger) {
    new Runnable() {
      @Override
      public void run() {
        call.setType(empty());
        List<? extends ParameterInfo> parameters = functionParameters();
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

      private List<ArgNode> assignedArguments(List<? extends ParameterInfo> parameters) {
        List<ArgNode> assignedArgs = asList(new ArgNode[parameters.size()]);
        Map<String, ? extends ParameterInfo> parametersMap = parameters.stream()
            .collect(toMap(ParameterInfo::name, p -> p));
        List<ArgNode> args = call.args();
        boolean inNamedArgsSection = false;
        for (int i = 0; i < args.size(); i++) {
          ArgNode arg = args.get(i);
          if (arg.hasName()) {
            inNamedArgsSection = true;
            ParameterInfo param = parametersMap.get(arg.name());
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
          ParameterInfo param = parameters.get(i);
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
        return "In call to `" + call.name() + "`: ";
      }

      private List<? extends ParameterInfo> functionParameters() {
        String name = call.name();
        Function function = imported.functions().get(name);
        if (function != null) {
          return function.signature().parameters();
        }
        ParameterizedNode node = functions.get(name);
        if (node != null) {
          return node.getParameterInfos();
        }
        throw new RuntimeException("Couldn't find '" + call.name() + "' function.");
      }

      private GenericTypeMap<Type> inferActualTypesOfGenericParameters(
          List<? extends ParameterInfo> parameters, List<ArgNode> assignedArgs) {
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
          logger.log(parseError(call,
              "Cannot infer actual type(s) for generic parameter(s) in call to '" + call.name() +
                  "'."));
          return null;
        }
      }

      private Optional<Type> callType(GenericTypeMap<Type> actualTypeMap) {
        return functionType().map(actualTypeMap::applyTo);
      }

      private Optional<Type> functionType() {
        String name = call.name();
        Function function = imported.functions().get(name);
        if (function != null) {
          return Optional.of(function.signature().type());
        }
        ParameterizedNode node = functions.get(name);
        if (node != null) {
          return node.type();
        }
        throw new RuntimeException("Couldn't find '" + call.name() + "' function.");
      }
    }.run();
  }
}
