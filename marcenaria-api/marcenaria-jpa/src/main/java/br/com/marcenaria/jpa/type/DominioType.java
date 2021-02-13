package br.com.marcenaria.jpa.type;

import lombok.Getter;

@Getter
public enum DominioType {

	CALCULO(1),
	AGRUPAMENTO(2),
	INDICADORES(3),
	CADASTROS(4),
	CONSULTAS(5),
	EXCLUSOES(6),
	ALTERACOES(7);
	
	Integer index;
	String dscAbreviadoDominio;
	String nomDominio;
	String dscDominio;
	
	DominioType(int index) {
		this.index = index;
		
		switch(index) {
		case 1:
			nomDominio = "Tipo_Operacao";
			dscAbreviadoDominio = "Cálculo";
			dscDominio = "Operacoes de calculo";
			break;
		case 2:
			nomDominio = "Tipo_Operacao";
			dscAbreviadoDominio = "Agrupamento";
			dscDominio = "Operacoes de agrupamento";
			break;
		case 3:
			nomDominio = "Tipo_Operacao";
			dscAbreviadoDominio = "Indicadores";
			dscDominio = "Operacoes de geração de indicadores";
			break;
		case 4:
			nomDominio = "Tipo_Operacao";
			dscAbreviadoDominio = "Cadastros";
			dscDominio = "Operacoes de cadastros";
			break;
		case 5:
			nomDominio = "Tipo_Operacao";
			dscAbreviadoDominio = "Consultas";
			dscDominio = "Operacoes de consultas";
			break;
		case 6:
			nomDominio = "Tipo_Operacao";
			dscAbreviadoDominio = "Exclusões";
			dscDominio = "Operacoes de exclusão";
			break;
		case 7:
			nomDominio = "Tipo_Operacao";
			dscAbreviadoDominio = "Alterações";
			dscDominio = "Operacoes de alteração";
			break;
		}
	}
}
