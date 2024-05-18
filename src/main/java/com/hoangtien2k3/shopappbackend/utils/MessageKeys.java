package com.hoangtien2k3.shopappbackend.utils;

public class MessageKeys {
    // login
    public static final String LOGIN_SUCCESS = "user.login.login_succesfully";
    public static final String LOGIN_FAILED = "user.login.login_failed";
    public static final String PHONE_NUMBER_EXISTED = "user.login.phone_number_existed";
    public static final String PHONE_NUMBER_AND_PASSWORD_FAILED = "user.login.phone_number_and_password_failed";
    public static final String CAN_NOT_CREATE_ACCOUNT_ROLE_ADMIN = "user.login.can_not_account_role_admin";

    // register
    public static final String PASSWORD_NOT_MATCH = "user.register.password_not_match";
    public static final String REGISTER_SUCCESS = "user.register.register_successlly";

    // validation
    public static final String ERROR_MESSAGE = "message.error";
    public static final String PHONE_NUMBER_REQUIRED = "phone_number.required";
    public static final String PASSWORD_REQUIRED = "password.required";
    public static final String RETYPE_PASSWORD_REQUIRED = "retype_password.required";
    public static final String ROLE_ID_REQUIRED = "role_id_required";
    public static final String PRODUCT_ID_REQUIRED = "product_id_required";
    public static final String IMAGE_SIZE_REQUIRED = "image_size_required";
    public static final String PRODUCT_TITLE_REQUIRED = "product_title_required";
    public static final String PRODUCT_TITLE_SIZE_REQUIRED = "product_title_size_required";
    public static final String PRODUCT_PRICE_MIN_REQUIRED = "product_price_min_required";
    public static final String PRODUCT_PRICE_MAX_REQUIRED = "product_price_max_required";
    public static final String USER_ID_REQUIRED = "user_id_required";
    public static final String PHONE_NUMBER_SIZE_REQUIRED = "phone_number_size.required";
    public static final String TOTAL_MONEY_REQUIRED = "total_money.required";
    public static final String ORDER_ID_REQUIRED = "order_id.required";
    public static final String NUMBER_OF_PRODUCT_REQUIRED = "number_of_product.required";
    public static final String CATEGORIES_NAME_REQUIRED = "categories_name.required";

    public static final String PRODUCT_NOT_FOUND = "product.valid.not_found";
    public static final String CATEGORY_NOT_FOUND = "category.valid.not_found";

    // token
    public static final String ERROR_REFRESH_TOKEN = "user.refresh_token.failed";

    // error get
    public static final String MESSAGE_ERROR_GET = "message.get.error";
    public static final String MESSAGE_UPDATE_GET = "message.update.success";
    public static final String MESSAGE_DELETE_SUCCESS = "message.delete.success";
    public static final String MESSAGE_DELETE_FAILED = "message.delete.failed";

    public static final String CREATE_ORDER_SUCCESS = "order.create.successfully";
    public static final String CREATE_ORDER_FAILED = "order.create.failed";
    public static final String GET_INFORMATION_FAILED = "get.information.data.failed";

    public static final String APP_AUTHORIZATION_403 = "app.authorization.403";
    public static final String APP_UNCATEGORIZED_500 = "app.uncategorized.500";

}
