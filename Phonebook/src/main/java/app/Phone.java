package app;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import util.DBWorker;

public class Phone {

    // Данные записи о человеке.
    private String id;
    private String owner;
    private String number;

    // Указатель на экземпляр класса.
    private static Phone instance = null;

    // Метод для получения экземпляра класса (реализован Singleton).
    public static Phone getInstance() throws SQLException {
        if (instance == null) {
            instance = new Phone();
        }
        return instance;
    }
    // Конструктор для создания записи о телефоне на основе данных из БД.
    public Phone(String id, String owner, String number) {
        this.id = id;
        this.owner = owner;
        this.number = number;
    }

    // Конструктор для создания пустой записи о человеке.
    public Phone() {
        this.id = "0";
        this.owner = "0";
        this.number = "";
    }

    // ++++++++++++++++++++++++++++++++++++++
    // Геттеры и сеттеры
    public String getId() {
        return this.id;
    }

    public String getOwner() {
        return this.owner;
    }

    public String getNumber() {
        return this.number;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setNumber(String number) {
        this.number = number;
    }
    // Геттеры и сеттеры
    // --------------------------------------

}