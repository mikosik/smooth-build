package org.smoothbuild.db.objects.instance;

import static org.smoothbuild.lang.base.STypes.FILE;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.instance.ArrayObject;
import org.smoothbuild.db.objects.read.ReadValue;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.testory.Closure;

import com.google.common.hash.HashCode;

public class ArrayObjectTest {
  HashedDb hashedDb = new HashedDb(new FakeFileSystem());
  HashCode hash = HashCode.fromInt(33);
  SBlob blob = mock(SBlob.class);

  ArrayObject<SFile> cachedFileArray;

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
    given(cachedFileArray = arrayObject(hashedDb, hash));
    when(cachedFileArray.type());
    thenReturned(FILE);
  }

  @Test
  public void hash_passed_to_constructor_is_returned_from_hash_method() throws Exception {
    given(cachedFileArray = arrayObject(hashedDb, hash));
    when(cachedFileArray.hash());
    thenReturned(hash);
  }

  private Closure newCachedArray(final HashedDb hashedDb, final HashCode hash) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return arrayObject(hashedDb, hash);
      }
    };
  }

  private static ArrayObject<SFile> arrayObject(HashedDb hashedDb, HashCode hash) {
    @SuppressWarnings("unchecked")
    ReadValue<SFile> valueReader = mock(ReadValue.class);
    return new ArrayObject<SFile>(hashedDb, hash, FILE, valueReader);
  }
}
