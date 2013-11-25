package org.smoothbuild.lang.function.def.args;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.function.def.args.Argument.namedArg;
import static org.smoothbuild.lang.function.def.args.Argument.namelessArg;
import static org.smoothbuild.lang.type.STypes.BLOB;
import static org.smoothbuild.lang.type.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.type.STypes.EMPTY_ARRAY;
import static org.smoothbuild.lang.type.STypes.FILE;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.lang.type.STypes.STRING_ARRAY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mockito.BDDMockito;
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
import org.smoothbuild.lang.type.SType;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.listen.PhaseFailedException;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.message.FakeMessageGroup;
import org.smoothbuild.testing.task.exec.FakePluginApi;

public class ArgumentNodesCreatorTest {
  FakeMessageGroup messages;

  // converting named arguments

  @Test
  public void converting_named_argument() {
    do_test_converting_named_argument(STRING, STRING);
    do_test_converting_named_argument(BLOB, BLOB);
    do_test_converting_named_argument(FILE, FILE);

    do_test_converting_named_argument(STRING_ARRAY, STRING_ARRAY);
    do_test_converting_named_argument(BLOB_ARRAY, BLOB_ARRAY);
    do_test_converting_named_argument(FILE_ARRAY, FILE_ARRAY);

    do_test_converting_named_argument(BLOB, FILE);
    do_test_converting_named_argument(BLOB_ARRAY, FILE_ARRAY);
  }

  private void do_test_converting_named_argument(SType<?> paramType, SType<?> argType) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(paramType, "name1");
    Param p2 = param(paramType, "name2");

    Argument a1 = argument(p1.name(), node(argType));

    // when
    Map<String, Node> result = create(params(p1, p2), list(a1));

    // then
    messages.assertNoProblems();
    assertThat(result.get(p1.name())).isSameAs(a1.node());
    assertThat(result.size()).isEqualTo(1);
  }

  @Test
  public void converting_named_empty_array_argument() throws Exception {
    do_test_converting_named_empty_array_argument(STRING_ARRAY);
    do_test_converting_named_empty_array_argument(BLOB_ARRAY);
    do_test_converting_named_empty_array_argument(FILE_ARRAY);
  }

  private void do_test_converting_named_empty_array_argument(SType<?> paramType) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(paramType, "name1");
    Param p2 = param(paramType, "name2");

    Argument a1 = argument(p1.name(), node(EMPTY_ARRAY));

    // when
    Map<String, Node> result = create(params(p1, p2), list(a1));

    // then
    messages.assertNoProblems();
    Node node = result.get(p1.name());
    assertThat(result.size()).isEqualTo(1);
    assertThat(node.type()).isEqualTo(paramType);
  }

  @Test
  public void convertingNamedEmptyArrayArgument() {
    doTestConvertingNamedEmptyArrayArgument(STRING_ARRAY);
    doTestConvertingNamedEmptyArrayArgument(BLOB_ARRAY);
    doTestConvertingNamedEmptyArrayArgument(FILE_ARRAY);
  }

  private void doTestConvertingNamedEmptyArrayArgument(SType<?> type) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(type, "name1");
    Param p2 = param(type, "name2");

    Argument a1 = argument(p1.name(), emptyArrayNode());

    // when
    Map<String, Node> result = create(params(p1, p2), list(a1));

    // then
    messages.assertNoProblems();
    assertThat(result.size()).isEqualTo(1);
    assertThatNodeHasEmptyArray(result.get(p1.name()));
  }

  @Test
  public void duplicatedNames() {
    doTestDuplicatedNames(STRING);
    doTestDuplicatedNames(STRING_ARRAY);
    doTestDuplicatedNames(BLOB);
    doTestDuplicatedNames(BLOB_ARRAY);
    doTestDuplicatedNames(FILE);
    doTestDuplicatedNames(FILE_ARRAY);
  }

  private void doTestDuplicatedNames(SType<?> type) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(type, "name1");

    Argument a1 = argument(p1.name(), node(type));
    Argument a2 = argument(p1.name(), node(type));

    // when
    create(params(p1), list(a1, a2));

    // then
    messages.assertOnlyProblem(DuplicateArgNameError.class);
  }

  @Test
  public void duplicatedNamedEmptyArrayNames() {
    doTestDuplicatedNamedEmptyArrayNames(STRING_ARRAY);
    doTestDuplicatedNamedEmptyArrayNames(BLOB_ARRAY);
    doTestDuplicatedNamedEmptyArrayNames(FILE_ARRAY);
  }

  private void doTestDuplicatedNamedEmptyArrayNames(SType<?> type) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(type, "name1");

    Argument a1 = argument(p1.name(), node(EMPTY_ARRAY));
    Argument a2 = argument(p1.name(), node(EMPTY_ARRAY));

    // when
    create(params(p1), list(a1, a2));

    // then
    messages.assertOnlyProblem(DuplicateArgNameError.class);
  }

  @Test
  public void unknownParamName() {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(STRING, "name1");
    Argument a1 = argument("otherName", node(STRING));

    // when
    create(params(p1), list(a1));

    // then
    messages.assertOnlyProblem(UnknownParamNameError.class);
  }

  @Test
  public void typeMismatchForStringParam() throws Exception {
    doTestTypeMismatchForParamProblem(STRING, STRING_ARRAY);
    doTestTypeMismatchForParamProblem(STRING, BLOB);
    doTestTypeMismatchForParamProblem(STRING, BLOB_ARRAY);
    doTestTypeMismatchForParamProblem(STRING, FILE);
    doTestTypeMismatchForParamProblem(STRING, FILE_ARRAY);
    doTestTypeMismatchForParamProblem(STRING, EMPTY_ARRAY);
  }

  @Test
  public void typeMismatchForStringArrayParam() throws Exception {
    doTestTypeMismatchForParamProblem(STRING_ARRAY, STRING);
    doTestTypeMismatchForParamProblem(STRING_ARRAY, BLOB);
    doTestTypeMismatchForParamProblem(STRING_ARRAY, BLOB_ARRAY);
    doTestTypeMismatchForParamProblem(STRING_ARRAY, FILE);
    doTestTypeMismatchForParamProblem(STRING_ARRAY, FILE_ARRAY);
  }

  @Test
  public void typeMismatchForBlobParam() throws Exception {
    doTestTypeMismatchForParamProblem(BLOB, STRING);
    doTestTypeMismatchForParamProblem(BLOB, STRING_ARRAY);
    doTestTypeMismatchForParamProblem(BLOB, BLOB_ARRAY);
    doTestTypeMismatchForParamProblem(BLOB, FILE_ARRAY);
    doTestTypeMismatchForParamProblem(BLOB, EMPTY_ARRAY);
  }

  @Test
  public void typeMismatchForBlobArrayParam() throws Exception {
    doTestTypeMismatchForParamProblem(BLOB_ARRAY, STRING);
    doTestTypeMismatchForParamProblem(BLOB_ARRAY, STRING_ARRAY);
    doTestTypeMismatchForParamProblem(BLOB_ARRAY, BLOB);
    doTestTypeMismatchForParamProblem(BLOB_ARRAY, FILE);
  }

  @Test
  public void typeMismatchForFileParam() throws Exception {
    doTestTypeMismatchForParamProblem(FILE, STRING);
    doTestTypeMismatchForParamProblem(FILE, STRING_ARRAY);
    doTestTypeMismatchForParamProblem(FILE, BLOB);
    doTestTypeMismatchForParamProblem(FILE, BLOB_ARRAY);
    doTestTypeMismatchForParamProblem(FILE, FILE_ARRAY);
    doTestTypeMismatchForParamProblem(FILE, EMPTY_ARRAY);
  }

  @Test
  public void typeMismatchForFileArrayParam() throws Exception {
    doTestTypeMismatchForParamProblem(FILE_ARRAY, STRING);
    doTestTypeMismatchForParamProblem(FILE_ARRAY, STRING_ARRAY);
    doTestTypeMismatchForParamProblem(FILE_ARRAY, BLOB);
    doTestTypeMismatchForParamProblem(FILE_ARRAY, BLOB_ARRAY);
    doTestTypeMismatchForParamProblem(FILE_ARRAY, FILE);
  }

  private void doTestTypeMismatchForParamProblem(SType<?> paramType, SType<?> argType)
      throws Exception {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(paramType, "name1");
    Argument a1 = argument(p1.name(), node(argType));

    // when
    create(params(p1), list(a1));

    // then
    messages.assertOnlyProblem(TypeMismatchError.class);
  }

  @Test
  public void convertingEmptyList() throws Exception {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(STRING, "name1");

    // when
    Map<String, Node> result = create(params(p1), list());

    // then
    messages.assertNoProblems();
    assertThat(result.size()).isEqualTo(0);
  }

  @Test
  public void convertingSingleNamelessStringArgument() {
    doTestConvertingSingleNamelessArgument(STRING, STRING, STRING_ARRAY);
    doTestConvertingSingleNamelessArgument(STRING, STRING, BLOB);
    doTestConvertingSingleNamelessArgument(STRING, STRING, BLOB_ARRAY);
    doTestConvertingSingleNamelessArgument(STRING, STRING, FILE);
    doTestConvertingSingleNamelessArgument(STRING, STRING, FILE_ARRAY);
  }

  @Test
  public void convertingSingleNamelessStringArrayArgument() {
    doTestConvertingSingleNamelessArgument(STRING_ARRAY, STRING_ARRAY, STRING);
    doTestConvertingSingleNamelessArgument(STRING_ARRAY, STRING_ARRAY, BLOB);
    doTestConvertingSingleNamelessArgument(STRING_ARRAY, STRING_ARRAY, BLOB_ARRAY);
    doTestConvertingSingleNamelessArgument(STRING_ARRAY, STRING_ARRAY, FILE);
    doTestConvertingSingleNamelessArgument(STRING_ARRAY, STRING_ARRAY, FILE_ARRAY);
  }

  @Test
  public void convertingSingleNamelessFileArgument() {
    doTestConvertingSingleNamelessArgument(FILE, FILE, STRING);
    doTestConvertingSingleNamelessArgument(FILE, FILE, STRING_ARRAY);
    doTestConvertingSingleNamelessArgument(FILE, FILE, BLOB_ARRAY);
    doTestConvertingSingleNamelessArgument(FILE, FILE, FILE_ARRAY);
  }

  @Test
  public void convertingSingleNamelessFileArrayArgument() {
    doTestConvertingSingleNamelessArgument(FILE_ARRAY, FILE_ARRAY, STRING);
    doTestConvertingSingleNamelessArgument(FILE_ARRAY, FILE_ARRAY, STRING_ARRAY);
    doTestConvertingSingleNamelessArgument(FILE_ARRAY, FILE_ARRAY, BLOB);
    doTestConvertingSingleNamelessArgument(FILE_ARRAY, FILE_ARRAY, FILE);
  }

  @Test
  public void convertingSingleNamelessFileArgumentToBlob() throws Exception {
    doTestConvertingSingleNamelessArgument(BLOB, FILE, STRING);
    doTestConvertingSingleNamelessArgument(BLOB, FILE, STRING_ARRAY);
    doTestConvertingSingleNamelessArgument(BLOB, FILE, BLOB_ARRAY);
    doTestConvertingSingleNamelessArgument(BLOB, FILE, FILE_ARRAY);
  }

  @Test
  public void convertingSingleNamelessFileArrayArgumentToBlobArray() throws Exception {
    doTestConvertingSingleNamelessArgument(BLOB_ARRAY, FILE_ARRAY, BLOB);
    doTestConvertingSingleNamelessArgument(BLOB_ARRAY, FILE_ARRAY, FILE);
    doTestConvertingSingleNamelessArgument(BLOB_ARRAY, FILE_ARRAY, STRING);
    doTestConvertingSingleNamelessArgument(BLOB_ARRAY, FILE_ARRAY, STRING_ARRAY);
  }

  private void doTestConvertingSingleNamelessArgument(SType<?> paramType, SType<?> argType,
      SType<?> otherParamsType) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(otherParamsType, "name1");
    Param p2 = param(paramType, "name2");
    Param p3 = param(otherParamsType, "name3");

    Argument a1 = argument(node(argType));

    // when
    Map<String, Node> result = create(params(p1, p2, p3), list(a1));

    // then
    messages.assertNoProblems();
    assertThat(result.get(p2.name())).isSameAs(a1.node());
    assertThat(result.size()).isEqualTo(1);
  }

  @Test
  public void convertingSingleNamelessEmptyArrayArgument() throws Exception {
    doTestConvertingSingleNamelessEmptyArrayArgument(STRING_ARRAY, STRING);
    doTestConvertingSingleNamelessEmptyArrayArgument(STRING_ARRAY, FILE);
    doTestConvertingSingleNamelessEmptyArrayArgument(STRING_ARRAY, BLOB);

    doTestConvertingSingleNamelessEmptyArrayArgument(BLOB_ARRAY, STRING);
    doTestConvertingSingleNamelessEmptyArrayArgument(BLOB_ARRAY, FILE);
    doTestConvertingSingleNamelessEmptyArrayArgument(BLOB_ARRAY, BLOB);

    doTestConvertingSingleNamelessEmptyArrayArgument(FILE_ARRAY, STRING);
    doTestConvertingSingleNamelessEmptyArrayArgument(FILE_ARRAY, FILE);
    doTestConvertingSingleNamelessEmptyArrayArgument(FILE_ARRAY, BLOB);
  }

  private void doTestConvertingSingleNamelessEmptyArrayArgument(SType<?> paramType,
      SType<?> otherParamType) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(otherParamType, "name1");
    Param p2 = param(paramType, "name2");
    Param p3 = param(otherParamType, "name3");

    Argument a1 = argument(node(EMPTY_ARRAY));

    // when
    Map<String, Node> result = create(params(p1, p2, p3), list(a1));

    // then
    messages.assertNoProblems();
    assertThat(result.size()).isEqualTo(1);
    assertThatNodeHasEmptyArray(result.get(p2.name()));
  }

  @Test
  public void convertingSingleNamelessArgumentWithOtherNamed() {
    doTestConvertingSingleNamelessArgumentWhitOthersNamed(STRING);
    doTestConvertingSingleNamelessArgumentWhitOthersNamed(STRING_ARRAY);
    doTestConvertingSingleNamelessArgumentWhitOthersNamed(BLOB);
    doTestConvertingSingleNamelessArgumentWhitOthersNamed(BLOB_ARRAY);
    doTestConvertingSingleNamelessArgumentWhitOthersNamed(FILE);
    doTestConvertingSingleNamelessArgumentWhitOthersNamed(FILE_ARRAY);
  }

  private void doTestConvertingSingleNamelessArgumentWhitOthersNamed(SType<?> type) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(type, "name1");
    Param p2 = param(type, "name2");
    Param p3 = param(type, "name3");

    Argument a1 = argument(p1.name(), node(type));
    Argument a2 = argument(node(type));
    Argument a3 = argument(p3.name(), node(type));

    // when
    Map<String, Node> result = create(params(p1, p2, p3), list(a1, a2, a3));

    // then
    messages.assertNoProblems();
    assertThat(result.get(p1.name())).isSameAs(a1.node());
    assertThat(result.get(p2.name())).isSameAs(a2.node());
    assertThat(result.get(p3.name())).isSameAs(a3.node());
    assertThat(result.size()).isEqualTo(3);
  }

  @Test
  public void convertingTwoNamelessArgumentsWithDifferentType() throws Exception {
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING, STRING_ARRAY);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING, BLOB);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING, BLOB_ARRAY);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING, FILE);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING, FILE_ARRAY);

    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING_ARRAY, STRING);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING_ARRAY, BLOB);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING_ARRAY, BLOB_ARRAY);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING_ARRAY, FILE);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING_ARRAY, FILE_ARRAY);

    doTestConvertingTwoNamelessArgumentsWithDifferentType(BLOB, STRING);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(BLOB, STRING_ARRAY);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(BLOB, BLOB_ARRAY);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(BLOB, FILE);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(BLOB, FILE_ARRAY);

    doTestConvertingTwoNamelessArgumentsWithDifferentType(BLOB_ARRAY, STRING);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(BLOB_ARRAY, STRING_ARRAY);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(BLOB_ARRAY, BLOB);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(BLOB_ARRAY, FILE);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(BLOB_ARRAY, FILE_ARRAY);

    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE, STRING);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE, STRING_ARRAY);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE, BLOB);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE, BLOB_ARRAY);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE, FILE_ARRAY);

    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE_ARRAY, STRING);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE_ARRAY, STRING_ARRAY);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE_ARRAY, BLOB);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE_ARRAY, BLOB_ARRAY);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE_ARRAY, FILE);
  }

  private void doTestConvertingTwoNamelessArgumentsWithDifferentType(SType<?> type1, SType<?> type2) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(type1, "name1");
    Param p2 = param(type2, "name2");

    Argument a1 = argument(node(type1));
    Argument a2 = argument(node(type2));

    // when
    Map<String, Node> result = create(params(p1, p2), list(a1, a2));

    // then
    messages.assertNoProblems();
    assertThat(result.get(p1.name())).isSameAs(a1.node());
    assertThat(result.get(p2.name())).isSameAs(a2.node());
    assertThat(result.size()).isEqualTo(2);
  }

  @Test
  public void convertingSingleNamelessArrayArgumentWhitOtherNamed() throws Exception {
    doTestConvertingSingleNamelessArrayArgumentWhitOtherNamed(STRING_ARRAY);
    doTestConvertingSingleNamelessArrayArgumentWhitOtherNamed(BLOB_ARRAY);
    doTestConvertingSingleNamelessArrayArgumentWhitOtherNamed(FILE_ARRAY);
  }

  private void doTestConvertingSingleNamelessArrayArgumentWhitOtherNamed(SType<?> type) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(type, "name1");
    Param p2 = param(type, "name2");
    Param p3 = param(type, "name3");

    Argument a1 = argument(p1.name(), node(type));
    Argument a2 = argument(node(EMPTY_ARRAY));
    Argument a3 = argument(p3.name(), node(type));

    // when
    Map<String, Node> result = create(params(p1, p2, p3), list(a1, a2, a3));

    // then
    messages.assertNoProblems();
    assertThat(result.get(p1.name())).isSameAs(a1.node());
    assertThatNodeHasEmptyArray(result.get(p2.name()));
    assertThat(result.get(p3.name())).isSameAs(a3.node());
    assertThat(result.size()).isEqualTo(3);
  }

  @Test
  public void convertingNamelessEmptyArrayArgWithOtherNamedArray() throws Exception {
    doTestConvertingNamelessEmptyArrayArgWithOtherNamedArray(STRING_ARRAY, BLOB_ARRAY);
    doTestConvertingNamelessEmptyArrayArgWithOtherNamedArray(STRING_ARRAY, FILE_ARRAY);

    doTestConvertingNamelessEmptyArrayArgWithOtherNamedArray(BLOB_ARRAY, STRING_ARRAY);
    doTestConvertingNamelessEmptyArrayArgWithOtherNamedArray(BLOB_ARRAY, FILE_ARRAY);

    doTestConvertingNamelessEmptyArrayArgWithOtherNamedArray(FILE_ARRAY, STRING_ARRAY);
    doTestConvertingNamelessEmptyArrayArgWithOtherNamedArray(FILE_ARRAY, BLOB_ARRAY);
  }

  private void doTestConvertingNamelessEmptyArrayArgWithOtherNamedArray(SType<?> arrayType,
      SType<?> otherArrayType) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(arrayType, "name1");
    Param p2 = param(otherArrayType, "name2");

    Argument a1 = argument(p1.name(), node(arrayType));
    Argument a2 = argument(node(EMPTY_ARRAY));

    // when
    Map<String, Node> result = create(params(p1, p2), list(a1, a2));

    // then
    messages.assertNoProblems();
    assertThat(result.get(p1.name())).isSameAs(a1.node());
    assertThatNodeHasEmptyArray(result.get(p2.name()));
    assertThat(result.size()).isEqualTo(2);
  }

  @Test
  public void ambigiuousNamelessArgument() throws Exception {
    doTestAmbiguousNamelessArgument(STRING, STRING);
    doTestAmbiguousNamelessArgument(STRING_ARRAY, STRING_ARRAY);
    doTestAmbiguousNamelessArgument(BLOB, BLOB);
    doTestAmbiguousNamelessArgument(BLOB_ARRAY, BLOB_ARRAY);
    doTestAmbiguousNamelessArgument(FILE, FILE);
    doTestAmbiguousNamelessArgument(FILE_ARRAY, FILE_ARRAY);

    doTestAmbiguousNamelessArgument(BLOB_ARRAY, EMPTY_ARRAY);
    doTestAmbiguousNamelessArgument(FILE_ARRAY, EMPTY_ARRAY);
    doTestAmbiguousNamelessArgument(STRING_ARRAY, EMPTY_ARRAY);
  }

  private void doTestAmbiguousNamelessArgument(SType<?> paramType, SType<?> argType) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(paramType, "name1");
    Param p2 = param(paramType, "name2");

    Argument a1 = argument(node(argType));

    // when
    create(params(p1, p2), list(a1));

    // then
    messages.assertOnlyProblem(AmbiguousNamelessArgsError.class);
  }

  @Test
  public void ambiguousNamelessEmptyArrayArgument() {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(STRING_ARRAY, "name1");
    Param p2 = param(FILE_ARRAY, "name2");

    Argument a1 = argument(node(EMPTY_ARRAY));

    // when
    create(params(p1, p2), list(a1));

    // then
    messages.assertOnlyProblem(AmbiguousNamelessArgsError.class);
  }

  @Test
  public void noParamWithProperTypeForNamelessStringArgument() throws Exception {
    doTestNoParamWithProperTypeForNamelessArgument(STRING, STRING_ARRAY);
    doTestNoParamWithProperTypeForNamelessArgument(STRING, BLOB);
    doTestNoParamWithProperTypeForNamelessArgument(STRING, BLOB_ARRAY);
    doTestNoParamWithProperTypeForNamelessArgument(STRING, FILE);
    doTestNoParamWithProperTypeForNamelessArgument(STRING, FILE_ARRAY);
  }

  @Test
  public void noParamWithProperTypeForNamelessStringArrayArgument() throws Exception {
    doTestNoParamWithProperTypeForNamelessArgument(STRING_ARRAY, STRING);
    doTestNoParamWithProperTypeForNamelessArgument(STRING_ARRAY, BLOB);
    doTestNoParamWithProperTypeForNamelessArgument(STRING_ARRAY, BLOB_ARRAY);
    doTestNoParamWithProperTypeForNamelessArgument(STRING_ARRAY, FILE);
    doTestNoParamWithProperTypeForNamelessArgument(STRING_ARRAY, FILE_ARRAY);
  }

  @Test
  public void noParamWithProperTypeForNamelessFileArgument() throws Exception {
    doTestNoParamWithProperTypeForNamelessArgument(FILE, STRING);
    doTestNoParamWithProperTypeForNamelessArgument(FILE, STRING_ARRAY);
    doTestNoParamWithProperTypeForNamelessArgument(FILE, BLOB_ARRAY);
    doTestNoParamWithProperTypeForNamelessArgument(FILE, FILE_ARRAY);
  }

  @Test
  public void noParamWithProperTypeForNamelessFileArrayArgument() throws Exception {
    doTestNoParamWithProperTypeForNamelessArgument(FILE_ARRAY, STRING);
    doTestNoParamWithProperTypeForNamelessArgument(FILE_ARRAY, STRING_ARRAY);
    doTestNoParamWithProperTypeForNamelessArgument(FILE_ARRAY, BLOB);
    doTestNoParamWithProperTypeForNamelessArgument(FILE_ARRAY, FILE);
  }

  @Test
  public void noParamWithProperTypeForNamelessEmptyArrayArgument() throws Exception {
    doTestNoParamWithProperTypeForNamelessArgument(EMPTY_ARRAY, STRING);
    doTestNoParamWithProperTypeForNamelessArgument(EMPTY_ARRAY, BLOB);
    doTestNoParamWithProperTypeForNamelessArgument(EMPTY_ARRAY, FILE);
  }

  private void doTestNoParamWithProperTypeForNamelessArgument(SType<?> type, SType<?> otherType) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(otherType, "name1");
    Argument a1 = argument(node(type));

    // when
    create(params(p1), list(a1));

    // then
    messages.assertOnlyProblem(AmbiguousNamelessArgsError.class);
  }

  private static Argument argument(Node node) {
    return namelessArg(1, node, new FakeCodeLocation());
  }

  private static Argument argument(String name, Node node) {
    return namedArg(1, name, node, new FakeCodeLocation());
  }

  private static Node node(SType<?> type) {
    Node node = mock(Node.class);
    BDDMockito.willReturn(type).given(node).type();
    return node;
  }

  private Map<String, Node> create(Iterable<Param> params, List<Argument> args) {
    ArgumentNodesCreator creator = new ArgumentNodesCreator();
    try {
      return creator.createArgumentNodes(new FakeCodeLocation(), messages, function(params), args);
    } catch (PhaseFailedException e) {
      return null;
    }
  }

  private static Function function(Iterable<Param> params) {
    Signature signature = new Signature(STRING, name("name"), params);
    return new NativeFunction(signature, mock(Invoker.class), true);
  }

  private static ArrayList<Argument> list(Argument... args) {
    return newArrayList(args);
  }

  private static void assertThatNodeHasEmptyArray(Node abstractNode) {
    TaskGenerator taskGenerator = mock(TaskGenerator.class);
    Task task = abstractNode.generateTask(taskGenerator);
    SValue result = task.execute(new FakePluginApi());
    assertThat((Iterable<?>) result).isEmpty();
  }

  private static Node emptyArrayNode() {
    Node node = mock(Node.class);
    BDDMockito.willReturn(EMPTY_ARRAY).given(node).type();
    return node;
  }

  public static Iterable<Param> params(Param... params) {
    return Arrays.asList(params);
  }
}
