package br.com.marcenaria.ejb.base;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

import br.com.marcenaria.client.base.BaseServiceEntity;
import br.com.marcenaria.client.base.ListWrapper;
import br.com.marcenaria.client.exception.BusinessException;
import br.com.marcenaria.jpa.base.BaseEntity;
import br.com.marcenaria.jpa.type.DominioType;

@SuppressWarnings("rawtypes")
@Stateless
public abstract class BaseServiceEntityImpl<T extends BaseEntity> extends BaseServiceImpl implements BaseServiceEntity<T> {

	

	@SuppressWarnings({ "unchecked" })
	private Class getGenericClass() {
		return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@SuppressWarnings({ "unchecked" })
	protected Long getNextval() throws BusinessException {
		Class clazz = getGenericClass();
		Annotation annotation = clazz.getAnnotation(Table.class);

		if (annotation != null) {
			String tableName;
			String schema;

			try {
				tableName = (String)annotation.annotationType().getMethod("name").invoke(annotation);
				schema = (String)annotation.annotationType().getMethod("schema").invoke(annotation);
			} catch (Exception e) {
				throw new BusinessException("Erro ao buscar o nome da tabela (msg: " + e.getMessage() + ")");
			}

			if (schema != null && !schema.isEmpty())
				schema += ".";
			else
				schema = "";

			StringBuilder hql = new StringBuilder();
			hql.append("SELECT NEXT VALUE FOR " + schema + "SQ_" + tableName);

			Query query = entityManager.createNativeQuery(hql.toString());

			Object result = query.getSingleResult();

			try {
				return ((BigDecimal)result).longValue();
			} catch (ClassCastException e) {
				return ((BigInteger)result).longValue();
			}
		}

		return null;
	}

	@Override
	public T save(T entity) throws BusinessException /*throws BusinessException */{
		DominioType dominio;
		boolean isNew = entity.isNew();

		if (isNew) {
			entity.setId(getNextval());
			dominio = DominioType.CADASTROS;
			super.entityManager.persist(entity);
		} else {
			dominio = DominioType.ALTERACOES;
			super.entityManager.merge(entity);
		}
		entityManager.flush();

		this.saveLog(dominio, entity);

		return entity;
	}

	private void saveLog(DominioType dominio, T entity) {

		String entityName = this.getGenericClassName();

		if (entityName.contains("LogAcesso") || entityName.contains("LogOperacao"))
			return;

		if (this.getToken() == null)
			return;

	}

	@Override
	public void delete(T entity)/* throws BusinessException */{
		T entityAux = entityManager.merge(entity);
		entityManager.remove(entityAux);

		this.saveLog(DominioType.EXCLUSOES, entityAux);
	}

	private String getGenericClassName() {
		return getGenericClass().getSimpleName();
	}

	@Override
	public StringBuilder getSimpleHQL() {
		boolean distinct = false;
		return this.getSimpleHQL(distinct);
	}

	@Override
	public StringBuilder getSimpleHQL(boolean distinct) {
		String entity = getGenericClassName();
		String entityAlias = entity.substring(0, 1).toLowerCase() + entity.substring(1);

		StringBuilder hql = new StringBuilder();
		hql.append("SELECT " + (distinct ? " DISTINCT " : "") + entityAlias + " ");
		hql.append("FROM " + entity + " " + entityAlias + " ");

		return hql;
	}

	protected StringBuilder getSimpleHQLCount() {
		String entity = getGenericClassName();
		String entityAlias = entity.substring(0, 1).toLowerCase() + entity.substring(1);

		StringBuilder hql = new StringBuilder();
		hql.append("SELECT COUNT(1) ");
		hql.append("FROM " + entity + " " + entityAlias + " ");

		return hql;
	}

	@Override
	public T load(StringBuilder hql) {
		return this.load(hql, null);
	}

	@Override
	public T load(Map<String, Object> parameters) {
		return this.load(this.getSimpleHQL(), parameters);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T load(StringBuilder hql, Map<String, Object> parameters) {

		Query query = super.parseHQL(parameters, hql, null);

		if (query == null) {
			return null;
		}

		try {
			return (T)query.getSingleResult();
		} catch (NoResultException e) {
				return null;
		}
	}

	@Override
	public T findById(Long id) {
		Map<String,Object> parameters = new HashMap<>();
		parameters.put("id", id);

		return this.load(getSimpleHQL(), parameters);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Long count(Map<String, Object> parameters) {
		StringBuilder hql = getSimpleHQLCount();
		return this.count(hql, parameters);
	}

	@Override
	public List<T> listAll() {
		Map<String, Object> parameters  = null;
		return this.list(parameters);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> listAtivos() {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("ativo", true);

		return list(parameters, getNaturalOrders());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> list(Map<String, Object> parameters) {
		return this.list(getSimpleHQL(), parameters, getNaturalOrders());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> list(Map<String, Object> parameters, List<String> sortFields) {
		return this.list(getSimpleHQL(), parameters, sortFields);
	}

	protected ListWrapper getWrapper(Integer page, Integer pageSize, List<String> sortFields, List<String> sortDirections,
			Map<String, Object> parameters) {
		StringBuilder hql = this.getSimpleHQL();
		return this.getWrapper(page, pageSize, sortFields, sortDirections, hql, parameters);
	}

	@SuppressWarnings({ "unchecked" })
	protected ListWrapper getWrapper(Integer page, Integer pageSize, List<String> sortFields, List<String> sortDirections,
			StringBuilder hql, Map<String, Object> parameters) {

		String entity = getGenericClassName();
		String entityAlias = entity.substring(0, 1).toLowerCase() + entity.substring(1);

		String countStr = hql.toString();
		countStr = countStr.replace("SELECT " + entityAlias, "SELECT COUNT(*)");
		countStr = countStr.replace("FETCH", "");

		StringBuilder hqlCount = new StringBuilder(countStr);
		Long count = this.count(hqlCount, parameters);

		List<T> list = this.list(hql, parameters, sortFields, sortDirections, page, pageSize);

		ListWrapper<T> wrapper = new ListWrapper<>();
		wrapper.setCurrentPage(page);
		wrapper.setPageSize(pageSize);
		wrapper.setSortFields(sortFields);
		wrapper.setSortDirections(sortDirections);
		wrapper.setList(list);
		wrapper.setTotalResults(count.intValue());

		return wrapper;
	}

	protected Response ok(Object data) {
		if (data instanceof BusinessException)
			return businessExceptionError((BusinessException)data);

		String authorization = this.getToken();

		boolean islistWrapper = data instanceof ListWrapper;

		ObjectMapper objectMapper = new ObjectMapper();

		Hibernate5Module hm = new Hibernate5Module();
		hm.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, false);

		objectMapper.registerModule(hm);

		Object mapper;

		try {
			if (islistWrapper)
				mapper = objectMapper.writeValueAsString(((ListWrapper)data).getList());
			else
			mapper = objectMapper.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			return internalServerError(e);
		}

		ResponseBuilder response = Response.ok(mapper)
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "accept, authorization, content-type, x-requested-with, app_key")
				.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, DELETE, PUT, HEAD")
				.header("Access-Control-Allow-Credentials", "true")
				.header("Access-Control-Max-Age", "1000")
				.header("content-type", MediaType.APPLICATION_JSON_TYPE.withCharset("utf-8"));


		if (islistWrapper) {
			Integer totalResult = ((ListWrapper)data).getTotalResults();

			response.header("Access-Control-Expose-Headers", "X-Total-Count");
			response.header("X-Total-Count", totalResult);
		}
		if (authorization != null && !authorization.isEmpty()) {
			response.header("Access-Control-Expose-Headers", "Authorization");
			response.header("Authorization", authorization);
		}
		return response.build();
	}

	private Response businessExceptionError(BusinessException e) {
		JSONObject json = new JSONObject();
		if(e.getArgs() != null)
			for (Entry<String, Object> arg : e.getArgs().entrySet())
				json.put(arg.getKey(), arg.getValue());

		json.put("error", e.getMessage());
		return Response.ok(Response.Status.OK).entity(json).type(MediaType.TEXT_PLAIN_TYPE).build();
	}

	protected Response internalServerError(Exception e) {
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e).type(MediaType.TEXT_PLAIN_TYPE).build();
	}
	
	
	 
}
