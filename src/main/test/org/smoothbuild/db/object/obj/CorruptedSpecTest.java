package org.smoothbuild.db.object.obj;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.db.object.spec.SpecDb.DATA_PATH;
import static org.smoothbuild.db.object.spec.SpecDb.LAMBDA_PARAMS_PATH;
import static org.smoothbuild.db.object.spec.SpecDb.LAMBDA_RESULT_PATH;
import static org.smoothbuild.db.object.spec.base.SpecKind.ANY;
import static org.smoothbuild.db.object.spec.base.SpecKind.ARRAY;
import static org.smoothbuild.db.object.spec.base.SpecKind.ARRAY_EXPR;
import static org.smoothbuild.db.object.spec.base.SpecKind.BLOB;
import static org.smoothbuild.db.object.spec.base.SpecKind.BOOL;
import static org.smoothbuild.db.object.spec.base.SpecKind.CALL;
import static org.smoothbuild.db.object.spec.base.SpecKind.CONST;
import static org.smoothbuild.db.object.spec.base.SpecKind.CONSTRUCT;
import static org.smoothbuild.db.object.spec.base.SpecKind.INT;
import static org.smoothbuild.db.object.spec.base.SpecKind.LAMBDA;
import static org.smoothbuild.db.object.spec.base.SpecKind.NATIVE_METHOD;
import static org.smoothbuild.db.object.spec.base.SpecKind.NOTHING;
import static org.smoothbuild.db.object.spec.base.SpecKind.REF;
import static org.smoothbuild.db.object.spec.base.SpecKind.SELECT;
import static org.smoothbuild.db.object.spec.base.SpecKind.STRING;
import static org.smoothbuild.db.object.spec.base.SpecKind.STRUCT;
import static org.smoothbuild.db.object.spec.base.SpecKind.TUPLE;
import static org.smoothbuild.db.object.spec.base.SpecKind.VARIABLE;
import static org.smoothbuild.testing.StringCreators.illegalString;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Named.named;
import static org.smoothbuild.util.collect.NamedList.namedList;

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
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.SpecKind;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.exc.DecodeSpecException;
import org.smoothbuild.db.object.spec.exc.DecodeSpecIllegalKindException;
import org.smoothbuild.db.object.spec.exc.DecodeSpecNodeException;
import org.smoothbuild.db.object.spec.exc.DecodeSpecRootException;
import org.smoothbuild.db.object.spec.exc.DecodeStructSpecWrongNamesSizeException;
import org.smoothbuild.db.object.spec.exc.DecodeVariableIllegalNameException;
import org.smoothbuild.db.object.spec.exc.UnexpectedSpecNodeException;
import org.smoothbuild.db.object.spec.exc.UnexpectedSpecSequenceException;
import org.smoothbuild.db.object.spec.expr.ConstSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.IntSpec;
import org.smoothbuild.db.object.spec.val.LambdaSpec;
import org.smoothbuild.db.object.spec.val.StrSpec;
import org.smoothbuild.db.object.spec.val.TupleSpec;
import org.smoothbuild.testing.TestingContextImpl;
import org.smoothbuild.testing.common.AssertCall.ThrownExceptionSubject;

import com.google.common.collect.ImmutableList;

import okio.ByteString;

public class CorruptedSpecTest extends TestingContextImpl {
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
    public void native_method_with_additional_child() throws Exception {
      test_base_spec_with_additional_child(NATIVE_METHOD);
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
      ImmutableList<ValSpec> parameterSpecs = list(strSpec(), boolSpec());
      TupleSpec parametersTuple = tupleSpec(parameterSpecs);
      Hash specHash = hash(
          hash(LAMBDA.marker()),
          hash(
              hash(intSpec()),
              hash(parametersTuple)
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
      TupleSpec parameterSpecs = tupleSpec(list(strSpec(), boolSpec()));
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
      assertCall(() -> ((LambdaSpec) specDb().get(specHash)).result())
          .throwsException(new DecodeSpecNodeException(specHash, LAMBDA, DATA_PATH))
          .withCause(new DecodeHashSequenceException(
              notHashOfSequence, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_result_pointing_nowhere() throws Exception {
      TupleSpec parameterSpecs = tupleSpec(list(strSpec(), boolSpec()));
      Hash nowhere = Hash.of(33);
      Hash specHash = hash(
          hash(LAMBDA.marker()),
          hash(
              nowhere,
              hash(parameterSpecs)
          )
      );
      assertCall(() -> specDb().get(specHash))
          .throwsException(new DecodeSpecNodeException(specHash, LAMBDA, LAMBDA_RESULT_PATH))
          .withCause(new DecodeSpecException(nowhere));
    }

    @Test
    public void with_result_being_expr_spec() throws Exception {
      TupleSpec parameterSpecs = tupleSpec(list(strSpec(), boolSpec()));
      Hash specHash = hash(
          hash(LAMBDA.marker()),
          hash(
              hash(constSpec()),
              hash(parameterSpecs)
          )
      );
      assertCall(() -> specDb().get(specHash))
          .throwsException(new UnexpectedSpecNodeException(
              specHash, LAMBDA, LAMBDA_RESULT_PATH, ValSpec.class, ConstSpec.class));
    }

    @Test
    public void with_result_spec_corrupted() throws Exception {
      TupleSpec parameterSpecs = tupleSpec(list(strSpec(), boolSpec()));
      Hash specHash = hash(
          hash(LAMBDA.marker()),
          hash(
              corruptedArraySpecHash(),
              hash(parameterSpecs)
          )
      );
      assertCall(() -> specDb().get(specHash))
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
      assertCall(() -> specDb().get(specHash))
          .throwsException(new DecodeSpecNodeException(specHash, LAMBDA, LAMBDA_PARAMS_PATH))
          .withCause(new DecodeSpecException(nowhere));
    }

    @Test
    public void with_parameters_not_being_tuple() throws Exception {
      Hash specHash = hash(
          hash(LAMBDA.marker()),
          hash(
              hash(intSpec()),
              hash(strSpec())
          )
      );
      assertThatGetSpec(specHash)
          .throwsException(new UnexpectedSpecNodeException(
              specHash, LAMBDA, DATA_PATH, 1, TupleSpec.class, StrSpec.class));
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
      assertCall(() -> specDb().get(specHash))
          .throwsException(new UnexpectedSpecNodeException(
              specHash, LAMBDA, LAMBDA_PARAMS_PATH, TupleSpec.class, ConstSpec.class));
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
      assertCall(() -> specDb().get(specHash))
          .throwsException(new DecodeSpecNodeException(specHash, LAMBDA, LAMBDA_PARAMS_PATH))
          .withCause(corruptedArraySpecException());
    }
  }

  @Nested
  class _tuple_spec {
    @Test
    public void learn_creating_spec() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save tuple spec in HashedDb.
       */
      Hash hash = hash(
          hash(TUPLE.marker()),
          hash(
              hash(strSpec()),
              hash(strSpec())
          )
      );
      assertThat(hash)
          .isEqualTo(perso_Spec().hash());
    }

    @Test
    public void without_data() throws Exception {
      test_spec_without_data(TUPLE);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_spec_with_additional_data(TUPLE);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_sequence(TUPLE);
    }

    @Test
    public void with_elements_not_being_sequence_of_hashes() throws Exception {
      Hash notSequence = hash("abc");
      Hash hash =
          hash(
              hash(TUPLE.marker()),
              notSequence
          );
      assertThatGetSpec(hash)
          .throwsException(new DecodeSpecNodeException(hash, TUPLE, DATA_PATH));
    }

    @Test
    public void with_elements_being_array_of_non_spec() throws Exception {
      Hash stringHash = hash(strVal("abc"));
      Hash hash =
          hash(
              hash(TUPLE.marker()),
              hash(
                  stringHash
              )
          );
      assertThatGetSpec(hash)
          .throwsException(new DecodeSpecNodeException(hash, TUPLE, "data[0]"))
          .withCause(new DecodeSpecException(stringHash));
    }

    @Test
    public void with_elements_being_sequence_of_expr_spec() throws Exception {
      Hash hash =
          hash(
              hash(TUPLE.marker()),
              hash(
                  hash(constSpec())
              )
          );
      assertThatGetSpec(hash)
          .throwsException(new UnexpectedSpecNodeException(
              hash, TUPLE, "data", 0, ValSpec.class, ConstSpec.class));
    }

    @Test
    public void with_corrupted_element_spec() throws Exception {
      Hash hash =
          hash(
              hash(TUPLE.marker()),
              hash(
                  corruptedArraySpecHash(),
                  hash(strSpec())));
      assertThatGetSpec(hash)
          .throwsException(new DecodeSpecNodeException(hash, TUPLE, "data[0]"))
          .withCause(corruptedArraySpecException());
    }
  }

  @Nested
  class _construct_spec {
    @Test
    public void learn_creating_spec() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save Construct spec in HashedDb.
       */
      Hash hash = hash(
          hash(CONSTRUCT.marker()),
          hash(tupleSpec(list(intSpec(), strSpec())))
      );
      assertThat(hash)
          .isEqualTo(constructSpec(list(intSpec(), strSpec())).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_spec_without_data(CONSTRUCT);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_spec_with_additional_data(CONSTRUCT);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_spec(CONSTRUCT);
    }

    @Test
    public void with_corrupted_spec_as_data() throws Exception {
      test_spec_with_corrupted_spec_as_data(CONSTRUCT);
    }

    @Test
    public void with_evaluation_spec_being_expr_spec() throws Exception {
      test_spec_with_data_spec_being_expr_spec(CONSTRUCT, TupleSpec.class);
    }

    @Test
    public void with_evaluation_spec_not_being_tuple_spec() throws Exception {
      Hash hash = hash(
          hash(CONSTRUCT.marker()),
          hash(intSpec())
      );
      assertThatGetSpec(hash)
          .throwsException(new UnexpectedSpecNodeException(
              hash, CONSTRUCT, DATA_PATH, TupleSpec.class, IntSpec.class));
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
    assertCall(() -> specDb().get(specHash))
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
    assertCall(() -> specDb().get(specHash))
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
      return assertCall(() -> specDb().get(hash));
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
  class _struct_spec {
    @Test
    public void learn_creating_spec() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save struct spec in HashedDb.
       */
      var name = "MyStruct";
      var field1 = intSpec();
      var field2 = strSpec();
      var name1 = "field1";
      var name2 = "field2";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              hash(name),
              hash(
                  hash(field1),
                  hash(field2)
              ),
              hash(
                  hash(name1),
                  hash(name2)
              )
          )
      );
      assertThat(hash).isEqualTo(
          structSpec(namedList(list(named(name1, field1), named(name2, field2)))).hash());
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
      Hash nowhere = Hash.of(33);
      var field1 = intSpec();
      var field2 = strSpec();
      var name1 = "field1";
      var name2 = "field2";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              nowhere,
              hash(
                  hash(field1),
                  hash(field2)
              ),
              hash(
                  hash(name1),
                  hash(name2)
              )
          )
      );
      assertThatGetSpec(hash)
          .throwsException(new DecodeSpecNodeException(hash, STRUCT, DATA_PATH + "[0]"));
    }

    @Test
    public void with_illegal_name() throws Exception {
      Hash nameHash = hash(illegalString());
      var field1 = intSpec();
      var field2 = strSpec();
      var name1 = "field1";
      var name2 = "field2";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              hash(nameHash),
              hash(
                  hash(field1),
                  hash(field2)
              ),
              hash(
                  hash(name1),
                  hash(name2)
              )
          )
      );
      assertThatGetSpec(hash)
          .throwsException(new DecodeSpecNodeException(hash, STRUCT, DATA_PATH + "[0]"))
          .withCause(DecodeStringException.class);
    }

//      TODO

    @Test
    public void with_elements_not_being_sequence_of_hashes() throws Exception {
      Hash notSequence = hash("abc");
      var name = "MyStruct";
      var name1 = "field1";
      var name2 = "field2";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              hash(name),
              notSequence,
              hash(
                  hash(name1),
                  hash(name2)
              )
          )
      );
      assertThatGetSpec(hash)
          .throwsException(new DecodeSpecNodeException(hash, STRUCT, "data[1]"));
    }

    @Test
    public void with_elements_being_sequence_of_non_spec() throws Exception {
      var name = "MyStruct";
      Hash stringHash = hash(strVal("abc"));
      var name1 = "field1";
      var name2 = "field2";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              hash(name),
              hash(
                  stringHash
              ),
              hash(
                  hash(name1),
                  hash(name2)
              )
          )
      );
      assertThatGetSpec(hash)
          .throwsException(new DecodeSpecNodeException(hash, STRUCT, "data[1][0]"))
          .withCause(new DecodeSpecException(stringHash));
    }

    @Test
    public void with_elements_being_sequence_of_expr_spec() throws Exception {
      var name = "MyStruct";
      var name1 = "field1";
      var name2 = "field2";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              hash(name),
              hash(
                  hash(constSpec())
              ),
              hash(
                  hash(name1),
                  hash(name2)
              )
          )
      );
      assertThatGetSpec(hash)
          .throwsException(new UnexpectedSpecNodeException(
              hash, STRUCT, "data[1]", 0, ValSpec.class, ConstSpec.class));
    }

    @Test
    public void with_corrupted_element_spec() throws Exception {
      var name = "MyStruct";
      var field1 = intSpec();
      var name1 = "field1";
      var name2 = "field2";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              hash(name),
              hash(
                  hash(field1),
                  corruptedArraySpecHash()
              ),
              hash(
                  hash(name1),
                  hash(name2)
              )
          )
      );
      assertThatGetSpec(hash)
          .throwsException(new DecodeSpecNodeException(hash, STRUCT, "data[1][1]"))
          .withCause(corruptedArraySpecException());
    }

    @Test
    public void with_names_size_different_than_items_size() throws Exception {
      var name = "MyStruct";
      var field1 = intSpec();
      var field2 = strSpec();
      var name1 = "field1";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              hash(name),
              hash(
                  hash(field1),
                  hash(field2)
              ),
              hash(
                  hash(name1)
              )
          )
      );
      assertThatGetSpec(hash)
          .throwsException(new DecodeStructSpecWrongNamesSizeException(hash, 2, 1));
    }

    @Test
    public void with_names_containing_illegal_string() throws Exception {
      var name = "MyStruct";
      var field1 = intSpec();
      var field2 = strSpec();
      Hash illegalString = hash(illegalString());
      var name1 = "field1";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              hash(name),
              hash(
                  hash(field1),
                  hash(field2)
              ),
              hash(
                  hash(name1),
                  illegalString
              )
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
      assertCall(() -> specDb().get(specHash))
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
