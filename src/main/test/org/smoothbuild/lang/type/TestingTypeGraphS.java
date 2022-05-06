package org.smoothbuild.lang.type;

import static com.google.common.collect.Multimaps.newSetMultimap;
import static org.smoothbuild.lang.type.impl.VarSetS.varSetS;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.params.provider.Arguments;
import org.smoothbuild.lang.type.impl.TypeS;
import org.smoothbuild.testing.type.TestingTS;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

public record TestingTypeGraphS(ImmutableMultimap<TypeS, TypeS> edges) {
  public TestingTypeGraphS(Multimap<TypeS, TypeS> edges) {
    this(ImmutableMultimap.copyOf(edges));
  }

  // building graph edges

  public static TestingTypeGraphS buildGraph(
      ImmutableList<TypeS> types, int levelCount, TestingTS testingT) {
    TestingTypeGraphS graph = baseGraph(types, testingT);
    for (int i = 0; i < levelCount; i++) {
      graph = levelUp(graph, testingT);
    }
    return graph;
  }

  private static TestingTypeGraphS baseGraph(ImmutableList<TypeS> types, TestingTS testingT) {
    Multimap<TypeS, TypeS> graph = newMultimap();
    types.forEach(t -> graph.put(testingT.nothing(), t));
    types.forEach(t -> graph.put(t, testingT.any()));
    return new TestingTypeGraphS(graph);
  }

  private static TestingTypeGraphS levelUp(TestingTypeGraphS graph, TestingTS testingT) {
    Multimap<TypeS, TypeS> newDimension = newMultimap();

    // arrays
    for (Entry<TypeS, TypeS> entry : graph.edges().entries()) {
      var lower = entry.getKey();
      var upper = entry.getValue();
      newDimension.put(testingT.array(lower), testingT.array(upper));
    }
    newDimension.put(testingT.nothing(), testingT.array(testingT.nothing()));
    newDimension.put(testingT.array(testingT.any()), testingT.any());

    // tuples
    if (testingT.isTupleSupported()) {
      for (Entry<TypeS, TypeS> entry : graph.edges().entries()) {
        var lower = entry.getKey();
        var upper = entry.getValue();
        newDimension.put(testingT.tuple(list(lower)), testingT.tuple(list(upper)));
      }
      newDimension.put(testingT.nothing(), testingT.tuple(list(testingT.nothing())));
      newDimension.put(testingT.tuple(list(testingT.any())), testingT.any());
    }

    // one param funcs
    Set<TypeS> allTypes = graph.allTypes();

    var vs = varSetS();
    for (TypeS type : allTypes) {
      for (Entry<TypeS, TypeS> entry : graph.edges().entries()) {
        var lower = entry.getKey();
        var upper = entry.getValue();
        newDimension.put(testingT.func(vs, lower, list(type)), testingT.func(vs, upper, list(type)));
        newDimension.put(testingT.func(vs, type, list(upper)), testingT.func(vs, type, list(lower)));

      }
    }
    newDimension.put(testingT.nothing(), testingT.func(vs, testingT.nothing(), list(testingT.any())));
    newDimension.put(testingT.func(vs, testingT.any(), list(testingT.nothing())), testingT.any());

    newDimension.putAll(graph.edges());
    return new TestingTypeGraphS(newDimension);
  }

  private Set<TypeS> allTypes() {
    HashSet<TypeS> types = new HashSet<>();
    for (Entry<TypeS, TypeS> entry : edges.entries()) {
      types.add(entry.getKey());
      types.add(entry.getValue());
    }
    return types;
  }

  // building test cases

  public Collection<Arguments> buildTestCases(TypeS rootNode) {
    ArrayList<TypeS> sorted = typesSortedTopologically(rootNode);
    int typesCount = sorted.size();
    int[][]intEdges = buildIntEdges(sorted);
    List<Arguments> result = new ArrayList<>(typesCount * typesCount);
    for (int i = 0; i < typesCount; i++) {
      for (int j = i; j < typesCount; j++) {
        result.add(buildTestCase(i, j, intEdges, sorted));
      }
    }
    return result;
  }

  private ArrayList<TypeS> typesSortedTopologically(TypeS rootNode) {
    var incomingEdgesCount = new HashMap<TypeS, AtomicInteger>();
    for (Entry<TypeS, TypeS> entry : edges.entries()) {
      incomingEdgesCount.computeIfAbsent(entry.getValue(), e -> new AtomicInteger()).
          incrementAndGet();
    }

    var queue = new LinkedList<TypeS>();
    var sorted = new ArrayList<TypeS>(incomingEdgesCount.size() + 1);
    queue.addLast(rootNode);
    while (!queue.isEmpty()) {
      TypeS current = queue.removeFirst();
      sorted.add(current);
      for (TypeS edgeEnd : edges.get(current)) {
        AtomicInteger count = incomingEdgesCount.get(edgeEnd);
        if (count.decrementAndGet() == 0) {
          queue.addLast(edgeEnd);
        }
      }
    }
    return sorted;
  }

  private Arguments buildTestCase(int i, int j, int[][] intEdges, ArrayList<TypeS> indexToType) {
    if (i == j) {
      TypeS type = indexToType.get(i);
      return Arguments.of(type, type, type);
    }
    // This method could be made faster by keeping separate `colors` array for `i` and `j` types.
    // `colors` array for `i` could be calculated once for the whole graph in outer for loop
    // in method that call this method.
    // For now time spent on graph building in negligible compared to time spent in test execution.
    int[] colors = new int[intEdges.length];
    colors[i] = 1;
    colors[j] = 2;
    int current = i;
    while (true) {
      int currentColor = colors[current];
      if (currentColor == 3) {
        return Arguments.of(indexToType.get(i), indexToType.get(j), indexToType.get(current));
      } else {
        for (int edgeEnd : intEdges[current]) {
          int endColor = colors[edgeEnd];
          if (endColor != 3 && endColor != currentColor) {
            colors[edgeEnd] = currentColor + endColor;
          }
        }
      }
      current++;
    }
  }

  private int[][] buildIntEdges(ArrayList<TypeS> sortedTs) {
    var typeToIndex = typeToIndex(sortedTs);

    int[][] intEdges = new int[sortedTs.size()][];
    for (int i = 0; i < sortedTs.size(); i++) {
      var type = sortedTs.get(i);
      intEdges[i] = edges.get(type).stream().mapToInt(typeToIndex::get).toArray();
    }
    return intEdges;
  }

  private static HashMap<TypeS, Integer> typeToIndex(ArrayList<TypeS> sortedTs) {
    HashMap<TypeS, Integer> typeToInteger = new HashMap<>();
    for (int i = 0; i < sortedTs.size(); i++) {
      typeToInteger.put(sortedTs.get(i), i);
    }
    return typeToInteger;
  }

  private static <T extends TypeS> Multimap<T, T> newMultimap() {
    return newSetMultimap(new HashMap<>(), HashSet::new);
  }

  public TestingTypeGraphS inverse() {
    return new TestingTypeGraphS(edges.inverse());
  }
}
