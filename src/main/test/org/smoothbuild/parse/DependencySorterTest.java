package org.smoothbuild.parse;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.smoothbuild.parse.DependencySorter.sortDependencies;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.module.ImmutableModule;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.LoggedMessages;
import org.smoothbuild.util.Empty;
import org.testory.Closure;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class DependencySorterTest {
  private Name name1;
  private Name name2;
  private Name name3;
  private Name name4;
  private Name name5;
  private Name name6;

  LoggedMessages messages = new LoggedMessages();
  private HashMap<Name, Set<Dependency>> map;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void linear_dependency() {
    Map<Name, Set<Dependency>> map = Maps.newHashMap();
    map.put(name3, dependencies(name4));
    map.put(name1, dependencies(name2));
    map.put(name4, dependencies());
    map.put(name2, dependencies(name3));

    assertThat(sort(map), contains(name4, name3, name2, name1));
    assertThat(messages, emptyIterable());
  }

  @Test
  public void tree_dependency() {
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
    assertThat(messages, emptyIterable());
  }

  @Test
  public void simple_recursion_is_logged_as_error() throws Exception {
    given(map = new HashMap<>());
    given(map).put(name1, dependencies(name1));
    when($sortDependencies(map));
    thenThrown(ParsingException.class);
  }

  @Test
  public void cycle_is_logged_as_error() throws Exception {
    given(map = new HashMap<>());
    given(map).put(name1, dependencies(name2));
    given(map).put(name2, dependencies(name3));
    given(map).put(name3, dependencies(name1));
    when($sortDependencies(map));
    thenThrown(ParsingException.class);
  }

  private Closure $sortDependencies(final HashMap<Name, Set<Dependency>> map) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return sortDependencies(new ImmutableModule(Empty.nameFunctionMap()), map);
      }
    };
  }

  private List<Name> sort(Map<Name, Set<Dependency>> map) {
    try {
      return sortDependencies(new ImmutableModule(Empty.nameFunctionMap()), map);
    } catch (Message e) {
      messages.log(e);
      return null;
    }
  }

  private static Set<Dependency> dependencies(Name... names) {
    Set<Dependency> result = Sets.newHashSet();
    for (Name name : names) {
      result.add(new Dependency(codeLocation(1), name));
    }
    return result;
  }
}
