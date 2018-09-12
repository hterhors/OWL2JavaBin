package de.uni.bielefeld.sc.hterhors.psink.obie.ontology.owl2javabin.enums;

public enum EAccessType {

	PRIVATE("private"), PACKAGE("protected"), PUBLIC("public"), NONE("");

	public final String value;

	private EAccessType(final String value) {
		this.value = value;
	}

}
