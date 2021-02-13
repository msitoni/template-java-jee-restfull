package br.com.marcenaria.ejb.base;

import lombok.Getter;

@Getter
public class OperationOR {
	
	private String[] fields;
	private OperationValue[] operations;
	
	public OperationOR(String[] fields, OperationValue[] operations) {
		this.fields = fields;
		this.operations = operations;
	}
	
	public OperationOR(String[] fields, OperationValue operationValue) {
		this.fields = fields;
		this.operations = new OperationValue[fields.length];
		
		for (int i = 0; i < this.operations.length; i++)
			this.operations[i] = operationValue;
	}
	
	public OperationOR(String field, OperationValue[] operations) {
		this.fields = new String[operations.length];
		this.operations = operations;
		
		for (int i = 0; i < this.fields.length; i++)
			this.fields[i] = field;
	}
}