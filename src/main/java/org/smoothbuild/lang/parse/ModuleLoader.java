package org.smoothbuild.lang.parse;

import static org.smoothbuild.cli.console.Maybe.maybeLogs;
import static org.smoothbuild.cli.console.Maybe.maybeValueAndLogs;
import static org.smoothbuild.lang.parse.AnalyzeSemantically.analyzeSemantically;
import static org.smoothbuild.lang.parse.ParseModule.parseModule;
import static org.smoothbuild.lang.parse.ReferenceResolver.resolveReferences;
import static org.smoothbuild.lang.parse.ast.AstCreator.fromParseTree;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import javax.inject.Inject;

import org.smoothbuild.antlr.lang.SmoothParser.ModuleContext;
import org.smoothbuild.cli.console.LogBuffer;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.lang.base.define.ConstructorS;
import org.smoothbuild.lang.base.define.DefinedType;
import org.smoothbuild.lang.base.define.DefinitionsS;
import org.smoothbuild.lang.base.define.ModuleFiles;
import org.smoothbuild.lang.base.define.ModulePath;
import org.smoothbuild.lang.base.define.ModuleS;
import org.smoothbuild.lang.base.define.StructS;
import org.smoothbuild.lang.base.define.TopEvaluableS;
import org.smoothbuild.lang.base.type.impl.StructTypeS;
import org.smoothbuild.lang.base.type.impl.TypeFactoryS;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.EvaluableNode;
import org.smoothbuild.lang.parse.ast.StructNode;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class ModuleLoader {
  private final TypeInferrer typeInferrer;
  private final TopEvaluableLoader topEvaluableLoader;
  private final TypeFactoryS typeFactory;

  @Inject
  public ModuleLoader(TypeInferrer typeInferrer, TopEvaluableLoader topEvaluableLoader,
      TypeFactoryS typeFactory) {
    this.typeInferrer = typeInferrer;
    this.topEvaluableLoader = topEvaluableLoader;
    this.typeFactory = typeFactory;
  }

  public Maybe<ModuleS> loadModule(
      ModulePath path, ModuleFiles moduleFiles, String sourceCode, DefinitionsS imported) {
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

    resolveReferences(logBuffer, imported, sortedAst);
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }

    logBuffer.logAll(typeInferrer.inferTypes(sortedAst, imported));
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }

    var modules = imported.modules().values().asList();
    var types = sortedAst.structs().map(s -> (DefinedType) loadStruct(path, s));
    var referencables = loadEvaluables(path, sortedAst);
    var moduleS = new ModuleS(path, moduleFiles, modules, types, referencables);
    return maybeValueAndLogs(moduleS, logBuffer);
  }

  private StructS loadStruct(ModulePath path, StructNode struct) {
    var type = (StructTypeS) struct.type().get();
    var name = struct.name();
    var location = struct.location();
    return new StructS(type, path, name, location);
  }

  private NList<TopEvaluableS> loadEvaluables(ModulePath path, Ast ast) {
    var local = ImmutableList.<TopEvaluableS>builder();
    for (StructNode struct : ast.structs()) {
      ConstructorS constructor = loadConstructor(path, struct);
      local.add(constructor);
    }
    for (EvaluableNode referencable : ast.evaluables()) {
      local.add(topEvaluableLoader.loadEvaluables(path, referencable));
    }
    return nList(local.build());
  }

  private ConstructorS loadConstructor(ModulePath path, StructNode struct) {
    var resultType = struct.type().get();
    var name = struct.constructor().name();
    var parameterTypes = map(struct.fields(), f -> f.type().get());
    var type = typeFactory.function(resultType, parameterTypes);
    var parameters = struct.fields().map(f -> f.toItem(path));
    return new ConstructorS(type, path, name, parameters, struct.location());
  }
}
