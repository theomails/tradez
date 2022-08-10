package net.progressit.tradez.model;

import java.awt.Color;
import java.util.Optional;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Tile {
	public enum TileType { COLORED, NON_COLORED, JAIL, CHANCE }
	
	private final String id;
	private final String name;
	@Builder.Default
	private final Optional<String> description = Optional.empty();
	@Builder.Default
	private final Optional<Color> color = Optional.empty();
	@Builder.Default
	private final boolean buyable = true;
	@Builder.Default
	private final Optional<Integer> priceOrCharge = Optional.empty();
	@Builder.Default
	private final boolean houseAllowed = true;
	@Builder.Default
	private final Optional<Integer> baseRent = Optional.empty();
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Tile [id=").append(id).append(", \n name=").append(name).append(", \n description=")
				.append(description).append(", \n color=").append(color).append(", \n buyable=").append(buyable)
				.append(", \n priceOrCharge=").append(priceOrCharge).append(", \n houseAllowed=").append(houseAllowed)
				.append(", \n baseRent=").append(baseRent).append("]");
		return builder.toString();
	}
	
	
}
