package it.polimi.db2.gma.entities;

public enum InputTypeValue {
	select("select"), // lowercase because it must correspond to the values in the DB. To use custom names for the enum, we should use a Converter (AttributeConverter)
	number("number"),
	text("text"),
	textarea("textarea");

	private final String name;

	InputTypeValue(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}