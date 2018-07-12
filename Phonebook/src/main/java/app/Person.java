package app;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.DBWorker;

public class Person {

	// Данные записи о человеке.
	private String id;
	private String name;
	private String surname;
	private String middlename;
	private HashMap<String, Phone> phones = new HashMap<>();

	// Указатель на экземпляр класса.
	private static Person instance = null;

	// Метод для получения экземпляра класса (реализован Singleton).
	public static Person getInstance() throws SQLException {
		if (instance == null) {
			instance = new Person();
		}

		return instance;
	}
	// Конструктор для создания записи о человеке на основе данных из БД. 
	public Person(String id, String name, String surname, String middlename) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.middlename = middlename;

		// Извлечение телефонов человека из БД.
		ResultSet dbData = DBWorker.getInstance().getDBData("SELECT * FROM `phone` WHERE `owner`=" + id);

		try {
			// Если у человека нет телефонов, ResultSet будет == null.
			if (dbData != null) {
				while (dbData.next()) {
					this.phones.put(dbData.getString("id"), new Phone(dbData.getString("id"), id, dbData.getString("number")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Конструктор для создания пустой записи о человеке.
	public Person() {
		this.id = "0";
		this.name = "";
		this.surname = "";
		this.middlename = "";
	}

	// Конструктор для создания записи, предназначенной для добавления в БД. 
	public Person(String name, String surname, String middlename) {
		this.id = "0";
		this.name = name;
		this.surname = surname;
		this.middlename = middlename;
	}

	// Валидация частей ФИО. Для отчества можно передать второй параетр == true,
	// тогда допускается пустое значение.
	public boolean validateFMLNamePart(String fmlNamePart, boolean emptyAllowed) {
		if (emptyAllowed) {
			return fmlNamePart.matches("[a-zA-Zа-яА-Я_0-9\\-]{0,150}");
		} else {
			return fmlNamePart.matches("[a-zA-Zа-яА-Я_0-9\\-]{1,150}");
		}

	}

	// ++++++++++++++++++++++++++++++++++++++
	// Геттеры и сеттеры
	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getSurname() {
		return this.surname;
	}

	public String getMiddlename() {
		if ((this.middlename != null) && (!this.middlename.equals("null"))) {
			return this.middlename;
		} else {
			return "";
		}
	}

	public HashMap<String, Phone> getPhones() {
		return this.phones;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}

	public void setPhones(HashMap<String, Phone> phones) {
		this.phones = phones;
	}
	// Геттеры и сеттеры
	// --------------------------------------

}