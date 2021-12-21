package org.smoothbuild.db.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.db.object.type.CatDb.DATA_PATH;
import static org.smoothbuild.db.object.type.CatDb.FUNC_PARAMS_PATH;
import static org.smoothbuild.db.object.type.CatDb.FUNC_RES_PATH;
import static org.smoothbuild.db.object.type.base.CatKindB.ANY;
import static org.smoothbuild.db.object.type.base.CatKindB.ARRAY;
import static org.smoothbuild.db.object.type.base.CatKindB.BLOB;
import static org.smoothbuild.db.object.type.base.CatKindB.BOOL;
import static org.smoothbuild.db.object.type.base.CatKindB.CALL;
import static org.smoothbuild.db.object.type.base.CatKindB.COMBINE;
import static org.smoothbuild.db.object.type.base.CatKindB.FUNC;
import static org.smoothbuild.db.object.type.base.CatKindB.IF;
import static org.smoothbuild.db.object.type.base.CatKindB.INT;
import static org.smoothbuild.db.object.type.base.CatKindB.INVOKE;
import static org.smoothbuild.db.object.type.base.CatKindB.MAP;
import static org.smoothbuild.db.object.type.base.CatKindB.NOTHING;
import static org.smoothbuild.db.object.type.base.CatKindB.ORDER;
import static org.smoothbuild.db.object.type.base.CatKindB.PARAM_REF;
import static org.smoothbuild.db.object.type.base.CatKindB.SELECT;
import static org.smoothbuild.db.object.type.base.CatKindB.STRING;
import static org.smoothbuild.db.object.type.base.CatKindB.TUPLE;
import static org.smoothbuild.db.object.type.base.CatKindB.VARIABLE;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.db.hashed.exc.DecodeHashSeqExc;
import org.smoothbuild.db.hashed.exc.DecodeStringExc;
import org.smoothbuild.db.hashed.exc.HashedDbExc;
import org.smoothbuild.db.hashed.exc.NoSuchDataExc;
import org.smoothbuild.db.object.obj.IllegalArrayByteSizesProvider;
import org.smoothbuild.db.object.obj.base.ObjB;
import org.smoothbuild.db.object.type.base.CatB;
import org.smoothbuild.db.object.type.base.CatKindB;
import org.smoothbuild.db.object.type.base.TypeB;
import org.smoothbuild.db.object.type.exc.DecodeCatExc;
import org.smoothbuild.db.object.type.exc.DecodeCatIllegalKindExc;
import org.smoothbuild.db.object.type.exc.DecodeCatNodeExc;
import org.smoothbuild.db.object.type.exc.DecodeCatRootExc;
import org.smoothbuild.db.object.type.exc.DecodeCatWrongNodeCatExc;
import org.smoothbuild.db.object.type.exc.DecodeCatWrongSeqSizeExc;
import org.smoothbuild.db.object.type.exc.DecodeVarIllegalNameExc;
import org.smoothbuild.db.object.type.expr.ParamRefCB;
import org.smoothbuild.db.object.type.val.ArrayTB;
import org.smoothbuild.db.object.type.val.FuncTB;
import org.smoothbuild.db.object.type.val.IntTB;
import org.smoothbuild.db.object.type.val.StringTB;
import org.smoothbuild.db.object.type.val.TupleTB;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.common.AssertCall.ThrownExceptionSubject;

import com.google.common.collect.ImmutableList;

import okio.ByteString;

public class CatBCorruptedTest extends TestingContext {
  @Nested
  class _illegal_type_marker {
    @Test
    public void causes_exception() throws Exception {
      Hash hash = hash(
          hash((byte) 99)
      );
      assertThatGet(hash)
          .throwsException(illegalTypeMarkerException(hash, 99));
    }

    @Test
    public void with_additional_child() throws Exception {
      Hash hash = hash(
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
      Hash hash = hash(
          hash(STRING.marker())
      );
      assertThat(hash)
          .isEqualTo(stringTB().hash());
    }

    @Test
    public void any_with_additional_child() throws Exception {
      test_base_type_with_additional_child(ANY);
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
    public void nothing_with_additional_child() throws Exception {
      test_base_type_with_additional_child(NOTHING);
    }

    @Test
    public void string_with_additional_child() throws Exception {
      test_base_type_with_additional_child(STRING);
    }

    private void test_base_type_with_additional_child(CatKindB kind) throws Exception {
      Hash hash = hash(
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
        Hash hash = hash(
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
      public void with_elem_type_being_expr_type() throws Exception {
        Hash hash = hash(
            hash(ARRAY.marker()),
            hash(paramRefCB())
        );
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongNodeCatExc(
                hash, ARRAY, DATA_PATH, TypeB.class, ParamRefCB.class));
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
        ImmutableList<TypeB> paramTs = list(stringTB(), boolTB());
        TupleTB paramsTuple = tupleTB(paramTs);
        Hash specHash = hash(
            hash(FUNC.marker()),
            hash(
                hash(intTB()),
                hash(paramsTuple)
            )
        );
        assertThat(specHash)
            .isEqualTo(funcTB(intTB(), paramTs).hash());
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
        assert_reading_cat_with_data_pointing_nowhere_instead_of_being_seq_causes_exc(FUNC);
      }

      @Test
      public void with_data_not_being_seq_of_hashes() throws Exception {
        Hash notHashOfSeq = hash("abc");
        Hash hash =
            hash(
                hash(FUNC.marker()),
                notHashOfSeq
            );
        assertThatGet(hash)
            .throwsException(new DecodeCatNodeExc(hash, FUNC, DATA_PATH));
      }

      @Test
      public void with_data_having_three_elems() throws Exception {
        TupleTB paramTs = tupleTB(list(stringTB(), boolTB()));
        Hash hash = hash(
            hash(FUNC.marker()),
            hash(
                hash(intTB()),
                hash(paramTs),
                hash(paramTs)
            )
        );
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongSeqSizeExc(hash, FUNC, DATA_PATH, 2, 3));
      }

      @Test
      public void with_data_having_one_elems() throws Exception {
        Hash hash = hash(
            hash(FUNC.marker()),
            hash(
                hash(intTB())
            )
        );
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongSeqSizeExc(hash, FUNC, DATA_PATH, 2, 1));
      }

      @ParameterizedTest
      @ArgumentsSource(IllegalArrayByteSizesProvider.class)
      public void with_data_seq_size_different_than_multiple_of_hash_size(
          int byteCount) throws Exception {
        Hash notHashOfSeq = hash(ByteString.of(new byte[byteCount]));
        Hash typeHash = hash(
            hash(FUNC.marker()),
            notHashOfSeq
        );
        assertCall(() -> ((FuncTB) catDb().get(typeHash)).res())
            .throwsException(new DecodeCatNodeExc(typeHash, FUNC, DATA_PATH))
            .withCause(new DecodeHashSeqExc(
                notHashOfSeq, byteCount % Hash.lengthInBytes()));
      }

      @Test
      public void with_result_pointing_nowhere() throws Exception {
        TupleTB paramTs = tupleTB(list(stringTB(), boolTB()));
        Hash nowhere = Hash.of(33);
        Hash typeHash = hash(
            hash(FUNC.marker()),
            hash(
                nowhere,
                hash(paramTs)
            )
        );
        assertCall(() -> catDb().get(typeHash))
            .throwsException(new DecodeCatNodeExc(typeHash, FUNC, FUNC_RES_PATH))
            .withCause(new DecodeCatExc(nowhere));
      }

      @Test
      public void with_result_being_expr_type() throws Exception {
        TupleTB paramT = tupleTB(list(stringTB(), boolTB()));
        Hash typeHash = hash(
            hash(FUNC.marker()),
            hash(
                hash(paramRefCB()),
                hash(paramT)
            )
        );
        assertCall(() -> catDb().get(typeHash))
            .throwsException(new DecodeCatWrongNodeCatExc(
                typeHash, FUNC, FUNC_RES_PATH, TypeB.class, ParamRefCB.class));
      }

      @Test
      public void with_result_type_corrupted() throws Exception {
        TupleTB paramTs = tupleTB(list(stringTB(), boolTB()));
        Hash typeHash = hash(
            hash(FUNC.marker()),
            hash(
                corruptedArrayTHash(),
                hash(paramTs)
            )
        );
        assertCall(() -> catDb().get(typeHash))
            .throwsException(new DecodeCatNodeExc(typeHash, FUNC, FUNC_RES_PATH))
            .withCause(corruptedArrayTypeExc());
      }

      @Test
      public void with_params_pointing_nowhere() throws Exception {
        Hash nowhere = Hash.of(33);
        Hash typeHash = hash(
            hash(FUNC.marker()),
            hash(
                hash(intTB()),
                nowhere
            )
        );
        assertCall(() -> catDb().get(typeHash))
            .throwsException(new DecodeCatNodeExc(typeHash, FUNC, FUNC_PARAMS_PATH))
            .withCause(new DecodeCatExc(nowhere));
      }

      @Test
      public void with_params_not_being_tuple() throws Exception {
        Hash typeHash = hash(
            hash(FUNC.marker()),
            hash(
                hash(intTB()),
                hash(stringTB())
            )
        );
        assertThatGet(typeHash)
            .throwsException(new DecodeCatWrongNodeCatExc(
                typeHash, FUNC, DATA_PATH, 1, TupleTB.class, StringTB.class));
      }

      @Test
      public void with_params_being_expr_type() throws Exception {
        Hash typeHash = hash(
            hash(FUNC.marker()),
            hash(
                hash(intTB()),
                hash(paramRefCB())
            )
        );
        assertCall(() -> catDb().get(typeHash))
            .throwsException(new DecodeCatWrongNodeCatExc(
                typeHash, FUNC, FUNC_PARAMS_PATH, TupleTB.class, ParamRefCB.class));
      }

      @Test
      public void with_params_type_corrupted() throws Exception {
        Hash typeHash = hash(
            hash(FUNC.marker()),
            hash(
                hash(intTB()),
                corruptedArrayTHash()
            )
        );
        assertCall(() -> catDb().get(typeHash))
            .throwsException(new DecodeCatNodeExc(typeHash, FUNC, FUNC_PARAMS_PATH))
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
        Hash hash = hash(
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
        assert_reading_cat_with_data_pointing_nowhere_instead_of_being_seq_causes_exc(TUPLE);
      }

      @Test
      public void with_elems_not_being_seq_of_hashes() throws Exception {
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
      public void with_elems_being_array_of_non_type() throws Exception {
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
      public void with_elems_being_seq_of_expr_type() throws Exception {
        Hash hash =
            hash(
                hash(TUPLE.marker()),
                hash(
                    hash(paramRefCB())
                )
            );
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongNodeCatExc(
                hash, TUPLE, "data", 0, TypeB.class, ParamRefCB.class));
      }

      @Test
      public void with_corrupted_elem_type() throws Exception {
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

  private void assert_reading_cat_without_data_causes_exc(CatKindB speckKind) throws Exception {
    Hash hash =
        hash(
            hash(speckKind.marker())
        );
    assertThatGet(hash)
        .throwsException(new DecodeCatRootExc(hash, speckKind, 1, 2));
  }

  private void assert_reading_cat_with_additional_data_causes_exc(CatKindB kind) throws Exception {
    Hash hash = hash(
        hash(kind.marker()),
        hash(stringTB()),
        hash("corrupted")
    );
    assertThatGet(hash)
        .throwsException(new DecodeCatRootExc(hash, 3));
  }

  private void assert_reading_cat_with_data_pointing_nowhere_causes_exc(CatKindB kind)
      throws Exception {
    Hash dataHash = Hash.of(33);
    Hash typeHash = hash(
        hash(kind.marker()),
        dataHash
    );
    assertCall(() -> catDb().get(typeHash))
        .throwsException(new DecodeCatNodeExc(typeHash, kind, DATA_PATH))
        .withCause(new DecodeCatExc(dataHash));
  }

  private void assert_reading_cat_with_data_pointing_nowhere_instead_of_being_seq_causes_exc(
      CatKindB kind) throws Exception {
    Hash dataHash = Hash.of(33);
    Hash typeHash = hash(
        hash(kind.marker()),
        dataHash
    );
    assertCall(() -> catDb().get(typeHash))
        .throwsException(new DecodeCatNodeExc(typeHash, kind, DATA_PATH))
        .withCause(new NoSuchDataExc(dataHash));
  }

  private void assert_reading_cat_with_corrupted_type_as_data_causes_exc(CatKindB kind)
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
      return assertCall(() -> catDb().get(hash));
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

  protected Hash hash(ObjB obj) {
    return obj.hash();
  }

  protected Hash hash(CatB type) {
    return type.hash();
  }

  protected Hash hash(Hash... hashes) throws HashedDbExc {
    return hashedDb().writeSeq(hashes);
  }

  @Nested
  class _var {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save Var type in HashedDb.
       */
      Hash hash = hash(
          hash(VARIABLE.marker()),
          hash("A")
      );
      assertThat(hash)
          .isEqualTo(varTB("A").hash());
    }

    @Test
    public void without_data() throws Exception {
      assert_reading_cat_without_data_causes_exc(VARIABLE);
    }

    @Test
    public void with_additional_data() throws Exception {
      assert_reading_cat_with_additional_data_causes_exc(VARIABLE);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      Hash dataHash = Hash.of(33);
      Hash typeHash = hash(
          hash(VARIABLE.marker()),
          dataHash
      );
      assertCall(() -> catDb().get(typeHash))
          .throwsException(new DecodeCatNodeExc(typeHash, VARIABLE, DATA_PATH))
          .withCause(new NoSuchDataExc(dataHash));
    }

    @Test
    public void with_corrupted_type_as_data() throws Exception {
      Hash hash =
          hash(
              hash(VARIABLE.marker()),
              corruptedArrayTHash());
      assertThatGet(hash)
          .throwsException(new DecodeCatNodeExc(hash, VARIABLE, DATA_PATH))
          .withCause(DecodeStringExc.class);
    }

    @Test
    public void with_illegal_name() throws Exception {
      Hash hash = hash(
          hash(VARIABLE.marker()),
          hash("a")
      );
      assertThatGet(hash)
          .throwsException(new DecodeVarIllegalNameExc(hash, "a"));
    }
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
        Hash hash = hash(
            hash(CALL.marker()),
            hash(intTB())
        );
        assertThat(hash)
            .isEqualTo(callCB(intTB()).hash());
      }

      @Nested
      class _expr_cat_tests extends ExprCatTestSet {
        protected _expr_cat_tests() {
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
        Hash hash = hash(
            hash(COMBINE.marker()),
            hash(tupleTB(list(intTB(), stringTB())))
        );
        assertThat(hash)
            .isEqualTo(combineCB(list(intTB(), stringTB())).hash());
      }

      @Nested
      class _expr_cat_tests extends ExprCatTestSet {
        protected _expr_cat_tests() {
          super(COMBINE, TupleTB.class);
        }
      }

      @Test
      public void with_evaluation_type_not_being_tuple_type() throws Exception {
        Hash hash = hash(
            hash(COMBINE.marker()),
            hash(intTB())
        );
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongNodeCatExc(
                hash, COMBINE, DATA_PATH, TupleTB.class, IntTB.class));
      }
    }

    @Nested
    class _if {
      @Test
      public void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save if category in HashedDb.
         */
        Hash hash = hash(
            hash(IF.marker()),
            hash(intTB())
        );
        assertThat(hash)
            .isEqualTo(ifCB(intTB()).hash());
      }

      @Nested
      class _expr_cat_tests extends ExprCatTestSet {
        protected _expr_cat_tests() {
          super(IF);
        }
      }
    }

    @Nested
    class _invoke {
      @Test
      public void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save `invoke` category in HashedDb.
         */
        Hash hash = hash(
            hash(INVOKE.marker()),
            hash(intTB())
        );
        assertThat(hash)
            .isEqualTo(invokeCB(intTB()).hash());
      }

      @Nested
      class _expr_cat_tests extends ExprCatTestSet {
        protected _expr_cat_tests() {
          super(INVOKE);
        }
      }
    }

    @Nested
    class _map {
      @Test
      public void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save `map` type in HashedDb.
         */
        Hash hash = hash(
            hash(MAP.marker()),
            hash(arrayTB(intTB()))
        );
        assertThat(hash)
            .isEqualTo(mapCB(arrayTB(intTB())).hash());
      }

      @Nested
      class _expr_cat_tests extends ExprCatTestSet {
        protected _expr_cat_tests() {
          super(MAP, ArrayTB.class);
        }
      }

      @Test
      public void eval_type_not_being_array_type() throws Exception {
        Hash hash = hash(
            hash(MAP.marker()),
            hash(intTB())
        );
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongNodeCatExc(
                hash, MAP, DATA_PATH, ArrayTB.class, IntTB.class));
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
        Hash hash = hash(
            hash(ORDER.marker()),
            hash(arrayTB(intTB()))
        );
        assertThat(hash)
            .isEqualTo(orderCB(intTB()).hash());
      }

      @Nested
      class _expr_cat_tests extends ExprCatTestSet {
        protected _expr_cat_tests() {
          super(ORDER, ArrayTB.class);
        }
      }

      @Test
      public void with_evaluation_type_not_being_array_type() throws Exception {
        Hash hash = hash(
            hash(ORDER.marker()),
            hash(intTB())
        );
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongNodeCatExc(
                hash, ORDER, DATA_PATH, ArrayTB.class, IntTB.class));
      }
    }

    @Nested
    class _param_ref {
      @Test
      public void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save param-ref in HashedDb.
         */
        Hash hash = hash(
            hash(PARAM_REF.marker()),
            hash(intTB())
        );
        assertThat(hash)
            .isEqualTo(paramRefCB(intTB()).hash());
      }

      @Nested
      class _expr_cat_tests extends ExprCatTestSet {
        protected _expr_cat_tests() {
          super(PARAM_REF);
        }
      }
    }

    @Nested
    class _select {
      @Test
      public void learning_test() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save Call type in HashedDb.
         */
        Hash hash = hash(
            hash(SELECT.marker()),
            hash(intTB())
        );
        assertThat(hash)
            .isEqualTo(selectCB(intTB()).hash());
      }

      @Nested
      class _expr_cat_tests extends ExprCatTestSet {
        protected _expr_cat_tests() {
          super(SELECT);
        }
      }
    }

    private abstract class ExprCatTestSet {
      private final CatKindB catKindB;
      private final Class<? extends CatB> type;

      protected ExprCatTestSet(CatKindB catKindB) {
        this(catKindB, TypeB.class);
      }

      protected ExprCatTestSet(CatKindB catKindB, Class<? extends CatB> type) {
        this.catKindB = catKindB;
        this.type = type;
      }

      @Test
      public void without_data() throws Exception {
        assert_reading_cat_without_data_causes_exc(catKindB);
      }

      @Test
      public void with_additional_data() throws Exception {
        assert_reading_cat_with_additional_data_causes_exc(catKindB);
      }

      @Test
      public void with_data_hash_pointing_nowhere() throws Exception {
        assert_reading_cat_with_data_pointing_nowhere_causes_exc(catKindB);
      }

      @Test
      public void with_corrupted_type_as_data() throws Exception {
        assert_reading_cat_with_corrupted_type_as_data_causes_exc(catKindB);
      }

      @Test
      public void with_evaluation_type_being_expr_type() throws Exception {
        Hash hash = hash(
            hash(catKindB.marker()),
            hash(paramRefCB())
        );
        assertThatGet(hash)
            .throwsException(new DecodeCatWrongNodeCatExc(
                hash, catKindB, DATA_PATH, type, ParamRefCB.class));
      }
    }
  }
}