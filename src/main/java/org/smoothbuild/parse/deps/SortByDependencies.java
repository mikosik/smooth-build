package org.smoothbuild.parse.deps;

import static org.smoothbuild.util.Collections.toMap;
import static org.smoothbuild.util.Maybe.error;
import static org.smoothbuild.util.Maybe.value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.parse.AstVisitor;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.parse.ast.Named;
import org.smoothbuild.util.Maybe;

public class SortByDependencies {
  public static Maybe<List<Name>> sortByDependencies(Functions functions, Ast ast) {
    List<FuncNode> funcs = ast.funcs();
    Set<Name> globalNames = new HashSet<>(functions.names());
    Maybe<List<Name>> sorted = sortByDependencies(
        "Function call graph", funcs, funcToStackElem(), globalNames);
    return sorted;
  }

  private static Function<FuncNode, StackElem> funcToStackElem() {
    return func -> {
      Set<Named> dependencies = new HashSet<>();
      new AstVisitor() {
        @Override
        public void visitCall(CallNode call) {
          super.visitCall(call);
          dependencies.add(call);
        }
      }.visitFunc(func);
      return new StackElem(func.name(), dependencies);
    };
  }

  private static <T extends Named> Maybe<List<Name>> sortByDependencies(String stackName,
      List<T> nodes, Function<T, StackElem> newStackElem, Set<Name> globalNames) {
    Map<Name, T> notSorted = toMap(nodes, Named::name);
    List<Name> sorted = new ArrayList<>(nodes.size());
    DependencyStack stack = new DependencyStack(stackName);
    while (!notSorted.isEmpty() || !stack.isEmpty()) {
      if (stack.isEmpty()) {
        T named = notSorted.remove(notSorted.keySet().iterator().next());
        stack.push(newStackElem.apply(named));
      }
      StackElem topElem = stack.peek();
      Named missing = findNotYetProcessedDependency(globalNames, sorted, topElem.dependencies());
      if (missing == null) {
        sorted.add(stack.pop().name());
      } else {
        topElem.setMissing(missing);
        T next = notSorted.remove(missing.name());
        if (next == null) {
          return error(stack.createCycleError());
        } else {
          stack.push(newStackElem.apply(next));
        }
      }
    }
    return value(sorted);
  }

  private static Named findNotYetProcessedDependency(
      Set<Name> globalNames, List<Name> sorted, Set<Named> dependencies) {
    for (Named dependency : dependencies) {
      Name name = dependency.name();
      if (!(sorted.contains(name) || globalNames.contains(name))) {
        return dependency;
      }
    }
    return null;
  }
}
