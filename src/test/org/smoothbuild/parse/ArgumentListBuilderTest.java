package org.smoothbuild.parse;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.function.base.Param.param;
import static org.smoothbuild.function.base.Param.params;
import static org.smoothbuild.function.base.QualifiedName.simpleName;
import static org.smoothbuild.function.base.Type.FILE;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.function.expr.LiteralExpression.literalExpression;
import static org.smoothbuild.function.expr.LiteralExpression.stringExpression;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.function.def.ExpressionNode;
import org.smoothbuild.function.expr.ExpressionId;
import org.smoothbuild.function.plugin.PluginFunction;
import org.smoothbuild.function.plugin.PluginInvoker;
import org.smoothbuild.parse.err.DuplicateArgNameProblem;
import org.smoothbuild.parse.err.ManyAmbigiousParamsAssignableFromImplicitArgProblem;
import org.smoothbuild.parse.err.NoParamAssignableFromImplicitArgProblem;
import org.smoothbuild.parse.err.UnknownParamNameProblem;
import org.smoothbuild.plugin.File;
import org.smoothbuild.problem.SourceLocation;
import org.smoothbuild.testing.problem.TestingProblemsListener;

import com.google.common.collect.ImmutableMap;

public class ArgumentListBuilderTest {
  String string1Name = "string1";
  String string2Name = "string2";
  String file1Name = "file1";
  String file2Name = "file2";

  Param string1Param = param(STRING, string1Name);
  Param string2Param = param(STRING, string2Name);
  Param file1Param = param(FILE, file1Name);
  Param file2Param = param(FILE, file2Name);

  DefinitionNode string1Expr = node(new ExpressionId("1"), "value1");
  DefinitionNode string2Expr = node(new ExpressionId("2"), "value2");
  DefinitionNode stringImplicit1Expr = node(new ExpressionId("3"), "value2");
  DefinitionNode stringImplicit2Expr = node(new ExpressionId("4"), "value2");
  DefinitionNode file1Expr = node(new ExpressionId("5"), FILE, mock(File.class));
  DefinitionNode file2Expr = node(new ExpressionId("6"), FILE, mock(File.class));

  Argument string1Arg = argument(string1Name, string1Expr);
  Argument string2Arg = argument(string2Name, string2Expr);
  Argument stringImplicit1Arg = argument(stringImplicit1Expr);
  Argument stringImplicit2Arg = argument(stringImplicit2Expr);
  Argument file1Arg = argument(file1Name, file1Expr);
  Argument file2Arg = argument(file2Name, file2Expr);

  TestingProblemsListener problemsListener = new TestingProblemsListener();
  ArgumentListBuilder argumentListBuilder = new ArgumentListBuilder(problemsListener);

  @Test
  public void convertingExplicitArguments() {
    ImmutableMap<String, Param> params = params(string1Param, file1Param);
    List<Argument> args = newArrayList(string1Arg, file1Arg);

    Map<String, DefinitionNode> result = argumentListBuilder.convert(function(params), args);

    assertThat(result.get(string1Name)).isSameAs(string1Expr);
    assertThat(result.get(file1Name)).isSameAs(file1Expr);
    assertThat(result.size()).isEqualTo(2);
    problemsListener.assertNoProblems();
  }

  @Test
  public void duplicatedExplicitNames() {
    ImmutableMap<String, Param> params = params(string1Param, string2Param);
    List<Argument> args = newArrayList(string1Arg, string1Arg);

    argumentListBuilder.convert(function(params), args);

    problemsListener.assertOnlyProblem(DuplicateArgNameProblem.class);
  }

  @Test
  public void unknownParamName() {
    ImmutableMap<String, Param> params = params(string1Param);
    List<Argument> args = newArrayList(file1Arg);

    argumentListBuilder.convert(function(params), args);

    problemsListener.assertOnlyProblem(UnknownParamNameProblem.class);
  }

  @Test
  public void convertingEmptyList() throws Exception {
    ImmutableMap<String, Param> params = params(string1Param, file1Param);
    List<Argument> args = newArrayList();

    Map<String, DefinitionNode> result = argumentListBuilder.convert(function(params), args);

    assertThat(result.size()).isEqualTo(0);
    problemsListener.assertNoProblems();
  }

  @Test
  public void ambigiuousImplicitArgument() throws Exception {
    ImmutableMap<String, Param> params = params(string1Param, string2Param);
    List<Argument> args = newArrayList(stringImplicit1Arg);

    argumentListBuilder.convert(function(params), args);

    problemsListener.assertOnlyProblem(ManyAmbigiousParamsAssignableFromImplicitArgProblem.class);
  }

  @Test
  public void noParamWithProperTypeForImplicitArgument() throws Exception {
    ImmutableMap<String, Param> params = params(file1Param);
    List<Argument> args = newArrayList(stringImplicit1Arg);

    argumentListBuilder.convert(function(params), args);

    problemsListener.assertOnlyProblem(NoParamAssignableFromImplicitArgProblem.class);
  }

  private static Argument argument(DefinitionNode node) {
    return new Argument(null, node, new SourceLocation(1, 2, 3));
  }

  private static Argument argument(String name, DefinitionNode node) {
    return new Argument(name, node, new SourceLocation(1, 2, 3));
  }

  private static DefinitionNode node(ExpressionId id, String value) {
    return new ExpressionNode(stringExpression(id, value));
  }

  private DefinitionNode node(ExpressionId expressionId, Type type, File file) {
    return new ExpressionNode(literalExpression(expressionId, type, file));
  }

  private static Function function(ImmutableMap<String, Param> params) {
    Signature signature = new Signature(STRING, simpleName("name"), params);
    return new PluginFunction(signature, mock(PluginInvoker.class));
  }
}
