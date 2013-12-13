package org.smoothbuild.testing.lang.type;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.type.STypes.FILE;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.util.Streams;
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
    thenReturned(FILE);
  }

  @Test
  public void path_returns_path_passed_to_constructor() {
    given(file = new FakeFile(path));
    when(file.path());
    thenReturned(path);
  }

  @Test
  public void open_output_stream_on_content_returns_data_passed_to_constructor() throws Exception {
    given(file = new FakeFile(path, content));
    when(Streams.inputStreamToString(file.content().openInputStream()));
    thenReturned(content);
  }

  @Test
  public void hash_of_file_is_different_from_hash_of_its_content() throws Exception {
    given(file = new FakeFile(path));
    when(file.content().hash());
    thenReturned(not(equalTo(file.hash())));
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
