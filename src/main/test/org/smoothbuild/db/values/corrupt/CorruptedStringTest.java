package org.smoothbuild.db.values.corrupt;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.value.SString;

import com.google.common.hash.HashCode;

public class CorruptedStringTest extends AbstractCorruptedTestCase {
  private HashCode instanceHash;

  @Test
  public void learning_test_create_string() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth bool
     * in HashedDb.
     */
    given(instanceHash =
        hash(
            hash(typesDb.string()),
            hash("aaa")));
    when(() -> ((SString) valuesDb.get(instanceHash)).data());
    thenReturned("aaa");
  }
}