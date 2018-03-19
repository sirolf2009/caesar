package com.sirolf2009.caesar.model.chart.type;

import com.sirolf2009.caesar.model.Chart;
import org.junit.Assert;
import org.junit.Test;

public class LineChartTypeTest extends ChartTypeTest {

	@Test public void testPredicate() {
		LineChartType type = new LineChartType();

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
			Assert.assertTrue("A chart with double columns and a single double row should be allowed", type.getPredicate().test(chart));
		}
		{
			Chart chart = new Chart("test-chart");
			chart.getChildren().add(doubleRow());
			Assert.assertFalse("A single double row should not be allowed", type.getPredicate().test(chart));

			chart.getChildren().add(doubleRow());
			Assert.assertFalse("Multiple double rows should not be allowed", type.getPredicate().test(chart));

			chart.getChildren().add(doubleRow());
			Assert.assertTrue("A chart with a single double column and double rows should be allowed", type.getPredicate().test(chart));
		}
		{
			Chart chart = new Chart("test-chart");
			chart.getChildren().add(stringColumn());
			Assert.assertFalse("A single string column should not be allowed", type.getPredicate().test(chart));

			chart.getChildren().add(doubleColumn());
			Assert.assertFalse("Multiple double/string columns should not be allowed", type.getPredicate().test(chart));
		}
	}

}
