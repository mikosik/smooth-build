package org.smoothbuild.virtualmachine.bytecode.expr;

import static com.google.common.truth.Truth.assertThat;
import static okio.Okio.buffer;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.testing.TestingString.illegalString;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr.DATA_PATH;
import static org.smoothbuild.virtualmachine.bytecode.expr.exc.RootHashChainHasWrongSizeException.cannotReadRootException;
import static org.smoothbuild.virtualmachine.bytecode.expr.exc.RootHashChainHasWrongSizeException.wrongSizeOfRootChainException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import okio.ByteString;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.support.ParameterDeclarations;
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
import org.smoothbuild.virtualmachine.bytecode.expr.base.BFold;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BIf;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambda;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BLambdaRef;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BMap;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BParamRef;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSwitch;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.ChoiceHasIndexOutOfBoundException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.ChooseHasIndexOutOfBoundException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprKindException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprNodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.NoSuchExprException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.SelectHasIndexOutOfBoundException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.SelectHasWrongEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.SubExprHasWrongEvaluationTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.SubExprHasWrongTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.SubExprsCountIsWrongException;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDb;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.DecodeBooleanException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.DecodeByteException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.DecodeHashChainException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.DecodeStringException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.NoSuchDataException;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BChoiceType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BLambdaType;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.kind.exc.DecodeKindException;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;

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
      assertThat(((BString) dbGet(hash)).toJavaString()).isEqualTo("aaa");
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void merkle_root_byte_count_is_not_multiple_of_hash_size(int byteCount)
        throws IOException, HashedDbException {
      var hash = hash(ByteString.of(new byte[byteCount]));
      assertCall(() -> dbGet(hash))
          .throwsException(cannotReadRootException(hash))
          .withCause(new DecodeHashChainException(hash, byteCount % Hash.lengthInBytes()));
    }

    @Test
    void corrupted_type() throws Exception {
      var typeHash = Hash.of("not a type");
      var hash = hash(typeHash, hash("aaa"));
      assertCall(() -> dbGet(hash))
          .throwsException(new DecodeExprKindException(hash))
          .withCause(new DecodeKindException(typeHash));
    }

    @Test
    void reading_elements_from_not_stored_object_throws_exception() {
      var hash = Hash.of(33);
      assertCall(() -> dbGet(hash))
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
          ((BArray) dbGet(hash)).elements(BString.class).map(BString::toJavaString);
      assertThat(strings).containsExactly("aaa", "bbb").inOrder();
    }

    @Test
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bIntArrayType());
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          bIntArrayType(), hashedDb().writeHashChain(), (Hash hash) -> ((BArray) dbGet(hash))
              .elements(BInt.class));
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bIntArrayType(), (Hash hash) -> ((BArray) dbGet(hash)).elements(BInt.class));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_chain_size_different_than_multiple_of_hash_size(int byteCount)
        throws Exception {
      var notHashOfChain = hash(ByteString.of(new byte[byteCount]));
      var type = bStringArrayType();
      var hash = hash(hash(type), notHashOfChain);
      assertCall(() -> ((BArray) dbGet(hash)).elements(BValue.class))
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
      assertCall(() -> ((BArray) dbGet(hash)).elements(BString.class))
          .throwsException(new DecodeExprNodeException(hash, arrayType, "elements[0]"))
          .withCause(new NoSuchExprException(nowhereHash));
    }

    @Test
    void with_one_elem_of_wrong_type() throws Exception {
      var arrayType = bStringArrayType();
      var hash = hash(
          hash(arrayType),
          hash(hash(hash(bStringType()), hash("aaa")), hash(hash(bBoolType()), hash(true))));
      assertCall(() -> ((BArray) dbGet(hash)).elements(BString.class))
          .throwsException(new SubExprHasWrongTypeException(
              hash, arrayType, "elements[1]", BString.class, BBool.class));
    }

    @Test
    void with_one_elem_being_operation() throws Exception {
      var arrayType = bStringArrayType();
      var hash =
          hash(hash(arrayType), hash(hash(hash(bStringType()), hash("aaa")), hash(bParamRef(1))));
      assertCall(() -> ((BArray) dbGet(hash)).elements(BString.class))
          .throwsException(new SubExprHasWrongTypeException(
              hash, arrayType, "elements[1]", BString.class, BParamRef.class));
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
      try (var source = buffer(((BBlob) dbGet(hash)).source())) {
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
          bBlobType(), hashedDb().writeByte((byte) 1), (Hash hash) -> ((BBlob) dbGet(hash))
              .source());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bBlobType(), (Hash hash) -> ((BBlob) dbGet(hash)).source());
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
      assertThat(((BBool) dbGet(hash)).toJavaBoolean()).isEqualTo(value);
    }

    @Test
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bBoolType());
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          bBoolType(), hashedDb().writeBoolean(true), (Hash hash) -> ((BBool) dbGet(hash))
              .toJavaBoolean());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bBoolType(), (Hash hash) -> ((BBool) dbGet(hash)).toJavaBoolean());
    }

    @Test
    void empty_bytes_as_data() throws Exception {
      var dataHash = hash(ByteString.of());
      var hash = hash(hash(bBoolType()), dataHash);
      assertCall(() -> ((BBool) dbGet(hash)).toJavaBoolean())
          .throwsException(new DecodeExprNodeException(hash, bBoolType(), DATA_PATH))
          .withCause(new DecodeBooleanException(dataHash, new DecodeByteException(dataHash)));
    }

    @Test
    void more_than_one_byte_as_data() throws Exception {
      var dataHash = hash(ByteString.of((byte) 0, (byte) 0));
      var hash = hash(hash(bBoolType()), dataHash);
      assertCall(() -> ((BBool) dbGet(hash)).toJavaBoolean())
          .throwsException(new DecodeExprNodeException(hash, bBoolType(), DATA_PATH))
          .withCause(new DecodeBooleanException(dataHash, new DecodeByteException(dataHash)));
    }

    @ParameterizedTest
    @ArgumentsSource(AllByteValuesExceptZeroAndOneProvider.class)
    public void one_byte_data_not_equal_zero_nor_one(byte value) throws Exception {
      var dataHash = hash(ByteString.of(value));
      var hash = hash(hash(bBoolType()), dataHash);
      assertCall(() -> ((BBool) dbGet(hash)).toJavaBoolean())
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

      var bCall = (BCall) dbGet(hash);
      assertThat(bCall.lambda()).isEqualTo(lambda);
      assertThat(bCall.arguments()).isEqualTo(args);
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
          bCallKind(bIntType()), dataHash, (Hash hash) -> ((BCall) dbGet(hash)).lambda());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bCallKind(bIntType()), (Hash hash) -> ((BCall) dbGet(hash)).lambda());
    }

    @Test
    void data_is_chain_with_one_elem() throws Exception {
      var lambdaType = bLambdaType(bStringType(), bIntType(), bIntType());
      var lambda = bLambda(lambdaType, bInt());
      var dataHash = hash(hash(lambda));
      var kind = bCallKind(bIntType());
      var hash = hash(hash(kind), dataHash);
      assertCall(() -> ((BCall) dbGet(hash)).lambda())
          .throwsException(new SubExprsCountIsWrongException(hash, kind, DATA_PATH, 2, 1));
    }

    @Test
    void data_is_chain_with_three_elements() throws Exception {
      var lambdaType = bLambdaType(bStringType(), bIntType(), bIntType());
      var lambda = bLambda(lambdaType, bInt());
      var args = bCombine(bString(), bInt());
      var dataHash = hash(hash(lambda), hash(args), hash(args));
      var kind = bCallKind(bIntType());
      var hash = hash(hash(kind), dataHash);
      assertCall(() -> ((BCall) dbGet(hash)).lambda())
          .throwsException(new SubExprsCountIsWrongException(hash, kind, DATA_PATH, 2, 3));
    }

    @Test
    void lambda_component_evaluation_type_is_not_lambda() throws Exception {
      var notLambda = bInt(3);
      var args = bCombine(bInt());
      var type = bCallKind(bStringType());
      var hash = hash(hash(type), hash(hash(notLambda), hash(args)));
      assertCall(() -> ((BCall) dbGet(hash)).lambda())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, type, "lambda", BLambdaType.class, bIntType()));
    }

    @Test
    void arguments_is_value_instead_of_expression_with_tuple_evaluation_type() throws Exception {
      var lambdaType = bLambdaType(bStringType(), bIntType(), bIntType());
      var lambda = bLambda(lambdaType, bInt());
      var type = bCallKind(bIntType());
      var hash = hash(hash(type), hash(hash(lambda), hash(bInt())));
      assertCall(() -> ((BCall) dbGet(hash)).arguments())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
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
      assertCall(() -> ((BCall) dbGet(hash)).arguments())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, type, "arguments", bTupleType(argumentTypes), notTuple.evaluationType()));
    }

    @Test
    void evaluation_type_is_different_than_lambda_evaluation_type_result() throws Exception {
      var lambdaType = bLambdaType(bStringType(), bIntType());
      var lambda = bLambda(lambdaType, bInt());
      var args = bCombine(bString());
      var type = bCallKind(bStringType());
      var hash = hash(hash(type), hash(hash(lambda), hash(args)));
      assertCall(() -> ((BCall) dbGet(hash)).lambda())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, type, "lambda.resultType", bStringType(), bIntType()));
    }

    @Test
    void lambda_evaluation_type_params_does_not_match_args_evaluation_types() throws Exception {
      var lambdaType = bLambdaType(bStringType(), bBoolType(), bIntType());
      var lambda = bLambda(lambdaType, bInt());
      var args = bCombine(bString(), bInt());
      var kind = bCallKind(bIntType());
      var hash = hash(hash(kind), hash(hash(lambda), hash(args)));
      assertCall(() -> ((BCall) dbGet(hash)).arguments())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
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

      var bChoice = (BChoice) dbGet(hash);
      assertThat(bChoice.index()).isEqualTo(index);
      assertThat(bChoice.chosen()).isEqualTo(chosen);
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
          choiceType, dataHash, (Hash hash) -> ((BChoice) dbGet(hash)).index());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          choiceType, (Hash hash) -> ((BChoice) dbGet(hash)).index());
    }

    @Test
    void data_is_chain_with_one_element() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var index = bInt(0);
      var dataHash = hash(hash(index));
      var hash = hash(hash(choiceType), dataHash);
      assertCall(() -> ((BChoice) dbGet(hash)).index())
          .throwsException(new SubExprsCountIsWrongException(hash, bChoiceType(), DATA_PATH, 2, 1));
    }

    @Test
    void data_is_chain_with_three_elements() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var index = bInt(0);
      var chosen = bString("abc");
      var dataHash = hash(hash(index), hash(chosen), hash(chosen));
      var hash = hash(hash(choiceType), dataHash);
      assertCall(() -> ((BChoice) dbGet(hash)).index())
          .throwsException(new SubExprsCountIsWrongException(hash, bChoiceType(), DATA_PATH, 2, 3));
    }

    @Test
    void index_is_lower_than_zero() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var index = bInt(-1);
      var chosen = bString("abc");
      var dataHash = hash(hash(index), hash(chosen));
      var hash = hash(hash(choiceType), dataHash);
      assertCall(() -> ((BChoice) dbGet(hash)).index())
          .throwsException(new ChoiceHasIndexOutOfBoundException(hash, choiceType, -1, 2));
    }

    @Test
    void index_is_equal_to_choice_size() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var index = bInt(choiceType.size());
      var chosen = bString("abc");
      var dataHash = hash(hash(index), hash(chosen));
      var hash = hash(hash(choiceType), dataHash);
      assertCall(() -> ((BChoice) dbGet(hash)).index())
          .throwsException(new ChoiceHasIndexOutOfBoundException(hash, choiceType, 2, 2));
    }

    @Test
    void value_is_not_value() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var index = bInt(0);
      var chosen = bParamRef(bStringType(), 0);
      var dataHash = hash(hash(index), hash(chosen));
      var hash = hash(hash(choiceType), dataHash);
      assertCall(() -> ((BChoice) dbGet(hash)).index())
          .throwsException(new SubExprHasWrongTypeException(
              hash, choiceType, "chosen", BValue.class, BParamRef.class));
    }

    @Test
    void value_has_wrong_type() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var index = bInt(0);
      var chosen = bInt(7);
      var dataHash = hash(hash(index), hash(chosen));
      var hash = hash(hash(choiceType), dataHash);
      assertCall(() -> ((BChoice) dbGet(hash)).index())
          .throwsException(new SubExprHasWrongTypeException(
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

      var bChoose = (BChoose) dbGet(hash);
      assertThat(bChoose.index()).isEqualTo(index);
      assertThat(bChoose.chosen()).isEqualTo(chosen);
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
          chooseKind, dataHash, (Hash hash) -> ((BChoose) dbGet(hash)).index());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var chooseKind = bChooseKind(choiceType);
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          chooseKind, (Hash hash) -> ((BChoose) dbGet(hash)).index());
    }

    @Test
    void data_is_chain_with_one_element() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var chooseKind = bChooseKind(choiceType);
      var index = bInt(0);
      var dataHash = hash(hash(index));
      var hash = hash(hash(chooseKind), dataHash);
      assertCall(() -> ((BChoose) dbGet(hash)).index())
          .throwsException(new SubExprsCountIsWrongException(hash, chooseKind, DATA_PATH, 2, 1));
    }

    @Test
    void data_is_chain_with_three_elements() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var chooseKind = bChooseKind(choiceType);
      var index = bInt(0);
      var chosen = bSelect(bCombine(bString("abc")), 0);
      var dataHash = hash(hash(index), hash(chosen), hash(chosen));
      var hash = hash(hash(chooseKind), dataHash);
      assertCall(() -> ((BChoose) dbGet(hash)).index())
          .throwsException(new SubExprsCountIsWrongException(hash, chooseKind, DATA_PATH, 2, 3));
    }

    @Test
    void index_is_lower_than_zero() throws Exception {
      var choiceType = bChoiceType(bStringType(), bIntType());
      var chooseKind = bChooseKind(choiceType);
      var index = bInt(-1);
      var chosen = bSelect(bCombine(bString("abc")), 0);
      var dataHash = hash(hash(index), hash(chosen));
      var hash = hash(hash(chooseKind), dataHash);
      assertCall(() -> ((BChoose) dbGet(hash)).index())
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
      assertCall(() -> ((BChoose) dbGet(hash)).index())
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
      assertCall(() -> ((BChoose) dbGet(hash)).chosen())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
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
      var switch_ = (BSwitch) dbGet(hash);
      assertThat(switch_.choice()).isEqualTo(choice);
      assertThat(switch_.handlers()).isEqualTo(handlers);
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
          switchKind, dataHash, (Hash hash) -> ((BSwitch) dbGet(hash)).choice());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      var switchKind = bSwitchKind(bIntType());
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          switchKind, (Hash hash) -> ((BSwitch) dbGet(hash)).choice());
    }

    @Test
    void data_is_chain_with_one_element() throws Exception {
      var switchKind = bSwitchKind(bIntType());
      var choiceType = bChoiceType(bStringType(), bIntType());
      var choice = bChoice(choiceType, 0, bString("abc"));
      var dataHash = hash(hash(choice));
      var hash = hash(hash(switchKind), dataHash);
      assertCall(() -> ((BSwitch) dbGet(hash)).choice())
          .throwsException(new SubExprsCountIsWrongException(hash, switchKind, DATA_PATH, 2, 1));
    }

    @Test
    void data_is_chain_with_three_elements() throws Exception {
      var switchKind = bSwitchKind(bIntType());
      var choiceType = bChoiceType(bStringType(), bIntType());
      var choice = bChoice(choiceType, 0, bString("abc"));
      var handlers = bCombine(bs2iLambda(), bi2iLambda());
      var dataHash = hash(hash(choice), hash(handlers), hash(handlers));
      var hash = hash(hash(switchKind), dataHash);

      assertCall(() -> ((BSwitch) dbGet(hash)).choice())
          .throwsException(new SubExprsCountIsWrongException(hash, switchKind, DATA_PATH, 2, 3));
    }

    @Test
    void choice_evaluation_type_is_not_choice_type() throws Exception {
      var switchKind = bSwitchKind(bIntType());
      var choice = bInt();
      var handlers = bCombine(bs2iLambda(), bi2iLambda(), bi2iLambda());
      var dataHash = hash(hash(choice), hash(handlers));
      var hash = hash(hash(switchKind), dataHash);

      assertCall(() -> ((BSwitch) dbGet(hash)).choice())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, switchKind, "choice", BChoiceType.class, bIntType()));
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
      assertCall(() -> ((BSwitch) dbGet(hash)).handlers())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
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
      assertCall(() -> ((BSwitch) dbGet(hash)).handlers())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
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
      assertCall(() -> ((BSwitch) dbGet(hash)).handlers())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
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
      var items = ((BCombine) dbGet(hash)).items();
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
          bOrderKind(), dataHash, (Hash hash) -> ((BCombine) dbGet(hash)).items());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bCombineKind(), (Hash hash) -> ((BCombine) dbGet(hash)).items());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_chain_size_different_than_multiple_of_hash_size(int byteCount)
        throws Exception {
      var notHashOfChain = hash(ByteString.of(new byte[byteCount]));
      var hash = hash(hash(bCombineKind()), notHashOfChain);
      assertCall(() -> ((BCombine) dbGet(hash)).items())
          .throwsException(new DecodeExprNodeException(hash, bCombineKind(), DATA_PATH))
          .withCause(
              new DecodeHashChainException(notHashOfChain, byteCount % Hash.lengthInBytes()));
    }

    @Test
    void with_chain_element_pointing_nowhere() throws Exception {
      var nowhere = Hash.of(33);
      var hash = hash(hash(bCombineKind()), hash(nowhere));
      assertCall(() -> ((BCombine) dbGet(hash)).items())
          .throwsException(new DecodeExprNodeException(hash, bCombineKind(), "items[0]"))
          .withCause(new NoSuchExprException(nowhere));
    }

    @Test
    void evaluation_type_items_size_is_different_than_actual_items_size() throws Exception {
      var item1 = bInt();
      var type = bCombineKind(bIntType(), bStringType());
      var hash = hash(hash(type), hash(hash(item1)));

      var expectedType = bTupleType(bIntType(), bStringType());
      var actualType = bTupleType(bIntType());
      assertCall(() -> ((BCombine) dbGet(hash)).items())
          .throwsException(
              new SubExprHasWrongTypeException(hash, type, "items", expectedType, actualType));
    }

    @Test
    void evaluation_type_item_is_different_than_evaluation_type_of_one_of_items() throws Exception {
      var item1 = bInt(1);
      var item2 = bString("abc");
      var type = bCombineKind(bIntType(), bBoolType());
      var hash = hash(hash(type), hash(hash(item1), hash(item2)));

      var expectedType = bTupleType(bIntType(), bBoolType());
      var actualType = bTupleType(bIntType(), bStringType());
      assertCall(() -> ((BCombine) dbGet(hash)).items())
          .throwsException(
              new SubExprHasWrongTypeException(hash, type, "items", expectedType, actualType));
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

      var bLambda = (BLambda) dbGet(hash);
      assertThat(bLambda.body()).isEqualTo(body);
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
      obj_root_with_two_data_hashes(kind, dataHash, (Hash hash) -> ((BLambda) dbGet(hash)).body());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_expr_but_nowhere(
          bLambdaType(), (Hash hash) -> ((BLambda) dbGet(hash)).body(), "body");
    }

    @Test
    void body_evaluation_type_is_not_equal_lambda_type_result() throws Exception {
      var body = bInt(17);
      var kind = bLambdaType(bIntType(), bStringType(), bBoolType());
      var hash = hash(hash(kind), hash(body));
      assertCall(() -> ((BLambda) dbGet(hash)).body())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
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
      var if_ = (BIf) dbGet(hash);
      assertThat(if_.condition()).isEqualTo(condition);
      assertThat(if_.then_()).isEqualTo(then_);
      assertThat(if_.else_()).isEqualTo(else_);
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
          bIfKind(), dataHash, (Hash hash) -> ((BIf) dbGet(hash)).condition());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bIfKind(), (Hash hash) -> ((BIf) dbGet(hash)).condition());
    }

    @Test
    void data_is_chain_with_one_element() throws Exception {
      var condition = bBool(true);
      var dataHash = hash(hash(condition));
      var hash = hash(hash(bIfKind()), dataHash);
      assertCall(() -> ((BIf) dbGet(hash)).condition())
          .throwsException(new SubExprsCountIsWrongException(hash, bIfKind(), DATA_PATH, 3, 1));
    }

    @Test
    void data_is_chain_with_two_elements() throws Exception {
      var condition = bBool(true);
      var then_ = bInt(1);
      var dataHash = hash(hash(condition), hash(then_));
      var hash = hash(hash(bIfKind()), dataHash);
      assertCall(() -> ((BIf) dbGet(hash)).condition())
          .throwsException(new SubExprsCountIsWrongException(hash, bIfKind(), DATA_PATH, 3, 2));
    }

    @Test
    void data_is_chain_with_four_element() throws Exception {
      var condition = bBool(true);
      var then_ = bInt(1);
      var else_ = bInt(2);
      var dataHash = hash(hash(condition), hash(then_), hash(else_), hash(else_));
      var hash = hash(hash(bIfKind()), dataHash);
      assertCall(() -> ((BIf) dbGet(hash)).condition())
          .throwsException(new SubExprsCountIsWrongException(hash, bIfKind(), DATA_PATH, 3, 4));
    }

    @Test
    void condition_evaluation_type_is_not_bool() throws Exception {
      var condition = bString();
      var then_ = bInt(1);
      var else_ = bInt(2);
      var dataHash = hash(hash(condition), hash(then_), hash(else_));
      var kind = bIfKind(bIntType());
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BIf) dbGet(hash)).condition())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
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

      assertCall(() -> ((BIf) dbGet(hash)).condition())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
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

      assertCall(() -> ((BIf) dbGet(hash)).condition())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
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
      assertThat(((BInt) dbGet(hash)).toJavaBigInteger())
          .isEqualTo(BigInteger.valueOf(3 * 256 + 2));
    }

    @Test
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bIntType());
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          bIntType(), hashedDb().writeByte((byte) 1), (Hash hash) -> ((BInt) dbGet(hash))
              .toJavaBigInteger());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bIntType(), (Hash hash) -> ((BInt) dbGet(hash)).toJavaBigInteger());
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

      var map = (BMap) dbGet(hash);
      assertThat(map.array()).isEqualTo(array);
      assertThat(map.mapper()).isEqualTo(mapper);
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
      obj_root_with_two_data_hashes(kind, dataHash, (Hash hash) -> ((BMap) dbGet(hash)).array());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      var kind = bMapKind(bIntArrayType());
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          kind, (Hash hash) -> ((BMap) dbGet(hash)).array());
    }

    @Test
    void data_is_chain_with_one_element() throws Exception {
      var array = bArray(bInt());
      var dataHash = hash(hash(array));
      var kind = bMapKind(bIntArrayType());
      var hash = hash(hash(kind), dataHash);
      assertCall(() -> ((BMap) dbGet(hash)).array())
          .throwsException(new SubExprsCountIsWrongException(hash, bMapKind(), DATA_PATH, 2, 1));
    }

    @Test
    void data_is_chain_with_three_elements() throws Exception {
      var array = bArray(bInt());
      var mapper = bIntIdLambda();
      var dataHash = hash(hash(array), hash(mapper), hash(mapper));
      var kind = bMapKind(bIntArrayType());
      var hash = hash(hash(kind), dataHash);
      assertCall(() -> ((BMap) dbGet(hash)).array())
          .throwsException(new SubExprsCountIsWrongException(hash, bMapKind(), DATA_PATH, 2, 3));
    }

    @Test
    void array_evaluation_type_is_not_array_type() throws Exception {
      var notArray = bInt(1);
      var mapper = bIntIdLambda();
      var dataHash = hash(hash(notArray), hash(mapper));
      var kind = bMapKind(bIntArrayType());
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BMap) dbGet(hash)).array())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, kind, "array", BArrayType.class, bIntType()));
    }

    @Test
    void mapper_evaluation_type_is_not_lambda_type() throws Exception {
      var array = bArray(bInt());
      var notMapper = bInt();
      var dataHash = hash(hash(array), hash(notMapper));
      var kind = bMapKind(bIntArrayType());
      var hash = hash(hash(kind), dataHash);
      var mapperType = bLambdaType(list(bIntType()), bIntType());

      assertCall(() -> ((BMap) dbGet(hash)).array())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, kind, "mapper", mapperType, bIntType()));
    }

    @Test
    void mapper_has_more_than_one_parameter() throws Exception {
      var array = bArray(bInt());
      var mapperWithTwoParams = bLambda(list(bIntType(), bIntType()), bInt());
      var dataHash = hash(hash(array), hash(mapperWithTwoParams));
      var kind = bMapKind(bIntArrayType());
      var hash = hash(hash(kind), dataHash);
      var mapperType = bLambdaType(list(bIntType()), bIntType());

      assertCall(() -> ((BMap) dbGet(hash)).array())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, kind, "mapper", mapperType, mapperWithTwoParams.type()));
    }

    @Test
    void mapper_param_type_is_different_than_array_element_type() throws Exception {
      var array = bArray(bString());
      var mapper = bIntIdLambda();
      var dataHash = hash(hash(array), hash(mapper));
      var kind = bMapKind(bIntArrayType());
      var hash = hash(hash(kind), dataHash);
      var mapperType = bLambdaType(list(bStringType()), bIntType());

      assertCall(() -> ((BMap) dbGet(hash)).array())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, kind, "mapper", mapperType, mapper.type()));
    }

    @Test
    void mapper_result_type_is_different_than_result_array_element_type() throws Exception {
      var array = bArray(bInt());
      var mapper = bIntIdLambda();
      var dataHash = hash(hash(array), hash(mapper));
      var kind = bMapKind(bStringArrayType());
      var hash = hash(hash(kind), dataHash);
      var mapperType = bLambdaType(list(bIntType()), bStringType());

      assertCall(() -> ((BMap) dbGet(hash)).array())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, kind, "mapper", mapperType, mapper.type()));
    }
  }

  @Nested
  class _fold {
    @Test
    void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save FOLD
       * in HashedDb.
       */
      var array = bArray(bInt(1));
      var initial = bInt(0);
      var folder = bLambda(list(bIntType(), bIntType()), bInt());
      var dataHash = hash(hash(array), hash(initial), hash(folder));
      var hash = hash(hash(bFoldKind(bIntType())), dataHash);
      var fold = (BFold) dbGet(hash);
      assertThat(fold.array()).isEqualTo(array);
      assertThat(fold.initial()).isEqualTo(initial);
      assertThat(fold.folder()).isEqualTo(folder);
    }

    @Test
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bFoldKind(bIntType()));
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      var array = bArray(bInt(1));
      var initial = bInt(0);
      var folder = bLambda(list(bIntType(), bIntType()), bInt());
      var kind = bFoldKind(bIntType());
      var dataHash = hash(hash(array), hash(initial), hash(folder));
      obj_root_with_two_data_hashes(kind, dataHash, (Hash hash) -> ((BFold) dbGet(hash)).array());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      var kind = bFoldKind(bIntType());
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          kind, (Hash hash) -> ((BFold) dbGet(hash)).array());
    }

    @Test
    void data_is_chain_with_one_element() throws Exception {
      var array = bArray(bInt());
      var dataHash = hash(hash(array));
      var kind = bFoldKind(bIntType());
      var hash = hash(hash(kind), dataHash);
      assertCall(() -> ((BFold) dbGet(hash)).array())
          .throwsException(new SubExprsCountIsWrongException(hash, kind, DATA_PATH, 3, 1));
    }

    @Test
    void data_is_chain_with_two_elements() throws Exception {
      var array = bArray(bInt());
      var initial = bInt(0);
      var dataHash = hash(hash(array), hash(initial));
      var kind = bFoldKind(bIntType());
      var hash = hash(hash(kind), dataHash);
      assertCall(() -> ((BFold) dbGet(hash)).array())
          .throwsException(new SubExprsCountIsWrongException(hash, kind, DATA_PATH, 3, 2));
    }

    @Test
    void data_is_chain_with_four_elements() throws Exception {
      var array = bArray(bInt());
      var initial = bInt(0);
      var folder = bLambda(list(bIntType(), bIntType()), bInt());
      var dataHash = hash(hash(array), hash(initial), hash(folder), hash(folder));
      var kind = bFoldKind(bIntType());
      var hash = hash(hash(kind), dataHash);
      assertCall(() -> ((BFold) dbGet(hash)).array())
          .throwsException(new SubExprsCountIsWrongException(hash, kind, DATA_PATH, 3, 4));
    }

    @Test
    void array_evaluation_type_is_not_array_type() throws Exception {
      var notArray = bInt(1);
      var initial = bInt(0);
      var folder = bLambda(list(bIntType(), bIntType()), bInt());
      var dataHash = hash(hash(notArray), hash(initial), hash(folder));
      var kind = bFoldKind(bIntType());
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BFold) dbGet(hash)).array())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, kind, "array", BArrayType.class, bIntType()));
    }

    @Test
    void folder_evaluation_type_is_not_lambda_type() throws Exception {
      var array = bArray(bInt());
      var initial = bInt(0);
      var notFolder = bInt();
      var dataHash = hash(hash(array), hash(initial), hash(notFolder));
      var kind = bFoldKind(bIntType());
      var hash = hash(hash(kind), dataHash);
      var folderType = bLambdaType(list(bIntType(), bIntType()), bIntType());

      assertCall(() -> ((BFold) dbGet(hash)).array())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, kind, "folder", folderType, bIntType()));
    }

    @Test
    void folder_has_wrong_number_of_parameters() throws Exception {
      var array = bArray(bInt());
      var initial = bInt(0);
      var folderWithOneParam = bLambda(list(bIntType()), bInt());
      var dataHash = hash(hash(array), hash(initial), hash(folderWithOneParam));
      var kind = bFoldKind(bIntType());
      var hash = hash(hash(kind), dataHash);
      var folderType = bLambdaType(list(bIntType(), bIntType()), bIntType());

      assertCall(() -> ((BFold) dbGet(hash)).array())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, kind, "folder", folderType, folderWithOneParam.type()));
    }

    @Test
    void folder_first_param_type_is_different_than_initial_type() throws Exception {
      var array = bArray(bInt());
      var initial = bInt(0);
      var folderWithWrongFirstParam = bLambda(list(bStringType(), bIntType()), bInt());
      var dataHash = hash(hash(array), hash(initial), hash(folderWithWrongFirstParam));
      var kind = bFoldKind(bIntType());
      var hash = hash(hash(kind), dataHash);
      var folderType = bLambdaType(list(bIntType(), bIntType()), bIntType());

      assertCall(() -> ((BFold) dbGet(hash)).array())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, kind, "folder", folderType, folderWithWrongFirstParam.type()));
    }

    @Test
    void folder_second_param_type_is_different_than_array_element_type() throws Exception {
      var array = bArray(bInt());
      var initial = bInt(0);
      var folderWithWrongSecondParam = bLambda(list(bIntType(), bStringType()), bInt());
      var dataHash = hash(hash(array), hash(initial), hash(folderWithWrongSecondParam));
      var kind = bFoldKind(bIntType());
      var hash = hash(hash(kind), dataHash);
      var folderType = bLambdaType(list(bIntType(), bIntType()), bIntType());

      assertCall(() -> ((BFold) dbGet(hash)).array())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, kind, "folder", folderType, folderWithWrongSecondParam.type()));
    }

    @Test
    void folder_result_type_is_different_than_initial_type() throws Exception {
      var array = bArray(bInt());
      var initial = bInt(0);
      var folderWithWrongResultType = bLambda(list(bIntType(), bIntType()), bString());
      var dataHash = hash(hash(array), hash(initial), hash(folderWithWrongResultType));
      var kind = bFoldKind(bIntType());
      var hash = hash(hash(kind), dataHash);
      var folderType = bLambdaType(list(bIntType(), bIntType()), bIntType());

      assertCall(() -> ((BFold) dbGet(hash)).array())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, kind, "folder", folderType, folderWithWrongResultType.type()));
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

      var invoke = (BInvoke) dbGet(hash);
      assertThat(invoke.method()).isEqualTo(method);
      assertThat(invoke.isPure()).isEqualTo(isPure);
      assertThat(invoke.arguments()).isEqualTo(arguments);
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
          kind, dataHash, (Hash hash) -> ((BInvoke) dbGet(hash)).method());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      var kind = bInvokeKind(bIntType());
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          kind, (Hash hash) -> ((BInvoke) dbGet(hash)).method());
    }

    @Test
    void data_is_chain_with_one_element() throws Exception {
      var kind = bInvokeKind(bIntType());
      var method = bMethodTuple();
      var dataHash = hash(hash(method));
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BInvoke) dbGet(hash)).method())
          .throwsException(new SubExprsCountIsWrongException(hash, kind, DATA_PATH, 3, 1));
    }

    @Test
    void data_is_chain_with_two_elements() throws Exception {
      var kind = bInvokeKind(bIntType());
      var method = bMethodTuple();
      var isPure = bBool(true);
      var dataHash = hash(hash(method), hash(isPure));
      var hash = hash(hash(kind), dataHash);

      assertCall(() -> ((BInvoke) dbGet(hash)).method())
          .throwsException(new SubExprsCountIsWrongException(hash, kind, DATA_PATH, 3, 2));
    }

    @Test
    void data_is_chain_with_four_elements() throws Exception {
      var type = bInvokeKind(bIntType());
      var method = bMethodTuple();
      var isPure = bBool(true);
      var arguments = bCombine(bInt());
      var dataHash = hash(hash(method), hash(isPure), hash(arguments), hash(arguments));
      var hash = hash(hash(type), dataHash);

      assertCall(() -> ((BInvoke) dbGet(hash)).method())
          .throwsException(new SubExprsCountIsWrongException(hash, type, DATA_PATH, 3, 4));
    }

    @Test
    void method_evaluation_type_is_not_method_tuple() throws Exception {
      var kind = bInvokeKind(bIntType());
      var method = bCombine(bBlob(), bInt());
      var isPure = bBool(true);
      var arguments = bCombine(bInt());
      var hash = hash(hash(kind), hash(hash(method), hash(isPure), hash(arguments)));
      assertCall(() -> ((BInvoke) dbGet(hash)).method())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, kind, "method", bMethodType(), bTupleType(bBlobType(), bIntType())));
    }

    @Test
    void is_pure_evaluation_type_is_not_bool() throws Exception {
      var kind = bInvokeKind(bIntType());
      var method = bMethodTuple();
      var isPure = bString();
      var arguments = bCombine(bInt());
      var hash = hash(hash(kind), hash(hash(method), hash(isPure), hash(arguments)));

      assertCall(() -> ((BInvoke) dbGet(hash)).isPure())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, kind, "isPure", bBoolType(), bStringType()));
    }

    @Test
    void arguments_evaluation_type_is_not_tuple() throws Exception {
      var kind = bInvokeKind(bIntType());
      var method = bMethodTuple();
      var isPure = bBool(true);
      var arguments = bInt();
      var hash = hash(hash(kind), hash(hash(method), hash(isPure), hash(arguments)));

      assertCall(() -> ((BInvoke) dbGet(hash)).arguments())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, kind, "arguments", BTupleType.class, bIntType()));
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

      var order = (BOrder) dbGet(hash);
      assertThat(order.elements()).containsExactly(expr1, expr2).inOrder();
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
          bOrderKind(), dataHash, (Hash hash) -> ((BOrder) dbGet(hash)).elements());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bOrderKind(), (Hash hash) -> ((BOrder) dbGet(hash)).elements());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_chain_size_different_than_multiple_of_hash_size(int byteCount)
        throws Exception {
      var notHashOfChain = hash(ByteString.of(new byte[byteCount]));
      var hash = hash(hash(bOrderKind()), notHashOfChain);
      assertCall(() -> ((BOrder) dbGet(hash)).elements())
          .throwsException(new DecodeExprNodeException(hash, bOrderKind(), DATA_PATH))
          .withCause(
              new DecodeHashChainException(notHashOfChain, byteCount % Hash.lengthInBytes()));
    }

    @Test
    void with_chain_elem_pointing_nowhere() throws Exception {
      var nowhereHash = Hash.of(33);
      var hash = hash(hash(bOrderKind()), hash(nowhereHash));
      assertCall(() -> ((BOrder) dbGet(hash)).elements())
          .throwsException(new DecodeExprNodeException(hash, bOrderKind(), "elements[0]"))
          .withCause(new NoSuchExprException(nowhereHash));
    }

    @Test
    void evaluation_type_elem_is_different_than_evaluation_type_of_one_of_elems() throws Exception {
      var expr1 = bInt();
      var expr2 = bString();
      var type = bOrderKind(bIntType());
      var hash = hash(hash(type), hash(hash(expr1), hash(expr2)));
      assertCall(() -> ((BOrder) dbGet(hash)).elements())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
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
      var index = bParamRef(bIntType(), 7);
      var hash = hash(hash(bPickKind(bStringType())), hash(hash(pickable), hash(index)));

      var pick = (BPick) dbGet(hash);
      assertThat(pick.pickable()).isEqualTo(pickable);
      assertThat(pick.index()).isEqualTo(index);
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
          bPickKind(), dataHash, (Hash hash) -> ((BPick) dbGet(hash)).pickable());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bPickKind(), (Hash hash) -> ((BPick) dbGet(hash)).pickable());
    }

    @Test
    void data_is_chain_with_one_element() throws Exception {
      var expr = bInt(123);
      var dataHash = hash(hash(expr));
      var hash = hash(hash(bPickKind()), dataHash);
      assertCall(() -> ((BPick) dbGet(hash)).pickable())
          .throwsException(new SubExprsCountIsWrongException(hash, bPickKind(), DATA_PATH, 2, 1));
    }

    @Test
    void data_is_chain_with_three_elements() throws Exception {
      var index = bInt(2);
      var expr = bInt(123);
      var dataHash = hash(hash(expr), hash(index), hash(index));
      var hash = hash(hash(bPickKind()), dataHash);
      assertCall(() -> ((BPick) dbGet(hash)).pickable())
          .throwsException(new SubExprsCountIsWrongException(hash, bPickKind(), DATA_PATH, 2, 3));
    }

    @Test
    void array_is_not_array_expr() throws Exception {
      var notArray = bInt(3);
      var index = bInt(0);
      var type = bPickKind(bStringType());
      var hash = hash(hash(type), hash(hash(notArray), hash(index)));

      assertCall(() -> ((BPick) dbGet(hash)).pickable())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, type, "pickable", bStringArrayType(), bIntType()));
    }

    @Test
    void index_is_not_int_expr() throws Exception {
      var type = bPickKind(bStringType());
      var pickable = bArray(bString("abc"));
      var index = bParamRef(bStringType(), 7);
      var hash = hash(hash(type), hash(hash(pickable), hash(index)));
      assertCall(() -> ((BPick) dbGet(hash)).pickable())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, type, "index", bIntType(), bStringType()));
    }

    @Test
    void evaluation_type_is_different_than_elem_type() throws Exception {
      var tuple = bArray(bString("abc"));
      var index = bInt(0);
      var type = bPickKind(bIntType());
      var hash = hash(hash(type), hash(hash(tuple), hash(index)));

      assertCall(() -> ((BPick) dbGet(hash)).pickable())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, type, "pickable", bIntArrayType(), bStringArrayType()));
    }
  }

  @Nested
  class _paramRef {
    @Test
    void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save param-ref
       * in HashedDb.
       */
      var index = bInt(34);
      var hash = hash(hash(bParamRefKind(bStringType())), hash(index));

      var paramRef = (BParamRef) dbGet(hash);
      assertThat(paramRef.index()).isEqualTo(index);
    }

    @Test
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bParamRefKind());
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      var index = bInt(0);
      var dataHash = hash(index);
      obj_root_with_two_data_hashes(
          bParamRefKind(bIntType()), dataHash, (Hash hash) -> ((BParamRef) dbGet(hash)).index());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_expr_but_nowhere(
          bParamRefKind(bIntType()), (Hash hash) -> ((BParamRef) dbGet(hash)).index(), "index");
    }
  }

  @Nested
  class _lambda_ref {
    @Test
    void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save lambda-ref
       * in HashedDb.
       */
      var name = bInt(34);
      var hash = hash(hash(bLambdaRefKind(bLambdaType())), hash(name));

      var lambdaRef = (BLambdaRef) dbGet(hash);
      assertThat(lambdaRef.lambdaName()).isEqualTo(name);
    }

    @Test
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bLambdaRefKind(bLambdaType()));
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      var index = bInt(0);
      var dataHash = hash(index);
      obj_root_with_two_data_hashes(
          bLambdaRefKind(bLambdaType()), dataHash, (Hash hash) -> ((BLambdaRef) dbGet(hash))
              .lambdaName());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_expr_but_nowhere(
          bLambdaRefKind(bLambdaType()),
          (Hash hash) -> ((BLambdaRef) dbGet(hash)).lambdaName(),
          "name");
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

      var select = (BSelect) dbGet(hash);
      assertThat(select.selectable()).isEqualTo(selectable);
      assertThat(select.index()).isEqualTo(index);
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
          bSelectKind(), dataHash, (Hash hash) -> ((BSelect) dbGet(hash)).selectable());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bSelectKind(), (Hash hash) -> ((BSelect) dbGet(hash)).selectable());
    }

    @Test
    void data_is_chain_with_one_element() throws Exception {
      var expr = bInt(123);
      var dataHash = hash(hash(expr));
      var hash = hash(hash(bSelectKind()), dataHash);
      assertCall(() -> ((BSelect) dbGet(hash)).selectable())
          .throwsException(new SubExprsCountIsWrongException(hash, bSelectKind(), DATA_PATH, 2, 1));
    }

    @Test
    void data_is_chain_with_three_element() throws Exception {
      var index = bInt(2);
      var expr = bInt(123);
      var dataHash = hash(hash(expr), hash(index), hash(index));
      var hash = hash(hash(bSelectKind()), dataHash);
      assertCall(() -> ((BSelect) dbGet(hash)).selectable())
          .throwsException(new SubExprsCountIsWrongException(hash, bSelectKind(), DATA_PATH, 2, 3));
    }

    @Test
    void selectable_evaluation_type_is_not_tuple() throws Exception {
      var expr = bInt(3);
      var index = bInt(0);
      var type = bSelectKind(bStringType());
      var hash = hash(hash(type), hash(hash(expr), hash(index)));

      assertCall(() -> ((BSelect) dbGet(hash)).selectable())
          .throwsException(new SubExprHasWrongEvaluationTypeException(
              hash, type, "selectable", BTupleType.class, bIntType()));
    }

    @Test
    void index_is_out_of_bounds() throws Exception {
      var tuple = bTuple(bString("abc"));
      var index = bInt(1);
      var type = bSelectKind(bStringType());
      var hash = hash(hash(type), hash(hash(tuple), hash(index)));

      assertCall(() -> ((BSelect) dbGet(hash)).selectable())
          .throwsException(new SelectHasIndexOutOfBoundException(hash, type, 1, 1));
    }

    @Test
    void evaluation_type_is_different_than_type_of_item_pointed_to_by_index() throws Exception {
      var tuple = bTuple(bString("abc"));
      var index = bInt(0);
      var type = bSelectKind(bIntType());
      var hash = hash(hash(type), hash(hash(tuple), hash(index)));

      assertCall(() -> ((BSelect) dbGet(hash)).selectable())
          .throwsException(new SelectHasWrongEvaluationTypeException(hash, type, bStringType()));
    }

    @Test
    void index_is_string_instead_of_int() throws Exception {
      var type = bSelectKind(bStringType());
      var tuple = bTuple(bString("abc"));
      var string = bString("abc");
      var hash = hash(hash(type), hash(hash(tuple), hash(string)));
      assertCall(() -> ((BSelect) dbGet(hash)).selectable())
          .throwsException(
              new SubExprHasWrongTypeException(hash, type, "index", BInt.class, BString.class));
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

      var string = (BString) dbGet(hash);
      assertThat(string.toJavaString()).isEqualTo("aaa");
    }

    @Test
    void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(bStringType());
    }

    @Test
    void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          bStringType(), hashedDb().writeBoolean(true), (Hash hash) -> ((BString) dbGet(hash))
              .toJavaString());
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bStringType(), (Hash hash) -> ((BString) dbGet(hash)).toJavaString());
    }

    @Test
    void data_being_invalid_utf8_chain() throws Exception {
      var notStringHash = hash(illegalString());
      var hash = hash(hash(bStringType()), notStringHash);
      assertCall(() -> ((BString) dbGet(hash)).toJavaString())
          .throwsException(new DecodeExprNodeException(hash, bStringType(), DATA_PATH))
          .withCause(new DecodeStringException(notStringHash));
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
          bPersonType(), hashedDb().writeBoolean(true), (Hash hash) -> ((BTuple) dbGet(hash))
              .get(0));
    }

    @Test
    void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          bPersonType(), (Hash hash) -> ((BTuple) dbGet(hash)).get(0));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_chain_size_different_than_multiple_of_hash_size(int byteCount)
        throws Exception {
      var notChainHash = hash(ByteString.of(new byte[byteCount]));
      var hash = hash(hash(bPersonType()), notChainHash);
      assertCall(() -> ((BTuple) dbGet(hash)).get(0))
          .throwsException(new DecodeExprNodeException(hash, bPersonType(), DATA_PATH))
          .withCause(new DecodeHashChainException(notChainHash, byteCount % Hash.lengthInBytes()));
    }

    @Test
    void with_chain_element_pointing_nowhere() throws Exception {
      var nowhereHash = Hash.of(33);
      var dataHash = hash(nowhereHash, nowhereHash);
      var hash = hash(hash(bPersonType()), dataHash);
      assertCall(() -> ((BTuple) dbGet(hash)).get(0))
          .throwsException(new DecodeExprNodeException(hash, bPersonType(), "elements[0]"))
          .withCause(new NoSuchExprException(nowhereHash));
    }

    @Test
    void with_too_few_elements() throws Exception {
      var dataHash = hash(hash(bString("John")));
      var hash = hash(hash(bPersonType()), dataHash);
      BTuple tuple = (BTuple) dbGet(hash);
      assertCall(() -> tuple.get(0))
          .throwsException(
              new SubExprsCountIsWrongException(hash, bPersonType(), "elements", 2, 1));
    }

    @Test
    void with_too_many_elements() throws Exception {
      var dataHash = hash(hash(bString("John")), hash(bString("Doe")), hash(bString("junk")));
      var hash = hash(hash(bPersonType()), dataHash);
      var tuple = (BTuple) dbGet(hash);
      assertCall(() -> tuple.get(0))
          .throwsException(
              new SubExprsCountIsWrongException(hash, bPersonType(), "elements", 2, 3));
    }

    @Test
    void with_element_of_wrong_type() throws Exception {
      var hash = hash(hash(bPersonType()), hash(hash(bString("John")), hash(bBool(true))));
      var tuple = (BTuple) dbGet(hash);
      var actualType = bTupleType(bStringType(), bBoolType());
      assertCall(() -> tuple.get(0))
          .throwsException(new SubExprHasWrongTypeException(
              hash, bPersonType(), "elements", bPersonType(), actualType));
    }

    @Test
    void with_element_being_operation() throws Exception {
      var hash = hash(hash(bPersonType()), hash(hash(bString("John")), hash(bParamRef(1))));
      var tuple = (BTuple) dbGet(hash);
      assertCall(() -> tuple.get(0))
          .throwsException(new SubExprHasWrongTypeException(
              hash, bPersonType(), "elements[1]", BValue.class, BParamRef.class));
    }
  }

  private void obj_root_without_data_hash(BKind kind) throws HashedDbException {
    var hash = hash(hash(kind));
    assertCall(() -> dbGet(hash)).throwsException(wrongSizeOfRootChainException(hash, kind, 1));
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
      BKind kind, Consumer1<Hash, BytecodeException> factory, String memberName)
      throws HashedDbException {
    var dataHash = Hash.of(33);
    var hash = hash(hash(kind), dataHash);
    assertCall(() -> factory.accept(hash))
        .throwsException(new DecodeExprNodeException(hash, kind, memberName))
        .withCause(new NoSuchExprException(dataHash));
  }

  // helper methods

  @NullMarked
  private static class AllByteValuesExceptZeroAndOneProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(
        ParameterDeclarations parameterDeclarations, ExtensionContext context) {
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

  private BExpr dbGet(Hash hash) throws BytecodeException {
    return provide().exprDb().get(hash);
  }

  private HashedDb hashedDb() {
    return provide().hashedDb();
  }
}
