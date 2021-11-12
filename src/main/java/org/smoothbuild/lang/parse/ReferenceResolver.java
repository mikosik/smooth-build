package org.smoothbuild.lang.parse;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static org.smoothbuild.lang.parse.ParseError.parseError;
import static org.smoothbuild.util.collect.NamedList.namedList;

import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.like.ReferencableLike;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.AstVisitor;
import org.smoothbuild.lang.parse.ast.RealFuncNode;
import org.smoothbuild.lang.parse.ast.RefNode;
import org.smoothbuild.util.Scope;

public class ReferenceResolver extends AstVisitor {
  private final Scope<? extends ReferencableLike> scope;
  private final Logger logger;

  public static void resolveReferences(Logger logger, Definitions imported, Ast ast) {
    var importedScope = new Scope<>(imported.referencables());
    var scope = new Scope<>(importedScope, ast.referencablesMap());
    new ReferenceResolver(scope, logger)
        .visitAst(ast);
  }

  public ReferenceResolver(Scope<? extends ReferencableLike> scope, Logger logger) {
    this.scope = scope;
    this.logger = logger;
  }

  @Override
  public void visitRealFunc(RealFuncNode func) {
    func.body().ifPresent(expr -> {
      var uniqueParams = func.params().stream()
          .collect(toImmutableMap(ReferencableLike::name, p -> p, (a, b) -> a));
      var params = namedList(uniqueParams);
      var referenceResolver = new ReferenceResolver(new Scope<>(scope, params), logger);
      referenceResolver.visitExpr(expr);
    });
    visitFunction(func);
  }

  @Override
  public void visitRef(RefNode ref) {
    super.visitRef(ref);
    String name = ref.name();
    if (scope.contains(name)) {
      ref.setReferenced(scope.get(name));
    } else {
      logger.log(parseError(ref.location(), "`" + name + "` is undefined."));
    }
  }
}
