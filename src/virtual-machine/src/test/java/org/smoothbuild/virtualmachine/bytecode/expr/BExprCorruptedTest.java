package org.smoothbuild.virtualmachine.bytecode.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.testing.TestingString.illegalString;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr.DATA_PATH;
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
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCall;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BIf;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMap;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BReference;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeCombineWrongElementsSizeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprKindException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprNoSuchExprException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprNodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongChainSizeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongMemberEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongMemberTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeClassException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeSelectIndexOutOfBoundsException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeSelectWrongEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.DecodeBooleanException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.DecodeByteException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.DecodeHashChainException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.DecodeStringException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.NoSuchDataException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BIntType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.kind.exc.DecodeKindException;
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
      var hash = hash(hash(bStringType()), hash("aaa"));
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
          hash(bArrayType(bStringType())),
          hash(hash(hash(bStringType()), hash("aaa")), hash(hash(bStringType()), hash("bbb"))));
      List<String> strings =
          ((BArray) exprDb().get(hash)).elements(BString.class).map(BString::toJavaString);
      assertThat(strings).containsExactly("aaa", "bbb").inOrder();
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bArrayType(bIntType()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          bArrayType(bIntType()),
          hashedDb().writeHashChain(),
          (Hash hash) -> ((BArray) exprDb().get(hash)).elements(BInt.class));
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bArrayType(bIntType()),
          (Hash hash) -> ((BArray) exprDb().get(hash)).elements(BInt.class));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_chain_size_different_than_multiple_of_hash_size(int byteCount)
        throws Exception {
      var notHashOfChain = hash(ByteString.of(new byte[byteCount]));
      var type = bArrayType(bStringType());
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
      var arrayType = bArrayType(bStringType());
      var hash = hash(hash(arrayType), dataHash);
      assertCall(() -> ((BArray) exprDb().get(hash)).elements(BString.class))
          .throwsException(new DecodeExprNodeException(hash, arrayType, DATA_PATH + "[0]"))
          .withCause(new DecodeExprNoSuchExprException(nowhereHash));
    }

    @Test
    public void with_one_elem_of_wrong_type() throws Exception {
      var arrayType = bArrayType(bStringType());
      var hash = hash(
          hash(arrayType),
          hash(hash(hash(bStringType()), hash("aaa")), hash(hash(bBoolType()), hash(true))));
      assertCall(() -> ((BArray) exprDb().get(hash)).elements(BString.class))
          .throwsException(new DecodeExprWrongNodeTypeException(
              hash, arrayType, DATA_PATH, 1, bStringType(), bBoolType()));
    }

    @Test
    public void with_one_elem_being_operation() throws Exception {
      var arrayType = bArrayType(bStringType());
      var hash =
          hash(hash(arrayType), hash(hash(hash(bStringType()), hash("aaa")), hash(bReference(1))));
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
      var hash = hash(hash(bBlobType()), hash(byteString));
      try (var source = ((BBlob) exprDb().get(hash)).source()) {
        assertThat(source.readByteString()).isEqualTo(byteString);
      }
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bBlobType());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          bBlobType(),
          hashedDb().writeByte((byte) 1),
          (Hash hash) -> ((BBlob) exprDb().get(hash)).source());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bBlobType(), (Hash hash) -> ((BBlob) exprDb().get(hash)).source());
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
      var hash = hash(hash(bBoolType()), hash(value));
      assertThat(((BBool) exprDb().get(hash)).toJavaBoolean()).isEqualTo(value);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bBoolType());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          bBoolType(),
          hashedDb().writeBoolean(true),
          (Hash hash) -> ((BBool) exprDb().get(hash)).toJavaBoolean());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bBoolType(), (Hash hash) -> ((BBool) exprDb().get(hash)).toJavaBoolean());
    }

    @Test
    public void empty_bytes_as_data() throws Exception {
      var dataHash = hash(ByteString.of());
      var hash = hash(hash(bBoolType()), dataHash);
      assertCall(() -> ((BBool) exprDb().get(hash)).toJavaBoolean())
          .throwsException(new DecodeExprNodeException(hash, bBoolType(), DATA_PATH))
          .withCause(new DecodeBooleanException(dataHash, new DecodeByteException(dataHash)));
    }

    @Test
    public void more_than_one_byte_as_data() throws Exception {
      var dataHash = hash(ByteString.of((byte) 0, (byte) 0));
      var hash = hash(hash(bBoolType()), dataHash);
      assertCall(() -> ((BBool) exprDb().get(hash)).toJavaBoolean())
          .throwsException(new DecodeExprNodeException(hash, bBoolType(), DATA_PATH))
          .withCause(new DecodeBooleanException(dataHash, new DecodeByteException(dataHash)));
    }

    @ParameterizedTest
    @ArgumentsSource(AllByteValuesExceptZeroAndOneProvider.class)
    public void one_byte_data_not_equal_zero_nor_one(byte value) throws Exception {
      var dataHash = hash(ByteString.of(value));
      var hash = hash(hash(bBoolType()), dataHash);
      assertCall(() -> ((BBool) exprDb().get(hash)).toJavaBoolean())
          .throwsException(new DecodeExprNodeException(hash, bBoolType(), DATA_PATH))
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
      var funcType = bLambdaType(bStringType(), bIntType(), bIntType());
      var func = bLambda(funcType, bInt());
      var args = bCombine(bString(), bInt());
      var hash = hash(hash(bCallKind(bIntType())), hash(hash(func), hash(args)));

      assertThat(((BCall) exprDb().get(hash)).subExprs())
          .isEqualTo(new BCall.SubExprsB(func, args));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bCallKind(bIntType()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var funcType = bLambdaType(bStringType(), bIntType(), bIntType());
      var func = bLambda(funcType, bInt());
      var args = bCombine(bString(), bInt());
      var dataHash = hash(hash(func), hash(args));
      obj_root_with_two_data_hashes(
          bCallKind(bIntType()), dataHash, (Hash hash) -> ((BCall) exprDb().get(hash)).subExprs());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bCallKind(bIntType()), (Hash hash) -> ((BCall) exprDb().get(hash)).subExprs());
    }

    @Test
    public void data_is_chain_with_one_elem() throws Exception {
      var funcType = bLambdaType(bStringType(), bIntType(), bIntType());
      var func = bLambda(funcType, bInt());
      var dataHash = hash(hash(func));
      var kind = bCallKind(bIntType());
      var hash = hash(hash(kind), dataHash);
      assertCall(() -> ((BCall) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongChainSizeException(hash, kind, DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_chain_with_three_elements() throws Exception {
      var funcType = bLambdaType(bStringType(), bIntType(), bIntType());
      var func = bLambda(funcType, bInt());
      var args = bCombine(bString(), bInt());
      var dataHash = hash(hash(func), hash(args), hash(args));
      var kind = bCallKind(bIntType());
      var hash = hash(hash(kind), dataHash);
      assertCall(() -> ((BCall) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongChainSizeException(hash, kind, DATA_PATH, 2, 3));
    }

    @Test
    public void func_component_evaluation_type_is_not_func() throws Exception {
      var notFunc = bInt(3);
      var args = bCombine(bInt());
      var type = bCallKind(bStringType());
      var hash = hash(hash(type), hash(hash(notFunc), hash(args)));
      assertCall(() -> ((BCall) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongMemberEvaluationTypeException(
              hash, type, "lambda", "BLambdaType", bIntType()));
    }

    @Test
    public void arguments_is_value_instead_of_expression_with_tuple_evaluation_type()
        throws Exception {
      var funcType = bLambdaType(bStringType(), bIntType(), bIntType());
      var func = bLambda(funcType, bInt());
      var type = bCallKind(bIntType());
      var hash = hash(hash(type), hash(hash(func), hash(bInt())));
      assertCall(() -> ((BCall) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongMemberEvaluationTypeException(
              hash, type, "arguments", bTupleType(bStringType(), bIntType()), bIntType()));
    }

    @Test
    public void args_component_evaluation_type_is_not_tuple_but_different_operation()
        throws Exception {
      var argumentTypes = list(bStringType(), bIntType());
      var funcType = bLambdaType(argumentTypes, bIntType());
      var func = bLambda(funcType, bInt());
      var type = bCallKind(bIntType());
      var notTuple = bOrder();
      var hash = hash(hash(type), hash(hash(func), hash(notTuple)));
      assertCall(() -> ((BCall) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongMemberEvaluationTypeException(
              hash, type, "arguments", bTupleType(argumentTypes), notTuple.evaluationType()));
    }

    @Test
    public void evaluation_type_is_different_than_func_evaluation_type_result() throws Exception {
      var funcType = bLambdaType(bStringType(), bIntType());
      var func = bLambda(funcType, bInt());
      var args = bCombine(bString());
      var type = bCallKind(bStringType());
      var hash = hash(hash(type), hash(hash(func), hash(args)));
      assertCall(() -> ((BCall) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongMemberEvaluationTypeException(
              hash, type, "function.resultType", bStringType(), bIntType()));
    }

    @Test
    public void func_evaluation_type_params_does_not_match_args_evaluation_types()
        throws Exception {
      var funcType = bLambdaType(bStringType(), bBoolType(), bIntType());
      var func = bLambda(funcType, bInt());
      var args = bCombine(bString(), bInt());
      var kind = bCallKind(bIntType());
      var hash = hash(hash(kind), hash(hash(func), hash(args)));
      assertCall(() -> ((BCall) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongMemberEvaluationTypeException(
              hash,
              kind,
              "arguments",
              bTupleType(bStringType(), bBoolType()),
              bTupleType(bStringType(), bIntType())));
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
      var expr1 = bInt(1);
      var expr2 = bString("abc");
      var hash =
          hash(hash(bCombineKind(bIntType(), bStringType())), hash(hash(expr1), hash(expr2)));
      var items = ((BCombine) exprDb().get(hash)).items();
      assertThat(items).containsExactly(expr1, expr2).inOrder();
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bCombineKind());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var item1 = bInt(1);
      var item2 = bString("abc");
      var dataHash = hash(hash(item1), hash(item2));
      obj_root_with_two_data_hashes(
          bOrderKind(), dataHash, (Hash hash) -> ((BCombine) exprDb().get(hash)).subExprs());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bCombineKind(), (Hash hash) -> ((BCombine) exprDb().get(hash)).subExprs());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_chain_size_different_than_multiple_of_hash_size(int byteCount)
        throws Exception {
      var notHashOfChain = hash(ByteString.of(new byte[byteCount]));
      var hash = hash(hash(bCombineKind()), notHashOfChain);
      assertCall(() -> ((BCombine) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprNodeException(hash, bCombineKind(), DATA_PATH))
          .withCause(
              new DecodeHashChainException(notHashOfChain, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_chain_element_pointing_nowhere() throws Exception {
      var nowhere = Hash.of(33);
      var hash = hash(hash(bCombineKind()), hash(nowhere));
      assertCall(() -> ((BCombine) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprNodeException(hash, bCombineKind(), DATA_PATH + "[0]"))
          .withCause(new DecodeExprNoSuchExprException(nowhere));
    }

    @Test
    public void evaluation_type_items_size_is_different_than_actual_items_size() throws Exception {
      var item1 = bInt();
      var type = bCombineKind(bIntType(), bStringType());
      var hash = hash(hash(type), hash(hash(item1)));

      assertCall(() -> ((BCombine) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeCombineWrongElementsSizeException(hash, type, 1));
    }

    @Test
    public void evaluation_type_item_is_different_than_evaluation_type_of_one_of_items()
        throws Exception {
      var item1 = bInt(1);
      var item2 = bString("abc");
      var type = bCombineKind(bIntType(), bBoolType());
      var hash = hash(hash(type), hash(hash(item1), hash(item2)));

      assertCall(() -> ((BCombine) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongNodeTypeException(
              hash, type, "elements", 1, bBoolType(), bStringType()));
    }
  }

  @Nested
  class _lambda {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save
       * lambda in HashedDb.
       */
      var body = bBool(true);
      var kind = bLambdaType(bIntType(), bStringType(), bBoolType());
      var hash = hash(hash(kind), hash(body));
      assertThat(((BLambda) exprDb().get(hash)).body()).isEqualTo(body);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bLambdaType());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var bodyExpr = bBool(true);
      var kind = bLambdaType(bIntType(), bStringType(), bBoolType());
      var dataHash = hash(bodyExpr);
      obj_root_with_two_data_hashes(
          kind, dataHash, (Hash hash) -> ((BLambda) exprDb().get(hash)).body());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_expr_but_nowhere(
          bLambdaType(), (Hash hash) -> ((BLambda) exprDb().get(hash)).body());
    }

    @Test
    public void body_evaluation_type_is_not_equal_func_type_result() throws Exception {
      var body = bInt(17);
      var kind = bLambdaType(bIntType(), bStringType(), bBoolType());
      var hash = hash(hash(kind), hash(body));
      assertCall(() -> ((BLambda) exprDb().get(hash)).body())
          .throwsException(new DecodeExprWrongMemberEvaluationTypeException(
              hash, kind, "body", bBoolType(), bIntType()));
    }
  }

  @Nested
  class _if {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save IF operation
       * in HashedDb.
       */
      var condition = bBool(true);
      var then_ = bInt(1);
      var else_ = bInt(2);
      var dataHash = hash(hash(condition), hash(then_), hash(else_));
      var hash = hash(hash(bIfKind(bIntType())), dataHash);
      assertThat(((BIf) exprDb().get(hash)).subExprs())
          .isEqualTo(new BIf.SubExprsB(condition, then_, else_));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bIfKind(bIntType()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var condition = bBool(true);
      var then_ = bInt(1);
      var else_ = bInt(2);
      var dataHash = hash(hash(condition), hash(then_), hash(else_));
      obj_root_with_two_data_hashes(
          bIfKind(), dataHash, (Hash hash) -> ((BIf) exprDb().get(hash)).subExprs());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bIfKind(), (Hash hash) -> ((BIf) exprDb().get(hash)).subExprs());
    }

    @Test
    public void data_is_chain_with_one_element() throws Exception {
      var condition = bBool(true);
      var dataHash = hash(hash(condition));
      var hash = hash(hash(bIfKind()), dataHash);
      assertCall(() -> ((BIf) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongChainSizeException(hash, bIfKind(), DATA_PATH, 3, 1));
    }

    @Test
    public void data_is_chain_with_two_elements() throws Exception {
      var condition = bBool(true);
      var then_ = bInt(1);
      var dataHash = hash(hash(condition), hash(then_));
      var hash = hash(hash(bIfKind()), dataHash);
      assertCall(() -> ((BIf) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongChainSizeException(hash, bIfKind(), DATA_PATH, 3, 2));
    }

    @Test
    public void data_is_chain_with_four_element() throws Exception {
      var condition = bBool(true);
      var then_ = bInt(1);
      var else_ = bInt(2);
      var dataHash = hash(hash(condition), hash(then_), hash(else_), hash(else_));
      var hash = hash(hash(bIfKind()), dataHash);
      assertCall(() -> ((BIf) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongChainSizeException(hash, bIfKind(), DATA_PATH, 3, 4));
    }

    @Test
    public void condition_evaluation_type_is_not_bool() throws Exception {
      var condition = bString();
      var then_ = bInt(1);
      var else_ = bInt(2);
      var dataHash = hash(hash(condition), hash(then_), hash(else_));
      var kind = bIfKind(bIntType());
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BIf) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongMemberEvaluationTypeException(
              hash, kind, "condition", bBoolType(), bStringType()));
    }

    @Test
    public void then_evaluation_type_is_not_equal_to_if_evaluation_type() throws Exception {
      var condition = bBool();
      var then_ = bString();
      var else_ = bInt(2);
      var dataHash = hash(hash(condition), hash(then_), hash(else_));
      var kind = bIfKind(bIntType());
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BIf) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongMemberEvaluationTypeException(
              hash, kind, "then", bIntType(), bStringType()));
    }

    @Test
    public void else_evaluation_type_is_not_equal_to_if_evaluation_type() throws Exception {
      var condition = bBool();
      var then_ = bInt(1);
      var else_ = bString();
      var dataHash = hash(hash(condition), hash(then_), hash(else_));
      var kind = bIfKind(bIntType());
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BIf) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongMemberEvaluationTypeException(
              hash, kind, "else", bIntType(), bStringType()));
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
      var hash = hash(hash(bIntType()), hash(byteString));
      assertThat(((BInt) exprDb().get(hash)).toJavaBigInteger())
          .isEqualTo(BigInteger.valueOf(3 * 256 + 2));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bIntType());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          bIntType(),
          hashedDb().writeByte((byte) 1),
          (Hash hash) -> ((BInt) exprDb().get(hash)).toJavaBigInteger());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bIntType(), (Hash hash) -> ((BInt) exprDb().get(hash)).toJavaBigInteger());
    }
  }

  @Nested
  class _map {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save IF
       * in HashedDb.
       */
      var array = bArray(bInt(1));
      var mapper = bIntIdFunc();
      var dataHash = hash(hash(array), hash(mapper));
      var hash = hash(hash(bMapKind(bArrayType(bIntType()))), dataHash);
      assertThat(((BMap) exprDb().get(hash)).subExprs())
          .isEqualTo(new BMap.SubExprsB(array, mapper));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bMapKind(bArrayType(bIntType())));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var array = bArray(bInt(1));
      var mapper = bIntIdFunc();
      var kind = bMapKind(bArrayType(bIntType()));
      var dataHash = hash(hash(array), hash(mapper));
      obj_root_with_two_data_hashes(
          kind, dataHash, (Hash hash) -> ((BMap) exprDb().get(hash)).subExprs());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      var kind = bMapKind(bArrayType(bIntType()));
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          kind, (Hash hash) -> ((BMap) exprDb().get(hash)).subExprs());
    }

    @Test
    public void data_is_chain_with_one_element() throws Exception {
      var array = bArray(bInt());
      var dataHash = hash(hash(array));
      var kind = bMapKind(bArrayType(bIntType()));
      var hash = hash(hash(kind), dataHash);
      assertCall(() -> ((BMap) exprDb().get(hash)).subExprs())
          .throwsException(
              new DecodeExprWrongChainSizeException(hash, bMapKind(), DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_chain_with_three_elements() throws Exception {
      var array = bArray(bInt());
      var mapper = bIntIdFunc();
      var dataHash = hash(hash(array), hash(mapper), hash(mapper));
      var kind = bMapKind(bArrayType(bIntType()));
      var hash = hash(hash(kind), dataHash);
      assertCall(() -> ((BMap) exprDb().get(hash)).subExprs())
          .throwsException(
              new DecodeExprWrongChainSizeException(hash, bMapKind(), DATA_PATH, 2, 3));
    }

    @Test
    public void array_evaluation_type_is_not_array_type() throws Exception {
      var notArray = bInt(1);
      var mapper = bIntIdFunc();
      var dataHash = hash(hash(notArray), hash(mapper));
      var kind = bMapKind(bArrayType(bIntType()));
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BMap) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongMemberEvaluationTypeException(
              hash, kind, "array", "BArrayType", bIntType()));
    }

    @Test
    public void mapper_evaluation_type_is_not_lambda_type() throws Exception {
      var array = bArray(bInt());
      var notMapper = bInt();
      var dataHash = hash(hash(array), hash(notMapper));
      var kind = bMapKind(bArrayType(bIntType()));
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BMap) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongMemberEvaluationTypeException(
              hash, kind, "mapper", "(Int)->Int", bIntType()));
    }

    @Test
    public void mapper_has_more_than_one_parameter() throws Exception {
      var array = bArray(bInt());
      var mapperWithTwoParams = bLambda(list(bIntType(), bIntType()), bInt());
      var dataHash = hash(hash(array), hash(mapperWithTwoParams));
      var kind = bMapKind(bArrayType(bIntType()));
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BMap) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongMemberEvaluationTypeException(
              hash, kind, "mapper", "(Int)->Int", mapperWithTwoParams.type()));
    }

    @Test
    public void mapper_param_type_is_different_than_array_element_type() throws Exception {
      var array = bArray(bString());
      var mapper = bIntIdFunc();
      var dataHash = hash(hash(array), hash(mapper));
      var kind = bMapKind(bArrayType(bIntType()));
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BMap) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongMemberEvaluationTypeException(
              hash, kind, "mapper", "(String)->Int", mapper.type()));
    }

    @Test
    public void mapper_result_type_is_different_than_result_array_element_type() throws Exception {
      var array = bArray(bInt());
      var mapper = bIntIdFunc();
      var dataHash = hash(hash(array), hash(mapper));
      var kind = bMapKind(bArrayType(bStringType()));
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BMap) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongMemberEvaluationTypeException(
              hash, kind, "mapper", "(Int)->String", mapper.type()));
    }
  }

  @Nested
  class _invoke {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save
       * INVOKE in HashedDb.
       */
      var kind = bInvokeKind(bIntType());
      var jar = bBlob();
      var classBinaryName = bString();
      var isPure = bBool(true);
      var arguments = bTuple(bInt());
      var hash =
          hash(hash(kind), hash(hash(jar), hash(classBinaryName), hash(isPure), hash(arguments)));

      var invokeSubExprs = ((BInvoke) exprDb().get(hash)).subExprs();
      assertThat(invokeSubExprs.jar()).isEqualTo(jar);
      assertThat(invokeSubExprs.classBinaryName()).isEqualTo(classBinaryName);
      assertThat(invokeSubExprs.isPure()).isEqualTo(isPure);
      assertThat(invokeSubExprs.arguments()).isEqualTo(arguments);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bIntType());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var kind = bInvokeKind(bIntType());
      var jar = bBlob();
      var classBinaryName = bString();
      var isPure = bBool(true);
      var arguments = bTuple(bInt(3));
      var dataHash = hash(hash(jar), hash(classBinaryName), hash(isPure), hash(arguments));
      obj_root_with_two_data_hashes(
          kind, dataHash, (Hash hash) -> ((BInvoke) exprDb().get(hash)).classBinaryName());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      var kind = bInvokeKind(bIntType());
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          kind, (Hash hash) -> ((BInvoke) exprDb().get(hash)).classBinaryName());
    }

    @Test
    public void data_is_chain_with_one_element() throws Exception {
      var kind = bInvokeKind(bIntType());
      var jar = bBlob();
      var dataHash = hash(hash(jar));
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BInvoke) exprDb().get(hash)).classBinaryName())
          .throwsException(new DecodeExprWrongChainSizeException(hash, kind, DATA_PATH, 4, 1));
    }

    @Test
    public void data_is_chain_with_two_elements() throws Exception {
      var kind = bInvokeKind(bIntType());
      var jar = bBlob();
      var classBinaryName = bString();
      var dataHash = hash(hash(jar), hash(classBinaryName));
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BInvoke) exprDb().get(hash)).classBinaryName())
          .throwsException(new DecodeExprWrongChainSizeException(hash, kind, DATA_PATH, 4, 2));
    }

    @Test
    public void data_is_chain_with_three_elements() throws Exception {
      var kind = bInvokeKind(bIntType());
      var jar = bBlob();
      var classBinaryName = bString();
      var isPure = bBool(true);
      var dataHash = hash(hash(jar), hash(classBinaryName), hash(isPure));
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BInvoke) exprDb().get(hash)).classBinaryName())
          .throwsException(new DecodeExprWrongChainSizeException(hash, kind, DATA_PATH, 4, 3));
    }

    @Test
    public void data_is_chain_with_five_elements() throws Exception {
      var type = bInvokeKind(bIntType());
      var jar = bBlob();
      var classBinaryName = bString();
      var isPure = bBool(true);
      var arguments = bTuple(bInt());
      var dataHash =
          hash(hash(jar), hash(classBinaryName), hash(isPure), hash(arguments), hash(arguments));
      var hash = hash(hash(type), dataHash);

      assertCall(() -> ((BInvoke) exprDb().get(hash)).classBinaryName())
          .throwsException(new DecodeExprWrongChainSizeException(hash, type, DATA_PATH, 4, 5));
    }

    @Test
    public void jar_file_evaluation_type_is_not_blob() throws Exception {
      var kind = bInvokeKind(bIntType());
      var jar = bString();
      var classBinaryName = bString();
      var isPure = bBool(true);
      var arguments = bTuple(bInt());
      var hash =
          hash(hash(kind), hash(hash(jar), hash(classBinaryName), hash(isPure), hash(arguments)));
      assertCall(() -> ((BInvoke) exprDb().get(hash)).subExprs().jar())
          .throwsException(new DecodeExprWrongMemberEvaluationTypeException(
              hash, kind, "jar", bBlobType(), bStringType()));
    }

    @Test
    public void class_binary_name_evaluation_type_is_not_string() throws Exception {
      var kind = bInvokeKind(bIntType());
      var jar = bBlob();
      var classBinaryName = bInt();
      var isPure = bBool(true);
      var arguments = bTuple(bInt());
      var hash =
          hash(hash(kind), hash(hash(jar), hash(classBinaryName), hash(isPure), hash(arguments)));

      assertCall(() -> ((BInvoke) exprDb().get(hash)).subExprs().classBinaryName())
          .throwsException(new DecodeExprWrongMemberEvaluationTypeException(
              hash, kind, "classBinaryName", bStringType(), bIntType()));
    }

    @Test
    public void is_pure_evaluation_type_is_not_bool() throws Exception {
      var kind = bInvokeKind(bIntType());
      var jar = bBlob();
      var classBinaryName = bString();
      var isPure = bString();
      var arguments = bTuple(bInt());
      var hash =
          hash(hash(kind), hash(hash(jar), hash(classBinaryName), hash(isPure), hash(arguments)));

      assertCall(() -> ((BInvoke) exprDb().get(hash)).subExprs().isPure())
          .throwsException(new DecodeExprWrongMemberEvaluationTypeException(
              hash, kind, "isPure", bBoolType(), bStringType()));
    }

    @Test
    public void arguments_evaluation_type_is_not_tuple() throws Exception {
      var kind = bInvokeKind(bIntType());
      var jar = bBlob();
      var classBinaryName = bString();
      var isPure = bBool();
      var arguments = bInt();
      var hash =
          hash(hash(kind), hash(hash(jar), hash(classBinaryName), hash(isPure), hash(arguments)));

      assertCall(() -> ((BInvoke) exprDb().get(hash)).subExprs().arguments())
          .throwsException(new DecodeExprWrongMemberTypeException(
              hash, kind, "arguments", BTupleType.class, BIntType.class));
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
      var expr1 = bInt(1);
      var expr2 = bInt(2);
      var hash = hash(hash(bOrderKind(bIntType())), hash(hash(expr1), hash(expr2)));
      var elements = ((BOrder) exprDb().get(hash)).elements();
      assertThat(elements).containsExactly(expr1, expr2).inOrder();
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bOrderKind());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var expr1 = bInt(1);
      var expr2 = bInt(2);
      var dataHash = hash(hash(expr1), hash(expr2));
      obj_root_with_two_data_hashes(
          bOrderKind(), dataHash, (Hash hash) -> ((BOrder) exprDb().get(hash)).subExprs());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bOrderKind(), (Hash hash) -> ((BOrder) exprDb().get(hash)).subExprs());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_chain_size_different_than_multiple_of_hash_size(int byteCount)
        throws Exception {
      var notHashOfChain = hash(ByteString.of(new byte[byteCount]));
      var hash = hash(hash(bOrderKind()), notHashOfChain);
      assertCall(() -> ((BOrder) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprNodeException(hash, bOrderKind(), DATA_PATH))
          .withCause(
              new DecodeHashChainException(notHashOfChain, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_chain_elem_pointing_nowhere() throws Exception {
      var nowhereHash = Hash.of(33);
      var hash = hash(hash(bOrderKind()), hash(nowhereHash));
      assertCall(() -> ((BOrder) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprNodeException(hash, bOrderKind(), DATA_PATH + "[0]"))
          .withCause(new DecodeExprNoSuchExprException(nowhereHash));
    }

    @Test
    public void evaluation_type_elem_is_different_than_evaluation_type_of_one_of_elems()
        throws Exception {
      var expr1 = bInt();
      var expr2 = bString();
      var type = bOrderKind(bIntType());
      var hash = hash(hash(type), hash(hash(expr1), hash(expr2)));
      assertCall(() -> ((BOrder) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongNodeTypeException(
              hash, type, "elements[1]", bIntType(), bStringType()));
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
      var pickable = bOrder(bString("abc"));
      var index = bReference(bIntType(), 7);
      var hash = hash(hash(bPickKind(bStringType())), hash(hash(pickable), hash(index)));
      assertThat(((BPick) exprDb().get(hash)).subExprs())
          .isEqualTo(new BPick.SubExprsB(pickable, index));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bPickKind(bIntType()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var index = bInt(2);
      var expr = bInt(123);
      var dataHash = hash(hash(expr), hash(index));
      obj_root_with_two_data_hashes(
          bPickKind(), dataHash, (Hash hash) -> ((BPick) exprDb().get(hash)).subExprs());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bPickKind(), (Hash hash) -> ((BPick) exprDb().get(hash)).subExprs());
    }

    @Test
    public void data_is_chain_with_one_element() throws Exception {
      var expr = bInt(123);
      var dataHash = hash(hash(expr));
      var hash = hash(hash(bPickKind()), dataHash);
      assertCall(() -> ((BPick) exprDb().get(hash)).subExprs())
          .throwsException(
              new DecodeExprWrongChainSizeException(hash, bPickKind(), DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_chain_with_three_elements() throws Exception {
      var index = bInt(2);
      var expr = bInt(123);
      var dataHash = hash(hash(expr), hash(index), hash(index));
      var hash = hash(hash(bPickKind()), dataHash);
      assertCall(() -> ((BPick) exprDb().get(hash)).subExprs())
          .throwsException(
              new DecodeExprWrongChainSizeException(hash, bPickKind(), DATA_PATH, 2, 3));
    }

    @Test
    public void array_is_not_array_expr() throws Exception {
      var notArray = bInt(3);
      var index = bInt(0);
      var type = bPickKind(bStringType());
      var hash = hash(hash(type), hash(hash(notArray), hash(index)));

      assertCall(() -> ((BPick) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongMemberEvaluationTypeException(
              hash, type, "pickable", bArrayType(bStringType()), bIntType()));
    }

    @Test
    public void index_is_not_int_expr() throws Exception {
      var type = bPickKind(bStringType());
      var pickable = bArray(bString("abc"));
      var index = bReference(bStringType(), 7);
      var hash = hash(hash(type), hash(hash(pickable), hash(index)));
      assertCall(() -> ((BPick) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongMemberEvaluationTypeException(
              hash, type, "index", bIntType(), bStringType()));
    }

    @Test
    public void evaluation_type_is_different_than_elem_type() throws Exception {
      var tuple = bArray(bString("abc"));
      var index = bInt(0);
      var type = bPickKind(bIntType());
      var hash = hash(hash(type), hash(hash(tuple), hash(index)));

      assertCall(() -> ((BPick) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongMemberEvaluationTypeException(
              hash, type, "pickable", bArrayType(bIntType()), bArrayType(bStringType())));
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
      var index = bInt(34);
      var hash = hash(hash(bReferenceKind(bStringType())), hash(index));
      assertThat(((BReference) exprDb().get(hash)).index()).isEqualTo(index);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bReferenceKind());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var index = bInt(0);
      var dataHash = hash(index);
      obj_root_with_two_data_hashes(
          bReferenceKind(bIntType()),
          dataHash,
          (Hash hash) -> ((BReference) exprDb().get(hash)).index());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_expr_but_nowhere(
          bReferenceKind(bIntType()), (Hash hash) -> ((BReference) exprDb().get(hash)).index());
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
      var tuple = bTuple(bString("abc"));
      var selectable = (BValue) tuple;
      var index = bInt(0);
      var hash = hash(hash(bSelectKind(bStringType())), hash(hash(selectable), hash(index)));
      assertThat(((BSelect) exprDb().get(hash)).subExprs())
          .isEqualTo(new BSelect.SubExprsB(selectable, index));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bSelectKind(bIntType()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var index = bInt(2);
      var expr = bInt(123);
      var dataHash = hash(hash(expr), hash(index));
      obj_root_with_two_data_hashes(
          bSelectKind(), dataHash, (Hash hash) -> ((BSelect) exprDb().get(hash)).subExprs());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bSelectKind(), (Hash hash) -> ((BSelect) exprDb().get(hash)).subExprs());
    }

    @Test
    public void data_is_chain_with_one_element() throws Exception {
      var expr = bInt(123);
      var dataHash = hash(hash(expr));
      var hash = hash(hash(bSelectKind()), dataHash);
      assertCall(() -> ((BSelect) exprDb().get(hash)).subExprs())
          .throwsException(
              new DecodeExprWrongChainSizeException(hash, bSelectKind(), DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_chain_with_three_element() throws Exception {
      var index = bInt(2);
      var expr = bInt(123);
      var dataHash = hash(hash(expr), hash(index), hash(index));
      var hash = hash(hash(bSelectKind()), dataHash);
      assertCall(() -> ((BSelect) exprDb().get(hash)).subExprs())
          .throwsException(
              new DecodeExprWrongChainSizeException(hash, bSelectKind(), DATA_PATH, 2, 3));
    }

    @Test
    public void tuple_is_not_tuple_expr() throws Exception {
      var expr = bInt(3);
      var index = bInt(0);
      var type = bSelectKind(bStringType());
      var hash = hash(hash(type), hash(hash(expr), hash(index)));

      assertCall(() -> ((BSelect) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongMemberTypeException(
              hash, type, "tuple", BTupleType.class, BIntType.class));
    }

    @Test
    public void index_is_out_of_bounds() throws Exception {
      var tuple = bTuple(bString("abc"));
      var index = bInt(1);
      var type = bSelectKind(bStringType());
      var hash = hash(hash(type), hash(hash(tuple), hash(index)));

      assertCall(() -> ((BSelect) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeSelectIndexOutOfBoundsException(hash, type, 1, 1));
    }

    @Test
    public void evaluation_type_is_different_than_type_of_item_pointed_to_by_index()
        throws Exception {
      var tuple = bTuple(bString("abc"));
      var index = bInt(0);
      var type = bSelectKind(bIntType());
      var hash = hash(hash(type), hash(hash(tuple), hash(index)));

      assertCall(() -> ((BSelect) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeSelectWrongEvaluationTypeException(hash, type, bStringType()));
    }

    @Test
    public void index_is_string_instead_of_int() throws Exception {
      var type = bSelectKind(bStringType());
      var tuple = bTuple(bString("abc"));
      var string = bString("abc");
      var hash = hash(hash(type), hash(hash(tuple), hash(string)));
      assertCall(() -> ((BSelect) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprWrongMemberTypeException(
              hash, type, "index", BInt.class, BString.class));
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
      var hash = hash(hash(bStringType()), hash("aaa"));
      assertThat(((BString) exprDb().get(hash)).toJavaString()).isEqualTo("aaa");
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bStringType());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          bStringType(),
          hashedDb().writeBoolean(true),
          (Hash hash) -> ((BString) exprDb().get(hash)).toJavaString());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bStringType(), (Hash hash) -> ((BString) exprDb().get(hash)).toJavaString());
    }

    @Test
    public void data_being_invalid_utf8_chain() throws Exception {
      var notStringHash = hash(illegalString());
      var hash = hash(hash(bStringType()), notStringHash);
      assertCall(() -> ((BString) exprDb().get(hash)).toJavaString())
          .throwsException(new DecodeExprNodeException(hash, bStringType(), DATA_PATH))
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
      assertThat(hash(hash(bPersonType()), hash(hash(bString("John")), hash(bString("Doe")))))
          .isEqualTo(bPerson("John", "Doe").hash());
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bPersonType());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          bPersonType(),
          hashedDb().writeBoolean(true),
          (Hash hash) -> ((BTuple) exprDb().get(hash)).get(0));
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bPersonType(), (Hash hash) -> ((BTuple) exprDb().get(hash)).get(0));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_chain_size_different_than_multiple_of_hash_size(int byteCount)
        throws Exception {
      var notChainHash = hash(ByteString.of(new byte[byteCount]));
      var hash = hash(hash(bPersonType()), notChainHash);
      assertCall(() -> ((BTuple) exprDb().get(hash)).get(0))
          .throwsException(new DecodeExprNodeException(hash, bPersonType(), DATA_PATH))
          .withCause(new DecodeHashChainException(notChainHash, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_chain_element_pointing_nowhere() throws Exception {
      var nowhereHash = Hash.of(33);
      var dataHash = hash(nowhereHash, nowhereHash);
      var hash = hash(hash(bPersonType()), dataHash);
      assertCall(() -> ((BTuple) exprDb().get(hash)).get(0))
          .throwsException(new DecodeExprNodeException(hash, bPersonType(), DATA_PATH + "[0]"))
          .withCause(new DecodeExprNoSuchExprException(nowhereHash));
    }

    @Test
    public void with_too_few_elements() throws Exception {
      var dataHash = hash(hash(bString("John")));
      var hash = hash(hash(bPersonType()), dataHash);
      BTuple tuple = (BTuple) exprDb().get(hash);
      assertCall(() -> tuple.get(0))
          .throwsException(
              new DecodeExprWrongChainSizeException(hash, bPersonType(), DATA_PATH, 2, 1));
    }

    @Test
    public void with_too_many_elements() throws Exception {
      var dataHash = hash(hash(bString("John")), hash(bString("Doe")), hash(bString("junk")));
      var hash = hash(hash(bPersonType()), dataHash);
      var tuple = (BTuple) exprDb().get(hash);
      assertCall(() -> tuple.get(0))
          .throwsException(
              new DecodeExprWrongChainSizeException(hash, bPersonType(), DATA_PATH, 2, 3));
    }

    @Test
    public void with_element_of_wrong_type() throws Exception {
      var hash = hash(hash(bPersonType()), hash(hash(bString("John")), hash(bBool(true))));
      var tuple = (BTuple) exprDb().get(hash);
      assertCall(() -> tuple.get(0))
          .throwsException(new DecodeExprWrongNodeTypeException(
              hash, bPersonType(), DATA_PATH, bPersonType(), "`{String,Bool}`"));
    }

    @Test
    public void with_element_being_operation() throws Exception {
      var hash = hash(hash(bPersonType()), hash(hash(bString("John")), hash(bReference(1))));
      var tuple = (BTuple) exprDb().get(hash);
      assertCall(() -> tuple.get(0))
          .throwsException(new DecodeExprWrongNodeClassException(
              hash, bPersonType(), DATA_PATH + "[1]", BValue.class, BReference.class));
    }
  }

  private void obj_root_without_data_hash(BKind kind) throws HashedDbException {
    var hash = hash(hash(kind));
    assertCall(() -> exprDb().get(hash))
        .throwsException(wrongSizeOfRootChainException(hash, kind, 1));
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
