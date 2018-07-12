package app;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import util.DBWorker;

public class Phones {

    // Хранилище записей о людях.
    private HashMap<String, Phone> phones = new HashMap<>();

    // Объект для работы с БД.
    private DBWorker db = DBWorker.getInstance();

    // Указатель на экземпляр класса.
    private static Phones instance = null;

    // Метод для получения экземпляра класса (реализован Singleton).
    public static Phones getInstance() throws SQLException {
        if (instance == null) {
            instance = new Phones();
        }

        return instance;
    }

    // При создании экземпляра класса из БД извлекаются все записи.
    protected Phones() throws SQLException {
        ResultSet dbData = this.db.getDBData("SELECT * FROM `phone` ORDER BY `id`");
        while (dbData.next()) {
            this.phones.put(dbData.getString("id"), new Phone(dbData.getString("id"), dbData.getString("owner"), dbData.getString("number")));
        }
    }

    // Добавление записи о человеке.
    public String addPhone(Phone phone) {
        String query;
        // У человека может не быть отчества.

        query = "INSERT INTO `phone` (`owner`, `number`) VALUES ('" + phone.getOwner() + "', '" + phone.getNumber() + "')";


        Integer affectedRows = this.db.changeDBData(query);

        // Если добавление прошло успешно...
        if (affectedRows > 0) {
            phone.setId(this.db.getLastInsertId().toString());

            // Добавляем запись о человеке в общий список.
            this.phones.put(phone.getId(), phone);

            return phone.getId();
        } else {
            return "0";
        }
    }


    // Обновление записи о человеке.
    public boolean updatePhone(String id, Phone phone) {

        Integer idFiltered = Integer.parseInt(phone.getId());
        String query;

        query = "UPDATE `phone` SET `owner` = '" + phone.getOwner() + "', `number` = '" + phone.getNumber() + "' WHERE `id` = " + idFiltered;

        Integer affectedRows = this.db.changeDBData(query);

        // Если обновление прошло успешно...
        if (affectedRows > 0) {
            // Обновляем запись о человеке в общем списке.
            this.phones.put(phone.getId(), phone);
            return true;
        } else {
            return false;
        }
    }


    // Удаление записи о человеке.
    public boolean deletePhone(String id) {
        if ((id != null) && (!id.equals("null"))) {
            int filteredId = Integer.parseInt(id);

            Integer affectedRows = this.db.changeDBData("DELETE FROM `phone` WHERE `id`=" + filteredId);

            // Если удаление прошло успешно...
            if (affectedRows > 0) {
                // Удаляем запись о человеке из общего списка.
                this.phones.remove(id);
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
    public HashMap<String, Phone> getContents() {
        return phones;
    }

    public Phone getPhone(String id) {
        return this.phones.get(id);
    }
    // Геттеры и сеттеры
    // -----------------------------------------

}