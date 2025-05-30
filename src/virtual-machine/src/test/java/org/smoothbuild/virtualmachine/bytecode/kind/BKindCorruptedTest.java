package org.smoothbuild.virtualmachine.bytecode.kind;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.virtualmachine.bytecode.kind.BKindDb.DATA_PATH;
import static org.smoothbuild.virtualmachine.bytecode.kind.BKindDb.LAMBDA_PARAMS_PATH;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.ARRAY;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.BLOB;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.BOOL;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.CALL;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.CHOICE;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.CHOOSE;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.COMBINE;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.FOLD;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.IF;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.INT;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.INVOKE;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.LAMBDA;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.MAP;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.ORDER;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.PICK;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.REFERENCE;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.SELECT;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.STRING;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.SWITCH;
import static org.smoothbuild.virtualmachine.bytecode.kind.base.KindId.TUPLE;

import okio.ByteString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.commontesting.AssertCall.ThrownExceptionSubject;
import org.smoothbuild.virtualmachine.bytecode.expr.IllegalArrayByteSizesProvider;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.DecodeHashChainException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.NoSuchDataException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BChoiceType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BIntType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BReferenceKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BStringType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.KindId;
import org.smoothbuild.virtualmachine.bytecode.kind.exc.DecodeKindException;
import org.smoothbuild.virtualmachine.bytecode.kind.exc.DecodeKindIllegalIdException;
import org.smoothbuild.virtualmachine.bytecode.kind.exc.DecodeKindNodeException;
import org.smoothbuild.virtualmachine.bytecode.kind.exc.DecodeKindRootException;
import org.smoothbuild.virtualmachine.bytecode.kind.exc.DecodeKindWrongChainSizeException;
import org.smoothbuild.virtualmachine.bytecode.kind.exc.DecodeKindWrongNodeKindException;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

public class BKindCorruptedTest extends VmTestContext {
  @Nested
  class _illegal_type_marker {
    @Test
    void causes_exception() throws Exception {
      var hash = hash(hash((byte) 99));
      assertThatGet(hash).throwsException(illegalTypeMarkerException(hash, 99));
    }

    @Test
    void with_additional_child() throws Exception {
      var hash = hash(hash((byte) 99), hash("corrupted"));
      assertThatGet(hash).throwsException(illegalTypeMarkerException(hash, 99));
    }
  }

  @Nested
  class _base {
    @Test
    void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save base type in HashedDb.
       */
      var hash = hash(hash(STRING.byteMarker()));
      assertThat(hash).isEqualTo(bStringType().hash());
    }

    @Test
    void blob_with_additional_child() throws Exception {
      test_base_type_with_additional_child(BLOB);
    }

    @Test
    void bool_with_additional_child() throws Exception {
      test_base_type_with_additional_child(BOOL);
    }

    @Test
    void int_with_additional_child() throws Exception {
      test_base_type_with_additional_child(INT);
    }

    @Test
    void string_with_additional_child() throws Exception {
      test_base_type_with_additional_child(STRING);
    }

    private void test_base_type_with_additional_child(KindId id) throws Exception {
      var hash = hash(hash(id.byteMarker()), hash("abc"));
      assertThatGet(hash).throwsException(new DecodeKindRootException(hash, id, 2, 1));
    }
  }

  @Nested
  class _composed {
    @Nested
    class _array {
      @Test
      void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save array type in HashedDb.
         */
        var hash = hash(hash(ARRAY.byteMarker()), hash(bStringType()));
        assertThat(hash).isEqualTo(bStringArrayType().hash());
      }

      @Test
      void without_data() throws Exception {
        assert_reading_kind_without_data_causes_exc(ARRAY);
      }

      @Test
      void with_additional_data() throws Exception {
        assert_reading_kind_with_additional_data_causes_exc(ARRAY);
      }

      @Test
      void with_data_hash_pointing_nowhere() throws Exception {
        assert_reading_kind_with_data_pointing_nowhere_causes_exc(ARRAY);
      }

      @Test
      void with_corrupted_type_as_data() throws Exception {
        assert_reading_kind_with_corrupted_type_as_data_causes_exc(ARRAY);
      }

      @Test
      void with_type_being_operation_type() throws Exception {
        var hash = hash(hash(ARRAY.byteMarker()), hash(bReferenceKind()));
        assertThatGet(hash)
            .throwsException(new DecodeKindWrongNodeKindException(
                hash, ARRAY, DATA_PATH, BType.class, BReferenceKind.class));
      }
    }

    @Nested
    class _lambda {
      @Test
      void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save lambda type in HashedDb.
         */
        var specHash = hash(
            hash(LAMBDA.byteMarker()),
            hash(hash(bTupleType(bStringType(), bBoolType())), hash(bIntType())));
        assertThat(specHash)
            .isEqualTo(bLambdaType(bStringType(), bBoolType(), bIntType()).hash());
      }

      @Test
      void without_data() throws Exception {
        assert_reading_kind_without_data_causes_exc(LAMBDA);
      }

      @Test
      void with_additional_data() throws Exception {
        assert_reading_kind_with_additional_data_causes_exc(LAMBDA);
      }

      @Test
      void with_data_hash_pointing_nowhere() throws Exception {
        assert_reading_kind_with_data_pointing_nowhere_instead_of_being_chain_causes_exc(LAMBDA);
      }

      @Test
      void with_data_not_being_hash_chain() throws Exception {
        var notHashOfChain = hash("abc");
        var hash = hash(hash(LAMBDA.byteMarker()), notHashOfChain);
        assertThatGet(hash).throwsException(new DecodeKindNodeException(hash, LAMBDA, DATA_PATH));
      }

      @Test
      void with_data_having_three_elements() throws Exception {
        var paramTs = bTupleType(bStringType(), bBoolType());
        var resultT = bIntType();
        var hash =
            hash(hash(LAMBDA.byteMarker()), hash(hash(paramTs), hash(resultT), hash(resultT)));
        assertThatGet(hash)
            .throwsException(new DecodeKindWrongChainSizeException(hash, LAMBDA, DATA_PATH, 2, 3));
      }

      @Test
      void with_data_having_one_elements() throws Exception {
        var paramTs = bTupleType(bStringType(), bBoolType());
        var hash = hash(hash(LAMBDA.byteMarker()), hash(hash(paramTs)));
        assertThatGet(hash)
            .throwsException(new DecodeKindWrongChainSizeException(hash, LAMBDA, DATA_PATH, 2, 1));
      }

      @ParameterizedTest
      @ArgumentsSource(IllegalArrayByteSizesProvider.class)
      public void with_data_chain_size_different_than_multiple_of_hash_size(int byteCount)
          throws Exception {
        var notHashOfChain = hash(ByteString.of(new byte[byteCount]));
        var typeHash = hash(hash(LAMBDA.byteMarker()), notHashOfChain);
        assertCall(() -> ((BLambdaType) provide().kindDb().get(typeHash)).result())
            .throwsException(new DecodeKindNodeException(typeHash, LAMBDA, DATA_PATH))
            .withCause(
                new DecodeHashChainException(notHashOfChain, byteCount % Hash.lengthInBytes()));
      }

      @Test
      void with_result_pointing_nowhere() throws Exception {
        var paramTypes = bTupleType(bStringType(), bBoolType());
        var nowhere = Hash.of(33);
        var typeHash = hash(hash(LAMBDA.byteMarker()), hash(hash(paramTypes), nowhere));
        assertCall(() -> provide().kindDb().get(typeHash))
            .throwsException(new DecodeKindNodeException(typeHash, LAMBDA, BKindDb.LAMBDA_RES_PATH))
            .withCause(new DecodeKindException(nowhere));
      }

      @Test
      void with_result_being_operation_type() throws Exception {
        var paramType = bTupleType(bStringType(), bBoolType());
        var typeHash =
            hash(hash(LAMBDA.byteMarker()), hash(hash(paramType), hash(bReferenceKind())));
        assertCall(() -> provide().kindDb().get(typeHash))
            .throwsException(new DecodeKindWrongNodeKindException(
                typeHash, LAMBDA, BKindDb.LAMBDA_RES_PATH, BType.class, BReferenceKind.class));
      }

      @Test
      void with_result_type_corrupted() throws Exception {
        var paramTypes = bTupleType(bStringType(), bBoolType());
        var typeHash =
            hash(hash(LAMBDA.byteMarker()), hash(hash(paramTypes), corruptedArrayTHash()));
        assertCall(() -> provide().kindDb().get(typeHash))
            .throwsException(new DecodeKindNodeException(typeHash, LAMBDA, BKindDb.LAMBDA_RES_PATH))
            .withCause(corruptedArrayTypeExc());
      }

      @Test
      void with_params_pointing_nowhere() throws Exception {
        var nowhere = Hash.of(33);
        var typeHash = hash(hash(LAMBDA.byteMarker()), hash(nowhere, hash(bIntType())));
        assertCall(() -> provide().kindDb().get(typeHash))
            .throwsException(new DecodeKindNodeException(typeHash, LAMBDA, LAMBDA_PARAMS_PATH))
            .withCause(new DecodeKindException(nowhere));
      }

      @Test
      void with_params_not_being_tuple() throws Exception {
        var typeHash = hash(hash(LAMBDA.byteMarker()), hash(hash(bStringType()), hash(bIntType())));
        assertThatGet(typeHash)
            .throwsException(new DecodeKindWrongNodeKindException(
                typeHash, LAMBDA, LAMBDA_PARAMS_PATH, BTupleType.class, BStringType.class));
      }

      @Test
      void with_params_being_operation_type() throws Exception {
        var typeHash =
            hash(hash(LAMBDA.byteMarker()), hash(hash(bReferenceKind()), hash(bIntType())));
        assertCall(() -> provide().kindDb().get(typeHash))
            .throwsException(new DecodeKindWrongNodeKindException(
                typeHash, LAMBDA, LAMBDA_PARAMS_PATH, BType.class, BReferenceKind.class));
      }

      @Test
      void with_params_type_corrupted() throws Exception {
        var typeHash =
            hash(hash(LAMBDA.byteMarker()), hash(corruptedArrayTHash(), hash(bIntType())));
        assertCall(() -> provide().kindDb().get(typeHash))
            .throwsException(new DecodeKindNodeException(typeHash, LAMBDA, LAMBDA_PARAMS_PATH))
            .withCause(corruptedArrayTypeExc());
      }
    }

    @Nested
    class _choice {
      @Test
      void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save Choice type in HashedDb.
         */
        var hash = hash(hash(CHOICE.byteMarker()), hash(hash(bBlobType()), hash(bIntType())));
        assertThat(hash).isEqualTo(bChoiceType(bBlobType(), bIntType()).hash());
      }

      @Test
      void without_data() throws Exception {
        assert_reading_kind_without_data_causes_exc(CHOICE);
      }

      @Test
      void with_additional_data() throws Exception {
        assert_reading_kind_with_additional_data_causes_exc(CHOICE);
      }

      @Test
      void with_data_hash_pointing_nowhere() throws Exception {
        assert_reading_kind_with_data_pointing_nowhere_instead_of_being_chain_causes_exc(CHOICE);
      }

      @Test
      void with_elements_not_being_hash_chain() throws Exception {
        var notHashOfChain = hash("abc");
        var hash = hash(hash(CHOICE.byteMarker()), notHashOfChain);
        assertThatGet(hash).throwsException(new DecodeKindNodeException(hash, CHOICE, DATA_PATH));
      }

      @Test
      void with_elements_being_array_of_non_type() throws Exception {
        var stringHash = hash(bString("abc"));
        var hash = hash(hash(CHOICE.byteMarker()), hash(stringHash));
        assertThatGet(hash)
            .throwsException(new DecodeKindNodeException(hash, CHOICE, "data[0]"))
            .withCause(new DecodeKindException(stringHash));
      }

      @Test
      void with_elements_being_chain_of_operation_types() throws Exception {
        var hash = hash(hash(CHOICE.byteMarker()), hash(hash(bReferenceKind())));
        assertThatGet(hash)
            .throwsException(new DecodeKindWrongNodeKindException(
                hash, CHOICE, "data", 0, BType.class, BReferenceKind.class));
      }

      @Test
      void with_corrupted_element_type() throws Exception {
        var hash =
            hash(hash(CHOICE.byteMarker()), hash(corruptedArrayTHash(), hash(bStringType())));
        assertThatGet(hash)
            .throwsException(new DecodeKindNodeException(hash, CHOICE, "data[0]"))
            .withCause(corruptedArrayTypeExc());
      }
    }

    @Nested
    class _tuple {
      @Test
      void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save Tuple type in HashedDb.
         */
        var hash = hash(hash(TUPLE.byteMarker()), hash(hash(bStringType()), hash(bStringType())));
        assertThat(hash).isEqualTo(bPersonType().hash());
      }

      @Test
      void without_data() throws Exception {
        assert_reading_kind_without_data_causes_exc(TUPLE);
      }

      @Test
      void with_additional_data() throws Exception {
        assert_reading_kind_with_additional_data_causes_exc(TUPLE);
      }

      @Test
      void with_data_hash_pointing_nowhere() throws Exception {
        assert_reading_kind_with_data_pointing_nowhere_instead_of_being_chain_causes_exc(TUPLE);
      }

      @Test
      void with_elements_not_being_hash_chain() throws Exception {
        var notHashOfChain = hash("abc");
        var hash = hash(hash(TUPLE.byteMarker()), notHashOfChain);
        assertThatGet(hash).throwsException(new DecodeKindNodeException(hash, TUPLE, DATA_PATH));
      }

      @Test
      void with_elements_being_array_of_non_type() throws Exception {
        var stringHash = hash(bString("abc"));
        var hash = hash(hash(TUPLE.byteMarker()), hash(stringHash));
        assertThatGet(hash)
            .throwsException(new DecodeKindNodeException(hash, TUPLE, "data[0]"))
            .withCause(new DecodeKindException(stringHash));
      }

      @Test
      void with_elements_being_chain_of_operation_types() throws Exception {
        var hash = hash(hash(TUPLE.byteMarker()), hash(hash(bReferenceKind())));
        assertThatGet(hash)
            .throwsException(new DecodeKindWrongNodeKindException(
                hash, TUPLE, "data", 0, BType.class, BReferenceKind.class));
      }

      @Test
      void with_corrupted_element_type() throws Exception {
        var hash = hash(hash(TUPLE.byteMarker()), hash(corruptedArrayTHash(), hash(bStringType())));
        assertThatGet(hash)
            .throwsException(new DecodeKindNodeException(hash, TUPLE, "data[0]"))
            .withCause(corruptedArrayTypeExc());
      }
    }
  }

  private void assert_reading_kind_without_data_causes_exc(KindId kindId) throws Exception {
    var hash = hash(hash(kindId.byteMarker()));
    assertThatGet(hash).throwsException(new DecodeKindRootException(hash, kindId, 1, 2));
  }

  private void assert_reading_kind_with_additional_data_causes_exc(KindId id) throws Exception {
    var hash = hash(hash(id.byteMarker()), hash(bStringType()), hash("corrupted"));
    assertThatGet(hash).throwsException(new DecodeKindRootException(hash, 3));
  }

  private void assert_reading_kind_with_data_pointing_nowhere_causes_exc(KindId id)
      throws Exception {
    var dataHash = Hash.of(33);
    var typeHash = hash(hash(id.byteMarker()), dataHash);
    assertCall(() -> provide().kindDb().get(typeHash))
        .throwsException(new DecodeKindNodeException(typeHash, id, DATA_PATH))
        .withCause(new DecodeKindException(dataHash));
  }

  private void assert_reading_kind_with_data_pointing_nowhere_instead_of_being_chain_causes_exc(
      KindId id) throws Exception {
    var dataHash = Hash.of(33);
    var typeHash = hash(hash(id.byteMarker()), dataHash);
    assertCall(() -> provide().kindDb().get(typeHash))
        .throwsException(new DecodeKindNodeException(typeHash, id, DATA_PATH))
        .withCause(new NoSuchDataException(dataHash));
  }

  private void assert_reading_kind_with_corrupted_type_as_data_causes_exc(KindId id)
      throws Exception {
    var hash = hash(hash(id.byteMarker()), corruptedArrayTHash());
    assertThatGet(hash)
        .throwsException(new DecodeKindNodeException(hash, id, DATA_PATH))
        .withCause(corruptedArrayTypeExc());
  }

  private ThrownExceptionSubject assertThatGet(Hash hash) {
    return assertCall(() -> provide().kindDb().get(hash));
  }

  private DecodeKindException illegalTypeMarkerException(Hash hash, int marker) {
    return new DecodeKindIllegalIdException(hash, (byte) marker);
  }

  private DecodeKindNodeException corruptedArrayTypeExc() throws Exception {
    return new DecodeKindNodeException(corruptedArrayTHash(), ARRAY, DATA_PATH);
  }

  private Hash corruptedArrayTHash() throws Exception {
    return hash(hash(ARRAY.byteMarker()), Hash.of(33));
  }

  protected Hash hash(String string) throws HashedDbException {
    return provide().hashedDb().writeString(string);
  }

  protected Hash hash(boolean value) throws Exception {
    return hash((byte) (value ? 1 : 0));
  }

  protected Hash hash(byte value) throws Exception {
    return provide().hashedDb().writeData(sink -> sink.writeByte(value));
  }

  protected Hash hash(ByteString bytes) throws Exception {
    return provide().hashedDb().writeData(sink -> sink.write(bytes));
  }

  protected Hash hash(BExpr expr) {
    return expr.hash();
  }

  protected Hash hash(BKind type) {
    return type.hash();
  }

  protected Hash hash(Hash... hashes) throws HashedDbException {
    return provide().hashedDb().writeHashChain(hashes);
  }

  @Nested
  class _operation {
    @Nested
    class _call {
      @Test
      void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save CALL kind in HashedDb.
         */
        var hash = hash(hash(CALL.byteMarker()), hash(bIntType()));
        assertThat(hash).isEqualTo(bCallKind(bIntType()).hash());
      }

      @Nested
      class _operation_kind_tests extends AbstractOperationKindTestSuite {
        protected _operation_kind_tests() {
          super(CALL);
        }
      }
    }

    @Nested
    class _choose {
      @Test
      void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save CHOOSE kind in HashedDb.
         */
        var hash = hash(hash(CHOOSE.byteMarker()), hash(bChoiceType()));
        assertThat(hash).isEqualTo(bChooseKind(bChoiceType()).hash());
      }

      @Nested
      class _operation_kind_tests extends AbstractOperationKindTestSuite {
        protected _operation_kind_tests() {
          super(CHOOSE, BChoiceType.class);
        }
      }

      @Test
      void with_evaluation_type_not_being_choice_type() throws Exception {
        var hash = hash(hash(CHOOSE.byteMarker()), hash(bIntType()));
        assertThatGet(hash)
            .throwsException(new DecodeKindWrongNodeKindException(
                hash, CHOOSE, DATA_PATH, BChoiceType.class, BIntType.class));
      }
    }

    @Nested
    class _combine {
      @Test
      void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save COMBINE kind in HashedDb.
         */
        var hash = hash(hash(COMBINE.byteMarker()), hash(bTupleType(bIntType(), bStringType())));
        assertThat(hash).isEqualTo(bCombineKind(bIntType(), bStringType()).hash());
      }

      @Nested
      class _operation_kind_tests extends AbstractOperationKindTestSuite {
        protected _operation_kind_tests() {
          super(COMBINE, BTupleType.class);
        }
      }

      @Test
      void with_evaluation_type_not_being_tuple_type() throws Exception {
        var hash = hash(hash(COMBINE.byteMarker()), hash(bIntType()));
        assertThatGet(hash)
            .throwsException(new DecodeKindWrongNodeKindException(
                hash, COMBINE, DATA_PATH, BTupleType.class, BIntType.class));
      }
    }

    @Nested
    class _if {
      @Test
      void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save IF kind in HashedDb.
         */
        var hash = hash(hash(IF.byteMarker()), hash(bIntType()));
        assertThat(hash).isEqualTo(bIfKind(bIntType()).hash());
      }

      @Nested
      class _operation_kind_tests extends AbstractOperationKindTestSuite {
        protected _operation_kind_tests() {
          super(IF);
        }
      }
    }

    @Nested
    class _invoke {
      @Test
      void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save INVOKE kind in HashedDb.
         */
        var hash = hash(hash(INVOKE.byteMarker()), hash(bIntType()));
        assertThat(hash).isEqualTo(bInvokeKind(bIntType()).hash());
      }

      @Nested
      class _operation_kind_tests extends AbstractOperationKindTestSuite {
        protected _operation_kind_tests() {
          super(INVOKE);
        }
      }
    }

    @Nested
    class _map {
      @Test
      void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save MAP kind in HashedDb.
         */
        var hash = hash(hash(MAP.byteMarker()), hash(bIntArrayType()));
        assertThat(hash).isEqualTo(bMapKind(bIntArrayType()).hash());
      }

      @Nested
      class _operation_kind_tests extends AbstractOperationKindTestSuite {
        protected _operation_kind_tests() {
          super(MAP, BArrayType.class);
        }
      }

      @Test
      void with_evaluation_type_not_being_array_type() throws Exception {
        var hash = hash(hash(MAP.byteMarker()), hash(bIntType()));
        assertThatGet(hash)
            .throwsException(new DecodeKindWrongNodeKindException(
                hash, MAP, DATA_PATH, BArrayType.class, BIntType.class));
      }
    }

    @Nested
    class _fold {
      @Test
      void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save FOLD kind in HashedDb.
         */
        var hash = hash(hash(FOLD.byteMarker()), hash(bIntType()));
        assertThat(hash).isEqualTo(bFoldKind(bIntType()).hash());
      }

      @Nested
      class _operation_kind_tests extends AbstractOperationKindTestSuite {
        protected _operation_kind_tests() {
          super(FOLD);
        }
      }
    }

    @Nested
    class _order {
      @Test
      void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save ORDER kind in HashedDb.
         */
        var hash = hash(hash(ORDER.byteMarker()), hash(bIntArrayType()));
        assertThat(hash).isEqualTo(bOrderKind(bIntType()).hash());
      }

      @Nested
      class _operation_kind_tests extends AbstractOperationKindTestSuite {
        protected _operation_kind_tests() {
          super(ORDER, BArrayType.class);
        }
      }

      @Test
      void with_evaluation_type_not_being_array_type() throws Exception {
        var hash = hash(hash(ORDER.byteMarker()), hash(bIntType()));
        assertThatGet(hash)
            .throwsException(new DecodeKindWrongNodeKindException(
                hash, ORDER, DATA_PATH, BArrayType.class, BIntType.class));
      }
    }

    @Nested
    class _pick {
      @Test
      void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save PICK kind in HashedDb.
         */
        var hash = hash(hash(PICK.byteMarker()), hash(bIntType()));
        assertThat(hash).isEqualTo(bPickKind(bIntType()).hash());
      }

      @Nested
      class _operation_kind_tests extends AbstractOperationKindTestSuite {
        protected _operation_kind_tests() {
          super(PICK);
        }
      }
    }

    @Nested
    class _reference {
      @Test
      void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save REFERENCE kind in HashedDb.
         */
        var hash = hash(hash(REFERENCE.byteMarker()), hash(bIntType()));
        assertThat(hash).isEqualTo(bReferenceKind(bIntType()).hash());
      }

      @Nested
      class _operation_kind_tests extends AbstractOperationKindTestSuite {
        protected _operation_kind_tests() {
          super(REFERENCE);
        }
      }
    }

    @Nested
    class _select {
      @Test
      void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save SELECT kind in HashedDb.
         */
        var hash = hash(hash(SELECT.byteMarker()), hash(bIntType()));
        assertThat(hash).isEqualTo(bSelectKind(bIntType()).hash());
      }

      @Nested
      class _operation_kind_tests extends AbstractOperationKindTestSuite {
        protected _operation_kind_tests() {
          super(SELECT);
        }
      }
    }

    @Nested
    class _switch {
      @Test
      void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save SWITCH kind in HashedDb.
         */
        var hash = hash(hash(SWITCH.byteMarker()), hash(bIntType()));
        assertThat(hash).isEqualTo(bSwitchKind(bIntType()).hash());
      }

      @Nested
      class _operation_kind_tests extends AbstractOperationKindTestSuite {
        protected _operation_kind_tests() {
          super(SWITCH);
        }
      }
    }

    private abstract class AbstractOperationKindTestSuite {
      private final KindId kindId;
      private final Class<? extends BKind> type;

      protected AbstractOperationKindTestSuite(KindId kindId) {
        this(kindId, BType.class);
      }

      protected AbstractOperationKindTestSuite(KindId kindId, Class<? extends BKind> type) {
        this.kindId = kindId;
        this.type = type;
      }

      @Test
      void without_data() throws Exception {
        assert_reading_kind_without_data_causes_exc(kindId);
      }

      @Test
      void with_additional_data() throws Exception {
        assert_reading_kind_with_additional_data_causes_exc(kindId);
      }

      @Test
      void with_data_hash_pointing_nowhere() throws Exception {
        assert_reading_kind_with_data_pointing_nowhere_causes_exc(kindId);
      }

      @Test
      void with_corrupted_type_as_data() throws Exception {
        assert_reading_kind_with_corrupted_type_as_data_causes_exc(kindId);
      }

      @Test
      void with_evaluation_type_being_operation_kind() throws Exception {
        var hash = hash(hash(kindId.byteMarker()), hash(bReferenceKind()));
        assertThatGet(hash)
            .throwsException(new DecodeKindWrongNodeKindException(
                hash, kindId, DATA_PATH, type, BReferenceKind.class));
      }
    }
  }
}
