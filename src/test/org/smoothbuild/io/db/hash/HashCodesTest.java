package org.smoothbuild.io.db.hash;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;

import com.google.common.hash.HashCode;

public class HashCodesTest {
  HashCode hash;

  @Test
  public void toPath() {
    given(hash = HashCode.fromInt(0xAB));
    when(HashCodes.toPath(hash));
    thenReturned(Path.path("ab000000"));
  }
}
