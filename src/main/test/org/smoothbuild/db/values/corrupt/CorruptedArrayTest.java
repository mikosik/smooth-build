package org.smoothbuild.db.values.corrupt;

import static com.google.common.collect.Streams.stream;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.db.values.ValuesDbException.corruptedValueException;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.values.ValuesDbException;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.testing.common.ExceptionMatcher;
import org.smoothbuild.util.Lists;

import com.google.common.hash.HashCode;

import okio.ByteString;

public class CorruptedArrayTest extends AbstractCorruptedTestCase {
  private HashCode instanceHash;

  @Test
  public void learning_test_create_array() throws Exception {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth array
     * in HashedDb.
     */
    given(instanceHash =
        hash(
            hash(typesDb.array(typesDb.string())),
            hash(
                hash(
                    hash(typesDb.string()),
                    hash("aaa")
                ),
                hash(
                    hash(typesDb.string()),
                    hash("bbb")
                )
            )));
    when(() -> stream(((Array) valuesDb.get(instanceHash))
        .asIterable(SString.class))
        .map(SString::data)
        .collect(toList()));
    thenReturned(Lists.list("aaa", "bbb"));
  }

  @Test
  public void array_with_data_size_different_than_multiple_of_hash_size_is_corrupted() throws
      Exception {
    for (int i = 0; i <= Hash.size() * 3 + 1; i++) {
      if (i % Hash.size() != 0) {
        run_array_with_data_size_different_than_multiple_of_hash_size_is_corrupted(i);
      }
    }
  }

  private void run_array_with_data_size_different_than_multiple_of_hash_size_is_corrupted(
      int byteCount) throws IOException {
    given(instanceHash =
        hash(
            hash(typesDb.array(typesDb.string())),
            hash(ByteString.of(new byte[byteCount]))
        ));
    ;
    when(() -> ((Array) valuesDb.get(instanceHash)).asIterable(Value.class));
    thenThrown(exception(corruptedValueException(instanceHash, "It is an Array which value stored" +
        " in ValuesDb number of bytes which is not multiple of hash size = 20.")));
  }

  @Test
  public void array_with_one_element_of_wrong_type_is_corrupted() throws IOException {
    given(instanceHash =
        hash(
            hash(typesDb.array(typesDb.string())),
            hash(
                hash(
                    hash(typesDb.string()),
                    hash("aaa")
                ),
                hash(
                    hash(typesDb.bool()),
                    hash(true)
                )
            )));
    when(() -> ((Array) valuesDb.get(instanceHash)).asIterable(SString.class));
    thenThrown(exception(corruptedValueException(instanceHash,
        "It is array with type '[String]' but one of its elements has type 'Bool'")));
  }
}