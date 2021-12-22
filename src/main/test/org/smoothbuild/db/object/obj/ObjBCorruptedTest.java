package org.smoothbuild.db.object.obj;

import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.db.object.obj.base.ObjB.DATA_PATH;
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
import org.smoothbuild.db.object.obj.base.ObjB;
import org.smoothbuild.db.object.obj.exc.DecodeCombineWrongItemsSizeExc;
import org.smoothbuild.db.object.obj.exc.DecodeMapIllegalMappingFuncExc;
import org.smoothbuild.db.object.obj.exc.DecodeObjCatExc;
import org.smoothbuild.db.object.obj.exc.DecodeObjNoSuchObjExc;
import org.smoothbuild.db.object.obj.exc.DecodeObjNodeExc;
import org.smoothbuild.db.object.obj.exc.DecodeObjWrongNodeCatExc;
import org.smoothbuild.db.object.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.db.object.obj.exc.DecodeObjWrongSeqSizeExc;
import org.smoothbuild.db.object.obj.exc.DecodeSelectIndexOutOfBoundsExc;
import org.smoothbuild.db.object.obj.exc.DecodeSelectWrongEvalTypeExc;
import org.smoothbuild.db.object.obj.expr.CallB;
import org.smoothbuild.db.object.obj.expr.CombineB;
import org.smoothbuild.db.object.obj.expr.IfB;
import org.smoothbuild.db.object.obj.expr.InvokeB;
import org.smoothbuild.db.object.obj.expr.MapB;
import org.smoothbuild.db.object.obj.expr.OrderB;
import org.smoothbuild.db.object.obj.expr.ParamRefB;
import org.smoothbuild.db.object.obj.expr.SelectB;
import org.smoothbuild.db.object.obj.val.ArrayB;
import org.smoothbuild.db.object.obj.val.BlobB;
import org.smoothbuild.db.object.obj.val.BoolB;
import org.smoothbuild.db.object.obj.val.FuncB;
import org.smoothbuild.db.object.obj.val.IntB;
import org.smoothbuild.db.object.obj.val.MethodB;
import org.smoothbuild.db.object.obj.val.StringB;
import org.smoothbuild.db.object.obj.val.TupleB;
import org.smoothbuild.db.object.obj.val.ValB;
import org.smoothbuild.db.object.type.base.CatB;
import org.smoothbuild.db.object.type.exc.DecodeCatExc;
import org.smoothbuild.db.object.type.val.ArrayTB;
import org.smoothbuild.db.object.type.val.FuncTB;
import org.smoothbuild.db.object.type.val.IntTB;
import org.smoothbuild.db.object.type.val.TupleTB;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ObjBCorruptedTest extends TestingContext {
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
              hash(stringTB()),
              hash("aaa"));
      assertThat(((StringB) byteDb().get(objHash)).toJ())
          .isEqualTo("aaa");
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void merkle_root_byte_count_is_not_multiple_of_hash_size(
        int byteCount) throws IOException, HashedDbExc {
      Hash objHash =
          hash(ByteString.of(new byte[byteCount]));
      assertCall(() -> byteDb().get(objHash))
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
      assertCall(() -> byteDb().get(objHash))
          .throwsException(new DecodeObjCatExc(objHash))
          .withCause(new DecodeCatExc(typeHash));
    }

    @Test
    public void reading_elems_from_not_stored_object_throws_exception() {
      Hash objHash = Hash.of(33);
      assertCall(() -> byteDb().get(objHash))
          .throwsException(new DecodeObjNoSuchObjExc(objHash))
          .withCause(new NoSuchDataExc(objHash));
    }
  }

  @Nested
  class _any {
    @Test
    public void learning_test() throws Exception {
      Hash objHash =
          hash(
              hash(anyTB()),
              hash("aaa"));
      assertCall(() -> byteDb().get(objHash))
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
      List<String> strings = ((ArrayB) byteDb().get(objHash))
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
          (Hash objHash) -> ((ArrayB) byteDb().get(objHash)).elems(IntB.class)
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          arrayTB(intTB()),
          (Hash objHash) -> ((ArrayB) byteDb().get(objHash)).elems(IntB.class));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_seq_size_different_than_multiple_of_hash_size(int byteCount) throws Exception {
      Hash notHashOfSeq = hash(ByteString.of(new byte[byteCount]));
      ArrayTB type = arrayTB(stringTB());
      Hash objHash =
          hash(
              hash(type),
              notHashOfSeq
          );
      assertCall(() -> ((ArrayB) byteDb().get(objHash)).elems(ValB.class))
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
      ArrayTB type = arrayTB(stringTB());
      Hash objHash =
          hash(
              hash(type),
              dataHash);
      assertCall(() -> ((ArrayB) byteDb().get(objHash)).elems(StringB.class))
          .throwsException(new DecodeObjNodeExc(objHash, type, DATA_PATH + "[0]"))
          .withCause(new DecodeObjNoSuchObjExc(nowhere));
    }

    @Test
    public void with_one_elem_of_wrong_type() throws Exception {
      ArrayTB type = arrayTB(stringTB());
      Hash objHash =
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
      assertCall(() -> ((ArrayB) byteDb().get(objHash)).elems(StringB.class))
          .throwsException(new DecodeObjWrongNodeCatExc(
              objHash, type, DATA_PATH, 1, stringTB(), boolTB()));
    }

    @Test
    public void with_one_elem_being_expr() throws Exception {
      ArrayTB type = arrayTB(stringTB());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(
                      hash(stringTB()),
                      hash("aaa")
                  ),
                  hash(paramRefB(1))
              ));
      assertCall(() -> ((ArrayB) byteDb().get(objHash)).elems(StringB.class))
          .throwsException(new DecodeObjWrongNodeCatExc(
              objHash, type, DATA_PATH, 1, ValB.class, ParamRefB.class));
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
              hash(blobTB()),
              hash(byteString));
      assertThat(((BlobB) byteDb().get(objHash)).source().readByteString())
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
          (Hash objHash) -> ((BlobB) byteDb().get(objHash)).source()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          blobTB(),
          (Hash objHash) -> ((BlobB) byteDb().get(objHash)).source());
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
              hash(boolTB()),
              hash(value));
      assertThat(((BoolB) byteDb().get(objHash)).toJ())
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
          (Hash objHash) -> ((BoolB) byteDb().get(objHash)).toJ()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          boolTB(),
          (Hash objHash) -> ((BoolB) byteDb().get(objHash)).toJ());
    }

    @Test
    public void empty_bytes_as_data() throws Exception {
      Hash dataHash = hash(ByteString.of());
      Hash objHash =
          hash(
              hash(boolTB()),
              dataHash);
      assertCall(() -> ((BoolB) byteDb().get(objHash)).toJ())
          .throwsException(new DecodeObjNodeExc(objHash, boolTB(), DATA_PATH))
          .withCause(new DecodeBooleanExc(dataHash, new DecodeByteExc(dataHash)));
    }

    @Test
    public void more_than_one_byte_as_data() throws Exception {
      Hash dataHash = hash(ByteString.of((byte) 0, (byte) 0));
      Hash objHash =
          hash(
              hash(boolTB()),
              dataHash);
      assertCall(() -> ((BoolB) byteDb().get(objHash)).toJ())
          .throwsException(new DecodeObjNodeExc(objHash, boolTB(), DATA_PATH))
          .withCause(new DecodeBooleanExc(dataHash, new DecodeByteExc(dataHash)));
    }

    @ParameterizedTest
    @ArgumentsSource(AllByteValuesExceptZeroAndOneProvider.class)
    public void one_byte_data_not_equal_zero_nor_one(byte value)
        throws Exception {
      Hash dataHash = hash(ByteString.of(value));
      Hash objHash =
          hash(
              hash(boolTB()),
              dataHash);
      assertCall(() -> ((BoolB) byteDb().get(objHash)).toJ())
          .throwsException(new DecodeObjNodeExc(objHash, boolTB(), DATA_PATH))
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
      var funcT = funcTB(intTB(), list(stringTB(), intTB()));
      var func = funcB(funcT, intB());
      var args = combineB(list(stringB(), intB()));
      var objHash =
          hash(
              hash(callCB(intTB())),
              hash(
                  hash(func),
                  hash(args)
              )
          );

      assertThat(((CallB) byteDb().get(objHash)).data().callable())
          .isEqualTo(func);
      assertThat(((CallB) byteDb().get(objHash)).data().args())
          .isEqualTo(args);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(callCB(intTB()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var funcT = funcTB(intTB(), list(stringTB(), intTB()));
      var func = funcB(funcT, intB());
      var args = combineB(list(stringB(), intB()));
      var dataHash = hash(
          hash(func),
          hash(args)
      );
      obj_root_with_two_data_hashes(
          callCB(intTB()),
          dataHash,
          (Hash objHash) -> ((CallB) byteDb().get(objHash)).data());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          callCB(intTB()),
          (Hash objHash) -> ((CallB) byteDb().get(objHash)).data());
    }

    @Test
    public void data_is_seq_with_one_elem() throws Exception {
      var funcT = funcTB(intTB(), list(stringTB(), intTB()));
      var func = funcB(funcT, intB());
      var dataHash = hash(
          hash(func)
      );
      var cat = callCB(intTB());
      var objHash =
          hash(
              hash(cat),
              dataHash
          );
      assertCall(() -> ((CallB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongSeqSizeExc(objHash, cat, DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_seq_with_three_elems() throws Exception {
      var funcT = funcTB(intTB(), list(stringTB(), intTB()));
      var func = funcB(funcT, intB());
      var args = combineB(list(stringB(), intB()));
      var dataHash = hash(
          hash(func),
          hash(args),
          hash(args)
      );
      var cat = callCB(intTB());
      var objHash =
          hash(
              hash(cat),
              dataHash
          );
      assertCall(() -> ((CallB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongSeqSizeExc(objHash, cat, DATA_PATH, 2, 3));
    }

    @Test
    public void func_component_evalT_is_not_func() throws Exception {
      var func = intB(3);
      var args = combineB(list(stringB(), intB()));
      var type = callCB(stringTB());
      var objHash =
          hash(
              hash(type),
              hash(
                  hash(func),
                  hash(args)
              )
          );
      assertCall(() -> ((CallB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongNodeTypeExc(
              objHash, type, "func", FuncTB.class, IntTB.class));
    }

    @Test
    public void args_is_val_instead_of_combine() throws Exception {
      var funcT = funcTB(intTB(), list(stringTB(), intTB()));
      var func = funcB(funcT, intB());
      var type = callCB(intTB());
      var objHash =
          hash(
              hash(type),
              hash(
                  hash(func),
                  hash(intB())
              )
          );
      assertCall(() -> ((CallB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongNodeCatExc(
              objHash, type, DATA_PATH + "[1]", CombineB.class, IntB.class));
    }

    @Test
    public void args_component_evalT_is_not_combine_but_different_expr()
        throws Exception {
      var funcT = funcTB(intTB(), list(stringTB(), intTB()));
      var func = funcB(funcT, intB());
      var type = callCB(intTB());
      var objHash =
          hash(
              hash(type),
              hash(
                  hash(func),
                  hash(paramRefB(1))
              )
          );
      assertCall(() -> ((CallB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongNodeCatExc(
              objHash, type, DATA_PATH + "[1]", CombineB.class, ParamRefB.class));
    }

    @Test
    public void evalT_is_different_than_func_evalT_result()
        throws Exception {
      var funcT = funcTB(intTB(), list(stringTB()));
      var func = funcB(funcT, intB());
      var args = combineB(list(stringB()));
      var type = callCB(stringTB());
      var objHash =
          hash(
              hash(type),
              hash(
                  hash(func),
                  hash(args)
              )
          );
      assertCall(() -> ((CallB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongNodeTypeExc(
                  objHash, type, "callable.result", stringTB(), intTB()));
    }

    @Test
    public void func_evalT_params_does_not_match_args_evalTs()
        throws Exception {
      var funcT = funcTB(intTB(), list(stringTB(), boolTB()));
      var func = funcB(funcT, intB());
      var args = combineB(list(stringB(), intB()));
      var spec = callCB(intTB());
      var objHash =
          hash(
              hash(spec),
              hash(
                  hash(func),
                  hash(args)
              )
          );
      assertCall(() -> ((CallB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongNodeTypeExc(
              objHash, spec, "args",
              tupleTB(list(stringTB(), boolTB())),
              tupleTB(list(stringTB(), intTB()))
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
      Hash objHash =
          hash(
              hash(combineCB(list(intTB(), stringTB()))),
              hash(
                  hash(expr1),
                  hash(expr2)
              ));
      var items = ((CombineB) byteDb().get(objHash)).items();
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
          (Hash objHash) -> ((CombineB) byteDb().get(objHash)).items()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          combineCB(),
          (Hash objHash) -> ((CombineB) byteDb().get(objHash)).items());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_seq_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      var notHashOfSeq = hash(ByteString.of(new byte[byteCount]));
      var objHash =
          hash(
              hash(combineCB()),
              notHashOfSeq
          );
      assertCall(() -> ((CombineB) byteDb().get(objHash)).items())
          .throwsException(new DecodeObjNodeExc(objHash, combineCB(), DATA_PATH))
          .withCause(new DecodeHashSeqExc(
              notHashOfSeq, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_seq_item_pointing_nowhere() throws Exception {
      var nowhere = Hash.of(33);
      var objHash =
          hash(
              hash(combineCB()),
              hash(
                  nowhere
              )
          );
      assertCall(() -> ((CombineB) byteDb().get(objHash)).items())
          .throwsException(new DecodeObjNodeExc(objHash, combineCB(), DATA_PATH + "[0]"))
          .withCause(new DecodeObjNoSuchObjExc(nowhere));
    }

    @Test
    public void evaluation_type_items_size_is_different_than_actual_items_size()
        throws Exception {
      var item1 =  intB();
      var type = combineCB(list(intTB(), stringTB()));
      var objHash =
          hash(
              hash(type),
              hash(
                  hash(item1)
              ));

      assertCall(() -> ((CombineB) byteDb().get(objHash)).items())
          .throwsException(new DecodeCombineWrongItemsSizeExc(objHash, type, 1));
    }

    @Test
    public void evaluation_type_item_is_different_than_evaluation_type_of_one_of_items()
        throws Exception {
      var item1 = intB(1);
      var item2 = stringB("abc");
      var type = combineCB(list(intTB(), boolTB()));
      var objHash =
          hash(
              hash(type),
              hash(
                  hash(item1),
                  hash(item2)
              ));

      assertCall(() -> ((CombineB) byteDb().get(objHash)).items())
          .throwsException(new DecodeObjWrongNodeTypeExc(
              objHash, type, "items[1]", boolTB(), stringTB()));
    }
  }

  @Nested
  class _func {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save Lambda
       * in HashedDb.
       */
      var bodyExpr = boolB(true);
      FuncTB type = funcTB(boolTB(), list(intTB(), stringTB()));
      Hash objHash =
          hash(
              hash(type),
              hash(bodyExpr)
          );
      assertThat(((FuncB) byteDb().get(objHash)).body())
          .isEqualTo(bodyExpr);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(funcTB());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var bodyExpr = boolB(true);
      var type = funcTB(boolTB(), list(intTB(), stringTB()));
      var dataHash = hash(bodyExpr);
      obj_root_with_two_data_hashes(
          type,
          dataHash,
          (Hash objHash) -> ((FuncB) byteDb().get(objHash)).body());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_obj_but_nowhere(funcTB(),
          (Hash objHash) -> ((FuncB) byteDb().get(objHash)).body());
    }

    @Test
    public void body_evaluation_type_is_not_equal_func_type_result() throws Exception {
      var bodyExpr = intB(3);
      var type = funcTB(boolTB(), list(intTB(), stringTB()));
      Hash objHash =
          hash(
              hash(type),
              hash(bodyExpr)
          );
      assertCall(() -> ((FuncB) byteDb().get(objHash)).body())
          .throwsException(new DecodeObjWrongNodeTypeExc(
              objHash, type, DATA_PATH, boolTB(), intTB()));
    }
  }

  @Nested
  class _if {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save IF
       * in HashedDb.
       */
      var condition = boolB(true);
      var then = intB(1);
      var else_ = intB(2);
      Hash objHash =
          hash(
              hash(ifCB(intTB())),
              hash(
                  hash(condition),
                  hash(then),
                  hash(else_)
              ));
      var data = ((IfB) byteDb().get(objHash)).data();
      assertThat(data)
          .isEqualTo(new IfB.Data(condition, then, else_));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(ifCB());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var condition = boolB(true);
      var then = intB(1);
      var else_ = intB(2);
      Hash dataHash = hash(
          hash(condition),
          hash(then),
          hash(else_)
      );
      obj_root_with_two_data_hashes(
          ifCB(intTB()),
          dataHash,
          (Hash objHash) -> ((IfB) byteDb().get(objHash)).data()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          ifCB(intTB()),
          (Hash objHash) -> ((IfB) byteDb().get(objHash)).data());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void data_seq_size_different_than_multiple_of_hash_size(int byteCount)
        throws Exception {
      var notHashOfSeq = hash(ByteString.of(new byte[byteCount]));
      var objHash =
          hash(
              hash(ifCB(intTB())),
              notHashOfSeq
          );
      assertCall(() -> ((IfB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjNodeExc(objHash, ifCB(intTB()), DATA_PATH))
          .withCause(new DecodeHashSeqExc(
              notHashOfSeq, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void data_seq_size_equal_two() throws Exception {
      var condition = boolB(true);
      var then = intB(1);
      var cat = ifCB(intTB());
      Hash objHash =
          hash(
              hash(cat),
              hash(
                  hash(condition),
                  hash(then)
              ));
      assertCall(() -> ((IfB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongSeqSizeExc(objHash, cat, DATA_PATH, 3, 2));
    }

    @Test
    public void data_seq_size_equal_four() throws Exception {
      var condition = boolB(true);
      var then = intB(1);
      var else_ = intB(2);
      var cat = ifCB(intTB());
      Hash objHash =
          hash(
              hash(cat),
              hash(
                  hash(condition),
                  hash(then),
                  hash(else_),
                  hash(else_)
              ));
      assertCall(() -> ((IfB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongSeqSizeExc(objHash, cat, DATA_PATH, 3, 4));
    }

    @Test
    public void data_seq_item_pointing_nowhere() throws Exception {
      var nowhereHash = Hash.of(33);
      var then = intB(1);
      var else_ = intB(2);
      var objHash =
          hash(
              hash(ifCB(intTB())),
              hash(
                  nowhereHash,
                  hash(then),
                  hash(else_)
              )
          );
      assertCall(() -> ((IfB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjNodeExc(objHash, ifCB(intTB()), DATA_PATH + "[0]"))
          .withCause(new DecodeObjNoSuchObjExc(nowhereHash));
    }

    @Test
    public void condition_type_not_equal_bool_type() throws Exception {
      var condition = intB();
      var then = intB(1);
      var else_ = intB(2);
      var cat = ifCB(intTB());
      var objHash =
          hash(
              hash(cat),
              hash(
                  hash(condition),
                  hash(then),
                  hash(else_)
              ));
      assertCall(() -> ((IfB) byteDb().get(objHash)).data())
          .throwsException(
              new DecodeObjWrongNodeTypeExc(objHash, cat, DATA_PATH, 0, boolTB(), intTB()));
    }

    @Test
    public void then_type_not_subtype_of_evalT() throws Exception {
      var condition = boolB(true);
      var then = stringB();
      var else_ = intB();
      var cat = ifCB(intTB());
      Hash objHash =
          hash(
              hash(cat),
              hash(
                  hash(condition),
                  hash(then),
                  hash(else_)
              ));
      assertCall(() -> ((IfB) byteDb().get(objHash)).data())
          .throwsException(
              new DecodeObjWrongNodeTypeExc(objHash, cat, DATA_PATH, 1, intTB(), stringTB()));
    }

    @Test
    public void else_type_not_subtype_of_evalT() throws Exception {
      var condition = boolB(true);
      var then = intB();
      var else_ = stringB();
      var cat = ifCB(intTB());
      Hash objHash =
          hash(
              hash(cat),
              hash(
                  hash(condition),
                  hash(then),
                  hash(else_)
              ));
      assertCall(() -> ((IfB) byteDb().get(objHash)).data())
          .throwsException(
              new DecodeObjWrongNodeTypeExc(objHash, cat, DATA_PATH, 2, intTB(), stringTB()));
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
              hash(intTB()),
              hash(byteString));
      assertThat(((IntB) byteDb().get(objHash)).toJ())
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
          (Hash objHash) -> ((IntB) byteDb().get(objHash)).toJ()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          intTB(),
          (Hash objHash) -> ((IntB) byteDb().get(objHash)).toJ());
    }
  }

  @Nested
  class _invoke {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save invoke
       * in HashedDb.
       */
      var methodT = methodTB(intTB(), list(stringTB(), intTB()));
      var method = methodB(methodT, blobB(), stringB(), boolB());
      var args = combineB(list(stringB(), intB()));
      Hash objHash =
          hash(
              hash(invokeCB(intTB())),
              hash(
                  hash(method),
                  hash(args)
              )
          );
      assertThat(((InvokeB) byteDb().get(objHash)).data().method())
          .isEqualTo(method);
      assertThat(((InvokeB) byteDb().get(objHash)).data().args())
          .isEqualTo(args);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(invokeCB(intTB()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var methodT = methodTB(intTB(), list(stringTB(), intTB()));
      var method = methodB(methodT, blobB(), stringB(), boolB());
      var args = combineB(list(stringB(), intB()));
      Hash dataHash = hash(
          hash(method),
          hash(args)
      );
      obj_root_with_two_data_hashes(
          invokeCB(intTB()),
          dataHash,
          (Hash objHash) -> ((InvokeB) byteDb().get(objHash)).data());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          invokeCB(intTB()),
          (Hash objHash) -> ((InvokeB) byteDb().get(objHash)).data());
    }

    @Test
    public void data_is_seq_with_one_elem() throws Exception {
      var methodT = methodTB(intTB(), list(stringTB(), intTB()));
      var method = methodB(methodT, blobB(), stringB(), boolB());
      var dataHash = hash(
          hash(method)
      );
      var cat = invokeCB(intTB());
      var objHash =
          hash(
              hash(cat),
              dataHash
          );
      assertCall(() -> ((InvokeB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongSeqSizeExc(objHash, cat, DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_seq_with_three_elems() throws Exception {
      var func = intB(0);
      var args = combineB(list(stringB(), intB()));
      var dataHash = hash(
          hash(func),
          hash(args),
          hash(args)
      );
      var cat = invokeCB(intTB());
      var objHash =
          hash(
              hash(cat),
              dataHash
          );
      assertCall(() -> ((InvokeB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongSeqSizeExc(objHash, cat, DATA_PATH, 2, 3));
    }

    @Test
    public void args_is_val_instead_of_expr() throws Exception {
      var methodT = methodTB(intTB(), list(stringTB(), intTB()));
      var method = methodB(methodT, blobB(), stringB(), boolB());
      var cat = invokeCB(intTB());
      var objHash =
          hash(
              hash(cat),
              hash(
                  hash(method),
                  hash(intB())
              )
          );
      assertCall(() -> ((InvokeB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongNodeCatExc(
              objHash, cat, DATA_PATH + "[1]", CombineB.class, IntB.class));
    }

    @Test
    public void args_component_evalT_is_not_combine_but_different_expr()
        throws Exception {
      var methodT = methodTB(intTB(), list(stringTB(), intTB()));
      var method = methodB(methodT, blobB(), stringB(), boolB());
      var type = invokeCB(intTB());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(method),
                  hash(paramRefB(1))
              )
          );
      assertCall(() -> ((InvokeB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongNodeCatExc(
              objHash, type, DATA_PATH + "[1]", CombineB.class, ParamRefB.class));
    }

    @Test
    public void evalT_is_different_than_method_evalT_result()
        throws Exception {
      var methodT = methodTB(intTB(), list(stringTB(), intTB()));
      var method = methodB(methodT, blobB(), stringB(), boolB());
      var args = combineB(list(stringB(), intB()));
      var type = invokeCB(stringTB());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(method),
                  hash(args)
              )
          );
      assertCall(() -> ((InvokeB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongNodeTypeExc(
              objHash, type, "callable.result", stringTB(), intTB()));
    }

    @Test
    public void method_evalT_params_does_not_match_args_evalTs() throws Exception {
      var methodT = methodTB(intTB(), list(stringTB(), boolTB()));
      var method = methodB(methodT, blobB(), stringB(), boolB());
      var args = combineB(list(stringB(), intB()));
      var spec = invokeCB(intTB());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(method),
                  hash(args)
              )
          );
      assertCall(() -> ((InvokeB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongNodeTypeExc(
              objHash, spec, "args",
              tupleTB(list(stringTB(), boolTB())),
              tupleTB(list(stringTB(), intTB()))
          ));
    }
  }

  @Nested
  class _map {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save MAP
       * in HashedDb.
       */
      var array = arrayB(intB(7));
      var func = funcB(list(intTB()), stringB("abc"));
      var cat = mapCB(arrayTB(stringTB()));
      Hash objHash = hash(
          hash(cat),
          hash(
              hash(array),
              hash(func)
          )
      );
      var data = ((MapB) byteDb().get(objHash)).data();
      assertThat(data)
          .isEqualTo(new MapB.Data(array, func));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(mapCB());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var array = arrayB(intB(7));
      var func = funcB(list(intTB()), stringB("abc"));
      var cat = mapCB(arrayTB(stringTB()));
      Hash dataHash = hash(
          hash(array),
          hash(func)
      );
      obj_root_with_two_data_hashes(
          cat,
          dataHash,
          (Hash objHash) -> ((MapB) byteDb().get(objHash)).data()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          mapCB(arrayTB(stringTB())),
          (Hash objHash) -> ((MapB) byteDb().get(objHash)).data());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void data_seq_size_different_than_multiple_of_hash_size(int byteCount) throws Exception {
      var cat = mapCB(arrayTB(stringTB()));
      var notHashOfSeq = hash(ByteString.of(new byte[byteCount]));
      var objHash = hash(
          hash(cat),
          notHashOfSeq
      );
      assertCall(() -> ((MapB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjNodeExc(objHash, cat, DATA_PATH))
          .withCause(new DecodeHashSeqExc(notHashOfSeq, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void data_seq_size_equal_one() throws Exception {
      var array = arrayB(intB(7));
      var cat = mapCB(arrayTB(stringTB()));
      Hash objHash = hash(
          hash(cat),
          hash(
              hash(array)
          )
      );
      assertCall(() -> ((MapB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongSeqSizeExc(objHash, cat, DATA_PATH, 2, 1));
    }

    @Test
    public void data_seq_size_equal_three() throws Exception {
      var array = arrayB(intB(7));
      var func = funcB(list(intTB()), stringB("abc"));
      var cat = mapCB(arrayTB(stringTB()));
      Hash objHash = hash(
          hash(cat),
          hash(
              hash(array),
              hash(func),
              hash(func)
          )
      );
      assertCall(() -> ((MapB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongSeqSizeExc(objHash, cat, DATA_PATH, 2, 3));
    }

    @Test
    public void data_seq_elem_pointing_nowhere() throws Exception {
      var array = arrayB(intB(7));
      var cat = mapCB(arrayTB(stringTB()));
      var nowhereHash = Hash.of(33);
      Hash objHash = hash(
          hash(cat),
          hash(
              hash(array),
              nowhereHash
          )
      );
      assertCall(() -> ((MapB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjNodeExc(objHash, cat, DATA_PATH + "[1]"))
          .withCause(new DecodeObjNoSuchObjExc(nowhereHash));
    }

    @Test
    public void array_component_type_not_equal_array_type() throws Exception {
      var notArray = intB(7);
      var func = funcB(list(intTB()), stringB("abc"));
      var cat = mapCB(arrayTB(stringTB()));
      Hash objHash = hash(
          hash(cat),
          hash(
              hash(notArray),
              hash(func)
          )
      );
      assertCall(() -> ((MapB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongNodeTypeExc(
              objHash, cat, DATA_PATH, 0, ArrayTB.class, IntTB.class));
    }

    @Test
    public void func_component_type_not_equal_func_type() throws Exception {
      var array = arrayB(intB(7));
      var notFunc = intB(8);
      var cat = mapCB(arrayTB(stringTB()));
      Hash objHash = hash(
          hash(cat),
          hash(
              hash(array),
              hash(notFunc)
          )
      );
      assertCall(() -> ((MapB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongNodeTypeExc(
              objHash, cat, DATA_PATH, 1, FuncTB.class, IntTB.class));
    }

    @Test
    public void func_component_type_is_func_type_with_more_than_one_param() throws Exception {
      var array = arrayB(intB(7));
      var func = funcB(list(intTB(), intTB()), stringB("abc"));
      var cat = mapCB(arrayTB(stringTB()));
      Hash objHash = hash(
          hash(cat),
          hash(
              hash(array),
              hash(func)
          )
      );
      assertCall(() -> ((MapB) byteDb().get(objHash)).data())
          .throwsException(new DecodeMapIllegalMappingFuncExc(
              objHash, cat, funcTB(stringTB(), list(intTB(), intTB()))));
    }

    @Test
    public void array_elemT_is_not_assignable_to_func_paramT() throws Exception {
      var array = arrayB(blobB());
      var func = funcB(list(intTB()), stringB("abc"));
      var cat = mapCB(arrayTB(stringTB()));
      Hash objHash = hash(
          hash(cat),
          hash(
              hash(array),
              hash(func)
          )
      );

      assertCall(() -> ((MapB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongNodeTypeExc(
              objHash, cat, "array element", intTB(), blobTB()));
    }

    @Test
    public void func_resT_is_not_assignable_to_output_array_elemT() throws Exception {
      var array = arrayB(intB(7));
      var func = funcB(list(intTB()), stringB("abc"));
      var cat = mapCB(arrayTB(blobTB()));
      Hash objHash = hash(
          hash(cat),
          hash(
              hash(array),
              hash(func)
          )
      );
      assertCall(() -> ((MapB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongNodeTypeExc(
              objHash, cat, "func.result", blobTB(), stringTB()));
    }
  }

  @Nested
  class _method {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save
       * Method in HashedDb.
       */
      var type = methodTB(stringTB(), list(intTB()));
      var jar = blobB();
      var classBinaryName = stringB();
      var isPure = boolB(true);
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(jar),
                  hash(classBinaryName),
                  hash(isPure)
              )
          );

      assertThat(((MethodB) byteDb().get(objHash)).jar())
          .isEqualTo(jar);
      assertThat(((MethodB) byteDb().get(objHash)).classBinaryName())
          .isEqualTo(classBinaryName);
      assertThat(((MethodB) byteDb().get(objHash)).isPure())
          .isEqualTo(isPure);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(methodTB(stringTB(), list(intTB())));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var type = methodTB(stringTB(), list(intTB()));
      var jar = blobB();
      var classBinaryName = stringB();
      var isPure = boolB(true);
      Hash dataHash = hash(
          hash(jar),
          hash(classBinaryName),
          hash(isPure)
      );
      obj_root_with_two_data_hashes(type, dataHash,
          (Hash objHash) -> ((MethodB) byteDb().get(objHash)).classBinaryName());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      var type = methodTB(stringTB(), list(intTB()));
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(type,
          (Hash objHash) -> ((MethodB) byteDb().get(objHash)).classBinaryName());
    }

    @Test
    public void data_is_seq_with_two_elem() throws Exception {
      var type = methodTB(stringTB(), list(intTB()));
      var jar = blobB();
      var classBinaryName = stringB();
      Hash dataHash = hash(
          hash(jar),
          hash(classBinaryName)
      );
      Hash objHash =
          hash(
              hash(type),
              dataHash
          );

      assertCall(() -> ((MethodB) byteDb().get(objHash)).classBinaryName())
          .throwsException(new DecodeObjWrongSeqSizeExc(
              objHash, type, DATA_PATH, 3, 2));
    }

    @Test
    public void data_is_seq_with_four_elems() throws Exception {
      var type = methodTB(stringTB(), list(intTB()));
      var jar = blobB();
      var classBinaryName = stringB();
      var isPure = boolB(true);
      Hash dataHash = hash(
          hash(jar),
          hash(classBinaryName),
          hash(isPure),
          hash(isPure)
      );
      Hash objHash =
          hash(
              hash(type),
              dataHash
          );

      assertCall(() -> ((MethodB) byteDb().get(objHash)).classBinaryName())
          .throwsException(new DecodeObjWrongSeqSizeExc(
              objHash, type, DATA_PATH, 3, 4));
    }

    @Test
    public void jar_file_is_not_blob_value() throws Exception {
      var type = methodTB(stringTB(), list(intTB()));
      var jar = stringB();
      var classBinaryName = stringB();
      var isPure = boolB(true);
      var objHash =
          hash(
              hash(type),
              hash(
                  hash(jar),
                  hash(classBinaryName),
                  hash(isPure)
              )
          );
      assertCall(() -> ((MethodB) byteDb().get(objHash)).jar())
          .throwsException(new DecodeObjWrongNodeCatExc(
              objHash, type, DATA_PATH + "[0]", BlobB.class, StringB.class));
    }

    @Test
    public void class_binary_name_is_not_string_value() throws Exception {
      var type = methodTB(stringTB(), list(intTB()));
      var jar = blobB();
      var classBinaryName = intB();
      var isPure = boolB(true);
      var objHash =
          hash(
              hash(type),
              hash(
                  hash(jar),
                  hash(classBinaryName),
                  hash(isPure)
              )
          );

      assertCall(() -> ((MethodB) byteDb().get(objHash)).classBinaryName())
          .throwsException(new DecodeObjWrongNodeCatExc(
              objHash, type, DATA_PATH + "[1]", StringB.class, IntB.class));
    }

    @Test
    public void is_pure_is_not_bool_value() throws Exception {
      var type = methodTB(stringTB(), list(intTB()));
      var jar = blobB();
      var classBinaryName = stringB();
      var isPure = stringB();
      var objHash =
          hash(
              hash(type),
              hash(
                  hash(jar),
                  hash(classBinaryName),
                  hash(isPure)
              )
          );

      assertCall(() -> ((MethodB) byteDb().get(objHash)).isPure())
          .throwsException(new DecodeObjWrongNodeCatExc(
              objHash, type, DATA_PATH + "[2]", BoolB.class, StringB.class));
    }
  }

  @Nested
  class _nothing {
    @Test
    public void learning_test() throws Exception {
      Hash objHash =
          hash(
              hash(nothingTB()),
              hash("aaa"));
      assertCall(() -> byteDb().get(objHash))
          .throwsException(UnsupportedOperationException.class);
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
      var expr1 = arrayB(intTB(), intB(1));
      var expr2 = arrayB(nothingTB());
      Hash objHash =
          hash(
              hash(orderCB(arrayTB(intTB()))),
              hash(
                  hash(expr1),
                  hash(expr2)
              ));
      var elems = ((OrderB) byteDb().get(objHash)).elems();
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
          (Hash objHash) -> ((OrderB) byteDb().get(objHash)).elems()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          orderCB(),
          (Hash objHash) -> ((OrderB) byteDb().get(objHash)).elems());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_seq_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSeq = hash(ByteString.of(new byte[byteCount]));
      Hash objHash =
          hash(
              hash(orderCB()),
              notHashOfSeq
          );
      assertCall(() -> ((OrderB) byteDb().get(objHash)).elems())
          .throwsException(new DecodeObjNodeExc(objHash, orderCB(), DATA_PATH))
          .withCause(new DecodeHashSeqExc(
              notHashOfSeq, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_seq_elem_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash objHash =
          hash(
              hash(orderCB()),
              hash(
                  nowhere
              )
          );
      assertCall(() -> ((OrderB) byteDb().get(objHash)).elems())
          .throwsException(new DecodeObjNodeExc(objHash, orderCB(), DATA_PATH + "[0]"))
          .withCause(new DecodeObjNoSuchObjExc(nowhere));
    }

    @Test
    public void evaluation_type_elem_is_different_than_evaluation_type_of_one_of_elems()
        throws Exception {
      var expr1 = intB();
      var expr2 = stringB();
      var type = orderCB(intTB());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(expr1),
                  hash(expr2)
              ));
      assertCall(() -> ((OrderB) byteDb().get(objHash)).elems())
          .throwsException(new DecodeObjWrongNodeTypeExc(
                  objHash, type, "elems[1]", intTB(), stringTB()));
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
      Hash objHash =
          hash(
              hash(paramRefCB(stringTB())),
              hash(byteString));
      assertThat(((ParamRefB) byteDb().get(objHash)).value())
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
          (Hash objHash) -> ((ParamRefB) byteDb().get(objHash)).value()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          paramRefCB(intTB()),
          (Hash objHash) -> ((ParamRefB) byteDb().get(objHash)).value());
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
      var tupleT = tupleTB(list(stringTB()));
      var tuple = tupleB(tupleT, list(stringB("abc")));
      var selectable = (ValB) tuple;
      var index = intB(0);
      Hash objHash =
          hash(
              hash(selectCB(stringTB())),
              hash(
                  hash(selectable),
                  hash(index)
              )
          );
      assertThat(((SelectB) byteDb().get(objHash)).data())
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
          (Hash objHash) -> ((SelectB) byteDb().get(objHash)).data());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          selectCB(),
          (Hash objHash) -> ((SelectB) byteDb().get(objHash)).data());
    }

    @Test
    public void data_is_seq_with_one_elem() throws Exception {
      var expr = intB(123);
      var dataHash = hash(
          hash(expr)
      );
      Hash objHash =
          hash(
              hash(selectCB()),
              dataHash
          );
      assertCall(() -> ((SelectB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongSeqSizeExc(
              objHash, selectCB(), DATA_PATH, 2, 1));
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
      Hash objHash =
          hash(
              hash(selectCB()),
              dataHash
          );
      assertCall(() -> ((SelectB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongSeqSizeExc(
              objHash, selectCB(), DATA_PATH, 2, 3));
    }

    @Test
    public void tuple_is_not_tuple_expr() throws Exception {
      var expr = intB(3);
      var index = intB(0);
      var type = selectCB(stringTB());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(expr),
                  hash(index)
              )
          );

      assertCall(() -> ((SelectB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongNodeTypeExc(
              objHash, type, "tuple", TupleTB.class, IntTB.class));
    }

    @Test
    public void index_is_out_of_bounds() throws Exception {
      var tupleT = tupleTB(list(stringTB()));
      var tuple = tupleB(tupleT, list(stringB("abc")));
      var index = intB(1);
      var type = selectCB(stringTB());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(tuple),
                  hash(index)
              )
          );

      assertCall(() -> ((SelectB) byteDb().get(objHash)).data())
          .throwsException(new DecodeSelectIndexOutOfBoundsExc(objHash, type, 1, 1));
    }

    @Test
    public void evaluation_type_is_different_than_type_of_item_pointed_to_by_index()
        throws Exception {
      var tupleT = tupleTB(list(stringTB()));
      var tuple = tupleB(tupleT, list(stringB("abc")));
      var index = intB(0);
      var type = selectCB(intTB());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(tuple),
                  hash(index)
              )
          );

      assertCall(() -> ((SelectB) byteDb().get(objHash)).data())
          .throwsException(new DecodeSelectWrongEvalTypeExc(objHash, type, stringTB()));
    }

    @Test
    public void index_is_string_instead_of_int() throws Exception {
      var type = selectCB(stringTB());
      var tupleT = tupleTB(list(stringTB()));
      var tuple = tupleB(tupleT, list(stringB("abc")));
      var strVal = stringB("abc");
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(tuple),
                  hash(strVal)
              )
          );
      assertCall(() -> ((SelectB) byteDb().get(objHash)).data())
          .throwsException(new DecodeObjWrongNodeCatExc(
              objHash, type, DATA_PATH + "[1]", IntB.class, StringB.class));
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
              hash(stringTB()),
              hash("aaa"));
      assertThat(((StringB) byteDb().get(objHash)).toJ())
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
          (Hash objHash) -> ((StringB) byteDb().get(objHash)).toJ()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          stringTB(),
          (Hash objHash) -> ((StringB) byteDb().get(objHash)).toJ());
    }

    @Test
    public void data_being_invalid_utf8_seq() throws Exception {
      Hash notStringHash = hash(illegalString());
      Hash objHash =
          hash(
              hash(stringTB()),
              notStringHash);
      assertCall(() -> ((StringB) byteDb().get(objHash)).toJ())
          .throwsException(new DecodeObjNodeExc(objHash, stringTB(), DATA_PATH))
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
          (Hash objHash) -> ((TupleB) byteDb().get(objHash)).get(0)
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          personTB(),
          (Hash objHash) -> ((TupleB) byteDb().get(objHash)).get(0));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_seq_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSeq = hash(ByteString.of(new byte[byteCount]));
      Hash objHash =
          hash(
              hash(personTB()),
              notHashOfSeq);
      assertCall(() -> ((TupleB) byteDb().get(objHash)).get(0))
          .throwsException(new DecodeObjNodeExc(objHash, personTB(), DATA_PATH))
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
              hash(personTB()),
              dataHash
          );
      assertCall(() -> ((TupleB) byteDb().get(objHash)).get(0))
          .throwsException(new DecodeObjNodeExc(objHash, personTB(), DATA_PATH + "[0]"))
          .withCause(new DecodeObjNoSuchObjExc(nowhere));
    }

    @Test
    public void with_too_few_elems() throws Exception {
      Hash dataHash =
          hash(
              hash(stringB("John")));
      Hash objHash =
          hash(
              hash(personTB()),
              dataHash);
      TupleB tuple = (TupleB) byteDb().get(objHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new DecodeObjWrongSeqSizeExc(objHash, personTB(), DATA_PATH, 2, 1));
    }

    @Test
    public void with_too_many_elems() throws Exception {
      Hash dataHash =
          hash(
              hash(stringB("John")),
              hash(stringB("Doe")),
              hash(stringB("junk")));
      Hash objHash =
          hash(
              hash(personTB()),
              dataHash);
      TupleB tuple = (TupleB) byteDb().get(objHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new DecodeObjWrongSeqSizeExc(objHash, personTB(), DATA_PATH, 2, 3));
    }

    @Test
    public void with_elem_of_wrong_type() throws Exception {
      Hash objHash =
          hash(
              hash(personTB()),
              hash(
                  hash(stringB("John")),
                  hash(boolB(true))));
      TupleB tuple = (TupleB) byteDb().get(objHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new DecodeObjWrongNodeCatExc(
              objHash, personTB(), DATA_PATH, 1, stringTB(), boolTB()));
    }

    @Test
    public void with_elem_being_expr() throws Exception {
      Hash objHash =
          hash(
              hash(personTB()),
              hash(
                  hash(stringB("John")),
                  hash(paramRefB(1))));
      TupleB tuple = (TupleB) byteDb().get(objHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new DecodeObjWrongNodeCatExc(
              objHash, personTB(), DATA_PATH + "[1]", ValB.class, ParamRefB.class));
    }
  }

  private void obj_root_without_data_hash(CatB type) throws HashedDbExc {
    Hash objHash =
        hash(
            hash(type));
    assertCall(() -> byteDb().get(objHash))
        .throwsException(wrongSizeOfRootSeqException(objHash, 1));
  }

  private void obj_root_with_two_data_hashes(
      CatB type, Hash dataHash, Function<Hash, ?> readClosure) throws HashedDbExc {
    Hash objHash =
        hash(
            hash(type),
            dataHash,
            dataHash);
    assertCall(() -> readClosure.apply(objHash))
        .throwsException(wrongSizeOfRootSeqException(objHash, 3));
  }

  private void obj_root_with_data_hash_not_pointing_to_obj_but_nowhere(
      CatB type, Function<Hash, ?> readClosure) throws HashedDbExc {
    Hash dataHash = Hash.of(33);
    Hash objHash =
        hash(
            hash(type),
            dataHash);
    assertCall(() -> readClosure.apply(objHash))
        .throwsException(new DecodeObjNodeExc(objHash, type, DATA_PATH))
        .withCause(new DecodeObjNoSuchObjExc(dataHash));
  }

  private void obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
      CatB type, Consumer<Hash> readClosure) throws HashedDbExc {
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
              hash(varTB("A")),
              hash("aaa"));
      assertCall(() -> byteDb().get(objHash))
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

  protected Hash hash(ObjB obj) {
    return obj.hash();
  }

  protected Hash hash(CatB type) {
    return type.hash();
  }

  protected Hash hash(Hash... hashes) throws HashedDbExc {
    return hashedDb().writeSeq(hashes);
  }
}
