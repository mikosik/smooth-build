package org.smoothbuild.fs.base.match;

import static org.hamcrest.Matchers.sameInstance;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.fs.base.Path.rootPath;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.testory.common.Closure;

import com.google.common.base.Predicate;

public class PathPredicatesTest {
  Path path = path("my/path");
  Path path2 = path("different/path");
  Predicate<Path> predicate;

  // alwaysTrue()

  @Test
  public void alwaysTrue_returns_true() {
    given(predicate = PathPredicates.alwaysTrue());
    when(predicate.apply(null));
    thenReturned(true);
  }

  // isEqual()

  @Test
  public void isEqual_returns_true_for_the_same_path() throws Exception {
    given(predicate = PathPredicates.isEqual(path));
    when(predicate.apply(path));
    thenReturned(true);
  }

  @Test
  public void isEqual_returns_false_for_different_path() throws Exception {
    given(predicate = PathPredicates.isEqual(path));
    when(predicate.apply(path2));
    thenReturned(false);
  }

  @Test
  public void isEqual_apply_returns_false_for_null_argument() throws Exception {
    given(predicate = PathPredicates.isEqual(path));
    when(predicate.apply(null));
    thenReturned(false);
  }

  @Test
  public void isEqual_does_not_accept_null_argument() throws Exception {
    when($isEqual(null));
    thenThrown(NullPointerException.class);
  }

  // hasOnlyOnePart

  @Test
  public void hasOnlyOnePart_returns_true_for_one_part_path() throws Exception {
    given(predicate = PathPredicates.hasOnlyOnePart());
    when(predicate.apply(path("some_path")));
    thenReturned(true);
  }

  @Test
  public void hasOnlyOnePart_returns_false_for_two_part_path() throws Exception {
    given(predicate = PathPredicates.hasOnlyOnePart());
    when(predicate.apply(path("my/path")));
    thenReturned(false);
  }

  @Test
  public void hasOnlyOnePart_returns_false_for_three_part_path() throws Exception {
    given(predicate = PathPredicates.hasOnlyOnePart());
    when(predicate.apply(path("my/path/file")));
    thenReturned(false);
  }

  @Test
  public void hasOnlyOnePart_returns_true_for_root_path() throws Exception {
    given(predicate = PathPredicates.hasOnlyOnePart());
    when(predicate.apply(rootPath()));
    thenReturned(true);
  }

  // doubleStarPredicate()

  @Test
  public void doubleStarPredicate_throws_exception() throws Exception {
    given(predicate = PathPredicates.doubleStarPredicate());
    when(predicate).apply(path);
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void doubleStarPredicate_alwasy_returns_same_instance() throws Exception {
    when(PathPredicates.doubleStarPredicate());
    thenReturned(sameInstance(PathPredicates.doubleStarPredicate()));
  }

  private static Closure $isEqual(final Path path) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return PathPredicates.isEqual(path);
      }
    };
  }
}
