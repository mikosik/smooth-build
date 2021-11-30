package org.smoothbuild.db.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.db.object.type.TypeDb.DATA_PATH;
import static org.smoothbuild.db.object.type.TypeDb.FUNCTION_PARAMS_PATH;
import static org.smoothbuild.db.object.type.TypeDb.FUNCTION_RES_PATH;
import static org.smoothbuild.db.object.type.base.SpecKindH.ABST_FUNC;
import static org.smoothbuild.db.object.type.base.SpecKindH.ANY;
import static org.smoothbuild.db.object.type.base.SpecKindH.ARRAY;
import static org.smoothbuild.db.object.type.base.SpecKindH.BLOB;
import static org.smoothbuild.db.object.type.base.SpecKindH.BOOL;
import static org.smoothbuild.db.object.type.base.SpecKindH.CALL;
import static org.smoothbuild.db.object.type.base.SpecKindH.CONSTRUCT;
import static org.smoothbuild.db.object.type.base.SpecKindH.INT;
import static org.smoothbuild.db.object.type.base.SpecKindH.NOTHING;
import static org.smoothbuild.db.object.type.base.SpecKindH.ORDER;
import static org.smoothbuild.db.object.type.base.SpecKindH.PARAM_REF;
import static org.smoothbuild.db.object.type.base.SpecKindH.SELECT;
import static org.smoothbuild.db.object.type.base.SpecKindH.STRING;
import static org.smoothbuild.db.object.type.base.SpecKindH.TUPLE;
import static org.smoothbuild.db.object.type.base.SpecKindH.VARIABLE;
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
import org.smoothbuild.db.object.type.base.SpecH;
import org.smoothbuild.db.object.type.base.SpecKindH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.exc.DecodeTypeExc;
import org.smoothbuild.db.object.type.exc.DecodeTypeIllegalKindExc;
import org.smoothbuild.db.object.type.exc.DecodeTypeNodeExc;
import org.smoothbuild.db.object.type.exc.DecodeTypeRootExc;
import org.smoothbuild.db.object.type.exc.DecodeVarIllegalNameExc;
import org.smoothbuild.db.object.type.exc.UnexpectedTypeNodeExc;
import org.smoothbuild.db.object.type.exc.UnexpectedTypeSeqExc;
import org.smoothbuild.db.object.type.expr.RefTypeH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.db.object.type.val.FuncTypeH;
import org.smoothbuild.db.object.type.val.IntTypeH;
import org.smoothbuild.db.object.type.val.StringTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.common.AssertCall.ThrownExceptionSubject;

import com.google.common.collect.ImmutableList;

import okio.ByteString;

public class SpecHCorruptedTest extends TestingContext {
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
          .isEqualTo(stringHT().hash());
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

    private void test_base_type_with_additional_child(SpecKindH kind) throws Exception {
      Hash hash = hash(
          hash(kind.marker()),
          hash("abc")
      );
      assertThatGet(hash)
          .throwsException(new DecodeTypeRootExc(hash, kind, 2, 1));
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
          hash(stringHT())
      );
      assertThat(hash)
          .isEqualTo(arrayHT(stringHT()).hash());
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
          hash(refHT())
      );
      assertThatGet(hash)
          .throwsException(new UnexpectedTypeNodeExc(
              hash, ARRAY, DATA_PATH, TypeH.class, RefTypeH.class));
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
          hash(intHT())
      );
      assertThat(hash)
          .isEqualTo(callHT(intHT()).hash());
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
          hash(CONSTRUCT.marker()),
          hash(tupleHT(list(intHT(), stringHT())))
      );
      assertThat(hash)
          .isEqualTo(combineHT(list(intHT(), stringHT())).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_type_without_data(CONSTRUCT);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_type_with_additional_data(CONSTRUCT);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_type(CONSTRUCT);
    }

    @Test
    public void with_corrupted_type_as_data() throws Exception {
      test_type_with_corrupted_type_as_data(CONSTRUCT);
    }

    @Test
    public void with_evaluation_type_being_expr_type() throws Exception {
      test_type_with_data_being_expr_type(CONSTRUCT, TupleTypeH.class);
    }

    @Test
    public void with_evaluation_type_not_being_tuple_type() throws Exception {
      Hash hash = hash(
          hash(CONSTRUCT.marker()),
          hash(intHT())
      );
      assertThatGet(hash)
          .throwsException(new UnexpectedTypeNodeExc(
              hash, CONSTRUCT, DATA_PATH, TupleTypeH.class, IntTypeH.class));
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
      ImmutableList<TypeH> paramTypes = list(stringHT(), boolHT());
      TupleTypeH paramsTuple = tupleHT(paramTypes);
      Hash specHash = hash(
          hash(ABST_FUNC.marker()),
          hash(
              hash(intHT()),
              hash(paramsTuple)
          )
      );
      assertThat(specHash)
          .isEqualTo(abstFuncHT(intHT(), paramTypes).hash());
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
          .throwsException(new DecodeTypeNodeExc(hash, ABST_FUNC, DATA_PATH));
    }

    @Test
    public void with_data_having_three_elems() throws Exception {
      TupleTypeH paramTypes = tupleHT(list(stringHT(), boolHT()));
      Hash hash = hash(
          hash(ABST_FUNC.marker()),
          hash(
              hash(intHT()),
              hash(paramTypes),
              hash(paramTypes)
          )
      );
      assertThatGet(hash)
          .throwsException(new UnexpectedTypeSeqExc(hash, ABST_FUNC, DATA_PATH, 2, 3));
    }

    @Test
    public void with_data_having_one_elems() throws Exception {
      Hash hash = hash(
          hash(ABST_FUNC.marker()),
          hash(
              hash(intHT())
          )
      );
      assertThatGet(hash)
          .throwsException(new UnexpectedTypeSeqExc(hash, ABST_FUNC, DATA_PATH, 2, 1));
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
      assertCall(() -> ((FuncTypeH) typeDb().get(typeHash)).res())
          .throwsException(new DecodeTypeNodeExc(typeHash, ABST_FUNC, DATA_PATH))
          .withCause(new DecodeHashSeqExc(
              notHashOfSeq, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_result_pointing_nowhere() throws Exception {
      TupleTypeH paramTypes = tupleHT(list(stringHT(), boolHT()));
      Hash nowhere = Hash.of(33);
      Hash typeHash = hash(
          hash(ABST_FUNC.marker()),
          hash(
              nowhere,
              hash(paramTypes)
          )
      );
      assertCall(() -> typeDb().get(typeHash))
          .throwsException(new DecodeTypeNodeExc(typeHash, ABST_FUNC, FUNCTION_RES_PATH))
          .withCause(new DecodeTypeExc(nowhere));
    }

    @Test
    public void with_result_being_expr_type() throws Exception {
      TupleTypeH paramType = tupleHT(list(stringHT(), boolHT()));
      Hash typeHash = hash(
          hash(ABST_FUNC.marker()),
          hash(
              hash(refHT()),
              hash(paramType)
          )
      );
      assertCall(() -> typeDb().get(typeHash))
          .throwsException(new UnexpectedTypeNodeExc(
              typeHash, ABST_FUNC, FUNCTION_RES_PATH, TypeH.class, RefTypeH.class));
    }

    @Test
    public void with_result_type_corrupted() throws Exception {
      TupleTypeH paramTypes = tupleHT(list(stringHT(), boolHT()));
      Hash typeHash = hash(
          hash(ABST_FUNC.marker()),
          hash(
              corruptedArrayTypeHash(),
              hash(paramTypes)
          )
      );
      assertCall(() -> typeDb().get(typeHash))
          .throwsException(new DecodeTypeNodeExc(typeHash, ABST_FUNC, FUNCTION_RES_PATH))
          .withCause(corruptedArrayTypeException());
    }

    @Test
    public void with_params_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash typeHash = hash(
          hash(ABST_FUNC.marker()),
          hash(
              hash(intHT()),
              nowhere
          )
      );
      assertCall(() -> typeDb().get(typeHash))
          .throwsException(new DecodeTypeNodeExc(typeHash, ABST_FUNC, FUNCTION_PARAMS_PATH))
          .withCause(new DecodeTypeExc(nowhere));
    }

    @Test
    public void with_params_not_being_tuple() throws Exception {
      Hash typeHash = hash(
          hash(ABST_FUNC.marker()),
          hash(
              hash(intHT()),
              hash(stringHT())
          )
      );
      assertThatGet(typeHash)
          .throwsException(new UnexpectedTypeNodeExc(
              typeHash, ABST_FUNC, DATA_PATH, 1, TupleTypeH.class, StringTypeH.class));
    }

    @Test
    public void with_params_being_expr_type() throws Exception {
      Hash typeHash = hash(
          hash(ABST_FUNC.marker()),
          hash(
              hash(intHT()),
              hash(refHT())
          )
      );
      assertCall(() -> typeDb().get(typeHash))
          .throwsException(new UnexpectedTypeNodeExc(
              typeHash, ABST_FUNC, FUNCTION_PARAMS_PATH, TupleTypeH.class, RefTypeH.class));
    }

    @Test
    public void with_params_type_corrupted() throws Exception {
      Hash typeHash = hash(
          hash(ABST_FUNC.marker()),
          hash(
              hash(intHT()),
              corruptedArrayTypeHash()
          )
      );
      assertCall(() -> typeDb().get(typeHash))
          .throwsException(new DecodeTypeNodeExc(typeHash, ABST_FUNC, FUNCTION_PARAMS_PATH))
          .withCause(corruptedArrayTypeException());
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
          hash(arrayHT(intHT()))
      );
      assertThat(hash)
          .isEqualTo(orderHT(intHT()).hash());
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
      test_type_with_data_being_expr_type(ORDER, ArrayTypeH.class);
    }

    @Test
    public void with_evaluation_type_not_being_array_type() throws Exception {
      Hash hash = hash(
          hash(ORDER.marker()),
          hash(intHT())
      );
      assertThatGet(hash)
          .throwsException(new UnexpectedTypeNodeExc(
              hash, ORDER, DATA_PATH, ArrayTypeH.class, IntTypeH.class));
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
          hash(intHT())
      );
      assertThat(hash)
          .isEqualTo(refHT(intHT()).hash());
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

  private void test_type_without_data(SpecKindH speckKind) throws Exception {
    Hash hash =
        hash(
            hash(speckKind.marker())
        );
    assertThatGet(hash)
        .throwsException(new DecodeTypeRootExc(hash, speckKind, 1, 2));
  }

  private void test_type_with_additional_data(SpecKindH kind) throws Exception {
    Hash hash = hash(
        hash(kind.marker()),
        hash(stringHT()),
        hash("corrupted")
    );
    assertThatGet(hash)
        .throwsException(new DecodeTypeRootExc(hash, 3));
  }

  private void test_data_hash_pointing_nowhere_instead_of_being_type(SpecKindH kind)
      throws Exception {
    Hash dataHash = Hash.of(33);
    Hash typeHash = hash(
        hash(kind.marker()),
        dataHash
    );
    assertCall(() -> typeDb().get(typeHash))
        .throwsException(new DecodeTypeNodeExc(typeHash, kind, DATA_PATH))
        .withCause(new DecodeTypeExc(dataHash));
  }

  private void test_data_hash_pointing_nowhere_instead_of_being_seq(SpecKindH kind)
      throws Exception {
    Hash dataHash = Hash.of(33);
    Hash typeHash = hash(
        hash(kind.marker()),
        dataHash
    );
    assertCall(() -> typeDb().get(typeHash))
        .throwsException(new DecodeTypeNodeExc(typeHash, kind, DATA_PATH))
        .withCause(new NoSuchDataExc(dataHash));
  }

  private void test_type_with_corrupted_type_as_data(SpecKindH kind) throws Exception {
    Hash hash =
        hash(
            hash(kind.marker()),
            corruptedArrayTypeHash());
    assertThatGet(hash)
        .throwsException(new DecodeTypeNodeExc(hash, kind, DATA_PATH))
        .withCause(corruptedArrayTypeException());
  }

  private void test_type_with_data_being_expr_type(SpecKindH kind, Class<? extends SpecH> expected)
      throws Exception {
    Hash hash = hash(
        hash(kind.marker()),
        hash(refHT())
    );
    assertThatGet(hash)
        .throwsException(new UnexpectedTypeNodeExc(
            hash, kind, DATA_PATH, expected, RefTypeH.class));
  }

  private ThrownExceptionSubject assertThatGet(Hash hash) {
      return assertCall(() -> typeDb().get(hash));
  }

  private DecodeTypeExc illegalTypeMarkerException(Hash hash, int marker) {
    return new DecodeTypeIllegalKindExc(hash, (byte) marker);
  }

  private DecodeTypeNodeExc corruptedArrayTypeException() throws Exception {
    return new DecodeTypeNodeExc(corruptedArrayTypeHash(), ARRAY, DATA_PATH);
  }

  private Hash corruptedArrayTypeHash() throws Exception {
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

  protected Hash hash(SpecH type) {
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
          hash(intHT())
      );
      assertThat(hash)
          .isEqualTo(selectHT(intHT()).hash());
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
              hash(stringHT()),
              hash(stringHT())
          )
      );
      assertThat(hash)
          .isEqualTo(personHT().hash());
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
          .throwsException(new DecodeTypeNodeExc(hash, TUPLE, DATA_PATH));
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
          .throwsException(new DecodeTypeNodeExc(hash, TUPLE, "data[0]"))
          .withCause(new DecodeTypeExc(stringHash));
    }

    @Test
    public void with_elems_being_seq_of_expr_type() throws Exception {
      Hash hash =
          hash(
              hash(TUPLE.marker()),
              hash(
                  hash(refHT())
              )
          );
      assertThatGet(hash)
          .throwsException(new UnexpectedTypeNodeExc(
              hash, TUPLE, "data", 0, TypeH.class, RefTypeH.class));
    }

    @Test
    public void with_corrupted_elem_type() throws Exception {
      Hash hash =
          hash(
              hash(TUPLE.marker()),
              hash(
                  corruptedArrayTypeHash(),
                  hash(stringHT())));
      assertThatGet(hash)
          .throwsException(new DecodeTypeNodeExc(hash, TUPLE, "data[0]"))
          .withCause(corruptedArrayTypeException());
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
          .isEqualTo(varHT("A").hash());
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
      assertCall(() -> typeDb().get(typeHash))
          .throwsException(new DecodeTypeNodeExc(typeHash, VARIABLE, DATA_PATH))
          .withCause(new NoSuchDataExc(dataHash));
    }

    @Test
    public void with_corrupted_type_as_data() throws Exception {
      Hash hash =
          hash(
              hash(VARIABLE.marker()),
              corruptedArrayTypeHash());
      assertThatGet(hash)
          .throwsException(new DecodeTypeNodeExc(hash, VARIABLE, DATA_PATH))
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
