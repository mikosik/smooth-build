package org.smoothbuild.io.cache.value;

import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.type.Type.FILE;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.testory.common.Closure;

import com.google.common.hash.HashCode;

public class CachedArrayTest {
  HashedDb hashedDb = new HashedDb(new FakeFileSystem());
  HashCode hash = HashCode.fromInt(33);
  SBlob blob = mock(SBlob.class);

  CachedArray<SFile> cachedFileArray;

  @Test
  public void null_value_db_is_forbidden() {
    when(newCachedArray(null, hash));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_hash_is_forbidden() {
    when(newCachedArray(hashedDb, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type() throws Exception {
    given(cachedFileArray = cachedArray(hashedDb, hash));
    when(cachedFileArray.type());
    thenReturned(FILE);
  }

  @Test
  public void hash_passed_to_constructor_is_returned_from_hash_method() throws Exception {
    given(cachedFileArray = cachedArray(hashedDb, hash));
    when(cachedFileArray.hash());
    thenReturned(hash);
  }

  private Closure newCachedArray(final HashedDb hashedDb, final HashCode hash) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return cachedArray(hashedDb, hash);
      }
    };
  }

  private static CachedArray<SFile> cachedArray(HashedDb hashedDb, HashCode hash) {
    @SuppressWarnings("unchecked")
    ValueReader<SFile> valueReader = mock(ValueReader.class);
    return new CachedArray<SFile>(hashedDb, hash, FILE, valueReader);
  }
}
