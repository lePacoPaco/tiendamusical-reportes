/**
 * 
 */
package com.ejemplo.tiendamusicalreportes.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.ejemplo.tiendamusicalreportes.services.DropboxAPIService;
import com.ejemplo.tiendamusicalreportes.services.impl.DropboxAPIServiceImpl;

/**
 * @author paco_
 * WebService que contendrá los métodos configurados como servicios del WS.
 */
@Component
@Path("/reportesWS")
public class ReportesWS {
	
	/**
	 * Access Token de la API de Dropbox
	 */
	@Value("${spring.dropbox.access.token}")
	String ACCESS_TOKEN;
	
	/**
	 * Se inyecta el servicio de dropbox con Spring
	 */
	@Autowired
	private DropboxAPIService DropboxAPIServiceImpl;
	
	@GET
	@Path("/pruebaWS")
	public String pruebaWS() {
		return "Ingresando al webservice...";
	}
	
	@POST
	@Path("/generarReporte")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response generarReporte(@FormParam("orderID") String orderID, @FormParam("cliente") String cliente, @FormParam("destinatario") String destinatario) {
		DbxRequestConfig dbxRequestConfig = DbxRequestConfig.newBuilder("dropbox/ejemplo").build();
		DbxClientV2 dbxClientV2 = new DbxClientV2(dbxRequestConfig, ACCESS_TOKEN);
		Response response = this.DropboxAPIServiceImpl.descargarReporte(dbxClientV2, orderID, cliente);
		return response;
	}
}
