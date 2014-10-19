/**
 * 
 */
package pIoT.client;

import java.util.ArrayList;

import pIoT.client.services.RulesService;
import pIoT.client.services.RulesServiceAsync;
import pIoT.shared.Rule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Dario Salvi
 *
 */
public class RulesViewer  extends Composite{

	private final RulesServiceAsync rules =  GWT.create(RulesService.class);

	ArrayList<String> actions = new ArrayList<String>();
	VerticalPanel mainPanel = new VerticalPanel();
	
	public RulesViewer() {
		ScrollPanel scrollable = new ScrollPanel();
		scrollable.add(mainPanel);
		mainPanel.setHeight("100%");
		mainPanel.setWidth("100%");
		Button addbutton = new Button("New");
		addbutton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				mainPanel.add(renderRule(null));
			}
		});
		mainPanel.add(addbutton);
		
		rules.getActionNames(new AsyncCallback<ArrayList<String>>() {
			@Override
			public void onSuccess(ArrayList<String> result) {
				actions = result;
				rules.getRules(new AsyncCallback<ArrayList<Rule>>() {
					@Override
					public void onSuccess(ArrayList<Rule> result) {
						for(Rule r: result){
							mainPanel.add(renderRule(r));
						}
					}
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Cannot retrieve rules\n"+caught.getMessage());
					}
				});
			}
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Cannot get action names list\n"+caught.getMessage());
			}
		});
		
		initWidget(scrollable);
	}

	private Widget renderRule(final Rule r){
		final DecoratorPanel frame = new DecoratorPanel();
		frame.setTitle("Rule Editor");
		frame.getElement().getStyle().setMargin(10, Unit.PX);
		
		VerticalPanel layout = new VerticalPanel();
		layout.setSpacing(5);
		layout.setWidth("100%");
		frame.add(layout);
		
		Label nameL = new Label("Name");
		layout.add(nameL);
		final TextBox nameT = new TextBox();
		nameT.setText(r==null? "": r.getName());
		nameT.setWidth("400px");
		layout.add(nameT);
		
		Label groupL = new Label("Group");
		layout.add(groupL);
		final TextBox groupT = new TextBox();
		groupT.setText(r==null? "": r.getNamespace());
		groupT.setWidth("400px");
		layout.add(groupT);
		
		Label descrL = new Label("Description");
		layout.add(descrL);
		final TextArea descrT = new TextArea();
		descrT.setText(r==null? "": r.getDescription());
		descrT.setWidth("400px");
		descrT.setHeight("80px");
		layout.add(descrT);
		
		Label exprL = new Label("Expression");
		layout.add(exprL);
		final TextArea exprT = new TextArea();
		exprT.setText(r==null? "": r.getExpression());
		exprT.setTitle("MVEL Expression Editor");
		exprT.setWidth("400px");
		exprT.setHeight("100px");
		layout.add(exprT);
		
		Label actionL = new Label("Action");
		layout.add(actionL);
		final ListBox actionT = new ListBox();
		for(String action : actions)
			actionT.addItem(action);
		actionT.setWidth("400px");
		layout.add(actionT);
		
		Label paramsL = new Label("Parameters");
		layout.add(paramsL);
		final TextBox paramsT = new TextBox();
		paramsT.setText(r==null? "": r.getParameters());
		paramsT.setWidth("400px");
		layout.add(paramsT);
		
		Label priorityL = new Label("Priority");
		layout.add(priorityL);
		final TextBox priorityT = new TextBox();//TODO: numeric
		priorityT.setText(""+ (r==null? "": r.getPriority()));
		priorityT.setWidth("30px");
		priorityT.setMaxLength(2);
		layout.add(priorityT);
		
		HorizontalPanel bottomLayout = new HorizontalPanel();
		Button saveB = new Button("Save");
		saveB.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Rule newrule = new Rule(nameT.getText(), 
						exprT.getText(), 
						actionT.getItemText(actionT.getSelectedIndex()),
						paramsT.getText(),
						Integer.parseInt(priorityT.getText()),
						groupT.getText(), descrT.getText());
				if(r == null){
					rules.addRule(newrule, new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							Window.alert("Rule saved");
						}
						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Cannot save rule.\n"+caught.getMessage());
						}
					});
				}else{
					rules.updateRule(r.getFullyQualifiedName(), newrule, new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							Window.alert("Rule saved");
						}
						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Cannot save rule.\n"+caught.getMessage());
						}
					});
					
				}
			}
		});
		bottomLayout.add(saveB);
		
		Button deleteB = new Button("Delete");
		deleteB.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				rules.removeRule(r, new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void result) {
						mainPanel.remove(frame);
					}
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Could not remove rule.\n"+caught.getMessage());
					}
				});
			}
		});
		bottomLayout.add(deleteB);
		layout.add(bottomLayout);
		
		return frame;
	}

}
