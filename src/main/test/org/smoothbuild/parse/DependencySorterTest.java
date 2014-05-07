package org.smoothbuild.parse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.testing.parse.FakeDependency.dependencies;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.module.ImmutableModule;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.parse.err.CycleInCallGraphError;
import org.smoothbuild.testing.message.FakeLoggedMessages;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

public class DependencySorterTest {
  private static final Name NAME1 = name("funcation1");
  private static final Name NAME2 = name("funcation2");
  private static final Name NAME3 = name("funcation3");
  private static final Name NAME4 = name("funcation4");
  private static final Name NAME5 = name("funcation5");
  private static final Name NAME6 = name("funcation6");

  FakeLoggedMessages messages = new FakeLoggedMessages();

  @Test
  public void linearDependency() {
    Map<Name, Set<Dependency>> map = Maps.newHashMap();
    map.put(NAME3, dependencies(NAME4));
    map.put(NAME1, dependencies(NAME2));
    map.put(NAME4, dependencies());
    map.put(NAME2, dependencies(NAME3));

    assertThat(sort(map)).isEqualTo(ImmutableList.of(NAME4, NAME3, NAME2, NAME1));
    messages.assertNoProblems();
  }

  @Test
  public void treeDependency() {
    Map<Name, Set<Dependency>> map = Maps.newHashMap();
    map.put(NAME1, dependencies(NAME2, NAME3));
    map.put(NAME2, dependencies(NAME4));
    map.put(NAME3, dependencies(NAME5));
    map.put(NAME4, dependencies());
    map.put(NAME5, dependencies(NAME6));
    map.put(NAME6, dependencies());

    List<Name> actual = sort(map);

    assertThat(actual).containsSubsequence(NAME4, NAME2, NAME1);
    assertThat(actual).containsSubsequence(NAME6, NAME5, NAME3, NAME1);

    messages.assertNoProblems();
  }

  @Test
  public void simpleRecursionIsLoggedAsError() throws Exception {
    Map<Name, Set<Dependency>> map = Maps.newHashMap();
    map.put(NAME1, dependencies(NAME1));

    sort(map);

    messages.assertContainsOnly(CycleInCallGraphError.class);
  }

  @Test
  public void cycleIsLoggedAsError() throws Exception {
    Map<Name, Set<Dependency>> map = Maps.newHashMap();
    map.put(NAME1, dependencies(NAME2));
    map.put(NAME2, dependencies(NAME3));
    map.put(NAME3, dependencies(NAME1));

    sort(map);

    messages.assertContainsOnly(CycleInCallGraphError.class);
  }

  private List<Name> sort(Map<Name, Set<Dependency>> map) {
    try {
      ImmutableModule builtinModule = new ImmutableModule(Empty.nameFunctionMap());
      return DependencySorter.sortDependencies(builtinModule, map);
    } catch (Message e) {
      messages.log(e);
      return null;
    }
  }
}
