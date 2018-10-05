package de.hterhors.obie.tools.owl2javabin.enums;

public enum EAccessType {

	PRIVATE("private"), PACKAGE("protected"), PUBLIC("public"), NONE("");

	public final String value;

	private EAccessType(final String value) {
		this.value = value;
	}

}
