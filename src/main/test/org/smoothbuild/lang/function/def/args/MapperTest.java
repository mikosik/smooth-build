package org.smoothbuild.lang.function.def.args;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.smoothbuild.lang.base.Types.BLOB;
import static org.smoothbuild.lang.base.Types.BLOB_ARRAY;
import static org.smoothbuild.lang.base.Types.FILE;
import static org.smoothbuild.lang.base.Types.FILE_ARRAY;
import static org.smoothbuild.lang.base.Types.NIL;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.base.Types.STRING_ARRAY;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Parameter.optionalParameter;
import static org.smoothbuild.lang.function.def.args.Argument.namedArgument;
import static org.smoothbuild.lang.function.def.args.Argument.namelessArgument;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.willReturn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.def.args.err.AmbiguousNamelessArgsError;
import org.smoothbuild.lang.function.def.args.err.DuplicateArgNameError;
import org.smoothbuild.lang.function.def.args.err.TypeMismatchError;
import org.smoothbuild.lang.function.def.args.err.UnknownParamNameError;
import org.smoothbuild.lang.function.nativ.Invoker;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.testing.message.FakeLoggedMessages;

import com.google.common.collect.ImmutableMap;

public class MapperTest {
  FakeLoggedMessages messages;

  // converting named arguments

  @Test
  public void converting_named_argument() {
    do_test_converting_named_argument(STRING, STRING);
    do_test_converting_named_argument(BLOB, BLOB);
    do_test_converting_named_argument(FILE, FILE);

    do_test_converting_named_argument(STRING_ARRAY, STRING_ARRAY);
    do_test_converting_named_argument(BLOB_ARRAY, BLOB_ARRAY);
    do_test_converting_named_argument(FILE_ARRAY, FILE_ARRAY);

    // conversions

    do_test_converting_named_argument(BLOB, FILE);
    do_test_converting_named_argument(BLOB_ARRAY, FILE_ARRAY);

    do_test_converting_named_argument(STRING_ARRAY, NIL);
    do_test_converting_named_argument(BLOB_ARRAY, NIL);
    do_test_converting_named_argument(FILE_ARRAY, NIL);
  }

  private void do_test_converting_named_argument(Type<?> paramType, Type<?> argType) {
    // given
    messages = new FakeLoggedMessages();
    Parameter p1 = optionalParameter(paramType, "name1");
    Parameter p2 = optionalParameter(paramType, "name2");

    Argument a1 = arg(p1.name(), argType);

    // when
    Map<Parameter, Argument> mapping = createMapping(params(p1, p2), list(a1));

    // then
    messages.assertNoProblems();
    assertEquals(ImmutableMap.of(p1, a1), mapping);
  }

  @Test
  public void duplicated_names() {
    do_test_duplicated_names(STRING, STRING);
    do_test_duplicated_names(STRING_ARRAY, STRING_ARRAY);
    do_test_duplicated_names(BLOB, BLOB);
    do_test_duplicated_names(BLOB_ARRAY, BLOB_ARRAY);
    do_test_duplicated_names(FILE, FILE);
    do_test_duplicated_names(FILE_ARRAY, FILE_ARRAY);

    // conversions
    do_test_duplicated_names(BLOB, FILE);
    do_test_duplicated_names(BLOB_ARRAY, FILE_ARRAY);
    do_test_duplicated_names(STRING_ARRAY, NIL);
    do_test_duplicated_names(BLOB_ARRAY, NIL);
    do_test_duplicated_names(FILE_ARRAY, NIL);
  }

  private void do_test_duplicated_names(Type<?> paramType, Type<?> argType) {
    // given
    messages = new FakeLoggedMessages();
    Parameter p1 = optionalParameter(paramType, "name1");

    Argument a1 = arg(p1.name(), argType);
    Argument a2 = arg(p1.name(), argType);

    // when
    createMapping(params(p1), list(a1, a2));

    // then
    messages.assertContainsOnly(DuplicateArgNameError.class);
  }

  @Test
  public void unknown_param_name() {
    // given
    messages = new FakeLoggedMessages();
    Parameter p1 = optionalParameter(STRING, "name1");
    Argument a1 = arg("otherName", STRING);

    // when
    createMapping(params(p1), list(a1));

    // then
    messages.assertContainsOnly(UnknownParamNameError.class);
  }

  // basic types

  @Test
  public void type_mismatch_for_string_param() throws Exception {
    do_test_type_mismatch_for_param_problem(STRING, BLOB);
    do_test_type_mismatch_for_param_problem(STRING, FILE);

    do_test_type_mismatch_for_param_problem(STRING, STRING_ARRAY);
    do_test_type_mismatch_for_param_problem(STRING, BLOB_ARRAY);
    do_test_type_mismatch_for_param_problem(STRING, FILE_ARRAY);
    do_test_type_mismatch_for_param_problem(STRING, NIL);
  }

  @Test
  public void type_mismatch_for_blob_param() throws Exception {
    do_test_type_mismatch_for_param_problem(BLOB, STRING);

    do_test_type_mismatch_for_param_problem(BLOB, STRING_ARRAY);
    do_test_type_mismatch_for_param_problem(BLOB, BLOB_ARRAY);
    do_test_type_mismatch_for_param_problem(BLOB, FILE_ARRAY);
    do_test_type_mismatch_for_param_problem(BLOB, NIL);
  }

  @Test
  public void type_mismatch_for_file_param() throws Exception {
    do_test_type_mismatch_for_param_problem(FILE, STRING);
    do_test_type_mismatch_for_param_problem(FILE, BLOB);

    do_test_type_mismatch_for_param_problem(FILE, STRING_ARRAY);
    do_test_type_mismatch_for_param_problem(FILE, BLOB_ARRAY);
    do_test_type_mismatch_for_param_problem(FILE, FILE_ARRAY);
    do_test_type_mismatch_for_param_problem(FILE, NIL);
  }

  // array types

  @Test
  public void type_mismatch_for_string_array_param() throws Exception {
    do_test_type_mismatch_for_param_problem(STRING_ARRAY, STRING);
    do_test_type_mismatch_for_param_problem(STRING_ARRAY, BLOB);
    do_test_type_mismatch_for_param_problem(STRING_ARRAY, FILE);

    do_test_type_mismatch_for_param_problem(STRING_ARRAY, BLOB_ARRAY);
    do_test_type_mismatch_for_param_problem(STRING_ARRAY, FILE_ARRAY);
  }

  @Test
  public void type_mismatch_for_blob_array_param() throws Exception {
    do_test_type_mismatch_for_param_problem(BLOB_ARRAY, STRING);
    do_test_type_mismatch_for_param_problem(BLOB_ARRAY, BLOB);
    do_test_type_mismatch_for_param_problem(BLOB_ARRAY, FILE);

    do_test_type_mismatch_for_param_problem(BLOB_ARRAY, STRING_ARRAY);
  }

  @Test
  public void type_mismatch_for_file_array_param() throws Exception {
    do_test_type_mismatch_for_param_problem(FILE_ARRAY, STRING);
    do_test_type_mismatch_for_param_problem(FILE_ARRAY, BLOB);
    do_test_type_mismatch_for_param_problem(FILE_ARRAY, FILE);

    do_test_type_mismatch_for_param_problem(FILE_ARRAY, STRING_ARRAY);
    do_test_type_mismatch_for_param_problem(FILE_ARRAY, BLOB_ARRAY);
  }

  private void do_test_type_mismatch_for_param_problem(Type<?> paramType, Type<?> argType)
      throws Exception {
    // given
    messages = new FakeLoggedMessages();
    Parameter p1 = optionalParameter(paramType, "name1");
    Argument a1 = arg(p1.name(), argType);

    // when
    createMapping(params(p1), list(a1));

    // then
    messages.assertContainsOnly(TypeMismatchError.class);
  }

  @Test
  public void mapping_empty_list_of_arguments() throws Exception {
    // given
    messages = new FakeLoggedMessages();
    Parameter p1 = optionalParameter(STRING, "name1");

    // when
    Map<Parameter, Argument> mapping = createMapping(params(p1), list());

    // then
    messages.assertNoProblems();
    assertThat(mapping.keySet(), empty());
  }

  // basic types

  @Test
  public void converting_single_nameless_string_argument() {
    do_test_converting_single_nameless_argument(STRING, STRING, BLOB);
    do_test_converting_single_nameless_argument(STRING, STRING, FILE);
    do_test_converting_single_nameless_argument(STRING, STRING, STRING_ARRAY);
    do_test_converting_single_nameless_argument(STRING, STRING, BLOB_ARRAY);
    do_test_converting_single_nameless_argument(STRING, STRING, FILE_ARRAY);
  }

  @Test
  public void converting_single_nameless_blob_argument() {
    do_test_converting_single_nameless_argument(BLOB, BLOB, STRING);
    do_test_converting_single_nameless_argument(BLOB, BLOB, FILE);
    do_test_converting_single_nameless_argument(BLOB, BLOB, STRING_ARRAY);
    do_test_converting_single_nameless_argument(BLOB, BLOB, BLOB_ARRAY);
    do_test_converting_single_nameless_argument(BLOB, BLOB, FILE_ARRAY);
  }

  @Test
  public void converting_single_nameless_file_argument() {
    do_test_converting_single_nameless_argument(FILE, FILE, STRING);
    do_test_converting_single_nameless_argument(FILE, FILE, STRING_ARRAY);
    do_test_converting_single_nameless_argument(FILE, FILE, BLOB_ARRAY);
    do_test_converting_single_nameless_argument(FILE, FILE, FILE_ARRAY);
  }

  @Test
  public void converting_single_nameless_file_argument_to_blob() throws Exception {
    do_test_converting_single_nameless_argument(BLOB, FILE, STRING);
    do_test_converting_single_nameless_argument(BLOB, FILE, STRING_ARRAY);
    do_test_converting_single_nameless_argument(BLOB, FILE, BLOB_ARRAY);
    do_test_converting_single_nameless_argument(BLOB, FILE, FILE_ARRAY);
  }

  // arrays

  @Test
  public void converting_single_nameless_string_array_argument() {
    do_test_converting_single_nameless_argument(STRING_ARRAY, STRING_ARRAY, STRING);
    do_test_converting_single_nameless_argument(STRING_ARRAY, STRING_ARRAY, BLOB);
    do_test_converting_single_nameless_argument(STRING_ARRAY, STRING_ARRAY, FILE);
    do_test_converting_single_nameless_argument(STRING_ARRAY, STRING_ARRAY, BLOB_ARRAY);
    do_test_converting_single_nameless_argument(STRING_ARRAY, STRING_ARRAY, FILE_ARRAY);
  }

  @Test
  public void converting_single_nameless_blob_array_argument() {
    do_test_converting_single_nameless_argument(BLOB_ARRAY, BLOB_ARRAY, STRING);
    do_test_converting_single_nameless_argument(BLOB_ARRAY, BLOB_ARRAY, BLOB);
    do_test_converting_single_nameless_argument(BLOB_ARRAY, BLOB_ARRAY, FILE);
    do_test_converting_single_nameless_argument(BLOB_ARRAY, BLOB_ARRAY, STRING_ARRAY);
    do_test_converting_single_nameless_argument(BLOB_ARRAY, BLOB_ARRAY, FILE_ARRAY);
  }

  @Test
  public void converting_single_nameless_file_array_argument() {
    do_test_converting_single_nameless_argument(FILE_ARRAY, FILE_ARRAY, STRING);
    do_test_converting_single_nameless_argument(FILE_ARRAY, FILE_ARRAY, BLOB);
    do_test_converting_single_nameless_argument(FILE_ARRAY, FILE_ARRAY, FILE);
    do_test_converting_single_nameless_argument(FILE_ARRAY, FILE_ARRAY, STRING_ARRAY);
  }

  @Test
  public void converting_single_nameless_file_array_argument_to_blob_array() throws Exception {
    do_test_converting_single_nameless_argument(BLOB_ARRAY, FILE_ARRAY, STRING);
    do_test_converting_single_nameless_argument(BLOB_ARRAY, FILE_ARRAY, BLOB);
    do_test_converting_single_nameless_argument(BLOB_ARRAY, FILE_ARRAY, FILE);
    do_test_converting_single_nameless_argument(BLOB_ARRAY, FILE_ARRAY, STRING_ARRAY);
  }

  // empty array

  @Test
  public void converting_single_nameless_nil_argument_to_string_array() {
    do_test_converting_single_nameless_argument(STRING_ARRAY, NIL, STRING);
    do_test_converting_single_nameless_argument(STRING_ARRAY, NIL, BLOB);
    do_test_converting_single_nameless_argument(STRING_ARRAY, NIL, FILE);
  }

  @Test
  public void converting_single_nameless_nil_argument_to_file_array() {
    do_test_converting_single_nameless_argument(FILE_ARRAY, NIL, STRING);
    do_test_converting_single_nameless_argument(FILE_ARRAY, NIL, BLOB);
    do_test_converting_single_nameless_argument(FILE_ARRAY, NIL, FILE);
  }

  @Test
  public void converting_single_nameless_nil_argument_to_blob_array() throws Exception {
    do_test_converting_single_nameless_argument(BLOB_ARRAY, NIL, BLOB);
    do_test_converting_single_nameless_argument(BLOB_ARRAY, NIL, FILE);
    do_test_converting_single_nameless_argument(BLOB_ARRAY, NIL, STRING);
  }

  private void do_test_converting_single_nameless_argument(Type<?> paramType, Type<?> argType,
      Type<?> otherParamsType) {
    // given
    messages = new FakeLoggedMessages();
    Parameter p1 = optionalParameter(otherParamsType, "name1");
    Parameter p2 = optionalParameter(paramType, "name2");
    Parameter p3 = optionalParameter(otherParamsType, "name3");

    Argument a1 = arg(argType);

    // when
    Map<Parameter, Argument> mapping = createMapping(params(p1, p2, p3), list(a1));

    // then
    messages.assertNoProblems();
    assertEquals(ImmutableMap.of(p2, a1), mapping);
  }

  @Test
  public void converting_single_nameless_argument_with_others_named() {
    do_test_converting_single_nameless_argument_with_others_named(STRING);
    do_test_converting_single_nameless_argument_with_others_named(BLOB);
    do_test_converting_single_nameless_argument_with_others_named(FILE);

    do_test_converting_single_nameless_argument_with_others_named(STRING_ARRAY);
    do_test_converting_single_nameless_argument_with_others_named(BLOB_ARRAY);
    do_test_converting_single_nameless_argument_with_others_named(FILE_ARRAY);
  }

  private void do_test_converting_single_nameless_argument_with_others_named(Type<?> type) {
    // given
    messages = new FakeLoggedMessages();
    Parameter p1 = optionalParameter(type, "name1");
    Parameter p2 = optionalParameter(type, "name2");
    Parameter p3 = optionalParameter(type, "name3");

    Argument a1 = arg(p1.name(), type);
    Argument a2 = arg(type);
    Argument a3 = arg(p3.name(), type);

    // when
    Map<Parameter, Argument> result = createMapping(params(p1, p2, p3), list(a1, a2, a3));

    // then
    messages.assertNoProblems();
    assertEquals(ImmutableMap.of(p1, a1, p2, a2, p3, a3), result);
  }

  @Test
  public void converting_two_nameless_arguments_with_different_types() throws Exception {
    do_test_converting_two_nameless_arguments_with_different_types(STRING, BLOB);
    do_test_converting_two_nameless_arguments_with_different_types(STRING, FILE);
    do_test_converting_two_nameless_arguments_with_different_types(STRING, STRING_ARRAY);
    do_test_converting_two_nameless_arguments_with_different_types(STRING, BLOB_ARRAY);
    do_test_converting_two_nameless_arguments_with_different_types(STRING, FILE_ARRAY);

    do_test_converting_two_nameless_arguments_with_different_types(STRING_ARRAY, STRING);
    do_test_converting_two_nameless_arguments_with_different_types(STRING_ARRAY, BLOB);
    do_test_converting_two_nameless_arguments_with_different_types(STRING_ARRAY, FILE);
    do_test_converting_two_nameless_arguments_with_different_types(STRING_ARRAY, BLOB_ARRAY);
    do_test_converting_two_nameless_arguments_with_different_types(STRING_ARRAY, FILE_ARRAY);

    do_test_converting_two_nameless_arguments_with_different_types(BLOB, STRING);
    do_test_converting_two_nameless_arguments_with_different_types(BLOB, FILE);
    do_test_converting_two_nameless_arguments_with_different_types(BLOB, STRING_ARRAY);
    do_test_converting_two_nameless_arguments_with_different_types(BLOB, BLOB_ARRAY);
    do_test_converting_two_nameless_arguments_with_different_types(BLOB, FILE_ARRAY);

    do_test_converting_two_nameless_arguments_with_different_types(BLOB_ARRAY, STRING);
    do_test_converting_two_nameless_arguments_with_different_types(BLOB_ARRAY, BLOB);
    do_test_converting_two_nameless_arguments_with_different_types(BLOB_ARRAY, FILE);
    do_test_converting_two_nameless_arguments_with_different_types(BLOB_ARRAY, STRING_ARRAY);
    do_test_converting_two_nameless_arguments_with_different_types(BLOB_ARRAY, FILE_ARRAY);

    do_test_converting_two_nameless_arguments_with_different_types(FILE, STRING);
    do_test_converting_two_nameless_arguments_with_different_types(FILE, BLOB);
    do_test_converting_two_nameless_arguments_with_different_types(FILE, STRING_ARRAY);
    do_test_converting_two_nameless_arguments_with_different_types(FILE, BLOB_ARRAY);
    do_test_converting_two_nameless_arguments_with_different_types(FILE, FILE_ARRAY);

    do_test_converting_two_nameless_arguments_with_different_types(FILE_ARRAY, STRING);
    do_test_converting_two_nameless_arguments_with_different_types(FILE_ARRAY, BLOB);
    do_test_converting_two_nameless_arguments_with_different_types(FILE_ARRAY, FILE);
    do_test_converting_two_nameless_arguments_with_different_types(FILE_ARRAY, STRING_ARRAY);
    do_test_converting_two_nameless_arguments_with_different_types(FILE_ARRAY, BLOB_ARRAY);
  }

  private void do_test_converting_two_nameless_arguments_with_different_types(Type<?> type1,
      Type<?> type2) {
    // given
    messages = new FakeLoggedMessages();
    Parameter p1 = optionalParameter(type1, "name1");
    Parameter p2 = optionalParameter(type2, "name2");

    Argument a1 = arg(type1);
    Argument a2 = arg(type2);

    // when
    Map<Parameter, Argument> result = createMapping(params(p1, p2), list(a1, a2));

    // then
    messages.assertNoProblems();
    assertEquals(ImmutableMap.of(p1, a1, p2, a2), result);
  }

  @Test
  public void converting_single_nameless_array_argument_with_other_named() throws Exception {
    do_test_doTestConvertingSingleNamelessArrayArgumentWhitOtherNamed(STRING_ARRAY);
    do_test_doTestConvertingSingleNamelessArrayArgumentWhitOtherNamed(BLOB_ARRAY);
    do_test_doTestConvertingSingleNamelessArrayArgumentWhitOtherNamed(FILE_ARRAY);
  }

  private void do_test_doTestConvertingSingleNamelessArrayArgumentWhitOtherNamed(Type<?> type) {
    // given
    messages = new FakeLoggedMessages();
    Parameter p1 = optionalParameter(type, "name1");
    Parameter p2 = optionalParameter(type, "name2");
    Parameter p3 = optionalParameter(type, "name3");

    Argument a1 = arg(p1.name(), type);
    Argument a2 = arg(NIL);
    Argument a3 = arg(p3.name(), type);

    // when
    Map<Parameter, Argument> mapping = createMapping(params(p1, p2, p3), list(a1, a2, a3));

    // then
    messages.assertNoProblems();
    assertEquals(ImmutableMap.of(p1, a1, p2, a2, p3, a3), mapping);
  }

  @Test
  public void converting_nameless_nil_arg_with_other_named_array() throws Exception {
    converting_nameless_nil_arg_with_other_named_array(STRING_ARRAY, BLOB_ARRAY);
    converting_nameless_nil_arg_with_other_named_array(STRING_ARRAY, FILE_ARRAY);

    converting_nameless_nil_arg_with_other_named_array(BLOB_ARRAY, STRING_ARRAY);
    converting_nameless_nil_arg_with_other_named_array(BLOB_ARRAY, FILE_ARRAY);

    converting_nameless_nil_arg_with_other_named_array(FILE_ARRAY, STRING_ARRAY);
    converting_nameless_nil_arg_with_other_named_array(FILE_ARRAY, BLOB_ARRAY);
  }

  private void converting_nameless_nil_arg_with_other_named_array(Type<?> arrayType,
      Type<?> otherArrayType) {
    // given
    messages = new FakeLoggedMessages();
    Parameter p1 = optionalParameter(arrayType, "name1");
    Parameter p2 = optionalParameter(otherArrayType, "name2");

    Argument a1 = arg(p1.name(), arrayType);
    Argument a2 = arg(NIL);

    // when
    Map<Parameter, Argument> result = createMapping(params(p1, p2), list(a1, a2));

    // then
    messages.assertNoProblems();
    assertEquals(ImmutableMap.of(p1, a1, p2, a2), result);
  }

  @Test
  public void ambiguous_nameless_argument() throws Exception {
    do_test_ambiguous_nameless_argument(STRING, STRING, STRING);
    do_test_ambiguous_nameless_argument(BLOB, BLOB, BLOB);
    do_test_ambiguous_nameless_argument(FILE, FILE, FILE);

    do_test_ambiguous_nameless_argument(STRING_ARRAY, STRING_ARRAY, STRING_ARRAY);
    do_test_ambiguous_nameless_argument(BLOB_ARRAY, BLOB_ARRAY, BLOB_ARRAY);
    do_test_ambiguous_nameless_argument(FILE_ARRAY, FILE_ARRAY, FILE_ARRAY);

    // conversions

    do_test_ambiguous_nameless_argument(BLOB, BLOB, FILE);
    do_test_ambiguous_nameless_argument(BLOB_ARRAY, BLOB_ARRAY, FILE_ARRAY);

    do_test_ambiguous_nameless_argument(STRING_ARRAY, STRING_ARRAY, NIL);
    do_test_ambiguous_nameless_argument(STRING_ARRAY, BLOB_ARRAY, NIL);
    do_test_ambiguous_nameless_argument(STRING_ARRAY, FILE_ARRAY, NIL);
    do_test_ambiguous_nameless_argument(BLOB_ARRAY, STRING_ARRAY, NIL);
    do_test_ambiguous_nameless_argument(BLOB_ARRAY, BLOB_ARRAY, NIL);
    do_test_ambiguous_nameless_argument(BLOB_ARRAY, FILE_ARRAY, NIL);
    do_test_ambiguous_nameless_argument(FILE_ARRAY, STRING_ARRAY, NIL);
    do_test_ambiguous_nameless_argument(FILE_ARRAY, BLOB_ARRAY, NIL);
    do_test_ambiguous_nameless_argument(FILE_ARRAY, FILE_ARRAY, NIL);
  }

  private void do_test_ambiguous_nameless_argument(Type<?> paramType, Type<?> paramType2,
      Type<?> argType) {
    // given
    messages = new FakeLoggedMessages();
    Parameter p1 = optionalParameter(paramType, "name1");
    Parameter p2 = optionalParameter(paramType2, "name2");

    Argument a1 = arg(argType);

    // when
    createMapping(params(p1, p2), list(a1));

    // then
    messages.assertContainsOnly(AmbiguousNamelessArgsError.class);
  }

  // basic types

  @Test
  public void no_param_with_proper_type_for_nameless_string_arg() throws Exception {
    do_test_no_param_with_proper_type_for_nameless_arg(STRING, BLOB);
    do_test_no_param_with_proper_type_for_nameless_arg(STRING, FILE);

    do_test_no_param_with_proper_type_for_nameless_arg(STRING, STRING_ARRAY);
    do_test_no_param_with_proper_type_for_nameless_arg(STRING, BLOB_ARRAY);
    do_test_no_param_with_proper_type_for_nameless_arg(STRING, FILE_ARRAY);
  }

  @Test
  public void no_param_with_proper_type_for_nameless_blob_arg() throws Exception {
    do_test_no_param_with_proper_type_for_nameless_arg(BLOB, STRING);
    do_test_no_param_with_proper_type_for_nameless_arg(BLOB, FILE);

    do_test_no_param_with_proper_type_for_nameless_arg(BLOB, STRING_ARRAY);
    do_test_no_param_with_proper_type_for_nameless_arg(BLOB, BLOB_ARRAY);
    do_test_no_param_with_proper_type_for_nameless_arg(BLOB, FILE_ARRAY);
  }

  @Test
  public void no_param_with_proper_type_for_nameless_file_arg() throws Exception {
    do_test_no_param_with_proper_type_for_nameless_arg(FILE, STRING);

    do_test_no_param_with_proper_type_for_nameless_arg(FILE, STRING_ARRAY);
    do_test_no_param_with_proper_type_for_nameless_arg(FILE, BLOB_ARRAY);
    do_test_no_param_with_proper_type_for_nameless_arg(FILE, FILE_ARRAY);
  }

  // array types

  @Test
  public void no_param_with_proper_type_for_nameless_string_array_arg() throws Exception {
    do_test_no_param_with_proper_type_for_nameless_arg(STRING_ARRAY, STRING);
    do_test_no_param_with_proper_type_for_nameless_arg(STRING_ARRAY, BLOB);
    do_test_no_param_with_proper_type_for_nameless_arg(STRING_ARRAY, FILE);

    do_test_no_param_with_proper_type_for_nameless_arg(STRING_ARRAY, BLOB_ARRAY);
    do_test_no_param_with_proper_type_for_nameless_arg(STRING_ARRAY, FILE_ARRAY);
  }

  @Test
  public void no_param_with_proper_type_for_nameless_blob_array_arg() throws Exception {
    do_test_no_param_with_proper_type_for_nameless_arg(BLOB_ARRAY, STRING);
    do_test_no_param_with_proper_type_for_nameless_arg(BLOB_ARRAY, BLOB);
    do_test_no_param_with_proper_type_for_nameless_arg(BLOB_ARRAY, FILE);
    do_test_no_param_with_proper_type_for_nameless_arg(BLOB_ARRAY, STRING_ARRAY);
    do_test_no_param_with_proper_type_for_nameless_arg(BLOB_ARRAY, FILE_ARRAY);
  }

  @Test
  public void no_param_with_proper_type_for_nameless_file_array_arg() throws Exception {
    do_test_no_param_with_proper_type_for_nameless_arg(FILE_ARRAY, STRING);
    do_test_no_param_with_proper_type_for_nameless_arg(FILE_ARRAY, BLOB);
    do_test_no_param_with_proper_type_for_nameless_arg(FILE_ARRAY, FILE);
    do_test_no_param_with_proper_type_for_nameless_arg(FILE_ARRAY, STRING_ARRAY);
  }

  @Test
  public void no_param_with_proper_type_for_nameless_nil_arg() throws Exception {
    do_test_no_param_with_proper_type_for_nameless_arg(NIL, STRING);
    do_test_no_param_with_proper_type_for_nameless_arg(NIL, BLOB);
    do_test_no_param_with_proper_type_for_nameless_arg(NIL, FILE);
  }

  private void do_test_no_param_with_proper_type_for_nameless_arg(Type<?> type, Type<?> otherType) {
    // given
    messages = new FakeLoggedMessages();
    Parameter p1 = optionalParameter(otherType, "name1");
    Argument a1 = arg(type);

    // when
    createMapping(params(p1), list(a1));

    // then
    messages.assertContainsOnly(AmbiguousNamelessArgsError.class);
  }

  private static Argument arg(Type<?> type) {
    return namelessArgument(1, expr(type), codeLocation(1));
  }

  private static Argument arg(String name, Type<?> type) {
    return namedArgument(1, name, expr(type), codeLocation(1));
  }

  private static Expression<?> expr(Type<?> type) {
    Expression<?> expression = mock(Expression.class);
    given(willReturn(type), expression).type();
    return expression;
  }

  private Map<Parameter, Argument> createMapping(Iterable<Parameter> params,
      List<Argument> arguments) {
    Function<?> function = function(params);
    return new Mapper(codeLocation(1), messages, function, arguments).detectMapping();
  }

  private static Function<?> function(Iterable<Parameter> params) {
    Signature<SString> signature = new Signature<>(STRING, name("name"), params);
    @SuppressWarnings("unchecked")
    Invoker<SString> invoker = mock(Invoker.class);
    return new NativeFunction<>(signature, invoker, true, Hash.integer(33));
  }

  private static ArrayList<Argument> list(Argument... arguments) {
    return newArrayList(arguments);
  }

  public static Iterable<Parameter> params(Parameter... parameters) {
    return Arrays.asList(parameters);
  }
}
