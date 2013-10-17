package org.smoothbuild.object;

import static org.mockito.Mockito.mock;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.InputStream;

import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.fs.base.Path;
import org.testory.common.Closure;

import com.google.common.hash.HashCode;

public class FileObjectTest {
  Path path = Path.path("my/file");
  BlobObject content = mock(BlobObject.class);
  HashCode hash = HashCode.fromInt(33);

  FileObject fileObject;

  @Test
  public void null_hash_is_forbidden() throws Exception {
    when(fileObject(path, content, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_content_is_forbidden() throws Exception {
    when(fileObject(path, null, hash));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_path_is_forbidden() throws Exception {
    when(fileObject(null, content, hash));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void path() {
    given(fileObject = new FileObject(path, content, hash));
    when(fileObject.path());
    thenReturned(path);
  }

  @Test
  public void hash() throws Exception {
    given(fileObject = new FileObject(path, content, hash));
    when(fileObject.hash());
    thenReturned(hash);
  }

  @Test
  public void opeInputStream() throws Exception {
    InputStream inputStream = mock(InputStream.class);
    Mockito.when(content.openInputStream()).thenReturn(inputStream);

    given(fileObject = new FileObject(path, content, hash));
    when(fileObject.openInputStream());
    thenReturned(inputStream);
  }

  private static Closure fileObject(final Path path, final BlobObject content, final HashCode hash) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new FileObject(path, content, hash);
      }
    };
  }
}
