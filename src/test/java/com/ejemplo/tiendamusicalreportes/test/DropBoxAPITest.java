/**
 * 
 */
package com.ejemplo.tiendamusicalreportes.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.users.FullAccount;

/**
 * @author paco_
 * Prueba unitaria para verificar la comunicacion de un aplicativo java para dropbox
 */
public class DropBoxAPITest {

	@Test
	public void test() {
		//Se configura el token de aceso a la app creada en dropbox
		String token = "sl.AvMxOvG59Dau2TcaVB0-6xsOr8-2vvIuMknvnWsw_9m1yaDCRNCA7OdO7oYwJjo-f71ZqQxj8awQhp9lzxeGgKDwYIXMriOgQDzzmRILw2X6eXUoXIs5mom1oKEHjfIUO0g6S5w";
		
		//Se configura el token y el ambiente de configuracion inicial de dropbox
		DbxRequestConfig dbxConfig = DbxRequestConfig.newBuilder("ejemplo/test-dropbox").build();
		DbxClientV2 dbxClientV2 = new DbxClientV2(dbxConfig, token);
		
		try {
			assertNotNull(dbxClientV2);
			//Se obtiene y se muestra la información de la cuenta perteneciente a la app
			FullAccount fullAccount = dbxClientV2.users().getCurrentAccount();
			System.out.println("Email de la cuenta "+ fullAccount.getEmail());
		} catch (DbxException e) {
			assertNull(dbxClientV2);
		}
	}

}
