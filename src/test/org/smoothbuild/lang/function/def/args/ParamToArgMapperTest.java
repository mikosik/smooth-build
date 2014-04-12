package org.smoothbuild.lang.function.def.args;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.EMPTY_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.function.def.args.Arg.namedArg;
import static org.smoothbuild.lang.function.def.args.Arg.namelessArg;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.willReturn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.lang.function.def.args.err.AmbiguousNamelessArgsError;
import org.smoothbuild.lang.function.def.args.err.DuplicateArgNameError;
import org.smoothbuild.lang.function.def.args.err.TypeMismatchError;
import org.smoothbuild.lang.function.def.args.err.UnknownParamNameError;
import org.smoothbuild.lang.function.nativ.Invoker;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.message.FakeLoggedMessages;

import com.google.common.collect.ImmutableMap;

public class ParamToArgMapperTest {
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

    do_test_converting_named_argument(STRING_ARRAY, EMPTY_ARRAY);
    do_test_converting_named_argument(BLOB_ARRAY, EMPTY_ARRAY);
    do_test_converting_named_argument(FILE_ARRAY, EMPTY_ARRAY);
  }

  private void do_test_converting_named_argument(SType<?> paramType, SType<?> argType) {
    // given
    messages = new FakeLoggedMessages();
    Param p1 = param(paramType, "name1");
    Param p2 = param(paramType, "name2");

    Arg a1 = arg(p1.name(), argType);

    // when
    Map<Param, Arg> mapping = createMapping(params(p1, p2), list(a1));

    // then
    messages.assertNoProblems();
    assertThat(mapping).isEqualTo(ImmutableMap.of(p1, a1));
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
    do_test_duplicated_names(STRING_ARRAY, EMPTY_ARRAY);
    do_test_duplicated_names(BLOB_ARRAY, EMPTY_ARRAY);
    do_test_duplicated_names(FILE_ARRAY, EMPTY_ARRAY);
  }

  private void do_test_duplicated_names(SType<?> paramType, SType<?> argType) {
    // given
    messages = new FakeLoggedMessages();
    Param p1 = param(paramType, "name1");

    Arg a1 = arg(p1.name(), argType);
    Arg a2 = arg(p1.name(), argType);

    // when
    createMapping(params(p1), list(a1, a2));

    // then
    messages.assertContainsOnly(DuplicateArgNameError.class);
  }

  @Test
  public void unknown_param_name() {
    // given
    messages = new FakeLoggedMessages();
    Param p1 = param(STRING, "name1");
    Arg a1 = arg("otherName", STRING);

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
    do_test_type_mismatch_for_param_problem(STRING, EMPTY_ARRAY);
  }

  @Test
  public void type_mismatch_for_blob_param() throws Exception {
    do_test_type_mismatch_for_param_problem(BLOB, STRING);

    do_test_type_mismatch_for_param_problem(BLOB, STRING_ARRAY);
    do_test_type_mismatch_for_param_problem(BLOB, BLOB_ARRAY);
    do_test_type_mismatch_for_param_problem(BLOB, FILE_ARRAY);
    do_test_type_mismatch_for_param_problem(BLOB, EMPTY_ARRAY);
  }

  @Test
  public void type_mismatch_for_file_param() throws Exception {
    do_test_type_mismatch_for_param_problem(FILE, STRING);
    do_test_type_mismatch_for_param_problem(FILE, BLOB);

    do_test_type_mismatch_for_param_problem(FILE, STRING_ARRAY);
    do_test_type_mismatch_for_param_problem(FILE, BLOB_ARRAY);
    do_test_type_mismatch_for_param_problem(FILE, FILE_ARRAY);
    do_test_type_mismatch_for_param_problem(FILE, EMPTY_ARRAY);
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

  private void do_test_type_mismatch_for_param_problem(SType<?> paramType, SType<?> argType)
      throws Exception {
    // given
    messages = new FakeLoggedMessages();
    Param p1 = param(paramType, "name1");
    Arg a1 = arg(p1.name(), argType);

    // when
    createMapping(params(p1), list(a1));

    // then
    messages.assertContainsOnly(TypeMismatchError.class);
  }

  @Test
  public void mapping_empty_list_of_arguments() throws Exception {
    // given
    messages = new FakeLoggedMessages();
    Param p1 = param(STRING, "name1");

    // when
    Map<Param, Arg> mapping = createMapping(params(p1), list());

    // then
    messages.assertNoProblems();
    assertThat(mapping).isEmpty();
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
  public void converting_single_nameless_empty_array_argument_to_string_array() {
    do_test_converting_single_nameless_argument(STRING_ARRAY, EMPTY_ARRAY, STRING);
    do_test_converting_single_nameless_argument(STRING_ARRAY, EMPTY_ARRAY, BLOB);
    do_test_converting_single_nameless_argument(STRING_ARRAY, EMPTY_ARRAY, FILE);
  }

  @Test
  public void converting_single_nameless_empty_array_argument_to_file_array() {
    do_test_converting_single_nameless_argument(FILE_ARRAY, EMPTY_ARRAY, STRING);
    do_test_converting_single_nameless_argument(FILE_ARRAY, EMPTY_ARRAY, BLOB);
    do_test_converting_single_nameless_argument(FILE_ARRAY, EMPTY_ARRAY, FILE);
  }

  @Test
  public void converting_single_nameless_empty_array_argument_to_blob_array() throws Exception {
    do_test_converting_single_nameless_argument(BLOB_ARRAY, EMPTY_ARRAY, BLOB);
    do_test_converting_single_nameless_argument(BLOB_ARRAY, EMPTY_ARRAY, FILE);
    do_test_converting_single_nameless_argument(BLOB_ARRAY, EMPTY_ARRAY, STRING);
  }

  private void do_test_converting_single_nameless_argument(SType<?> paramType, SType<?> argType,
      SType<?> otherParamsType) {
    // given
    messages = new FakeLoggedMessages();
    Param p1 = param(otherParamsType, "name1");
    Param p2 = param(paramType, "name2");
    Param p3 = param(otherParamsType, "name3");

    Arg a1 = arg(argType);

    // when
    Map<Param, Arg> mapping = createMapping(params(p1, p2, p3), list(a1));

    // then
    messages.assertNoProblems();
    assertThat(mapping).isEqualTo(ImmutableMap.of(p2, a1));
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

  private void do_test_converting_single_nameless_argument_with_others_named(SType<?> type) {
    // given
    messages = new FakeLoggedMessages();
    Param p1 = param(type, "name1");
    Param p2 = param(type, "name2");
    Param p3 = param(type, "name3");

    Arg a1 = arg(p1.name(), type);
    Arg a2 = arg(type);
    Arg a3 = arg(p3.name(), type);

    // when
    Map<Param, Arg> result = createMapping(params(p1, p2, p3), list(a1, a2, a3));

    // then
    messages.assertNoProblems();
    assertThat(result).isEqualTo(ImmutableMap.of(p1, a1, p2, a2, p3, a3));
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

  private void do_test_converting_two_nameless_arguments_with_different_types(SType<?> type1,
      SType<?> type2) {
    // given
    messages = new FakeLoggedMessages();
    Param p1 = param(type1, "name1");
    Param p2 = param(type2, "name2");

    Arg a1 = arg(type1);
    Arg a2 = arg(type2);

    // when
    Map<Param, Arg> result = createMapping(params(p1, p2), list(a1, a2));

    // then
    messages.assertNoProblems();
    assertThat(result).isEqualTo(ImmutableMap.of(p1, a1, p2, a2));
  }

  @Test
  public void converting_single_nameless_array_argument_with_other_named() throws Exception {
    do_test_doTestConvertingSingleNamelessArrayArgumentWhitOtherNamed(STRING_ARRAY);
    do_test_doTestConvertingSingleNamelessArrayArgumentWhitOtherNamed(BLOB_ARRAY);
    do_test_doTestConvertingSingleNamelessArrayArgumentWhitOtherNamed(FILE_ARRAY);
  }

  private void do_test_doTestConvertingSingleNamelessArrayArgumentWhitOtherNamed(SType<?> type) {
    // given
    messages = new FakeLoggedMessages();
    Param p1 = param(type, "name1");
    Param p2 = param(type, "name2");
    Param p3 = param(type, "name3");

    Arg a1 = arg(p1.name(), type);
    Arg a2 = arg(EMPTY_ARRAY);
    Arg a3 = arg(p3.name(), type);

    // when
    Map<Param, Arg> mapping = createMapping(params(p1, p2, p3), list(a1, a2, a3));

    // then
    messages.assertNoProblems();
    assertThat(mapping).isEqualTo(ImmutableMap.of(p1, a1, p2, a2, p3, a3));
  }

  @Test
  public void converting_nameless_empty_array_arg_with_other_named_array() throws Exception {
    converting_nameless_empty_array_arg_with_other_named_array(STRING_ARRAY, BLOB_ARRAY);
    converting_nameless_empty_array_arg_with_other_named_array(STRING_ARRAY, FILE_ARRAY);

    converting_nameless_empty_array_arg_with_other_named_array(BLOB_ARRAY, STRING_ARRAY);
    converting_nameless_empty_array_arg_with_other_named_array(BLOB_ARRAY, FILE_ARRAY);

    converting_nameless_empty_array_arg_with_other_named_array(FILE_ARRAY, STRING_ARRAY);
    converting_nameless_empty_array_arg_with_other_named_array(FILE_ARRAY, BLOB_ARRAY);
  }

  private void converting_nameless_empty_array_arg_with_other_named_array(SType<?> arrayType,
      SType<?> otherArrayType) {
    // given
    messages = new FakeLoggedMessages();
    Param p1 = param(arrayType, "name1");
    Param p2 = param(otherArrayType, "name2");

    Arg a1 = arg(p1.name(), arrayType);
    Arg a2 = arg(EMPTY_ARRAY);

    // when
    Map<Param, Arg> result = createMapping(params(p1, p2), list(a1, a2));

    // then
    messages.assertNoProblems();
    assertThat(result).isEqualTo(ImmutableMap.of(p1, a1, p2, a2));
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

    do_test_ambiguous_nameless_argument(STRING_ARRAY, STRING_ARRAY, EMPTY_ARRAY);
    do_test_ambiguous_nameless_argument(STRING_ARRAY, BLOB_ARRAY, EMPTY_ARRAY);
    do_test_ambiguous_nameless_argument(STRING_ARRAY, FILE_ARRAY, EMPTY_ARRAY);
    do_test_ambiguous_nameless_argument(BLOB_ARRAY, STRING_ARRAY, EMPTY_ARRAY);
    do_test_ambiguous_nameless_argument(BLOB_ARRAY, BLOB_ARRAY, EMPTY_ARRAY);
    do_test_ambiguous_nameless_argument(BLOB_ARRAY, FILE_ARRAY, EMPTY_ARRAY);
    do_test_ambiguous_nameless_argument(FILE_ARRAY, STRING_ARRAY, EMPTY_ARRAY);
    do_test_ambiguous_nameless_argument(FILE_ARRAY, BLOB_ARRAY, EMPTY_ARRAY);
    do_test_ambiguous_nameless_argument(FILE_ARRAY, FILE_ARRAY, EMPTY_ARRAY);
  }

  private void do_test_ambiguous_nameless_argument(SType<?> paramType, SType<?> paramType2,
      SType<?> argType) {
    // given
    messages = new FakeLoggedMessages();
    Param p1 = param(paramType, "name1");
    Param p2 = param(paramType2, "name2");

    Arg a1 = arg(argType);

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
  public void no_param_with_proper_type_for_nameless_empty_array_arg() throws Exception {
    do_test_no_param_with_proper_type_for_nameless_arg(EMPTY_ARRAY, STRING);
    do_test_no_param_with_proper_type_for_nameless_arg(EMPTY_ARRAY, BLOB);
    do_test_no_param_with_proper_type_for_nameless_arg(EMPTY_ARRAY, FILE);
  }

  private void do_test_no_param_with_proper_type_for_nameless_arg(SType<?> type, SType<?> otherType) {
    // given
    messages = new FakeLoggedMessages();
    Param p1 = param(otherType, "name1");
    Arg a1 = arg(type);

    // when
    createMapping(params(p1), list(a1));

    // then
    messages.assertContainsOnly(AmbiguousNamelessArgsError.class);
  }

  private static Arg arg(SType<?> type) {
    return namelessArg(1, node(type), new FakeCodeLocation());
  }

  private static Arg arg(String name, SType<?> type) {
    return namedArg(1, name, node(type), new FakeCodeLocation());
  }

  private static Node<?> node(SType<?> type) {
    Node<?> node = mock(Node.class);
    given(willReturn(type), node).type();
    return node;
  }

  private Map<Param, Arg> createMapping(Iterable<Param> params, List<Arg> args) {
    FakeCodeLocation codeLocation = new FakeCodeLocation();
    Function<?> function = function(params);
    return new ParamToArgMapper(codeLocation, messages, function, args).detectMapping();
  }

  private static Function<?> function(Iterable<Param> params) {
    Signature<SString> signature = new Signature<>(STRING, name("name"), params);
    @SuppressWarnings("unchecked")
    Invoker<SString> invoker = mock(Invoker.class);
    return new NativeFunction<>(signature, invoker, true);
  }

  private static ArrayList<Arg> list(Arg... args) {
    return newArrayList(args);
  }

  public static Iterable<Param> params(Param... params) {
    return Arrays.asList(params);
  }
}
