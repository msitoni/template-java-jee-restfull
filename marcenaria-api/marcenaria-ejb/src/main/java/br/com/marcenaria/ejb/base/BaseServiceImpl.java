package br.com.marcenaria.ejb.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.marcenaria.client.base.BaseService;
import lombok.Getter;
import lombok.Setter;

@Stateless
public abstract class BaseServiceImpl<T> implements BaseService<T> {

	@PersistenceContext(unitName = "JPAEntityManager")
	protected EntityManager entityManager;

	private static final String INICIAL = "Inicial";
	private static final String FINAL = "Final";

	@Getter @Setter
	private String token;

	protected List<String> getNaturalOrders() {
		return new ArrayList<>();
	}

	private String getParameterName(String parameter) {
		return parameter.substring(parameter.lastIndexOf('.') + 1) + Integer.toString(parameter.hashCode()).replace("-", "");
	}

	protected Query parseHQL(Map<String, Object> parameters, StringBuilder hql, List<String> orders) {
		Integer firstResult = null;
		Integer maxResult = null;
		List<String> sortDirections = null;

		return this.parseHQL(parameters, hql, orders, sortDirections, firstResult, maxResult);
	}

	private Query parseHQL(Map<String, Object> parameters, StringBuilder hql, List<String> sortFields, List<String> sortDirections, Integer firstResult, Integer maxResult) {
		if (hql == null || hql.length() == 0)
			return null;

		if (parameters != null && !parameters.isEmpty())
			this.parseHQLAddParameters(hql, parameters);

		SQLUtil.addSortFieldsAndDirections(hql, sortFields, sortDirections);

		Query query = entityManager.createQuery(hql.toString());

		if (firstResult != null && maxResult != null) {
			int start = (firstResult - 1) * maxResult;

			query.setFirstResult(start);
			query.setMaxResults(maxResult.intValue());
		}

		if (parameters != null && !parameters.isEmpty())
			for (Entry<String, Object> parameter : parameters.entrySet()) {

				String key = parameter.getKey();
				Object value = parameter.getValue();

				this.setParameter(query, key, value);
			}

		return query;
	}

	private void parseHQLAddParameters(StringBuilder hql, Map<String, Object> parameters) {
		boolean hasWhere = hql.toString().toUpperCase().contains("WHERE");

		for (Entry<String, Object> parameter : parameters.entrySet()) {
			String clause;

			if (hasWhere)
				clause = " AND ";
			else {
				clause = " WHERE ";
				hasWhere = true;
			}

			String key = parameter.getKey();
			Object value = parameter.getValue();

			clause += this.getClause(key, value);

			hql.append(clause);
		}
	}

	private String getClause(String key, Object value) {
		StringBuilder clause = new StringBuilder();

		if (value instanceof OperationValue) {
			OperationValue operationValue = (OperationValue)value;
			return this.getClauseOperationValue(key, operationValue);
		} else if (value instanceof OperationOR) {
			String[] fields = ((OperationOR)value).getFields();
			OperationValue[] operations = ((OperationOR)value).getOperations();

			clause.append(" (");

			for (int i = 0; i < fields.length; i++) {
				if (i != 0)
					clause.append(" OR ");

				String keyAux = fields[i];
				Object valueAux = operations[i];

				clause.append(this.getClause(keyAux, valueAux));
			}

			clause.append(") ");
		} else {
			clause.append(key + " = :" + getParameterName(key));
		}

		return clause.toString();
	}

	private String getClauseOperationValue(String key, OperationValue operationValue) {
		switch (operationValue.getOperation()) {
			case EQUAL_CASE_SENSITIVE:
				return "UPPER(TRIM(" + key + "))" + " = UPPER(TRIM(:" + getParameterName(key) + "))";
			case LIKE:
				return "UPPER (" + key + ") LIKE UPPER(:" + getParameterName(key) + " )";
			case BETWEEN:
				return key + " BETWEEN :" + key + INICIAL + " AND :" + key + FINAL;
			case IN:
				return this.getClauseOperationValueIN(key, operationValue);
			case IS_NULL:
				return key + " " + operationValue.getOperation().getOperation();
			default:
				return key + " " + operationValue.getOperation().getOperation() +  " :" + getParameterName(key);
		}
	}

	private String getClauseOperationValueIN(String key, OperationValue operationValue) {
		StringBuilder valueAux = new StringBuilder();

		if (operationValue.getValue().getClass().isArray())
			for (Object obj : (Object[])operationValue.getValue())
				if (obj instanceof Long)
					if (valueAux.length() == 0)
						valueAux.append(obj.toString());
					else
						valueAux.append(',' + obj.toString());

		if (valueAux.length() != 0)
			return key + " IN (" + valueAux.toString() + " )";

		return null;
	}

	private void setParameter(Query query, String key, Object value) {
		if (value instanceof OperationValue) {
			OperationValue operationValue = (OperationValue)value;

			switch (operationValue.getOperation()) {
				case LIKE: {
					query.setParameter(getParameterName(key), "%" + operationValue.getValue() + "%");
					break;
				}
				case BETWEEN: {
					query.setParameter(key + INICIAL, operationValue.getValue());
					query.setParameter(key + FINAL, operationValue.getValue2());
					break;
				}
				case IN: {
					// faz nada
					break;
				}
				case IS_NULL: {
					// faz nada
					break;
				}
				default: {
					query.setParameter(getParameterName(key), operationValue.getValue());
				}
			}
		} else if (value instanceof OperationOR) {
			String[] fields = ((OperationOR)value).getFields();
			OperationValue[] operations = ((OperationOR)value).getOperations();

			for (int i = 0; i < fields.length; i++) {
				String keyAux = fields[i];
				Object valueAux = operations[i];

				this.setParameter(query, keyAux, valueAux);
			}
		} else {
			query.setParameter(getParameterName(key), value);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T first(StringBuilder hql) {
		Map<String, Object> parameters = null;

		Query query = parseHQL(parameters, hql, null);

		if (query == null)
			return null;

		query.setMaxResults(1);

		try {
			return (T)query.getSingleResult();
		} catch (NoResultException e) {
				return null;
		}
	}

	@Override
	public Object find(StringBuilder hql, Map<String, Object> parameters) {

		Query query = parseHQL(parameters, hql, null);

		if (query == null)
			return null;

		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
				return null;
		}
	}

	@Override
	public Long count(StringBuilder hql, Map<String, Object> parameters) {
		List<String> orders = null;

		Query query = parseHQL(parameters, hql, orders);

		if (query == null)
			return null;

		try {
			return (Long)query.getSingleResult();
		} catch (NoResultException e) {
				return null;
		}
	}

	@Override
	public List<T> list(StringBuilder hql) {
		Map<String, Object> parameters = null;

		return this.list(hql, parameters);
	}

	@Override
	public List<T> list(StringBuilder hql, Map<String, Object> parameters) {
		List<String> sortFields = this.getNaturalOrders();
		List<String> sortDirections = null;
		Integer firstResult = null;
		Integer maxResult = null;

		return this.list(hql, parameters, sortFields, sortDirections, firstResult, maxResult);
	}

	@Override
	public List<T> list(StringBuilder hql, List<String> sortFields) {
		Map<String, Object> parameters  = null;
		List<String> sortDirections = null;
		Integer firstResult = null;
		Integer maxResult = null;

		return this.list(hql, parameters, sortFields, sortDirections, firstResult, maxResult);
	}

	@Override
	public List<T> list(StringBuilder hql, Map<String, Object> parameters, List<String> sortFields) {
		List<String> sortDirections = null;
		Integer firstResult = null;
		Integer maxResult = null;

		return this.list(hql, parameters, sortFields, sortDirections, firstResult, maxResult);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> list(StringBuilder hql, Map<String, Object> parameters, List<String> sortFields, List<String> sortDirections, Integer firstResult,  Integer maxResult) {

		Query query = parseHQL(parameters, hql, sortFields, sortDirections, firstResult, maxResult);

		if (query == null)
			return new ArrayList<>();

		return query.getResultList();
	}
}
