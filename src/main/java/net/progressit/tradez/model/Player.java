package net.progressit.tradez.model;

import java.awt.Color;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Player {
	private final String name;
	private final Color color;
}
