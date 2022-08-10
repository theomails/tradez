package net.progressit.tradez.playermodal;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import net.progressit.tradez.TradezMain;
import net.progressit.tradez.model.Player;

public class AddPlayerPanel extends JPanel{
	private static final long serialVersionUID = 1L;

	private TradezMain main;
	public AddPlayerPanel(TradezMain main) {
		init();
		this.main = main;
	}
	
	private JLabel lblName = new JLabel("Player name: ");
	private JTextField tfName = new JTextField();
	private JLabel lblColor = new JLabel("Token color: ");
	private JLabel lblColorDisplay = new JLabel();
	private JButton btnSubmit = new JButton("Add Player");
	
	private static final Color[] defaultColors = new Color[] {Color.pink, Color.blue, Color.red, Color.green};
	
	private void init() {
		setLayout(new MigLayout("insets 5","[]5[300::,grow, fill]20[]5[45::45,fill]20[]","[fill]"));
		
		add(lblName);
		add(tfName);
		add(lblColor);
		add(lblColorDisplay);
		add(btnSubmit);
		
		lblColorDisplay.setBorder(BorderFactory.createLineBorder(Color.gray));
		lblColorDisplay.setBackground( defaultColors[ (int) (Math.random()*3.9)] );
		lblColorDisplay.setOpaque(true);
		
		btnSubmit.addActionListener( (e)->{
			Player player = Player.builder()
					.name(tfName.getText())
					.color(lblColorDisplay.getBackground())
					.build();
			main.addPlayer(player);
		} );
	}
}
