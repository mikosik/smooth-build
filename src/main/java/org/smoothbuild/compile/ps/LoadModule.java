package org.smoothbuild.compile.ps;

import static org.smoothbuild.compile.ps.AnalyzeSemantically.analyzeSemantically;
import static org.smoothbuild.compile.ps.DetectUndefinedRefs.detectUndefinedRefs;
import static org.smoothbuild.compile.ps.FindSyntaxErrors.findSyntaxErrors;
import static org.smoothbuild.compile.ps.ModuleCreator.createModuleS;
import static org.smoothbuild.compile.ps.ParseModule.parseModule;
import static org.smoothbuild.compile.ps.ast.AstSorter.sortByDeps;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.out.log.Maybe.maybeLogs;

import org.smoothbuild.antlr.lang.SmoothParser.ModContext;
import org.smoothbuild.compile.ap.ApTranslator;
import org.smoothbuild.compile.lang.define.DefsS;
import org.smoothbuild.compile.lang.define.ModFiles;
import org.smoothbuild.compile.lang.define.ModuleS;
import org.smoothbuild.compile.ps.ast.Ast;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logs;
import org.smoothbuild.out.log.Maybe;

public class LoadModule {
  public static Maybe<ModuleS> loadModule(ModFiles modFiles, String sourceCode, DefsS imported) {
    var logBuffer = new LogBuffer();
    var filePath = modFiles.smoothFile();
    Maybe<ModContext> moduleContext = parseModule(filePath, sourceCode);
    logBuffer.logAll(moduleContext.logs());
    if (logBuffer.containsAtLeast(ERROR)) {
      return maybeLogs(logBuffer);
    }

    var maybeAst = ApTranslator.translate(filePath, moduleContext.value());
    logBuffer.logAll(maybeAst.logs());
    if (maybeAst.logs().containsAtLeast(ERROR)) {
      return maybeLogs(logBuffer);
    }
    var ast = maybeAst.value();
    logBuffer.logAll(findSyntaxErrors(ast));
    if (logBuffer.containsAtLeast(ERROR)) {
      return maybeLogs(logBuffer);
    }

    logBuffer.logAll(analyzeSemantically(imported, ast));
    if (logBuffer.containsAtLeast(ERROR)) {
      return maybeLogs(logBuffer);
    }

    Maybe<Ast> maybeSortedAst = sortByDeps(ast);
    logBuffer.logAll(maybeSortedAst.logs());
    if (logBuffer.containsAtLeast(ERROR)) {
      return maybeLogs(logBuffer);
    }
    Ast sortedAst = maybeSortedAst.value();

    Logs undefinedRefsProblems = detectUndefinedRefs(sortedAst, imported);
    logBuffer.logAll(undefinedRefsProblems);
    if (logBuffer.containsAtLeast(ERROR)) {
      return maybeLogs(logBuffer);
    }

    var mod = createModuleS(modFiles, sortedAst, imported);
    logBuffer.logAll(mod.logs());
    if (logBuffer.containsAtLeast(ERROR)) {
      return maybeLogs(logBuffer);
    }

    return maybe(mod.value(), logBuffer);
  }
}
