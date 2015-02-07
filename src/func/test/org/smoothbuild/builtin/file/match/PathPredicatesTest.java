package org.smoothbuild.builtin.file.match;

import static org.hamcrest.Matchers.sameInstance;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Path.rootPath;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.builtin.util.Predicate;
import org.smoothbuild.io.fs.base.Path;

public class PathPredicatesTest {
  Path path = path("my/path");
  Path path2 = path("different/path");
  Predicate<Path> predicate;

  // hasOnlyOnePart

  @Test
  public void hasOnlyOnePart_returns_true_for_one_part_path() throws Exception {
    given(predicate = PathPredicates.hasOnlyOnePart());
    when(predicate.test(path("some_path")));
    thenReturned(true);
  }

  @Test
  public void hasOnlyOnePart_returns_false_for_two_part_path() throws Exception {
    given(predicate = PathPredicates.hasOnlyOnePart());
    when(predicate.test(path("my/path")));
    thenReturned(false);
  }

  @Test
  public void hasOnlyOnePart_returns_false_for_three_part_path() throws Exception {
    given(predicate = PathPredicates.hasOnlyOnePart());
    when(predicate.test(path("my/path/file")));
    thenReturned(false);
  }

  @Test
  public void hasOnlyOnePart_returns_true_for_root_path() throws Exception {
    given(predicate = PathPredicates.hasOnlyOnePart());
    when(predicate.test(rootPath()));
    thenReturned(true);
  }

  // doubleStarPredicate()

  @Test
  public void doubleStarPredicate_throws_exception() throws Exception {
    given(predicate = PathPredicates.doubleStarPredicate());
    when(predicate).test(path);
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void doubleStarPredicate_alwasy_returns_same_instance() throws Exception {
    when(PathPredicates.doubleStarPredicate());
    thenReturned(sameInstance(PathPredicates.doubleStarPredicate()));
  }
}
