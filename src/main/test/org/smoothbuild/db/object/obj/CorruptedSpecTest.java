package org.smoothbuild.db.object.obj;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.db.object.db.SpecDb.DATA_PATH;
import static org.smoothbuild.db.object.db.SpecDb.LAMBDA_PARAMS_PATH;
import static org.smoothbuild.db.object.db.SpecDb.LAMBDA_RESULT_PATH;
import static org.smoothbuild.db.object.spec.base.SpecKind.ARRAY;
import static org.smoothbuild.db.object.spec.base.SpecKind.BLOB;
import static org.smoothbuild.db.object.spec.base.SpecKind.BOOL;
import static org.smoothbuild.db.object.spec.base.SpecKind.CALL;
import static org.smoothbuild.db.object.spec.base.SpecKind.CONST;
import static org.smoothbuild.db.object.spec.base.SpecKind.DEFINED_LAMBDA;
import static org.smoothbuild.db.object.spec.base.SpecKind.EARRAY;
import static org.smoothbuild.db.object.spec.base.SpecKind.INT;
import static org.smoothbuild.db.object.spec.base.SpecKind.NATIVE_LAMBDA;
import static org.smoothbuild.db.object.spec.base.SpecKind.NOTHING;
import static org.smoothbuild.db.object.spec.base.SpecKind.NULL;
import static org.smoothbuild.db.object.spec.base.SpecKind.RECORD;
import static org.smoothbuild.db.object.spec.base.SpecKind.REF;
import static org.smoothbuild.db.object.spec.base.SpecKind.SELECT;
import static org.smoothbuild.db.object.spec.base.SpecKind.STRING;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.db.hashed.exc.DecodeHashSequenceException;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.hashed.exc.NoSuchDataException;
import org.smoothbuild.db.object.exc.DecodeSpecException;
import org.smoothbuild.db.object.exc.DecodeSpecIllegalKindException;
import org.smoothbuild.db.object.exc.DecodeSpecNodeException;
import org.smoothbuild.db.object.exc.DecodeSpecRootException;
import org.smoothbuild.db.object.exc.UnexpectedSpecNodeException;
import org.smoothbuild.db.object.exc.UnexpectedSpecSequenceException;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.SpecKind;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.expr.ConstSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.DefinedLambdaSpec;
import org.smoothbuild.db.object.spec.val.IntSpec;
import org.smoothbuild.db.object.spec.val.LambdaSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.db.object.spec.val.StrSpec;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.common.AssertCall.ThrownExceptionSubject;

import okio.ByteString;

public class CorruptedSpecTest extends TestingContext {
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
    public void with_additional_child() throws Exception {
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
    public void blob_with_additional_child() throws Exception {
      test_base_spec_with_additional_child(BLOB);
    }

    @Test
    public void bool_with_additional_child() throws Exception {
      test_base_spec_with_additional_child(BOOL);
    }

    @Test
    public void int_with_additional_child() throws Exception {
      test_base_spec_with_additional_child(INT);
    }

    @Test
    public void null_with_additional_child() throws Exception {
      test_base_spec_with_additional_child(NULL);
    }

    @Test
    public void nothing_with_additional_child() throws Exception {
      test_base_spec_with_additional_child(NOTHING);
    }

    @Test
    public void string_with_additional_child() throws Exception {
      test_base_spec_with_additional_child(STRING);
    }

    private void test_base_spec_with_additional_child(SpecKind kind) throws Exception {
      Hash hash = hash(
          hash(kind.marker()),
          hash("abc")
      );
      assertThatGetSpec(hash)
          .throwsException(new DecodeSpecRootException(hash, kind, 2, 1));
    }
  }

  @Nested
  class array_spec {
    @Test
    public void learn_creating_spec() throws Exception {
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
    public void without_data() throws Exception {
      test_spec_without_data(ARRAY);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_spec_with_additional_data(ARRAY);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_spec(ARRAY);
    }

    @Test
    public void with_corrupted_spec_as_data() throws Exception {
      test_spec_with_corrupted_spec_as_data(ARRAY);
    }

    @Test
    public void with_element_spec_being_expr_spec() throws Exception {
      Hash hash = hash(
          hash(ARRAY.marker()),
          hash(constSpec(intSpec()))
      );
      assertThatGetSpec(hash)
          .throwsException(new UnexpectedSpecNodeException(
              hash, ARRAY, DATA_PATH, ValSpec.class, ConstSpec.class));
    }
  }

  @Nested
  class call_spec {
    @Test
    public void learn_creating_spec() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save call spec in HashedDb.
       */
      Hash hash = hash(
          hash(CALL.marker()),
          hash(intSpec())
      );
      assertThat(hash)
          .isEqualTo(callSpec(intSpec()).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_spec_without_data(CALL);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_spec_with_additional_data(CALL);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_spec(CALL);
    }

    @Test
    public void with_corrupted_spec_as_data() throws Exception {
      test_spec_with_corrupted_spec_as_data(CALL);
    }

    @Test
    public void with_evaluation_spec_being_expr_spec() throws Exception {
      test_spec_with_data_spec_being_expr_spec(CALL, ValSpec.class);
    }
  }

  @Nested
  class const_spec {
    @Test
    public void learn_creating_spec() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save const spec in HashedDb.
       */
      Hash hash = hash(
          hash(CONST.marker()),
          hash(intSpec())
      );
      assertThat(hash)
          .isEqualTo(constSpec(intSpec()).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_spec_without_data(CONST);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_spec_with_additional_data(CONST);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_spec(CONST);
    }

    @Test
    public void with_corrupted_spec_as_data() throws Exception {
      test_spec_with_corrupted_spec_as_data(CONST);
    }

    @Test
    public void with_evaluation_spec_being_expr_spec() throws Exception {
      test_spec_with_data_spec_being_expr_spec(CONST, ValSpec.class);
    }
  }

  @Nested
  class defined_lambda_spec extends abstract_lambda_spec_test {
    @Override
    protected SpecKind specKind() {
      return DEFINED_LAMBDA;
    }

    @Override
    protected LambdaSpec newSpec(ValSpec result, RecSpec arguments) {
      return definedLambdaSpec(result, arguments);
    }
  }

  @Nested
  class native_lambda_spec extends abstract_lambda_spec_test {
    @Override
    protected SpecKind specKind() {
      return NATIVE_LAMBDA;
    }

    @Override
    protected LambdaSpec newSpec(ValSpec result, RecSpec arguments) {
      return nativeLambdaSpec(result, arguments);
    }
  }

  abstract class abstract_lambda_spec_test {
    @Test
    public void learn_creating_spec() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save defined-lambda spec in HashedDb.
       */
      RecSpec argumentSpecs = recSpec(list(strSpec(), boolSpec()));
      Hash specHash = hash(
          hash(specKind().marker()),
          hash(
              hash(intSpec()),
              hash(argumentSpecs)
          )
      );
      assertThat(specHash)
          .isEqualTo(newSpec(intSpec(), argumentSpecs).hash());
    }

    protected abstract LambdaSpec newSpec(ValSpec result, RecSpec arguments);

    protected abstract SpecKind specKind();

    @Test
    public void without_data() throws Exception {
      test_spec_without_data(specKind());
    }

    @Test
    public void with_additional_data() throws Exception {
      test_spec_with_additional_data(specKind());
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_sequence(specKind());
    }

    @Test
    public void with_corrupted_spec_as_data() throws Exception {
      test_spec_with_corrupted_spec_as_data(specKind());
    }

    @Test
    public void with_data_not_being_sequence_of_hashes() throws Exception {
      Hash notSequence = hash("abc");
      Hash hash =
          hash(
              hash(specKind().marker()),
              notSequence
          );
      assertThatGetSpec(hash)
          .throwsException(new DecodeSpecNodeException(hash, specKind(), DATA_PATH));
    }

    @Test
    public void with_data_having_three_elements() throws Exception {
      RecSpec argumentSpecs = recSpec(list(strSpec(), boolSpec()));
      Hash hash = hash(
          hash(specKind().marker()),
          hash(
              hash(intSpec()),
              hash(argumentSpecs),
              hash(argumentSpecs)
          )
      );
      assertThatGetSpec(hash)
          .throwsException(new UnexpectedSpecSequenceException(
              hash, specKind(), DATA_PATH, 2, 3));
    }

    @Test
    public void with_data_having_one_elements() throws Exception {
      Hash hash = hash(
          hash(specKind().marker()),
          hash(
              hash(intSpec())
          )
      );
      assertThatGetSpec(hash)
          .throwsException(new UnexpectedSpecSequenceException(
              hash, specKind(), DATA_PATH, 2, 1));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_data_sequence_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSequence = hash(ByteString.of(new byte[byteCount]));
      Hash specHash = hash(
          hash(specKind().marker()),
          notHashOfSequence
      );
      assertCall(() -> ((DefinedLambdaSpec) specDb().getSpec(specHash)).result())
          .throwsException(new DecodeSpecNodeException(specHash, specKind(), DATA_PATH))
          .withCause(new DecodeHashSequenceException(
              notHashOfSequence, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_result_pointing_nowhere() throws Exception {
      RecSpec argumentSpecs = recSpec(list(strSpec(), boolSpec()));
      Hash nowhere = Hash.of(33);
      Hash specHash = hash(
          hash(specKind().marker()),
          hash(
              nowhere,
              hash(argumentSpecs)
          )
      );
      assertCall(() -> specDb().getSpec(specHash))
          .throwsException(new DecodeSpecNodeException(specHash, specKind(), LAMBDA_RESULT_PATH))
          .withCause(new DecodeSpecException(nowhere));
    }

    @Test
    public void with_result_being_expr_spec() throws Exception {
      RecSpec argumentSpecs = recSpec(list(strSpec(), boolSpec()));
      Hash specHash = hash(
          hash(specKind().marker()),
          hash(
              hash(constSpec()),
              hash(argumentSpecs)
          )
      );
      assertCall(() -> specDb().getSpec(specHash))
          .throwsException(new UnexpectedSpecNodeException(
              specHash, specKind(), LAMBDA_RESULT_PATH, ValSpec.class, ConstSpec.class));
    }

    @Test
    public void with_result_spec_corrupted() throws Exception {
      RecSpec argumentSpecs = recSpec(list(strSpec(), boolSpec()));
      Hash resultHash = corruptedArraySpecHash();
      Hash specHash = hash(
          hash(specKind().marker()),
          hash(
              resultHash,
              hash(argumentSpecs)
          )
      );
      assertCall(() -> specDb().getSpec(specHash))
          .throwsException(new DecodeSpecNodeException(specHash, specKind(), LAMBDA_RESULT_PATH))
          .withCause(corruptedArraySpecException());
    }

    @Test
    public void with_arguments_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash specHash = hash(
          hash(specKind().marker()),
          hash(
              hash(intSpec()),
              nowhere
          )
      );
      assertCall(() -> specDb().getSpec(specHash))
          .throwsException(new DecodeSpecNodeException(specHash, specKind(), LAMBDA_PARAMS_PATH))
          .withCause(new DecodeSpecException(nowhere));
    }

    @Test
    public void with_arguments_not_being_rec() throws Exception {
      Hash specHash = hash(
          hash(specKind().marker()),
          hash(
              hash(intSpec()),
              hash(strSpec())
          )
      );
      assertThatGetSpec(specHash)
          .throwsException(new UnexpectedSpecNodeException(
              specHash, specKind(), DATA_PATH, 1, RecSpec.class, StrSpec.class));
    }

    @Test
    public void with_arguments_being_expr_spec() throws Exception {
      Hash specHash = hash(
          hash(specKind().marker()),
          hash(
              hash(intSpec()),
              hash(constSpec())
          )
      );
      assertCall(() -> specDb().getSpec(specHash))
          .throwsException(new UnexpectedSpecNodeException(
              specHash, specKind(), LAMBDA_PARAMS_PATH, RecSpec.class, ConstSpec.class));
    }

    @Test
    public void with_arguments_spec_corrupted() throws Exception {
      Hash corrutpedArgumentHash = corruptedArraySpecHash();
      Hash specHash = hash(
          hash(specKind().marker()),
          hash(
              hash(intSpec()),
              corrutpedArgumentHash
          )
      );
      assertCall(() -> specDb().getSpec(specHash))
          .throwsException(new DecodeSpecNodeException(specHash, specKind(), LAMBDA_PARAMS_PATH))
          .withCause(corruptedArraySpecException());
    }
  }

  @Nested
  class earray_spec {
    @Test
    public void learn_creating_spec() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save array spec in HashedDb.
       */
      Hash hash = hash(
          hash(EARRAY.marker()),
          hash(arraySpec(intSpec()))
      );
      assertThat(hash)
          .isEqualTo(eArraySpec(intSpec()).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_spec_without_data(EARRAY);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_spec_with_additional_data(EARRAY);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_spec(EARRAY);
    }

    @Test
    public void with_corrupted_spec_as_data() throws Exception {
      test_spec_with_corrupted_spec_as_data(EARRAY);
    }

    @Test
    public void with_evaluation_spec_being_expr_spec() throws Exception {
      test_spec_with_data_spec_being_expr_spec(EARRAY, ArraySpec.class);
    }

    @Test
    public void with_evaluation_spec_not_being_array_spec() throws Exception {
      Hash hash = hash(
          hash(EARRAY.marker()),
          hash(intSpec())
      );
      assertThatGetSpec(hash)
          .throwsException(new UnexpectedSpecNodeException(
              hash, EARRAY, DATA_PATH, ArraySpec.class, IntSpec.class));
    }
  }

  @Nested
  class select_spec {
    @Test
    public void learn_creating_spec() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save call spec in HashedDb.
       */
      Hash hash = hash(
          hash(SELECT.marker()),
          hash(intSpec())
      );
      assertThat(hash)
          .isEqualTo(selectSpec(intSpec()).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_spec_without_data(SELECT);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_spec_with_additional_data(SELECT);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_spec(SELECT);
    }

    @Test
    public void with_corrupted_spec_as_data() throws Exception {
      test_spec_with_corrupted_spec_as_data(SELECT);
    }

    @Test
    public void with_evaluation_spec_being_expr_spec() throws Exception {
      test_spec_with_data_spec_being_expr_spec(SELECT, ValSpec.class);
    }
  }

  @Nested
  class rec_spec {
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

    @Test
    public void without_data() throws Exception {
      test_spec_without_data(RECORD);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_spec_with_additional_data(RECORD);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_sequence(RECORD);
    }

    @Test
    public void with_elements_not_being_sequence_of_hashes() throws Exception {
      Hash notSequence = hash("abc");
      Hash hash =
          hash(
              hash(RECORD.marker()),
              notSequence
          );
      assertThatGetSpec(hash)
          .throwsException(new DecodeSpecNodeException(hash, RECORD, DATA_PATH));
    }

    @Test
    public void with_elements_being_array_of_non_spec() throws Exception {
      Hash stringHash = hash(strVal("abc"));
      Hash hash =
          hash(
              hash(RECORD.marker()),
              hash(
                  stringHash
              )
          );
      assertThatGetSpec(hash)
          .throwsException(new DecodeSpecNodeException(hash, RECORD, "data[0]"))
          .withCause(new DecodeSpecException(stringHash));
    }

    @Test
    public void with_elements_being_sequence_of_expr_spec() throws Exception {
      Hash hash =
          hash(
              hash(RECORD.marker()),
              hash(
                  hash(constSpec())
              )
          );
      assertThatGetSpec(hash)
          .throwsException(new UnexpectedSpecNodeException(
              hash, RECORD, "data", 0, ValSpec.class, ConstSpec.class));
    }

    @Test
    public void with_corrupted_element_spec() throws Exception {
      Hash notASpecHash = hash("not a spec");
      Hash hash =
          hash(
              hash(RECORD.marker()),
              hash(
                  notASpecHash,
                  hash(strSpec())));
      assertThatGetSpec(hash)
          .throwsException(new DecodeSpecNodeException(hash, RECORD, "data[0]"))
          .withCause(new DecodeSpecException(notASpecHash));
    }
  }

  @Nested
  class ref_spec {
    @Test
    public void learn_creating_spec() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save call spec in HashedDb.
       */
      Hash hash = hash(
          hash(REF.marker()),
          hash(intSpec())
      );
      assertThat(hash)
          .isEqualTo(refSpec(intSpec()).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_spec_without_data(REF);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_spec_with_additional_data(REF);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_spec(REF);
    }

    @Test
    public void with_corrupted_spec_as_data() throws Exception {
      test_spec_with_corrupted_spec_as_data(REF);
    }

    @Test
    public void with_evaluation_spec_being_expr_spec() throws Exception {
      test_spec_with_data_spec_being_expr_spec(REF, ValSpec.class);
    }
  }

  private void test_spec_without_data(SpecKind speckKind) throws Exception {
    Hash hash =
        hash(
            hash(speckKind.marker())
        );
    assertThatGetSpec(hash)
        .throwsException(new DecodeSpecRootException(hash, speckKind, 1, 2));
  }

  private void test_spec_with_additional_data(SpecKind specKind) throws Exception {
    Hash hash = hash(
        hash(specKind.marker()),
        hash(strSpec()),
        hash("corrupted")
    );
    assertThatGetSpec(hash)
        .throwsException(new DecodeSpecRootException(hash, 3));
  }

  private void test_data_hash_pointing_nowhere_instead_of_being_spec(SpecKind specKind)
      throws Exception {
    Hash dataHash = Hash.of(33);
    Hash specHash = hash(
        hash(specKind.marker()),
        dataHash
    );
    assertCall(() -> specDb().getSpec(specHash))
        .throwsException(new DecodeSpecNodeException(specHash, specKind, DATA_PATH))
        .withCause(new DecodeSpecException(dataHash));
  }

  private void test_data_hash_pointing_nowhere_instead_of_being_sequence(SpecKind specKind)
      throws Exception {
    Hash dataHash = Hash.of(33);
    Hash specHash = hash(
        hash(specKind.marker()),
        dataHash
    );
    assertCall(() -> specDb().getSpec(specHash))
        .throwsException(new DecodeSpecNodeException(specHash, specKind, DATA_PATH))
        .withCause(new NoSuchDataException(dataHash));
  }

  private void test_spec_with_corrupted_spec_as_data(SpecKind specKind)
      throws Exception {
    Hash notASpecHash = hash("not a type");
    Hash hash =
        hash(
            hash(specKind.marker()),
            notASpecHash);
    assertThatGetSpec(hash)
        .throwsException(new DecodeSpecNodeException(hash, specKind, DATA_PATH));
  }

  private void test_spec_with_data_spec_being_expr_spec(
      SpecKind specKind, Class<? extends Spec> expected)
      throws Exception {
    Hash hash = hash(
        hash(specKind.marker()),
        hash(constSpec(intSpec()))
    );
    assertThatGetSpec(hash)
        .throwsException(new UnexpectedSpecNodeException(
            hash, specKind, DATA_PATH, expected, ConstSpec.class));
  }

  private ThrownExceptionSubject assertThatGetSpec(Hash hash) {
      return assertCall(() -> specDb().getSpec(hash));
  }

  private DecodeSpecException illegalSpecMarkerException(Hash hash, int marker) {
    return new DecodeSpecIllegalKindException(hash, (byte) marker);
  }

  private DecodeSpecNodeException corruptedArraySpecException() throws Exception {
    return new DecodeSpecNodeException(corruptedArraySpecHash(), ARRAY, DATA_PATH);
  }

  private Hash corruptedArraySpecHash() throws Exception {
    return hash(
        hash(ARRAY.marker()),
        Hash.of(33)
    );
  }

  protected Hash hash(String string) throws HashedDbException {
    return hashedDb().writeString(string);
  }

  protected Hash hash(boolean value) throws Exception {
    return hash((byte) (value ? 1 : 0));
  }

  protected Hash hash(byte value) throws Exception {
    try (HashingBufferedSink sink = hashedDb().sink()) {
      sink.writeByte(value);
      sink.close();
      return sink.hash();
    }
  }

  protected Hash hash(ByteString bytes) throws Exception {
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
