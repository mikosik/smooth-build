package org.smoothbuild.function.def.args;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.function.base.Param.param;
import static org.smoothbuild.function.base.Type.EMPTY_SET;
import static org.smoothbuild.function.base.Type.FILE;
import static org.smoothbuild.function.base.Type.FILE_SET;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.function.base.Type.STRING_SET;
import static org.smoothbuild.function.base.Type.VOID;
import static org.smoothbuild.function.def.args.Argument.namedArg;
import static org.smoothbuild.function.def.args.Argument.namelessArg;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.testing.task.exec.HashedTasksTester.hashedTasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.function.def.args.err.AmbiguousNamelessArgsError;
import org.smoothbuild.function.def.args.err.DuplicateArgNameError;
import org.smoothbuild.function.def.args.err.TypeMismatchError;
import org.smoothbuild.function.def.args.err.UnknownParamNameError;
import org.smoothbuild.function.def.args.err.VoidArgError;
import org.smoothbuild.function.nativ.Invoker;
import org.smoothbuild.function.nativ.NativeFunction;
import org.smoothbuild.message.listen.PhaseFailedException;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.message.FakeMessageGroup;
import org.smoothbuild.testing.task.exec.FakeSandbox;

public class ArgumentNodesCreatorTest {

  FakeMessageGroup messages;

  @Test
  public void convertingNamedArgument() {
    doTestConvertingNamedArgument(STRING);
    doTestConvertingNamedArgument(STRING_SET);
    doTestConvertingNamedArgument(FILE);
    doTestConvertingNamedArgument(FILE_SET);
  }

  private void doTestConvertingNamedArgument(Type type) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(type, "name1");
    Param p2 = param(type, "name2");

    Argument a1 = argument(p1.name(), node(type));

    // when
    Map<String, DefinitionNode> result = create(params(p1, p2), list(a1));

    // then
    messages.assertNoProblems();
    assertThat(result.get(p1.name())).isSameAs(a1.definitionNode());
    assertThat(result.size()).isEqualTo(1);
  }

  @Test
  public void convertingNamedEmptySetArgument() {
    doTestConvertingNamedEmptySetArgument(STRING_SET);
    doTestConvertingNamedEmptySetArgument(FILE_SET);
  }

  private void doTestConvertingNamedEmptySetArgument(Type type) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(type, "name1");
    Param p2 = param(type, "name2");

    Argument a1 = argument(p1.name(), emptySetNode());

    // when
    Map<String, DefinitionNode> result = create(params(p1, p2), list(a1));

    // then
    messages.assertNoProblems();
    assertThat(result.size()).isEqualTo(1);
    assertThatNodeHasEmptySet(result.get(p1.name()));
  }

  @Test
  public void duplicatedNames() {
    doTestDuplicatedNames(STRING);
    doTestDuplicatedNames(STRING_SET);
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
    doTestTypeMismatchForParamProblem(STRING, FILE);
    doTestTypeMismatchForParamProblem(STRING, FILE_SET);
    doTestTypeMismatchForParamProblem(STRING, EMPTY_SET);
  }

  @Test
  public void typeMismatchForStringSetParam() throws Exception {
    doTestTypeMismatchForParamProblem(STRING_SET, STRING);
    doTestTypeMismatchForParamProblem(STRING_SET, FILE);
    doTestTypeMismatchForParamProblem(STRING_SET, FILE_SET);
  }

  @Test
  public void typeMismatchForFileParam() throws Exception {
    doTestTypeMismatchForParamProblem(FILE, STRING);
    doTestTypeMismatchForParamProblem(FILE, STRING_SET);
    doTestTypeMismatchForParamProblem(FILE, FILE_SET);
    doTestTypeMismatchForParamProblem(FILE, EMPTY_SET);
  }

  @Test
  public void typeMismatchForFileSetParam() throws Exception {
    doTestTypeMismatchForParamProblem(FILE_SET, STRING);
    doTestTypeMismatchForParamProblem(FILE_SET, STRING_SET);
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
  public void voidTypeOfNamedArgument() throws Exception {
    doTestVoidTypeOfNamedArgument(STRING);
    doTestVoidTypeOfNamedArgument(STRING_SET);
    doTestVoidTypeOfNamedArgument(FILE);
    doTestVoidTypeOfNamedArgument(FILE_SET);
  }

  private void doTestVoidTypeOfNamedArgument(Type paramType) throws Exception {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(paramType, "name1");
    Argument a1 = argument(p1.name(), node(VOID));

    // when
    create(params(p1), list(a1));

    // then
    messages.assertOnlyProblem(VoidArgError.class);
  }

  @Test
  public void voidTypeOfNamelessArgument() throws Exception {
    doTestVoidTypeOfNamelessArgument(STRING);
    doTestVoidTypeOfNamelessArgument(STRING_SET);
    doTestVoidTypeOfNamelessArgument(FILE);
    doTestVoidTypeOfNamelessArgument(FILE_SET);
  }

  private void doTestVoidTypeOfNamelessArgument(Type paramType) throws Exception {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(paramType, "name1");
    Argument a1 = argument(node(VOID));

    // when
    create(params(p1), list(a1));

    // then
    messages.assertOnlyProblem(VoidArgError.class);
  }

  @Test
  public void convertingEmptyList() throws Exception {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(STRING, "name1");

    // when
    Map<String, DefinitionNode> result = create(params(p1), list());

    // then
    messages.assertNoProblems();
    assertThat(result.size()).isEqualTo(0);
  }

  @Test
  public void convertingSingleNamelessStringArgument() {
    doTestConvertingSingleNamelessArgument(STRING, STRING_SET);
    doTestConvertingSingleNamelessArgument(STRING, FILE);
    doTestConvertingSingleNamelessArgument(STRING, FILE_SET);
  }

  @Test
  public void convertingSingleNamelessStringSetArgument() {
    doTestConvertingSingleNamelessArgument(STRING_SET, STRING);
    doTestConvertingSingleNamelessArgument(STRING_SET, FILE);
    doTestConvertingSingleNamelessArgument(STRING_SET, FILE_SET);
  }

  @Test
  public void convertingSingleNamelessFileArgument() {
    doTestConvertingSingleNamelessArgument(FILE, STRING);
    doTestConvertingSingleNamelessArgument(FILE, STRING_SET);
    doTestConvertingSingleNamelessArgument(FILE, FILE_SET);
  }

  @Test
  public void convertingSingleNamelessFileSetArgument() {
    doTestConvertingSingleNamelessArgument(FILE_SET, STRING);
    doTestConvertingSingleNamelessArgument(FILE_SET, STRING_SET);
    doTestConvertingSingleNamelessArgument(FILE_SET, FILE);
  }

  private void doTestConvertingSingleNamelessArgument(Type type, Type otherType) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(otherType, "name1");
    Param p2 = param(type, "name2");
    Param p3 = param(otherType, "name3");

    Argument a1 = argument(node(type));

    // when
    Map<String, DefinitionNode> result = create(params(p1, p2, p3), list(a1));

    // then
    messages.assertNoProblems();
    assertThat(result.get(p2.name())).isSameAs(a1.definitionNode());
    assertThat(result.size()).isEqualTo(1);
  }

  @Test
  public void convertingSingleNamelessEmptySetArgument() throws Exception {
    doTestConvertingSingleNamelessEmptySetArgument(STRING_SET, STRING);
    doTestConvertingSingleNamelessEmptySetArgument(STRING_SET, FILE);

    doTestConvertingSingleNamelessEmptySetArgument(FILE_SET, STRING);
    doTestConvertingSingleNamelessEmptySetArgument(FILE_SET, FILE);
  }

  private void doTestConvertingSingleNamelessEmptySetArgument(Type type, Type otherType) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(otherType, "name1");
    Param p2 = param(type, "name2");
    Param p3 = param(otherType, "name3");

    Argument a1 = argument(node(EMPTY_SET));

    // when
    Map<String, DefinitionNode> result = create(params(p1, p2, p3), list(a1));

    // then
    messages.assertNoProblems();
    assertThat(result.size()).isEqualTo(1);
    assertThatNodeHasEmptySet(result.get(p2.name()));
  }

  @Test
  public void convertingSingleNamelessArgumentWithOtherNamed() {
    doTestConvertingSingleNamelessArgumentWhitOthersNamed(STRING);
    doTestConvertingSingleNamelessArgumentWhitOthersNamed(STRING_SET);
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
    Map<String, DefinitionNode> result = create(params(p1, p2, p3), list(a1, a2, a3));

    // then
    messages.assertNoProblems();
    assertThat(result.get(p1.name())).isSameAs(a1.definitionNode());
    assertThat(result.get(p2.name())).isSameAs(a2.definitionNode());
    assertThat(result.get(p3.name())).isSameAs(a3.definitionNode());
    assertThat(result.size()).isEqualTo(3);
  }

  @Test
  public void convertingTwoNamelessArgumentsWithDifferentType() throws Exception {
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING, STRING_SET);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING, FILE);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING, FILE_SET);

    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING_SET, STRING);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING_SET, FILE);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(STRING_SET, FILE_SET);

    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE, STRING);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE, STRING_SET);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE, FILE_SET);

    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE_SET, STRING);
    doTestConvertingTwoNamelessArgumentsWithDifferentType(FILE_SET, STRING_SET);
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
    Map<String, DefinitionNode> result = create(params(p1, p2), list(a1, a2));

    // then
    messages.assertNoProblems();
    assertThat(result.get(p1.name())).isSameAs(a1.definitionNode());
    assertThat(result.get(p2.name())).isSameAs(a2.definitionNode());
    assertThat(result.size()).isEqualTo(2);
  }

  @Test
  public void convertingSingleNamelessSetArgumentWhitOtherNamed() throws Exception {
    doTestConvertingSingleNamelessSetArgumentWhitOtherNamed(STRING_SET);
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
    Map<String, DefinitionNode> result = create(params(p1, p2, p3), list(a1, a2, a3));

    // then
    messages.assertNoProblems();
    assertThat(result.get(p1.name())).isSameAs(a1.definitionNode());
    assertThatNodeHasEmptySet(result.get(p2.name()));
    assertThat(result.get(p3.name())).isSameAs(a3.definitionNode());
    assertThat(result.size()).isEqualTo(3);
  }

  @Test
  public void convertingNamelessEmptySetArgWithOtherNamedSet() throws Exception {
    doTestConvertingNamelessEmptySetArgWithOtherNamedSet(STRING_SET, FILE_SET);
    doTestConvertingNamelessEmptySetArgWithOtherNamedSet(FILE_SET, STRING_SET);
  }

  private void doTestConvertingNamelessEmptySetArgWithOtherNamedSet(Type setType, Type otherSetType) {
    // given
    messages = new FakeMessageGroup();
    Param p1 = param(setType, "name1");
    Param p2 = param(otherSetType, "name2");

    Argument a1 = argument(p1.name(), node(setType));
    Argument a2 = argument(node(EMPTY_SET));

    // when
    Map<String, DefinitionNode> result = create(params(p1, p2), list(a1, a2));

    // then
    messages.assertNoProblems();
    assertThat(result.get(p1.name())).isSameAs(a1.definitionNode());
    assertThatNodeHasEmptySet(result.get(p2.name()));
    assertThat(result.size()).isEqualTo(2);
  }

  @Test
  public void ambigiuousNamelessArgument() throws Exception {
    doTestAmbiguousNamelessArgument(STRING, STRING);
    doTestAmbiguousNamelessArgument(STRING_SET, STRING_SET);
    doTestAmbiguousNamelessArgument(FILE, FILE);
    doTestAmbiguousNamelessArgument(FILE_SET, FILE_SET);

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
    doTestNoParamWithProperTypeForNamelessArgument(STRING, FILE);
    doTestNoParamWithProperTypeForNamelessArgument(STRING, FILE_SET);
  }

  @Test
  public void noParamWithProperTypeForNamelessStringSetArgument() throws Exception {
    doTestNoParamWithProperTypeForNamelessArgument(STRING_SET, STRING);
    doTestNoParamWithProperTypeForNamelessArgument(STRING_SET, FILE);
    doTestNoParamWithProperTypeForNamelessArgument(STRING_SET, FILE_SET);
  }

  @Test
  public void noParamWithProperTypeForNamelessFileArgument() throws Exception {
    doTestNoParamWithProperTypeForNamelessArgument(FILE, STRING);
    doTestNoParamWithProperTypeForNamelessArgument(FILE, STRING_SET);
    doTestNoParamWithProperTypeForNamelessArgument(FILE, FILE_SET);
  }

  @Test
  public void noParamWithProperTypeForNamelessFileSetArgument() throws Exception {
    doTestNoParamWithProperTypeForNamelessArgument(FILE_SET, STRING);
    doTestNoParamWithProperTypeForNamelessArgument(FILE_SET, STRING_SET);
    doTestNoParamWithProperTypeForNamelessArgument(FILE_SET, FILE);
  }

  @Test
  public void noParamWithProperTypeForNamelessEmptySetArgument() throws Exception {
    doTestNoParamWithProperTypeForNamelessArgument(EMPTY_SET, STRING);
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

  private static Argument argument(DefinitionNode node) {
    return namelessArg(1, node, codeLocation(1, 2, 3));
  }

  private static Argument argument(String name, DefinitionNode node) {
    return namedArg(1, name, node, codeLocation(1, 2, 3));
  }

  private static DefinitionNode node(Type type) {
    DefinitionNode node = mock(DefinitionNode.class);
    when(node.type()).thenReturn(type);
    return node;
  }

  private Map<String, DefinitionNode> create(Iterable<Param> params, List<Argument> args) {
    ArgumentNodesCreator creator = new ArgumentNodesCreator();
    try {
      return creator.createArgumentNodes(codeLocation(1, 2, 3), messages, function(params), args);
    } catch (PhaseFailedException e) {
      return null;
    }
  }

  private static Function function(Iterable<Param> params) {
    Signature signature = new Signature(STRING, simpleName("name"), params);
    return new NativeFunction(signature, mock(Invoker.class));
  }

  private static ArrayList<Argument> list(Argument... args) {
    return newArrayList(args);
  }

  private static void assertThatNodeHasEmptySet(DefinitionNode node) {
    TaskGenerator taskGenerator = mock(TaskGenerator.class);
    Task task = node.generateTask(taskGenerator);
    task.execute(new FakeSandbox(), hashedTasks());
    assertThat((Iterable<?>) task.result()).isEmpty();
  }

  private static DefinitionNode emptySetNode() {
    DefinitionNode node = mock(DefinitionNode.class);
    when(node.type()).thenReturn(EMPTY_SET);
    return node;
  }

  public static Iterable<Param> params(Param... params) {
    return Arrays.asList(params);
  }
}
