package org.smoothbuild.parse;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;
import static org.smoothbuild.lang.type.GenericTypeMap.inferMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.smoothbuild.lang.base.ParameterInfo;
import org.smoothbuild.lang.runtime.Functions;
import org.smoothbuild.lang.runtime.SRuntime;
import org.smoothbuild.lang.type.GenericTypeMap;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.CallNode;

public class InferCallTypeAndParamAssignment {
  public static void inferCallTypeAndParamAssignment(CallNode call, SRuntime runtime, Ast ast,
      List<ParseError> errors) {
    new Runnable() {
      @Override
      public void run() {
        call.set(Type.class, null);
        call.setAssignedArgs(null);

        List<? extends ParameterInfo> parameters = functionParameters();
        List<ArgNode> assignedArgs = assignedArguments(parameters);
        if (!errors.isEmpty()) {
          return;
        }

        GenericTypeMap<Type> actualTypeMap =
            inferActualTypesOfGenericParameters(parameters, assignedArgs);
        if (actualTypeMap == null) {
          return;
        }

        call.setAssignedArgs(assignedArgs);
        call.set(Type.class, callType(actualTypeMap));
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
              errors.add(new ParseError(arg,
                  "Function '" + call.name() + "' has no parameter '" + arg.name() + "'."));
            } else if (assignedArgs.get(param.index()) != null) {
              errors.add(new ParseError(arg, "Argument '" + arg.name() + "' is already assigned."));
            } else {
              assignedArgs.set(param.index(), arg);
            }
          } else {
            if (inNamedArgsSection) {
              errors.add(new ParseError(
                  arg, "Positional arguments must be placed before named arguments."));
            } else if (i < parameters.size()) {
              assignedArgs.set(i, arg);
            } else {
              errors.add(new ParseError(arg, "Too many positional arguments."));
            }
          }
        }
        if (!errors.isEmpty()) {
          return null;
        }

        for (int i = 0; i < parameters.size(); i++) {
          ParameterInfo param = parameters.get(i);
          ArgNode arg = assignedArgs.get(i);
          if (arg == null) {
            if (!param.hasDefaultValue()) {
              errors.add(
                  new ParseError(call, "Parameter " + param.q() + " must be specified."));
            }
          } else {
            Type argType = arg.get(Type.class);
            if (!param.type().isParamAssignableFrom(argType)) {
              errors.add(new ParseError(arg,
                  "Cannot assign argument of type " + argType.q() + " to parameter '" +
                      param.name() + "' of type " + param.type().q() + "."));
            }
          }
        }
        return assignedArgs;
      }

      private List<? extends ParameterInfo> functionParameters() {
        String name = call.name();
        Functions functions = runtime.functions();
        if (functions.contains(name)) {
          return functions.get(name).signature().parameters();
        }
        if (ast.containsFunc(name)) {
          return ast.func(name).getParameterInfos();
        }
        if (ast.containsStruct(name)) {
          return ast.struct(name).getParameterInfos();
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
            actualTypes.add(assignedArgs.get(i).get(Type.class));
          }
        }
        if (actualTypes.contains(null)) {
          return null;
        }
        try {
          return inferMapping(genericTypes, actualTypes);
        } catch (IllegalArgumentException e) {
          errors.add(new ParseError(call,
              "Cannot infer actual type(s) for generic parameter(s) in call to '" + call.name() +
                  "'."));
          return null;
        }
      }

      private Type callType(GenericTypeMap<Type> actualTypeMap) {
        Type functionType = functionType();
        if (functionType == null) {
          return null;
        } else {
          return actualTypeMap.applyTo(functionType);
        }
      }

      private Type functionType() {
        String name = call.name();
        Functions functions = runtime.functions();
        if (functions.contains(name)) {
          return functions.get(name).signature().type();
        }
        if (ast.containsFunc(name)) {
          return ast.func(name).get(Type.class);
        }
        if (ast.containsStruct(name)) {
          return ast.struct(name).get(Type.class);
        }
        throw new RuntimeException("Couldn't find '" + call.name() + "' function.");
      }
    }.run();
  }
}
