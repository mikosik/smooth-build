package org.smoothbuild.db.object.obj;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.db.object.type.ObjTypeDb.DATA_PATH;
import static org.smoothbuild.db.object.type.ObjTypeDb.LAMBDA_PARAMS_PATH;
import static org.smoothbuild.db.object.type.ObjTypeDb.LAMBDA_RESULT_PATH;
import static org.smoothbuild.db.object.type.base.ObjKind.ANY;
import static org.smoothbuild.db.object.type.base.ObjKind.ARRAY;
import static org.smoothbuild.db.object.type.base.ObjKind.BLOB;
import static org.smoothbuild.db.object.type.base.ObjKind.BOOL;
import static org.smoothbuild.db.object.type.base.ObjKind.CALL;
import static org.smoothbuild.db.object.type.base.ObjKind.CONST;
import static org.smoothbuild.db.object.type.base.ObjKind.CONSTRUCT;
import static org.smoothbuild.db.object.type.base.ObjKind.INT;
import static org.smoothbuild.db.object.type.base.ObjKind.LAMBDA;
import static org.smoothbuild.db.object.type.base.ObjKind.NATIVE_METHOD;
import static org.smoothbuild.db.object.type.base.ObjKind.NOTHING;
import static org.smoothbuild.db.object.type.base.ObjKind.ORDER;
import static org.smoothbuild.db.object.type.base.ObjKind.REF;
import static org.smoothbuild.db.object.type.base.ObjKind.SELECT;
import static org.smoothbuild.db.object.type.base.ObjKind.STRING;
import static org.smoothbuild.db.object.type.base.ObjKind.STRUCT;
import static org.smoothbuild.db.object.type.base.ObjKind.TUPLE;
import static org.smoothbuild.db.object.type.base.ObjKind.VARIABLE;
import static org.smoothbuild.testing.StringCreators.illegalString;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Named.named;
import static org.smoothbuild.util.collect.NamedList.namedList;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashingBufferedSink;
import org.smoothbuild.db.hashed.exc.DecodeHashSequenceException;
import org.smoothbuild.db.hashed.exc.DecodeStringException;
import org.smoothbuild.db.hashed.exc.HashedDbException;
import org.smoothbuild.db.hashed.exc.NoSuchDataException;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.type.base.ObjKind;
import org.smoothbuild.db.object.type.base.ObjType;
import org.smoothbuild.db.object.type.base.ValType;
import org.smoothbuild.db.object.type.exc.DecodeStructTypeWrongNamesSizeException;
import org.smoothbuild.db.object.type.exc.DecodeTypeException;
import org.smoothbuild.db.object.type.exc.DecodeTypeIllegalKindException;
import org.smoothbuild.db.object.type.exc.DecodeTypeNodeException;
import org.smoothbuild.db.object.type.exc.DecodeTypeRootException;
import org.smoothbuild.db.object.type.exc.DecodeVariableIllegalNameException;
import org.smoothbuild.db.object.type.exc.UnexpectedTypeNodeException;
import org.smoothbuild.db.object.type.exc.UnexpectedTypeSequenceException;
import org.smoothbuild.db.object.type.expr.ConstOType;
import org.smoothbuild.db.object.type.val.ArrayOType;
import org.smoothbuild.db.object.type.val.IntOType;
import org.smoothbuild.db.object.type.val.LambdaOType;
import org.smoothbuild.db.object.type.val.StringOType;
import org.smoothbuild.db.object.type.val.TupleOType;
import org.smoothbuild.testing.TestingContextImpl;
import org.smoothbuild.testing.common.AssertCall.ThrownExceptionSubject;

import com.google.common.collect.ImmutableList;

import okio.ByteString;

public class CorruptedObjTypeTest extends TestingContextImpl {
  @Nested
  class _illegal_type_marker {
    @Test
    public void causes_exception() throws Exception {
      Hash hash = hash(
          hash((byte) 99)
      );
      assertThatGet(hash)
          .throwsException(illegalTypeMarkerException(hash, 99));
    }

    @Test
    public void with_additional_child() throws Exception {
      Hash hash = hash(
          hash((byte) 99),
          hash("corrupted")
      );
      assertThatGet(hash)
          .throwsException(illegalTypeMarkerException(hash, 99));
    }
  }

  @Nested
  class _base {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save base type in HashedDb.
       */
      Hash hash = hash(
          hash(STRING.marker())
      );
      assertThat(hash)
          .isEqualTo(stringOT().hash());
    }

    @Test
    public void any_with_additional_child() throws Exception {
      test_base_type_with_additional_child(ANY);
    }

    @Test
    public void blob_with_additional_child() throws Exception {
      test_base_type_with_additional_child(BLOB);
    }

    @Test
    public void bool_with_additional_child() throws Exception {
      test_base_type_with_additional_child(BOOL);
    }

    @Test
    public void int_with_additional_child() throws Exception {
      test_base_type_with_additional_child(INT);
    }

    @Test
    public void native_method_with_additional_child() throws Exception {
      test_base_type_with_additional_child(NATIVE_METHOD);
    }

    @Test
    public void nothing_with_additional_child() throws Exception {
      test_base_type_with_additional_child(NOTHING);
    }

    @Test
    public void string_with_additional_child() throws Exception {
      test_base_type_with_additional_child(STRING);
    }

    private void test_base_type_with_additional_child(ObjKind kind) throws Exception {
      Hash hash = hash(
          hash(kind.marker()),
          hash("abc")
      );
      assertThatGet(hash)
          .throwsException(new DecodeTypeRootException(hash, kind, 2, 1));
    }
  }

  @Nested
  class _array {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save array type in HashedDb.
       */
      Hash hash = hash(
          hash(ARRAY.marker()),
          hash(stringOT())
      );
      assertThat(hash)
          .isEqualTo(arrayOT(stringOT()).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_type_without_data(ARRAY);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_type_with_additional_data(ARRAY);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_type(ARRAY);
    }

    @Test
    public void with_corrupted_type_as_data() throws Exception {
      test_type_with_corrupted_type_as_data(ARRAY);
    }

    @Test
    public void with_element_type_being_expr_type() throws Exception {
      Hash hash = hash(
          hash(ARRAY.marker()),
          hash(constOT(intOT()))
      );
      assertThatGet(hash)
          .throwsException(new UnexpectedTypeNodeException(
              hash, ARRAY, DATA_PATH, ValType.class, ConstOType.class));
    }
  }

  @Nested
  class _call {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save call type in HashedDb.
       */
      Hash hash = hash(
          hash(CALL.marker()),
          hash(intOT())
      );
      assertThat(hash)
          .isEqualTo(callOT(intOT()).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_type_without_data(CALL);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_type_with_additional_data(CALL);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_type(CALL);
    }

    @Test
    public void with_corrupted_type_as_data() throws Exception {
      test_type_with_corrupted_type_as_data(CALL);
    }

    @Test
    public void with_evaluation_type_being_expr_type() throws Exception {
      test_type_with_data_being_expr_type(CALL, ValType.class);
    }
  }

  @Nested
  class _const {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save const type in HashedDb.
       */
      Hash hash = hash(
          hash(CONST.marker()),
          hash(intOT())
      );
      assertThat(hash)
          .isEqualTo(constOT(intOT()).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_type_without_data(CONST);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_type_with_additional_data(CONST);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_type(CONST);
    }

    @Test
    public void with_corrupted_type_as_data() throws Exception {
      test_type_with_corrupted_type_as_data(CONST);
    }

    @Test
    public void with_evaluation_type_being_expr_type() throws Exception {
      test_type_with_data_being_expr_type(CONST, ValType.class);
    }
  }

  @Nested
  class _construct {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save Construct type in HashedDb.
       */
      Hash hash = hash(
          hash(CONSTRUCT.marker()),
          hash(tupleOT(list(intOT(), stringOT())))
      );
      assertThat(hash)
          .isEqualTo(constructOT(list(intOT(), stringOT())).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_type_without_data(CONSTRUCT);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_type_with_additional_data(CONSTRUCT);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_type(CONSTRUCT);
    }

    @Test
    public void with_corrupted_type_as_data() throws Exception {
      test_type_with_corrupted_type_as_data(CONSTRUCT);
    }

    @Test
    public void with_evaluation_type_being_expr_spec() throws Exception {
      test_type_with_data_being_expr_type(CONSTRUCT, TupleOType.class);
    }

    @Test
    public void with_evaluation_type_not_being_tuple_type() throws Exception {
      Hash hash = hash(
          hash(CONSTRUCT.marker()),
          hash(intOT())
      );
      assertThatGet(hash)
          .throwsException(new UnexpectedTypeNodeException(
              hash, CONSTRUCT, DATA_PATH, TupleOType.class, IntOType.class));
    }
  }

  @Nested
  class _lambda {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save lambda type in HashedDb.
       */
      ImmutableList<ValType> parameterTypes = list(stringOT(), boolOT());
      TupleOType parametersTuple = tupleOT(parameterTypes);
      Hash specHash = hash(
          hash(LAMBDA.marker()),
          hash(
              hash(intOT()),
              hash(parametersTuple)
          )
      );
      assertThat(specHash)
          .isEqualTo(lambdaOT(intOT(), parameterTypes).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_type_without_data(LAMBDA);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_type_with_additional_data(LAMBDA);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_sequence(LAMBDA);
    }

    @Test
    public void with_data_not_being_sequence_of_hashes() throws Exception {
      Hash notSequence = hash("abc");
      Hash hash =
          hash(
              hash(LAMBDA.marker()),
              notSequence
          );
      assertThatGet(hash)
          .throwsException(new DecodeTypeNodeException(hash, LAMBDA, DATA_PATH));
    }

    @Test
    public void with_data_having_three_elements() throws Exception {
      TupleOType parameterTypes = tupleOT(list(stringOT(), boolOT()));
      Hash hash = hash(
          hash(LAMBDA.marker()),
          hash(
              hash(intOT()),
              hash(parameterTypes),
              hash(parameterTypes)
          )
      );
      assertThatGet(hash)
          .throwsException(new UnexpectedTypeSequenceException(hash, LAMBDA, DATA_PATH, 2, 3));
    }

    @Test
    public void with_data_having_one_elements() throws Exception {
      Hash hash = hash(
          hash(LAMBDA.marker()),
          hash(
              hash(intOT())
          )
      );
      assertThatGet(hash)
          .throwsException(new UnexpectedTypeSequenceException(hash, LAMBDA, DATA_PATH, 2, 1));
    }

    @ParameterizedTest
    @ArgumentsSource(IllegalArrayByteSizesProvider.class)
    public void with_data_sequence_size_different_than_multiple_of_hash_size(
        int byteCount) throws Exception {
      Hash notHashOfSequence = hash(ByteString.of(new byte[byteCount]));
      Hash typeHash = hash(
          hash(LAMBDA.marker()),
          notHashOfSequence
      );
      assertCall(() -> ((LambdaOType) objTypeDb().get(typeHash)).result())
          .throwsException(new DecodeTypeNodeException(typeHash, LAMBDA, DATA_PATH))
          .withCause(new DecodeHashSequenceException(
              notHashOfSequence, byteCount % Hash.lengthInBytes()));
    }

    @Test
    public void with_result_pointing_nowhere() throws Exception {
      TupleOType parameterTypes = tupleOT(list(stringOT(), boolOT()));
      Hash nowhere = Hash.of(33);
      Hash typeHash = hash(
          hash(LAMBDA.marker()),
          hash(
              nowhere,
              hash(parameterTypes)
          )
      );
      assertCall(() -> objTypeDb().get(typeHash))
          .throwsException(new DecodeTypeNodeException(typeHash, LAMBDA, LAMBDA_RESULT_PATH))
          .withCause(new DecodeTypeException(nowhere));
    }

    @Test
    public void with_result_being_expr_type() throws Exception {
      TupleOType parameterType = tupleOT(list(stringOT(), boolOT()));
      Hash typeHash = hash(
          hash(LAMBDA.marker()),
          hash(
              hash(constOT()),
              hash(parameterType)
          )
      );
      assertCall(() -> objTypeDb().get(typeHash))
          .throwsException(new UnexpectedTypeNodeException(
              typeHash, LAMBDA, LAMBDA_RESULT_PATH, ValType.class, ConstOType.class));
    }

    @Test
    public void with_result_type_corrupted() throws Exception {
      TupleOType parameterTypes = tupleOT(list(stringOT(), boolOT()));
      Hash typeHash = hash(
          hash(LAMBDA.marker()),
          hash(
              corruptedArrayTypeHash(),
              hash(parameterTypes)
          )
      );
      assertCall(() -> objTypeDb().get(typeHash))
          .throwsException(new DecodeTypeNodeException(typeHash, LAMBDA, LAMBDA_RESULT_PATH))
          .withCause(corruptedArrayTypeException());
    }

    @Test
    public void with_parameters_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      Hash typeHash = hash(
          hash(LAMBDA.marker()),
          hash(
              hash(intOT()),
              nowhere
          )
      );
      assertCall(() -> objTypeDb().get(typeHash))
          .throwsException(new DecodeTypeNodeException(typeHash, LAMBDA, LAMBDA_PARAMS_PATH))
          .withCause(new DecodeTypeException(nowhere));
    }

    @Test
    public void with_parameters_not_being_tuple() throws Exception {
      Hash typeHash = hash(
          hash(LAMBDA.marker()),
          hash(
              hash(intOT()),
              hash(stringOT())
          )
      );
      assertThatGet(typeHash)
          .throwsException(new UnexpectedTypeNodeException(
              typeHash, LAMBDA, DATA_PATH, 1, TupleOType.class, StringOType.class));
    }

    @Test
    public void with_parameters_being_expr_type() throws Exception {
      Hash typeHash = hash(
          hash(LAMBDA.marker()),
          hash(
              hash(intOT()),
              hash(constOT())
          )
      );
      assertCall(() -> objTypeDb().get(typeHash))
          .throwsException(new UnexpectedTypeNodeException(
              typeHash, LAMBDA, LAMBDA_PARAMS_PATH, TupleOType.class, ConstOType.class));
    }

    @Test
    public void with_parameters_type_corrupted() throws Exception {
      Hash typeHash = hash(
          hash(LAMBDA.marker()),
          hash(
              hash(intOT()),
              corruptedArrayTypeHash()
          )
      );
      assertCall(() -> objTypeDb().get(typeHash))
          .throwsException(new DecodeTypeNodeException(typeHash, LAMBDA, LAMBDA_PARAMS_PATH))
          .withCause(corruptedArrayTypeException());
    }
  }

  @Nested
  class _ref {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save call type in HashedDb.
       */
      Hash hash = hash(
          hash(REF.marker()),
          hash(intOT())
      );
      assertThat(hash)
          .isEqualTo(refOT(intOT()).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_type_without_data(REF);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_type_with_additional_data(REF);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_type(REF);
    }

    @Test
    public void with_corrupted_type_as_data() throws Exception {
      test_type_with_corrupted_type_as_data(REF);
    }

    @Test
    public void with_evaluation_type_being_expr_type() throws Exception {
      test_type_with_data_being_expr_type(REF, ValType.class);
    }
  }

  private void test_type_without_data(ObjKind speckKind) throws Exception {
    Hash hash =
        hash(
            hash(speckKind.marker())
        );
    assertThatGet(hash)
        .throwsException(new DecodeTypeRootException(hash, speckKind, 1, 2));
  }

  private void test_type_with_additional_data(ObjKind objKind) throws Exception {
    Hash hash = hash(
        hash(objKind.marker()),
        hash(stringOT()),
        hash("corrupted")
    );
    assertThatGet(hash)
        .throwsException(new DecodeTypeRootException(hash, 3));
  }

  private void test_data_hash_pointing_nowhere_instead_of_being_type(ObjKind objKind)
      throws Exception {
    Hash dataHash = Hash.of(33);
    Hash typeHash = hash(
        hash(objKind.marker()),
        dataHash
    );
    assertCall(() -> objTypeDb().get(typeHash))
        .throwsException(new DecodeTypeNodeException(typeHash, objKind, DATA_PATH))
        .withCause(new DecodeTypeException(dataHash));
  }

  private void test_data_hash_pointing_nowhere_instead_of_being_sequence(ObjKind objKind)
      throws Exception {
    Hash dataHash = Hash.of(33);
    Hash typeHash = hash(
        hash(objKind.marker()),
        dataHash
    );
    assertCall(() -> objTypeDb().get(typeHash))
        .throwsException(new DecodeTypeNodeException(typeHash, objKind, DATA_PATH))
        .withCause(new NoSuchDataException(dataHash));
  }

  private void test_type_with_corrupted_type_as_data(ObjKind objKind) throws Exception {
    Hash hash =
        hash(
            hash(objKind.marker()),
            corruptedArrayTypeHash());
    assertThatGet(hash)
        .throwsException(new DecodeTypeNodeException(hash, objKind, DATA_PATH))
        .withCause(corruptedArrayTypeException());
  }

  private void test_type_with_data_being_expr_type(
      ObjKind objKind, Class<? extends ObjType> expected)
      throws Exception {
    Hash hash = hash(
        hash(objKind.marker()),
        hash(constOT(intOT()))
    );
    assertThatGet(hash)
        .throwsException(new UnexpectedTypeNodeException(
            hash, objKind, DATA_PATH, expected, ConstOType.class));
  }

  private ThrownExceptionSubject assertThatGet(Hash hash) {
      return assertCall(() -> objTypeDb().get(hash));
  }

  private DecodeTypeException illegalTypeMarkerException(Hash hash, int marker) {
    return new DecodeTypeIllegalKindException(hash, (byte) marker);
  }

  private DecodeTypeNodeException corruptedArrayTypeException() throws Exception {
    return new DecodeTypeNodeException(corruptedArrayTypeHash(), ARRAY, DATA_PATH);
  }

  private Hash corruptedArrayTypeHash() throws Exception {
    return hash(
        hash(ARRAY.marker()),
        Hash.of(33)
    );
  }

  protected Hash hash(String string) throws HashedDbException {
    return hashedDb().writeString(string);
  }

  protected Hash hash(boolean value) throws Exception {
    return hash((byte) (value ? 1 : 0));
  }

  protected Hash hash(byte value) throws Exception {
    try (HashingBufferedSink sink = hashedDb().sink()) {
      sink.writeByte(value);
      sink.close();
      return sink.hash();
    }
  }

  protected Hash hash(ByteString bytes) throws Exception {
    try (HashingBufferedSink sink = hashedDb().sink()) {
      sink.write(bytes);
      sink.close();
      return sink.hash();
    }
  }

  protected Hash hash(Obj obj) {
    return obj.hash();
  }

  protected Hash hash(ObjType type) {
    return type.hash();
  }

  protected Hash hash(Hash... hashes) throws HashedDbException {
    return hashedDb().writeSequence(hashes);
  }

  @Nested
  class _select {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save Call type in HashedDb.
       */
      Hash hash = hash(
          hash(SELECT.marker()),
          hash(intOT())
      );
      assertThat(hash)
          .isEqualTo(selectOT(intOT()).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_type_without_data(SELECT);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_type_with_additional_data(SELECT);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_type(SELECT);
    }

    @Test
    public void with_corrupted_type_as_data() throws Exception {
      test_type_with_corrupted_type_as_data(SELECT);
    }

    @Test
    public void with_evaluation_type_being_expr_type() throws Exception {
      test_type_with_data_being_expr_type(SELECT, ValType.class);
    }
  }

  @Nested
  class _struct {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save Struct type in HashedDb.
       */
      var name = "MyStruct";
      var field1 = intOT();
      var field2 = stringOT();
      var name1 = "field1";
      var name2 = "field2";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              hash(name),
              hash(
                  hash(field1),
                  hash(field2)
              ),
              hash(
                  hash(name1),
                  hash(name2)
              )
          )
      );
      assertThat(hash).isEqualTo(
          structOT(namedList(list(named(name1, field1), named(name2, field2)))).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_type_without_data(STRUCT);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_type_with_additional_data(STRUCT);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_sequence(STRUCT);
    }

    @Test
    public void with_name_pointing_nowhere() throws Exception {
      Hash nowhere = Hash.of(33);
      var field1 = intOT();
      var field2 = stringOT();
      var name1 = "field1";
      var name2 = "field2";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              nowhere,
              hash(
                  hash(field1),
                  hash(field2)
              ),
              hash(
                  hash(name1),
                  hash(name2)
              )
          )
      );
      assertThatGet(hash)
          .throwsException(new DecodeTypeNodeException(hash, STRUCT, DATA_PATH + "[0]"));
    }

    @Test
    public void with_illegal_name() throws Exception {
      Hash nameHash = hash(illegalString());
      var field1 = intOT();
      var field2 = stringOT();
      var name1 = "field1";
      var name2 = "field2";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              hash(nameHash),
              hash(
                  hash(field1),
                  hash(field2)
              ),
              hash(
                  hash(name1),
                  hash(name2)
              )
          )
      );
      assertThatGet(hash)
          .throwsException(new DecodeTypeNodeException(hash, STRUCT, DATA_PATH + "[0]"))
          .withCause(DecodeStringException.class);
    }

//      TODO

    @Test
    public void with_elements_not_being_sequence_of_hashes() throws Exception {
      Hash notSequence = hash("abc");
      var name = "MyStruct";
      var name1 = "field1";
      var name2 = "field2";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              hash(name),
              notSequence,
              hash(
                  hash(name1),
                  hash(name2)
              )
          )
      );
      assertThatGet(hash)
          .throwsException(new DecodeTypeNodeException(hash, STRUCT, "data[1]"));
    }

    @Test
    public void with_elements_being_sequence_of_non_type() throws Exception {
      var name = "MyStruct";
      Hash stringHash = hash(string("abc"));
      var name1 = "field1";
      var name2 = "field2";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              hash(name),
              hash(
                  stringHash
              ),
              hash(
                  hash(name1),
                  hash(name2)
              )
          )
      );
      assertThatGet(hash)
          .throwsException(new DecodeTypeNodeException(hash, STRUCT, "data[1][0]"))
          .withCause(new DecodeTypeException(stringHash));
    }

    @Test
    public void with_elements_being_sequence_of_expr_type() throws Exception {
      var name = "MyStruct";
      var name1 = "field1";
      var name2 = "field2";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              hash(name),
              hash(
                  hash(constOT())
              ),
              hash(
                  hash(name1),
                  hash(name2)
              )
          )
      );
      assertThatGet(hash)
          .throwsException(new UnexpectedTypeNodeException(
              hash, STRUCT, "data[1]", 0, ValType.class, ConstOType.class));
    }

    @Test
    public void with_corrupted_element_type() throws Exception {
      var name = "MyStruct";
      var field1 = intOT();
      var name1 = "field1";
      var name2 = "field2";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              hash(name),
              hash(
                  hash(field1),
                  corruptedArrayTypeHash()
              ),
              hash(
                  hash(name1),
                  hash(name2)
              )
          )
      );
      assertThatGet(hash)
          .throwsException(new DecodeTypeNodeException(hash, STRUCT, "data[1][1]"))
          .withCause(corruptedArrayTypeException());
    }

    @Test
    public void with_names_size_different_than_items_size() throws Exception {
      var name = "MyStruct";
      var field1 = intOT();
      var field2 = stringOT();
      var name1 = "field1";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              hash(name),
              hash(
                  hash(field1),
                  hash(field2)
              ),
              hash(
                  hash(name1)
              )
          )
      );
      assertThatGet(hash)
          .throwsException(new DecodeStructTypeWrongNamesSizeException(hash, 2, 1));
    }

    @Test
    public void with_names_containing_illegal_string() throws Exception {
      var name = "MyStruct";
      var field1 = intOT();
      var field2 = stringOT();
      Hash illegalString = hash(illegalString());
      var name1 = "field1";
      Hash hash = hash(
          hash(STRUCT.marker()),
          hash(
              hash(name),
              hash(
                  hash(field1),
                  hash(field2)
              ),
              hash(
                  hash(name1),
                  illegalString
              )
          )
      );
      assertThatGet(hash)
          .throwsException(new DecodeTypeNodeException(hash, STRUCT, "data[2][1]"))
          .withCause(DecodeStringException.class);
    }
  }

  @Nested
  class _tuple {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save Tuple type in HashedDb.
       */
      Hash hash = hash(
          hash(TUPLE.marker()),
          hash(
              hash(stringOT()),
              hash(stringOT())
          )
      );
      assertThat(hash)
          .isEqualTo(perso_OT().hash());
    }

    @Test
    public void without_data() throws Exception {
      test_type_without_data(TUPLE);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_type_with_additional_data(TUPLE);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_sequence(TUPLE);
    }

    @Test
    public void with_elements_not_being_sequence_of_hashes() throws Exception {
      Hash notSequence = hash("abc");
      Hash hash =
          hash(
              hash(TUPLE.marker()),
              notSequence
          );
      assertThatGet(hash)
          .throwsException(new DecodeTypeNodeException(hash, TUPLE, DATA_PATH));
    }

    @Test
    public void with_elements_being_array_of_non_type() throws Exception {
      Hash stringHash = hash(string("abc"));
      Hash hash =
          hash(
              hash(TUPLE.marker()),
              hash(
                  stringHash
              )
          );
      assertThatGet(hash)
          .throwsException(new DecodeTypeNodeException(hash, TUPLE, "data[0]"))
          .withCause(new DecodeTypeException(stringHash));
    }

    @Test
    public void with_elements_being_sequence_of_expr_type() throws Exception {
      Hash hash =
          hash(
              hash(TUPLE.marker()),
              hash(
                  hash(constOT())
              )
          );
      assertThatGet(hash)
          .throwsException(new UnexpectedTypeNodeException(
              hash, TUPLE, "data", 0, ValType.class, ConstOType.class));
    }

    @Test
    public void with_corrupted_element_type() throws Exception {
      Hash hash =
          hash(
              hash(TUPLE.marker()),
              hash(
                  corruptedArrayTypeHash(),
                  hash(stringOT())));
      assertThatGet(hash)
          .throwsException(new DecodeTypeNodeException(hash, TUPLE, "data[0]"))
          .withCause(corruptedArrayTypeException());
    }
  }

  @Nested
  class _order {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save Order type in HashedDb.
       */
      Hash hash = hash(
          hash(ORDER.marker()),
          hash(arrayOT(intOT()))
      );
      assertThat(hash)
          .isEqualTo(orderOT(intOT()).hash());
    }

    @Test
    public void without_data() throws Exception {
      test_type_without_data(ORDER);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_type_with_additional_data(ORDER);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      test_data_hash_pointing_nowhere_instead_of_being_type(ORDER);
    }

    @Test
    public void with_corrupted_type_as_data() throws Exception {
      test_type_with_corrupted_type_as_data(ORDER);
    }

    @Test
    public void with_evaluation_type_being_expr_type() throws Exception {
      test_type_with_data_being_expr_type(ORDER, ArrayOType.class);
    }

    @Test
    public void with_evaluation_type_not_being_array_type() throws Exception {
      Hash hash = hash(
          hash(ORDER.marker()),
          hash(intOT())
      );
      assertThatGet(hash)
          .throwsException(new UnexpectedTypeNodeException(
              hash, ORDER, DATA_PATH, ArrayOType.class, IntOType.class));
    }
  }

  @Nested
  class _variable {
    @Test
    public void learning_test() throws Exception {
      /*
       * This test makes sure that other tests in this class use proper scheme
       * to save Variable type in HashedDb.
       */
      Hash hash = hash(
          hash(VARIABLE.marker()),
          hash("A")
      );
      assertThat(hash)
          .isEqualTo(variableOT("A").hash());
    }

    @Test
    public void without_data() throws Exception {
      test_type_without_data(VARIABLE);
    }

    @Test
    public void with_additional_data() throws Exception {
      test_type_with_additional_data(VARIABLE);
    }

    @Test
    public void with_data_hash_pointing_nowhere() throws Exception {
      Hash dataHash = Hash.of(33);
      Hash typeHash = hash(
          hash(VARIABLE.marker()),
          dataHash
      );
      assertCall(() -> objTypeDb().get(typeHash))
          .throwsException(new DecodeTypeNodeException(typeHash, VARIABLE, DATA_PATH))
          .withCause(new NoSuchDataException(dataHash));
    }

    @Test
    public void with_corrupted_type_as_data() throws Exception {
      Hash hash =
          hash(
              hash(VARIABLE.marker()),
              corruptedArrayTypeHash());
      assertThatGet(hash)
          .throwsException(new DecodeTypeNodeException(hash, VARIABLE, DATA_PATH))
          .withCause(DecodeStringException.class);
    }

    @Test
    public void with_illegal_name() throws Exception {
      Hash hash = hash(
          hash(VARIABLE.marker()),
          hash("a")
      );
      assertThatGet(hash)
          .throwsException(new DecodeVariableIllegalNameException(hash, "a"));
    }
  }
}
