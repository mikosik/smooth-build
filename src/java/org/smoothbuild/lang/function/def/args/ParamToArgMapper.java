package org.smoothbuild.lang.function.def.args;

import static org.smoothbuild.lang.type.STypes.allTypes;

import java.util.Collection;
import java.util.Set;

import org.smoothbuild.lang.convert.Conversions;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.def.args.err.AmbiguousNamelessArgsError;
import org.smoothbuild.lang.function.def.args.err.DuplicateArgNameError;
import org.smoothbuild.lang.function.def.args.err.MissingRequiredArgsError;
import org.smoothbuild.lang.function.def.args.err.TypeMismatchError;
import org.smoothbuild.lang.function.def.args.err.UnknownParamNameError;
import org.smoothbuild.lang.type.SType;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.listen.LoggedMessages;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class ParamToArgMapper {
  private final CodeLocation codeLocation;
  private final LoggedMessages messages;
  private final Function function;
  private final ParamsPool paramsPool;
  private final Collection<Arg> allArguments;

  public ParamToArgMapper(CodeLocation codeLocation, LoggedMessages messages, Function function,
      Collection<Arg> args) {
    this.codeLocation = codeLocation;
    this.messages = messages;
    this.function = function;
    this.paramsPool = new ParamsPool(function.params());
    this.allArguments = args;
  }

  public ImmutableMap<Param, Arg> detectMapping() {
    ImmutableList<Arg> namedArgs = Arg.filterNamed(allArguments);

    detectDuplicatedAndUnknownArgNames(namedArgs);
    if (messages.containsProblems()) {
      return null;
    }

    ParamToArgMapBuilder paramToArgMapBuilder = new ParamToArgMapBuilder();
    processNamedArguments(paramToArgMapBuilder, namedArgs);
    if (messages.containsProblems()) {
      return null;
    }

    processNamelessArguments(paramToArgMapBuilder);
    if (messages.containsProblems()) {
      return null;
    }

    Set<Param> missingRequiredParams = paramsPool.availableRequiredParams();
    if (missingRequiredParams.size() != 0) {
      messages.log(new MissingRequiredArgsError(codeLocation, function, paramToArgMapBuilder,
          missingRequiredParams));
      return null;
    }

    return paramToArgMapBuilder.build();
  }

  private void detectDuplicatedAndUnknownArgNames(Collection<Arg> namedArgs) {
    Set<String> names = Sets.newHashSet();
    for (Arg arg : namedArgs) {
      if (arg.hasName()) {
        String name = arg.name();
        if (names.contains(name)) {
          messages.log(new DuplicateArgNameError(arg));
        } else if (!function.params().containsKey(name)) {
          messages.log(new UnknownParamNameError(function.name(), arg));
        } else {
          names.add(name);
        }
      }
    }
  }

  private void processNamedArguments(ParamToArgMapBuilder paramToArgMapBuilder,
      Collection<Arg> namedArgs) {
    for (Arg arg : namedArgs) {
      if (arg.hasName()) {
        String name = arg.name();
        Param param = paramsPool.takeByName(name);
        SType<?> paramType = param.type();
        if (!Conversions.canConvert(arg.type(), paramType)) {
          messages.log(new TypeMismatchError(arg, paramType));
        } else {
          paramToArgMapBuilder.add(param, arg);
        }
      }
    }
  }

  private void processNamelessArguments(ParamToArgMapBuilder paramToArgMapBuilder) {
    ImmutableMap<SType<?>, Set<Arg>> namelessArgs = Arg.filterNameless(allArguments);

    for (SType<?> type : allTypes()) {
      Set<Arg> availableArgs = namelessArgs.get(type);
      int argsSize = availableArgs.size();
      if (0 < argsSize) {
        TypedParamsPool availableTypedParams = paramsPool.availableForType(type);

        if (argsSize == 1 && availableTypedParams.hasCandidate()) {
          Arg onlyArg = availableArgs.iterator().next();
          Param candidateParam = availableTypedParams.candidate();
          paramToArgMapBuilder.add(candidateParam, onlyArg);
          paramsPool.take(candidateParam);
        } else {
          AmbiguousNamelessArgsError error =
              new AmbiguousNamelessArgsError(function.name(), paramToArgMapBuilder.build(),
                  availableArgs, availableTypedParams);
          messages.log(error);
          return;
        }
      }
    }
  }

}
