package org.smoothbuild.lang.function.def.args;

import static org.smoothbuild.lang.type.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.type.STypes.EMPTY_ARRAY;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.type.STypes.STRING_ARRAY;
import static org.smoothbuild.lang.type.STypes.allTypes;
import static org.smoothbuild.message.base.MessageType.FATAL;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.convert.Conversions;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.def.ArrayNode;
import org.smoothbuild.lang.function.def.CachingNode;
import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.lang.function.def.args.err.AmbiguousNamelessArgsError;
import org.smoothbuild.lang.function.def.args.err.DuplicateArgNameError;
import org.smoothbuild.lang.function.def.args.err.MissingRequiredArgsError;
import org.smoothbuild.lang.function.def.args.err.TypeMismatchError;
import org.smoothbuild.lang.function.def.args.err.UnknownParamNameError;
import org.smoothbuild.lang.type.SType;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Sets;

public class ArgumentNodesCreator {

  public Map<String, Node> createArgumentNodes(CodeLocation codeLocation, MessageGroup messages,
      Function function, Collection<Argument> arguments) {
    Map<String, Node> result = new Worker(codeLocation, messages, function, arguments).convert();
    messages.failIfContainsProblems();
    return result;
  }

  private static class Worker {
    private final CodeLocation codeLocation;
    private final MessageGroup messages;
    private final Function function;
    private final ParamsPool paramsPool;
    private final Collection<Argument> allArguments;

    public Worker(CodeLocation codeLocation, MessageGroup messages, Function function,
        Collection<Argument> arguments) {
      this.codeLocation = codeLocation;
      this.messages = messages;
      this.function = function;
      this.paramsPool = new ParamsPool(function.params());
      this.allArguments = arguments;
    }

    public Map<String, Node> convert() {
      ImmutableList<Argument> namedArgs = Argument.filterNamed(allArguments);

      detectDuplicatedAndUnknownArgNames(namedArgs);
      if (messages.containsProblems()) {
        return null;
      }

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
        messages.report(new MissingRequiredArgsError(codeLocation, function, paramToArgMapBuilder,
            missingRequiredParams));
        return null;
      }
      return createArgumentNodes(paramToArgMapBuilder.build());
    }

    private void detectDuplicatedAndUnknownArgNames(Collection<Argument> namedArgs) {
      Set<String> names = Sets.newHashSet();
      for (Argument argument : namedArgs) {
        if (argument.hasName()) {
          String name = argument.name();
          if (names.contains(name)) {
            messages.report(new DuplicateArgNameError(argument));
          } else if (!function.params().containsKey(name)) {
            messages.report(new UnknownParamNameError(function.name(), argument));
          } else {
            names.add(name);
          }
        }
      }
    }

    private void processNamedArguments(ParamToArgMapBuilder paramToArgMapBuilder,
        Collection<Argument> namedArgs) {
      for (Argument argument : namedArgs) {
        if (argument.hasName()) {
          String name = argument.name();
          Param param = paramsPool.takeByName(name);
          SType<?> paramType = param.type();
          if (!Conversions.canConvert(argument.type(), paramType)) {
            messages.report(new TypeMismatchError(argument, paramType));
          } else {
            paramToArgMapBuilder.add(param, argument);
          }
        }
      }
    }

    private void processNamelessArguments(ParamToArgMapBuilder paramToArgMapBuilder) {
      ImmutableMap<SType<?>, Set<Argument>> namelessArgs = Argument.filterNameless(allArguments);

      for (SType<?> type : allTypes()) {
        Set<Argument> availableArgs = namelessArgs.get(type);
        int argsSize = availableArgs.size();
        if (0 < argsSize) {
          TypedParamsPool availableTypedParams = paramsPool.availableForType(type);

          if (argsSize == 1 && availableTypedParams.hasCandidate()) {
            Argument onlyArg = availableArgs.iterator().next();
            Param candidateParam = availableTypedParams.candidate();
            paramToArgMapBuilder.add(candidateParam, onlyArg);
            paramsPool.take(candidateParam);
          } else {
            AmbiguousNamelessArgsError error =
                new AmbiguousNamelessArgsError(function.name(), paramToArgMapBuilder.build(),
                    availableArgs, availableTypedParams);
            messages.report(error);
            return;
          }
        }
      }
    }

    private Map<String, Node> createArgumentNodes(ImmutableMap<Param, Argument> paramToArgMap) {
      Builder<String, Node> builder = ImmutableMap.builder();

      for (Map.Entry<Param, Argument> entry : paramToArgMap.entrySet()) {
        Param param = entry.getKey();
        Argument arg = entry.getValue();
        Node node = argumentNode(param, arg);
        builder.put(param.name(), node);
      }

      return builder.build();
    }

    private Node argumentNode(Param param, Argument arg) {
      SType<?> paramType = param.type();
      if (arg.type() == EMPTY_ARRAY) {
        if (paramType == STRING_ARRAY) {
          ArrayNode node = new ArrayNode(STRING_ARRAY, Empty.nodeList(), arg.codeLocation());
          return new CachingNode(node);
        } else if (paramType == FILE_ARRAY) {
          ArrayNode node = new ArrayNode(FILE_ARRAY, Empty.nodeList(), arg.codeLocation());
          return new CachingNode(node);
        } else if (paramType == BLOB_ARRAY) {
          ArrayNode node = new ArrayNode(BLOB_ARRAY, Empty.nodeList(), arg.codeLocation());
          return new CachingNode(node);
        } else {
          throw new ErrorMessageException(new Message(FATAL,
              "Bug in smooth binary: Cannot convert from " + arg.type() + " to " + paramType + "."));
        }
      } else {
        return arg.node();
      }
    }
  }
}
