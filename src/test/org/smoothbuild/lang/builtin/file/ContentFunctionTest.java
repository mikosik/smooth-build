package org.smoothbuild.lang.builtin.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.io.cache.value.build.FileBuilder;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.testing.lang.type.FakeBlob;
import org.smoothbuild.testing.task.exec.FakeNativeApi;

public class ContentFunctionTest {
  FakeNativeApi nativeApi = new FakeNativeApi();

  @Test
  public void content_of_file_is_returned_as_blob() throws Exception {
    FileBuilder builder = nativeApi.fileBuilder();
    builder.setPath(path("some/path"));
    FakeBlob blob = new FakeBlob();
    builder.setContent(blob);
    SFile file = builder.build();

    SBlob actual = ContentFunction.execute(nativeApi, params(file));
    assertThat(actual).isSameAs(file.content());
  }

  private static ContentFunction.Parameters params(final SFile file) {
    return new ContentFunction.Parameters() {
      @Override
      public SFile file() {
        return file;
      }
    };
  }
}
