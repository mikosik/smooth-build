package org.smoothbuild.parse;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.smoothbuild.testing.parse.FakeDependency.dependencies;
import static org.testory.Testory.givenTest;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.module.ImmutableModule;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.parse.err.CycleInCallGraphError;
import org.smoothbuild.testing.message.FakeLoggedMessages;
import org.smoothbuild.util.Empty;

import com.google.common.collect.Maps;

public class DependencySorterTest {
  private Name name1;
  private Name name2;
  private Name name3;
  private Name name4;
  private Name name5;
  private Name name6;

  FakeLoggedMessages messages = new FakeLoggedMessages();

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void linearDependency() {
    Map<Name, Set<Dependency>> map = Maps.newHashMap();
    map.put(name3, dependencies(name4));
    map.put(name1, dependencies(name2));
    map.put(name4, dependencies());
    map.put(name2, dependencies(name3));

    assertThat(sort(map), contains(name4, name3, name2, name1));
    messages.assertNoProblems();
  }

  @Test
  public void treeDependency() {
    Map<Name, Set<Dependency>> map = Maps.newHashMap();
    map.put(name1, dependencies(name2, name3));
    map.put(name2, dependencies(name4));
    map.put(name3, dependencies(name5));
    map.put(name4, dependencies());
    map.put(name5, dependencies(name6));
    map.put(name6, dependencies());

    List<Name> actual = sort(map);

    assertTrue(actual.indexOf(name4) < actual.indexOf(name2));
    assertTrue(actual.indexOf(name2) < actual.indexOf(name1));

    assertTrue(actual.indexOf(name6) < actual.indexOf(name5));
    assertTrue(actual.indexOf(name5) < actual.indexOf(name3));
    assertTrue(actual.indexOf(name3) < actual.indexOf(name1));

    messages.assertNoProblems();
  }

  @Test
  public void simpleRecursionIsLoggedAsError() throws Exception {
    Map<Name, Set<Dependency>> map = Maps.newHashMap();
    map.put(name1, dependencies(name1));

    sort(map);

    messages.assertContainsOnly(CycleInCallGraphError.class);
  }

  @Test
  public void cycleIsLoggedAsError() throws Exception {
    Map<Name, Set<Dependency>> map = Maps.newHashMap();
    map.put(name1, dependencies(name2));
    map.put(name2, dependencies(name3));
    map.put(name3, dependencies(name1));

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
