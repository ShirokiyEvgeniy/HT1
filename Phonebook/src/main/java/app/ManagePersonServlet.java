package app;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ManagePersonServlet extends HttpServlet {

	// Идентификатор для сериализации/десериализации.
	private static final long serialVersionUID = 1L;

	// Основной объект, хранящий данные телефонной книги.
	private Phonebook phonebook;
	// Основной объект, хранящий все телефоны.
    private Phones phones;

	public ManagePersonServlet() {
		// Вызов родительского конструктора.
		super();

		// Создание экземпляра телефонной книги и телефонов.
		try {
			this.phonebook = Phonebook.getInstance();
			this.phones = Phones.getInstance();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Валидация ФИО и генерация сообщения об ошибке в случае невалидных данных.
	private String validatePersonFMLName(Person person) {
		String error_message = "";

		if (!person.validateFMLNamePart(person.getName(), false)) {
			error_message += "Имя должно быть строкой от 1 до 150 символов из букв, цифр, знаков подчёркивания и знаков минус.<br />";
		}

		if (!person.validateFMLNamePart(person.getSurname(), false)) {
			error_message += "Фамилия должна быть строкой от 1 до 150 символов из букв, цифр, знаков подчёркивания и знаков минус.<br />";
		}

		if (!person.validateFMLNamePart(person.getMiddlename(), true)) {
			error_message += "Отчество должно быть строкой от 0 до 150 символов из букв, цифр, знаков подчёркивания и знаков минус.<br />";
		}

		return error_message;
	}

	// Реакция на GET-запросы.
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Обязательно ДО обращения к любому параметру нужно переключиться в UTF-8,
		// иначе русский язык при передаче GET/POST-параметрами превращается в "кракозябры".
		request.setCharacterEncoding("UTF-8");

		// В JSP нам понадобится сама телефонная книга. Можно создать её экземпляр там,
		// но с архитектурной точки зрения логичнее создать его в сервлете и передать в JSP.
		request.setAttribute("phonebook", this.phonebook);
        request.setAttribute("phones", this.phones);

		// Хранилище параметров для передачи в JSP.
		HashMap<String, String> jsp_parameters = new HashMap<>();

		// Диспетчеры для передачи управления на разные JSP (разные представления (view)).
		RequestDispatcher dispatcher_for_manager = request.getRequestDispatcher("/ManagePerson.jsp");
		RequestDispatcher dispatcher_for_list = request.getRequestDispatcher("/List.jsp");
		RequestDispatcher dispatcher_for_edit = request.getRequestDispatcher("/EditPerson.jsp");
		RequestDispatcher dispatcher_for_edit_phone = request.getRequestDispatcher("/EditPhone.jsp");

		// Действие (action) и идентификатор записи (id) над которой выполняется это действие.
		String action = request.getParameter("action");
		String id = request.getParameter("id");
		String ownerID = request.getParameter("ownerID");

		// Если идентификатор и действие не указаны, мы находимся в состоянии
		// "просто показать список и больше ничего не делать".
		if ((action == null) && (id == null)) {
			request.setAttribute("jsp_parameters", jsp_parameters);
			dispatcher_for_list.forward(request, response);
		}
		// Если же действие указано, то...
		else {
			switch (action) {
				// Добавление записи.
				case "add":
					// Создание новой пустой записи о пользователе.
					Person empty_person = new Person();

					// Подготовка параметров для JSP.
					jsp_parameters.put("current_action", "add");
					jsp_parameters.put("next_action", "add_go");
					jsp_parameters.put("next_action_label", "Добавить");

					// Установка параметров JSP.
					request.setAttribute("person", empty_person);
					request.setAttribute("jsp_parameters", jsp_parameters);

					// Передача запроса в JSP.
					dispatcher_for_manager.forward(request, response);
					break;

				// Редактирование записи.
				case "edit":
					// Извлечение из телефонной книги информации о редактируемой записи.
					Person editable_person = this.phonebook.getPerson(id);

					// Подготовка параметров для JSP.
					jsp_parameters.put("current_action", "edit");
					jsp_parameters.put("next_action", "edit_go");
					jsp_parameters.put("next_action_label", "Сохранить");

					// Установка параметров JSP.
					request.setAttribute("person", editable_person);
					request.setAttribute("jsp_parameters", jsp_parameters);

					// Передача запроса в JSP.
					dispatcher_for_edit.forward(request, response);
					break;

				// Удаление записи.
				case "delete":

					// Если запись удалось удалить...
					if (phonebook.deletePerson(id)) {
						jsp_parameters.put("current_action_result", "DELETION_SUCCESS");
						jsp_parameters.put("current_action_result_label", "Удаление выполнено успешно");
					}
					// Если запись не удалось удалить (например, такой записи нет)...
					else {
						jsp_parameters.put("current_action_result", "DELETION_FAILURE");
						jsp_parameters.put("current_action_result_label", "Ошибка удаления (возможно, запись не найдена)");
					}

					// Установка параметров JSP.
					request.setAttribute("jsp_parameters", jsp_parameters);

					// Передача запроса в JSP.
					dispatcher_for_list.forward(request, response);
					break;
                case "addPhone" :
                    // Создание новой пустой записи о пользователе.
                    Phone empty_phone = new Phone();
                    Person updated_person = this.phonebook.getPerson(ownerID);

                    // Подготовка параметров для JSP.
                    jsp_parameters.put("current_action", "addPhone");
                    jsp_parameters.put("next_action", "addPhone_go");
                    jsp_parameters.put("next_action_label", "Добавить");

                    // Установка параметров JSP.
                    request.setAttribute("phone", empty_phone);
                    request.setAttribute("person", updated_person);
                    request.setAttribute("jsp_parameters", jsp_parameters);

                    // Передача запроса в JSP.
                    dispatcher_for_edit_phone.forward(request, response);
                    break;
                case "editPhone":
                    // Извлечение из телефонной книги информации о редактируемой записи.
                    Phone editable_phone = this.phones.getPhone(id);
                    Person edited_person = this.phonebook.getPerson(ownerID);

                    // Подготовка параметров для JSP.
                    jsp_parameters.put("current_action", "editPhone");
                    jsp_parameters.put("next_action", "editPhone_go");
                    jsp_parameters.put("next_action_label", "Сохранить");

                    // Установка параметров JSP.
                    request.setAttribute("phone", editable_phone);
                    request.setAttribute("person", edited_person);
                    request.setAttribute("jsp_parameters", jsp_parameters);

                    // Передача запроса в JSP.
                    dispatcher_for_edit_phone.forward(request, response);
                    break;

                // Удаление записи.
                case "deletePhone":

                    // Если запись удалось удалить...
                    if (phones.deletePhone(id)) {
                        Person person_for_update = phonebook.getPerson(ownerID);
                        person_for_update.getPhones().remove(id);
                        this.phonebook.updatePerson(request.getParameter("ownerID"), person_for_update);

                        jsp_parameters.put("current_action_result", "DELETION_SUCCESS");
                        jsp_parameters.put("current_action_result_label", "Удаление выполнено успешно");
                    }
                    // Если запись не удалось удалить (например, такой записи нет)...
                    else {
                        jsp_parameters.put("current_action_result", "DELETION_FAILURE");
                        jsp_parameters.put("current_action_result_label", "Ошибка удаления (возможно, телефон не найдена)");
                    }

                    // Установка параметров JSP.
                    request.setAttribute("jsp_parameters", jsp_parameters);

                    // Передача запроса в JSP.
                    dispatcher_for_list.forward(request, response);
                    break;
			}
		}

	}

	// Реакция на POST-запросы.
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Обязательно ДО обращения к любому параметру нужно переключиться в UTF-8,
		// иначе русский язык при передаче GET/POST-параметрами превращается в "кракозябры".
		request.setCharacterEncoding("UTF-8");

		// В JSP нам понадобится сама телефонная книга. Можно создать её экземпляр там,
		// но с архитектурной точки зрения логичнее создать его в сервлете и передать в JSP.
		request.setAttribute("phonebook", this.phonebook);
        request.setAttribute("phones", this.phones);

		// Хранилище параметров для передачи в JSP.
		HashMap<String, String> jsp_parameters = new HashMap<>();

		// Диспетчеры для передачи управления на разные JSP (разные представления (view)).
		RequestDispatcher dispatcher_for_manager = request.getRequestDispatcher("/ManagePerson.jsp");
		RequestDispatcher dispatcher_for_list = request.getRequestDispatcher("/List.jsp");
		RequestDispatcher dispatcher_for_edit = request.getRequestDispatcher("/EditPerson.jsp");
		RequestDispatcher dispatcher_for_edit_phone = request.getRequestDispatcher("/EditPhone.jsp");


		// Действие (add_go, edit_go) и идентификатор записи (id) над которой выполняется это действие.
		String add_go = request.getParameter("add_go");
		String edit_go = request.getParameter("edit_go");
        String addPhone_go = request.getParameter("addPhone_go");
        String editPhone_go = request.getParameter("editPhone_go");
        String id = request.getParameter("id");

        System.out.println("add_go = " + add_go + ", edit_go = " + edit_go + ", addPhone_go = " + addPhone_go + ", editPhone_go = " + editPhone_go + ", id = " + id);

		// Добавление записи.
		if (add_go != null) {
			// Создание записи на основе данных из формы.
			Person new_person = new Person(request.getParameter("name"), request.getParameter("surname"), request.getParameter("middlename"));

			// Валидация ФИО.
			String error_message = this.validatePersonFMLName(new_person);

			// Если данные верные, можно производить добавление.
			if (error_message.equals("")) {

				// Если запись удалось добавить...
				if (this.phonebook.addPerson(new_person)) {
					jsp_parameters.put("current_action_result", "ADDITION_SUCCESS");
					jsp_parameters.put("current_action_result_label", "Добавление выполнено успешно");
				}
				// Если запись НЕ удалось добавить...
				else {
					jsp_parameters.put("current_action_result", "ADDITION_FAILURE");
					jsp_parameters.put("current_action_result_label", "Ошибка добавления");
				}

				// Установка параметров JSP.
				request.setAttribute("jsp_parameters", jsp_parameters);

				// Передача запроса в JSP.
				dispatcher_for_list.forward(request, response);
			}
			// Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
			else {
				// Подготовка параметров для JSP.
				jsp_parameters.put("current_action", "add");
				jsp_parameters.put("next_action", "add_go");
				jsp_parameters.put("next_action_label", "Добавить");
				jsp_parameters.put("error_message", error_message);

				// Установка параметров JSP.
				request.setAttribute("person", new_person);
				request.setAttribute("jsp_parameters", jsp_parameters);

				// Передача запроса в JSP.
				dispatcher_for_manager.forward(request, response);
			}
		}

		// Редактирование записи.
		if (edit_go != null) {
			// Получение записи и её обновление на основе данных из формы.
			Person updatable_person = this.phonebook.getPerson(request.getParameter("id"));
			updatable_person.setName(request.getParameter("name"));
			updatable_person.setSurname(request.getParameter("surname"));
			updatable_person.setMiddlename(request.getParameter("middlename"));

			// Валидация ФИО.
			String error_message = this.validatePersonFMLName(updatable_person);

			// Если данные верные, можно производить добавление.
			if (error_message.equals("")) {

				// Если запись удалось обновить...
				if (this.phonebook.updatePerson(id, updatable_person)) {
					jsp_parameters.put("current_action_result", "UPDATE_SUCCESS");
					jsp_parameters.put("current_action_result_label", "Обновление выполнено успешно");
				}
				// Если запись НЕ удалось обновить...
				else {
					jsp_parameters.put("current_action_result", "UPDATE_FAILURE");
					jsp_parameters.put("current_action_result_label", "Ошибка обновления");
				}

				// Установка параметров JSP.
				request.setAttribute("jsp_parameters", jsp_parameters);

				// Передача запроса в JSP.
				dispatcher_for_edit.forward(request, response);
			}
			// Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
			else {

				// Подготовка параметров для JSP.
				jsp_parameters.put("current_action", "edit");
				jsp_parameters.put("next_action", "edit_go");
				jsp_parameters.put("next_action_label", "Сохранить");
				jsp_parameters.put("error_message", error_message);

				// Установка параметров JSP.
				request.setAttribute("person", updatable_person);
				request.setAttribute("jsp_parameters", jsp_parameters);

				// Передача запроса в JSP.
				dispatcher_for_edit.forward(request, response);

			}
		}
        if (addPhone_go != null) {
            // Создание записи на основе данных из формы.
            Phone new_phone = new Phone(request.getParameter("id"), request.getParameter("ownerID"), request.getParameter("number"));
            Person updated_person = this.phonebook.getPerson(request.getParameter("ownerID"));

            // Валидация ФИО.
            String error_message = ""; //= this.validatePersonFMLName(new_person);

            // Если данные верные, можно производить добавление.
            if (error_message.equals("")) {
                String new_id;
                // Если запись удалось добавить...
                if (!(new_id = this.phones.addPhone(new_phone)).equals("0")) {
                    updated_person.getPhones().put(new_id, this.phones.getPhone(new_id));
                    this.phonebook.updatePerson(request.getParameter("ownerID"), updated_person);

                    jsp_parameters.put("current_action_result", "ADDITION_SUCCESS");
                    jsp_parameters.put("current_action_result_label", "Добавление выполнено успешно");
                }
                // Если запись НЕ удалось добавить...
                else {
                    jsp_parameters.put("current_action_result", "ADDITION_FAILURE");
                    jsp_parameters.put("current_action_result_label", "Ошибка добавления");
                }

                // Установка параметров JSP.
                request.setAttribute("jsp_parameters", jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_list.forward(request, response);
            }
            // Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
            else {
                // Подготовка параметров для JSP.
                jsp_parameters.put("current_action", "addPhone");
                jsp_parameters.put("next_action", "addPhone_go");
                jsp_parameters.put("next_action_label", "Добавить");
                jsp_parameters.put("error_message", error_message);

                // Установка параметров JSP.
                request.setAttribute("phone", new_phone);
                request.setAttribute("jsp_parameters", jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_edit_phone.forward(request, response);
            }
        }

        // Редактирование записи.
        if (editPhone_go != null) {
            // Получение записи и её обновление на основе данных из формы.
            Phone updatable_phone = this.phones.getPhone(request.getParameter("id"));
            Person updated_person = this.phonebook.getPerson(request.getParameter("ownerID"));

            updatable_phone.setId(request.getParameter("id"));
            updatable_phone.setOwner(request.getParameter("ownerID"));
            updatable_phone.setNumber(request.getParameter("number"));

            // Валидация ФИО.
            String error_message = ""; //this.validatePersonFMLName(updatable_person);

            // Если данные верные, можно производить добавление.
            if (error_message.equals("")) {

                // Если запись удалось обновить...
                if (this.phones.updatePhone(id, updatable_phone)) {
                    updated_person.getPhones().put(request.getParameter("id"), updatable_phone);
                    this.phonebook.updatePerson(request.getParameter("ownerID"), updated_person);
                    jsp_parameters.put("current_action_result", "UPDATE_SUCCESS");
                    jsp_parameters.put("current_action_result_label", "Обновление выполнено успешно");
                }
                // Если запись НЕ удалось обновить...
                else {
                    jsp_parameters.put("current_action_result", "UPDATE_FAILURE");
                    jsp_parameters.put("current_action_result_label", "Ошибка обновления");
                }

                // Установка параметров JSP.
                request.setAttribute("jsp_parameters", jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_list.forward(request, response);
            }
            // Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
            else {

                // Подготовка параметров для JSP.
                jsp_parameters.put("current_action", "editPhone");
                jsp_parameters.put("next_action", "editPhone_go");
                jsp_parameters.put("next_action_label", "Сохранить");
                jsp_parameters.put("error_message", error_message);

                // Установка параметров JSP.
                request.setAttribute("phone", updatable_phone);
                request.setAttribute("jsp_parameters", jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_list.forward(request, response);

            }
        }
	}
}