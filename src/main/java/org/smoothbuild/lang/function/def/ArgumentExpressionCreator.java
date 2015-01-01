package org.smoothbuild.lang.function.def;

import static org.smoothbuild.lang.function.base.Parameters.parametersToNames;
import static org.smoothbuild.lang.type.Conversions.canConvert;
import static org.smoothbuild.lang.type.Types.allTypes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.ImplicitConverter;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.function.def.err.AmbiguousNamelessArgsError;
import org.smoothbuild.lang.function.def.err.DuplicateArgNameError;
import org.smoothbuild.lang.function.def.err.MissingRequiredArgsError;
import org.smoothbuild.lang.function.def.err.TypeMismatchError;
import org.smoothbuild.lang.function.def.err.UnknownParamNameError;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.listen.LoggedMessages;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Sets;

public class ArgumentExpressionCreator {
  private final ImplicitConverter implicitConverter;

  @Inject
  public ArgumentExpressionCreator(ImplicitConverter implicitConverter) {
    this.implicitConverter = implicitConverter;
  }

  public Map<String, Expression> createArgExprs(CodeLocation codeLocation, LoggedMessages messages,
      Function function, Collection<Argument> arguments) {
    ParametersPool parametersPool = new ParametersPool(function.parameters());
    ImmutableList<Argument> namedArguments = Argument.filterNamed(arguments);

    detectDuplicatedAndUnknownArgumentNames(function, messages, namedArguments);
    if (messages.containsProblems()) {
      return null;
    }

    Map<Parameter, Argument> argumentMap = new HashMap<>();
    processNamedArguments(parametersPool, messages, argumentMap, namedArguments);
    if (messages.containsProblems()) {
      return null;
    }

    processNamelessArguments(function, arguments, parametersPool, messages, argumentMap);
    if (messages.containsProblems()) {
      return null;
    }
    Set<Parameter> missingRequiredParameters = parametersPool.allRequired();
    if (missingRequiredParameters.size() != 0) {
      messages.log(new MissingRequiredArgsError(codeLocation, function, argumentMap,
          missingRequiredParameters));
      return null;
    }

    messages.failIfContainsProblems();
    return convert(argumentMap);
  }

  private static void detectDuplicatedAndUnknownArgumentNames(Function function,
      LoggedMessages messages, Collection<Argument> namedArguments) {
    Set<String> unusedNames = Sets.newHashSet(parametersToNames(function.parameters()));
    Set<String> usedNames = Sets.newHashSet();
    for (Argument argument : namedArguments) {
      if (argument.hasName()) {
        String name = argument.name();
        if (unusedNames.contains(name)) {
          unusedNames.remove(name);
          usedNames.add(name);
        } else if (usedNames.contains(name)) {
          messages.log(new DuplicateArgNameError(argument));
        } else {
          messages.log(new UnknownParamNameError(function.name(), argument));
        }
      }
    }
  }

  private static void processNamedArguments(ParametersPool parametersPool, LoggedMessages messages,
      Map<Parameter, Argument> argumentMap, Collection<Argument> namedArguments) {
    for (Argument argument : namedArguments) {
      if (argument.hasName()) {
        String name = argument.name();
        Parameter parameter = parametersPool.take(name);
        Type paramType = parameter.type();
        if (!canConvert(argument.type(), paramType)) {
          messages.log(new TypeMismatchError(argument, paramType));
        } else {
          argumentMap.put(parameter, argument);
        }
      }
    }
  }

  private static void processNamelessArguments(Function function, Collection<Argument> arguments,
      ParametersPool parametersPool, LoggedMessages messages, Map<Parameter, Argument> argumentMap) {
    ImmutableMultimap<Type, Argument> namelessArgs = Argument.filterNameless(arguments);

    for (Type type : allTypes()) {
      Collection<Argument> availableArguments = namelessArgs.get(type);
      int argsSize = availableArguments.size();
      if (0 < argsSize) {
        TypedParametersPool availableTypedParams = parametersPool.assignableFrom(type);

        if (argsSize == 1 && availableTypedParams.hasCandidate()) {
          Argument onlyArgument = availableArguments.iterator().next();
          Parameter candidateParameter = availableTypedParams.candidate();
          argumentMap.put(candidateParameter, onlyArgument);
          parametersPool.take(candidateParameter);
        } else {
          AmbiguousNamelessArgsError error =
              new AmbiguousNamelessArgsError(function.name(), argumentMap, availableArguments,
                  availableTypedParams);
          messages.log(error);
          return;
        }
      }
    }
  }

  private Map<String, Expression> convert(Map<Parameter, Argument> paramToArgMap) {
    Map<String, Expression> map = new HashMap<>();

    for (Map.Entry<Parameter, Argument> entry : paramToArgMap.entrySet()) {
      Parameter parameter = entry.getKey();
      Argument argument = entry.getValue();
      Expression expression = implicitConverter.apply(parameter.type(), argument.expression());
      map.put(parameter.name(), expression);
    }

    return map;
  }
}
