package org.smoothbuild.virtualmachine.bytecode.expr;

import static com.google.common.truth.Truth.assertThat;
import static okio.Okio.buffer;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.testing.TestingString.illegalString;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr.DATA_PATH;
import static org.smoothbuild.virtualmachine.bytecode.expr.exc.RootHashChainSizeIsWrongException.cannotReadRootException;
import static org.smoothbuild.virtualmachine.bytecode.expr.exc.RootHashChainSizeIsWrongException.wrongSizeOfRootChainException;

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
import org.smoothbuild.virtualmachine.bytecode.expr.base.BChoice;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BChoose;
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
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect.BSubExprs;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSwitch;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.ChoiceHasIndexOutOfBoundException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.ChooseHasIndexOutOfBoundException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprKindException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprNodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.MemberHasWrongEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.MemberHasWrongTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.NoSuchExprException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.NodeChainSizeIsWrongException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.NodeClassIsWrongException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.NodeHasWrongTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.SelectHasIndexOutOfBoundException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.SelectHasWrongEvaluationTypeException;
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
import org.smoothbuild.virtualmachine.testing.VmTestContext;

public class BExprCorruptedTest extends VmTestContext {
  @Nested
  class _expr {
    @Test
    void learning_test() throws Exception {
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
    void corrupted_type() throws Exception {
      var typeHash = Hash.of("not a type");
      var hash = hash(typeHash, hash("aaa"));
      assertCall(() -> exprDb().get(hash))
          .throwsException(new DecodeExprKindException(hash))
          .withCause(new DecodeKindException(typeHash));
    }

    @Test
    void reading_elements_from_not_stored_object_throws_exception() {
      var hash = Hash.of(33);
      assertCall(() -> exprDb().get(hash))
          .throwsException(new NoSuchExprException(hash))
          .withCause(new NoSuchDataException(hash));
    }
  }

  @Nested
  class _array {
    @Test
    void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save array
       * in HashedDb.
       */
      var hash = hash(
          hash(bStringArrayType()),
          hash(hash(hash(bStringType()), hash("aaa")), hash(hash(bStringType()), hash("bbb"))));
      List<String> strings =
          ((BArray) exprDb().get(hash)).elements(BString.class).map(BString::toJavaString);
      assertThat(strings).containsExactly("aaa", "bbb").inOrder();
    }

    @Test
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bIntArrayType());
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          bIntArrayType(),
          hashedDb().writeHashChain(),
          (Hash hash) -> ((BArray) exprDb().get(hash)).elements(BInt.class));
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bIntArrayType(), (Hash hash) -> ((BArray) exprDb().get(hash)).elements(BInt.class));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_chain_size_different_than_multiple_of_hash_size(int byteCount)
        throws Exception {
      var notHashOfChain = hash(ByteString.of(new byte[byteCount]));
      var type = bStringArrayType();
      var hash = hash(hash(type), notHashOfChain);
      assertCall(() -> ((BArray) exprDb().get(hash)).elements(BValue.class))
          .throwsException(new DecodeExprNodeException(hash, type, DATA_PATH))
          .withCause(
              new DecodeHashChainException(notHashOfChain, byteCount % Hash.lengthInBytes()));
    }

    @Test
    void with_chain_element_pointing_nowhere() throws Exception {
      var nowhereHash = Hash.of(33);
      var dataHash = hash(nowhereHash);
      var arrayType = bStringArrayType();
      var hash = hash(hash(arrayType), dataHash);
      assertCall(() -> ((BArray) exprDb().get(hash)).elements(BString.class))
          .throwsException(new DecodeExprNodeException(hash, arrayType, DATA_PATH + "[0]"))
          .withCause(new NoSuchExprException(nowhereHash));
    }

    @Test
    void with_one_elem_of_wrong_type() throws Exception {
      var arrayType = bStringArrayType();
      var hash = hash(
          hash(arrayType),
          hash(hash(hash(bStringType()), hash("aaa")), hash(hash(bBoolType()), hash(true))));
      assertCall(() -> ((BArray) exprDb().get(hash)).elements(BString.class))
          .throwsException(new NodeHasWrongTypeException(
              hash, arrayType, DATA_PATH, 1, bStringType(), bBoolType()));
    }

    @Test
    void with_one_elem_being_operation() throws Exception {
      var arrayType = bStringArrayType();
      var hash =
          hash(hash(arrayType), hash(hash(hash(bStringType()), hash("aaa")), hash(bReference(1))));
      assertCall(() -> ((BArray) exprDb().get(hash)).elements(BString.class))
          .throwsException(new NodeClassIsWrongException(
              hash, arrayType, DATA_PATH, 1, BValue.class, BReference.class));
    }
  }

  @Nested
  class _blob {
    @Test
    void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save blob
       * in HashedDb.
       */
      var byteString = ByteString.of((byte) 1, (byte) 2);
      var hash = hash(hash(bBlobType()), hash(byteString));
      try (var source = buffer(((BBlob) exprDb().get(hash)).source())) {
        assertThat(source.readByteString()).isEqualTo(byteString);
      }
    }

    @Test
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bBlobType());
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          bBlobType(),
          hashedDb().writeByte((byte) 1),
          (Hash hash) -> ((BBlob) exprDb().get(hash)).source());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
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
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bBoolType());
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          bBoolType(),
          hashedDb().writeBoolean(true),
          (Hash hash) -> ((BBool) exprDb().get(hash)).toJavaBoolean());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bBoolType(), (Hash hash) -> ((BBool) exprDb().get(hash)).toJavaBoolean());
    }

    @Test
    void empty_bytes_as_data() throws Exception {
      var dataHash = hash(ByteString.of());
      var hash = hash(hash(bBoolType()), dataHash);
      assertCall(() -> ((BBool) exprDb().get(hash)).toJavaBoolean())
          .throwsException(new DecodeExprNodeException(hash, bBoolType(), DATA_PATH))
          .withCause(new DecodeBooleanException(dataHash, new DecodeByteException(dataHash)));
    }

    @Test
    void more_than_one_byte_as_data() throws Exception {
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
    void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save call
       * in HashedDb.
       */
      var lambdaType = bLambdaType(bStringType(), bIntType(), bIntType());
      var lambda = bLambda(lambdaType, bInt());
      var args = bCombine(bString(), bInt());
      var hash = hash(hash(bCallKind(bIntType())), hash(hash(lambda), hash(args)));

      assertThat(((BCall) exprDb().get(hash)).subExprs())
          .isEqualTo(new BCall.BSubExprs(lambda, args));
    }

    @Test
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bCallKind(bIntType()));
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      var lambdaType = bLambdaType(bStringType(), bIntType(), bIntType());
      var lambda = bLambda(lambdaType, bInt());
      var args = bCombine(bString(), bInt());
      var dataHash = hash(hash(lambda), hash(args));
      obj_root_with_two_data_hashes(
          bCallKind(bIntType()), dataHash, (Hash hash) -> ((BCall) exprDb().get(hash)).subExprs());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bCallKind(bIntType()), (Hash hash) -> ((BCall) exprDb().get(hash)).subExprs());
    }

    @Test
    void data_is_chain_with_one_elem() throws Exception {
      var lambdaType = bLambdaType(bStringType(), bIntType(), bIntType());
      var lambda = bLambda(lambdaType, bInt());
      var dataHash = hash(hash(lambda));
      var kind = bCallKind(bIntType());
      var hash = hash(hash(kind), dataHash);
      assertCall(() -> ((BCall) exprDb().get(hash)).subExprs())
          .throwsException(new NodeChainSizeIsWrongException(hash, kind, DATA_PATH, 2, 1));
    }

    @Test
    void data_is_chain_with_three_elements() throws Exception {
      var lambdaType = bLambdaType(bStringType(), bIntType(), bIntType());
      var lambda = bLambda(lambdaType, bInt());
      var args = bCombine(bString(), bInt());
      var dataHash = hash(hash(lambda), hash(args), hash(args));
      var kind = bCallKind(bIntType());
      var hash = hash(hash(kind), dataHash);
      assertCall(() -> ((BCall) exprDb().get(hash)).subExprs())
          .throwsException(new NodeChainSizeIsWrongException(hash, kind, DATA_PATH, 2, 3));
    }

    @Test
    void lambda_component_evaluation_type_is_not_lambda() throws Exception {
      var notLambda = bInt(3);
      var args = bCombine(bInt());
      var type = bCallKind(bStringType());
      var hash = hash(hash(type), hash(hash(notLambda), hash(args)));
      assertCall(() -> ((BCall) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, type, "lambda", "BLambdaType", bIntType()));
    }

    @Test
    void arguments_is_value_instead_of_expression_with_tuple_evaluation_type() throws Exception {
      var lambdaType = bLambdaType(bStringType(), bIntType(), bIntType());
      var lambda = bLambda(lambdaType, bInt());
      var type = bCallKind(bIntType());
      var hash = hash(hash(type), hash(hash(lambda), hash(bInt())));
      assertCall(() -> ((BCall) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, type, "arguments", bTupleType(bStringType(), bIntType()), bIntType()));
    }

    @Test
    void args_component_evaluation_type_is_not_tuple_but_different_operation() throws Exception {
      var argumentTypes = list(bStringType(), bIntType());
      var lambdaType = bLambdaType(argumentTypes, bIntType());
      var lambda = bLambda(lambdaType, bInt());
      var type = bCallKind(bIntType());
      var notTuple = bOrder();
      var hash = hash(hash(type), hash(hash(lambda), hash(notTuple)));
      assertCall(() -> ((BCall) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, type, "arguments", bTupleType(argumentTypes), notTuple.evaluationType()));
    }

    @Test
    void evaluation_type_is_different_than_lambda_evaluation_type_result() throws Exception {
      var lambdaType = bLambdaType(bStringType(), bIntType());
      var lambda = bLambda(lambdaType, bInt());
      var args = bCombine(bString());
      var type = bCallKind(bStringType());
      var hash = hash(hash(type), hash(hash(lambda), hash(args)));
      assertCall(() -> ((BCall) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, type, "lambda.resultType", bStringType(), bIntType()));
    }

    @Test
    void lambda_evaluation_type_params_does_not_match_args_evaluation_types() throws Exception {
      var lambdaType = bLambdaType(bStringType(), bBoolType(), bIntType());
      var lambda = bLambda(lambdaType, bInt());
      var args = bCombine(bString(), bInt());
      var kind = bCallKind(bIntType());
      var hash = hash(hash(kind), hash(hash(lambda), hash(args)));
      assertCall(() -> ((BCall) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash,
              kind,
              "arguments",
              bTupleType(bStringType(), bBoolType()),
              bTupleType(bStringType(), bIntType())));
    }
  }

  @Nested
  class _choice {
    @Test
    void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save Choice
       * in HashedDb.
       */
      var choiceType = bChoiceType(bStringType(), bIntType());
      var index = bInt(0);
      var chosen = bString("abc");
      var dataHash = hash(hash(index), hash(chosen));
      var hash = hash(hash(choiceType), dataHash);
      assertThat(((BChoice) exprDb().get(hash)).nodes())
          .isEqualTo(new BChoice.BSubExprs(index, chosen));
    }

    @Test
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bChoiceType(bIntType()));
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var index = bInt(0);
      var chosen = bString("abc");
      var dataHash = hash(hash(index), hash(chosen));
      obj_root_with_two_data_hashes(
          choiceType, dataHash, (Hash hash) -> ((BChoice) exprDb().get(hash)).nodes());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          choiceType, (Hash hash) -> ((BChoice) exprDb().get(hash)).nodes());
    }

    @Test
    void data_is_chain_with_one_element() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var index = bInt(0);
      var dataHash = hash(hash(index));
      var hash = hash(hash(choiceType), dataHash);
      assertCall(() -> ((BChoice) exprDb().get(hash)).nodes())
          .throwsException(new NodeChainSizeIsWrongException(hash, bChoiceType(), DATA_PATH, 2, 1));
    }

    @Test
    void data_is_chain_with_three_elements() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var index = bInt(0);
      var chosen = bString("abc");
      var dataHash = hash(hash(index), hash(chosen), hash(chosen));
      var hash = hash(hash(choiceType), dataHash);
      assertCall(() -> ((BChoice) exprDb().get(hash)).nodes())
          .throwsException(new NodeChainSizeIsWrongException(hash, bChoiceType(), DATA_PATH, 2, 3));
    }

    @Test
    void index_is_lower_than_zero() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var index = bInt(-1);
      var chosen = bString("abc");
      var dataHash = hash(hash(index), hash(chosen));
      var hash = hash(hash(choiceType), dataHash);
      assertCall(() -> ((BChoice) exprDb().get(hash)).nodes())
          .throwsException(new ChoiceHasIndexOutOfBoundException(hash, choiceType, -1, 2));
    }

    @Test
    void index_is_equal_to_choice_size() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var index = bInt(choiceType.size());
      var chosen = bString("abc");
      var dataHash = hash(hash(index), hash(chosen));
      var hash = hash(hash(choiceType), dataHash);
      assertCall(() -> ((BChoice) exprDb().get(hash)).nodes())
          .throwsException(new ChoiceHasIndexOutOfBoundException(hash, choiceType, 2, 2));
    }

    @Test
    void value_is_not_value() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var index = bInt(0);
      var chosen = bReference(bStringType(), 0);
      var dataHash = hash(hash(index), hash(chosen));
      var hash = hash(hash(choiceType), dataHash);
      assertCall(() -> ((BChoice) exprDb().get(hash)).nodes())
          .throwsException(new MemberHasWrongTypeException(
              hash, choiceType, "chosen", BValue.class, BReference.class));
    }

    @Test
    void value_has_wrong_type() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var index = bInt(0);
      var chosen = bInt(7);
      var dataHash = hash(hash(index), hash(chosen));
      var hash = hash(hash(choiceType), dataHash);
      assertCall(() -> ((BChoice) exprDb().get(hash)).nodes())
          .throwsException(new MemberHasWrongTypeException(
              hash, choiceType, "chosen", bStringType(), bIntType()));
    }
  }

  @Nested
  class _choose {
    @Test
    void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save Choose
       * in HashedDb.
       */
      var choiceType = bChoiceType(bStringType(), bIntType());
      var chooseKind = bChooseKind(choiceType);
      var index = bInt(0);
      var chosen = bSelect(bCombine(bString("abc")), 0);
      var dataHash = hash(hash(index), hash(chosen));
      var hash = hash(hash(chooseKind), dataHash);
      assertThat(((BChoose) exprDb().get(hash)).subExprs())
          .isEqualTo(new BChoose.BSubExprs(index, chosen));
    }

    @Test
    void root_without_data_hash() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var chooseKind = bChooseKind(choiceType);
      obj_root_without_data_hash(chooseKind);
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var chooseKind = bChooseKind(choiceType);
      var index = bInt(0);
      var chosen = bSelect(bCombine(bString("abc")), 0);
      var dataHash = hash(hash(index), hash(chosen));
      obj_root_with_two_data_hashes(
          chooseKind, dataHash, (Hash hash) -> ((BChoose) exprDb().get(hash)).subExprs());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var chooseKind = bChooseKind(choiceType);
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          chooseKind, (Hash hash) -> ((BChoose) exprDb().get(hash)).subExprs());
    }

    @Test
    void data_is_chain_with_one_element() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var chooseKind = bChooseKind(choiceType);
      var index = bInt(0);
      var dataHash = hash(hash(index));
      var hash = hash(hash(chooseKind), dataHash);
      assertCall(() -> ((BChoose) exprDb().get(hash)).subExprs())
          .throwsException(new NodeChainSizeIsWrongException(hash, chooseKind, DATA_PATH, 2, 1));
    }

    @Test
    void data_is_chain_with_three_elements() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var chooseKind = bChooseKind(choiceType);
      var index = bInt(0);
      var chosen = bSelect(bCombine(bString("abc")), 0);
      var dataHash = hash(hash(index), hash(chosen), hash(chosen));
      var hash = hash(hash(chooseKind), dataHash);
      assertCall(() -> ((BChoose) exprDb().get(hash)).subExprs())
          .throwsException(new NodeChainSizeIsWrongException(hash, chooseKind, DATA_PATH, 2, 3));
    }

    @Test
    void index_is_lower_than_zero() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var chooseKind = bChooseKind(choiceType);
      var index = bInt(-1);
      var chosen = bSelect(bCombine(bString("abc")), 0);
      var dataHash = hash(hash(index), hash(chosen));
      var hash = hash(hash(chooseKind), dataHash);
      assertCall(() -> ((BChoose) exprDb().get(hash)).subExprs())
          .throwsException(new ChooseHasIndexOutOfBoundException(hash, choiceType, -1, 2));
    }

    @Test
    void index_is_equal_to_choice_size() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var chooseKind = bChooseKind(choiceType);
      var index = bInt(choiceType.size());
      var chosen = bSelect(bCombine(bString("abc")), 0);
      var dataHash = hash(hash(index), hash(chosen));
      var hash = hash(hash(chooseKind), dataHash);
      assertCall(() -> ((BChoose) exprDb().get(hash)).subExprs())
          .throwsException(new ChooseHasIndexOutOfBoundException(hash, choiceType, 2, 2));
    }

    @Test
    void chosen_has_wrong_evaluation_type() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var chooseKind = bChooseKind(choiceType);
      var index = bInt(0);
      var chosen = bSelect(bCombine(bInt()), 0);
      var dataHash = hash(hash(index), hash(chosen));
      var hash = hash(hash(chooseKind), dataHash);
      assertCall(() -> ((BChoose) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, chooseKind, "chosen", bStringType(), bIntType()));
    }
  }

  @Nested
  class _switch {
    @Test
    void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save Switch
       * in HashedDb.
       */
      var switchKind = bSwitchKind(bIntType());
      var choiceType = bChoiceType(bStringType(), bIntType());
      var choice = bChoice(choiceType, 0, bString("abc"));
      var handlers = bCombine(bs2iLambda(), bi2iLambda());
      var dataHash = hash(hash(choice), hash(handlers));
      var hash = hash(hash(switchKind), dataHash);
      assertThat(((BSwitch) exprDb().get(hash)).subExprs())
          .isEqualTo(new BSwitch.BSubExprs(choice, handlers));
    }

    @Test
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bSwitchKind(bIntType()));
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      var switchKind = bSwitchKind(bIntType());
      var choiceType = bChoiceType(bStringType(), bIntType());
      var choice = bChoice(choiceType, 0, bString("abc"));
      var handlers = bCombine(bs2iLambda(), bi2iLambda());
      var dataHash = hash(hash(choice), hash(handlers));
      obj_root_with_two_data_hashes(
          switchKind, dataHash, (Hash hash) -> ((BSwitch) exprDb().get(hash)).subExprs());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      var switchKind = bSwitchKind(bIntType());
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          switchKind, (Hash hash) -> ((BSwitch) exprDb().get(hash)).subExprs());
    }

    @Test
    void data_is_chain_with_one_element() throws Exception {
      var switchKind = bSwitchKind(bIntType());
      var choiceType = bChoiceType(bStringType(), bIntType());
      var choice = bChoice(choiceType, 0, bString("abc"));
      var dataHash = hash(hash(choice));
      var hash = hash(hash(switchKind), dataHash);
      assertCall(() -> ((BSwitch) exprDb().get(hash)).subExprs())
          .throwsException(new NodeChainSizeIsWrongException(hash, switchKind, DATA_PATH, 2, 1));
    }

    @Test
    void data_is_chain_with_three_elements() throws Exception {
      var switchKind = bSwitchKind(bIntType());
      var choiceType = bChoiceType(bStringType(), bIntType());
      var choice = bChoice(choiceType, 0, bString("abc"));
      var handlers = bCombine(bs2iLambda(), bi2iLambda());
      var dataHash = hash(hash(choice), hash(handlers), hash(handlers));
      var hash = hash(hash(switchKind), dataHash);

      assertCall(() -> ((BSwitch) exprDb().get(hash)).subExprs())
          .throwsException(new NodeChainSizeIsWrongException(hash, switchKind, DATA_PATH, 2, 3));
    }

    @Test
    void handlers_size_is_different_than_choice_alternatives() throws Exception {
      var switchKind = bSwitchKind(bIntType());
      var choiceType = bChoiceType(bStringType(), bIntType());
      var choice = bChoice(choiceType, 0, bString("abc"));
      var handlers = bCombine(bs2iLambda(), bi2iLambda(), bi2iLambda());
      var dataHash = hash(hash(choice), hash(handlers));
      var hash = hash(hash(switchKind), dataHash);

      var expected = bTupleType(bs2iLambda().type(), bi2iLambda().type());
      var actual =
          bTupleType(bs2iLambda().type(), bi2iLambda().type(), bi2iLambda().type());
      assertCall(() -> ((BSwitch) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, switchKind, "handlers", expected, actual));
    }

    @Test
    void handler_param_type_not_matches_alternative_type() throws Exception {
      var switchKind = bSwitchKind(bIntType());
      var choiceType = bChoiceType(bStringType(), bIntType());
      var choice = bChoice(choiceType, 0, bString("abc"));
      var handlers = bCombine(bs2iLambda(), bs2iLambda());
      var dataHash = hash(hash(choice), hash(handlers));
      var hash = hash(hash(switchKind), dataHash);

      var expected = bTupleType(bs2iLambda().type(), bi2iLambda().type());
      var actual = bTupleType(bs2iLambda().type(), bs2iLambda().type());
      assertCall(() -> ((BSwitch) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, switchKind, "handlers", expected, actual));
    }

    @Test
    void handler_result_type_not_matches_switch_evaluation_type() throws Exception {
      var switchKind = bSwitchKind(bIntType());
      var choiceType = bChoiceType(bStringType(), bIntType());
      var choice = bChoice(choiceType, 0, bString("abc"));
      var handlers = bCombine(bs2iLambda(), bi2sLambda());
      var dataHash = hash(hash(choice), hash(handlers));
      var hash = hash(hash(switchKind), dataHash);

      var expected = bTupleType(bs2iLambda().type(), bi2iLambda().type());
      var actual = bTupleType(bs2iLambda().type(), bi2sLambda().type());
      assertCall(() -> ((BSwitch) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, switchKind, "handlers", expected, actual));
    }
  }

  @Nested
  class _combine {
    @Test
    void learning_test() throws Exception {
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
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bCombineKind());
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      var item1 = bInt(1);
      var item2 = bString("abc");
      var dataHash = hash(hash(item1), hash(item2));
      obj_root_with_two_data_hashes(
          bOrderKind(), dataHash, (Hash hash) -> ((BCombine) exprDb().get(hash)).subExprs());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
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
    void with_chain_element_pointing_nowhere() throws Exception {
      var nowhere = Hash.of(33);
      var hash = hash(hash(bCombineKind()), hash(nowhere));
      assertCall(() -> ((BCombine) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprNodeException(hash, bCombineKind(), DATA_PATH + "[0]"))
          .withCause(new NoSuchExprException(nowhere));
    }

    @Test
    void evaluation_type_items_size_is_different_than_actual_items_size() throws Exception {
      var item1 = bInt();
      var type = bCombineKind(bIntType(), bStringType());
      var hash = hash(hash(type), hash(hash(item1)));

      var expectedType = bTupleType(bIntType(), bStringType());
      var actualType = bTupleType(bIntType());
      assertCall(() -> ((BCombine) exprDb().get(hash)).subExprs())
          .throwsException(
              new MemberHasWrongTypeException(hash, type, "elements", expectedType, actualType));
    }

    @Test
    void evaluation_type_item_is_different_than_evaluation_type_of_one_of_items() throws Exception {
      var item1 = bInt(1);
      var item2 = bString("abc");
      var type = bCombineKind(bIntType(), bBoolType());
      var hash = hash(hash(type), hash(hash(item1), hash(item2)));

      var expectedType = bTupleType(bIntType(), bBoolType());
      var actualType = bTupleType(bIntType(), bStringType());
      assertCall(() -> ((BCombine) exprDb().get(hash)).subExprs())
          .throwsException(
              new MemberHasWrongTypeException(hash, type, "elements", expectedType, actualType));
    }
  }

  @Nested
  class _lambda {
    @Test
    void learning_test() throws Exception {
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
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bLambdaType());
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      var bodyExpr = bBool(true);
      var kind = bLambdaType(bIntType(), bStringType(), bBoolType());
      var dataHash = hash(bodyExpr);
      obj_root_with_two_data_hashes(
          kind, dataHash, (Hash hash) -> ((BLambda) exprDb().get(hash)).body());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_expr_but_nowhere(
          bLambdaType(), (Hash hash) -> ((BLambda) exprDb().get(hash)).body());
    }

    @Test
    void body_evaluation_type_is_not_equal_lambda_type_result() throws Exception {
      var body = bInt(17);
      var kind = bLambdaType(bIntType(), bStringType(), bBoolType());
      var hash = hash(hash(kind), hash(body));
      assertCall(() -> ((BLambda) exprDb().get(hash)).body())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, kind, "body", bBoolType(), bIntType()));
    }
  }

  @Nested
  class _if {
    @Test
    void learning_test() throws Exception {
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
          .isEqualTo(new BIf.BSubExprs(condition, then_, else_));
    }

    @Test
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bIfKind(bIntType()));
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      var condition = bBool(true);
      var then_ = bInt(1);
      var else_ = bInt(2);
      var dataHash = hash(hash(condition), hash(then_), hash(else_));
      obj_root_with_two_data_hashes(
          bIfKind(), dataHash, (Hash hash) -> ((BIf) exprDb().get(hash)).subExprs());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bIfKind(), (Hash hash) -> ((BIf) exprDb().get(hash)).subExprs());
    }

    @Test
    void data_is_chain_with_one_element() throws Exception {
      var condition = bBool(true);
      var dataHash = hash(hash(condition));
      var hash = hash(hash(bIfKind()), dataHash);
      assertCall(() -> ((BIf) exprDb().get(hash)).subExprs())
          .throwsException(new NodeChainSizeIsWrongException(hash, bIfKind(), DATA_PATH, 3, 1));
    }

    @Test
    void data_is_chain_with_two_elements() throws Exception {
      var condition = bBool(true);
      var then_ = bInt(1);
      var dataHash = hash(hash(condition), hash(then_));
      var hash = hash(hash(bIfKind()), dataHash);
      assertCall(() -> ((BIf) exprDb().get(hash)).subExprs())
          .throwsException(new NodeChainSizeIsWrongException(hash, bIfKind(), DATA_PATH, 3, 2));
    }

    @Test
    void data_is_chain_with_four_element() throws Exception {
      var condition = bBool(true);
      var then_ = bInt(1);
      var else_ = bInt(2);
      var dataHash = hash(hash(condition), hash(then_), hash(else_), hash(else_));
      var hash = hash(hash(bIfKind()), dataHash);
      assertCall(() -> ((BIf) exprDb().get(hash)).subExprs())
          .throwsException(new NodeChainSizeIsWrongException(hash, bIfKind(), DATA_PATH, 3, 4));
    }

    @Test
    void condition_evaluation_type_is_not_bool() throws Exception {
      var condition = bString();
      var then_ = bInt(1);
      var else_ = bInt(2);
      var dataHash = hash(hash(condition), hash(then_), hash(else_));
      var kind = bIfKind(bIntType());
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BIf) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, kind, "condition", bBoolType(), bStringType()));
    }

    @Test
    void then_evaluation_type_is_not_equal_to_if_evaluation_type() throws Exception {
      var condition = bBool();
      var then_ = bString();
      var else_ = bInt(2);
      var dataHash = hash(hash(condition), hash(then_), hash(else_));
      var kind = bIfKind(bIntType());
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BIf) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, kind, "then", bIntType(), bStringType()));
    }

    @Test
    void else_evaluation_type_is_not_equal_to_if_evaluation_type() throws Exception {
      var condition = bBool();
      var then_ = bInt(1);
      var else_ = bString();
      var dataHash = hash(hash(condition), hash(then_), hash(else_));
      var kind = bIfKind(bIntType());
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BIf) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, kind, "else", bIntType(), bStringType()));
    }
  }

  @Nested
  class _int {
    @Test
    void learning_test() throws Exception {
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
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bIntType());
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          bIntType(),
          hashedDb().writeByte((byte) 1),
          (Hash hash) -> ((BInt) exprDb().get(hash)).toJavaBigInteger());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bIntType(), (Hash hash) -> ((BInt) exprDb().get(hash)).toJavaBigInteger());
    }
  }

  @Nested
  class _map {
    @Test
    void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save MAP
       * in HashedDb.
       */
      var array = bArray(bInt(1));
      var mapper = bIntIdLambda();
      var dataHash = hash(hash(array), hash(mapper));
      var hash = hash(hash(bMapKind(bIntArrayType())), dataHash);
      assertThat(((BMap) exprDb().get(hash)).subExprs())
          .isEqualTo(new BMap.BSubExprs(array, mapper));
    }

    @Test
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bMapKind(bIntArrayType()));
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      var array = bArray(bInt(1));
      var mapper = bIntIdLambda();
      var kind = bMapKind(bIntArrayType());
      var dataHash = hash(hash(array), hash(mapper));
      obj_root_with_two_data_hashes(
          kind, dataHash, (Hash hash) -> ((BMap) exprDb().get(hash)).subExprs());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      var kind = bMapKind(bIntArrayType());
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          kind, (Hash hash) -> ((BMap) exprDb().get(hash)).subExprs());
    }

    @Test
    void data_is_chain_with_one_element() throws Exception {
      var array = bArray(bInt());
      var dataHash = hash(hash(array));
      var kind = bMapKind(bIntArrayType());
      var hash = hash(hash(kind), dataHash);
      assertCall(() -> ((BMap) exprDb().get(hash)).subExprs())
          .throwsException(new NodeChainSizeIsWrongException(hash, bMapKind(), DATA_PATH, 2, 1));
    }

    @Test
    void data_is_chain_with_three_elements() throws Exception {
      var array = bArray(bInt());
      var mapper = bIntIdLambda();
      var dataHash = hash(hash(array), hash(mapper), hash(mapper));
      var kind = bMapKind(bIntArrayType());
      var hash = hash(hash(kind), dataHash);
      assertCall(() -> ((BMap) exprDb().get(hash)).subExprs())
          .throwsException(new NodeChainSizeIsWrongException(hash, bMapKind(), DATA_PATH, 2, 3));
    }

    @Test
    void array_evaluation_type_is_not_array_type() throws Exception {
      var notArray = bInt(1);
      var mapper = bIntIdLambda();
      var dataHash = hash(hash(notArray), hash(mapper));
      var kind = bMapKind(bIntArrayType());
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BMap) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, kind, "array", "BArrayType", bIntType()));
    }

    @Test
    void mapper_evaluation_type_is_not_lambda_type() throws Exception {
      var array = bArray(bInt());
      var notMapper = bInt();
      var dataHash = hash(hash(array), hash(notMapper));
      var kind = bMapKind(bIntArrayType());
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BMap) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, kind, "mapper", "(Int)->Int", bIntType()));
    }

    @Test
    void mapper_has_more_than_one_parameter() throws Exception {
      var array = bArray(bInt());
      var mapperWithTwoParams = bLambda(list(bIntType(), bIntType()), bInt());
      var dataHash = hash(hash(array), hash(mapperWithTwoParams));
      var kind = bMapKind(bIntArrayType());
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BMap) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, kind, "mapper", "(Int)->Int", mapperWithTwoParams.type()));
    }

    @Test
    void mapper_param_type_is_different_than_array_element_type() throws Exception {
      var array = bArray(bString());
      var mapper = bIntIdLambda();
      var dataHash = hash(hash(array), hash(mapper));
      var kind = bMapKind(bIntArrayType());
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BMap) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, kind, "mapper", "(String)->Int", mapper.type()));
    }

    @Test
    void mapper_result_type_is_different_than_result_array_element_type() throws Exception {
      var array = bArray(bInt());
      var mapper = bIntIdLambda();
      var dataHash = hash(hash(array), hash(mapper));
      var kind = bMapKind(bStringArrayType());
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BMap) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, kind, "mapper", "(Int)->String", mapper.type()));
    }
  }

  @Nested
  class _invoke {
    @Test
    void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save
       * INVOKE in HashedDb.
       */
      var kind = bInvokeKind(bIntType());
      var method = bMethodTuple();
      var isPure = bBool(true);
      var arguments = bCombine(bInt());
      var hash = hash(hash(kind), hash(hash(method), hash(isPure), hash(arguments)));

      var invokeSubExprs = ((BInvoke) exprDb().get(hash)).subExprs();
      assertThat(invokeSubExprs.method()).isEqualTo(method);
      assertThat(invokeSubExprs.isPure()).isEqualTo(isPure);
      assertThat(invokeSubExprs.arguments()).isEqualTo(arguments);
    }

    @Test
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bIntType());
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      var kind = bInvokeKind(bIntType());
      var method = bMethodTuple();
      var isPure = bBool(true);
      var arguments = bCombine(bInt());
      var dataHash = hash(hash(method), hash(isPure), hash(arguments));
      obj_root_with_two_data_hashes(
          kind, dataHash, (Hash hash) -> ((BInvoke) exprDb().get(hash)).subExprs());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      var kind = bInvokeKind(bIntType());
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          kind, (Hash hash) -> ((BInvoke) exprDb().get(hash)).subExprs());
    }

    @Test
    void data_is_chain_with_one_element() throws Exception {
      var kind = bInvokeKind(bIntType());
      var method = bMethodTuple();
      var dataHash = hash(hash(method));
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BInvoke) exprDb().get(hash)).subExprs())
          .throwsException(new NodeChainSizeIsWrongException(hash, kind, DATA_PATH, 3, 1));
    }

    @Test
    void data_is_chain_with_two_elements() throws Exception {
      var kind = bInvokeKind(bIntType());
      var method = bMethodTuple();
      var isPure = bBool(true);
      var dataHash = hash(hash(method), hash(isPure));
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BInvoke) exprDb().get(hash)).subExprs())
          .throwsException(new NodeChainSizeIsWrongException(hash, kind, DATA_PATH, 3, 2));
    }

    @Test
    void data_is_chain_with_four_elements() throws Exception {
      var type = bInvokeKind(bIntType());
      var method = bMethodTuple();
      var isPure = bBool(true);
      var arguments = bCombine(bInt());
      var dataHash = hash(hash(method), hash(isPure), hash(arguments), hash(arguments));
      var hash = hash(hash(type), dataHash);

      assertCall(() -> ((BInvoke) exprDb().get(hash)).subExprs())
          .throwsException(new NodeChainSizeIsWrongException(hash, type, DATA_PATH, 3, 4));
    }

    @Test
    void method_evaluation_type_is_not_method_tuple() throws Exception {
      var kind = bInvokeKind(bIntType());
      var method = bCombine(bBlob(), bInt());
      var isPure = bBool(true);
      var arguments = bCombine(bInt());
      var hash = hash(hash(kind), hash(hash(method), hash(isPure), hash(arguments)));
      assertCall(() -> ((BInvoke) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, kind, "method", bMethodType(), bTupleType(bBlobType(), bIntType())));
    }

    @Test
    void is_pure_evaluation_type_is_not_bool() throws Exception {
      var kind = bInvokeKind(bIntType());
      var method = bMethodTuple();
      var isPure = bString();
      var arguments = bCombine(bInt());
      var hash = hash(hash(kind), hash(hash(method), hash(isPure), hash(arguments)));

      assertCall(() -> ((BInvoke) exprDb().get(hash)).subExprs().isPure())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, kind, "isPure", bBoolType(), bStringType()));
    }

    @Test
    void arguments_evaluation_type_is_not_tuple() throws Exception {
      var kind = bInvokeKind(bIntType());
      var method = bMethodTuple();
      var isPure = bBool(true);
      var arguments = bInt();
      var hash = hash(hash(kind), hash(hash(method), hash(isPure), hash(arguments)));

      assertCall(() -> ((BInvoke) exprDb().get(hash)).subExprs().arguments())
          .throwsException(new MemberHasWrongTypeException(
              hash, kind, "arguments", BTupleType.class, BIntType.class));
    }
  }

  @Nested
  class _order {
    @Test
    void learning_test() throws Exception {
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
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bOrderKind());
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      var expr1 = bInt(1);
      var expr2 = bInt(2);
      var dataHash = hash(hash(expr1), hash(expr2));
      obj_root_with_two_data_hashes(
          bOrderKind(), dataHash, (Hash hash) -> ((BOrder) exprDb().get(hash)).subExprs());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
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
    void with_chain_elem_pointing_nowhere() throws Exception {
      var nowhereHash = Hash.of(33);
      var hash = hash(hash(bOrderKind()), hash(nowhereHash));
      assertCall(() -> ((BOrder) exprDb().get(hash)).subExprs())
          .throwsException(new DecodeExprNodeException(hash, bOrderKind(), DATA_PATH + "[0]"))
          .withCause(new NoSuchExprException(nowhereHash));
    }

    @Test
    void evaluation_type_elem_is_different_than_evaluation_type_of_one_of_elems() throws Exception {
      var expr1 = bInt();
      var expr2 = bString();
      var type = bOrderKind(bIntType());
      var hash = hash(hash(type), hash(hash(expr1), hash(expr2)));
      assertCall(() -> ((BOrder) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongTypeException(
              hash, type, "elements[1]", bIntType(), bStringType()));
    }
  }

  @Nested
  class _pick {
    @Test
    void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save smooth
       * pick in HashedDb.
       */
      var pickable = bOrder(bString("abc"));
      var index = bReference(bIntType(), 7);
      var hash = hash(hash(bPickKind(bStringType())), hash(hash(pickable), hash(index)));
      assertThat(((BPick) exprDb().get(hash)).subExprs())
          .isEqualTo(new BPick.BSubExprs(pickable, index));
    }

    @Test
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bPickKind(bIntType()));
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      var index = bInt(2);
      var expr = bInt(123);
      var dataHash = hash(hash(expr), hash(index));
      obj_root_with_two_data_hashes(
          bPickKind(), dataHash, (Hash hash) -> ((BPick) exprDb().get(hash)).subExprs());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bPickKind(), (Hash hash) -> ((BPick) exprDb().get(hash)).subExprs());
    }

    @Test
    void data_is_chain_with_one_element() throws Exception {
      var expr = bInt(123);
      var dataHash = hash(hash(expr));
      var hash = hash(hash(bPickKind()), dataHash);
      assertCall(() -> ((BPick) exprDb().get(hash)).subExprs())
          .throwsException(new NodeChainSizeIsWrongException(hash, bPickKind(), DATA_PATH, 2, 1));
    }

    @Test
    void data_is_chain_with_three_elements() throws Exception {
      var index = bInt(2);
      var expr = bInt(123);
      var dataHash = hash(hash(expr), hash(index), hash(index));
      var hash = hash(hash(bPickKind()), dataHash);
      assertCall(() -> ((BPick) exprDb().get(hash)).subExprs())
          .throwsException(new NodeChainSizeIsWrongException(hash, bPickKind(), DATA_PATH, 2, 3));
    }

    @Test
    void array_is_not_array_expr() throws Exception {
      var notArray = bInt(3);
      var index = bInt(0);
      var type = bPickKind(bStringType());
      var hash = hash(hash(type), hash(hash(notArray), hash(index)));

      assertCall(() -> ((BPick) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, type, "pickable", bStringArrayType(), bIntType()));
    }

    @Test
    void index_is_not_int_expr() throws Exception {
      var type = bPickKind(bStringType());
      var pickable = bArray(bString("abc"));
      var index = bReference(bStringType(), 7);
      var hash = hash(hash(type), hash(hash(pickable), hash(index)));
      assertCall(() -> ((BPick) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, type, "index", bIntType(), bStringType()));
    }

    @Test
    void evaluation_type_is_different_than_elem_type() throws Exception {
      var tuple = bArray(bString("abc"));
      var index = bInt(0);
      var type = bPickKind(bIntType());
      var hash = hash(hash(type), hash(hash(tuple), hash(index)));

      assertCall(() -> ((BPick) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongEvaluationTypeException(
              hash, type, "pickable", bIntArrayType(), bStringArrayType()));
    }
  }

  @Nested
  class _variable {
    @Test
    void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save variable
       * in HashedDb.
       */
      var index = bInt(34);
      var hash = hash(hash(bReferenceKind(bStringType())), hash(index));
      assertThat(((BReference) exprDb().get(hash)).index()).isEqualTo(index);
    }

    @Test
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bReferenceKind());
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      var index = bInt(0);
      var dataHash = hash(index);
      obj_root_with_two_data_hashes(
          bReferenceKind(bIntType()),
          dataHash,
          (Hash hash) -> ((BReference) exprDb().get(hash)).index());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_expr_but_nowhere(
          bReferenceKind(bIntType()), (Hash hash) -> ((BReference) exprDb().get(hash)).index());
    }
  }

  @Nested
  class _select {
    @Test
    void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save smooth
       * select in HashedDb.
       */
      var tuple = bTuple(bString("abc"));
      var selectable = (BValue) tuple;
      var index = bInt(0);
      var hash = hash(hash(bSelectKind(bStringType())), hash(hash(selectable), hash(index)));
      assertThat(((BSelect) exprDb().get(hash)).subExprs())
          .isEqualTo(new BSubExprs(selectable, index));
    }

    @Test
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bSelectKind(bIntType()));
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      var index = bInt(2);
      var expr = bInt(123);
      var dataHash = hash(hash(expr), hash(index));
      obj_root_with_two_data_hashes(
          bSelectKind(), dataHash, (Hash hash) -> ((BSelect) exprDb().get(hash)).subExprs());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bSelectKind(), (Hash hash) -> ((BSelect) exprDb().get(hash)).subExprs());
    }

    @Test
    void data_is_chain_with_one_element() throws Exception {
      var expr = bInt(123);
      var dataHash = hash(hash(expr));
      var hash = hash(hash(bSelectKind()), dataHash);
      assertCall(() -> ((BSelect) exprDb().get(hash)).subExprs())
          .throwsException(new NodeChainSizeIsWrongException(hash, bSelectKind(), DATA_PATH, 2, 1));
    }

    @Test
    void data_is_chain_with_three_element() throws Exception {
      var index = bInt(2);
      var expr = bInt(123);
      var dataHash = hash(hash(expr), hash(index), hash(index));
      var hash = hash(hash(bSelectKind()), dataHash);
      assertCall(() -> ((BSelect) exprDb().get(hash)).subExprs())
          .throwsException(new NodeChainSizeIsWrongException(hash, bSelectKind(), DATA_PATH, 2, 3));
    }

    @Test
    void tuple_is_not_tuple_expr() throws Exception {
      var expr = bInt(3);
      var index = bInt(0);
      var type = bSelectKind(bStringType());
      var hash = hash(hash(type), hash(hash(expr), hash(index)));

      assertCall(() -> ((BSelect) exprDb().get(hash)).subExprs())
          .throwsException(new MemberHasWrongTypeException(
              hash, type, "tuple", BTupleType.class, BIntType.class));
    }

    @Test
    void index_is_out_of_bounds() throws Exception {
      var tuple = bTuple(bString("abc"));
      var index = bInt(1);
      var type = bSelectKind(bStringType());
      var hash = hash(hash(type), hash(hash(tuple), hash(index)));

      assertCall(() -> ((BSelect) exprDb().get(hash)).subExprs())
          .throwsException(new SelectHasIndexOutOfBoundException(hash, type, 1, 1));
    }

    @Test
    void evaluation_type_is_different_than_type_of_item_pointed_to_by_index() throws Exception {
      var tuple = bTuple(bString("abc"));
      var index = bInt(0);
      var type = bSelectKind(bIntType());
      var hash = hash(hash(type), hash(hash(tuple), hash(index)));

      assertCall(() -> ((BSelect) exprDb().get(hash)).subExprs())
          .throwsException(new SelectHasWrongEvaluationTypeException(hash, type, bStringType()));
    }

    @Test
    void index_is_string_instead_of_int() throws Exception {
      var type = bSelectKind(bStringType());
      var tuple = bTuple(bString("abc"));
      var string = bString("abc");
      var hash = hash(hash(type), hash(hash(tuple), hash(string)));
      assertCall(() -> ((BSelect) exprDb().get(hash)).subExprs())
          .throwsException(
              new MemberHasWrongTypeException(hash, type, "index", BInt.class, BString.class));
    }
  }

  @Nested
  class _string {
    @Test
    void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save bool
       * in HashedDb.
       */
      var hash = hash(hash(bStringType()), hash("aaa"));
      assertThat(((BString) exprDb().get(hash)).toJavaString()).isEqualTo("aaa");
    }

    @Test
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bStringType());
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          bStringType(),
          hashedDb().writeBoolean(true),
          (Hash hash) -> ((BString) exprDb().get(hash)).toJavaString());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bStringType(), (Hash hash) -> ((BString) exprDb().get(hash)).toJavaString());
    }

    @Test
    void data_being_invalid_utf8_chain() throws Exception {
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
    void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save tuple
       * in HashedDb.
       */
      assertThat(hash(hash(bPersonType()), hash(hash(bString("John")), hash(bString("Doe")))))
          .isEqualTo(bPerson("John", "Doe").hash());
    }

    @Test
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bPersonType());
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          bPersonType(),
          hashedDb().writeBoolean(true),
          (Hash hash) -> ((BTuple) exprDb().get(hash)).get(0));
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
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
    void with_chain_element_pointing_nowhere() throws Exception {
      var nowhereHash = Hash.of(33);
      var dataHash = hash(nowhereHash, nowhereHash);
      var hash = hash(hash(bPersonType()), dataHash);
      assertCall(() -> ((BTuple) exprDb().get(hash)).get(0))
          .throwsException(new DecodeExprNodeException(hash, bPersonType(), DATA_PATH + "[0]"))
          .withCause(new NoSuchExprException(nowhereHash));
    }

    @Test
    void with_too_few_elements() throws Exception {
      var dataHash = hash(hash(bString("John")));
      var hash = hash(hash(bPersonType()), dataHash);
      BTuple tuple = (BTuple) exprDb().get(hash);
      assertCall(() -> tuple.get(0))
          .throwsException(new NodeChainSizeIsWrongException(hash, bPersonType(), DATA_PATH, 2, 1));
    }

    @Test
    void with_too_many_elements() throws Exception {
      var dataHash = hash(hash(bString("John")), hash(bString("Doe")), hash(bString("junk")));
      var hash = hash(hash(bPersonType()), dataHash);
      var tuple = (BTuple) exprDb().get(hash);
      assertCall(() -> tuple.get(0))
          .throwsException(new NodeChainSizeIsWrongException(hash, bPersonType(), DATA_PATH, 2, 3));
    }

    @Test
    void with_element_of_wrong_type() throws Exception {
      var hash = hash(hash(bPersonType()), hash(hash(bString("John")), hash(bBool(true))));
      var tuple = (BTuple) exprDb().get(hash);
      var actualType = bTupleType(bStringType(), bBoolType());
      assertCall(() -> tuple.get(0))
          .throwsException(new MemberHasWrongTypeException(
              hash, bPersonType(), "elements", bPersonType(), actualType));
    }

    @Test
    void with_element_being_operation() throws Exception {
      var hash = hash(hash(bPersonType()), hash(hash(bString("John")), hash(bReference(1))));
      var tuple = (BTuple) exprDb().get(hash);
      assertCall(() -> tuple.get(0))
          .throwsException(new NodeClassIsWrongException(
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
        .withCause(new NoSuchExprException(dataHash));
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
