package org.smoothbuild.parse.def;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.function.base.Param.param;
import static org.smoothbuild.function.base.Param.params;
import static org.smoothbuild.function.base.Type.EMPTY_SET;
import static org.smoothbuild.function.base.Type.FILE;
import static org.smoothbuild.function.base.Type.FILE_SET;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.function.base.Type.STRING_SET;
import static org.smoothbuild.function.base.Type.VOID;
import static org.smoothbuild.function.def.EmptySetNode.emptySetNode;
import static org.smoothbuild.parse.def.Argument.explicitArg;
import static org.smoothbuild.parse.def.Argument.implicitArg;
import static org.smoothbuild.problem.CodeLocation.codeLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.function.plugin.PluginFunction;
import org.smoothbuild.function.plugin.PluginInvoker;
import org.smoothbuild.parse.def.err.AmbiguousImplicitArgsError;
import org.smoothbuild.parse.def.err.DuplicateArgNameError;
import org.smoothbuild.parse.def.err.TypeMismatchError;
import org.smoothbuild.parse.def.err.UnknownParamNameError;
import org.smoothbuild.parse.def.err.VoidArgError;
import org.smoothbuild.task.Task;
import org.smoothbuild.testing.plugin.internal.TestSandbox;
import org.smoothbuild.testing.problem.TestProblemsListener;

import com.google.common.collect.ImmutableMap;

public class ArgumentNodesCreatorTest {

  TestProblemsListener problemsListener;

  @Test
  public void convertingExplicitArgument() {
    doTestConvertingExplicitArgument(STRING);
    doTestConvertingExplicitArgument(STRING_SET);
    doTestConvertingExplicitArgument(FILE);
    doTestConvertingExplicitArgument(FILE_SET);
  }

  private void doTestConvertingExplicitArgument(Type type) {
    // given
    problemsListener = new TestProblemsListener();
    Param p1 = param(type, "name1");
    Param p2 = param(type, "name2");

    Argument a1 = argument(p1.name(), node(type));

    // when
    Map<String, DefinitionNode> result = create(params(p1, p2), list(a1));

    // then
    problemsListener.assertNoProblems();
    assertThat(result.get(p1.name())).isSameAs(a1.definitionNode());
    assertThat(result.size()).isEqualTo(1);
  }

  @Test
  public void convertingExplicitEmptySetArgument() {
    doTestConvertingExplicitEmptySetArgument(STRING_SET);
    doTestConvertingExplicitEmptySetArgument(FILE_SET);
  }

  private void doTestConvertingExplicitEmptySetArgument(Type type) {
    // given
    problemsListener = new TestProblemsListener();
    Param p1 = param(type, "name1");
    Param p2 = param(type, "name2");

    Argument a1 = argument(p1.name(), emptySetNode());

    // when
    Map<String, DefinitionNode> result = create(params(p1, p2), list(a1));

    // then
    problemsListener.assertNoProblems();
    assertThat(result.size()).isEqualTo(1);
    assertThatNodeHasEmptySet(result.get(p1.name()));
  }

  @Test
  public void duplicatedExplicitNames() {
    doTestDuplicatedExplicitNames(STRING);
    doTestDuplicatedExplicitNames(STRING_SET);
    doTestDuplicatedExplicitNames(FILE);
    doTestDuplicatedExplicitNames(FILE_SET);
  }

  private void doTestDuplicatedExplicitNames(Type type) {
    // given
    problemsListener = new TestProblemsListener();
    Param p1 = param(type, "name1");

    Argument a1 = argument(p1.name(), node(type));
    Argument a2 = argument(p1.name(), node(type));

    // when
    create(params(p1), list(a1, a2));

    // then
    problemsListener.assertOnlyProblem(DuplicateArgNameError.class);
  }

  @Test
  public void duplicatedExplicitEmptySetNames() {
    doTestDuplicatedExplicitEmptySetNames(STRING_SET);
    doTestDuplicatedExplicitEmptySetNames(FILE_SET);
  }

  private void doTestDuplicatedExplicitEmptySetNames(Type type) {
    // given
    problemsListener = new TestProblemsListener();
    Param p1 = param(type, "name1");

    Argument a1 = argument(p1.name(), node(EMPTY_SET));
    Argument a2 = argument(p1.name(), node(EMPTY_SET));

    // when
    create(params(p1), list(a1, a2));

    // then
    problemsListener.assertOnlyProblem(DuplicateArgNameError.class);
  }

  @Test
  public void unknownParamName() {
    // given
    problemsListener = new TestProblemsListener();
    Param p1 = param(STRING, "name1");
    Argument a1 = argument("otherName", node(STRING));

    // when
    create(params(p1), list(a1));

    // then
    problemsListener.assertOnlyProblem(UnknownParamNameError.class);
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
    problemsListener = new TestProblemsListener();
    Param p1 = param(paramType, "name1");
    Argument a1 = argument(p1.name(), node(argType));

    // when
    create(params(p1), list(a1));

    // then
    problemsListener.assertOnlyProblem(TypeMismatchError.class);
  }

  @Test
  public void voidTypeOfExplicitArgument() throws Exception {
    doTestVoidTypeOfExplicitArgument(STRING);
    doTestVoidTypeOfExplicitArgument(STRING_SET);
    doTestVoidTypeOfExplicitArgument(FILE);
    doTestVoidTypeOfExplicitArgument(FILE_SET);
  }

  private void doTestVoidTypeOfExplicitArgument(Type paramType) throws Exception {
    // given
    problemsListener = new TestProblemsListener();
    Param p1 = param(paramType, "name1");
    Argument a1 = argument(p1.name(), node(VOID));

    // when
    create(params(p1), list(a1));

    // then
    problemsListener.assertOnlyProblem(VoidArgError.class);
  }

  @Test
  public void voidTypeOfImplicitArgument() throws Exception {
    doTestVoidTypeOfImplicitArgument(STRING);
    doTestVoidTypeOfImplicitArgument(STRING_SET);
    doTestVoidTypeOfImplicitArgument(FILE);
    doTestVoidTypeOfImplicitArgument(FILE_SET);
  }

  private void doTestVoidTypeOfImplicitArgument(Type paramType) throws Exception {
    // given
    problemsListener = new TestProblemsListener();
    Param p1 = param(paramType, "name1");
    Argument a1 = argument(node(VOID));

    // when
    create(params(p1), list(a1));

    // then
    problemsListener.assertOnlyProblem(VoidArgError.class);
  }

  @Test
  public void convertingEmptyList() throws Exception {
    // given
    problemsListener = new TestProblemsListener();
    Param p1 = param(STRING, "name1");

    // when
    Map<String, DefinitionNode> result = create(params(p1), list());

    // then
    problemsListener.assertNoProblems();
    assertThat(result.size()).isEqualTo(0);
  }

  @Test
  public void convertingSingleImplicitStringArgument() {
    doTestConvertingSingleImplicitArgument(STRING, STRING_SET);
    doTestConvertingSingleImplicitArgument(STRING, FILE);
    doTestConvertingSingleImplicitArgument(STRING, FILE_SET);
  }

  @Test
  public void convertingSingleImplicitStringSetArgument() {
    doTestConvertingSingleImplicitArgument(STRING_SET, STRING);
    doTestConvertingSingleImplicitArgument(STRING_SET, FILE);
    doTestConvertingSingleImplicitArgument(STRING_SET, FILE_SET);
  }

  @Test
  public void convertingSingleImplicitFileArgument() {
    doTestConvertingSingleImplicitArgument(FILE, STRING);
    doTestConvertingSingleImplicitArgument(FILE, STRING_SET);
    doTestConvertingSingleImplicitArgument(FILE, FILE_SET);
  }

  @Test
  public void convertingSingleImplicitFileSetArgument() {
    doTestConvertingSingleImplicitArgument(FILE_SET, STRING);
    doTestConvertingSingleImplicitArgument(FILE_SET, STRING_SET);
    doTestConvertingSingleImplicitArgument(FILE_SET, FILE);
  }

  private void doTestConvertingSingleImplicitArgument(Type type, Type otherType) {
    // given
    problemsListener = new TestProblemsListener();
    Param p1 = param(otherType, "name1");
    Param p2 = param(type, "name2");
    Param p3 = param(otherType, "name3");

    Argument a1 = argument(node(type));

    // when
    Map<String, DefinitionNode> result = create(params(p1, p2, p3), list(a1));

    // then
    problemsListener.assertNoProblems();
    assertThat(result.get(p2.name())).isSameAs(a1.definitionNode());
    assertThat(result.size()).isEqualTo(1);
  }

  @Test
  public void convertingSingleImplicitEmptySetArgument() throws Exception {
    doTestConvertingSingleImplicitEmptySetArgument(STRING_SET, STRING);
    doTestConvertingSingleImplicitEmptySetArgument(STRING_SET, FILE);

    doTestConvertingSingleImplicitEmptySetArgument(FILE_SET, STRING);
    doTestConvertingSingleImplicitEmptySetArgument(FILE_SET, FILE);
  }

  private void doTestConvertingSingleImplicitEmptySetArgument(Type type, Type otherType) {
    // given
    problemsListener = new TestProblemsListener();
    Param p1 = param(otherType, "name1");
    Param p2 = param(type, "name2");
    Param p3 = param(otherType, "name3");

    Argument a1 = argument(node(EMPTY_SET));

    // when
    Map<String, DefinitionNode> result = create(params(p1, p2, p3), list(a1));

    // then
    problemsListener.assertNoProblems();
    assertThat(result.size()).isEqualTo(1);
    assertThatNodeHasEmptySet(result.get(p2.name()));
  }

  @Test
  public void convertingSingleImplicitArgumentWithOtherExplicit() {
    doTestConvertingSingleImplicitArgumentWhitOthersExplicit(STRING);
    doTestConvertingSingleImplicitArgumentWhitOthersExplicit(STRING_SET);
    doTestConvertingSingleImplicitArgumentWhitOthersExplicit(FILE);
    doTestConvertingSingleImplicitArgumentWhitOthersExplicit(FILE_SET);
  }

  private void doTestConvertingSingleImplicitArgumentWhitOthersExplicit(Type type) {
    // given
    problemsListener = new TestProblemsListener();
    Param p1 = param(type, "name1");
    Param p2 = param(type, "name2");
    Param p3 = param(type, "name3");

    Argument a1 = argument(p1.name(), node(type));
    Argument a2 = argument(node(type));
    Argument a3 = argument(p3.name(), node(type));

    // when
    Map<String, DefinitionNode> result = create(params(p1, p2, p3), list(a1, a2, a3));

    // then
    problemsListener.assertNoProblems();
    assertThat(result.get(p1.name())).isSameAs(a1.definitionNode());
    assertThat(result.get(p2.name())).isSameAs(a2.definitionNode());
    assertThat(result.get(p3.name())).isSameAs(a3.definitionNode());
    assertThat(result.size()).isEqualTo(3);
  }

  @Test
  public void convertingTwoImplicitArgumentsWithDifferentType() throws Exception {
    doTestConvertingTwoImplicitArgumentsWithDifferentType(STRING, STRING_SET);
    doTestConvertingTwoImplicitArgumentsWithDifferentType(STRING, FILE);
    doTestConvertingTwoImplicitArgumentsWithDifferentType(STRING, FILE_SET);

    doTestConvertingTwoImplicitArgumentsWithDifferentType(STRING_SET, STRING);
    doTestConvertingTwoImplicitArgumentsWithDifferentType(STRING_SET, FILE);
    doTestConvertingTwoImplicitArgumentsWithDifferentType(STRING_SET, FILE_SET);

    doTestConvertingTwoImplicitArgumentsWithDifferentType(FILE, STRING);
    doTestConvertingTwoImplicitArgumentsWithDifferentType(FILE, STRING_SET);
    doTestConvertingTwoImplicitArgumentsWithDifferentType(FILE, FILE_SET);

    doTestConvertingTwoImplicitArgumentsWithDifferentType(FILE_SET, STRING);
    doTestConvertingTwoImplicitArgumentsWithDifferentType(FILE_SET, STRING_SET);
    doTestConvertingTwoImplicitArgumentsWithDifferentType(FILE_SET, FILE);
  }

  private void doTestConvertingTwoImplicitArgumentsWithDifferentType(Type type1, Type type2) {
    // given
    problemsListener = new TestProblemsListener();
    Param p1 = param(type1, "name1");
    Param p2 = param(type2, "name2");

    Argument a1 = argument(node(type1));
    Argument a2 = argument(node(type2));

    // when
    Map<String, DefinitionNode> result = create(params(p1, p2), list(a1, a2));

    // then
    problemsListener.assertNoProblems();
    assertThat(result.get(p1.name())).isSameAs(a1.definitionNode());
    assertThat(result.get(p2.name())).isSameAs(a2.definitionNode());
    assertThat(result.size()).isEqualTo(2);
  }

  @Test
  public void convertingSingleImplicitSetArgumentWhitOtherExplicit() throws Exception {
    doTestConvertingSingleImplicitSetArgumentWhitOtherExplicit(STRING_SET);
    doTestConvertingSingleImplicitSetArgumentWhitOtherExplicit(FILE_SET);
  }

  private void doTestConvertingSingleImplicitSetArgumentWhitOtherExplicit(Type type) {
    // given
    problemsListener = new TestProblemsListener();
    Param p1 = param(type, "name1");
    Param p2 = param(type, "name2");
    Param p3 = param(type, "name3");

    Argument a1 = argument(p1.name(), node(type));
    Argument a2 = argument(node(EMPTY_SET));
    Argument a3 = argument(p3.name(), node(type));

    // when
    Map<String, DefinitionNode> result = create(params(p1, p2, p3), list(a1, a2, a3));

    // then
    problemsListener.assertNoProblems();
    assertThat(result.get(p1.name())).isSameAs(a1.definitionNode());
    assertThatNodeHasEmptySet(result.get(p2.name()));
    assertThat(result.get(p3.name())).isSameAs(a3.definitionNode());
    assertThat(result.size()).isEqualTo(3);
  }

  @Test
  public void convertingImplicitEmptySetArgWithOtherExplicitSet() throws Exception {
    doTestConvertingImplicitEmptySetArgWithOtherExplicitSet(STRING_SET, FILE_SET);
    doTestConvertingImplicitEmptySetArgWithOtherExplicitSet(FILE_SET, STRING_SET);
  }

  private void doTestConvertingImplicitEmptySetArgWithOtherExplicitSet(Type setType,
      Type otherSetType) {
    // given
    problemsListener = new TestProblemsListener();
    Param p1 = param(setType, "name1");
    Param p2 = param(otherSetType, "name2");

    Argument a1 = argument(p1.name(), node(setType));
    Argument a2 = argument(node(EMPTY_SET));

    // when
    Map<String, DefinitionNode> result = create(params(p1, p2), list(a1, a2));

    // then
    problemsListener.assertNoProblems();
    assertThat(result.get(p1.name())).isSameAs(a1.definitionNode());
    assertThatNodeHasEmptySet(result.get(p2.name()));
    assertThat(result.size()).isEqualTo(2);
  }

  @Test
  public void ambigiuousImplicitArgument() throws Exception {
    doTestAmbiguousImplicitArgument(STRING, STRING);
    doTestAmbiguousImplicitArgument(STRING_SET, STRING_SET);
    doTestAmbiguousImplicitArgument(FILE, FILE);
    doTestAmbiguousImplicitArgument(FILE_SET, FILE_SET);

    doTestAmbiguousImplicitArgument(FILE_SET, EMPTY_SET);
    doTestAmbiguousImplicitArgument(STRING_SET, EMPTY_SET);
  }

  private void doTestAmbiguousImplicitArgument(Type paramType, Type argType) {
    // given
    problemsListener = new TestProblemsListener();
    Param p1 = param(paramType, "name1");
    Param p2 = param(paramType, "name2");

    Argument a1 = argument(node(argType));

    // when
    create(params(p1, p2), list(a1));

    // then
    problemsListener.assertOnlyProblem(AmbiguousImplicitArgsError.class);
  }

  @Test
  public void ambiguousImplicitEmptySetArgument() {
    // given
    problemsListener = new TestProblemsListener();
    Param p1 = param(STRING_SET, "name1");
    Param p2 = param(FILE_SET, "name2");

    Argument a1 = argument(node(EMPTY_SET));

    // when
    create(params(p1, p2), list(a1));

    // then
    problemsListener.assertOnlyProblem(AmbiguousImplicitArgsError.class);
  }

  @Test
  public void noParamWithProperTypeForImplicitStringArgument() throws Exception {
    doTestNoParamWithProperTypeForImplicitArgument(STRING, STRING_SET);
    doTestNoParamWithProperTypeForImplicitArgument(STRING, FILE);
    doTestNoParamWithProperTypeForImplicitArgument(STRING, FILE_SET);
  }

  @Test
  public void noParamWithProperTypeForImplicitStringSetArgument() throws Exception {
    doTestNoParamWithProperTypeForImplicitArgument(STRING_SET, STRING);
    doTestNoParamWithProperTypeForImplicitArgument(STRING_SET, FILE);
    doTestNoParamWithProperTypeForImplicitArgument(STRING_SET, FILE_SET);
  }

  @Test
  public void noParamWithProperTypeForImplicitFileArgument() throws Exception {
    doTestNoParamWithProperTypeForImplicitArgument(FILE, STRING);
    doTestNoParamWithProperTypeForImplicitArgument(FILE, STRING_SET);
    doTestNoParamWithProperTypeForImplicitArgument(FILE, FILE_SET);
  }

  @Test
  public void noParamWithProperTypeForImplicitFileSetArgument() throws Exception {
    doTestNoParamWithProperTypeForImplicitArgument(FILE_SET, STRING);
    doTestNoParamWithProperTypeForImplicitArgument(FILE_SET, STRING_SET);
    doTestNoParamWithProperTypeForImplicitArgument(FILE_SET, FILE);
  }

  @Test
  public void noParamWithProperTypeForImplicitEmptySetArgument() throws Exception {
    doTestNoParamWithProperTypeForImplicitArgument(EMPTY_SET, STRING);
    doTestNoParamWithProperTypeForImplicitArgument(EMPTY_SET, FILE);
  }

  private void doTestNoParamWithProperTypeForImplicitArgument(Type type, Type otherType) {
    // given
    problemsListener = new TestProblemsListener();
    Param p1 = param(otherType, "name1");
    Argument a1 = argument(node(type));

    // when
    create(params(p1), list(a1));

    // then
    problemsListener.assertOnlyProblem(AmbiguousImplicitArgsError.class);
  }

  private static Argument argument(DefinitionNode node) {
    return implicitArg(node, codeLocation(1, 2, 3));
  }

  private static Argument argument(String name, DefinitionNode node) {
    return explicitArg(name, node, codeLocation(1, 2, 3));
  }

  private static DefinitionNode node(Type type) {
    DefinitionNode node = mock(DefinitionNode.class);
    when(node.type()).thenReturn(type);
    return node;
  }

  private Map<String, DefinitionNode> create(ImmutableMap<String, Param> params, List<Argument> args) {
    return ArgumentNodesCreator.createArgumentNodes(problemsListener, function(params), args);
  }

  private static Function function(ImmutableMap<String, Param> params) {
    Signature signature = new Signature(STRING, simpleName("name"), params);
    return new PluginFunction(signature, mock(PluginInvoker.class));
  }

  private static ArrayList<Argument> list(Argument... args) {
    return newArrayList(args);
  }

  private static void assertThatNodeHasEmptySet(DefinitionNode node) {
    Task task = node.generateTask();
    task.execute(new TestSandbox());
    assertThat((Iterable<?>) task.result()).isEmpty();
  }
}
