package org.smoothbuild.vm.bytecode.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.StringCreators.illegalString;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.vm.bytecode.expr.ExprB.DATA_PATH;
import static org.smoothbuild.vm.bytecode.expr.exc.DecodeExprRootException.cannotReadRootException;
import static org.smoothbuild.vm.bytecode.expr.exc.DecodeExprRootException.wrongSizeOfRootChainException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import okio.ByteString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeCombineWrongElementsSizeException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprCatException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprNoSuchExprException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprNodeException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprRootException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongChainSizeException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeClassException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodePickWrongEvaluationTypeException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeSelectIndexOutOfBoundsException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeSelectWrongEvaluationTypeException;
import org.smoothbuild.vm.bytecode.expr.oper.CallB;
import org.smoothbuild.vm.bytecode.expr.oper.CallSubExprsB;
import org.smoothbuild.vm.bytecode.expr.oper.CombineB;
import org.smoothbuild.vm.bytecode.expr.oper.OrderB;
import org.smoothbuild.vm.bytecode.expr.oper.PickB;
import org.smoothbuild.vm.bytecode.expr.oper.PickSubExprsB;
import org.smoothbuild.vm.bytecode.expr.oper.SelectB;
import org.smoothbuild.vm.bytecode.expr.oper.SelectSubExprsB;
import org.smoothbuild.vm.bytecode.expr.oper.VarB;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.BoolB;
import org.smoothbuild.vm.bytecode.expr.value.IntB;
import org.smoothbuild.vm.bytecode.expr.value.LambdaB;
import org.smoothbuild.vm.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.vm.bytecode.expr.value.StringB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.hashed.exc.DecodeBooleanException;
import org.smoothbuild.vm.bytecode.hashed.exc.DecodeByteException;
import org.smoothbuild.vm.bytecode.hashed.exc.DecodeHashChainException;
import org.smoothbuild.vm.bytecode.hashed.exc.DecodeStringException;
import org.smoothbuild.vm.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.vm.bytecode.hashed.exc.NoSuchDataException;
import org.smoothbuild.vm.bytecode.type.CategoryB;
import org.smoothbuild.vm.bytecode.type.exc.DecodeCatException;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;
import org.smoothbuild.vm.bytecode.type.value.IntTB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;

public class ExprBCorruptedTest extends TestContext {
  @Nested
  class _expr {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save value
       * in HashedDb.
       */
      var hash = hash(hash(stringTB()), hash("aaa"));
      assertThat(((StringB) bytecodeDb().get(hash)).toJ()).isEqualTo("aaa");
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void merkle_root_byte_count_is_not_multiple_of_hash_size(int byteCount)
        throws IOException, HashedDbException {
      var hash = hash(ByteString.of(new byte[byteCount]));
      assertCall(() -> bytecodeDb().get(hash))
          .throwsException(cannotReadRootException(hash, null))
          .withCause(new DecodeHashChainException(hash, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void corrupted_type() throws Exception {
      var typeHash = Hash.of("not a type");
      var hash = hash(typeHash, hash("aaa"));
      assertCall(() -> bytecodeDb().get(hash))
          .throwsException(new DecodeExprCatException(hash))
          .withCause(new DecodeCatException(typeHash));
    }

    @Test
    public void reading_elems_from_not_stored_object_throws_exception() {
      var hash = Hash.of(33);
      assertCall(() -> bytecodeDb().get(hash))
          .throwsException(new DecodeExprNoSuchExprException(hash))
          .withCause(new NoSuchDataException(hash));
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
      var hash = hash(
          hash(arrayTB(stringTB())),
          hash(hash(hash(stringTB()), hash("aaa")), hash(hash(stringTB()), hash("bbb"))));
      List<String> strings =
          ((ArrayB) bytecodeDb().get(hash)).elems(StringB.class).map(StringB::toJ);
      assertThat(strings).containsExactly("aaa", "bbb").inOrder();
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(arrayTB(intTB()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          arrayTB(intTB()),
          hashedDb().writeHashChain(),
          (Hash hash) -> ((ArrayB) bytecodeDb().get(hash)).elems(IntB.class));
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          arrayTB(intTB()), (Hash hash) -> ((ArrayB) bytecodeDb().get(hash)).elems(IntB.class));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_chain_size_different_than_multiple_of_hash_size(int byteCount)
        throws Exception {
      var notHashOfChain = hash(ByteString.of(new byte[byteCount]));
      ArrayTB type = arrayTB(stringTB());
      var hash = hash(hash(type), notHashOfChain);
      assertCall(() -> ((ArrayB) bytecodeDb().get(hash)).elems(ValueB.class))
          .throwsException(new DecodeExprNodeException(hash, type, DATA_PATH))
          .withCause(
              new DecodeHashChainException(notHashOfChain, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_chain_element_pointing_nowhere() throws Exception {
      var nowhereHash = Hash.of(33);
      var dataHash = hash(nowhereHash);
      var arrayTB = arrayTB(stringTB());
      var hash = hash(hash(arrayTB), dataHash);
      assertCall(() -> ((ArrayB) bytecodeDb().get(hash)).elems(StringB.class))
          .throwsException(new DecodeExprNodeException(hash, arrayTB, DATA_PATH + "[0]"))
          .withCause(new DecodeExprNoSuchExprException(nowhereHash));
    }

    @Test
    public void with_one_elem_of_wrong_type() throws Exception {
      var arrayTB = arrayTB(stringTB());
      var hash = hash(
          hash(arrayTB),
          hash(hash(hash(stringTB()), hash("aaa")), hash(hash(boolTB()), hash(true))));
      assertCall(() -> ((ArrayB) bytecodeDb().get(hash)).elems(StringB.class))
          .throwsException(new DecodeExprWrongNodeTypeException(
              hash, arrayTB, DATA_PATH, 1, stringTB(), boolTB()));
    }

    @Test
    public void with_one_elem_being_oper() throws Exception {
      var arrayTB = arrayTB(stringTB());
      var hash = hash(hash(arrayTB), hash(hash(hash(stringTB()), hash("aaa")), hash(varB(1))));
      assertCall(() -> ((ArrayB) bytecodeDb().get(hash)).elems(StringB.class))
          .throwsException(new DecodeExprWrongNodeClassException(
              hash, arrayTB, DATA_PATH, 1, ValueB.class, VarB.class));
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
      var byteString = ByteString.of((byte) 1, (byte) 2);
      var hash = hash(hash(blobTB()), hash(byteString));
      try (var source = ((BlobB) bytecodeDb().get(hash)).source()) {
        assertThat(source.readByteString()).isEqualTo(byteString);
      }
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
          (Hash hash) -> ((BlobB) bytecodeDb().get(hash)).source());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          blobTB(), (Hash hash) -> ((BlobB) bytecodeDb().get(hash)).source());
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
      var hash = hash(hash(boolTB()), hash(value));
      assertThat(((BoolB) bytecodeDb().get(hash)).toJ()).isEqualTo(value);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(boolTB());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(boolTB(), hashedDb().writeBoolean(true), (Hash hash) -> ((BoolB)
              bytecodeDb().get(hash))
          .toJ());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          boolTB(), (Hash hash) -> ((BoolB) bytecodeDb().get(hash)).toJ());
    }

    @Test
    public void empty_bytes_as_data() throws Exception {
      var dataHash = hash(ByteString.of());
      var hash = hash(hash(boolTB()), dataHash);
      assertCall(() -> ((BoolB) bytecodeDb().get(hash)).toJ())
          .throwsException(new DecodeExprNodeException(hash, boolTB(), DATA_PATH))
          .withCause(new DecodeBooleanException(dataHash, new DecodeByteException(dataHash)));
    }

    @Test
    public void more_than_one_byte_as_data() throws Exception {
      var dataHash = hash(ByteString.of((byte) 0, (byte) 0));
      var hash = hash(hash(boolTB()), dataHash);
      assertCall(() -> ((BoolB) bytecodeDb().get(hash)).toJ())
          .throwsException(new DecodeExprNodeException(hash, boolTB(), DATA_PATH))
          .withCause(new DecodeBooleanException(dataHash, new DecodeByteException(dataHash)));
    }

    @ParameterizedTest
    @ArgumentsSource(AllByteValuesExceptZeroAndOneProvider.class)
    public void one_byte_data_not_equal_zero_nor_one(byte value) throws Exception {
      var dataHash = hash(ByteString.of(value));
      var hash = hash(hash(boolTB()), dataHash);
      assertCall(() -> ((BoolB) bytecodeDb().get(hash)).toJ())
          .throwsException(new DecodeExprNodeException(hash, boolTB(), DATA_PATH))
          .withCause(new DecodeBooleanException(dataHash));
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
      var funcT = funcTB(stringTB(), intTB(), intTB());
      var func = lambdaB(funcT, intB());
      var args = combineB(stringB(), intB());
      var hash = hash(hash(callCB(intTB())), hash(hash(func), hash(args)));

      assertThat(((CallB) bytecodeDb().get(hash)).subExprs())
          .isEqualTo(new CallSubExprsB(func, args));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(callCB(intTB()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var funcT = funcTB(stringTB(), intTB(), intTB());
      var func = lambdaB(funcT, intB());
      var args = combineB(stringB(), intB());
      var dataHash = hash(hash(func), hash(args));
      obj_root_with_two_data_hashes(
          callCB(intTB()), dataHash, (Hash hash) -> ((CallB) bytecodeDb().get(hash)).subExprs());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          callCB(intTB()), (Hash hash) -> ((CallB) bytecodeDb().get(hash)).subExprs());
    }

    @Test
    public void data_is_chain_with_one_elem() throws Exception {
      var funcT = funcTB(stringTB(), intTB(), intTB());
      var func = lambdaB(funcT, intB());
      var dataHash = hash(hash(func));
      var cat = callCB(intTB());
      var hash = hash(hash(cat), dataHash);
      assertCall(() -> ((CallB) bytecodeDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongChainSizeException(hash, cat, DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_chain_with_three_elems() throws Exception {
      var funcT = funcTB(stringTB(), intTB(), intTB());
      var func = lambdaB(funcT, intB());
      var args = combineB(stringB(), intB());
      var dataHash = hash(hash(func), hash(args), hash(args));
      var cat = callCB(intTB());
      var hash = hash(hash(cat), dataHash);
      assertCall(() -> ((CallB) bytecodeDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongChainSizeException(hash, cat, DATA_PATH, 2, 3));
    }

    @Test
    public void func_component_evaluation_type_is_not_func() throws Exception {
      var func = intB(3);
      var args = combineB(stringB(), intB());
      var type = callCB(stringTB());
      var hash = hash(hash(type), hash(hash(func), hash(args)));
      assertCall(() -> ((CallB) bytecodeDb().get(hash)).subExprs())
          .throwsException(
              new DecodeExprWrongNodeTypeException(hash, type, "func", FuncTB.class, intTB()));
    }

    @Test
    public void args_is_val_instead_of_combine() throws Exception {
      var funcT = funcTB(stringTB(), intTB(), intTB());
      var func = lambdaB(funcT, intB());
      var type = callCB(intTB());
      var hash = hash(hash(type), hash(hash(func), hash(intB())));
      assertCall(() -> ((CallB) bytecodeDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongNodeClassException(
              hash, type, DATA_PATH + "[1]", CombineB.class, IntB.class));
    }

    @Test
    public void args_component_evaluation_type_is_not_combine_but_different_oper()
        throws Exception {
      var funcT = funcTB(stringTB(), intTB(), intTB());
      var func = lambdaB(funcT, intB());
      var type = callCB(intTB());
      var hash = hash(hash(type), hash(hash(func), hash(varB(1))));
      assertCall(() -> ((CallB) bytecodeDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongNodeClassException(
              hash, type, DATA_PATH + "[1]", CombineB.class, VarB.class));
    }

    @Test
    public void evaluation_type_is_different_than_func_evaluation_type_result() throws Exception {
      var funcT = funcTB(stringTB(), intTB());
      var func = lambdaB(funcT, intB());
      var args = combineB(stringB());
      var type = callCB(stringTB());
      var hash = hash(hash(type), hash(hash(func), hash(args)));
      assertCall(() -> ((CallB) bytecodeDb().get(hash)).subExprs())
          .throwsException(
              new DecodeExprWrongNodeTypeException(hash, type, "call.result", stringTB(), intTB()));
    }

    @Test
    public void func_evaluation_type_params_does_not_match_args_evaluation_types()
        throws Exception {
      var funcT = funcTB(stringTB(), boolTB(), intTB());
      var func = lambdaB(funcT, intB());
      var args = combineB(stringB(), intB());
      var spec = callCB(intTB());
      var hash = hash(hash(spec), hash(hash(func), hash(args)));
      assertCall(() -> ((CallB) bytecodeDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongNodeTypeException(
              hash, spec, "args", tupleTB(stringTB(), boolTB()), tupleTB(stringTB(), intTB())));
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
      var hash = hash(hash(combineCB(intTB(), stringTB())), hash(hash(expr1), hash(expr2)));
      var items = ((CombineB) bytecodeDb().get(hash)).items();
      assertThat(items).containsExactly(expr1, expr2).inOrder();
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(combineCB());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var item1 = intB(1);
      var item2 = stringB("abc");
      var dataHash = hash(hash(item1), hash(item2));
      obj_root_with_two_data_hashes(
          orderCB(), dataHash, (Hash hash) -> ((CombineB) bytecodeDb().get(hash)).subExprs());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          combineCB(), (Hash hash) -> ((CombineB) bytecodeDb().get(hash)).subExprs());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_chain_size_different_than_multiple_of_hash_size(int byteCount)
        throws Exception {
      var notHashOfChain = hash(ByteString.of(new byte[byteCount]));
      var hash = hash(hash(combineCB()), notHashOfChain);
      assertCall(() -> ((CombineB) bytecodeDb().get(hash)).subExprs())
          .throwsException(new DecodeExprNodeException(hash, combineCB(), DATA_PATH))
          .withCause(
              new DecodeHashChainException(notHashOfChain, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_chain_element_pointing_nowhere() throws Exception {
      var nowhere = Hash.of(33);
      var hash = hash(hash(combineCB()), hash(nowhere));
      assertCall(() -> ((CombineB) bytecodeDb().get(hash)).subExprs())
          .throwsException(new DecodeExprNodeException(hash, combineCB(), DATA_PATH + "[0]"))
          .withCause(new DecodeExprNoSuchExprException(nowhere));
    }

    @Test
    public void evaluation_type_items_size_is_different_than_actual_items_size() throws Exception {
      var item1 = intB();
      var type = combineCB(intTB(), stringTB());
      var hash = hash(hash(type), hash(hash(item1)));

      assertCall(() -> ((CombineB) bytecodeDb().get(hash)).subExprs())
          .throwsException(new DecodeCombineWrongElementsSizeException(hash, type, 1));
    }

    @Test
    public void evaluation_type_item_is_different_than_evaluation_type_of_one_of_items()
        throws Exception {
      var item1 = intB(1);
      var item2 = stringB("abc");
      var type = combineCB(intTB(), boolTB());
      var hash = hash(hash(type), hash(hash(item1), hash(item2)));

      assertCall(() -> ((CombineB) bytecodeDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongNodeTypeException(
              hash, type, "elements", 1, boolTB(), stringTB()));
    }
  }

  @Nested
  class _expression_func {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save
       * expression function in HashedDb.
       */
      var body = boolB(true);
      var cat = lambdaCB(intTB(), stringTB(), boolTB());
      var hash = hash(hash(cat), hash(body));
      assertThat(((LambdaB) bytecodeDb().get(hash)).body()).isEqualTo(body);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(lambdaCB());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var bodyExpr = boolB(true);
      var cat = lambdaCB(intTB(), stringTB(), boolTB());
      var dataHash = hash(bodyExpr);
      obj_root_with_two_data_hashes(
          cat, dataHash, (Hash hash) -> ((LambdaB) bytecodeDb().get(hash)).body());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_expr_but_nowhere(
          lambdaCB(), (Hash hash) -> ((LambdaB) bytecodeDb().get(hash)).body());
    }

    @Test
    public void body_evaluation_type_is_not_equal_func_type_result() throws Exception {
      var body = intB(17);
      var cat = lambdaCB(intTB(), stringTB(), boolTB());
      var hash = hash(hash(cat), hash(body));
      assertCall(() -> ((LambdaB) bytecodeDb().get(hash)).body())
          .throwsException(
              new DecodeExprWrongNodeTypeException(hash, cat, DATA_PATH, boolTB(), intTB()));
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
      var hash = hash(hash(ifFuncCB(intTB())));
      assertThat(hash).isEqualTo(ifFuncB(intTB()).hash());
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
      var byteString = ByteString.of((byte) 3, (byte) 2);
      var hash = hash(hash(intTB()), hash(byteString));
      assertThat(((IntB) bytecodeDb().get(hash)).toJ()).isEqualTo(BigInteger.valueOf(3 * 256 + 2));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(intTB());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(intTB(), hashedDb().writeByte((byte) 1), (Hash hash) -> ((IntB)
              bytecodeDb().get(hash))
          .toJ());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          intTB(), (Hash hash) -> ((IntB) bytecodeDb().get(hash)).toJ());
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
      var hash = hash(hash(mapFuncCB(intTB(), stringTB())));
      assertThat(hash).isEqualTo(mapFuncB(intTB(), stringTB()).hash());
    }

    @Test
    public void root_with_data_hash() throws Exception {
      obj_root_with_data_hash(mapFuncCB(intTB(), stringTB()));
    }
  }

  @Nested
  class _native_func {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save
       * Method in HashedDb.
       */
      var category = nativeFuncCB(intTB(), stringTB());
      var jar = blobB();
      var classBinaryName = stringB();
      var isPure = boolB(true);
      var hash = hash(hash(category), hash(hash(jar), hash(classBinaryName), hash(isPure)));

      assertThat(((NativeFuncB) bytecodeDb().get(hash)).jar()).isEqualTo(jar);
      assertThat(((NativeFuncB) bytecodeDb().get(hash)).classBinaryName())
          .isEqualTo(classBinaryName);
      assertThat(((NativeFuncB) bytecodeDb().get(hash)).isPure()).isEqualTo(isPure);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(funcTB(intTB(), stringTB()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var category = nativeFuncCB(intTB(), stringTB());
      var jar = blobB();
      var classBinaryName = stringB();
      var isPure = boolB(true);
      var dataHash = hash(hash(jar), hash(classBinaryName), hash(isPure));
      obj_root_with_two_data_hashes(
          category, dataHash, (Hash hash) -> ((NativeFuncB) bytecodeDb().get(hash))
              .classBinaryName());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      var category = nativeFuncCB(intTB(), stringTB());
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          category, (Hash hash) -> ((NativeFuncB) bytecodeDb().get(hash)).classBinaryName());
    }

    @Test
    public void data_is_chain_with_two_elements() throws Exception {
      var category = nativeFuncCB(intTB(), stringTB());
      var jar = blobB();
      var classBinaryName = stringB();
      var dataHash = hash(hash(jar), hash(classBinaryName));
      var hash = hash(hash(category), dataHash);

      assertCall(() -> ((NativeFuncB) bytecodeDb().get(hash)).classBinaryName())
          .throwsException(new DecodeExprWrongChainSizeException(hash, category, DATA_PATH, 3, 2));
    }

    @Test
    public void data_is_chain_with_four_elements() throws Exception {
      var type = nativeFuncCB(intTB(), stringTB());
      var jar = blobB();
      var classBinaryName = stringB();
      var isPure = boolB(true);
      var dataHash = hash(hash(jar), hash(classBinaryName), hash(isPure), hash(isPure));
      var hash = hash(hash(type), dataHash);

      assertCall(() -> ((NativeFuncB) bytecodeDb().get(hash)).classBinaryName())
          .throwsException(new DecodeExprWrongChainSizeException(hash, type, DATA_PATH, 3, 4));
    }

    @Test
    public void jar_file_is_not_blob_value() throws Exception {
      var category = nativeFuncCB(intTB(), stringTB());
      var jar = stringB();
      var classBinaryName = stringB();
      var isPure = boolB(true);
      var hash = hash(hash(category), hash(hash(jar), hash(classBinaryName), hash(isPure)));
      assertCall(() -> ((NativeFuncB) bytecodeDb().get(hash)).jar())
          .throwsException(new DecodeExprWrongNodeClassException(
              hash, category, DATA_PATH + "[0]", BlobB.class, StringB.class));
    }

    @Test
    public void class_binary_name_is_not_string_value() throws Exception {
      var category = nativeFuncCB(intTB(), stringTB());
      var jar = blobB();
      var classBinaryName = intB();
      var isPure = boolB(true);
      var hash = hash(hash(category), hash(hash(jar), hash(classBinaryName), hash(isPure)));

      assertCall(() -> ((NativeFuncB) bytecodeDb().get(hash)).classBinaryName())
          .throwsException(new DecodeExprWrongNodeClassException(
              hash, category, DATA_PATH + "[1]", StringB.class, IntB.class));
    }

    @Test
    public void is_pure_is_not_bool_value() throws Exception {
      var category = nativeFuncCB(intTB(), stringTB());
      var jar = blobB();
      var classBinaryName = stringB();
      var isPure = stringB();
      var hash = hash(hash(category), hash(hash(jar), hash(classBinaryName), hash(isPure)));

      assertCall(() -> ((NativeFuncB) bytecodeDb().get(hash)).isPure())
          .throwsException(new DecodeExprWrongNodeClassException(
              hash, category, DATA_PATH + "[2]", BoolB.class, StringB.class));
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
      var hash = hash(hash(orderCB(intTB())), hash(hash(expr1), hash(expr2)));
      var elems = ((OrderB) bytecodeDb().get(hash)).elements();
      assertThat(elems).containsExactly(expr1, expr2).inOrder();
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(orderCB());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var expr1 = intB(1);
      var expr2 = intB(2);
      var dataHash = hash(hash(expr1), hash(expr2));
      obj_root_with_two_data_hashes(
          orderCB(), dataHash, (Hash hash) -> ((OrderB) bytecodeDb().get(hash)).subExprs());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          orderCB(), (Hash hash) -> ((OrderB) bytecodeDb().get(hash)).subExprs());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_chain_size_different_than_multiple_of_hash_size(int byteCount)
        throws Exception {
      var notHashOfChain = hash(ByteString.of(new byte[byteCount]));
      var hash = hash(hash(orderCB()), notHashOfChain);
      assertCall(() -> ((OrderB) bytecodeDb().get(hash)).subExprs())
          .throwsException(new DecodeExprNodeException(hash, orderCB(), DATA_PATH))
          .withCause(
              new DecodeHashChainException(notHashOfChain, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_chain_elem_pointing_nowhere() throws Exception {
      var nowhereHash = Hash.of(33);
      var hash = hash(hash(orderCB()), hash(nowhereHash));
      assertCall(() -> ((OrderB) bytecodeDb().get(hash)).subExprs())
          .throwsException(new DecodeExprNodeException(hash, orderCB(), DATA_PATH + "[0]"))
          .withCause(new DecodeExprNoSuchExprException(nowhereHash));
    }

    @Test
    public void evaluation_type_elem_is_different_than_evaluation_type_of_one_of_elems()
        throws Exception {
      var expr1 = intB();
      var expr2 = stringB();
      var type = orderCB(intTB());
      var hash = hash(hash(type), hash(hash(expr1), hash(expr2)));
      assertCall(() -> ((OrderB) bytecodeDb().get(hash)).subExprs())
          .throwsException(
              new DecodeExprWrongNodeTypeException(hash, type, "elements[1]", intTB(), stringTB()));
    }
  }

  @Nested
  class _pick {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save smooth
       * pick in HashedDb.
       */
      var pickable = orderB(stringB("abc"));
      var index = varB(intTB(), 7);
      var hash = hash(hash(pickCB(stringTB())), hash(hash(pickable), hash(index)));
      assertThat(((PickB) bytecodeDb().get(hash)).subExprs())
          .isEqualTo(new PickSubExprsB(pickable, index));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(pickCB(intTB()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var index = intB(2);
      var expr = intB(123);
      var dataHash = hash(hash(expr), hash(index));
      obj_root_with_two_data_hashes(
          pickCB(), dataHash, (Hash hash) -> ((PickB) bytecodeDb().get(hash)).subExprs());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          pickCB(), (Hash hash) -> ((PickB) bytecodeDb().get(hash)).subExprs());
    }

    @Test
    public void data_is_chain_with_one_element() throws Exception {
      var expr = intB(123);
      var dataHash = hash(hash(expr));
      var hash = hash(hash(pickCB()), dataHash);
      assertCall(() -> ((PickB) bytecodeDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongChainSizeException(hash, pickCB(), DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_chain_with_three_elements() throws Exception {
      var index = intB(2);
      var expr = intB(123);
      var dataHash = hash(hash(expr), hash(index), hash(index));
      var hash = hash(hash(pickCB()), dataHash);
      assertCall(() -> ((PickB) bytecodeDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongChainSizeException(hash, pickCB(), DATA_PATH, 2, 3));
    }

    @Test
    public void array_is_not_array_expr() throws Exception {
      var array = intB(3);
      var index = intB(0);
      var type = pickCB(stringTB());
      var hash = hash(hash(type), hash(hash(array), hash(index)));

      assertCall(() -> ((PickB) bytecodeDb().get(hash)).subExprs())
          .throwsException(
              new DecodeExprWrongNodeTypeException(hash, type, "array", ArrayTB.class, intTB()));
    }

    @Test
    public void index_is_not_int_expr() throws Exception {
      var type = pickCB(stringTB());
      var pickable = arrayB(stringB("abc"));
      var index = varB(stringTB(), 7);
      var hash = hash(hash(type), hash(hash(pickable), hash(index)));
      assertCall(() -> ((PickB) bytecodeDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongNodeTypeException(
              hash, type, DATA_PATH, 1, IntB.class, stringTB()));
    }

    @Test
    public void evaluation_type_is_different_than_elem_type() throws Exception {
      var tuple = arrayB(stringB("abc"));
      var index = intB(0);
      var type = pickCB(intTB());
      var hash = hash(hash(type), hash(hash(tuple), hash(index)));

      assertCall(() -> ((PickB) bytecodeDb().get(hash)).subExprs())
          .throwsException(new DecodePickWrongEvaluationTypeException(hash, type, stringTB()));
    }
  }

  @Nested
  class _variable {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save variable
       * in HashedDb.
       */
      var index = intB(34);
      var hash = hash(hash(varCB(stringTB())), hash(index));
      assertThat(((VarB) bytecodeDb().get(hash)).index()).isEqualTo(index);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(varCB());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var index = intB(0);
      var dataHash = hash(index);
      obj_root_with_two_data_hashes(
          varCB(intTB()), dataHash, (Hash hash) -> ((VarB) bytecodeDb().get(hash)).index());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_expr_but_nowhere(
          varCB(intTB()), (Hash hash) -> ((VarB) bytecodeDb().get(hash)).index());
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
      var tuple = tupleB(stringB("abc"));
      var selectable = (ValueB) tuple;
      var index = intB(0);
      var hash = hash(hash(selectCB(stringTB())), hash(hash(selectable), hash(index)));
      assertThat(((SelectB) bytecodeDb().get(hash)).subExprs())
          .isEqualTo(new SelectSubExprsB(selectable, index));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(selectCB(intTB()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var index = intB(2);
      var expr = intB(123);
      var dataHash = hash(hash(expr), hash(index));
      obj_root_with_two_data_hashes(
          selectCB(), dataHash, (Hash hash) -> ((SelectB) bytecodeDb().get(hash)).subExprs());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          selectCB(), (Hash hash) -> ((SelectB) bytecodeDb().get(hash)).subExprs());
    }

    @Test
    public void data_is_chain_with_one_element() throws Exception {
      var expr = intB(123);
      var dataHash = hash(hash(expr));
      var hash = hash(hash(selectCB()), dataHash);
      assertCall(() -> ((SelectB) bytecodeDb().get(hash)).subExprs())
          .throwsException(
              new DecodeExprWrongChainSizeException(hash, selectCB(), DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_chain_with_three_element() throws Exception {
      var index = intB(2);
      var expr = intB(123);
      var dataHash = hash(hash(expr), hash(index), hash(index));
      var hash = hash(hash(selectCB()), dataHash);
      assertCall(() -> ((SelectB) bytecodeDb().get(hash)).subExprs())
          .throwsException(
              new DecodeExprWrongChainSizeException(hash, selectCB(), DATA_PATH, 2, 3));
    }

    @Test
    public void tuple_is_not_tuple_expr() throws Exception {
      var expr = intB(3);
      var index = intB(0);
      var type = selectCB(stringTB());
      var hash = hash(hash(type), hash(hash(expr), hash(index)));

      assertCall(() -> ((SelectB) bytecodeDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongNodeClassException(
              hash, type, "tuple", TupleTB.class, IntTB.class));
    }

    @Test
    public void index_is_out_of_bounds() throws Exception {
      var tuple = tupleB(stringB("abc"));
      var index = intB(1);
      var type = selectCB(stringTB());
      var hash = hash(hash(type), hash(hash(tuple), hash(index)));

      assertCall(() -> ((SelectB) bytecodeDb().get(hash)).subExprs())
          .throwsException(new DecodeSelectIndexOutOfBoundsException(hash, type, 1, 1));
    }

    @Test
    public void evaluation_type_is_different_than_type_of_item_pointed_to_by_index()
        throws Exception {
      var tuple = tupleB(stringB("abc"));
      var index = intB(0);
      var type = selectCB(intTB());
      var hash = hash(hash(type), hash(hash(tuple), hash(index)));

      assertCall(() -> ((SelectB) bytecodeDb().get(hash)).subExprs())
          .throwsException(new DecodeSelectWrongEvaluationTypeException(hash, type, stringTB()));
    }

    @Test
    public void index_is_string_instead_of_int() throws Exception {
      var type = selectCB(stringTB());
      var tuple = tupleB(stringB("abc"));
      var strVal = stringB("abc");
      var hash = hash(hash(type), hash(hash(tuple), hash(strVal)));
      assertCall(() -> ((SelectB) bytecodeDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongNodeClassException(
              hash, type, DATA_PATH + "[1]", IntB.class, StringB.class));
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
      var hash = hash(hash(stringTB()), hash("aaa"));
      assertThat(((StringB) bytecodeDb().get(hash)).toJ()).isEqualTo("aaa");
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
          (Hash hash) -> ((StringB) bytecodeDb().get(hash)).toJ());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          stringTB(), (Hash hash) -> ((StringB) bytecodeDb().get(hash)).toJ());
    }

    @Test
    public void data_being_invalid_utf8_chain() throws Exception {
      var notStringHash = hash(illegalString());
      var hash = hash(hash(stringTB()), notStringHash);
      assertCall(() -> ((StringB) bytecodeDb().get(hash)).toJ())
          .throwsException(new DecodeExprNodeException(hash, stringTB(), DATA_PATH))
          .withCause(new DecodeStringException(notStringHash, null));
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
      assertThat(hash(hash(personTB()), hash(hash(stringB("John")), hash(stringB("Doe")))))
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
          (Hash hash) -> ((TupleB) bytecodeDb().get(hash)).get(0));
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          personTB(), (Hash hash) -> ((TupleB) bytecodeDb().get(hash)).get(0));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_chain_size_different_than_multiple_of_hash_size(int byteCount)
        throws Exception {
      var notChainHash = hash(ByteString.of(new byte[byteCount]));
      var hash = hash(hash(personTB()), notChainHash);
      assertCall(() -> ((TupleB) bytecodeDb().get(hash)).get(0))
          .throwsException(new DecodeExprNodeException(hash, personTB(), DATA_PATH))
          .withCause(new DecodeHashChainException(notChainHash, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_chain_element_pointing_nowhere() throws Exception {
      var nowhereHash = Hash.of(33);
      var dataHash = hash(nowhereHash, nowhereHash);
      var hash = hash(hash(personTB()), dataHash);
      assertCall(() -> ((TupleB) bytecodeDb().get(hash)).get(0))
          .throwsException(new DecodeExprNodeException(hash, personTB(), DATA_PATH + "[0]"))
          .withCause(new DecodeExprNoSuchExprException(nowhereHash));
    }

    @Test
    public void with_too_few_elements() throws Exception {
      var dataHash = hash(hash(stringB("John")));
      var hash = hash(hash(personTB()), dataHash);
      TupleB tuple = (TupleB) bytecodeDb().get(hash);
      assertCall(() -> tuple.get(0))
          .throwsException(
              new DecodeExprWrongChainSizeException(hash, personTB(), DATA_PATH, 2, 1));
    }

    @Test
    public void with_too_many_elements() throws Exception {
      var dataHash = hash(hash(stringB("John")), hash(stringB("Doe")), hash(stringB("junk")));
      var hash = hash(hash(personTB()), dataHash);
      var tuple = (TupleB) bytecodeDb().get(hash);
      assertCall(() -> tuple.get(0))
          .throwsException(
              new DecodeExprWrongChainSizeException(hash, personTB(), DATA_PATH, 2, 3));
    }

    @Test
    public void with_element_of_wrong_type() throws Exception {
      var hash = hash(hash(personTB()), hash(hash(stringB("John")), hash(boolB(true))));
      var tuple = (TupleB) bytecodeDb().get(hash);
      assertCall(() -> tuple.get(0))
          .throwsException(new DecodeExprWrongNodeTypeException(
              hash, personTB(), DATA_PATH, personTB(), "`{String,Bool}`"));
    }

    @Test
    public void with_element_being_oper() throws Exception {
      var hash = hash(hash(personTB()), hash(hash(stringB("John")), hash(varB(1))));
      var tuple = (TupleB) bytecodeDb().get(hash);
      assertCall(() -> tuple.get(0))
          .throwsException(new DecodeExprWrongNodeClassException(
              hash, personTB(), DATA_PATH + "[1]", ValueB.class, VarB.class));
    }
  }

  private void obj_root_without_data_hash(CategoryB cat) throws HashedDbException {
    var hash = hash(hash(cat));
    assertCall(() -> bytecodeDb().get(hash))
        .throwsException(wrongSizeOfRootChainException(hash, cat, 1));
  }

  private void obj_root_with_data_hash(CategoryB category) throws HashedDbException {
    var hash = hash(hash(category), hash(category));
    assertCall(() -> bytecodeDb().get(hash))
        .throwsException(wrongSizeOfRootChainException(hash, category, 2));
  }

  private void obj_root_with_two_data_hashes(
      CategoryB type, Hash dataHash, Function1<Hash, ?, BytecodeException> factory)
      throws HashedDbException {
    var hash = hash(hash(type), dataHash, dataHash);
    assertCall(() -> factory.apply(hash))
        .throwsException(DecodeExprRootException.wrongSizeOfRootChainException(hash, 3));
  }

  private void obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
      CategoryB category, Consumer1<Hash, BytecodeException> factory) throws HashedDbException {
    var dataHash = Hash.of(33);
    var hash = hash(hash(category), dataHash);
    assertCall(() -> factory.accept(hash))
        .throwsException(new DecodeExprNodeException(hash, category, DATA_PATH))
        .withCause(new NoSuchDataException(dataHash));
  }

  private void obj_root_with_data_hash_not_pointing_to_expr_but_nowhere(
      CategoryB category, Consumer1<Hash, BytecodeException> factory) throws HashedDbException {
    var dataHash = Hash.of(33);
    var hash = hash(hash(category), dataHash);
    assertCall(() -> factory.accept(hash))
        .throwsException(new DecodeExprNodeException(hash, category, DATA_PATH))
        .withCause(new DecodeExprNoSuchExprException(dataHash));
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

  protected Hash hash(String string) throws HashedDbException {
    return hashedDb().writeString(string);
  }

  protected Hash hash(boolean value) throws IOException, HashedDbException {
    return hash((byte) (value ? 1 : 0));
  }

  protected Hash hash(byte value) throws IOException, HashedDbException {
    return hashedDb().writeData(sink -> sink.writeByte(value));
  }

  protected Hash hash(ByteString bytes) throws IOException, HashedDbException {
    return hashedDb().writeData(sink -> sink.write(bytes));
  }

  protected Hash hash(ExprB expr) {
    return expr.hash();
  }

  protected Hash hash(CategoryB type) {
    return type.hash();
  }

  protected Hash hash(Hash... hashes) throws HashedDbException {
    return hashedDb().writeHashChain(hashes);
  }
}
