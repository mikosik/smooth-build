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
import org.smoothbuild.db.object.obj.base.ExprH;
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
import org.smoothbuild.db.object.obj.expr.ConstH;
import org.smoothbuild.db.object.obj.expr.ConstructH;
import org.smoothbuild.db.object.obj.expr.OrderH;
import org.smoothbuild.db.object.obj.expr.RefH;
import org.smoothbuild.db.object.obj.expr.SelectH;
import org.smoothbuild.db.object.obj.expr.SelectH.SelectData;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BoolH;
import org.smoothbuild.db.object.obj.val.FunctionH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.NativeMethodH;
import org.smoothbuild.db.object.obj.val.StringH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.exc.DecodeTypeException;
import org.smoothbuild.db.object.type.expr.CallTypeH;
import org.smoothbuild.db.object.type.expr.ConstTypeH;
import org.smoothbuild.db.object.type.expr.ConstructTypeH;
import org.smoothbuild.db.object.type.expr.OrderTypeH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.db.object.type.val.FunctionTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

import okio.ByteString;

public class CorruptedObjectHTest extends TestingContext {
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
              hash(stringOT()),
              hash("aaa"));
      assertThat(((StringH) objectDb().get(objHash)).jValue())
          .isEqualTo("aaa");
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void merkle_root_byte_count_is_not_multiple_of_hash_size(
        int byteCount) throws IOException, HashedDbException {
      Hash objHash =
          hash(ByteString.of(new byte[byteCount]));
      assertCall(() -> objectDb().get(objHash))
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
      assertCall(() -> objectDb().get(objHash))
          .throwsException(new DecodeObjTypeException(objHash))
          .withCause(new DecodeTypeException(typeHash));
    }

    @Test
    public void reading_elements_from_not_stored_object_throws_exception() {
      Hash objHash = Hash.of(33);
      assertCall(() -> objectDb().get(objHash))
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
              hash(anyOT()),
              hash("aaa"));
      assertCall(() -> objectDb().get(objHash))
          .throwsException(new UnsupportedOperationException(
              "Cannot create object for ANY type."));
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
              hash(arrayOT(stringOT())),
              hash(
                  hash(
                      hash(stringOT()),
                      hash("aaa")
                  ),
                  hash(
                      hash(stringOT()),
                      hash("bbb")
                  )
              ));
      List<String> strings = ((ArrayH) objectDb().get(objHash))
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
      obj_root_without_data_hash(arrayOT(intOT()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          arrayOT(intOT()),
          hashedDb().writeSequence(),
          (Hash objHash) -> ((ArrayH) objectDb().get(objHash)).elements(IntH.class)
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          arrayOT(intOT()),
          (Hash objHash) -> ((ArrayH) objectDb().get(objHash)).elements(IntH.class));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_sequence_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSequence = hash(ByteString.of(new byte[byteCount]));
      ArrayTypeH type = arrayOT(stringOT());
      Hash objHash =
          hash(
              hash(type),
              notHashOfSequence
          );
      assertCall(() -> ((ArrayH) objectDb().get(objHash)).elements(ValueH.class))
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
      ArrayTypeH type = arrayOT(stringOT());
      Hash objHash =
          hash(
              hash(type),
              dataHash);
      assertCall(() -> ((ArrayH) objectDb().get(objHash)).elements(StringH.class))
          .throwsException(new DecodeObjNodeException(objHash, type, DATA_PATH + "[0]"))
          .withCause(new NoSuchObjException(nowhere));
    }

    @Test
    public void with_one_element_of_wrong_types() throws Exception {
      ArrayTypeH type = arrayOT(stringOT());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(
                      hash(stringOT()),
                      hash("aaa")
                  ),
                  hash(
                      hash(boolOT()),
                      hash(true)
                  )
              ));
      assertCall(() -> ((ArrayH) objectDb().get(objHash)).elements(StringH.class))
          .throwsException(new UnexpectedObjNodeException(
              objHash, type, DATA_PATH, 1, stringOT(), boolOT()));
    }

    @Test
    public void with_one_element_being_expr() throws Exception {
      ArrayTypeH type = arrayOT(stringOT());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(
                      hash(stringOT()),
                      hash("aaa")
                  ),
                  hash(intExpr(13))
              ));
      assertCall(() -> ((ArrayH) objectDb().get(objHash)).elements(StringH.class))
          .throwsException(new UnexpectedObjNodeException(
              objHash, type, DATA_PATH, 1, stringOT(), constOT(intOT())));
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
              hash(blobOT()),
              hash(byteString));
      assertThat(((BlobH) objectDb().get(objHash)).source().readByteString())
          .isEqualTo(byteString);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(blobOT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          blobOT(),
          hashedDb().writeByte((byte) 1),
          (Hash objHash) -> ((BlobH) objectDb().get(objHash)).source()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          blobOT(),
          (Hash objHash) -> ((BlobH) objectDb().get(objHash)).source());
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
              hash(boolOT()),
              hash(value));
      assertThat(((BoolH) objectDb().get(objHash)).jValue())
          .isEqualTo(value);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(boolOT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          boolOT(),
          hashedDb().writeBoolean(true),
          (Hash objHash) -> ((BoolH) objectDb().get(objHash)).jValue()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          boolOT(),
          (Hash objHash) -> ((BoolH) objectDb().get(objHash)).jValue());
    }

    @Test
    public void empty_bytes_as_data() throws Exception {
      Hash dataHash = hash(ByteString.of());
      Hash objHash =
          hash(
              hash(boolOT()),
              dataHash);
      assertCall(() -> ((BoolH) objectDb().get(objHash)).jValue())
          .throwsException(new DecodeObjNodeException(objHash, boolOT(), DATA_PATH))
          .withCause(new DecodeBooleanException(dataHash, new DecodeByteException(dataHash)));
    }

    @Test
    public void more_than_one_byte_as_data() throws Exception {
      Hash dataHash = hash(ByteString.of((byte) 0, (byte) 0));
      Hash objHash =
          hash(
              hash(boolOT()),
              dataHash);
      assertCall(() -> ((BoolH) objectDb().get(objHash)).jValue())
          .throwsException(new DecodeObjNodeException(objHash, boolOT(), DATA_PATH))
          .withCause(new DecodeBooleanException(dataHash, new DecodeByteException(dataHash)));
    }

    @ParameterizedTest
    @ArgumentsSource(AllByteValuesExceptZeroAndOneProvider.class)
    public void one_byte_data_not_equal_zero_nor_one(byte value)
        throws Exception {
      Hash dataHash = hash(ByteString.of(value));
      Hash objHash =
          hash(
              hash(boolOT()),
              dataHash);
      assertCall(() -> ((BoolH) objectDb().get(objHash)).jValue())
          .throwsException(new DecodeObjNodeException(objHash, boolOT(), DATA_PATH))
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
      var functionType = functionOT(intOT(), list(stringOT(), intOT()));
      var function = function(functionType, intExpr());
      ConstH functionConst = const_(function);
      ConstructH arguments = construct(list(stringExpr(), intExpr()));
      Hash objHash =
          hash(
              hash(callOT()),
              hash(
                  hash(functionConst),
                  hash(arguments)
              )
          );

      assertThat(((CallH) objectDb().get(objHash)).data().function())
          .isEqualTo(functionConst);
      assertThat(((CallH) objectDb().get(objHash)).data().arguments())
          .isEqualTo(arguments);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(callOT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      ConstH function = intExpr(0);
      ConstructH arguments = construct(list(stringExpr(), intExpr()));
      Hash dataHash = hash(
          hash(function),
          hash(arguments)
      );
      obj_root_with_two_data_hashes(
          callOT(),
          dataHash,
          (Hash objHash) -> ((CallH) objectDb().get(objHash)).data());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          callOT(),
          (Hash objHash) -> ((CallH) objectDb().get(objHash)).data());
    }

    @Test
    public void data_is_sequence_with_one_element() throws Exception {
      ConstH function = intExpr(0);
      Hash dataHash = hash(
          hash(function)
      );
      Hash objHash =
          hash(
              hash(callOT()),
              dataHash
          );
      assertCall(() -> ((CallH) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSequenceException(objHash, callOT(), DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_sequence_with_three_elements() throws Exception {
      ConstH function = intExpr(0);
      ConstructH arguments = construct(list(stringExpr(), intExpr()));
      Hash dataHash = hash(
          hash(function),
          hash(arguments),
          hash(arguments)
      );
      Hash objHash =
          hash(
              hash(callOT()),
              dataHash
          );
      assertCall(() -> ((CallH) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSequenceException(objHash, callOT(), DATA_PATH, 2, 3));
    }

    @Test
    public void function_is_val_instead_of_expr() throws Exception {
      IntH val = int_(0);
      ConstructH arguments = construct(list(stringExpr(), intExpr()));
      Hash objHash =
          hash(
              hash(callOT()),
              hash(
                  hash(val),
                  hash(arguments)
              )
          );
      assertCall(() -> ((CallH) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjNodeException(
              objHash, callOT(), DATA_PATH + "[0]", ExprH.class, IntH.class));
    }

    @Test
    public void function_component_evaluation_type_is_not_function() throws Exception {
      ConstH function = intExpr(3);
      ConstructH arguments = construct(list(stringExpr(), intExpr()));
      CallTypeH type = callOT(stringOT());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(function),
                  hash(arguments)
              )
          );
      assertCall(() -> ((CallH) objectDb().get(objHash)).data())
          .throwsException(new DecodeExprWrongEvaluationTypeOfComponentException(
              objHash, type, "function", FunctionTypeH.class, intOT()));
    }

    @Test
    public void arguments_is_val_instead_of_expr() throws Exception {
      var functionType = functionOT(intOT(), list(stringOT(), intOT()));
      var function = function(functionType, intExpr());
      ConstH functionConst = const_(function);
      Hash objHash =
          hash(
              hash(callOT()),
              hash(
                  hash(functionConst),
                  hash(int_())
              )
          );
      assertCall(() -> ((CallH) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjNodeException(
              objHash, callOT(), DATA_PATH + "[1]", ConstructH.class, IntH.class));
    }

    @Test
    public void arguments_component_evaluation_type_is_not_construct_but_different_expr()
        throws Exception {
      var functionType = functionOT(intOT(), list(stringOT(), intOT()));
      var function = function(functionType, intExpr());
      ConstH functionConst = const_(function);
      CallTypeH type = callOT();
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(functionConst),
                  hash(intExpr())
              )
          );
      assertCall(() -> ((CallH) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjNodeException(
              objHash, type, DATA_PATH + "[1]", ConstructH.class, ConstH.class));
    }

    @Test
    public void evaluation_type_is_different_than_function_evaluation_type_result()
        throws Exception {
      ConstH function = const_(function(functionOT(intOT(), list(stringOT())), intExpr()));
      ConstructH arguments = construct(list(stringExpr(), intExpr()));
      CallTypeH type = callOT(stringOT());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(function),
                  hash(arguments)
              )
          );
      assertCall(() -> ((CallH) objectDb().get(objHash)).data())
          .throwsException(new DecodeExprWrongEvaluationTypeOfComponentException(
                  objHash, type, "function.result", stringOT(), intOT()));
    }

    @Test
    public void function_evaluation_type_parameters_does_not_match_arguments_evaluation_types()
        throws Exception {
      FunctionTypeH functionType = functionOT(intOT(), list(stringOT(), boolOT()));
      ConstH function = const_(function(functionType, intExpr()));
      ConstructH arguments = construct(list(stringExpr(), intExpr()));
      CallTypeH spec = callOT(intOT());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(function),
                  hash(arguments)
              )
          );
      assertCall(() -> ((CallH) objectDb().get(objHash)).data())
          .throwsException(new DecodeExprWrongEvaluationTypeOfComponentException(
              objHash, spec, "arguments",
              tupleOT(list(stringOT(), boolOT())),
              tupleOT(list(stringOT(), intOT()))
          ));
    }
  }

  @Nested
  class _const {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save const
       * in HashedDb.
       */
      ValueH val = int_(123);
      Hash objHash =
          hash(
              hash(constOT()),
              hash(val));
      assertThat(((ConstH) objectDb().get(objHash)).value())
          .isEqualTo(val);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(constOT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          constOT(),
          int_(123).hash(),
          (Hash objHash) -> ((ConstH) objectDb().get(objHash)).value()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_obj_but_nowhere(
          constOT(),
          (Hash objHash) -> ((ConstH) objectDb().get(objHash)).value());
    }

    @Test
    public void data_hash_pointing_to_expr_instead_of_value() throws Exception {
      ValueH val = int_(123);
      Hash exprHash =
          hash(
              hash(constOT()),
              hash(val));
      Hash objHash =
          hash(
              hash(constOT()),
              exprHash);
      assertCall(() -> ((ConstH) objectDb().get(objHash)).value())
          .throwsException(new UnexpectedObjNodeException(
              objHash, constOT(), DATA_PATH, ValueH.class, ConstH.class));
    }

    @Test
    public void evaluation_type_is_different_than_type_of_wrapped_value()
        throws Exception {
      ValueH val = int_(123);
      ConstTypeH type = constOT(stringOT());
      Hash objHash =
          hash(
              hash(type),
              hash(val));
      assertCall(() -> ((ConstH) objectDb().get(objHash)).value())
          .throwsException(
              new UnexpectedObjNodeException(objHash, type, DATA_PATH, stringOT(), intOT()));
    }
  }

  @Nested
  class _function {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save Lambda
       * in HashedDb.
       */
      ConstH bodyExpr = boolExpr();
      FunctionTypeH type = functionOT(boolOT(), list(intOT(), stringOT()));
      Hash objHash =
          hash(
              hash(type),
              hash(bodyExpr)
          );
      assertThat(((FunctionH) objectDb().get(objHash)).body())
          .isEqualTo(bodyExpr);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(functionOT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      ConstH bodyExpr = boolExpr();
      FunctionTypeH type = functionOT(boolOT(), list(intOT(), stringOT()));
      Hash dataHash = hash(bodyExpr);
      obj_root_with_two_data_hashes(
          type,
          dataHash,
          (Hash objHash) -> ((FunctionH) objectDb().get(objHash)).body());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_obj_but_nowhere(
          functionOT(),
          (Hash objHash) -> ((FunctionH) objectDb().get(objHash)).body());
    }

    @Test
    public void body_is_val_instead_of_expr() throws Exception {
      BoolH bodyExpr = bool(true);
      FunctionTypeH type = functionOT(boolOT(), list(intOT(), stringOT()));
      Hash objHash =
          hash(
              hash(type),
              hash(bodyExpr)
          );
      assertCall(() -> ((FunctionH) objectDb().get(objHash)).body())
          .throwsException(new UnexpectedObjNodeException(
              objHash, type, DATA_PATH, ExprH.class, BoolH.class));
    }

    @Test
    public void body_evaluation_type_is_not_equal_function_type_result() throws Exception {
      ConstH bodyExpr = intExpr(3);
      FunctionTypeH type = functionOT(boolOT(), list(intOT(), stringOT()));
      Hash objHash =
          hash(
              hash(type),
              hash(bodyExpr)
          );
      assertCall(() -> ((FunctionH) objectDb().get(objHash)).body())
          .throwsException(new DecodeExprWrongEvaluationTypeOfComponentException(
              objHash, type, DATA_PATH, intOT(), boolOT()));
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
      ConstH expr1 = intExpr(1);
      ConstH expr2 = intExpr(2);
      Hash objHash =
          hash(
              hash(orderOT()),
              hash(
                  hash(expr1),
                  hash(expr2)
              ));
      ImmutableList<ExprH> elements = ((OrderH) objectDb().get(objHash)).elements();
      assertThat(elements)
          .containsExactly(expr1, expr2)
          .inOrder();
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(orderOT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      ConstH expr1 = intExpr(1);
      ConstH expr2 = intExpr(2);
      Hash dataHash = hash(
          hash(expr1),
          hash(expr2)
      );
      obj_root_with_two_data_hashes(
          orderOT(),
          dataHash,
          (Hash objHash) -> ((OrderH) objectDb().get(objHash)).elements()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          orderOT(),
          (Hash objHash) -> ((OrderH) objectDb().get(objHash)).elements());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_sequence_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSequence = hash(ByteString.of(new byte[byteCount]));
      Hash objHash =
          hash(
              hash(orderOT()),
              notHashOfSequence
          );
      assertCall(() -> ((OrderH) objectDb().get(objHash)).elements())
          .throwsException(new DecodeObjNodeException(objHash, orderOT(), DATA_PATH))
          .withCause(new DecodeHashSequenceException(
              notHashOfSequence, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_sequence_element_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash objHash =
          hash(
              hash(orderOT()),
              hash(
                  nowhere
              )
          );
      assertCall(() -> ((OrderH) objectDb().get(objHash)).elements())
          .throwsException(new DecodeObjNodeException(objHash, orderOT(), DATA_PATH + "[0]"))
          .withCause(new NoSuchObjException(nowhere));
    }

    @Test
    public void with_one_element_being_val() throws Exception {
      ConstH expr1 = intExpr(1);
      IntH val = int_(123);
      Hash objHash =
          hash(
              hash(orderOT()),
              hash(
                  hash(expr1),
                  hash(val)
              ));
      assertCall(() -> ((OrderH) objectDb().get(objHash)).elements())
          .throwsException(new UnexpectedObjNodeException(
              objHash, orderOT(), DATA_PATH + "[1]", ExprH.class, IntH.class));
    }

    @Test
    public void evaluation_type_element_is_different_than_evaluation_type_of_one_of_elements()
        throws Exception {
      ConstH expr1 = intExpr();
      ConstH expr2 = stringExpr();
      OrderTypeH type = orderOT(intOT());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(expr1),
                  hash(expr2)
              ));
      assertCall(() -> ((OrderH) objectDb().get(objHash)).elements())
          .throwsException(
              new DecodeExprWrongEvaluationTypeOfComponentException(
                  objHash, type, "elements[1]", intOT(), stringOT()));
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
      ConstH expr1 = intExpr(1);
      ConstH expr2 = stringExpr("abc");
      Hash objHash =
          hash(
              hash(constructOT(list(intOT(), stringOT()))),
              hash(
                  hash(expr1),
                  hash(expr2)
              ));
      ImmutableList<ExprH> items = ((ConstructH) objectDb().get(objHash)).items();
      assertThat(items)
          .containsExactly(expr1, expr2)
          .inOrder();
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(constructOT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      ConstH expr1 = intExpr(1);
      ConstH expr2 = stringExpr("abc");
      Hash dataHash = hash(
          hash(expr1),
          hash(expr2)
      );
      obj_root_with_two_data_hashes(
          orderOT(),
          dataHash,
          (Hash objHash) -> ((ConstructH) objectDb().get(objHash)).items()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          constructOT(),
          (Hash objHash) -> ((ConstructH) objectDb().get(objHash)).items());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_sequence_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSequence = hash(ByteString.of(new byte[byteCount]));
      Hash objHash =
          hash(
              hash(constructOT()),
              notHashOfSequence
          );
      assertCall(() -> ((ConstructH) objectDb().get(objHash)).items())
          .throwsException(new DecodeObjNodeException(objHash, constructOT(), DATA_PATH))
          .withCause(new DecodeHashSequenceException(
              notHashOfSequence, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_sequence_item_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash objHash =
          hash(
              hash(constructOT()),
              hash(
                  nowhere
              )
          );
      assertCall(() -> ((ConstructH) objectDb().get(objHash)).items())
          .throwsException(new DecodeObjNodeException(objHash, constructOT(), DATA_PATH + "[0]"))
          .withCause(new NoSuchObjException(nowhere));
    }

    @Test
    public void with_one_item_being_val() throws Exception {
      ConstH expr1 = intExpr(1);
      IntH val = int_(123);
      Hash objHash =
          hash(
              hash(constructOT(list(intOT(), stringOT()))),
              hash(
                  hash(expr1),
                  hash(val)
              ));

      assertCall(() -> ((ConstructH) objectDb().get(objHash)).items())
          .throwsException(new UnexpectedObjNodeException(
              objHash, constructOT(), DATA_PATH + "[1]", ExprH.class, IntH.class));
    }

    @Test
    public void evaluation_type_items_size_is_different_than_actual_items_size()
        throws Exception {
      ConstH expr1 = intExpr(1);
      ConstructTypeH type = constructOT(list(intOT(), stringOT()));
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(expr1)
              ));

      assertCall(() -> ((ConstructH) objectDb().get(objHash)).items())
          .throwsException(new DecodeConstructWrongItemsSizeException(objHash, type, 1));
    }

    @Test
    public void evaluation_type_item_is_different_than_evaluation_type_of_one_of_items()
        throws Exception {
      ConstH expr1 = intExpr(1);
      ConstH expr2 = stringExpr("abc");
      ConstructTypeH type = constructOT(list(intOT(), boolOT()));
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(expr1),
                  hash(expr2)
              ));

      assertCall(() -> ((ConstructH) objectDb().get(objHash)).items())
          .throwsException(
              new DecodeExprWrongEvaluationTypeOfComponentException(
                  objHash, type, "items[1]", boolOT(), stringOT()));
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
      var tupleType = tupleOT(list(stringOT()));
      var tuple = tuple(tupleType, list(string("abc")));
      var expr = const_(tuple);
      var index = int_(0);
      Hash objHash =
          hash(
              hash(selectOT(stringOT())),
              hash(
                  hash(expr),
                  hash(index)
              )
          );
      assertThat(((SelectH) objectDb().get(objHash)).data())
          .isEqualTo(new SelectData(expr, index));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(selectOT(intOT()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      ValueH index = int_(2);
      ConstH expr = const_(int_(123));
      Hash dataHash = hash(
          hash(expr),
          hash(index)
      );
      obj_root_with_two_data_hashes(
          selectOT(),
          dataHash,
          (Hash objHash) -> ((SelectH) objectDb().get(objHash)).data());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          selectOT(),
          (Hash objHash) -> ((SelectH) objectDb().get(objHash)).data());
    }

    @Test
    public void data_is_sequence_with_one_element() throws Exception {
      ConstH expr = const_(int_(123));
      Hash dataHash = hash(
          hash(expr)
      );
      Hash objHash =
          hash(
              hash(selectOT()),
              dataHash
          );
      assertCall(() -> ((SelectH) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSequenceException(
              objHash, selectOT(), DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_sequence_with_three_elements() throws Exception {
      ValueH index = int_(2);
      ConstH expr = const_(int_(123));
      Hash dataHash = hash(
          hash(expr),
          hash(index),
          hash(index)
      );
      Hash objHash =
          hash(
              hash(selectOT()),
              dataHash
          );
      assertCall(() -> ((SelectH) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSequenceException(
              objHash, selectOT(), DATA_PATH, 2, 3));
    }

    @Test
    public void tuple_is_val_instead_of_expr() throws Exception {
      ValueH val = int_(2);
      ValueH index = int_(2);
      Hash objHash =
          hash(
              hash(selectOT()),
              hash(
                  hash(val),
                  hash(index)
              )
          );
      assertCall(() -> ((SelectH) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjNodeException(
              objHash, selectOT(), DATA_PATH + "[0]", ExprH.class, IntH.class));
    }

    @Test
    public void tuple_is_not_tuple_expr() throws Exception {
      var expr = intExpr(3);
      var index = int_(0);
      var type = selectOT(stringOT());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(expr),
                  hash(index)
              )
          );

      assertCall(() -> ((SelectH) objectDb().get(objHash)).data())
          .throwsException(new DecodeExprWrongEvaluationTypeOfComponentException(
              objHash, type, "tuple", TupleTypeH.class, intOT()));
    }

    @Test
    public void index_is_out_of_bounds() throws Exception {
      var tupleType = tupleOT(list(stringOT()));
      var tuple = tuple(tupleType, list(string("abc")));
      var expr = const_(tuple);
      var index = int_(1);
      var type = selectOT(stringOT());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(expr),
                  hash(index)
              )
          );

      assertCall(() -> ((SelectH) objectDb().get(objHash)).data())
          .throwsException(new DecodeSelectIndexOutOfBoundsException(objHash, type, 1, 1));
    }

    @Test
    public void evaluation_type_is_different_than_type_of_item_pointed_to_by_index()
        throws Exception {
      var tupleType = tupleOT(list(stringOT()));
      var tuple = tuple(tupleType, list(string("abc")));
      var expr = const_(tuple);
      var index = int_(0);
      var type = selectOT(intOT());
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(expr),
                  hash(index)
              )
          );

      assertCall(() -> ((SelectH) objectDb().get(objHash)).data())
          .throwsException(new DecodeSelectWrongEvaluationTypeException(objHash, type, stringOT()));
    }

    @Test
    public void index_is_string_instead_of_int() throws Exception {
      var type = selectOT(stringOT());
      var tupleType = tupleOT(list(stringOT()));
      var tuple = tuple(tupleType, list(string("abc")));
      var expr = const_(tuple);
      var strVal = string("abc");
      Hash objHash =
          hash(
              hash(type),
              hash(
                  hash(expr),
                  hash(strVal)
              )
          );
      assertCall(() -> ((SelectH) objectDb().get(objHash)).data())
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
              hash(intOT()),
              hash(byteString));
      assertThat(((IntH) objectDb().get(objHash)).jValue())
          .isEqualTo(BigInteger.valueOf(3 * 256 + 2));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(intOT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          intOT(),
          hashedDb().writeByte((byte) 1),
          (Hash objHash) -> ((IntH) objectDb().get(objHash)).jValue()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          intOT(),
          (Hash objHash) -> ((IntH) objectDb().get(objHash)).jValue());
    }
  }

  @Nested
  class _native_method {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save native_method
       * in HashedDb.
       */
      BlobH jarFile = blob();
      StringH classBinaryName = string();
      Hash objHash =
          hash(
              hash(nativeMethodOT()),
              hash(
                  hash(jarFile),
                  hash(classBinaryName)
              )
          );

      assertThat(((NativeMethodH) objectDb().get(objHash)).jarFile())
          .isEqualTo(jarFile);
      assertThat(((NativeMethodH) objectDb().get(objHash)).classBinaryName())
          .isEqualTo(classBinaryName);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(nativeMethodOT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      BlobH jarFile = blob();
      StringH classBinaryName = string();
      Hash dataHash = hash(
          hash(jarFile),
          hash(classBinaryName)
      );
      obj_root_with_two_data_hashes(
          nativeMethodOT(),
          dataHash,
          (Hash objHash) -> ((NativeMethodH) objectDb().get(objHash)).classBinaryName());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          nativeMethodOT(),
          (Hash objHash) -> ((NativeMethodH) objectDb().get(objHash)).classBinaryName());
    }

    @Test
    public void data_is_sequence_with_one_element() throws Exception {
      BlobH jarFile = blob();
      Hash dataHash = hash(
          hash(jarFile)
      );
      Hash objHash =
          hash(
              hash(nativeMethodOT()),
              dataHash
          );

      assertCall(() -> ((NativeMethodH) objectDb().get(objHash)).classBinaryName())
          .throwsException(new UnexpectedObjSequenceException(
              objHash, nativeMethodOT(), DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_sequence_with_three_elements() throws Exception {
      BlobH jarFile = blob();
      StringH classBinaryName = string();
      Hash dataHash = hash(
          hash(jarFile),
          hash(classBinaryName),
          hash(classBinaryName)
      );
      Hash objHash =
          hash(
              hash(nativeMethodOT()),
              dataHash
          );

      assertCall(() -> ((NativeMethodH) objectDb().get(objHash)).classBinaryName())
          .throwsException(new UnexpectedObjSequenceException(
              objHash, nativeMethodOT(), DATA_PATH, 2, 3));
    }

    @Test
    public void jar_file_is_not_blob_value() throws Exception {
      StringH jarFile = string();
      StringH classBinaryName = string();
      Hash objHash =
          hash(
              hash(nativeMethodOT()),
              hash(
                  hash(jarFile),
                  hash(classBinaryName)
              )
          );
      assertCall(() -> ((NativeMethodH) objectDb().get(objHash)).jarFile())
          .throwsException(new UnexpectedObjNodeException(
              objHash, nativeMethodOT(), DATA_PATH + "[0]", BlobH.class, StringH.class));
    }

    @Test
    public void class_binary_name_is_not_string_value() throws Exception {
      BlobH jarFile = blob();
      IntH classBinaryName = int_();
      Hash objHash =
          hash(
              hash(nativeMethodOT()),
              hash(
                  hash(jarFile),
                  hash(classBinaryName)
              )
          );

      assertCall(() -> ((NativeMethodH) objectDb().get(objHash)).classBinaryName())
          .throwsException(new UnexpectedObjNodeException(
              objHash, nativeMethodOT(), DATA_PATH + "[1]", StringH.class, IntH.class));
    }
  }

  @Nested
  class _nothing {
    @Test
    public void learning_test() throws Exception {
      Hash objHash =
          hash(
              hash(nothingOT()),
              hash("aaa"));
      assertCall(() -> objectDb().get(objHash))
          .throwsException(new UnsupportedOperationException(
              "Cannot create object for NOTHING type."));
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
              hash(stringOT()),
              hash("aaa"));
      assertThat(((StringH) objectDb().get(objHash)).jValue())
          .isEqualTo("aaa");
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(stringOT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          stringOT(),
          hashedDb().writeBoolean(true),
          (Hash objHash) -> ((StringH) objectDb().get(objHash)).jValue()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          stringOT(),
          (Hash objHash) -> ((StringH) objectDb().get(objHash)).jValue());
    }

    @Test
    public void data_being_invalid_utf8_sequence() throws Exception {
      Hash notStringHash = hash(illegalString());
      Hash objHash =
          hash(
              hash(stringOT()),
              notStringHash);
      assertCall(() -> ((StringH) objectDb().get(objHash)).jValue())
          .throwsException(new DecodeObjNodeException(objHash, stringOT(), DATA_PATH))
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
              hash(personOT()),
              hash(
                  hash(string("John")),
                  hash(string("Doe")))))
          .isEqualTo(person("John", "Doe").hash());
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(personOT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          personOT(),
          hashedDb().writeBoolean(true),
          (Hash objHash) -> ((TupleH) objectDb().get(objHash)).get(0)
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          personOT(),
          (Hash objHash) -> ((TupleH) objectDb().get(objHash)).get(0));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_sequence_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSequence = hash(ByteString.of(new byte[byteCount]));
      Hash objHash =
          hash(
              hash(personOT()),
              notHashOfSequence);
      assertCall(() -> ((TupleH) objectDb().get(objHash)).get(0))
          .throwsException(new DecodeObjNodeException(objHash, personOT(), DATA_PATH))
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
              hash(personOT()),
              dataHash
          );
      assertCall(() -> ((TupleH) objectDb().get(objHash)).get(0))
          .throwsException(new DecodeObjNodeException(objHash, personOT(), DATA_PATH + "[0]"))
          .withCause(new NoSuchObjException(nowhere));
    }

    @Test
    public void with_too_few_elements() throws Exception {
      Hash dataHash =
          hash(
              hash(string("John")));
      Hash objHash =
          hash(
              hash(personOT()),
              dataHash);
      TupleH tuple = (TupleH) objectDb().get(objHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new UnexpectedObjSequenceException(objHash, personOT(), DATA_PATH, 2, 1));
    }

    @Test
    public void with_too_many_elements() throws Exception {
      Hash dataHash =
          hash(
              hash(string("John")),
              hash(string("Doe")),
              hash(string("junk")));
      Hash objHash =
          hash(
              hash(personOT()),
              dataHash);
      TupleH tuple = (TupleH) objectDb().get(objHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new UnexpectedObjSequenceException(objHash, personOT(), DATA_PATH, 2, 3));
    }

    @Test
    public void with_element_of_wrong_type() throws Exception {
      Hash objHash =
          hash(
              hash(personOT()),
              hash(
                  hash(string("John")),
                  hash(bool(true))));
      TupleH tuple = (TupleH) objectDb().get(objHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new UnexpectedObjNodeException(
              objHash, personOT(), DATA_PATH, 1, stringOT(), boolOT()));
    }

    @Test
    public void with_element_being_expr() throws Exception {
      Hash objHash =
          hash(
              hash(personOT()),
              hash(
                  hash(string("John")),
                  hash(intExpr())));
      TupleH tuple = (TupleH) objectDb().get(objHash);
      assertCall(() -> tuple.get(0))
          .throwsException(new UnexpectedObjNodeException(
              objHash, personOT(), DATA_PATH + "[1]", ValueH.class, ConstH.class));
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
              hash(refOT(stringOT())),
              hash(byteString));
      assertThat(((RefH) objectDb().get(objHash)).value())
          .isEqualTo(BigInteger.valueOf(3 * 256 + 2));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(refOT());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          refOT(),
          hashedDb().writeByte((byte) 1),
          (Hash objHash) -> ((RefH) objectDb().get(objHash)).value()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          refOT(),
          (Hash objHash) -> ((RefH) objectDb().get(objHash)).value());
    }
  }

  private void obj_root_without_data_hash(TypeH type) throws HashedDbException {
    Hash objHash =
        hash(
            hash(type));
    assertCall(() -> objectDb().get(objHash))
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
              hash(variableOT("A")),
              hash("aaa"));
      assertCall(() -> objectDb().get(objHash))
          .throwsException(new UnsupportedOperationException(
              "Cannot create object for VARIABLE type."));
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