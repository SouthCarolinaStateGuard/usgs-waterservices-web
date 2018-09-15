/*
 * Copyright 2018 South Carolina State Guard.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.sc.sg.engineering.hydro.web;

import gov.sc.sg.engineering.hydro.LatLonPointType;
import gov.sc.sg.engineering.hydro.SiteInfoType;
import gov.sc.sg.engineering.hydro.SourceInfoType;
import gov.sc.sg.engineering.hydro.TimeSeriesResponseType;
import gov.sc.sg.engineering.hydro.TimeSeriesType;
import gov.sc.sg.engineering.hydro.TsValuesSingleVariableType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.primefaces.model.chart.MeterGaugeChartModel;

@ManagedBean
public class ChartView implements Serializable {

    private static final long serialVersionUID = -5968536550292971322L;

    private MeterGaugeChartModel meterGaugeModel1;
    private MeterGaugeChartModel meterGaugeModel2;
    private final List<MeterGaugeChartModel> models;

    public ChartView() {
        models = new ArrayList<>(9);
    }

    @PostConstruct
    public void init() {
        createMeterGaugeModels();
        models.add(generateModel());
    }

    public MeterGaugeChartModel getMeterGaugeModel1() {
        return meterGaugeModel1;
    }

    public MeterGaugeChartModel getMeterGaugeModel2() {
        return meterGaugeModel2;
    }

    private MeterGaugeChartModel initMeterGaugeModel() {
        List<Number> intervals = new ArrayList<Number>() {
            private static final long serialVersionUID = 3109256773218160485L;

            {
                add(8);
                add(9);
                add(11);
                add(12);
            }
        };

        return new MeterGaugeChartModel(12, intervals);
    }

    private void createMeterGaugeModels() {
        meterGaugeModel1 = initMeterGaugeModel();
        meterGaugeModel1.setTitle("Flood Gauge");
        meterGaugeModel1.setSeriesColors("FFFF00,FF9900,FF0000,CC33FF");
        meterGaugeModel1.setGaugeLabel("ft.");

        meterGaugeModel2 = initMeterGaugeModel();
        meterGaugeModel2.setTitle("Custom Options");
        // meterGaugeModel2.setSeriesColors("66cc66,93b75f,E7E658,cc6666");
        meterGaugeModel2.setSeriesColors("FFFF00,FF9900,FFFF00,CC33FF");
        meterGaugeModel2.setGaugeLabel("ft");
        meterGaugeModel2.setGaugeLabelPosition("bottom");
        meterGaugeModel2.setShowTickLabels(false);
        meterGaugeModel2.setLabelHeightAdjust(110);
        meterGaugeModel2.setIntervalOuterRadius(100);
    }

    private MeterGaugeChartModel generateModel() {
        Map<String, String> map = getData();
        MeterGaugeChartModel model = new MeterGaugeChartModel();
        model.setSeriesColors("66CC66,FFFF00,FF9900,FF0000,CC33FF");
        model.setIntervals(new ArrayList<Number>() {
            private static final long serialVersionUID = 3109256773218160485L;

            {
                add(8);
                add(9);
                add(11);
                add(12);
                add(15);
            }
        });
        model.setGaugeLabel(map.get("unitCode"));
        model.setTitle(map.get("siteName"));
        double value = Double.valueOf(map.get("value"));
        model.setValue(value);
        model.setMax(15);
        return model;
    }

    private Map<String, String> getData() {
        Map<String, String> map = new TreeMap<>();
        TimeSeriesResponseType tsrt = getJson("02135000");
        TimeSeriesType tst = tsrt.getTimeSeries().get(0);
        SourceInfoType sit = tst.getSourceInfo();

        if (sit instanceof SiteInfoType) {
            SiteInfoType sif = (SiteInfoType) sit;
            String siteCode = sif.getSiteCode().get(0).getValue();
            map.put("siteCode", siteCode);
            map.put("siteName", sif.getSiteName());
            SiteInfoType.GeoLocation gl = sif.getGeoLocation();
            if (gl.getGeogLocation() instanceof LatLonPointType) {
                LatLonPointType lls = (LatLonPointType) (gl.getGeogLocation());
                map.put("latitude", String.valueOf(lls.getLatitude()));
                map.put("longitude", String.valueOf(lls.getLongitude()));
            }
        }
        map.put("unitCode", tst.getVariable().getUnit().getUnitCode());
        TsValuesSingleVariableType tvsvt = tst.getValues().get(0);
        map.put("value", tvsvt.getValue().get(0).getValue().toPlainString());
        map.put("date", tvsvt.getValue().get(0).getDateTime().toString());
        return map;
    }

    private TimeSeriesResponseType getJson(final String site) {
        Client client = ClientBuilder.newClient()
                .register(MoxyJsonFeature.class);
        WebTarget target = client.target("https://waterservices.usgs.gov")
                .path("/nwis/iv")
                .queryParam("site", site)
                .queryParam("parameterCd", "00065")
                .queryParam("siteStatus", "active");
        return target.request(MediaType.APPLICATION_JSON_TYPE).get(TimeSeriesResponseType.class);
    }

    public List<MeterGaugeChartModel> getModels() {
        return Collections.unmodifiableList(models);
    }

}
