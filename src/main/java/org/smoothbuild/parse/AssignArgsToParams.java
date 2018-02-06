package org.smoothbuild.parse;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.ImmutableListMultimap.toImmutableListMultimap;
import static com.google.common.collect.Sets.filter;
import static org.smoothbuild.lang.type.TypeHierarchy.sortedTypes;
import static org.smoothbuild.parse.arg.ArgsStringHelper.argsToString;
import static org.smoothbuild.parse.arg.ArgsStringHelper.assignedArgsToString;
import static org.smoothbuild.util.Collections.toMap;
import static org.smoothbuild.util.Lists.filter;
import static org.smoothbuild.util.Maybe.maybe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.ParameterInfo;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.parse.arg.ArgsStringHelper;
import org.smoothbuild.parse.arg.ParametersPool;
import org.smoothbuild.parse.arg.TypedParametersPool;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.util.Maybe;

import com.google.common.collect.ImmutableMultimap;

public class AssignArgsToParams {
  public static Maybe<Ast> assignArgsToParams(Functions functions, Ast ast) {
    List<ParseError> errors = new ArrayList<>();
    new AstVisitor() {
      @Override
      public void visitCall(CallNode call) {
        super.visitCall(call);
        Set<ParameterInfo> parameters = functionParameters(call);
        if (parameters == null) {
          return;
        }
        if (assignNamedArguments(call, parameters)) {
          return;
        }
        if (assignNamelessArguments(call, parameters)) {
          return;
        }
        failWhenUnassignedRequiredParameterIsLeft(errors, call, parameters);
      }

      private Set<ParameterInfo> functionParameters(CallNode call) {
        Name name = call.name();
        if (functions.contains(name)) {
          return new HashSet<>(functions.get(name).signature().parameters());
        }
        if (ast.containsFunction(name)) {
          FuncNode function = ast.function(name);
          if (function.has(List.class)) {
            return new HashSet<>(function.get(List.class));
          }
        }
        return null;
      }

      private boolean assignNamedArguments(CallNode call,
          Set<ParameterInfo> parameters) {
        boolean failed = false;
        List<ArgNode> namedArgs = filter(call.args(), ArgNode::hasName);
        Map<Name, ParameterInfo> map = toMap(parameters, ParameterInfo::name);
        for (ArgNode arg : namedArgs) {
          ParameterInfo parameter = map.get(arg.name());
          Type paramType = parameter.type();
          if (paramType.isAssignableFrom(arg.get(Type.class))) {
            arg.set(ParameterInfo.class, parameter);
            parameters.remove(parameter);
          } else {
            failed = true;
            errors.add(new ParseError(arg,
                "Type mismatch, cannot convert argument '" + arg.name() + "' of type '"
                    + arg.get(Type.class).name() + "' to '" + paramType.name() + "'."));
          }
        }
        return failed;
      }

      private boolean assignNamelessArguments(CallNode call, Set<ParameterInfo> parameters) {
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
            String message = ambiguousAssignmentErrorMessage(
                call, availableArguments, availableTypedParams);
            errors.add(new ParseError(call, message));
            return true;
          }
        }
        return false;
      }

      private void failWhenUnassignedRequiredParameterIsLeft(List<ParseError> errors, CallNode call,
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

    }.visitAst(ast);
    return maybe(ast, errors);
  }
}
