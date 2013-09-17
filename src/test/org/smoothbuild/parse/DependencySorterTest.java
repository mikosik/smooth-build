package org.smoothbuild.parse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.smoothbuild.testing.parse.TestDependency.dependencies;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.mockito.Matchers;
import org.smoothbuild.parse.err.CycleInCallGraphError;
import org.smoothbuild.problem.MessageListener;
import org.smoothbuild.testing.parse.TestImportedFunctions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

public class DependencySorterTest {
  private static final String NAME1 = "funcation1";
  private static final String NAME2 = "funcation2";
  private static final String NAME3 = "funcation3";
  private static final String NAME4 = "funcation4";
  private static final String NAME5 = "funcation5";
  private static final String NAME6 = "funcation6";

  MessageListener messageListener = mock(MessageListener.class);
  SymbolTable importedFunctions = new TestImportedFunctions();

  @Test
  public void linearDependency() {
    Map<String, Set<Dependency>> map = Maps.newHashMap();
    map.put(NAME3, dependencies(NAME4));
    map.put(NAME1, dependencies(NAME2));
    map.put(NAME4, dependencies());
    map.put(NAME2, dependencies(NAME3));

    assertThat(sort(map)).isEqualTo(ImmutableList.of(NAME4, NAME3, NAME2, NAME1));
    verifyZeroInteractions(messageListener);
  }

  @Test
  public void treeDependency() {
    Map<String, Set<Dependency>> map = Maps.newHashMap();
    map.put(NAME1, dependencies(NAME2, NAME3));
    map.put(NAME2, dependencies(NAME4));
    map.put(NAME3, dependencies(NAME5));
    map.put(NAME4, dependencies());
    map.put(NAME5, dependencies(NAME6));
    map.put(NAME6, dependencies());

    List<String> actual = sort(map);

    assertThat(actual).containsSubsequence(NAME4, NAME2, NAME1);
    assertThat(actual).containsSubsequence(NAME6, NAME5, NAME3, NAME1);

    verifyZeroInteractions(messageListener);
  }

  @Test
  public void simpleRecursionIsReported() throws Exception {
    Map<String, Set<Dependency>> map = Maps.newHashMap();
    map.put(NAME1, dependencies(NAME1));

    sort(map);

    verify(messageListener).report(Matchers.isA(CycleInCallGraphError.class));
  }

  @Test
  public void cycleIsReported() throws Exception {
    Map<String, Set<Dependency>> map = Maps.newHashMap();
    map.put(NAME1, dependencies(NAME2));
    map.put(NAME2, dependencies(NAME3));
    map.put(NAME3, dependencies(NAME1));

    sort(map);

    verify(messageListener).report(Matchers.isA(CycleInCallGraphError.class));
  }

  private List<String> sort(Map<String, Set<Dependency>> map) {
    return DependencySorter.sortDependencies(messageListener, importedFunctions, map);
  }
}
