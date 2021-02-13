package br.com.marcenaria.client.base;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import br.com.marcenaria.client.exception.BusinessException;
import br.com.marcenaria.jpa.base.BaseEntity;

@SuppressWarnings("rawtypes")
@Local
public interface BaseServiceEntity<T extends BaseEntity> extends BaseService {

	public T save(T t) throws BusinessException; // throws BusinessException;
	public void delete(T t); // throws BusinessException;
	public StringBuilder getSimpleHQL();
	public StringBuilder getSimpleHQL(boolean distinct);
	public T load(StringBuilder hql);
	public T load(Map<String, Object> parameters);
	public T load(StringBuilder hql, Map<String, Object> parameters);
	public T findById(Long id);
	public Long count(Map<String, Object> parameters);
	public List<T> listAll();
	public List<T> listAtivos();
	public List<T> list(Map<String, Object> parameters);
	public List<T> list(Map<String, Object> parameters, List<String> sortFields);
}
