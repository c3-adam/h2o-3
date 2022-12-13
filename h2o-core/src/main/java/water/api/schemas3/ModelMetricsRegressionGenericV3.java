package water.api.schemas3;

import hex.ModelMetricsRegressionGeneric;
import water.api.API;

public class ModelMetricsRegressionGenericV3<I extends ModelMetricsRegressionGeneric, S extends ModelMetricsRegressionGenericV3<I, S>> extends ModelMetricsBaseV3<I, S> {

  @API(help="The mean residual deviance for this scoring run.", direction=API.Direction.OUTPUT)
  public double mean_residual_deviance;

  @API(help="The mean residual deviance for this scoring run - calculated by the full non-optimized formula.", direction=API.Direction.OUTPUT)
  public double mean_residual_deviance_2;

  @API(help="The mean absolute error for this scoring run.", direction=API.Direction.OUTPUT)
  public double mae;

  @API(help="The root mean squared log error for this scoring run.", direction=API.Direction.OUTPUT)
  public double rmsle;

  @Override
  public S fillFromImpl(I modelMetrics) {
    super.fillFromImpl(modelMetrics);
    mae = modelMetrics._mean_absolute_error;
    rmsle = modelMetrics._root_mean_squared_log_error;
    mean_residual_deviance = modelMetrics._mean_residual_deviance;
    mean_residual_deviance_2 = modelMetrics._mean_residual_deviance_2;
    return (S) this;
  }
}
