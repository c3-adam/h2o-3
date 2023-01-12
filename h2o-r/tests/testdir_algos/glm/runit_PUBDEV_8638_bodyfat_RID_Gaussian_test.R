setwd(normalizePath(dirname(R.utils::commandArgs(asValues=TRUE)$"f")))
source("../../../scripts/h2o-r-test-setup.R")

test_RID_gaussian <- function() {
  fat <- h2o.importFile(locate("smalldata/glm_test/bodyfat.csv"))
  bodyfat <- as.data.frame(fat)
  bodyfat1 <- bodyfat[-c(1),]
  fat1 <- as.h2o(bodyfat1)
  rGlmGaussian <- glm(weight ~ neck+density+height, data=bodyfat)
  lm3 <- glm(pctfat.brozek ~ age+fatfreeweight+neck+factor(bmi), data=bodyfat)
  browser()
  summary(influence.measures(rGlmGaussian))
  dfbetasGlmG <- dfbetas(rGlmGaussian)
  dfbetasRlm3 <- dfbetas(lm3)
  glmFull <- h2o.glm(x=c("age", "neck", "fatfreeweight", "bmi"), y="pctfat.brozek", lambda=0, family="gaussian", standardize=FALSE, compute_p_values=TRUE, remove_collinear_columns=TRUE, influence="dfbetas", training_frame=fat)
  glmMinusOne <- h2o.glm(x=c("age", "neck", "fatfreeweight", "bmi"), y="pctfat.brozek", lambda=0, family="gaussian", standardize=FALSE, compute_p_values=TRUE, remove_collinear_columns=TRUE, influence="dfbetas", training_frame=fat1)
  dfbetasglmFull <- h2o.get_regression_influence_diagnostics(glmFull)
  dfbetasglmMinusOne <- h2o.get_regression_influence_diagnostics(glmMinusOne)
  print("getting done")
}

doTest("compare GLM regression influence diagnostics in GLM with R for Gaussian", test_RID_gaussian)