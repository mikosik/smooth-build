package org.smoothbuild.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.smoothbuild.cli.Console;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;

import com.google.common.collect.ImmutableList;

/**
 * Sorts functions so all dependencies of each function are placed before that
 * function in returned list. Detects cycles in dependency graph.
 */
public class DependencySorter {
  public static List<Name> sortDependencies(Functions functions,
      Map<Name, Set<Dependency>> dependencies, Console console) {
    Worker worker = new Worker(functions, dependencies, console);
    worker.work();
    return worker.result();
  }

  private static class Worker {
    private final Map<Name, Set<Dependency>> notSorted;
    private final Set<Name> reachableNames;
    private final List<Name> sorted;
    private final DependencyStack stack;
    private final Console console;

    public Worker(Functions functions, Map<Name, Set<Dependency>> dependencies, Console console) {
      this.console = console;
      this.notSorted = new HashMap<>(dependencies);
      this.reachableNames = new HashSet<>(functions.names());
      this.sorted = new ArrayList<>(dependencies.size());
      this.stack = new DependencyStack();
    }

    public void work() {
      while (!notSorted.isEmpty() || !stack.isEmpty()) {
        if (stack.isEmpty()) {
          stack.push(removeNext(notSorted));
        }
        processStackTop();
      }
    }

    private void processStackTop() {
      DependencyStackElem stackTop = stack.peek();
      Dependency missing = findUnreachableDependency(reachableNames, stackTop.dependencies());
      if (missing == null) {
        addStackTopToSorted();
      } else {
        stackTop.setMissing(missing);
        Set<Dependency> next = notSorted.remove(missing.functionName());
        if (next == null) {
          // DependencyCollector made sure that all dependency exists so the
          // only possibility at this point is that missing dependency is on
          // stack and we have cycle in call graph.
          stack.reportAndThrowCycleException(console);
        } else {
          stack.push(new DependencyStackElem(missing.functionName(), next));
        }
      }
    }

    private void addStackTopToSorted() {
      Name name = stack.pop().name();
      sorted.add(name);
      reachableNames.add(name);
    }

    public List<Name> result() {
      return ImmutableList.copyOf(sorted);
    }

    private Dependency findUnreachableDependency(Set<Name> reachableNames,
        Set<Dependency> dependencies) {
      for (Dependency dependency : dependencies) {
        if (!reachableNames.contains(dependency.functionName())) {
          return dependency;
        }
      }
      return null;
    }

    private DependencyStackElem removeNext(Map<Name, Set<Dependency>> dependencies) {
      Iterator<Entry<Name, Set<Dependency>>> it = dependencies.entrySet().iterator();
      Entry<Name, Set<Dependency>> element = it.next();
      it.remove();
      return new DependencyStackElem(element.getKey(), element.getValue());
    }
  }
}
