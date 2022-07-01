package org.smoothbuild.parse;

import static org.smoothbuild.parse.ParseError.parseError;
import static org.smoothbuild.util.bindings.Bindings.immutableBindings;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Maps.mapValues;

import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.define.FuncS;
import org.smoothbuild.lang.define.TopRefableS;
import org.smoothbuild.lang.define.ValS;
import org.smoothbuild.lang.like.common.RefableC;
import org.smoothbuild.lang.like.wrap.FuncW;
import org.smoothbuild.lang.like.wrap.TopRefableW;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.AstVisitor;
import org.smoothbuild.parse.ast.FuncP;
import org.smoothbuild.parse.ast.RefP;
import org.smoothbuild.parse.ast.StructP;
import org.smoothbuild.util.Throwables;
import org.smoothbuild.util.bindings.Bindings;
import org.smoothbuild.util.bindings.ImmutableBindings;
import org.smoothbuild.util.collect.Nameables;

public class ReferenceResolver extends AstVisitor {
  private final Bindings<RefableC> bindings;
  private final Logger logger;

  public static void resolveReferences(Logger logger, DefsS imported, Ast ast) {
    var scope = scope(imported, ast);
    new ReferenceResolver(scope, logger)
        .visitAst(ast);
  }

  private static Bindings<RefableC> scope(DefsS imported, Ast ast) {
    ImmutableBindings<RefableC> bindings = immutableBindings(
        mapValues(imported.topRefables().asMap(), ReferenceResolver::wrap));
    var ctors = map(ast.structs(), StructP::ctor);
    var refables = ast.topRefables();
    return bindings.newMutableScope()
        .addAll(Nameables.<RefableC>toMap(concat(refables, ctors)));
  }

  private static TopRefableW wrap(TopRefableS topRefableS) {
    return switch (topRefableS) {
      case FuncS funcS -> new FuncW(funcS);
      case ValS valS -> new TopRefableW(valS);
      default -> throw Throwables.unexpectedCaseExc(topRefableS);
    };
  }

  public ReferenceResolver(Bindings<RefableC> bindings, Logger logger) {
    this.bindings = bindings;
    this.logger = logger;
  }

  @Override
  public void visitFunc(FuncP funcP) {
    visitParams(funcP.params());
    funcP.body().ifPresent(expr -> {
      var withParamBindings = bindings.newMutableScope()
          .addAll(funcP.params().map());
      var referenceResolver = new ReferenceResolver(withParamBindings, logger);
      referenceResolver.visitObj(expr);
    });
  }

  @Override
  public void visitRef(RefP ref) {
    super.visitRef(ref);
    var refableC = bindings.getOpt(ref.name());
    if (refableC.isPresent()) {
      ref.setReferenced(refableC.get());
    } else {
      logger.log(parseError(ref.loc(), "`" + ref.name() + "` is undefined."));
    }
  }
}
