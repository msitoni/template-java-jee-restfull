package br.com.marcenaria.client.base;

import java.util.List;

import javax.persistence.Transient;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ListWrapper<T> {
	
	@Transient
	private int currentPage;
	
	@Transient
	private int pageSize;
	
	@Transient
	private List<String> sortFields;
	
	@Transient
	private List<String> sortDirections;
	
	private int totalResults;
	private List<T> list;
}