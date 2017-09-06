package org.smoothbuild.parse.deps;

import static org.smoothbuild.util.Maybe.error;
import static org.smoothbuild.util.Maybe.value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.parse.AstVisitor;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.CallNode.ParamRefFlag;
import org.smoothbuild.parse.ast.FuncNode;
import org.smoothbuild.util.Lists;
import org.smoothbuild.util.Maybe;

public class SortByDependencies {
  public static Maybe<Ast> sortedByDependencies(Functions functions, Ast ast) {
    Map<Name, FuncNode> nodeMap = ast.nameToFunctionMap();
    Map<Name, FuncNode> notSorted = new HashMap<>(nodeMap);
    Set<Name> availableFunctions = new HashSet<>(functions.names());
    List<Name> sorted = new ArrayList<>(nodeMap.size());
    DependencyStack stack = new DependencyStack();

    while (!notSorted.isEmpty() || !stack.isEmpty()) {
      if (stack.isEmpty()) {
        stack.push(removeNext(notSorted));
      }
      DependencyStackElem stackTop = stack.peek();
      Dependency missing = findUnreachableDependency(
          availableFunctions, sorted, stackTop.dependencies());
      if (missing == null) {
        sorted.add(stack.pop().name());
      } else {
        stackTop.setMissing(missing);
        FuncNode next = notSorted.remove(missing.functionName());
        if (next == null) {
          return error(stack.createCycleError());
        } else {
          stack.push(newStackElem(next));
        }
      }
    }
    return value(new Ast(Lists.map(sorted, n -> nodeMap.get(n))));
  }

  private static Dependency findUnreachableDependency(Set<Name> availableFunctions,
      List<Name> sorted, Set<Dependency> dependencies) {
    for (Dependency dependency : dependencies) {
      Name name = dependency.functionName();
      if (!(sorted.contains(name) || availableFunctions.contains(name))) {
        return dependency;
      }
    }
    return null;
  }

  private static DependencyStackElem removeNext(Map<Name, FuncNode> dependencies) {
    Iterator<Entry<Name, FuncNode>> it = dependencies.entrySet().iterator();
    Entry<Name, FuncNode> element = it.next();
    it.remove();
    return newStackElem(element.getValue());
  }

  private static DependencyStackElem newStackElem(FuncNode func) {
    Set<Dependency> dependencies = new HashSet<>();
    new AstVisitor() {
      public void visitCall(CallNode call) {
        super.visitCall(call);
        if (!call.has(ParamRefFlag.class)) {
          dependencies.add(new Dependency(call.location(), call.name()));
        }
      }
    }.visitFunction(func);
    return new DependencyStackElem(func, dependencies);
  }
}
