package org.smoothbuild.db.object.obj;

import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.db.object.exc.DecodeObjRootException.cannotReadRootException;
import static org.smoothbuild.db.object.exc.DecodeObjRootException.nonNullObjRootException;
import static org.smoothbuild.db.object.exc.DecodeObjRootException.nullObjRootException;
import static org.smoothbuild.db.object.exc.DecodeObjRootException.wrongSizeOfRootSequenceException;
import static org.smoothbuild.db.object.obj.base.Obj.DATA_PATH;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

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
import org.smoothbuild.db.object.exc.DecodeCallWrongArgumentsSizeException;
import org.smoothbuild.db.object.exc.DecodeExprWrongEvaluationSpecOfComponentException;
import org.smoothbuild.db.object.exc.DecodeObjNodeException;
import org.smoothbuild.db.object.exc.DecodeObjSpecException;
import org.smoothbuild.db.object.exc.DecodeSelectIndexOutOfBoundsException;
import org.smoothbuild.db.object.exc.DecodeSelectWrongEvaluationSpec;
import org.smoothbuild.db.object.exc.DecodeSpecException;
import org.smoothbuild.db.object.exc.NoSuchObjException;
import org.smoothbuild.db.object.exc.UnexpectedObjNodeException;
import org.smoothbuild.db.object.exc.UnexpectedObjSequenceException;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.obj.expr.EArray;
import org.smoothbuild.db.object.obj.expr.Ref;
import org.smoothbuild.db.object.obj.expr.Select;
import org.smoothbuild.db.object.obj.expr.Select.SelectData;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.DefinedLambda;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.NativeLambda;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.expr.CallSpec;
import org.smoothbuild.db.object.spec.expr.ConstSpec;
import org.smoothbuild.db.object.spec.expr.EArraySpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.DefinedLambdaSpec;
import org.smoothbuild.db.object.spec.val.LambdaSpec;
import org.smoothbuild.db.object.spec.val.NativeLambdaSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

import okio.ByteString;

public class CorruptedObjTest extends TestingContext {
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
              hash(strSpec()),
              hash("aaa"));
      assertThat(((Str) objectDb().get(objHash)).jValue())
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
    public void corrupted_spec() throws Exception {
      Hash specHash = Hash.of("not a spec");
      Hash objHash =
          hash(
              specHash,
              hash("aaa"));
      assertCall(() -> objectDb().get(objHash))
          .throwsException(new DecodeObjSpecException(objHash))
          .withCause(new DecodeSpecException(specHash));
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
  class _array {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save array
       * in HashedDb.
       */
      Hash objHash =
          hash(
              hash(arraySpec(strSpec())),
              hash(
                  hash(
                      hash(strSpec()),
                      hash("aaa")
                  ),
                  hash(
                      hash(strSpec()),
                      hash("bbb")
                  )
              ));
      List<String> strings = ((Array) objectDb().get(objHash))
          .elements(Str.class)
          .stream()
          .map(Str::jValue)
          .collect(toList());
      assertThat(strings)
          .containsExactly("aaa", "bbb")
          .inOrder();
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(arraySpec(intSpec()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          arraySpec(intSpec()),
          hashedDb().writeSequence(),
          (Hash objHash) -> ((Array) objectDb().get(objHash)).elements(Int.class)
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          arraySpec(intSpec()),
          (Hash objHash) -> ((Array) objectDb().get(objHash)).elements(Int.class));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_sequence_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSequence = hash(ByteString.of(new byte[byteCount]));
      ArraySpec spec = arraySpec(strSpec());
      Hash objHash =
          hash(
              hash(spec),
              notHashOfSequence
          );
      assertCall(() -> ((Array) objectDb().get(objHash)).elements(Val.class))
          .throwsException(new DecodeObjNodeException(objHash, spec, DATA_PATH))
          .withCause(new DecodeHashSequenceException(
              notHashOfSequence, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_sequence_element_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash dataHash = hash(
          nowhere
      );
      ArraySpec spec = arraySpec(strSpec());
      Hash objHash =
          hash(
              hash(spec),
              dataHash);
      assertCall(() -> ((Array) objectDb().get(objHash)).elements(Str.class))
          .throwsException(new DecodeObjNodeException(objHash, spec, DATA_PATH + "[0]"))
          .withCause(new NoSuchObjException(nowhere));
    }

    @Test
    public void with_one_element_of_wrong_spec() throws Exception {
      ArraySpec spec = arraySpec(strSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(
                      hash(strSpec()),
                      hash("aaa")
                  ),
                  hash(
                      hash(boolSpec()),
                      hash(true)
                  )
              ));
      assertCall(() -> ((Array) objectDb().get(objHash)).elements(Str.class))
          .throwsException(new UnexpectedObjNodeException(
              objHash, spec, DATA_PATH, 1, strSpec(), boolSpec()));
    }

    @Test
    public void with_one_element_being_expr() throws Exception {
      ArraySpec spec = arraySpec(strSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(
                      hash(strSpec()),
                      hash("aaa")
                  ),
                  hash(intExpr(13))
              ));
      assertCall(() -> ((Array) objectDb().get(objHash)).elements(Str.class))
          .throwsException(new UnexpectedObjNodeException(
              objHash, spec, DATA_PATH, 1, strSpec(), constSpec(intSpec())));
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
              hash(blobSpec()),
              hash(byteString));
      assertThat(((Blob) objectDb().get(objHash)).source().readByteString())
          .isEqualTo(byteString);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(blobSpec());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          blobSpec(),
          hashedDb().writeByte((byte) 1),
          (Hash objHash) -> ((Blob) objectDb().get(objHash)).source()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          blobSpec(),
          (Hash objHash) -> ((Blob) objectDb().get(objHash)).source());
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
              hash(boolSpec()),
              hash(value));
      assertThat(((Bool) objectDb().get(objHash)).jValue())
          .isEqualTo(value);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(boolSpec());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          boolSpec(),
          hashedDb().writeBoolean(true),
          (Hash objHash) -> ((Bool) objectDb().get(objHash)).jValue()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          boolSpec(),
          (Hash objHash) -> ((Bool) objectDb().get(objHash)).jValue());
    }

    @Test
    public void empty_bytes_as_data() throws Exception {
      Hash dataHash = hash(ByteString.of());
      Hash objHash =
          hash(
              hash(boolSpec()),
              dataHash);
      assertCall(() -> ((Bool) objectDb().get(objHash)).jValue())
          .throwsException(new DecodeObjNodeException(objHash, boolSpec(), DATA_PATH))
          .withCause(new DecodeBooleanException(dataHash, new DecodeByteException(dataHash)));
    }

    @Test
    public void more_than_one_byte_as_data() throws Exception {
      Hash dataHash = hash(ByteString.of((byte) 0, (byte) 0));
      Hash objHash =
          hash(
              hash(boolSpec()),
              dataHash);
      assertCall(() -> ((Bool) objectDb().get(objHash)).jValue())
          .throwsException(new DecodeObjNodeException(objHash, boolSpec(), DATA_PATH))
          .withCause(new DecodeBooleanException(dataHash, new DecodeByteException(dataHash)));
    }

    @ParameterizedTest
    @ArgumentsSource(AllByteValuesExceptZeroAndOneProvider.class)
    public void one_byte_data_not_equal_zero_nor_one(byte value)
        throws Exception {
      Hash dataHash = hash(ByteString.of(value));
      Hash objHash =
          hash(
              hash(boolSpec()),
              dataHash);
      assertCall(() -> ((Bool) objectDb().get(objHash)).jValue())
          .throwsException(new DecodeObjNodeException(objHash, boolSpec(), DATA_PATH))
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
      var lambdaSpec = definedLambdaSpec(intSpec(), strSpec(), intSpec());
      var definedLambda = definedLambdaVal(lambdaSpec, intExpr(), list(strExpr(), intExpr()));
      Const function = constExpr(definedLambda);
      Const arg1 = strExpr();
      Const arg2 = intExpr();
      Hash objHash =
          hash(
              hash(callSpec()),
              hash(
                  hash(function),
                  hash(
                      hash(arg1),
                      hash(arg2)
                  )
              )
          );

      assertThat(((Call) objectDb().get(objHash)).data().function())
          .isEqualTo(function);
      assertThat(((Call) objectDb().get(objHash)).data().arguments())
          .isEqualTo(list(arg1, arg2));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(callSpec());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      Const function = intExpr(0);
      Const arg1 = intExpr(1);
      Const arg2 = intExpr(2);
      Hash dataHash = hash(
          hash(function),
          hash(
              hash(arg1),
              hash(arg2)
          )
      );
      obj_root_with_two_data_hashes(
          callSpec(),
          dataHash,
          (Hash objHash) -> ((Call) objectDb().get(objHash)).data());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          callSpec(),
          (Hash objHash) -> ((Call) objectDb().get(objHash)).data());
    }

    @Test
    public void data_is_sequence_with_one_element() throws Exception {
      Const function = intExpr(0);
      Hash dataHash = hash(
          hash(function)
      );
      Hash objHash =
          hash(
              hash(callSpec()),
              dataHash
          );
      assertCall(() -> ((Call) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSequenceException(objHash, callSpec(), DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_sequence_with_three_elements() throws Exception {
      Const function = intExpr(0);
      Const arg1 = intExpr(1);
      Const arg2 = intExpr(2);
      Hash arguments = hash(
          hash(arg1),
          hash(arg2)
      );
      Hash dataHash = hash(
          hash(function),
          arguments,
          arguments
      );
      Hash objHash =
          hash(
              hash(callSpec()),
              dataHash
          );
      assertCall(() -> ((Call) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSequenceException(objHash, callSpec(), DATA_PATH, 2, 3));
    }

    @Test
    public void function_is_val_instead_of_expr() throws Exception {
      Int val = intVal(0);
      Const arg1 = intExpr(1);
      Const arg2 = intExpr(2);
      Hash objHash =
          hash(
              hash(callSpec()),
              hash(
                  hash(val),
                  hash(
                      hash(arg1),
                      hash(arg2)
                  )
              )
          );
      assertCall(() -> ((Call) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjNodeException(
              objHash, callSpec(), DATA_PATH + "[0]", Expr.class, Int.class));
    }


    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void arguments_sequence_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSequence = hash(ByteString.of(new byte[byteCount]));
      Const function = intExpr(0);
      Hash objHash =
          hash(
              hash(callSpec()),
              hash(
                  hash(function),
                  notHashOfSequence
              )
          );
      assertCall(() -> ((Call) objectDb().get(objHash)).data())
          .throwsException(new DecodeObjNodeException(objHash, callSpec(), DATA_PATH + "[1]"))
          .withCause(new DecodeHashSequenceException(
              notHashOfSequence, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void arguments_sequence_element_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Const function = intExpr(0);
      Const arg1 = intExpr(1);
      Hash objHash =
          hash(
              hash(callSpec()),
              hash(
                  hash(function),
                  hash(
                      hash(arg1),
                      nowhere
                  )
              )
          );
      assertCall(() -> ((Call) objectDb().get(objHash)).data())
          .throwsException(new DecodeObjNodeException(objHash, callSpec(), DATA_PATH + "[1][1]"))
          .withCause(new NoSuchObjException(nowhere));
    }

    @Test
    public void arguments_contain_val_instead_of_expr() throws Exception {
      Const function = intExpr(0);
      Const arg1 = intExpr(1);
      Int arg2 = intVal(2);
      Hash objHash =
          hash(
              hash(callSpec()),
              hash(
                  hash(function),
                  hash(
                      hash(arg1),
                      hash(arg2)
                  )
              )
          );
      assertCall(() -> ((Call) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjNodeException(
              objHash, callSpec(), DATA_PATH + "[1][1]", Expr.class, Int.class));
    }

    @Test
    public void function_component_evaluation_spec_is_not_lambda() throws Exception {
      Const function = intExpr(3);
      Const arg1 = intExpr(1);
      Const arg2 = intExpr(2);
      CallSpec spec = callSpec(strSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(function),
                  hash(
                      hash(arg1),
                      hash(arg2)
                  )
              )
          );
      assertCall(() -> ((Call) objectDb().get(objHash)).data())
          .throwsException(new DecodeExprWrongEvaluationSpecOfComponentException(
              objHash, spec, "function", LambdaSpec.class, intSpec()));
    }

    @Test
    public void evaluation_spec_is_different_than_function_evaluation_spec_result()
        throws Exception {
      Const function = constExpr(definedLambdaVal(
          definedLambdaSpec(intSpec(), strSpec()), intExpr(), list(strExpr())));
      Const arg1 = intExpr(1);
      Const arg2 = intExpr(2);
      CallSpec spec = callSpec(strSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(function),
                  hash(
                      hash(arg1),
                      hash(arg2)
                  )
              )
          );
      assertCall(() -> ((Call) objectDb().get(objHash)).data())
          .throwsException(new DecodeExprWrongEvaluationSpecOfComponentException(
                  objHash, spec, "function.result", strSpec(), intSpec()));
    }

    @Test
    public void function_evaluation_spec_parameter_count_does_not_match_arguments_count()
        throws Exception {
      DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), strSpec(), intSpec());
      Const function = constExpr(definedLambdaVal(
          lambdaSpec, intExpr(), list(strExpr(), intExpr())));
      Const arg1 = strExpr();
      CallSpec spec = callSpec(intSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(function),
                  hash(
                      hash(arg1)
                  )
              )
          );
      assertCall(() -> ((Call) objectDb().get(objHash)).data())
          .throwsException(new DecodeCallWrongArgumentsSizeException(objHash, spec, 2, 1));
    }

    @Test
    public void function_evaluation_spec_parameters_does_not_match_arguments_evaluation_specs()
        throws Exception {
      DefinedLambdaSpec lambdaSpec = definedLambdaSpec(intSpec(), strSpec(), boolSpec());
      Const function = constExpr(definedLambdaVal(
          lambdaSpec, intExpr(), list(strExpr(), boolExpr())));
      Const arg1 = strExpr();
      Const arg2 = intExpr();
      CallSpec spec = callSpec(intSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(function),
                  hash(
                      hash(arg1),
                      hash(arg2)
                  )
              )
          );
      assertCall(() -> ((Call) objectDb().get(objHash)).data())
          .throwsException(new DecodeExprWrongEvaluationSpecOfComponentException(
                  objHash, spec, "arguments", recSpec(list(strSpec(), boolSpec())),
              recSpec(list(strSpec(), boolSpec()))
              // TODO this is workaround until Call.arguments are kept as RecExpr
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
      Val val = objectDb().intVal(BigInteger.valueOf(123));
      Hash objHash =
          hash(
              hash(constSpec()),
              hash(val));
      assertThat(((Const) objectDb().get(objHash)).value())
          .isEqualTo(val);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(constSpec());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          constSpec(),
          objectDb().intVal(BigInteger.valueOf(123)).hash(),
          (Hash objHash) -> ((Const) objectDb().get(objHash)).value()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_obj_but_nowhere(
          constSpec(),
          (Hash objHash) -> ((Const) objectDb().get(objHash)).value());
    }

    @Test
    public void data_hash_pointing_to_expr_instead_of_value() throws Exception {
      Val val = objectDb().intVal(BigInteger.valueOf(123));
      Hash exprHash =
          hash(
              hash(constSpec()),
              hash(val));
      Hash objHash =
          hash(
              hash(constSpec()),
              exprHash);
      assertCall(() -> ((Const) objectDb().get(objHash)).value())
          .throwsException(new UnexpectedObjNodeException(
              objHash, constSpec(), DATA_PATH, Val.class, Const.class));
    }

    @Test
    public void evaluation_spec_is_different_than_spec_of_wrapped_value()
        throws Exception {
      Val val = objectDb().intVal(BigInteger.valueOf(123));
      ConstSpec spec = constSpec(strSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(val));
      assertCall(() -> ((Const) objectDb().get(objHash)).value())
          .throwsException(
              new UnexpectedObjNodeException(objHash, spec, DATA_PATH, strSpec(), intSpec()));
    }
  }

  @Nested
  class _defined_lambda {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save definedLambda
       * in HashedDb.
       */
      Const bodyExpr = boolExpr();
      Const defaultArgument1 = intExpr(1);
      Const defaultArgument2 = strExpr("abc");
      DefinedLambdaSpec spec = definedLambdaSpec(boolSpec(), intSpec(), strSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(bodyExpr),
                  hash(
                      hash(defaultArgument1),
                      hash(defaultArgument2)
                  )
              )
          );
      assertThat(((DefinedLambda) objectDb().get(objHash)).data().body())
          .isEqualTo(bodyExpr);
      assertThat(((DefinedLambda) objectDb().get(objHash)).data().defaultArguments())
          .isEqualTo(list(defaultArgument1, defaultArgument2));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(definedLambdaSpec());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      Const bodyExpr = boolExpr();
      Const defaultArgument1 = intExpr(1);
      Const defaultArgument2 = strExpr("abc");
      DefinedLambdaSpec spec = definedLambdaSpec(boolSpec(), intSpec(), strSpec());
      Hash dataHash = hash(
          hash(bodyExpr),
          hash(
              hash(defaultArgument1),
              hash(defaultArgument2)
          )
      );
      obj_root_with_two_data_hashes(
          spec,
          dataHash,
          (Hash objHash) -> ((DefinedLambda) objectDb().get(objHash)).data());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          definedLambdaSpec(),
          (Hash objHash) -> ((DefinedLambda) objectDb().get(objHash)).data());
    }

    @Test
    public void data_is_sequence_with_one_element() throws Exception {
      Const bodyExpr = boolExpr();
      DefinedLambdaSpec spec = definedLambdaSpec(boolSpec(), intSpec(), strSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(bodyExpr)
              )
          );
      assertCall(() -> ((DefinedLambda) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSequenceException(objHash, spec, DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_sequence_with_three_elements() throws Exception {
      Const bodyExpr = boolExpr();
      Const defaultArgument1 = intExpr(1);
      Const defaultArgument2 = strExpr("abc");
      DefinedLambdaSpec spec = definedLambdaSpec(boolSpec(), intSpec(), strSpec());
      Hash defaultArguments = hash(
          hash(defaultArgument1),
          hash(defaultArgument2)
      );
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(bodyExpr),
                  defaultArguments,
                  defaultArguments
              )
          );

      assertCall(() -> ((DefinedLambda) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSequenceException(objHash, spec, DATA_PATH, 2, 3));
    }

    @Test
    public void body_is_val_instead_of_expr() throws Exception {
      Bool bodyExpr = boolVal(true);
      Const defaultArgument1 = intExpr(1);
      Const defaultArgument2 = strExpr("abc");
      DefinedLambdaSpec spec = definedLambdaSpec(boolSpec(), intSpec(), strSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(bodyExpr),
                  hash(
                      hash(defaultArgument1),
                      hash(defaultArgument2)
                  )
              )
          );
      assertCall(() -> ((DefinedLambda) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjNodeException(
              objHash, spec, DATA_PATH + "[0]", Expr.class, Bool.class));
    }

    @Test
    public void body_evaluation_spec_is_not_equal_function_spec_result() throws Exception {
      Const bodyExpr = intExpr(3);
      Const defaultArgument1 = intExpr(1);
      Const defaultArgument2 = strExpr("abc");
      DefinedLambdaSpec spec = definedLambdaSpec(boolSpec(), intSpec(), strSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(bodyExpr),
                  hash(
                      hash(defaultArgument1),
                      hash(defaultArgument2)
                  )
              )
          );
      assertCall(() -> ((DefinedLambda) objectDb().get(objHash)).data())
          .throwsException(new DecodeExprWrongEvaluationSpecOfComponentException(
              objHash, spec, DATA_PATH + "[0]", intSpec(), boolSpec()));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void default_arguments_sequence_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      DefinedLambdaSpec spec = definedLambdaSpec(boolSpec(), intSpec(), strSpec());
      Hash bodyHash = hash(boolExpr());
      test_default_arguments_sequence_size_different_than_multiple_of_hash_size(
          spec, bodyHash, byteCount,
          (objHash) -> ((DefinedLambda) objectDb().get(objHash)).data()
      );
    }

    @Test
    public void default_arguments_sequence_element_pointing_nowhere() throws Exception {
      DefinedLambdaSpec spec = definedLambdaSpec(boolSpec(), intSpec(), strSpec());
      Hash bodyHash = hash(boolExpr());
      test_default_arguments_sequence_element_pointing_nowhere(spec, bodyHash,
          (objHash) -> ((DefinedLambda) objectDb().get(objHash)).data());
    }

    @Test
    public void default_arguments_size_different_than_spec_parameters_size() throws Exception {
      DefinedLambdaSpec spec = definedLambdaSpec(boolSpec(), intSpec());
      Hash bodyHash = hash(boolExpr());
      test_default_arguments_size_different_than_spec_parameters_size(spec,
          bodyHash, (objHash) -> ((DefinedLambda) objectDb().get(objHash)).data()
      );
    }

    @Test
    public void default_arguments_contain_val_instead_of_expr() throws Exception {
      DefinedLambdaSpec spec = definedLambdaSpec(boolSpec(), intSpec(), strSpec());
      Hash bodyHash = hash(boolExpr());
      test_default_arguments_contain_val_instead_of_expr(
          spec, bodyHash, (objHash) -> ((DefinedLambda) objectDb().get(objHash)).data());
    }

    @Test
    public void default_argument_evaluation_spec_is_not_equal_function_parameter_spec()
        throws Exception {
      DefinedLambdaSpec spec = definedLambdaSpec(boolSpec(), intSpec(), strSpec());
      Hash bodyHash = hash(boolExpr());

      test_default_argument_evaluation_spec_is_not_equal_function_parameter_spec(spec, bodyHash,
          (objHash) -> ((DefinedLambda) objectDb().get(objHash)).data());
    }
  }

  @Nested
  class _earray {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save eArray
       * in HashedDb.
       */
      Const expr1 = intExpr(1);
      Const expr2 = intExpr(2);
      Hash objHash =
          hash(
              hash(eArraySpec()),
              hash(
                  hash(expr1),
                  hash(expr2)
              ));
      ImmutableList<Expr> elements = ((EArray) objectDb().get(objHash)).elements();
      assertThat(elements)
          .containsExactly(expr1, expr2)
          .inOrder();
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(eArraySpec());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      Const expr1 = intExpr(1);
      Const expr2 = intExpr(2);
      Hash dataHash = hash(
          hash(expr1),
          hash(expr2)
      );
      obj_root_with_two_data_hashes(
          eArraySpec(),
          dataHash,
          (Hash objHash) -> ((EArray) objectDb().get(objHash)).elements()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          eArraySpec(),
          (Hash objHash) -> ((EArray) objectDb().get(objHash)).elements());
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_sequence_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSequence = hash(ByteString.of(new byte[byteCount]));
      Hash objHash =
          hash(
              hash(eArraySpec()),
              notHashOfSequence
          );
      assertCall(() -> ((EArray) objectDb().get(objHash)).elements())
          .throwsException(new DecodeObjNodeException(objHash, eArraySpec(), DATA_PATH))
          .withCause(new DecodeHashSequenceException(
              notHashOfSequence, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_sequence_element_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash objHash =
          hash(
              hash(eArraySpec()),
              hash(
                  nowhere
              )
          );
      assertCall(() -> ((EArray) objectDb().get(objHash)).elements())
          .throwsException(new DecodeObjNodeException(objHash, eArraySpec(), DATA_PATH + "[0]"))
          .withCause(new NoSuchObjException(nowhere));
    }

    @Test
    public void with_one_element_being_val() throws Exception {
      Const expr1 = intExpr(1);
      Int val = intVal(123);
      Hash objHash =
          hash(
              hash(eArraySpec()),
              hash(
                  hash(expr1),
                  hash(val)
              ));
      assertCall(() -> ((EArray) objectDb().get(objHash)).elements())
          .throwsException(new UnexpectedObjNodeException(
              objHash, eArraySpec(), DATA_PATH + "[1]", Expr.class, Int.class));
    }

    @Test
    public void evaluation_spec_element_is_different_than_evaluation_spec_of_one_of_elements()
        throws Exception {
      Const expr1 = intExpr();
      Const expr2 = strExpr();
      EArraySpec spec = eArraySpec(intSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(expr1),
                  hash(expr2)
              ));
      assertCall(() -> ((EArray) objectDb().get(objHash)).elements())
          .throwsException(
              new DecodeExprWrongEvaluationSpecOfComponentException(
                  objHash, spec, "elements[1]", intSpec(), strSpec()));
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
      var recSpec = recSpec(list(strSpec()));
      var rec = objectDb().recVal(recSpec, list(objectDb().strVal("abc")));
      var expr = objectDb().constExpr(rec);
      var index = objectDb().intVal(BigInteger.valueOf(0));
      Hash objHash =
          hash(
              hash(selectSpec(strSpec())),
              hash(
                  hash(expr),
                  hash(index)
              )
          );
      assertThat(((Select) objectDb().get(objHash)).data())
          .isEqualTo(new SelectData(expr, index));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(selectSpec(intSpec()));
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      Val index = objectDb().intVal(BigInteger.valueOf(2));
      Const expr = objectDb().constExpr(objectDb().intVal(BigInteger.valueOf(123)));
      Hash dataHash = hash(
          hash(expr),
          hash(index)
      );
      obj_root_with_two_data_hashes(
          selectSpec(),
          dataHash,
          (Hash objHash) -> ((Select) objectDb().get(objHash)).data());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          selectSpec(),
          (Hash objHash) -> ((Select) objectDb().get(objHash)).data());
    }

    @Test
    public void data_is_sequence_with_one_element() throws Exception {
      Const expr = objectDb().constExpr(objectDb().intVal(BigInteger.valueOf(123)));
      Hash dataHash = hash(
          hash(expr)
      );
      Hash objHash =
          hash(
              hash(selectSpec()),
              dataHash
          );
      assertCall(() -> ((Select) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSequenceException(
              objHash, selectSpec(), DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_sequence_with_three_elements() throws Exception {
      Val index = objectDb().intVal(BigInteger.valueOf(2));
      Const expr = objectDb().constExpr(objectDb().intVal(BigInteger.valueOf(123)));
      Hash dataHash = hash(
          hash(expr),
          hash(index),
          hash(index)
      );
      Hash objHash =
          hash(
              hash(selectSpec()),
              dataHash
          );
      assertCall(() -> ((Select) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSequenceException(
              objHash, selectSpec(), DATA_PATH, 2, 3));
    }

    @Test
    public void rec_is_val_instead_of_expr() throws Exception {
      Val val = objectDb().intVal(BigInteger.valueOf(2));
      Val index = objectDb().intVal(BigInteger.valueOf(2));
      Hash objHash =
          hash(
              hash(selectSpec()),
              hash(
                  hash(val),
                  hash(index)
              )
          );
      assertCall(() -> ((Select) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjNodeException(
              objHash, selectSpec(), DATA_PATH + "[0]", Expr.class, Int.class));
    }

    @Test
    public void rec_is_not_rec_expr() throws Exception {
      var expr = intExpr(3);
      var index = objectDb().intVal(BigInteger.valueOf(0));
      var spec = selectSpec(strSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(expr),
                  hash(index)
              )
          );

      assertCall(() -> ((Select) objectDb().get(objHash)).data())
          .throwsException(new DecodeExprWrongEvaluationSpecOfComponentException(
              objHash, spec, "rec", RecSpec.class, intSpec()));
    }

    @Test
    public void index_is_out_of_bounds() throws Exception {
      var recSpec = recSpec(list(strSpec()));
      var rec = objectDb().recVal(recSpec, list(objectDb().strVal("abc")));
      var expr = objectDb().constExpr(rec);
      var index = objectDb().intVal(BigInteger.valueOf(1));
      var spec = selectSpec(strSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(expr),
                  hash(index)
              )
          );

      assertCall(() -> ((Select) objectDb().get(objHash)).data())
          .throwsException(new DecodeSelectIndexOutOfBoundsException(objHash, spec, 1, 1));
    }

    @Test
    public void evaluation_spec_is_different_than_spec_of_item_pointed_to_by_index()
        throws Exception {
      var recSpec = recSpec(list(strSpec()));
      var rec = objectDb().recVal(recSpec, list(objectDb().strVal("abc")));
      var expr = objectDb().constExpr(rec);
      var index = objectDb().intVal(BigInteger.valueOf(0));
      var spec = selectSpec(intSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(expr),
                  hash(index)
              )
          );

      assertCall(() -> ((Select) objectDb().get(objHash)).data())
          .throwsException(new DecodeSelectWrongEvaluationSpec(objHash, spec, strSpec()));
    }

    @Test
    public void index_is_string_instead_of_int() throws Exception {
      var spec = selectSpec(strSpec());
      var recSpec = recSpec(list(strSpec()));
      var rec = objectDb().recVal(recSpec, list(objectDb().strVal("abc")));
      var expr = objectDb().constExpr(rec);
      var strVal = objectDb().strVal("abc");
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(expr),
                  hash(strVal)
              )
          );
      assertCall(() -> ((Select) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjNodeException(
              objHash, spec, DATA_PATH + "[1]", Int.class, Str.class));
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
              hash(intSpec()),
              hash(byteString));
      assertThat(((Int) objectDb().get(objHash)).jValue())
          .isEqualTo(BigInteger.valueOf(3 * 256 + 2));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(intSpec());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          intSpec(),
          hashedDb().writeByte((byte) 1),
          (Hash objHash) -> ((Int) objectDb().get(objHash)).jValue()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          intSpec(),
          (Hash objHash) -> ((Int) objectDb().get(objHash)).jValue());
    }
  }

  @Nested
  class _native_lambda {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save definedLambda
       * in HashedDb.
       */
      Str classBinaryName = strVal("classBinaryName");
      Blob nativeJar = blobVal();
      Const defaultArgument1 = intExpr(1);
      Const defaultArgument2 = strExpr("abc");
      NativeLambdaSpec spec = nativeLambdaSpec(boolSpec(), intSpec(), strSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(
                      hash(classBinaryName),
                      hash(nativeJar)
                  ),
                  hash(
                      hash(defaultArgument1),
                      hash(defaultArgument2)
                  )
              )
          );
      assertThat(((NativeLambda) objectDb().get(objHash)).data().classBinaryName())
          .isEqualTo(classBinaryName);
      assertThat(((NativeLambda) objectDb().get(objHash)).data().nativeJar())
          .isEqualTo(nativeJar);
      assertThat(((NativeLambda) objectDb().get(objHash)).data().defaultArguments())
          .isEqualTo(list(defaultArgument1, defaultArgument2));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(nativeLambdaSpec());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      Str classBinaryName = strVal("classBinaryName");
      Blob nativeJar = blobVal();
      Const defaultArgument1 = intExpr(1);
      Const defaultArgument2 = strExpr("abc");
      NativeLambdaSpec spec = nativeLambdaSpec(boolSpec(), intSpec(), strSpec());
      Hash dataHash = hash(
          hash(
              hash(classBinaryName),
              hash(nativeJar)
          ),
          hash(
              hash(defaultArgument1),
              hash(defaultArgument2)
          )
      );
      obj_root_with_two_data_hashes(
          spec,
          dataHash,
          (Hash objHash) -> ((NativeLambda) objectDb().get(objHash)).data().nativeJar());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          nativeLambdaSpec(),
          (Hash objHash) -> ((NativeLambda) objectDb().get(objHash)).data().nativeJar());
    }

    @Test
    public void data_is_sequence_with_one_element() throws Exception {
      Str classBinaryName = strVal("classBinaryName");
      Blob nativeJar = blobVal();
      NativeLambdaSpec spec = nativeLambdaSpec(boolSpec(), intSpec(), strSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(
                      hash(classBinaryName),
                      hash(nativeJar)
                  )
              )
          );
      assertCall(() -> ((NativeLambda) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSequenceException(objHash, spec, DATA_PATH, 2, 1));
    }

    @Test
    public void data_is_sequence_with_three_elements() throws Exception {
      Str classBinaryName = strVal("classBinaryName");
      Blob nativeJar = blobVal();
      Const defaultArgument1 = intExpr(1);
      Const defaultArgument2 = strExpr("abc");
      NativeLambdaSpec spec = nativeLambdaSpec(boolSpec(), intSpec(), strSpec());
      Hash defaultArgumentsHash = hash(
          hash(defaultArgument1),
          hash(defaultArgument2)
      );
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(
                      hash(classBinaryName),
                      hash(nativeJar)
                  ),
                  defaultArgumentsHash,
                  defaultArgumentsHash
              )
          );

      assertCall(() -> ((NativeLambda) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSequenceException(objHash, spec, DATA_PATH, 2, 3));
    }


    @Test
    public void body_is_sequence_with_one_element() throws Exception {
      Str classBinaryName = strVal("classBinaryName");
      Const defaultArgument1 = intExpr(1);
      Const defaultArgument2 = strExpr("abc");
      NativeLambdaSpec spec = nativeLambdaSpec(boolSpec(), intSpec(), strSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(
                      hash(classBinaryName)
                  ),
                  hash(
                      hash(defaultArgument1),
                      hash(defaultArgument2)
                  )
              )
          );
      assertCall(() -> ((NativeLambda) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSequenceException(objHash, spec, DATA_PATH + "[0]", 2, 1));
    }

    @Test
    public void body_is_sequence_with_three_elements() throws Exception {
      Str classBinaryName = strVal("classBinaryName");
      Blob nativeJar = blobVal();
      Const defaultArgument1 = intExpr(1);
      Const defaultArgument2 = strExpr("abc");
      NativeLambdaSpec spec = nativeLambdaSpec(boolSpec(), intSpec(), strSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(
                      hash(classBinaryName),
                      hash(nativeJar),
                      hash(nativeJar)
                  ),
                  hash(
                      hash(defaultArgument1),
                      hash(defaultArgument2)
                  )
              )
          );

      assertCall(() -> ((NativeLambda) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjSequenceException(objHash, spec, DATA_PATH + "[0]", 2, 3));
    }

    @Test
    public void class_binary_name_is_expr_instead_of_str() throws Exception {
      Const classBinaryName = strExpr("classBinaryName");
      Blob nativeJar = blobVal();
      Const defaultArgument1 = intExpr(1);
      Const defaultArgument2 = strExpr("abc");
      NativeLambdaSpec spec = nativeLambdaSpec(boolSpec(), intSpec(), strSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(
                      hash(classBinaryName),
                      hash(nativeJar)
                  ),
                  hash(
                      hash(defaultArgument1),
                      hash(defaultArgument2)
                  )
              )
          );
      assertCall(() -> ((NativeLambda) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjNodeException(
              objHash, spec, DATA_PATH + "[0][0]", Str.class, Const.class));
    }

    @Test
    public void class_binary_name_is_int_instead_of_str() throws Exception {
      Int classBinaryName = intVal(3);
      Blob nativeJar = blobVal();
      Const defaultArgument1 = intExpr(1);
      Const defaultArgument2 = strExpr("abc");
      NativeLambdaSpec spec = nativeLambdaSpec(boolSpec(), intSpec(), strSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(
                      hash(classBinaryName),
                      hash(nativeJar)
                  ),
                  hash(
                      hash(defaultArgument1),
                      hash(defaultArgument2)
                  )
              )
          );
      assertCall(() -> ((NativeLambda) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjNodeException(
              objHash, spec, DATA_PATH + "[0][0]", Str.class, Int.class));
    }

    @Test
    public void native_jar_is_expr_instead_of_val() throws Exception {
      Const nativeJar = constExpr(blobVal());
      Str classBinaryName = strVal("classBinaryName");
      Const defaultArgument1 = intExpr(1);
      Const defaultArgument2 = strExpr("abc");
      NativeLambdaSpec spec = nativeLambdaSpec(boolSpec(), intSpec(), strSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(
                      hash(classBinaryName),
                      hash(nativeJar)
                  ),
                  hash(
                      hash(defaultArgument1),
                      hash(defaultArgument2)
                  )
              )
          );
      assertCall(() -> ((NativeLambda) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjNodeException(
              objHash, spec, DATA_PATH + "[0][1]", Blob.class, Const.class));
    }

    @Test
    public void native_jar_is_int_instead_of_blob() throws Exception {
      Int nativeJar = intVal(3);
      Str classBinaryName = strVal("classBinaryName");
      Const defaultArgument1 = intExpr(1);
      Const defaultArgument2 = strExpr("abc");
      NativeLambdaSpec spec = nativeLambdaSpec(boolSpec(), intSpec(), strSpec());
      Hash objHash =
          hash(
              hash(spec),
              hash(
                  hash(
                      hash(classBinaryName),
                      hash(nativeJar)
                  ),
                  hash(
                      hash(defaultArgument1),
                      hash(defaultArgument2)
                  )
              )
          );
      assertCall(() -> ((NativeLambda) objectDb().get(objHash)).data())
          .throwsException(new UnexpectedObjNodeException(
              objHash, spec, DATA_PATH + "[0][1]", Blob.class, Int.class));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void default_arguments_sequence_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Str classBinaryName = strVal("classBinaryName");
      Blob nativeJar = blobVal();
      Hash bodyHash = hash(
          hash(classBinaryName),
          hash(nativeJar)
      );
      NativeLambdaSpec spec = nativeLambdaSpec(boolSpec(), intSpec(), strSpec());
      test_default_arguments_sequence_size_different_than_multiple_of_hash_size(
          spec, bodyHash, byteCount, (objHash) -> ((NativeLambda) objectDb().get(objHash)).data()
      );
    }

    @Test
    public void default_arguments_sequence_element_pointing_nowhere() throws Exception {
      Str classBinaryName = strVal("classBinaryName");
      Blob nativeJar = blobVal();
      NativeLambdaSpec spec = nativeLambdaSpec(boolSpec(), intSpec(), strSpec());
      Hash bodyHash = hash(
          hash(classBinaryName),
          hash(nativeJar)
      );
      test_default_arguments_sequence_element_pointing_nowhere(
          spec, bodyHash, (objHash) -> ((NativeLambda) objectDb().get(objHash)).data());
    }

    @Test
    public void default_arguments_size_different_than_spec_parameters_size() throws Exception {
      NativeLambdaSpec spec = nativeLambdaSpec(intSpec(), blobSpec());
      Str classBinaryName = strVal("classBinaryName");
      Blob nativeJar = blobVal();
      Hash bodyHash = hash(
          hash(classBinaryName),
          hash(nativeJar)
      );
      test_default_arguments_size_different_than_spec_parameters_size(
          spec, bodyHash, (objHash) -> ((NativeLambda) objectDb().get(objHash)).data()
      );
    }

    @Test
    public void default_arguments_contain_val_instead_of_expr() throws Exception {
      NativeLambdaSpec spec = nativeLambdaSpec(boolSpec(), intSpec(), strSpec());
      Str classBinaryName = strVal("classBinaryName");
      Blob nativeJar = blobVal();
      Hash bodyHash = hash(
          hash(classBinaryName),
          hash(nativeJar)
      );
      test_default_arguments_contain_val_instead_of_expr(
          spec, bodyHash, (objHash) -> ((NativeLambda) objectDb().get(objHash)).data());
    }

    @Test
    public void default_argument_evaluation_spec_is_not_equal_function_parameter_spec()
        throws Exception {
      NativeLambdaSpec spec = nativeLambdaSpec(boolSpec(), intSpec(), strSpec());
      Str classBinaryName = strVal("classBinaryName");
      Blob nativeJar = blobVal();
      Hash bodyHash = hash(
          hash(classBinaryName),
          hash(nativeJar)
      );

      test_default_argument_evaluation_spec_is_not_equal_function_parameter_spec(spec, bodyHash,
          (objHash) -> ((NativeLambda) objectDb().get(objHash)).data());
    }
  }

  @Nested
  class _null {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save null
       * in HashedDb.
       */
      Hash objHash =
          hash(
              hash(nullSpec())
          );
      objectDb().get(objHash);
    }

    @Test
    public void root_with_data_hash() throws Exception {
      Hash dataHash = hash(intVal(33));
      Hash objHash =
          hash(
              hash(nullSpec()),
              dataHash
          );
      assertCall(() -> objectDb().get(objHash))
          .throwsException(nullObjRootException(objHash, 2));
    }
  }

  @Nested
  class _nothing {
    @Test
    public void learning_test() throws Exception {
      Hash objHash =
          hash(
              hash(nothingSpec()),
              hash("aaa"));
      assertCall(() -> objectDb().get(objHash))
          .throwsException(new UnsupportedOperationException(
              "Cannot create object for 'NOTHING' spec."));
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
              hash(strSpec()),
              hash("aaa"));
      assertThat(((Str) objectDb().get(objHash)).jValue())
          .isEqualTo("aaa");
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(strSpec());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          strSpec(),
          hashedDb().writeBoolean(true),
          (Hash objHash) -> ((Str) objectDb().get(objHash)).jValue()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          strSpec(),
          (Hash objHash) -> ((Str) objectDb().get(objHash)).jValue());
    }

    @Test
    public void data_being_invalid_utf8_sequence() throws Exception {
      Hash notStringHash = hash(ByteString.of((byte) -64));
      Hash objHash =
          hash(
              hash(strSpec()),
              notStringHash);
      assertCall(() -> ((Str) objectDb().get(objHash)).jValue())
          .throwsException(new DecodeObjNodeException(objHash, strSpec(), DATA_PATH))
          .withCause(new DecodeStringException(notStringHash, null));
    }
  }

  @Nested
  class _rec {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save rec
       * in HashedDb.
       */
      assertThat(
          hash(
              hash(personSpec()),
              hash(
                  hash(strVal("John")),
                  hash(strVal("Doe")))))
          .isEqualTo(personVal("John", "Doe").hash());
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(personSpec());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          personSpec(),
          hashedDb().writeBoolean(true),
          (Hash objHash) -> ((Rec) objectDb().get(objHash)).get(0)
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          personSpec(),
          (Hash objHash) -> ((Rec) objectDb().get(objHash)).get(0));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_sequence_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSequence = hash(ByteString.of(new byte[byteCount]));
      Hash objHash =
          hash(
              hash(personSpec()),
              notHashOfSequence);
      assertCall(() -> ((Rec) objectDb().get(objHash)).get(0))
          .throwsException(new DecodeObjNodeException(objHash, personSpec(), DATA_PATH))
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
              hash(personSpec()),
              dataHash
          );
      assertCall(() -> ((Rec) objectDb().get(objHash)).get(0))
          .throwsException(new DecodeObjNodeException(objHash, personSpec(), DATA_PATH + "[0]"))
          .withCause(new NoSuchObjException(nowhere));
    }

    @Test
    public void with_too_few_elements() throws Exception {
      Hash dataHash =
          hash(
              hash(strVal("John")));
      Hash objHash =
          hash(
              hash(personSpec()),
              dataHash);
      Rec rec = (Rec) objectDb().get(objHash);
      assertCall(() -> rec.get(0))
          .throwsException(new UnexpectedObjSequenceException(objHash, personSpec(), DATA_PATH, 2, 1));
    }

    @Test
    public void with_too_many_elements() throws Exception {
      Hash dataHash =
          hash(
              hash(strVal("John")),
              hash(strVal("Doe")),
              hash(strVal("junk")));
      Hash objHash =
          hash(
              hash(personSpec()),
              dataHash);
      Rec rec = (Rec) objectDb().get(objHash);
      assertCall(() -> rec.get(0))
          .throwsException(new UnexpectedObjSequenceException(objHash, personSpec(), DATA_PATH, 2, 3));
    }

    @Test
    public void with_element_of_wrong_spec() throws Exception {
      Hash objHash =
          hash(
              hash(personSpec()),
              hash(
                  hash(strVal("John")),
                  hash(boolVal(true))));
      Rec rec = (Rec) objectDb().get(objHash);
      assertCall(() -> rec.get(0))
          .throwsException(new UnexpectedObjNodeException(
              objHash, personSpec(), DATA_PATH, 1, strSpec(), boolSpec()));
    }

    @Test
    public void with_element_being_expr() throws Exception {
      Hash objHash =
          hash(
              hash(personSpec()),
              hash(
                  hash(strVal("John")),
                  hash(intExpr())));
      Rec rec = (Rec) objectDb().get(objHash);
      assertCall(() -> rec.get(0))
          .throwsException(new UnexpectedObjNodeException(
              objHash, personSpec(), DATA_PATH + "[1]", Val.class, Const.class));
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
              hash(refSpec(strSpec())),
              hash(byteString));
      assertThat(((Ref) objectDb().get(objHash)).value())
          .isEqualTo(BigInteger.valueOf(3 * 256 + 2));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(refSpec());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      obj_root_with_two_data_hashes(
          refSpec(),
          hashedDb().writeByte((byte) 1),
          (Hash objHash) -> ((Ref) objectDb().get(objHash)).value()
      );
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
          refSpec(),
          (Hash objHash) -> ((Ref) objectDb().get(objHash)).value());
    }
  }

  private void obj_root_without_data_hash(Spec spec) throws HashedDbException {
    Hash objHash =
        hash(
            hash(spec));
    assertCall(() -> objectDb().get(objHash))
        .throwsException(nonNullObjRootException(objHash, 1));
  }

  private void obj_root_with_two_data_hashes(
      Spec spec, Hash dataHash, Function<Hash, ?> readClosure) throws HashedDbException {
    Hash objHash =
        hash(
            hash(spec),
            dataHash,
            dataHash);
    assertCall(() -> readClosure.apply(objHash))
        .throwsException(wrongSizeOfRootSequenceException(objHash, 3));
  }

  private void obj_root_with_data_hash_not_pointing_to_obj_but_nowhere(
      Spec spec, Function<Hash, ?> readClosure) throws HashedDbException {
    Hash dataHash = Hash.of(33);
    Hash objHash =
        hash(
            hash(spec),
            dataHash);
    assertCall(() -> readClosure.apply(objHash))
        .throwsException(new DecodeObjNodeException(objHash, spec, DATA_PATH))
        .withCause(new NoSuchObjException(dataHash));
  }

  private void obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
      Spec spec, Consumer<Hash> readClosure) throws HashedDbException {
    Hash dataHash = Hash.of(33);
    Hash objHash =
        hash(
            hash(spec),
            dataHash);
    assertCall(() -> readClosure.accept(objHash))
        .throwsException(new DecodeObjNodeException(objHash, spec, DATA_PATH))
        .withCause(new NoSuchDataException(dataHash));
  }

  // helper methods for testing functionality common to DefinedLambda and NativeLambda

  private void test_default_arguments_sequence_size_different_than_multiple_of_hash_size(
      LambdaSpec spec, Hash bodyHash, int byteCount, Consumer<Hash> consumer)
      throws IOException, HashedDbException {
    Hash notHashOfSequence = hash(ByteString.of(new byte[byteCount]));
    Hash objHash =
        hash(
            hash(spec),
            hash(
                bodyHash,
                notHashOfSequence
            )
        );
    assertCall(() -> consumer.accept(objHash))
        .throwsException(new DecodeObjNodeException(objHash, spec, DATA_PATH + "[1]"))
        .withCause(
            new DecodeHashSequenceException(notHashOfSequence, byteCount % Hash.lengthInBytes()));
  }

  private void test_default_arguments_sequence_element_pointing_nowhere(
      LambdaSpec spec, Hash bodyHash, Consumer<Hash> consumer) throws HashedDbException {
    Const defaultArgument1 = intExpr(1);
    Hash nowhere = Hash.of(33);
    Hash objHash =
        hash(
            hash(spec),
            hash(
                bodyHash,
                hash(
                    hash(defaultArgument1),
                    nowhere
                )
            )
        );
    assertCall(() -> consumer.accept(objHash))
        .throwsException(new DecodeObjNodeException(objHash, spec, DATA_PATH + "[1][1]"))
        .withCause(new NoSuchObjException(nowhere));
  }

  private void test_default_arguments_size_different_than_spec_parameters_size(
      LambdaSpec spec, Hash bodyHash, Consumer<Hash> consumer) throws HashedDbException {
    Const defaultArgument1 = intExpr(1);
    Const defaultArgument2 = strExpr("abc");
    Hash objHash =
        hash(
            hash(spec),
            hash(
                bodyHash,
                hash(
                    hash(defaultArgument1),
                    hash(defaultArgument2)
                )
            )
        );
    assertCall(() -> consumer.accept(objHash))
        .throwsException(new UnexpectedObjSequenceException(objHash, spec, DATA_PATH + "[1]", 1, 2));
  }

  private void test_default_arguments_contain_val_instead_of_expr(
      LambdaSpec spec, Hash bodyHash, Consumer<Hash> consumer) throws HashedDbException {
    Const defaultArgument1 = intExpr(1);
    Str defaultArgument2 = strVal("abc");
    Hash objHash =
        hash(
            hash(spec),
            hash(
                bodyHash,
                hash(
                    hash(defaultArgument1),
                    hash(defaultArgument2)
                )
            )
        );
    assertCall(() -> consumer.accept(objHash))
        .throwsException(new UnexpectedObjNodeException(
            objHash, spec, DATA_PATH + "[1][1]", Expr.class, Str.class));
  }

  private void test_default_argument_evaluation_spec_is_not_equal_function_parameter_spec(
      LambdaSpec spec, Hash bodyHash, Consumer<Hash> consumer) throws HashedDbException {
    Const defaultArgument1 = intExpr(1);
    Const defaultArgument2 = intExpr(2);
    Hash objHash =
        hash(
            hash(spec),
            hash(
                bodyHash,
                hash(
                    hash(defaultArgument1),
                    hash(defaultArgument2)
                )
            )
        );
    assertCall(() -> consumer.accept(objHash))
        .throwsException(new DecodeExprWrongEvaluationSpecOfComponentException(
            objHash, spec, DATA_PATH + "[1][1]", strSpec(), intSpec()));
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

  protected Hash hash(Obj obj) {
    return obj.hash();
  }

  protected Hash hash(Spec spec) {
    return spec.hash();
  }

  protected Hash hash(Hash... hashes) throws HashedDbException {
    return hashedDb().writeSequence(hashes);
  }
}
