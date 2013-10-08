package org.smoothbuild.parse.def;

import static org.smoothbuild.function.base.Type.EMPTY_SET;
import static org.smoothbuild.function.base.Type.FILE;
import static org.smoothbuild.function.base.Type.FILE_SET;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.function.base.Type.STRING_SET;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.message.listen.DetectingErrorsMessageListener;
import org.smoothbuild.message.listen.MessageListener;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.parse.def.err.AmbiguousNamelessArgsError;
import org.smoothbuild.parse.def.err.DuplicateArgNameError;
import org.smoothbuild.parse.def.err.MissingRequiredArgsError;
import org.smoothbuild.parse.def.err.TypeMismatchError;
import org.smoothbuild.parse.def.err.UnknownParamNameError;
import org.smoothbuild.parse.def.err.VoidArgError;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class ArgumentNodesCreator {

  public static Map<String, DefinitionNode> createArgumentNodes(CodeLocation codeLocation,
      MessageListener messages, Function function, Collection<Argument> arguments) {
    return new Worker(codeLocation, messages, function, arguments).convert();
  }

  private static class Worker {
    private final CodeLocation codeLocation;
    private final DetectingErrorsMessageListener messages;
    private final Function function;
    private final ParamsPool paramsPool;
    private final Collection<Argument> allArguments;

    public Worker(CodeLocation codeLocation, MessageListener messageListener, Function function,
        Collection<Argument> arguments) {
      this.codeLocation = codeLocation;
      this.messages = new DetectingErrorsMessageListener(messageListener);
      this.function = function;
      this.paramsPool = new ParamsPool(function.params());
      this.allArguments = arguments;
    }

    public Map<String, DefinitionNode> convert() {
      ImmutableList<Argument> namedArgs = Argument.filterNamed(allArguments);

      detectDuplicatedAndUnknownArgNames(namedArgs);
      if (messages.errorDetected()) {
        return null;
      }

      detectVoidArguments();
      if (messages.errorDetected()) {
        return null;
      }

      AssignmentList assignmentList = new AssignmentList();
      processNamedArguments(assignmentList, namedArgs);
      if (messages.errorDetected()) {
        return null;
      }

      processNamelessArguments(assignmentList);
      if (messages.errorDetected()) {
        return null;
      }

      Set<Param> missingRequiredParams = paramsPool.availableRequiredParams();
      if (missingRequiredParams.size() != 0) {
        messages.report(new MissingRequiredArgsError(codeLocation, function, assignmentList,
            missingRequiredParams));
        return null;
      }
      return assignmentList.createNodesMap();
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
            assignmentList.add(param, argument);
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
            // DefinitionNode node = convert(onlyParam.type(),
            // onlyArg.definitionNode());
            assignmentList.add(candidateParam, onlyArg);
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
  }
}
