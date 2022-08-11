package net.progressit.tradez.panels.log;

import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import net.miginfocom.swing.MigLayout;
import net.progressit.progressive.PLeafComponent;
import net.progressit.progressive.PLifecycleHandler;
import net.progressit.progressive.PPlacers;
import net.progressit.progressive.helpers.PSimpleLifecycleHandler;
import net.progressit.tradez.TradezLogic.LogEvent;
import net.progressit.util.CollectionsUtil;

public class LogPanel extends PLeafComponent<LogPanelData, LogPanelData>{
	private static final Logger LOGGER = LoggerFactory.getLogger( LogPanel.class.getName() );
	

	private JPanel panel = new JPanel(new MigLayout("insets 10","[grow, fill]","[grow, fill]"));
	private JList<String> lstLogs = new JList<>();
	private JScrollPane spLogs = new JScrollPane(lstLogs);
	private final LogStringer stringer = LogStringer.getInstance();
	public LogPanel(PPlacers placers, EventBus globalBus) {
		super(placers, globalBus);
	}

	@Override
	protected JComponent getUiComponent() {
		return panel;
	}

	@Override
	protected void renderSelf(LogPanelData data) {
		LOGGER.info("Rendering..");
		data = ensureBaseData(data);
		List<String> logStrings = data.getLoggedEvents().stream().map( (le) -> stringer.toString(le.getOriginalEvent())).collect(Collectors.toList());
		lstLogs.setListData(logStrings.toArray(new String[] {}));
	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return new PSimpleLifecycleHandler() {
			@Override
			public void prePlacement() {
				panel.add(spLogs, "");
				panel.setOpaque(false);
				panel.setBorder(BorderFactory.createTitledBorder("Game progress:"));
				
				getGlobalBus().register(LogPanel.this);
			}
			@Override
			public void postProps() {
				LogPanelData data = LogPanel.this.getData();
				data = data==null?LogPanelData.builder().build():data;
				LogPanel.this.setData(data); //Ignore props
			}
		};
	}
	
	@Subscribe
	public void handle(LogEvent le) {
		LOGGER.info("GOT A LOG");
		LogPanelData data = ensureBaseData(getData());
		List<LogEvent> loggedEvents = CollectionsUtil.cloneToArrayList(data.getLoggedEvents());
		loggedEvents.add(0, le);
		setData(getData().toBuilder().loggedEvents(loggedEvents).build());
	}
	
	private LogPanelData ensureBaseData(LogPanelData data) {
		List<LogEvent> loggedEvents = data.getLoggedEvents();
		if(loggedEvents==null) {
			data = data.toBuilder().loggedEvents(List.of()).build();
		}
		return data;
	}

	@Override
	protected List<Class<?>> declareEmittedEvents() {
		return List.of();
	}

}
