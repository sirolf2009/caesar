package com.sirolf2009.caesar.model.chart.type;

import com.sirolf2009.caesar.model.Chart;
import org.junit.Assert;
import org.junit.Test;

import static com.sirolf2009.caesar.SerializationTest.testCloning;

public class LEDChartTypeTest extends ChartTypeTest {

	@Test public void testPredicate() {
		LEDChartType type = new LEDChartType();

		{
			Chart chart = new Chart("test-chart");
			Assert.assertFalse("An empty chart should not be allowed", type.getPredicate().test(chart));
		}
		{
			Chart chart = new Chart("test-chart");
			chart.getChildren().add(doubleColumn());
			Assert.assertFalse("A single double column should not be allowed", type.getPredicate().test(chart));

			chart.getChildren().add(longColumn());
			Assert.assertFalse("Multiple number columns should not be allowed", type.getPredicate().test(chart));

			chart.getChildren().add(doubleRow());
			Assert.assertFalse("A chart with double columns and a single double row should not be allowed", type.getPredicate().test(chart));
		}
		{
			Chart chart = new Chart("test-chart");
			chart.getChildren().add(booleanRow());
			Assert.assertTrue("A single boolean row should be allowed", type.getPredicate().test(chart));

			chart.getChildren().add(booleanRow());
			Assert.assertTrue("Multiple boolean rows should be allowed", type.getPredicate().test(chart));

			chart.getChildren().add(doubleColumn());
			Assert.assertFalse("A chart with a single double column and double rows should not be allowed", type.getPredicate().test(chart));
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
		chart.getChildren().add(doubleColumn());
		chart.getChildren().add(longRow());
		chart.chartTypeSetupProperty().set(new LEDChartType().getSetup(chart));
		testCloning(chart, Chart.class);
	}

}
