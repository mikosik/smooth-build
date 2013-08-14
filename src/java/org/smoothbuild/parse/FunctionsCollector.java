package org.smoothbuild.parse;

import static org.smoothbuild.lang.function.CanonicalName.isValidSimpleName;

import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.smoothbuild.antlr.SmoothBaseVisitor;
import org.smoothbuild.antlr.SmoothParser.FunctionContext;
import org.smoothbuild.antlr.SmoothParser.FunctionNameContext;
import org.smoothbuild.lang.function.CanonicalName;
import org.smoothbuild.parse.err.DuplicateFunctionError;
import org.smoothbuild.parse.err.IllegalFunctionNameError;
import org.smoothbuild.parse.err.OverridenImportWarning;
import org.smoothbuild.problem.ProblemsListener;
import org.smoothbuild.problem.SourceLocation;
import org.smoothbuild.registry.ImportedFunctions;

import com.google.common.collect.Maps;

public class FunctionsCollector extends SmoothBaseVisitor<Void> {
  private final ProblemsListener problemsListener;
  private final ImportedFunctions importedFunctions;
  private final Map<String, FunctionContext> functions;

  public FunctionsCollector(ProblemsListener problemsListener, ImportedFunctions importedFunctions) {
    this.problemsListener = problemsListener;
    this.importedFunctions = importedFunctions;
    this.functions = Maps.newHashMap();
  }

  @Override
  public Void visitFunction(FunctionContext functionContext) {
    FunctionNameContext nameContext = functionContext.functionName();
    String name = nameContext.getText();

    if (!isValidSimpleName(name)) {
      problemsListener.report(new IllegalFunctionNameError(location(nameContext), name));
      return null;
    }

    if (functions.keySet().contains(name)) {
      problemsListener.report(new DuplicateFunctionError(location(nameContext), name));
      return null;
    }
    if (importedFunctions.contains(name)) {
      CanonicalName importedName = importedFunctions.get(name).name();
      SourceLocation location = location(nameContext);
      problemsListener.report(new OverridenImportWarning(location, name, importedName));
      return null;
    }

    functions.put(name, functionContext);
    return null;
  }

  public Map<String, FunctionContext> foundFunctions() {
    return functions;
  }

  private static SourceLocation location(ParserRuleContext parserRuleContext) {
    Token startToken = parserRuleContext.getStart();
    Token endToken = parserRuleContext.getStop();

    int line = startToken.getLine();
    int start = startToken.getStartIndex();
    int end = endToken.getStopIndex();

    return new SourceLocation(line, start, end);
  }
}
