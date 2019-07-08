INSERT INTO APPS(ID, SECRET, APP_NAME, GRANT_TYPE, LOGIN_REDIRECT_URL, HOME_PAGE, API_ID)
VALUES('uEjbUR5IIqTMavhVgZg2KCQUHL0Al4iK', 'iKkhlb0qVZ8e7UeP6izXEGVoHA6UZAgh', 'Test App', 'AUTH_CODE',
       'http://localhost:8080/callback', 'localhost:8080', 'resource-api');
 
INSERT INTO USERS(APP_ID, EMAIL, PHONE_NUMBER)
VALUES ('uEjbUR5IIqTMavhVgZg2KCQUHL0Al4iK', 'abdulwahabogunmodede@gmail.com', '08138349676');
      
INSERT INTO USERS(APP_ID, EMAIL, PHONE_NUMBER)
VALUES ('uEjbUR5IIqTMavhVgZg2KCQUHL0Al4iK', 'abdulwahabogunmodede@yahoo.com', '08138349676');      
