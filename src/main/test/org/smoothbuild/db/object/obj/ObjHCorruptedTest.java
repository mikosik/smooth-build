package org.smoothbuild.db.object.obj;

import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.db.object.obj.base.ObjH.DATA_PATH;
import static org.smoothbuild.db.object.obj.exc.DecodeObjRootExc.cannotReadRootException;
import static org.smoothbuild.db.object.obj.exc.DecodeObjRootExc.wrongSizeOfRootSeqException;
import static org.smoothbuild.testing.StringCreators.illegalString;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;

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
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.db.hashed.exc.DecodeBooleanExc;
import org.smoothbuild.db.hashed.exc.DecodeByteExc;
import org.smoothbuild.db.hashed.exc.DecodeHashSeqExc;
import org.smoothbuild.db.hashed.exc.DecodeStringExc;
import org.smoothbuild.db.hashed.exc.HashedDbExc;
import org.smoothbuild.db.hashed.exc.NoSuchDataExc;
import org.smoothbuild.db.object.obj.base.ObjH;
import org.smoothbuild.db.object.obj.exc.DecodeCombineWrongItemsSizeExc;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvalTypeOfCompExc;
import org.smoothbuild.db.object.obj.exc.DecodeObjNodeExc;
import org.smoothbuild.db.object.obj.exc.DecodeObjTypeExc;
import org.smoothbuild.db.object.obj.exc.DecodeSelectIndexOutOfBoundsExc;
import org.smoothbuild.db.object.obj.exc.DecodeSelectWrongEvalTypeExc;
import org.smoothbuild.db.object.obj.exc.NoSuchObjExc;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjNodeExc;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjSeqExc;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.obj.expr.CombineH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.ParamRefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.expr.SelectH.SelectData;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.DefFuncH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.NatFuncH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.db.object.type.base.SpecH;
import org.smoothbuild.db.object.type.exc.DecodeTypeExc;
import org.smoothbuild.db.object.type.expr.CallTypeH;
import org.smoothbuild.db.object.type.expr.CombineTypeH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.db.object.type.val.DefFuncTypeH;
import org.smoothbuild.db.object.type.val.FuncTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ObjHCorruptedTest extends TestingContext {
  @Nested
  class _obj {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save value
       * in HashedDb.
       */
      Hash objHash =
          hash(
              hash(stringHT()),
              hash("aaa"));
      assertThat(((StringH) objDb().get(objHash)).toJ())
          .isEqualTo("aaa");
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void merkle_root_byte_count_is_not_multiple_of_hash_size(
        int byteCount) throws IOException, HashedDbExc {
      Hash objHash =
          hash(ByteString.of(new byte[byteCount]));
      assertCall(() -> objDb().get(objHash))
          .throwsException(cannotReadRootException(objHash, null))
          .withCause(new DecodeHashSeqExc(objHash, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void corrupted_type() throws Exception {
      Hash typeHash = Hash.of("not a type");
      Hash objHash =
          hash(
              typeHash,
              hash("aaa"));
      assertCall(() -> objDb().get(objHash))
          .throwsException(new DecodeObjTypeExc(objHash))
          .withCause(new DecodeTypeExc(typeHash));
    }

    @Test
    public void reading_elems_from_not_stored_object_throws_exception() {
      Hash objHash = Hash.of(33);
      assertCall(() -> objDb().get(objHash))
          .throwsException(new NoSuchObjExc(objHash))
          .withCause(new NoSuchDataExc(objHash));
    }
  }

  @Nested
  class _any {
    @Test
    public void learning_test() throws Exception {
      Hash objHash =
          hash(
              hash(anyHT()),
              hash("aaa"));
      assertCall(() -> objDb().get(objHash))
          .throwsException(UnsupportedOperationException.class);
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
      Hash objHash =
          hash(
              hash(arrayHT(stringHT())),
              hash(
                  hash(
                      hash(stringHT()),
                      hash("aaa")
                  ),
                  hash(
                      hash(stringHT()),
                      hash("bbb")
                  )
              ));
      List<String> strings = ((ArrayH) objDb().get(objHash))
          .elems(StringH.class)
          .stream()
          .map(StringH::toJ)
          .collect(toList());
      assertThat(strings)
          .containsExactly("aaa", "bbb")
          .inOrder();
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(arrayHT(intHT()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          arrayHT(intHT()),
          hashedDb().writeSeq(),
          (Hash objHash) -> ((ArrayH) objDb().get(objHash)).elems(IntH.class)
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          arrayHT(intHT()),
          (Hash objHash) -> ((ArrayH) objDb().get(objHash)).elems(IntH.class));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_seq_size_different_than_multiple_of_hash_size(int byteCount) throws Exception {
      Hash notHashOfSeq = hash(ByteString.of(new byte[byteCount]));
      ArrayTypeH type = arrayHT(stringHT());
      Hash objHash =
          hash(
              hash(type),
              notHashOfSeq
          );
      assertCall(() -> ((ArrayH) objDb().get(objHash)).elems(ValH.class))
          .throwsException(new DecodeObjNodeExc(objHash, type, DATA_PATH))
          .withCause(new DecodeHashSeqExc(
              notHashOfSeq, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_seq_elem_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash dataHash = hash(
          nowhere
      );
      ArrayTypeH type = arrayHT(stringHT());
      Hash objHash =
          hash(
              hash(type),
              dataHash);
      assertCall(() -> ((ArrayH) objDb().get(objHash)).elems(StringH.class))
          .throwsException(new DecodeObjNodeExc(objHash, type, DATA_PATH + "[0]"))
          .withCause(new NoSuchObjExc(nowhere));
    }

    @Test
    public void with_one_elem_of_wrong_types() throws Exception {
      ArrayTypeH type = arrayHT(stringHT());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(
                      hash(stringHT()),
                      hash("aaa")
                  ),
                  hash(
                      hash(boolHT()),
                      hash(true)
                  )
              ));
      assertCall(() -> ((ArrayH) objDb().get(objHash)).elems(StringH.class))
          .throwsException(new UnexpectedObjNodeExc(
              objHash, type, DATA_PATH, 1, stringHT(), boolHT()));
    }

    @Test
    public void with_one_elem_being_expr() throws Exception {
      ArrayTypeH type = arrayHT(stringHT());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(
                      hash(stringHT()),
                      hash("aaa")
                  ),
                  hash(paramRefH(1))
              ));
      assertCall(() -> ((ArrayH) objDb().get(objHash)).elems(StringH.class))
          .throwsException(new UnexpectedObjNodeExc(
              objHash, type, DATA_PATH, 1, ValH.class, ParamRefH.class));
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
      Hash objHash =
          hash(
              hash(blobHT()),
              hash(byteString));
      assertThat(((BlobH) objDb().get(objHash)).source().readByteString())
          .isEqualTo(byteString);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(blobHT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          blobHT(),
          hashedDb().writeByte((byte) 1),
          (Hash objHash) -> ((BlobH) objDb().get(objHash)).source()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          blobHT(),
          (Hash objHash) -> ((BlobH) objDb().get(objHash)).source());
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
      Hash objHash =
          hash(
              hash(boolHT()),
              hash(value));
      assertThat(((BoolH) objDb().get(objHash)).toJ())
          .isEqualTo(value);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(boolHT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          boolHT(),
          hashedDb().writeBoolean(true),
          (Hash objHash) -> ((BoolH) objDb().get(objHash)).toJ()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          boolHT(),
          (Hash objHash) -> ((BoolH) objDb().get(objHash)).toJ());
    }

    @Test
    public void empty_bytes_as_data() throws Exception {
      Hash dataHash = hash(ByteString.of());
      Hash objHash =
          hash(
              hash(boolHT()),
              dataHash);
      assertCall(() -> ((BoolH) objDb().get(objHash)).toJ())
          .throwsException(new DecodeObjNodeExc(objHash, boolHT(), DATA_PATH))
          .withCause(new DecodeBooleanExc(dataHash, new DecodeByteExc(dataHash)));
    }

    @Test
    public void more_than_one_byte_as_data() throws Exception {
      Hash dataHash = hash(ByteString.of((byte) 0, (byte) 0));
      Hash objHash =
          hash(
              hash(boolHT()),
              dataHash);
      assertCall(() -> ((BoolH) objDb().get(objHash)).toJ())
          .throwsException(new DecodeObjNodeExc(objHash, boolHT(), DATA_PATH))
          .withCause(new DecodeBooleanExc(dataHash, new DecodeByteExc(dataHash)));
    }

    @ParameterizedTest
    @ArgumentsSource(AllByteValuesExceptZeroAndOneProvider.class)
    public void one_byte_data_not_equal_zero_nor_one(byte value)
        throws Exception {
      Hash dataHash = hash(ByteString.of(value));
      Hash objHash =
          hash(
              hash(boolHT()),
              dataHash);
      assertCall(() -> ((BoolH) objDb().get(objHash)).toJ())
          .throwsException(new DecodeObjNodeExc(objHash, boolHT(), DATA_PATH))
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
      var funcType = defFuncHT(intHT(), list(stringHT(), intHT()));
      var func = defFuncH(funcType, intH());
      CombineH args = combineH(list(stringH(), intH()));
      Hash objHash =
          hash(
              hash(callHT()),
              hash(
                  hash(func),
                  hash(args)
              )
          );

      assertThat(((CallH) objDb().get(objHash)).data().callable())
          .isEqualTo(func);
      assertThat(((CallH) objDb().get(objHash)).data().args())
          .isEqualTo(args);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(callHT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var func = intH(0);
      var args = combineH(list(stringH(), intH()));
      Hash dataHash = hash(
          hash(func),
          hash(args)
      );
      obj_root_with_two_data_hashes(
          callHT(),
          dataHash,
          (Hash objHash) -> ((CallH) objDb().get(objHash)).data());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          callHT(),
          (Hash objHash) -> ((CallH) objDb().get(objHash)).data());
    }

    @Test
    public void data_is_seq_with_one_elem() throws Exception {
      var func = intH(0);
      Hash dataHash = hash(
          hash(func)
      );
      Hash objHash =
          hash(
              hash(callHT()),
              dataHash
          );
      assertCall(() -> ((CallH) objDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSeqExc(objHash, callHT(), DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_seq_with_three_elems() throws Exception {
      var func = intH(0);
      var args = combineH(list(stringH(), intH()));
      Hash dataHash = hash(
          hash(func),
          hash(args),
          hash(args)
      );
      Hash objHash =
          hash(
              hash(callHT()),
              dataHash
          );
      assertCall(() -> ((CallH) objDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSeqExc(objHash, callHT(), DATA_PATH, 2, 3));
    }

    @Test
    public void func_component_evaluation_type_is_not_func() throws Exception {
      var func = intH(3);
      CombineH args = combineH(list(stringH(), intH()));
      CallTypeH type = callHT(stringHT());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(func),
                  hash(args)
              )
          );
      assertCall(() -> ((CallH) objDb().get(objHash)).data())
          .throwsException(new DecodeExprWrongEvalTypeOfCompExc(
              objHash, type, "func", FuncTypeH.class, intHT()));
    }

    @Test
    public void args_is_val_instead_of_expr() throws Exception {
      var funcType = defFuncHT(intHT(), list(stringHT(), intHT()));
      var func = defFuncH(funcType, intH());
      Hash objHash =
          hash(
              hash(callHT()),
              hash(
                  hash(func),
                  hash(intH())
              )
          );
      assertCall(() -> ((CallH) objDb().get(objHash)).data())
          .throwsException(new UnexpectedObjNodeExc(
              objHash, callHT(), DATA_PATH + "[1]", CombineH.class, IntH.class));
    }

    @Test
    public void args_component_evaluation_type_is_not_combine_but_different_expr()
        throws Exception {
      var funcType = defFuncHT(intHT(), list(stringHT(), intHT()));
      var func = defFuncH(funcType, intH());
      var type = callHT();
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(func),
                  hash(paramRefH(1))
              )
          );
      assertCall(() -> ((CallH) objDb().get(objHash)).data())
          .throwsException(new UnexpectedObjNodeExc(
              objHash, type, DATA_PATH + "[1]", CombineH.class, ParamRefH.class));
    }

    @Test
    public void evaluation_type_is_different_than_func_evaluation_type_result()
        throws Exception {
      DefFuncTypeH funcType = defFuncHT(intHT(), list(stringHT()));
      var func = defFuncH(funcType, intH());
      var args = combineH(list(stringH()));
      var type = callHT(stringHT());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(func),
                  hash(args)
              )
          );
      assertCall(() -> ((CallH) objDb().get(objHash)).data())
          .throwsException(new DecodeExprWrongEvalTypeOfCompExc(
                  objHash, type, "func.result", stringHT(), intHT()));
    }

    @Test
    public void func_evaluation_type_params_does_not_match_args_evaluation_types()
        throws Exception {
      var funcType = defFuncHT(intHT(), list(stringHT(), boolHT()));
      var func = defFuncH(funcType, intH());
      var args = combineH(list(stringH(), intH()));
      var spec = callHT(intHT());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(func),
                  hash(args)
              )
          );
      assertCall(() -> ((CallH) objDb().get(objHash)).data())
          .throwsException(new DecodeExprWrongEvalTypeOfCompExc(
              objHash, spec, "args",
              tupleHT(list(stringHT(), boolHT())),
              tupleHT(list(stringHT(), intHT()))
          ));
    }
  }

  @Nested
  class _def_func {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save Lambda
       * in HashedDb.
       */
      var bodyExpr = boolH(true);
      FuncTypeH type = defFuncHT(boolHT(), list(intHT(), stringHT()));
      Hash objHash =
          hash(
              hash(type),
              hash(bodyExpr)
          );
      assertThat(((DefFuncH) objDb().get(objHash)).body())
          .isEqualTo(bodyExpr);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(defFuncHT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var bodyExpr = boolH(true);
      var type = defFuncHT(boolHT(), list(intHT(), stringHT()));
      var dataHash = hash(bodyExpr);
      obj_root_with_two_data_hashes(
          type,
          dataHash,
          (Hash objHash) -> ((DefFuncH) objDb().get(objHash)).body());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_obj_but_nowhere(
          defFuncHT(),
          (Hash objHash) -> ((DefFuncH) objDb().get(objHash)).body());
    }

    @Test
    public void body_evaluation_type_is_not_equal_func_type_result() throws Exception {
      var bodyExpr = intH(3);
      var type = defFuncHT(boolHT(), list(intHT(), stringHT()));
      Hash objHash =
          hash(
              hash(type),
              hash(bodyExpr)
          );
      assertCall(() -> ((DefFuncH) objDb().get(objHash)).body())
          .throwsException(new DecodeExprWrongEvalTypeOfCompExc(
              objHash, type, DATA_PATH, boolHT(), intHT()));
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
      var expr1 = intH(1);
      var expr2 = intH(2);
      Hash objHash =
          hash(
              hash(orderHT()),
              hash(
                  hash(expr1),
                  hash(expr2)
              ));
      var elems = ((OrderH) objDb().get(objHash)).elems();
      assertThat(elems)
          .containsExactly(expr1, expr2)
          .inOrder();
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(orderHT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var expr1 = intH(1);
      var expr2 = intH(2);
      var dataHash = hash(
          hash(expr1),
          hash(expr2)
      );
      obj_root_with_two_data_hashes(
          orderHT(),
          dataHash,
          (Hash objHash) -> ((OrderH) objDb().get(objHash)).elems()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          orderHT(),
          (Hash objHash) -> ((OrderH) objDb().get(objHash)).elems());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_seq_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSeq = hash(ByteString.of(new byte[byteCount]));
      Hash objHash =
          hash(
              hash(orderHT()),
              notHashOfSeq
          );
      assertCall(() -> ((OrderH) objDb().get(objHash)).elems())
          .throwsException(new DecodeObjNodeExc(objHash, orderHT(), DATA_PATH))
          .withCause(new DecodeHashSeqExc(
              notHashOfSeq, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_seq_elem_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash objHash =
          hash(
              hash(orderHT()),
              hash(
                  nowhere
              )
          );
      assertCall(() -> ((OrderH) objDb().get(objHash)).elems())
          .throwsException(new DecodeObjNodeExc(objHash, orderHT(), DATA_PATH + "[0]"))
          .withCause(new NoSuchObjExc(nowhere));
    }

    @Test
    public void evaluation_type_elem_is_different_than_evaluation_type_of_one_of_elems()
        throws Exception {
      var expr1 = intH();
      var expr2 = stringH();
      var type = orderHT(intHT());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(expr1),
                  hash(expr2)
              ));
      assertCall(() -> ((OrderH) objDb().get(objHash)).elems())
          .throwsException(
              new DecodeExprWrongEvalTypeOfCompExc(
                  objHash, type, "elems[1]", intHT(), stringHT()));
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
      var expr1 = intH(1);
      var expr2 = stringH("abc");
      Hash objHash =
          hash(
              hash(combineHT(list(intHT(), stringHT()))),
              hash(
                  hash(expr1),
                  hash(expr2)
              ));
      var items = ((CombineH) objDb().get(objHash)).items();
      assertThat(items)
          .containsExactly(expr1, expr2)
          .inOrder();
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(combineHT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var expr1 = intH(1);
      var expr2 = stringH("abc");
      Hash dataHash = hash(
          hash(expr1),
          hash(expr2)
      );
      obj_root_with_two_data_hashes(
          orderHT(),
          dataHash,
          (Hash objHash) -> ((CombineH) objDb().get(objHash)).items()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          combineHT(),
          (Hash objHash) -> ((CombineH) objDb().get(objHash)).items());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_seq_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSeq = hash(ByteString.of(new byte[byteCount]));
      Hash objHash =
          hash(
              hash(combineHT()),
              notHashOfSeq
          );
      assertCall(() -> ((CombineH) objDb().get(objHash)).items())
          .throwsException(new DecodeObjNodeExc(objHash, combineHT(), DATA_PATH))
          .withCause(new DecodeHashSeqExc(
              notHashOfSeq, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_seq_item_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash objHash =
          hash(
              hash(combineHT()),
              hash(
                  nowhere
              )
          );
      assertCall(() -> ((CombineH) objDb().get(objHash)).items())
          .throwsException(new DecodeObjNodeExc(objHash, combineHT(), DATA_PATH + "[0]"))
          .withCause(new NoSuchObjExc(nowhere));
    }

    @Test
    public void evaluation_type_items_size_is_different_than_actual_items_size()
        throws Exception {
      IntH expr1 =  intH();
      CombineTypeH type = combineHT(list(intHT(), stringHT()));
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(expr1)
              ));

      assertCall(() -> ((CombineH) objDb().get(objHash)).items())
          .throwsException(new DecodeCombineWrongItemsSizeExc(objHash, type, 1));
    }

    @Test
    public void evaluation_type_item_is_different_than_evaluation_type_of_one_of_items()
        throws Exception {
      var expr1 = intH(1);
      var expr2 = stringH("abc");
      var type = combineHT(list(intHT(), boolHT()));
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(expr1),
                  hash(expr2)
              ));

      assertCall(() -> ((CombineH) objDb().get(objHash)).items())
          .throwsException(
              new DecodeExprWrongEvalTypeOfCompExc(
                  objHash, type, "items[1]", boolHT(), stringHT()));
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
      var tupleType = tupleHT(list(stringHT()));
      var tuple = tupleH(tupleType, list(stringH("abc")));
      var expr = (ValH) tuple;
      var index = intH(0);
      Hash objHash =
          hash(
              hash(selectHT(stringHT())),
              hash(
                  hash(expr),
                  hash(index)
              )
          );
      assertThat(((SelectH) objDb().get(objHash)).data())
          .isEqualTo(new SelectData(expr, index));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(selectHT(intHT()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var index = intH(2);
      var expr = intH(123);
      Hash dataHash = hash(
          hash(expr),
          hash(index)
      );
      obj_root_with_two_data_hashes(
          selectHT(),
          dataHash,
          (Hash objHash) -> ((SelectH) objDb().get(objHash)).data());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          selectHT(),
          (Hash objHash) -> ((SelectH) objDb().get(objHash)).data());
    }

    @Test
    public void data_is_seq_with_one_elem() throws Exception {
      var expr = intH(123);
      var dataHash = hash(
          hash(expr)
      );
      Hash objHash =
          hash(
              hash(selectHT()),
              dataHash
          );
      assertCall(() -> ((SelectH) objDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSeqExc(
              objHash, selectHT(), DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_seq_with_three_elems() throws Exception {
      var index = intH(2);
      var expr = intH(123);
      var dataHash = hash(
          hash(expr),
          hash(index),
          hash(index)
      );
      Hash objHash =
          hash(
              hash(selectHT()),
              dataHash
          );
      assertCall(() -> ((SelectH) objDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSeqExc(
              objHash, selectHT(), DATA_PATH, 2, 3));
    }

    @Test
    public void tuple_is_not_tuple_expr() throws Exception {
      var expr = intH(3);
      var index = intH(0);
      var type = selectHT(stringHT());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(expr),
                  hash(index)
              )
          );

      assertCall(() -> ((SelectH) objDb().get(objHash)).data())
          .throwsException(new DecodeExprWrongEvalTypeOfCompExc(
              objHash, type, "tuple", TupleTypeH.class, intHT()));
    }

    @Test
    public void index_is_out_of_bounds() throws Exception {
      var tupleType = tupleHT(list(stringHT()));
      var tuple = tupleH(tupleType, list(stringH("abc")));
      var index = intH(1);
      var type = selectHT(stringHT());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(tuple),
                  hash(index)
              )
          );

      assertCall(() -> ((SelectH) objDb().get(objHash)).data())
          .throwsException(new DecodeSelectIndexOutOfBoundsExc(objHash, type, 1, 1));
    }

    @Test
    public void evaluation_type_is_different_than_type_of_item_pointed_to_by_index()
        throws Exception {
      var tupleType = tupleHT(list(stringHT()));
      var tuple = tupleH(tupleType, list(stringH("abc")));
      var index = intH(0);
      var type = selectHT(intHT());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(tuple),
                  hash(index)
              )
          );

      assertCall(() -> ((SelectH) objDb().get(objHash)).data())
          .throwsException(new DecodeSelectWrongEvalTypeExc(objHash, type, stringHT()));
    }

    @Test
    public void index_is_string_instead_of_int() throws Exception {
      var type = selectHT(stringHT());
      var tupleType = tupleHT(list(stringHT()));
      var tuple = tupleH(tupleType, list(stringH("abc")));
      var strVal = stringH("abc");
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(tuple),
                  hash(strVal)
              )
          );
      assertCall(() -> ((SelectH) objDb().get(objHash)).data())
          .throwsException(new UnexpectedObjNodeExc(
              objHash, type, DATA_PATH + "[1]", IntH.class, StringH.class));
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
      Hash objHash =
          hash(
              hash(intHT()),
              hash(byteString));
      assertThat(((IntH) objDb().get(objHash)).toJ())
          .isEqualTo(BigInteger.valueOf(3 * 256 + 2));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(intHT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          intHT(),
          hashedDb().writeByte((byte) 1),
          (Hash objHash) -> ((IntH) objDb().get(objHash)).toJ()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          intHT(),
          (Hash objHash) -> ((IntH) objDb().get(objHash)).toJ());
    }
  }

  @Nested
  class _nat_func {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save
       * nat_func in HashedDb.
       */
      BlobH jarFile = blobH();
      StringH classBinaryName = stringH();
      BoolH isPure = boolH(true);
      Hash objHash =
          hash(
              hash(natFuncHT()),
              hash(
                  hash(jarFile),
                  hash(classBinaryName),
                  hash(isPure)
              )
          );

      assertThat(((NatFuncH) objDb().get(objHash)).jarFile())
          .isEqualTo(jarFile);
      assertThat(((NatFuncH) objDb().get(objHash)).classBinaryName())
          .isEqualTo(classBinaryName);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(natFuncHT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      BlobH jarFile = blobH();
      StringH classBinaryName = stringH();
      BoolH isPure = boolH(true);
      Hash dataHash = hash(
          hash(jarFile),
          hash(classBinaryName),
          hash(isPure)
      );
      obj_root_with_two_data_hashes(
          natFuncHT(),
          dataHash,
          (Hash objHash) -> ((NatFuncH) objDb().get(objHash)).classBinaryName());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          natFuncHT(),
          (Hash objHash) -> ((NatFuncH) objDb().get(objHash)).classBinaryName());
    }

    @Test
    public void data_is_seq_with_two_elem() throws Exception {
      BlobH jarFile = blobH();
      StringH classBinaryName = stringH();
      Hash dataHash = hash(
          hash(jarFile),
          hash(classBinaryName)
      );
      Hash objHash =
          hash(
              hash(natFuncHT()),
              dataHash
          );

      assertCall(() -> ((NatFuncH) objDb().get(objHash)).classBinaryName())
          .throwsException(new UnexpectedObjSeqExc(
              objHash, natFuncHT(), DATA_PATH, 3, 2));
    }

    @Test
    public void data_is_seq_with_four_elems() throws Exception {
      BlobH jarFile = blobH();
      StringH classBinaryName = stringH();
      BoolH isPure = boolH(true);
      Hash dataHash = hash(
          hash(jarFile),
          hash(classBinaryName),
          hash(isPure),
          hash(isPure)
      );
      Hash objHash =
          hash(
              hash(natFuncHT()),
              dataHash
          );

      assertCall(() -> ((NatFuncH) objDb().get(objHash)).classBinaryName())
          .throwsException(new UnexpectedObjSeqExc(
              objHash, natFuncHT(), DATA_PATH, 3, 4));
    }

    @Test
    public void jar_file_is_not_blob_value() throws Exception {
      StringH jarFile = stringH();
      StringH classBinaryName = stringH();
      BoolH isPure = boolH(true);
      Hash objHash =
          hash(
              hash(natFuncHT()),
              hash(
                  hash(jarFile),
                  hash(classBinaryName),
                  hash(isPure)
              )
          );
      assertCall(() -> ((NatFuncH) objDb().get(objHash)).jarFile())
          .throwsException(new UnexpectedObjNodeExc(
              objHash, natFuncHT(), DATA_PATH + "[0]", BlobH.class, StringH.class));
    }

    @Test
    public void class_binary_name_is_not_string_value() throws Exception {
      BlobH jarFile = blobH();
      IntH classBinaryName = intH();
      BoolH isPure = boolH(true);
      Hash objHash =
          hash(
              hash(natFuncHT()),
              hash(
                  hash(jarFile),
                  hash(classBinaryName),
                  hash(isPure)
              )
          );

      assertCall(() -> ((NatFuncH) objDb().get(objHash)).classBinaryName())
          .throwsException(new UnexpectedObjNodeExc(
              objHash, natFuncHT(), DATA_PATH + "[1]", StringH.class, IntH.class));
    }

    @Test
    public void is_pure_is_not_bool_value() throws Exception {
      BlobH jarFile = blobH();
      StringH classBinaryName = stringH();
      StringH isPure = stringH();
      Hash objHash =
          hash(
              hash(natFuncHT()),
              hash(
                  hash(jarFile),
                  hash(classBinaryName),
                  hash(isPure)
              )
          );

      assertCall(() -> ((NatFuncH) objDb().get(objHash)).isPure())
          .throwsException(new UnexpectedObjNodeExc(
              objHash, natFuncHT(), DATA_PATH + "[2]", BoolH.class, StringH.class));
    }
  }

  @Nested
  class _nothing {
    @Test
    public void learning_test() throws Exception {
      Hash objHash =
          hash(
              hash(nothingHT()),
              hash("aaa"));
      assertCall(() -> objDb().get(objHash))
          .throwsException(UnsupportedOperationException.class);
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
      Hash objHash =
          hash(
              hash(stringHT()),
              hash("aaa"));
      assertThat(((StringH) objDb().get(objHash)).toJ())
          .isEqualTo("aaa");
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(stringHT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          stringHT(),
          hashedDb().writeBoolean(true),
          (Hash objHash) -> ((StringH) objDb().get(objHash)).toJ()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          stringHT(),
          (Hash objHash) -> ((StringH) objDb().get(objHash)).toJ());
    }

    @Test
    public void data_being_invalid_utf8_seq() throws Exception {
      Hash notStringHash = hash(illegalString());
      Hash objHash =
          hash(
              hash(stringHT()),
              notStringHash);
      assertCall(() -> ((StringH) objDb().get(objHash)).toJ())
          .throwsException(new DecodeObjNodeExc(objHash, stringHT(), DATA_PATH))
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
              hash(personHT()),
              hash(
                  hash(stringH("John")),
                  hash(stringH("Doe")))))
          .isEqualTo(personH("John", "Doe").hash());
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(personHT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          personHT(),
          hashedDb().writeBoolean(true),
          (Hash objHash) -> ((TupleH) objDb().get(objHash)).get(0)
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          personHT(),
          (Hash objHash) -> ((TupleH) objDb().get(objHash)).get(0));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_seq_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSeq = hash(ByteString.of(new byte[byteCount]));
      Hash objHash =
          hash(
              hash(personHT()),
              notHashOfSeq);
      assertCall(() -> ((TupleH) objDb().get(objHash)).get(0))
          .throwsException(new DecodeObjNodeExc(objHash, personHT(), DATA_PATH))
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
      Hash objHash =
          hash(
              hash(personHT()),
              dataHash
          );
      assertCall(() -> ((TupleH) objDb().get(objHash)).get(0))
          .throwsException(new DecodeObjNodeExc(objHash, personHT(), DATA_PATH + "[0]"))
          .withCause(new NoSuchObjExc(nowhere));
    }

    @Test
    public void with_too_few_elems() throws Exception {
      Hash dataHash =
          hash(
              hash(stringH("John")));
      Hash objHash =
          hash(
              hash(personHT()),
              dataHash);
      TupleH tuple = (TupleH) objDb().get(objHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new UnexpectedObjSeqExc(objHash, personHT(), DATA_PATH, 2, 1));
    }

    @Test
    public void with_too_many_elems() throws Exception {
      Hash dataHash =
          hash(
              hash(stringH("John")),
              hash(stringH("Doe")),
              hash(stringH("junk")));
      Hash objHash =
          hash(
              hash(personHT()),
              dataHash);
      TupleH tuple = (TupleH) objDb().get(objHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new UnexpectedObjSeqExc(objHash, personHT(), DATA_PATH, 2, 3));
    }

    @Test
    public void with_elem_of_wrong_type() throws Exception {
      Hash objHash =
          hash(
              hash(personHT()),
              hash(
                  hash(stringH("John")),
                  hash(boolH(true))));
      TupleH tuple = (TupleH) objDb().get(objHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new UnexpectedObjNodeExc(
              objHash, personHT(), DATA_PATH, 1, stringHT(), boolHT()));
    }

    @Test
    public void with_elem_being_expr() throws Exception {
      Hash objHash =
          hash(
              hash(personHT()),
              hash(
                  hash(stringH("John")),
                  hash(paramRefH(1))));
      TupleH tuple = (TupleH) objDb().get(objHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new UnexpectedObjNodeExc(
              objHash, personHT(), DATA_PATH + "[1]", ValH.class, ParamRefH.class));
    }
  }

  @Nested
  class _ref {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save ref
       * in HashedDb.
       */
      ByteString byteString = ByteString.of((byte) 3, (byte) 2);
      Hash objHash =
          hash(
              hash(refHT(stringHT())),
              hash(byteString));
      assertThat(((ParamRefH) objDb().get(objHash)).value())
          .isEqualTo(BigInteger.valueOf(3 * 256 + 2));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(refHT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          refHT(),
          hashedDb().writeByte((byte) 1),
          (Hash objHash) -> ((ParamRefH) objDb().get(objHash)).value()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          refHT(),
          (Hash objHash) -> ((ParamRefH) objDb().get(objHash)).value());
    }
  }

  private void obj_root_without_data_hash(SpecH type) throws HashedDbExc {
    Hash objHash =
        hash(
            hash(type));
    assertCall(() -> objDb().get(objHash))
        .throwsException(wrongSizeOfRootSeqException(objHash, 1));
  }

  private void obj_root_with_two_data_hashes(
      SpecH type, Hash dataHash, Function<Hash, ?> readClosure) throws HashedDbExc {
    Hash objHash =
        hash(
            hash(type),
            dataHash,
            dataHash);
    assertCall(() -> readClosure.apply(objHash))
        .throwsException(wrongSizeOfRootSeqException(objHash, 3));
  }

  private void obj_root_with_data_hash_not_pointing_to_obj_but_nowhere(
      SpecH type, Function<Hash, ?> readClosure) throws HashedDbExc {
    Hash dataHash = Hash.of(33);
    Hash objHash =
        hash(
            hash(type),
            dataHash);
    assertCall(() -> readClosure.apply(objHash))
        .throwsException(new DecodeObjNodeExc(objHash, type, DATA_PATH))
        .withCause(new NoSuchObjExc(dataHash));
  }

  private void obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
      SpecH type, Consumer<Hash> readClosure) throws HashedDbExc {
    Hash dataHash = Hash.of(33);
    Hash objHash =
        hash(
            hash(type),
            dataHash);
    assertCall(() -> readClosure.accept(objHash))
        .throwsException(new DecodeObjNodeExc(objHash, type, DATA_PATH))
        .withCause(new NoSuchDataExc(dataHash));
  }

  @Nested
  class _var {
    @Test
    public void learning_test() throws Exception {
      Hash objHash =
          hash(
              hash(varHT("A")),
              hash("aaa"));
      assertCall(() -> objDb().get(objHash))
          .throwsException(UnsupportedOperationException.class);
    }
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

  protected Hash hash(ObjH obj) {
    return obj.hash();
  }

  protected Hash hash(SpecH type) {
    return type.hash();
  }

  protected Hash hash(Hash... hashes) throws HashedDbExc {
    return hashedDb().writeSeq(hashes);
  }
}
