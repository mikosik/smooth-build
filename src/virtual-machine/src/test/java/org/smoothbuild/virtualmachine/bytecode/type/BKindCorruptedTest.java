package org.smoothbuild.virtualmachine.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.virtualmachine.bytecode.type.BKindDb.DATA_PATH;
import static org.smoothbuild.virtualmachine.bytecode.type.BKindDb.FUNC_PARAMS_PATH;
import static org.smoothbuild.virtualmachine.bytecode.type.KindId.ARRAY;
import static org.smoothbuild.virtualmachine.bytecode.type.KindId.BLOB;
import static org.smoothbuild.virtualmachine.bytecode.type.KindId.BOOL;
import static org.smoothbuild.virtualmachine.bytecode.type.KindId.CALL;
import static org.smoothbuild.virtualmachine.bytecode.type.KindId.COMBINE;
import static org.smoothbuild.virtualmachine.bytecode.type.KindId.FUNC;
import static org.smoothbuild.virtualmachine.bytecode.type.KindId.IF_FUNC;
import static org.smoothbuild.virtualmachine.bytecode.type.KindId.INT;
import static org.smoothbuild.virtualmachine.bytecode.type.KindId.LAMBDA;
import static org.smoothbuild.virtualmachine.bytecode.type.KindId.MAP_FUNC;
import static org.smoothbuild.virtualmachine.bytecode.type.KindId.NATIVE_FUNC;
import static org.smoothbuild.virtualmachine.bytecode.type.KindId.ORDER;
import static org.smoothbuild.virtualmachine.bytecode.type.KindId.PICK;
import static org.smoothbuild.virtualmachine.bytecode.type.KindId.REFERENCE;
import static org.smoothbuild.virtualmachine.bytecode.type.KindId.SELECT;
import static org.smoothbuild.virtualmachine.bytecode.type.KindId.STRING;
import static org.smoothbuild.virtualmachine.bytecode.type.KindId.TUPLE;
import static org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeFuncKindWrongFuncTypeException.illegalIfFuncTypeExc;
import static org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeFuncKindWrongFuncTypeException.illegalMapFuncTypeExc;

import okio.ByteString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.commontesting.AssertCall.ThrownExceptionSubject;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.IllegalArrayByteSizesProvider;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.DecodeHashChainException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.NoSuchDataException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeKindException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeKindIllegalIdException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeKindNodeException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeKindRootException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeKindWrongChainSizeException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeKindWrongNodeKindException;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BReferenceKind;
import org.smoothbuild.virtualmachine.bytecode.type.value.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BFuncType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BIntType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BStringType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BKindCorruptedTest extends TestingVirtualMachine {
  @Nested
  class _illegal_type_marker {
    @Test
    public void causes_exception() throws Exception {
      var hash = hash(hash((byte) 99));
      assertThatGet(hash).throwsException(illegalTypeMarkerException(hash, 99));
    }

    @Test
    public void with_additional_child() throws Exception {
      var hash = hash(hash((byte) 99), hash("corrupted"));
      assertThatGet(hash).throwsException(illegalTypeMarkerException(hash, 99));
    }
  }

  @Nested
  class _base {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save base type in HashedDb.
       */
      var hash = hash(hash(STRING.byteMarker()));
      assertThat(hash).isEqualTo(stringTB().hash());
    }

    @Test
    public void blob_with_additional_child() throws Exception {
      test_base_type_with_additional_child(BLOB);
    }

    @Test
    public void bool_with_additional_child() throws Exception {
      test_base_type_with_additional_child(BOOL);
    }

    @Test
    public void int_with_additional_child() throws Exception {
      test_base_type_with_additional_child(INT);
    }

    @Test
    public void string_with_additional_child() throws Exception {
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
      public void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save array type in HashedDb.
         */
        var hash = hash(hash(ARRAY.byteMarker()), hash(stringTB()));
        assertThat(hash).isEqualTo(arrayTB(stringTB()).hash());
      }

      @Test
      public void without_data() throws Exception {
        assert_reading_kind_without_data_causes_exc(ARRAY);
      }

      @Test
      public void with_additional_data() throws Exception {
        assert_reading_kind_with_additional_data_causes_exc(ARRAY);
      }

      @Test
      public void with_data_hash_pointing_nowhere() throws Exception {
        assert_reading_kind_with_data_pointing_nowhere_causes_exc(ARRAY);
      }

      @Test
      public void with_corrupted_type_as_data() throws Exception {
        assert_reading_kind_with_corrupted_type_as_data_causes_exc(ARRAY);
      }

      @Test
      public void with_type_being_oper_type() throws Exception {
        var hash = hash(hash(ARRAY.byteMarker()), hash(varCB()));
        assertThatGet(hash)
            .throwsException(new DecodeKindWrongNodeKindException(
                hash, ARRAY, DATA_PATH, BType.class, BReferenceKind.class));
      }
    }

    @Nested
    class _expression_func extends _abstract_func_kind_test_suite {
      @Test
      public void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save expression function type in HashedDb.
         */
        var specHash = hash(hash(LAMBDA.byteMarker()), hash(funcTB(stringTB(), boolTB(), intTB())));
        assertThat(specHash).isEqualTo(lambdaCB(stringTB(), boolTB(), intTB()).hash());
      }

      @Override
      protected KindId kindId() {
        return LAMBDA;
      }
    }

    @Nested
    class _if_func extends _abstract_func_kind_test_suite {
      @Test
      public void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save if func kind in HashedDb.
         */
        var specHash = hash(
            hash(IF_FUNC.byteMarker()), hash(funcTB(list(boolTB(), intTB(), intTB()), intTB())));
        assertThat(specHash).isEqualTo(ifFuncCB(intTB()).hash());
      }

      @Test
      public void illegal_func_type_causes_error() throws Exception {
        var illegalIfType = funcTB(list(boolTB(), intTB(), intTB()), blobTB());
        var kindHash = hash(hash(IF_FUNC.byteMarker()), hash(illegalIfType));
        assertCall(() -> kindDb().get(kindHash))
            .throwsException(illegalIfFuncTypeExc(kindHash, illegalIfType));
      }

      @Override
      protected KindId kindId() {
        return IF_FUNC;
      }
    }

    @Nested
    class _map_func extends _abstract_func_kind_test_suite {
      @Test
      public void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save map func kind in HashedDb.
         */
        var specHash = hash(
            hash(MAP_FUNC.byteMarker()),
            hash(funcTB(arrayTB(blobTB()), funcTB(blobTB(), intTB()), arrayTB(intTB()))));
        assertThat(specHash).isEqualTo(mapFuncCB(intTB(), blobTB()).hash());
      }

      @Test
      public void illegal_func_type_causes_error() throws Exception {
        var illegalType = funcTB(arrayTB(blobTB()), funcTB(stringTB(), intTB()), arrayTB(intTB()));
        var kindHash = hash(hash(MAP_FUNC.byteMarker()), hash(illegalType));
        assertCall(() -> kindDb().get(kindHash))
            .throwsException(illegalMapFuncTypeExc(kindHash, illegalType));
      }

      @Override
      protected KindId kindId() {
        return MAP_FUNC;
      }
    }

    @Nested
    class _native_func extends _abstract_func_kind_test_suite {
      @Test
      public void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save func type in HashedDb.
         */
        var specHash =
            hash(hash(NATIVE_FUNC.byteMarker()), hash(funcTB(stringTB(), boolTB(), intTB())));
        assertThat(specHash)
            .isEqualTo(nativeFuncCB(stringTB(), boolTB(), intTB()).hash());
      }

      @Override
      protected KindId kindId() {
        return NATIVE_FUNC;
      }
    }

    abstract class _abstract_func_kind_test_suite {
      protected abstract KindId kindId();

      @Test
      public void without_data() throws Exception {
        assert_reading_kind_without_data_causes_exc(kindId());
      }

      @Test
      public void with_additional_data() throws Exception {
        assert_reading_kind_with_additional_data_causes_exc(kindId());
      }

      @Test
      public void with_func_type_hash_pointing_nowhere() throws Exception {
        var dataHash = Hash.of(33);
        var typeHash = hash(hash(kindId().byteMarker()), dataHash);
        assertCall(() -> kindDb().get(typeHash))
            .throwsException(new DecodeKindNodeException(typeHash, kindId(), DATA_PATH))
            .withCause(new DecodeKindException(dataHash));
      }

      @Test
      public void with_func_type_being_oper_type() throws Exception {
        var notFuncType = varCB(intTB());
        var typeHash = hash(hash(kindId().byteMarker()), hash(notFuncType));
        assertCall(() -> kindDb().get(typeHash))
            .throwsException(new DecodeKindWrongNodeKindException(
                typeHash, kindId(), DATA_PATH, BFuncType.class, BReferenceKind.class));
      }
    }

    @Nested
    class _func {
      @Test
      public void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save func type in HashedDb.
         */
        var specHash =
            hash(hash(FUNC.byteMarker()), hash(hash(tupleTB(stringTB(), boolTB())), hash(intTB())));
        assertThat(specHash).isEqualTo(funcTB(stringTB(), boolTB(), intTB()).hash());
      }

      @Test
      public void without_data() throws Exception {
        assert_reading_kind_without_data_causes_exc(FUNC);
      }

      @Test
      public void with_additional_data() throws Exception {
        assert_reading_kind_with_additional_data_causes_exc(FUNC);
      }

      @Test
      public void with_data_hash_pointing_nowhere() throws Exception {
        assert_reading_kind_with_data_pointing_nowhere_instead_of_being_chain_causes_exc(FUNC);
      }

      @Test
      public void with_data_not_being_hash_chain() throws Exception {
        var notHashOfChain = hash("abc");
        var hash = hash(hash(FUNC.byteMarker()), notHashOfChain);
        assertThatGet(hash).throwsException(new DecodeKindNodeException(hash, FUNC, DATA_PATH));
      }

      @Test
      public void with_data_having_three_elements() throws Exception {
        var paramTs = tupleTB(stringTB(), boolTB());
        var resultT = intTB();
        var hash = hash(hash(FUNC.byteMarker()), hash(hash(paramTs), hash(resultT), hash(resultT)));
        assertThatGet(hash)
            .throwsException(new DecodeKindWrongChainSizeException(hash, FUNC, DATA_PATH, 2, 3));
      }

      @Test
      public void with_data_having_one_elements() throws Exception {
        var paramTs = tupleTB(stringTB(), boolTB());
        var hash = hash(hash(FUNC.byteMarker()), hash(hash(paramTs)));
        assertThatGet(hash)
            .throwsException(new DecodeKindWrongChainSizeException(hash, FUNC, DATA_PATH, 2, 1));
      }

      @ParameterizedTest
      @ArgumentsSource(IllegalArrayByteSizesProvider.class)
      public void with_data_chain_size_different_than_multiple_of_hash_size(int byteCount)
          throws Exception {
        var notHashOfChain = hash(ByteString.of(new byte[byteCount]));
        var typeHash = hash(hash(FUNC.byteMarker()), notHashOfChain);
        assertCall(() -> ((BFuncType) kindDb().get(typeHash)).result())
            .throwsException(new DecodeKindNodeException(typeHash, FUNC, DATA_PATH))
            .withCause(
                new DecodeHashChainException(notHashOfChain, byteCount % Hash.lengthInBytes()));
      }

      @Test
      public void with_result_pointing_nowhere() throws Exception {
        var paramTypes = tupleTB(stringTB(), boolTB());
        var nowhere = Hash.of(33);
        var typeHash = hash(hash(FUNC.byteMarker()), hash(hash(paramTypes), nowhere));
        assertCall(() -> kindDb().get(typeHash))
            .throwsException(new DecodeKindNodeException(typeHash, FUNC, BKindDb.FUNC_RES_PATH))
            .withCause(new DecodeKindException(nowhere));
      }

      @Test
      public void with_result_being_oper_type() throws Exception {
        var paramType = tupleTB(stringTB(), boolTB());
        var typeHash = hash(hash(FUNC.byteMarker()), hash(hash(paramType), hash(varCB())));
        assertCall(() -> kindDb().get(typeHash))
            .throwsException(new DecodeKindWrongNodeKindException(
                typeHash, FUNC, BKindDb.FUNC_RES_PATH, BType.class, BReferenceKind.class));
      }

      @Test
      public void with_result_type_corrupted() throws Exception {
        var paramTypes = tupleTB(stringTB(), boolTB());
        var typeHash = hash(hash(FUNC.byteMarker()), hash(hash(paramTypes), corruptedArrayTHash()));
        assertCall(() -> kindDb().get(typeHash))
            .throwsException(new DecodeKindNodeException(typeHash, FUNC, BKindDb.FUNC_RES_PATH))
            .withCause(corruptedArrayTypeExc());
      }

      @Test
      public void with_params_pointing_nowhere() throws Exception {
        var nowhere = Hash.of(33);
        var typeHash = hash(hash(FUNC.byteMarker()), hash(nowhere, hash(intTB())));
        assertCall(() -> kindDb().get(typeHash))
            .throwsException(new DecodeKindNodeException(typeHash, FUNC, FUNC_PARAMS_PATH))
            .withCause(new DecodeKindException(nowhere));
      }

      @Test
      public void with_params_not_being_tuple() throws Exception {
        var typeHash = hash(hash(FUNC.byteMarker()), hash(hash(stringTB()), hash(intTB())));
        assertThatGet(typeHash)
            .throwsException(new DecodeKindWrongNodeKindException(
                typeHash, FUNC, FUNC_PARAMS_PATH, BTupleType.class, BStringType.class));
      }

      @Test
      public void with_params_being_oper_type() throws Exception {
        var typeHash = hash(hash(FUNC.byteMarker()), hash(hash(varCB()), hash(intTB())));
        assertCall(() -> kindDb().get(typeHash))
            .throwsException(new DecodeKindWrongNodeKindException(
                typeHash, FUNC, FUNC_PARAMS_PATH, BType.class, BReferenceKind.class));
      }

      @Test
      public void with_params_type_corrupted() throws Exception {
        var typeHash = hash(hash(FUNC.byteMarker()), hash(corruptedArrayTHash(), hash(intTB())));
        assertCall(() -> kindDb().get(typeHash))
            .throwsException(new DecodeKindNodeException(typeHash, FUNC, FUNC_PARAMS_PATH))
            .withCause(corruptedArrayTypeExc());
      }
    }

    @Nested
    class _tuple {
      @Test
      public void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save Tuple type in HashedDb.
         */
        var hash = hash(hash(TUPLE.byteMarker()), hash(hash(stringTB()), hash(stringTB())));
        assertThat(hash).isEqualTo(personTB().hash());
      }

      @Test
      public void without_data() throws Exception {
        assert_reading_kind_without_data_causes_exc(TUPLE);
      }

      @Test
      public void with_additional_data() throws Exception {
        assert_reading_kind_with_additional_data_causes_exc(TUPLE);
      }

      @Test
      public void with_data_hash_pointing_nowhere() throws Exception {
        assert_reading_kind_with_data_pointing_nowhere_instead_of_being_chain_causes_exc(TUPLE);
      }

      @Test
      public void with_elements_not_being_hash_chain() throws Exception {
        var notHashOfChain = hash("abc");
        var hash = hash(hash(TUPLE.byteMarker()), notHashOfChain);
        assertThatGet(hash).throwsException(new DecodeKindNodeException(hash, TUPLE, DATA_PATH));
      }

      @Test
      public void with_elements_being_array_of_non_type() throws Exception {
        var stringHash = hash(stringB("abc"));
        var hash = hash(hash(TUPLE.byteMarker()), hash(stringHash));
        assertThatGet(hash)
            .throwsException(new DecodeKindNodeException(hash, TUPLE, "data[0]"))
            .withCause(new DecodeKindException(stringHash));
      }

      @Test
      public void with_elements_being_chain_of_oper_types() throws Exception {
        var hash = hash(hash(TUPLE.byteMarker()), hash(hash(varCB())));
        assertThatGet(hash)
            .throwsException(new DecodeKindWrongNodeKindException(
                hash, TUPLE, "data", 0, BType.class, BReferenceKind.class));
      }

      @Test
      public void with_corrupted_element_type() throws Exception {
        var hash = hash(hash(TUPLE.byteMarker()), hash(corruptedArrayTHash(), hash(stringTB())));
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
    var hash = hash(hash(id.byteMarker()), hash(stringTB()), hash("corrupted"));
    assertThatGet(hash).throwsException(new DecodeKindRootException(hash, 3));
  }

  private void assert_reading_kind_with_data_pointing_nowhere_causes_exc(KindId id)
      throws Exception {
    var dataHash = Hash.of(33);
    var typeHash = hash(hash(id.byteMarker()), dataHash);
    assertCall(() -> kindDb().get(typeHash))
        .throwsException(new DecodeKindNodeException(typeHash, id, DATA_PATH))
        .withCause(new DecodeKindException(dataHash));
  }

  private void assert_reading_kind_with_data_pointing_nowhere_instead_of_being_chain_causes_exc(
      KindId id) throws Exception {
    var dataHash = Hash.of(33);
    var typeHash = hash(hash(id.byteMarker()), dataHash);
    assertCall(() -> kindDb().get(typeHash))
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
    return assertCall(() -> kindDb().get(hash));
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
    return hashedDb().writeString(string);
  }

  protected Hash hash(boolean value) throws Exception {
    return hash((byte) (value ? 1 : 0));
  }

  protected Hash hash(byte value) throws Exception {
    return hashedDb().writeData(sink -> sink.writeByte(value));
  }

  protected Hash hash(ByteString bytes) throws Exception {
    return hashedDb().writeData(sink -> sink.write(bytes));
  }

  protected Hash hash(BExpr expr) {
    return expr.hash();
  }

  protected Hash hash(BKind type) {
    return type.hash();
  }

  protected Hash hash(Hash... hashes) throws HashedDbException {
    return hashedDb().writeHashChain(hashes);
  }

  @Nested
  class _expr {
    @Nested
    class _call {
      @Test
      public void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save call type in HashedDb.
         */
        var hash = hash(hash(CALL.byteMarker()), hash(intTB()));
        assertThat(hash).isEqualTo(callCB(intTB()).hash());
      }

      @Nested
      class _oper_kind_tests extends AbstractOperKindTestSuite {
        protected _oper_kind_tests() {
          super(CALL);
        }
      }
    }

    @Nested
    class _combine {
      @Test
      public void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save Combine type in HashedDb.
         */
        var hash = hash(hash(COMBINE.byteMarker()), hash(tupleTB(intTB(), stringTB())));
        assertThat(hash).isEqualTo(combineCB(intTB(), stringTB()).hash());
      }

      @Nested
      class _oper_kind_tests extends AbstractOperKindTestSuite {
        protected _oper_kind_tests() {
          super(COMBINE, BTupleType.class);
        }
      }

      @Test
      public void with_evaluation_type_not_being_tuple_type() throws Exception {
        var hash = hash(hash(COMBINE.byteMarker()), hash(intTB()));
        assertThatGet(hash)
            .throwsException(new DecodeKindWrongNodeKindException(
                hash, COMBINE, DATA_PATH, BTupleType.class, BIntType.class));
      }
    }

    @Nested
    class _order {
      @Test
      public void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save Order type in HashedDb.
         */
        var hash = hash(hash(ORDER.byteMarker()), hash(arrayTB(intTB())));
        assertThat(hash).isEqualTo(orderCB(intTB()).hash());
      }

      @Nested
      class _oper_kind_tests extends AbstractOperKindTestSuite {
        protected _oper_kind_tests() {
          super(ORDER, BArrayType.class);
        }
      }

      @Test
      public void with_evaluation_type_not_being_array_type() throws Exception {
        var hash = hash(hash(ORDER.byteMarker()), hash(intTB()));
        assertThatGet(hash)
            .throwsException(new DecodeKindWrongNodeKindException(
                hash, ORDER, DATA_PATH, BArrayType.class, BIntType.class));
      }
    }

    @Nested
    class _pick {
      @Test
      public void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save Pick type in HashedDb.
         */
        var hash = hash(hash(PICK.byteMarker()), hash(intTB()));
        assertThat(hash).isEqualTo(pickCB(intTB()).hash());
      }

      @Nested
      class _oper_kind_tests extends AbstractOperKindTestSuite {
        protected _oper_kind_tests() {
          super(PICK);
        }
      }
    }

    @Nested
    class _var {
      @Test
      public void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save variable in HashedDb.
         */
        var hash = hash(hash(REFERENCE.byteMarker()), hash(intTB()));
        assertThat(hash).isEqualTo(varCB(intTB()).hash());
      }

      @Nested
      class _oper_kind_tests extends AbstractOperKindTestSuite {
        protected _oper_kind_tests() {
          super(REFERENCE);
        }
      }
    }

    @Nested
    class _select {
      @Test
      public void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save Select type in HashedDb.
         */
        var hash = hash(hash(SELECT.byteMarker()), hash(intTB()));
        assertThat(hash).isEqualTo(selectCB(intTB()).hash());
      }

      @Nested
      class _oper_kind_tests extends AbstractOperKindTestSuite {
        protected _oper_kind_tests() {
          super(SELECT);
        }
      }
    }

    private abstract class AbstractOperKindTestSuite {
      private final KindId kindId;
      private final Class<? extends BKind> type;

      protected AbstractOperKindTestSuite(KindId kindId) {
        this(kindId, BType.class);
      }

      protected AbstractOperKindTestSuite(KindId kindId, Class<? extends BKind> type) {
        this.kindId = kindId;
        this.type = type;
      }

      @Test
      public void without_data() throws Exception {
        assert_reading_kind_without_data_causes_exc(kindId);
      }

      @Test
      public void with_additional_data() throws Exception {
        assert_reading_kind_with_additional_data_causes_exc(kindId);
      }

      @Test
      public void with_data_hash_pointing_nowhere() throws Exception {
        assert_reading_kind_with_data_pointing_nowhere_causes_exc(kindId);
      }

      @Test
      public void with_corrupted_type_as_data() throws Exception {
        assert_reading_kind_with_corrupted_type_as_data_causes_exc(kindId);
      }

      @Test
      public void with_evaluation_type_being_oper_type() throws Exception {
        var hash = hash(hash(kindId.byteMarker()), hash(varCB()));
        assertThatGet(hash)
            .throwsException(new DecodeKindWrongNodeKindException(
                hash, kindId, DATA_PATH, type, BReferenceKind.class));
      }
    }
  }
}
