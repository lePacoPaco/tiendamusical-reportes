/**
 * 
 */
package com.ejemplo.tiendamusicalreportes.services.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.stream.ImageInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ejemplo.tiendamusicalreportes.services.JasperReportsService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

/**
 * @author paco_
 * Clase que implementa los métodos de lógica de negocio de jasper reports.
 */
@Service
public class JasperReportsServiceImpl implements JasperReportsService {

	@Value("${spring.datasource.driverClassName}")
	String driver;
	
	@Value("${spring.datasource.url}")
	String url;
	
	@Value("${spring.datasource.username}")
	String username;
	
	@Value("${spring.datasource.password}")
	String password;
	
	
	@Override
	public JasperPrint compilarReporteJasper(ByteArrayOutputStream archivoBytes, String orderID) throws ClassNotFoundException, SQLException, JRException, IOException {
		//Se obtiene la imagen del archivo del classpath del proyecto
		InputStream imageInputStream = this.getClass().getClassLoader().getResourceAsStream("images/Smile.png");
		
		//Se envian los parámetros de compilación para el archivo jrxml
		Map<String, Object> map = new HashMap<>();
		map.put("orderID", orderID);
		map.put("logo", imageInputStream);
		
		//Se convierte el archivo de salida a un flujo de bytes.
		byte[] bytes = archivoBytes.toByteArray();
		InputStream archivoInputStream = new ByteArrayInputStream(bytes);
		
		//Se asignan los parámetros de conexión para el archivo de jasper
		Class.forName(this.driver);
		Connection connection = DriverManager.getConnection(this.url,this.username,this.password);
		
		JasperReport jasperReport = JasperCompileManager.compileReport(archivoInputStream);
		
		imageInputStream.close();
		archivoInputStream.close();
		
		return JasperFillManager.fillReport(jasperReport, map, connection);
	}

}
