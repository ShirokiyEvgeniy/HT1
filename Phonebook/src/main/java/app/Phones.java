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
        ResultSet db_data = this.db.getDBData("SELECT * FROM `phone` ORDER BY `id`");
        while (db_data.next()) {
            this.phones.put(db_data.getString("id"), new Phone(db_data.getString("id"), db_data.getString("owner"), db_data.getString("number")));
        }
    }

    // Добавление записи о человеке.
    public String addPhone(Phone phone) {
        ResultSet db_result;
        String query;
        // У человека может не быть отчества.

        query = "INSERT INTO `phone` (`owner`, `number`) VALUES ('" + phone.getOwner() + "', '" + phone.getNumber() + "')";


        Integer affected_rows = this.db.changeDBData(query);

        // Если добавление прошло успешно...
        if (affected_rows > 0) {
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

        Integer id_filtered = Integer.parseInt(phone.getId());
        String query;

        query = "UPDATE `phone` SET `owner` = '" + phone.getOwner() + "', `number` = '" + phone.getNumber() + "' WHERE `id` = " + id_filtered;

        Integer affected_rows = this.db.changeDBData(query);

        // Если обновление прошло успешно...
        if (affected_rows > 0) {
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
            int filtered_id = Integer.parseInt(id);

            Integer affected_rows = this.db.changeDBData("DELETE FROM `phone` WHERE `id`=" + filtered_id);

            // Если удаление прошло успешно...
            if (affected_rows > 0) {
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