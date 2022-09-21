package org.smoothbuild.bytecode.expr;

import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.bytecode.expr.ExprB.DATA_PATH;
import static org.smoothbuild.bytecode.expr.exc.DecodeExprRootExc.cannotReadRootException;
import static org.smoothbuild.bytecode.expr.exc.DecodeExprRootExc.wrongSizeOfRootSeqException;
import static org.smoothbuild.testing.StringCreators.illegalString;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.bytecode.expr.exc.DecodeCombineWrongItemsSizeExc;
import org.smoothbuild.bytecode.expr.exc.DecodeExprCatExc;
import org.smoothbuild.bytecode.expr.exc.DecodeExprNoSuchExprExc;
import org.smoothbuild.bytecode.expr.exc.DecodeExprNodeExc;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeClassExc;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongSeqSizeExc;
import org.smoothbuild.bytecode.expr.exc.DecodeSelectIndexOutOfBoundsExc;
import org.smoothbuild.bytecode.expr.exc.DecodeSelectWrongEvalTypeExc;
import org.smoothbuild.bytecode.expr.oper.CallB;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.oper.ParamRefB;
import org.smoothbuild.bytecode.expr.oper.SelectB;
import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.BlobB;
import org.smoothbuild.bytecode.expr.val.BoolB;
import org.smoothbuild.bytecode.expr.val.DefFuncB;
import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.bytecode.expr.val.NatFuncB;
import org.smoothbuild.bytecode.expr.val.StringB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.hashed.HashingBufferedSink;
import org.smoothbuild.bytecode.hashed.exc.DecodeBooleanExc;
import org.smoothbuild.bytecode.hashed.exc.DecodeByteExc;
import org.smoothbuild.bytecode.hashed.exc.DecodeHashSeqExc;
import org.smoothbuild.bytecode.hashed.exc.DecodeStringExc;
import org.smoothbuild.bytecode.hashed.exc.HashedDbExc;
import org.smoothbuild.bytecode.hashed.exc.NoSuchDataExc;
import org.smoothbuild.bytecode.type.CategoryB;
import org.smoothbuild.bytecode.type.exc.DecodeCatExc;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.IntTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.testing.TestContext;

import okio.ByteString;

public class ExprBCorruptedTest extends TestContext {
  @Nested
  class expr {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save value
       * in HashedDb.
       */
      Hash exprHash =
          hash(
              hash(stringTB()),
              hash("aaa"));
      assertThat(((StringB) bytecodeDb().get(exprHash)).toJ())
          .isEqualTo("aaa");
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void merkle_root_byte_count_is_not_multiple_of_hash_size(
        int byteCount) throws IOException, HashedDbExc {
      Hash exprHash =
          hash(ByteString.of(new byte[byteCount]));
      assertCall(() -> bytecodeDb().get(exprHash))
          .throwsException(cannotReadRootException(exprHash, null))
          .withCause(new DecodeHashSeqExc(exprHash, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void corrupted_type() throws Exception {
      Hash typeHash = Hash.of("not a type");
      Hash exprHash =
          hash(
              typeHash,
              hash("aaa"));
      assertCall(() -> bytecodeDb().get(exprHash))
          .throwsException(new DecodeExprCatExc(exprHash))
          .withCause(new DecodeCatExc(typeHash));
    }

    @Test
    public void reading_elems_from_not_stored_object_throws_exception() {
      Hash exprHash = Hash.of(33);
      assertCall(() -> bytecodeDb().get(exprHash))
          .throwsException(new DecodeExprNoSuchExprExc(exprHash))
          .withCause(new NoSuchDataExc(exprHash));
    }
  }

  @Nested
  class _array {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save array
       * in HashedDb.
       */
      Hash exprHash =
          hash(
              hash(arrayTB(stringTB())),
              hash(
                  hash(
                      hash(stringTB()),
                      hash("aaa")
                  ),
                  hash(
                      hash(stringTB()),
                      hash("bbb")
                  )
              ));
      List<String> strings = ((ArrayB) bytecodeDb().get(exprHash))
          .elems(StringB.class)
          .stream()
          .map(StringB::toJ)
          .collect(toList());
      assertThat(strings)
          .containsExactly("aaa", "bbb")
          .inOrder();
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(arrayTB(intTB()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          arrayTB(intTB()),
          hashedDb().writeSeq(),
          (Hash exprHash) -> ((ArrayB) bytecodeDb().get(exprHash)).elems(IntB.class)
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          arrayTB(intTB()),
          (Hash exprHash) -> ((ArrayB) bytecodeDb().get(exprHash)).elems(IntB.class));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_seq_size_different_than_multiple_of_hash_size(int byteCount) throws Exception {
      Hash notHashOfSeq = hash(ByteString.of(new byte[byteCount]));
      ArrayTB type = arrayTB(stringTB());
      Hash exprHash =
          hash(
              hash(type),
              notHashOfSeq
          );
      assertCall(() -> ((ArrayB) bytecodeDb().get(exprHash)).elems(ValB.class))
          .throwsException(new DecodeExprNodeExc(exprHash, type, DATA_PATH))
          .withCause(new DecodeHashSeqExc(
              notHashOfSeq, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_seq_elem_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash dataHash = hash(
          nowhere
      );
      ArrayTB type = arrayTB(stringTB());
      Hash exprHash =
          hash(
              hash(type),
              dataHash);
      assertCall(() -> ((ArrayB) bytecodeDb().get(exprHash)).elems(StringB.class))
          .throwsException(new DecodeExprNodeExc(exprHash, type, DATA_PATH + "[0]"))
          .withCause(new DecodeExprNoSuchExprExc(nowhere));
    }

    @Test
    public void with_one_elem_of_wrong_type() throws Exception {
      ArrayTB type = arrayTB(stringTB());
      Hash exprHash =
          hash(
              hash(type),
              hash(
                  hash(
                      hash(stringTB()),
                      hash("aaa")
                  ),
                  hash(
                      hash(boolTB()),
                      hash(true)
                  )
              ));
      assertCall(() -> ((ArrayB) bytecodeDb().get(exprHash)).elems(StringB.class))
          .throwsException(new DecodeExprWrongNodeTypeExc(
              exprHash, type, DATA_PATH, 1, stringTB(), boolTB()));
    }

    @Test
    public void with_one_elem_being_oper() throws Exception {
      ArrayTB type = arrayTB(stringTB());
      Hash exprHash =
          hash(
              hash(type),
              hash(
                  hash(
                      hash(stringTB()),
                      hash("aaa")
                  ),
                  hash(paramRefB(1))
              ));
      assertCall(() -> ((ArrayB) bytecodeDb().get(exprHash)).elems(StringB.class))
          .throwsException(new DecodeExprWrongNodeClassExc(
              exprHash, type, DATA_PATH, 1, ValB.class, ParamRefB.class));
    }
  }

  @Nested
  class _blob {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save blob
       * in HashedDb.
       */
      ByteString byteString = ByteString.of((byte) 1, (byte) 2);
      Hash exprHash =
          hash(
              hash(blobTB()),
              hash(byteString));
      assertThat(((BlobB) bytecodeDb().get(exprHash)).source().readByteString())
          .isEqualTo(byteString);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(blobTB());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          blobTB(),
          hashedDb().writeByte((byte) 1),
          (Hash exprHash) -> ((BlobB) bytecodeDb().get(exprHash)).source()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          blobTB(),
          (Hash exprHash) -> ((BlobB) bytecodeDb().get(exprHash)).source());
    }
  }

  @Nested
  class _bool {
    /*
     * This test makes sure that other tests in this class use proper scheme to save bool
     * in HashedDb.
     */
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void learning_test(boolean value) throws Exception {
      Hash exprHash =
          hash(
              hash(boolTB()),
              hash(value));
      assertThat(((BoolB) bytecodeDb().get(exprHash)).toJ())
          .isEqualTo(value);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(boolTB());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          boolTB(),
          hashedDb().writeBoolean(true),
          (Hash exprHash) -> ((BoolB) bytecodeDb().get(exprHash)).toJ()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          boolTB(),
          (Hash exprHash) -> ((BoolB) bytecodeDb().get(exprHash)).toJ());
    }

    @Test
    public void empty_bytes_as_data() throws Exception {
      Hash dataHash = hash(ByteString.of());
      Hash exprHash =
          hash(
              hash(boolTB()),
              dataHash);
      assertCall(() -> ((BoolB) bytecodeDb().get(exprHash)).toJ())
          .throwsException(new DecodeExprNodeExc(exprHash, boolTB(), DATA_PATH))
          .withCause(new DecodeBooleanExc(dataHash, new DecodeByteExc(dataHash)));
    }

    @Test
    public void more_than_one_byte_as_data() throws Exception {
      Hash dataHash = hash(ByteString.of((byte) 0, (byte) 0));
      Hash exprHash =
          hash(
              hash(boolTB()),
              dataHash);
      assertCall(() -> ((BoolB) bytecodeDb().get(exprHash)).toJ())
          .throwsException(new DecodeExprNodeExc(exprHash, boolTB(), DATA_PATH))
          .withCause(new DecodeBooleanExc(dataHash, new DecodeByteExc(dataHash)));
    }

    @ParameterizedTest
    @ArgumentsSource(AllByteValuesExceptZeroAndOneProvider.class)
    public void one_byte_data_not_equal_zero_nor_one(byte value)
        throws Exception {
      Hash dataHash = hash(ByteString.of(value));
      Hash exprHash =
          hash(
              hash(boolTB()),
              dataHash);
      assertCall(() -> ((BoolB) bytecodeDb().get(exprHash)).toJ())
          .throwsException(new DecodeExprNodeExc(exprHash, boolTB(), DATA_PATH))
          .withCause(new DecodeBooleanExc(dataHash));
    }
  }

  @Nested
  class _call {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save call
       * in HashedDb.
       */
      var funcT = funcTB(intTB(), stringTB(), intTB());
      var func = defFuncB(funcT, intB());
      var args = combineB(stringB(), intB());
      var exprHash =
          hash(
              hash(callCB(intTB())),
              hash(
                  hash(func),
                  hash(args)
              )
          );

      assertThat(((CallB) bytecodeDb().get(exprHash)).data().callable())
          .isEqualTo(func);
      assertThat(((CallB) bytecodeDb().get(exprHash)).data().args())
          .isEqualTo(args);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(callCB(intTB()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var funcT = funcTB(intTB(), stringTB(), intTB());
      var func = defFuncB(funcT, intB());
      var args = combineB(stringB(), intB());
      var dataHash = hash(
          hash(func),
          hash(args)
      );
      obj_root_with_two_data_hashes(
          callCB(intTB()),
          dataHash,
          (Hash exprHash) -> ((CallB) bytecodeDb().get(exprHash)).data());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          callCB(intTB()),
          (Hash exprHash) -> ((CallB) bytecodeDb().get(exprHash)).data());
    }

    @Test
    public void data_is_seq_with_one_elem() throws Exception {
      var funcT = funcTB(intTB(), stringTB(), intTB());
      var func = defFuncB(funcT, intB());
      var dataHash = hash(
          hash(func)
      );
      var cat = callCB(intTB());
      var exprHash =
          hash(
              hash(cat),
              dataHash
          );
      assertCall(() -> ((CallB) bytecodeDb().get(exprHash)).data())
          .throwsException(new DecodeExprWrongSeqSizeExc(exprHash, cat, DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_seq_with_three_elems() throws Exception {
      var funcT = funcTB(intTB(), stringTB(), intTB());
      var func = defFuncB(funcT, intB());
      var args = combineB(stringB(), intB());
      var dataHash = hash(
          hash(func),
          hash(args),
          hash(args)
      );
      var cat = callCB(intTB());
      var exprHash =
          hash(
              hash(cat),
              dataHash
          );
      assertCall(() -> ((CallB) bytecodeDb().get(exprHash)).data())
          .throwsException(new DecodeExprWrongSeqSizeExc(exprHash, cat, DATA_PATH, 2, 3));
    }

    @Test
    public void func_component_evalT_is_not_func() throws Exception {
      var func = intB(3);
      var args = combineB(stringB(), intB());
      var type = callCB(stringTB());
      var exprHash =
          hash(
              hash(type),
              hash(
                  hash(func),
                  hash(args)
              )
          );
      assertCall(() -> ((CallB) bytecodeDb().get(exprHash)).data())
          .throwsException(new DecodeExprWrongNodeTypeExc(
              exprHash, type, "func", FuncTB.class, intTB()));
    }

    @Test
    public void args_is_val_instead_of_combine() throws Exception {
      var funcT = funcTB(intTB(), stringTB(), intTB());
      var func = defFuncB(funcT, intB());
      var type = callCB(intTB());
      var exprHash =
          hash(
              hash(type),
              hash(
                  hash(func),
                  hash(intB())
              )
          );
      assertCall(() -> ((CallB) bytecodeDb().get(exprHash)).data())
          .throwsException(new DecodeExprWrongNodeClassExc(
              exprHash, type, DATA_PATH + "[1]", CombineB.class, IntB.class));
    }

    @Test
    public void args_component_evalT_is_not_combine_but_different_oper() throws Exception {
      var funcT = funcTB(intTB(), stringTB(), intTB());
      var func = defFuncB(funcT, intB());
      var type = callCB(intTB());
      var exprHash =
          hash(
              hash(type),
              hash(
                  hash(func),
                  hash(paramRefB(1))
              )
          );
      assertCall(() -> ((CallB) bytecodeDb().get(exprHash)).data())
          .throwsException(new DecodeExprWrongNodeClassExc(
              exprHash, type, DATA_PATH + "[1]", CombineB.class, ParamRefB.class));
    }

    @Test
    public void evalT_is_different_than_func_evalT_result() throws Exception {
      var funcT = funcTB(intTB(), stringTB());
      var func = defFuncB(funcT, intB());
      var args = combineB(stringB());
      var type = callCB(stringTB());
      var exprHash =
          hash(
              hash(type),
              hash(
                  hash(func),
                  hash(args)
              )
          );
      assertCall(() -> ((CallB) bytecodeDb().get(exprHash)).data())
          .throwsException(new DecodeExprWrongNodeTypeExc(
                  exprHash, type, "call.result", stringTB(), intTB()));
    }

    @Test
    public void func_evalT_params_does_not_match_args_evalTs() throws Exception {
      var funcT = funcTB(intTB(), stringTB(), boolTB());
      var func = defFuncB(funcT, intB());
      var args = combineB(stringB(), intB());
      var spec = callCB(intTB());
      var exprHash =
          hash(
              hash(spec),
              hash(
                  hash(func),
                  hash(args)
              )
          );
      assertCall(() -> ((CallB) bytecodeDb().get(exprHash)).data())
          .throwsException(new DecodeExprWrongNodeTypeExc(
              exprHash, spec, "args",
              tupleTB(stringTB(), boolTB()),
              tupleTB(stringTB(), intTB())
          ));
    }
  }

  @Nested
  class _combine {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save Combine
       * in HashedDb.
       */
      var expr1 = intB(1);
      var expr2 = stringB("abc");
      Hash exprHash =
          hash(
              hash(combineCB(intTB(), stringTB())),
              hash(
                  hash(expr1),
                  hash(expr2)
              ));
      var items = ((CombineB) bytecodeDb().get(exprHash)).items();
      assertThat(items)
          .containsExactly(expr1, expr2)
          .inOrder();
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(combineCB());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var item1 = intB(1);
      var item2 = stringB("abc");
      Hash dataHash = hash(
          hash(item1),
          hash(item2)
      );
      obj_root_with_two_data_hashes(
          orderCB(),
          dataHash,
          (Hash exprHash) -> ((CombineB) bytecodeDb().get(exprHash)).items()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          combineCB(),
          (Hash exprHash) -> ((CombineB) bytecodeDb().get(exprHash)).items());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_seq_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      var notHashOfSeq = hash(ByteString.of(new byte[byteCount]));
      var exprHash =
          hash(
              hash(combineCB()),
              notHashOfSeq
          );
      assertCall(() -> ((CombineB) bytecodeDb().get(exprHash)).items())
          .throwsException(new DecodeExprNodeExc(exprHash, combineCB(), DATA_PATH))
          .withCause(new DecodeHashSeqExc(
              notHashOfSeq, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_seq_item_pointing_nowhere() throws Exception {
      var nowhere = Hash.of(33);
      var exprHash =
          hash(
              hash(combineCB()),
              hash(
                  nowhere
              )
          );
      assertCall(() -> ((CombineB) bytecodeDb().get(exprHash)).items())
          .throwsException(new DecodeExprNodeExc(exprHash, combineCB(), DATA_PATH + "[0]"))
          .withCause(new DecodeExprNoSuchExprExc(nowhere));
    }

    @Test
    public void evaluation_type_items_size_is_different_than_actual_items_size()
        throws Exception {
      var item1 = intB();
      var type = combineCB(intTB(), stringTB());
      var exprHash =
          hash(
              hash(type),
              hash(
                  hash(item1)
              ));

      assertCall(() -> ((CombineB) bytecodeDb().get(exprHash)).items())
          .throwsException(new DecodeCombineWrongItemsSizeExc(exprHash, type, 1));
    }

    @Test
    public void evaluation_type_item_is_different_than_evaluation_type_of_one_of_items()
        throws Exception {
      var item1 = intB(1);
      var item2 = stringB("abc");
      var type = combineCB(intTB(), boolTB());
      var exprHash =
          hash(
              hash(type),
              hash(
                  hash(item1),
                  hash(item2)
              ));

      assertCall(() -> ((CombineB) bytecodeDb().get(exprHash)).items())
          .throwsException(new DecodeExprWrongNodeTypeExc(
              exprHash, type, "items[1]", boolTB(), stringTB()));
    }
  }

  @Nested
  class _def_func {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save DefFunc
       * in HashedDb.
       */
      var bodyExpr = boolB(true);
      var cat = defFuncCB(boolTB(), intTB(), stringTB());
      Hash exprHash =
          hash(
              hash(cat),
              hash(bodyExpr)
          );
      assertThat(((DefFuncB) bytecodeDb().get(exprHash)).body())
          .isEqualTo(bodyExpr);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(defFuncCB());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var bodyExpr = boolB(true);
      var cat = defFuncCB(boolTB(), intTB(), stringTB());
      var dataHash = hash(bodyExpr);
      obj_root_with_two_data_hashes(
          cat,
          dataHash,
          (Hash exprHash) -> ((DefFuncB) bytecodeDb().get(exprHash)).body());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_obj_but_nowhere(defFuncCB(),
          (Hash exprHash) -> ((DefFuncB) bytecodeDb().get(exprHash)).body());
    }

    @Test
    public void body_evaluation_type_is_not_equal_func_type_result() throws Exception {
      var bodyExpr = intB(3);
      var cat = defFuncCB(boolTB(), intTB(), stringTB());
      Hash exprHash =
          hash(
              hash(cat),
              hash(bodyExpr)
          );
      assertCall(() -> ((DefFuncB) bytecodeDb().get(exprHash)).body())
          .throwsException(new DecodeExprWrongNodeTypeExc(
              exprHash, cat, DATA_PATH, boolTB(), intTB()));
    }
  }

  @Nested
  class _if_func {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save IF
       * in HashedDb.
       */
      var hash = hash(
          hash(ifFuncCB(intTB()))
      );
      assertThat(hash)
          .isEqualTo(ifFuncB(intTB()).hash());
    }

    @Test
    public void root_with_data_hash() throws Exception {
      obj_root_with_data_hash(ifFuncCB(intTB()));
    }
  }

  @Nested
  class _int {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save int
       * in HashedDb.
       */
      ByteString byteString = ByteString.of((byte) 3, (byte) 2);
      Hash exprHash =
          hash(
              hash(intTB()),
              hash(byteString));
      assertThat(((IntB) bytecodeDb().get(exprHash)).toJ())
          .isEqualTo(BigInteger.valueOf(3 * 256 + 2));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(intTB());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          intTB(),
          hashedDb().writeByte((byte) 1),
          (Hash exprHash) -> ((IntB) bytecodeDb().get(exprHash)).toJ()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          intTB(),
          (Hash exprHash) -> ((IntB) bytecodeDb().get(exprHash)).toJ());
    }
  }

  @Nested
  class _map_func {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save IF
       * in HashedDb.
       */
      var hash = hash(
          hash(mapFuncCB(intTB(), stringTB()))
      );
      assertThat(hash)
          .isEqualTo(mapFuncB(intTB(), stringTB()).hash());
    }

    @Test
    public void root_with_data_hash() throws Exception {
      obj_root_with_data_hash(mapFuncCB(intTB(), stringTB()));
    }
  }

  @Nested
  class _nat_func {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save
       * Method in HashedDb.
       */
      var category = natFuncCB(stringTB(), intTB());
      var jar = blobB();
      var classBinaryName = stringB();
      var isPure = boolB(true);
      Hash exprHash =
          hash(
              hash(category),
              hash(
                  hash(jar),
                  hash(classBinaryName),
                  hash(isPure)
              )
          );

      assertThat(((NatFuncB) bytecodeDb().get(exprHash)).jar())
          .isEqualTo(jar);
      assertThat(((NatFuncB) bytecodeDb().get(exprHash)).classBinaryName())
          .isEqualTo(classBinaryName);
      assertThat(((NatFuncB) bytecodeDb().get(exprHash)).isPure())
          .isEqualTo(isPure);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(funcTB(stringTB(), intTB()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var category = natFuncCB(stringTB(), intTB());
      var jar = blobB();
      var classBinaryName = stringB();
      var isPure = boolB(true);
      Hash dataHash = hash(
          hash(jar),
          hash(classBinaryName),
          hash(isPure)
      );
      obj_root_with_two_data_hashes(category, dataHash,
          (Hash exprHash) -> ((NatFuncB) bytecodeDb().get(exprHash)).classBinaryName());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      var category = natFuncCB(stringTB(), intTB());
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(category,
          (Hash exprHash) -> ((NatFuncB) bytecodeDb().get(exprHash)).classBinaryName());
    }

    @Test
    public void data_is_seq_with_two_elem() throws Exception {
      var category = natFuncCB(stringTB(), intTB());
      var jar = blobB();
      var classBinaryName = stringB();
      var dataHash = hash(
          hash(jar),
          hash(classBinaryName)
      );
      var exprHash =
          hash(
              hash(category),
              dataHash
          );

      assertCall(() -> ((NatFuncB) bytecodeDb().get(exprHash)).classBinaryName())
          .throwsException(new DecodeExprWrongSeqSizeExc(
              exprHash, category, DATA_PATH, 3, 2));
    }

    @Test
    public void data_is_seq_with_four_elems() throws Exception {
      var type = natFuncCB(stringTB(), intTB());
      var jar = blobB();
      var classBinaryName = stringB();
      var isPure = boolB(true);
      var dataHash = hash(
          hash(jar),
          hash(classBinaryName),
          hash(isPure),
          hash(isPure)
      );
      var exprHash =
          hash(
              hash(type),
              dataHash
          );

      assertCall(() -> ((NatFuncB) bytecodeDb().get(exprHash)).classBinaryName())
          .throwsException(new DecodeExprWrongSeqSizeExc(
              exprHash, type, DATA_PATH, 3, 4));
    }

    @Test
    public void jar_file_is_not_blob_value() throws Exception {
      var category = natFuncCB(stringTB(), intTB());
      var jar = stringB();
      var classBinaryName = stringB();
      var isPure = boolB(true);
      var exprHash =
          hash(
              hash(category),
              hash(
                  hash(jar),
                  hash(classBinaryName),
                  hash(isPure)
              )
          );
      assertCall(() -> ((NatFuncB) bytecodeDb().get(exprHash)).jar())
          .throwsException(new DecodeExprWrongNodeClassExc(
              exprHash, category, DATA_PATH + "[0]", BlobB.class, StringB.class));
    }

    @Test
    public void class_binary_name_is_not_string_value() throws Exception {
      var category = natFuncCB(stringTB(), intTB());
      var jar = blobB();
      var classBinaryName = intB();
      var isPure = boolB(true);
      var exprHash =
          hash(
              hash(category),
              hash(
                  hash(jar),
                  hash(classBinaryName),
                  hash(isPure)
              )
          );

      assertCall(() -> ((NatFuncB) bytecodeDb().get(exprHash)).classBinaryName())
          .throwsException(new DecodeExprWrongNodeClassExc(
              exprHash, category, DATA_PATH + "[1]", StringB.class, IntB.class));
    }

    @Test
    public void is_pure_is_not_bool_value() throws Exception {
      var category = natFuncCB(stringTB(), intTB());
      var jar = blobB();
      var classBinaryName = stringB();
      var isPure = stringB();
      var exprHash =
          hash(
              hash(category),
              hash(
                  hash(jar),
                  hash(classBinaryName),
                  hash(isPure)
              )
          );

      assertCall(() -> ((NatFuncB) bytecodeDb().get(exprHash)).isPure())
          .throwsException(new DecodeExprWrongNodeClassExc(
              exprHash, category, DATA_PATH + "[2]", BoolB.class, StringB.class));
    }
  }

  @Nested
  class _order {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save Order expr
       * in HashedDb.
       */
      var expr1 = intB(1);
      var expr2 = intB(2);
      Hash exprHash =
          hash(
              hash(orderCB(intTB())),
              hash(
                  hash(expr1),
                  hash(expr2)
              ));
      var elems = ((OrderB) bytecodeDb().get(exprHash)).elems();
      assertThat(elems)
          .containsExactly(expr1, expr2)
          .inOrder();
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(orderCB());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var expr1 = intB(1);
      var expr2 = intB(2);
      var dataHash = hash(
          hash(expr1),
          hash(expr2)
      );
      obj_root_with_two_data_hashes(
          orderCB(),
          dataHash,
          (Hash exprHash) -> ((OrderB) bytecodeDb().get(exprHash)).elems()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          orderCB(),
          (Hash exprHash) -> ((OrderB) bytecodeDb().get(exprHash)).elems());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_seq_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSeq = hash(ByteString.of(new byte[byteCount]));
      Hash exprHash =
          hash(
              hash(orderCB()),
              notHashOfSeq
          );
      assertCall(() -> ((OrderB) bytecodeDb().get(exprHash)).elems())
          .throwsException(new DecodeExprNodeExc(exprHash, orderCB(), DATA_PATH))
          .withCause(new DecodeHashSeqExc(
              notHashOfSeq, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_seq_elem_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash exprHash =
          hash(
              hash(orderCB()),
              hash(
                  nowhere
              )
          );
      assertCall(() -> ((OrderB) bytecodeDb().get(exprHash)).elems())
          .throwsException(new DecodeExprNodeExc(exprHash, orderCB(), DATA_PATH + "[0]"))
          .withCause(new DecodeExprNoSuchExprExc(nowhere));
    }

    @Test
    public void evaluation_type_elem_is_different_than_evaluation_type_of_one_of_elems()
        throws Exception {
      var expr1 = intB();
      var expr2 = stringB();
      var type = orderCB(intTB());
      Hash exprHash =
          hash(
              hash(type),
              hash(
                  hash(expr1),
                  hash(expr2)
              ));
      assertCall(() -> ((OrderB) bytecodeDb().get(exprHash)).elems())
          .throwsException(new DecodeExprWrongNodeTypeExc(
                  exprHash, type, "elems[1]", intTB(), stringTB()));
    }
  }

  @Nested
  class _param_ref {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save ref
       * in HashedDb.
       */
      ByteString byteString = ByteString.of((byte) 3, (byte) 2);
      Hash exprHash =
          hash(
              hash(paramRefCB(stringTB())),
              hash(byteString));
      assertThat(((ParamRefB) bytecodeDb().get(exprHash)).value())
          .isEqualTo(BigInteger.valueOf(3 * 256 + 2));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(paramRefCB());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var index = intB(0);
      Hash dataHash = hash(index);
      obj_root_with_two_data_hashes(
          paramRefCB(intTB()),
          dataHash,
          (Hash exprHash) -> ((ParamRefB) bytecodeDb().get(exprHash)).value()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          paramRefCB(intTB()),
          (Hash exprHash) -> ((ParamRefB) bytecodeDb().get(exprHash)).value());
    }
  }

  @Nested
  class _select {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save smooth
       * select in HashedDb.
       */
      var tupleT = tupleTB(stringTB());
      var tuple = tupleB(tupleT, stringB("abc"));
      var selectable = (ValB) tuple;
      var index = intB(0);
      Hash exprHash =
          hash(
              hash(selectCB(stringTB())),
              hash(
                  hash(selectable),
                  hash(index)
              )
          );
      assertThat(((SelectB) bytecodeDb().get(exprHash)).data())
          .isEqualTo(new SelectB.Data(selectable, index));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(selectCB(intTB()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var index = intB(2);
      var expr = intB(123);
      Hash dataHash = hash(
          hash(expr),
          hash(index)
      );
      obj_root_with_two_data_hashes(
          selectCB(),
          dataHash,
          (Hash exprHash) -> ((SelectB) bytecodeDb().get(exprHash)).data());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          selectCB(),
          (Hash exprHash) -> ((SelectB) bytecodeDb().get(exprHash)).data());
    }

    @Test
    public void data_is_seq_with_one_elem() throws Exception {
      var expr = intB(123);
      var dataHash = hash(
          hash(expr)
      );
      Hash exprHash =
          hash(
              hash(selectCB()),
              dataHash
          );
      assertCall(() -> ((SelectB) bytecodeDb().get(exprHash)).data())
          .throwsException(new DecodeExprWrongSeqSizeExc(
              exprHash, selectCB(), DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_seq_with_three_elems() throws Exception {
      var index = intB(2);
      var expr = intB(123);
      var dataHash = hash(
          hash(expr),
          hash(index),
          hash(index)
      );
      Hash exprHash =
          hash(
              hash(selectCB()),
              dataHash
          );
      assertCall(() -> ((SelectB) bytecodeDb().get(exprHash)).data())
          .throwsException(new DecodeExprWrongSeqSizeExc(
              exprHash, selectCB(), DATA_PATH, 2, 3));
    }

    @Test
    public void tuple_is_not_tuple_expr() throws Exception {
      var expr = intB(3);
      var index = intB(0);
      var type = selectCB(stringTB());
      Hash exprHash =
          hash(
              hash(type),
              hash(
                  hash(expr),
                  hash(index)
              )
          );

      assertCall(() -> ((SelectB) bytecodeDb().get(exprHash)).data())
          .throwsException(new DecodeExprWrongNodeClassExc(
              exprHash, type, "tuple", TupleTB.class, IntTB.class));
    }

    @Test
    public void index_is_out_of_bounds() throws Exception {
      var tupleT = tupleTB(stringTB());
      var tuple = tupleB(tupleT, stringB("abc"));
      var index = intB(1);
      var type = selectCB(stringTB());
      Hash exprHash =
          hash(
              hash(type),
              hash(
                  hash(tuple),
                  hash(index)
              )
          );

      assertCall(() -> ((SelectB) bytecodeDb().get(exprHash)).data())
          .throwsException(new DecodeSelectIndexOutOfBoundsExc(exprHash, type, 1, 1));
    }

    @Test
    public void evaluation_type_is_different_than_type_of_item_pointed_to_by_index()
        throws Exception {
      var tupleT = tupleTB(stringTB());
      var tuple = tupleB(tupleT, stringB("abc"));
      var index = intB(0);
      var type = selectCB(intTB());
      Hash exprHash =
          hash(
              hash(type),
              hash(
                  hash(tuple),
                  hash(index)
              )
          );

      assertCall(() -> ((SelectB) bytecodeDb().get(exprHash)).data())
          .throwsException(new DecodeSelectWrongEvalTypeExc(exprHash, type, stringTB()));
    }

    @Test
    public void index_is_string_instead_of_int() throws Exception {
      var type = selectCB(stringTB());
      var tupleT = tupleTB(stringTB());
      var tuple = tupleB(tupleT, stringB("abc"));
      var strVal = stringB("abc");
      Hash exprHash =
          hash(
              hash(type),
              hash(
                  hash(tuple),
                  hash(strVal)
              )
          );
      assertCall(() -> ((SelectB) bytecodeDb().get(exprHash)).data())
          .throwsException(new DecodeExprWrongNodeClassExc(
              exprHash, type, DATA_PATH + "[1]", IntB.class, StringB.class));
    }
  }

  @Nested
  class _string {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save bool
       * in HashedDb.
       */
      Hash exprHash =
          hash(
              hash(stringTB()),
              hash("aaa"));
      assertThat(((StringB) bytecodeDb().get(exprHash)).toJ())
          .isEqualTo("aaa");
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(stringTB());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          stringTB(),
          hashedDb().writeBoolean(true),
          (Hash exprHash) -> ((StringB) bytecodeDb().get(exprHash)).toJ()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          stringTB(),
          (Hash exprHash) -> ((StringB) bytecodeDb().get(exprHash)).toJ());
    }

    @Test
    public void data_being_invalid_utf8_seq() throws Exception {
      Hash notStringHash = hash(illegalString());
      Hash exprHash =
          hash(
              hash(stringTB()),
              notStringHash);
      assertCall(() -> ((StringB) bytecodeDb().get(exprHash)).toJ())
          .throwsException(new DecodeExprNodeExc(exprHash, stringTB(), DATA_PATH))
          .withCause(new DecodeStringExc(notStringHash, null));
    }
  }

  @Nested
  class _tuple {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save tuple
       * in HashedDb.
       */
      assertThat(
          hash(
              hash(personTB()),
              hash(
                  hash(stringB("John")),
                  hash(stringB("Doe")))))
          .isEqualTo(personB("John", "Doe").hash());
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(personTB());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          personTB(),
          hashedDb().writeBoolean(true),
          (Hash exprHash) -> ((TupleB) bytecodeDb().get(exprHash)).get(0)
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          personTB(),
          (Hash exprHash) -> ((TupleB) bytecodeDb().get(exprHash)).get(0));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_seq_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSeq = hash(ByteString.of(new byte[byteCount]));
      Hash exprHash =
          hash(
              hash(personTB()),
              notHashOfSeq);
      assertCall(() -> ((TupleB) bytecodeDb().get(exprHash)).get(0))
          .throwsException(new DecodeExprNodeExc(exprHash, personTB(), DATA_PATH))
          .withCause(new DecodeHashSeqExc(
              notHashOfSeq, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_seq_elem_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash dataHash = hash(
          nowhere,
          nowhere
      );
      Hash exprHash =
          hash(
              hash(personTB()),
              dataHash
          );
      assertCall(() -> ((TupleB) bytecodeDb().get(exprHash)).get(0))
          .throwsException(new DecodeExprNodeExc(exprHash, personTB(), DATA_PATH + "[0]"))
          .withCause(new DecodeExprNoSuchExprExc(nowhere));
    }

    @Test
    public void with_too_few_elems() throws Exception {
      Hash dataHash =
          hash(
              hash(stringB("John")));
      Hash exprHash =
          hash(
              hash(personTB()),
              dataHash);
      TupleB tuple = (TupleB) bytecodeDb().get(exprHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new DecodeExprWrongSeqSizeExc(exprHash, personTB(), DATA_PATH, 2, 1));
    }

    @Test
    public void with_too_many_elems() throws Exception {
      Hash dataHash =
          hash(
              hash(stringB("John")),
              hash(stringB("Doe")),
              hash(stringB("junk")));
      Hash exprHash =
          hash(
              hash(personTB()),
              dataHash);
      TupleB tuple = (TupleB) bytecodeDb().get(exprHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new DecodeExprWrongSeqSizeExc(exprHash, personTB(), DATA_PATH, 2, 3));
    }

    @Test
    public void with_elem_of_wrong_type() throws Exception {
      Hash exprHash =
          hash(
              hash(personTB()),
              hash(
                  hash(stringB("John")),
                  hash(boolB(true))));
      TupleB tuple = (TupleB) bytecodeDb().get(exprHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new DecodeExprWrongNodeTypeExc(
              exprHash, personTB(), DATA_PATH, personTB(), "`{String,Bool}`"));
    }

    @Test
    public void with_elem_being_oper() throws Exception {
      Hash exprHash =
          hash(
              hash(personTB()),
              hash(
                  hash(stringB("John")),
                  hash(paramRefB(1))));
      TupleB tuple = (TupleB) bytecodeDb().get(exprHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new DecodeExprWrongNodeClassExc(
              exprHash, personTB(), DATA_PATH + "[1]", ValB.class, ParamRefB.class));
    }
  }

  private void obj_root_without_data_hash(CategoryB cat) throws HashedDbExc {
    var exprHash =
        hash(
            hash(cat));
    assertCall(() -> bytecodeDb().get(exprHash))
        .throwsException(wrongSizeOfRootSeqException(exprHash, cat, 1));
  }

  private void obj_root_with_data_hash(CategoryB category) throws HashedDbExc {
    var hash =
        hash(
            hash(category),
            hash(category));
    assertCall(() -> bytecodeDb().get(hash))
        .throwsException(wrongSizeOfRootSeqException(hash, category, 2));
  }

  private void obj_root_with_two_data_hashes(
      CategoryB type, Hash dataHash, Function<Hash, ?> readClosure) throws HashedDbExc {
    Hash exprHash =
        hash(
            hash(type),
            dataHash,
            dataHash);
    assertCall(() -> readClosure.apply(exprHash))
        .throwsException(wrongSizeOfRootSeqException(exprHash, 3));
  }

  private void obj_root_with_data_hash_not_pointing_to_obj_but_nowhere(
      CategoryB type, Function<Hash, ?> readClosure) throws HashedDbExc {
    Hash dataHash = Hash.of(33);
    Hash exprHash =
        hash(
            hash(type),
            dataHash);
    assertCall(() -> readClosure.apply(exprHash))
        .throwsException(new DecodeExprNodeExc(exprHash, type, DATA_PATH))
        .withCause(new DecodeExprNoSuchExprExc(dataHash));
  }

  private void obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
      CategoryB category, Consumer<Hash> readClosure) throws HashedDbExc {
    Hash dataHash = Hash.of(33);
    Hash exprHash =
        hash(
            hash(category),
            dataHash);
    assertCall(() -> readClosure.accept(exprHash))
        .throwsException(new DecodeExprNodeExc(exprHash, category, DATA_PATH))
        .withCause(new NoSuchDataExc(dataHash));
  }

  // helper methods

  private static class AllByteValuesExceptZeroAndOneProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return IntStream.rangeClosed(-128, 127)
          .filter(v -> v != 0 && v != 1)
          .boxed()
          .map(Integer::byteValue)
          .map(Arguments::of);
    }
  }

  protected Hash hash(String string) throws HashedDbExc {
    return hashedDb().writeString(string);
  }

  protected Hash hash(boolean value) throws IOException, HashedDbExc {
    return hash((byte) (value ? 1 : 0));
  }

  protected Hash hash(byte value) throws IOException, HashedDbExc {
    try (HashingBufferedSink sink = hashedDb().sink()) {
      sink.writeByte(value);
      sink.close();
      return sink.hash();
    }
  }

  protected Hash hash(ByteString bytes) throws IOException, HashedDbExc {
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
}
