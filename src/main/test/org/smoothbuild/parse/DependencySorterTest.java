package org.smoothbuild.parse;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.lang.message.CodeLocation.codeLocation;
import static org.smoothbuild.parse.DependencySorter.sortDependencies;
import static org.smoothbuild.parse.Maybe.value;
import static org.testory.Testory.given;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;

import com.google.common.base.Predicate;

public class DependencySorterTest {
  private final Name name1 = Name.name("name1");
  private final Name name2 = Name.name("name2");
  private final Name name3 = Name.name("name3");
  private final Name name4 = Name.name("name4");
  private final Name name5 = Name.name("name5");
  private final Name name6 = Name.name("name6");
  private HashMap<Name, Set<Dependency>> map;

  @Test
  public void linear_dependency() {
    given(map = new HashMap<>());
    given(map).put(name3, dependencies(name4));
    given(map).put(name1, dependencies(name2));
    given(map).put(name4, dependencies());
    given(map).put(name2, dependencies(name3));
    when(() -> sortDependencies(new Functions(), map));
    thenReturned(value(asList(name4, name3, name2, name1)));
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

    List<Name> list = sortDependencies(new Functions(), map).value();
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
    when(() -> sortDependencies(new Functions(), map));
    thenReturned((Predicate<Maybe<?>>) maybe -> !maybe.hasValue());
  }

  @Test
  public void cycle_is_logged_as_error() throws Exception {
    given(map = new HashMap<>());
    given(map).put(name1, dependencies(name2));
    given(map).put(name2, dependencies(name3));
    given(map).put(name3, dependencies(name1));
    when(() -> sortDependencies(new Functions(), map));
    thenReturned((Predicate<Maybe<?>>) maybe -> !maybe.hasValue());
  }

  private static Set<Dependency> dependencies(Name... names) {
    return stream(names).map((name) -> new Dependency(codeLocation(1), name)).collect(toSet());
  }
}
