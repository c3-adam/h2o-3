package hex.glm;

import org.junit.Test;
import org.junit.runner.RunWith;
import water.TestUtil;
import water.runner.CloudSize;
import water.runner.H2ORunner;

import java.util.Random;

import static hex.glm.GLMUtils.sumGramInv;
import static org.junit.Assert.assertTrue;

@RunWith(H2ORunner.class)
@CloudSize(1)
public class GLMUtilsTest extends TestUtil {
  @Test
  public void testSumMatrixRowsCols() {
    long seed = 12345;
    int matSize = 10;
    Random randObj = new Random(seed);
    double[][] matrix = new double[matSize][matSize];
    for (int rInd=0; rInd<matSize;rInd++) { // generate symmetric matrix
      for (int cInd=rInd; cInd<matSize; cInd++) {
        matrix[rInd][cInd] = randObj.nextDouble();
        matrix[cInd][rInd] = matrix[rInd][cInd];
      }
    }
    double[] sumMatRowCols = sumGramInv(matrix);
    double[] manualSum = new double[matSize];
    for (int rInd=0; rInd<matSize; rInd++) {
      for (int cInd=0; cInd<matSize; cInd++) {
        manualSum[cInd] += matrix[rInd][cInd];
      }
    }
    // check results
    for (int index=0; index<matSize; index++)
      assertTrue(Math.abs(manualSum[index]-sumMatRowCols[index]) < 1e-6);
  }
  
}
