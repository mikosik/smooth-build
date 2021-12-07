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
import org.smoothbuild.db.object.obj.exc.DecodeObjCatExc;
import org.smoothbuild.db.object.obj.exc.DecodeObjNodeExc;
import org.smoothbuild.db.object.obj.exc.DecodeSelectIndexOutOfBoundsExc;
import org.smoothbuild.db.object.obj.exc.DecodeSelectWrongEvalTypeExc;
import org.smoothbuild.db.object.obj.exc.NoSuchObjExc;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjNodeExc;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjSeqExc;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.obj.expr.CombineH;
import org.smoothbuild.db.object.obj.expr.InvokeH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.ParamRefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.expr.SelectH.SelectData;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.FuncH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.db.object.type.base.CatH;
import org.smoothbuild.db.object.type.exc.DecodeCatExc;
import org.smoothbuild.db.object.type.expr.CallCH;
import org.smoothbuild.db.object.type.expr.CombineCH;
import org.smoothbuild.db.object.type.val.ArrayTH;
import org.smoothbuild.db.object.type.val.FuncTH;
import org.smoothbuild.db.object.type.val.TupleTH;
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
              hash(stringTH()),
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
          .throwsException(new DecodeObjCatExc(objHash))
          .withCause(new DecodeCatExc(typeHash));
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
              hash(anyTH()),
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
              hash(arrayTH(stringTH())),
              hash(
                  hash(
                      hash(stringTH()),
                      hash("aaa")
                  ),
                  hash(
                      hash(stringTH()),
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
      obj_root_without_data_hash(arrayTH(intTH()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          arrayTH(intTH()),
          hashedDb().writeSeq(),
          (Hash objHash) -> ((ArrayH) objDb().get(objHash)).elems(IntH.class)
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          arrayTH(intTH()),
          (Hash objHash) -> ((ArrayH) objDb().get(objHash)).elems(IntH.class));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_seq_size_different_than_multiple_of_hash_size(int byteCount) throws Exception {
      Hash notHashOfSeq = hash(ByteString.of(new byte[byteCount]));
      ArrayTH type = arrayTH(stringTH());
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
      ArrayTH type = arrayTH(stringTH());
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
      ArrayTH type = arrayTH(stringTH());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(
                      hash(stringTH()),
                      hash("aaa")
                  ),
                  hash(
                      hash(boolTH()),
                      hash(true)
                  )
              ));
      assertCall(() -> ((ArrayH) objDb().get(objHash)).elems(StringH.class))
          .throwsException(new UnexpectedObjNodeExc(
              objHash, type, DATA_PATH, 1, stringTH(), boolTH()));
    }

    @Test
    public void with_one_elem_being_expr() throws Exception {
      ArrayTH type = arrayTH(stringTH());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(
                      hash(stringTH()),
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
              hash(blobTH()),
              hash(byteString));
      assertThat(((BlobH) objDb().get(objHash)).source().readByteString())
          .isEqualTo(byteString);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(blobTH());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          blobTH(),
          hashedDb().writeByte((byte) 1),
          (Hash objHash) -> ((BlobH) objDb().get(objHash)).source()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          blobTH(),
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
              hash(boolTH()),
              hash(value));
      assertThat(((BoolH) objDb().get(objHash)).toJ())
          .isEqualTo(value);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(boolTH());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          boolTH(),
          hashedDb().writeBoolean(true),
          (Hash objHash) -> ((BoolH) objDb().get(objHash)).toJ()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          boolTH(),
          (Hash objHash) -> ((BoolH) objDb().get(objHash)).toJ());
    }

    @Test
    public void empty_bytes_as_data() throws Exception {
      Hash dataHash = hash(ByteString.of());
      Hash objHash =
          hash(
              hash(boolTH()),
              dataHash);
      assertCall(() -> ((BoolH) objDb().get(objHash)).toJ())
          .throwsException(new DecodeObjNodeExc(objHash, boolTH(), DATA_PATH))
          .withCause(new DecodeBooleanExc(dataHash, new DecodeByteExc(dataHash)));
    }

    @Test
    public void more_than_one_byte_as_data() throws Exception {
      Hash dataHash = hash(ByteString.of((byte) 0, (byte) 0));
      Hash objHash =
          hash(
              hash(boolTH()),
              dataHash);
      assertCall(() -> ((BoolH) objDb().get(objHash)).toJ())
          .throwsException(new DecodeObjNodeExc(objHash, boolTH(), DATA_PATH))
          .withCause(new DecodeBooleanExc(dataHash, new DecodeByteExc(dataHash)));
    }

    @ParameterizedTest
    @ArgumentsSource(AllByteValuesExceptZeroAndOneProvider.class)
    public void one_byte_data_not_equal_zero_nor_one(byte value)
        throws Exception {
      Hash dataHash = hash(ByteString.of(value));
      Hash objHash =
          hash(
              hash(boolTH()),
              dataHash);
      assertCall(() -> ((BoolH) objDb().get(objHash)).toJ())
          .throwsException(new DecodeObjNodeExc(objHash, boolTH(), DATA_PATH))
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
      var funcT = funcTH(intTH(), list(stringTH(), intTH()));
      var func = funcH(funcT, intH());
      CombineH args = combineH(list(stringH(), intH()));
      Hash objHash =
          hash(
              hash(callCH()),
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
      obj_root_without_data_hash(callCH());
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
          callCH(),
          dataHash,
          (Hash objHash) -> ((CallH) objDb().get(objHash)).data());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          callCH(),
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
              hash(callCH()),
              dataHash
          );
      assertCall(() -> ((CallH) objDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSeqExc(objHash, callCH(), DATA_PATH, 2, 1));
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
              hash(callCH()),
              dataHash
          );
      assertCall(() -> ((CallH) objDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSeqExc(objHash, callCH(), DATA_PATH, 2, 3));
    }

    @Test
    public void func_component_evaluation_type_is_not_func() throws Exception {
      var func = intH(3);
      CombineH args = combineH(list(stringH(), intH()));
      CallCH type = callCH(stringTH());
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
              objHash, type, "func", FuncTH.class, intTH()));
    }

    @Test
    public void args_is_val_instead_of_expr() throws Exception {
      var funcT = funcTH(intTH(), list(stringTH(), intTH()));
      var func = funcH(funcT, intH());
      Hash objHash =
          hash(
              hash(callCH()),
              hash(
                  hash(func),
                  hash(intH())
              )
          );
      assertCall(() -> ((CallH) objDb().get(objHash)).data())
          .throwsException(new UnexpectedObjNodeExc(
              objHash, callCH(), DATA_PATH + "[1]", CombineH.class, IntH.class));
    }

    @Test
    public void args_component_evaluation_type_is_not_combine_but_different_expr()
        throws Exception {
      var funcT = funcTH(intTH(), list(stringTH(), intTH()));
      var func = funcH(funcT, intH());
      var type = callCH();
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
      var funcT = funcTH(intTH(), list(stringTH()));
      var func = funcH(funcT, intH());
      var args = combineH(list(stringH()));
      var type = callCH(stringTH());
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
                  objHash, type, "func.result", stringTH(), intTH()));
    }

    @Test
    public void func_evaluation_type_params_does_not_match_args_evaluation_types()
        throws Exception {
      var funcT = funcTH(intTH(), list(stringTH(), boolTH()));
      var func = funcH(funcT, intH());
      var args = combineH(list(stringH(), intH()));
      var spec = callCH(intTH());
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
              tupleTH(list(stringTH(), boolTH())),
              tupleTH(list(stringTH(), intTH()))
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
      FuncTH type = funcTH(boolTH(), list(intTH(), stringTH()));
      Hash objHash =
          hash(
              hash(type),
              hash(bodyExpr)
          );
      assertThat(((FuncH) objDb().get(objHash)).body())
          .isEqualTo(bodyExpr);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(funcTH());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var bodyExpr = boolH(true);
      var type = funcTH(boolTH(), list(intTH(), stringTH()));
      var dataHash = hash(bodyExpr);
      obj_root_with_two_data_hashes(
          type,
          dataHash,
          (Hash objHash) -> ((FuncH) objDb().get(objHash)).body());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_obj_but_nowhere(funcTH(),
          (Hash objHash) -> ((FuncH) objDb().get(objHash)).body());
    }

    @Test
    public void body_evaluation_type_is_not_equal_func_type_result() throws Exception {
      var bodyExpr = intH(3);
      var type = funcTH(boolTH(), list(intTH(), stringTH()));
      Hash objHash =
          hash(
              hash(type),
              hash(bodyExpr)
          );
      assertCall(() -> ((FuncH) objDb().get(objHash)).body())
          .throwsException(new DecodeExprWrongEvalTypeOfCompExc(
              objHash, type, DATA_PATH, boolTH(), intTH()));
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
              hash(orderCH()),
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
      obj_root_without_data_hash(orderCH());
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
          orderCH(),
          dataHash,
          (Hash objHash) -> ((OrderH) objDb().get(objHash)).elems()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          orderCH(),
          (Hash objHash) -> ((OrderH) objDb().get(objHash)).elems());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_seq_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSeq = hash(ByteString.of(new byte[byteCount]));
      Hash objHash =
          hash(
              hash(orderCH()),
              notHashOfSeq
          );
      assertCall(() -> ((OrderH) objDb().get(objHash)).elems())
          .throwsException(new DecodeObjNodeExc(objHash, orderCH(), DATA_PATH))
          .withCause(new DecodeHashSeqExc(
              notHashOfSeq, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_seq_elem_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash objHash =
          hash(
              hash(orderCH()),
              hash(
                  nowhere
              )
          );
      assertCall(() -> ((OrderH) objDb().get(objHash)).elems())
          .throwsException(new DecodeObjNodeExc(objHash, orderCH(), DATA_PATH + "[0]"))
          .withCause(new NoSuchObjExc(nowhere));
    }

    @Test
    public void evaluation_type_elem_is_different_than_evaluation_type_of_one_of_elems()
        throws Exception {
      var expr1 = intH();
      var expr2 = stringH();
      var type = orderCH(intTH());
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
                  objHash, type, "elems[1]", intTH(), stringTH()));
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
              hash(combineCH(list(intTH(), stringTH()))),
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
      obj_root_without_data_hash(combineCH());
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
          orderCH(),
          dataHash,
          (Hash objHash) -> ((CombineH) objDb().get(objHash)).items()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          combineCH(),
          (Hash objHash) -> ((CombineH) objDb().get(objHash)).items());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_seq_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSeq = hash(ByteString.of(new byte[byteCount]));
      Hash objHash =
          hash(
              hash(combineCH()),
              notHashOfSeq
          );
      assertCall(() -> ((CombineH) objDb().get(objHash)).items())
          .throwsException(new DecodeObjNodeExc(objHash, combineCH(), DATA_PATH))
          .withCause(new DecodeHashSeqExc(
              notHashOfSeq, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_seq_item_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash objHash =
          hash(
              hash(combineCH()),
              hash(
                  nowhere
              )
          );
      assertCall(() -> ((CombineH) objDb().get(objHash)).items())
          .throwsException(new DecodeObjNodeExc(objHash, combineCH(), DATA_PATH + "[0]"))
          .withCause(new NoSuchObjExc(nowhere));
    }

    @Test
    public void evaluation_type_items_size_is_different_than_actual_items_size()
        throws Exception {
      IntH expr1 =  intH();
      CombineCH type = combineCH(list(intTH(), stringTH()));
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
      var type = combineCH(list(intTH(), boolTH()));
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
                  objHash, type, "items[1]", boolTH(), stringTH()));
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
      var tupleT = tupleTH(list(stringTH()));
      var tuple = tupleH(tupleT, list(stringH("abc")));
      var selectable = (ValH) tuple;
      var index = intH(0);
      Hash objHash =
          hash(
              hash(selectCH(stringTH())),
              hash(
                  hash(selectable),
                  hash(index)
              )
          );
      assertThat(((SelectH) objDb().get(objHash)).data())
          .isEqualTo(new SelectData(selectable, index));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(selectCH(intTH()));
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
          selectCH(),
          dataHash,
          (Hash objHash) -> ((SelectH) objDb().get(objHash)).data());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          selectCH(),
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
              hash(selectCH()),
              dataHash
          );
      assertCall(() -> ((SelectH) objDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSeqExc(
              objHash, selectCH(), DATA_PATH, 2, 1));
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
              hash(selectCH()),
              dataHash
          );
      assertCall(() -> ((SelectH) objDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSeqExc(
              objHash, selectCH(), DATA_PATH, 2, 3));
    }

    @Test
    public void tuple_is_not_tuple_expr() throws Exception {
      var expr = intH(3);
      var index = intH(0);
      var type = selectCH(stringTH());
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
              objHash, type, "tuple", TupleTH.class, intTH()));
    }

    @Test
    public void index_is_out_of_bounds() throws Exception {
      var tupleT = tupleTH(list(stringTH()));
      var tuple = tupleH(tupleT, list(stringH("abc")));
      var index = intH(1);
      var type = selectCH(stringTH());
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
      var tupleT = tupleTH(list(stringTH()));
      var tuple = tupleH(tupleT, list(stringH("abc")));
      var index = intH(0);
      var type = selectCH(intTH());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(tuple),
                  hash(index)
              )
          );

      assertCall(() -> ((SelectH) objDb().get(objHash)).data())
          .throwsException(new DecodeSelectWrongEvalTypeExc(objHash, type, stringTH()));
    }

    @Test
    public void index_is_string_instead_of_int() throws Exception {
      var type = selectCH(stringTH());
      var tupleT = tupleTH(list(stringTH()));
      var tuple = tupleH(tupleT, list(stringH("abc")));
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
              hash(intTH()),
              hash(byteString));
      assertThat(((IntH) objDb().get(objHash)).toJ())
          .isEqualTo(BigInteger.valueOf(3 * 256 + 2));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(intTH());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          intTH(),
          hashedDb().writeByte((byte) 1),
          (Hash objHash) -> ((IntH) objDb().get(objHash)).toJ()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          intTH(),
          (Hash objHash) -> ((IntH) objDb().get(objHash)).toJ());
    }
  }

  @Nested
  class _invoke {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save
       * nat_func in HashedDb.
       */
      var type = invokeCH(stringTH(), list(intTH()));
      var jar = blobH();
      var classBinaryName = stringH();
      var isPure = boolH(true);
      var args = combineH(list(intH(1)));
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(jar),
                  hash(classBinaryName),
                  hash(isPure),
                  hash(args)
              )
          );

      assertThat(((InvokeH) objDb().get(objHash)).jarFile())
          .isEqualTo(jar);
      assertThat(((InvokeH) objDb().get(objHash)).classBinaryName())
          .isEqualTo(classBinaryName);
      assertThat(((InvokeH) objDb().get(objHash)).isPure())
          .isEqualTo(isPure);
      assertThat(((InvokeH) objDb().get(objHash)).args())
          .isEqualTo(args);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(invokeCH());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var type = invokeCH(stringTH(), list(intTH()));
      var jar = blobH();
      var classBinaryName = stringH();
      var isPure = boolH(true);
      var args = combineH(list(intH(1)));
      Hash dataHash = hash(
          hash(jar),
          hash(classBinaryName),
          hash(isPure),
          hash(args)
      );
      obj_root_with_two_data_hashes(type, dataHash,
          (Hash objHash) -> ((InvokeH) objDb().get(objHash)).classBinaryName());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      var type = invokeCH(stringTH(), list(intTH()));
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(type,
          (Hash objHash) -> ((InvokeH) objDb().get(objHash)).classBinaryName());
    }

    @Test
    public void data_is_seq_with_three_elem() throws Exception {
      var type = invokeCH(stringTH(), list(intTH()));
      var jarFile = blobH();
      var classBinaryName = stringH();
      var isPure = boolH(true);
      Hash dataHash = hash(
          hash(jarFile),
          hash(classBinaryName),
          hash(isPure)
      );
      Hash objHash =
          hash(
              hash(type),
              dataHash
          );

      assertCall(() -> ((InvokeH) objDb().get(objHash)).classBinaryName())
          .throwsException(new UnexpectedObjSeqExc(
              objHash, type, DATA_PATH, 4, 3));
    }

    @Test
    public void data_is_seq_with_five_elems() throws Exception {
      var type = invokeCH(stringTH(), list(intTH()));
      var jarFile = blobH();
      var classBinaryName = stringH();
      var isPure = boolH(true);
      var args = combineH(list(intH(1)));
      Hash dataHash = hash(
          hash(jarFile),
          hash(classBinaryName),
          hash(isPure),
          hash(args),
          hash(args)
      );
      Hash objHash =
          hash(
              hash(type),
              dataHash
          );

      assertCall(() -> ((InvokeH) objDb().get(objHash)).classBinaryName())
          .throwsException(new UnexpectedObjSeqExc(
              objHash, type, DATA_PATH, 4, 5));
    }

    @Test
    public void jar_file_is_not_blob_value() throws Exception {
      var type = invokeCH(stringTH(), list(intTH()));
      var jarFile = stringH();
      var classBinaryName = stringH();
      var isPure = boolH(true);
      var args = combineH(list(intH(1)));
      var objHash =
          hash(
              hash(type),
              hash(
                  hash(jarFile),
                  hash(classBinaryName),
                  hash(isPure),
                  hash(args)
              )
          );
      assertCall(() -> ((InvokeH) objDb().get(objHash)).jarFile())
          .throwsException(new UnexpectedObjNodeExc(
              objHash, type, DATA_PATH + "[0]", BlobH.class, StringH.class));
    }

    @Test
    public void class_binary_name_is_not_string_value() throws Exception {
      var type = invokeCH(stringTH(), list(intTH()));
      var jarFile = blobH();
      var classBinaryName = intH();
      var isPure = boolH(true);
      var args = combineH(list(intH(1)));
      var objHash =
          hash(
              hash(type),
              hash(
                  hash(jarFile),
                  hash(classBinaryName),
                  hash(isPure),
                  hash(args)
              )
          );

      assertCall(() -> ((InvokeH) objDb().get(objHash)).classBinaryName())
          .throwsException(new UnexpectedObjNodeExc(
              objHash, type, DATA_PATH + "[1]", StringH.class, IntH.class));
    }

    @Test
    public void is_pure_is_not_bool_value() throws Exception {
      var type = invokeCH(stringTH(), list(intTH()));
      var jarFile = blobH();
      var classBinaryName = stringH();
      var isPure = stringH();
      var args = combineH(list(intH(1)));
      var objHash =
          hash(
              hash(type),
              hash(
                  hash(jarFile),
                  hash(classBinaryName),
                  hash(isPure),
                  hash(args)
              )
          );

      assertCall(() -> ((InvokeH) objDb().get(objHash)).isPure())
          .throwsException(new UnexpectedObjNodeExc(
              objHash, type, DATA_PATH + "[2]", BoolH.class, StringH.class));
    }
  }

  @Nested
  class _nothing {
    @Test
    public void learning_test() throws Exception {
      Hash objHash =
          hash(
              hash(nothingTH()),
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
              hash(stringTH()),
              hash("aaa"));
      assertThat(((StringH) objDb().get(objHash)).toJ())
          .isEqualTo("aaa");
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(stringTH());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          stringTH(),
          hashedDb().writeBoolean(true),
          (Hash objHash) -> ((StringH) objDb().get(objHash)).toJ()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          stringTH(),
          (Hash objHash) -> ((StringH) objDb().get(objHash)).toJ());
    }

    @Test
    public void data_being_invalid_utf8_seq() throws Exception {
      Hash notStringHash = hash(illegalString());
      Hash objHash =
          hash(
              hash(stringTH()),
              notStringHash);
      assertCall(() -> ((StringH) objDb().get(objHash)).toJ())
          .throwsException(new DecodeObjNodeExc(objHash, stringTH(), DATA_PATH))
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
              hash(personTH()),
              hash(
                  hash(stringH("John")),
                  hash(stringH("Doe")))))
          .isEqualTo(personH("John", "Doe").hash());
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(personTH());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          personTH(),
          hashedDb().writeBoolean(true),
          (Hash objHash) -> ((TupleH) objDb().get(objHash)).get(0)
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          personTH(),
          (Hash objHash) -> ((TupleH) objDb().get(objHash)).get(0));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_seq_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSeq = hash(ByteString.of(new byte[byteCount]));
      Hash objHash =
          hash(
              hash(personTH()),
              notHashOfSeq);
      assertCall(() -> ((TupleH) objDb().get(objHash)).get(0))
          .throwsException(new DecodeObjNodeExc(objHash, personTH(), DATA_PATH))
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
              hash(personTH()),
              dataHash
          );
      assertCall(() -> ((TupleH) objDb().get(objHash)).get(0))
          .throwsException(new DecodeObjNodeExc(objHash, personTH(), DATA_PATH + "[0]"))
          .withCause(new NoSuchObjExc(nowhere));
    }

    @Test
    public void with_too_few_elems() throws Exception {
      Hash dataHash =
          hash(
              hash(stringH("John")));
      Hash objHash =
          hash(
              hash(personTH()),
              dataHash);
      TupleH tuple = (TupleH) objDb().get(objHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new UnexpectedObjSeqExc(objHash, personTH(), DATA_PATH, 2, 1));
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
              hash(personTH()),
              dataHash);
      TupleH tuple = (TupleH) objDb().get(objHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new UnexpectedObjSeqExc(objHash, personTH(), DATA_PATH, 2, 3));
    }

    @Test
    public void with_elem_of_wrong_type() throws Exception {
      Hash objHash =
          hash(
              hash(personTH()),
              hash(
                  hash(stringH("John")),
                  hash(boolH(true))));
      TupleH tuple = (TupleH) objDb().get(objHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new UnexpectedObjNodeExc(
              objHash, personTH(), DATA_PATH, 1, stringTH(), boolTH()));
    }

    @Test
    public void with_elem_being_expr() throws Exception {
      Hash objHash =
          hash(
              hash(personTH()),
              hash(
                  hash(stringH("John")),
                  hash(paramRefH(1))));
      TupleH tuple = (TupleH) objDb().get(objHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new UnexpectedObjNodeExc(
              objHash, personTH(), DATA_PATH + "[1]", ValH.class, ParamRefH.class));
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
              hash(paramRefCH(stringTH())),
              hash(byteString));
      assertThat(((ParamRefH) objDb().get(objHash)).value())
          .isEqualTo(BigInteger.valueOf(3 * 256 + 2));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(paramRefCH());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          paramRefCH(),
          hashedDb().writeByte((byte) 1),
          (Hash objHash) -> ((ParamRefH) objDb().get(objHash)).value()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          paramRefCH(),
          (Hash objHash) -> ((ParamRefH) objDb().get(objHash)).value());
    }
  }

  private void obj_root_without_data_hash(CatH type) throws HashedDbExc {
    Hash objHash =
        hash(
            hash(type));
    assertCall(() -> objDb().get(objHash))
        .throwsException(wrongSizeOfRootSeqException(objHash, 1));
  }

  private void obj_root_with_two_data_hashes(
      CatH type, Hash dataHash, Function<Hash, ?> readClosure) throws HashedDbExc {
    Hash objHash =
        hash(
            hash(type),
            dataHash,
            dataHash);
    assertCall(() -> readClosure.apply(objHash))
        .throwsException(wrongSizeOfRootSeqException(objHash, 3));
  }

  private void obj_root_with_data_hash_not_pointing_to_obj_but_nowhere(
      CatH type, Function<Hash, ?> readClosure) throws HashedDbExc {
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
      CatH type, Consumer<Hash> readClosure) throws HashedDbExc {
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
              hash(varTH("A")),
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

  protected Hash hash(CatH type) {
    return type.hash();
  }

  protected Hash hash(Hash... hashes) throws HashedDbExc {
    return hashedDb().writeSeq(hashes);
  }
}
