package org.smoothbuild.db.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.db.object.type.CatDb.DATA_PATH;
import static org.smoothbuild.db.object.type.CatDb.FUNC_PARAMS_PATH;
import static org.smoothbuild.db.object.type.CatDb.FUNC_RES_PATH;
import static org.smoothbuild.db.object.type.base.CatKindH.ABST_FUNC;
import static org.smoothbuild.db.object.type.base.CatKindH.ANY;
import static org.smoothbuild.db.object.type.base.CatKindH.ARRAY;
import static org.smoothbuild.db.object.type.base.CatKindH.BLOB;
import static org.smoothbuild.db.object.type.base.CatKindH.BOOL;
import static org.smoothbuild.db.object.type.base.CatKindH.CALL;
import static org.smoothbuild.db.object.type.base.CatKindH.COMBINE;
import static org.smoothbuild.db.object.type.base.CatKindH.INT;
import static org.smoothbuild.db.object.type.base.CatKindH.NOTHING;
import static org.smoothbuild.db.object.type.base.CatKindH.ORDER;
import static org.smoothbuild.db.object.type.base.CatKindH.PARAM_REF;
import static org.smoothbuild.db.object.type.base.CatKindH.SELECT;
import static org.smoothbuild.db.object.type.base.CatKindH.STRING;
import static org.smoothbuild.db.object.type.base.CatKindH.TUPLE;
import static org.smoothbuild.db.object.type.base.CatKindH.VARIABLE;
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
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.type.base.CatH;
import org.smoothbuild.db.object.type.base.CatKindH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.exc.DecodeCatExc;
import org.smoothbuild.db.object.type.exc.DecodeCatIllegalKindExc;
import org.smoothbuild.db.object.type.exc.DecodeCatNodeExc;
import org.smoothbuild.db.object.type.exc.DecodeCatRootExc;
import org.smoothbuild.db.object.type.exc.DecodeVarIllegalNameExc;
import org.smoothbuild.db.object.type.exc.UnexpectedCatNodeExc;
import org.smoothbuild.db.object.type.exc.UnexpectedCatSeqExc;
import org.smoothbuild.db.object.type.expr.ParamRefCH;
import org.smoothbuild.db.object.type.val.ArrayTH;
import org.smoothbuild.db.object.type.val.FuncTH;
import org.smoothbuild.db.object.type.val.IntTH;
import org.smoothbuild.db.object.type.val.StringTH;
import org.smoothbuild.db.object.type.val.TupleTH;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.common.AssertCall.ThrownExceptionSubject;

import com.google.common.collect.ImmutableList;

import okio.ByteString;

public class CatHCorruptedTest extends TestingContext {
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
          .isEqualTo(stringTH().hash());
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

    private void test_base_type_with_additional_child(CatKindH kind) throws Exception {
      Hash hash = hash(
          hash(kind.marker()),
          hash("abc")
      );
      assertThatGet(hash)
          .throwsException(new DecodeCatRootExc(hash, kind, 2, 1));
    }
  }

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
          hash(stringTH())
      );
      assertThat(hash)
          .isEqualTo(arrayTH(stringTH()).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_type_without_data(ARRAY);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_type_with_additional_data(ARRAY);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_type(ARRAY);
    }

    @Test
    public void with_corrupted_type_as_data() throws Exception {
      test_type_with_corrupted_type_as_data(ARRAY);
    }

    @Test
    public void with_elem_type_being_expr_type() throws Exception {
      Hash hash = hash(
          hash(ARRAY.marker()),
          hash(paramRefCH())
      );
      assertThatGet(hash)
          .throwsException(new UnexpectedCatNodeExc(
              hash, ARRAY, DATA_PATH, TypeH.class, ParamRefCH.class));
    }
  }

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
          hash(intTH())
      );
      assertThat(hash)
          .isEqualTo(callCH(intTH()).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_type_without_data(CALL);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_type_with_additional_data(CALL);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_type(CALL);
    }

    @Test
    public void with_corrupted_type_as_data() throws Exception {
      test_type_with_corrupted_type_as_data(CALL);
    }

    @Test
    public void with_evaluation_type_being_expr_type() throws Exception {
      test_type_with_data_being_expr_type(CALL, TypeH.class);
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
          hash(tupleTH(list(intTH(), stringTH())))
      );
      assertThat(hash)
          .isEqualTo(combineCH(list(intTH(), stringTH())).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_type_without_data(COMBINE);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_type_with_additional_data(COMBINE);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_type(COMBINE);
    }

    @Test
    public void with_corrupted_type_as_data() throws Exception {
      test_type_with_corrupted_type_as_data(COMBINE);
    }

    @Test
    public void with_evaluation_type_being_expr_type() throws Exception {
      test_type_with_data_being_expr_type(COMBINE, TupleTH.class);
    }

    @Test
    public void with_evaluation_type_not_being_tuple_type() throws Exception {
      Hash hash = hash(
          hash(COMBINE.marker()),
          hash(intTH())
      );
      assertThatGet(hash)
          .throwsException(new UnexpectedCatNodeExc(
              hash, COMBINE, DATA_PATH, TupleTH.class, IntTH.class));
    }
  }

  @Nested
  class _abstract_func {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save func type in HashedDb.
       */
      ImmutableList<TypeH> paramTs = list(stringTH(), boolTH());
      TupleTH paramsTuple = tupleTH(paramTs);
      Hash specHash = hash(
          hash(ABST_FUNC.marker()),
          hash(
              hash(intTH()),
              hash(paramsTuple)
          )
      );
      assertThat(specHash)
          .isEqualTo(abstFuncTH(intTH(), paramTs).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_type_without_data(ABST_FUNC);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_type_with_additional_data(ABST_FUNC);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_seq(ABST_FUNC);
    }

    @Test
    public void with_data_not_being_seq_of_hashes() throws Exception {
      Hash notHashOfSeq = hash("abc");
      Hash hash =
          hash(
              hash(ABST_FUNC.marker()),
              notHashOfSeq
          );
      assertThatGet(hash)
          .throwsException(new DecodeCatNodeExc(hash, ABST_FUNC, DATA_PATH));
    }

    @Test
    public void with_data_having_three_elems() throws Exception {
      TupleTH paramTs = tupleTH(list(stringTH(), boolTH()));
      Hash hash = hash(
          hash(ABST_FUNC.marker()),
          hash(
              hash(intTH()),
              hash(paramTs),
              hash(paramTs)
          )
      );
      assertThatGet(hash)
          .throwsException(new UnexpectedCatSeqExc(hash, ABST_FUNC, DATA_PATH, 2, 3));
    }

    @Test
    public void with_data_having_one_elems() throws Exception {
      Hash hash = hash(
          hash(ABST_FUNC.marker()),
          hash(
              hash(intTH())
          )
      );
      assertThatGet(hash)
          .throwsException(new UnexpectedCatSeqExc(hash, ABST_FUNC, DATA_PATH, 2, 1));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_data_seq_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSeq = hash(ByteString.of(new byte[byteCount]));
      Hash typeHash = hash(
          hash(ABST_FUNC.marker()),
          notHashOfSeq
      );
      assertCall(() -> ((FuncTH) catDb().get(typeHash)).res())
          .throwsException(new DecodeCatNodeExc(typeHash, ABST_FUNC, DATA_PATH))
          .withCause(new DecodeHashSeqExc(
              notHashOfSeq, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_result_pointing_nowhere() throws Exception {
      TupleTH paramTs = tupleTH(list(stringTH(), boolTH()));
      Hash nowhere = Hash.of(33);
      Hash typeHash = hash(
          hash(ABST_FUNC.marker()),
          hash(
              nowhere,
              hash(paramTs)
          )
      );
      assertCall(() -> catDb().get(typeHash))
          .throwsException(new DecodeCatNodeExc(typeHash, ABST_FUNC, FUNC_RES_PATH))
          .withCause(new DecodeCatExc(nowhere));
    }

    @Test
    public void with_result_being_expr_type() throws Exception {
      TupleTH paramT = tupleTH(list(stringTH(), boolTH()));
      Hash typeHash = hash(
          hash(ABST_FUNC.marker()),
          hash(
              hash(paramRefCH()),
              hash(paramT)
          )
      );
      assertCall(() -> catDb().get(typeHash))
          .throwsException(new UnexpectedCatNodeExc(
              typeHash, ABST_FUNC, FUNC_RES_PATH, TypeH.class, ParamRefCH.class));
    }

    @Test
    public void with_result_type_corrupted() throws Exception {
      TupleTH paramTs = tupleTH(list(stringTH(), boolTH()));
      Hash typeHash = hash(
          hash(ABST_FUNC.marker()),
          hash(
              corruptedArrayTHash(),
              hash(paramTs)
          )
      );
      assertCall(() -> catDb().get(typeHash))
          .throwsException(new DecodeCatNodeExc(typeHash, ABST_FUNC, FUNC_RES_PATH))
          .withCause(corruptedArrayTypeExc());
    }

    @Test
    public void with_params_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash typeHash = hash(
          hash(ABST_FUNC.marker()),
          hash(
              hash(intTH()),
              nowhere
          )
      );
      assertCall(() -> catDb().get(typeHash))
          .throwsException(new DecodeCatNodeExc(typeHash, ABST_FUNC, FUNC_PARAMS_PATH))
          .withCause(new DecodeCatExc(nowhere));
    }

    @Test
    public void with_params_not_being_tuple() throws Exception {
      Hash typeHash = hash(
          hash(ABST_FUNC.marker()),
          hash(
              hash(intTH()),
              hash(stringTH())
          )
      );
      assertThatGet(typeHash)
          .throwsException(new UnexpectedCatNodeExc(
              typeHash, ABST_FUNC, DATA_PATH, 1, TupleTH.class, StringTH.class));
    }

    @Test
    public void with_params_being_expr_type() throws Exception {
      Hash typeHash = hash(
          hash(ABST_FUNC.marker()),
          hash(
              hash(intTH()),
              hash(paramRefCH())
          )
      );
      assertCall(() -> catDb().get(typeHash))
          .throwsException(new UnexpectedCatNodeExc(
              typeHash, ABST_FUNC, FUNC_PARAMS_PATH, TupleTH.class, ParamRefCH.class));
    }

    @Test
    public void with_params_type_corrupted() throws Exception {
      Hash typeHash = hash(
          hash(ABST_FUNC.marker()),
          hash(
              hash(intTH()),
              corruptedArrayTHash()
          )
      );
      assertCall(() -> catDb().get(typeHash))
          .throwsException(new DecodeCatNodeExc(typeHash, ABST_FUNC, FUNC_PARAMS_PATH))
          .withCause(corruptedArrayTypeExc());
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
          hash(arrayTH(intTH()))
      );
      assertThat(hash)
          .isEqualTo(orderCH(intTH()).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_type_without_data(ORDER);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_type_with_additional_data(ORDER);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_type(ORDER);
    }

    @Test
    public void with_corrupted_type_as_data() throws Exception {
      test_type_with_corrupted_type_as_data(ORDER);
    }

    @Test
    public void with_evaluation_type_being_expr_type() throws Exception {
      test_type_with_data_being_expr_type(ORDER, ArrayTH.class);
    }

    @Test
    public void with_evaluation_type_not_being_array_type() throws Exception {
      Hash hash = hash(
          hash(ORDER.marker()),
          hash(intTH())
      );
      assertThatGet(hash)
          .throwsException(new UnexpectedCatNodeExc(
              hash, ORDER, DATA_PATH, ArrayTH.class, IntTH.class));
    }
  }

  @Nested
  class _ref {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save call type in HashedDb.
       */
      Hash hash = hash(
          hash(PARAM_REF.marker()),
          hash(intTH())
      );
      assertThat(hash)
          .isEqualTo(paramRefCH(intTH()).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_type_without_data(PARAM_REF);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_type_with_additional_data(PARAM_REF);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_type(PARAM_REF);
    }

    @Test
    public void with_corrupted_type_as_data() throws Exception {
      test_type_with_corrupted_type_as_data(PARAM_REF);
    }

    @Test
    public void with_evaluation_type_being_expr_type() throws Exception {
      test_type_with_data_being_expr_type(PARAM_REF, TypeH.class);
    }
  }

  private void test_type_without_data(CatKindH speckKind) throws Exception {
    Hash hash =
        hash(
            hash(speckKind.marker())
        );
    assertThatGet(hash)
        .throwsException(new DecodeCatRootExc(hash, speckKind, 1, 2));
  }

  private void test_type_with_additional_data(CatKindH kind) throws Exception {
    Hash hash = hash(
        hash(kind.marker()),
        hash(stringTH()),
        hash("corrupted")
    );
    assertThatGet(hash)
        .throwsException(new DecodeCatRootExc(hash, 3));
  }

  private void test_data_hash_pointing_nowhere_instead_of_being_type(CatKindH kind)
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

  private void test_data_hash_pointing_nowhere_instead_of_being_seq(CatKindH kind)
      throws Exception {
    Hash dataHash = Hash.of(33);
    Hash typeHash = hash(
        hash(kind.marker()),
        dataHash
    );
    assertCall(() -> catDb().get(typeHash))
        .throwsException(new DecodeCatNodeExc(typeHash, kind, DATA_PATH))
        .withCause(new NoSuchDataExc(dataHash));
  }

  private void test_type_with_corrupted_type_as_data(CatKindH kind) throws Exception {
    Hash hash =
        hash(
            hash(kind.marker()),
            corruptedArrayTHash());
    assertThatGet(hash)
        .throwsException(new DecodeCatNodeExc(hash, kind, DATA_PATH))
        .withCause(corruptedArrayTypeExc());
  }

  private void test_type_with_data_being_expr_type(CatKindH kind, Class<? extends CatH> expected)
      throws Exception {
    Hash hash = hash(
        hash(kind.marker()),
        hash(paramRefCH())
    );
    assertThatGet(hash)
        .throwsException(new UnexpectedCatNodeExc(
            hash, kind, DATA_PATH, expected, ParamRefCH.class));
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

  protected Hash hash(ObjH obj) {
    return obj.hash();
  }

  protected Hash hash(CatH type) {
    return type.hash();
  }

  protected Hash hash(Hash... hashes) throws HashedDbExc {
    return hashedDb().writeSeq(hashes);
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
          hash(intTH())
      );
      assertThat(hash)
          .isEqualTo(selectCH(intTH()).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_type_without_data(SELECT);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_type_with_additional_data(SELECT);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_type(SELECT);
    }

    @Test
    public void with_corrupted_type_as_data() throws Exception {
      test_type_with_corrupted_type_as_data(SELECT);
    }

    @Test
    public void with_evaluation_type_being_expr_type() throws Exception {
      test_type_with_data_being_expr_type(SELECT, TypeH.class);
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
              hash(stringTH()),
              hash(stringTH())
          )
      );
      assertThat(hash)
          .isEqualTo(personTH().hash());
    }

    @Test
    public void without_data() throws Exception {
      test_type_without_data(TUPLE);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_type_with_additional_data(TUPLE);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_seq(TUPLE);
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
      Hash stringHash = hash(stringH("abc"));
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
                  hash(paramRefCH())
              )
          );
      assertThatGet(hash)
          .throwsException(new UnexpectedCatNodeExc(
              hash, TUPLE, "data", 0, TypeH.class, ParamRefCH.class));
    }

    @Test
    public void with_corrupted_elem_type() throws Exception {
      Hash hash =
          hash(
              hash(TUPLE.marker()),
              hash(
                  corruptedArrayTHash(),
                  hash(stringTH())));
      assertThatGet(hash)
          .throwsException(new DecodeCatNodeExc(hash, TUPLE, "data[0]"))
          .withCause(corruptedArrayTypeExc());
    }
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
          .isEqualTo(varTH("A").hash());
    }

    @Test
    public void without_data() throws Exception {
      test_type_without_data(VARIABLE);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_type_with_additional_data(VARIABLE);
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
}
