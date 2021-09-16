package org.smoothbuild.db.object.obj;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.db.object.spec.base.SpecKind.ARRAY;
import static org.smoothbuild.db.object.spec.base.SpecKind.BLOB;
import static org.smoothbuild.db.object.spec.base.SpecKind.BOOL;
import static org.smoothbuild.db.object.spec.base.SpecKind.CALL;
import static org.smoothbuild.db.object.spec.base.SpecKind.CONST;
import static org.smoothbuild.db.object.spec.base.SpecKind.EARRAY;
import static org.smoothbuild.db.object.spec.base.SpecKind.FIELD_READ;
import static org.smoothbuild.db.object.spec.base.SpecKind.NOTHING;
import static org.smoothbuild.db.object.spec.base.SpecKind.NULL;
import static org.smoothbuild.db.object.spec.base.SpecKind.RECORD;
import static org.smoothbuild.db.object.spec.base.SpecKind.REF;
import static org.smoothbuild.db.object.spec.base.SpecKind.STRING;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.io.IOException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.object.exc.DecodeSpecException;
import org.smoothbuild.db.object.exc.DecodeSpecRootException;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.SpecKind;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.common.AssertCall.ThrownExceptionSubject;

import okio.ByteString;

public class CorruptedSpecTest extends TestingContext {
    @Nested
    class learn {
      @Test
      public void creating_base_spec() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save base spec in HashedDb.
         */
        Hash hash = hash(
            hash(STRING.marker())
        );
        assertThat(hash)
            .isEqualTo(strSpec().hash());
      }

      @Test
      public void creating_array_spec() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save array spec in HashedDb.
         */
        Hash hash = hash(
            hash(ARRAY.marker()),
            hash(strSpec())
        );
        assertThat(hash)
            .isEqualTo(arraySpec(strSpec()).hash());
      }

      @Test
      public void creating_rec_spec() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save rec spec in HashedDb.
         */
        Hash hash = hash(
            hash(RECORD.marker()),
            hash(
                hash(strSpec()),
                hash(strSpec())
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
    class base_spec {
      @Test
      public void blob_with_additional_child_causes_exception() throws Exception {
        do_test_with_additional_child(BLOB);
      }

      @Test
      public void bool_with_additional_child_causes_exception() throws Exception {
        do_test_with_additional_child(BOOL);
      }

      @Test
      public void call_with_additional_child_causes_exception() throws Exception {
        do_test_with_additional_child(CALL);
      }

      @Test
      public void const_with_additional_child_causes_exception() throws Exception {
        do_test_with_additional_child(CONST);
      }

      @Test
      public void earray_with_additional_child_causes_exception() throws Exception {
        do_test_with_additional_child(EARRAY);
      }

      @Test
      public void field_read_with_additional_child_causes_exception() throws Exception {
        do_test_with_additional_child(FIELD_READ);
      }

      @Test
      public void null_with_additional_child_causes_exception() throws Exception {
        do_test_with_additional_child(NULL);
      }

      @Test
      public void nothing_with_additional_child_causes_exception() throws Exception {
        do_test_with_additional_child(NOTHING);
      }

      @Test
      public void string_with_additional_child_causes_exception() throws Exception {
        do_test_with_additional_child(STRING);
      }

      @Test
      public void ref_with_additional_child_causes_exception() throws Exception {
        do_test_with_additional_child(REF);
      }

      private void do_test_with_additional_child(SpecKind kind) throws Exception {
        Hash hash = hash(
            hash(kind.marker()),
            hash("abc")
        );
        assertThatGetSpec(hash)
            .throwsException(new DecodeSpecException(hash, brokenSpecMessage(kind, 2, 1)));
      }
    }

    @Nested
    class rec_spec {
      @Test
      public void without_elements_causes_exception() throws Exception {
        Hash hash =
            hash(
                hash(RECORD.marker())
            );
        assertThatGetSpec(hash)
            .throwsException(new DecodeSpecException(hash, brokenSpecMessage(RECORD, 1, 2)));
      }

      @Test
      public void with_additional_child() throws Exception {
        Hash hash = hash(
            hash(RECORD.marker()),
            hash(
                hash(strSpec()),
                hash(strSpec())
            ),
            hash("corrupted")
        );
        assertThatGetSpec(hash)
            .throwsException(new DecodeSpecRootException(hash, 3));
      }

      @Test
      public void with_elements_not_being_sequence_of_hashes_causes_exception() throws Exception {
        Hash hash =
            hash(
                hash(RECORD.marker()),
                hash("abc")
            );
        assertThatGetSpec(hash)
            .throwsException(new DecodeSpecException(
                hash, "Its specKind == RECORD but reading its item specs caused error."));
      }

      @Test
      public void with_elements_being_array_of_non_spec_causes_exception() throws Exception {
        Hash stringHash = hash(strVal("abc"));
        Hash hash =
            hash(
                hash(RECORD.marker()),
                hash(
                    stringHash
                )
            );
        assertThatGetSpec(hash)
            .throwsException(new DecodeSpecException(hash, elementReadingErrorMessage(0)))
            .withCause(new DecodeSpecException(stringHash));
      }

      @Test
      public void with_corrupted_element_spec_causes_exception() throws Exception {
        Hash notASpecHash = hash("not a spec");
        Hash hash =
            hash(
                hash(RECORD.marker()),
                hash(
                    notASpecHash,
                    hash(strSpec())));
        assertThatGetSpec(hash)
            .throwsException(new DecodeSpecException(hash, elementReadingErrorMessage(0)))
            .withCause(new DecodeSpecException(notASpecHash));
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
            .throwsException(new DecodeSpecException(hash, brokenSpecMessage(ARRAY, 1, 2)));
      }

      @Test
      public void with_additional_child() throws Exception {
        Hash hash = hash(
            hash(ARRAY.marker()),
            hash(strSpec()),
            hash("corrupted")
        );
        assertThatGetSpec(hash)
            .throwsException(new DecodeSpecRootException(hash, 3));
      }

      @Test
      public void with_corrupted_element_spec_causes_exception() throws Exception {
        Hash notASpecHash = hash("not a type");
        Hash hash =
            hash(
                hash(ARRAY.marker()),
                notASpecHash);
        assertThatGetSpec(hash)
            .throwsException(new DecodeSpecException(hash));
      }

      @Test
      public void with_element_spec_being_expr_causes_exception() throws Exception {
        Hash hash = hash(
            hash(ARRAY.marker()),
            hash(constSpec())
        );
        assertThatGetSpec(hash)
            .throwsException(new DecodeSpecException(hash,
                "It is ARRAY Spec which element Spec is CONST but should be Spec of some Val."));
      }
    }

    private ThrownExceptionSubject assertThatGetSpec(Hash hash) {
      return assertCall(() -> objectDb().getSpec(hash));
    }

    private String brokenSpecMessage(SpecKind specKind, int actual, int expected) {
      return "Its specKind == " + specKind + " but its merkle root has " + actual
          + " children when " + expected + " is expected.";
    }

    private String elementReadingErrorMessage(int index) {
      return "Its specKind == RECORD but reading item spec at index " + index + " caused error.";
    }

    private DecodeSpecException illegalSpecMarkerException(Hash hash, int marker) {
      return new DecodeSpecException(hash, "It has illegal SpecKind marker = " + marker + ".");
    }

  protected Hash hash(String string) throws HashedDbException {
    return hashedDb().writeString(string);
  }

  protected Hash hash(boolean value) throws IOException, HashedDbException {
    return hash((byte) (value ? 1 : 0));
  }

  protected Hash hash(byte value) throws IOException, HashedDbException {
    try (HashingBufferedSink sink = hashedDb().sink()) {
      sink.writeByte(value);
      sink.close();
      return sink.hash();
    }
  }

  protected Hash hash(ByteString bytes) throws IOException, HashedDbException {
    try (HashingBufferedSink sink = hashedDb().sink()) {
      sink.write(bytes);
      sink.close();
      return sink.hash();
    }
  }

  protected Hash hash(Obj obj) {
    return obj.hash();
  }

  protected Hash hash(Spec spec) {
    return spec.hash();
  }

  protected Hash hash(Hash... hashes) throws HashedDbException {
    return hashedDb().writeSequence(hashes);
  }
}
