
INSERT INTO DEVELOPERS(EMAIL, PASSWORD, NAME) VALUES('ade@test.com', 'password', 'Ade Test');

INSERT INTO APPS(ID, SECRET, APP_NAME, GRANT_TYPE, LOGIN_REDIRECT_URL, DEVELOPER_ID, HOME_PAGE, API_ID)
VALUES('uEjbUR5IIqTMavhVgZg2KCQUHL0Al4iK', 'iKkhlb0qVZ8e7UeP6izXEGVoHA6UZAgh', 'Test App', 'AUTH_CODE',
       'localhost:8085/callback', 'ade@test.com', 'localhost:8085', 'resource-api');

INSERT INTO USERS(APP_ID, EMAIL, PHONE_NUMBER)
VALUES ('uEjbUR5IIqTMavhVgZg2KCQUHL0Al4iK', 'abdulwahabogunmodede@gmail.com', '08138349676');
