package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.lang.type.GenericTypeMap.inferMapping;
import static org.smoothbuild.parse.ArgToParamInferer.findAssignment;
import static org.smoothbuild.util.Collections.toMap;
import static org.smoothbuild.util.Lists.filter;
import static org.smoothbuild.util.Lists.map;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.base.ParameterInfo;
import org.smoothbuild.lang.runtime.Functions;
import org.smoothbuild.lang.runtime.SRuntime;
import org.smoothbuild.lang.type.GenericTypeMap;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.parse.ArgToParamInferer.ArgToParamInfererException;
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
        List<? extends ParameterInfo> parametersList = functionParameters();
        Set<ParameterInfo> parameters = new HashSet<>(parametersList);
        if (assignNamedArguments(parameters)) {
          return;
        }
        GenericTypeMap<Type> actualTypeMap = inferActualTypesOfGenericParameters();
        if (actualTypeMap == null) {
          return;
        }
        parameters
            .stream()
            .filter(p -> p.type().isGeneric())
            .forEach(p -> errors.add(implicitAssignmentOfGenericParameterError(call, p)));
        if (!errors.isEmpty()) {
          return;
        }
        if (assignNamelessArguments(parameters)) {
          return;
        }
        call.set(Type.class, callType(parametersList, actualTypeMap));
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

      private boolean assignNamedArguments(Set<ParameterInfo> parameters) {
        boolean failed = false;
        List<ArgNode> namedArgs = filter(call.args(), ArgNode::hasName);
        Map<String, ParameterInfo> map = toMap(parameters, ParameterInfo::name);
        for (ArgNode arg : namedArgs) {
          ParameterInfo parameter = map.get(arg.name());
          Type paramType = parameter.type();
          Type argType = arg.get(Type.class);
          if (argType == null) {
            failed = true;
          } else if (paramType.isParamAssignableFrom(argType)) {
            arg.set(ParameterInfo.class, parameter);
            parameters.remove(parameter);
          } else {
            failed = true;
            errors.add(new ParseError(arg,
                "Type mismatch, cannot convert argument '" + arg.name() + "' of type "
                    + argType.q() + " to '" + paramType.name() + "'."));
          }
        }
        return failed;
      }

      private GenericTypeMap<Type> inferActualTypesOfGenericParameters() {
        List<ArgNode> argAssignedToGenericParam = call
            .args()
            .stream()
            .filter(ArgNode::hasName)
            .filter(a -> a.get(ParameterInfo.class).type().isGeneric())
            .collect(toImmutableList());
        try {
          List<Type> types = map(argAssignedToGenericParam, a -> a.get(ParameterInfo.class).type());
          List<Type> actualTypes = map(argAssignedToGenericParam, a -> a.get(Type.class));
          if (actualTypes.contains(null)) {
            return null;
          }
          return inferMapping(types, actualTypes);
        } catch (IllegalArgumentException e) {
          errors.add(new ParseError(call,
              "Cannot infer actual type(s) for generic parameter(s) in call to '" + call.name()
                  + "'."));
          return null;
        }
      }

      private ParseError implicitAssignmentOfGenericParameterError(CallNode call,
          ParameterInfo parameter) {
        return new ParseError(
            call, "Generic parameter '" + parameter.name() + "' must be assigned explicitly");
      }

      private boolean assignNamelessArguments(Set<ParameterInfo> parameters) {
        List<ArgNode> namelessArgsList = filter(call.args(), a -> !a.hasName());
        try {
          ArrayList<ParameterInfo> parameterList = new ArrayList<>(parameters);
          List<ArgNode> assignment = findAssignment(namelessArgsList, parameterList);
          for (int i = 0; i < assignment.size(); i++) {
            ArgNode arg = assignment.get(i);
            if (arg != null) {
              arg.set(ParameterInfo.class, parameterList.get(i));
            }
          }
        } catch (ArgToParamInfererException e) {
          errors.add(new ParseError(call,
              "Cannot infer arguments to parameters assignment in call to '" + call.name() + "'. "
                  + e.getMessage()));
          return true;
        }
        return false;
      }

      private Type callType(List<? extends ParameterInfo> parameters,
          GenericTypeMap<Type> actualTypeMap) {
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
