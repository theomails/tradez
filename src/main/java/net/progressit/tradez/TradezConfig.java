package net.progressit.tradez;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.progressit.tradez.model.Holdings;
import net.progressit.tradez.model.Player;
import net.progressit.tradez.model.Tile;

public class TradezConfig {
	
	private static Color DARK_BLUE = new Color(0x3A3270);
	private static Color SUBTLE_BLUE = new Color(0x98B7B8);
	private static Color PINK = new Color(0xB32964);
	private static Color ORANGE = new Color(0xBE703A);
	private static Color RED = new Color(0xB32315);
	private static Color YELLOW = new Color(0xBAB433);
	private static Color GREEN = new Color(0x0E6A1E);
	private static Color BLUE = new Color(0x1E4978);

	public static void main(String[] args){
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		System.out.println(g.toJson(getAllTiles()));
	}
	
	public static List<Player> getDefaultPlayers(){
		return List.of(
				Player.builder()
					.name("Rene")
					.color(Color.pink)
					.build(),
				Player.builder()
					.name("Daddy")
					.color(Color.cyan)
					.build()
			);
	}
	
	public static List<String> getAllChanceMessages(){
		List<String> chanceMessages = new ArrayList<>();
		chanceMessages.addAll(List.of(
				"Free Ticket Booth Purple",
				"Free Ticket Booth Purple",
				"Free Ticket Booth Light Blue",
				"Free Ticket Booth Light Blue",
				"Free Ticket Booth Pink",
				"Free Ticket Booth Pink",
				"Free Ticket Booth Orange",
				"Free Ticket Booth Orange",
				"Free Ticket Booth Red",
				"Free Ticket Booth Red",
				"Free Ticket Booth Yellow",
				"Free Ticket Booth Yellow",
				"Free Ticket Booth Green",
				"Free Ticket Booth Green",
				"Free Ticket Booth Blue",
				"Free Ticket Booth Blue",
				"Take a ride on the Yellow Line and Roll Again",
				"Take a ride on the Green Line and Roll Again",
				"Take a ride on the Blue Line and Roll Again",
				"Take a ride on the Red Line and Roll Again",
				"Go to the Fireworks and Pay $2",
				"Go to the Water Show and Pay $2",
				"Go to the Fireworks and Pay $2",
				"Go to the Water Show and Pay $2"
			));
		Collections.shuffle(chanceMessages);
		return chanceMessages;
	}
	
	public static Holdings initialHoldingsForPlayer() {
		Map<Integer, Integer> currencyPossessionMap = new LinkedHashMap<>();
		currencyPossessionMap.put(5, 1);
		currencyPossessionMap.put(4, 1);
		currencyPossessionMap.put(3, 3);
		currencyPossessionMap.put(2, 4);
		currencyPossessionMap.put(1, 5);
		return Holdings.builder()
				.currencyPossessionMap(currencyPossessionMap)
				.build();
	}
	
	public static Holdings initialHoldingsForBank() {
		Map<Integer, Integer> currencyPossessionMap = new LinkedHashMap<>();
		currencyPossessionMap.put(5, 20);
		currencyPossessionMap.put(4, 20);
		currencyPossessionMap.put(3, 20);
		currencyPossessionMap.put(2, 20);
		currencyPossessionMap.put(1, 20);
		return Holdings.builder()
				.currencyPossessionMap(currencyPossessionMap)
				.build();
	}
	
	public static Holdings initialHoldingsForUncle() {
		Map<Integer, Integer> currencyPossessionMap = new LinkedHashMap<>();
		currencyPossessionMap.put(5, 0);
		currencyPossessionMap.put(4, 0);
		currencyPossessionMap.put(3, 0);
		currencyPossessionMap.put(2, 0);
		currencyPossessionMap.put(1, 0);
		return Holdings.builder()
				.currencyPossessionMap(currencyPossessionMap)
				.build();
	}
	
	public static List<Tile> getAllTiles(){
		List<Tile> result = new ArrayList<>();
		
		//BOTTOM Right to Left
		result.add(Tile.builder()
				.id("Start")
				.name("START")
				.description(Optional.of("Collect $2 pocket money as you pass!"))
				.buyable(false)
				.houseAllowed(false)
				.build());
		
		result.add(Tile.builder()
				.id("Chance1")
				.name("CHANCE")
				.description(Optional.of("Pick a card!"))
				.buyable(false)
				.houseAllowed(false)
				.build());
		
		result.add(Tile.builder()
				.id("Baloon Stand")
				.name("Baloon Stand")
				.color(Optional.of(DARK_BLUE))
				.buyable(true)
				.priceOrCharge(Optional.of(1))
				.baseRent(Optional.of(1))
				.houseAllowed(true)
				.build());
		
		result.add(Tile.builder()
				.id("Candy Floss")
				.name("Candy Floss")
				.color(Optional.of(DARK_BLUE))
				.buyable(true)
				.priceOrCharge(Optional.of(1))
				.baseRent(Optional.of(1))
				.houseAllowed(true)
				.build());
		
		result.add(Tile.builder()
				.id("Chance2")
				.name("CHANCE")
				.description(Optional.of("Pick a card!"))
				.buyable(false)
				.houseAllowed(false)
				.build());
		
		result.add(Tile.builder()
				.id("Yellow1")
				.name("Yellow Line Railway")
				.description(Optional.of("Roll Again!"))
				.buyable(false)
				.houseAllowed(false)
				.build());
		
		result.add(Tile.builder()
				.id("Puppet Show")
				.name("Puppet Show")
				.color(Optional.of(SUBTLE_BLUE))
				.buyable(true)
				.priceOrCharge(Optional.of(2))
				.baseRent(Optional.of(1))
				.houseAllowed(true)
				.build());
		
		result.add(Tile.builder()
				.id("Magic Show")
				.name("Magic Show")
				.color(Optional.of(SUBTLE_BLUE))
				.buyable(true)
				.priceOrCharge(Optional.of(2))
				.baseRent(Optional.of(1))
				.houseAllowed(true)
				.build());
		
		result.add(Tile.builder()
				.id("Fireworks1")
				.name("Fireworks")
				.description(Optional.of("Pay $2 to see the Fireworks!"))
				.buyable(false)
				.houseAllowed(false)
				.build());
		
		result.add(Tile.builder()
				.id("Chance3")
				.name("CHANCE")
				.description(Optional.of("Pick a card!"))
				.buyable(false)
				.houseAllowed(false)
				.build());
		
		result.add(Tile.builder()
				.id("Cafe1")
				.name("Café")
				.description(Optional.of("Just hangout!"))
				.buyable(false)
				.houseAllowed(false)
				.build());
		
		//LEFT column up
		result.add(Tile.builder()
				.id("Merry-go-Round")
				.name("Merry-go-Round")
				.color(Optional.of(PINK))
				.buyable(true)
				.priceOrCharge(Optional.of(2))
				.baseRent(Optional.of(1))
				.houseAllowed(true)
				.build());
		
		result.add(Tile.builder()
				.id("Paddle Boats")
				.name("Paddle Boats")
				.color(Optional.of(PINK))
				.buyable(true)
				.priceOrCharge(Optional.of(2))
				.baseRent(Optional.of(1))
				.houseAllowed(true)
				.build());
		
		result.add(Tile.builder()
				.id("Green1")
				.name("Green Line Railway")
				.description(Optional.of("Roll Again!"))
				.buyable(false)
				.houseAllowed(false)
				.build());
		
		result.add(Tile.builder()
				.id("Water Chute")
				.name("Water Chute")
				.color(Optional.of(ORANGE))
				.buyable(true)
				.priceOrCharge(Optional.of(3))
				.baseRent(Optional.of(2))
				.houseAllowed(true)
				.build());
		
		result.add(Tile.builder()
				.id("Mini Golf")
				.name("Mini Golf")
				.color(Optional.of(ORANGE))
				.buyable(true)
				.priceOrCharge(Optional.of(3))
				.baseRent(Optional.of(2))
				.houseAllowed(true)
				.build());
		
		result.add(Tile.builder()
				.id("Uncle Pennybag")
				.name("Uncle Pennybag's Loose change")
				.description(Optional.of("Collect shows and tram money!"))
				.buyable(false)
				.houseAllowed(false)
				.build());
		
		//TOP Left to Right
		result.add(Tile.builder()
				.id("Chance4")
				.name("CHANCE")
				.description(Optional.of("Pick a card!"))
				.buyable(false)
				.houseAllowed(false)
				.build());
		
		result.add(Tile.builder()
				.id("Video Arcade")
				.name("Video Arcade")
				.color(Optional.of(RED))
				.buyable(true)
				.priceOrCharge(Optional.of(3))
				.baseRent(Optional.of(2))
				.houseAllowed(true)
				.build());
		
		result.add(Tile.builder()
				.id("Haunted House")
				.name("Haunted House")
				.color(Optional.of(RED))
				.buyable(true)
				.priceOrCharge(Optional.of(3))
				.baseRent(Optional.of(2))
				.houseAllowed(true)
				.build());
		
		result.add(Tile.builder()
				.id("Chance5")
				.name("CHANCE")
				.description(Optional.of("Pick a card!"))
				.buyable(false)
				.houseAllowed(false)
				.build());
		
		result.add(Tile.builder()
				.id("Blue1")
				.name("Blue Line Railway")
				.description(Optional.of("Roll Again!"))
				.buyable(false)
				.houseAllowed(false)
				.build());
		
		result.add(Tile.builder()
				.id("Helicopter Ride")
				.name("Helicopter Ride")
				.color(Optional.of(YELLOW))
				.buyable(true)
				.priceOrCharge(Optional.of(4))
				.baseRent(Optional.of(3))
				.houseAllowed(true)
				.build());
		
		result.add(Tile.builder()
				.id("Pony Ride")
				.name("Pony Ride")
				.color(Optional.of(YELLOW))
				.buyable(true)
				.priceOrCharge(Optional.of(4))
				.baseRent(Optional.of(3))
				.houseAllowed(true)
				.build());
		
		result.add(Tile.builder()
				.id("Water1")
				.name("Water Show")
				.description(Optional.of("Pay $2 to see the Water Show!"))
				.buyable(false)
				.houseAllowed(false)
				.build());
		
		result.add(Tile.builder()
				.id("Chance6")
				.name("CHANCE")
				.description(Optional.of("Pick a card!"))
				.buyable(false)
				.houseAllowed(false)
				.build());
		
		result.add(Tile.builder()
				.id("Tram1")
				.name("Tram Station")
				.description(Optional.of("Pay $3 to take the Tram to Café!"))
				.buyable(false)
				.houseAllowed(false)
				.build());
		
		//RIGHT Down
		result.add(Tile.builder()
				.id("Dodgems")
				.name("Dodgems")
				.color(Optional.of(GREEN))
				.buyable(true)
				.priceOrCharge(Optional.of(4))
				.baseRent(Optional.of(3))
				.houseAllowed(true)
				.build());
		
		result.add(Tile.builder()
				.id("Big Wheel")
				.name("Big Wheel")
				.color(Optional.of(GREEN))
				.buyable(true)
				.priceOrCharge(Optional.of(4))
				.baseRent(Optional.of(3))
				.houseAllowed(true)
				.build());
		
		result.add(Tile.builder()
				.id("Red1")
				.name("Red Line Railway")
				.description(Optional.of("Roll Again!"))
				.buyable(false)
				.houseAllowed(false)
				.build());
		
		result.add(Tile.builder()
				.id("Loop the Loop")
				.name("Loop the Loop")
				.color(Optional.of(BLUE))
				.buyable(true)
				.priceOrCharge(Optional.of(5))
				.baseRent(Optional.of(4))
				.houseAllowed(true)
				.build());
		
		result.add(Tile.builder()
				.id("Roller Coaster")
				.name("Roller Coaster")
				.color(Optional.of(BLUE))
				.buyable(true)
				.priceOrCharge(Optional.of(5))
				.baseRent(Optional.of(4))
				.houseAllowed(true)
				.build());
		
		return result;
	}

}
