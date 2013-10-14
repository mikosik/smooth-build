package org.smoothbuild.function.def.args;

import static org.smoothbuild.function.base.Type.EMPTY_SET;
import static org.smoothbuild.function.base.Type.FILE;
import static org.smoothbuild.function.base.Type.FILE_SET;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.function.base.Type.STRING_SET;
import static org.smoothbuild.function.def.args.Assignment.assignment;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.function.def.FileSetNode;
import org.smoothbuild.function.def.StringSetNode;
import org.smoothbuild.function.def.args.err.AmbiguousNamelessArgsError;
import org.smoothbuild.function.def.args.err.DuplicateArgNameError;
import org.smoothbuild.function.def.args.err.MissingRequiredArgsError;
import org.smoothbuild.function.def.args.err.TypeMismatchError;
import org.smoothbuild.function.def.args.err.UnknownParamNameError;
import org.smoothbuild.function.def.args.err.VoidArgError;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Sets;

public class ArgumentNodesCreator {

  public Map<String, DefinitionNode> createArgumentNodes(CodeLocation codeLocation,
      MessageGroup messages, Function function, Collection<Argument> arguments) {
    Map<String, DefinitionNode> result = new Worker(codeLocation, messages, function, arguments)
        .convert();
    messages.failIfContainsErrors();
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

    public Map<String, DefinitionNode> convert() {
      ImmutableList<Argument> namedArgs = Argument.filterNamed(allArguments);

      detectDuplicatedAndUnknownArgNames(namedArgs);
      if (messages.containsErrors()) {
        return null;
      }

      detectVoidArguments();
      if (messages.containsErrors()) {
        return null;
      }

      AssignmentList assignmentList = new AssignmentList();
      processNamedArguments(assignmentList, namedArgs);
      if (messages.containsErrors()) {
        return null;
      }

      processNamelessArguments(assignmentList);
      if (messages.containsErrors()) {
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

    private void detectVoidArguments() {
      for (Argument argument : allArguments) {
        if (argument.type() == Type.VOID) {
          messages.report(new VoidArgError(argument));
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

    // EMPTY_SET has to be handled after STRING_SET and FILE_SET.
    // This way assignment algorithm is more powerful. Consider smooth function
    // named 'myFunction' that has exactly two parameters:
    //
    // - 'files' parameter of type FILE_SET
    // - 'strings' parameter of type STRING_SET
    //
    // arguments in the following call:
    //
    // myFunction([], [ "stringA", "stringB" ])
    //
    // can be correctly assigned to proper parameters only when we handle second
    // argument (of type STRING_SET) first (assigning it to 'files' parameter)
    // and then assigning empty list to the only left parameter which is 'files'
    // (of FILE_SET type). If we start with empty list argument we would not be
    // able to decide to which parameter it should be assigned to as both
    // (STRING_SET and FILE_SET) can be assigned from empty set.
    private static final ImmutableList<Type> TYPES_ORDER = ImmutableList.of(STRING, FILE,
        STRING_SET, FILE_SET, EMPTY_SET);

    private void processNamelessArguments(AssignmentList assignmentList) {
      ImmutableMap<Type, Set<Argument>> namelessArgs = Argument.filterNameless(allArguments);

      for (Type type : TYPES_ORDER) {
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

    private Map<String, DefinitionNode> createArgumentNodes(AssignmentList assignments) {
      Builder<String, DefinitionNode> builder = ImmutableMap.builder();

      for (Assignment assignment : assignments) {
        DefinitionNode node = argumentNode(assignment);
        builder.put(assignment.assignedName(), node);
      }

      return builder.build();
    }

    private DefinitionNode argumentNode(Assignment assignment) {
      Type type = assignment.param().type();
      Argument argument = assignment.argument();
      if (argument.type() == Type.EMPTY_SET) {
        if (type == Type.STRING_SET) {
          return new StringSetNode(Empty.definitionNodeList(), argument.codeLocation());
        } else if (type == Type.FILE_SET) {
          return new FileSetNode(Empty.definitionNodeList(), argument.codeLocation());
        } else {
          throw new RuntimeException("Cannot convert from " + argument.type() + " to " + type + ".");
        }
      } else {
        return argument.definitionNode();
      }
    }
  }
}
