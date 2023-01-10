setwd(normalizePath(dirname(R.utils::commandArgs(asValues=TRUE)$"f")))
source("../../../scripts/h2o-r-test-setup.R")

test_RID <- function() {
  #fat <- h2o.importFile(locate("smalldata/glm_test/bodyfat.csv"))
  bodyfat <- read.csv("/Users/wendycwong/h2o-3/smalldata/glm_test/bodyfat.csv", header=TRUE, sep=",")
  colNames <- colnames(bodyfat)
  for (colName in colNames) {
    c1 <- bodyfat[colName]
    c1 <- as.numeric(c1)
 }
 # bodyfat <- as.data.frame(fat)
  fatdata <- bodyfat[,c(1, 2, 5:11)]
  summary(fatdata[,-1])
  lm1 <- lm(pctfat.brozek ~ neck , data=fatdata)
}