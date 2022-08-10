package net.progressit.tradez.panels.chance;

import java.awt.Color;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;

import lombok.Data;
import net.miginfocom.swing.MigLayout;
import net.progressit.progressive.PLeafComponent;
import net.progressit.progressive.PLifecycleHandler;
import net.progressit.progressive.PPlacers;
import net.progressit.progressive.helpers.PSimpleLifecycleHandler;

public class ChancePanel extends PLeafComponent<ChancePanelData, ChancePanelData>{
	private static final Logger LOGGER = LoggerFactory.getLogger( ChancePanel.class.getName() );
	
	@Data
	public static class ChancePickCardEvent{}
	
	@Data
	public static class ChanceClearCurrentCardEvent{}

	private JPanel panel = new JPanel(new MigLayout("insets 10","[fill][grow, fill][]","[][]"));
	private JLabel lblAvailableTitle = new JLabel("Available cards #:");
	private JLabel lblAvailableCount = new JLabel("0");
	private JLabel lblCurrentChanceMsg = new JLabel();
	private JButton btnPickCard = new JButton("Pick a Card");
	private JButton btnClose = new JButton("Clear");

	public ChancePanel(PPlacers placers, EventBus globalBus) {
		super(placers, globalBus);
	}

	@Override
	protected JComponent getUiComponent() {
		return panel;
	}

	@Override
	protected void renderSelf(ChancePanelData data) {
		LOGGER.info("Rendering..");
		lblAvailableCount.setText(""+data.getAvailableChanceCards().size());
		lblCurrentChanceMsg.setText( data.getCurrentChanceCard().orElse("") );
		if(data.getCurrentChanceCard().isEmpty()) {
			lblCurrentChanceMsg.setVisible(false);
			btnClose.setVisible(false);
		}else {
			lblCurrentChanceMsg.setVisible(true);
			btnClose.setVisible(true);
		}
	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return new PSimpleLifecycleHandler() {
			@Override
			public void prePlacement() {
				panel.add(lblAvailableTitle, "");
				panel.add(lblAvailableCount, "");
				panel.add(btnPickCard, "wrap");
				
				panel.add(lblCurrentChanceMsg, "spanx 2, hidemode 3");
				panel.add(btnClose, "hidemode 3");
				
				panel.setOpaque(false);
				panel.setBorder(BorderFactory.createTitledBorder("Chance"));
				
				lblCurrentChanceMsg.setBorder(
						BorderFactory.createCompoundBorder(
								BorderFactory.createLineBorder(Color.gray),
								BorderFactory.createEmptyBorder(10, 10, 10, 10)
								)
						);
				lblCurrentChanceMsg.setOpaque(true);
				lblCurrentChanceMsg.setBackground(Color.white);
				
				btnPickCard.addActionListener( (e)-> ChancePanel.this.post( new ChancePickCardEvent() ) );
				btnClose.addActionListener( (e)-> ChancePanel.this.post( new ChanceClearCurrentCardEvent() ) );

			}
			@Override
			public void postProps() {
				ChancePanel.this.setData(ChancePanel.this.getProps());
			}
		};
	}

	@Override
	protected List<Class<?>> declareEmittedEvents() {
		return List.of(ChancePickCardEvent.class, ChanceClearCurrentCardEvent.class);
	}

}
