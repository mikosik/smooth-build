package org.smoothbuild.lang.parse;

import static org.smoothbuild.lang.parse.ParseError.parseError;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.like.ReferencableLike;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.AstVisitor;
import org.smoothbuild.lang.parse.ast.RealFuncNode;
import org.smoothbuild.lang.parse.ast.RefNode;
import org.smoothbuild.lang.parse.ast.StructNode;
import org.smoothbuild.util.Scope;

public class ReferenceResolver extends AstVisitor {
  private final Scope<? extends ReferencableLike> scope;
  private final Logger logger;

  public static void resolveReferences(Logger logger, Definitions imported, Ast ast) {
    var importedScope = new Scope<>(imported.referencables());
    var constructors = map(ast.structs(), StructNode::constructor);
    var referencables = ast.referencables();
    var scope = new Scope<>(importedScope, nList(concat(referencables, constructors)));
    new ReferenceResolver(scope, logger)
        .visitAst(ast);
  }

  public ReferenceResolver(Scope<? extends ReferencableLike> scope, Logger logger) {
    this.scope = scope;
    this.logger = logger;
  }

  @Override
  public void visitRealFunc(RealFuncNode func) {
    visitParams(func.params());
    func.body().ifPresent(expr -> {
      var referenceResolver = new ReferenceResolver(new Scope<>(scope, func.params()), logger);
      referenceResolver.visitExpr(expr);
    });
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
