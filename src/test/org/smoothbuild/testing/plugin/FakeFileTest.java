package org.smoothbuild.testing.plugin;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.fs.base.Path.path;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.function.base.Type;
import org.testory.common.Closure;

public class FakeFileTest {
  Path path = path("my/path");
  Path path2 = path("my/path2");
  String content = "content";
  String content2 = "content2";
  FakeFile file;

  @Test
  public void null_path_is_forbidden() throws Exception {
    when($fakeFile(null));
    thenThrown(NullPointerException.class);
  }

  private Closure $fakeFile(final Path path) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new FakeFile(path);
      }
    };
  }

  @Test
  public void type() throws Exception {
    given(file = new FakeFile(path));
    when(file.type());
    thenReturned(Type.FILE);
  }

  @Test
  public void path_returns_path_passed_to_constructor() {
    given(file = new FakeFile(path));
    when(file.path());
    thenReturned(path);
  }

  @Test
  public void hash_of_files_with_different_paths_is_different() throws Exception {
    given(file = new FakeFile(path));
    when(file.hash());
    thenReturned(not(equalTo(new FakeFile(path2).hash())));
  }

  @Test
  public void hash_of_files_with_different_content_is_different() throws Exception {
    given(file = new FakeFile(path, content));
    when(file.hash());
    thenReturned(not(equalTo(new FakeFile(path, content2).hash())));
  }
}
