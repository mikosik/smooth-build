package org.smoothbuild.lang.object.corrupt;

import static com.google.common.collect.Streams.stream;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.testing.common.ExceptionMatcher.exception;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.DecodingHashSequenceException;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.SString;
import org.smoothbuild.lang.object.db.ObjectDbException;
import org.smoothbuild.util.Lists;

import okio.ByteString;

public class CorruptedArrayTest extends AbstractCorruptedTestCase {
  private Hash instanceHash;
  private Hash notHashOfHashSequence;

  @Test
  public void learning_test_create_array() {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth array
     * in HashedDb.
     */
    given(() -> instanceHash =
        hash(
            hash(arrayType(stringType())),
            hash(
                hash(
                    hash(stringType()),
                    hash("aaa")
                ),
                hash(
                    hash(stringType()),
                    hash("bbb")
                )
            )));
    when(() -> stream(((Array) objectDb().get(instanceHash))
        .asIterable(SString.class))
        .map(SString::jValue)
        .collect(toList()));
    thenReturned(Lists.list("aaa", "bbb"));
  }

  @Test
  public void array_with_data_size_different_than_multiple_of_hash_size_is_corrupted() {
    for (int i = 0; i <= Hash.hashesSize() * 3 + 1; i++) {
      if (i % Hash.hashesSize() != 0) {
        run_array_with_data_size_different_than_multiple_of_hash_size_is_corrupted(i);
      }
    }
  }

  private void run_array_with_data_size_different_than_multiple_of_hash_size_is_corrupted(
      int byteCount) {
    given(() -> notHashOfHashSequence = hash(ByteString.of(new byte[byteCount])));
    given(() -> instanceHash =
        hash(
            hash(arrayType(stringType())),
            notHashOfHashSequence
        ));
    when(() -> ((Array) objectDb().get(instanceHash)).asIterable(SObject.class));
    thenThrown(exception(new ObjectDbException(instanceHash,
        new DecodingHashSequenceException(notHashOfHashSequence))));
  }

  @Test
  public void array_with_one_element_of_wrong_type_is_corrupted() {
    given(() -> instanceHash =
        hash(
            hash(arrayType(stringType())),
            hash(
                hash(
                    hash(stringType()),
                    hash("aaa")
                ),
                hash(
                    hash(boolType()),
                    hash(true)
                )
            )));
    when(() -> ((Array) objectDb().get(instanceHash)).asIterable(SString.class));
    thenThrown(exception(new ObjectDbException(instanceHash,
        "It is array with type '[String]' but one of its elements has type 'Bool'")));
  }
}
