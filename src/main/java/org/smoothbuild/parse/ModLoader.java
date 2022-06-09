package org.smoothbuild.parse;

import static org.smoothbuild.lang.define.PolyFuncS.polyFuncS;
import static org.smoothbuild.out.log.Maybe.maybeLogs;
import static org.smoothbuild.out.log.Maybe.maybeValueAndLogs;
import static org.smoothbuild.parse.AnalyzeSemantically.analyzeSemantically;
import static org.smoothbuild.parse.ParseModule.parseModule;
import static org.smoothbuild.parse.ReferenceResolver.resolveReferences;
import static org.smoothbuild.parse.ast.AstCreator.fromParseTree;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import javax.inject.Inject;

import org.smoothbuild.antlr.lang.SmoothParser.ModContext;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.define.ModFiles;
import org.smoothbuild.lang.define.ModPath;
import org.smoothbuild.lang.define.ModS;
import org.smoothbuild.lang.define.PolyFuncS;
import org.smoothbuild.lang.define.RefableObjS;
import org.smoothbuild.lang.define.StructDefS;
import org.smoothbuild.lang.define.SyntCtorS;
import org.smoothbuild.lang.define.TDefS;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.TypeSF;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.ItemN;
import org.smoothbuild.parse.ast.StructN;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class ModLoader {
  private final TypeInferrer typeInferrer;
  private final TopObjLoader topObjLoader;
  private final TypeSF typeSF;

  @Inject
  public ModLoader(TypeInferrer typeInferrer, TopObjLoader topObjLoader, TypeSF typeSF) {
    this.typeInferrer = typeInferrer;
    this.topObjLoader = topObjLoader;
    this.typeSF = typeSF;
  }

  public Maybe<ModS> loadModule(
      ModPath path, ModFiles modFiles, String sourceCode, DefsS imported) {
    var logBuffer = new LogBuffer();
    FilePath filePath = modFiles.smoothFile();
    Maybe<ModContext> moduleContext = parseModule(filePath, sourceCode);
    logBuffer.logAll(moduleContext.logs());
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }

    Ast ast = fromParseTree(filePath, moduleContext.value());
    logBuffer.logAll(analyzeSemantically(imported, ast));
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }

    Maybe<Ast> maybeSortedAst = ast.sortedByDeps();
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

    var types = sortedAst.structs().map(s -> loadStructDef(path, s));
    var evals = loadTopRefables(path, sortedAst);
    var moduleS = new ModS(path, modFiles, types, evals);
    return maybeValueAndLogs(moduleS, logBuffer);
  }

  private TDefS loadStructDef(ModPath path, StructN struct) {
    var type = (StructTS) struct.typeO().get();
    var loc = struct.loc();
    return new StructDefS(type, path, loc);
  }

  private NList<RefableObjS> loadTopRefables(ModPath path, Ast ast) {
    var local = ImmutableList.<RefableObjS>builder();
    for (var structN : ast.structs()) {
      var ctorS = loadSyntCtor(path, structN);
      local.add(ctorS);
    }
    for (var refableN : ast.topRefables()) {
      local.add(topObjLoader.loadTopObj(path, refableN));
    }
    return nList(local.build());
  }

  private PolyFuncS loadSyntCtor(ModPath path, StructN struct) {
    var resultT = (StructTS) struct.typeO().get();
    var name = struct.ctor().name();
    var paramTs = map(struct.fields(), f -> f.typeO().get());
    var type = typeSF.func(resultT, paramTs);
    var params = struct.fields().map(ItemN::toItemS);
    var loc = struct.loc();
    return polyFuncS(new SyntCtorS(type, path, name, params, loc));
  }
}
