package br.com.marcenaria.war;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.marcenaria.client.MaterialService;
import br.com.marcenaria.client.exception.BusinessException;
import br.com.marcenaria.jpa.Material;

@ApplicationPath("/")
@ApplicationScoped
@Named
@Path("material")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MaterialREST extends Application {

	@EJB private MaterialService materialService;

	@GET
	@Path("/{id}")
	public Material getById(@PathParam("id") Long id) {
		System.out.println("TESTE");
		
		Material m = new Material();
		m.setDescricao("Parafuso");
		m.setId(1l);
		try {
			return materialService.save(m);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
