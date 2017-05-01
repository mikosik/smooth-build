package org.smoothbuild.parse;

import static org.smoothbuild.parse.Maybe.error;
import static org.smoothbuild.parse.Maybe.value;

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

import com.google.common.collect.ImmutableList;

/**
 * Sorts functions so all dependencies of each function are placed before that
 * function in returned list. Detects cycles in dependency graph.
 */
public class DependencySorter {
  public static Maybe<List<Name>> sortDependencies(Functions functions,
      Map<Name, FunctionNode> dependencies) {
    Map<Name, FunctionNode> notSorted = new HashMap<>(dependencies);
    Set<Name> availableFunctions = new HashSet<>(functions.names());
    List<Name> sorted = new ArrayList<>(dependencies.size());
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
        FunctionNode next = notSorted.remove(missing.functionName());
        if (next == null) {
          // DependencyCollector made sure that all dependency exists so the
          // only possibility at this point is that missing dependency is on
          // stack and we have cycle in call graph.
          return error(stack.createCycleError());
        } else {
          stack.push(new DependencyStackElem(next));
        }
      }
    }
    return value(ImmutableList.copyOf(sorted));
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

  private static DependencyStackElem removeNext(Map<Name, FunctionNode> dependencies) {
    Iterator<Entry<Name, FunctionNode>> it = dependencies.entrySet().iterator();
    Entry<Name, FunctionNode> element = it.next();
    it.remove();
    return new DependencyStackElem(element.getValue());
  }
}
