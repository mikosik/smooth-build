package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Optional.empty;
import static org.smoothbuild.lang.parse.AnalyzeSemantically.analyzeSemantically;
import static org.smoothbuild.lang.parse.AssignArgsToParams.assignArgsToParams;
import static org.smoothbuild.lang.parse.InferTypes.inferTypes;
import static org.smoothbuild.lang.parse.LoadReferencable.loadReferencable;
import static org.smoothbuild.lang.parse.ParseModule.parseModule;
import static org.smoothbuild.lang.parse.ast.AstCreator.fromParseTree;

import java.util.HashMap;

import org.smoothbuild.antlr.lang.SmoothParser.ModuleContext;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.lang.base.define.Constructor;
import org.smoothbuild.lang.base.define.Defined;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.ModuleLocation;
import org.smoothbuild.lang.base.define.Referencable;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.ReferencableNode;
import org.smoothbuild.lang.parse.ast.StructNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class LoadModule {
  public static Maybe<Definitions> loadModule(
      Definitions imported, ModuleLocation moduleLocation, String sourceCode) {
    var result = new Maybe<Definitions>();

    Maybe<ModuleContext> moduleContext = parseModule(moduleLocation, sourceCode);
    result.logAllFrom(moduleContext);
    if (result.hasProblems()) {
      return Maybe.withLogsFrom(moduleContext);
    }

    Ast ast = fromParseTree(moduleLocation, moduleContext.value());
    result.logAll(analyzeSemantically(imported, ast));
    if (result.hasProblems()) {
      return result;
    }

    result.logAll(assignArgsToParams(ast, imported));
    if (result.hasProblems()) {
      return result;
    }

    Maybe<Ast> maybeSortedAst = ast.sortedByDependencies();
    result.logAllFrom(maybeSortedAst);
    if (result.hasProblems()) {
      return result;
    }
    Ast sortedAst = maybeSortedAst.value();

    result.logAll(inferTypes(sortedAst, imported));
    if (result.hasProblems()) {
      return result;
    }

    var referencables = loadReferencables(imported, sortedAst);
    var definedStructs = sortedAst.structs().stream()
        .map(structNode -> structNode.struct().get())
        .collect(toImmutableMap(Defined::name, d -> (Defined) d));
    result.setValue(new Definitions(definedStructs, referencables));
    return result;
  }

  private static ImmutableMap<String, Referencable> loadReferencables(Definitions imported, Ast ast) {
    var local = new HashMap<String, Referencable>();
    for (StructNode struct : ast.structs()) {
      Constructor constructor = loadConstructor(struct);
      local.put(constructor.name(), constructor);
    }
    Referencables referencables = new Referencables(imported.referencables(), local);
    for (ReferencableNode referencable : ast.referencable()) {
      local.put(referencable.name(), loadReferencable(referencable, referencables));
    }
    return ImmutableMap.copyOf(local);
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
