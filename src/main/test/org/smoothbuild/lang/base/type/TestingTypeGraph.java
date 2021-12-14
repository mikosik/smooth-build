package org.smoothbuild.lang.base.type;

import static com.google.common.collect.Multimaps.newSetMultimap;
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
import org.smoothbuild.lang.base.type.api.Type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

public record TestingTypeGraph<T extends Type>(ImmutableMultimap<T, T> edges) {
  public TestingTypeGraph(Multimap<T, T> edges) {
    this(ImmutableMultimap.copyOf(edges));
  }

  // building graph edges

  public static <T extends Type> TestingTypeGraph<T> buildGraph(
      ImmutableList<T> types, int levelCount, TestingT<T> testingT) {
    TestingTypeGraph<T> graph = baseGraph(types, testingT);
    for (int i = 0; i < levelCount; i++) {
      graph = levelUp(graph, testingT);
    }
    return graph;
  }

  private static <T extends Type> TestingTypeGraph<T> baseGraph(
      ImmutableList<T> types, TestingT<T> testingT) {
    Multimap<T, T> graph = newMultimap();
    types.forEach(t -> graph.put(testingT.nothing(), t));
    types.forEach(t -> graph.put(t, testingT.any()));
    return new TestingTypeGraph<>(graph);
  }

  private static <T extends Type> TestingTypeGraph<T> levelUp(TestingTypeGraph<T> graph,
      TestingT<T> testingT) {
    Multimap<T, T> newDimension = newMultimap();

    // arrays
    for (Entry<T, T> entry : graph.edges().entries()) {
      var lower = entry.getKey();
      var upper = entry.getValue();
      newDimension.put(testingT.array(lower), testingT.array(upper));
    }
    newDimension.put(testingT.nothing(), testingT.array(testingT.nothing()));
    newDimension.put(testingT.array(testingT.any()), testingT.any());

    // one param funcs
    Set<T> allTypes = graph.allTypes();

    for (T type : allTypes) {
      for (Entry<T, T> entry : graph.edges().entries()) {
        var lower = entry.getKey();
        var upper = entry.getValue();
        newDimension.put(testingT.func(lower, list(type)), testingT.func(upper, list(type)));
        newDimension.put(testingT.func(type, list(upper)), testingT.func(type, list(lower)));

      }
    }
    newDimension.put(testingT.nothing(), testingT.func(testingT.nothing(), list(testingT.any())));
    newDimension.put(testingT.func(testingT.any(), list(testingT.nothing())), testingT.any());

    newDimension.putAll(graph.edges());
    return new TestingTypeGraph(newDimension);
  }

  private Set<T> allTypes() {
    HashSet<T> types = new HashSet<>();
    for (Entry<T, T> entry : edges.entries()) {
      types.add(entry.getKey());
      types.add(entry.getValue());
    }
    return types;
  }

  // building test cases

  public Collection<Arguments> buildTestCases(T rootNode) {
    ArrayList<T> sorted = typesSortedTopologically(rootNode);
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

  private ArrayList<T> typesSortedTopologically(T rootNode) {
    var incomingEdgesCount = new HashMap<T, AtomicInteger>();
    for (Entry<T, T> entry : edges.entries()) {
      incomingEdgesCount.computeIfAbsent(entry.getValue(), e -> new AtomicInteger()).
          incrementAndGet();
    }

    var queue = new LinkedList<T>();
    var sorted = new ArrayList<T>(incomingEdgesCount.size() + 1);
    queue.addLast(rootNode);
    while (!queue.isEmpty()) {
      T current = queue.removeFirst();
      sorted.add(current);
      for (T edgeEnd : edges.get(current)) {
        AtomicInteger count = incomingEdgesCount.get(edgeEnd);
        if (count.decrementAndGet() == 0) {
          queue.addLast(edgeEnd);
        }
      }
    }
    return sorted;
  }

  private Arguments buildTestCase(int i, int j, int[][] intEdges, ArrayList<T> indexToType) {
    if (i == j) {
      Type type = indexToType.get(i);
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

  private int[][] buildIntEdges(ArrayList<T> sortedTs) {
    var typeToIndex = typeToIndex(sortedTs);

    int[][] intEdges = new int[sortedTs.size()][];
    for (int i = 0; i < sortedTs.size(); i++) {
      var type = sortedTs.get(i);
      intEdges[i] = edges.get(type).stream().mapToInt(typeToIndex::get).toArray();
    }
    return intEdges;
  }

  private static <T extends Type> HashMap<T, Integer> typeToIndex(ArrayList<T> sortedTs) {
    HashMap<T, Integer> typeToInteger = new HashMap<>();
    for (int i = 0; i < sortedTs.size(); i++) {
      typeToInteger.put(sortedTs.get(i), i);
    }
    return typeToInteger;
  }

  private static <T extends Type> Multimap<T, T> newMultimap() {
    return newSetMultimap(new HashMap<>(), HashSet::new);
  }

  public TestingTypeGraph<T> inverse() {
    return new TestingTypeGraph<>(edges.inverse());
  }
}
