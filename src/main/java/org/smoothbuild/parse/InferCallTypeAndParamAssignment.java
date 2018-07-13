package org.smoothbuild.parse;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.ImmutableListMultimap.toImmutableListMultimap;
import static com.google.common.collect.Sets.filter;
import static org.smoothbuild.lang.type.TypeHierarchy.sortedTypes;
import static org.smoothbuild.parse.arg.ArgsStringHelper.argsToString;
import static org.smoothbuild.parse.arg.ArgsStringHelper.assignedArgsToString;
import static org.smoothbuild.util.Collections.toMap;
import static org.smoothbuild.util.Lists.filter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.function.ParameterInfo;
import org.smoothbuild.lang.runtime.Functions;
import org.smoothbuild.lang.runtime.SRuntime;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.parse.arg.ArgsStringHelper;
import org.smoothbuild.parse.arg.ParametersPool;
import org.smoothbuild.parse.arg.TypedParametersPool;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.CallNode;

import com.google.common.collect.ImmutableMultimap;

public class InferCallTypeAndParamAssignment {
  public static void inferCallTypeAndParamAssignment(CallNode call, SRuntime runtime, Ast ast,
      List<ParseError> errors) {
    new Runnable() {
      @Override
      public void run() {
        call.set(Type.class, callType());
        List<? extends ParameterInfo> parametersList = functionParameters();
        if (parametersList == null) {
          return;
        }
        Set<ParameterInfo> parameters = new HashSet<>(parametersList);
        if (assignNamedArguments(parameters)) {
          return;
        }
        if (assignNamelessArguments(parameters)) {
          return;
        }
        failWhenUnassignedRequiredParameterIsLeft(errors, parameters);
      }

      private List<? extends ParameterInfo> functionParameters() {
        String name = call.name();
        Functions functions = runtime.functions();
        if (functions.contains(name)) {
          return functions.get(name).signature().parameters();
        }
        if (ast.containsFunc(name)) {
          return ast.func(name).get(List.class);
        }
        if (ast.containsStruct(name)) {
          return ast.struct(name).get(List.class);
        }
        throw new RuntimeException("Couldn't find '" + call.name() + "' function.");
      }

      private Type callType() {
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

      private boolean assignNamedArguments(Set<ParameterInfo> parameters) {
        boolean failed = false;
        List<ArgNode> namedArgs = filter(call.args(), ArgNode::hasName);
        Map<String, ParameterInfo> map = toMap(parameters, ParameterInfo::name);
        for (ArgNode arg : namedArgs) {
          ParameterInfo parameter = map.get(arg.name());
          Type paramType = parameter.type();
          Type fixedArgType = runtime.types().fixNameClashIfExists(paramType, arg.get(Type.class));
          if (paramType.isAssignableFrom(fixedArgType)) {
            arg.set(ParameterInfo.class, parameter);
            parameters.remove(parameter);
          } else {
            failed = true;
            errors.add(new ParseError(arg,
                "Type mismatch, cannot convert argument '" + arg.name() + "' of type '"
                    + fixedArgType.name() + "' to '" + paramType.name() + "'."));
          }
        }
        return failed;
      }

      private boolean assignNamelessArguments(Set<ParameterInfo> parameters) {
        ParametersPool parametersPool = new ParametersPool(
            filter(parameters, not(ParameterInfo::isRequired)),
            filter(parameters, ParameterInfo::isRequired));
        ImmutableMultimap<Type, ArgNode> namelessArgs = call
            .args()
            .stream()
            .filter(a -> !a.hasName())
            .collect(toImmutableListMultimap(a -> a.get(Type.class), a -> a));
        for (Type type : sortedTypes(namelessArgs.keySet())) {
          Collection<ArgNode> availableArguments = namelessArgs.get(type);
          int argsSize = availableArguments.size();
          TypedParametersPool availableTypedParams = parametersPool.assignableFrom(type);
          if (argsSize == 1 && availableTypedParams.hasCandidate()) {
            ArgNode onlyArgument = availableArguments.iterator().next();
            ParameterInfo candidateParameter = availableTypedParams.candidate();
            onlyArgument.set(ParameterInfo.class, candidateParameter);
            parametersPool.take(candidateParameter);
            parameters.remove(candidateParameter);
          } else {
            availableArguments.stream().forEach(a -> a.set(ParameterInfo.class, null));
            String message = ambiguousAssignmentErrorMessage(
                call, availableArguments, availableTypedParams);
            errors.add(new ParseError(call, message));
            return true;
          }
        }
        return false;
      }

      private void failWhenUnassignedRequiredParameterIsLeft(List<ParseError> errors,
          Set<ParameterInfo> parameters) {
        Set<ParameterInfo> unassignedRequiredParameters = filter(parameters, p -> p.isRequired());
        if (!unassignedRequiredParameters.isEmpty()) {
          errors.add(new ParseError(call,
              missingRequiredArgsMessage(call, unassignedRequiredParameters)));
          return;
        }
      }

      private String missingRequiredArgsMessage(CallNode call,
          Set<ParameterInfo> missingRequiredParameters) {
        return "Not all parameters required by '" + call.name()
            + "' function has been specified.\n"
            + "Missing required parameters:\n"
            + ParameterInfo.iterableToString(missingRequiredParameters)
            + "All correct 'parameters <- arguments' assignments:\n"
            + ArgsStringHelper.assignedArgsToString(call);
      }

      private String ambiguousAssignmentErrorMessage(CallNode call,
          Collection<ArgNode> availableArgs, TypedParametersPool availableTypedParams) {
        String assignmentList = assignedArgsToString(call);
        if (availableTypedParams.isEmpty()) {
          return "Can't find parameter(s) of proper type in '"
              + call.name()
              + "' function for some nameless argument(s):\n"
              + "List of assignments that were successfully detected so far is following:\n"
              + assignmentList
              + "List of arguments for which no parameter could be found is following:\n"
              + argsToString(availableArgs);
        } else {
          return "Can't decide unambiguously to which parameters in '" + call.name()
              + "' function some nameless arguments should be assigned:\n"
              + "List of assignments that were successfully detected is following:\n"
              + assignmentList
              + "List of nameless arguments that caused problems:\n"
              + argsToString(availableArgs)
              + "List of unassigned parameters of desired type is following:\n"
              + availableTypedParams.toFormattedString();
        }
      }
    }.run();
  }
}
