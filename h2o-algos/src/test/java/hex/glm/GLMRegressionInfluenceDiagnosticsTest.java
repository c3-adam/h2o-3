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
      Frame train = parseAndTrackTestFile("smalldata/gam_test/synthetic_20Cols_gaussian_20KRows.csv");
      // set cat columns
      int numCols = train.numCols();
      int enumCols = (numCols-1)/2;
      for (int cindex=0; cindex<enumCols; cindex++) {
        train.replace(cindex, train.vec(cindex).toCategoricalVec()).remove();
      }
      train.replace((10), train.vec(10).toCategoricalVec()).remove();
      DKV.put(train);
      GLMModel.GLMParameters params = new GLMModel.GLMParameters(gaussian);
      params._response_column = "C21";
      params._solver = IRLSM;
      params._train = train._key;
      params._influence = dfbetas;
      GLMModel glm = new GLM(params).trainModel().get();
      Scope.track_generic(glm);
      Frame gaussianRID = glm.getRIDFrame();
      Scope.track(gaussianRID);
      List<String> coeffNames = Arrays.stream(glm._output._coefficient_names).collect(Collectors.toList());
      String[] RIDCoeffNames = gaussianRID.names();
      for (String ridCoeffName : RIDCoeffNames)
        assertTrue(coeffNames.contains(ridCoeffName));
    } finally {
      Scope.exit();
    }
  }
}
