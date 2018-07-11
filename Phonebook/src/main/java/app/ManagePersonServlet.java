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
    public static final String ENCODING = "UTF-8";
    public static final String ACTION = "action";
    public static final String ID = "id";
    public static final String OWNER_ID = "ownerID";
    public static final String JSP_PARAMETERS = "jsp_parameters";
    public static final String CURRENT_ACTION = "current_action";
    public static final String ADD_PERSON = "add";
    public static final String NEXT_ACTION = "next_action";
    public static final String ADD_GO_PERSON = "add_go";
    public static final String NEXT_ACTION_LABEL = "next_action_label";
    public static final String EDIT_PERSON = "edit";
    public static final String EDIT_GO_PERSON = "edit_go";
    public static final String DELETE_PERSON = "delete";
    public static final String CURRENT_ACTION_RESULT = "current_action_result";
    public static final String CURRENT_ACTION_RESULT_LABEL = "current_action_result_label";
    public static final String ADD_PHONE = "addPhone";
    public static final String ADD__GO_PHONE = "addPhone_go";
    public static final String EDIT_PHONE = "editPhone";
    public static final String EDIT_GO_PHONE = "editPhone_go";
    public static final String DELETE_PHONE = "deletePhone";
    public static final String MANAGE_PERSON_JSP = "/ManagePerson.jsp";
    public static final String LIST_JSP = "/List.jsp";
    public static final String EDIT_PERSON_JSP = "/EditPerson.jsp";
    public static final String EDIT_PHONE_JSP = "/EditPhone.jsp";
    public static final String ERROR_MESSAGE = "error_message";

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
		request.setCharacterEncoding(ENCODING);

		// В JSP нам понадобится сама телефонная книга. Можно создать её экземпляр там,
		// но с архитектурной точки зрения логичнее создать его в сервлете и передать в JSP.
		request.setAttribute("phonebook", this.phonebook);
        request.setAttribute("phones", this.phones);

		// Хранилище параметров для передачи в JSP.
		HashMap<String, String> jsp_parameters = new HashMap<>();

		// Диспетчеры для передачи управления на разные JSP (разные представления (view)).
		RequestDispatcher dispatcher_for_manager = request.getRequestDispatcher(MANAGE_PERSON_JSP);
		RequestDispatcher dispatcher_for_list = request.getRequestDispatcher(LIST_JSP);
		RequestDispatcher dispatcher_for_edit = request.getRequestDispatcher(EDIT_PERSON_JSP);
		RequestDispatcher dispatcher_for_edit_phone = request.getRequestDispatcher(EDIT_PHONE_JSP);

		// Действие (action) и идентификатор записи (id) над которой выполняется это действие.
		String action = request.getParameter(ACTION);
		String id = request.getParameter(ID);
		String ownerID = request.getParameter(OWNER_ID);

		// Если идентификатор и действие не указаны, мы находимся в состоянии
		// "просто показать список и больше ничего не делать".
		if ((action == null) && (id == null)) {
			request.setAttribute(JSP_PARAMETERS, jsp_parameters);
			dispatcher_for_list.forward(request, response);
		}
		// Если же действие указано, то...
		else {
			switch (action) {
				// Добавление записи.
				case ADD_PERSON:
					// Создание новой пустой записи о пользователе.
					Person empty_person = new Person();

					// Подготовка параметров для JSP.
					jsp_parameters.put(CURRENT_ACTION, ADD_PERSON);
					jsp_parameters.put(NEXT_ACTION, ADD_GO_PERSON);
					jsp_parameters.put(NEXT_ACTION_LABEL, "Добавить");

					// Установка параметров JSP.
					request.setAttribute("person", empty_person);
					request.setAttribute(JSP_PARAMETERS, jsp_parameters);

					// Передача запроса в JSP.
					dispatcher_for_manager.forward(request, response);
					break;

				// Редактирование записи.
				case EDIT_PERSON:
					// Извлечение из телефонной книги информации о редактируемой записи.
					Person editable_person = this.phonebook.getPerson(id);

					// Подготовка параметров для JSP.
					jsp_parameters.put(CURRENT_ACTION, EDIT_PERSON);
					jsp_parameters.put(NEXT_ACTION, EDIT_GO_PERSON);
					jsp_parameters.put(NEXT_ACTION_LABEL, "Сохранить");

					// Установка параметров JSP.
					request.setAttribute("person", editable_person);
					request.setAttribute(JSP_PARAMETERS, jsp_parameters);

					// Передача запроса в JSP.
					dispatcher_for_edit.forward(request, response);
					break;

				// Удаление записи.
				case DELETE_PERSON:

					// Если запись удалось удалить...
					if (phonebook.deletePerson(id)) {
						jsp_parameters.put(CURRENT_ACTION_RESULT, "DELETION_SUCCESS");
						jsp_parameters.put(CURRENT_ACTION_RESULT_LABEL, "Удаление выполнено успешно");
					}
					// Если запись не удалось удалить (например, такой записи нет)...
					else {
						jsp_parameters.put(CURRENT_ACTION_RESULT, "DELETION_FAILURE");
						jsp_parameters.put(CURRENT_ACTION_RESULT_LABEL, "Ошибка удаления (возможно, запись не найдена)");
					}

					// Установка параметров JSP.
					request.setAttribute(JSP_PARAMETERS, jsp_parameters);

					// Передача запроса в JSP.
					dispatcher_for_list.forward(request, response);
					break;
                case ADD_PHONE:
                    // Создание новой пустой записи о пользователе.
                    Phone empty_phone = new Phone();
                    Person updated_person = this.phonebook.getPerson(ownerID);

                    // Подготовка параметров для JSP.
                    jsp_parameters.put(CURRENT_ACTION, ADD_PHONE);
                    jsp_parameters.put(NEXT_ACTION, ADD__GO_PHONE);
                    jsp_parameters.put(NEXT_ACTION_LABEL, "Добавить");

                    // Установка параметров JSP.
                    request.setAttribute("phone", empty_phone);
                    request.setAttribute("person", updated_person);
                    request.setAttribute(JSP_PARAMETERS, jsp_parameters);

                    // Передача запроса в JSP.
                    dispatcher_for_edit_phone.forward(request, response);
                    break;
                case EDIT_PHONE:
                    // Извлечение из телефонной книги информации о редактируемой записи.
                    Phone editable_phone = this.phones.getPhone(id);
                    Person edited_person = this.phonebook.getPerson(ownerID);

                    // Подготовка параметров для JSP.
                    jsp_parameters.put(CURRENT_ACTION, EDIT_PHONE);
                    jsp_parameters.put(NEXT_ACTION, EDIT_GO_PHONE);
                    jsp_parameters.put(NEXT_ACTION_LABEL, "Сохранить");

                    // Установка параметров JSP.
                    request.setAttribute("phone", editable_phone);
                    request.setAttribute("person", edited_person);
                    request.setAttribute(JSP_PARAMETERS, jsp_parameters);

                    // Передача запроса в JSP.
                    dispatcher_for_edit_phone.forward(request, response);
                    break;

                // Удаление записи.
                case DELETE_PHONE:

                    // Если запись удалось удалить...
                    if (phones.deletePhone(id)) {
                        Person person_for_update = phonebook.getPerson(ownerID);
                        person_for_update.getPhones().remove(id);
                        this.phonebook.updatePerson(request.getParameter(OWNER_ID), person_for_update);

                        jsp_parameters.put(CURRENT_ACTION_RESULT, "DELETION_SUCCESS");
                        jsp_parameters.put(CURRENT_ACTION_RESULT_LABEL, "Удаление выполнено успешно");
                    }
                    // Если запись не удалось удалить (например, такой записи нет)...
                    else {
                        jsp_parameters.put(CURRENT_ACTION_RESULT, "DELETION_FAILURE");
                        jsp_parameters.put(CURRENT_ACTION_RESULT_LABEL, "Ошибка удаления (возможно, телефон не найдена)");
                    }

                    // Установка параметров JSP.
                    request.setAttribute(JSP_PARAMETERS, jsp_parameters);

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
		request.setCharacterEncoding(ENCODING);

		// В JSP нам понадобится сама телефонная книга. Можно создать её экземпляр там,
		// но с архитектурной точки зрения логичнее создать его в сервлете и передать в JSP.
		request.setAttribute("phonebook", this.phonebook);
        request.setAttribute("phones", this.phones);

		// Хранилище параметров для передачи в JSP.
		HashMap<String, String> jsp_parameters = new HashMap<>();

		// Диспетчеры для передачи управления на разные JSP (разные представления (view)).
		RequestDispatcher dispatcher_for_manager = request.getRequestDispatcher(MANAGE_PERSON_JSP);
		RequestDispatcher dispatcher_for_list = request.getRequestDispatcher(LIST_JSP);
		RequestDispatcher dispatcher_for_edit = request.getRequestDispatcher(EDIT_PERSON_JSP);
		RequestDispatcher dispatcher_for_edit_phone = request.getRequestDispatcher(EDIT_PHONE_JSP);


		// Действие (add_go, edit_go) и идентификатор записи (id) над которой выполняется это действие.
		String add_go = request.getParameter(ADD_GO_PERSON);
		String edit_go = request.getParameter(EDIT_GO_PERSON);
        String addPhone_go = request.getParameter(ADD__GO_PHONE);
        String editPhone_go = request.getParameter(EDIT_GO_PHONE);
        String id = request.getParameter(ID);

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
					jsp_parameters.put(CURRENT_ACTION_RESULT, "ADDITION_SUCCESS");
					jsp_parameters.put(CURRENT_ACTION_RESULT_LABEL, "Добавление выполнено успешно");
				}
				// Если запись НЕ удалось добавить...
				else {
					jsp_parameters.put(CURRENT_ACTION_RESULT, "ADDITION_FAILURE");
					jsp_parameters.put(CURRENT_ACTION_RESULT_LABEL, "Ошибка добавления");
				}

				// Установка параметров JSP.
				request.setAttribute(JSP_PARAMETERS, jsp_parameters);

				// Передача запроса в JSP.
				dispatcher_for_list.forward(request, response);
			}
			// Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
			else {
				// Подготовка параметров для JSP.
				jsp_parameters.put(CURRENT_ACTION, ADD_PERSON);
				jsp_parameters.put(NEXT_ACTION, ADD_GO_PERSON);
				jsp_parameters.put(NEXT_ACTION_LABEL, "Добавить");
				jsp_parameters.put(ERROR_MESSAGE, error_message);

				// Установка параметров JSP.
				request.setAttribute("person", new_person);
				request.setAttribute(JSP_PARAMETERS, jsp_parameters);

				// Передача запроса в JSP.
				dispatcher_for_manager.forward(request, response);
			}
		}

		// Редактирование записи.
		if (edit_go != null) {
			// Получение записи и её обновление на основе данных из формы.
			Person updatable_person = this.phonebook.getPerson(request.getParameter(ID));
			updatable_person.setName(request.getParameter("name"));
			updatable_person.setSurname(request.getParameter("surname"));
			updatable_person.setMiddlename(request.getParameter("middlename"));

			// Валидация ФИО.
			String error_message = this.validatePersonFMLName(updatable_person);

			// Если данные верные, можно производить добавление.
			if (error_message.equals("")) {

				// Если запись удалось обновить...
				if (this.phonebook.updatePerson(id, updatable_person)) {
					jsp_parameters.put(CURRENT_ACTION_RESULT, "UPDATE_SUCCESS");
					jsp_parameters.put(CURRENT_ACTION_RESULT_LABEL, "Обновление выполнено успешно");
				}
				// Если запись НЕ удалось обновить...
				else {
					jsp_parameters.put(CURRENT_ACTION_RESULT, "UPDATE_FAILURE");
					jsp_parameters.put(CURRENT_ACTION_RESULT_LABEL, "Ошибка обновления");
				}

				// Установка параметров JSP.
				request.setAttribute(JSP_PARAMETERS, jsp_parameters);

				// Передача запроса в JSP.
				dispatcher_for_edit.forward(request, response);
			}
			// Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
			else {

				// Подготовка параметров для JSP.
				jsp_parameters.put(CURRENT_ACTION, EDIT_PERSON);
				jsp_parameters.put(NEXT_ACTION, EDIT_GO_PERSON);
				jsp_parameters.put(NEXT_ACTION_LABEL, "Сохранить");
				jsp_parameters.put(ERROR_MESSAGE, error_message);

				// Установка параметров JSP.
				request.setAttribute("person", updatable_person);
				request.setAttribute(JSP_PARAMETERS, jsp_parameters);

				// Передача запроса в JSP.
				dispatcher_for_edit.forward(request, response);

			}
		}
        if (addPhone_go != null) {
            // Создание записи на основе данных из формы.
            Phone new_phone = new Phone(request.getParameter(ID), request.getParameter(OWNER_ID), request.getParameter("number"));
            Person updated_person = this.phonebook.getPerson(request.getParameter(OWNER_ID));

            // Валидация ФИО.
            String error_message = ""; //= this.validatePersonFMLName(new_person);

            // Если данные верные, можно производить добавление.
            if (error_message.equals("")) {
                String new_id;
                // Если запись удалось добавить...
                if (!(new_id = this.phones.addPhone(new_phone)).equals("0")) {
                    updated_person.getPhones().put(new_id, this.phones.getPhone(new_id));
                    this.phonebook.updatePerson(request.getParameter(OWNER_ID), updated_person);

                    jsp_parameters.put(CURRENT_ACTION_RESULT, "ADDITION_SUCCESS");
                    jsp_parameters.put(CURRENT_ACTION_RESULT_LABEL, "Добавление выполнено успешно");
                }
                // Если запись НЕ удалось добавить...
                else {
                    jsp_parameters.put(CURRENT_ACTION_RESULT, "ADDITION_FAILURE");
                    jsp_parameters.put(CURRENT_ACTION_RESULT_LABEL, "Ошибка добавления");
                }

                // Установка параметров JSP.
                request.setAttribute(JSP_PARAMETERS, jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_list.forward(request, response);
            }
            // Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
            else {
                // Подготовка параметров для JSP.
                jsp_parameters.put(CURRENT_ACTION, ADD_PHONE);
                jsp_parameters.put(NEXT_ACTION, ADD__GO_PHONE);
                jsp_parameters.put(NEXT_ACTION_LABEL, "Добавить");
                jsp_parameters.put(ERROR_MESSAGE, error_message);

                // Установка параметров JSP.
                request.setAttribute("phone", new_phone);
                request.setAttribute(JSP_PARAMETERS, jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_edit_phone.forward(request, response);
            }
        }

        // Редактирование записи.
        if (editPhone_go != null) {
            // Получение записи и её обновление на основе данных из формы.
            Phone updatable_phone = this.phones.getPhone(request.getParameter(ID));
            Person updated_person = this.phonebook.getPerson(request.getParameter(OWNER_ID));

            updatable_phone.setId(request.getParameter(ID));
            updatable_phone.setOwner(request.getParameter(OWNER_ID));
            updatable_phone.setNumber(request.getParameter("number"));

            // Валидация ФИО.
            String error_message = ""; //this.validatePersonFMLName(updatable_person);

            // Если данные верные, можно производить добавление.
            if (error_message.equals("")) {

                // Если запись удалось обновить...
                if (this.phones.updatePhone(id, updatable_phone)) {
                    updated_person.getPhones().put(request.getParameter(ID), updatable_phone);
                    this.phonebook.updatePerson(request.getParameter(OWNER_ID), updated_person);
                    jsp_parameters.put(CURRENT_ACTION_RESULT, "UPDATE_SUCCESS");
                    jsp_parameters.put(CURRENT_ACTION_RESULT_LABEL, "Обновление выполнено успешно");
                }
                // Если запись НЕ удалось обновить...
                else {
                    jsp_parameters.put(CURRENT_ACTION_RESULT, "UPDATE_FAILURE");
                    jsp_parameters.put(CURRENT_ACTION_RESULT_LABEL, "Ошибка обновления");
                }

                // Установка параметров JSP.
                request.setAttribute(JSP_PARAMETERS, jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_list.forward(request, response);
            }
            // Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
            else {

                // Подготовка параметров для JSP.
                jsp_parameters.put(CURRENT_ACTION, EDIT_PHONE);
                jsp_parameters.put(NEXT_ACTION, EDIT_GO_PHONE);
                jsp_parameters.put(NEXT_ACTION_LABEL, "Сохранить");
                jsp_parameters.put(ERROR_MESSAGE, error_message);

                // Установка параметров JSP.
                request.setAttribute("phone", updatable_phone);
                request.setAttribute(JSP_PARAMETERS, jsp_parameters);

                // Передача запроса в JSP.
                dispatcher_for_list.forward(request, response);

            }
        }
	}
}