package app;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import util.DBWorker;

public class Phonebook {

	// Хранилище записей о людях.
	private HashMap<String, Person> persons = new HashMap<>();

	// Объект для работы с БД.
	private DBWorker db = DBWorker.getInstance();

	// Указатель на экземпляр класса.
	private static Phonebook instance = null;

	// Метод для получения экземпляра класса (реализован Singleton).
	public static Phonebook getInstance() throws SQLException {
		if (instance == null) {
			instance = new Phonebook();
		}

		return instance;
	}

	// При создании экземпляра класса из БД извлекаются все записи.
	protected Phonebook() throws SQLException {
		ResultSet dbData = this.db.getDBData("SELECT * FROM `person` ORDER BY `surname` ASC");
		while (dbData.next()) {
			this.persons.put(dbData.getString("id"), new Person(dbData.getString("id"), dbData.getString("name"), dbData.getString("surname"), dbData.getString("middlename")));
		}
	}

	// Добавление записи о человеке.
	public boolean addPerson(Person person) {
		String query;
		// У человека может не быть отчества.
		if (!person.getMiddlename().equals("")) {
			query = "INSERT INTO `person` (`name`, `surname`, `middlename`) VALUES ('" + person.getName() + "', '" + person.getSurname() + "', '" + person.getMiddlename() + "')";
		} else {
			query = "INSERT INTO `person` (`name`, `surname`) VALUES ('" + person.getName() + "', '" + person.getSurname() + "')";
		}

		Integer affected_rows = this.db.changeDBData(query);

		// Если добавление прошло успешно...
		if (affected_rows > 0) {
			person.setId(this.db.getLastInsertId().toString());

			// Добавляем запись о человеке в общий список.
			this.persons.put(person.getId(), person);

			return true;
		} else {
			return false;
		}
	}


	// Обновление записи о человеке.
	public boolean updatePerson(String id, Person person) {
		Integer idFiltered = Integer.parseInt(person.getId());
		String query;

		// У человека может не быть отчества.
		if (!person.getMiddlename().equals("")) {
			query = "UPDATE `person` SET `name` = '" + person.getName() + "', `surname` = '" + person.getSurname() + "', `middlename` = '" + person.getMiddlename() + "' WHERE `id` = " + idFiltered;
		} else {
			query = "UPDATE `person` SET `name` = '" + person.getName() + "', `surname` = '" + person.getSurname() + "' WHERE `id` = " + idFiltered;
		}

		Integer affectedRows = this.db.changeDBData(query);

		// Если обновление прошло успешно...
		if (affectedRows > 0) {
			// Обновляем запись о человеке в общем списке.
			this.persons.put(person.getId(), person);
			return true;
		} else {
			return false;
		}
	}


	// Удаление записи о человеке.
	public boolean deletePerson(String id) {
		if ((id != null) && (!id.equals("null"))) {
			int filteredId = Integer.parseInt(id);

			Integer affectedRows = this.db.changeDBData("DELETE FROM `person` WHERE `id`=" + filteredId);

			// Если удаление прошло успешно...
			if (affectedRows > 0) {
				// Удаляем запись о человеке из общего списка.
				this.persons.remove(id);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++
	// Геттеры и сеттеры
	public HashMap<String, Person> getContents() {
		return persons;
	}

	public Person getPerson(String id) {
		return this.persons.get(id);
	}
	// Геттеры и сеттеры
	// -----------------------------------------

}