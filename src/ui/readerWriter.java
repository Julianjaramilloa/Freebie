package ui;
import logic.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Scanner;
import java.util.Locale;
import java.util.Locale.Category;

/*
 * freebee
 * @author Marcos Pinz�n Pardo
 */

public class readerWriter {
	
	
	// De aqu� en adelante readAndLoad()
	UserList userList;
	final String fileName = "freebeeRecord.txt";
	User currentUser = null;
	
	// "user" es el usuario que se est� llenando de cuentas y transacciones en cada momento
	User user = null;

	// Constructor
	public readerWriter(UserList userList) {
		this.userList = userList;
	}
	
	// Crea el archivo o inicia readData
	public void readFile() {
		
		try {
			// Si el archivo no existe, lo crea
		    File textfile = new File(fileName);
		    if (textfile.createNewFile()) {
		      System.out.println("Nuevo archivo " + fileName + " creado");   
		    // Si ya existe, lee los datos y crea usuarios, cuentas y transacciones
		    } else {
		      System.out.println("El archivo " + fileName + " ya existe");
		      // Lee el archivo y crea los objetos
		      readData(fileName);
		      }
		    } catch (IOException e) {
		System.out.println("Algo sali� mal con la creaci�n de " + fileName);
	    e.printStackTrace();
	    }
	System.out.println("");
	}
		
	// Lee linea por linea y crea usuario, luego cuentas y luego transacciones
	private void readData(String fileName) {
		
		File f = new File(fileName);
		try {
			Scanner sc = new Scanner(f);
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				tokenize(line);
			}
			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error leyendo archivo");
			e.printStackTrace();
		}
	}
	//Este m�todo, como se puede ver en readData(), se aplica sobre cada rengl�n del txt
	private void tokenize(String line) {
		
		System.out.println("Tokenizando l�nea");
		Scanner sc = new Scanner(line);
		sc.useDelimiter(";");
		
		String data = sc.next().trim();
		
		if(data.charAt(0) == 'U') {
			// Se lee el usuario temporal, se agrega a la lista de usuarios y se usa hasta que se lee otro que lo sobreescribe
			User userToken = tokenizeUser(sc.next().trim(), sc.next().trim());
			currentUser = userToken;
			
			userList.addUser(userToken);
			System.out.println("Es un usuario, tokenizando");
			
			
		} else if(data.charAt(0) == 'C') {
			String name = sc.next().trim();
			float balance = Float.parseFloat(sc.next().trim());
			String currency = sc.next().trim();
			tokenizeAccount( name, balance, currency, currentUser);
			System.out.println("Es una cuenta, tokenizando");
			
		} else if(data.charAt(0) == 'T') {
			//System.out.println(sc.next().trim());
			DateTimeFormatter df = new DateTimeFormatterBuilder()
				    // case insensitive to parse JAN and FEB
				    .parseCaseInsensitive()
				    // add pattern
				    .appendPattern("dd-MM-yyyy")
				    // create formatter (use English Locale to parse month names)
				    .toFormatter(Locale.ENGLISH);
			LocalDate dateOfTransaction = LocalDate.parse(sc.next().trim(),df);
			String desc = sc.next().trim();
			Categories cat = Categories.valueOf(sc.next().trim()) ;
			float amount = Float.parseFloat(sc.next().trim());
			Boolean isIngreso = Boolean.parseBoolean(sc.next().trim());
			short accId = Short.valueOf(sc.next().trim());
			tokenizeTransaction(dateOfTransaction, accId, desc, cat, amount, isIngreso, currentUser);

			System.out.println("Es una transacci�n, tokenizando");
			
		} else {
			System.err.println("Algo fall� con la tokenizaci�n");
		}
		
		sc.close();

	}

	private User tokenizeUser(String name, String password) {
		

		System.out.println("Tokenizando usuario");	
	
		
		User user = new User(name, password);
		System.out.println("\n\tUsuario creado:\n\n" + "Name:" + name + " Password:" + password + "\n");
		
		return user;
	}

	private void tokenizeAccount(String name, float balance, String currency, User currentUser) {
		
		System.out.println("Tokenizando cuenta");
	
		
		
		//Hay que especificar a qu� usuario se a�ade
		currentUser.addAccount(name, balance, currency);
		System.out.println("\n\tCuenta creada:\n\n" + "Name:" + name + " Balance:" + balance + " Currency:" + currency + "\n");
		
	}

	private void tokenizeTransaction(LocalDate dateOfTransaction, short accId, String desc, Categories cat, float amount, boolean isIngreso,  User user) {
		
		System.out.println("Tokenizando transacci�n");
		
		
		
		//Hay que especificar a qu� usuario se a�ade
		user.addTransaction(dateOfTransaction, accId, desc, cat, amount, isIngreso);
		
		System.out.println("\n\tTransacci�n creada:\n\n" + 
		"Fecha:" + dateOfTransaction + " Descripci�n:" + desc + " Category:" + cat +
		"Cantidad:" + amount + " Es un ingreso:" + isIngreso + " Cuenta Asociada:" + accId + "\n");
		
	}

	
	// Hasta aqu� la parte de lectura del .txt
	
	// De aqu� en adelante saveChanges()
	
	/*public void saveChanges() {
		
		// Para que esta funci�n correctamente, se debe modificar el get, de manera que quede
		// formateado (parecido a lo que se ve arriba de los prints para confirmar que se imprimi� bien)
		// y de esta manera se guarde en el .txt como queremos, todo separado por ";"
		
		try (FileWriter f = new FileWriter(fileName, true);
				BufferedWriter b = new BufferedWriter(f);
				PrintWriter p = new PrintWriter(b);) {
			
			// Recorre la lista de usuarios (lo llam� userList)
			for (int u = 0; u < UserList.size(); u++) {
				
				f.write("U; " + UserList.get(u) + "\n");
				
				// Demostrativo, hay que implementar bien:
				listaDeCuentas = u.getAccounts; 
				listaDeTrans = u.getTransactions;
						
				for (int a = 0; a < listaDeCuentas.size(); a++) {f.write("C; " +listaDeCuentas.get(a) + "\n");}
				for (int t = 0; t < listaDeTrans.size(); t++) {f.write("T; " +listaDeTrans.get(t) + "\n");}
				
	        }
			
		} catch (IOException i1) {i1.printStackTrace();}
		
	}
	*/
	

}