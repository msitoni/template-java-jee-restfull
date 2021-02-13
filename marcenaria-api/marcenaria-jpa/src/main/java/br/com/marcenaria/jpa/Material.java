package br.com.marcenaria.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.com.marcenaria.jpa.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "Material")
public class Material  implements BaseEntity  {

	@Id
	@Column(name = "id", nullable = false, length = 11)
	private Long id;
	
	@Column(name = "Descricao", nullable = false, length = 100)
	private String descricao;
	
}
