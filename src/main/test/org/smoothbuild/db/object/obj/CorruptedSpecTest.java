package org.smoothbuild.db.object.obj;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.db.object.db.SpecDb.DATA_PATH;
import static org.smoothbuild.db.object.db.SpecDb.LAMBDA_PARAMS_PATH;
import static org.smoothbuild.db.object.db.SpecDb.LAMBDA_RESULT_PATH;
import static org.smoothbuild.db.object.spec.base.SpecKind.ABSENT;
import static org.smoothbuild.db.object.spec.base.SpecKind.ANY;
import static org.smoothbuild.db.object.spec.base.SpecKind.ARRAY;
import static org.smoothbuild.db.object.spec.base.SpecKind.ARRAY_EXPR;
import static org.smoothbuild.db.object.spec.base.SpecKind.BLOB;
import static org.smoothbuild.db.object.spec.base.SpecKind.BOOL;
import static org.smoothbuild.db.object.spec.base.SpecKind.CALL;
import static org.smoothbuild.db.object.spec.base.SpecKind.CONST;
import static org.smoothbuild.db.object.spec.base.SpecKind.INT;
import static org.smoothbuild.db.object.spec.base.SpecKind.INVOKE;
import static org.smoothbuild.db.object.spec.base.SpecKind.LAMBDA;
import static org.smoothbuild.db.object.spec.base.SpecKind.NOTHING;
import static org.smoothbuild.db.object.spec.base.SpecKind.NULL;
import static org.smoothbuild.db.object.spec.base.SpecKind.RECORD;
import static org.smoothbuild.db.object.spec.base.SpecKind.RECORD_EXPR;
import static org.smoothbuild.db.object.spec.base.SpecKind.REF;
import static org.smoothbuild.db.object.spec.base.SpecKind.SELECT;
import static org.smoothbuild.db.object.spec.base.SpecKind.STRING;
import static org.smoothbuild.db.object.spec.base.SpecKind.STRUCT;
import static org.smoothbuild.db.object.spec.base.SpecKind.VARIABLE;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.db.hashed.exc.DecodeHashSequenceException;
import org.smoothbuild.db.hashed.exc.DecodeStringException;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.hashed.exc.NoSuchDataException;
import org.smoothbuild.db.object.exc.DecodeSpecException;
import org.smoothbuild.db.object.exc.DecodeSpecIllegalKindException;
import org.smoothbuild.db.object.exc.DecodeSpecNodeException;
import org.smoothbuild.db.object.exc.DecodeSpecRootException;
import org.smoothbuild.db.object.exc.DecodeStructSpecWrongNamesSizeException;
import org.smoothbuild.db.object.exc.DecodeVariableIllegalNameException;
import org.smoothbuild.db.object.exc.UnexpectedSpecNodeException;
import org.smoothbuild.db.object.exc.UnexpectedSpecSequenceException;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.SpecKind;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.expr.ConstSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.IntSpec;
import org.smoothbuild.db.object.spec.val.LambdaSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.db.object.spec.val.StrSpec;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.common.AssertCall.ThrownExceptionSubject;

import okio.ByteString;

public class CorruptedSpecTest extends TestingContext {
  @Nested
  class _illegal_spec_marker {
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
  class _base_spec {
    @Test
    public void learn_creating_base_spec() throws Exception {
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
    public void absent_with_additional_child() throws Exception {
      test_base_spec_with_additional_child(ABSENT);
    }

    @Test
    public void any_with_additional_child() throws Exception {
      test_base_spec_with_additional_child(ANY);
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
  class _array_spec {
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
  class _call_spec {
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
  class _const_spec {
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
  class _lambda_spec {
    @Test
    public void learn_creating_spec() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save lambda spec in HashedDb.
       */
      var parameterSpecs = list(strSpec(), boolSpec());
      RecSpec parameterRec = recSpec(parameterSpecs);
      Hash specHash = hash(
          hash(LAMBDA.marker()),
          hash(
              hash(intSpec()),
              hash(parameterRec)
          )
      );
      assertThat(specHash)
          .isEqualTo(lambdaSpec(intSpec(), parameterSpecs).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_spec_without_data(LAMBDA);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_spec_with_additional_data(LAMBDA);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_sequence(LAMBDA);
    }

    @Test
    public void with_data_not_being_sequence_of_hashes() throws Exception {
      Hash notSequence = hash("abc");
      Hash hash =
          hash(
              hash(LAMBDA.marker()),
              notSequence
          );
      assertThatGetSpec(hash)
          .throwsException(new DecodeSpecNodeException(hash, LAMBDA, DATA_PATH));
    }

    @Test
    public void with_data_having_three_elements() throws Exception {
      RecSpec parameterSpecs = recSpec(list(strSpec(), boolSpec()));
      Hash hash = hash(
          hash(LAMBDA.marker()),
          hash(
              hash(intSpec()),
              hash(parameterSpecs),
              hash(parameterSpecs)
          )
      );
      assertThatGetSpec(hash)
          .throwsException(new UnexpectedSpecSequenceException(hash, LAMBDA, DATA_PATH, 2, 3));
    }

    @Test
    public void with_data_having_one_elements() throws Exception {
      Hash hash = hash(
          hash(LAMBDA.marker()),
          hash(
              hash(intSpec())
          )
      );
      assertThatGetSpec(hash)
          .throwsException(new UnexpectedSpecSequenceException(hash, LAMBDA, DATA_PATH, 2, 1));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_data_sequence_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSequence = hash(ByteString.of(new byte[byteCount]));
      Hash specHash = hash(
          hash(LAMBDA.marker()),
          notHashOfSequence
      );
      assertCall(() -> ((LambdaSpec) specDb().getSpec(specHash)).result())
          .throwsException(new DecodeSpecNodeException(specHash, LAMBDA, DATA_PATH))
          .withCause(new DecodeHashSequenceException(
              notHashOfSequence, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_result_pointing_nowhere() throws Exception {
      RecSpec parameterSpecs = recSpec(list(strSpec(), boolSpec()));
      Hash nowhere = Hash.of(33);
      Hash specHash = hash(
          hash(LAMBDA.marker()),
          hash(
              nowhere,
              hash(parameterSpecs)
          )
      );
      assertCall(() -> specDb().getSpec(specHash))
          .throwsException(new DecodeSpecNodeException(specHash, LAMBDA, LAMBDA_RESULT_PATH))
          .withCause(new DecodeSpecException(nowhere));
    }

    @Test
    public void with_result_being_expr_spec() throws Exception {
      RecSpec parameterSpecs = recSpec(list(strSpec(), boolSpec()));
      Hash specHash = hash(
          hash(LAMBDA.marker()),
          hash(
              hash(constSpec()),
              hash(parameterSpecs)
          )
      );
      assertCall(() -> specDb().getSpec(specHash))
          .throwsException(new UnexpectedSpecNodeException(
              specHash, LAMBDA, LAMBDA_RESULT_PATH, ValSpec.class, ConstSpec.class));
    }

    @Test
    public void with_result_spec_corrupted() throws Exception {
      RecSpec parameterSpecs = recSpec(list(strSpec(), boolSpec()));
      Hash specHash = hash(
          hash(LAMBDA.marker()),
          hash(
              corruptedArraySpecHash(),
              hash(parameterSpecs)
          )
      );
      assertCall(() -> specDb().getSpec(specHash))
          .throwsException(new DecodeSpecNodeException(specHash, LAMBDA, LAMBDA_RESULT_PATH))
          .withCause(corruptedArraySpecException());
    }

    @Test
    public void with_parameters_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash specHash = hash(
          hash(LAMBDA.marker()),
          hash(
              hash(intSpec()),
              nowhere
          )
      );
      assertCall(() -> specDb().getSpec(specHash))
          .throwsException(new DecodeSpecNodeException(specHash, LAMBDA, LAMBDA_PARAMS_PATH))
          .withCause(new DecodeSpecException(nowhere));
    }

    @Test
    public void with_parameters_not_being_rec() throws Exception {
      Hash specHash = hash(
          hash(LAMBDA.marker()),
          hash(
              hash(intSpec()),
              hash(strSpec())
          )
      );
      assertThatGetSpec(specHash)
          .throwsException(new UnexpectedSpecNodeException(
              specHash, LAMBDA, DATA_PATH, 1, RecSpec.class, StrSpec.class));
    }

    @Test
    public void with_parameters_being_expr_spec() throws Exception {
      Hash specHash = hash(
          hash(LAMBDA.marker()),
          hash(
              hash(intSpec()),
              hash(constSpec())
          )
      );
      assertCall(() -> specDb().getSpec(specHash))
          .throwsException(new UnexpectedSpecNodeException(
              specHash, LAMBDA, LAMBDA_PARAMS_PATH, RecSpec.class, ConstSpec.class));
    }

    @Test
    public void with_parameters_spec_corrupted() throws Exception {
      Hash specHash = hash(
          hash(LAMBDA.marker()),
          hash(
              hash(intSpec()),
              corruptedArraySpecHash()
          )
      );
      assertCall(() -> specDb().getSpec(specHash))
          .throwsException(new DecodeSpecNodeException(specHash, LAMBDA, LAMBDA_PARAMS_PATH))
          .withCause(corruptedArraySpecException());
    }
  }

  @Nested
  class _invoke_spec {
    @Test
    public void learn_creating_spec() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save invoke spec in HashedDb.
       */
      Hash hash = hash(
          hash(INVOKE.marker()),
          hash(intSpec())
      );
      assertThat(hash)
          .isEqualTo(invokeSpec(intSpec()).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_spec_without_data(INVOKE);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_spec_with_additional_data(INVOKE);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_spec(INVOKE);
    }

    @Test
    public void with_corrupted_spec_as_data() throws Exception {
      test_spec_with_corrupted_spec_as_data(INVOKE);
    }

    @Test
    public void with_evaluation_spec_being_expr_spec() throws Exception {
      test_spec_with_data_spec_being_expr_spec(INVOKE, ValSpec.class);
    }
  }

  @Nested
  class _array_expr_spec {
    @Test
    public void learn_creating_spec() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save ArrayExpr spec in HashedDb.
       */
      Hash hash = hash(
          hash(ARRAY_EXPR.marker()),
          hash(arraySpec(intSpec()))
      );
      assertThat(hash)
          .isEqualTo(arrayExprSpec(intSpec()).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_spec_without_data(ARRAY_EXPR);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_spec_with_additional_data(ARRAY_EXPR);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_spec(ARRAY_EXPR);
    }

    @Test
    public void with_corrupted_spec_as_data() throws Exception {
      test_spec_with_corrupted_spec_as_data(ARRAY_EXPR);
    }

    @Test
    public void with_evaluation_spec_being_expr_spec() throws Exception {
      test_spec_with_data_spec_being_expr_spec(ARRAY_EXPR, ArraySpec.class);
    }

    @Test
    public void with_evaluation_spec_not_being_array_spec() throws Exception {
      Hash hash = hash(
          hash(ARRAY_EXPR.marker()),
          hash(intSpec())
      );
      assertThatGetSpec(hash)
          .throwsException(new UnexpectedSpecNodeException(
              hash, ARRAY_EXPR, DATA_PATH, ArraySpec.class, IntSpec.class));
    }
  }

  @Nested
  class _record_expr_spec {
    @Test
    public void learn_creating_spec() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save recordExpr spec in HashedDb.
       */
      Hash hash = hash(
          hash(RECORD_EXPR.marker()),
          hash(recSpec(list(intSpec(), strSpec())))
      );
      assertThat(hash)
          .isEqualTo(recExprSpec(list(intSpec(), strSpec())).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_spec_without_data(RECORD_EXPR);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_spec_with_additional_data(RECORD_EXPR);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_spec(RECORD_EXPR);
    }

    @Test
    public void with_corrupted_spec_as_data() throws Exception {
      test_spec_with_corrupted_spec_as_data(RECORD_EXPR);
    }

    @Test
    public void with_evaluation_spec_being_expr_spec() throws Exception {
      test_spec_with_data_spec_being_expr_spec(RECORD_EXPR, RecSpec.class);
    }

    @Test
    public void with_evaluation_spec_not_being_rec_spec() throws Exception {
      Hash hash = hash(
          hash(RECORD_EXPR.marker()),
          hash(intSpec())
      );
      assertThatGetSpec(hash)
          .throwsException(new UnexpectedSpecNodeException(
              hash, RECORD_EXPR, DATA_PATH, RecSpec.class, IntSpec.class));
    }
  }

  @Nested
  class _select_spec {
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
  class _rec_spec {
    @Test
    public void learn_creating_spec() throws Exception {
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
      Hash hash =
          hash(
              hash(RECORD.marker()),
              hash(
                  corruptedArraySpecHash(),
                  hash(strSpec())));
      assertThatGetSpec(hash)
          .throwsException(new DecodeSpecNodeException(hash, RECORD, "data[0]"))
          .withCause(corruptedArraySpecException());
    }
  }

  @Nested
  class _ref_spec {
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

  private void test_spec_with_corrupted_spec_as_data(SpecKind specKind) throws Exception {
    Hash hash =
        hash(
            hash(specKind.marker()),
            corruptedArraySpecHash());
    assertThatGetSpec(hash)
        .throwsException(new DecodeSpecNodeException(hash, specKind, DATA_PATH))
        .withCause(corruptedArraySpecException());
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

  @Nested
  class _struct_spec {
    @Test
    public void learn_creating_spec() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save struct spec in HashedDb.
       */
      String name = "MyStruct";
      RecSpec itemsSpec = recSpec(list(intSpec(), strSpec()));
      String field1 = "field1";
      String field2 = "field2";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              hash(name),
              hash(itemsSpec),
              hash(hash(field1), hash(field2))
          )
      );
      assertThat(hash)
          .isEqualTo(structSpec(itemsSpec, list(field1, field2)).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_spec_without_data(STRUCT);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_spec_with_additional_data(STRUCT);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_sequence(STRUCT);
    }

    @Test
    public void with_name_pointing_nowhere() throws Exception {
      Hash nameHash = Hash.of(33);
      RecSpec itemsSpec = recSpec(list(intSpec(), strSpec()));
      String field1 = "field1";
      String field2 = "field2";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              nameHash,
              hash(itemsSpec),
              hash(hash(field1), hash(field2))
          )
      );
      assertThatGetSpec(hash)
          .throwsException(new DecodeSpecNodeException(hash, STRUCT, DATA_PATH + "[0]"));
    }

    @Test
    public void with_illegal_name() throws Exception {
      Hash nameHash = hash(ByteString.of((byte) -64));
      RecSpec itemsSpec = recSpec(list(intSpec(), strSpec()));
      String field1 = "field1";
      String field2 = "field2";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              nameHash,
              hash(itemsSpec),
              hash(hash(field1), hash(field2))
          )
      );
      assertThatGetSpec(hash)
          .throwsException(new DecodeSpecNodeException(hash, STRUCT, DATA_PATH + "[0]"))
          .withCause(DecodeStringException.class);
    }

    @Test
    public void with_items_pointing_nowhere() throws Exception {
      Hash itemsHash = Hash.of(33);
      String name = "MyStruct";
      String field1 = "field1";
      String field2 = "field2";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              hash(name),
              itemsHash,
              hash(hash(field1), hash(field2))
          )
      );
      assertThatGetSpec(hash)
          .throwsException(new DecodeSpecNodeException(hash, STRUCT, DATA_PATH + "[1]"));
    }

    @Test
    public void with_items_not_being_rec_spec() throws Exception {
      String name = "MyStruct";
      IntSpec itemsSpec = intSpec();
      String field1 = "field1";
      String field2 = "field2";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              hash(name),
              hash(itemsSpec),
              hash(hash(field1), hash(field2))
          )
      );
      assertThatGetSpec(hash)
          .throwsException(new UnexpectedSpecNodeException(
              hash, STRUCT, DATA_PATH, 1, RecSpec.class, IntSpec.class));
    }

    @Test
    public void with_names_size_different_than_items_size() throws Exception {
      String name = "MyStruct";
      RecSpec itemsSpec = recSpec(list(intSpec(), strSpec()));
      String field1 = "field1";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              hash(name),
              hash(itemsSpec),
              hash(hash(field1))
          )
      );
      assertThatGetSpec(hash)
          .throwsException(new DecodeStructSpecWrongNamesSizeException(hash, 2, 1));
    }

    @Test
    public void with_names_containing_illegal_string() throws Exception {
      String name = "MyStruct";
      RecSpec itemsSpec = recSpec(list(intSpec(), strSpec()));
      String field1 = "field1";
      Hash field2Hash = hash(ByteString.of((byte) -64));
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              hash(name),
              hash(itemsSpec),
              hash(hash(field1), field2Hash)
          )
      );
      assertThatGetSpec(hash)
          .throwsException(new DecodeSpecNodeException(hash, STRUCT, "data[2][1]"))
          .withCause(DecodeStringException.class);
    }
  }

  @Nested
  class _variable_spec {
    @Test
    public void learn_creating_spec() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save variable spec in HashedDb.
       */
      Hash hash = hash(
          hash(VARIABLE.marker()),
          hash("A")
      );
      assertThat(hash)
          .isEqualTo(variableSpec("A").hash());
    }

    @Test
    public void without_data() throws Exception {
      test_spec_without_data(VARIABLE);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_spec_with_additional_data(VARIABLE);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      Hash dataHash = Hash.of(33);
      Hash specHash = hash(
          hash(VARIABLE.marker()),
          dataHash
      );
      assertCall(() -> specDb().getSpec(specHash))
          .throwsException(new DecodeSpecNodeException(specHash, VARIABLE, DATA_PATH))
          .withCause(new NoSuchDataException(dataHash));
    }

    @Test
    public void with_corrupted_spec_as_data() throws Exception {
      Hash hash =
          hash(
              hash(VARIABLE.marker()),
              corruptedArraySpecHash());
      assertThatGetSpec(hash)
          .throwsException(new DecodeSpecNodeException(hash, VARIABLE, DATA_PATH))
          .withCause(DecodeStringException.class);
    }

    @Test
    public void with_illegal_name() throws Exception {
      Hash hash = hash(
          hash(VARIABLE.marker()),
          hash("a")
      );
      assertThatGetSpec(hash)
          .throwsException(new DecodeVariableIllegalNameException(hash, "a"));
    }
  }
}
