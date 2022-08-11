package net.progressit.tradez;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.progressit.tradez.model.Holdings;
import net.progressit.tradez.model.Player;
import net.progressit.tradez.model.TradezData;
import net.progressit.tradez.panels.transfer.TransferPanel.TransferParty;
import net.progressit.tradez.panels.transfer.TransferPanel.TransferPartyType;
import net.progressit.tradez.panels.transfer.TransferPanel.DoTransferClick;
import net.progressit.util.CollectionsUtil;

public class TradezTransactionLogic {
	private static final Logger LOGGER = LoggerFactory.getLogger(TradezTransactionLogic.class.getName());
	
	public boolean doTransfter(TradezData data, DoTransferClick tr, Consumer<TradezData> dataSetter) {
		LOGGER.info("TRF");
		if(tr.getFrom().equals(tr.getTo())) return true;
		
		LOGGER.info("1");
		Holdings fromHoldings = getPartyHoldings(tr.getFrom(), data);
		Holdings toHoldings = getPartyHoldings(tr.getTo(), data);
		
		LOGGER.info("2");
		Map<Integer, Integer> fromBagCopy = CollectionsUtil.cloneToLinkedHashMap(fromHoldings.getCurrencyPossessionMap());
		Map<Integer, Integer> toBagCopy = CollectionsUtil.cloneToLinkedHashMap(toHoldings.getCurrencyPossessionMap());
		
		LOGGER.info("3");
		Map<Integer, Integer> sendCopy = CollectionsUtil.cloneToLinkedHashMap(tr.getSend());
		Map<Integer, Integer> receiveCopy = CollectionsUtil.cloneToLinkedHashMap(tr.getReceive());
		netOffBags(sendCopy, receiveCopy);
		LOGGER.info("Netted sendCopy: " + sendCopy);
		LOGGER.info("Netted receiveCopy: " + receiveCopy);
		
		LOGGER.info("4");
		boolean ok1 = checkFeasibility(fromBagCopy, sendCopy);
		boolean ok2 = checkFeasibility(toBagCopy, receiveCopy);
		
		LOGGER.info("5");
		if(!ok1 || !ok2) return false;
		
		LOGGER.info("Pre fromBagCopy: " + fromBagCopy);
		LOGGER.info("Pre toBagCopy: " + toBagCopy);
		moveMoney(fromBagCopy, toBagCopy, sendCopy);
		moveMoney(toBagCopy, fromBagCopy, receiveCopy);
		LOGGER.info("Post fromBagCopy: " + fromBagCopy);
		LOGGER.info("Post toBagCopy: " + toBagCopy);
		
		LOGGER.info("6");
		TradezData data1 = updateDataForParty(data, tr.getFrom(), fromBagCopy);
		TradezData data2 = updateDataForParty(data1, tr.getTo(), toBagCopy);
		
		LOGGER.info("7");
		dataSetter.accept(data2);
		return true;
	}


	Holdings getPartyHoldings(TransferParty tp, TradezData data) {
		if(tp.getType()==TransferPartyType.PLAYER) {
			return data.getPlayerHoldings().get(tp.getPlayer().get());
		}else if(tp.getType()==TransferPartyType.BANK) {
			return data.getBankHoldings();
		}else if(tp.getType()==TransferPartyType.UNCLE) {
			return data.getUncleHoldings();
		}else {
			throw new RuntimeException("Unhandled TP type");
		}
	}
	
	void netOffBags(Map<Integer, Integer> send, Map<Integer, Integer> receive) {
		Set<Integer> currency = new HashSet<>();
		currency.addAll(send.keySet());
		currency.addAll(receive.keySet());
		for(Integer cur:currency) {
			Integer sendVal = send.get(cur);
			Integer receiveVal = receive.get(cur);
			sendVal = (sendVal==null)?0:sendVal;
			receiveVal = (receiveVal==null)?0:receiveVal;
			if(sendVal<0) {
				receiveVal -= sendVal;
				sendVal = 0;
			}
			if(receiveVal<0) {
				sendVal -= receiveVal;
				receiveVal = 0;
			}
			if(sendVal>0 && receiveVal>0) {
				if(sendVal>=receiveVal) {
					sendVal -= receiveVal;
					receiveVal = 0;
				}else{
					receiveVal -= sendVal;
					sendVal = 0;
				}
			}
			send.put(cur, sendVal);
			receive.put(cur, receiveVal);
		}
	}
	
	boolean checkFeasibility(Map<Integer, Integer> holding, Map<Integer, Integer> transaction) {
		Set<Integer> currency = transaction.keySet();
		for(Integer cur: currency) {
			Integer has = holding.get(cur);
			Integer needs = transaction.get(cur);
			has = (has==null)?0:has;
			needs = (needs==null)?0:needs;
			if(needs>has) return false;
		}
		return true;
	}
	
	void moveMoney(Map<Integer, Integer> fromBag, Map<Integer, Integer> toBag, Map<Integer, Integer> transaction) {
		Set<Integer> currency = transaction.keySet();
		for(Integer cur: currency) {
			Integer from = fromBag.get(cur);
			Integer to = toBag.get(cur);
			Integer tx = transaction.get(cur);
			from = (from==null)?0:from;
			to = (to==null)?0:to;
			tx = (tx==null)?0:tx;
			from -= tx;
			to += tx;
			fromBag.put(cur, from);
			toBag.put(cur, to);
		}
	}
	
	TradezData updateDataForParty(TradezData data, TransferParty tp, Map<Integer, Integer> holdingBagCopy) {
		if(tp.getType()==TransferPartyType.PLAYER) {
			Map<Player, Holdings> playerHoldingsCopy = CollectionsUtil.cloneToHashMap(data.getPlayerHoldings());
			Holdings thisPlayerHoldings = playerHoldingsCopy.get(tp.getPlayer().get());
			Holdings thisPlayerHoldingsNew = thisPlayerHoldings.toBuilder().currencyPossessionMap(holdingBagCopy).build();
			playerHoldingsCopy.put(tp.getPlayer().get(), thisPlayerHoldingsNew);
			return data.toBuilder().playerHoldings(playerHoldingsCopy).build();
		}else if(tp.getType()==TransferPartyType.BANK) {
			return data.toBuilder().bankHoldings( 
					data.getBankHoldings().toBuilder().currencyPossessionMap(holdingBagCopy).build() 
				).build();
		}else if(tp.getType()==TransferPartyType.UNCLE) {
			return data.toBuilder().uncleHoldings( 
					data.getUncleHoldings().toBuilder().currencyPossessionMap(holdingBagCopy).build() 
				).build();
		}else {
			throw new RuntimeException("Unhandled TP type");
		}
	}
}
