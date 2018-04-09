package com.sirolf2009.caesar.model.chart.type;

import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.chart.type.xy.LineChartType;
import com.sirolf2009.caesar.model.chart.type.xy.TimeseriesChartType;
import org.junit.Assert;
import org.junit.Test;

import static com.sirolf2009.caesar.SerializationTest.testCloning;

public class TimeseriesChartTypeTest extends ChartTypeTest {

	@Test public void testPredicate() {
		TimeseriesChartType type = new TimeseriesChartType();

		{
			Chart chart = new Chart("test-chart");
			Assert.assertFalse("An empty chart should not be allowed", type.getPredicate().test(chart));
		}
		{
			Chart chart = new Chart("test-chart");
			chart.getChildren().add(doubleColumn());
			Assert.assertFalse("A single double column should not be allowed", type.getPredicate().test(chart));

			chart.getChildren().add(doubleColumn());
			Assert.assertFalse("Multiple double columns should not be allowed", type.getPredicate().test(chart));

			chart.getChildren().add(doubleRow());
			Assert.assertFalse("A chart with double columns and a single double row should be allowed", type.getPredicate().test(chart));
		}
		{
			Chart chart = new Chart("test-chart");
			chart.getChildren().add(doubleRow());
			Assert.assertFalse("A single double row should not be allowed", type.getPredicate().test(chart));

			chart.getChildren().add(doubleRow());
			Assert.assertFalse("Multiple double rows should not be allowed", type.getPredicate().test(chart));

			chart.getChildren().add(dateColumn());
			Assert.assertTrue("A chart with a single date column and double rows should be allowed", type.getPredicate().test(chart));
		}
		{
			Chart chart = new Chart("test-chart");
			chart.getChildren().add(stringColumn());
			Assert.assertFalse("A single string column should not be allowed", type.getPredicate().test(chart));

			chart.getChildren().add(doubleColumn());
			Assert.assertFalse("Multiple double/string columns should not be allowed", type.getPredicate().test(chart));
		}
	}

	@Test public void testSerialization() {
		Chart chart = new Chart("test-chart");
		chart.getChildren().add(dateColumn());
		chart.getChildren().add(doubleRow());
		chart.chartTypeSetupProperty().set(new TimeseriesChartType().getSetup(chart));
		testCloning(chart, Chart.class);
	}

}
