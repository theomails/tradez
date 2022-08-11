package net.progressit.tradez;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import net.progressit.tradez.model.Holdings;
import net.progressit.tradez.model.Player;
import net.progressit.tradez.model.TradezData;
import net.progressit.tradez.panels.transfer.TransferPanel.TransferParty;
import net.progressit.tradez.panels.transfer.TransferPanel.TransferPartyType;
import net.progressit.tradez.panels.transfer.TransferPanel.DoTransferClick;

@ExtendWith(MockitoExtension.class)
public class TradezTransactionLogicTest {
	
	private TradezTransactionLogic ttl;
	
	@Mock
	private Consumer<TradezData> dataSetter;
	
	@Captor
	private ArgumentCaptor<TradezData> dataCaptor;
	
	@BeforeEach
	public void setup() {
		ttl = new TradezTransactionLogic();
	}
	
	@Test
	public void shouldDoTransfer() {
		Player p1 = Player.builder()
				.name("P1")
				.color(Color.black)
				.build();
		Player p2 = Player.builder()
				.name("P2")
				.color(Color.red)
				.build();
		
		Holdings p1Holdings = Holdings.builder().currencyPossessionMap(Map.of(5,1,4,1,3,1,2,1,1,1)).build();
		Holdings p2Holdings = Holdings.builder().currencyPossessionMap(Map.of(5,1,4,1,3,1,2,1,1,1)).build();
		
		TradezData data = TradezData.builder()
				.allPlayers(List.of(p1, p2))
				.playerHoldings(Map.of(p1, p1Holdings, p2, p2Holdings))
				.build();
		
		Map<Integer, Integer> send = Map.of(5,1,3,1);
		Map<Integer, Integer> receive = Map.of(4,1,2,1);
		
		DoTransferClick tre = DoTransferClick.builder()
				.from(TransferParty.builder().type(TransferPartyType.PLAYER).player(Optional.of(p1)).build())
				.to(TransferParty.builder().type(TransferPartyType.PLAYER).player(Optional.of(p2)).build())
				.send(send)
				.receive(receive)
				.build();
		
		ttl.doTransfter(data, tre, dataSetter);
		
		Mockito.verify(dataSetter).accept(dataCaptor.capture());
		TradezData outData = dataCaptor.getValue();
		
		Map<Integer, Integer> outP1Cash = outData.getPlayerHoldings().get(p1).getCurrencyPossessionMap();
		Map<Integer, Integer> outP2Cash = outData.getPlayerHoldings().get(p2).getCurrencyPossessionMap();
		
		Map<Integer, Integer> outP1Expect = Map.of(5,0,4,2,3,0,2,2,1,1);
		Map<Integer, Integer> outP2Expect = Map.of(5,2,4,0,3,2,2,0,1,1);
		
		assertEquals(outP1Expect, outP1Cash);
		assertEquals(outP2Expect, outP2Cash);
		
	}
	
}
