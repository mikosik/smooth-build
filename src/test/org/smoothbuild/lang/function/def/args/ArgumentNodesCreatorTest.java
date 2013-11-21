package org.smoothbuild.lang.function.def.args;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.function.base.Type.BLOB;
import static org.smoothbuild.lang.function.base.Type.BLOB_SET;
import static org.smoothbuild.lang.function.base.Type.EMPTY_SET;
import static org.smoothbuild.lang.function.base.Type.FILE;
import static org.smoothbuild.lang.function.base.Type.FILE_SET;
import static org.smoothbuild.lang.function.base.Type.STRING;
import static org.smoothbuild.lang.function.base.Type.STRING_SET;
import static org.smoothbuild.lang.function.def.args.Argument.namedArg;
import static org.smoothbuild.lang.function.def.args.Argument.namelessArg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.base.Signature;
import org.smoothbuild.lang.function.base.Type;
import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.lang.function.def.args.err.AmbiguousNamelessArgsError;
import org.smoothbuild.lang.function.def.args.err.DuplicateArgNameError;
import org.smoothbuild.lang.function.def.args.err.TypeMismatchError;
import org.smoothbuild.lang.function.def.args.err.UnknownParamNameError;
import org.smoothbuild.lang.function.nativ.Invoker;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.type.Value;
import org.smoothbuild.message.listen.PhaseFailedException;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.message.FakeMessageGroup;
import org.smoothbuild.testing.task.exec.FakeSandbox;

public class ArgumentNodesCreatorTest {
  FakeMessageGroup messages;

  // converting named arguments

  @Test
  public void converting_named_argument() {
    do_test_converting_named_argument(STRING, STRING);
    do_test_converting_named_argument(BLOB, BLOB);
    do_test_converting_named_argument(FILE, FILE);

    do_test_converting_named_argument(STRING_SET, STRING_SET);
    do_test_converting_named_argument(BLOB_SET, BLOB_SET);
    do_test_converting_named_argument(FILE_SET, FILE_SET);

    do_test_converting_named_argument(BLOB, FILE);
    do_test_converting_named_argument(BLOB_SET, FILE_SET);
  }

  private void do_test_converting_named_argument(Type paramType, Type argType) {
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
  public void converting_named_empty_set_argument() throws Exception {
    do_test_converting_named_empty_set_argument(STRING_SET);
    do_test_converting_named_empty_set_argument(BLOB_SET);
    do_test_converting_named_empty_set_argument(FILE_SET);
  }

  private void do_test_converting_named_empty_set_argument(Type paramType) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(paramType, "name1");
    Param p2 = param(paramType, "name2");

    Argument a1 = argument(p1.name(), node(EMPTY_SET));

    // when
    Map<String, Node> result = create(params(p1, p2), list(a1));

    // then
    messages.assertNoProblems();
    Node node = result.get(p1.name());
    assertThat(result.size()).isEqualTo(1);
    assertThat(node.type()).isEqualTo(paramType);
  }

  @Test
  public void convertingNamedEmptySetArgument() {
    doTestConvertingNamedEmptySetArgument(STRING_SET);
    doTestConvertingNamedEmptySetArgument(BLOB_SET);
    doTestConvertingNamedEmptySetArgument(FILE_SET);
  }

  private void doTestConvertingNamedEmptySetArgument(Type type) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(type, "name1");
    Param p2 = param(type, "name2");

    Argument a1 = argument(p1.name(), emptySetNode());

    // when
    Map<String, Node> result = create(params(p1, p2), list(a1));

    // then
    messages.assertNoProblems();
    assertThat(result.size()).isEqualTo(1);
    assertThatNodeHasEmptySet(result.get(p1.name()));
  }

  @Test
  public void duplicatedNames() {
    doTestDuplicatedNames(STRING);
    doTestDuplicatedNames(STRING_SET);
    doTestDuplicatedNames(BLOB);
    doTestDuplicatedNames(BLOB_SET);
    doTestDuplicatedNames(FILE);
    doTestDuplicatedNames(FILE_SET);
  }

  private void doTestDuplicatedNames(Type type) {
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
  public void duplicatedNamedEmptySetNames() {
    doTestDuplicatedNamedEmptySetNames(STRING_SET);
    doTestDuplicatedNamedEmptySetNames(BLOB_SET);
    doTestDuplicatedNamedEmptySetNames(FILE_SET);
  }

  private void doTestDuplicatedNamedEmptySetNames(Type type) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(type, "name1");

    Argument a1 = argument(p1.name(), node(EMPTY_SET));
    Argument a2 = argument(p1.name(), node(EMPTY_SET));

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
    doTestTypeMismatchForParamProblem(STRING, STRING_SET);
    doTestTypeMismatchForParamProblem(STRING, BLOB);
    doTestTypeMismatchForParamProblem(STRING, BLOB_SET);
    doTestTypeMismatchForParamProblem(STRING, FILE);
    doTestTypeMismatchForParamProblem(STRING, FILE_SET);
    doTestTypeMismatchForParamProblem(STRING, EMPTY_SET);
  }

  @Test
  public void typeMismatchForStringSetParam() throws Exception {
    doTestTypeMismatchForParamProblem(STRING_SET, STRING);
    doTestTypeMismatchForParamProblem(STRING_SET, BLOB);
    doTestTypeMismatchForParamProblem(STRING_SET, BLOB_SET);
    doTestTypeMismatchForParamProblem(STRING_SET, FILE);
    doTestTypeMismatchForParamProblem(STRING_SET, FILE_SET);
  }

  @Test
  public void typeMismatchForBlobParam() throws Exception {
    doTestTypeMismatchForParamProblem(BLOB, STRING);
    doTestTypeMismatchForParamProblem(BLOB, STRING_SET);
    doTestTypeMismatchForParamProblem(BLOB, BLOB_SET);
    doTestTypeMismatchForParamProblem(BLOB, FILE_SET);
    doTestTypeMismatchForParamProblem(BLOB, EMPTY_SET);
  }

  @Test
  public void typeMismatchForBlobSetParam() throws Exception {
    doTestTypeMismatchForParamProblem(BLOB_SET, STRING);
    doTestTypeMismatchForParamProblem(BLOB_SET, STRING_SET);
    doTestTypeMismatchForParamProblem(BLOB_SET, BLOB);
    doTestTypeMismatchForParamProblem(BLOB_SET, FILE);
  }

  @Test
  public void typeMismatchForFileParam() throws Exception {
    doTestTypeMismatchForParamProblem(FILE, STRING);
    doTestTypeMismatchForParamProblem(FILE, STRING_SET);
    doTestTypeMismatchForParamProblem(FILE, BLOB);
    doTestTypeMismatchForParamProblem(FILE, BLOB_SET);
    doTestTypeMismatchForParamProblem(FILE, FILE_SET);
    doTestTypeMismatchForParamProblem(FILE, EMPTY_SET);
  }

  @Test
  public void typeMismatchForFileSetParam() throws Exception {
    doTestTypeMismatchForParamProblem(FILE_SET, STRING);
    doTestTypeMismatchForParamProblem(FILE_SET, STRING_SET);
    doTestTypeMismatchForParamProblem(FILE_SET, BLOB);
    doTestTypeMismatchForParamProblem(FILE_SET, BLOB_SET);
    doTestTypeMismatchForParamProblem(FILE_SET, FILE);
  }

  private void doTestTypeMismatchForParamProblem(Type paramType, Type argType) throws Exception {
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
    doTestConvertingSingleNamelessArgument(STRING, STRING, STRING_SET);
    doTestConvertingSingleNamelessArgument(STRING, STRING, BLOB);
    doTestConvertingSingleNamelessArgument(STRING, STRING, BLOB_SET);
    doTestConvertingSingleNamelessArgument(STRING, STRING, FILE);
    doTestConvertingSingleNamelessArgument(STRING, STRING, FILE_SET);
  }

  @Test
  public void convertingSingleNamelessStringSetArgument() {
    doTestConvertingSingleNamelessArgument(STRING_SET, STRING_SET, STRING);
    doTestConvertingSingleNamelessArgument(STRING_SET, STRING_SET, BLOB);
    doTestConvertingSingleNamelessArgument(STRING_SET, STRING_SET, BLOB_SET);
    doTestConvertingSingleNamelessArgument(STRING_SET, STRING_SET, FILE);
    doTestConvertingSingleNamelessArgument(STRING_SET, STRING_SET, FILE_SET);
  }

  @Test
  public void convertingSingleNamelessFileArgument() {
    doTestConvertingSingleNamelessArgument(FILE, FILE, STRING);
    doTestConvertingSingleNamelessArgument(FILE, FILE, STRING_SET);
    doTestConvertingSingleNamelessArgument(FILE, FILE, BLOB_SET);
    doTestConvertingSingleNamelessArgument(FILE, FILE, FILE_SET);
  }

  @Test
  public void convertingSingleNamelessFileSetArgument() {
    doTestConvertingSingleNamelessArgument(FILE_SET, FILE_SET, STRING);
    doTestConvertingSingleNamelessArgument(FILE_SET, FILE_SET, STRING_SET);
    doTestConvertingSingleNamelessArgument(FILE_SET, FILE_SET, BLOB);
    doTestConvertingSingleNamelessArgument(FILE_SET, FILE_SET, FILE);
  }

  @Test
  public void convertingSingleNamelessFileArgumentToBlob() throws Exception {
    doTestConvertingSingleNamelessArgument(BLOB, FILE, STRING);
    doTestConvertingSingleNamelessArgument(BLOB, FILE, STRING_SET);
    doTestConvertingSingleNamelessArgument(BLOB, FILE, BLOB_SET);
    doTestConvertingSingleNamelessArgument(BLOB, FILE, FILE_SET);
  }

  @Test
  public void convertingSingleNamelessFileSetArgumentToBlobSet() throws Exception {
    doTestConvertingSingleNamelessArgument(BLOB_SET, FILE_SET, BLOB);
    doTestConvertingSingleNamelessArgument(BLOB_SET, FILE_SET, FILE);
    doTestConvertingSingleNamelessArgument(BLOB_SET, FILE_SET, STRING);
    doTestConvertingSingleNamelessArgument(BLOB_SET, FILE_SET, STRING_SET);
  }

  private void doTestConvertingSingleNamelessArgument(Type paramType, Type argType,
      Type otherParamsType) {
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
  public void convertingSingleNamelessEmptySetArgument() throws Exception {
    doTestConvertingSingleNamelessEmptySetArgument(STRING_SET, STRING);
    doTestConvertingSingleNamelessEmptySetArgument(STRING_SET, FILE);
    doTestConvertingSingleNamelessEmptySetArgument(STRING_SET, BLOB);

    doTestConvertingSingleNamelessEmptySetArgument(BLOB_SET, STRING);
    doTestConvertingSingleNamelessEmptySetArgument(BLOB_SET, FILE);
    doTestConvertingSingleNamelessEmptySetArgument(BLOB_SET, BLOB);

    doTestConvertingSingleNamelessEmptySetArgument(FILE_SET, STRING);
    doTestConvertingSingleNamelessEmptySetArgument(FILE_SET, FILE);
    doTestConvertingSingleNamelessEmptySetArgument(FILE_SET, BLOB);
  }

  private void doTestConvertingSingleNamelessEmptySetArgument(Type paramType, Type otherParamType) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(otherParamType, "name1");
    Param p2 = param(paramType, "name2");
    Param p3 = param(otherParamType, "name3");

    Argument a1 = argument(node(EMPTY_SET));

    // when
    Map<String, Node> result = create(params(p1, p2, p3), list(a1));

    // then
    messages.assertNoProblems();
    assertThat(result.size()).isEqualTo(1);
    assertThatNodeHasEmptySet(result.get(p2.name()));
  }

  @Test
  public void convertingSingleNamelessArgumentWithOtherNamed() {
    doTestConvertingSingleNamelessArgumentWhitOthersNamed(STRING);
    doTestConvertingSingleNamelessArgumentWhitOthersNamed(STRING_SET);
    doTestConvertingSingleNamelessArgumentWhitOthersNamed(BLOB);
    doTestConvertingSingleNamelessArgumentWhitOthersNamed(BLOB_SET);
    doTestConvertingSingleNamelessArgumentWhitOthersNamed(FILE);
    doTestConvertingSingleNamelessArgumentWhitOthersNamed(FILE_SET);
  }

  private void doTestConvertingSingleNamelessArgumentWhitOthersNamed(Type type) {
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
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING, STRING_SET);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING, BLOB);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING, BLOB_SET);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING, FILE);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING, FILE_SET);

    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING_SET, STRING);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING_SET, BLOB);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING_SET, BLOB_SET);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING_SET, FILE);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING_SET, FILE_SET);

    doTestConvertingTwoNamelessArgumentsWithDifferentType(BLOB, STRING);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(BLOB, STRING_SET);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(BLOB, BLOB_SET);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(BLOB, FILE);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(BLOB, FILE_SET);

    doTestConvertingTwoNamelessArgumentsWithDifferentType(BLOB_SET, STRING);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(BLOB_SET, STRING_SET);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(BLOB_SET, BLOB);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(BLOB_SET, FILE);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(BLOB_SET, FILE_SET);

    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE, STRING);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE, STRING_SET);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE, BLOB);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE, BLOB_SET);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE, FILE_SET);

    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE_SET, STRING);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE_SET, STRING_SET);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE_SET, BLOB);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE_SET, BLOB_SET);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE_SET, FILE);
  }

  private void doTestConvertingTwoNamelessArgumentsWithDifferentType(Type type1, Type type2) {
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
  public void convertingSingleNamelessSetArgumentWhitOtherNamed() throws Exception {
    doTestConvertingSingleNamelessSetArgumentWhitOtherNamed(STRING_SET);
    doTestConvertingSingleNamelessSetArgumentWhitOtherNamed(BLOB_SET);
    doTestConvertingSingleNamelessSetArgumentWhitOtherNamed(FILE_SET);
  }

  private void doTestConvertingSingleNamelessSetArgumentWhitOtherNamed(Type type) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(type, "name1");
    Param p2 = param(type, "name2");
    Param p3 = param(type, "name3");

    Argument a1 = argument(p1.name(), node(type));
    Argument a2 = argument(node(EMPTY_SET));
    Argument a3 = argument(p3.name(), node(type));

    // when
    Map<String, Node> result = create(params(p1, p2, p3), list(a1, a2, a3));

    // then
    messages.assertNoProblems();
    assertThat(result.get(p1.name())).isSameAs(a1.node());
    assertThatNodeHasEmptySet(result.get(p2.name()));
    assertThat(result.get(p3.name())).isSameAs(a3.node());
    assertThat(result.size()).isEqualTo(3);
  }

  @Test
  public void convertingNamelessEmptySetArgWithOtherNamedSet() throws Exception {
    doTestConvertingNamelessEmptySetArgWithOtherNamedSet(STRING_SET, BLOB_SET);
    doTestConvertingNamelessEmptySetArgWithOtherNamedSet(STRING_SET, FILE_SET);

    doTestConvertingNamelessEmptySetArgWithOtherNamedSet(BLOB_SET, STRING_SET);
    doTestConvertingNamelessEmptySetArgWithOtherNamedSet(BLOB_SET, FILE_SET);

    doTestConvertingNamelessEmptySetArgWithOtherNamedSet(FILE_SET, STRING_SET);
    doTestConvertingNamelessEmptySetArgWithOtherNamedSet(FILE_SET, BLOB_SET);
  }

  private void doTestConvertingNamelessEmptySetArgWithOtherNamedSet(Type setType, Type otherSetType) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(setType, "name1");
    Param p2 = param(otherSetType, "name2");

    Argument a1 = argument(p1.name(), node(setType));
    Argument a2 = argument(node(EMPTY_SET));

    // when
    Map<String, Node> result = create(params(p1, p2), list(a1, a2));

    // then
    messages.assertNoProblems();
    assertThat(result.get(p1.name())).isSameAs(a1.node());
    assertThatNodeHasEmptySet(result.get(p2.name()));
    assertThat(result.size()).isEqualTo(2);
  }

  @Test
  public void ambigiuousNamelessArgument() throws Exception {
    doTestAmbiguousNamelessArgument(STRING, STRING);
    doTestAmbiguousNamelessArgument(STRING_SET, STRING_SET);
    doTestAmbiguousNamelessArgument(BLOB, BLOB);
    doTestAmbiguousNamelessArgument(BLOB_SET, BLOB_SET);
    doTestAmbiguousNamelessArgument(FILE, FILE);
    doTestAmbiguousNamelessArgument(FILE_SET, FILE_SET);

    doTestAmbiguousNamelessArgument(BLOB_SET, EMPTY_SET);
    doTestAmbiguousNamelessArgument(FILE_SET, EMPTY_SET);
    doTestAmbiguousNamelessArgument(STRING_SET, EMPTY_SET);
  }

  private void doTestAmbiguousNamelessArgument(Type paramType, Type argType) {
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
  public void ambiguousNamelessEmptySetArgument() {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(STRING_SET, "name1");
    Param p2 = param(FILE_SET, "name2");

    Argument a1 = argument(node(EMPTY_SET));

    // when
    create(params(p1, p2), list(a1));

    // then
    messages.assertOnlyProblem(AmbiguousNamelessArgsError.class);
  }

  @Test
  public void noParamWithProperTypeForNamelessStringArgument() throws Exception {
    doTestNoParamWithProperTypeForNamelessArgument(STRING, STRING_SET);
    doTestNoParamWithProperTypeForNamelessArgument(STRING, BLOB);
    doTestNoParamWithProperTypeForNamelessArgument(STRING, BLOB_SET);
    doTestNoParamWithProperTypeForNamelessArgument(STRING, FILE);
    doTestNoParamWithProperTypeForNamelessArgument(STRING, FILE_SET);
  }

  @Test
  public void noParamWithProperTypeForNamelessStringSetArgument() throws Exception {
    doTestNoParamWithProperTypeForNamelessArgument(STRING_SET, STRING);
    doTestNoParamWithProperTypeForNamelessArgument(STRING_SET, BLOB);
    doTestNoParamWithProperTypeForNamelessArgument(STRING_SET, BLOB_SET);
    doTestNoParamWithProperTypeForNamelessArgument(STRING_SET, FILE);
    doTestNoParamWithProperTypeForNamelessArgument(STRING_SET, FILE_SET);
  }

  @Test
  public void noParamWithProperTypeForNamelessFileArgument() throws Exception {
    doTestNoParamWithProperTypeForNamelessArgument(FILE, STRING);
    doTestNoParamWithProperTypeForNamelessArgument(FILE, STRING_SET);
    doTestNoParamWithProperTypeForNamelessArgument(FILE, BLOB_SET);
    doTestNoParamWithProperTypeForNamelessArgument(FILE, FILE_SET);
  }

  @Test
  public void noParamWithProperTypeForNamelessFileSetArgument() throws Exception {
    doTestNoParamWithProperTypeForNamelessArgument(FILE_SET, STRING);
    doTestNoParamWithProperTypeForNamelessArgument(FILE_SET, STRING_SET);
    doTestNoParamWithProperTypeForNamelessArgument(FILE_SET, BLOB);
    doTestNoParamWithProperTypeForNamelessArgument(FILE_SET, FILE);
  }

  @Test
  public void noParamWithProperTypeForNamelessEmptySetArgument() throws Exception {
    doTestNoParamWithProperTypeForNamelessArgument(EMPTY_SET, STRING);
    doTestNoParamWithProperTypeForNamelessArgument(EMPTY_SET, BLOB);
    doTestNoParamWithProperTypeForNamelessArgument(EMPTY_SET, FILE);
  }

  private void doTestNoParamWithProperTypeForNamelessArgument(Type type, Type otherType) {
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

  private static Node node(Type type) {
    Node node = mock(Node.class);
    when(node.type()).thenReturn(type);
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

  private static void assertThatNodeHasEmptySet(Node abstractNode) {
    TaskGenerator taskGenerator = mock(TaskGenerator.class);
    Task task = abstractNode.generateTask(taskGenerator);
    Value result = task.execute(new FakeSandbox());
    assertThat((Iterable<?>) result).isEmpty();
  }

  private static Node emptySetNode() {
    Node node = mock(Node.class);
    when(node.type()).thenReturn(EMPTY_SET);
    return node;
  }

  public static Iterable<Param> params(Param... params) {
    return Arrays.asList(params);
  }
}
