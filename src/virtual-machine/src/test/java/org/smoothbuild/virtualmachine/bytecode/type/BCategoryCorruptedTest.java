package org.smoothbuild.virtualmachine.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryDb.DATA_PATH;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryDb.FUNC_PARAMS_PATH;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.ARRAY;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.BLOB;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.BOOL;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.CALL;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.COMBINE;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.FUNC;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.IF_FUNC;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.INT;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.LAMBDA;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.MAP_FUNC;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.NATIVE_FUNC;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.ORDER;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.PICK;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.REFERENCE;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.SELECT;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.STRING;
import static org.smoothbuild.virtualmachine.bytecode.type.CategoryId.TUPLE;
import static org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeFuncCatWrongFuncTypeException.illegalIfFuncTypeExc;
import static org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeFuncCatWrongFuncTypeException.illegalMapFuncTypeExc;

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
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeCatException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeCatIllegalIdException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeCatNodeException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeCatRootException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeCatWrongChainSizeException;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeCatWrongNodeCatException;
import org.smoothbuild.virtualmachine.bytecode.type.oper.BReferenceCategory;
import org.smoothbuild.virtualmachine.bytecode.type.value.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BFuncType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BIntType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BStringType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BCategoryCorruptedTest extends TestingVirtualMachine {
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

    private void test_base_type_with_additional_child(CategoryId id) throws Exception {
      var hash = hash(hash(id.byteMarker()), hash("abc"));
      assertThatGet(hash).throwsException(new DecodeCatRootException(hash, id, 2, 1));
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
        assert_reading_cat_without_data_causes_exc(ARRAY);
      }

      @Test
      public void with_additional_data() throws Exception {
        assert_reading_cat_with_additional_data_causes_exc(ARRAY);
      }

      @Test
      public void with_data_hash_pointing_nowhere() throws Exception {
        assert_reading_cat_with_data_pointing_nowhere_causes_exc(ARRAY);
      }

      @Test
      public void with_corrupted_type_as_data() throws Exception {
        assert_reading_cat_with_corrupted_type_as_data_causes_exc(ARRAY);
      }

      @Test
      public void with_type_being_oper_type() throws Exception {
        var hash = hash(hash(ARRAY.byteMarker()), hash(varCB()));
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongNodeCatException(
                hash, ARRAY, DATA_PATH, BType.class, BReferenceCategory.class));
      }
    }

    @Nested
    class _expression_func extends _abstract_func_category_test_suite {
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
      protected CategoryId categoryId() {
        return LAMBDA;
      }
    }

    @Nested
    class _if_func extends _abstract_func_category_test_suite {
      @Test
      public void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save if func category in HashedDb.
         */
        var specHash = hash(
            hash(IF_FUNC.byteMarker()), hash(funcTB(list(boolTB(), intTB(), intTB()), intTB())));
        assertThat(specHash).isEqualTo(ifFuncCB(intTB()).hash());
      }

      @Test
      public void illegal_func_type_causes_error() throws Exception {
        var illegalIfType = funcTB(list(boolTB(), intTB(), intTB()), blobTB());
        var categoryHash = hash(hash(IF_FUNC.byteMarker()), hash(illegalIfType));
        assertCall(() -> categoryDb().get(categoryHash))
            .throwsException(illegalIfFuncTypeExc(categoryHash, illegalIfType));
      }

      @Override
      protected CategoryId categoryId() {
        return IF_FUNC;
      }
    }

    @Nested
    class _map_func extends _abstract_func_category_test_suite {
      @Test
      public void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save map func category in HashedDb.
         */
        var specHash = hash(
            hash(MAP_FUNC.byteMarker()),
            hash(funcTB(arrayTB(blobTB()), funcTB(blobTB(), intTB()), arrayTB(intTB()))));
        assertThat(specHash).isEqualTo(mapFuncCB(intTB(), blobTB()).hash());
      }

      @Test
      public void illegal_func_type_causes_error() throws Exception {
        var illegalType = funcTB(arrayTB(blobTB()), funcTB(stringTB(), intTB()), arrayTB(intTB()));
        var categoryHash = hash(hash(MAP_FUNC.byteMarker()), hash(illegalType));
        assertCall(() -> categoryDb().get(categoryHash))
            .throwsException(illegalMapFuncTypeExc(categoryHash, illegalType));
      }

      @Override
      protected CategoryId categoryId() {
        return MAP_FUNC;
      }
    }

    @Nested
    class _native_func extends _abstract_func_category_test_suite {
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
      protected CategoryId categoryId() {
        return NATIVE_FUNC;
      }
    }

    abstract class _abstract_func_category_test_suite {
      protected abstract CategoryId categoryId();

      @Test
      public void without_data() throws Exception {
        assert_reading_cat_without_data_causes_exc(categoryId());
      }

      @Test
      public void with_additional_data() throws Exception {
        assert_reading_cat_with_additional_data_causes_exc(categoryId());
      }

      @Test
      public void with_func_type_hash_pointing_nowhere() throws Exception {
        var dataHash = Hash.of(33);
        var typeHash = hash(hash(categoryId().byteMarker()), dataHash);
        assertCall(() -> categoryDb().get(typeHash))
            .throwsException(new DecodeCatNodeException(typeHash, categoryId(), DATA_PATH))
            .withCause(new DecodeCatException(dataHash));
      }

      @Test
      public void with_func_type_being_oper_type() throws Exception {
        var notFuncType = varCB(intTB());
        var typeHash = hash(hash(categoryId().byteMarker()), hash(notFuncType));
        assertCall(() -> categoryDb().get(typeHash))
            .throwsException(new DecodeCatWrongNodeCatException(
                typeHash, categoryId(), DATA_PATH, BFuncType.class, BReferenceCategory.class));
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
        assert_reading_cat_without_data_causes_exc(FUNC);
      }

      @Test
      public void with_additional_data() throws Exception {
        assert_reading_cat_with_additional_data_causes_exc(FUNC);
      }

      @Test
      public void with_data_hash_pointing_nowhere() throws Exception {
        assert_reading_cat_with_data_pointing_nowhere_instead_of_being_chain_causes_exc(FUNC);
      }

      @Test
      public void with_data_not_being_hash_chain() throws Exception {
        var notHashOfChain = hash("abc");
        var hash = hash(hash(FUNC.byteMarker()), notHashOfChain);
        assertThatGet(hash).throwsException(new DecodeCatNodeException(hash, FUNC, DATA_PATH));
      }

      @Test
      public void with_data_having_three_elements() throws Exception {
        var paramTs = tupleTB(stringTB(), boolTB());
        var resultT = intTB();
        var hash = hash(hash(FUNC.byteMarker()), hash(hash(paramTs), hash(resultT), hash(resultT)));
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongChainSizeException(hash, FUNC, DATA_PATH, 2, 3));
      }

      @Test
      public void with_data_having_one_elements() throws Exception {
        var paramTs = tupleTB(stringTB(), boolTB());
        var hash = hash(hash(FUNC.byteMarker()), hash(hash(paramTs)));
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongChainSizeException(hash, FUNC, DATA_PATH, 2, 1));
      }

      @ParameterizedTest
      @ArgumentsSource(IllegalArrayByteSizesProvider.class)
      public void with_data_chain_size_different_than_multiple_of_hash_size(int byteCount)
          throws Exception {
        var notHashOfChain = hash(ByteString.of(new byte[byteCount]));
        var typeHash = hash(hash(FUNC.byteMarker()), notHashOfChain);
        assertCall(() -> ((BFuncType) categoryDb().get(typeHash)).result())
            .throwsException(new DecodeCatNodeException(typeHash, FUNC, DATA_PATH))
            .withCause(
                new DecodeHashChainException(notHashOfChain, byteCount % Hash.lengthInBytes()));
      }

      @Test
      public void with_result_pointing_nowhere() throws Exception {
        var paramTypes = tupleTB(stringTB(), boolTB());
        var nowhere = Hash.of(33);
        var typeHash = hash(hash(FUNC.byteMarker()), hash(hash(paramTypes), nowhere));
        assertCall(() -> categoryDb().get(typeHash))
            .throwsException(new DecodeCatNodeException(typeHash, FUNC, CategoryDb.FUNC_RES_PATH))
            .withCause(new DecodeCatException(nowhere));
      }

      @Test
      public void with_result_being_oper_type() throws Exception {
        var paramType = tupleTB(stringTB(), boolTB());
        var typeHash = hash(hash(FUNC.byteMarker()), hash(hash(paramType), hash(varCB())));
        assertCall(() -> categoryDb().get(typeHash))
            .throwsException(new DecodeCatWrongNodeCatException(
                typeHash, FUNC, CategoryDb.FUNC_RES_PATH, BType.class, BReferenceCategory.class));
      }

      @Test
      public void with_result_type_corrupted() throws Exception {
        var paramTypes = tupleTB(stringTB(), boolTB());
        var typeHash = hash(hash(FUNC.byteMarker()), hash(hash(paramTypes), corruptedArrayTHash()));
        assertCall(() -> categoryDb().get(typeHash))
            .throwsException(new DecodeCatNodeException(typeHash, FUNC, CategoryDb.FUNC_RES_PATH))
            .withCause(corruptedArrayTypeExc());
      }

      @Test
      public void with_params_pointing_nowhere() throws Exception {
        var nowhere = Hash.of(33);
        var typeHash = hash(hash(FUNC.byteMarker()), hash(nowhere, hash(intTB())));
        assertCall(() -> categoryDb().get(typeHash))
            .throwsException(new DecodeCatNodeException(typeHash, FUNC, FUNC_PARAMS_PATH))
            .withCause(new DecodeCatException(nowhere));
      }

      @Test
      public void with_params_not_being_tuple() throws Exception {
        var typeHash = hash(hash(FUNC.byteMarker()), hash(hash(stringTB()), hash(intTB())));
        assertThatGet(typeHash)
            .throwsException(new DecodeCatWrongNodeCatException(
                typeHash, FUNC, FUNC_PARAMS_PATH, BTupleType.class, BStringType.class));
      }

      @Test
      public void with_params_being_oper_type() throws Exception {
        var typeHash = hash(hash(FUNC.byteMarker()), hash(hash(varCB()), hash(intTB())));
        assertCall(() -> categoryDb().get(typeHash))
            .throwsException(new DecodeCatWrongNodeCatException(
                typeHash, FUNC, FUNC_PARAMS_PATH, BType.class, BReferenceCategory.class));
      }

      @Test
      public void with_params_type_corrupted() throws Exception {
        var typeHash = hash(hash(FUNC.byteMarker()), hash(corruptedArrayTHash(), hash(intTB())));
        assertCall(() -> categoryDb().get(typeHash))
            .throwsException(new DecodeCatNodeException(typeHash, FUNC, FUNC_PARAMS_PATH))
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
        assert_reading_cat_without_data_causes_exc(TUPLE);
      }

      @Test
      public void with_additional_data() throws Exception {
        assert_reading_cat_with_additional_data_causes_exc(TUPLE);
      }

      @Test
      public void with_data_hash_pointing_nowhere() throws Exception {
        assert_reading_cat_with_data_pointing_nowhere_instead_of_being_chain_causes_exc(TUPLE);
      }

      @Test
      public void with_elements_not_being_hash_chain() throws Exception {
        var notHashOfChain = hash("abc");
        var hash = hash(hash(TUPLE.byteMarker()), notHashOfChain);
        assertThatGet(hash).throwsException(new DecodeCatNodeException(hash, TUPLE, DATA_PATH));
      }

      @Test
      public void with_elements_being_array_of_non_type() throws Exception {
        var stringHash = hash(stringB("abc"));
        var hash = hash(hash(TUPLE.byteMarker()), hash(stringHash));
        assertThatGet(hash)
            .throwsException(new DecodeCatNodeException(hash, TUPLE, "data[0]"))
            .withCause(new DecodeCatException(stringHash));
      }

      @Test
      public void with_elements_being_chain_of_oper_types() throws Exception {
        var hash = hash(hash(TUPLE.byteMarker()), hash(hash(varCB())));
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongNodeCatException(
                hash, TUPLE, "data", 0, BType.class, BReferenceCategory.class));
      }

      @Test
      public void with_corrupted_element_type() throws Exception {
        var hash = hash(hash(TUPLE.byteMarker()), hash(corruptedArrayTHash(), hash(stringTB())));
        assertThatGet(hash)
            .throwsException(new DecodeCatNodeException(hash, TUPLE, "data[0]"))
            .withCause(corruptedArrayTypeExc());
      }
    }
  }

  private void assert_reading_cat_without_data_causes_exc(CategoryId speckcategoryId)
      throws Exception {
    var hash = hash(hash(speckcategoryId.byteMarker()));
    assertThatGet(hash).throwsException(new DecodeCatRootException(hash, speckcategoryId, 1, 2));
  }

  private void assert_reading_cat_with_additional_data_causes_exc(CategoryId id) throws Exception {
    var hash = hash(hash(id.byteMarker()), hash(stringTB()), hash("corrupted"));
    assertThatGet(hash).throwsException(new DecodeCatRootException(hash, 3));
  }

  private void assert_reading_cat_with_data_pointing_nowhere_causes_exc(CategoryId id)
      throws Exception {
    var dataHash = Hash.of(33);
    var typeHash = hash(hash(id.byteMarker()), dataHash);
    assertCall(() -> categoryDb().get(typeHash))
        .throwsException(new DecodeCatNodeException(typeHash, id, DATA_PATH))
        .withCause(new DecodeCatException(dataHash));
  }

  private void assert_reading_cat_with_data_pointing_nowhere_instead_of_being_chain_causes_exc(
      CategoryId id) throws Exception {
    var dataHash = Hash.of(33);
    var typeHash = hash(hash(id.byteMarker()), dataHash);
    assertCall(() -> categoryDb().get(typeHash))
        .throwsException(new DecodeCatNodeException(typeHash, id, DATA_PATH))
        .withCause(new NoSuchDataException(dataHash));
  }

  private void assert_reading_cat_with_corrupted_type_as_data_causes_exc(CategoryId id)
      throws Exception {
    var hash = hash(hash(id.byteMarker()), corruptedArrayTHash());
    assertThatGet(hash)
        .throwsException(new DecodeCatNodeException(hash, id, DATA_PATH))
        .withCause(corruptedArrayTypeExc());
  }

  private ThrownExceptionSubject assertThatGet(Hash hash) {
    return assertCall(() -> categoryDb().get(hash));
  }

  private DecodeCatException illegalTypeMarkerException(Hash hash, int marker) {
    return new DecodeCatIllegalIdException(hash, (byte) marker);
  }

  private DecodeCatNodeException corruptedArrayTypeExc() throws Exception {
    return new DecodeCatNodeException(corruptedArrayTHash(), ARRAY, DATA_PATH);
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

  protected Hash hash(BCategory type) {
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
      class _oper_cat_tests extends AbstractOperCategoryTestSuite {
        protected _oper_cat_tests() {
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
      class _oper_cat_tests extends AbstractOperCategoryTestSuite {
        protected _oper_cat_tests() {
          super(COMBINE, BTupleType.class);
        }
      }

      @Test
      public void with_evaluation_type_not_being_tuple_type() throws Exception {
        var hash = hash(hash(COMBINE.byteMarker()), hash(intTB()));
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongNodeCatException(
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
      class _oper_cat_tests extends AbstractOperCategoryTestSuite {
        protected _oper_cat_tests() {
          super(ORDER, BArrayType.class);
        }
      }

      @Test
      public void with_evaluation_type_not_being_array_type() throws Exception {
        var hash = hash(hash(ORDER.byteMarker()), hash(intTB()));
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongNodeCatException(
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
      class _oper_cat_tests extends AbstractOperCategoryTestSuite {
        protected _oper_cat_tests() {
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
      class _oper_cat_tests extends AbstractOperCategoryTestSuite {
        protected _oper_cat_tests() {
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
      class _oper_cat_tests extends AbstractOperCategoryTestSuite {
        protected _oper_cat_tests() {
          super(SELECT);
        }
      }
    }

    private abstract class AbstractOperCategoryTestSuite {
      private final CategoryId categoryId;
      private final Class<? extends BCategory> type;

      protected AbstractOperCategoryTestSuite(CategoryId categoryId) {
        this(categoryId, BType.class);
      }

      protected AbstractOperCategoryTestSuite(
          CategoryId categoryId, Class<? extends BCategory> type) {
        this.categoryId = categoryId;
        this.type = type;
      }

      @Test
      public void without_data() throws Exception {
        assert_reading_cat_without_data_causes_exc(categoryId);
      }

      @Test
      public void with_additional_data() throws Exception {
        assert_reading_cat_with_additional_data_causes_exc(categoryId);
      }

      @Test
      public void with_data_hash_pointing_nowhere() throws Exception {
        assert_reading_cat_with_data_pointing_nowhere_causes_exc(categoryId);
      }

      @Test
      public void with_corrupted_type_as_data() throws Exception {
        assert_reading_cat_with_corrupted_type_as_data_causes_exc(categoryId);
      }

      @Test
      public void with_evaluation_type_being_oper_type() throws Exception {
        var hash = hash(hash(categoryId.byteMarker()), hash(varCB()));
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongNodeCatException(
                hash, categoryId, DATA_PATH, type, BReferenceCategory.class));
      }
    }
  }
}
