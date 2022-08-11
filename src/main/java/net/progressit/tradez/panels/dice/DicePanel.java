package net.progressit.tradez.panels.dice;

import java.awt.Font;
import java.util.List;

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

public class DicePanel extends PLeafComponent<DicePanelData, DicePanelData>{
	private static final Logger LOGGER = LoggerFactory.getLogger( DicePanel.class.getName() );
	
	@Data
	public static class DiceRollClick{}
	
	@Data
	public static class MovePlayerByDiceClick{}

	private JPanel panel = new JPanel(new MigLayout("insets 10","[][grow, fill][]","[][]"));
	private JButton btnRollDice = new JButton("Roll Dice");
	private JLabel lblDiceValue = new JLabel();
	private JButton btnMovePlayer = new JButton("Move!");

	public DicePanel(PPlacers placers, EventBus globalBus) {
		super(placers, globalBus);
	}

	@Override
	protected JComponent getUiComponent() {
		return panel;
	}

	@Override
	protected void renderSelf(DicePanelData data) {
		LOGGER.info("Rendering..");
		if(data.getCurrentPlayer().isPresent()) {
			btnRollDice.setText("Roll Dice for " + data.getCurrentPlayer().get().getName());
		}else {
			btnRollDice.setText("Roll Dice");
		}
		if(data.getCurrentDiceValue().isEmpty()) {
			lblDiceValue.setText("");
			btnMovePlayer.setEnabled(false);
		}else {
			lblDiceValue.setText(""+data.getCurrentDiceValue().get());
			btnMovePlayer.setEnabled(true);
		}
	}

	@Override
	protected PLifecycleHandler getLifecycleHandler() {
		return new PSimpleLifecycleHandler() {
			@Override
			public void prePlacement() {
				panel.add(btnRollDice, "");
				panel.add(lblDiceValue, "");
				panel.add(btnMovePlayer, "");
				
				panel.setOpaque(false);
				
				lblDiceValue.setFont(lblDiceValue.getFont().deriveFont(Font.BOLD));
				
				btnRollDice.addActionListener( (e)-> DicePanel.this.post( new DiceRollClick() ) );
				btnMovePlayer.addActionListener( (e)-> DicePanel.this.post( new MovePlayerByDiceClick() ) );

			}
			@Override
			public void postProps() {
				DicePanel.this.setData(DicePanel.this.getProps());
			}
		};
	}

	@Override
	protected List<Class<?>> declareEmittedEvents() {
		return List.of(DiceRollClick.class, MovePlayerByDiceClick.class);
	}

}
