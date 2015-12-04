package org.smoothbuild.parse;

import static org.smoothbuild.lang.message.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.mock;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.cli.Console;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.lang.module.ImmutableModule;
import org.smoothbuild.util.Empty;

import com.google.common.collect.ImmutableList;

public class DependencySorterTest {
  private Name name1;
  private Name name2;
  private Name name3;
  private Name name4;
  private Name name5;
  private Name name6;

  private HashMap<Name, Set<Dependency>> map;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void linear_dependency() {
    given(map = new HashMap<>());
    given(map).put(name3, dependencies(name4));
    given(map).put(name1, dependencies(name2));
    given(map).put(name4, dependencies());
    given(map).put(name2, dependencies(name3));
    when(() -> sortDependencies(map));
    thenReturned(ImmutableList.of(name4, name3, name2, name1));
  }

  @Test
  public void tree_dependency() {
    given(map = new HashMap<>());
    given(map).put(name1, dependencies(name2, name3));
    given(map).put(name2, dependencies(name4));
    given(map).put(name3, dependencies(name5));
    given(map).put(name4, dependencies());
    given(map).put(name5, dependencies(name6));
    given(map).put(name6, dependencies());

    List<Name> list = sortDependencies(map);
    then(list.indexOf(name4) < list.indexOf(name2));
    then(list.indexOf(name2) < list.indexOf(name1));

    then(list.indexOf(name6) < list.indexOf(name5));
    then(list.indexOf(name5) < list.indexOf(name3));
    then(list.indexOf(name3) < list.indexOf(name1));
  }

  @Test
  public void simple_recursion_is_logged_as_error() throws Exception {
    given(map = new HashMap<>());
    given(map).put(name1, dependencies(name1));
    when(() -> sortDependencies(map));
    thenThrown(ParsingException.class);
  }

  @Test
  public void cycle_is_logged_as_error() throws Exception {
    given(map = new HashMap<>());
    given(map).put(name1, dependencies(name2));
    given(map).put(name2, dependencies(name3));
    given(map).put(name3, dependencies(name1));
    when(() -> sortDependencies(map));
    thenThrown(ParsingException.class);
  }

  private static List<Name> sortDependencies(final HashMap<Name, Set<Dependency>> map) {
    return DependencySorter.sortDependencies(new ImmutableModule(Empty.nameFunctionMap()), map,
        mock(Console.class));
  }

  private static Set<Dependency> dependencies(Name... names) {
    Set<Dependency> result = new HashSet<>();
    for (Name name : names) {
      result.add(new Dependency(codeLocation(1), name));
    }
    return result;
  }
}
