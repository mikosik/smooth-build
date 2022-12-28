package org.smoothbuild.compile;

import static org.smoothbuild.compile.ps.PsTranslator.translatePs;

import javax.inject.Inject;

import org.smoothbuild.compile.fp.FpTranslator;
import org.smoothbuild.compile.lang.define.DefinitionsS;
import org.smoothbuild.compile.lang.define.ModFiles;
import org.smoothbuild.compile.lang.define.ModuleS;
import org.smoothbuild.compile.ps.ast.expr.ModuleP;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.out.log.MaybeProcessor;

/**
 * F->S Translator.
 * Translates source code F-ile to S-objects.
 */
public class FsTranslator {
  private final FpTranslator fpTranslator;

  @Inject
  public FsTranslator(FpTranslator fpTranslator) {
    this.fpTranslator = fpTranslator;
  }

  public Maybe<ModuleS> translateFs(ModFiles modFiles, DefinitionsS imported) {
    return new Translator(fpTranslator, modFiles, imported)
        .process();
  }

  private static class Translator extends MaybeProcessor<ModuleS> {
    private final FpTranslator fpTranslator;
    private final ModFiles modFiles;
    private final DefinitionsS imported;

    public Translator(FpTranslator fpTranslator, ModFiles modFiles, DefinitionsS imported) {
      this.fpTranslator = fpTranslator;
      this.modFiles = modFiles;
      this.imported = imported;
    }

    @Override
    protected ModuleS processImpl() throws FailedException {
      ModuleP moduleP = addLogsAndGetValue(fpTranslator.translateFp(modFiles));
      return addLogsAndGetValue(translatePs(moduleP, imported));
    }
  }
}
