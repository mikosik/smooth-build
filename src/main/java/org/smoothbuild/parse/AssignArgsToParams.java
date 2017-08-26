package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableListMultimap.toImmutableListMultimap;
import static org.smoothbuild.lang.function.base.Parameter.parametersToString;
import static org.smoothbuild.lang.type.Conversions.canConvert;
import static org.smoothbuild.parse.arg.ArgsStringHelper.argsToString;
import static org.smoothbuild.parse.arg.ArgsStringHelper.assignedArgsToString;

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
import org.smoothbuild.parse.arg.ArgsStringHelper;
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
        if (!eachArgHasType(call)) {
          return;
        }
        Signature signature = functionSignature(call);
        if (signature == null) {
          return;
        }
        ParametersPool parametersPool = new ParametersPool(signature.parameters());
        if (processNamedArguments(call, parametersPool)) {
          return;
        }
        if (processNamelessArguments(
            call, signature, parametersPool, call.codeLocation())) {
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
            errors.add(new ParseError(call.codeLocation(), "Parameter '" + parameter.name().value()
                + "' has to be assigned explicitly as type 'Nothing' doesn't have default value."));
            return;
          }
        }
      }

      private boolean eachArgHasType(CallNode call) {
        return call
            .args()
            .stream()
            .allMatch(a -> a.has(Type.class));
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

      private String missingRequiredArgsMessage(CallNode call, Signature signature,
          Set<Parameter> missingRequiredParameters) {
        return "Not all parameters required by " + signature.name()
            + " function has been specified.\n"
            + "Missing required parameters:\n"
            + parametersToString(missingRequiredParameters)
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
          Parameter parameter = parametersPool.take(name);
          Type paramType = parameter.type();
          if (!canConvert(arg.get(Type.class), paramType)) {
            failed = true;
            errors.add(new ParseError(arg.codeLocation(),
                "Type mismatch, cannot convert argument '" + arg.name().value() + "' of type '"
                    + arg.get(Type.class).name() + "' to '" + paramType.name() + "'."));
            arg.set(Parameter.class, null);
          } else {
            arg.set(Parameter.class, parameter);
          }
        }
        return failed;
      }

      private boolean processNamelessArguments(CallNode call, Signature signature,
          ParametersPool parametersPool, CodeLocation codeLocation) {
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
              Parameter candidateParameter = availableTypedParams.candidate();
              onlyArgument.set(Parameter.class, candidateParameter);
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
          Collection<ArgNode> availableArgs, TypedParametersPool availableTypedParams) {
        String assignmentList = assignedArgsToString(call);
        if (availableTypedParams.isEmpty()) {
          return "Can't find parameter(s) of proper type in "
              + signature.name()
              + " function for some nameless argument(s):\n"
              + "List of assignments that were successfully detected so far is following:\n"
              + assignmentList
              + "List of arguments for which no parameter could be found is following:\n"
              + argsToString(availableArgs);
        } else {
          return "Can't decide unambiguously to which parameters in " + signature.name()
              + " function some nameless arguments should be assigned:\n"
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
