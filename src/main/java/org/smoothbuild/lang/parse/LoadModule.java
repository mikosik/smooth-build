package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Optional.empty;
import static org.smoothbuild.lang.parse.AnalyzeSemantically.analyzeSemantically;
import static org.smoothbuild.lang.parse.EvaluableLoader.loadFunction;
import static org.smoothbuild.lang.parse.EvaluableLoader.loadValue;
import static org.smoothbuild.lang.parse.InferTypesAndParamAssignments.inferTypesAndParamAssignment;
import static org.smoothbuild.lang.parse.ParseModule.parseModule;
import static org.smoothbuild.lang.parse.ast.AstCreator.fromParseTree;

import java.util.HashMap;
import java.util.List;

import org.smoothbuild.antlr.lang.SmoothParser.ModuleContext;
import org.smoothbuild.cli.console.ValueWithLogs;
import org.smoothbuild.lang.base.Callable;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.Definitions;
import org.smoothbuild.lang.base.Evaluable;
import org.smoothbuild.lang.base.ModuleLocation;
import org.smoothbuild.lang.base.Parameter;
import org.smoothbuild.lang.base.Signature;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.EvaluableNode;
import org.smoothbuild.lang.parse.ast.FuncNode;
import org.smoothbuild.lang.parse.ast.ItemNode;
import org.smoothbuild.lang.parse.ast.StructNode;
import org.smoothbuild.lang.parse.ast.ValueNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class LoadModule {
  public static ValueWithLogs<Definitions> loadModule(
      Definitions imports, ModuleLocation moduleLocation, String sourceCode) {
    var result = new ValueWithLogs<Definitions>();

    ModuleContext moduleContext = parseModule(moduleLocation, result, sourceCode);
    if (result.hasProblems()) {
      return result;
    }

    Ast ast = fromParseTree(moduleLocation, moduleContext);
    analyzeSemantically(imports, ast, result);
    if (result.hasProblems()) {
      return result;
    }
    Ast sortedAst = ast.sortedByDependencies(result);
    if (result.hasProblems()) {
      return result;
    }
    inferTypesAndParamAssignment(sortedAst, imports, result);
    if (result.hasProblems()) {
      return result;
    }
    var declaredFunctions = loadCodes(imports, sortedAst);
    var declaredTypes = sortedAst.structs().stream()
        .map(structNode -> structNode.type().get())
        .collect(toImmutableMap(Type::name, t -> t));
    result.setValue(new Definitions(declaredTypes, declaredFunctions));
    return result;
  }

  private static ImmutableMap<String, Evaluable> loadCodes(Definitions imports, Ast ast) {
    var localFunctions = new HashMap<String, Evaluable>();
    for (StructNode struct : ast.structs()) {
      Constructor constructor = loadConstructor(struct);
      localFunctions.put(constructor.name(), constructor);
    }
    for (EvaluableNode evaluable : ast.evaluables()) {
      if (evaluable instanceof FuncNode func) {
        Callable function = loadFunction(func, imports.evaluables(), localFunctions);
        localFunctions.put(function.name(), function);
      } else if (evaluable instanceof ValueNode valueNode) {
        Value value = loadValue(valueNode, imports.evaluables(), localFunctions);
        localFunctions.put(value.name(), value);
      } else {
        throw new RuntimeException("Unexpected case: " + evaluable.getClass().getCanonicalName());
      }
    }
    return ImmutableMap.copyOf(localFunctions);
  }

  private static Constructor loadConstructor(StructNode struct) {
    ImmutableList.Builder<Parameter> builder = ImmutableList.builder();
    List<ItemNode> fields = struct.fields();
    for (int i = 0; i < fields.size(); i++) {
      ItemNode field = fields.get(i);
      builder.add(new Parameter(i, field.type().get(), field.name(), empty(), field.location()));
    }
    ImmutableList<Parameter> parameters = builder.build();
    Signature signature =
        new Signature(struct.type().get(), struct.constructor().name(), parameters);
    return new Constructor(signature, struct.location());
  }
}
