setwd(normalizePath(dirname(R.utils::commandArgs(asValues=TRUE)$"f")))
source("../../../scripts/h2o-r-test-setup.R")

test_RID <- function() {
  fat <- h2o.importFile(locate("smalldata/glm_test/bodyfat.csv"))
  bodyfat <- as.data.frame(fat)
  rGlmBinomial <- glm(bmi ~ neck+density+hip, data=bodyfat, family=binomial())
  dfbetasGlmB <- dfbetas(rGlmBinomial)
  hGlmBinomial <- h2o.glm(x=c("neck", "density", "hip"), y="bmi", lambda=0, family="binomial", standardize=FALSE, influence="dfbetas", training_frame=fat)
  browser()
  dfbetashGlmB <- h2o.get_regression_influence_diagnostics(hGlmBinomial)
  print(dfbetashGlmB)
  
  
}

doTest("compare GLM regression influence diagnostics in GLM with R", test_RID)