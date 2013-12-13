package org.smoothbuild.io.cache.value.instance;

import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.type.STypes.FILE;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.InputStream;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.testory.common.Closure;

import com.google.common.hash.HashCode;

public class CachedFileTest {
  Path path = Path.path("my/file");
  CachedBlob content = mock(CachedBlob.class);
  HashCode hash = HashCode.fromInt(33);
  InputStream inputStream;

  CachedFile cachedFile;

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
  public void type() throws Exception {
    given(cachedFile = new CachedFile(path, content, hash));
    when(cachedFile.type());
    thenReturned(FILE);
  }

  @Test
  public void path() {
    given(cachedFile = new CachedFile(path, content, hash));
    when(cachedFile.path());
    thenReturned(path);
  }

  @Test
  public void content() {
    given(cachedFile = new CachedFile(path, content, hash));
    when(cachedFile.content());
    thenReturned(content);
  }

  @Test
  public void hash() throws Exception {
    given(cachedFile = new CachedFile(path, content, hash));
    when(cachedFile.hash());
    thenReturned(hash);
  }

  private static Closure fileObject(final Path path, final CachedBlob content, final HashCode hash) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new CachedFile(path, content, hash);
      }
    };
  }
}
