package br.com.marcenaria.client.base;

import java.util.List;
import java.util.Map;

import javax.ejb.Local;

@Local
public interface BaseService<T> {

	public String getToken();
	public void setToken(String token);
	
	public T first(StringBuilder hql);
	public Object find(StringBuilder hql, Map<String, Object> parameters);
	public Long count(StringBuilder hql, Map<String, Object> parameters);
	public List<T> list(StringBuilder hql);
	public List<T> list(StringBuilder hql, Map<String, Object> parameters);
	public List<T> list(StringBuilder hql, List<String> sortFields);
	public List<T> list(StringBuilder hql, Map<String, Object> parameters, List<String> sortFields);
	public List<T> list(StringBuilder hql, Map<String, Object> parameters, List<String> sortFields, List<String> sortDirections, Integer firstResult, Integer maxResult);
}