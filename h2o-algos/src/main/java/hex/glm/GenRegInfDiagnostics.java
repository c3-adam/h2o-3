package hex.glm;

import hex.DataInfo;
import water.Job;
import water.MRTask;
import water.fvec.Chunk;
import water.fvec.NewChunk;
import water.util.FrameUtils;

import java.util.Arrays;

import static hex.glm.GLMModel.GLMParameters.Family.gaussian;

public class GenRegInfDiagnostics extends MRTask<GenRegInfDiagnostics> {
  final double[] _beta; // non-standized GLM coefficients
  final double[] sumGramInv;
  final double[][] _gramInv;
  final boolean _isGaussian;  // true for gaussian, false for binomial
  final Job _j;
  final int _betaSize;
  final GLMModel.GLMParameters _parms;
  final boolean _sparse;
  final DataInfo _dinfo;

  public GenRegInfDiagnostics(Job j, double[] beta, double[] sumGI, double[][] gramInv, GLMModel.GLMParameters parms, DataInfo dinfo) {
    _j = j;
    _beta = beta;
    _betaSize = beta.length;
    sumGramInv = sumGI;
    _gramInv = gramInv;
    _isGaussian = gaussian.equals(parms._family);
    _parms = parms;
    _sparse = FrameUtils.sparseRatio(dinfo._adaptedFrame) < 0.5;
    _dinfo = dinfo;
  }

  @Override
  public void map(Chunk[] chks, NewChunk[] nc) {
    if (isCancelled() || _j != null && _j.stop_requested()) return;
    double[] dfbetas = new double[_betaSize];
    double[] row2Array = new double[_betaSize];
    if (_sparse) {
      for (DataInfo.Row r : _dinfo.extractSparseRows(chks)) {
        genDfBetasRow(r, nc, row2Array, dfbetas);
      }
    } else {
      DataInfo.Row r = _dinfo.newDenseRow();
      for (int rid = 0; rid < chks[0]._len; ++rid) {
        _dinfo.extractDenseRow(chks, rid, r);
        genDfBetasRow(r, nc, row2Array, dfbetas);
      }
    }

    if (_j != null)
      _j.update(1);
  }

  private void genDfBetasRow(DataInfo.Row r, NewChunk[] preds, double[] row2Array, double[] dfbetas) {
    if (r.response_bad) {
      Arrays.fill(dfbetas, Double.NaN);
    } else {
      r.expandCats(row2Array);  // change Row to array
      // generate diagonal 1.0/mll = 1.0/(1-hll)
      double oneOverMLL = genMLL(r, dfbetas);
      // generate residual
      // generate equation 3
    }
    for (int c=0; c<_betaSize; c++) // copy dfbetas over to new chunks
      preds[c].addNum(dfbetas[c]);
  }
  
  public double genMLL(DataInfo.Row r, double[] dfbetas) {
    return 0.0;
  }
}
