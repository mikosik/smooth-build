package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Arrays.asList;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;
import static org.smoothbuild.lang.parse.ParseError.parseError;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.cli.console.MemoryLogger;
import org.smoothbuild.lang.base.Callable;
import org.smoothbuild.lang.base.Declared;
import org.smoothbuild.lang.base.Definitions;
import org.smoothbuild.lang.parse.ast.ArgNode;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.AstVisitor;
import org.smoothbuild.lang.parse.ast.CallNode;
import org.smoothbuild.lang.parse.ast.CallableNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class AssignArgsToParams {
  public static List<Log> assignArgsToParams(Ast ast, Definitions imported) {
    var logger = new MemoryLogger();
    new AstVisitor(){
      @Override
      public void visitCall(CallNode call) {
        super.visitCall(call);
        List<AParam> parameters = parameters(call, imported, ast.callablesMap());
        var assigned = assigned(call, parameters);
        logger.logAllFrom(assigned);
        if (!assigned.hasProblems()) {
          call.setAssignedArgs(assigned.value());
        }
      }
    }.visitAst(ast);
    return logger.logs();
  }

  public static ImmutableList<AParam> parameters(
      CallNode call, Definitions imported, ImmutableMap<String, CallableNode> callables) {
    String name = call.calledName();
    Declared evaluable = imported.evaluables().get(name);
    if (evaluable != null) {
      return ((Callable) evaluable).parameterSignatures()
          .stream()
          .map(p -> new AParam(p.name().get(), p.defaultValueType().isPresent()))
          .collect(toImmutableList());
    }
    CallableNode node = callables.get(name);
    if (node != null) {
      return node.params()
          .stream()
          .map(p -> new AParam(p.name(), p.defaultValue().isPresent()))
          .collect(toImmutableList());
    }
    throw new RuntimeException("Couldn't find `" + call.calledName() + "` function.");
  }

  private static Maybe<List<ArgNode>> assigned(
      CallNode call, List<AParam> parameters) {
    var result = new Maybe<List<ArgNode>>();
    var nameToIndex = nameToIndex(parameters);
    ImmutableList<ArgNode> positionalArguments = leadingPositionalArguments(call);

    result.logAll(findPositionalArgumentAfterNamedArgumentError(call));
    result.logAll(findTooManyPositionalArgumentsError(call, positionalArguments, parameters));
    result.logAll(findUnknownParameterNameErrors(call, nameToIndex));
    result.logAll(findDuplicateAssignmentErrors(call, positionalArguments, parameters));
    if (result.hasProblems()) {
      return result;
    }

    List<ArgNode> assignedArgs = assignedArgs(call, parameters, nameToIndex);
    result.logAll(findUnassignedParametersWithoutDefaultValuesErrors(
        call, assignedArgs, parameters));
    result.setValue(assignedArgs);
    return result;
  }

  private static ImmutableList<ArgNode> leadingPositionalArguments(CallNode call) {
    return call.args()
        .stream()
        .takeWhile(not(ArgNode::declaresName))
        .collect(toImmutableList());
  }

  private static List<ArgNode> assignedArgs(
      CallNode call, List<AParam> parameters, Map<String, Integer> nameToIndex) {
    List<ArgNode> args = call.args();
    List<ArgNode> assignedList = asList(new ArgNode[parameters.size()]);
    for (int i = 0; i < args.size(); i++) {
      ArgNode arg = args.get(i);
      if (arg.declaresName()) {
        assignedList.set(nameToIndex.get(arg.name()), arg);
      } else {
        assignedList.set(i, arg);
      }
    }
    return assignedList;
  }

  private static List<Log> findPositionalArgumentAfterNamedArgumentError(CallNode call) {
    return call.args()
        .stream()
        .dropWhile(not(ArgNode::declaresName))
        .dropWhile(ArgNode::declaresName)
        .map(a -> parseError(a, inCallToPrefix(call)
            + "Positional arguments must be placed before named arguments."))
        .collect(toList());
  }

  private static List<Log> findTooManyPositionalArgumentsError(
      CallNode call, List<ArgNode> positionalArguments, List<AParam> parameters) {
    if (parameters.size() < positionalArguments.size()) {
      return List.of(parseError(call, inCallToPrefix(call) + "Too many positional arguments."));
    }
    return List.of();
  }

  private static List<Log> findUnknownParameterNameErrors(
      CallNode call, Map<String, Integer> nameToIndex) {
    return call.args()
        .stream()
        .filter(ArgNode::declaresName)
        .filter(a -> !nameToIndex.containsKey(a.name()))
        .map(a -> parseError(a, inCallToPrefix(call) + "Unknown parameter " + a.q() + "."))
        .collect(toList());
  }

  private static List<Log> findDuplicateAssignmentErrors(
      CallNode call, List<ArgNode> positionalArguments, List<AParam> parameters) {
    var names = new HashSet<String>();
    parameters.stream()
        .limit(positionalArguments.size())
        .forEach(p -> names.add(p.name()));
    return call.args()
        .stream()
        .filter(ArgNode::declaresName)
        .filter(a -> !names.add(a.name()))
        .map(a -> parseError(a, inCallToPrefix(call) + a.q() + " is already assigned."))
        .collect(toList());
  }

  private static List<Log> findUnassignedParametersWithoutDefaultValuesErrors(CallNode call,
      List<ArgNode> assignedList, List<AParam> parameters) {
    return range(0, assignedList.size())
        .filter(i -> assignedList.get(i) == null)
        .mapToObj(parameters::get)
        .filter(p -> !p.hasDefault())
        .map(p -> parameterMustBeSpecifiedError(call, p))
        .collect(toList());
  }

  private static Log parameterMustBeSpecifiedError(CallNode call, AParam param) {
    return parseError(call, inCallToPrefix(call) + "Parameter `" + param.name() +
        "` must be specified.");
  }

  private static Map<String, Integer> nameToIndex(List<AParam> parameters) {
    return range(0, parameters.size())
        .boxed()
        .collect(toMap(i -> parameters.get(i).name(), i -> i));
  }

  private static String inCallToPrefix(CallNode call) {
    return "In call to `" + call.calledName() + "`: ";
  }

  private record AParam(String name, boolean hasDefault) {
    @Override
    public String toString() {
      return "`" + name + "`";
    }
  }
}
