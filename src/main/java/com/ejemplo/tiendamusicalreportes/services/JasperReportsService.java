/**
 * 
 */
package com.ejemplo.tiendamusicalreportes.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * @author paco_
 * Interface que contiene los métodos de lógica de negocio para compilar y generar el reporte pdf
 */
public interface JasperReportsService {

	/**
	 * Método que permite compilar el archivo jasper jrxml descargado de Dropbox.
	 * @param archivoBytes {@link ByteArrayOutputStream} archivo jrxml a compilar
	 * @param orderID {@link String} orden de pedido del cliente.
	 * @return {@link JasperPrint} archivo jasper con la información a generarse en pdf
	 * @throws ClassNotFoundException {@link ClassNotFoundException} excepcion en caso de no encontrar el driver.
	 * @throws SQLException {@link SQLException} excepcion en caso de error al conectarse a la base de datos.
	 * @throws JRException {@link JRException} excepcion en caso de error al compilar el reporte de jasper.
	 * @throws IOException {@link IOException} excepcion en caso de error al cerrar el flujo de datos de los archivos.
	 */
	JasperPrint compilarReporteJasper(ByteArrayOutputStream archivoBytes, String orderID) throws ClassNotFoundException, SQLException, JRException, IOException;
}
