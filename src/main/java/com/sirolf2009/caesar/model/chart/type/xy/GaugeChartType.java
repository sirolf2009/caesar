package com.sirolf2009.caesar.model.chart.type.xy;

import com.dooapp.fxform.FXForm;
import com.dooapp.fxform.annotation.NonVisual;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sirolf2009.caesar.model.Chart;
import com.sirolf2009.caesar.model.chart.series.ISeries;
import com.sirolf2009.caesar.model.chart.type.IChartType;
import com.sirolf2009.caesar.model.chart.type.IChartTypeSetup;
import com.sirolf2009.caesar.model.serializer.CaesarSerializer;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
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

    @Override public IChartTypeSetup getSetup(Chart chart) {
        return new GaugeChartTypeSetup(chart, FXCollections.observableArrayList());
    }

    public static class GaugeChartTypeSetup extends AbstractComparisonChartSetup<Number, Number, GaugeSeries> {

        public GaugeChartTypeSetup(Chart chart, ObservableList<GaugeSeries> series) {
            super(chart, series);
        }

        @Override protected GaugeSeries createContainer(ISeries<Number> x, ISeries<Number> y) {
            return new GaugeSeries(y, x, new SimpleObjectProperty<>(Gauge.SkinType.DASHBOARD), new SimpleStringProperty(""), new SimpleStringProperty(""));
        }

        @Override public Node createChart() {
            GaugeSeries series = getGaugeSeries();
            Gauge gauge = GaugeBuilder.create().skinType(series.skinType.get()).title(series.title.get()).subTitle(series.subTitle.get()).build();
            ObservableList<Number> column = series.getMaxSeries().get();
            column.addListener((InvalidationListener) e -> gauge.setMaxValue(column.get(column.size() - 1).doubleValue()));
            ObservableList<Number> row = series.getValueSeries().get();
            row.addListener((InvalidationListener) e -> gauge.setValue(row.get(row.size()-1).doubleValue()));
            series.skinTypeProperty().addListener(e -> gauge.setSkinType(series.skinTypeProperty().get()));
            series.titleProperty().addListener(e -> gauge.setTitle(series.titleProperty().get()));
            series.subTitleProperty().addListener(e -> gauge.setSubTitle(series.subTitleProperty().get()));
            return gauge;
        }

        public GaugeSeries getGaugeSeries() {
            return getSeries().get(0);
        }

        @Override public Node createConfiguration() {
            return new FXForm<GaugeSeries>(getGaugeSeries());
        }
    }

    public static class GaugeSeries implements AbstractComparisonChartSetup.ComparisonSeries<Number, Number> {

        @NonVisual
        private final ISeries<Number> maxSeries;
        @NonVisual
        private final ISeries<Number> valueSeries;
        private final ObjectProperty<Gauge.SkinType> skinType;
        private final StringProperty title;
        private final StringProperty subTitle;

        public GaugeSeries(ISeries<Number> valueSeries, ISeries<Number> maxSeries, ObjectProperty<Gauge.SkinType> skinType, StringProperty title, StringProperty subTitle) {
            this.valueSeries = valueSeries;
            this.maxSeries = maxSeries;
            this.skinType = skinType;
            this.title = title;
            this.subTitle = subTitle;
        }

        public ISeries<Number> getValueSeries() {
            return valueSeries;
        }

        @Override public ISeries<Number> getX() {
            return valueSeries;
        }

        public ISeries<Number> getMaxSeries() {
            return maxSeries;
        }

        @Override public ISeries<Number> getY() {
            return maxSeries;
        }

        public ObjectProperty<Gauge.SkinType> skinTypeProperty() {
            return skinType;
        }

        public StringProperty titleProperty() {
            return title;
        }

        public StringProperty subTitleProperty() {
            return subTitle;
        }
    }

}
