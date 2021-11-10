package org.smoothbuild.lang.parse;

import static org.smoothbuild.cli.console.Maybe.maybeLogs;
import static org.smoothbuild.cli.console.Maybe.maybeValueAndLogs;
import static org.smoothbuild.lang.parse.AnalyzeSemantically.analyzeSemantically;
import static org.smoothbuild.lang.parse.ParseModule.parseModule;
import static org.smoothbuild.lang.parse.ast.AstCreator.fromParseTree;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Maps.toMap;

import java.util.HashMap;

import javax.inject.Inject;

import org.smoothbuild.antlr.lang.SmoothParser.ModuleContext;
import org.smoothbuild.cli.console.LogBuffer;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.lang.base.define.ConstructorS;
import org.smoothbuild.lang.base.define.Defined;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.GlobalReferencable;
import org.smoothbuild.lang.base.define.ModuleFiles;
import org.smoothbuild.lang.base.define.ModulePath;
import org.smoothbuild.lang.base.define.ModuleS;
import org.smoothbuild.lang.base.define.StructS;
import org.smoothbuild.lang.base.type.impl.StructTypeS;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.ReferencableNode;
import org.smoothbuild.lang.parse.ast.StructNode;

import com.google.common.collect.ImmutableMap;

public class ModuleLoader {
  private final TypeInferrer typeInferrer;
  private final ReferencableLoader referencableLoader;
  private final TypeFactoryS typeFactory;

  @Inject
  public ModuleLoader(TypeInferrer typeInferrer, ReferencableLoader referencableLoader,
      TypeFactoryS typeFactory) {
    this.typeInferrer = typeInferrer;
    this.referencableLoader = referencableLoader;
    this.typeFactory = typeFactory;
  }

  public Maybe<ModuleS> loadModule(ModulePath path, Hash hash, ModuleFiles moduleFiles,
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

    logBuffer.logAll(typeInferrer.inferTypes(sortedAst, imported));
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }

    var referencables = loadReferencables(path, sortedAst);
    var structs = map(sortedAst.structs(), s -> loadStruct(path, s));
    var definedStructs = toMap(structs, Defined::name, d -> (Defined) d);
    ModuleS module = new ModuleS(path, hash, moduleFiles, imported.modules().values().asList(),
        definedStructs, referencables);
    return maybeValueAndLogs(module, logBuffer);
  }

  private StructS loadStruct(ModulePath path, StructNode struct) {
    var type = (StructTypeS) struct.type().get();
    var name = struct.name();
    var items = map(struct.fields(), f -> f.toItem(path));
    var location = struct.location();
    return new StructS(type, path, name, items, location);
  }

  private ImmutableMap<String, GlobalReferencable> loadReferencables(ModulePath path, Ast ast) {
    var local = new HashMap<String, GlobalReferencable>();
    for (StructNode struct : ast.structs()) {
      ConstructorS constructor = loadConstructor(path, struct);
      local.put(constructor.name(), constructor);
    }
    for (ReferencableNode referencable : ast.referencable()) {
      local.put(referencable.name(), referencableLoader.loadReferencable(path, referencable));
    }
    return ImmutableMap.copyOf(local);
  }

  private ConstructorS loadConstructor(ModulePath path, StructNode struct) {
    var resultType = struct.type().get();
    var name = struct.constructor().name();
    var parameterTypes = map(struct.fields(), f -> f.type().get());
    var type = typeFactory.function(resultType, parameterTypes);
    var parameters = map(struct.fields(), f -> f.toItem(path));
    return new ConstructorS(type, path, name, parameters, struct.location());
  }
}
