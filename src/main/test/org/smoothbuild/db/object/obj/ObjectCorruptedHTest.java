package org.smoothbuild.db.object.obj;

import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.db.object.obj.base.ObjectH.DATA_PATH;
import static org.smoothbuild.db.object.obj.exc.DecodeObjRootException.cannotReadRootException;
import static org.smoothbuild.db.object.obj.exc.DecodeObjRootException.wrongSizeOfRootSequenceException;
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
import org.smoothbuild.db.hashed.exc.DecodeBooleanException;
import org.smoothbuild.db.hashed.exc.DecodeByteException;
import org.smoothbuild.db.hashed.exc.DecodeHashSequenceException;
import org.smoothbuild.db.hashed.exc.DecodeStringException;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.hashed.exc.NoSuchDataException;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.exc.DecodeConstructWrongItemsSizeException;
import org.smoothbuild.db.object.obj.exc.DecodeExprWrongEvaluationTypeOfComponentException;
import org.smoothbuild.db.object.obj.exc.DecodeObjNodeException;
import org.smoothbuild.db.object.obj.exc.DecodeObjTypeException;
import org.smoothbuild.db.object.obj.exc.DecodeSelectIndexOutOfBoundsException;
import org.smoothbuild.db.object.obj.exc.DecodeSelectWrongEvaluationTypeException;
import org.smoothbuild.db.object.obj.exc.NoSuchObjException;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjNodeException;
import org.smoothbuild.db.object.obj.exc.UnexpectedObjSequenceException;
import org.smoothbuild.db.object.obj.expr.CallH;
import org.smoothbuild.db.object.obj.expr.ConstructH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.RefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.expr.SelectH.SelectData;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.DefinedFunctionH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.NativeFunctionH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.exc.DecodeTypeException;
import org.smoothbuild.db.object.type.expr.CallTypeH;
import org.smoothbuild.db.object.type.expr.ConstructTypeH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.db.object.type.val.DefinedFunctionTypeH;
import org.smoothbuild.db.object.type.val.FunctionTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ObjectCorruptedHTest extends TestingContext {
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
      assertThat(((StringH) objectHDb().get(objHash)).jValue())
          .isEqualTo("aaa");
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void merkle_root_byte_count_is_not_multiple_of_hash_size(
        int byteCount) throws IOException, HashedDbException {
      Hash objHash =
          hash(ByteString.of(new byte[byteCount]));
      assertCall(() -> objectHDb().get(objHash))
          .throwsException(cannotReadRootException(objHash, null))
          .withCause(new DecodeHashSequenceException(objHash, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void corrupted_type() throws Exception {
      Hash typeHash = Hash.of("not a type");
      Hash objHash =
          hash(
              typeHash,
              hash("aaa"));
      assertCall(() -> objectHDb().get(objHash))
          .throwsException(new DecodeObjTypeException(objHash))
          .withCause(new DecodeTypeException(typeHash));
    }

    @Test
    public void reading_elements_from_not_stored_object_throws_exception() {
      Hash objHash = Hash.of(33);
      assertCall(() -> objectHDb().get(objHash))
          .throwsException(new NoSuchObjException(objHash))
          .withCause(new NoSuchDataException(objHash));
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
      assertCall(() -> objectHDb().get(objHash))
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
      List<String> strings = ((ArrayH) objectHDb().get(objHash))
          .elements(StringH.class)
          .stream()
          .map(StringH::jValue)
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
          hashedDb().writeSequence(),
          (Hash objHash) -> ((ArrayH) objectHDb().get(objHash)).elements(IntH.class)
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          arrayHT(intHT()),
          (Hash objHash) -> ((ArrayH) objectHDb().get(objHash)).elements(IntH.class));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_sequence_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSequence = hash(ByteString.of(new byte[byteCount]));
      ArrayTypeH type = arrayHT(stringHT());
      Hash objHash =
          hash(
              hash(type),
              notHashOfSequence
          );
      assertCall(() -> ((ArrayH) objectHDb().get(objHash)).elements(ValueH.class))
          .throwsException(new DecodeObjNodeException(objHash, type, DATA_PATH))
          .withCause(new DecodeHashSequenceException(
              notHashOfSequence, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_sequence_element_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash dataHash = hash(
          nowhere
      );
      ArrayTypeH type = arrayHT(stringHT());
      Hash objHash =
          hash(
              hash(type),
              dataHash);
      assertCall(() -> ((ArrayH) objectHDb().get(objHash)).elements(StringH.class))
          .throwsException(new DecodeObjNodeException(objHash, type, DATA_PATH + "[0]"))
          .withCause(new NoSuchObjException(nowhere));
    }

    @Test
    public void with_one_element_of_wrong_types() throws Exception {
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
      assertCall(() -> ((ArrayH) objectHDb().get(objHash)).elements(StringH.class))
          .throwsException(new UnexpectedObjNodeException(
              objHash, type, DATA_PATH, 1, stringHT(), boolHT()));
    }

    @Test
    public void with_one_element_being_expr() throws Exception {
      ArrayTypeH type = arrayHT(stringHT());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(
                      hash(stringHT()),
                      hash("aaa")
                  ),
                  hash(refH(1))
              ));
      assertCall(() -> ((ArrayH) objectHDb().get(objHash)).elements(StringH.class))
          .throwsException(new UnexpectedObjNodeException(
              objHash, type, DATA_PATH, 1, ValueH.class, RefH.class));
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
      assertThat(((BlobH) objectHDb().get(objHash)).source().readByteString())
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
          (Hash objHash) -> ((BlobH) objectHDb().get(objHash)).source()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          blobHT(),
          (Hash objHash) -> ((BlobH) objectHDb().get(objHash)).source());
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
      assertThat(((BoolH) objectHDb().get(objHash)).jValue())
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
          (Hash objHash) -> ((BoolH) objectHDb().get(objHash)).jValue()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          boolHT(),
          (Hash objHash) -> ((BoolH) objectHDb().get(objHash)).jValue());
    }

    @Test
    public void empty_bytes_as_data() throws Exception {
      Hash dataHash = hash(ByteString.of());
      Hash objHash =
          hash(
              hash(boolHT()),
              dataHash);
      assertCall(() -> ((BoolH) objectHDb().get(objHash)).jValue())
          .throwsException(new DecodeObjNodeException(objHash, boolHT(), DATA_PATH))
          .withCause(new DecodeBooleanException(dataHash, new DecodeByteException(dataHash)));
    }

    @Test
    public void more_than_one_byte_as_data() throws Exception {
      Hash dataHash = hash(ByteString.of((byte) 0, (byte) 0));
      Hash objHash =
          hash(
              hash(boolHT()),
              dataHash);
      assertCall(() -> ((BoolH) objectHDb().get(objHash)).jValue())
          .throwsException(new DecodeObjNodeException(objHash, boolHT(), DATA_PATH))
          .withCause(new DecodeBooleanException(dataHash, new DecodeByteException(dataHash)));
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
      assertCall(() -> ((BoolH) objectHDb().get(objHash)).jValue())
          .throwsException(new DecodeObjNodeException(objHash, boolHT(), DATA_PATH))
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
      var functionType = definedFunctionHT(intHT(), list(stringHT(), intHT()));
      var function = definedFunctionH(functionType, intH());
      ConstructH arguments = constructH(list(stringH(), intH()));
      Hash objHash =
          hash(
              hash(callHT()),
              hash(
                  hash(function),
                  hash(arguments)
              )
          );

      assertThat(((CallH) objectHDb().get(objHash)).data().function())
          .isEqualTo(function);
      assertThat(((CallH) objectHDb().get(objHash)).data().arguments())
          .isEqualTo(arguments);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(callHT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var function = intH(0);
      var arguments = constructH(list(stringH(), intH()));
      Hash dataHash = hash(
          hash(function),
          hash(arguments)
      );
      obj_root_with_two_data_hashes(
          callHT(),
          dataHash,
          (Hash objHash) -> ((CallH) objectHDb().get(objHash)).data());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          callHT(),
          (Hash objHash) -> ((CallH) objectHDb().get(objHash)).data());
    }

    @Test
    public void data_is_sequence_with_one_element() throws Exception {
      var function = intH(0);
      Hash dataHash = hash(
          hash(function)
      );
      Hash objHash =
          hash(
              hash(callHT()),
              dataHash
          );
      assertCall(() -> ((CallH) objectHDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSequenceException(objHash, callHT(), DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_sequence_with_three_elements() throws Exception {
      var function = intH(0);
      var arguments = constructH(list(stringH(), intH()));
      Hash dataHash = hash(
          hash(function),
          hash(arguments),
          hash(arguments)
      );
      Hash objHash =
          hash(
              hash(callHT()),
              dataHash
          );
      assertCall(() -> ((CallH) objectHDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSequenceException(objHash, callHT(), DATA_PATH, 2, 3));
    }

    @Test
    public void function_component_evaluation_type_is_not_function() throws Exception {
      var function = intH(3);
      ConstructH arguments = constructH(list(stringH(), intH()));
      CallTypeH type = callHT(stringHT());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(function),
                  hash(arguments)
              )
          );
      assertCall(() -> ((CallH) objectHDb().get(objHash)).data())
          .throwsException(new DecodeExprWrongEvaluationTypeOfComponentException(
              objHash, type, "function", FunctionTypeH.class, intHT()));
    }

    @Test
    public void arguments_is_val_instead_of_expr() throws Exception {
      var functionType = definedFunctionHT(intHT(), list(stringHT(), intHT()));
      var function = definedFunctionH(functionType, intH());
      Hash objHash =
          hash(
              hash(callHT()),
              hash(
                  hash(function),
                  hash(intH())
              )
          );
      assertCall(() -> ((CallH) objectHDb().get(objHash)).data())
          .throwsException(new UnexpectedObjNodeException(
              objHash, callHT(), DATA_PATH + "[1]", ConstructH.class, IntH.class));
    }

    @Test
    public void arguments_component_evaluation_type_is_not_construct_but_different_expr()
        throws Exception {
      var functionType = definedFunctionHT(intHT(), list(stringHT(), intHT()));
      var function = definedFunctionH(functionType, intH());
      var type = callHT();
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(function),
                  hash(refH(1))
              )
          );
      assertCall(() -> ((CallH) objectHDb().get(objHash)).data())
          .throwsException(new UnexpectedObjNodeException(
              objHash, type, DATA_PATH + "[1]", ConstructH.class, RefH.class));
    }

    @Test
    public void evaluation_type_is_different_than_function_evaluation_type_result()
        throws Exception {
      DefinedFunctionTypeH functionType = definedFunctionHT(intHT(), list(stringHT()));
      var function = definedFunctionH(functionType, intH());
      var arguments = constructH(list(stringH()));
      var type = callHT(stringHT());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(function),
                  hash(arguments)
              )
          );
      assertCall(() -> ((CallH) objectHDb().get(objHash)).data())
          .throwsException(new DecodeExprWrongEvaluationTypeOfComponentException(
                  objHash, type, "function.result", stringHT(), intHT()));
    }

    @Test
    public void function_evaluation_type_parameters_does_not_match_arguments_evaluation_types()
        throws Exception {
      var functionType = definedFunctionHT(intHT(), list(stringHT(), boolHT()));
      var function = definedFunctionH(functionType, intH());
      var arguments = constructH(list(stringH(), intH()));
      var spec = callHT(intHT());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(function),
                  hash(arguments)
              )
          );
      assertCall(() -> ((CallH) objectHDb().get(objHash)).data())
          .throwsException(new DecodeExprWrongEvaluationTypeOfComponentException(
              objHash, spec, "arguments",
              tupleHT(list(stringHT(), boolHT())),
              tupleHT(list(stringHT(), intHT()))
          ));
    }
  }

  @Nested
  class _defined_function {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save Lambda
       * in HashedDb.
       */
      var bodyExpr = boolH(true);
      FunctionTypeH type = definedFunctionHT(boolHT(), list(intHT(), stringHT()));
      Hash objHash =
          hash(
              hash(type),
              hash(bodyExpr)
          );
      assertThat(((DefinedFunctionH) objectHDb().get(objHash)).body())
          .isEqualTo(bodyExpr);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(definedFunctionHT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      var bodyExpr = boolH(true);
      var type = definedFunctionHT(boolHT(), list(intHT(), stringHT()));
      var dataHash = hash(bodyExpr);
      obj_root_with_two_data_hashes(
          type,
          dataHash,
          (Hash objHash) -> ((DefinedFunctionH) objectHDb().get(objHash)).body());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_obj_but_nowhere(
          definedFunctionHT(),
          (Hash objHash) -> ((DefinedFunctionH) objectHDb().get(objHash)).body());
    }

    @Test
    public void body_evaluation_type_is_not_equal_function_type_result() throws Exception {
      var bodyExpr = intH(3);
      var type = definedFunctionHT(boolHT(), list(intHT(), stringHT()));
      Hash objHash =
          hash(
              hash(type),
              hash(bodyExpr)
          );
      assertCall(() -> ((DefinedFunctionH) objectHDb().get(objHash)).body())
          .throwsException(new DecodeExprWrongEvaluationTypeOfComponentException(
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
      var elements = ((OrderH) objectHDb().get(objHash)).elements();
      assertThat(elements)
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
          (Hash objHash) -> ((OrderH) objectHDb().get(objHash)).elements()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          orderHT(),
          (Hash objHash) -> ((OrderH) objectHDb().get(objHash)).elements());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_sequence_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSequence = hash(ByteString.of(new byte[byteCount]));
      Hash objHash =
          hash(
              hash(orderHT()),
              notHashOfSequence
          );
      assertCall(() -> ((OrderH) objectHDb().get(objHash)).elements())
          .throwsException(new DecodeObjNodeException(objHash, orderHT(), DATA_PATH))
          .withCause(new DecodeHashSequenceException(
              notHashOfSequence, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_sequence_element_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash objHash =
          hash(
              hash(orderHT()),
              hash(
                  nowhere
              )
          );
      assertCall(() -> ((OrderH) objectHDb().get(objHash)).elements())
          .throwsException(new DecodeObjNodeException(objHash, orderHT(), DATA_PATH + "[0]"))
          .withCause(new NoSuchObjException(nowhere));
    }

    @Test
    public void evaluation_type_element_is_different_than_evaluation_type_of_one_of_elements()
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
      assertCall(() -> ((OrderH) objectHDb().get(objHash)).elements())
          .throwsException(
              new DecodeExprWrongEvaluationTypeOfComponentException(
                  objHash, type, "elements[1]", intHT(), stringHT()));
    }
  }

  @Nested
  class _construct {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save Construct
       * in HashedDb.
       */
      var expr1 = intH(1);
      var expr2 = stringH("abc");
      Hash objHash =
          hash(
              hash(constructHT(list(intHT(), stringHT()))),
              hash(
                  hash(expr1),
                  hash(expr2)
              ));
      var items = ((ConstructH) objectHDb().get(objHash)).items();
      assertThat(items)
          .containsExactly(expr1, expr2)
          .inOrder();
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(constructHT());
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
          (Hash objHash) -> ((ConstructH) objectHDb().get(objHash)).items()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          constructHT(),
          (Hash objHash) -> ((ConstructH) objectHDb().get(objHash)).items());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_sequence_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSequence = hash(ByteString.of(new byte[byteCount]));
      Hash objHash =
          hash(
              hash(constructHT()),
              notHashOfSequence
          );
      assertCall(() -> ((ConstructH) objectHDb().get(objHash)).items())
          .throwsException(new DecodeObjNodeException(objHash, constructHT(), DATA_PATH))
          .withCause(new DecodeHashSequenceException(
              notHashOfSequence, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_sequence_item_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash objHash =
          hash(
              hash(constructHT()),
              hash(
                  nowhere
              )
          );
      assertCall(() -> ((ConstructH) objectHDb().get(objHash)).items())
          .throwsException(new DecodeObjNodeException(objHash, constructHT(), DATA_PATH + "[0]"))
          .withCause(new NoSuchObjException(nowhere));
    }

    @Test
    public void evaluation_type_items_size_is_different_than_actual_items_size()
        throws Exception {
      IntH expr1 =  intH();
      ConstructTypeH type = constructHT(list(intHT(), stringHT()));
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(expr1)
              ));

      assertCall(() -> ((ConstructH) objectHDb().get(objHash)).items())
          .throwsException(new DecodeConstructWrongItemsSizeException(objHash, type, 1));
    }

    @Test
    public void evaluation_type_item_is_different_than_evaluation_type_of_one_of_items()
        throws Exception {
      var expr1 = intH(1);
      var expr2 = stringH("abc");
      var type = constructHT(list(intHT(), boolHT()));
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(expr1),
                  hash(expr2)
              ));

      assertCall(() -> ((ConstructH) objectHDb().get(objHash)).items())
          .throwsException(
              new DecodeExprWrongEvaluationTypeOfComponentException(
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
      var expr = (ValueH) tuple;
      var index = intH(0);
      Hash objHash =
          hash(
              hash(selectHT(stringHT())),
              hash(
                  hash(expr),
                  hash(index)
              )
          );
      assertThat(((SelectH) objectHDb().get(objHash)).data())
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
          (Hash objHash) -> ((SelectH) objectHDb().get(objHash)).data());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          selectHT(),
          (Hash objHash) -> ((SelectH) objectHDb().get(objHash)).data());
    }

    @Test
    public void data_is_sequence_with_one_element() throws Exception {
      var expr = intH(123);
      var dataHash = hash(
          hash(expr)
      );
      Hash objHash =
          hash(
              hash(selectHT()),
              dataHash
          );
      assertCall(() -> ((SelectH) objectHDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSequenceException(
              objHash, selectHT(), DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_sequence_with_three_elements() throws Exception {
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
      assertCall(() -> ((SelectH) objectHDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSequenceException(
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

      assertCall(() -> ((SelectH) objectHDb().get(objHash)).data())
          .throwsException(new DecodeExprWrongEvaluationTypeOfComponentException(
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

      assertCall(() -> ((SelectH) objectHDb().get(objHash)).data())
          .throwsException(new DecodeSelectIndexOutOfBoundsException(objHash, type, 1, 1));
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

      assertCall(() -> ((SelectH) objectHDb().get(objHash)).data())
          .throwsException(new DecodeSelectWrongEvaluationTypeException(objHash, type, stringHT()));
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
      assertCall(() -> ((SelectH) objectHDb().get(objHash)).data())
          .throwsException(new UnexpectedObjNodeException(
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
      assertThat(((IntH) objectHDb().get(objHash)).jValue())
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
          (Hash objHash) -> ((IntH) objectHDb().get(objHash)).jValue()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          intHT(),
          (Hash objHash) -> ((IntH) objectHDb().get(objHash)).jValue());
    }
  }

  @Nested
  class _native_function {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save
       * native_function in HashedDb.
       */
      BlobH jarFile = blobH();
      StringH classBinaryName = stringH();
      BoolH isPure = boolH(true);
      Hash objHash =
          hash(
              hash(nativeFunctionHT()),
              hash(
                  hash(jarFile),
                  hash(classBinaryName),
                  hash(isPure)
              )
          );

      assertThat(((NativeFunctionH) objectHDb().get(objHash)).jarFile())
          .isEqualTo(jarFile);
      assertThat(((NativeFunctionH) objectHDb().get(objHash)).classBinaryName())
          .isEqualTo(classBinaryName);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(nativeFunctionHT());
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
          nativeFunctionHT(),
          dataHash,
          (Hash objHash) -> ((NativeFunctionH) objectHDb().get(objHash)).classBinaryName());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          nativeFunctionHT(),
          (Hash objHash) -> ((NativeFunctionH) objectHDb().get(objHash)).classBinaryName());
    }

    @Test
    public void data_is_sequence_with_two_element() throws Exception {
      BlobH jarFile = blobH();
      StringH classBinaryName = stringH();
      Hash dataHash = hash(
          hash(jarFile),
          hash(classBinaryName)
      );
      Hash objHash =
          hash(
              hash(nativeFunctionHT()),
              dataHash
          );

      assertCall(() -> ((NativeFunctionH) objectHDb().get(objHash)).classBinaryName())
          .throwsException(new UnexpectedObjSequenceException(
              objHash, nativeFunctionHT(), DATA_PATH, 3, 2));
    }

    @Test
    public void data_is_sequence_with_four_elements() throws Exception {
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
              hash(nativeFunctionHT()),
              dataHash
          );

      assertCall(() -> ((NativeFunctionH) objectHDb().get(objHash)).classBinaryName())
          .throwsException(new UnexpectedObjSequenceException(
              objHash, nativeFunctionHT(), DATA_PATH, 3, 4));
    }

    @Test
    public void jar_file_is_not_blob_value() throws Exception {
      StringH jarFile = stringH();
      StringH classBinaryName = stringH();
      BoolH isPure = boolH(true);
      Hash objHash =
          hash(
              hash(nativeFunctionHT()),
              hash(
                  hash(jarFile),
                  hash(classBinaryName),
                  hash(isPure)
              )
          );
      assertCall(() -> ((NativeFunctionH) objectHDb().get(objHash)).jarFile())
          .throwsException(new UnexpectedObjNodeException(
              objHash, nativeFunctionHT(), DATA_PATH + "[0]", BlobH.class, StringH.class));
    }

    @Test
    public void class_binary_name_is_not_string_value() throws Exception {
      BlobH jarFile = blobH();
      IntH classBinaryName = intH();
      BoolH isPure = boolH(true);
      Hash objHash =
          hash(
              hash(nativeFunctionHT()),
              hash(
                  hash(jarFile),
                  hash(classBinaryName),
                  hash(isPure)
              )
          );

      assertCall(() -> ((NativeFunctionH) objectHDb().get(objHash)).classBinaryName())
          .throwsException(new UnexpectedObjNodeException(
              objHash, nativeFunctionHT(), DATA_PATH + "[1]", StringH.class, IntH.class));
    }

    @Test
    public void is_pure_is_not_bool_value() throws Exception {
      BlobH jarFile = blobH();
      StringH classBinaryName = stringH();
      StringH isPure = stringH();
      Hash objHash =
          hash(
              hash(nativeFunctionHT()),
              hash(
                  hash(jarFile),
                  hash(classBinaryName),
                  hash(isPure)
              )
          );

      assertCall(() -> ((NativeFunctionH) objectHDb().get(objHash)).isPure())
          .throwsException(new UnexpectedObjNodeException(
              objHash, nativeFunctionHT(), DATA_PATH + "[2]", BoolH.class, StringH.class));
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
      assertCall(() -> objectHDb().get(objHash))
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
      assertThat(((StringH) objectHDb().get(objHash)).jValue())
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
          (Hash objHash) -> ((StringH) objectHDb().get(objHash)).jValue()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          stringHT(),
          (Hash objHash) -> ((StringH) objectHDb().get(objHash)).jValue());
    }

    @Test
    public void data_being_invalid_utf8_sequence() throws Exception {
      Hash notStringHash = hash(illegalString());
      Hash objHash =
          hash(
              hash(stringHT()),
              notStringHash);
      assertCall(() -> ((StringH) objectHDb().get(objHash)).jValue())
          .throwsException(new DecodeObjNodeException(objHash, stringHT(), DATA_PATH))
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
          (Hash objHash) -> ((TupleH) objectHDb().get(objHash)).get(0)
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          personHT(),
          (Hash objHash) -> ((TupleH) objectHDb().get(objHash)).get(0));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_sequence_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSequence = hash(ByteString.of(new byte[byteCount]));
      Hash objHash =
          hash(
              hash(personHT()),
              notHashOfSequence);
      assertCall(() -> ((TupleH) objectHDb().get(objHash)).get(0))
          .throwsException(new DecodeObjNodeException(objHash, personHT(), DATA_PATH))
          .withCause(new DecodeHashSequenceException(
              notHashOfSequence, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_sequence_element_pointing_nowhere() throws Exception {
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
      assertCall(() -> ((TupleH) objectHDb().get(objHash)).get(0))
          .throwsException(new DecodeObjNodeException(objHash, personHT(), DATA_PATH + "[0]"))
          .withCause(new NoSuchObjException(nowhere));
    }

    @Test
    public void with_too_few_elements() throws Exception {
      Hash dataHash =
          hash(
              hash(stringH("John")));
      Hash objHash =
          hash(
              hash(personHT()),
              dataHash);
      TupleH tuple = (TupleH) objectHDb().get(objHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new UnexpectedObjSequenceException(objHash, personHT(), DATA_PATH, 2, 1));
    }

    @Test
    public void with_too_many_elements() throws Exception {
      Hash dataHash =
          hash(
              hash(stringH("John")),
              hash(stringH("Doe")),
              hash(stringH("junk")));
      Hash objHash =
          hash(
              hash(personHT()),
              dataHash);
      TupleH tuple = (TupleH) objectHDb().get(objHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new UnexpectedObjSequenceException(objHash, personHT(), DATA_PATH, 2, 3));
    }

    @Test
    public void with_element_of_wrong_type() throws Exception {
      Hash objHash =
          hash(
              hash(personHT()),
              hash(
                  hash(stringH("John")),
                  hash(boolH(true))));
      TupleH tuple = (TupleH) objectHDb().get(objHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new UnexpectedObjNodeException(
              objHash, personHT(), DATA_PATH, 1, stringHT(), boolHT()));
    }

    @Test
    public void with_element_being_expr() throws Exception {
      Hash objHash =
          hash(
              hash(personHT()),
              hash(
                  hash(stringH("John")),
                  hash(refH(1))));
      TupleH tuple = (TupleH) objectHDb().get(objHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new UnexpectedObjNodeException(
              objHash, personHT(), DATA_PATH + "[1]", ValueH.class, RefH.class));
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
      assertThat(((RefH) objectHDb().get(objHash)).value())
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
          (Hash objHash) -> ((RefH) objectHDb().get(objHash)).value()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          refHT(),
          (Hash objHash) -> ((RefH) objectHDb().get(objHash)).value());
    }
  }

  private void obj_root_without_data_hash(TypeH type) throws HashedDbException {
    Hash objHash =
        hash(
            hash(type));
    assertCall(() -> objectHDb().get(objHash))
        .throwsException(wrongSizeOfRootSequenceException(objHash, 1));
  }

  private void obj_root_with_two_data_hashes(
      TypeH type, Hash dataHash, Function<Hash, ?> readClosure) throws HashedDbException {
    Hash objHash =
        hash(
            hash(type),
            dataHash,
            dataHash);
    assertCall(() -> readClosure.apply(objHash))
        .throwsException(wrongSizeOfRootSequenceException(objHash, 3));
  }

  private void obj_root_with_data_hash_not_pointing_to_obj_but_nowhere(
      TypeH type, Function<Hash, ?> readClosure) throws HashedDbException {
    Hash dataHash = Hash.of(33);
    Hash objHash =
        hash(
            hash(type),
            dataHash);
    assertCall(() -> readClosure.apply(objHash))
        .throwsException(new DecodeObjNodeException(objHash, type, DATA_PATH))
        .withCause(new NoSuchObjException(dataHash));
  }

  private void obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
      TypeH type, Consumer<Hash> readClosure) throws HashedDbException {
    Hash dataHash = Hash.of(33);
    Hash objHash =
        hash(
            hash(type),
            dataHash);
    assertCall(() -> readClosure.accept(objHash))
        .throwsException(new DecodeObjNodeException(objHash, type, DATA_PATH))
        .withCause(new NoSuchDataException(dataHash));
  }

  @Nested
  class _variable {
    @Test
    public void learning_test() throws Exception {
      Hash objHash =
          hash(
              hash(variableHT("A")),
              hash("aaa"));
      assertCall(() -> objectHDb().get(objHash))
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

  protected Hash hash(String string) throws HashedDbException {
    return hashedDb().writeString(string);
  }

  protected Hash hash(boolean value) throws IOException, HashedDbException {
    return hash((byte) (value ? 1 : 0));
  }

  protected Hash hash(byte value) throws IOException, HashedDbException {
    try (HashingBufferedSink sink = hashedDb().sink()) {
      sink.writeByte(value);
      sink.close();
      return sink.hash();
    }
  }

  protected Hash hash(ByteString bytes) throws IOException, HashedDbException {
    try (HashingBufferedSink sink = hashedDb().sink()) {
      sink.write(bytes);
      sink.close();
      return sink.hash();
    }
  }

  protected Hash hash(ObjectH obj) {
    return obj.hash();
  }

  protected Hash hash(TypeH type) {
    return type.hash();
  }

  protected Hash hash(Hash... hashes) throws HashedDbException {
    return hashedDb().writeSequence(hashes);
  }
}
