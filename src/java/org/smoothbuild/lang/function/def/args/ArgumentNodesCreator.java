package org.smoothbuild.lang.function.def.args;

import static org.smoothbuild.lang.function.base.Type.BLOB_SET;
import static org.smoothbuild.lang.function.base.Type.EMPTY_SET;
import static org.smoothbuild.lang.function.base.Type.FILE_SET;
import static org.smoothbuild.lang.function.def.args.Assignment.assignment;
import static org.smoothbuild.message.base.MessageType.FATAL;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.base.Type;
import org.smoothbuild.lang.function.def.BlobSetNode;
import org.smoothbuild.lang.function.def.CachingNode;
import org.smoothbuild.lang.function.def.FileSetNode;
import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.lang.function.def.StringSetNode;
import org.smoothbuild.lang.function.def.args.err.AmbiguousNamelessArgsError;
import org.smoothbuild.lang.function.def.args.err.DuplicateArgNameError;
import org.smoothbuild.lang.function.def.args.err.MissingRequiredArgsError;
import org.smoothbuild.lang.function.def.args.err.TypeMismatchError;
import org.smoothbuild.lang.function.def.args.err.UnknownParamNameError;
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

      AssignmentList assignmentList = new AssignmentList();
      processNamedArguments(assignmentList, namedArgs);
      if (messages.containsProblems()) {
        return null;
      }

      processNamelessArguments(assignmentList);
      if (messages.containsProblems()) {
        return null;
      }

      Set<Param> missingRequiredParams = paramsPool.availableRequiredParams();
      if (missingRequiredParams.size() != 0) {
        messages.report(new MissingRequiredArgsError(codeLocation, function, assignmentList,
            missingRequiredParams));
        return null;
      }
      return createArgumentNodes(assignmentList);
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

    private void processNamedArguments(AssignmentList assignmentList, Collection<Argument> namedArgs) {
      for (Argument argument : namedArgs) {
        if (argument.hasName()) {
          String name = argument.name();
          Param param = paramsPool.takeByName(name);
          Type paramType = param.type();
          if (!paramType.isAssignableFrom(argument.type())) {
            messages.report(new TypeMismatchError(argument, paramType));
          } else {
            assignmentList.add(assignment(param, argument));
          }
        }
      }
    }

    private void processNamelessArguments(AssignmentList assignmentList) {
      ImmutableMap<Type, Set<Argument>> namelessArgs = Argument.filterNameless(allArguments);

      for (Type type : Type.allTypes()) {
        Set<Argument> availableArgs = namelessArgs.get(type);
        int argsSize = availableArgs.size();
        if (0 < argsSize) {
          TypedParamsPool availableTypedParams = paramsPool.availableForType(type);

          if (argsSize == 1 && availableTypedParams.hasCandidate()) {
            Argument onlyArg = availableArgs.iterator().next();
            Param candidateParam = availableTypedParams.candidate();
            assignmentList.add(assignment(candidateParam, onlyArg));
            paramsPool.take(candidateParam);
          } else {
            AmbiguousNamelessArgsError error = new AmbiguousNamelessArgsError(function.name(),
                assignmentList, availableArgs, availableTypedParams);
            messages.report(error);
            return;
          }
        }
      }
    }

    private Map<String, Node> createArgumentNodes(AssignmentList assignments) {
      Builder<String, Node> builder = ImmutableMap.builder();

      for (Assignment assignment : assignments) {
        Node node = argumentNode(assignment);
        builder.put(assignment.assignedName(), node);
      }

      return builder.build();
    }

    private Node argumentNode(Assignment assignment) {
      Type type = assignment.param().type();
      Argument argument = assignment.argument();
      if (argument.type() == EMPTY_SET) {
        if (type == Type.STRING_SET) {
          StringSetNode node = new StringSetNode(Empty.nodeList(), argument.codeLocation());
          return new CachingNode(node);
        } else if (type == FILE_SET) {
          FileSetNode node = new FileSetNode(Empty.nodeList(), argument.codeLocation());
          return new CachingNode(node);
        } else if (type == BLOB_SET) {
          BlobSetNode node = new BlobSetNode(Empty.nodeList(), argument.codeLocation());
          return new CachingNode(node);
        } else {
          throw new ErrorMessageException(new Message(FATAL,
              "Bug in smooth binary: Cannot convert from " + argument.type() + " to " + type + "."));
        }
      } else {
        return argument.node();
      }
    }
  }
}
