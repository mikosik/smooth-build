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
import org.smoothbuild.parse.def.err.AmbiguousImplicitArgsError;
import org.smoothbuild.parse.def.err.DuplicateArgNameError;
import org.smoothbuild.parse.def.err.TypeMismatchError;
import org.smoothbuild.parse.def.err.UnknownParamNameError;
import org.smoothbuild.parse.def.err.VoidArgError;
import org.smoothbuild.problem.DetectingErrorsProblemsListener;
import org.smoothbuild.problem.ProblemsListener;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class ArgumentNodesCreator {

  public static Map<String, DefinitionNode> createArgumentNodes(ProblemsListener problems,
      Function function, Collection<Argument> arguments) {
    return new Worker(problems, function, arguments).convert();
  }

  private static class Worker {
    private final DetectingErrorsProblemsListener problems;
    private final Function function;
    private final ParamsPool paramsPool;
    private final Collection<Argument> allArguments;

    public Worker(ProblemsListener problemsListener, Function function,
        Collection<Argument> arguments) {
      this.problems = new DetectingErrorsProblemsListener(problemsListener);
      this.function = function;
      this.paramsPool = new ParamsPool(function.params());
      this.allArguments = arguments;
    }

    public Map<String, DefinitionNode> convert() {
      ImmutableList<Argument> explicitArgs = Argument.filterExplicit(allArguments);

      detectDuplicatedAndUnknownArgNames(explicitArgs);
      if (problems.errorDetected()) {
        return null;
      }

      detectVoidArguments();
      if (problems.errorDetected()) {
        return null;
      }

      AssignmentList assignmentList = new AssignmentList();
      processExplicitArguments(assignmentList, explicitArgs);
      if (problems.errorDetected()) {
        return null;
      }

      processImplicitArguments(assignmentList);
      if (problems.errorDetected()) {
        return null;
      }

      return assignmentList.createNodesMap();
    }

    private void detectDuplicatedAndUnknownArgNames(Collection<Argument> explicitArgs) {
      Set<String> explicitNames = Sets.newHashSet();
      for (Argument argument : explicitArgs) {
        if (argument.isExplicit()) {
          String name = argument.name();
          if (explicitNames.contains(name)) {
            problems.report(new DuplicateArgNameError(argument));
          } else if (!function.params().containsKey(name)) {
            problems.report(new UnknownParamNameError(function.name(), argument));
          } else {
            explicitNames.add(name);
          }
        }
      }
    }

    private void detectVoidArguments() {
      for (Argument argument : allArguments) {
        if (argument.type() == Type.VOID) {
          problems.report(new VoidArgError(argument));
        }
      }
    }

    private void processExplicitArguments(AssignmentList assignmentList,
        Collection<Argument> explicitArgs) {
      for (Argument argument : explicitArgs) {
        if (argument.isExplicit()) {
          String name = argument.name();
          Param param = paramsPool.takeByName(name);
          Type paramType = param.type();
          if (!paramType.isAssignableFrom(argument.type())) {
            problems.report(new TypeMismatchError(argument, paramType));
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

    private void processImplicitArguments(AssignmentList assignmentList) {
      ImmutableMap<Type, Set<Argument>> implicitArgs = Argument.filterImplicit(allArguments);

      for (Type type : TYPES_ORDER) {
        Set<Argument> availableArgs = implicitArgs.get(type);
        int argsSize = availableArgs.size();
        if (0 < argsSize) {
          Set<Param> availableParams = paramsPool.availableForType(type);
          int paramsSize = availableParams.size();

          if (argsSize == 1 && paramsSize == 1) {
            Argument onlyArg = availableArgs.iterator().next();
            Param onlyParam = availableParams.iterator().next();
            // DefinitionNode node = convert(onlyParam.type(),
            // onlyArg.definitionNode());
            assignmentList.add(onlyParam, onlyArg);
            paramsPool.take(onlyParam);
          } else {
            AmbiguousImplicitArgsError error = new AmbiguousImplicitArgsError(availableArgs,
                availableParams);
            problems.report(error);
            return;
          }
        }
      }
    }
  }
}
