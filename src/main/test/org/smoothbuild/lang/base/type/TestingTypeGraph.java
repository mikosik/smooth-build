package org.smoothbuild.lang.base.type;

import static com.google.common.collect.Multimaps.newSetMultimap;
import static org.smoothbuild.lang.base.type.TestingSTypes.ANY;
import static org.smoothbuild.lang.base.type.TestingSTypes.NOTHING;
import static org.smoothbuild.lang.base.type.TestingSTypes.a;
import static org.smoothbuild.lang.base.type.TestingSTypes.f;

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

public record TestingTypeGraph(ImmutableMultimap<Type, Type> edges) {
  public TestingTypeGraph(Multimap<Type, Type> edges) {
    this(ImmutableMultimap.copyOf(edges));
  }

  // building graph edges

  public static TestingTypeGraph buildGraph(ImmutableList<Type> types, int levelCount) {
    TestingTypeGraph graph = baseGraph(types);
    for (int i = 0; i < levelCount; i++) {
      graph = levelUp(graph);
    }
    return graph;
  }

  private static TestingTypeGraph baseGraph(ImmutableList<Type> types) {
    Multimap<Type, Type> graph = newMultimap();
    types.forEach(t -> graph.put(NOTHING, t));
    types.forEach(t -> graph.put(t, ANY));
    return new TestingTypeGraph(graph);
  }

  private static TestingTypeGraph levelUp(TestingTypeGraph graph) {
    Multimap<Type, Type> newDimension = newMultimap();

    // arrays
    for (Entry<Type, Type> entry : graph.edges().entries()) {
      Type lower = entry.getKey();
      Type upper = entry.getValue();
      newDimension.put(a(lower), a(upper));
    }
    newDimension.put(NOTHING, a(NOTHING));
    newDimension.put(a(ANY), ANY);

    // one parameter functions
    Set<Type> allTypes = graph.allTypes();

    for (Type type : allTypes) {
      for (Entry<Type, Type> entry : graph.edges().entries()) {
        Type lower = entry.getKey();
        Type upper = entry.getValue();
        newDimension.put(f(lower, type), f(upper, type));
        newDimension.put(f(type, upper), f(type, lower));

      }
    }
    newDimension.put(NOTHING, f(NOTHING, ANY));
    newDimension.put(f(ANY, NOTHING), ANY);

    newDimension.putAll(graph.edges());
    return new TestingTypeGraph(newDimension);
  }

  private Set<Type> allTypes() {
    HashSet<Type> types = new HashSet<>();
    for (Entry<Type, Type> entry : edges.entries()) {
      types.add(entry.getKey());
      types.add(entry.getValue());
    }
    return types;
  }

  // building test cases

  public Collection<Arguments> buildTestCases(Type rootNode) {
    ArrayList<Type> sorted = typesSortedTopologically(rootNode);
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

  private ArrayList<Type> typesSortedTopologically(Type rootNode) {
    var incomingEdgesCount = new HashMap<Type, AtomicInteger>();
    for (Entry<Type, Type> entry : edges.entries()) {
      incomingEdgesCount.computeIfAbsent(entry.getValue(), e -> new AtomicInteger()).
          incrementAndGet();
    }

    var queue = new LinkedList<Type>();
    var sorted = new ArrayList<Type>(incomingEdgesCount.size() + 1);
    queue.addLast(rootNode);
    while (!queue.isEmpty()) {
      Type current = queue.removeFirst();
      sorted.add(current);
      for (Type edgeEnd : edges.get(current)) {
        AtomicInteger count = incomingEdgesCount.get(edgeEnd);
        if (count.decrementAndGet() == 0) {
          queue.addLast(edgeEnd);
        }
      }
    }
    return sorted;
  }

  private Arguments buildTestCase(int i, int j, int[][] intEdges, ArrayList<Type> indexToType) {
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

  private int[][] buildIntEdges(ArrayList<Type> sortedTypes) {
    var typeToIndex = typeToIndex(sortedTypes);

    int[][] intEdges = new int[sortedTypes.size()][];
    for (int i = 0; i < sortedTypes.size(); i++) {
      Type type = sortedTypes.get(i);
      intEdges[i] = edges.get(type).stream().mapToInt(typeToIndex::get).toArray();
    }
    return intEdges;
  }

  private static HashMap<Type, Integer> typeToIndex(ArrayList<Type> sortedTypes) {
    HashMap<Type, Integer> typeToInteger = new HashMap<>();
    for (int i = 0; i < sortedTypes.size(); i++) {
      typeToInteger.put(sortedTypes.get(i), i);
    }
    return typeToInteger;
  }

  private static Multimap<Type, Type> newMultimap() {
    return newSetMultimap(new HashMap<>(), HashSet::new);
  }

  public TestingTypeGraph inverse() {
    return new TestingTypeGraph(edges.inverse());
  }
}
