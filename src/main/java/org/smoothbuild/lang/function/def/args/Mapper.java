package org.smoothbuild.lang.function.def.args;

import static org.smoothbuild.lang.base.Conversions.canConvert;
import static org.smoothbuild.lang.base.Types.allTypes;
import static org.smoothbuild.lang.function.base.Parameters.parametersToNames;

import java.util.Collection;
import java.util.Set;

import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.function.def.args.err.AmbiguousNamelessArgsError;
import org.smoothbuild.lang.function.def.args.err.DuplicateArgNameError;
import org.smoothbuild.lang.function.def.args.err.MissingRequiredArgsError;
import org.smoothbuild.lang.function.def.args.err.TypeMismatchError;
import org.smoothbuild.lang.function.def.args.err.UnknownParamNameError;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.listen.LoggedMessages;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Sets;

public class Mapper {
  private final CodeLocation codeLocation;
  private final LoggedMessages messages;
  private final Function function;
  private final ParametersPool parametersPool;
  private final Collection<Argument> allArguments;

  public Mapper(CodeLocation codeLocation, LoggedMessages messages, Function function,
      Collection<Argument> arguments) {
    this.codeLocation = codeLocation;
    this.messages = messages;
    this.function = function;
    this.parametersPool = new ParametersPool(function.parameters());
    this.allArguments = arguments;
  }

  public ImmutableMap<Parameter, Argument> detectMapping() {
    ImmutableList<Argument> namedArguments = Argument.filterNamed(allArguments);

    detectDuplicatedAndUnknownArgumentNames(namedArguments);
    if (messages.containsProblems()) {
      return null;
    }

    Builder<Parameter, Argument> mapBuilder = ImmutableMap.builder();
    processNamedArguments(mapBuilder, namedArguments);
    if (messages.containsProblems()) {
      return null;
    }

    processNamelessArguments(mapBuilder);
    if (messages.containsProblems()) {
      return null;
    }
    ImmutableMap<Parameter, Argument> mapping = mapBuilder.build();

    Set<Parameter> missingRequiredParameters = parametersPool.allRequired();
    if (missingRequiredParameters.size() != 0) {
      messages.log(new MissingRequiredArgsError(codeLocation, function, mapping,
          missingRequiredParameters));
      return null;
    }

    return mapping;
  }

  private void detectDuplicatedAndUnknownArgumentNames(Collection<Argument> namedArguments) {
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

  private void processNamedArguments(Builder<Parameter, Argument> mapBuilder,
      Collection<Argument> namedArguments) {
    for (Argument argument : namedArguments) {
      if (argument.hasName()) {
        String name = argument.name();
        Parameter parameter = parametersPool.take(name);
        Type paramType = parameter.type();
        if (!canConvert(argument.type(), paramType)) {
          messages.log(new TypeMismatchError(argument, paramType));
        } else {
          mapBuilder.put(parameter, argument);
        }
      }
    }
  }

  private void processNamelessArguments(Builder<Parameter, Argument> mapBuilder) {
    ImmutableMultimap<Type, Argument> namelessArgs = Argument.filterNameless(allArguments);

    for (Type type : allTypes()) {
      Collection<Argument> availableArguments = namelessArgs.get(type);
      int argsSize = availableArguments.size();
      if (0 < argsSize) {
        TypedParametersPool availableTypedParams = parametersPool.assignableFrom(type);

        if (argsSize == 1 && availableTypedParams.hasCandidate()) {
          Argument onlyArgument = availableArguments.iterator().next();
          Parameter candidateParameter = availableTypedParams.candidate();
          mapBuilder.put(candidateParameter, onlyArgument);
          parametersPool.take(candidateParameter);
        } else {
          AmbiguousNamelessArgsError error =
              new AmbiguousNamelessArgsError(function.name(), mapBuilder.build(),
                  availableArguments, availableTypedParams);
          messages.log(error);
          return;
        }
      }
    }
  }

}
