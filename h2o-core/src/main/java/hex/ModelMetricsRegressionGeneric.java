package hex;

import water.fvec.Frame;

public class ModelMetricsRegressionGeneric extends ModelMetricsRegression {

  public ModelMetricsRegressionGeneric(Model model, Frame frame, long nobs, double mse, double sigma, double mae, double rmsle,
                                       double meanResidualDeviance, double meanResidualDeviance2,
                                       CustomMetric customMetric, String description) {
    super(model, frame, nobs, mse, sigma, mae, rmsle, meanResidualDeviance, meanResidualDeviance2, customMetric);
    _description = description;
  }

  public ModelMetricsRegressionGeneric(Model model, Frame frame, long nobs, double mse, double sigma, double mae, double rmsle,
                                       double meanResidualDeviance, CustomMetric customMetric, String description) {
    super(model, frame, nobs, mse, sigma, mae, rmsle, meanResidualDeviance, Double.NaN, customMetric);
    _description = description;
  }
}
