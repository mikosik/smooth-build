package org.smoothbuild.lang.parse;

import static org.smoothbuild.lang.parse.ParseError.parseError;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import org.smoothbuild.lang.base.define.DefsS;
import org.smoothbuild.lang.base.like.EvalLike;
import org.smoothbuild.lang.parse.ast.Ast;
import org.smoothbuild.lang.parse.ast.AstVisitor;
import org.smoothbuild.lang.parse.ast.FuncN;
import org.smoothbuild.lang.parse.ast.RefN;
import org.smoothbuild.lang.parse.ast.StructN;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.util.Scope;

public class ReferenceResolver extends AstVisitor {
  private final Scope<? extends EvalLike> scope;
  private final Logger logger;

  public static void resolveReferences(Logger logger, DefsS imported, Ast ast) {
    var importedScope = new Scope<>(imported.topEvals());
    var ctors = map(ast.structs(), StructN::ctor);
    var evals = ast.topEvals();
    var scope = new Scope<>(importedScope, nList(concat(evals, ctors)));
    new ReferenceResolver(scope, logger)
        .visitAst(ast);
  }

  public ReferenceResolver(Scope<? extends EvalLike> scope, Logger logger) {
    this.scope = scope;
    this.logger = logger;
  }

  @Override
  public void visitFunc(FuncN funcN) {
    visitParams(funcN.params());
    funcN.body().ifPresent(expr -> {
      var referenceResolver = new ReferenceResolver(new Scope<>(scope, funcN.params()), logger);
      referenceResolver.visitExpr(expr);
    });
  }

  @Override
  public void visitRef(RefN ref) {
    super.visitRef(ref);
    String name = ref.name();
    if (scope.contains(name)) {
      ref.setReferenced(scope.get(name));
    } else {
      logger.log(parseError(ref.loc(), "`" + name + "` is undefined."));
    }
  }
}
