package org.smoothbuild.parse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.smoothbuild.message.MessageListener;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Sorts functions so all dependencies of each function are placed before that
 * function in returned list. Detects cycles in dependency graph.
 */
public class DependencySorter {
  public static List<String> sortDependencies(MessageListener messages,
      SymbolTable importedFunctions, Map<String, Set<Dependency>> dependenciesOrig) {

    Worker worker = new Worker(messages, importedFunctions, dependenciesOrig);
    worker.work();
    return worker.result();
  }

  private static class Worker {
    private final MessageListener messages;
    private final HashMap<String, Set<Dependency>> notSorted;
    private final HashSet<String> reachableNames;
    private final List<String> sorted;
    private final DependencyStack stack;

    public Worker(MessageListener messages, SymbolTable importedFunctions,
        Map<String, Set<Dependency>> dependenciesOrig) {
      this.messages = messages;
      this.notSorted = Maps.newHashMap(dependenciesOrig);
      this.reachableNames = Sets.newHashSet(importedFunctions.names());
      this.sorted = Lists.newArrayListWithCapacity(dependenciesOrig.size());
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
          reportCycle();
        } else {
          stack.push(new DependencyStackElem(missing.functionName(), next));
        }
      }
    }

    private void reportCycle() {
      // DependencyCollector made sure that all dependency exists so the
      // only possibility at this point is that missing dependency is on
      // stack and we have cycle in call graph.
      messages.report(stack.createCycleError());
      addStackTopToSorted();
    }

    private void addStackTopToSorted() {
      String name = stack.pop().name();
      sorted.add(name);
      reachableNames.add(name);
    }

    public List<String> result() {
      return ImmutableList.copyOf(sorted);
    }

    private Dependency findUnreachableDependency(Set<String> reachableNames,
        Set<Dependency> dependencies) {
      for (Dependency dependency : dependencies) {
        if (!reachableNames.contains(dependency.functionName())) {
          return dependency;
        }
      }
      return null;
    }

    private DependencyStackElem removeNext(Map<String, Set<Dependency>> dependencies) {
      Iterator<Entry<String, Set<Dependency>>> it = dependencies.entrySet().iterator();
      Entry<String, Set<Dependency>> element = it.next();
      it.remove();
      return new DependencyStackElem(element.getKey(), element.getValue());
    }
  }
}
