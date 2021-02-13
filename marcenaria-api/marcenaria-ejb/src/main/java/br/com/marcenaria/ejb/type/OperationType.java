package br.com.marcenaria.ejb.type;

public enum OperationType {

	LIKE(" LIKE "),
	NOT_EQUAL(" != "),
	EQUAL(" = "),
	EQUAL_CASE_SENSITIVE(" = "),
	BETWEEN(" BETWEEN "),
	GREATER(" > "),
	GREATER_THAN(" >= "),
	LESS(" < "),
	LESS_THAN(" <= "),
	IN(" IN "),
	IS_NULL(" IS NULL ");
	
	private String operation;
	
	private OperationType(String operation) {
		this.operation = operation;
	}
	
	public String getOperation() {
		return this.operation;
	}
}