package org.smoothbuild.vm.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.vm.bytecode.type.CategoryDb.DATA_PATH;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.ARRAY;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.BLOB;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.BOOL;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.CALL;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.COMBINE;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.EXPR_FUNC;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.FUNC;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.IF_FUNC;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.INT;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.MAP_FUNC;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.NATIVE_FUNC;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.ORDER;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.PICK;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.SELECT;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.STRING;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.TUPLE;
import static org.smoothbuild.vm.bytecode.type.CategoryKinds.VAR;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.common.AssertCall.ThrownExceptionSubject;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.IllegalArrayByteSizesProvider;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.hashed.HashingBufferedSink;
import org.smoothbuild.vm.bytecode.hashed.exc.DecodeHashSeqExc;
import org.smoothbuild.vm.bytecode.hashed.exc.HashedDbExc;
import org.smoothbuild.vm.bytecode.hashed.exc.NoSuchDataExc;
import org.smoothbuild.vm.bytecode.type.exc.DecodeCatExc;
import org.smoothbuild.vm.bytecode.type.exc.DecodeCatIllegalKindExc;
import org.smoothbuild.vm.bytecode.type.exc.DecodeCatNodeExc;
import org.smoothbuild.vm.bytecode.type.exc.DecodeCatRootExc;
import org.smoothbuild.vm.bytecode.type.exc.DecodeCatWrongNodeCatExc;
import org.smoothbuild.vm.bytecode.type.exc.DecodeCatWrongSeqSizeExc;
import org.smoothbuild.vm.bytecode.type.exc.DecodeFuncCatWrongFuncTypeExc;
import org.smoothbuild.vm.bytecode.type.oper.VarCB;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;
import org.smoothbuild.vm.bytecode.type.value.IntTB;
import org.smoothbuild.vm.bytecode.type.value.StringTB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

import okio.ByteString;

public class CategoryBCorruptedTest extends TestContext {
  @Nested
  class _illegal_type_marker {
    @Test
    public void causes_exception() throws Exception {
      var hash = hash(
          hash((byte) 99)
      );
      assertThatGet(hash)
          .throwsException(illegalTypeMarkerException(hash, 99));
    }

    @Test
    public void with_additional_child() throws Exception {
      var hash = hash(
          hash((byte) 99),
          hash("corrupted")
      );
      assertThatGet(hash)
          .throwsException(illegalTypeMarkerException(hash, 99));
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
      var hash = hash(
          hash(STRING.marker())
      );
      assertThat(hash)
          .isEqualTo(stringTB().hash());
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

    private void test_base_type_with_additional_child(CategoryKindB kind) throws Exception {
      var hash = hash(
          hash(kind.marker()),
          hash("abc")
      );
      assertThatGet(hash)
          .throwsException(new DecodeCatRootExc(hash, kind, 2, 1));
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
        var hash = hash(
            hash(ARRAY.marker()),
            hash(stringTB())
        );
        assertThat(hash)
            .isEqualTo(arrayTB(stringTB()).hash());
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
        var hash = hash(
            hash(ARRAY.marker()),
            hash(varCB())
        );
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongNodeCatExc(
                hash, ARRAY, DATA_PATH, TypeB.class, VarCB.class));
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
        var specHash = hash(
            hash(EXPR_FUNC.marker()),
            hash(funcTB(stringTB(), boolTB(), intTB()))
        );
        assertThat(specHash)
            .isEqualTo(exprFuncCB(stringTB(), boolTB(), intTB()).hash());
      }

      @Override
      protected CategoryKindB categoryKind() {
        return EXPR_FUNC;
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
            hash(IF_FUNC.marker()),
            hash(funcTB(list(boolTB(), intTB(), intTB()), intTB()))
        );
        assertThat(specHash)
            .isEqualTo(ifFuncCB(intTB()).hash());
      }

      @Test
      public void illegal_func_type_causes_error() throws Exception {
        var illegalIfType = funcTB(list(boolTB(), intTB(), intTB()), blobTB());
        var categoryHash = hash(
            hash(IF_FUNC.marker()),
            hash(illegalIfType)
        );
        assertCall(() -> categoryDb().get(categoryHash))
            .throwsException(
                DecodeFuncCatWrongFuncTypeExc.illegalIfFuncTypeExc(categoryHash, illegalIfType));
      }

      @Override
      protected CategoryKindB categoryKind() {
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
            hash(MAP_FUNC.marker()),
            hash(funcTB(arrayTB(blobTB()), funcTB(blobTB(), intTB()), arrayTB(intTB())))
        );
        assertThat(specHash)
            .isEqualTo(mapFuncCB(intTB(), blobTB()).hash());
      }

      @Test
      public void illegal_func_type_causes_error() throws Exception {
        var illegalType = funcTB(arrayTB(blobTB()), funcTB(stringTB(), intTB()), arrayTB(intTB()));
        var categoryHash = hash(
            hash(MAP_FUNC.marker()),
            hash(illegalType)
        );
        assertCall(() -> categoryDb().get(categoryHash))
            .throwsException(
                DecodeFuncCatWrongFuncTypeExc.illegalMapFuncTypeExc(categoryHash, illegalType));
      }

      @Override
      protected CategoryKindB categoryKind() {
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
        var specHash = hash(
            hash(NATIVE_FUNC.marker()),
            hash(funcTB(stringTB(), boolTB(), intTB()))
        );
        assertThat(specHash)
            .isEqualTo(nativeFuncCB(stringTB(), boolTB(), intTB()).hash());
      }

      @Override
      protected CategoryKindB categoryKind() {
        return NATIVE_FUNC;
      }
    }

    abstract class _abstract_func_category_test_suite {
      protected abstract CategoryKindB categoryKind();

      @Test
      public void without_data() throws Exception {
        assert_reading_cat_without_data_causes_exc(categoryKind());
      }

      @Test
      public void with_additional_data() throws Exception {
        assert_reading_cat_with_additional_data_causes_exc(categoryKind());
      }

      @Test
      public void with_func_type_hash_pointing_nowhere() throws Exception {
        var dataHash = Hash.of(33);
        var typeHash = hash(
            hash(categoryKind().marker()),
            dataHash
        );
        assertCall(() -> categoryDb().get(typeHash))
            .throwsException(new DecodeCatNodeExc(typeHash, categoryKind(), DATA_PATH))
            .withCause(new DecodeCatExc(dataHash));
      }

      @Test
      public void with_func_type_being_oper_type() throws Exception {
        var notFuncTB = varCB(intTB());
        var typeHash = hash(
            hash(categoryKind().marker()),
            hash(notFuncTB)
        );
        assertCall(() -> categoryDb().get(typeHash))
            .throwsException(new DecodeCatWrongNodeCatExc(
                typeHash, categoryKind(), DATA_PATH, FuncTB.class, VarCB.class));
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
        var specHash = hash(
            hash(FUNC.marker()),
            hash(
                hash(tupleTB(stringTB(), boolTB())),
                hash(intTB()))
        );
        assertThat(specHash)
            .isEqualTo(funcTB(stringTB(), boolTB(), intTB()).hash());
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
        assert_reading_cat_with_data_pointing_nowhere_instead_of_being_seq_causes_exc(
            FUNC);
      }

      @Test
      public void with_data_not_being_seq_of_hashes() throws Exception {
        var notHashOfSeq = hash("abc");
        var hash =
            hash(
                hash(FUNC.marker()),
                notHashOfSeq
            );
        assertThatGet(hash)
            .throwsException(new DecodeCatNodeExc(hash, FUNC, DATA_PATH));
      }

      @Test
      public void with_data_having_three_elems() throws Exception {
        var paramTs = tupleTB(stringTB(), boolTB());
        var resultT = intTB();
        var hash = hash(
            hash(FUNC.marker()),
            hash(
                hash(paramTs),
                hash(resultT),
                hash(resultT)
            )
        );
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongSeqSizeExc(hash, FUNC, DATA_PATH, 2, 3));
      }

      @Test
      public void with_data_having_one_elems() throws Exception {
        var paramTs = tupleTB(stringTB(), boolTB());
        var hash = hash(
            hash(FUNC.marker()),
            hash(
                hash(paramTs)
            )
        );
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongSeqSizeExc(hash, FUNC, DATA_PATH, 2, 1));
      }

      @ParameterizedTest
      @ArgumentsSource(IllegalArrayByteSizesProvider.class)
      public void with_data_seq_size_different_than_multiple_of_hash_size(int byteCount)
          throws Exception {
        var notHashOfSeq = hash(ByteString.of(new byte[byteCount]));
        var typeHash = hash(
            hash(FUNC.marker()),
            notHashOfSeq
        );
        assertCall(() -> ((FuncTB) categoryDb().get(typeHash)).result())
            .throwsException(new DecodeCatNodeExc(typeHash, FUNC, DATA_PATH))
            .withCause(new DecodeHashSeqExc(
                notHashOfSeq, byteCount % Hash.lengthInBytes()));
      }

      @Test
      public void with_result_pointing_nowhere() throws Exception {
        var paramTs = tupleTB(stringTB(), boolTB());
        var nowhere = Hash.of(33);
        var typeHash = hash(
            hash(FUNC.marker()),
            hash(
                hash(paramTs),
                nowhere
            )
        );
        assertCall(() -> categoryDb().get(typeHash))
            .throwsException(new DecodeCatNodeExc(typeHash, FUNC, CategoryDb.FUNC_RES_PATH))
            .withCause(new DecodeCatExc(nowhere));
      }

      @Test
      public void with_result_being_oper_type() throws Exception {
        var paramT = tupleTB(stringTB(), boolTB());
        var typeHash = hash(
            hash(FUNC.marker()),
            hash(
                hash(paramT),
                hash(varCB())
            )
        );
        assertCall(() -> categoryDb().get(typeHash))
            .throwsException(new DecodeCatWrongNodeCatExc(
                typeHash, FUNC, CategoryDb.FUNC_RES_PATH, TypeB.class, VarCB.class));
      }

      @Test
      public void with_result_type_corrupted() throws Exception {
        var paramTs = tupleTB(stringTB(), boolTB());
        var typeHash = hash(
            hash(FUNC.marker()),
            hash(
                hash(paramTs),
                corruptedArrayTHash()
            )
        );
        assertCall(() -> categoryDb().get(typeHash))
            .throwsException(new DecodeCatNodeExc(typeHash, FUNC, CategoryDb.FUNC_RES_PATH))
            .withCause(corruptedArrayTypeExc());
      }

      @Test
      public void with_params_pointing_nowhere() throws Exception {
        var nowhere = Hash.of(33);
        var typeHash = hash(
            hash(FUNC.marker()),
            hash(
                nowhere,
                hash(intTB())
            )
        );
        assertCall(() -> categoryDb().get(typeHash))
            .throwsException(new DecodeCatNodeExc(typeHash, FUNC, CategoryDb.FUNC_PARAMS_PATH))
            .withCause(new DecodeCatExc(nowhere));
      }

      @Test
      public void with_params_not_being_tuple() throws Exception {
        var typeHash = hash(
            hash(FUNC.marker()),
            hash(
                hash(stringTB()),
                hash(intTB())
            )
        );
        assertThatGet(typeHash)
            .throwsException(new DecodeCatWrongNodeCatExc(
                typeHash, FUNC, CategoryDb.FUNC_PARAMS_PATH, TupleTB.class, StringTB.class));
      }

      @Test
      public void with_params_being_oper_type() throws Exception {
        var typeHash = hash(
            hash(FUNC.marker()),
            hash(
                hash(varCB()),
                hash(intTB())
            )
        );
        assertCall(() -> categoryDb().get(typeHash))
            .throwsException(new DecodeCatWrongNodeCatExc(
                typeHash, FUNC, CategoryDb.FUNC_PARAMS_PATH, TypeB.class, VarCB.class));
      }

      @Test
      public void with_params_type_corrupted() throws Exception {
        var typeHash = hash(
            hash(FUNC.marker()),
            hash(
                corruptedArrayTHash(),
                hash(intTB())
            )
        );
        assertCall(() -> categoryDb().get(typeHash))
            .throwsException(new DecodeCatNodeExc(typeHash, FUNC, CategoryDb.FUNC_PARAMS_PATH))
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
        var hash = hash(
            hash(TUPLE.marker()),
            hash(
                hash(stringTB()),
                hash(stringTB())
            )
        );
        assertThat(hash)
            .isEqualTo(personTB().hash());
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
        assert_reading_cat_with_data_pointing_nowhere_instead_of_being_seq_causes_exc(
            TUPLE);
      }

      @Test
      public void with_elements_not_being_seq_of_hashes() throws Exception {
        Hash notHashOfSeq = hash("abc");
        Hash hash =
            hash(
                hash(TUPLE.marker()),
                notHashOfSeq
            );
        assertThatGet(hash)
            .throwsException(new DecodeCatNodeExc(hash, TUPLE, DATA_PATH));
      }

      @Test
      public void with_elements_being_array_of_non_type() throws Exception {
        Hash stringHash = hash(stringB("abc"));
        Hash hash =
            hash(
                hash(TUPLE.marker()),
                hash(
                    stringHash
                )
            );
        assertThatGet(hash)
            .throwsException(new DecodeCatNodeExc(hash, TUPLE, "data[0]"))
            .withCause(new DecodeCatExc(stringHash));
      }

      @Test
      public void with_elements_being_seq_of_oper_types() throws Exception {
        Hash hash =
            hash(
                hash(TUPLE.marker()),
                hash(
                    hash(varCB())
                )
            );
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongNodeCatExc(
                hash, TUPLE, "data", 0, TypeB.class, VarCB.class));
      }

      @Test
      public void with_corrupted_element_type() throws Exception {
        Hash hash =
            hash(
                hash(TUPLE.marker()),
                hash(
                    corruptedArrayTHash(),
                    hash(stringTB())));
        assertThatGet(hash)
            .throwsException(new DecodeCatNodeExc(hash, TUPLE, "data[0]"))
            .withCause(corruptedArrayTypeExc());
      }
    }
  }

  private void assert_reading_cat_without_data_causes_exc(CategoryKindB speckKind) throws Exception {
    Hash hash =
        hash(
            hash(speckKind.marker())
        );
    assertThatGet(hash)
        .throwsException(new DecodeCatRootExc(hash, speckKind, 1, 2));
  }

  private void assert_reading_cat_with_additional_data_causes_exc(CategoryKindB kind) throws Exception {
    var hash = hash(
        hash(kind.marker()),
        hash(stringTB()),
        hash("corrupted")
    );
    assertThatGet(hash)
        .throwsException(new DecodeCatRootExc(hash, 3));
  }

  private void assert_reading_cat_with_data_pointing_nowhere_causes_exc(CategoryKindB kind)
      throws Exception {
    Hash dataHash = Hash.of(33);
    Hash typeHash = hash(
        hash(kind.marker()),
        dataHash
    );
    assertCall(() -> categoryDb().get(typeHash))
        .throwsException(new DecodeCatNodeExc(typeHash, kind, DATA_PATH))
        .withCause(new DecodeCatExc(dataHash));
  }

  private void assert_reading_cat_with_data_pointing_nowhere_instead_of_being_seq_causes_exc(
      CategoryKindB kind) throws Exception {
    Hash dataHash = Hash.of(33);
    Hash typeHash = hash(
        hash(kind.marker()),
        dataHash
    );
    assertCall(() -> categoryDb().get(typeHash))
        .throwsException(new DecodeCatNodeExc(typeHash, kind, DATA_PATH))
        .withCause(new NoSuchDataExc(dataHash));
  }

  private void assert_reading_cat_with_corrupted_type_as_data_causes_exc(CategoryKindB kind)
      throws Exception {
    Hash hash =
        hash(
            hash(kind.marker()),
            corruptedArrayTHash());
    assertThatGet(hash)
        .throwsException(new DecodeCatNodeExc(hash, kind, DATA_PATH))
        .withCause(corruptedArrayTypeExc());
  }

  private ThrownExceptionSubject assertThatGet(Hash hash) {
      return assertCall(() -> categoryDb().get(hash));
  }

  private DecodeCatExc illegalTypeMarkerException(Hash hash, int marker) {
    return new DecodeCatIllegalKindExc(hash, (byte) marker);
  }

  private DecodeCatNodeExc corruptedArrayTypeExc() throws Exception {
    return new DecodeCatNodeExc(corruptedArrayTHash(), ARRAY, DATA_PATH);
  }

  private Hash corruptedArrayTHash() throws Exception {
    return hash(
        hash(ARRAY.marker()),
        Hash.of(33)
    );
  }

  protected Hash hash(String string) throws HashedDbExc {
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

  protected Hash hash(ExprB expr) {
    return expr.hash();
  }

  protected Hash hash(CategoryB type) {
    return type.hash();
  }

  protected Hash hash(Hash... hashes) throws HashedDbExc {
    return hashedDb().writeSeq(hashes);
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
        var hash = hash(
            hash(CALL.marker()),
            hash(intTB())
        );
        assertThat(hash)
            .isEqualTo(callCB(intTB()).hash());
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
        var hash = hash(
            hash(COMBINE.marker()),
            hash(tupleTB(intTB(), stringTB()))
        );
        assertThat(hash)
            .isEqualTo(combineCB(intTB(), stringTB()).hash());
      }

      @Nested
      class _oper_cat_tests extends AbstractOperCategoryTestSuite {
        protected _oper_cat_tests() {
          super(COMBINE, TupleTB.class);
        }
      }

      @Test
      public void with_evaluation_type_not_being_tuple_type() throws Exception {
        var hash = hash(
            hash(COMBINE.marker()),
            hash(intTB())
        );
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongNodeCatExc(
                hash, COMBINE, DATA_PATH, TupleTB.class, IntTB.class));
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
        var hash = hash(
            hash(ORDER.marker()),
            hash(arrayTB(intTB()))
        );
        assertThat(hash)
            .isEqualTo(orderCB(intTB()).hash());
      }

      @Nested
      class _oper_cat_tests extends AbstractOperCategoryTestSuite {
        protected _oper_cat_tests() {
          super(ORDER, ArrayTB.class);
        }
      }

      @Test
      public void with_evaluation_type_not_being_array_type() throws Exception {
        var hash = hash(
            hash(ORDER.marker()),
            hash(intTB())
        );
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongNodeCatExc(
                hash, ORDER, DATA_PATH, ArrayTB.class, IntTB.class));
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
        var hash = hash(
            hash(PICK.marker()),
            hash(intTB())
        );
        assertThat(hash)
            .isEqualTo(pickCB(intTB()).hash());
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
        var hash = hash(
            hash(VAR.marker()),
            hash(intTB())
        );
        assertThat(hash)
            .isEqualTo(varCB(intTB()).hash());
      }

      @Nested
      class _oper_cat_tests extends AbstractOperCategoryTestSuite {
        protected _oper_cat_tests() {
          super(VAR);
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
        var hash = hash(
            hash(SELECT.marker()),
            hash(intTB())
        );
        assertThat(hash)
            .isEqualTo(selectCB(intTB()).hash());
      }

      @Nested
      class _oper_cat_tests extends AbstractOperCategoryTestSuite {
        protected _oper_cat_tests() {
          super(SELECT);
        }
      }
    }

    private abstract class AbstractOperCategoryTestSuite {
      private final CategoryKindB categoryKindB;
      private final Class<? extends CategoryB> type;

      protected AbstractOperCategoryTestSuite(CategoryKindB categoryKindB) {
        this(categoryKindB, TypeB.class);
      }

      protected AbstractOperCategoryTestSuite(CategoryKindB categoryKindB, Class<? extends CategoryB> type) {
        this.categoryKindB = categoryKindB;
        this.type = type;
      }

      @Test
      public void without_data() throws Exception {
        assert_reading_cat_without_data_causes_exc(categoryKindB);
      }

      @Test
      public void with_additional_data() throws Exception {
        assert_reading_cat_with_additional_data_causes_exc(categoryKindB);
      }

      @Test
      public void with_data_hash_pointing_nowhere() throws Exception {
        assert_reading_cat_with_data_pointing_nowhere_causes_exc(categoryKindB);
      }

      @Test
      public void with_corrupted_type_as_data() throws Exception {
        assert_reading_cat_with_corrupted_type_as_data_causes_exc(categoryKindB);
      }

      @Test
      public void with_evaluation_type_being_oper_type() throws Exception {
        var hash = hash(
            hash(categoryKindB.marker()),
            hash(varCB())
        );
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongNodeCatExc(
                hash, categoryKindB, DATA_PATH, type, VarCB.class));
      }
    }
  }
}
