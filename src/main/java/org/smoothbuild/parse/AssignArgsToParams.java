package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.lang.function.base.Parameter.parametersToString;
import static org.smoothbuild.lang.type.Conversions.canConvert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.parse.arg.Argument;
import org.smoothbuild.parse.arg.MapToString;
import org.smoothbuild.parse.arg.ParametersPool;
import org.smoothbuild.parse.arg.TypedParametersPool;
import org.smoothbuild.parse.ast.ArgNode;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.FuncNode;

import com.google.common.collect.ImmutableMultimap;

public class AssignArgsToParams {
  public static List<ParseError> assignArgsToParams(Functions functions, Ast ast) {
    List<ParseError> errors = new ArrayList<>();
    new AstVisitor() {
      public void visitCall(CallNode call) {
        super.visitCall(call);
        List<Argument> arguments = call.args()
            .stream()
            .map(this::toArgument)
            .collect(toImmutableList());
        for (Argument argument : arguments) {
          if (!argument.hasType()) {
            return;
          }
        }
        Signature signature = functionSignature(call);
        if (signature == null) {
          return;
        }
        ParametersPool parametersPool = new ParametersPool(signature.parameters());
        if (processNamedArguments(parametersPool, arguments)) {
          return;
        }
        if (processNamelessArguments(
            call, signature, arguments, parametersPool, call.codeLocation())) {
          return;
        }
        Set<Parameter> missingRequiredParameters = parametersPool.allRequired();
        if (missingRequiredParameters.size() != 0) {
          errors.add(new ParseError(call.codeLocation(),
              missingRequiredArgsMessage(call, signature, missingRequiredParameters)));
          return;
        }
        for (Parameter parameter : parametersPool.allOptional()) {
          if (parameter.type() == Types.NOTHING) {
            errors.add(new ParseError(call.codeLocation(), "Parameter '" + parameter.name()
                + "' has to be assigned explicitly as type 'Nothing' doesn't have default value."));
            return;
          }
        }
      }

      private Signature functionSignature(CallNode call) {
        Name name = call.name();
        if (functions.contains(name)) {
          return functions.get(name).signature();
        }
        if (ast.nameToFunctionMap().containsKey(name)) {
          FuncNode function = ast.nameToFunctionMap().get(name);
          return function.get(Signature.class);
        }
        return null;
      }

      private Argument toArgument(ArgNode arg) {
        return new Argument(arg);
      }

      private String missingRequiredArgsMessage(CallNode call, Signature signature,
          Set<Parameter> missingRequiredParameters) {
        return "Not all parameters required by " + signature.name()
            + " function has been specified.\n"
            + "Missing required parameters:\n"
            + parametersToString(missingRequiredParameters)
            + "All correct 'parameters <- arguments' assignments:\n"
            + MapToString.toString(call);
      }

      private boolean processNamedArguments(ParametersPool parametersPool,
          Collection<Argument> arguments) {
        boolean failed = false;
        List<Argument> namedArguments = Argument.filterNamed(arguments);
        for (Argument argument : namedArguments) {
          if (argument.hasName()) {
            String name = argument.name();
            Parameter parameter = parametersPool.take(name);
            Type paramType = parameter.type();
            if (!canConvert(argument.type(), paramType)) {
              failed = true;
              errors.add(new ParseError(argument.codeLocation(),
                  "Type mismatch, cannot convert argument '" + argument.name() + "' of type '"
                      + argument.type().name() + "' to '" + paramType.name() + "'."));
              argument.arg().set(Parameter.class, null);
            } else {
              argument.arg().set(Parameter.class, parameter);
            }
          }
        }
        return failed;
      }

      private boolean processNamelessArguments(CallNode call, Signature signature,
          Collection<Argument> arguments, ParametersPool parametersPool,
          CodeLocation codeLocation) {
        ImmutableMultimap<Type, Argument> namelessArgs = Argument.filterNameless(arguments);
        for (Type type : Types.allTypes()) {
          Collection<Argument> availableArguments = namelessArgs.get(type);
          int argsSize = availableArguments.size();
          if (0 < argsSize) {
            TypedParametersPool availableTypedParams = parametersPool.assignableFrom(type);

            if (argsSize == 1 && availableTypedParams.hasCandidate()) {
              Argument onlyArgument = availableArguments.iterator().next();
              Parameter candidateParameter = availableTypedParams.candidate();
              onlyArgument.arg().set(Parameter.class, candidateParameter);
              parametersPool.take(candidateParameter);
            } else {
              String message = ambiguousAssignmentErrorMessage(
                  call, signature, availableArguments, availableTypedParams);
              errors.add(new ParseError(codeLocation, message));
              return true;
            }
          }
        }
        return false;
      }

      private String ambiguousAssignmentErrorMessage(CallNode call, Signature signature,
          Collection<Argument> availableArguments, TypedParametersPool availableTypedParams) {
        String assignmentList = MapToString.toString(call);
        if (availableTypedParams.isEmpty()) {
          return "Can't find parameter(s) of proper type in "
              + signature.name()
              + " function for some nameless argument(s):\n"
              + "List of assignments that were successfully detected so far is following:\n"
              + assignmentList
              + "List of arguments for which no parameter could be found is following:\n"
              + argsToList(availableArguments);
        } else {
          return "Can't decide unambiguously to which parameters in " + signature.name()
              + " function some nameless arguments should be assigned:\n"
              + "List of assignments that were successfully detected is following:\n"
              + assignmentList
              + "List of nameless arguments that caused problems:\n"
              + argsToList(availableArguments)
              + "List of unassigned parameters of desired type is following:\n"
              + availableTypedParams.toFormattedString();
        }
      }

      private String argsToList(Collection<Argument> availableArguments) {
        List<Argument> arguments = Argument.POSITION_ORDERING.sortedCopy(availableArguments);
        int typeLength = longestArgType(arguments);
        int nameLength = longestArgName(arguments);
        int positionLength = longestArgPosition(arguments);

        StringBuilder builder = new StringBuilder();
        for (Argument argument : arguments) {
          builder.append("  " + argument.toPaddedString(
              typeLength, nameLength, positionLength) + "\n");
        }
        return builder.toString();
      }

      private int longestArgType(List<Argument> arguments) {
        int result = 0;
        for (Argument argument : arguments) {
          result = Math.max(result, argument.type().name().length());
        }
        return result;
      }

      private int longestArgName(List<Argument> arguments) {
        int result = 0;
        for (Argument argument : arguments) {
          result = Math.max(result, argument.nameSanitized().length());
        }
        return result;
      }

      private int longestArgPosition(List<Argument> arguments) {
        int maxPosition = 0;
        for (Argument argument : arguments) {
          maxPosition = Math.max(maxPosition, argument.position());
        }
        return Integer.toString(maxPosition).length();
      }

    }.visitAst(ast);
    return errors;
  }
}
