package com.sirolf2009.caesar.model.chart.type;

import com.sirolf2009.caesar.model.Chart;
import org.junit.Assert;
import org.junit.Test;

public class BarChartTypeTest extends ChartTypeTest {

	@Test public void testPredicate() {
		BarChartType type = new BarChartType();

		{
			Chart chart = new Chart("test-chart");
			Assert.assertFalse("An empty chart should not be allowed", type.getPredicate().test(chart));
		}
		{
			Chart chart = new Chart("test-chart");
			chart.getChildren().add(doubleColumn());
			Assert.assertTrue("A single double column should be allowed", type.getPredicate().test(chart));

			chart.getChildren().add(doubleColumn());
			Assert.assertTrue("Multiple double columns should be allowed", type.getPredicate().test(chart));

			chart.getChildren().add(doubleRow());
			Assert.assertFalse("A chart with rows should not be allowed", type.getPredicate().test(chart));
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
