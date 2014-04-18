package org.smoothbuild.lang.builtin.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.lang.base.FileBuilder;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.task.exec.NativeApiImpl;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.task.exec.FakeNativeApi;

public class ContentFunctionTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private final NativeApiImpl nativeApi = new FakeNativeApi();

  @Test
  public void content_of_file_is_returned_as_blob() throws Exception {
    FileBuilder builder = objectsDb.fileBuilder();
    builder.setPath(path("some/path"));
    builder.setContent(objectsDb.blob("content"));
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
