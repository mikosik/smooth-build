package org.smoothbuild.parse;

import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.out.log.Maybe.maybeLogs;
import static org.smoothbuild.parse.AnalyzeSemantically.analyzeSemantically;
import static org.smoothbuild.parse.CreateModS.createModS;
import static org.smoothbuild.parse.ParseModule.parseModule;
import static org.smoothbuild.parse.ast.AstCreator.fromParseTree;
import static org.smoothbuild.parse.ast.AstSorter.sortParsedByDeps;

import org.smoothbuild.antlr.lang.SmoothParser.ModContext;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.define.ModFiles;
import org.smoothbuild.lang.define.ModPath;
import org.smoothbuild.lang.define.ModS;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.parse.ast.Ast;

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

    var mod = createModS(path, modFiles, sortedAst, imported);
    logBuffer.logAll(mod.logs());
    if (logBuffer.containsProblem()) {
      return maybeLogs(logBuffer);
    }

    return maybe(mod.value(), logBuffer);
  }
}
