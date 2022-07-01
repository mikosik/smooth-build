package org.smoothbuild.parse;

import static org.smoothbuild.out.log.Maybe.maybeLogs;
import static org.smoothbuild.out.log.Maybe.maybeValueAndLogs;
import static org.smoothbuild.parse.AnalyzeSemantically.analyzeSemantically;
import static org.smoothbuild.parse.LoadTopObj.loadTopObj;
import static org.smoothbuild.parse.ParseModule.parseModule;
import static org.smoothbuild.parse.ReferenceResolver.resolveReferences;
import static org.smoothbuild.parse.TypeInferrer.inferTypes;
import static org.smoothbuild.parse.ast.AstCreator.fromParseTree;
import static org.smoothbuild.parse.ast.AstSorter.sortParsedByDeps;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import java.util.HashMap;

import org.smoothbuild.antlr.lang.SmoothParser.ModContext;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.define.ModFiles;
import org.smoothbuild.lang.define.ModPath;
import org.smoothbuild.lang.define.ModS;
import org.smoothbuild.lang.define.MonoFuncS;
import org.smoothbuild.lang.define.StructDefS;
import org.smoothbuild.lang.define.SyntCtorS;
import org.smoothbuild.lang.define.TDefS;
import org.smoothbuild.lang.define.TopRefableS;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.TypeFS;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.ItemP;
import org.smoothbuild.parse.ast.StructP;
import org.smoothbuild.util.NameBindings;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableMap;

public class LoadMod {
  public static Maybe<ModS> loadModule(
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

    Maybe<Ast> maybeSortedAst = sortParsedByDeps(ast);
    logBuffer.logAll(maybeSortedAst.logs());
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }
    Ast sortedAst = maybeSortedAst.value();

    resolveReferences(logBuffer, imported, sortedAst);
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }

    logBuffer.logAll(inferTypes(sortedAst, imported));
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }

    var types = sortedAst.structs().map(s -> loadStructDef(path, s));
    var topRefables = loadTopRefables(path, sortedAst, imported);
    var moduleS = new ModS(path, modFiles, types, topRefables);
    return maybeValueAndLogs(moduleS, logBuffer);
  }

  private static TDefS loadStructDef(ModPath path, StructP struct) {
    var type = (StructTS) struct.typeS().get();
    var loc = struct.loc();
    return new StructDefS(type, path, loc);
  }

  private static NList<TopRefableS> loadTopRefables(ModPath path, Ast ast, DefsS imported) {
    var importedBindings = new NameBindings<>(imported.topRefables().map());
    var localBindings = new HashMap<String, TopRefableS>();
    var bindings = new NameBindings<>(importedBindings, localBindings);

    for (var structP : ast.structs()) {
      var ctorS = loadSyntCtor(path, structP);
      localBindings.put(ctorS.name(), ctorS);
    }

    for (var refableP : ast.topRefables()) {
      var loaded = loadTopObj(path, refableP, bindings);
      localBindings.put(loaded.name(), loaded);
    }
    return nList(ImmutableMap.copyOf(localBindings));
  }

  private static MonoFuncS loadSyntCtor(ModPath path, StructP struct) {
    var resultT = (StructTS) struct.typeS().get();
    var name = struct.ctor().name();
    var paramTs = map(struct.fields(), f -> f.typeS().get());
    var type = TypeFS.func(resultT, paramTs);
    var params = struct.fields().map(ItemP::toItemS);
    var loc = struct.loc();
    return new SyntCtorS(type, path, name, params, loc);
  }
}
