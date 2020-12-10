package org.smoothbuild.db.object.base;

import static com.google.common.collect.Streams.stream;
import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.db.object.spec.SpecKind.ANY;
import static org.smoothbuild.db.object.spec.SpecKind.ARRAY;
import static org.smoothbuild.db.object.spec.SpecKind.BLOB;
import static org.smoothbuild.db.object.spec.SpecKind.BOOL;
import static org.smoothbuild.db.object.spec.SpecKind.NOTHING;
import static org.smoothbuild.db.object.spec.SpecKind.STRING;
import static org.smoothbuild.db.object.spec.SpecKind.TUPLE;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.db.hashed.DecodingBooleanException;
import org.smoothbuild.db.hashed.DecodingByteException;
import org.smoothbuild.db.hashed.DecodingHashSequenceException;
import org.smoothbuild.db.hashed.DecodingStringException;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.db.hashed.NoSuchDataException;
import org.smoothbuild.db.object.db.CannotDecodeObjectException;
import org.smoothbuild.db.object.db.CannotDecodeSpecException;
import org.smoothbuild.db.object.db.ObjectDbException;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.db.object.spec.SpecKind;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.common.AssertCall.ThrownExceptionSubject;

import okio.ByteString;

public class CorruptedObjTest extends TestingContext {
  @Nested
  class _obj {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save smooth value
       * in HashedDb.
       */
      Hash objectHash =
          hash(
              hash(stringSpec()),
              hash("aaa"));
      assertThat(((Str) objectDb().get(objectHash)).jValue())
          .isEqualTo("aaa");
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void which_merkle_root_byte_count_is_not_multiple_of_hash_size_is_corrupted(
        int byteCount) throws IOException, HashedDbException {
      Hash objectHash =
          hash(ByteString.of(new byte[byteCount]));
      assertCall(() -> objectDb().get(objectHash))
          .throwsException(new CannotDecodeObjectException(objectHash))
          .withCause(new DecodingHashSequenceException(objectHash));
    }

    @Test
    public void which_spec_is_corrupted_is_corrupted() throws Exception {
      Hash specHash = Hash.of("not a spec");
      Hash objectHash =
          hash(
              specHash,
              hash("aaa"));
      assertCall(() -> objectDb().get(objectHash))
          .throwsException(new CannotDecodeObjectException(objectHash))
          .withCause(new CannotDecodeSpecException(specHash));
    }

    @Test
    public void reading_elements_from_not_stored_object_throws_exception() {
      Hash objectHash = Hash.of(33);
      assertCall(() -> objectDb().get(objectHash))
          .throwsException(new CannotDecodeObjectException(objectHash))
          .withCause(new NoSuchDataException(objectHash));
    }
  }

  @Nested
  class _any {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save smooth any
       * in HashedDb.
       */
      Hash value = Hash.of(1234);
      Hash objectHash =
          hash(
              hash(anySpec()),
              hash(value));
      assertThat(((Any) objectDb().get(objectHash)).wrappedHash())
          .isEqualTo(value);
    }

    @Test
    public void data_hash_pointing_nowhere_is_corrupted() throws Exception {
      Hash dataHash = Hash.of(33);
      Hash objectHash =
          hash(
              hash(anySpec()),
              dataHash);
      assertCall(() -> ((Any) objectDb().get(objectHash)).wrappedHash())
          .throwsException(new CannotDecodeObjectException(objectHash))
          .withCause(new NoSuchDataException(dataHash));
    }
  }

  @Nested
  class _blob {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save smooth blob
       * in HashedDb.
       */
      ByteString byteString = ByteString.of((byte) 1, (byte) 2);
      Hash objectHash =
          hash(
              hash(blobSpec()),
              hash(byteString));
      assertThat(((Blob) objectDb().get(objectHash)).source().readByteString())
          .isEqualTo(byteString);
    }

    @Test
    public void data_hash_pointing_nowhere_is_corrupted() throws Exception {
      Hash dataHash = Hash.of(33);
      Hash objectHash =
          hash(
              hash(blobSpec()),
              dataHash);
      assertCall(() -> ((Blob) objectDb().get(objectHash)).source())
          .throwsException(new CannotDecodeObjectException(objectHash))
          .withCause(new NoSuchDataException(dataHash));
    }
  }

  @Nested
  class _bool {
    /*
     * This test makes sure that other tests in this class use proper scheme to save smooth bool
     * in HashedDb.
     */
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void learning_test(boolean value) throws Exception {
      Hash objectHash =
          hash(
              hash(boolSpec()),
              hash(value));
      assertThat(((Bool) objectDb().get(objectHash)).jValue())
          .isEqualTo(value);
    }

    @Test
    public void empty_bytes_as_data_is_corrupted() throws Exception {
      Hash dataHash = hash(ByteString.of());
      Hash objectHash =
          hash(
              hash(boolSpec()),
              dataHash);
      assertCall(() -> ((Bool) objectDb().get(objectHash)).jValue())
          .throwsException(new CannotDecodeObjectException(objectHash))
          .withCause(new DecodingBooleanException(dataHash, new DecodingByteException(dataHash)));
    }

    @Test
    public void more_than_one_byte_as_data_is_corrupted() throws Exception {
      Hash dataHash = hash(ByteString.of((byte) 0, (byte) 0));
      Hash objectHash =
          hash(
              hash(boolSpec()),
              dataHash);
      assertCall(() -> ((Bool) objectDb().get(objectHash)).jValue())
          .throwsException(new CannotDecodeObjectException(objectHash))
          .withCause(new DecodingBooleanException(dataHash, new DecodingByteException(dataHash)));
    }

    @ParameterizedTest
    @ArgumentsSource(AllByteValuesExceptZeroAndOneProvider.class)
    public void one_byte_data_not_equal_zero_nor_one_is_corrupted(byte value)
        throws Exception {
      Hash dataHash = hash(ByteString.of(value));
      Hash objectHash =
          hash(
              hash(boolSpec()),
              dataHash);
      assertCall(() -> ((Bool) objectDb().get(objectHash)).jValue())
          .throwsException(new CannotDecodeObjectException(objectHash))
          .withCause(new DecodingBooleanException(dataHash));
    }
  }

  private static class AllByteValuesExceptZeroAndOneProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return IntStream.rangeClosed(-128, 127)
          .filter(v -> v != 0 && v != 1)
          .boxed()
          .map(Integer::byteValue)
          .map(Arguments::of);
    }
  }

  @Nested
  class _nothing {
    @Test
    public void learning_test() throws Exception {
      Hash objectHash =
          hash(
              hash(nothingSpec()),
              hash("aaa"));
      assertCall(() -> ((Str) objectDb().get(objectHash)).jValue())
          .throwsException(new ObjectDbException("Cannot create java object for 'NOTHING' spec."));
    }
  }

  @Nested
  class _array {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save smooth array
       * in HashedDb.
       */
      Hash objectHash =
          hash(
              hash(arraySpec(stringSpec())),
              hash(
                  hash(
                      hash(stringSpec()),
                      hash("aaa")
                  ),
                  hash(
                      hash(stringSpec()),
                      hash("bbb")
                  )
              ));
      List<String> strings = stream(((Array) objectDb().get(objectHash))
          .asIterable(Str.class))
          .map(Str::jValue)
          .collect(toList());
      assertThat(strings)
          .containsExactly("aaa", "bbb")
          .inOrder();
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_data_size_different_than_multiple_of_hash_size_is_corrupted(
        int byteCount) throws Exception {
      Hash notHashOfHashSequence = hash(ByteString.of(new byte[byteCount]));
      Hash objectHash =
          hash(
              hash(arraySpec(stringSpec())),
              notHashOfHashSequence
          );
      assertCall(() -> ((Array) objectDb().get(objectHash)).asIterable(Obj.class))
          .throwsException(new CannotDecodeObjectException(objectHash))
          .withCause(new DecodingHashSequenceException(notHashOfHashSequence));
    }

    @Test
    public void with_one_element_of_wrong_spec_is_corrupted() throws Exception {
      Hash objectHash =
          hash(
              hash(arraySpec(stringSpec())),
              hash(
                  hash(
                      hash(stringSpec()),
                      hash("aaa")
                  ),
                  hash(
                      hash(boolSpec()),
                      hash(true)
                  )
              ));
      assertCall(() -> ((Array) objectDb().get(objectHash)).asIterable(Str.class))
          .throwsException(new CannotDecodeObjectException(objectHash,
              "It is array which spec == [STRING] but one of its elements has spec == BOOL"));
    }
  }

  private static class IllegalArrayByteSizesProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return IntStream.rangeClosed(1, Hash.hashesSize() * 3 + 1)
          .filter(i -> i % Hash.hashesSize() != 0)
          .mapToObj(Arguments::of);
    }
  }

  @Nested
  class _string {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save smooth bool
       * in HashedDb.
       */
      Hash objectHash =
          hash(
              hash(stringSpec()),
              hash("aaa"));
      assertThat(((Str) objectDb().get(objectHash)).jValue())
          .isEqualTo("aaa");
    }

    @Test
    public void data_being_invalid_utf8_sequence_is_corrupted() throws Exception {
      Hash notStringHash = hash(ByteString.of((byte) -64));
      Hash objectHash =
          hash(
              hash(stringSpec()),
              notStringHash);
      assertCall(() -> ((Str) objectDb().get(objectHash)).jValue())
          .throwsException(new CannotDecodeObjectException(objectHash))
          .withCause(new DecodingStringException(notStringHash, null));
    }
  }

  @Nested
  class _tuple {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save smooth tuple
       * in HashedDb.
       */
      assertThat(
          hash(
              hash(personSpec()),
              hash(
                  hash(string("John")),
                  hash(string("Doe")))))
          .isEqualTo(person("John", "Doe").hash());
    }

    @Test
    public void with_too_few_elements_is_corrupted() throws Exception {
      Hash elementValuesHash =
          hash(
              hash(string("John")));
      Hash tupleHash =
          hash(
              hash(personSpec()),
              elementValuesHash);
      Tuple tuple = (Tuple) objectDb().get(tupleHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new CannotDecodeObjectException(tupleHash, errorReadingElementHashes()))
          .withCause(new DecodingHashSequenceException(elementValuesHash, 2, 1));
    }

    @Test
    public void with_too_many_elements_is_corrupted() throws Exception {
      Hash elementValuesHash =
          hash(
              hash(string("John")),
              hash(string("Doe")),
              hash(string("junk")));
      Hash tupleHash =
          hash(
              hash(personSpec()),
              elementValuesHash);
      Tuple tuple = (Tuple) objectDb().get(tupleHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new CannotDecodeObjectException(tupleHash, errorReadingElementHashes()))
          .withCause(new DecodingHashSequenceException(elementValuesHash, 2, 3));
    }

    @Test
    public void with_element_of_wrong_spec_is_corrupted() throws Exception {
      Hash tupleHash =
          hash(
              hash(personSpec()),
              hash(
                  hash(string("John")),
                  hash(bool(true))));
      Tuple tuple = (Tuple) objectDb().get(tupleHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new CannotDecodeObjectException(tupleHash,
              "Its TUPLE spec declares element 1 to have STRING spec but its data has object"
                  + " with BOOL spec at that index."));
    }

    private String errorReadingElementHashes() {
      return "Error reading element hashes.";
    }
  }

  @Nested
  class _spec {
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
    class base_spec {
      @Test
      public void any_with_additional_child_causes_exception() throws Exception {
        do_test_with_additional_child(ANY);
      }

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

    private String brokenSpecMessage(SpecKind specKind, int actual, int expected) {
      return "Its specKind == " + specKind + " but its merkle root has " + actual
          + " children when " + expected + " is expected.";
    }

    private String elementReadingErrorMessage(int index) {
      return "Its specKind == TUPLE but reading element spec at index " + index + " caused error.";
    }

    private CannotDecodeSpecException illegalSpecMarkerException(Hash hash, int marker) {
      return new CannotDecodeSpecException(hash, "It has illegal SpecKind marker = " + marker + ".");
    }
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

  protected Hash hash(Obj object) {
    return object.hash();
  }

  protected Hash hash(Spec spec) {
    return spec.hash();
  }

  protected Hash hash(Hash... hashes) throws HashedDbException {
    return hashedDb().writeHashes(hashes);
  }
}
