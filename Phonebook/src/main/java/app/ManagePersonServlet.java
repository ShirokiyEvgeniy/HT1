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
		String errorMessage = "";

		if (!person.validateFMLNamePart(person.getName(), false)) {
			errorMessage += "Имя должно быть строкой от 1 до 150 символов из букв, цифр, знаков подчёркивания и знаков минус.<br />";
		}

		if (!person.validateFMLNamePart(person.getSurname(), false)) {
			errorMessage += "Фамилия должна быть строкой от 1 до 150 символов из букв, цифр, знаков подчёркивания и знаков минус.<br />";
		}

		if (!person.validateFMLNamePart(person.getMiddlename(), true)) {
			errorMessage += "Отчество должно быть строкой от 0 до 150 символов из букв, цифр, знаков подчёркивания и знаков минус.<br />";
		}

		return errorMessage;
	}

	private String validatePhone(Phone phone) {
	    String errorMessage = "";

	    if (!phone.validateNumber(phone.getNumber())) {
	        errorMessage += "Требования к телефонному номеру: от 2 до 50 символов: цифра, +, -, #.";
        }

        return errorMessage;
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
		HashMap<String, String> jspParameters = new HashMap<>();

		// Диспетчеры для передачи управления на разные JSP (разные представления (view)).
		RequestDispatcher dispatcherForManager = request.getRequestDispatcher(MANAGE_PERSON_JSP);
		RequestDispatcher dispatcherForList = request.getRequestDispatcher(LIST_JSP);
		RequestDispatcher dispatcherForEdit = request.getRequestDispatcher(EDIT_PERSON_JSP);
		RequestDispatcher dispatcherForEditPhone = request.getRequestDispatcher(EDIT_PHONE_JSP);

		// Действие (action) и идентификатор записи (id) над которой выполняется это действие.
		String action = request.getParameter(ACTION);
		String id = request.getParameter(ID);
		String ownerID = request.getParameter(OWNER_ID);

		// Если идентификатор и действие не указаны, мы находимся в состоянии
		// "просто показать список и больше ничего не делать".
		if ((action == null) && (id == null)) {
			request.setAttribute(JSP_PARAMETERS, jspParameters);
			dispatcherForList.forward(request, response);
		}
		// Если же действие указано, то...
		else {
			switch (action) {
				// Добавление записи.
				case ADD_PERSON:
					// Создание новой пустой записи о пользователе.
					Person emptyPerson = new Person();

					// Подготовка параметров для JSP.
					jspParameters.put(CURRENT_ACTION, ADD_PERSON);
					jspParameters.put(NEXT_ACTION, ADD_GO_PERSON);
					jspParameters.put(NEXT_ACTION_LABEL, "Добавить");

					// Установка параметров JSP.
					request.setAttribute("person", emptyPerson);
					request.setAttribute(JSP_PARAMETERS, jspParameters);

					// Передача запроса в JSP.
					dispatcherForManager.forward(request, response);
					break;

				// Редактирование записи.
				case EDIT_PERSON:
					// Извлечение из телефонной книги информации о редактируемой записи.
					Person editablePerson = this.phonebook.getPerson(id);

					// Подготовка параметров для JSP.
					jspParameters.put(CURRENT_ACTION, EDIT_PERSON);
					jspParameters.put(NEXT_ACTION, EDIT_GO_PERSON);
					jspParameters.put(NEXT_ACTION_LABEL, "Сохранить");

					// Установка параметров JSP.
					request.setAttribute("person", editablePerson);
					request.setAttribute(JSP_PARAMETERS, jspParameters);

					// Передача запроса в JSP.
					dispatcherForEdit.forward(request, response);
					break;

				// Удаление записи.
				case DELETE_PERSON:

					// Если запись удалось удалить...
					if (phonebook.deletePerson(id)) {
						jspParameters.put(CURRENT_ACTION_RESULT, "DELETION_SUCCESS");
						jspParameters.put(CURRENT_ACTION_RESULT_LABEL, "Удаление выполнено успешно");
					}
					// Если запись не удалось удалить (например, такой записи нет)...
					else {
						jspParameters.put(CURRENT_ACTION_RESULT, "DELETION_FAILURE");
						jspParameters.put(CURRENT_ACTION_RESULT_LABEL, "Ошибка удаления (возможно, запись не найдена)");
					}

					// Установка параметров JSP.
					request.setAttribute(JSP_PARAMETERS, jspParameters);

					// Передача запроса в JSP.
					dispatcherForList.forward(request, response);
					break;
                case ADD_PHONE:
                    // Создание новой пустой записи о пользователе.
                    Phone emptyPhone = new Phone();
                    Person updatedPerson = this.phonebook.getPerson(ownerID);

                    // Подготовка параметров для JSP.
                    jspParameters.put(CURRENT_ACTION, ADD_PHONE);
                    jspParameters.put(NEXT_ACTION, ADD__GO_PHONE);
                    jspParameters.put(NEXT_ACTION_LABEL, "Добавить");

                    // Установка параметров JSP.
                    request.setAttribute("phone", emptyPhone);
                    request.setAttribute("person", updatedPerson);
                    request.setAttribute(JSP_PARAMETERS, jspParameters);

                    // Передача запроса в JSP.
                    dispatcherForEditPhone.forward(request, response);
                    break;
                case EDIT_PHONE:
                    // Извлечение из телефонной книги информации о редактируемой записи.
                    Phone editablePhone = this.phones.getPhone(id);
                    Person editedPerson = this.phonebook.getPerson(ownerID);

                    // Подготовка параметров для JSP.
                    jspParameters.put(CURRENT_ACTION, EDIT_PHONE);
                    jspParameters.put(NEXT_ACTION, EDIT_GO_PHONE);
                    jspParameters.put(NEXT_ACTION_LABEL, "Сохранить");

                    // Установка параметров JSP.
                    request.setAttribute("phone", editablePhone);
                    request.setAttribute("person", editedPerson);
                    request.setAttribute(JSP_PARAMETERS, jspParameters);

                    // Передача запроса в JSP.
                    dispatcherForEditPhone.forward(request, response);
                    break;

                // Удаление записи.
                case DELETE_PHONE:

                    // Если запись удалось удалить...
                    if (phones.deletePhone(id)) {
                        Person personForUpdate = phonebook.getPerson(ownerID);
                        personForUpdate.getPhones().remove(id);
                        this.phonebook.updatePerson(request.getParameter(OWNER_ID), personForUpdate);

                        jspParameters.put(CURRENT_ACTION_RESULT, "DELETION_SUCCESS");
                        jspParameters.put(CURRENT_ACTION_RESULT_LABEL, "Удаление выполнено успешно");
                    }
                    // Если запись не удалось удалить (например, такой записи нет)...
                    else {
                        jspParameters.put(CURRENT_ACTION_RESULT, "DELETION_FAILURE");
                        jspParameters.put(CURRENT_ACTION_RESULT_LABEL, "Ошибка удаления (возможно, телефон не найдена)");
                    }

                    // Установка параметров JSP.
                    request.setAttribute(JSP_PARAMETERS, jspParameters);

                    // Передача запроса в JSP.
                    dispatcherForList.forward(request, response);
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
		HashMap<String, String> jspParameters = new HashMap<>();

		// Диспетчеры для передачи управления на разные JSP (разные представления (view)).
		RequestDispatcher dispatcherForManager = request.getRequestDispatcher(MANAGE_PERSON_JSP);
		RequestDispatcher dispatcherForList = request.getRequestDispatcher(LIST_JSP);
		RequestDispatcher dispatcherForEdit = request.getRequestDispatcher(EDIT_PERSON_JSP);
		RequestDispatcher dispatcherForEditPhone = request.getRequestDispatcher(EDIT_PHONE_JSP);


		// Действие (add_go, edit_go) и идентификатор записи (id) над которой выполняется это действие.
		String addGo = request.getParameter(ADD_GO_PERSON);
		String editGo = request.getParameter(EDIT_GO_PERSON);
        String addPhoneGo = request.getParameter(ADD__GO_PHONE);
        String editPhoneGo = request.getParameter(EDIT_GO_PHONE);
        String id = request.getParameter(ID);

		// Добавление записи.
		if (addGo != null) {
			// Создание записи на основе данных из формы.
			Person newPerson = new Person(request.getParameter("name"), request.getParameter("surname"), request.getParameter("middlename"));

			// Валидация ФИО.
			String errorMessage = this.validatePersonFMLName(newPerson);

			// Если данные верные, можно производить добавление.
			if (errorMessage.equals("")) {

				// Если запись удалось добавить...
				if (this.phonebook.addPerson(newPerson)) {
					jspParameters.put(CURRENT_ACTION_RESULT, "ADDITION_SUCCESS");
					jspParameters.put(CURRENT_ACTION_RESULT_LABEL, "Добавление выполнено успешно");
				}
				// Если запись НЕ удалось добавить...
				else {
					jspParameters.put(CURRENT_ACTION_RESULT, "ADDITION_FAILURE");
					jspParameters.put(CURRENT_ACTION_RESULT_LABEL, "Ошибка добавления");
				}

				// Установка параметров JSP.
				request.setAttribute(JSP_PARAMETERS, jspParameters);

				// Передача запроса в JSP.
				dispatcherForList.forward(request, response);
			}
			// Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
			else {
				// Подготовка параметров для JSP.
				jspParameters.put(CURRENT_ACTION, ADD_PERSON);
				jspParameters.put(NEXT_ACTION, ADD_GO_PERSON);
				jspParameters.put(NEXT_ACTION_LABEL, "Добавить");
				jspParameters.put(ERROR_MESSAGE, errorMessage);

				// Установка параметров JSP.
				request.setAttribute("person", newPerson);
				request.setAttribute(JSP_PARAMETERS, jspParameters);

				// Передача запроса в JSP.
				dispatcherForManager.forward(request, response);
			}
		}

		// Редактирование записи.
		if (editGo != null) {
			// Получение записи и её обновление на основе данных из формы.
			Person updatablePerson = this.phonebook.getPerson(request.getParameter(ID));

			// Валидация ФИО.
			String errorMessage = this.validatePersonFMLName(new Person(request.getParameter("name"), request.getParameter("surname"), request.getParameter("middlename")));

			// Если данные верные, можно производить добавление.
			if (errorMessage.equals("")) {

                updatablePerson.setName(request.getParameter("name"));
                updatablePerson.setSurname(request.getParameter("surname"));
                updatablePerson.setMiddlename(request.getParameter("middlename"));

				// Если запись удалось обновить...
				if (this.phonebook.updatePerson(id, updatablePerson)) {
					jspParameters.put(CURRENT_ACTION_RESULT, "UPDATE_SUCCESS");
					jspParameters.put(CURRENT_ACTION_RESULT_LABEL, "Обновление выполнено успешно");
				}
				// Если запись НЕ удалось обновить...
				else {
					jspParameters.put(CURRENT_ACTION_RESULT, "UPDATE_FAILURE");
					jspParameters.put(CURRENT_ACTION_RESULT_LABEL, "Ошибка обновления");
				}

				// Установка параметров JSP.
				request.setAttribute(JSP_PARAMETERS, jspParameters);

				// Передача запроса в JSP.
				dispatcherForEdit.forward(request, response);
			}
			// Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
			else {

				// Подготовка параметров для JSP.
				jspParameters.put(CURRENT_ACTION, EDIT_PERSON);
				jspParameters.put(NEXT_ACTION, EDIT_GO_PERSON);
				jspParameters.put(NEXT_ACTION_LABEL, "Сохранить");
				jspParameters.put(ERROR_MESSAGE, errorMessage);

				// Установка параметров JSP.
				request.setAttribute("person", updatablePerson);
				request.setAttribute(JSP_PARAMETERS, jspParameters);

				// Передача запроса в JSP.
				dispatcherForEdit.forward(request, response);

			}
		}
        if (addPhoneGo != null) {
            // Создание записи на основе данных из формы.
            Phone newPhone = new Phone(request.getParameter(ID), request.getParameter(OWNER_ID), request.getParameter("number"));
            Person updatedPerson = this.phonebook.getPerson(request.getParameter(OWNER_ID));

            // Валидация ФИО.
            String errorMessage = this.validatePhone(newPhone);

            // Если данные верные, можно производить добавление.
            if (errorMessage.equals("")) {
                String newID;
                // Если запись удалось добавить...
                if (!(newID = this.phones.addPhone(newPhone)).equals("0")) {
                    updatedPerson.getPhones().put(newID, this.phones.getPhone(newID));
                    this.phonebook.updatePerson(request.getParameter(OWNER_ID), updatedPerson);

                    jspParameters.put(CURRENT_ACTION_RESULT, "ADDITION_SUCCESS");
                    jspParameters.put(CURRENT_ACTION_RESULT_LABEL, "Добавление выполнено успешно");
                }
                // Если запись НЕ удалось добавить...
                else {
                    jspParameters.put(CURRENT_ACTION_RESULT, "ADDITION_FAILURE");
                    jspParameters.put(CURRENT_ACTION_RESULT_LABEL, "Ошибка добавления");
                }

                // Установка параметров JSP.
                request.setAttribute(JSP_PARAMETERS, jspParameters);

                // Передача запроса в JSP.
                dispatcherForList.forward(request, response);
            }
            // Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
            else {
                // Подготовка параметров для JSP.
                jspParameters.put(CURRENT_ACTION, ADD_PHONE);
                jspParameters.put(NEXT_ACTION, ADD__GO_PHONE);
                jspParameters.put(NEXT_ACTION_LABEL, "Добавить");
                jspParameters.put(ERROR_MESSAGE, errorMessage);

                // Установка параметров JSP.
                request.setAttribute("phone", newPhone);
                request.setAttribute("person", updatedPerson);
                request.setAttribute(JSP_PARAMETERS, jspParameters);

                // Передача запроса в JSP.
                dispatcherForEditPhone.forward(request, response);
            }
        }

        // Редактирование записи.
        if (editPhoneGo != null) {
            // Получение записи и её обновление на основе данных из формы.
            Phone updatablePhone = this.phones.getPhone(request.getParameter(ID));
            Person updatedPerson = this.phonebook.getPerson(request.getParameter(OWNER_ID));

            // Валидация ФИО.
            String errorMessage = validatePhone(new Phone("0", "0", request.getParameter("number")));

            // Если данные верные, можно производить добавление.
            if (errorMessage.equals("")) {

                updatablePhone.setId(request.getParameter(ID));
                updatablePhone.setOwner(request.getParameter(OWNER_ID));
                updatablePhone.setNumber(request.getParameter("number"));

                // Если запись удалось обновить...
                if (this.phones.updatePhone(id, updatablePhone)) {
                    updatedPerson.getPhones().put(request.getParameter(ID), updatablePhone);
                    this.phonebook.updatePerson(request.getParameter(OWNER_ID), updatedPerson);
                    jspParameters.put(CURRENT_ACTION_RESULT, "UPDATE_SUCCESS");
                    jspParameters.put(CURRENT_ACTION_RESULT_LABEL, "Обновление выполнено успешно");
                }
                // Если запись НЕ удалось обновить...
                else {
                    jspParameters.put(CURRENT_ACTION_RESULT, "UPDATE_FAILURE");
                    jspParameters.put(CURRENT_ACTION_RESULT_LABEL, "Ошибка обновления");
                }

                // Установка параметров JSP.
                request.setAttribute(JSP_PARAMETERS, jspParameters);

                // Передача запроса в JSP.
                dispatcherForList.forward(request, response);
            }
            // Если в данных были ошибки, надо заново показать форму и сообщить об ошибках.
            else {

                // Подготовка параметров для JSP.
                jspParameters.put(CURRENT_ACTION, EDIT_PHONE);
                jspParameters.put(NEXT_ACTION, EDIT_GO_PHONE);
                jspParameters.put(NEXT_ACTION_LABEL, "Сохранить");
                jspParameters.put(ERROR_MESSAGE, errorMessage);

                // Установка параметров JSP.
                request.setAttribute("phone", updatablePhone);
                request.setAttribute("person", updatedPerson);
                request.setAttribute(JSP_PARAMETERS, jspParameters);

                // Передача запроса в JSP.
                dispatcherForEditPhone.forward(request, response);

            }
        }
	}
}