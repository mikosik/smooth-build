package org.smoothbuild.db.object.obj;

import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.db.object.db.DecodeObjRootException.nonNullObjRootException;
import static org.smoothbuild.db.object.db.DecodeObjRootException.nullObjRootException;
import static org.smoothbuild.db.object.spec.base.SpecKind.ARRAY;
import static org.smoothbuild.db.object.spec.base.SpecKind.BLOB;
import static org.smoothbuild.db.object.spec.base.SpecKind.BOOL;
import static org.smoothbuild.db.object.spec.base.SpecKind.CALL;
import static org.smoothbuild.db.object.spec.base.SpecKind.CONST;
import static org.smoothbuild.db.object.spec.base.SpecKind.EARRAY;
import static org.smoothbuild.db.object.spec.base.SpecKind.FIELD_READ;
import static org.smoothbuild.db.object.spec.base.SpecKind.NOTHING;
import static org.smoothbuild.db.object.spec.base.SpecKind.NULL;
import static org.smoothbuild.db.object.spec.base.SpecKind.RECORD;
import static org.smoothbuild.db.object.spec.base.SpecKind.REF;
import static org.smoothbuild.db.object.spec.base.SpecKind.STRING;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
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
import org.smoothbuild.db.hashed.DecodeBooleanException;
import org.smoothbuild.db.hashed.DecodeByteException;
import org.smoothbuild.db.hashed.DecodeHashSequenceException;
import org.smoothbuild.db.hashed.DecodeStringException;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.db.hashed.NoSuchDataException;
import org.smoothbuild.db.object.db.DecodeDataSequenceException;
import org.smoothbuild.db.object.db.DecodeObjException;
import org.smoothbuild.db.object.db.DecodeObjRootException;
import org.smoothbuild.db.object.db.DecodeSpecException;
import org.smoothbuild.db.object.db.DecodeSpecRootException;
import org.smoothbuild.db.object.db.ObjectDbException;
import org.smoothbuild.db.object.obj.base.Expr;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.expr.Call;
import org.smoothbuild.db.object.obj.expr.Const;
import org.smoothbuild.db.object.obj.expr.EArray;
import org.smoothbuild.db.object.obj.expr.FieldRead;
import org.smoothbuild.db.object.obj.expr.Ref;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.Bool;
import org.smoothbuild.db.object.obj.val.Int;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.db.object.obj.val.Str;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.db.object.spec.base.SpecKind;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.common.AssertCall.ThrownExceptionSubject;

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
          .throwsException(new DecodeObjException(objHash))
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
          .throwsException(new DecodeObjException(objHash))
          .withCause(new DecodeSpecException(specHash));
    }

    @Test
    public void reading_elements_from_not_stored_object_throws_exception() {
      Hash objHash = Hash.of(33);
      assertCall(() -> objectDb().get(objHash))
          .throwsException(new DecodeObjException(objHash))
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
      obj_root_with_data_hash_not_pointing_to_obj_but_nowhere(
          arraySpec(intSpec()),
          (Hash objHash) -> ((Array) objectDb().get(objHash)).elements(Int.class));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_sequence_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSequence = hash(ByteString.of(new byte[byteCount]));
      Hash objHash =
          hash(
              hash(arraySpec(strSpec())),
              notHashOfSequence
          );
      assertCall(() -> ((Array) objectDb().get(objHash)).elements(Val.class))
          .throwsException(new DecodeObjException(objHash))
          .withCause(new DecodeObjException(notHashOfSequence));
    }

    @Test
    public void with_sequence_element_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash dataHash = hash(
          nowhere
      );
      Hash objHash =
          hash(
              hash(arraySpec(strSpec())),
              dataHash);
      assertCall(() -> ((Array) objectDb().get(objHash)).elements(Str.class))
          .throwsException(new DecodeObjException(objHash))
          .withCause(new DecodeObjException(nowhere));
    }

    @Test
    public void with_one_element_of_wrong_spec() throws Exception {
      Hash objHash =
          hash(
              hash(arraySpec(strSpec())),
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
          .throwsException(new DecodeObjException(objHash,
              "It is array which spec == [STRING] but one of its elements has spec == BOOL"));
    }

    @Test
    public void with_one_element_being_expr() throws Exception {
      Hash objHash =
          hash(
              hash(arraySpec(strSpec())),
              hash(
                  hash(
                      hash(strSpec()),
                      hash("aaa")
                  ),
                  hash(constExpr())
              ));
      assertCall(() -> ((Array) objectDb().get(objHash)).elements(Str.class))
          .throwsException(new DecodeObjException(objHash,
              "It is array which spec == [STRING] but one of its elements has spec == CONST"));
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
          .throwsException(new DecodeObjException(objHash))
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
          .throwsException(new DecodeObjException(objHash))
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
          .throwsException(new DecodeObjException(objHash))
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
      Const function = constExpr(intVal(0));
      Const arg1 = constExpr(intVal(1));
      Const arg2 = constExpr(intVal(2));
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
      assertThat(((Call) objectDb().get(objHash)).function())
          .isEqualTo(function);
      assertThat(((Call) objectDb().get(objHash)).arguments())
          .isEqualTo(list(arg1, arg2));
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(callSpec());
    }

    @Test
    public void root_with_two_data_hashes() throws Exception {
      Const function = constExpr(intVal(0));
      Const arg1 = constExpr(intVal(1));
      Const arg2 = constExpr(intVal(2));
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
          (Hash objHash) -> ((Call) objectDb().get(objHash)).function());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_obj_but_nowhere(
          callSpec(),
          (Hash objHash) -> ((Call) objectDb().get(objHash)).function());
    }

    @Test
    public void data_is_sequence_with_one_element() throws Exception {
      Const function = constExpr(intVal(0));
      Hash dataHash = hash(
          hash(function)
      );
      Hash objHash =
          hash(
              hash(callSpec()),
              dataHash
          );
      assertCall(() -> ((Call) objectDb().get(objHash)).function())
          .throwsException(new DecodeDataSequenceException(objHash, dataHash, 2, 1));
    }

    @Test
    public void data_is_sequence_with_three_elements() throws Exception {
      Const function = constExpr(intVal(0));
      Const arg1 = constExpr(intVal(1));
      Const arg2 = constExpr(intVal(2));
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
      assertCall(() -> ((Call) objectDb().get(objHash)).function())
          .throwsException(new DecodeDataSequenceException(objHash, dataHash, 2, 3));
    }

    @Test
    public void function_is_val_instead_of_expr() throws Exception {
      Int val = intVal(0);
      Const arg1 = constExpr(intVal(1));
      Const arg2 = constExpr(intVal(2));
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
      assertCall(() -> ((Call) objectDb().get(objHash)).function())
          .throwsException(new DecodeObjException(
              objHash, "Its data[0] should contain Expr but contains INT."));
    }


    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void arguments_sequence_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSequence = hash(ByteString.of(new byte[byteCount]));
      Const function = constExpr(intVal(0));
      Hash objHash =
          hash(
              hash(callSpec()),
              hash(
                  hash(function),
                  notHashOfSequence
              )
          );
      assertCall(() -> ((Call) objectDb().get(objHash)).arguments())
          .throwsException(new DecodeObjException(objHash))
          .withCause(new DecodeObjException(notHashOfSequence));
    }

    @Test
    public void arguments_sequence_element_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Const function = constExpr(intVal(0));
      Const arg1 = constExpr(intVal(1));
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
      assertCall(() -> ((Call) objectDb().get(objHash)).arguments())
          .throwsException(new DecodeObjException(objHash))
          .withCause(new DecodeObjException(nowhere));
    }

    @Test
    public void arguments_contain_val_instead_of_expr() throws Exception {
      Const function = constExpr(intVal(0));
      Const arg1 = constExpr(intVal(1));
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
      assertCall(() -> ((Call) objectDb().get(objHash)).arguments())
          .throwsException(new DecodeObjException(
              objHash, "It is CALL but one of its elements is INT instead of Expr."));
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
          .throwsException(new DecodeObjException(
              objHash, "Its data should contain Val but contains CONST."));
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
      Const expr1 = constExpr(intVal(1));
      Const expr2 = constExpr(intVal(2));
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
      Const expr1 = constExpr(intVal(1));
      Const expr2 = constExpr(intVal(2));
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
      obj_root_with_data_hash_not_pointing_to_obj_but_nowhere(
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
          .throwsException(new DecodeObjException(objHash))
          .withCause(new DecodeObjException(notHashOfSequence));
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
          .throwsException(new DecodeObjException(objHash))
          .withCause(new DecodeObjException(nowhere));
    }

    @Test
    public void with_one_element_being_val() throws Exception {
      Const expr1 = constExpr(intVal(1));
      Int val = intVal(123);
      Hash objHash =
          hash(
              hash(eArraySpec()),
              hash(
                  hash(expr1),
                  hash(val)
              ));
      assertCall(() -> ((EArray) objectDb().get(objHash)).elements())
          .throwsException(new DecodeObjException(
              objHash, "It is EARRAY but one of its elements is INT instead of Expr."));
    }
  }

  @Nested
  class _field_read {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme to save smooth
       * field_read in HashedDb.
       */
      Val index = objectDb().intVal(BigInteger.valueOf(2));
      Const expr = objectDb().constExpr(objectDb().intVal(BigInteger.valueOf(123)));
      Hash objHash =
          hash(
              hash(fieldReadSpec()),
              hash(
                  hash(expr),
                  hash(index)
              )
          );
      assertThat(((FieldRead) objectDb().get(objHash)).rec())
          .isEqualTo(expr);
      assertThat(((FieldRead) objectDb().get(objHash)).index())
          .isEqualTo(index);
    }

    @Test
    public void root_without_data_hash() throws Exception {
      obj_root_without_data_hash(fieldReadSpec());
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
          fieldReadSpec(),
          dataHash,
          (Hash objHash) -> ((FieldRead) objectDb().get(objHash)).rec());
    }

    @Test
    public void root_with_data_hash_pointing_nowhere() throws Exception {
      obj_root_with_data_hash_not_pointing_to_obj_but_nowhere(
          fieldReadSpec(),
          (Hash objHash) -> ((FieldRead) objectDb().get(objHash)).rec());
    }

    @Test
    public void data_is_sequence_with_one_element() throws Exception {
      Const expr = objectDb().constExpr(objectDb().intVal(BigInteger.valueOf(123)));
      Hash dataHash = hash(
          hash(expr)
      );
      Hash objHash =
          hash(
              hash(fieldReadSpec()),
              dataHash
          );
      assertCall(() -> ((FieldRead) objectDb().get(objHash)).rec())
          .throwsException(new DecodeDataSequenceException(objHash, dataHash, 2, 1));
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
              hash(fieldReadSpec()),
              dataHash
          );
      assertCall(() -> ((FieldRead) objectDb().get(objHash)).rec())
          .throwsException(new DecodeDataSequenceException(objHash, dataHash, 2, 3));
    }

    @Test
    public void rec_is_val_instead_of_expr() throws Exception {
      Val val = objectDb().intVal(BigInteger.valueOf(2));
      Val index = objectDb().intVal(BigInteger.valueOf(2));
      Hash objHash =
          hash(
              hash(fieldReadSpec()),
              hash(
                  hash(val),
                  hash(index)
              )
          );
      assertCall(() -> ((FieldRead) objectDb().get(objHash)).rec())
          .throwsException(new DecodeObjException(
              objHash, "Its data[0] should contain Expr but contains Val."));
    }

    @Test
    public void index_is_string_instead_of_int() throws Exception {
      Val strVal = objectDb().strVal("abc");
      Const expr = objectDb().constExpr(objectDb().intVal(BigInteger.valueOf(123)));
      Hash objHash =
          hash(
              hash(fieldReadSpec()),
              hash(
                  hash(expr),
                  hash(strVal)
              )
          );
      assertCall(() -> ((FieldRead) objectDb().get(objHash)).index())
          .throwsException(new DecodeObjException(
              objHash, "Its data[1] should contain INT but contains STRING."));
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
          .throwsException(new ObjectDbException("Cannot create java object for 'NOTHING' spec."));
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
          .throwsException(new DecodeObjException(objHash))
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
      obj_root_with_data_hash_not_pointing_to_obj_but_nowhere(
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
          .throwsException(new DecodeObjException(objHash))
          .withCause(new DecodeObjException(notHashOfSequence));
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
          .throwsException(new DecodeObjException(objHash))
          .withCause(new DecodeObjException(nowhere));
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
          .throwsException(new DecodeDataSequenceException(objHash, dataHash, 2, 1));
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
          .throwsException(new DecodeDataSequenceException(objHash, dataHash, 2, 3));
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
          .throwsException(new DecodeObjException(objHash,
              "Its RECORD spec declares element 1 to have STRING spec but its data has object"
                  + " with BOOL spec at that index."));
    }

    @Test
    public void with_element_being_expr() throws Exception {
      Hash objHash =
          hash(
              hash(personSpec()),
              hash(
                  hash(strVal("John")),
                  hash(constExpr())));
      Rec rec = (Rec) objectDb().get(objHash);
      assertCall(() -> rec.get(0))
          .throwsException(new DecodeObjException(objHash,
              "Its RECORD spec declares element 1 to have STRING spec but its data has object"
                  + " with CONST spec at that index."));
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
              hash(refSpec()),
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

  @Nested
  class _spec {
    @Nested
    class learn {
      @Test
      public void creating_base_spec() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save base spec in HashedDb.
         */
        Hash hash = hash(
            hash(STRING.marker())
        );
        assertThat(hash)
            .isEqualTo(strSpec().hash());
      }

      @Test
      public void creating_array_spec() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save array spec in HashedDb.
         */
        Hash hash = hash(
            hash(ARRAY.marker()),
            hash(strSpec())
        );
        assertThat(hash)
            .isEqualTo(arraySpec(strSpec()).hash());
      }

      @Test
      public void creating_rec_spec() throws Exception {
        /*
         * This test makes sure that other tests in this class use proper scheme
         * to save rec spec in HashedDb.
         */
        Hash hash = hash(
            hash(RECORD.marker()),
            hash(
                hash(strSpec()),
                hash(strSpec())
            )
        );
        assertThat(hash)
            .isEqualTo(personSpec().hash());
      }
    }

    @Nested
    class illegal_spec_marker {
      @Test
      public void causes_exception() throws Exception {
        Hash hash = hash(
            hash((byte) 99)
        );
        assertThatGetSpec(hash)
            .throwsException(illegalSpecMarkerException(hash, 99));
      }

      @Test
      public void with_additional_child_causes_exception() throws Exception {
        Hash hash = hash(
            hash((byte) 99),
            hash("corrupted")
        );
        assertThatGetSpec(hash)
            .throwsException(illegalSpecMarkerException(hash, 99));
      }
    }

    @Nested
    class base_spec {
      @Test
      public void blob_with_additional_child_causes_exception() throws Exception {
        do_test_with_additional_child(BLOB);
      }

      @Test
      public void bool_with_additional_child_causes_exception() throws Exception {
        do_test_with_additional_child(BOOL);
      }

      @Test
      public void call_with_additional_child_causes_exception() throws Exception {
        do_test_with_additional_child(CALL);
      }

      @Test
      public void const_with_additional_child_causes_exception() throws Exception {
        do_test_with_additional_child(CONST);
      }

      @Test
      public void earray_with_additional_child_causes_exception() throws Exception {
        do_test_with_additional_child(EARRAY);
      }

      @Test
      public void field_read_with_additional_child_causes_exception() throws Exception {
        do_test_with_additional_child(FIELD_READ);
      }

      @Test
      public void null_with_additional_child_causes_exception() throws Exception {
        do_test_with_additional_child(NULL);
      }

      @Test
      public void nothing_with_additional_child_causes_exception() throws Exception {
        do_test_with_additional_child(NOTHING);
      }

      @Test
      public void string_with_additional_child_causes_exception() throws Exception {
        do_test_with_additional_child(STRING);
      }

      @Test
      public void ref_with_additional_child_causes_exception() throws Exception {
        do_test_with_additional_child(REF);
      }

      private void do_test_with_additional_child(SpecKind kind) throws Exception {
        Hash hash = hash(
            hash(kind.marker()),
            hash("abc")
        );
        assertThatGetSpec(hash)
            .throwsException(new DecodeSpecException(hash, brokenSpecMessage(kind, 2, 1)));
      }
    }

    @Nested
    class rec_spec {
      @Test
      public void without_elements_causes_exception() throws Exception {
        Hash hash =
            hash(
                hash(RECORD.marker())
            );
        assertThatGetSpec(hash)
            .throwsException(new DecodeSpecException(hash, brokenSpecMessage(RECORD, 1, 2)));
      }

      @Test
      public void with_additional_child() throws Exception {
        Hash hash = hash(
            hash(RECORD.marker()),
            hash(
                hash(strSpec()),
                hash(strSpec())
            ),
            hash("corrupted")
        );
        assertThatGetSpec(hash)
            .throwsException(new DecodeSpecRootException(hash, 3));
      }

      @Test
      public void with_elements_not_being_sequence_of_hashes_causes_exception() throws Exception {
        Hash hash =
            hash(
                hash(RECORD.marker()),
                hash("abc")
            );
        assertThatGetSpec(hash)
            .throwsException(new DecodeSpecException(
                hash, "Its specKind == RECORD but reading its element specs caused error."));
      }

      @Test
      public void with_elements_being_array_of_non_spec_causes_exception() throws Exception {
        Hash stringHash = hash(strVal("abc"));
        Hash hash =
            hash(
                hash(RECORD.marker()),
                hash(
                    stringHash
                )
            );
        assertThatGetSpec(hash)
            .throwsException(new DecodeSpecException(hash, elementReadingErrorMessage(0)))
            .withCause(new DecodeSpecException(stringHash));
      }

      @Test
      public void with_corrupted_element_spec_causes_exception() throws Exception {
        Hash notASpecHash = hash("not a spec");
        Hash hash =
            hash(
                hash(RECORD.marker()),
                hash(
                    notASpecHash,
                    hash(strSpec())));
        assertThatGetSpec(hash)
            .throwsException(new DecodeSpecException(hash, elementReadingErrorMessage(0)))
            .withCause(new DecodeSpecException(notASpecHash));
      }
    }

    @Nested
    class array_spec {
      @Test
      public void without_element_spec_causes_exception() throws Exception {
        Hash hash =
            hash(
                hash(ARRAY.marker())
            );
        assertThatGetSpec(hash)
            .throwsException(new DecodeSpecException(hash, brokenSpecMessage(ARRAY, 1, 2)));
      }

      @Test
      public void with_additional_child() throws Exception {
        Hash hash = hash(
            hash(ARRAY.marker()),
            hash(strSpec()),
            hash("corrupted")
        );
        assertThatGetSpec(hash)
            .throwsException(new DecodeSpecRootException(hash, 3));
      }

      @Test
      public void with_corrupted_element_spec_causes_exception() throws Exception {
        Hash notASpecHash = hash("not a type");
        Hash hash =
            hash(
                hash(ARRAY.marker()),
                notASpecHash);
        assertThatGetSpec(hash)
            .throwsException(new DecodeSpecException(hash));
      }

      @Test
      public void with_element_spec_being_expr_causes_exception() throws Exception {
        Hash hash = hash(
            hash(ARRAY.marker()),
            hash(constSpec())
        );
        assertThatGetSpec(hash)
            .throwsException(new DecodeSpecException(hash,
                "It is ARRAY Spec which element Spec is CONST but should be Spec of some Val."));
      }
    }

    private ThrownExceptionSubject assertThatGetSpec(Hash hash) {
      return assertCall(() -> objectDb().getSpec(hash));
    }

    private String brokenSpecMessage(SpecKind specKind, int actual, int expected) {
      return "Its specKind == " + specKind + " but its merkle root has " + actual
          + " children when " + expected + " is expected.";
    }

    private String elementReadingErrorMessage(int index) {
      return "Its specKind == RECORD but reading element spec at index " + index + " caused error.";
    }

    private DecodeSpecException illegalSpecMarkerException(Hash hash, int marker) {
      return new DecodeSpecException(hash, "It has illegal SpecKind marker = " + marker + ".");
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
        .throwsException(new DecodeObjRootException(objHash, 3));
  }

  private void obj_root_with_data_hash_not_pointing_to_obj_but_nowhere(
      Spec spec, Function<Hash, ?> readClosure) throws HashedDbException {
    Hash dataHash = Hash.of(33);
    Hash objHash =
        hash(
            hash(spec),
            dataHash);
    assertCall(() -> readClosure.apply(objHash))
        .throwsException(new DecodeObjException(objHash))
        .withCause(new DecodeObjException(dataHash));
  }

  private void obj_root_with_data_hash_not_pointing_to_raw_data_but_nowhere(
      Spec spec, Function<Hash, ?> readClosure) throws HashedDbException {
    Hash dataHash = Hash.of(33);
    Hash objHash =
        hash(
            hash(spec),
            dataHash);
    assertCall(() -> readClosure.apply(objHash))
        .throwsException(new DecodeObjException(objHash))
        .withCause(new NoSuchDataException(dataHash));
  }

  private static class IllegalArrayByteSizesProvider implements ArgumentsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return IntStream.rangeClosed(1, Hash.lengthInBytes() * 3 + 1)
          .filter(i -> i % Hash.lengthInBytes() != 0)
          .mapToObj(Arguments::of);
    }
  }

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