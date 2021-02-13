package br.com.marcenaria.jpa.base;

import java.beans.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) 
public interface BaseEntity {

	Long getId();
	void setId(Long id);
	
	@Transient
	default boolean isNew() {
		return this.getId() == null || this.getId() == 0;
	}
}
