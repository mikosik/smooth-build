package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.Optional.empty;
import static org.smoothbuild.cli.console.Maybe.maybeLogs;
import static org.smoothbuild.cli.console.Maybe.maybeValueAndLogs;
import static org.smoothbuild.lang.parse.AnalyzeSemantically.analyzeSemantically;
import static org.smoothbuild.lang.parse.InferTypes.inferTypes;
import static org.smoothbuild.lang.parse.LoadReferencable.loadReferencable;
import static org.smoothbuild.lang.parse.ParseModule.parseModule;
import static org.smoothbuild.lang.parse.ast.AstCreator.fromParseTree;

import java.util.HashMap;

import org.smoothbuild.antlr.lang.SmoothParser.ModuleContext;
import org.smoothbuild.cli.console.LogBuffer;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.lang.base.define.Constructor;
import org.smoothbuild.lang.base.define.Defined;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.Item;
import org.smoothbuild.lang.base.define.ModuleFiles;
import org.smoothbuild.lang.base.define.ModulePath;
import org.smoothbuild.lang.base.define.Referencable;
import org.smoothbuild.lang.base.define.SModule;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.ReferencableNode;
import org.smoothbuild.lang.parse.ast.StructNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class LoadModule {
  public static Maybe<SModule> loadModule(ModulePath path, Hash hash, ModuleFiles moduleFiles,
      String sourceCode, Definitions imported) {
    var logBuffer = new LogBuffer();
    FilePath filePath = moduleFiles.smoothFile();
    Maybe<ModuleContext> moduleContext = parseModule(filePath, sourceCode);
    logBuffer.logAll(moduleContext.logs());
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }

    Ast ast = fromParseTree(filePath, moduleContext.value());
    logBuffer.logAll(analyzeSemantically(imported, ast));
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }

    Maybe<Ast> maybeSortedAst = ast.sortedByDependencies();
    logBuffer.logAll(maybeSortedAst.logs());
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }
    Ast sortedAst = maybeSortedAst.value();

    logBuffer.logAll(inferTypes(path, sortedAst, imported));
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }

    var referencables = loadReferencables(path, imported, sortedAst);
    var definedStructs = sortedAst.structs().stream()
        .map(structNode -> structNode.struct().get())
        .collect(toImmutableMap(Defined::name, d -> (Defined) d));
    SModule module = new SModule(path, hash, moduleFiles, imported.modules().values().asList(),
        definedStructs, referencables);
    return maybeValueAndLogs(module, logBuffer);
  }

  private static ImmutableMap<String, Referencable> loadReferencables(
      ModulePath path, Definitions imported, Ast ast) {
    var local = new HashMap<String, Referencable>();
    for (StructNode struct : ast.structs()) {
      Constructor constructor = loadConstructor(path, struct);
      local.put(constructor.name(), constructor);
    }
    Referencables referencables = new Referencables(imported.referencables(), local);
    for (ReferencableNode referencable : ast.referencable()) {
      local.put(referencable.name(), loadReferencable(path, referencable, referencables));
    }
    return ImmutableMap.copyOf(local);
  }

  private static Constructor loadConstructor(ModulePath path, StructNode struct) {
    Type resultType = struct.type().get();
    String name = struct.constructor().name();
    ImmutableList<Item> parameters = struct.fields()
        .stream()
        .map(field -> new Item(field.type().get(), field.name(), empty()))
        .collect(toImmutableList());
    return new Constructor(resultType, path, name, parameters, struct.location());
  }
}
