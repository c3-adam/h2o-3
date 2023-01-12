import sys
sys.path.insert(1,"../../../")
import h2o
from tests import pyunit_utils
from h2o.estimators.glm import H2OGeneralizedLinearEstimator

def test_RID_binomial():
    #training_data = h2o.import_file("http://h2o-public-test-data.s3.amazonaws.com/smalldata/glm_test/gamma_dispersion_factor_9_10kRows.csv")
    training_data = h2o.import_file(pyunit_utils.locate("smalldata/glm_test/vasoConstrctionRID.csv"))
    Y = 'Response'
    x = ["logVolume", "logRate"]
    model = H2OGeneralizedLinearEstimator(family='binomial', lambda_=0, compute_p_values=True, 
                                          remove_collinear_columns=True, influence="dfbetas")
    model.train(training_frame=training_data, x=x, y=Y)
    rid_frame = model.getRegressionInfluenceDiagnostics()


if __name__ == "__main__":
  pyunit_utils.standalone_test(test_RID_binomial)
else:
    test_RID_binomial()
