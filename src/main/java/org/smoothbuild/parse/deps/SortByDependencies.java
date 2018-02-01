package org.smoothbuild.parse.deps;

import static org.smoothbuild.util.Maybe.error;
import static org.smoothbuild.util.Maybe.invokeWrap;
import static org.smoothbuild.util.Maybe.value;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.smoothbuild.util.Lists;
import org.smoothbuild.util.Maybe;

public class SortByDependencies {
  public static Maybe<Ast> functionsSortedByDependencies(Functions functions, Ast ast) {
    Map<Name, FuncNode> nodeMap = ast.nameToFunctionMap();
    Set<Name> globalNames = new HashSet<>(functions.names());
    Maybe<List<Name>> sorted = sortByDependencies(nodeMap, funcNodeToStackElem(), globalNames);
    return invokeWrap(sorted, s -> new Ast(Lists.map(s, n -> nodeMap.get(n))));
  }

  private static Function<FuncNode, StackElem> funcNodeToStackElem() {
    return funcNode -> {
      Set<Named> dependencies = new HashSet<>();
      new AstVisitor() {
        @Override
        public void visitCall(CallNode call) {
          super.visitCall(call);
          dependencies.add(call);
        }
      }.visitFunction(funcNode);
      return new StackElem(funcNode.name(), dependencies);
    };
  }

  private static <T extends Named> Maybe<List<Name>> sortByDependencies(
      Map<Name, T> nodeMap, Function<T, StackElem> newStackElem, Set<Name> globalNames) {
    Map<Name, T> notSorted = new HashMap<>(nodeMap);
    List<Name> sorted = new ArrayList<>(nodeMap.size());
    DependencyStack stack = new DependencyStack();
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
