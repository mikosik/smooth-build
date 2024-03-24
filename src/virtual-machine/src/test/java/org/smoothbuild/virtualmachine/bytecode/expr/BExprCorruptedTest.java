package org.smoothbuild.virtualmachine.bytecode.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.testing.TestingString.illegalString;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.virtualmachine.bytecode.expr.BExpr.DATA_PATH;
import static org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprRootException.cannotReadRootException;
import static org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprRootException.wrongSizeOfRootChainException;

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
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeCombineWrongElementsSizeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprKindException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprNoSuchExprException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprNodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongChainSizeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeClassException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodePickWrongEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeSelectIndexOutOfBoundsException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeSelectWrongEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BReference;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BNativeFunc;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.DecodeBooleanException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.DecodeByteException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.DecodeHashChainException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.DecodeStringException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.NoSuchDataException;
import org.smoothbuild.virtualmachine.bytecode.type.BKind;
import org.smoothbuild.virtualmachine.bytecode.type.exc.DecodeKindException;
import org.smoothbuild.virtualmachine.bytecode.type.value.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BFuncType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BIntType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BTupleType;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class BExprCorruptedTest extends TestingVirtualMachine {
  @Nested
  class _expr {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save value
       * in HashedDb.
       */
      var hash = hash(hash(stringTB()), hash("aaa"));
      assertThat(((BString) exprDb().get(hash)).toJavaString()).isEqualTo("aaa");
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void merkle_root_byte_count_is_not_multiple_of_hash_size(int byteCount)
        throws IOException, HashedDbException {
      var hash = hash(ByteString.of(new byte[byteCount]));
      assertCall(() -> exprDb().get(hash))
          .throwsException(cannotReadRootException(hash, null))
          .withCause(new DecodeHashChainException(hash, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void corrupted_type() throws Exception {
      var typeHash = Hash.of("not a type");
      var hash = hash(typeHash, hash("aaa"));
      assertCall(() -> exprDb().get(hash))
          .throwsException(new DecodeExprKindException(hash))
          .withCause(new DecodeKindException(typeHash));
    }

    @Test
    public void reading_elements_from_not_stored_object_throws_exception() {
      var hash = Hash.of(33);
      assertCall(() -> exprDb().get(hash))
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
          ((BArray) exprDb().get(hash)).elements(BString.class).map(BString::toJavaString);
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
          (Hash hash) -> ((BArray) exprDb().get(hash)).elements(BInt.class));
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          arrayTB(intTB()), (Hash hash) -> ((BArray) exprDb().get(hash)).elements(BInt.class));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_chain_size_different_than_multiple_of_hash_size(int byteCount)
        throws Exception {
      var notHashOfChain = hash(ByteString.of(new byte[byteCount]));
      var type = arrayTB(stringTB());
      var hash = hash(hash(type), notHashOfChain);
      assertCall(() -> ((BArray) exprDb().get(hash)).elements(BValue.class))
          .throwsException(new DecodeExprNodeException(hash, type, DATA_PATH))
          .withCause(
              new DecodeHashChainException(notHashOfChain, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_chain_element_pointing_nowhere() throws Exception {
      var nowhereHash = Hash.of(33);
      var dataHash = hash(nowhereHash);
      var arrayType = arrayTB(stringTB());
      var hash = hash(hash(arrayType), dataHash);
      assertCall(() -> ((BArray) exprDb().get(hash)).elements(BString.class))
          .throwsException(new DecodeExprNodeException(hash, arrayType, DATA_PATH + "[0]"))
          .withCause(new DecodeExprNoSuchExprException(nowhereHash));
    }

    @Test
    public void with_one_elem_of_wrong_type() throws Exception {
      var arrayType = arrayTB(stringTB());
      var hash = hash(
          hash(arrayType),
          hash(hash(hash(stringTB()), hash("aaa")), hash(hash(boolTB()), hash(true))));
      assertCall(() -> ((BArray) exprDb().get(hash)).elements(BString.class))
          .throwsException(new DecodeExprWrongNodeTypeException(
              hash, arrayType, DATA_PATH, 1, stringTB(), boolTB()));
    }

    @Test
    public void with_one_elem_being_oper() throws Exception {
      var arrayType = arrayTB(stringTB());
      var hash =
          hash(hash(arrayType), hash(hash(hash(stringTB()), hash("aaa")), hash(referenceB(1))));
      assertCall(() -> ((BArray) exprDb().get(hash)).elements(BString.class))
          .throwsException(new DecodeExprWrongNodeClassException(
              hash, arrayType, DATA_PATH, 1, BValue.class, BReference.class));
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
      try (var source = ((BBlob) exprDb().get(hash)).source()) {
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
          (Hash hash) -> ((BBlob) exprDb().get(hash)).source());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          blobTB(), (Hash hash) -> ((BBlob) exprDb().get(hash)).source());
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
      assertThat(((BBool) exprDb().get(hash)).toJavaBoolean()).isEqualTo(value);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(boolTB());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(boolTB(), hashedDb().writeBoolean(true), (Hash hash) -> ((BBool)
              exprDb().get(hash))
          .toJavaBoolean());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          boolTB(), (Hash hash) -> ((BBool) exprDb().get(hash)).toJavaBoolean());
    }

    @Test
    public void empty_bytes_as_data() throws Exception {
      var dataHash = hash(ByteString.of());
      var hash = hash(hash(boolTB()), dataHash);
      assertCall(() -> ((BBool) exprDb().get(hash)).toJavaBoolean())
          .throwsException(new DecodeExprNodeException(hash, boolTB(), DATA_PATH))
          .withCause(new DecodeBooleanException(dataHash, new DecodeByteException(dataHash)));
    }

    @Test
    public void more_than_one_byte_as_data() throws Exception {
      var dataHash = hash(ByteString.of((byte) 0, (byte) 0));
      var hash = hash(hash(boolTB()), dataHash);
      assertCall(() -> ((BBool) exprDb().get(hash)).toJavaBoolean())
          .throwsException(new DecodeExprNodeException(hash, boolTB(), DATA_PATH))
          .withCause(new DecodeBooleanException(dataHash, new DecodeByteException(dataHash)));
    }

    @ParameterizedTest
    @ArgumentsSource(AllByteValuesExceptZeroAndOneProvider.class)
    public void one_byte_data_not_equal_zero_nor_one(byte value) throws Exception {
      var dataHash = hash(ByteString.of(value));
      var hash = hash(hash(boolTB()), dataHash);
      assertCall(() -> ((BBool) exprDb().get(hash)).toJavaBoolean())
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
      var funcType = funcTB(stringTB(), intTB(), intTB());
      var func = lambdaB(funcType, intB());
      var args = combineB(stringB(), intB());
      var hash = hash(hash(callCB(intTB())), hash(hash(func), hash(args)));

      assertThat(((BCall) exprDb().get(hash)).subExprs())
          .isEqualTo(new BCall.SubExprsB(func, args));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(callCB(intTB()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var funcType = funcTB(stringTB(), intTB(), intTB());
      var func = lambdaB(funcType, intB());
      var args = combineB(stringB(), intB());
      var dataHash = hash(hash(func), hash(args));
      obj_root_with_two_data_hashes(
          callCB(intTB()), dataHash, (Hash hash) -> ((BCall) exprDb().get(hash)).subExprs());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          callCB(intTB()), (Hash hash) -> ((BCall) exprDb().get(hash)).subExprs());
    }

    @Test
    public void data_is_chain_with_one_elem() throws Exception {
      var funcType = funcTB(stringTB(), intTB(), intTB());
      var func = lambdaB(funcType, intB());
      var dataHash = hash(hash(func));
      var kind = callCB(intTB());
      var hash = hash(hash(kind), dataHash);
      assertCall(() -> ((BCall) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongChainSizeException(hash, kind, DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_chain_with_three_elements() throws Exception {
      var funcType = funcTB(stringTB(), intTB(), intTB());
      var func = lambdaB(funcType, intB());
      var args = combineB(stringB(), intB());
      var dataHash = hash(hash(func), hash(args), hash(args));
      var kind = callCB(intTB());
      var hash = hash(hash(kind), dataHash);
      assertCall(() -> ((BCall) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongChainSizeException(hash, kind, DATA_PATH, 2, 3));
    }

    @Test
    public void func_component_evaluation_type_is_not_func() throws Exception {
      var func = intB(3);
      var args = combineB(stringB(), intB());
      var type = callCB(stringTB());
      var hash = hash(hash(type), hash(hash(func), hash(args)));
      assertCall(() -> ((BCall) exprDb().get(hash)).subExprs())
          .throwsException(
              new DecodeExprWrongNodeTypeException(hash, type, "func", BFuncType.class, intTB()));
    }

    @Test
    public void args_is_val_instead_of_combine() throws Exception {
      var funcType = funcTB(stringTB(), intTB(), intTB());
      var func = lambdaB(funcType, intB());
      var type = callCB(intTB());
      var hash = hash(hash(type), hash(hash(func), hash(intB())));
      assertCall(() -> ((BCall) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongNodeClassException(
              hash, type, DATA_PATH + "[1]", BCombine.class, BInt.class));
    }

    @Test
    public void args_component_evaluation_type_is_not_combine_but_different_oper()
        throws Exception {
      var funcType = funcTB(stringTB(), intTB(), intTB());
      var func = lambdaB(funcType, intB());
      var type = callCB(intTB());
      var hash = hash(hash(type), hash(hash(func), hash(referenceB(1))));
      assertCall(() -> ((BCall) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongNodeClassException(
              hash, type, DATA_PATH + "[1]", BCombine.class, BReference.class));
    }

    @Test
    public void evaluation_type_is_different_than_func_evaluation_type_result() throws Exception {
      var funcType = funcTB(stringTB(), intTB());
      var func = lambdaB(funcType, intB());
      var args = combineB(stringB());
      var type = callCB(stringTB());
      var hash = hash(hash(type), hash(hash(func), hash(args)));
      assertCall(() -> ((BCall) exprDb().get(hash)).subExprs())
          .throwsException(
              new DecodeExprWrongNodeTypeException(hash, type, "call.result", stringTB(), intTB()));
    }

    @Test
    public void func_evaluation_type_params_does_not_match_args_evaluation_types()
        throws Exception {
      var funcType = funcTB(stringTB(), boolTB(), intTB());
      var func = lambdaB(funcType, intB());
      var args = combineB(stringB(), intB());
      var spec = callCB(intTB());
      var hash = hash(hash(spec), hash(hash(func), hash(args)));
      assertCall(() -> ((BCall) exprDb().get(hash)).subExprs())
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
      var items = ((BCombine) exprDb().get(hash)).items();
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
          orderCB(), dataHash, (Hash hash) -> ((BCombine) exprDb().get(hash)).subExprs());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          combineCB(), (Hash hash) -> ((BCombine) exprDb().get(hash)).subExprs());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_chain_size_different_than_multiple_of_hash_size(int byteCount)
        throws Exception {
      var notHashOfChain = hash(ByteString.of(new byte[byteCount]));
      var hash = hash(hash(combineCB()), notHashOfChain);
      assertCall(() -> ((BCombine) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprNodeException(hash, combineCB(), DATA_PATH))
          .withCause(
              new DecodeHashChainException(notHashOfChain, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_chain_element_pointing_nowhere() throws Exception {
      var nowhere = Hash.of(33);
      var hash = hash(hash(combineCB()), hash(nowhere));
      assertCall(() -> ((BCombine) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprNodeException(hash, combineCB(), DATA_PATH + "[0]"))
          .withCause(new DecodeExprNoSuchExprException(nowhere));
    }

    @Test
    public void evaluation_type_items_size_is_different_than_actual_items_size() throws Exception {
      var item1 = intB();
      var type = combineCB(intTB(), stringTB());
      var hash = hash(hash(type), hash(hash(item1)));

      assertCall(() -> ((BCombine) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeCombineWrongElementsSizeException(hash, type, 1));
    }

    @Test
    public void evaluation_type_item_is_different_than_evaluation_type_of_one_of_items()
        throws Exception {
      var item1 = intB(1);
      var item2 = stringB("abc");
      var type = combineCB(intTB(), boolTB());
      var hash = hash(hash(type), hash(hash(item1), hash(item2)));

      assertCall(() -> ((BCombine) exprDb().get(hash)).subExprs())
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
      var kind = lambdaCB(intTB(), stringTB(), boolTB());
      var hash = hash(hash(kind), hash(body));
      assertThat(((BLambda) exprDb().get(hash)).body()).isEqualTo(body);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(lambdaCB());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var bodyExpr = boolB(true);
      var kind = lambdaCB(intTB(), stringTB(), boolTB());
      var dataHash = hash(bodyExpr);
      obj_root_with_two_data_hashes(
          kind, dataHash, (Hash hash) -> ((BLambda) exprDb().get(hash)).body());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_expr_but_nowhere(
          lambdaCB(), (Hash hash) -> ((BLambda) exprDb().get(hash)).body());
    }

    @Test
    public void body_evaluation_type_is_not_equal_func_type_result() throws Exception {
      var body = intB(17);
      var kind = lambdaCB(intTB(), stringTB(), boolTB());
      var hash = hash(hash(kind), hash(body));
      assertCall(() -> ((BLambda) exprDb().get(hash)).body())
          .throwsException(
              new DecodeExprWrongNodeTypeException(hash, kind, DATA_PATH, boolTB(), intTB()));
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
      assertThat(((BInt) exprDb().get(hash)).toJavaBigInteger())
          .isEqualTo(BigInteger.valueOf(3 * 256 + 2));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(intTB());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(intTB(), hashedDb().writeByte((byte) 1), (Hash hash) -> ((BInt)
              exprDb().get(hash))
          .toJavaBigInteger());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          intTB(), (Hash hash) -> ((BInt) exprDb().get(hash)).toJavaBigInteger());
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
      var kind = nativeFuncCB(intTB(), stringTB());
      var jar = blobB();
      var classBinaryName = stringB();
      var isPure = boolB(true);
      var hash = hash(hash(kind), hash(hash(jar), hash(classBinaryName), hash(isPure)));

      assertThat(((BNativeFunc) exprDb().get(hash)).jar()).isEqualTo(jar);
      assertThat(((BNativeFunc) exprDb().get(hash)).classBinaryName()).isEqualTo(classBinaryName);
      assertThat(((BNativeFunc) exprDb().get(hash)).isPure()).isEqualTo(isPure);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(funcTB(intTB(), stringTB()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var kind = nativeFuncCB(intTB(), stringTB());
      var jar = blobB();
      var classBinaryName = stringB();
      var isPure = boolB(true);
      var dataHash = hash(hash(jar), hash(classBinaryName), hash(isPure));
      obj_root_with_two_data_hashes(
          kind, dataHash, (Hash hash) -> ((BNativeFunc) exprDb().get(hash)).classBinaryName());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      var kind = nativeFuncCB(intTB(), stringTB());
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          kind, (Hash hash) -> ((BNativeFunc) exprDb().get(hash)).classBinaryName());
    }

    @Test
    public void data_is_chain_with_two_elements() throws Exception {
      var kind = nativeFuncCB(intTB(), stringTB());
      var jar = blobB();
      var classBinaryName = stringB();
      var dataHash = hash(hash(jar), hash(classBinaryName));
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BNativeFunc) exprDb().get(hash)).classBinaryName())
          .throwsException(new DecodeExprWrongChainSizeException(hash, kind, DATA_PATH, 3, 2));
    }

    @Test
    public void data_is_chain_with_four_elements() throws Exception {
      var type = nativeFuncCB(intTB(), stringTB());
      var jar = blobB();
      var classBinaryName = stringB();
      var isPure = boolB(true);
      var dataHash = hash(hash(jar), hash(classBinaryName), hash(isPure), hash(isPure));
      var hash = hash(hash(type), dataHash);

      assertCall(() -> ((BNativeFunc) exprDb().get(hash)).classBinaryName())
          .throwsException(new DecodeExprWrongChainSizeException(hash, type, DATA_PATH, 3, 4));
    }

    @Test
    public void jar_file_is_not_blob_value() throws Exception {
      var kind = nativeFuncCB(intTB(), stringTB());
      var jar = stringB();
      var classBinaryName = stringB();
      var isPure = boolB(true);
      var hash = hash(hash(kind), hash(hash(jar), hash(classBinaryName), hash(isPure)));
      assertCall(() -> ((BNativeFunc) exprDb().get(hash)).jar())
          .throwsException(new DecodeExprWrongNodeClassException(
              hash, kind, DATA_PATH + "[0]", BBlob.class, BString.class));
    }

    @Test
    public void class_binary_name_is_not_string_value() throws Exception {
      var kind = nativeFuncCB(intTB(), stringTB());
      var jar = blobB();
      var classBinaryName = intB();
      var isPure = boolB(true);
      var hash = hash(hash(kind), hash(hash(jar), hash(classBinaryName), hash(isPure)));

      assertCall(() -> ((BNativeFunc) exprDb().get(hash)).classBinaryName())
          .throwsException(new DecodeExprWrongNodeClassException(
              hash, kind, DATA_PATH + "[1]", BString.class, BInt.class));
    }

    @Test
    public void is_pure_is_not_bool_value() throws Exception {
      var kind = nativeFuncCB(intTB(), stringTB());
      var jar = blobB();
      var classBinaryName = stringB();
      var isPure = stringB();
      var hash = hash(hash(kind), hash(hash(jar), hash(classBinaryName), hash(isPure)));

      assertCall(() -> ((BNativeFunc) exprDb().get(hash)).isPure())
          .throwsException(new DecodeExprWrongNodeClassException(
              hash, kind, DATA_PATH + "[2]", BBool.class, BString.class));
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
      var elements = ((BOrder) exprDb().get(hash)).elements();
      assertThat(elements).containsExactly(expr1, expr2).inOrder();
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
          orderCB(), dataHash, (Hash hash) -> ((BOrder) exprDb().get(hash)).subExprs());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          orderCB(), (Hash hash) -> ((BOrder) exprDb().get(hash)).subExprs());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_chain_size_different_than_multiple_of_hash_size(int byteCount)
        throws Exception {
      var notHashOfChain = hash(ByteString.of(new byte[byteCount]));
      var hash = hash(hash(orderCB()), notHashOfChain);
      assertCall(() -> ((BOrder) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprNodeException(hash, orderCB(), DATA_PATH))
          .withCause(
              new DecodeHashChainException(notHashOfChain, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_chain_elem_pointing_nowhere() throws Exception {
      var nowhereHash = Hash.of(33);
      var hash = hash(hash(orderCB()), hash(nowhereHash));
      assertCall(() -> ((BOrder) exprDb().get(hash)).subExprs())
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
      assertCall(() -> ((BOrder) exprDb().get(hash)).subExprs())
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
      var index = referenceB(intTB(), 7);
      var hash = hash(hash(pickCB(stringTB())), hash(hash(pickable), hash(index)));
      assertThat(((BPick) exprDb().get(hash)).subExprs())
          .isEqualTo(new BPick.SubExprsB(pickable, index));
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
          pickCB(), dataHash, (Hash hash) -> ((BPick) exprDb().get(hash)).subExprs());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          pickCB(), (Hash hash) -> ((BPick) exprDb().get(hash)).subExprs());
    }

    @Test
    public void data_is_chain_with_one_element() throws Exception {
      var expr = intB(123);
      var dataHash = hash(hash(expr));
      var hash = hash(hash(pickCB()), dataHash);
      assertCall(() -> ((BPick) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongChainSizeException(hash, pickCB(), DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_chain_with_three_elements() throws Exception {
      var index = intB(2);
      var expr = intB(123);
      var dataHash = hash(hash(expr), hash(index), hash(index));
      var hash = hash(hash(pickCB()), dataHash);
      assertCall(() -> ((BPick) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongChainSizeException(hash, pickCB(), DATA_PATH, 2, 3));
    }

    @Test
    public void array_is_not_array_expr() throws Exception {
      var array = intB(3);
      var index = intB(0);
      var type = pickCB(stringTB());
      var hash = hash(hash(type), hash(hash(array), hash(index)));

      assertCall(() -> ((BPick) exprDb().get(hash)).subExprs())
          .throwsException(
              new DecodeExprWrongNodeTypeException(hash, type, "array", BArrayType.class, intTB()));
    }

    @Test
    public void index_is_not_int_expr() throws Exception {
      var type = pickCB(stringTB());
      var pickable = arrayB(stringB("abc"));
      var index = referenceB(stringTB(), 7);
      var hash = hash(hash(type), hash(hash(pickable), hash(index)));
      assertCall(() -> ((BPick) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongNodeTypeException(
              hash, type, DATA_PATH, 1, BInt.class, stringTB()));
    }

    @Test
    public void evaluation_type_is_different_than_elem_type() throws Exception {
      var tuple = arrayB(stringB("abc"));
      var index = intB(0);
      var type = pickCB(intTB());
      var hash = hash(hash(type), hash(hash(tuple), hash(index)));

      assertCall(() -> ((BPick) exprDb().get(hash)).subExprs())
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
      assertThat(((BReference) exprDb().get(hash)).index()).isEqualTo(index);
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
          varCB(intTB()), dataHash, (Hash hash) -> ((BReference) exprDb().get(hash)).index());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_expr_but_nowhere(
          varCB(intTB()), (Hash hash) -> ((BReference) exprDb().get(hash)).index());
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
      var selectable = (BValue) tuple;
      var index = intB(0);
      var hash = hash(hash(selectCB(stringTB())), hash(hash(selectable), hash(index)));
      assertThat(((BSelect) exprDb().get(hash)).subExprs())
          .isEqualTo(new BSelect.SubExprsB(selectable, index));
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
          selectCB(), dataHash, (Hash hash) -> ((BSelect) exprDb().get(hash)).subExprs());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          selectCB(), (Hash hash) -> ((BSelect) exprDb().get(hash)).subExprs());
    }

    @Test
    public void data_is_chain_with_one_element() throws Exception {
      var expr = intB(123);
      var dataHash = hash(hash(expr));
      var hash = hash(hash(selectCB()), dataHash);
      assertCall(() -> ((BSelect) exprDb().get(hash)).subExprs())
          .throwsException(
              new DecodeExprWrongChainSizeException(hash, selectCB(), DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_chain_with_three_element() throws Exception {
      var index = intB(2);
      var expr = intB(123);
      var dataHash = hash(hash(expr), hash(index), hash(index));
      var hash = hash(hash(selectCB()), dataHash);
      assertCall(() -> ((BSelect) exprDb().get(hash)).subExprs())
          .throwsException(
              new DecodeExprWrongChainSizeException(hash, selectCB(), DATA_PATH, 2, 3));
    }

    @Test
    public void tuple_is_not_tuple_expr() throws Exception {
      var expr = intB(3);
      var index = intB(0);
      var type = selectCB(stringTB());
      var hash = hash(hash(type), hash(hash(expr), hash(index)));

      assertCall(() -> ((BSelect) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongNodeClassException(
              hash, type, "tuple", BTupleType.class, BIntType.class));
    }

    @Test
    public void index_is_out_of_bounds() throws Exception {
      var tuple = tupleB(stringB("abc"));
      var index = intB(1);
      var type = selectCB(stringTB());
      var hash = hash(hash(type), hash(hash(tuple), hash(index)));

      assertCall(() -> ((BSelect) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeSelectIndexOutOfBoundsException(hash, type, 1, 1));
    }

    @Test
    public void evaluation_type_is_different_than_type_of_item_pointed_to_by_index()
        throws Exception {
      var tuple = tupleB(stringB("abc"));
      var index = intB(0);
      var type = selectCB(intTB());
      var hash = hash(hash(type), hash(hash(tuple), hash(index)));

      assertCall(() -> ((BSelect) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeSelectWrongEvaluationTypeException(hash, type, stringTB()));
    }

    @Test
    public void index_is_string_instead_of_int() throws Exception {
      var type = selectCB(stringTB());
      var tuple = tupleB(stringB("abc"));
      var string = stringB("abc");
      var hash = hash(hash(type), hash(hash(tuple), hash(string)));
      assertCall(() -> ((BSelect) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongNodeClassException(
              hash, type, DATA_PATH + "[1]", BInt.class, BString.class));
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
      assertThat(((BString) exprDb().get(hash)).toJavaString()).isEqualTo("aaa");
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
          (Hash hash) -> ((BString) exprDb().get(hash)).toJavaString());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          stringTB(), (Hash hash) -> ((BString) exprDb().get(hash)).toJavaString());
    }

    @Test
    public void data_being_invalid_utf8_chain() throws Exception {
      var notStringHash = hash(illegalString());
      var hash = hash(hash(stringTB()), notStringHash);
      assertCall(() -> ((BString) exprDb().get(hash)).toJavaString())
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
          (Hash hash) -> ((BTuple) exprDb().get(hash)).get(0));
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          personTB(), (Hash hash) -> ((BTuple) exprDb().get(hash)).get(0));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_chain_size_different_than_multiple_of_hash_size(int byteCount)
        throws Exception {
      var notChainHash = hash(ByteString.of(new byte[byteCount]));
      var hash = hash(hash(personTB()), notChainHash);
      assertCall(() -> ((BTuple) exprDb().get(hash)).get(0))
          .throwsException(new DecodeExprNodeException(hash, personTB(), DATA_PATH))
          .withCause(new DecodeHashChainException(notChainHash, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_chain_element_pointing_nowhere() throws Exception {
      var nowhereHash = Hash.of(33);
      var dataHash = hash(nowhereHash, nowhereHash);
      var hash = hash(hash(personTB()), dataHash);
      assertCall(() -> ((BTuple) exprDb().get(hash)).get(0))
          .throwsException(new DecodeExprNodeException(hash, personTB(), DATA_PATH + "[0]"))
          .withCause(new DecodeExprNoSuchExprException(nowhereHash));
    }

    @Test
    public void with_too_few_elements() throws Exception {
      var dataHash = hash(hash(stringB("John")));
      var hash = hash(hash(personTB()), dataHash);
      BTuple tuple = (BTuple) exprDb().get(hash);
      assertCall(() -> tuple.get(0))
          .throwsException(
              new DecodeExprWrongChainSizeException(hash, personTB(), DATA_PATH, 2, 1));
    }

    @Test
    public void with_too_many_elements() throws Exception {
      var dataHash = hash(hash(stringB("John")), hash(stringB("Doe")), hash(stringB("junk")));
      var hash = hash(hash(personTB()), dataHash);
      var tuple = (BTuple) exprDb().get(hash);
      assertCall(() -> tuple.get(0))
          .throwsException(
              new DecodeExprWrongChainSizeException(hash, personTB(), DATA_PATH, 2, 3));
    }

    @Test
    public void with_element_of_wrong_type() throws Exception {
      var hash = hash(hash(personTB()), hash(hash(stringB("John")), hash(boolB(true))));
      var tuple = (BTuple) exprDb().get(hash);
      assertCall(() -> tuple.get(0))
          .throwsException(new DecodeExprWrongNodeTypeException(
              hash, personTB(), DATA_PATH, personTB(), "`{String,Bool}`"));
    }

    @Test
    public void with_element_being_oper() throws Exception {
      var hash = hash(hash(personTB()), hash(hash(stringB("John")), hash(referenceB(1))));
      var tuple = (BTuple) exprDb().get(hash);
      assertCall(() -> tuple.get(0))
          .throwsException(new DecodeExprWrongNodeClassException(
              hash, personTB(), DATA_PATH + "[1]", BValue.class, BReference.class));
    }
  }

  private void obj_root_without_data_hash(BKind cat) throws HashedDbException {
    var hash = hash(hash(cat));
    assertCall(() -> exprDb().get(hash))
        .throwsException(wrongSizeOfRootChainException(hash, cat, 1));
  }

  private void obj_root_with_data_hash(BKind kind) throws HashedDbException {
    var hash = hash(hash(kind), hash(kind));
    assertCall(() -> exprDb().get(hash))
        .throwsException(wrongSizeOfRootChainException(hash, kind, 2));
  }

  private void obj_root_with_two_data_hashes(
      BKind type, Hash dataHash, Function1<Hash, ?, BytecodeException> factory)
      throws HashedDbException {
    var hash = hash(hash(type), dataHash, dataHash);
    assertCall(() -> factory.apply(hash)).throwsException(wrongSizeOfRootChainException(hash, 3));
  }

  private void obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
      BKind kind, Consumer1<Hash, BytecodeException> factory) throws HashedDbException {
    var dataHash = Hash.of(33);
    var hash = hash(hash(kind), dataHash);
    assertCall(() -> factory.accept(hash))
        .throwsException(new DecodeExprNodeException(hash, kind, DATA_PATH))
        .withCause(new NoSuchDataException(dataHash));
  }

  private void obj_root_with_data_hash_not_pointing_to_expr_but_nowhere(
      BKind kind, Consumer1<Hash, BytecodeException> factory) throws HashedDbException {
    var dataHash = Hash.of(33);
    var hash = hash(hash(kind), dataHash);
    assertCall(() -> factory.accept(hash))
        .throwsException(new DecodeExprNodeException(hash, kind, DATA_PATH))
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

  protected Hash hash(BExpr expr) {
    return expr.hash();
  }

  protected Hash hash(BKind type) {
    return type.hash();
  }

  protected Hash hash(Hash... hashes) throws HashedDbException {
    return hashedDb().writeHashChain(hashes);
  }
}
