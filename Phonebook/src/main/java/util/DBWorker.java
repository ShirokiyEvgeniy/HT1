package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import com.mysql.cj.jdbc.Driver;

public class DBWorker {
	
	// Количество рядов таблицы, затронутых последним запросом.
	private Integer affectedRows = 0;
	
	// Значение автоинкрементируемого первичного ключа, полученное после
	// добавления новой записи.
	private Integer lastInsertId = 0;

	// Указатель на экземпляр класса.
	private static DBWorker instance = null;
	
	// Метод для получения экземпляра класса (реализован Singleton).
	public static DBWorker getInstance()
	{
		if (instance == null)
		{
			instance = new DBWorker();
		}
	
		return instance;
	}
	
	// "Заглушка", чтобы экземпляр класса нельзя было получить напрямую.
	private DBWorker()
	{
	 // Просто "заглушка".			
	}
	
	// Выполнение запросов на выборку данных.
	public ResultSet getDBData(String query)
	{
		Statement statement;
		Connection connect;
        ResourceBundle resourceBundle = ResourceBundle.getBundle("DBLogin");
		try
		{
			Driver driver = new Driver();
			DriverManager.registerDriver(driver);
			connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/phonebook?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", resourceBundle.getString("login"), resourceBundle.getString("password"));
			statement = connect.createStatement();
			return statement.executeQuery(query);
		}
		catch (SQLException  e )
		{
			e.printStackTrace();
		}

        System.out.println("null on getDBData()!");
		return null;

	}
	
	// Выполнение запросов на модификацию данных.
	public Integer changeDBData(String query)
	{
		Statement statement;
		Connection connect;
		ResourceBundle resourceBundle = ResourceBundle.getBundle("DBLogin");
		try
		{
            Driver driver = new Driver();
            DriverManager.registerDriver(driver);
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/phonebook?useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", resourceBundle.getString("login"), resourceBundle.getString("password"));
            statement = connect.createStatement();
			this.affectedRows = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

			// Получаем lastInsertId() для операции вставки.
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()){
            	this.lastInsertId = rs.getInt(1);
            }

			return this.affectedRows;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		System.out.println("null on changeDBData()!");
		return null;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++
	// Геттеры и сеттеры.
	public Integer getAffectedRowsCount()
	{
		return this.affectedRows;
	}
	
	public Integer getLastInsertId()
	{
		return this.lastInsertId;
	}
	// Геттеры и сеттеры.
	// -------------------------------------------------
}

