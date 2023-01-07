package hex.glm;

import hex.DataInfo;
import water.Job;
import water.MRTask;
import water.fvec.Chunk;
import water.fvec.NewChunk;
import water.util.ArrayUtils;

import java.util.Arrays;

import static hex.glm.GLMModel.GLMParameters.Family.gaussian;

public class GenRegInfDiagnostics extends MRTask<GenRegInfDiagnostics> {
  final double[] _beta; // non-standized GLM coefficients
  final double[][] _gramInv;
  final boolean _isGaussian;  // true for gaussian, false for binomial
  final Job _j;
  final int _betaSize;
  final GLMModel.GLMParameters _parms;
  final DataInfo _dinfo;

  public GenRegInfDiagnostics(Job j, double[] beta, double[][] gramInv, GLMModel.GLMParameters parms, DataInfo dinfo) {
    _j = j;
    _beta = beta; // denormalized beta
    _betaSize = beta.length;
    _gramInv = gramInv;// denormalized gram inv
    _isGaussian = gaussian.equals(parms._family);
    _parms = parms;
    _dinfo = dinfo;
    _dinfo._normMul = null;   // make sure we read in non-standardized data
  }

  @Override
  public void map(Chunk[] chks, NewChunk[] nc) {
    if (isCancelled() || _j != null && _j.stop_requested()) return;
    double[] dfbetas = new double[_betaSize];
    double[] row2Array = new double[_betaSize];
    double[] xTimesGramInv = new double[_betaSize];
    DataInfo.Row r = _dinfo.newDenseRow();
    for (int rid = 0; rid < chks[0]._len; ++rid) {
      _dinfo.extractDenseRow(chks, rid, r);
      genDfBetasRow(r, nc, row2Array, dfbetas, xTimesGramInv);
    }

    if (_j != null)
      _j.update(1);
  }

  private void genDfBetasRow(DataInfo.Row r, NewChunk[] preds, double[] row2Array, double[] dfbetas,
                             double[] xTimesGramInv) {
    if (r.response_bad) {
      Arrays.fill(dfbetas, Double.NaN);
    } else if (r.weight == 0) {
      Arrays.fill(dfbetas, 0.0);
    } else {
      r.expandCatsPredsOnly(row2Array);  // change Row to array
      // generate diagonal 1.0/mll = 1.0/(1-hll)
      double oneOverMLL = gen1OverMLL(row2Array, xTimesGramInv);
      // generate residual
      double residual = genResidual(r);
      // generate equation 3
      genDfBetas(oneOverMLL, residual, row2Array, dfbetas);
    }
    for (int c = 0; c < _betaSize; c++) // copy dfbetas over to new chunks
      preds[c].addNum(dfbetas[c]);
  }
  
  public void genDfBetas(double oneOverMLL, double residual, double[] row2Array, double[] dfbetas) {
    double resOverMLL = oneOverMLL*residual;
    for (int index=0; index<_betaSize; index++) {
      dfbetas[index] = resOverMLL*ArrayUtils.innerProduct(row2Array, _gramInv[index]);
    }
  }
  
  public double gen1OverMLL(double[] row2Array, double[] xTimesGramInv) {
    for (int index=0; index<_betaSize; index++) {  // form X*invGram
      xTimesGramInv[index] = ArrayUtils.innerProduct(row2Array, _gramInv[index]);
    }
    return 1.0/(1.0-ArrayUtils.innerProduct(xTimesGramInv, row2Array));
  }
  
  public double genResidual(DataInfo.Row r) {
    double resp = r.response(0);
    return resp - _parms.linkInv(r.innerProduct(_beta)+r.offset);
  }
}
