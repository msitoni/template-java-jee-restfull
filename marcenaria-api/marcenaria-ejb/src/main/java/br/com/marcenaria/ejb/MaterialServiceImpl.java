package br.com.marcenaria.ejb;

import javax.ejb.Stateless;

import br.com.marcenaria.client.MaterialService;
import br.com.marcenaria.ejb.base.BaseServiceEntityImpl;
import br.com.marcenaria.jpa.Material;

@Stateless
public class MaterialServiceImpl  extends BaseServiceEntityImpl<Material> implements MaterialService {

}
