/**
 * 
 */
package com.ejemplo.tiendamusicalreportes.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;

import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadBuilder;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.files.WriteMode;
import com.ejemplo.tiendamusicalreportes.services.DropboxAPIService;
import com.ejemplo.tiendamusicalreportes.services.JasperReportsService;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;

/**
 * @author paco_
 * Clase que implementa los metodos para realizar la logica de negocio para descargar y cargar reportes con Dropbox.f
 */
@Service
public class DropboxAPIServiceImpl implements DropboxAPIService {
	
	@Value("${spring.dropbox.access.token}")
	String ACCESS_TOKEN;
	
	@Value("${spring.dropbox.directorio.reportes}")
	String DIRECTORIO_REPORTES;
	
	@Value("${spring.dropbox.archivo.jrxml}")
	String ARCHIVO_JASPER_JRXML;
	
	@Autowired
	private JasperReportsService jasperReportsServiceImpl;

	@Override
	public Response descargarReporte(DbxClientV2 dbxClientV2, String orderID, String cliente) {
		ByteArrayOutputStream archivoByte = new ByteArrayOutputStream();
		String mensaje = "";
		try {
			//Se descarga el archivo jrxml de Dropbox
			DbxDownloader<FileMetadata> downloader = dbxClientV2.files().download(DIRECTORIO_REPORTES + ARCHIVO_JASPER_JRXML);
			downloader.download(archivoByte);
			mensaje = "Comprobante generado exitosamente";
			
			//Se envia a compilar el archivo de jasper y se llena con la información consultada en la base de datos.
			JasperPrint jasperPrint = this.jasperReportsServiceImpl.compilarReporteJasper(archivoByte, orderID);
			
			//Se realiza la carga o subida del reporte pdf a dropbox	 		
			this.cargarReporteDropbox(dbxClientV2, orderID, cliente, jasperPrint);
		} catch (DownloadErrorException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (DbxException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (JRException e) {
			
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
		
		return Response.status(Response.Status.OK).entity(mensaje).build();
	}

	@Override
	public void cargarReporteDropbox(DbxClientV2 dbxClientV2, String orderID, String cliente, JasperPrint jasperPrint) throws IOException, JRException, UploadErrorException, DbxException {
		String nombreArchivoPDF = orderID + ".pdf";
		
		File filePDF = File.createTempFile("temp", nombreArchivoPDF);
		
		InputStream archivoExport = new FileInputStream(filePDF);
		
		JRPdfExporter jrPdfExporter = new JRPdfExporter();
		jrPdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		jrPdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(filePDF));
		
		SimplePdfReportConfiguration simplePdfReportConfiguration = new SimplePdfReportConfiguration();
		jrPdfExporter.setConfiguration(simplePdfReportConfiguration);
		
		jrPdfExporter.exportReport();
		
		UploadBuilder uploadBuilder = dbxClientV2.files().uploadBuilder(DIRECTORIO_REPORTES + "/" + cliente + "/" + nombreArchivoPDF);
		uploadBuilder.withClientModified(new Date(filePDF.lastModified()));
		uploadBuilder.withMode(WriteMode.ADD);
		uploadBuilder.withAutorename(true);
		
		uploadBuilder.uploadAndFinish(archivoExport);
		
		archivoExport.close();
	}

}
