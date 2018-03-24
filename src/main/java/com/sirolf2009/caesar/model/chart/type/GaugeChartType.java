package com.sirolf2009.caesar.model.chart.type;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.serializer.CaesarSerializer;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;

import java.util.function.Predicate;

@DefaultSerializer(GaugeChartType.GaugeChartTypeSerializer.class)
public class GaugeChartType implements IChartType {

    private static Predicate<Chart> has1Col = chart -> chart.getColumns().count() == 1;
    private static Predicate<Chart> has1Row = chart -> chart.getRows().count() == 1;
    @Override public Predicate<Chart> getPredicate() {
        return has1Col.and(areColumnsNumbers).and(has1Row).and(areRowsNumbers);
    }

    @Override public Node getChart(Chart chart) {
        Gauge gauge = GaugeBuilder.create().skinType(Gauge.SkinType.DASHBOARD).build();
        ObservableList<Number> column = (ObservableList<Number>) chart.getColumns().findAny().get().getSeries().get();
        column.addListener((InvalidationListener) e -> gauge.setMaxValue(column.get(column.size()-1).doubleValue()));
        ObservableList<Number> row = (ObservableList<Number>) chart.getRows().findAny().get().getSeries().get();
        row.addListener((InvalidationListener) e -> gauge.setValue(row.get(row.size()-1).doubleValue()));
        return gauge;
    }

    @Override public String getName() {
        return "Gauge";
    }

    public static class GaugeChartTypeSerializer extends CaesarSerializer<GaugeChartType> {

        @Override
        public void write(Kryo kryo, Output output, GaugeChartType object) {
        }

        @Override
        public GaugeChartType read(Kryo kryo, Input input, Class<GaugeChartType> type) {
            return new GaugeChartType();
        }
    }

}
