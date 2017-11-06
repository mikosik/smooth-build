package org.smoothbuild.parse;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableListMultimap.toImmutableListMultimap;
import static org.smoothbuild.lang.type.Conversions.canConvert;
import static org.smoothbuild.parse.arg.ArgsStringHelper.argsToString;
import static org.smoothbuild.parse.arg.ArgsStringHelper.assignedArgsToString;
import static org.smoothbuild.util.Lists.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.function.base.TypedName;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.parse.arg.ArgsStringHelper;
import org.smoothbuild.parse.arg.ParametersPool;
import org.smoothbuild.parse.arg.TypedParametersPool;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.ParamNode;

import com.google.common.collect.ImmutableMultimap;

public class AssignArgsToParams {
  public static List<ParseError> assignArgsToParams(Functions functions, Ast ast) {
    List<ParseError> errors = new ArrayList<>();
    new AstVisitor() {
      public void visitCall(CallNode call) {
        super.visitCall(call);
        ParametersPool parametersPool = parametersPool(call);
        if (parametersPool == null) {
          return;
        }
        if (processNamedArguments(call, parametersPool)) {
          return;
        }
        if (processNamelessArguments(call, parametersPool)) {
          return;
        }
        Set<TypedName> missingRequiredParameters = parametersPool.allRequired();
        if (missingRequiredParameters.size() != 0) {
          errors.add(new ParseError(call,
              missingRequiredArgsMessage(call, missingRequiredParameters)));
          return;
        }
      }

      private ParametersPool parametersPool(CallNode call) {
        Name name = call.name();
        if (functions.contains(name)) {
          List<Parameter> parameters = functions.get(name).signature().parameters();
          return new ParametersPool(
              filter(parameters, not(Parameter::isRequired)),
              filter(parameters, Parameter::isRequired));
        }
        if (ast.nameToFunctionMap().containsKey(name)) {
          FuncNode function = ast.nameToFunctionMap().get(name);
          if (function.has(List.class)) {
            List<TypedName> typedNames = function.get(List.class);
            List<ParamNode> paramNodes = function.params();
            List<TypedName> required = new ArrayList<>();
            List<TypedName> optional = new ArrayList<>();
            for (int i = 0; i < paramNodes.size(); i++) {
              if (paramNodes.get(i).hasDefaultValue()) {
                optional.add(typedNames.get(i));
              } else {
                required.add(typedNames.get(i));
              }
            }
            return new ParametersPool(optional, required);
          }
        }
        return null;
      }

      private String missingRequiredArgsMessage(CallNode call,
          Set<TypedName> missingRequiredParameters) {
        return "Not all parameters required by '" + call.name()
            + "' function has been specified.\n"
            + "Missing required parameters:\n"
            + TypedName.iterableToString(missingRequiredParameters)
            + "All correct 'parameters <- arguments' assignments:\n"
            + ArgsStringHelper.assignedArgsToString(call);
      }

      private boolean processNamedArguments(CallNode call, ParametersPool parametersPool) {
        boolean failed = false;
        List<ArgNode> namedArgs = call
            .args()
            .stream()
            .filter(a -> a.hasName())
            .collect(toImmutableList());
        for (ArgNode arg : namedArgs) {
          Name name = arg.name();
          TypedName parameter = parametersPool.take(name);
          Type paramType = parameter.type();
          if (canConvert(arg.get(Type.class), paramType)) {
            arg.set(TypedName.class, parameter);
          } else {
            failed = true;
            errors.add(new ParseError(arg,
                "Type mismatch, cannot convert argument '" + arg.name() + "' of type '"
                    + arg.get(Type.class).name() + "' to '" + paramType.name() + "'."));
          }
        }
        return failed;
      }

      private boolean processNamelessArguments(CallNode call, ParametersPool parametersPool) {
        ImmutableMultimap<Type, ArgNode> namelessArgs = call
            .args()
            .stream()
            .filter(a -> !a.hasName())
            .collect(toImmutableListMultimap(a -> a.get(Type.class), a -> a));
        for (Type type : Types.allTypes()) {
          Collection<ArgNode> availableArguments = namelessArgs.get(type);
          int argsSize = availableArguments.size();
          if (0 < argsSize) {
            TypedParametersPool availableTypedParams = parametersPool.assignableFrom(type);

            if (argsSize == 1 && availableTypedParams.hasCandidate()) {
              ArgNode onlyArgument = availableArguments.iterator().next();
              TypedName candidateParameter = availableTypedParams.candidate();
              onlyArgument.set(TypedName.class, candidateParameter);
              parametersPool.take(candidateParameter);
            } else {
              String message = ambiguousAssignmentErrorMessage(
                  call, availableArguments, availableTypedParams);
              errors.add(new ParseError(call, message));
              return true;
            }
          }
        }
        return false;
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
    return errors;
  }
}
