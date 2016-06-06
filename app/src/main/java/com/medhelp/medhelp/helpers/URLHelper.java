package com.medhelp.medhelp.helpers;

public class URLHelper {

    public static final String LOGIN_URL = "http://192.168.1.6:8080/api/users/login";

    public static final String SIGNUP_URL = "http://192.168.1.6:8080/api/users";

    public static final String GET_PATIENT_URL = "http://192.168.1.6:8080/api/patients";

    public static final String SAVE_PATIENT_URL = "http://192.168.1.6:8080/api/patients";

    public static final String GET_DOCTOR_URL = "http://192.168.1.6:8080/api/doctors";

    public static final String SAVE_DOCTOR_URL = "http://192.168.1.6:8080/api/doctors";

    public static final String DOCTOR_FIND_SUGGESTION_URL = "http://192.168.1.6:8080/api/doctors/find/suggestions";

    public static final String DOCTOR_FIND_URL = "http://192.168.1.6:8080/api/doctors/find";

    public static final String GET_PATIENT_BODY_PARTS_URL = "http://192.168.1.6:8080/api/patients/:id/bodyparts";

    public static final String ADD_PATIENT_BODY_PART_PROBLEM_URL = "http://192.168.1.6:8080/api/patients/:id/bodyparts";

    public static final String GET_PATIENT_IMAGE = "http://192.168.1.6:8080/api/patients/:id/image";

    public static final String SAVE_PATIENT_IMAGE = "http://192.168.1.6:8080/api/patients/:id/image";

    public static final String GET_DOCTOR_IMAGE = "http://192.168.1.6:8080/api/doctors/:id/image";

    public static final String SAVE_DOCTOR_IMAGE = "http://192.168.1.6:8080/api/doctors/:id/image";

    public static final String SAVE_DOCTOR_OPINIONS = "http://192.168.1.6:8080/api/doctors/:id/opinions";

    public static final String GET_DOCTOR_OPINIONS = "http://192.168.1.6:8080/api/doctors/:id/opinions";

    public static final String GET_DOCTOR_OPINIONS_SUMMARY = "http://192.168.1.6:8080/api/doctors/:id/opinions/summary";
}
