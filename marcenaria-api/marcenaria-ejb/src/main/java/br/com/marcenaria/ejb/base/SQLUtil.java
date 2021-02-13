package br.com.marcenaria.ejb.base;

import java.util.List;

public class SQLUtil {

	public static void addSortFieldsAndDirections(StringBuilder hql, List<String> sortFields, List<String> sortDirections) {
		if (sortFields != null && !sortFields.isEmpty()) {
			String orderBy = " ORDER BY ";
			
			boolean isFirst = true;
			
			for (int i = 0; i < sortFields.size(); i++) {
				String order = sortFields.get(i);
				
				if (isFirst)
					isFirst = false;
				else {
					orderBy += ", ";
				}
			
				if (sortDirections != null && i < sortDirections.size()) {
					order += " " + sortDirections.get(i) + " ";
				}
				
				orderBy += order;
			}
			
			hql.append(orderBy);
		}
	}
}
