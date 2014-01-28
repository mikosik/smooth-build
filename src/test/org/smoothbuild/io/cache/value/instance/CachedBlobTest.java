package org.smoothbuild.io.cache.value.instance;

import static org.smoothbuild.lang.type.STypes.BLOB;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.smoothbuild.io.cache.hash.HashedDb;
import org.testory.Closure;

import com.google.common.hash.HashCode;

public class CachedBlobTest {
  String content = "content";
  InputStream inputStream = mock(InputStream.class);
  HashedDb hashedDb = mock(HashedDb.class);
  HashCode hash = HashCode.fromInt(1);

  CachedBlob cachedBlob;

  @Test
  public void null_hash_is_forbidden() throws Exception {
    when(blobObject(hashedDb, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_file_system_is_forbidden() throws Exception {
    when(blobObject(null, hash));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type() throws Exception {
    given(cachedBlob = new CachedBlob(hashedDb, hash));
    when(cachedBlob.type());
    thenReturned(BLOB);
  }

  @Test
  public void hash() {
    given(cachedBlob = new CachedBlob(hashedDb, hash));
    when(cachedBlob.hash());
    thenReturned(hash);
  }

  @Test
  public void open_input_stream_calls_hashed_db_open_input_stream() throws IOException {
    given(willReturn(inputStream), hashedDb).openInputStream(hash);
    given(cachedBlob = new CachedBlob(hashedDb, hash));
    when(cachedBlob.openInputStream());
    thenReturned(inputStream);
  }

  private static Closure blobObject(final HashedDb hashedDb, final HashCode hash) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new CachedBlob(hashedDb, hash);
      }
    };
  }
}
