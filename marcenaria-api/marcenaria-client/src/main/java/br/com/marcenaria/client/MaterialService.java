package br.com.marcenaria.client;

import javax.ejb.Local;

import br.com.marcenaria.client.base.BaseServiceEntity;
import br.com.marcenaria.jpa.Material;

@Local
public interface  MaterialService extends BaseServiceEntity<Material> {

}
