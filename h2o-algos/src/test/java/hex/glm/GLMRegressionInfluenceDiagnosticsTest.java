package hex.glm;

import org.junit.Test;
import org.junit.runner.RunWith;
import water.DKV;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;
import water.runner.CloudSize;
import water.runner.H2ORunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static hex.glm.GLMModel.GLMParameters.Family.binomial;
import static hex.glm.GLMModel.GLMParameters.Family.gaussian;
import static hex.glm.GLMModel.GLMParameters.Influence.dfbetas;
import static hex.glm.GLMModel.GLMParameters.Solver.IRLSM;
import static org.junit.Assert.assertTrue;

@RunWith(H2ORunner.class)
@CloudSize(1)
public class GLMRegressionInfluenceDiagnosticsTest extends TestUtil {

  /**
   * This test aims to test that RID is generated for Gaussian family with the same size as the original training 
   * dataset and we do not have any leaked keys.
   */
  @Test
  public void testGaussianRID(){
    Scope.enter();
    try {
      Frame train = parseAndTrackTestFile("smalldata/glm_test/prostate_cat_train.csv");
      // set cat columns
      train.replace(3, train.vec(3).toCategoricalVec()).remove(); // race to be enum
      train.replace(4, train.vec(4).toCategoricalVec()).remove(); // DPROS to be enum
      train.replace(5, train.vec(5).toCategoricalVec()).remove(); // DCAPS to be enum
      DKV.put(train);
      GLMModel.GLMParameters params = new GLMModel.GLMParameters(gaussian);
      params._response_column = "Response";
      params._ignored_columns = new String[]{"ID"};
      params._solver = IRLSM;
      params._train = train._key;
      params._influence = dfbetas;
      params._lambda = new double[]{0.0};
    //  params._standardize = false;
      GLMModel glm = new GLM(params).trainModel().get();
      Scope.track_generic(glm);
      Frame gaussianRID = glm.getRIDFrame();
      Scope.track(gaussianRID);
      List<String> coeffNames = Arrays.stream(glm._output._coefficient_names).collect(Collectors.toList());
      String[] RIDCoeffNames = gaussianRID.names();
      for (String ridCoeffName : RIDCoeffNames)
        if (ridCoeffName.startsWith("DFBETA"))
          assertTrue(coeffNames.contains(ridCoeffName.split("DFBETA_")[1]));
    } finally {
      Scope.exit();
    }
  }

  /**
   * We use the same test set as in the paper Logistic Regression Diagnostic by Daryl Pregibon and make sure we get the
   * same results.
   */
  @Test
  public void testBinomialRID() {
    Scope.enter();
    try {
      Frame train = parseAndTrackTestFile("smalldata/glm_test/vasoConstrctionRID.csv");
      // set cat columns
      train.replace(4, train.vec(4).toCategoricalVec()).remove(); // Response to be enum
      DKV.put(train);
      GLMModel.GLMParameters params = new GLMModel.GLMParameters(binomial);
      params._response_column = "Response";
      params._ignored_columns = new String[]{"Volume", "Rate"};
      params._solver = IRLSM;
      params._train = train._key;
      params._influence = dfbetas;
      params._lambda = new double[]{0.0};
      //  params._standardize = false;
      GLMModel glm = new GLM(params).trainModel().get();
      Scope.track_generic(glm);
      Frame gaussianRID = glm.getRIDFrame();
      Scope.track(gaussianRID);
      List<String> coeffNames = Arrays.stream(glm._output._coefficient_names).collect(Collectors.toList());
      String[] RIDCoeffNames = gaussianRID.names();
      for (String ridCoeffName : RIDCoeffNames)
        if (ridCoeffName.startsWith("DFBETA"))
          assertTrue(coeffNames.contains(ridCoeffName.split("DFBETA_")[1]));
    } finally {
      Scope.exit();
    }
  }
}
