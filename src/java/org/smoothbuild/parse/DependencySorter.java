package org.smoothbuild.parse;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.listen.ErrorMessageException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Sorts functions so all dependencies of each function are placed before that
 * function in returned list. Detects cycles in dependency graph.
 */
public class DependencySorter {
  public static List<Name> sortDependencies(SymbolTable importedFunctions,
      Map<Name, Set<Dependency>> dependenciesOrig) {

    Worker worker = new Worker(importedFunctions, dependenciesOrig);
    worker.work();
    return worker.result();
  }

  private static class Worker {
    private final Map<Name, Set<Dependency>> notSorted;
    private final Set<Name> reachableNames;
    private final List<Name> sorted;
    private final DependencyStack stack;

    public Worker(SymbolTable importedFunctions, Map<Name, Set<Dependency>> dependenciesOrig) {
      this.notSorted = Maps.newHashMap(dependenciesOrig);
      this.reachableNames = reachableNames(importedFunctions);
      this.sorted = Lists.newArrayListWithCapacity(dependenciesOrig.size());
      this.stack = new DependencyStack();
    }

    // TODO remove once importedFunctions.names returns proper type
    private static HashSet<Name> reachableNames(SymbolTable importedFunctions) {
      HashSet<Name> result = Sets.newHashSet();
      for (String name : importedFunctions.names()) {
        result.add(Name.name(name));
      }
      return result;
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
          throw new ErrorMessageException(stack.createCycleError());
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
