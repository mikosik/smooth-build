package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Optional.empty;
import static org.smoothbuild.lang.parse.AnalyzeSemantically.analyzeSemantically;
import static org.smoothbuild.lang.parse.AssignArgsToParams.assignArgsToParams;
import static org.smoothbuild.lang.parse.EvaluableLoader.loadFunction;
import static org.smoothbuild.lang.parse.EvaluableLoader.loadValue;
import static org.smoothbuild.lang.parse.InferTypes.inferTypes;
import static org.smoothbuild.lang.parse.ParseModule.parseModule;
import static org.smoothbuild.lang.parse.ast.AstCreator.fromParseTree;

import java.util.HashMap;

import org.smoothbuild.antlr.lang.SmoothParser.ModuleContext;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.lang.base.Callable;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.Declared;
import org.smoothbuild.lang.base.Definitions;
import org.smoothbuild.lang.base.Item;
import org.smoothbuild.lang.base.ModuleLocation;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.EvaluableNode;
import org.smoothbuild.lang.parse.ast.FuncNode;
import org.smoothbuild.lang.parse.ast.StructNode;
import org.smoothbuild.lang.parse.ast.ValueNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class LoadModule {
  public static Maybe<Definitions> loadModule(
      Definitions imports, ModuleLocation moduleLocation, String sourceCode) {
    var result = new Maybe<Definitions>();

    Maybe<ModuleContext> moduleContext = parseModule(moduleLocation, sourceCode);
    result.logAllFrom(moduleContext);
    if (result.hasProblems()) {
      return Maybe.withLogsFrom(moduleContext);
    }

    Ast ast = fromParseTree(moduleLocation, moduleContext.value());
    result.logAll(analyzeSemantically(imports, ast));
    if (result.hasProblems()) {
      return result;
    }

    result.logAll(assignArgsToParams(ast, imports));
    if (result.hasProblems()) {
      return result;
    }

    Maybe<Ast> maybeSortedAst = ast.sortedByDependencies();
    result.logAllFrom(maybeSortedAst);
    if (result.hasProblems()) {
      return result;
    }
    Ast sortedAst = maybeSortedAst.value();

    result.logAll(inferTypes(sortedAst, imports));
    if (result.hasProblems()) {
      return result;
    }

    var declaredFunctions = loadCodes(imports, sortedAst);
    var declaredStructs = sortedAst.structs().stream()
        .map(structNode -> structNode.struct().get())
        .collect(toImmutableMap(Declared::name, d -> d));
    result.setValue(new Definitions(declaredStructs, declaredFunctions));
    return result;
  }

  private static ImmutableMap<String, Declared> loadCodes(Definitions imports, Ast ast) {
    var localFunctions = new HashMap<String, Declared>();
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
    Type resultType = struct.type().get();
    String name = struct.constructor().name();
    ImmutableList<Item> parameters = struct.fields()
        .stream()
        .map(field -> new Item(field.type().get(), field.name(), empty()))
        .collect(toImmutableList());
    return new Constructor(resultType, name, parameters, struct.location());
  }
}
