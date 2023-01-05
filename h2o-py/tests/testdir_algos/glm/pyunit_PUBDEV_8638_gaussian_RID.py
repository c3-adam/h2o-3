from __future__ import print_function
import sys, os
sys.path.insert(1,"../../../")
import h2o
from h2o.estimators.glm import H2OGeneralizedLinearEstimator
from tests import pyunit_utils

def test_glm_gaussian_rid():
    df = h2o.import_file(pyunit_utils.locate("smalldata/kaggle/CreditCard/creditcard_train_cat.csv"),
                         col_types={"DEFAULT_PAYMENT_NEXT_MONTH": "enum"})
    glm_beta = H2OGeneralizedLinearEstimator(model_id="beta_glm", influence="dfbetas", lambda_=0.0, compute_p_values=True,
                                             remove_collinear_columns=True)
    glm_beta.train(y="DEFAULT_PAYMENT_NEXT_MONTH", training_frame=df)


if __name__ == "__main__":
    pyunit_utils.standalone_test(test_glm_gaussian_rid)
else:
    test_glm_gaussian_rid()
