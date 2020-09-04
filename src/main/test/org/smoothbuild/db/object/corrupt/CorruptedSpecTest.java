package org.smoothbuild.db.object.corrupt;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.db.object.spec.SpecKind.ARRAY;
import static org.smoothbuild.db.object.spec.SpecKind.BLOB;
import static org.smoothbuild.db.object.spec.SpecKind.BOOL;
import static org.smoothbuild.db.object.spec.SpecKind.NOTHING;
import static org.smoothbuild.db.object.spec.SpecKind.STRING;
import static org.smoothbuild.db.object.spec.SpecKind.TUPLE;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.CannotDecodeSpecException;
import org.smoothbuild.db.object.spec.SpecKind;
import org.smoothbuild.testing.common.AssertCall.ThrownExceptionSubject;

public class CorruptedSpecTest extends AbstractCorruptedTestCase {
  @Nested
  class learn {
    @Test
    public void creating_string_spec() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save basic spec in HashedDb.
       */
      Hash hash = hash(
          hash(STRING.marker())
      );
      assertThat(hash)
          .isEqualTo(stringSpec().hash());
    }

    @Test
    public void creating_array_spec() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save array spec in HashedDb.
       */
      Hash hash = hash(
          hash(ARRAY.marker()),
          hash(stringSpec())
      );
      assertThat(hash)
          .isEqualTo(arraySpec(stringSpec()).hash());
    }

    @Test
    public void creating_tuple_spec() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save tuple spec in HashedDb.
       */
      Hash hash = hash(
          hash(TUPLE.marker()),
          hash(
              hash(stringSpec()),
              hash(stringSpec())
          )
      );
      assertThat(hash)
          .isEqualTo(personSpec().hash());
    }
  }

  @Nested
  class illegal_spec_marker {
    @Test
    public void causes_exception() throws Exception {
      Hash hash = hash(
          hash((byte) 99)
      );
      assertThatGetSpec(hash)
          .throwsException(illegalSpecMarkerException(hash, 99));
    }

    @Test
    public void with_additional_child_causes_exception() throws Exception {
      Hash hash = hash(
          hash((byte) 99),
          hash("corrupted")
      );
      assertThatGetSpec(hash)
          .throwsException(illegalSpecMarkerException(hash, 99));
    }
  }

  @Nested
  class basic_spec {
    @Test
    public void blob_with_additional_child_causes_exception() throws Exception {
      do_test_with_additional_child(BLOB);
    }

    @Test
    public void bool_with_additional_child_causes_exception() throws Exception {
      do_test_with_additional_child(BOOL);
    }

    @Test
    public void nothing_with_additional_child_causes_exception() throws Exception {
      do_test_with_additional_child(NOTHING);
    }

    @Test
    public void string_with_additional_child_causes_exception() throws Exception {
      do_test_with_additional_child(STRING);
    }

    private void do_test_with_additional_child(SpecKind kind) throws Exception {
      Hash hash = hash(
          hash(kind.marker()),
          hash("abc")
      );
      assertThatGetSpec(hash)
          .throwsException(new CannotDecodeSpecException(hash, brokenSpecMessage(kind, 2, 1)));
    }
  }

  @Nested
  class tuple_spec {
    @Test
    public void without_elements_causes_exception() throws Exception {
      Hash hash =
          hash(
              hash(TUPLE.marker())
          );
      assertThatGetSpec(hash)
          .throwsException(new CannotDecodeSpecException(hash, brokenSpecMessage(TUPLE, 1, 2)));
    }

    @Test
    public void with_additional_child() throws Exception {
      Hash hash = hash(
          hash(TUPLE.marker()),
          hash(
              hash(stringSpec()),
              hash(stringSpec())
          ),
          hash("corrupted")
      );
      assertThatGetSpec(hash)
          .throwsException(new CannotDecodeSpecException(hash));
    }

    @Test
    public void with_elements_not_being_sequence_of_hashes_causes_exception() throws Exception {
      Hash hash =
          hash(
              hash(TUPLE.marker()),
              hash("abc")
          );
      assertThatGetSpec(hash)
          .throwsException(new CannotDecodeSpecException(
              hash, "Its specKind == TUPLE but reading its element specs caused error."));
    }

    @Test
    public void with_elements_being_array_of_non_spec_causes_exception() throws Exception {
      Hash stringHash = hash(string("abc"));
      Hash hash =
          hash(
              hash(TUPLE.marker()),
              hash(
                  stringHash
              )
          );
      assertThatGetSpec(hash)
          .throwsException(new CannotDecodeSpecException(hash, elementReadingErrorMessage(0)))
          .withCause(new CannotDecodeSpecException(stringHash));
    }

    @Test
    public void with_corrupted_element_spec_causes_exception() throws Exception {
      Hash notASpecHash = hash("not a spec");
      Hash hash =
              hash(
                  hash(TUPLE.marker()),
                  hash(
                      notASpecHash,
                      hash(stringSpec())));
      assertThatGetSpec(hash)
          .throwsException(new CannotDecodeSpecException(hash, elementReadingErrorMessage(0)))
          .withCause(new CannotDecodeSpecException(notASpecHash));
    }
  }

  @Nested
  class array_spec {
    @Test
    public void without_element_spec_causes_exception() throws Exception {
      Hash hash =
          hash(
              hash(ARRAY.marker())
          );
      assertThatGetSpec(hash)
          .throwsException(new CannotDecodeSpecException(hash, brokenSpecMessage(ARRAY, 1, 2)));
    }

    @Test
    public void with_additional_child() throws Exception {
      Hash hash = hash(
          hash(ARRAY.marker()),
          hash(stringSpec()),
          hash("corrupted")
      );
      assertThatGetSpec(hash)
          .throwsException(new CannotDecodeSpecException(hash));
    }

    @Test
    public void with_corrupted_element_spec_causes_exception() throws Exception {
      Hash notASpecHash = hash("not a type");
      Hash hash =
          hash(
              hash(ARRAY.marker()),
              notASpecHash);
      assertThatGetSpec(hash)
          .throwsException(new CannotDecodeSpecException(hash));
    }
  }

  private ThrownExceptionSubject assertThatGetSpec(Hash hash) {
    return assertCall(() -> objectDb().getSpec(hash));
  }

  private static String brokenSpecMessage(SpecKind specKind, int actual, int expected) {
    return "Its specKind == " + specKind + " but its merkle root has " + actual
        + " children when " + expected + " is expected.";
  }

  private static String elementReadingErrorMessage(int index) {
    return "Its specKind == TUPLE but reading element spec at index " + index + " caused error.";
  }

  private static CannotDecodeSpecException illegalSpecMarkerException(Hash hash, int marker) {
    return new CannotDecodeSpecException(hash, "It has illegal SpecKind marker = " + marker + ".");
  }
}
