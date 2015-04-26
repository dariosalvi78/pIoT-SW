package pIoT.client.charts;


import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import pIoT.client.services.ChartService;
import pIoT.client.services.ChartServiceAsync;
import pIoT.shared.ChartConfiguration;
import pIoT.shared.ChartValue;
import pIoT.shared.ChartValue.Type;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DatePicker;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.LineChart;
import com.googlecode.gwt.charts.client.corechart.LineChartOptions;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.VAxis;

public class LinesChart extends DockLayoutPanel {

	private Logger logger = Logger.getLogger( LinesChart.class.getName());
	private LineChart chart;

	private final ChartServiceAsync ChartS = GWT.create(ChartService.class);

	ChartConfiguration config;

	public LinesChart(final int width, final int height, final ChartConfiguration cf) {
		super(Unit.PX);
		config = cf;

		final DecoratorPanel frame = new DecoratorPanel();
		add(frame);
		final VerticalPanel vp = new VerticalPanel();
		frame.add(vp);
		final HorizontalPanel hp = new HorizontalPanel();
		hp.setWidth("100%");
		vp.add(hp);


		ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
		chartLoader.loadApi(new Runnable() {

			@Override
			public void run() {
				logger.info("API loaded");
				final Anchor startDate = new Anchor();
				final DatePicker startDatePicker = new DatePicker();
				final PopupPanel startDatepopup = new PopupPanel(true);
				startDatepopup.add(startDatePicker);
				
				startDatePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
					public void onValueChange(ValueChangeEvent<Date> event) {
						Date date = event.getValue();
						String dateString = DateTimeFormat.getMediumDateFormat().format(date);
						cf.setStartDate(date);
						startDate.setText("Start Date: "+dateString);
						startDatepopup.hide();
						draw();
					}
				});
				if(cf.getStartDate()!= null)
					startDatePicker.setValue(cf.getStartDate(), true);
				else startDatePicker.setValue(new Date(), true);
				
				startDate.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						startDatepopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
							public void setPosition(int offsetWidth, int offsetHeight) {
								int left = startDate.getAbsoluteLeft();
								int top = startDate.getAbsoluteTop();
								startDatepopup.setPopupPosition(left, top);
							}
						});
					}
				});
				hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
				hp.add(startDate);

				final Anchor endDate = new Anchor();
				final DatePicker endDatePicker = new DatePicker();
				final PopupPanel endDatepopup = new PopupPanel(true);
				endDatepopup.add(endDatePicker);
				
				endDatePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
					public void onValueChange(ValueChangeEvent<Date> event) {
						Date date = event.getValue();
						String dateString = DateTimeFormat.getMediumDateFormat().format(date);
						cf.setEndDate(date);
						endDate.setText("End Date: "+dateString);
						endDatepopup.hide();
						draw();
					}
				});
				if(cf.getEndDate() != null)
					endDatePicker.setValue(cf.getEndDate(), true);
				else endDatePicker.setValue(new Date(), true);
				
				endDate.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						endDatepopup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
							public void setPosition(int offsetWidth, int offsetHeight) {
								int left = endDate.getAbsoluteLeft();
								int top = endDate.getAbsoluteTop();
								endDatepopup.setPopupPosition(left, top);
							}
						});
					}
				});
				hp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
				hp.add(endDate);


				// Create and attach the chart
				chart = new LineChart();
				chart.setWidth((width-10)+"px");
				chart.setHeight((height-30)+"px");
				vp.add(chart);
				draw();
			}
		});
	}

	private void draw()  {
		// Prepare the data
		final DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.DATETIME, "Date & Time");
		ChartS.getData(config, new AsyncCallback<LinkedHashMap<Long,ArrayList<ChartValue>>>() {

			@Override
			public void onSuccess(LinkedHashMap<Long, ArrayList<ChartValue>> result) {
				logger.finer("Got data to plot "+result.size());
				if(result.size() == 0)
					return;
				
				final ArrayList<ChartValue> samples = result.get( result.keySet().iterator().next() );
				for(int k=0; k<samples.size(); k++){
					if(samples.get(k).getType() == Type.number)
						dataTable.addColumn(ColumnType.NUMBER, config.getDatasets().get(k).getPropertyName());
					if(samples.get(k).getType() == Type.string)
						dataTable.addColumn(ColumnType.STRING, config.getDatasets().get(k).getPropertyName());
					if(samples.get(k).getType() == Type.date)
						dataTable.addColumn(ColumnType.DATETIME, config.getDatasets().get(k).getPropertyName());
					if(samples.get(k).getType() == Type.bool)
						dataTable.addColumn(ColumnType.BOOLEAN, config.getDatasets().get(k).getPropertyName());
				}
				
				dataTable.addRows(result.size());
				int i=0;
				for(Long ts: result.keySet()){
					dataTable.setValue(i, 0, new Date(ts));
					ArrayList<ChartValue> obs = result.get(ts);
					for(int j=0;j<obs.size(); j++){
						ChartValue val = obs.get(j);
						if(val.isNull())
							dataTable.setValueNull(i, j+1);
						else{
							if(val.getType() == Type.number){
								dataTable.setCell(i, j+1,val.getNumber());
							}
							if(val.getType() == Type.bool){
								dataTable.setCell(i, j+1,val.getBoolean());
							}
							if(val.getType() == Type.string){
								dataTable.setCell(i, j+1,val.getString());
							}
							if(val.getType() == Type.date){
								dataTable.setCell(i, j+1,val.getDateTime());
							}
						}
					}
					i++;
				}
				
				// Set options
				LineChartOptions options = LineChartOptions.create();
				options.setTitle(config.getTitle());
				options.setHAxis(HAxis.create("Date & Time"));
				options.setVAxis(VAxis.create(config.getValuesName()));
				// Draw the chart
				chart.draw(dataTable, options);
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed "+caught.getMessage());
			}
		});

	}

}